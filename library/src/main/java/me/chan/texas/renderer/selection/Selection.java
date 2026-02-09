package me.chan.texas.renderer.selection;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;

import me.chan.texas.R;
import me.chan.texas.misc.Rect;
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
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.ViewSegment;
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

	/**
	 * @return 获取当前选中的tags，忽略空tag
	 */
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

	/**
	 * @return 选中区域边界
	 */
	private final Rect mLocations = new Rect();

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

	@Nullable
	public RectEdge getSelectedRectEdge() {
		return getSelectedRectEdge(false);
	}

	/**
	 * @param strict 是否是严格模式
	 * @return 选中区域边界
	 */
	@VisibleForTesting
	@Nullable
	RectEdge getSelectedRectEdge(boolean strict) {
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
			if (!result && strict) {
				throw new RuntimeException("get paragraph location failed");
			}

			RectF firstRegion = paragraphSelection.getFirstRegion();
			assert firstRegion != null;

			mRectEdge.topY = firstRegion.top + mLocations.top;
			mRectEdge.topX = firstRegion.left + mLocations.left;
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
			if (!result && strict) {
				throw new RuntimeException("get paragraph location failed");
			}

			RectF lastRegion = paragraphSelection.getLastRegion();
			assert lastRegion != null;

			mRectEdge.bottomY = lastRegion.bottom + mLocations.top;
			mRectEdge.bottomX = lastRegion.right + mLocations.left;
			break;
		}

		return hasModified ? mRectEdge : null;
	}

	boolean getParagraphLocation(TexasRecyclerView container, Paragraph paragraph, Rect locations) {
		Document document = container.getDocument();
		if (document == null) {
			return false;
		}

		int index = document.indexOfSegment(paragraph);
		if (index == -1) {
			return false;
		}

		ViewSegment viewSegment = paragraph.getTag(R.id.me_chan_texas_paragraph_outer_segment);
		if (viewSegment == null) {
			return container.getSegmentLocations(paragraph, locations);
		}

		SelectionProvider provider = viewSegment.getSelectionProvider();
		if (provider == null) {
			return container.getSegmentLocations(paragraph, locations);
		}

		for (int i = 0; i < provider.getParagraphCount(); ++i) {
			if (provider.getParagraph(i) == paragraph) {
				return container.getViewSegmentParagraphLocations(viewSegment, paragraph, locations);
			}
		}
		return false;
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

	/**
	 * 清除选中
	 */
	public void clear() {
		if (isInvalidate()) {
			return;
		}

		// 通知内容被清除的时候还需要
		for (ParagraphSelection paragraphSelection : mParagraphSelections) {
			Paragraph paragraph = paragraphSelection.getParagraph();
			paragraph.setSelection(mType, null);
			try {
				paragraph.requestRedraw();
			} catch (Throwable ignore) {
				/* do nothing */
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

	/**
	 * Coordinate system base on screen
	 */
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
			/**
			 * 点击
			 */
			CLICKED,
			/**
			 * 长按
			 */
			LONG_CLICKED,
			/**
			 * 用户自定义
			 */
			USER_DEFINED,
			/**
			 * 高亮
			 */
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

		// 实现 AnimatorListenerAdapter 剩下的接口
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

	/**
	 * 选中类型，不同的选中类型允许同时存在，相同的选中类型只有一个
	 */
	public enum Type {
		/**
		 * 高亮 {@link me.chan.texas.renderer.TexasView#highlightParagraphs(ParagraphPredicates)}
		 */
		HIGHLIGHT,
		/**
		 * 选中，选中可以分为单击、长按、自由选择。其中自由选择和长按效果默认一致，当然你也可以自己定义
		 * {@link me.chan.texas.renderer.TexasView#selectParagraphs(ParagraphPredicates)}
		 */
		SELECTION
	}
}
