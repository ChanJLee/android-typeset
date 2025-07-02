package me.chan.texas.renderer.selection;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;

import me.chan.texas.misc.RectF;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.ui.rv.TexasLayoutManager;
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.renderer.ui.text.TextureParagraph;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.utils.IntSet;

import java.util.ArrayList;
import java.util.List;

public class Selection extends DefaultRecyclable {
	private static final ObjectPool<Selection> POOL = new ObjectPool<>(8);
	private Type mType;
	private TexasRecyclerView mContainer;
	protected final List<ParagraphSelection> mParagraphSelections = new ArrayList<>();
	private final IntSet mSet = new IntSet();
	private final RectEdge mRectEdge = new RectEdge();
	private Styles mStyles;

	private Selection() {
	}

	public Styles getStyles() {
		return mStyles;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void add(ParagraphSelection selection) {
		mParagraphSelections.add(selection);
		mSet.add(selection.getId());
	}

	@Nullable
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	ParagraphSelection getParagraphSelection(Paragraph paragraph) {
		return paragraph.getSelection(mType);
	}

	public Type getType() {
		return mType;
	}

	
	@Nullable
	public List<Paragraph> getSelectedParagraphs() {
		if (mParagraphSelections.isEmpty() || isInvalidate()) {
			return null;
		}

		List<Paragraph> list = new ArrayList<>();
		for (ParagraphSelection selection : mParagraphSelections) {
			Paragraph paragraph = selection.getParagraph();
			list.add(paragraph);
		}
		return list;
	}

	
	private final int[] mLocations = new int[2];

	private RectEdge getSelectedRectEdgeSingle() {
		if (mParagraphSelections.isEmpty()) {
			return null;
		}

		ParagraphSelection paragraphSelection = mParagraphSelections.get(0);
		if (paragraphSelection.isSelectedRegionEmpty()) {
			return null;
		}

		RectF firstRegion = paragraphSelection.getFirstRegion();
		mRectEdge.topY = firstRegion.top;
		mRectEdge.topX = firstRegion.left;
		mRectEdge.lineHeight = firstRegion.bottom - firstRegion.top;

		RectF lastRegion = paragraphSelection.getLastRegion();
		mRectEdge.bottomY = lastRegion.bottom;
		mRectEdge.bottomX = lastRegion.right;
		return mRectEdge;
	}

	public RectEdge getSelectedRectEdge() {
		int size = mParagraphSelections.size();
		if (size == 0) {
			return null;
		}

		if (mContainer == null) {
			return getSelectedRectEdgeSingle();
		}

		boolean hasModified = false;
		for (int i = 0; i < size; ++i) {
			ParagraphSelection paragraphSelection = mParagraphSelections.get(i);
			Paragraph paragraph = paragraphSelection.getParagraph();
			if (paragraph == null || paragraph.getSelection(mType) != paragraphSelection) {
				return null;
			}
			if (paragraphSelection.isSelectedRegionEmpty()) {
				continue;
			}

			hasModified = true;
			boolean result = getParagraphLocation(mContainer, paragraph, mLocations);
			if (!result) {
				w("get first region location failed");
			}

			RectF firstRegion = paragraphSelection.getFirstRegion();
			assert firstRegion != null;

			mRectEdge.topY = firstRegion.top + mLocations[1];
			mRectEdge.topX = firstRegion.left + mLocations[0];
			mRectEdge.lineHeight = firstRegion.bottom - firstRegion.top;
			break;
		}

		for (int i = size - 1; i >= 0; --i) {
			ParagraphSelection paragraphSelection = mParagraphSelections.get(i);
			Paragraph paragraph = paragraphSelection.getParagraph();
			if (paragraph == null || paragraph.getSelection(mType) != paragraphSelection) {
				return null;
			}
			if (paragraphSelection.isSelectedRegionEmpty()) {
				continue;
			}

			hasModified = true;
			boolean result = getParagraphLocation(mContainer, paragraph, mLocations);
			if (!result) {
				w("get last region location failed");
			}

			RectF lastRegion = paragraphSelection.getLastRegion();
			assert lastRegion != null;

			mRectEdge.bottomY = lastRegion.bottom + mLocations[1];
			mRectEdge.bottomX = lastRegion.right + mLocations[0];
			break;
		}

		return hasModified ? mRectEdge : null;
	}

	boolean getParagraphLocation(TexasRecyclerView container, Paragraph paragraph, int[] locations) {
		Document document = container.getDocument();
		if (document == null) {
			return false;
		}

		int index = document.indexOfSegment(paragraph);
		if (index == -1) {
			return false;
		}

		TexasLayoutManager layoutManager = container.getTexasLayoutManager();
		if (layoutManager == null) {
			return false;
		}

		TextureParagraph child = layoutManager.findTextureParagraphByPosition(index);
		if (child == null) {
			return false;
		}

		container.getChildLocations(child, locations);
		return true;
	}

	public int size() {
		return mParagraphSelections.size();
	}

	public Paragraph getParagraph(int index) {
		return mParagraphSelections.get(index).getParagraph();
	}

	public ParagraphSelection get(int index) {
		return mParagraphSelections.get(index);
	}

	@Override
	protected void onRecycle() {
		mParagraphSelections.clear();
		mSet.clear();
		mContainer = null;
		mRectEdge.bottomX = mRectEdge.topX =
				mRectEdge.bottomY = mRectEdge.topY = mRectEdge.lineHeight = 0;
		mStyles = null;
		POOL.release(this);
	}

	
	public void clear() {
		if (isInvalidate()) {
			return;
		}


		for (ParagraphSelection paragraphSelection : mParagraphSelections) {
			Paragraph paragraph = paragraphSelection.getParagraph();
			paragraph.setSelection(mType, null);
			try {
				paragraph.requestRedraw();
			} catch (Throwable ignore) {
				
			}
			paragraphSelection.recycle();
		}
		mParagraphSelections.clear();
		mSet.clear();
	}

	@VisibleForTesting
	boolean isInvalidate() {
		for (ParagraphSelection selection : mParagraphSelections) {
			if (!mSet.contains(selection.getId())) {
				return true;
			}

			Paragraph paragraph = selection.getParagraph();
			if (paragraph == null || paragraph.getSelection(mType) != selection) {
				return true;
			}
		}

		return false;
	}

	public boolean isEmpty() {
		return mParagraphSelections.isEmpty();
	}

	private ValueAnimator mAnimator;

	public void startAnimator(@NonNull ValueAnimator animator, @NonNull SelectionAnimatorListener listener) {
		if (listener == null || animator == null) {
			throw new IllegalArgumentException("listener or animator is null");
		}

		stopAnimator();
		mAnimator = animator;
		listener.setStyles(this);
		mAnimator.addListener(listener);
		mAnimator.addUpdateListener(listener);
		mAnimator.start();
	}

	public void stopAnimator() {
		if (mAnimator != null) {
			mAnimator.cancel();
		}
	}

	private void refresh() {
		for (ParagraphSelection selection : mParagraphSelections) {
			Paragraph paragraph = selection.getParagraph();
			if (paragraph != null) {
				paragraph.requestRedraw();
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		for (ParagraphSelection selection : mParagraphSelections) {
			builder.append(selection.toString(selection.getParagraph())).append(", ");
		}
		builder.append("]");
		return builder.toString();
	}

	private static void w(String msg) {
		Log.w("TexasSelection", msg);
	}

	
	public static class RectEdge {
		public float topY = -1;
		public float bottomY = -1;
		public float topX = -1;
		public float bottomX = -1;

		public float lineHeight = -1;

		@Override
		public String toString() {
			return "RectEdge{" +
					"topY=" + topY +
					", bottomY=" + bottomY +
					", topX=" + topX +
					", bottomX=" + bottomX +
					", lineHeight=" + lineHeight +
					'}';
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static Selection obtain(Type type, Styles styles) {
		return obtain(type, null, styles);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static Selection obtain(Type type, @Nullable TexasRecyclerView container, Styles styles) {
		Selection selection = POOL.acquire();
		if (selection == null) {
			selection = new Selection();
		}

		selection.mType = type;
		selection.mContainer = container;
		selection.mStyles = styles;
		selection.reuse();
		return selection;
	}

	public static class Styles {
		private int mBackgroundColor;
		private int mTextColor;

		private Source mSource;

		private boolean mEnableDrag = true;

		private int mVersion = 0;

		private Styles(int backgroundColor, int textColor, Source source) {
			mBackgroundColor = backgroundColor;
			mTextColor = textColor;
			mSource = source;
		}

		@RestrictTo(RestrictTo.Scope.LIBRARY)
		public int getVersion() {
			return mVersion;
		}

		@RestrictTo(RestrictTo.Scope.LIBRARY)
		public Source getSource() {
			return mSource;
		}

		public int getBackgroundColor() {
			return mBackgroundColor;
		}

		public int getTextColor() {
			return mTextColor;
		}

		@RestrictTo(RestrictTo.Scope.LIBRARY)
		public boolean isEnableDrag() {
			return mEnableDrag;
		}

		@RestrictTo(RestrictTo.Scope.LIBRARY)
		public void setSource(Source source) {
			mSource = source;
		}

		@RestrictTo(RestrictTo.Scope.LIBRARY)
		public Styles setEnableDrag(boolean enableDrag) {
			mEnableDrag = enableDrag;
			return this;
		}

		public void setBackgroundColor(int backgroundColor) {
			if (mBackgroundColor != backgroundColor) {
				++mVersion;
			}
			mBackgroundColor = backgroundColor;
		}

		public void setTextColor(int textColor) {
			if (mTextColor != textColor) {
				++mVersion;
			}
			mTextColor = textColor;
		}

		@Override
		public String toString() {
			return "Styles{" +
					"mBackgroundColor=" + String.format("#%08x", mBackgroundColor) +
					", mTextColor=" + String.format("#%08x", mTextColor) +
					", mEnableDrag=" + mEnableDrag +
					'}';
		}

		@RestrictTo(RestrictTo.Scope.LIBRARY)
		public static Selection.Styles createFromTouch(RenderOption option, boolean isLongClicked) {
			if (isLongClicked) {
				return new Selection.Styles(
						option.getSelectedByLongClickBackgroundColor(),
						option.getSelectedByLongClickTextColor(),
						Source.LONG_CLICKED
				);
			}

			return new Selection.Styles(
					option.getSelectedBackgroundColor(),
					option.getSelectedTextColor(),
					Source.CLICKED
			).setEnableDrag(false);
		}

		@RestrictTo(RestrictTo.Scope.LIBRARY)
		public static Selection.Styles createFromHighLight(RenderOption option) {
			return new Selection.Styles(
					Color.TRANSPARENT,
					option.getSpanHighlightTextColor(),
					Source.HIGHLIGHT
			);
		}

		public static Selection.Styles create(int backgroundColor, int textColor) {
			return new Selection.Styles(backgroundColor, textColor, Source.USER_DEFINED);
		}

		public void update(RenderOption option) {
			if (mSource == Source.USER_DEFINED) {
				return;
			}

			if (mSource == Source.CLICKED) {
				mBackgroundColor = option.getSelectedBackgroundColor();
				mTextColor = option.getSelectedTextColor();
			} else if (mSource == Source.LONG_CLICKED) {
				mBackgroundColor = option.getSelectedByLongClickBackgroundColor();
				mTextColor = option.getSelectedByLongClickTextColor();
			} else if (mSource == Source.HIGHLIGHT) {
				mBackgroundColor = Color.TRANSPARENT;
				mTextColor = option.getSpanHighlightTextColor();
			}
			++mVersion;
		}

		@RestrictTo(RestrictTo.Scope.LIBRARY)
		public enum Source {
			
			CLICKED,
			
			LONG_CLICKED,
			
			USER_DEFINED,
			
			HIGHLIGHT
		}
	}

	public static abstract class SelectionAnimatorListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
		private Styles mStyles;
		private Selection mSelection;

		public void setStyles(Selection selection) {
			mStyles = selection.getStyles();
			mSelection = selection;
		}

		@Override
		public final void onAnimationUpdate(@NonNull ValueAnimator animation) {
			int v = mStyles.getVersion();
			onUpdate(animation, mStyles);
			if (v != mStyles.getVersion()) {
				mSelection.refresh();
			}
		}

		protected abstract void onUpdate(ValueAnimator animation, Styles styles);

		@Override
		public final void onAnimationCancel(Animator animation) {
			int v = mStyles.getVersion();
			onAnimationCancel(animation, mStyles);
			if (v != mStyles.getVersion()) {
				mSelection.refresh();
			}
		}

		protected void onAnimationCancel(Animator animation, Styles styles) {

		}


		@Override
		public final void onAnimationEnd(Animator animation) {
			int v = mStyles.getVersion();
			onAnimationEnd(animation, mStyles);
			if (v != mStyles.getVersion()) {
				mSelection.refresh();
			}
		}

		@Override
		public final void onAnimationRepeat(Animator animation) {
			int v = mStyles.getVersion();
			onAnimationRepeat(animation, mStyles);
			if (v != mStyles.getVersion()) {
				mSelection.refresh();
			}
		}

		@Override
		public final void onAnimationStart(Animator animation) {
			int v = mStyles.getVersion();
			onAnimationStart(animation, mStyles);
			if (v != mStyles.getVersion()) {
				mSelection.refresh();
			}
		}

		@Override
		public final void onAnimationPause(Animator animation) {
			int v = mStyles.getVersion();
			onAnimationPause(animation, mStyles);
			if (v != mStyles.getVersion()) {
				mSelection.refresh();
			}
		}

		@Override
		public final void onAnimationResume(Animator animation) {
			int v = mStyles.getVersion();
			onAnimationResume(animation, mStyles);
			if (v != mStyles.getVersion()) {
				mSelection.refresh();
			}
		}

		protected void onAnimationEnd(Animator animation, Styles styles) {

		}

		protected void onAnimationRepeat(Animator animation, Styles styles) {

		}

		protected void onAnimationStart(Animator animation, Styles styles) {

		}

		protected void onAnimationPause(Animator animation, Styles styles) {

		}

		protected void onAnimationResume(Animator animation, Styles styles) {

		}

		@Override
		public final void onAnimationStart(@NonNull Animator animation, boolean isReverse) {
			int v = mStyles.getVersion();
			onAnimationStart(animation, isReverse, mStyles);
			if (v != mStyles.getVersion()) {
				mSelection.refresh();
			}
		}

		@Override
		public final void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
			int v = mStyles.getVersion();
			onAnimationEnd(animation, isReverse, mStyles);
			if (v != mStyles.getVersion()) {
				mSelection.refresh();
			}
		}

		protected void onAnimationStart(Animator animation, boolean isReverse, Styles styles) {

		}

		protected void onAnimationEnd(Animator animation, boolean isReverse, Styles styles) {

		}
	}

	
	public enum Type {
		
		HIGHLIGHT,
		
		SELECTION
	}
}
