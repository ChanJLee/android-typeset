package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Color;

import androidx.annotation.Nullable;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public class SqrtNode extends RendererNode {
	private final RendererNode mContent;

	@Nullable
	private final RendererNode mRoot;
	private final SqrtSymbolNode mSymbol;

	public SqrtNode(RendererNode content, @Nullable RendererNode root) {
		mContent = content;
		mRoot = root;
		mSymbol = new SqrtSymbolNode();
//		if (mRoot != null) {
//			mRoot.setScale(0.5f);
//		}
	}

	@Override
	protected void onMeasure(TexasPaint paint) {
		mContent.measure(paint);
		mSymbol.measure(paint);
		mSymbol.setContentWidth(mContent.getWidth() + mSymbol.getKernAfterDegree());

		int width = (int) Math.ceil(mSymbol.getWidth() + mSymbol.getContentWidth());
		if (mRoot != null) {
			mRoot.measure(paint);
			width += (int) Math.ceil(mRoot.getWidth() + mSymbol.getKernBeforeDegree());
		}

		setMeasuredSize(width, mSymbol.getHeight());
	}

	@Override
	protected void onLayoutChildren() {
		float left = 0;
		float top = 0;
		float bottom = top + getHeight();
		if (mRoot != null) {
			mRoot.layout(left, bottom - mSymbol.getDegreeBottomRaisePercent() * mSymbol.getHeight() - mRoot.getHeight());
			left += (mRoot.getWidth() - mSymbol.getKernBeforeDegree());
		}

		mSymbol.layout(left, top);
		left += (mSymbol.getWidth() + mSymbol.getKernAfterDegree());

		mContent.layout(left, top + mSymbol.getExtraAscender() + mSymbol.getRuleThickness() + mSymbol.getVerticalGap());
	}

	@Override
	protected void onDraw(TexasCanvas canvas, TexasPaint paint) {
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
