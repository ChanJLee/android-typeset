package me.chan.texas.ext.markdown.math.renderer;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class GridGroupNode extends RendererNode {

	private final List<LinearGroupNode> mNodes = new ArrayList<>();
	private final int mColumnCount;

	public GridGroupNode(float scale, int columnCount, List<RendererNode> nodes) {
		super(scale);
		mColumnCount = columnCount;

		// spilt nodes
		int size = nodes.size() / columnCount;
		for (int i = 0; i < size; ++i) {
			mNodes.add(new LinearGroupNode(scale, nodes.subList(i * columnCount, (i + 1) * columnCount), LinearGroupNode.Gravity.HORIZONTAL));
		}
		if (nodes.size() % columnCount != 0) {
			mNodes.add(new LinearGroupNode(scale, nodes.subList(size * columnCount, nodes.size()), LinearGroupNode.Gravity.HORIZONTAL));
		}
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		float height = 0;
		float width = 0;
		for (RendererNode node : mNodes) {
			node.measure(paint);
			width = Math.max(width, node.getWidth());
			height += node.getHeight();
		}
		setMeasuredSize((int) Math.ceil(width), (int) Math.ceil(height));
	}

	@Override
	protected void onLayoutChildren() {
		float right = 0;
		for (int c = 0; c < mColumnCount; ++c) {
			RendererNode anchor = null;
			for (int r = 0; r < mNodes.size(); ++r) {
				LinearGroupNode group = mNodes.get(r);
				RendererNode node = group.tryGetChildAt(c);
				if (anchor == null) {
					anchor = node;
					continue;
				}

				if (node == null) {
					continue;
				}

				if (node.getWidth() > anchor.getWidth()) {
					anchor = node;
				}
			}
			if (anchor == null) {
				continue;
			}

			anchor.translate(right - anchor.getLeft(), 0);
			for (int r = 0; r < mNodes.size(); ++r) {
				LinearGroupNode group = mNodes.get(r);
				RendererNode node = group.tryGetChildAt(c);
				if (node == null || node == anchor) {
					continue;
				}

				node.translate(anchor.getCenterX() - node.getCenterX(), 0);
			}
			right = anchor.getRight();
		}

		float top = 0;
		for (int r = 0; r < mNodes.size(); ++r) {
			LinearGroupNode group = mNodes.get(r);
			group.resize();
			group.layout(0, top);
			top = group.getBottom();
		}
	}

	@Override
	public float getBaseline() {
		return getCenterY();
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		for (RendererNode node : mNodes) {
			node.draw(canvas, paint);
		}
	}

	@Override
	protected String toPretty() {
		return "Grid[]";
	}
}
