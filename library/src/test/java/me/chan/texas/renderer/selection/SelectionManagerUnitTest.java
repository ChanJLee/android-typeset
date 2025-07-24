package me.chan.texas.renderer.selection;

import static org.junit.Assert.assertNotNull;

import android.graphics.Canvas;

import me.chan.texas.misc.RectF;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.TestUtils;
import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.selection.overlay.DragSelectView;
import me.chan.texas.renderer.ui.TexasRendererAdapter;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.renderer.ui.rv.TexasLayoutManager;
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.renderer.ui.text.OnMeasureInterceptor;
import me.chan.texas.renderer.ui.text.OnSelectedChangedListener;
import me.chan.texas.renderer.ui.text.TextureParagraph;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.typesetter.ParagraphTypesetter;
import me.chan.texas.utils.TexasUtils;
import me.chan.texas.utils.concurrency.Worker;

public class SelectionManagerUnitTest {
	private Document mDocument;
	private RenderOption mRenderOption;
	private SelectionManager mSelectionManager;
	private MyLayoutManager mLayoutManager;
	private MySelectionListener mSelectionListener;
	private MyDragView mDragSelectView;
	private MyRecyclerView mContainer;
	private List<TextureParagraph> mTextureParagraphs;

	@Before
	public void init() {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setMockTextSize(1);

		Measurer measurer = new MockMeasurer(textPaint);
		TextAttribute textAttribute = new TextAttribute(measurer);
		TexasOption texasOption = new TexasOption(new PaintSet(textPaint), Hyphenation.getInstance(), measurer, textAttribute, mRenderOption = new RenderOption().setLineSpacingExtra(1));

		Document.Builder builder = new Document.Builder();
		List<Segment> list = new ArrayList<>();
		list.add(Paragraph.Builder.newBuilder(texasOption).tag("p1")
				.stream("1 2 3 4 5 6 7 8 9", token -> Paragraph.Span.obtain(token)
						.tag(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString())).build());
		list.add(Paragraph.Builder.newBuilder(texasOption).tag("p2").stream("a b c d e f g h i", token ->
				Paragraph.Span.obtain(token).tag(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString())).build());
		list.add(Paragraph.Builder.newBuilder(texasOption).setTypesetPolicy(Paragraph.TYPESET_POLICY_DEFAULT).tag("p3").stream("一 二 三 四 五 六 七 八 九", token -> Paragraph.Span.obtain(token)
				.tag(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString())).build());
		builder.addSegments(0, list);
		mDocument = builder.build();

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();

		for (int i = 0; i < mDocument.getSegmentCount(); ++i) {
			Segment segment = mDocument.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			paragraph.measure(measurer, textAttribute);
			texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 5);
			assertNotNull(paragraph);
			Layout layout = paragraph.getLayout();
			Assert.assertEquals(layout.getLineCount(), 3);
		}

