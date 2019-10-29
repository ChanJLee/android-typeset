package me.chan.te.data;

import android.support.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;

import me.chan.te.misc.ObjectFactory;

public final class Document implements Recyclable {
	public final static Document EMPTY = obtain();

	private static final ObjectFactory<Document> POOL = new ObjectFactory<>(8);

	private List<Paragraph> mParagraphs = new LinkedList<>();
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
	public int getParagraphCount() {
		return mParagraphs.size();
	}

	@Nullable
	public Paragraph getParagraph(int index) {
		return mParagraphs.get(index);
	}

	@Override
	public void recycle() {
		for (Paragraph paragraph : mParagraphs) {
			paragraph.recycle();
		}
		mParagraphs.clear();
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
		mParagraphs.add(paragraph);
	}
}
