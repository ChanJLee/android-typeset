package com.shanbay.lib.texas.text;

import java.util.ArrayList;
import java.util.List;

import com.shanbay.lib.texas.Texas;
import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.misc.ObjectFactory;

/**
 * 文档
 */
public final class Document extends DefaultRecyclable {
	private static final ObjectFactory<Document> POOL = new ObjectFactory<>(8);
	public final static Document EMPTY = obtain();

	private List<Segment> mSegments;
	private Segment mFocusSegment;
	private Object mRaw;

	private Document() {
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mSegments = new ArrayList<>(memoryOption.getDocumentSegmentInitialCapacity());
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
	 * @param segment 设置当前焦点segment，焦点segment的意思是下次渲染的时候，优先将视图滚动到当前segment
	 */
	public void setFocusSegment(Segment segment) {
		mFocusSegment = segment;
	}

	/**
	 * @return 获取focus segment的下标，负值则为没有focus segment
	 */
	@Hidden
	public int getFocusSegmentIndex() {
		if (mFocusSegment == null) {
			return -1;
		}

		return indexOf(mFocusSegment);
	}


	/**
	 * 获取对应segment的下标
	 *
	 * @param segment segment
	 * @return 获取segment的下标，负值则为没有segment
	 */
	public int indexOf(Segment segment) {
		return mSegments.indexOf(segment);
	}

	/**
	 * 获取段落数目
	 *
	 * @return 段落数目
	 */
	public int getSegmentCount() {
		return mSegments.size();
	}

	/**
	 * 获取segment
	 *
	 * @param index 下标
	 * @return segment
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *                                   (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	public Segment getSegment(int index) {
		return mSegments.get(index);
	}

	/**
	 * 添加segment
	 *
	 * @param segment 添加segment
	 */
	public void addSegment(Segment segment) {
		mSegments.add(segment);
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

		mRaw = null;
		mFocusSegment = null;
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
