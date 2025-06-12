package me.chan.texas.renderer.selection;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.TexasOption;
import me.chan.texas.di.FakeMeasureFactory;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;

public class SelectionUnitTest {

	@Test
	public void testStyles() {
		RenderOption renderOption = new RenderOption();
		renderOption.setSelectedTextColor(1);
		renderOption.setSelectedBackgroundColor(2);
		renderOption.setSelectedByLongClickTextColor(3);
		renderOption.setSelectedByLongClickBackgroundColor(4);
		renderOption.setSpanHighlightTextColor(5);

		Selection.Styles styles = Selection.Styles.create(1, 2);
		Assert.assertEquals(2, styles.getTextColor());
		Assert.assertEquals(1, styles.getBackgroundColor());
		Assert.assertEquals(0, styles.getVersion());
		Assert.assertEquals(Selection.Styles.Source.USER_DEFINED, styles.getSource());

		styles.setTextColor(2);
		Assert.assertEquals(0, styles.getVersion());
		styles.setTextColor(3);
		Assert.assertEquals(1, styles.getVersion());

		styles.setBackgroundColor(1);
		Assert.assertEquals(1, styles.getVersion());
		styles.setBackgroundColor(2);
		Assert.assertEquals(2, styles.getVersion());

		styles = Selection.Styles.createFromTouch(renderOption, true);
		Assert.assertEquals(Selection.Styles.Source.LONG_CLICKED, styles.getSource());
		Assert.assertEquals(3, styles.getTextColor());
		Assert.assertEquals(4, styles.getBackgroundColor());

		styles = Selection.Styles.createFromTouch(renderOption, false);
		Assert.assertEquals(Selection.Styles.Source.CLICKED, styles.getSource());
		Assert.assertEquals(1, styles.getTextColor());
		Assert.assertEquals(2, styles.getBackgroundColor());

		styles = Selection.Styles.createFromHighLight(renderOption);
		Assert.assertEquals(5, styles.getTextColor());
		Assert.assertEquals(0, styles.getBackgroundColor());
		Assert.assertEquals(Selection.Styles.Source.HIGHLIGHT, styles.getSource());
	}

	@Test
	public void checkValidate() {
		Selection selection = Selection.obtain(Selection.Type.SELECTION, Selection.Styles.create(1, 2));
		Assert.assertFalse(selection.isInvalidate());

		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		PaintSet paintSet = new PaintSet(factory.getMockTextPaint());
		TextAttribute textAttribute = new TextAttribute(measurer);
		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);
		Paragraph paragraph = Paragraph.Builder.newBuilder(texasOption)
				.text("hello")
				.build();

		ParagraphSelection paragraphSelection = ParagraphSelection.obtain(Selection.Type.SELECTION, selection.getStyles(), paragraph);
		paragraph.setSelection(Selection.Type.SELECTION, paragraphSelection);
		selection.add(paragraphSelection);
		Assert.assertFalse(selection.isInvalidate());

		paragraphSelection.clear();
		Assert.assertFalse(selection.isInvalidate());

		paragraphSelection.recycle();
		Assert.assertTrue(selection.isInvalidate());

		ParagraphSelection prev = paragraphSelection;
		paragraphSelection = ParagraphSelection.obtain(Selection.Type.SELECTION, selection.getStyles(), paragraph);
		paragraph.setSelection(Selection.Type.SELECTION, paragraphSelection);
		Assert.assertSame(prev, paragraphSelection);
		Assert.assertTrue(selection.isInvalidate());

		selection = Selection.obtain(Selection.Type.SELECTION, Selection.Styles.create(1, 2));
		paragraphSelection = ParagraphSelection.obtain(Selection.Type.SELECTION, selection.getStyles(), paragraph);
		paragraph.setSelection(Selection.Type.SELECTION, paragraphSelection);
		selection.add(paragraphSelection);
		Assert.assertFalse(selection.isInvalidate());

		paragraphSelection = ParagraphSelection.obtain(Selection.Type.SELECTION, selection.getStyles(), paragraph);
		paragraph.setSelection(Selection.Type.SELECTION, paragraphSelection);
		Assert.assertTrue(selection.isInvalidate());
	}
}
