package me.chan.texas.ext.markdown.math.renderer;

import java.util.List;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class LinearGroupNode extends RendererNode {
	private final List<RendererNode> mNodes;
	private final Gravity mGravity;
	private float mBaseline;

	public LinearGroupNode(float scale, List<RendererNode> nodes, Gravity gravity) {
		super(scale);
		mNodes = nodes;
		mGravity = gravity;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		for (RendererNode node : mNodes) {
			node.measure(paint);
		}

		if (mGravity == Gravity.HORIZONTAL) {
			float height = 0;
			float width = 0;
			for (RendererNode node : mNodes) {
				height = Math.max(height, node.getHeight());
				width += node.getWidth();
			}
			setMeasuredSize((int) Math.ceil(width), (int) Math.ceil(height));
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

	@Override
	protected void onLayoutChildren() {
		if (mGravity == Gravity.HORIZONTAL) {
			layoutHorizontal();
			return;
		}

		layoutVertical();
	}

	@Override
	public float getBaseline() {
		return mBaseline;
	}

	private void layoutHorizontal() {
		float left = 0;
		float bottom = 0;
		for (RendererNode node : mNodes) {
			node.layout(left, 0);
			left = node.getRight();
			bottom = Math.max(bottom, node.getBottom());
		}

		for (RendererNode node : mNodes) {
			node.translate(0, (bottom - node.getHeight()) / 2);
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
		mBaseline = getCenterY();
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

	public enum Gravity {
		HORIZONTAL,
		VERTICAL,
	}
}
