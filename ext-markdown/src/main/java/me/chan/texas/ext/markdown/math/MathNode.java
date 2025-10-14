package me.chan.texas.ext.markdown.math;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.misc.RectF;

public abstract class MathNode {

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

	public final MathNode getChild(int index) {
		if (mChildren == null) {
			throw new IndexOutOfBoundsException();
		}

		return mChildren.get(index);
	}

	public final void measure() {
		/* TODO */
	}

	public final void layout(RectF bounds) {
		/* TODO */
	}

	public final void draw() {
		/* TODO */
	}
}
