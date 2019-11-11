package me.chan.te.text;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.misc.DefaultRecyclable;
import me.chan.te.misc.ObjectFactory;

public class Page extends DefaultRecyclable {
	private static final ObjectFactory<Page> POOL = new ObjectFactory<>(120);

	private List<Segment> mSegments = new ArrayList<>(512);

	private float mWidth;
	private float mHeight;

	private Page() {
	}

	public float getWidth() {
		return mWidth;
	}

	public void setWidth(float width) {
		mWidth = width;
	}

	public float getHeight() {
		return mHeight;
	}

	public void setHeight(float height) {
		mHeight = height;
	}

	/**
	 * 获取段落数目
	 *
	 * @return 段落数目
	 */
	public int getSegmentCount() {
		return mSegments.size();
	}

	public Segment getSegment(int index) {
		return mSegments.get(index);
	}

	public void addSegment(Segment segment) {
		mSegments.add(segment);
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		mWidth = mHeight = 0;
		mSegments.clear();
		POOL.release(this);
	}

	public static Page obtain() {
		Page page = POOL.acquire();
		if (page != null) {
			page.reuse();
			return page;
		}

		return new Page();
	}

	public static void clean() {
		POOL.clean();
	}
}
