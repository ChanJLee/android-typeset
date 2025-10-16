package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.Nullable;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public class SqrtNode extends RendererNode {
	private final RendererNode mContent;

	@Nullable
	private final RendererNode mRoot;
	// TODO 从配置中读取
	private final int mVerticalOffset = 50;
	private final int mHorizontalOffset = 50;

	public SqrtNode(float scale, RendererNode content, @Nullable RendererNode root) {
		super(scale);
		mContent = content;
		mRoot = root;
	}

	@Override
	protected void onMeasure(TexasPaint paint) {
		mContent.measure(paint);
		int width = mContent.getWidth() + mVerticalOffset;
		int height = mContent.getHeight() + mHorizontalOffset;

		if (mRoot != null) {
			mRoot.measure(paint);
			width += mRoot.getWidth();
		}

		setMeasuredSize(width, height);
	}

	@Override
	protected void onLayout(float left, float top, float right, float bottom) {
		if (mRoot != null) {
			mRoot.layout(left, top + mVerticalOffset, left + mRoot.getWidth(), top + mVerticalOffset + mRoot.getHeight());
			left += mRoot.getWidth();
		}

		mContent.layout(left, top + mVerticalOffset, left + mContent.getWidth(), top + mVerticalOffset + mContent.getHeight());
	}

	@Override
	protected void onDraw(TexasCanvas canvas, TexasPaint paint) {
		canvas.save();
		canvas.translate(mContent.getLeft() - getLeft(), mContent.getTop() - getTop());
		mContent.draw(canvas, paint);
		canvas.restore();
	}
}
