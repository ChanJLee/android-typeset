package me.chan.texas.renderer.selection;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.misc.BitBucket;
import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanPredicate;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.selection.overlay.SelectionDragView;
import me.chan.texas.renderer.selection.visitor.SelectedTextByClickedVisitor;
import me.chan.texas.renderer.selection.visitor.SelectedTextByDragVisitor;
import me.chan.texas.renderer.selection.visitor.PredicatesDriveSelectedVisitor;
import me.chan.texas.renderer.ui.RendererAdapter;
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.renderer.ui.text.OnSelectedChangedListener;
import me.chan.texas.renderer.ui.text.TextureParagraph;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextStyles;
import me.chan.texas.text.layout.Box;

/**
 * 负责处理select paragraph
 * <p>
 * 目前有三个地方会触发选中
 * 1. 长按 & 点击 操作
 * 2. 主动调用 {@link TexasView#selectParagraphs} 接口
 * 3. 长按后拖动水滴
 * Created by Otway on 2021/11/12.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelectionManager implements OnSelectedChangedListener {
	private Selection mCurrentSelection;

	private final RendererAdapter mAdapter;
	private final LinearLayoutManager mLayoutManager;
	private final Listener mListener;
	private final SelectionDragView mDropView;
	private final TexasRecyclerView mContentView;

	/**
	 * 用于自驱式的选中文本
	 * <p>
	 * 即主动调用 {@link TexasView#selectParagraphs} 接口，而不是通过点击操作
	 */
	private final PredicatesDriveSelectedVisitor mPredicatesDriveSelectedVisitor = new PredicatesDriveSelectedVisitor();
	/**
	 * 用于拖拽时选中文本 {@link SelectionManager#handleMoveToSelection(float, float, float, float, boolean)}
	 */
	private final SelectedTextByDragVisitor mSelectedTextByDragVisitor = new SelectedTextByDragVisitor();
	/**
	 * 用于点击是选中文本 {@link SelectionManager#onBoxSelected(View, MotionEvent, Paragraph, int, Box)}
	 */
	private final SelectedTextByClickedVisitor mSelectedTextByClickedVisitor = new SelectedTextByClickedVisitor();

	/**
	 * 拖拽时定位用
	 */
	private final int[] mLocations = new int[2];
	private SpanTouchEventHandler mSpanTouchEventHandler;
	private final SpanPredicate mOnSpanClickedPredicate = new SpanPredicate() {
		@Override
		public boolean accept(@Nullable Object clickedTag, @Nullable Object tag) {
			return mSpanTouchEventHandler.applySpanClicked(clickedTag, tag);
		}
	};
	private final SpanPredicate mOnSpanLongClickedPredicate = new SpanPredicate() {
		@Override
		public boolean accept(@Nullable Object clickedTag, @Nullable Object tag) {
			return mSpanTouchEventHandler.applySpanLongClicked(clickedTag, tag);
		}
	};

	public SelectionManager(RendererAdapter adapter,
							LinearLayoutManager layoutManager,
							Listener listener,
							TexasView texasView,
							TexasRecyclerView recyclerView) {
		mAdapter = adapter;
		mLayoutManager = layoutManager;
		mListener = listener;
		mDropView = new SelectionDragView(texasView.getContext(), texasView);
		texasView.addView(mDropView,
				new TexasView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT)
		);
		mDropView.setVisibility(View.GONE);
		mDropView.setSelectionManager(this);
		mContentView = recyclerView;
		mContentView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
				mDropView.updateContentScrollY(-dy);
			}
		});
	}

	public void setSpanTouchEventHandler(SpanTouchEventHandler listener) {
		mSpanTouchEventHandler = listener;
	}

	/**
	 * @return 获取当前的选中信息
	 */
	@Nullable
	public Selection getCurrentSelection() {
		return mCurrentSelection;
	}

	@Override
	public boolean onSegmentClicked(View source, MotionEvent e, Paragraph paragraph, int eventType) {
		if (mListener == null) {
			return false;
		}

		if (eventType == OnSelectedChangedListener.EVENT_CLICKED) {
			mListener.onSegmentClicked(TouchEvent.obtain(source, e), paragraph.getTag());
			return true;
		}

		if (eventType == EVENT_DOUBLE_CLICKED) {
			mListener.onSegmentDoubleClicked(TouchEvent.obtain(source, e), paragraph.getTag());
			return true;
		}

		return false;
	}

	/**
	 * 由用户 长按 & 点击触发选中
	 *
	 * @param e         点击事件
	 * @param paragraph paragraph
	 * @param box       被选中的box
	 * @return 是否有box被选中
	 */
	@Override
	public boolean onBoxSelected(View source, MotionEvent e, Paragraph paragraph, @EventType int eventType, Box box) {
		if (eventType == OnSelectedChangedListener.EVENT_CLICKED ||
				eventType == OnSelectedChangedListener.EVENT_LONG_CLICKED) {
			boolean handled = onBoxSelected(source, e, paragraph, eventType == OnSelectedChangedListener.EVENT_LONG_CLICKED, box);
			if (!handled && eventType == OnSelectedChangedListener.EVENT_CLICKED && mListener != null) {
				mListener.onSegmentClicked(TouchEvent.obtain(source, e), paragraph.getTag());
				return true;
			}
		}

		return false;
	}

	private boolean onBoxSelected(View source, MotionEvent e, Paragraph paragraph, boolean isLongClicked, Box box) {
		SpanPredicate predicate = isLongClicked ? mOnSpanLongClickedPredicate : mOnSpanClickedPredicate;
		clearSelection();

		boolean handled = false;
		try {
			handled = handleParagraphClicked(paragraph, isLongClicked, predicate, box);

			if (handled && mListener != null) {
				if (isLongClicked) {
					mListener.onSpanLongClicked(TouchEvent.obtain(source, e), box.getTag());
					notifyUpdateSelectionDropView();
				} else {
					mListener.onSpanClicked(TouchEvent.obtain(source, e), box.getTag());
				}
			}
		} catch (ParagraphVisitor.VisitException ex) {
			w(ex);
		}

		return handled;
	}

	private boolean handleParagraphClicked(Paragraph paragraph,
										   boolean isLongClicked,
										   SpanPredicate predicate,
										   Box box) throws ParagraphVisitor.VisitException {
		Document document = mAdapter.getDocument();
		if (document == null) {
			return false;
		}

		RenderOption renderOption = mAdapter.getRenderOption();
		int index = document.indexOfSegment(paragraph);
		if (index < 0) {
			return false;
		}

		return handleParagraphClicked0(paragraph, renderOption, isLongClicked, predicate, box.getTag(), index);
	}

	private boolean handleParagraphClicked0(Paragraph paragraph,
											RenderOption renderOption,
											boolean isLongClicked,
											SpanPredicate predicate,
											Object boxTag,
											int index) throws ParagraphVisitor.VisitException {
		try {
			mSelectedTextByClickedVisitor.reset(
					isLongClicked,
					renderOption
			);
			mSelectedTextByClickedVisitor.setPredicate(predicate, boxTag);
			mSelectedTextByClickedVisitor.startVisit(
					paragraph
			);
			handleParagraphSelected(paragraph, index);
			return mSelectedTextByClickedVisitor.isHandled();
		} finally {
			mSelectedTextByClickedVisitor.clear();
		}
	}

	private void notifyUpdateSelectionDropView() {
		Selection selection = getCurrentSelection();
		if (selection == null || selection.isEmpty()) {
			mContentView.allowHandleTouchEvent();
			mDropView.setVisibility(View.GONE);
			return;
		}

		Selection.RectEdge selectedRectEdge = selection.getSelectedRectEdge(mContentView);
		if (selectedRectEdge == null) {
			mContentView.allowHandleTouchEvent();
			mDropView.setVisibility(View.GONE);
			return;
		}

		// 为了让交换的时候能正确的判定区域，需要在底部和顶部各收缩一像素
		// 这样针对非focus点可以用适用非严格的边界判断规则
		mContentView.disallowHandleTouchEvent();
		mDropView.setVisibility(View.VISIBLE);
		mDropView.renderRegion(selectedRectEdge.topX,
				selectedRectEdge.topY + 1 /* trick */,
				selectedRectEdge.bottomX,
				selectedRectEdge.bottomY - 1 /* trick */,
				selectedRectEdge.lineHeight);
	}

	/**
	 * 点击空白
	 */
	public void handleClickNothing() {
		mContentView.allowHandleTouchEvent();
		mDropView.setVisibility(View.GONE);
		clearSelection();
		mListener.onDragDismiss();
	}

	/**
	 * 开始拖拽水滴
	 *
	 * @param event 事件
	 */
	public void handleDragStart(TouchEvent event) {
		mListener.onDragStart(event);
	}

	/**
	 * 结束拖拽水滴
	 *
	 * @param event 事件
	 */
	public void handleDragEnd(TouchEvent event) {
		mListener.onDragEnd(event);
	}

	/**
	 * 长按后滑动选中
	 *
	 * @param x1        顶部x
	 * @param y1        顶部y
	 * @param x2        底部x
	 * @param y2        底部y
	 * @param isFocusP1 手指是否按在p1上滑动
	 */
	@SuppressLint("NotifyDataSetChanged")
	public void handleMoveToSelection(float x1, float y1, float x2, float y2, boolean isFocusP1) {
		int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
		int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

		Selection prevSelection = mCurrentSelection;

		RenderOption renderOption = mAdapter.getRenderOption();

		Selection currentSelection = Selection.obtain(mAdapter);
		for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; ++i) {
			View content = mLayoutManager.findViewByPosition(i);
			if (!(content instanceof TextureParagraph)) {
				continue;
			}

			TextureParagraph textureParagraph = (TextureParagraph) content;
			mContentView.getChildLocations(content, mLocations);

			if (mLocations[1] + content.getHeight() < y1) {
				continue;
			}

			if (mLocations[1] > y2) {
				continue;
			}

			try {
				mSelectedTextByDragVisitor.reset(true, renderOption);
				float tempX1 = x1 - mLocations[0];
				float tempY1 = y1 - mLocations[1];
				float tempX2 = x2 - mLocations[0];
				float tempY2 = y2 - mLocations[1];
				mSelectedTextByDragVisitor.setRegion(tempX1, tempY1, tempX2, tempY2, isFocusP1);
				Paragraph paragraph = textureParagraph.getParagraph();
				mSelectedTextByDragVisitor.startVisit(paragraph);
				addParagraphSelection(currentSelection, paragraph);
			} catch (ParagraphVisitor.VisitException ex) {
				w(ex);
			} finally {
				mSelectedTextByDragVisitor.clear();
			}
		}

		updateMotionSelection(prevSelection, currentSelection);
	}

	private BitBucket mSelectionDiffBucket;

	private void updateMotionSelection(Selection prevSelection, Selection currentSelection) {
		// 先置换下 因为 adapter 在绘制的时候会先查这里
		mCurrentSelection = currentSelection;

		int size = mAdapter.getItemCount();
		if (mSelectionDiffBucket == null || mSelectionDiffBucket.size() < size) {
			mSelectionDiffBucket = new BitBucket(size);
		}

		// 因此 需要变化的 item 都在 两个集合里了
		mSelectionDiffBucket.clear();
		for (int i = 0; i < currentSelection.size(); ++i) {
			Paragraph paragraph = currentSelection.get(i);
			int index = mAdapter.sendSignal(paragraph, RendererAdapter.SIG_SELECTION_CHANGED);
			if (index < 0) {
				continue;
			}
			mSelectionDiffBucket.set(index, true);
		}

		for (int i = 0; i < prevSelection.size(); ++i) {
			Paragraph paragraph = prevSelection.get(i);
			int index = mAdapter.indexOf(paragraph);
			if (!mSelectionDiffBucket.get(index)) {
				ParagraphSelection paragraphSelection = prevSelection.getParagraphSelection(paragraph);
				if (paragraphSelection != null) {
					paragraphSelection.recycle();
					paragraph.setSelection(null);
				}
				mAdapter.sendSignal(index, RendererAdapter.SIG_SELECTION_CHANGED);
			}
		}

		notifyUpdateSelectionDropView();

		prevSelection.recycle();
	}

	/**
	 * 清除选中区域
	 */
	public void clearSelection() {
		if (mCurrentSelection != null) {
			mCurrentSelection.clear();
		}
	}

	/**
	 * 清除所有选中效果
	 */
	public void clear() {
		clearSelection();
		notifyUpdateSelectionDropView();
	}

	/**
	 * 主动 选择 paragraph
	 *
	 * @param predicates 谓词
	 * @return 选中区域
	 */
	@Nullable
	public Selection selectParagraphs(ParagraphPredicates predicates, @Nullable TextStyles styles) {
		Document document = mAdapter.getDocument();
		clearSelection();

		for (int i = 0; i < document.getSegmentCount(); ++i) {
			Segment segment = document.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			selectParagraph((Paragraph) segment, predicates, i, styles);
		}

		notifyUpdateSelectionDropView();
		return mCurrentSelection;
	}

	private void selectParagraph(Paragraph paragraph, ParagraphPredicates predicates, int index, @Nullable TextStyles styles) {
		if (paragraph == null) {
			return;
		}
		// todo clear

		try {
			RenderOption renderOption = mAdapter.getRenderOption();
			mPredicatesDriveSelectedVisitor.reset(renderOption, predicates, styles);
			mPredicatesDriveSelectedVisitor.startVisit(paragraph);
			handleParagraphSelected(paragraph, index);
		} catch (ParagraphVisitor.VisitException ignored) {
			/* do nothing */
		} finally {
			mPredicatesDriveSelectedVisitor.clear();
		}
	}

	private void addParagraphSelection(Selection selection, Paragraph paragraph) {
		// TODO 去重
		ParagraphSelection paragraphSelection = paragraph.getSelection();
		if (paragraphSelection != null) {
			selection.add(paragraph);
		}
	}

	/**
	 * 处理 paragraph 被选中
	 *
	 * @param paragraph paragraph
	 * @param index     index
	 */
	private void handleParagraphSelected(Paragraph paragraph, int index) {
		if (mCurrentSelection == null) {
			mCurrentSelection = Selection.obtain(mAdapter);
		}

		if (index < 0) {
			Document document = mAdapter.getDocument();
			index = document.indexOfSegment(paragraph);
		}

		addParagraphSelection(mCurrentSelection, paragraph);

		try {
			mAdapter.sendSignal(index, RendererAdapter.SIG_SELECTION_CHANGED);
		} catch (Throwable ignore) {
			/* do nothing */
		}
	}

	private static void w(Throwable throwable) {
		Log.w("SelectionManager", throwable);
	}

	public OnSelectedChangedListener getOnTextSelectedListener() {
		return this;
	}

	public void updateRenderOption(RenderOption renderOption) {
		mDropView.setColor(renderOption.getDragViewColor());
	}

	public void autoScrollUp() {
		int position = mLayoutManager.findFirstCompletelyVisibleItemPosition();
		if (position == 0) {
			return;
		}

		mContentView.scrollBy(0, (int) (-mContentView.getHeight() * 0.1f));
	}

	public void autoScrollDown() {
		int position = mLayoutManager.findLastCompletelyVisibleItemPosition();
		if (position == mAdapter.getItemCount() - 1) {
			return;
		}

		mContentView.scrollBy(0, (int) (mContentView.getHeight() * 0.1f));
	}

	public SpanTouchEventHandler getSpanTouchEventHandler() {
		return mSpanTouchEventHandler;
	}

	public interface Listener {
		void onSpanClicked(TouchEvent event, Object tag);

		void onSpanLongClicked(TouchEvent event, Object tag);

		void onDragStart(TouchEvent event);

		void onDragEnd(TouchEvent event);

		void onDragDismiss();

		void onSegmentDoubleClicked(TouchEvent event, Object paragraphTag);

		void onSegmentClicked(TouchEvent event, Object paragraphTag);
	}
}
