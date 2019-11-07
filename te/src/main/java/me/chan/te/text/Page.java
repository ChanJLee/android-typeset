package me.chan.te.text;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.misc.ObjectFactory;
import me.chan.te.misc.Recyclable;

public class Page implements Recyclable {
	private static final ObjectFactory<Page> POOL = new ObjectFactory<>(120);

	private List<Segment> mSegments = new ArrayList<>(512);

	private Page() {
	}

	/**
	 * 获取段落数目
	 *
	 * @return 段落数目
	 */
	public int getCount() {
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
		for (Segment segment : mSegments) {
			segment.recycle();
		}
		mSegments.clear();
		POOL.release(this);
	}

	public static Page obtian() {
		Page page = POOL.acquire();
		if (page != null) {
			return page;
		}

		return new Page();
	}

	public static void clean() {
		POOL.clean();
	}
}
