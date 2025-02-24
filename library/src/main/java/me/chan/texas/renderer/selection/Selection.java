package me.chan.texas.renderer.selection;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.ui.rv.TexasLayoutManager;
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.renderer.ui.text.TextureParagraph;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;

import java.util.ArrayList;
import java.util.List;

public class Selection extends DefaultRecyclable {
	private static final ObjectPool<Selection> POOL = new ObjectPool<>(8);

	private TexasRecyclerView mContainer;
	protected final List<Paragraph> mParagraphs = new ArrayList<>();
	private final RectEdge mRectEdge = new RectEdge();
	private Styles mStyles;

	protected Selection() {
	}

	protected Selection(TexasRecyclerView container, Styles styles) {
		mContainer = container;
		mStyles = styles;
	}

	public Styles getStyles() {
		return mStyles;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void add(Paragraph paragraph) {
		mParagraphs.add(paragraph);
	}

	@Nullable
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	ParagraphSelection getParagraphSelection(Paragraph paragraph) {
		return paragraph.getSelection();
	}

	/**
	 * @return 获取当前选中的tags，忽略空tag
	 */
	@Nullable
	public List<Paragraph> getSelectedParagraphs() {
		if (mParagraphs.isEmpty()) {
			return null;
		}

		return mParagraphs;
	}

	/**
	 * @return 选中区域边界
	 */
	private final int[] mLocations = new int[2];

	public RectEdge getSelectedRectEdge() {
		int size = mParagraphs.size();
		if (size == 0) {
			return null;
		}

		boolean hasModified = false;
		for (int i = 0; i < size; ++i) {
			Paragraph paragraph = mParagraphs.get(i);
			ParagraphSelection paragraphSelection = paragraph.getSelection();
			if (paragraphSelection == null || paragraphSelection.isSelectedRegionEmpty()) {
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
			Paragraph paragraph = mParagraphs.get(i);
			ParagraphSelection paragraphSelection = paragraph.getSelection();
			if (paragraphSelection == null || paragraphSelection.isSelectedRegionEmpty()) {
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
		return mParagraphs.size();
	}

	public Paragraph getParagraph(int index) {
		return mParagraphs.get(index);
	}

	public ParagraphSelection get(int index) {
		return getParagraph(index).getSelection();
	}

	@Override
	protected void onRecycle() {
		mParagraphs.clear();
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
		// 通知内容被清除的时候还需要
		for (Paragraph paragraph : mParagraphs) {
			if (paragraph.isRecycled()) {
				continue;
			}

			ParagraphSelection paragraphSelection = paragraph.getSelection();
			if (paragraphSelection == null) {
				continue;
			}

			paragraph.setSelection(null);
			try {
				paragraph.requestRedraw();
			} catch (Throwable ignore) {
				/* do nothing */
			}
			paragraphSelection.recycle();
		}
		mParagraphs.clear();
	}

	public boolean isEmpty() {
		return mParagraphs.isEmpty();
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
		for (Paragraph paragraph : mParagraphs) {
			paragraph.requestRedraw();
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		for (Paragraph paragraph : mParagraphs) {
			builder.append(paragraph.getSelection().toString(paragraph)).append(", ");
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
	public static Selection obtain(TexasRecyclerView container, Styles styles) {
		Selection selection = POOL.acquire();
		if (selection == null) {
			selection = new Selection();
		}

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
}
