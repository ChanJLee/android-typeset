package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.renderer.core.graphics.TexasCanvas;

public abstract class MathGroupNode extends MathNode {

	@Nullable
	private List<MathNode> mChildren;

	public void addChild(MathNode node) {
		addChild(getChildCount(), node);
	}

	public final void addChild(int index, MathNode node) {
		if (mChildren == null) {
			mChildren = new ArrayList<>();
		}

		mChildren.add(index, node);
	}

	public final int getChildCount() {
		return mChildren == null ? 0 : mChildren.size();
	}

	public final MathNode getChildAt(int index) {
		if (mChildren == null) {
			throw new IndexOutOfBoundsException();
		}

		return mChildren.get(index);
	}

	@Override
	protected void onMeasure() {
		int size = getChildCount();
		for (int i = 0; i < size; ++i) {
			MathNode child = getChildAt(i);
			child.measure();
		}
	}

	@Override
	protected void onDraw(TexasCanvas canvas) {
		int size = getChildCount();
		for (int i = 0; i < size; ++i) {
			MathNode child = getChildAt(i);
			child.draw(canvas);
		}
	}
}
