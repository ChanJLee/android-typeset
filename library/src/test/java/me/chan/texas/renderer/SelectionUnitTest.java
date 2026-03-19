package me.chan.texas.renderer;

import me.chan.texas.misc.RectF;

import androidx.annotation.NonNull;

import me.chan.texas.TestUtils;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.test.mock.MockTextPaint;

import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Span;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextSpan;
import me.chan.texas.typesetter.ParagraphTypesetter;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SelectionUnitTest {

	private final MockMeasurer mMeasurer;
	private final TextAttribute mTextAttribute;
	private final PaintSet mPaintSet;

	public SelectionUnitTest() throws IOException {
		MockTextPaint textPaint = new MockTextPaint(1);
		mPaintSet = new PaintSet(textPaint);
		mMeasurer = new MockMeasurer(textPaint);
		mTextAttribute = new TextAttribute(mMeasurer);
	}

	@Test
	public void testParagraphSelection() throws ParagraphVisitor.VisitException {
		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);

		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, renderOption);
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		for (int i = 0; i < 20; ++i) {
			String text = String.valueOf(i);
			builder.newSpanBuilder()
					.next(text)
					.tag(i)
					.buildSpan();
		}

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		Paragraph paragraph = builder.build();
		paragraph.measure(mMeasurer, mTextAttribute);
		texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 10);

		ParagraphSelection paragraphSelection = ParagraphSelection.obtain(Selection.Type.SELECTION, Selection.Styles.createFromTouch(renderOption, true), paragraph);

		Assert.assertTrue(paragraphSelection.isEmpty());
		Span span = TextSpan.obtain(
				"fuck",
				0,
				1,
				null,
				null,
				null,
				null
		);
		paragraphSelection.appendBox(span);
		Assert.assertFalse(paragraphSelection.isEmpty());
		Assert.assertSame(paragraphSelection.getLastBox(), span);
		Assert.assertSame(paragraphSelection.getFirstBox(), span);

		Span span2 = TextSpan.obtain(
				"fuck",
				0,
				2,
				null,
				null,
				null,
				null
		);
		paragraphSelection.appendBox(span2);
		Assert.assertFalse(paragraphSelection.isEmpty());
		Assert.assertSame(paragraphSelection.getLastBox(), span2);
		Assert.assertSame(paragraphSelection.getFirstBox(), span);

		paragraphSelection.clear();
		Assert.assertTrue(paragraphSelection.isEmpty());
		Assert.assertNull(paragraphSelection.getLastBox());
		Assert.assertNull(paragraphSelection.getFirstBox());

		Set<Span> set = new HashSet<>();
		for (int i = 0; i < 10; ++i) {
			Element element = paragraph.getElement(i);
			if (element instanceof Span) {
				Span span1 = (Span) element;
				set.add(span1);
				paragraphSelection.appendBox(span1);
			}
		}
		paragraph.setSelection(Selection.Type.SELECTION, paragraphSelection);

		Set<Object> tags = new HashSet<>(paragraphSelection.getSelectedTags());

		TestBoxVisitor visitor = new TestBoxVisitor(set, paragraphSelection);
		visitor.test(paragraph, renderOption);

		TestTagBoxVisitor testTagBoxVisitor = new TestTagBoxVisitor(tags, paragraphSelection);
		testTagBoxVisitor.test(paragraph, renderOption);

		tags.clear();
		set.clear();
		paragraphSelection.clear();
		visitor = new TestBoxVisitor(set, paragraphSelection);
		visitor.test(paragraph, renderOption);
		testTagBoxVisitor = new TestTagBoxVisitor(tags, paragraphSelection);
		testTagBoxVisitor.test(paragraph, renderOption);

		paragraphSelection.recycle();
		TestUtils.testRecycled(paragraphSelection);
	}

	private static class TestBoxVisitor extends ParagraphVisitor {
		private final Set<Span> mBoxes;
		private int mCount;
		private ParagraphSelection mParagraphSelection;

		public TestBoxVisitor(Set<Span> boxes, ParagraphSelection paragraphSelection) {
			mBoxes = boxes;
			mParagraphSelection = paragraphSelection;
		}

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {

		}

		@Override
		protected void onVisitParagraphEnd(Paragraph paragraph) {

		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {

		}

		@Override
		protected void onVisitLineEnd(Line line, float x, float y) {

		}

		@Override
		protected void onVisitBox(Span span, RectF inner, RectF outer, @NonNull RendererContext context) {
			if (mParagraphSelection.isSelected(span)) {
				++mCount;
				Assert.assertTrue(mBoxes.contains(span));
			}
		}

		public void test(Paragraph subParagraph, RenderOption renderOption) throws VisitException {
			visit(subParagraph);
			Assert.assertEquals(mCount, mBoxes.size());
		}
	}

	private static class TestTagBoxVisitor extends ParagraphVisitor {
		private final Set<Object> mBoxes;
		private int mCount;
		private ParagraphSelection mParagraphSelection;

		public TestTagBoxVisitor(Set<Object> boxes, ParagraphSelection paragraphSelection) {
			mBoxes = boxes;
			mParagraphSelection = paragraphSelection;
		}


		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {

		}

		@Override
		protected void onVisitParagraphEnd(Paragraph paragraph) {

		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {

		}

		@Override
		protected void onVisitLineEnd(Line line, float x, float y) {

		}

		@Override
		protected void onVisitBox(Span span, RectF inner, RectF outer, @NonNull RendererContext context) {
			if (mParagraphSelection.isSelected(span)) {
				++mCount;
				Assert.assertTrue(mBoxes.contains(span.getTag()));
			}
		}

		public void test(Paragraph subParagraph, RenderOption renderOption) throws VisitException {
			visit(subParagraph);
			Assert.assertEquals(mCount, mBoxes.size());
		}
	}
}
