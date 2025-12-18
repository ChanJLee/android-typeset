package me.chan.texas.ext.markdown.math.view;

import android.graphics.Canvas;

import androidx.annotation.Nullable;

public interface AsyncMathViewRenderer {
	/**
	 * @param width  宽度
	 * @param height 高度
	 * @return 获取一个canvas
	 */
	@Nullable
	Canvas lockCanvas(int width, int height);

	/**
	 * @param canvas 完成绘制 canvas 来自 {@link #lockCanvas(int, int)}
	 */
	void unlockCanvasAndPost(Canvas canvas);
}
