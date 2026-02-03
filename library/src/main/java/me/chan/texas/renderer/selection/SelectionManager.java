package me.chan.texas.renderer.selection;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.misc.BitBucket;
import me.chan.texas.misc.Rect;
import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanPredicate;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.selection.overlay.DragSelectView;
import me.chan.texas.renderer.selection.visitor.SelectedTextByClickedVisitor;
import me.chan.texas.renderer.selection.visitor.SelectedTextByDragVisitor;
import me.chan.texas.renderer.selection.visitor.PredicatesDriveSelectedVisitor;
import me.chan.texas.renderer.ui.TexasRendererAdapter;
import me.chan.texas.renderer.ui.rv.TexasLayoutManager;
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.renderer.ui.text.OnSelectedChangedListener;
import me.chan.texas.renderer.ui.text.TextureParagraph;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
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
	private Selection mCurrentHighlightSelection;

	private final TexasRendererAdapter mAdapter;
	private final TexasLayoutManager mLayoutManager;
	private final Listener mListener;
	private DragSelectView mDropView;
	private final TexasRecyclerView mContentView;

	/**
	 * 用于自驱式的选中文本
	 * <p>
	 * 即主动调用 {@link TexasView#selectParagraphs} 接口，而不是通过点击操作
	 */
	private final PredicatesDriveSelectedVisitor mPredicatesDriveSelectedVisitor = new PredicatesDriveSelectedVisitor();
	/**
	 * 用于拖拽时选中文本 {@link SelectionManager#handleMoveToSelection(float, float, float, float)}
	 */
	private final SelectedTextByDragVisitor mSelectedTextByDragVisitor = new SelectedTextByDragVisitor();
	/**
	 * 用于点击是选中文本 {@link SelectionManager#onBoxSelected(TouchEvent, Paragraph, int, Box)}
	 */
	private final SelectedTextByClickedVisitor mSelectedTextByClickedVisitor = new SelectedTextByClickedVisitor();

	/**
	 * 拖拽时定位用
	 */
	private final Rect mLocations = new Rect();
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

	public SelectionManager(TexasRendererAdapter adapter,
							TexasLayoutManager layoutManager,
							Listener listener,
							DragSelectView selectableView,
							TexasRecyclerView contextView) {
		mAdapter = adapter;
		mLayoutManager = layoutManager;
		mListener = listener;
		mDropView = selectableView;
		mDropView.setVisibility(View.GONE);
		mDropView.setSelectionManager(this);
		mContentView = contextView;
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

	@Nullable
	public Selection getCurrentSelection() {
		return getCurrentSelection(Selection.Type.SELECTION);
	}

	/**
	 * @return 获取当前的选中信息
	 */
	@Nullable
	public Selection getCurrentSelection(Selection.Type type) {
		if (type == Selection.Type.SELECTION) {
			return mCurrentSelection;
		} else if (type == Selection.Type.HIGHLIGHT) {
			return mCurrentHighlightSelection;
		}

		throw new IllegalArgumentException("Unknown selection type: " + type);
	}

	@Override
	public boolean onSegmentClicked(TouchEvent e, Paragraph paragraph, int eventType) {
		if (mListener == null || paragraph == null) {
			return false;
		}

		if (eventType == OnSelectedChangedListener.EVENT_CLICKED) {
			mListener.onSegmentClicked(e, paragraph.getTag());
			return true;
		}

		if (eventType == EVENT_DOUBLE_CLICKED) {
			mListener.onSegmentDoubleClicked(e, paragraph.getTag());
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
	public boolean onBoxSelected(TouchEvent e, Paragraph paragraph, @EventType int eventType, Box box) {
		if (paragraph == null) {
			return false;
		}

		if (eventType == OnSelectedChangedListener.EVENT_CLICKED ||
				eventType == OnSelectedChangedListener.EVENT_LONG_CLICKED) {
			boolean handled = onBoxSelected(e, paragraph, eventType == OnSelectedChangedListener.EVENT_LONG_CLICKED, box);
			if (!handled && eventType == OnSelectedChangedListener.EVENT_CLICKED && mListener != null) {
				mListener.onSegmentClicked(e, paragraph.getTag());
				return true;
			}
			return handled;
		}

		return false;
	}

	private boolean onBoxSelected(TouchEvent e, Paragraph paragraph, boolean isLongClicked, Box box) {
		SpanPredicate predicate = isLongClicked ? mOnSpanLongClickedPredicate : mOnSpanClickedPredicate;
		clearSelection();

		boolean handled = false;
		try {
			handled = handleParagraphClicked(paragraph, isLongClicked, predicate, box);

			if (handled && mListener != null) {
				if (isLongClicked) {
					mListener.onSpanLongClicked(e, box.getTag());
					notifyUpdateSelectionDropView();
				} else {
					mListener.onSpanClicked(e, box.getTag());
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
			Selection.Styles styles = Selection.Styles.createFromTouch(mAdapter.getRenderOption(), isLongClicked);
			mSelectedTextByClickedVisitor.reset(
					Selection.Type.SELECTION,
					styles,
					paragraph,
					renderOption
			);
			mSelectedTextByClickedVisitor.setPredicate(predicate, boxTag);
			mSelectedTextByClickedVisitor.startVisit(
					paragraph
			);

			handleParagraphSelected(paragraph, styles);

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

		Selection.RectEdge selectedRectEdge = selection.getSelectedRectEdge();
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
		handleClickNothing(false);
	}

	/**
	 * 点击空白
	 */
	public void handleClickNothing(boolean silence) {
		mContentView.allowHandleTouchEvent();
		mDropView.setVisibility(View.GONE);
		clearSelection();
		if (!silence) {
			mListener.onDragDismiss();
		}
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
	 * @param x1 x1
	 * @param y1 y1
	 * @param x2 x2
	 * @param y2 y2
	 */
	@SuppressLint("NotifyDataSetChanged")
	public void handleMoveToSelection(float x1, float y1, float x2, float y2) {
		if (mCurrentSelection == null) {
			return;
		}

		int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
		int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

		Selection prevSelection = mCurrentSelection;

		RenderOption renderOption = mAdapter.getRenderOption();

		Selection currentSelection = Selection.obtain(mCurrentSelection.getType(), mContentView, prevSelection.getStyles());
		for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; ++i) {
			Segment segment = mAdapter.getItem(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			if (!mContentView.getSegmentLocations(paragraph, mLocations)) {
				continue;
			}

			if (mLocations.bottom < y1 || mLocations.top > y2) {
				continue;
			}

			try {
				mSelectedTextByDragVisitor.reset(mCurrentSelection.getType(), mCurrentSelection.getStyles(), paragraph, renderOption);
				float tempX1 = x1 - mLocations.left;
				float tempY1 = y1 - mLocations.top;
				float tempX2 = x2 - mLocations.left;
				float tempY2 = y2 - mLocations.top;
				mSelectedTextByDragVisitor.setRegion(tempX1, tempY1, tempX2, tempY2);
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

	private void updateMotionSelection(@Nullable Selection prevSelection, Selection currentSelection) {
		// 先置换下 因为 adapter 在绘制的时候会先查这里
		mCurrentSelection = currentSelection;

		int size = mAdapter.getItemCount();
		if (mSelectionDiffBucket == null) {
			mSelectionDiffBucket = new BitBucket(size);
		}

		// 因此 需要变化的 item 都在 两个集合里了
		mSelectionDiffBucket.clear();
		for (int i = 0; i < currentSelection.size(); ++i) {
			Paragraph paragraph = currentSelection.getParagraph(i);
			paragraph.requestRedraw();
			int index = mAdapter.indexOf(paragraph);
			if (index < 0) {
				continue;
			}
			mSelectionDiffBucket.set(index, true);
		}

		if (prevSelection != null) {
			for (int i = 0; i < prevSelection.size(); ++i) {
				Paragraph paragraph = prevSelection.getParagraph(i);
				if (paragraph == null) {
					continue;
				}

				int index = mAdapter.indexOf(paragraph);
				if (!mSelectionDiffBucket.get(index)) {
					ParagraphSelection paragraphSelection = prevSelection.getParagraphSelection(paragraph);
					if (paragraphSelection != null) {
						paragraphSelection.recycle();
						paragraph.setSelection(Selection.Type.SELECTION, null);
					}
					paragraph.requestRedraw();
				}
			}
			prevSelection.recycle();
		}

		notifyUpdateSelectionDropView();
	}

	/**
	 * 清除选中区域
	 */
	public void clearSelection() {
		if (mCurrentSelection != null) {
			mCurrentSelection.clear();
			mCurrentSelection = null;
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
	public Selection selectParagraphs(ParagraphPredicates predicates, @NonNull Selection.Styles styles) {
		Document document = mAdapter.getDocument();
		clearSelection();

		for (int i = 0; i < document.getSegmentCount(); ++i) {
			Segment segment = document.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			selectParagraph((Paragraph) segment, predicates, styles);
		}

		if (styles.isEnableDrag()) {
			notifyUpdateSelectionDropView();
		}

		return mCurrentSelection;
	}

	private void selectParagraph(Paragraph paragraph, ParagraphPredicates predicates, @NonNull Selection.Styles styles) {
		if (paragraph == null) {
			return;
		}

		try {
			RenderOption renderOption = mAdapter.getRenderOption();
			mPredicatesDriveSelectedVisitor.reset(Selection.Type.SELECTION, renderOption, predicates, paragraph, styles);
			mPredicatesDriveSelectedVisitor.startVisit(paragraph);
			handleParagraphSelected(paragraph, styles);
		} catch (ParagraphVisitor.VisitException ignored) {
			/* do nothing */
		} finally {
			mPredicatesDriveSelectedVisitor.clear();
		}
	}

	private void addParagraphSelection(Selection selection, Paragraph paragraph) {
		ParagraphSelection paragraphSelection = paragraph.getSelection(selection.getType());
		if (paragraphSelection != null) {
			selection.add(paragraphSelection);
		}
	}

	/**
	 * 处理 paragraph 被选中
	 *
	 * @param paragraph paragraph
	 */
	private void handleParagraphSelected(Paragraph paragraph, Selection.Styles styles) {
		if (mCurrentSelection == null) {
			mCurrentSelection = Selection.obtain(Selection.Type.SELECTION, mContentView, styles);
		}

		addParagraphSelection(mCurrentSelection, paragraph);

		try {
			paragraph.requestRedraw();
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
		mDropView.setEnable(renderOption.isDragToSelectEnable());

		if (mCurrentSelection != null) {
			Selection.Styles styles = mCurrentSelection.getStyles();
			styles.update(renderOption);
		}

		if (mCurrentHighlightSelection != null) {
			Selection.Styles styles = mCurrentHighlightSelection.getStyles();
			styles.update(renderOption);
		}
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

	public Selection highlightParagraphs(ParagraphPredicates predicates, Selection.Styles styles) {
		Document document = mAdapter.getDocument();
		clearHighlight();

		for (int i = 0; i < document.getSegmentCount(); ++i) {
			Segment segment = document.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			highlightParagraph((Paragraph) segment, predicates, styles);
		}

		return mCurrentHighlightSelection;
	}

	private void highlightParagraph(Paragraph paragraph, ParagraphPredicates predicates, @NonNull Selection.Styles styles) {
		if (paragraph == null) {
			return;
		}

		try {
			RenderOption renderOption = mAdapter.getRenderOption();
			mPredicatesDriveSelectedVisitor.reset(Selection.Type.HIGHLIGHT, renderOption, predicates, paragraph, styles);
			mPredicatesDriveSelectedVisitor.startVisit(paragraph);
			handleParagraphHighlighted(paragraph, styles);
		} catch (ParagraphVisitor.VisitException ignored) {
			/* do nothing */
		} finally {
			mPredicatesDriveSelectedVisitor.clear();
		}
	}

	private void handleParagraphHighlighted(Paragraph paragraph, Selection.Styles styles) {
		if (mCurrentHighlightSelection == null) {
			mCurrentHighlightSelection = Selection.obtain(Selection.Type.HIGHLIGHT, mContentView, styles);
		}

		addParagraphHighlight(mCurrentHighlightSelection, paragraph);

		try {
			paragraph.requestRedraw();
		} catch (Throwable ignore) {
			/* do nothing */
		}
	}

	private void addParagraphHighlight(Selection selection, Paragraph paragraph) {
		ParagraphSelection paragraphSelection = paragraph.getSelection(selection.getType());
		if (paragraphSelection != null) {
			selection.add(paragraphSelection);
		}
	}

	public void clearHighlight() {
		if (mCurrentHighlightSelection != null) {
			mCurrentHighlightSelection.clear();
			mCurrentHighlightSelection = null;
		}
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
