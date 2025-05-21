package me.chan.texas.text.layout;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextGravity;

public class AdviseUnitTest {

	@Test
	public void test() {
		MockTextPaint textPaint = new MockTextPaint(20);
		MockMeasurer measurer = new MockMeasurer(textPaint);
		TextAttribute textAttribute = new TextAttribute(measurer);
		PaintSet paintSet = new PaintSet(textPaint);

		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpace(1f);
		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		Paragraph paragraph = builder.build();

		Layout.Advise advise = paragraph.getLayout().getAdvise();
		Assert.assertEquals(BreakStrategy.SIMPLE, advise.getBreakStrategy());
		Assert.assertEquals(1f, advise.getLineSpace(), 0.001f);
		Assert.assertEquals(TextGravity.START, advise.getTextGravity());

		renderOption.setLineSpace(2);
		renderOption.setBreakStrategy(BreakStrategy.BALANCED);
		renderOption.setTextGravity(TextGravity.CENTER_HORIZONTAL);
		advise.copy(renderOption);

		Assert.assertEquals(BreakStrategy.BALANCED, advise.getBreakStrategy());
		Assert.assertEquals(2f, advise.getLineSpace(), 0.001f);
		Assert.assertEquals(TextGravity.CENTER_HORIZONTAL, advise.getTextGravity());

		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.breakStrategy(BreakStrategy.SIMPLE);
		builder.lineSpace(3);
		builder.textGravity(TextGravity.END);
		paragraph = builder.build();

		advise = paragraph.getLayout().getAdvise();
		Assert.assertEquals(BreakStrategy.SIMPLE, advise.getBreakStrategy());
		Assert.assertEquals(3f, advise.getLineSpace(), 0.001f);
		Assert.assertEquals(TextGravity.END, advise.getTextGravity());

		// 用户不是用的默认值就不变更值
		renderOption.setBreakStrategy(BreakStrategy.UNKNOWN);
		renderOption.setLineSpace(4);
		renderOption.setTextGravity(TextGravity.START);
		advise.copy(renderOption);
		Assert.assertEquals(BreakStrategy.SIMPLE, advise.getBreakStrategy());
		Assert.assertEquals(3f, advise.getLineSpace(), 0.001f);
		Assert.assertEquals(TextGravity.END, advise.getTextGravity());

		advise.reset();
		advise.copy(renderOption);
		Assert.assertEquals(BreakStrategy.UNKNOWN, advise.getBreakStrategy());
		Assert.assertEquals(4f, advise.getLineSpace(), 0.001f);
		Assert.assertEquals(TextGravity.START, advise.getTextGravity());
	}
}
