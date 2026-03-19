package me.chan.texas.text;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.layout.TextBox;

/**
 * 文字样式
 */
public abstract class TextStyle {

	private static final AtomicInteger UUID = new AtomicInteger(Integer.MIN_VALUE);

	public static final TextStyle NONE = new TextStyle() {
		@Override
		public void update(TexasPaint textPaint, @NonNull TextBox box) {
			/* do nothing */
		}
	};
	public static final TextStyle BOLD = new TextStyle() {

		@Override
		public void update(TexasPaint textPaint, @NonNull TextBox box) {
			textPaint.setFakeBoldText(true);
		}
	};
	public static final TextStyle ITALIC = new TextStyle() {
		@Override
		public void update(TexasPaint textPaint, @NonNull TextBox box) {
			textPaint.setTextSkewX(-0.25f);
		}
	};

	public static final TextStyle BOLD_ITALIC = new TextStyle() {
		@Override
		public void update(TexasPaint textPaint, @NonNull TextBox box) {
			textPaint.setFakeBoldText(true);
			textPaint.setTextSkewX(-0.25f);
		}
	};

	private final int mId;

	public TextStyle() {
		mId = UUID.incrementAndGet();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public int getId() {
		return mId;
	}

	// 文字样式没法给定 DrawContext，因为 DrawContext 一定是排版完成后准备绘制才有
	// 但是我们需要在排版前就得用 text style 去确定文字大小

	/**
	 * 更新text paint的样式
	 *
	 * @param textPaint text paint
	 * @param box       box
	 */
	@AnyThread
	public abstract void update(@NonNull TexasPaint textPaint, @NonNull TextBox box);
}