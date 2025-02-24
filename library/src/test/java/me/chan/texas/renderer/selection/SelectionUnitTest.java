package me.chan.texas.renderer.selection;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.renderer.RenderOption;

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
}
