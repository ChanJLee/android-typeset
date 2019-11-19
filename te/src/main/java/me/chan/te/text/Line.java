package me.chan.te.text;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.misc.DefaultRecyclable;
import me.chan.te.misc.ObjectFactory;

/**
 * 绘制行
 */
public class Line extends DefaultRecyclable {
	private static final ObjectFactory<Line> POOL = new ObjectFactory<>(4096);

	private List<Box> mBoxes = new ArrayList<>(150);
	private float mLineHeight;
	private float mLineWidth;
	private float mRatio;
	private float mSpaceWidth;
	private Gravity mGravity = Gravity.LEFT;

	private Line() {
		reset();
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
		if (isRecycled()) {
			return;
		}

		super.recycle();
		reset();
		POOL.release(this);
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

	public Box getBox(int index) {
		return mBoxes.get(index);
	}

	public static void clean() {
		POOL.clean();
	}

	public static Line obtain() {
		Line line = POOL.acquire();
		if (line == null) {
			return new Line();
		}
		line.reset();
		line.reuse();
		return line;
	}
}
