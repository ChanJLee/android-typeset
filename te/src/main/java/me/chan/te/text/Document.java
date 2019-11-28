package me.chan.te.text;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.Te;
import me.chan.te.annotations.Hidden;
import me.chan.te.misc.DefaultRecyclable;
import me.chan.te.misc.ObjectFactory;

public final class Document extends DefaultRecyclable {
	private static final ObjectFactory<Document> POOL = new ObjectFactory<>(8);
	public final static Document EMPTY = obtain();

	private List<Segment> mSegments;
	private List<Page> mPages;

	private Object mRaw;

	private Document() {
		Te.MemoryOption memoryOption = Te.getMemoryOption();
		mSegments = new ArrayList<>(memoryOption.getDocumentSegmentInitialCapacity());
		mPages = new ArrayList<>(memoryOption.getDocumentPageInitialCapacity());
	}

	@Hidden
	public Object getRaw() {
		return mRaw;
	}

	@Hidden
	public void setRaw(Object raw) {
		mRaw = raw;
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

	/**
	 * 获取页数
	 *
	 * @return 段落数目
	 */
	@Hidden
	public int getPageCount() {
		return mPages.size();
	}

	@Hidden
	public Page getPage(int index) {
		return mPages.get(index);
	}

	@Hidden
	public void addPage(Page page) {
		mPages.add(page);
	}

	@Override
	@Hidden
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		for (Segment segment : mSegments) {
			segment.recycle();
		}
		mSegments.clear();

		for (Page page : mPages) {
			page.recycle();
		}
		mPages.clear();
		mRaw = null;
		POOL.release(this);
	}

	@Hidden
	public static void clean() {
		POOL.clean();
	}

	public static Document obtain() {
		Document document = POOL.acquire();
		if (document == null) {
			return new Document();
		}
		document.reuse();
		return document;
	}
}
