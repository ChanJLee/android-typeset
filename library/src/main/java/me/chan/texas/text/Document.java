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

	private Document() {
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mSegments = new ArrayList<>(memoryOption.getDocumentSegmentInitialCapacity());
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

	@RestrictTo(LIBRARY)
	public void release() {
		clear();
		mSegments = null;
	}

	public static Document obtain() {
		return new Document();
	}

	@RestrictTo(LIBRARY)
	public void insertHead(List<Segment> segments) {
		segments.addAll(mSegments);
		mSegments = segments;
	}

	@RestrictTo(LIBRARY)
	public void insertTail(List<Segment> segments) {
		mSegments.addAll(segments);
	}

	@RestrictTo(LIBRARY)
	public void clear() {
		if (mSegments == null || mSegments.isEmpty()) {
			return;
		}

		final int count = mSegments.size();
		for (int i = 0; i < count; ++i) {
			Segment segment = mSegments.get(i);
			segment.recycle();
		}
		mSegments.clear();
	}
}
