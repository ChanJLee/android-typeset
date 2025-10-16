package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.utils.CharArrayPool;
import me.chan.texas.utils.TexasUtils;

public class TextNode extends RendererNode {
	private static final CharArrayPool POOL = new CharArrayPool();

	private final String mContent;
	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private float mBaselineOffset;

	public TextNode(float scale, String content) {
		super(scale);
		mContent = content;
	}

	@Override
	protected void onMeasure(TexasPaint paint) {
		paint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		int height = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading);
		mBaselineOffset = (float) Math.ceil(fontMetrics.descent);

		int size = mContent.length();
		char[] buf = POOL.obtain(size);
		TexasUtils.getChars(mContent, 0, size, buf, 0);
		int width = (int) Math.ceil(paint.getRunAdvance(buf, 0, size, 0, size, false, size));
		POOL.release(buf);

		setMeasuredSize(width, height);
	}

	@Override
	protected void onDraw(TexasCanvas canvas) {
		canvas.save();
		canvas.drawText(mContent, 0, mBaselineOffset, );
		canvas.restore();
	}
}
