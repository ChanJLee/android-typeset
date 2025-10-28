package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.Nullable;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class DecorGroupNode extends RendererNode {
	private final Builder mBuilder;

	private DecorGroupNode(float scale, Builder builder) {
		super(scale);
		mBuilder = builder;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mBuilder.center.measure(paint);
		if (mBuilder.leftTop != null) {
			mBuilder.leftTop.measure(paint);
		}
		if (mBuilder.leftBottom != null) {
			mBuilder.leftBottom.measure(paint);
		}
		if (mBuilder.rightTop != null) {
			mBuilder.rightTop.measure(paint);
		}
		if (mBuilder.rightBottom != null) {
			mBuilder.rightBottom.measure(paint);
		}
		if (mBuilder.top != null) {
			mBuilder.top.measure(paint);
		}
		if (mBuilder.bottom != null) {
			mBuilder.bottom.measure(paint);
		}

		preLayout();

		float top = mBuilder.center.getTop();
		float left = mBuilder.center.getLeft();
		float bottom = mBuilder.center.getBottom();
		float right = mBuilder.center.getRight();

		if (mBuilder.leftTop != null) {
			left = Math.min(left, mBuilder.leftTop.getLeft());
			top = Math.min(top, mBuilder.leftTop.getTop());
		}

		if (mBuilder.leftBottom != null) {
			left = Math.min(left, mBuilder.leftBottom.getLeft());
			bottom = Math.max(bottom, mBuilder.leftBottom.getBottom());
		}

		if (mBuilder.rightTop != null) {
			right = Math.max(right, mBuilder.rightTop.getRight());
			top = Math.min(top, mBuilder.rightTop.getTop());
		}

		if (mBuilder.rightBottom != null) {
			right = Math.max(right, mBuilder.rightBottom.getRight());
			bottom = Math.max(bottom, mBuilder.rightBottom.getBottom());
		}

		if (mBuilder.top != null) {
			top = Math.min(top, mBuilder.top.getTop());
			left = Math.min(left, mBuilder.top.getLeft());
			right = Math.max(right, mBuilder.top.getRight());
		}

		if (mBuilder.bottom != null) {
			bottom = Math.max(bottom, mBuilder.bottom.getBottom());
			left = Math.min(left, mBuilder.bottom.getLeft());
			right = Math.max(right, mBuilder.bottom.getRight());
		}

		setMeasuredSize((int) Math.ceil(right - left), (int) Math.ceil(bottom - top));

		float dx = 0;
		float dy = 0;
		if (top < 0) {
			dy = -top;
		}
		if (left < 0) {
			dx = -left;
		}

		mBuilder.center.translate(dx, dy);
		if (mBuilder.leftTop != null) {
			mBuilder.leftTop.translate(dx, dy);
		}
		if (mBuilder.leftBottom != null) {
			mBuilder.leftBottom.translate(dx, dy);
		}
		if (mBuilder.rightTop != null) {
			mBuilder.rightTop.translate(dx, dy);
		}
		if (mBuilder.rightBottom != null) {
			mBuilder.rightBottom.translate(dx, dy);
		}
		if (mBuilder.top != null) {
			mBuilder.top.translate(dx, dy);
		}
		if (mBuilder.bottom != null) {
			mBuilder.bottom.translate(dx, dy);
		}
	}

	@Override
	public float getBaseline() {
		return mBuilder.center.getBaseline();
	}

	private void preLayout() {
		float offsetYPercent = 0.5f;
		mBuilder.center.layout(0, 0);
		if (mBuilder.leftTop != null) {
			mBuilder.leftTop.layout(-mBuilder.leftTop.getWidth(), -mBuilder.leftTop.getHeight() * offsetYPercent);
		}
		if (mBuilder.leftBottom != null) {
			mBuilder.leftBottom.layout(-mBuilder.leftBottom.getWidth(), mBuilder.center.getBottom() - mBuilder.leftBottom.getHeight() * (1 - offsetYPercent));
		}
		if (mBuilder.rightTop != null) {
			mBuilder.rightTop.layout(mBuilder.center.getRight(), -mBuilder.rightTop.getHeight() * offsetYPercent);
		}
		if (mBuilder.rightBottom != null) {
			mBuilder.rightBottom.layout(mBuilder.center.getRight(), mBuilder.center.getBottom() - mBuilder.rightBottom.getHeight() * (1 - offsetYPercent));
		}
		if (mBuilder.top != null) {
			mBuilder.top.layout(0, -mBuilder.top.getHeight());
		}
		if (mBuilder.bottom != null) {
			mBuilder.bottom.layout(0, mBuilder.center.getBottom());
		}
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		mBuilder.center.draw(canvas, paint);
		if (mBuilder.leftTop != null) {
			mBuilder.leftTop.draw(canvas, paint);
		}
		if (mBuilder.leftBottom != null) {
			mBuilder.leftBottom.draw(canvas, paint);
		}
		if (mBuilder.rightTop != null) {
			mBuilder.rightTop.draw(canvas, paint);
		}
		if (mBuilder.rightBottom != null) {
			mBuilder.rightBottom.draw(canvas, paint);
		}
		if (mBuilder.top != null) {
			mBuilder.top.draw(canvas, paint);
		}
		if (mBuilder.bottom != null) {
			mBuilder.bottom.draw(canvas, paint);
		}
	}

	@Override
	protected String toPretty() {
		return "Decor{}";
	}

	public static class Builder {
		private final RendererNode center;
		private final float mScale;
		@Nullable
		private RendererNode leftTop;
		@Nullable
		private RendererNode leftBottom;
		@Nullable
		private RendererNode rightTop;
		@Nullable
		private RendererNode rightBottom;
		@Nullable
		private RendererNode top;
		@Nullable
		private RendererNode bottom;

		public Builder(float scale, RendererNode centerNode) {
			mScale = scale;
			center = centerNode;
		}

		public Builder leftTop(RendererNode node) {
			leftTop = node;
			return this;
		}

		public Builder leftBottom(RendererNode node) {
			leftBottom = node;
			return this;
		}

		public Builder rightTop(RendererNode node) {
			rightTop = node;
			return this;
		}

		public Builder rightBottom(RendererNode node) {
			rightBottom = node;
			return this;
		}

		public Builder top(RendererNode node) {
			top = node;
			return this;
		}

		public Builder bottom(RendererNode node) {
			bottom = node;
			return this;
		}

		public DecorGroupNode build() {
			return new DecorGroupNode(mScale, this);
		}
	}
}
