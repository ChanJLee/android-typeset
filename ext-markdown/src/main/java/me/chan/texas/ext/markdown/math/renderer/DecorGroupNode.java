package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class DecorGroupNode extends RendererNode implements OptimizableRendererNode {
	private final Builder mBuilder;

	private DecorGroupNode(MathPaint.Styles styles, Builder builder) {
		super(styles);
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
		if (mBuilder.left != null) {
			mBuilder.left.measure(paint);
		}
		if (mBuilder.right != null) {
			mBuilder.right.measure(paint);
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

		if (mBuilder.left != null) {
			left = Math.min(left, mBuilder.left.getLeft());
			top = Math.min(top, mBuilder.left.getTop());
			bottom = Math.max(bottom, mBuilder.left.getBottom());
		}

		if (mBuilder.right != null) {
			right = Math.max(right, mBuilder.right.getRight());
			top = Math.min(top, mBuilder.right.getTop());
			bottom = Math.max(bottom, mBuilder.right.getBottom());
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
		if (mBuilder.left != null) {
			mBuilder.left.translate(dx, dy);
		}
		if (mBuilder.right != null) {
			mBuilder.right.translate(dx, dy);
		}
	}

	private void preLayout() {
		float offsetYPercent = 0.5f;
		mBuilder.center.layout(0, 0);

		float offsetX = mBuilder.mStyles.getTextSize() * 0.2f;

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
			mBuilder.top.layout(mBuilder.center.getCenterX() - mBuilder.top.getWidth() / 2.0f, -mBuilder.top.getHeight());
		}
		if (mBuilder.bottom != null) {
			mBuilder.bottom.layout(mBuilder.center.getCenterX() - mBuilder.bottom.getWidth() / 2.0f, mBuilder.center.getBottom());
		}
		if (mBuilder.left != null) {
			mBuilder.left.layout(-mBuilder.left.getWidth(), mBuilder.center.getBottom() - mBuilder.left.getHeight() - offsetX);
		}
		if (mBuilder.right != null) {
			mBuilder.right.layout(mBuilder.center.getRight() + offsetX, mBuilder.center.getBottom() - mBuilder.right.getHeight());
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
		if (mBuilder.left != null) {
			mBuilder.left.draw(canvas, paint);
		}
		if (mBuilder.right != null) {
			mBuilder.right.draw(canvas, paint);
		}
	}

	@Override
	protected String toPretty() {
		return "Decor{}";
	}

	@NonNull
	@Override
	public RendererNode optimize() {
		boolean hasDecor = false;
		if (mBuilder.top instanceof OptimizableRendererNode) {
			hasDecor = true;
			mBuilder.top = optimize((OptimizableRendererNode) mBuilder.top);
		}
		if (mBuilder.bottom instanceof OptimizableRendererNode) {
			hasDecor = true;
			mBuilder.bottom = optimize((OptimizableRendererNode) mBuilder.bottom);
		}
		if (mBuilder.left instanceof OptimizableRendererNode) {
			hasDecor = true;
			mBuilder.left = optimize((OptimizableRendererNode) mBuilder.left);
		}
		if (mBuilder.right instanceof OptimizableRendererNode) {
			hasDecor = true;
			mBuilder.right = optimize((OptimizableRendererNode) mBuilder.right);
		}
		if (mBuilder.leftTop instanceof OptimizableRendererNode) {
			hasDecor = true;
			mBuilder.leftTop = optimize((OptimizableRendererNode) mBuilder.leftTop);
		}
		if (mBuilder.leftBottom instanceof OptimizableRendererNode) {
			hasDecor = true;
			mBuilder.leftBottom = optimize((OptimizableRendererNode) mBuilder.leftBottom);
		}
		if (mBuilder.rightTop instanceof OptimizableRendererNode) {
			hasDecor = true;
			mBuilder.rightTop = optimize((OptimizableRendererNode) mBuilder.rightTop);
		}
		if (mBuilder.rightBottom instanceof OptimizableRendererNode) {
			hasDecor = true;
			mBuilder.rightBottom = optimize((OptimizableRendererNode) mBuilder.rightBottom);
		}
		if (mBuilder.center instanceof OptimizableRendererNode) {
			mBuilder.center = optimize((OptimizableRendererNode) mBuilder.center);
		}

		if (hasDecor) {
			return this;
		}

		if (getStyles() != mBuilder.center.getStyles()) {
			return this;
		}

		return mBuilder.center;
	}

	private RendererNode optimize(OptimizableRendererNode node) {
		return node.optimize();
	}

	public static class Builder {
		private RendererNode center;
		private final MathPaint.Styles mStyles;
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
		@Nullable
		private RendererNode left;
		@Nullable
		private RendererNode right;

		public Builder(MathPaint.Styles styles, RendererNode centerNode) {
			mStyles = styles;
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

		public Builder left(RendererNode node) {
			left = node;
			return this;
		}

		public Builder right(RendererNode node) {
			right = node;
			return this;
		}

		public DecorGroupNode build() {
			return new DecorGroupNode(mStyles, this);
		}
	}
}
