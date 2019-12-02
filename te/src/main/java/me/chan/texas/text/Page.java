package me.chan.texas.text;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.Texas;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectFactory;

public class Page extends DefaultRecyclable {
	private static final ObjectFactory<Page> POOL = new ObjectFactory<>(120);

	private List<Segment> mSegments;

	private Page() {
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mSegments = new ArrayList<>(memoryOption.getPageSegmentInitialCapacity());
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
