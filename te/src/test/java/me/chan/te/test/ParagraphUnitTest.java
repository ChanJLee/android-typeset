package me.chan.te.test;

import android.graphics.drawable.ColorDrawable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;
import me.chan.te.test.mock.MockMeasurer;
import me.chan.te.test.mock.MockTextPaint;
import me.chan.te.text.Background;
import me.chan.te.text.DrawableBox;
import me.chan.te.text.Glue;
import me.chan.te.text.OnClickedListener;
import me.chan.te.text.Paragraph;
import me.chan.te.text.Penalty;
import me.chan.te.text.TextAttribute;
import me.chan.te.text.TextBox;
import me.chan.te.text.TextStyle;
import me.chan.te.text.UnderLine;
import me.chan.te.typesetter.ParagraphTypesetter;

public class ParagraphUnitTest {

	private Measurer mMeasurer;
	private TextAttribute mTextAttribute;

	@Before
	public void setup() {
		mMeasurer = new MockMeasurer(new MockTextPaint(20));
		mTextAttribute = new TextAttribute(mMeasurer);
	}

	@Test
	public void testBuilder() {
		String msg = "hello";
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(mMeasurer, Hypher.getInstance(), mTextAttribute, msg);
		Paragraph paragraph = builder.build();
		Assert.assertTrue(builder.isRecycled());
		Assert.assertFalse(paragraph.isRecycled());
		Assert.assertTrue(builder.isRecycled());

		try {
			builder.build();
			Assert.fail("check build twice failed");
		} catch (IllegalStateException e) {

		}

		// test recycle twice
		Paragraph.Builder p = builder;
		builder = Paragraph.Builder.newBuilder(mMeasurer, Hypher.getInstance(), mTextAttribute, msg);
		Assert.assertSame(p, builder);
		Assert.assertNotSame(builder, Paragraph.Builder.newBuilder(mMeasurer, Hypher.getInstance(), mTextAttribute, msg));

		ColorDrawable colorDrawable = new ColorDrawable(10);
		OnClickedListener onClickedListener = new OnClickedListener() {
			@Override
			public boolean onClicked(float x, float y) {
				return false;
			}
		};
		builder.drawable(colorDrawable, 1, 2, onClickedListener);
		builder.text(msg);

		OnClickedListener spanOnClickedListener = new OnClickedListener() {
			@Override
			public boolean onClicked(float x, float y) {
				return false;
			}
		};
		Background background = Background.obtain(10);
		UnderLine underLine = UnderLine.obtain(10);
		builder.newSpanBuilder(spanOnClickedListener)
				.next("triangle")
				.setBackground(background)
				.setForeground(underLine)
				.setExtra(msg)
				.setOnClickedListener(onClickedListener)
				.setTextStyle(TextStyle.BOLD)
				.buildSpan();

		paragraph = builder.build();
		Assert.assertEquals(paragraph.getElementCount(), 11);
		DrawableBox drawableBox = (DrawableBox) paragraph.getElement(0);
		Assert.assertSame(drawableBox.getDrawable(), colorDrawable);
		Glue glue = (Glue) paragraph.getElement(1);
		Assert.assertEquals(glue.getWidth(), mTextAttribute.getSpaceWidth(), 0);
		Assert.assertEquals(glue.getShrink(), mTextAttribute.getSpaceShrink(), 0);
		Assert.assertEquals(glue.getStretch(), mTextAttribute.getSpaceStretch(), 0);

		TextBox textBox = (TextBox) paragraph.getElement(2);
		Assert.assertNull(textBox.getBackground());
		Assert.assertNull(textBox.getForeground());
		Assert.assertNull(textBox.getTextStyle());
		Assert.assertEquals(textBox.getHeight(), mMeasurer.getDesiredHeight(msg, 0, msg.length(), null), 0);
		Assert.assertNull(textBox.getOnClickedListener());
		Assert.assertEquals(textBox.getWidth(), mMeasurer.getDesiredWidth(msg, 0, msg.length(), null), 0);

		glue = (Glue) paragraph.getElement(3);
		Assert.assertEquals(glue.getWidth(), mTextAttribute.getSpaceWidth(), 0);
		Assert.assertEquals(glue.getShrink(), mTextAttribute.getSpaceShrink(), 0);
		Assert.assertEquals(glue.getStretch(), mTextAttribute.getSpaceStretch(), 0);


		String[] strings = new String[] {
				"tri",
				"an",
				"gle"
		};
		for (int i = 0; i < 3; ++i) {
			textBox = (TextBox) paragraph.getElement(4 + i * 2);
			Assert.assertSame(textBox.getBackground(), background);
			Assert.assertSame(textBox.getForeground(), underLine);
			Assert.assertSame(textBox.getTextStyle(), TextStyle.BOLD);
			Assert.assertSame(textBox.getExtra(), msg);
			Assert.assertSame(textBox.getSpanOnClickedListener(), spanOnClickedListener);
			Assert.assertEquals(textBox.getHeight(), mMeasurer.getDesiredHeight(strings[i], 0, strings[i].length(), null), 0);
			Assert.assertSame(textBox.getOnClickedListener(), onClickedListener);
			Assert.assertEquals(textBox.getWidth(), mMeasurer.getDesiredWidth(strings[i], 0, strings[i].length(), null), 0);

			int next = 5 + i * 2;
			if (next == 9) {
				continue;
			}
			Penalty penalty = (Penalty) paragraph.getElement(5 + i * 2);
			Assert.assertEquals(penalty.getWidth(), mTextAttribute.getHyphenWidth(), 0);
			Assert.assertEquals(penalty.getHeight(), mMeasurer.getDesiredHeight("-", 0, 1, null), 0);
			Assert.assertEquals(penalty.getPenalty(),ParagraphTypesetter.HYPHEN_PENALTY, 0);
			Assert.assertTrue(penalty.isFlag());
		}

		glue = (Glue) paragraph.getElement(9);
		Assert.assertEquals(glue.getWidth(), 0, 0);
		Assert.assertEquals(glue.getStretch(), ParagraphTypesetter.INFINITY, 0);
		Assert.assertEquals(glue.getShrink(), 0, 0);

		Penalty penalty = (Penalty) paragraph.getElement(10);
		Assert.assertEquals(penalty.getWidth(), 0, 0);
		Assert.assertEquals(penalty.getHeight(), 0, 0);
		Assert.assertEquals(penalty.getPenalty(), -ParagraphTypesetter.INFINITY, 0);
		Assert.assertTrue(penalty.isFlag());
	}

	@Test
	public void testParagraph() {
		String msg = "hello";
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(mMeasurer, Hypher.getInstance(), mTextAttribute, msg);
		Paragraph paragraph = builder.build();

		Assert.assertSame(paragraph.getExtra(), msg);
	}
}
