package me.chan.texas.renderer.selection;

import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.ui.RendererAdapterImpl;
import me.chan.texas.renderer.ui.TexasRendererAdapter;
import me.chan.texas.renderer.ui.rv.TexasLayoutManager;
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;

import java.util.ArrayList;
import java.util.List;

public final class Selection extends DefaultRecyclable {
	private static final ObjectPool<Selection> POOL = new ObjectPool<>(8);

	private TexasRendererAdapter mTexasAdapter;
	private final List<Paragraph> mParagraphs = new ArrayList<>();
	private final RectEdge mRectEdge = new RectEdge();

	private Selection() {
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void add(Paragraph paragraph) {
		mParagraphs.add(paragraph);
	}

	@Nullable
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	ParagraphSelection getParagraphSelection(Paragraph paragraph) {
		return paragraph.getSelection();
	}

	/**
	 * @return 获取当前选中的tags，忽略空tag
	 */
	@Nullable
	public List<Paragraph> getSelectedParagraphs() {
		if (mParagraphs.isEmpty()) {
			return null;
		}

		return mParagraphs;
	}

	/**
	 * @return 选中区域边界
	 */
	private final int[] mLocations = new int[2];

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	RectEdge getSelectedRectEdge(TexasRecyclerView container) {
		int size = mParagraphs.size();
		if (size == 0) {
			return null;
		}

		boolean hasModified = false;
		for (int i = 0; i < size; ++i) {
			Paragraph paragraph = mParagraphs.get(i);
			ParagraphSelection paragraphSelection = paragraph.getSelection();
			if (paragraphSelection == null || paragraphSelection.isSelectedRegionEmpty()) {
				continue;
			}

			hasModified = true;
			boolean result = getParagraphLocation(container, paragraph, mLocations);
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
			Paragraph paragraph = mParagraphs.get(i);
			ParagraphSelection paragraphSelection = paragraph.getSelection();
			if (paragraphSelection == null || paragraphSelection.isSelectedRegionEmpty()) {
				continue;
			}

			hasModified = true;
			boolean result = getParagraphLocation(container, paragraph, mLocations);
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

	boolean getParagraphLocation(TexasRecyclerView container, Paragraph paragraph, int[] locations) {
		Document document = mTexasAdapter.getDocument();
		if (document == null) {
			return false;
		}

		int index = document.indexOfSegment(paragraph);
		if (index == -1) {
			return false;
		}

		TexasLayoutManager layoutManager = container.getTexasLayoutManager();
		if (layoutManager == null) {
			return false;
		}

		View child = layoutManager.findViewByPosition(index);
		if (child == null) {
			return false;
		}

		container.getChildLocations(child, locations);
		return true;
	}

	public int size() {
		return mParagraphs.size();
	}

	public Paragraph getParagraph(int index) {
		return mParagraphs.get(index);
	}

	public ParagraphSelection get(int index) {
		return getParagraph(index).getSelection();
	}

	@Override
	protected void onRecycle() {
		mParagraphs.clear();
		mTexasAdapter = null;
		mRectEdge.bottomX = mRectEdge.topX =
				mRectEdge.bottomY = mRectEdge.topY = mRectEdge.lineHeight = 0;
		POOL.release(this);
	}

	/**
	 * 清除选中
	 */
	public void clear() {
		// 通知内容被清除的时候还需要
		for (Paragraph paragraph : mParagraphs) {
			if (paragraph.isRecycled()) {
				continue;
			}

			ParagraphSelection paragraphSelection = paragraph.getSelection();
			if (paragraphSelection == null) {
				continue;
			}

			paragraph.setSelection(null);
			try {
				if (mTexasAdapter != null) {
					mTexasAdapter.sendSignal(paragraph, RendererAdapterImpl.SIG_SELECTION_CHANGED);
				}
			} catch (Throwable ignore) {
				/* do nothing */
			}
			paragraphSelection.recycle();
		}
		mParagraphs.clear();
	}

	public boolean isEmpty() {
		return mParagraphs.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		for (Paragraph paragraph : mParagraphs) {
			builder.append(paragraph.getSelection().toString(paragraph)).append(", ");
		}
		builder.append("]");
		return builder.toString();
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

	public static Selection obtain(TexasRendererAdapter adapter) {
		Selection selection = POOL.acquire();
		if (selection == null) {
			selection = new Selection();
		}

		selection.mTexasAdapter = adapter;
		selection.reuse();
		return selection;
	}

	public static class Styles {
		private final int mBackgroundColor;
		private final int mTextColor;

		private boolean mEnableDrag = true;

		public Styles(int backgroundColor, int textColor) {
			mBackgroundColor = backgroundColor;
			mTextColor = textColor;
		}

		public int getBackgroundColor() {
			return mBackgroundColor;
		}

		public int getTextColor() {
			return mTextColor;
		}

		public boolean isEnableDrag() {
			return mEnableDrag;
		}

		public Styles setEnableDrag(boolean enableDrag) {
			mEnableDrag = enableDrag;
			return this;
		}
	}
}
