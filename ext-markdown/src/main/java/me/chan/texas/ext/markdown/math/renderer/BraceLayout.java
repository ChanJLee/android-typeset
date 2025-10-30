package me.chan.texas.ext.markdown.math.renderer;

import android.text.TextUtils;

import androidx.annotation.Nullable;

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
		mContent.measure(paint);
		float scale = mContent.getHeight() * 1.0f / paint.getTextSize();

		if (mLeftSymbol != null) {
			mLeftSymbol.setScale(scale);
			mLeftSymbol.measure(paint);
			width += mLeftSymbol.getWidth();
		}

		if (mRightSymbol != null) {
			mRightSymbol.setScale(scale);
			mRightSymbol.measure(paint);
			width += mRightSymbol.getWidth();
		}

		width += mContent.getWidth();
		setMeasuredSize((int) Math.ceil(width), mContent.getHeight());
	}

	@Override
	protected void onLayoutChildren() {
		float left = 0;
		if (mLeftSymbol != null) {
			mLeftSymbol.layout(left, 0);
			left = mLeftSymbol.getRight();
		}

		mContent.layout(left, 0);
		left = mContent.getRight();

		if (mRightSymbol != null) {
			mRightSymbol.layout(left, 0);
		}
	}

	@Override
	public float getBaseline() {
		return getCenterX();
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		mContent.draw(canvas, paint);
		if (mLeftSymbol != null) {
			mLeftSymbol.draw(canvas, paint);
		}
		if (mRightSymbol != null) {
			mRightSymbol.draw(canvas, paint);
		}
	}

	@Override
	protected String toPretty() {
		return "[]";
	}
}
