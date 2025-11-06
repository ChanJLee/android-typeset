package me.chan.texas;

import static me.chan.texas.ext.markdown.math.renderer.MathRendererInflater.mockText;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.IOException;

import me.chan.texas.ext.markdown.math.renderer.AccentNode;
import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.MathRendererInflater;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvasImpl;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.graphics.TexasCanvasImpl;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;

public class MathView extends View {

	private RendererNode mRendererNode;

	private MathPaint mTexasPaint;
	private MathCanvas mCanvas;

	public MathView(Context context, @Nullable AttributeSet attrs) throws IOException {
		super(context, attrs);
		TextPaint textPaint = new TextPaint();

		textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "texas_markdown_ext/latinmodern-math.otf"));
		textPaint.setTextSize(16);
		textPaint.setStyle(Paint.Style.FILL);

//		mRendererNode = new TextNode(1f, "Hello World!");
//		mRendererNode = RendererNodeInflater.mockText("ABgface + x + y");
//		mRendererNode.setScale(0.5f);
//		mRendererNode = RendererNodeInflater.mockSqrt();
//		mRendererNode = RendererNodeInflater.mockFractionNode();
//		{
//			mRendererNode = RendererNodeInflater.mockText();
//			textPaint.setTextSize(48);
//		}
//
		{
			textPaint.setTextSize(128);
			mRendererNode = MathRendererInflater.mockList();
		}

		TexasPaintImpl paint = new TexasPaintImpl();
		paint.reset(new PaintSet(textPaint));
		mTexasPaint = new MathPaintImpl(paint);

		mCanvas = new MathCanvasImpl(new TexasCanvasImpl());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mRendererNode.measure(mTexasPaint);
		super.onMeasure(MeasureSpec.makeMeasureSpec(mRendererNode.getWidth(), MeasureSpec.EXACTLY),
				heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mRendererNode.layout(0, 0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mCanvas.reset(canvas);
		mRendererNode.draw(mCanvas, mTexasPaint);
	}

	public void setRendererNode(RendererNode rendererNode) {
		mRendererNode = rendererNode;
		requestLayout();
	}
}