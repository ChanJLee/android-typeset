package me.chan.texas.text;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.Texas;
import me.chan.texas.annotations.Hidden;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectFactory;

public final class Document extends DefaultRecyclable {
	private static final ObjectFactory<Document> POOL = new ObjectFactory<>(8);
	public final static Document EMPTY = obtain(null);

	private List<Segment> mSegments;
	private OnClickedListener mOnClickedListener;
	private Segment mFocusSegment;
	private Object mRaw;

	private Document(OnClickedListener onClickedListener) {
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mSegments = new ArrayList<>(memoryOption.getDocumentSegmentInitialCapacity());
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

	// TODO unit test
	public void setFocusSegment(Segment segment) {
		mFocusSegment = segment;
	}

	public int getFocusIndex() {
		if (mFocusSegment == null) {
			return -1;
		}

		return mSegments.indexOf(mFocusSegment);
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
		mOnClickedListener = null;
		mFocusSegment = null;
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
