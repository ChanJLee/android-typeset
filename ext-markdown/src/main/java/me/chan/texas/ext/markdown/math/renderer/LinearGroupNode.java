package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class LinearGroupNode extends RendererNode implements OptimizableRendererNode {
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
		if (mGravity != Gravity.HORIZONTAL) {
			return;
		}

		float left = 0;
		for (RendererNode node : mNodes) {
			node.layout(left, 0);
			left = node.getRight();
		}

		RendererNode anchor = mNodes.get(0);
		for (int i = 1; i < mNodes.size(); ++i) {
			RendererNode node = mNodes.get(i);
			if (node.getCenterY() > anchor.getCenterY()) {
				anchor = node;
			}
		}

		float top = 0;
		for (RendererNode node : mNodes) {
			node.translate(0, anchor.getCenterY() - node.getCenterY());
			top = Math.min(node.getTop(), top);
		}

		float bottom = 0;
		for (RendererNode node : mNodes) {
			node.translate(0, -top);
			bottom = Math.max(node.getBottom(), bottom);
		}

		setMeasuredSize((int) Math.ceil(left), (int) Math.ceil(bottom));
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

	public enum Gravity {
		HORIZONTAL,
		VERTICAL,
	}
}
