package com.shanbay.lib.texas.measurer;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.misc.ObjectPool;
import com.shanbay.lib.texas.text.TextStyle;

/**
 * 文字测量器
 */
@RestrictTo(LIBRARY)
public interface Measurer {

	/**
	 * @param charSequence 文本
	 * @param start        文本开始下表
	 * @param end          文本结束下标
	 * @param textStyle    text style
	 * @param tag          {@link com.shanbay.lib.texas.text.Paragraph.SpanBuilder#tag(Object)}
	 * @return 文本高度
	 */
	CharSequenceSpec measure(CharSequence charSequence, int start, int end, TextStyle textStyle, Object tag);

	/**
	 * @param charSequence 文本
	 * @param start        文本开始下表
	 * @param end          文本结束下标
	 * @param textStyle    text style
	 * @param tag          {@link com.shanbay.lib.texas.text.Paragraph.SpanBuilder#tag(Object)}
	 * @param spec         输出
	 */
	void measure(CharSequence charSequence, int start, int end, TextStyle textStyle, Object tag, CharSequenceSpec spec);

	@RestrictTo(LIBRARY)
	class CharSequenceSpec extends DefaultRecyclable {
		private static final ObjectPool<CharSequenceSpec> POOL = new ObjectPool<>(16);

		private float mWidth;
		private float mHeight;
		private float mFontTopPadding;
		private float mFontBottomPadding;
		private float mBaselineHeight;

		CharSequenceSpec() {
		}

		public void reset(float width, float height, float fontTopPadding, float fontBottomPadding, float baselineHeight) {
			mWidth = width;
			mHeight = height;
			mFontTopPadding = fontTopPadding;
			mFontBottomPadding = fontBottomPadding;
			mBaselineHeight = baselineHeight;
		}

		public float getWidth() {
			return mWidth;
		}

		public float getHeight() {
			return mHeight;
		}

		public float getFontTopPadding() {
			return mFontTopPadding;
		}

		public float getFontBottomPadding() {
			return mFontBottomPadding;
		}

		public float getBaselineOffset() {
			return mBaselineHeight;
		}

		public static CharSequenceSpec obtain() {
			CharSequenceSpec spec = POOL.acquire();
			if (spec == null) {
				spec = new CharSequenceSpec();
			}
			spec.reuse();
			return spec;
		}

		@Override
		public void recycle() {
			if (isRecycled()) {
				return;
			}

			super.recycle();
			mWidth = mHeight = mFontBottomPadding = mFontTopPadding = mBaselineHeight = 0;
			POOL.release(this);
		}
	}
}