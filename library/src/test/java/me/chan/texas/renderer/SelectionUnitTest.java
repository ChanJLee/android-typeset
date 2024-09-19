package me.chan.texas.renderer;

import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.shanbay.lib.texas.TestUtils;
import com.shanbay.lib.texas.test.mock.MockTextPaint;

import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.typesetter.ParagraphTypesetter;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SelectionUnitTest {

	private final MockMeasurer mMeasurer;
	private final TextAttribute mTextAttribute;

	public SelectionUnitTest() throws IOException {
		mMeasurer = new MockMeasurer(new MockTextPaint(1));
		mTextAttribute = new TextAttribute(mMeasurer);
	}

	@Test
	public void testParagraphSelection() throws ParagraphVisitor.VisitException {
		RenderOption renderOption = new RenderOption();

		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), mMeasurer, mTextAttribute, renderOption);
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
		texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 10, 1);

		ParagraphSelection paragraphSelection = ParagraphSelection.obtain(paragraph, ParagraphSelection.LONG_CLICK);

		Assert.assertTrue(paragraphSelection.isEmpty());
		Box box = TextBox.obtain(
				"fuck",
				0,
				1,
				mMeasurer,
				null,
				null,
				null,
				null
		);
		paragraphSelection.appendBox(box);
		Assert.assertFalse(paragraphSelection.isEmpty());
		Assert.assertSame(paragraphSelection.getLastBox(), box);
		Assert.assertSame(paragraphSelection.getFirstBox(), box);

		Box box2 = TextBox.obtain(
				"fuck",
				0,
				2,
				mMeasurer,
				null,
				null,
				null,
				null
		);
		paragraphSelection.appendBox(box2);
		Assert.assertFalse(paragraphSelection.isEmpty());
		Assert.assertSame(paragraphSelection.getLastBox(), box2);
		Assert.assertSame(paragraphSelection.getFirstBox(), box);

		paragraphSelection.clear();
		Assert.assertTrue(paragraphSelection.isEmpty());
		Assert.assertNull(paragraphSelection.getLastBox());
		Assert.assertNull(paragraphSelection.getFirstBox());

		Set<Box> set = new HashSet<>();
		for (int i = 0; i < 10; ++i) {
			Element element = paragraph.getElement(i);
			if (element instanceof Box) {
				Box box1 = (Box) element;
				set.add(box1);
				paragraphSelection.appendBox(box1);
			}
		}

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
		private final Set<Box> mBoxes;
		private int mCount;
		private ParagraphSelection mParagraphSelection;

		public TestBoxVisitor(Set<Box> boxes, ParagraphSelection paragraphSelection) {
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
		protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
			if (mParagraphSelection.isSelected(box)) {
				++mCount;
				Assert.assertTrue(mBoxes.contains(box));
			}
		}

		public void test(Paragraph subParagraph, RenderOption renderOption) throws VisitException {
			visit(subParagraph, renderOption);
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
		protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
			if (mParagraphSelection.isSelected(box)) {
				++mCount;
				Assert.assertTrue(mBoxes.contains(box.getTag()));
			}
		}

		public void test(Paragraph subParagraph, RenderOption renderOption) throws VisitException {
			visit(subParagraph, renderOption);
			Assert.assertEquals(mCount, mBoxes.size());
		}
	}
}
