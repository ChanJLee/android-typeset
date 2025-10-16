package me.chan.texas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.IOException;

import me.chan.texas.ext.markdown.math.renderer.core.LatinModernRadicalRenderer;

public class MathView extends View {
	private final Paint textPaint;
	private LatinModernRadicalRenderer radicalRenderer;
	private Font mathFont;

	public MathView(Context context, @Nullable AttributeSet attrs) throws IOException {
		super(context, attrs);
		radicalRenderer = new LatinModernRadicalRenderer(context);
		textPaint = new Paint();

		textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "texas_markdown_ext/latinmodern-math.otf"));
		textPaint.setTextSize(48);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			mathFont = new Font.Builder(context.getAssets(), "texas_markdown_ext/latinmodern-math.otf").build();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 渲染 √(x²+y²)
		float x = 500;
		float y = 200;  // 基线
		float fontSize = 48;

		textPaint.setTextSize(fontSize);

		// 绘制根号

		// 绘制内容（x²+y²）
		canvas.drawText("x²+y²", x, y, textPaint);

		drawRadical(canvas, fontSize, x, y, textPaint);
	}

	private void drawRadical(Canvas canvas, float fontSize, float x, float y, Paint paint) {
		canvas.save();

		// 计算缩放比例
		float unit = 1000;
		float scale = fontSize / unit;

		canvas.translate(x, y);
		canvas.scale(scale, scale);

		// 绘制字形（使用 Font 而非 Typeface）
		int[] glyphIds = { 3077 };
		float[] positions = { 0, 0 };

		paint.setTextSize(unit);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			canvas.drawGlyphs(glyphIds, 0, positions, 0, 1, mathFont, paint);
		}
		paint.setTextSize(fontSize);

		canvas.restore();
	}
}