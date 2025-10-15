package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public class TextMathNode extends MathNode {
	private CharSequence mText;
	private int mStart;
	private int mEnd;

	public TextMathNode(CharSequence text) {
		this(text, 0, text.length());
	}

	public TextMathNode(CharSequence text, int start, int end) {
		mText = text;
		mStart = start;
		mEnd = end;
	}

	@Override
	protected void onMeasure(TexasPaint paint) {
		throw new RuntimeException("Stub!");
	}

	@Override
	protected void onLayout(RectF bounds) {
		throw new RuntimeException("Stub!");
	}

	@Override
	protected void onDraw(TexasCanvas canvas) {
		throw new RuntimeException("Stub!");
	}
}
