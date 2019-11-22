package me.chan.te.text;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.Te;
import me.chan.te.misc.DefaultRecyclable;
import me.chan.te.misc.ObjectFactory;

public class Page extends DefaultRecyclable {
	private static final ObjectFactory<Page> POOL = new ObjectFactory<>(120);

	private List<Segment> mSegments;

	private Page() {
		Te.MemoryOption memoryOption = Te.getMemoryOption();
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
