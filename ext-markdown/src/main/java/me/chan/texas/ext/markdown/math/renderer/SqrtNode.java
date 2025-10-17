package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.Nullable;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public class SqrtNode extends RendererNode {
	private final RendererNode mContent;

	@Nullable
	private final RendererNode mRoot;
	private final RendererNode mSymbol;

	public SqrtNode(float scale, RendererNode content, @Nullable RendererNode root) {
		super(scale);
		mContent = content;
		mRoot = root;
		mSymbol = new TextNode(scale, "√");
	}

	@Override
	protected void onMeasure(TexasPaint paint) {
		mContent.measure(paint);
		int width = mContent.getWidth();
		int height = mContent.getHeight();

		if (mRoot != null) {
			mRoot.measure(paint);
			width += mRoot.getWidth();
		}

		mSymbol.measure(paint);
		width += mSymbol.getWidth();

		setMeasuredSize(width, height);
	}

	@Override
	protected void onLayout(float left, float top) {
		if (mRoot != null) {
			mRoot.layout(left, top);
			left += mRoot.getWidth();
		}

		mSymbol.layout(left, top);
		left += mSymbol.getWidth();

		mContent.layout(left, top);
	}

	@Override
	protected void onDraw(TexasCanvas canvas, TexasPaint paint) {
		mContent.draw(canvas, paint);
		if (mRoot != null) {
			mRoot.draw(canvas, paint);
		}
		mSymbol.draw(canvas, paint);
	}

	@Override
	protected String toPretty() {
		return "sqrt {}";
	}
}
