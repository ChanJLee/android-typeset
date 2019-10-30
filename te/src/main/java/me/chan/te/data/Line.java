package me.chan.te.data;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.misc.ObjectFactory;
import me.chan.te.text.Gravity;

/**
 * 绘制行
 */
public class Line implements Recyclable {
	private static final ObjectFactory<Line> POOL = new ObjectFactory<>(6000);

	private List<Box> mBoxes = new ArrayList<>(150);
	private float mLineHeight;
	private float mLineWidth;
	private float mRatio;
	private float mSpaceWidth;
	private Gravity mGravity = Gravity.LEFT;

	private Line() {
		reset();
	}

	public static void clean() {
		POOL.clean();
	}

	private void reset() {
		mBoxes.clear();
		mLineHeight = -1;
		mLineWidth = -1;
		mRatio = -1;
		mSpaceWidth = -1;
		mGravity = Gravity.LEFT;
	}

	public Gravity getGravity() {
		return mGravity;
	}

	// TODO
	public List<Box> getBoxes() {
		return mBoxes;
	}

	public float getLineHeight() {
		return mLineHeight;
	}

	public float getRatio() {
		return mRatio;
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

	public void setGravity(Gravity gravity) {
		mGravity = gravity;
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

	public int getCount() {
		return mBoxes.size();
	}

	public void add(Box box) {
		mBoxes.add(box);
	}

	public boolean isEmpty() {
		return mBoxes.isEmpty();
	}

	public float getLineWidth() {
		return mLineWidth;
	}
}
