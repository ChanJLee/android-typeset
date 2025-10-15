package me.chan.texas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.IOException;

import me.chan.texas.ext.markdown.math.renderer.core.LatinModernRadicalRenderer;

public class MathView extends View {
	private final Paint textPaint;
	private LatinModernRadicalRenderer radicalRenderer;

	public MathView(Context context, @Nullable AttributeSet attrs) throws IOException {
		super(context, attrs);
		radicalRenderer = new LatinModernRadicalRenderer(context);
		textPaint = new Paint();
		textPaint.setTextSize(48);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 渲染 √(x²+y²)
		float x = 100;
		float y = 200;  // 基线
		float fontSize = 48;

		// 内容的高度和深度（简化，实际应该测量）
		float contentHeight = fontSize * 0.7f;
		float contentDepth = fontSize * 0.3f;

		// 绘制根号
		float radicalWidth = radicalRenderer.renderRadical(
				canvas, x, y,
				contentHeight, contentDepth,
				fontSize
		);

		// 绘制内容（x²+y²）
		canvas.drawText("x²+y²", x + radicalWidth, y, textPaint);
	}
}