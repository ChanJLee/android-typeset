package me.chan.texas.renderer.selection;

import static org.junit.Assert.assertNotNull;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.ui.TexasRendererAdapter;
import me.chan.texas.renderer.ui.rv.TexasLayoutManager;
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.renderer.ui.text.OnSelectedChangedListener;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.typesetter.ParagraphTypesetter;

public class SelectionManagerUnitTest {
	private Document mDocument;
	private RenderOption mRenderOption;
	private SelectionManager mSelectionManager;
	private MyLayoutManager mLayoutManager;
	private MySelectionListener mSelectionListener;

	@Before
	public void init() {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setMockTextSize(1);

		Measurer measurer = new MockMeasurer(textPaint);
		TextAttribute textAttribute = new TextAttribute(measurer);
		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), measurer, textAttribute, mRenderOption = new RenderOption());

		mDocument = Document.obtain();
		List<Segment> list = new ArrayList<>();
		list.add(Paragraph.Builder.newBuilder(texasOption).tag("p1")
				.stream("1 2 3 4 5 6 7 8 9", token -> Paragraph.Span.obtain(token)
						.tag(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString())).build());
		list.add(Paragraph.Builder.newBuilder(texasOption).tag("p2").stream("a b c d e f g h i", token ->
				Paragraph.Span.obtain(token).tag(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString())).build());
		list.add(Paragraph.Builder.newBuilder(texasOption).setTypesetPolicy(Paragraph.TYPESET_POLICY_DEFAULT).tag("p3").stream("一 二 三 四 五 六 七 八 九", token -> Paragraph.Span.obtain(token)
				.tag(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString())).build());
		mDocument.insertTail(list);

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();

		for (int i = 0; i < mDocument.getSegmentCount(); ++i) {
			Segment segment = mDocument.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 5, 1);
			assertNotNull(paragraph);
			Layout layout = paragraph.getLayout();
			Assert.assertEquals(layout.getLineCount(), 3);
		}

		mSelectionManager = new SelectionManager(new MyAdapter(),
				mLayoutManager = new MyLayoutManager(mDocument),
				mSelectionListener = new MySelectionListener(), null, new MyRecyclerView());

		Assert.assertNull(mSelectionManager.getCurrentSelection());
	}

	@Test
	public void testPredicate() {
		Selection selection = mSelectionManager.selectParagraphs(new ParagraphPredicates() {
			@Override
			public boolean acceptSpan(@Nullable Object spanTag) {
				return false;
			}

			@Override
			public boolean acceptParagraph(@Nullable Object paragraphTag) {
				return false;
			}
		}, new Selection.Styles(1, 2).setEnableDrag(false));
		Assert.assertTrue(selection.isEmpty());

		Selection prev = selection;
		selection = mSelectionManager.selectParagraphs(new ParagraphPredicates() {
			@Override
			public boolean acceptSpan(@Nullable Object spanTag) {
				return "1".equals(spanTag) || "一".equals(spanTag) || "二".equals(spanTag) || "三".equals(spanTag) || "四".equals(spanTag);
			}

			@Override
			public boolean acceptParagraph(@Nullable Object paragraphTag) {
				return "p1".equals(paragraphTag) || "p3".equals(paragraphTag);
			}
		}, new Selection.Styles(1, 2).setEnableDrag(false));

		Assert.assertNotSame(prev, selection);
		Assert.assertFalse(selection.isEmpty());
		Assert.assertEquals(2, selection.size());

		ParagraphSelection paragraphSelection = selection.get(0);
		RectF first = paragraphSelection.getFirstRegion();
		RectF last = paragraphSelection.getLastRegion();
		Assert.assertSame(first, last);

		Assert.assertEquals(first.left, 0, 0.1);
		Assert.assertEquals(first.top, 0, 0.1);
		Assert.assertEquals(first.right, 1.5, 0.1);
		Assert.assertEquals(first.bottom, 1, 0.1);
		Assert.assertFalse(paragraphSelection.isEmpty());
		Assert.assertFalse(paragraphSelection.isSelectedRegionEmpty());

		List<Object> list = paragraphSelection.getSelectedTags();
		Assert.assertEquals(list.size(), 1);
		Assert.assertEquals(list.get(0), "1");

		paragraphSelection = selection.get(1);
		first = paragraphSelection.getFirstRegion();
		last = paragraphSelection.getLastRegion();
		Assert.assertNotSame(first, last);

		Assert.assertEquals(first.left, 0, 0.1);
		Assert.assertEquals(first.top, 0, 0.1);
		Assert.assertEquals(first.right, 5, 0.1);
		Assert.assertEquals(first.bottom, 1, 0.1);

		Assert.assertEquals(last.left, 0, 0.1);
		Assert.assertEquals(last.top, 2, 0.1);
		Assert.assertEquals(last.right, 1.5, 0.1);
		Assert.assertEquals(last.bottom, 3, 0.1);

		list = paragraphSelection.getSelectedTags();
		Assert.assertEquals(list.size(), 4);
		Assert.assertEquals(list.get(0), "一");
		Assert.assertEquals(list.get(1), "二");
		Assert.assertEquals(list.get(2), "三");
		Assert.assertEquals(list.get(3), "四");
	}

	@Test
	public void testMotion() {
		Selection selection = mSelectionManager.getCurrentSelection();
		Assert.assertNull(selection);

		Paragraph paragraph = (Paragraph) mDocument.getSegment(0);
		TouchEvent touchEvent = TouchEvent.obtain(null, 0, 0, 0, 0);
		Assert.assertTrue(mSelectionManager.onSegmentClicked(touchEvent, paragraph, OnSelectedChangedListener.EVENT_CLICKED));
		Assert.assertEquals(mSelectionListener.mEvent, SelectionEvent.SEGMENT_CLICKED);

		Assert.assertTrue(mSelectionManager.onSegmentClicked(touchEvent, paragraph, OnSelectedChangedListener.EVENT_DOUBLE_CLICKED));
		Assert.assertEquals(mSelectionListener.mEvent, SelectionEvent.SEGMENT_DOUBLE_CLICKED);

		Assert.assertFalse(mSelectionManager.onSegmentClicked(touchEvent, paragraph, OnSelectedChangedListener.EVENT_LONG_CLICKED));
		Assert.assertEquals(mSelectionListener.mEvent, SelectionEvent.SEGMENT_DOUBLE_CLICKED);
	}

	private class MyRecyclerView implements TexasRecyclerView {

		@Override
		public void addOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {

		}

		@Override
		public void allowHandleTouchEvent() {

		}

		@Override
		public void disallowHandleTouchEvent() {

		}

		@Override
		public TexasLayoutManager getTexasLayoutManager() {
			return mLayoutManager;
		}

		@Override
		public void getChildLocations(View child, int[] locations) {

		}

		@Override
		public void scrollBy(int x, int y) {

		}

		@Override
		public int getHeight() {
			return 10000;
		}
	}

	enum SelectionEvent {
		NONE,
		SPAN_CLICKED,
		SPAN_LONG_CLICKED,
		DRAG_START,
		DRAG_END,
		DRAG_DISMISS,
		SEGMENT_DOUBLE_CLICKED,
		SEGMENT_CLICKED
	}

	private class MySelectionListener implements SelectionManager.Listener {

		private SelectionEvent mEvent = SelectionEvent.NONE;

		@Override
		public void onSpanClicked(TouchEvent event, Object tag) {
			mEvent = SelectionEvent.SPAN_CLICKED;
		}

		@Override
		public void onSpanLongClicked(TouchEvent event, Object tag) {
			mEvent = SelectionEvent.SPAN_LONG_CLICKED;
		}

		@Override
		public void onDragStart(TouchEvent event) {
			mEvent = SelectionEvent.DRAG_START;
		}

		@Override
		public void onDragEnd(TouchEvent event) {
			mEvent = SelectionEvent.DRAG_END;
		}

		@Override
		public void onDragDismiss() {
			mEvent = SelectionEvent.DRAG_DISMISS;
		}

		@Override
		public void onSegmentDoubleClicked(TouchEvent event, Object paragraphTag) {
			mEvent = SelectionEvent.SEGMENT_DOUBLE_CLICKED;
		}

		@Override
		public void onSegmentClicked(TouchEvent event, Object paragraphTag) {
			mEvent = SelectionEvent.SEGMENT_CLICKED;
		}
	}

	private class MyLayoutManager implements TexasLayoutManager {
		private int mFirstVisibleItemPosition;
		private int mLastVisibleItemPosition;
		private int mFirstCompletelyVisibleItemPosition;
		private int mLastCompletelyVisibleItemPosition;

		public MyLayoutManager(Document document) {
			mFirstVisibleItemPosition = 0;
			mLastVisibleItemPosition = document.getSegmentCount() - 1;
			mFirstCompletelyVisibleItemPosition = 0;
			mLastCompletelyVisibleItemPosition = document.getSegmentCount() - 1;
		}

		@Override
		public int findFirstVisibleItemPosition() {
			return mFirstVisibleItemPosition;
		}

		@Override
		public int findLastVisibleItemPosition() {
			return mLastVisibleItemPosition;
		}

		@Override
		public View findViewByPosition(int index) {
			return null;
		}

		@Override
		public int findFirstCompletelyVisibleItemPosition() {
			return mFirstCompletelyVisibleItemPosition;
		}

		@Override
		public int findLastCompletelyVisibleItemPosition() {
			return mLastCompletelyVisibleItemPosition;
		}
	}

	private class MyAdapter implements TexasRendererAdapter {

		@Override
		public int getItemCount() {
			return mDocument.getSegmentCount();
		}

		@Override
		public Segment getItem(int position) {
			return mDocument.getSegment(position);
		}

		@Override
		public Document getDocument() {
			return mDocument;
		}

		@Override
		public RenderOption getRenderOption() {
			return mRenderOption;
		}

		@Override
		public int sendSignal(Segment segment, Object sig) {
			return mDocument.indexOfSegment(segment);
		}

		@Override
		public int sendSignal(int index, Object sig) {
			return index;
		}

		@Override
		public int indexOf(Segment segment) {
			return mDocument.indexOfSegment(segment);
		}
	}
}
