package me.chan.texas.ext.markdown.math.renderer;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.List;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class BraceLayout extends RendererNode {

	private final RendererNode mLeftSymbol;
	private final RendererNode mRightSymbol;
	private final RendererNode mContent;

	public BraceLayout(float scale, @Nullable String leftSymbol, RendererNode content, @Nullable String rightSymbol) {
		super(scale);

		mLeftSymbol = TextUtils.isEmpty(leftSymbol) ? null : new TextNode(scale, leftSymbol);
		mRightSymbol = TextUtils.isEmpty(rightSymbol) ? null : new TextNode(scale, rightSymbol);
		mContent = content;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		float width = 0;
		if (mLeftSymbol != null) {
			mLeftSymbol.measure(paint);
			width += mLeftSymbol.getWidth();
		}

		if (mRightSymbol != null) {
			mRightSymbol.measure(paint);
			width += mRightSymbol.getWidth();
		}

		mContent.measure(paint);
		width += mContent.getWidth();
		setMeasuredSize((int) Math.ceil(width), mContent.getHeight());
	}

	@Override
	public float getBaseline() {
		return getCenterX();
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		mContent.draw(canvas, paint);
	}

	private void drawLeftSymbol() {

	}

	@Override
	protected String toPretty() {
		return "[]";
	}
}
