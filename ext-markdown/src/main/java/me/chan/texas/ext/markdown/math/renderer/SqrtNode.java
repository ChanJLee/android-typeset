package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Color;

import androidx.annotation.Nullable;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;


public class SqrtNode extends RendererNode {
	private final RendererNode mContent;

	@Nullable
	private final RendererNode mRoot;
	private final SqrtSymbolNode mSymbol;

	public SqrtNode(float scale, RendererNode content, @Nullable RendererNode root) {
		super(scale);
		mContent = content;
		mRoot = root;
		mSymbol = new SqrtSymbolNode(scale);
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mContent.measure(paint);
		mSymbol.measure(paint);
		mSymbol.setContentWidth(mContent.getWidth() + mSymbol.getKernAfterDegree());
		if (mRoot != null) {
			mRoot.measure(paint);
		}

		preLayout();
		float left = mSymbol.getLeft();
		float top = mSymbol.getTop();
		float right = mContent.getRight();
		float bottom = Math.max(mContent.getBottom(), mSymbol.getBottom());
		if (mRoot != null) {
			left = mRoot.getLeft();
		}

		setMeasuredSize((int) Math.ceil(right - left), (int) Math.ceil(bottom - top));
	}

	private void preLayout() {
		float left = 0;
		float top = 0;
		if (mRoot != null) {
			mRoot.layout(left, top);
			left = (mRoot.getRight() - mSymbol.getKernBeforeDegree());
			top = (mRoot.getBottom() + mSymbol.getHeight() * mSymbol.getDegreeBottomRaisePercent() - mSymbol.getHeight());
		}

		mSymbol.layout(left, top);
		left = (mSymbol.getRight() + mSymbol.getKernAfterDegree());

		mContent.layout(left, top + mSymbol.getTopPadding() + mSymbol.getVerticalGap());

		top = mSymbol.getTop();
		if (top < 0) {
			float dy = -top;
			if (mRoot != null) {
				mRoot.translate(0, dy);
			}
			mSymbol.translate(0, dy);
			mContent.translate(0, dy);
		}
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		int color = paint.getColor();

		if (DEBUG) {
			paint.setColor(Color.RED);
		}
		mContent.draw(canvas, paint);

		if (DEBUG) {
			paint.setColor(Color.BLUE);
		}
		if (mRoot != null) {
			mRoot.draw(canvas, paint);
		}

		if (DEBUG) {
			paint.setColor(Color.BLACK);
		}
		mSymbol.draw(canvas, paint);
		if (DEBUG) {
			paint.setColor(color);
		}
	}

	@Override
	protected String toPretty() {
		return "sqrt {}";
	}
}
