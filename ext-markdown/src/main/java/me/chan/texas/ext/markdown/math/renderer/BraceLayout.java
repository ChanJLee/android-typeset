package me.chan.texas.ext.markdown.math.renderer;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class BraceLayout extends RendererNode {

	private final StretchyNode mLeftSymbol;
	private final StretchyNode mRightSymbol;
	private final RendererNode mContent;

	public BraceLayout(float scale, @Nullable StretchyNode leftSymbol, RendererNode content, @Nullable StretchyNode rightSymbol) {
		super(scale);

		mLeftSymbol = leftSymbol;
		mRightSymbol = rightSymbol;
		mContent = content;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		float width = 0;
		mContent.measure(paint);

		float left = 0;
		if (mLeftSymbol != null) {
			mLeftSymbol.measure(paint, RendererNode.makeMeasureSpec(0, RendererNode.UNSPECIFIED), RendererNode.makeMeasureSpec(mContent.getHeight(), RendererNode.EXACTLY));
			mLeftSymbol.layout(left, 0);
			width += mLeftSymbol.getWidth();
			left = mLeftSymbol.getRight();
		}

		mContent.layout(left, 0);
		left = mContent.getRight();

		if (mRightSymbol != null) {
			mRightSymbol.measure(paint, RendererNode.makeMeasureSpec(0, RendererNode.UNSPECIFIED), RendererNode.makeMeasureSpec(mContent.getHeight(), RendererNode.EXACTLY));
			mRightSymbol.layout(left, 0);
			width += mRightSymbol.getWidth();
		}

		width += mContent.getWidth();
		setMeasuredSize((int) Math.ceil(width), mContent.getHeight());
	}

	@Override
	public float getBaseline() {
		return getCenterY();
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
