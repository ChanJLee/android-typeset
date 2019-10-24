package me.chan.te.data;

import java.util.LinkedList;
import java.util.List;

import me.chan.te.misc.ObjectFactory;

/**
 * 绘制行
 */
public class Line implements Recyclable {
	private static final ObjectFactory<Line> POOL = new ObjectFactory<>(3000);

	private List<Box> mBoxes = new LinkedList<>();
	private float mLineHeight;
	private float mLineWidth;
	private float mRatio;
	private float mSpaceWidth;

	private Line() {
		reset();
	}

	private void reset() {
		mBoxes.clear();
		mLineHeight = -1;
		mRatio = -1;
		mLineWidth = -1;
	}

	public List<Box> getBoxes() {
		return mBoxes;
	}

	public float getLineHeight() {
		return mLineHeight;
	}

	public float getRatio() {
		return mRatio;
	}

	public float getLineWidth() {
		return mLineWidth;
	}

	public float getSpaceWidth() {
		return mSpaceWidth;
	}

	public void setSpaceWidth(float spaceWidth) {
		mSpaceWidth = spaceWidth;
	}

	public void setLineHeight(float lineHeight) {
		mLineHeight = lineHeight;
	}

	public void setLineWidth(float lineWidth) {
		mLineWidth = lineWidth;
	}

	public void setRatio(float ratio) {
		mRatio = ratio;
	}

	@Override
	public void recycle() {
		reset();
		POOL.release(this);
	}

	public static Line obtain() {
		Line line = POOL.acquire();
		if (line == null) {
			return new Line();
		}
		line.reset();
		return line;
	}

	public void add(Box box) {
		mBoxes.add(box);
	}

	public boolean isEmpty() {
		return mBoxes.isEmpty();
	}
}
