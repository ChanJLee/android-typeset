package me.chan.texas.typesetter;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import static me.chan.texas.typesetter.AbsParagraphTypesetter.DEBUG;

import android.util.Log;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Layout;
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
	 * @param lineSpace
	 * @return 排版是否成功
	 */
	public boolean typeset(Paragraph paragraph, BreakStrategy breakStrategy, int width, float lineSpace) {
		if (width <= 0) {
			throw new IllegalArgumentException("width must be positive: " + width);
		}

		if (typeset0(paragraph, breakStrategy, width)) {
			Layout layout = paragraph.getLayout();
			layout.setLineWidth(width);
			layout.setLineSpace(lineSpace);
			return true;
		}

		return false;
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
