package me.chan.texas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.IOException;

import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.RendererNodeInflater;
import me.chan.texas.ext.markdown.math.renderer.TextNode;
import me.chan.texas.ext.markdown.math.renderer.core.LatinModernRadicalRenderer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.graphics.TexasCanvasImpl;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;

public class MathView extends View {
	private LatinModernRadicalRenderer radicalRenderer;
	private Font mathFont;

	private RendererNode mRendererNode;

	private TexasPaint mTexasPaint;
	private TexasCanvasImpl mCanvas;

	public MathView(Context context, @Nullable AttributeSet attrs) throws IOException {
		super(context, attrs);
		radicalRenderer = new LatinModernRadicalRenderer(context);
		TextPaint textPaint = new TextPaint();

		textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "texas_markdown_ext/latinmodern-math.otf"));
		textPaint.setTextSize(48);
		textPaint.setStyle(Paint.Style.STROKE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			mathFont = new Font.Builder(context.getAssets(), "texas_markdown_ext/latinmodern-math.otf").build();
		}

//		mRendererNode = new TextNode(1f, "Hello World!");
		mRendererNode = RendererNodeInflater.mockSqrt();

		mTexasPaint = new TexasPaintImpl();
		mTexasPaint.reset(new PaintSet(textPaint));

		mCanvas = new TexasCanvasImpl();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mRendererNode.measure(mTexasPaint);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mRendererNode.layout(left + 10, top + 10, right - 10, bottom - 10);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mCanvas.reset(canvas);
		mRendererNode.draw(mCanvas, mTexasPaint);
	}

//	private void drawRadical(Canvas canvas, float fontSize, float x, float y, Paint paint) {
//		canvas.save();
//
//		// 计算缩放比例
//		float unit = 1000;
//		float scale = fontSize / unit;
//
//		canvas.drawLine(x - fontSize, y + 10, x, y + 10, textPaint);
//		canvas.translate(x, y);
//
//		canvas.scale(scale, scale);
//
//		// 绘制字形（使用 Font 而非 Typeface）
//		int[] glyphIds = {3077};
//		float[] positions = {0, 0};
//
//		paint.setTextSize(unit);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//			canvas.drawGlyphs(glyphIds, 0, positions, 0, 1, mathFont, paint);
//		}
//		paint.setTextSize(fontSize);
//
//		canvas.restore();
//	}
}