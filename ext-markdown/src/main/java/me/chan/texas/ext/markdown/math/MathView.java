package me.chan.texas.ext.markdown.math;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvasImpl;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.graphics.GraphicsBuffer;
import me.chan.texas.renderer.core.graphics.TexasCanvasImpl;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;

public class MathView extends View {

	private final GraphicsBuffer mGraphicsBuffer;

	@Nullable
	private RendererNode mRendererNode;

	private final MathPaint mTexasPaint;
	private final MathCanvas mCanvas;

	public MathView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		TextPaint textPaint = new TextPaint();

		textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "texas_markdown_ext/latinmodern-math.otf"));
		textPaint.setTextSize(16);
		textPaint.setStyle(Paint.Style.FILL);

		TexasPaintImpl paint = new TexasPaintImpl();
		paint.reset(new PaintSet(textPaint));
		mTexasPaint = new MathPaintImpl(paint);
		mCanvas = new MathCanvasImpl(new TexasCanvasImpl());

		mGraphicsBuffer = new GraphicsBuffer();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mRendererNode == null) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}

		mRendererNode.measure(mTexasPaint);
		super.onMeasure(MeasureSpec.makeMeasureSpec(Math.max(MeasureSpec.getSize(widthMeasureSpec), mRendererNode.getWidth()), MeasureSpec.EXACTLY),
				heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mRendererNode != null) {
			mRendererNode.layout(0, 0);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mRendererNode == null) {
			return;
		}

		mCanvas.reset(canvas);
		mRendererNode.draw(mCanvas, mTexasPaint);
	}
}