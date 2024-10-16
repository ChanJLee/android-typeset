package me.chan.texas.renderer.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import me.chan.texas.TexasOption;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.adapter.TextAdapter;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.ui.TexasRendererAdapter;
import me.chan.texas.renderer.ui.rv.TexasLayoutManager;
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.source.SourceOpenException;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.typesetter.ParagraphTypesetter;

public class SelectionUnitTest {
	private Document mDocument;
	private RenderOption mRenderOption;
	private SelectionManager mSelectionManager;
	private MyLayoutManager mLayoutManager;

	@Before
	public void init() throws SourceOpenException, ParseException {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setMockTextSize(1);

		Measurer measurer = new MockMeasurer(textPaint);
		TextAttribute textAttribute = new TextAttribute(measurer);
		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), measurer, textAttribute, mRenderOption = new RenderOption());

		MockTextPaint paint = new MockTextPaint();
		paint.setTextSize(1);
		TextAdapter textParser = new TextAdapter();
		textParser.setData("1 2 3 4 5 6 7 8 9\na b c d e f g h i\n一 二 三 四 五 六 七 八 九");
		mDocument = textParser.getDocument(texasOption);

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
			assertEquals(layout.getLineCount(), 3);
		}

		mSelectionManager = new SelectionManager(new MyAdapter(),
				mLayoutManager = new MyLayoutManager(mDocument),
				new MySelectionListener(), null, new MyRecyclerView());

		Assert.assertNull(mSelectionManager.getCurrentSelection());
	}

	@Test
	public void testBase() {

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

	private class MySelectionListener implements SelectionManager.Listener {

		@Override
		public void onSpanClicked(TouchEvent event, Object tag) {

		}

		@Override
		public void onSpanLongClicked(TouchEvent event, Object tag) {

		}

		@Override
		public void onDragStart(TouchEvent event) {

		}

		@Override
		public void onDragEnd(TouchEvent event) {

		}

		@Override
		public void onDragDismiss() {

		}

		@Override
		public void onSegmentDoubleClicked(TouchEvent event, Object paragraphTag) {

		}

		@Override
		public void onSegmentClicked(TouchEvent event, Object paragraphTag) {

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
