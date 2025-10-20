package me.chan.texas.ext.markdown.math.renderer;

import java.util.List;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public class GridGroupNode extends RendererNode {

	private final List<RendererNode> mNodes;
	private final int mColumnCount;

	public GridGroupNode(float scale, int columnCount, List<RendererNode> nodes) {
		super(scale);
		mColumnCount = columnCount;
		mNodes = nodes;
	}

	@Override
	protected void onMeasure(TexasPaint paint) {
		for (RendererNode node : mNodes) {
			node.measure(paint);
		}
	}

	@Override
	protected void onLayoutChildren() {
		int size = mNodes.size();
		int rowCount = size / mColumnCount;
		if (size % mColumnCount != 0) {
			++rowCount;
		}

		float top = 0;
		for (int r = 0; r < rowCount; ++r) {
			float left = 0;
			float bottom = 0;
			for (int c = 0; c < mColumnCount; ++c) {
				int i = r * mColumnCount + c;
				RendererNode node = mNodes.get(i);
				node.layout(left, top);
				left = node.getRight();
				bottom = Math.max(node.getBottom(), bottom);
			}

			for (int c = 0; c < mColumnCount; ++c) {
				int i = r * mColumnCount + c;
				RendererNode node = mNodes.get(i);
				node.translate(0, (bottom - top - node.getHeight()) / 2);
			}

			top = bottom;
		}

		float left = 0;
		for (int c = 0; c < mColumnCount; ++c) {
			float right = 0;
			for (int r = 0; r < rowCount; ++r) {
				int i = r * mColumnCount + c;
				RendererNode node = mNodes.get(i);
				right = Math.max(node.getRight(), right);
			}

			for (int r = 0; r < rowCount; ++r) {
				int i = r * mColumnCount + c;
				RendererNode node = mNodes.get(i);
				node.translate((right - left - node.getWidth()) / 2, 0);
			}
			left = right;
		}
	}

	@Override
	protected void onDraw(TexasCanvas canvas, TexasPaint paint) {
		for (RendererNode node : mNodes) {
			node.draw(canvas, paint);
		}
	}

	@Override
	protected String toPretty() {
		return "Grid[]";
	}
}