		mSelectionManager = new SelectionManager(new MyAdapter(),
				mLayoutManager = new MyLayoutManager(mDocument),
				mSelectionListener = new MySelectionListener(),
				mDragSelectView = new MyDragView(),
				mContainer = new MyRecyclerView()
		);
		mSelectionManager.setSpanTouchEventHandler(new SpanTouchEventHandler() {

			@Override
			public boolean isSpanClickable(@Nullable Object tag) {
				return true;
			}

			@Override
			public boolean applySpanClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
				return clickedTag == otherTag;
			}

			@Override
			public boolean applySpanLongClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
				return clickedTag == otherTag;
			}
		});

		Assert.assertNull(mSelectionManager.getCurrentSelection());

		mTextureParagraphs = new ArrayList<>();
		Paragraph paragraph = (Paragraph) mDocument.getSegment(0);
		int offset = 0;
		mTextureParagraphs.add(new MyTextureParagraph(paragraph, offset));
		offset += (paragraph.getLayout().getHeight() + 1);
		mTextureParagraphs.add(new MyTextureParagraph(paragraph = (Paragraph) mDocument.getSegment(1), offset));
		offset += (paragraph.getLayout().getHeight() + 1);
		mTextureParagraphs.add(new MyTextureParagraph(paragraph = (Paragraph) mDocument.getSegment(2), offset));
	}

	@Test
	public void testPredicate() {
		mSelectionManager.clearSelection();
		Assert.assertNull(mSelectionManager.getCurrentSelection());
		Selection selection = mSelectionManager.selectParagraphs(new ParagraphPredicates() {
			@Override
			public boolean acceptSpan(@Nullable Object spanTag) {
				return false;
			}

			@Override
			public boolean acceptParagraph(@Nullable Object paragraphTag) {
				return false;
			}
		}, Selection.Styles.create(1, 2).setEnableDrag(false));
		Assert.assertTrue(selection.isEmpty());
		Assert.assertSame(selection, mSelectionManager.getCurrentSelection());

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
		}, Selection.Styles.create(1, 2).setEnableDrag(false));
		Assert.assertSame(selection, mSelectionManager.getCurrentSelection());

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
	public void testHighlight() {
		Assert.assertNull(mSelectionManager.getCurrentSelection());
		Selection selection = mSelectionManager.highlightParagraphs(new ParagraphPredicates() {
			@Override
			public boolean acceptSpan(@Nullable Object spanTag) {
				return false;
			}

			@Override
			public boolean acceptParagraph(@Nullable Object paragraphTag) {
				return false;
			}
		}, Selection.Styles.create(1, 2).setEnableDrag(false));
		Assert.assertTrue(selection.isEmpty());
		Assert.assertNull(mSelectionManager.getCurrentSelection());

		Selection prev = selection;
		selection = mSelectionManager.highlightParagraphs(new ParagraphPredicates() {
			@Override
			public boolean acceptSpan(@Nullable Object spanTag) {
				return "1".equals(spanTag) || "一".equals(spanTag) || "二".equals(spanTag) || "三".equals(spanTag) || "四".equals(spanTag);
			}

			@Override
			public boolean acceptParagraph(@Nullable Object paragraphTag) {
				return "p1".equals(paragraphTag) || "p3".equals(paragraphTag);
			}
		}, Selection.Styles.create(1, 2).setEnableDrag(false));

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

		mSelectionManager.clearHighlight();
		Assert.assertTrue(selection.isEmpty());
	}

	@Test
	public void testMotion() {
		// 0-----1-----2-----3-----4-----5
		// |--1--|-----|--2--|-----|--3--|
		// |-----------------------------|
		// |--4--|-----|--5--|-----|--6--|
		// |-----------------------------|
		// |--7--|-----|--8--|-----|--9--|
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

		Box box = (Box) paragraph.getElement(0);
		Assert.assertTrue(mSelectionManager.onBoxSelected(touchEvent, paragraph, OnSelectedChangedListener.EVENT_LONG_CLICKED, box));
		Assert.assertEquals(SelectionEvent.SPAN_LONG_CLICKED, mSelectionListener.mEvent);
		Assert.assertNotNull(paragraph.getSelection(Selection.Type.SELECTION));
		Assert.assertTrue(mSelectionManager.onBoxSelected(touchEvent, paragraph, OnSelectedChangedListener.EVENT_CLICKED, box));
		Assert.assertEquals(SelectionEvent.SPAN_CLICKED, mSelectionListener.mEvent);
		mSelectionManager.handleClickNothing();

		for (TextureParagraph textureParagraph : mTextureParagraphs) {
			Paragraph tmp = textureParagraph.getParagraph();
			Assert.assertNull(tmp.getSelection(Selection.Type.SELECTION));
		}

		Assert.assertEquals(View.GONE, mDragSelectView.mVisibility);
		Assert.assertTrue(mSelectionManager.onBoxSelected(touchEvent, paragraph, OnSelectedChangedListener.EVENT_LONG_CLICKED, box));
		selection = mSelectionManager.getCurrentSelection();
		Assert.assertNotNull(selection);
		Assert.assertEquals(View.VISIBLE, mDragSelectView.mVisibility);

		Selection.RectEdge edge = selection.getSelectedRectEdge();
		Assert.assertEquals(edge.topX, 0, 0.1);
		Assert.assertEquals(edge.topY, 0, 0.1);
		Assert.assertEquals(edge.bottomX, 1.5, 0.1);
		Assert.assertEquals(edge.bottomY, 1, 0.1);

		// 顶部超出屏幕
		{
			mSelectionManager.handleMoveToSelection(-1, -1, 1.5f, 1);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 0, 0.1);
			Assert.assertEquals(edge.topY, 0, 0.1);
			Assert.assertEquals(edge.bottomX, 1.5, 0.1);
			Assert.assertEquals(edge.bottomY, 1, 0.1);
			Assert.assertEquals(1, selection.size());
			ParagraphSelection paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "1");

			mSelectionManager.handleMoveToSelection(-1, -1, 2f, 1f);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 0, 0.1);
			Assert.assertEquals(edge.topY, 0, 0.1);
			Assert.assertEquals(edge.bottomX, 3.5, 0.1);
			Assert.assertEquals(edge.bottomY, 1, 0.1);
			Assert.assertEquals(1, selection.size());
			paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "1", "2");

			mSelectionManager.handleMoveToSelection(-1, -1, 3f, 1f);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 0, 0.1);
			Assert.assertEquals(edge.topY, 0, 0.1);
			Assert.assertEquals(edge.bottomX, 3.5, 0.1);
			Assert.assertEquals(edge.bottomY, 1, 0.1);
			Assert.assertEquals(1, selection.size());
			paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "1", "2");

			mSelectionManager.handleMoveToSelection(-1, -1, 1.5f, 3);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 0, 0.1);
			Assert.assertEquals(edge.topY, 0, 0.1);
			Assert.assertEquals(edge.bottomX, 1.5, 0.1);
			Assert.assertEquals(edge.bottomY, 3, 0.1);
			Assert.assertEquals(1, selection.size());
			paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "1", "2", "3", "4");

			mSelectionManager.handleMoveToSelection(-1, -1, 1.5f, 2);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 0, 0.1);
			Assert.assertEquals(edge.topY, 0, 0.1);
			Assert.assertEquals(edge.bottomX, 5, 0.1);
			Assert.assertEquals(edge.bottomY, 1, 0.1);
			Assert.assertEquals(1, selection.size());
			paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "1", "2", "3");

			mSelectionManager.handleMoveToSelection(-1, -1, 3, 3);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 0, 0.1);
			Assert.assertEquals(edge.topY, 0, 0.1);
			Assert.assertEquals(edge.bottomX, 3.5, 0.1);
			Assert.assertEquals(edge.bottomY, 3, 0.1);
			Assert.assertEquals(1, selection.size());
			paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "1", "2", "3", "4", "5");
		}

		// 正常的
		{
			mSelectionManager.handleMoveToSelection(1f, 0, 3, 3);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 0, 0.1);
			Assert.assertEquals(edge.topY, 0, 0.1);
			Assert.assertEquals(edge.bottomX, 3.5, 0.1);
			Assert.assertEquals(edge.bottomY, 3, 0.1);
			Assert.assertEquals(1, selection.size());
			ParagraphSelection paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "1", "2", "3", "4", "5");

			mSelectionManager.handleMoveToSelection(1.5f, 0, 3, 3);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 1.5, 0.1);
			Assert.assertEquals(edge.topY, 0, 0.1);
			Assert.assertEquals(edge.bottomX, 3.5, 0.1);
			Assert.assertEquals(edge.bottomY, 3, 0.1);
			Assert.assertEquals(1, selection.size());
			paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "2", "3", "4", "5");

			mSelectionManager.handleMoveToSelection(1.5f, 1, 3, 3);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 1.5, 0.1);
			Assert.assertEquals(edge.topY, 0, 0.1);
			Assert.assertEquals(edge.bottomX, 3.5, 0.1);
			Assert.assertEquals(edge.bottomY, 3, 0.1);
			Assert.assertEquals(1, selection.size());
			paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "2", "3", "4", "5");

			paragraphSelection.recycle();
			TestUtils.testRecycled(paragraphSelection);
		}

		// 小区域
		{
			mSelectionManager.handleMoveToSelection(1, 2, 3, 3);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 0, 0.1);
			Assert.assertEquals(edge.topY, 2, 0.1);
			Assert.assertEquals(edge.bottomX, 3.5, 0.1);
			Assert.assertEquals(edge.bottomY, 3, 0.1);
			Assert.assertEquals(1, selection.size());
			ParagraphSelection paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "4", "5");
		}

		// 底部超出去
		{
			mSelectionManager.handleMoveToSelection(4, 4, 6f, 6f);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 3.5, 0.1);
			Assert.assertEquals(edge.topY, 4, 0.1);
			Assert.assertEquals(edge.bottomX, 5, 0.1);
			Assert.assertEquals(edge.bottomY, 5, 0.1);
			Assert.assertEquals(1, selection.size());
			ParagraphSelection paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "9");

			mSelectionManager.handleMoveToSelection(2, 2, 6, 6f);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 1.5, 0.1);
			Assert.assertEquals(edge.topY, 2, 0.1);
			Assert.assertEquals(edge.bottomX, 5, 0.1);
			Assert.assertEquals(edge.bottomY, 5, 0.1);
			Assert.assertEquals(1, selection.size());
			paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "5", "6", "7", "8", "9");
		}

		// 跨区域
		{
			mSelectionManager.handleMoveToSelection(4, 4, 7, 7f);
			selection = mSelectionManager.getCurrentSelection();
			edge = selection.getSelectedRectEdge();
			Assert.assertEquals(edge.topX, 3.5, 0.1);
			Assert.assertEquals(edge.topY, 4, 0.1);
			Assert.assertEquals(edge.bottomX, 5, 0.1);
			Assert.assertEquals(edge.bottomY, 7, 0.1);
			Assert.assertEquals(2, selection.size());
			ParagraphSelection paragraphSelection = selection.get(0);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "9");

			paragraphSelection = selection.get(1);
			checkSelectedTag(paragraphSelection.getSelectedTags(), "a", "b", "c");
		}
	}

	private static void checkSelectedTag(List<Object> actual, Object... excepts) {
		Assert.assertEquals(actual.size(), excepts.length);
		for (int i = 0; i < actual.size(); ++i) {
			Assert.assertEquals(actual.get(i), excepts[i]);
		}
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
		public void getChildLocations(TextureParagraph child, int[] locations) {
			MyTextureParagraph paragraph = (MyTextureParagraph) child;
			paragraph.getLocationOnScreen(locations);
		}

		@Override
		public void scrollBy(int x, int y) {

		}

		@Override
		public int getHeight() {
			return 10000;
		}

		@Nullable
		@Override
		public Document getDocument() {
			return mDocument;
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

	private class MyTextureParagraph implements TextureParagraph {
		private final Paragraph mParagraph;
		private final int mOffset;

		private MyTextureParagraph(Paragraph paragraph, int offset) {
			mParagraph = paragraph;
			mOffset = offset;
		}

		@Nullable
		@Override
		public Canvas lockCanvas(int width, int height) {
			return null;
		}

		@Override
		public void unlockCanvasAndPost(Canvas canvas) {

		}

		@Override
		public void getLocationOnScreen(int[] location) {
			location[0] = 0;
			location[1] = mOffset;
		}

		@Override
		public void render(@NonNull Paragraph paragraph, @NonNull PaintSet paintSet, @NonNull RenderOption renderOption, @Nullable ParagraphDecor decor, @Nullable SpanTouchEventHandler spanClickedEventHandler) {

		}

		@Override
		public void setOnTextSelectedListener(OnSelectedChangedListener onTextSelectedListener) {

		}

		@Override
		public Worker.Token getToken() {
			return null;
		}

		@Override
		public void syncUI() {

		}

		@Override
		public Paragraph getParagraph() {
			return mParagraph;
		}

		@Override
		public void clear() {

		}

		@Override
		public int getHeight() {
			return mParagraph.getLayout().getHeight();
		}

		@Override
		public ViewGroup.LayoutParams getLayoutParams() {
			return null;
		}

		@Override
		public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {

		}

		@Override
		public void setOnMeasureInterceptor(OnMeasureInterceptor interceptor) {

		}
	}

	private class MyDragView implements DragSelectView {
		private float mX1;
		private float mY1;
		private float mX2;
		private float mY2;
		private float mAdviseOffsetY;

		private float mContentScrollY;
		private int mVisibility;

		@Override
		public void setVisibility(int visible) {
			mVisibility = visible;
		}

		@Override
		public void setSelectionManager(SelectionManager selectionManager) {

		}

		@Override
		public void updateContentScrollY(int y) {
			mContentScrollY = y;
		}

		@Override
		public void renderRegion(float x1, float y1, float x2, float y2, float adviseOffsetY) {
			mX1 = x1;
			mY1 = y1;
			mX2 = x2;
			mY2 = y2;
			mAdviseOffsetY = adviseOffsetY;
		}

		@Override
		public void setColor(int color) {

		}

		@Override
		public void setEnable(boolean enable) {

		}
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
		public TextureParagraph findTextureParagraphByPosition(int index) {
			return mTextureParagraphs.get(index);
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
		public int indexOf(Segment segment) {
			return mDocument.indexOfSegment(segment);
		}

		@Override
		public void updateSegment(Object holder, Segment segment) {

		}
	}

	@Test
	public void testTntersect() {
		RectF lhs = new RectF();
		RectF rhs = new RectF();

		TexasUtils.setRect(rhs, 0, 0, 2, 2);
		TexasUtils.setRect(lhs, -1, -1, 1, 1);
		Assert.assertTrue(TexasUtils.intersects(lhs, rhs));

		TexasUtils.setRect(lhs, 0, 0, 1, 1);
		Assert.assertTrue(TexasUtils.intersects(lhs, rhs));

		TexasUtils.setRect(lhs, 1, 1, 3, 3);
		Assert.assertTrue(TexasUtils.intersects(lhs, rhs));
	}
}
