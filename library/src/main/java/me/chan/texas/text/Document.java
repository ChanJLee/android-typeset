package me.chan.texas.text;

import me.chan.texas.Texas;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 文档
 */
public final class Document {
	private List<Segment> mSegments;
	private Segment mFocusSegment;
	private int mFocusSegmentOffset;

	private Document() {
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mSegments = new ArrayList<>(memoryOption.getDocumentSegmentInitialCapacity());
	}

	/**
	 * @param segment 设置当前焦点segment，焦点segment的意思是下次渲染的时候，优先将视图滚动到当前segment
	 */
	public void setFocusSegment(Segment segment) {
		setFocusSegment(segment, 0);
	}

	/**
	 * @param segment 设置当前焦点segment，焦点segment的意思是下次渲染的时候，优先将视图滚动到当前segment
	 * @param offset  垂直方向偏移
	 */
	public void setFocusSegment(Segment segment, int offset) {
		mFocusSegment = segment;
		mFocusSegmentOffset = offset;
	}

	public int getFocusSegmentSegmentIndex() {
		return indexOfSegment(mFocusSegment);
	}

	/**
	 * @return 获取focus segment偏移量
	 */
	public int getFocusSegmentOffset() {
		return mFocusSegmentOffset;
	}

	/**
	 * @param segment segment
	 * @return segment在document中的下标
	 */
	public int indexOfSegment(Segment segment) {
		if (segment == null || mSegments == null) {
			return -1;
		}

		return mSegments.indexOf(segment);
	}

	/**
	 * 获取段落数目
	 *
	 * @return 段落数目
	 */
	public int getSegmentCount() {
		return mSegments == null ? 0 : mSegments.size();
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
		// 后续如果要支持移除，则需要考虑到 TexasAdapter 的id是否stable
		mSegments.add(segment);
	}

	@RestrictTo(LIBRARY)
	public void release() {
		int count = mSegments == null ? 0 : mSegments.size();
		for (int i = 0; i < count; ++i) {
			Segment segment = mSegments.get(i);
			segment.recycle();
		}
		mSegments = null;
		mFocusSegment = null;
	}

	public static Document obtain() {
		return new Document();
	}

	public static Document createEmptyDocument() {
		return obtain();
	}
}
