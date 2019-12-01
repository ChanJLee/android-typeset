package me.chan.te.text;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.Te;
import me.chan.te.annotations.Hidden;
import me.chan.te.misc.DefaultRecyclable;
import me.chan.te.misc.ObjectFactory;

public final class Document extends DefaultRecyclable {
	private static final ObjectFactory<Document> POOL = new ObjectFactory<>(8);
	public final static Document EMPTY = obtain(null);

	private List<Segment> mSegments;
	private List<Page> mPages;
	private OnClickedListener mOnClickedListener;

	private Object mRaw;

	private Document(OnClickedListener onClickedListener) {
		Te.MemoryOption memoryOption = Te.getMemoryOption();
		mSegments = new ArrayList<>(memoryOption.getDocumentSegmentInitialCapacity());
		mPages = new ArrayList<>(memoryOption.getDocumentPageInitialCapacity());
		mOnClickedListener = onClickedListener;
	}

	@Hidden
	public Object getRaw() {
		return mRaw;
	}

	@Hidden
	public void setRaw(Object raw) {
		mRaw = raw;
	}

	public OnClickedListener getOnClickedListener() {
		return mOnClickedListener;
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
		mOnClickedListener = null;
		POOL.release(this);
	}

	@Hidden
	public static void clean() {
		POOL.clean();
	}

	public static Document obtain() {
		return obtain(null);
	}

	public static Document obtain(OnClickedListener onClickedListener) {
		Document document = POOL.acquire();
		if (document == null) {
			return new Document(onClickedListener);
		}
		document.mOnClickedListener = onClickedListener;
		document.reuse();
		return document;
	}
}
