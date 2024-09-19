package me.chan.texas.renderer;

import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.shanbay.lib.texas.TestUtils;
import com.shanbay.lib.texas.test.mock.MockTextPaint;

import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.renderer.highlight.ParagraphHighlight;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Line;
import me.chan.texas.typesetter.ParagraphTypesetter;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class HighlightUnitTest {
	private final MockMeasurer mMeasurer;
	private final TextAttribute mTextAttribute;

	public HighlightUnitTest() throws IOException {
		mMeasurer = new MockMeasurer(new MockTextPaint(1));
		mTextAttribute = new TextAttribute(mMeasurer);
	}

	@Test
	public void test() throws ParagraphVisitor.VisitException {
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

		ParagraphHighlight paragraphHighlight = ParagraphHighlight.obtain(
				1,
				paragraph
		);

		Set<Box> set = new HashSet<>();
		for (int i = 0; i < 10; ++i) {
			Element element = paragraph.getElement(i);
			if (element instanceof Box) {
				Box box1 = (Box) element;
				set.add(box1);
				paragraphHighlight.addBox(box1);
			}
		}

		TestBoxVisitor visitor = new TestBoxVisitor(set, paragraphHighlight);
		visitor.test(paragraph, renderOption);

		set.clear();
		paragraphHighlight.clear();
		visitor = new TestBoxVisitor(set, paragraphHighlight);
		visitor.test(paragraph, renderOption);

		paragraphHighlight.recycle();
		TestUtils.testRecycled(paragraphHighlight);
	}

	private static class TestBoxVisitor extends ParagraphVisitor {
		private final Set<Box> mBoxes;
		private int mCount;
		private final ParagraphHighlight mParagraphHighlight;

		public TestBoxVisitor(Set<Box> boxes, ParagraphHighlight paragraphHighlight) {
			mBoxes = boxes;
			mParagraphHighlight = paragraphHighlight;
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
			if (mParagraphHighlight.isHighlight(box)) {
				++mCount;
				Assert.assertTrue(mBoxes.contains(box));
			}
		}

		public void test(Paragraph subParagraph, RenderOption renderOption) throws VisitException {
			visit(subParagraph, renderOption);
			Assert.assertEquals(mCount, mBoxes.size());
		}
	}
}
