package me.chan.texas.text.layout;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.TexasOption;
import me.chan.texas.di.FakeMeasureFactory;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.misc.Rect;
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextGravity;
import me.chan.texas.typesetter.ParagraphTypesetter;

public class LayoutUnitTest {

	@Test
	public void test() throws ParagraphVisitor.VisitException {
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);

		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		PaintSet paintSet = new PaintSet(factory.getMockTextPaint());
		TextAttribute textAttribute = new TextAttribute(measurer);

		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption)
				.textGravity(TextGravity.START)
				.text("triangle");
		Paragraph paragraph = builder.build();
		paragraph.setRect(new Rect(1, 2, 3, 4));

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		paragraph.measure(measurer, textAttribute);
		texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 10);

		Layout layout = paragraph.getLayout();
		RectF bounds = layout.getLine(0).getBounds();
		Assert.assertEquals(1, bounds.left, 0.0001);
		Assert.assertEquals(2, bounds.top, 0.0001);
		Assert.assertEquals(9, bounds.right, 0.0001);
		Assert.assertEquals(3, bounds.bottom, 0.0001);


		builder = Paragraph.Builder.newBuilder(texasOption)
				.textGravity(TextGravity.END)
				.text("triangle");
		paragraph = builder.build();
		paragraph.setRect(new Rect(1, 2, 3, 4));

		texTypesetter = new ParagraphTypesetter();
		paragraph.measure(measurer, textAttribute);
		texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 10);

		bounds = new RectF();
		layout = paragraph.getLayout();
		bounds = layout.getLine(0).getBounds();
		Assert.assertEquals(3, bounds.left, 0.0001);
		Assert.assertEquals(2, bounds.top, 0.0001);
		Assert.assertEquals(11, bounds.right, 0.0001);
		Assert.assertEquals(3, bounds.bottom, 0.0001);

		builder = Paragraph.Builder.newBuilder(texasOption)
				.textGravity(TextGravity.CENTER_HORIZONTAL)
				.text("triangle");
		paragraph = builder.build();
		paragraph.setRect(new Rect(1, 2, 3, 4));

		texTypesetter = new ParagraphTypesetter();
		paragraph.measure(measurer, textAttribute);
		texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 10);

		layout = paragraph.getLayout();
		bounds = layout.getLine(0).getBounds();
		Assert.assertEquals(2, bounds.left, 0.0001);
		Assert.assertEquals(2, bounds.top, 0.0001);
		Assert.assertEquals(10, bounds.right, 0.0001);
		Assert.assertEquals(3, bounds.bottom, 0.0001);
	}
}
