package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class LinearGroupNode extends RendererNode implements OptimizableRendererNode, HorizontalCalibratedNode {
	private final List<RendererNode> mNodes;
	private final Gravity mGravity;

	public LinearGroupNode(MathPaint.Styles styles, List<RendererNode> nodes) {
		this(styles, nodes, Gravity.HORIZONTAL);
	}

	public LinearGroupNode(MathPaint.Styles styles, List<RendererNode> nodes, Gravity gravity) {
		super(styles);
		mNodes = nodes;
		mGravity = gravity;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		for (RendererNode node : mNodes) {
			node.measure(paint);
		}

		if (mNodes.isEmpty()) {
			setMeasuredSize(0, 0);
			return;
		}

		if (mGravity == Gravity.HORIZONTAL) {
			preLayout();
			return;
		}

		float height = 0;
		float width = 0;
		for (RendererNode node : mNodes) {
			height += node.getWidth();
			width = Math.max(width, node.getHeight());
		}
		setMeasuredSize((int) Math.ceil(height), (int) Math.ceil(width));
	}

	private void preLayout() {
		if (mGravity != Gravity.HORIZONTAL || mNodes.isEmpty()) {
			return;
		}

		preLayoutAlignCenter();
//		adjustHorizontalBaseline();
	}

	private void preLayoutAlignCenter() {
		RendererNode anchor = mNodes.get(0);
		anchor.layout(0, 0);
		float left = anchor.getRight();
		for (int i = 1; i < mNodes.size(); ++i) {
			RendererNode node = mNodes.get(i);
			node.layout(left, anchor.getCenterY() - node.getCenterY());
			left = node.getRight();
		}
		adjustBounds();
	}

	private void adjustHorizontalBaseline() {
		int anchorIndex = -1;
		for (int i = 0; i < mNodes.size(); ++i) {
			if (mNodes.get(i) instanceof HorizontalCalibratedNode) {
				anchorIndex = i;
				break;
			}
		}
		if (anchorIndex < 0) {
			return;
		}

		HorizontalCalibratedNode anchor = (HorizontalCalibratedNode) mNodes.get(anchorIndex);
		for (int i = anchorIndex + 1; i < mNodes.size(); ++i) {
			RendererNode node = mNodes.get(i);
			if (!(node instanceof HorizontalCalibratedNode)) {
				continue;
			}

			HorizontalCalibratedNode horizontalCalibratedNode = (HorizontalCalibratedNode) node;
			float dy = anchor.getBaseline() - horizontalCalibratedNode.getBaseline();
			node.translate(0, dy);
		}
		adjustBounds();
	}

	private void adjustBounds() {
		float top = 0;
		float bottom = 0;
		float right = 0;
		for (RendererNode node : mNodes) {
			top = Math.min(node.getTop(), top);
			bottom = Math.max(node.getBottom(), bottom);
			right = node.getRight();
		}
		for (RendererNode node : mNodes) {
			node.translate(0, -top);
		}
		setMeasuredSize((int) Math.ceil(right), (int) Math.ceil(bottom - top));
	}

	@Override
	protected void onLayoutChildren() {
		if (mGravity == Gravity.VERTICAL) {
			layoutVertical();
		}
	}

	private void layoutVertical() {
		float top = 0;
		float right = 0;
		for (RendererNode node : mNodes) {
			node.layout(0, top);
			top = node.getBottom();
			right = Math.max(right, node.getRight());
		}

		for (RendererNode node : mNodes) {
			node.translate((right - node.getWidth()) / 2, 0);
		}
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		for (RendererNode node : mNodes) {
			node.draw(canvas, paint);
		}
	}

	@Override
	protected String toPretty() {
		return "[]";
	}

	public int getChildCount() {
		return mNodes.size();
	}

	public RendererNode getChildAt(int index) {
		return mNodes.get(index);
	}

	@Nullable
	public RendererNode tryGetChildAt(int index) {
		if (index < 0 || index >= mNodes.size()) {
			return null;
		}
		return mNodes.get(index);
	}

	void resize() {
		if (mNodes.isEmpty()) {
			return;
		}

		RendererNode last = mNodes.get(mNodes.size() - 1);
		setMeasuredSize((int) Math.ceil(last.getRight()), getHeight());
	}

	@NonNull
	@Override
	public RendererNode optimize() {
		for (int i = 0; i < mNodes.size(); ++i) {
			RendererNode node = mNodes.get(i);
			if (node instanceof OptimizableRendererNode) {
				OptimizableRendererNode optimizableRendererNode = (OptimizableRendererNode) node;
				mNodes.set(i, optimizableRendererNode.optimize());
			}
		}

		if (mNodes.size() != 1) {
			return this;
		}

		RendererNode child = mNodes.get(0);
		if (child.getStyles() != getStyles()) {
			return this;
		}

		return child;
	}

	@Override
	public float getBaseline() {
		for (RendererNode rendererNode : mNodes) {
			if (rendererNode instanceof HorizontalCalibratedNode) {
				return ((HorizontalCalibratedNode) rendererNode).getBaseline();
			}
		}
		return getCenterY();
	}

	@Override
	protected void onDrawDebug(MathCanvas canvas, MathPaint paint) {
		super.onDrawDebug(canvas, paint);
		float y = getBaseline();
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(paint.getStrokeWidth() * 3);
		canvas.drawLine(0, y, getWidth(), y, paint);
	}

	public enum Gravity {
		HORIZONTAL,
		VERTICAL,
	}
}
