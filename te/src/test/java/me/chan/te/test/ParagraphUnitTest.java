package me.chan.te.test;

import android.graphics.drawable.ColorDrawable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

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
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(mMeasurer, Hypher.getInstance(), mTextAttribute);
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
		builder = Paragraph.Builder.newBuilder(mMeasurer, Hypher.getInstance(), mTextAttribute);
		Assert.assertSame(p, builder);
		Assert.assertNotSame(builder, Paragraph.Builder.newBuilder(mMeasurer, Hypher.getInstance(), mTextAttribute));

		ColorDrawable colorDrawable = new ColorDrawable(10);
		OnClickedListener onClickedListener = new OnClickedListener() {
			@Override
			public void onClicked(float x, float y) {
			}
		};
		builder.drawable(colorDrawable, 1, 2, onClickedListener);
		builder.text(msg);

		OnClickedListener spanOnClickedListener = new OnClickedListener() {
			@Override
			public void onClicked(float x, float y) {
			}
		};
		Background background = Background.obtain(10);
		UnderLine underLine = UnderLine.obtain(10);
		builder.newSpanBuilder(spanOnClickedListener)
				.next("triangle")
				.setBackground(background)
				.setForeground(underLine)
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


		String[] strings = new String[]{
				"tri",
				"an",
				"gle"
		};
		for (int i = 0; i < 3; ++i) {
			textBox = (TextBox) paragraph.getElement(4 + i * 2);
			Assert.assertSame(textBox.getBackground(), background);
			Assert.assertSame(textBox.getForeground(), underLine);
			Assert.assertSame(textBox.getTextStyle(), TextStyle.BOLD);
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
			Assert.assertEquals(penalty.getPenalty(), ParagraphTypesetter.HYPHEN_PENALTY, 0);
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
	public void testParagraph() throws NoSuchFieldException, IllegalAccessException {
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(mMeasurer, Hypher.getInstance(), mTextAttribute);
		builder.text("hello");
		Paragraph paragraph = builder.build();

		Assert.assertEquals(paragraph.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElementCount(), 3);


		Glue glue = Glue.obtain(10, 11, 12);
		paragraph.replace(0, glue);
		Assert.assertEquals(paragraph.getElementCount(), 3);
		Assert.assertSame(paragraph.getElement(0), glue);

		Paragraph.Line line1 = Paragraph.Line.obtain();
		Paragraph.Line line2 = Paragraph.Line.obtain();
		Paragraph.Line line3 = Paragraph.Line.obtain();
		paragraph.addLine(line1);
		paragraph.addLine(line2);
		paragraph.addLine(line3);

		Assert.assertEquals(paragraph.getLineCount(), 3);
		Assert.assertSame(paragraph.getLine(0), line1);
		Assert.assertSame(paragraph.getLine(1), line2);
		Assert.assertSame(paragraph.getLine(2), line3);

		Paragraph paragraph1 = paragraph.spilt(1);
		Assert.assertEquals(paragraph.getLineCount(), 1);
		Assert.assertSame(paragraph.getLine(0), line1);

		Assert.assertEquals(paragraph1.getLineCount(), 2);
		Assert.assertSame(paragraph1.getLine(0), line2);
		Assert.assertSame(paragraph1.getLine(1), line3);

		Assert.assertSame(paragraph, paragraph1.getPrev());
		Assert.assertSame(paragraph.getNext(), paragraph1);
		Assert.assertNull(paragraph.getPrev());
		Assert.assertNull(paragraph1.getNext());


		Paragraph paragraph2 = paragraph1.spilt(1);
		Assert.assertEquals(paragraph.getLineCount(), 1);
		Assert.assertSame(paragraph.getLine(0), line1);

		Assert.assertEquals(paragraph1.getLineCount(), 1);
		Assert.assertSame(paragraph1.getLine(0), line2);

		Assert.assertEquals(paragraph2.getLineCount(), 1);
		Assert.assertSame(paragraph2.getLine(0), line3);

		Assert.assertSame(paragraph, paragraph1.getPrev());
		Assert.assertSame(paragraph.getNext(), paragraph1);
		Assert.assertNull(paragraph.getPrev());
		Assert.assertSame(paragraph1.getNext(), paragraph2);
		Assert.assertSame(paragraph2.getPrev(), paragraph1);
		Assert.assertNull(paragraph2.getNext());

		Class<?> clazz = Paragraph.class;
		Field lines = clazz.getDeclaredField("mLines");
		lines.setAccessible(true);

		Field elements = clazz.getDeclaredField("mElements");
		elements.setAccessible(true);

		Assert.assertNotSame(lines.get(paragraph), lines.get(paragraph1));
		Assert.assertNotSame(elements.get(paragraph), elements.get(paragraph1));

		paragraph.recycle();
		Assert.assertEquals(paragraph.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElementCount(), 0);
		Assert.assertNull(paragraph.getNext());
		Assert.assertNull(paragraph.getPrev());

		// check recycle twice
		paragraph.recycle();

		paragraph1 = paragraph;
		builder = Paragraph.Builder.newBuilder(mMeasurer, Hypher.getInstance(), mTextAttribute);
		builder.text("triangle");
		paragraph = builder.build();
		Assert.assertSame(paragraph, paragraph1);
		Assert.assertEquals(paragraph.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElementCount(), 7);

		Assert.assertNotSame(paragraph, Paragraph.Builder.newBuilder(mMeasurer, Hypher.getInstance(), mTextAttribute).build());
	}
}
