package com.shanbay.lib.texas.renderer.selection;

import android.graphics.RectF;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shanbay.lib.log.Log;
import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.misc.ObjectPool;
import com.shanbay.lib.texas.renderer.ui.TexasAdapter;
import com.shanbay.lib.texas.text.Document;
import com.shanbay.lib.texas.text.Paragraph;

import java.util.ArrayList;
import java.util.List;

public final class Selection extends DefaultRecyclable {
	private static final ObjectPool<Selection> POOL = new ObjectPool<>(8);

	private TexasAdapter mTexasAdapter;
	private LinearLayoutManager mTexasLayoutManager;
	private final List<ParagraphSelection> mParagraphSelectionList = new ArrayList<>();
	private final RectEdge mRectEdge = new RectEdge();

	private Selection() {
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void add(ParagraphSelection selection) {
		mParagraphSelectionList.add(selection);
	}

	// TODO: 2021/11/18 遍历性能优化 guangcheng.zhang@shanbay.com
	@Nullable
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	ParagraphSelection getParagraphSelection(Paragraph paragraph) {
		if (paragraph == null || paragraph.getTag() == null) {
			return null;
		}

		for (ParagraphSelection paragraphSelection : mParagraphSelectionList) {
			if (paragraphSelection.getParagraph() == paragraph) {
				return paragraphSelection;
			}
		}

		return null;
	}

	/**
	 * @return 获取当前选中的tags，忽略空tag
	 */
	@Nullable
	public List<ParagraphSelectedTag> getSelectedTags() {
		if (mParagraphSelectionList.isEmpty()) {
			return null;
		}

		final List<ParagraphSelectedTag> list = new ArrayList<>();
		for (ParagraphSelection paragraphSelection : mParagraphSelectionList) {
			ParagraphSelectedTag paragraphSelectedTag = new ParagraphSelectedTag();
			paragraphSelectedTag.paragraphTag = paragraphSelection.getParagraph().getTag();
			paragraphSelectedTag.boxTags = paragraphSelection.getSelectedTags();
			list.add(paragraphSelectedTag);
		}
		return list;
	}

	/**
	 * @return 选中区域边界
	 */
	private final int[] mLocations = new int[2];

	public RectEdge getSelectedRectEdge() {

		int size = mParagraphSelectionList.size();
		if (size == 0) {
			return null;
		}

		boolean hasModified = false;
		for (int i = 0; i < size; ++i) {
			ParagraphSelection paragraphSelection = mParagraphSelectionList.get(i);
			if (paragraphSelection.isSelectedRegionEmpty()) {
				continue;
			}

			hasModified = true;
			boolean result = getParagraphLocationOnScreen(paragraphSelection.getParagraph(), mLocations);
			if (!result) {
				w("get first region location failed");
			}

			RectF firstRegion = paragraphSelection.getFirstRegion();
			assert firstRegion != null;

			mRectEdge.topY = firstRegion.top + mLocations[1];
			mRectEdge.topX = firstRegion.left + mLocations[0];
			mRectEdge.lineHeight = firstRegion.height();
			break;
		}

		for (int i = size - 1; i >= 0; --i) {
			ParagraphSelection paragraphSelection = mParagraphSelectionList.get(i);
			if (paragraphSelection.isSelectedRegionEmpty()) {
				continue;
			}

			hasModified = true;
			boolean result = getParagraphLocationOnScreen(paragraphSelection.getParagraph(), mLocations);
			if (!result) {
				w("get last region location failed");
			}

			RectF lastRegion = paragraphSelection.getLastRegion();
			assert lastRegion != null;

			mRectEdge.bottomY = lastRegion.bottom + mLocations[1];
			mRectEdge.bottomX = lastRegion.right + mLocations[0];
			break;
		}

		return hasModified ? mRectEdge : null;
	}

	boolean getParagraphLocationOnScreen(Paragraph paragraph, int[] locations) {
		Document document = mTexasAdapter.getDocument();
		if (document == null) {
			return false;
		}

		int index = document.indexOfSegment(paragraph);
		if (index == -1) {
			return false;
		}

		View child = mTexasLayoutManager.findViewByPosition(index);
		if (child == null) {
			return false;
		}

		child.getLocationOnScreen(locations);
		return true;
	}

	private boolean checkIfInvalid(ParagraphSelection paragraphSelection) {
		Paragraph paragraph = paragraphSelection.getParagraph();
		if (paragraph == null) {
			return true;
		}

		return paragraph.isRecycled();
	}

	public int size() {
		return mParagraphSelectionList.size();
	}

	public ParagraphSelection get(int index) {
		return mParagraphSelectionList.get(index);
	}

	@Override
	public void recycle() {
		if (!isRecycled()) {
			return;
		}

		mParagraphSelectionList.clear();
		mTexasLayoutManager = null;
		mTexasAdapter = null;
		mRectEdge.bottomX = mRectEdge.topX =
				mRectEdge.bottomY = mRectEdge.topY = mRectEdge.lineHeight = 0;

		super.recycle();
		POOL.release(this);
	}

	/**
	 * 清除选中
	 */
	public void clear() {
		// 通知内容被清除的时候还需要
		for (ParagraphSelection paragraphSelection : mParagraphSelectionList) {
			if (checkIfInvalid(paragraphSelection)) {
				continue;
			}

			int index = paragraphSelection.getIndex();
			paragraphSelection.clear();
			try {
				if (mTexasAdapter != null) {
					mTexasAdapter.notifyItemChanged(index);
				}
			} catch (Throwable ignore) {
				/* do nothing */
			}
			paragraphSelection.recycle();
		}
		mParagraphSelectionList.clear();
	}

	public boolean isEmpty() {
		return mParagraphSelectionList.isEmpty();
	}

	private static void w(String msg) {
		Log.w("TexasSelection", msg);
	}

	/**
	 * Coordinate system base on screen
	 */
	public static class RectEdge {
		public float topY = -1;
		public float bottomY = -1;
		public float topX = -1;
		public float bottomX = -1;

		public float lineHeight = -1;

		@Override
		public String toString() {
			return "RectEdge{" +
					"topY=" + topY +
					", bottomY=" + bottomY +
					", topX=" + topX +
					", bottomX=" + bottomX +
					", lineHeight=" + lineHeight +
					'}';
		}
	}

	public static Selection obtain(TexasAdapter adapter, LinearLayoutManager layoutManager) {
		Selection selection = POOL.acquire();
		if (selection == null) {
			selection = new Selection();
		}

		selection.mTexasAdapter = adapter;
		selection.mTexasLayoutManager = layoutManager;
		selection.reuse();
		return selection;
	}

	public static class ParagraphSelectedTag {
		public Object paragraphTag;
		public List<Object> boxTags;
	}
}
