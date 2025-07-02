package me.chan.texas.renderer.selection;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.misc.BitBucket;
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


@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelectionManager implements OnSelectedChangedListener {
	private Selection mCurrentSelection;
	private Selection mCurrentHighlightSelection;

	private final TexasRendererAdapter mAdapter;
	private final TexasLayoutManager mLayoutManager;
	private final Listener mListener;
	private DragSelectView mDropView;
	private final TexasRecyclerView mContentView;

	
	private final PredicatesDriveSelectedVisitor mPredicatesDriveSelectedVisitor = new PredicatesDriveSelectedVisitor();
	
	private final SelectedTextByDragVisitor mSelectedTextByDragVisitor = new SelectedTextByDragVisitor();
	
	private final SelectedTextByClickedVisitor mSelectedTextByClickedVisitor = new SelectedTextByClickedVisitor();

	
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
		if (mListener == null) {
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

	
	@Override
	public boolean onBoxSelected(TouchEvent e, Paragraph paragraph, @EventType int eventType, Box box) {
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



		mContentView.disallowHandleTouchEvent();
		mDropView.setVisibility(View.VISIBLE);
		mDropView.renderRegion(selectedRectEdge.topX,
				selectedRectEdge.topY + 1 ,
				selectedRectEdge.bottomX,
				selectedRectEdge.bottomY - 1 ,
				selectedRectEdge.lineHeight);
	}

	
	public void handleClickNothing() {
		handleClickNothing(false);
	}

	
	public void handleClickNothing(boolean silence) {
		mContentView.allowHandleTouchEvent();
		mDropView.setVisibility(View.GONE);
		clearSelection();
		if (!silence) {
			mListener.onDragDismiss();
		}
	}

	
	public void handleDragStart(TouchEvent event) {
		mListener.onDragStart(event);
	}

	
	public void handleDragEnd(TouchEvent event) {
		mListener.onDragEnd(event);
	}

	
	@SuppressLint("NotifyDataSetChanged")
	public void handleMoveToSelection(float x1, float y1, float x2, float y2) {
		int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
		int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

		Selection prevSelection = mCurrentSelection;

		RenderOption renderOption = mAdapter.getRenderOption();

		Selection currentSelection = Selection.obtain(mCurrentSelection.getType(), mContentView, prevSelection.getStyles());
		for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; ++i) {
			TextureParagraph textureParagraph = mLayoutManager.findTextureParagraphByPosition(i);
			if (textureParagraph == null) {
				continue;
			}

			mContentView.getChildLocations(textureParagraph, mLocations);
			if (mLocations[1] + textureParagraph.getHeight() < y1) {
				continue;
			}

			if (mLocations[1] > y2) {
				continue;
			}

			try {
				Paragraph paragraph = textureParagraph.getParagraph();
				mSelectedTextByDragVisitor.reset(mCurrentSelection.getType(), mCurrentSelection.getStyles(), paragraph, renderOption);
				float tempX1 = x1 - mLocations[0];
				float tempY1 = y1 - mLocations[1];
				float tempX2 = x2 - mLocations[0];
				float tempY2 = y2 - mLocations[1];
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

		mCurrentSelection = currentSelection;

		int size = mAdapter.getItemCount();
		if (mSelectionDiffBucket == null) {
			mSelectionDiffBucket = new BitBucket(size);
		}


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

	
	public void clearSelection() {
		if (mCurrentSelection != null) {
			mCurrentSelection.clear();
			mCurrentSelection = null;
		}
	}

	
	public void clear() {
		clearSelection();
		notifyUpdateSelectionDropView();
	}

	
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

	
	private void handleParagraphSelected(Paragraph paragraph, Selection.Styles styles) {
		if (mCurrentSelection == null) {
			mCurrentSelection = Selection.obtain(Selection.Type.SELECTION, mContentView, styles);
		}

		addParagraphSelection(mCurrentSelection, paragraph);

		try {
			paragraph.requestRedraw();
		} catch (Throwable ignore) {
			
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
