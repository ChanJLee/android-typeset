package me.chan.te.text;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.annotations.Hidden;
import me.chan.te.misc.DefaultRecyclable;
import me.chan.te.misc.ObjectFactory;

public final class Document extends DefaultRecyclable {
	private static final ObjectFactory<Document> POOL = new ObjectFactory<>(8);
	public final static Document EMPTY = obtain();

	private List<Segment> mSegments = new ArrayList<>(512);
	private List<Page> mPages = new ArrayList<>(128);

	private Object mExtra;
	private Object mRaw;

	private Document(Object extra) {
		mExtra = extra;
	}

	public Object getRaw() {
		return mRaw;
	}

	public void setRaw(Object raw) {
		mRaw = raw;
	}

	@Nullable
	public Object getExtra() {
		return mExtra;
	}

	/**
	 * 获取段落数目
	 *
	 * @return 段落数目
	 */
	@Hidden
	public int getSegmentCount() {
		return mSegments.size();
	}

	@Hidden
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
	public int getPageCount() {
		return mPages.size();
	}

	public Page getPage(int index) {
		return mPages.get(index);
	}

	public void addPage(Page page) {
		mPages.add(page);
	}

	@Override
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

	public static void clean() {
		POOL.clean();
	}

	public static Document obtain() {
		return obtain(null);
	}

	public static Document obtain(Object extra) {
		Document document = POOL.acquire();
		if (document == null) {
			return new Document(extra);
		}
		document.mExtra = extra;
		document.reuse();
		return document;
	}
}
