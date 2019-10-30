package me.chan.te.data;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.misc.ObjectFactory;

public final class Document implements Recyclable {
	private static final ObjectFactory<Document> POOL = new ObjectFactory<>(8);
	public final static Document EMPTY = obtain();

	private List<Segment> mSegments = new ArrayList<>(512);
	private Object mExtra;

	private Document(Object extra) {
		mExtra = extra;
	}

	public static void clean() {
		POOL.clean();
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
	public int getCount() {
		return mSegments.size();
	}

	public Segment getSegment(int index) {
		return mSegments.get(index);
	}

	@Override
	public void recycle() {
		for (Segment segment : mSegments) {
			segment.recycle();
		}
		mSegments.clear();
		POOL.release(this);
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
		return document;
	}

	public void addParagraph(Paragraph paragraph) {
		mSegments.add(paragraph);
	}
}
