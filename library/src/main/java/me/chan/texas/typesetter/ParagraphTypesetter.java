package me.chan.texas.typesetter;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import static me.chan.texas.typesetter.AbsParagraphTypesetter.DEBUG;

import android.util.Log;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.misc.RectF;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextGravity;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.typesetter.simple.SimpleParagraphTypesetter;
import me.chan.texas.typesetter.tex.TexParagraphTypesetter;
import me.chan.texas.typesetter.tex.TexParagraphTypesetterCompat;

@RestrictTo(LIBRARY)
public class ParagraphTypesetter {
	private final AbsParagraphTypesetter mTexTypesetter;
	private final AbsParagraphTypesetter mSimpleTypesetter;

	private final Status mStatus;

	public ParagraphTypesetter() {
		mTexTypesetter = Texas.isEnableTexCompat() ? new TexParagraphTypesetterCompat() : new TexParagraphTypesetter();
		mSimpleTypesetter = new SimpleParagraphTypesetter();
		mStatus = DEBUG ? new Status() : null;
	}

	/**
	 * width must be positive
	 *
	 * @param paragraph     要排版的段落
	 * @param breakStrategy 排版策略
	 * @param width         排版的宽度
	 * @return 排版是否成功
	 */
	public boolean typeset(Paragraph paragraph, BreakStrategy breakStrategy, int width) {
		if (width <= 0) {
			Log.w("ParagraphTypesetter", "width must be positive");
			return false;
		}

		if (typeset0(paragraph, breakStrategy, width)) {
			buildLayoutBounds(paragraph, width);
			return true;
		}

		return false;
	}

	private final RectF mLineRect = new RectF();
	private final RectF mBoxRect = new RectF();

	private void buildLayoutBounds(Paragraph paragraph, int width) {
		Layout layout = paragraph.getLayout();
		mLineRect.top = 0;
		int horizontalGravity = layout.getHorizontalGravity();
		int paddingLeft = layout.getPaddingLeft();
		float lineSpacingExtra = (int) layout.getLineSpacingExtra();
		mLineRect.bottom = layout.getPaddingTop() - lineSpacingExtra /* 为了方便叠加spacing */;
		for (int i = 0; i < layout.getLineCount(); ++i) {
			Line line = layout.getLine(i);
			getLineHorizontalBounds(horizontalGravity, line, mLineRect, width, paddingLeft);
			mLineRect.top = mLineRect.bottom + lineSpacingExtra;
			mLineRect.bottom = mLineRect.top + line.getLineHeight();
			Box prev = null;
			mBoxRect.set(mLineRect.left, mLineRect.top, mLineRect.left, mLineRect.bottom);
			for (int j = 0; j < line.getCount(); ++j) {
				Element element = line.getElement(j);
				if (element instanceof Box) {
					Box current = (Box) element;
					mBoxRect.right = mBoxRect.left + current.getWidth();
					if (prev != null) {
						linkBox(prev, current);
					}
					prev = current;
					mBoxRect.left = mBoxRect.right;
					continue;
				}
				mBoxRect.left += getAdjustGlueWidth(line, (Glue) element);
			}
		}
	}

	private static void linkBox(Box lhs, Box rhs) {
		RectF lhsInner = lhs.getInner();
		RectF lhsOuter = lhs.getOuter();
		RectF rhsInner = rhs.getInner();
		RectF rhsOuter = rhs.getOuter();

		float mid = (lhsInner.right + rhsInner.left) / 2.0f;
		lhsOuter.right = rhsOuter.left = mid;
	}

	private static float getAdjustGlueWidth(Line line, Glue glue) {
		float ratio = line.getRatio();
		if (ratio == 0) {
			return glue.getWidth();
		}

		if (ratio > 0) {
			return glue.getWidth() + ratio * glue.getStretch();
		}

		return glue.getWidth() + ratio * glue.getShrink();
	}

	private static void getLineHorizontalBounds(int horizontalGravity, Line line, RectF bounds, int width, int paddingLeft) {
		if (horizontalGravity == TextGravity.START) {
			bounds.left = paddingLeft;
		} else if (horizontalGravity == TextGravity.CENTER_HORIZONTAL) {
			float offsetX = (width - line.getLineWidth()) / 2.0f;
			bounds.left = paddingLeft + offsetX;
		} else if (horizontalGravity == TextGravity.END) {
			float offsetX = width - line.getLineWidth();
			bounds.left = paddingLeft + offsetX;
		} else {
			throw new IllegalStateException("unknown text gravity");
		}
		bounds.right = bounds.left + line.getLineWidth();
	}

	/**
	 * @param paragraph     要排版的段落
	 * @param breakStrategy 排版策略
	 * @return 排版是否成功
	 */
	public boolean desire(Paragraph paragraph, BreakStrategy breakStrategy) {
		if (!typeset0(paragraph, breakStrategy, AbsParagraphTypesetter.INFINITY_WIDTH)) {
			return false;
		}

		Layout layout = paragraph.getLayout();
		float actualWidth = 0;
		for (int i = 0; i < layout.getLineCount(); ++i) {
			Line line = layout.getLine(i);
			actualWidth = Math.max(line.getLineWidth(), actualWidth);
		}
		buildLayoutBounds(paragraph, (int) Math.ceil(actualWidth));
		return true;
	}

	private boolean typeset0(Paragraph paragraph, BreakStrategy breakStrategy, int width) {
		if (DEBUG) {
			++mStatus.mCount;
			mStatus.mInternalState = null;
		}

		if (breakStrategy == BreakStrategy.SIMPLE) {
			return mSimpleTypesetter.typeset(paragraph, breakStrategy, width);
		}

		if (!mTexTypesetter.typeset(paragraph, breakStrategy, width)) {
			// tex 存在找不到完美解的情况，如果在这种case下
			// 回归到朴素的排版算法
			if (DEBUG) {
				++mStatus.mFallbackCount;
				Log.w("ParagraphTypesetter", "can not find active nodes: " + paragraph);
			}

			return mSimpleTypesetter.typeset(paragraph, breakStrategy, width);
		}

		if (DEBUG) {
			mStatus.mInternalState = mTexTypesetter.getInternalState();
		}
		return true;
	}

	public Object getInternalState() {
		return mStatus;
	}

	public String stats() {
		return TexParagraphTypesetter.stats();
	}

	public static class Status {
		private int mCount;
		private int mFallbackCount;
		private Object mInternalState;

		public int getCount() {
			return mCount;
		}

		public int getFallbackCount() {
			return mFallbackCount;
		}

		public Object getInternalState() {
			return mInternalState;
		}

		@Override
		public String toString() {
			float ratio = 0;
			if (mCount != 0) {
				ratio = mFallbackCount * 1.0f / mCount;
			}
			return "Status{" +
					"mCount=" + mCount +
					", mFallbackCount=" + mFallbackCount +
					", ratio: " + ratio +
					'}';
		}
	}
}
