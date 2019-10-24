package me.chan.te.data;

import java.util.List;

import me.chan.te.misc.ObjectFactory;

/**
 * 绘制行
 */
public class Line implements Recyclable {
	private static final ObjectFactory<Line> POOL = new ObjectFactory<>(3000);

	// TODO list recycle
	private List<Box> mBoxes;
	private float mLineHeight;
	private float mLineWidth;
	private float mRatio;
	private float mSpaceWidth;

	private Line(List<Box> boxes, float lineWidth, float lineHeight, float ratio) {
		reset(boxes, lineWidth, lineHeight, ratio);
	}

	private void reset(List<Box> boxes, float lineWidth, float lineHeight, float ratio) {
		mBoxes = boxes;
		mLineHeight = lineHeight;
		mRatio = ratio;
		mLineWidth = lineWidth;
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

	@Override
	public void recycle() {
		reset(null, -1, -1, -1);
		POOL.release(this);
	}

	public static Line obtain(List<Box> boxes, float lineWidth, float lineHeight, float ratio) {
		Line line = POOL.acquire();
		if (line == null) {
			return new Line(boxes, lineWidth, lineHeight, ratio);
		}
		line.reset(boxes, lineWidth, lineHeight, ratio);
		return line;
	}
}
