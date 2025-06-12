package me.chan.texas.measurer;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.RestrictTo;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.TextStyle;
import me.chan.texas.text.Paragraph;

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
	 * @param tag          {@link Paragraph.SpanBuilder#tag(Object)}
	 * @return 文本高度
	 */
	CharSequenceSpec measure(CharSequence charSequence, int start, int end, TextStyle textStyle, Object tag);

	/**
	 * @param charSequence 文本
	 * @param start        文本开始下表
	 * @param end          文本结束下标
	 * @param textStyle    text style
	 * @param tag          {@link Paragraph.SpanBuilder#tag(Object)}
	 * @param spec         输出
	 */
	void measure(CharSequence charSequence, int start, int end, TextStyle textStyle, Object tag, CharSequenceSpec spec);

	@RestrictTo(LIBRARY)
	class CharSequenceSpec extends DefaultRecyclable {
		private static final ObjectPool<CharSequenceSpec> POOL = new ObjectPool<>(16);

		private float mWidth;
		private float mHeight;
		private float mBaselineOffset;

		CharSequenceSpec() {
		}

		public void reset(float width, float height, float baselineOffset) {
			mWidth = width;
			mHeight = height;
			mBaselineOffset = baselineOffset;
		}

		public float getWidth() {
			return mWidth;
		}

		public float getHeight() {
			return mHeight;
		}

		public float getBaselineOffset() {
			return mBaselineOffset;
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
		protected void onRecycle() {
			mWidth = mHeight = mBaselineOffset = 0;
			POOL.release(this);
		}
	}
}