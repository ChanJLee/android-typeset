package me.chan.te.test;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import me.chan.te.data.DrawableBox;
import me.chan.te.data.Glue;
import me.chan.te.data.Penalty;
import me.chan.te.data.TextBox;
import me.chan.te.hypher.Hypher;
import me.chan.te.test.mock.MockMeasurer;
import me.chan.te.test.mock.MockTextAttribute;
import me.chan.te.test.mock.MockTextPaint;
import me.chan.te.text.Background;
import me.chan.te.text.Document;
import me.chan.te.text.Figure;
import me.chan.te.text.Foreground;
import me.chan.te.text.Gravity;
import me.chan.te.text.Line;
import me.chan.te.text.Page;
import me.chan.te.text.Paragraph;
import me.chan.te.text.TextStyle;
import me.chan.te.text.UnderLine;
import me.chan.te.typesetter.BreakPoint;
import me.chan.te.typesetter.Candidate;
import me.chan.te.typesetter.Node;
import me.chan.te.typesetter.Sum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class DataUnitTest {
	private MockTextPaint mMockTextPaint;

	@Before
	public void setup() {
		mMockTextPaint = new MockTextPaint();
		MockMeasurer mockMeasurer = new MockMeasurer(mMockTextPaint);

		Assert.assertNotEquals(mMockTextPaint.getMockTextSize(), 0);
		Assert.assertNotEquals(mMockTextPaint.getMockTextHeight(), 0);

		String hello = "hello";
		Assert.assertEquals("check measure text", mockMeasurer.getDesiredWidth(hello,
				0, hello.length(), TextStyle.NONE), hello.length() * mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals("check measure text", mockMeasurer.getDesiredWidth(hello,
				1, 2, TextStyle.NONE), mMockTextPaint.getMockTextSize(), 0);

		try {
			TextBox.obtain(null, 0, 10, 1, 1, null, null, null, null).toString();
			fail("obtain null box");
		} catch (Throwable throwable) {
			assertFalse(throwable instanceof AssertionError);
		}

		try {
			TextBox.obtain(null, 1, 1, 1, 1, null, null, null, null).toString();
			fail("obtain null box");
		} catch (Throwable throwable) {
			assertFalse(throwable instanceof AssertionError);
		}
	}

	@Test
	public void testGlue() {
		Glue glue = Glue.obtain(1, 2, 3);
		Assert.assertNotNull(glue);

		Assert.assertFalse(glue.isRecycled());
		Assert.assertEquals("check width: ", glue.getWidth(), 1, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), 2, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), 3, 0);

		Glue previous = glue;
		glue.recycle();
		Assert.assertTrue(glue.isRecycled());
		Assert.assertEquals("check width: ", glue.getWidth(), -1, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), -1, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), -1, 0);

		// test recycle twice
		glue.recycle();

		glue = Glue.obtain(4, 5, 6);
		Assert.assertNotNull(glue);
		Assert.assertSame(previous, glue);
		Assert.assertFalse(glue.isRecycled());
		Assert.assertEquals("check width: ", glue.getWidth(), 4, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), 5, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), 6, 0);
	}

	@Test
	public void testPenalty() {
		Penalty penalty = Penalty.obtain(1, 2, 3, true);
		Assert.assertNotNull(penalty);

		Assert.assertFalse(penalty.isRecycled());
		Assert.assertEquals("check width: ", penalty.getWidth(), 1, 0);
		Assert.assertEquals("check height: ", penalty.getHeight(), 2, 0);
		Assert.assertEquals("check penalty: ", penalty.getPenalty(), 3, 0);
		Assert.assertTrue("check flag", penalty.isFlag());

		Penalty prev = penalty;
		penalty.recycle();
		Assert.assertTrue(penalty.isRecycled());
		Assert.assertEquals("check width: ", penalty.getWidth(), -1, 0);
		Assert.assertEquals("check height: ", penalty.getHeight(), -1, 0);
		Assert.assertEquals("check penalty: ", penalty.getPenalty(), -1, 0);
		Assert.assertFalse("check flag", penalty.isFlag());

		// test recycle twice
		penalty.recycle();

		penalty = Penalty.obtain(4, 5, 6, false);
		Assert.assertNotNull(penalty);
		Assert.assertSame(penalty, prev);
		Assert.assertFalse(penalty.isRecycled());
		Assert.assertEquals("check width: ", penalty.getWidth(), 4, 0);
		Assert.assertEquals("check height: ", penalty.getHeight(), 5, 0);
		Assert.assertEquals("check penalty: ", penalty.getPenalty(), 6, 0);
		Assert.assertFalse("check flag", penalty.isFlag());
	}

	@Test
	public void testBackground() {
		Background background = Background.obtain(10);
		assertNotNull(background);
		Assert.assertFalse(background.isRecycled());
		assertEquals(background.getColor(), 10);

		background.recycle();
		Assert.assertTrue(background.isRecycled());
		assertNotEquals(background.getColor(), 10);

		// test recycle twice
		background.recycle();

		Background p = background;
		background = Background.obtain(20);
		Assert.assertFalse(background.isRecycled());
		Assert.assertNotNull(background);
		Assert.assertSame(p, background);
		assertEquals(background.getColor(), 20);
	}

	@Test
	public void testForeground() {
		UnderLine underLine = UnderLine.obtain(10);
		assertNotNull(underLine);
		assertEquals(underLine.getColor(), 10);
		Assert.assertFalse(underLine.isRecycled());

		underLine.recycle();
		Assert.assertTrue(underLine.isRecycled());
		assertNotEquals(underLine.getColor(), 10);

		// test recycle twice
		underLine.recycle();

		UnderLine p = underLine;
		underLine = UnderLine.obtain(20);
		Assert.assertFalse(underLine.isRecycled());
		Assert.assertNotNull(underLine);
		Assert.assertSame(p, underLine);
		assertEquals(underLine.getColor(), 20);
	}

	@Test
	public void testFigure() {
		String extra = "ok";
		String url = "hello";
		Figure figure = Figure.obtain(url, 1, 2);
		figure.setExtra(extra);
		Assert.assertNotNull(figure);
		Assert.assertFalse(figure.isRecycled());
		Assert.assertSame(figure.getUrl(), url);
		Assert.assertSame(figure.getExtra(), extra);
		Assert.assertEquals(figure.getWidth(), 1, 0);
		Assert.assertEquals(figure.getHeight(), 2, 0);

		Figure p = figure;
		figure.recycle();
		Assert.assertTrue(figure.isRecycled());
		Assert.assertNotSame(figure.getUrl(), url);
		Assert.assertNotSame(figure.getExtra(), extra);
		Assert.assertNotEquals(figure.getWidth(), 1, 0);
		Assert.assertNotEquals(figure.getHeight(), 2, 0);

		figure.recycle();

		figure = Figure.obtain(url, 1, 2);
		Assert.assertSame(figure, p);
		Assert.assertNotNull(figure);
		Assert.assertFalse(figure.isRecycled());
		Assert.assertSame(figure.getUrl(), url);
		Assert.assertNotSame(figure.getExtra(), extra);
		Assert.assertEquals(figure.getWidth(), 1, 0);
		Assert.assertEquals(figure.getHeight(), 2, 0);
	}

	@Test
	public void testBoxBase() {
		String msg = "hello world";
		TextBox box = TextBox.obtain(msg, 0, msg.length(),
				mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isSelected());
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());
		Assert.assertFalse(box.isRecycled());

		// check content
		Assert.assertNull(box.getTextStyle());
		checkBoxContent(box, msg);

		TextStyle textStyle = TextStyle.NONE;
		Background background = Background.obtain(10);
		Foreground foreground = UnderLine.obtain(10);

		TextBox box2 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), textStyle, background, foreground, null);
		Assert.assertNotNull(box2);
		box2.setSelected(true);
		Assert.assertNotEquals(box, box2);
		Assert.assertTrue(box2.isSelected());
		Assert.assertSame(box2.getTextStyle(), textStyle);
		Assert.assertSame(background, box2.getBackground());
		Assert.assertSame(foreground, box2.getForeground());
		checkBoxContent(box2, msg);

		TextBox prev = box2;
		box2.recycle();
		Assert.assertTrue(box2.isRecycled());
		Assert.assertNull(box2.getTextStyle());
		Assert.assertFalse(box2.isSelected());
		Assert.assertEquals(box2.getHeight(), -1, 0);
		Assert.assertEquals(box2.getWidth(), -1, 0);
		Assert.assertNull(box2.getBackground());
		Assert.assertNull(box2.getForeground());

		// test recycle twice
		box2.recycle();

		msg = "hello";
		box2 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), textStyle);
		Assert.assertNotNull(box2);
		Assert.assertSame(prev, box2);
		Assert.assertNull(box2.getBackground());
		Assert.assertFalse(box2.isRecycled());
		Assert.assertNull(box2.getForeground());
		checkBoxContent(box2, msg);

		box = TextBox.obtain(msg, 1, msg.length(), mMockTextPaint.getMockTextSize() * (msg.length() - 1), mMockTextPaint.getMockTextHeight(), textStyle);
		Assert.assertNotNull(box);
		checkBoxContent(box, "ello");

		try {
			TextBox.obtain(msg, -1, msg.length(), 1, 1, textStyle).toString();
			fail("check illegal index failed");
		} catch (Throwable e) {
			assertFalse(e instanceof AssertionError);
		}

		try {
			TextBox.obtain(msg, 0, msg.length() + 1, 1, 1, textStyle).toString();
			fail("check illegal index failed");
		} catch (Throwable e) {
			assertFalse(e instanceof AssertionError);
		}
	}

	@Test
	public void testBoxEquals() {
		String msg = "hello world";
		TextBox box1 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertNotNull(box1);

		TextBox box2 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertNotNull(box2);

		Assert.assertNotSame(box1, box2);

		Assert.assertEquals(box1, box1);
		Assert.assertEquals(box2, box2);
		Assert.assertEquals(box1, box2);

		checkBoxContent(box1, msg);
		checkBoxContent(box2, msg);

		StringBuilder stringBuilder = new StringBuilder("hello ");
		stringBuilder.append("world");

		msg = stringBuilder.toString();
		box2 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertNotNull(box2);

		Assert.assertNotSame(box1, box2);

		Assert.assertEquals(box1, box1);
		Assert.assertEquals(box2, box2);
		Assert.assertEquals(box1, box2);

		checkBoxContent(box1, msg);
		checkBoxContent(box2, msg);
	}

	@Test
	public void testBoxAppend() {
		String msg = "hello world";
		TextBox box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());

		Penalty penalty = Penalty.obtain(mMockTextPaint.getMockTextSize(), 2, 3, true);
		Assert.assertNotNull(penalty);

		box.append(penalty);
		Assert.assertTrue(box.isPenalty());
		Assert.assertFalse(box.isSplit());

		checkBoxContent(box, msg + "-");

		msg = "xxxx";
		box.recycle();
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());

		box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());
		checkBoxContent(box, msg);

		penalty = Penalty.obtain(mMockTextPaint.getMockTextSize(), mMockTextPaint.getMockTextHeight() * 2, 3, true);
		Assert.assertNotNull(penalty);
		box.append(penalty);
		Assert.assertTrue(box.isPenalty());
		Assert.assertFalse(box.isSplit());
		checkBoxContent(box, msg + "-", mMockTextPaint.getMockTextHeight() * 2);

		msg = "abc";
		box.recycle();
		box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());

		String msg2 = "dcf";
		TextBox box2 = TextBox.obtain(msg2, 0, msg2.length(), mMockTextPaint.getMockTextSize() * msg2.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertNotNull(box2);
		Assert.assertFalse(box2.isPenalty());
		Assert.assertFalse(box2.isSplit());

		box.append(box2);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());

		checkBoxContent(box, msg + msg2);
		box.recycle();
		box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());
		checkBoxContent(box, msg);
	}

	@Test
	public void testBoxSpilt() {
		String msg = "hello world";
		TextBox box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), TextStyle.NONE, null, null, null);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());

		Assert.assertNull(box.spilt(-1));
		Assert.assertNull(box.spilt((msg.length() + 1) * mMockTextPaint.getMockTextSize()));

		TextBox suffix = box.spilt("hello".length() * mMockTextPaint.getMockTextSize());
		Assert.assertNotNull(suffix);

		checkBoxContent(box, "hello");
		checkBoxContent(suffix, " world");
		Assert.assertTrue(box.isSplit());
		Assert.assertFalse(suffix.isSplit());
		Assert.assertEquals(suffix.getHeight(), box.getHeight(), 0);
		Assert.assertEquals(suffix.getTextStyle(), box.getTextStyle());
		Assert.assertEquals(suffix.getExtra(), box.getExtra());

		TextBox previous = box;
		box.recycle();
		msg = "hello";
		box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertSame(previous, box);
		Assert.assertFalse(box.isSplit());
	}

	@Test
	public void testBoxCopy() {
		String msg = "hello world";
		TextBox box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), TextStyle.NONE, null, null, null);
		box.setSelected(true);

		TextBox copy = TextBox.obtain(msg + "x", 0, msg.length() + 1, mMockTextPaint.getMockTextSize() * (msg.length() + 1), mMockTextPaint.getMockTextHeight(), TextStyle.NONE, null, null, null);
		checkBoxContent(copy, msg + "x");
		Assert.assertNotSame(copy, box);
		copy.copy(box);

		Assert.assertTrue(copy.isSelected());
		checkBoxContent(copy, msg);
	}

	@Test
	public void testLine() throws NoSuchFieldException, IllegalAccessException {
		Line line = Line.obtain();
		Field field = Line.class.getDeclaredField("mBoxes");
		field.setAccessible(true);
		List<TextBox> boxes = (List<TextBox>) field.get(line);
		Assert.assertNotNull(line);
		Assert.assertFalse(line.isRecycled());
		Assert.assertNotNull(boxes);
		Assert.assertTrue(boxes.isEmpty());
		line.setSpaceWidth(1);
		Assert.assertEquals(line.getSpaceWidth(), 1, 0);
		line.setLineHeight(2);
		Assert.assertEquals(line.getLineHeight(), 2, 0);
		line.setLineWidth(3);
		Assert.assertEquals(line.getLineWidth(), 3, 0);
		line.setRatio(4);
		Assert.assertEquals(line.getRatio(), 4, 0);
		Assert.assertSame(line.getGravity(), Gravity.LEFT);
		line.setGravity(Gravity.CENTER);
		Assert.assertSame(line.getGravity(), Gravity.CENTER);
		boxes.add(TextBox.obtain("hello", 0, 1, 1, 1, null));
		Assert.assertFalse(boxes.isEmpty());

		Line prev = line;
		line.recycle();
		Assert.assertTrue(line.isRecycled());
		boxes = (List<TextBox>) field.get(line);
		Assert.assertTrue(boxes.isEmpty());
		Assert.assertSame(line.getGravity(), Gravity.LEFT);
		Assert.assertNotEquals(line.getSpaceWidth(), 1, 0);
		Assert.assertNotEquals(line.getLineHeight(), 2, 0);
		Assert.assertNotEquals(line.getLineWidth(), 3, 0);
		Assert.assertNotEquals(line.getRatio(), 4, 0);

		// test recycle twice
		line.recycle();

		line = Line.obtain();
		boxes = (List<TextBox>) field.get(line);
		Assert.assertNotNull(line);
		Assert.assertFalse(line.isRecycled());
		Assert.assertSame(prev, line);
		Assert.assertTrue(boxes.isEmpty());
		Assert.assertSame(line.getGravity(), Gravity.LEFT);
		Assert.assertNotEquals(line.getSpaceWidth(), 1, 0);
		Assert.assertNotEquals(line.getLineHeight(), 2, 0);
		Assert.assertNotEquals(line.getLineWidth(), 3, 0);
		Assert.assertNotEquals(line.getRatio(), 4, 0);
	}

	@Test
	public void testParagraphBuilder() {
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(new MockMeasurer(mMockTextPaint), Hypher.getInstance(), new MockTextAttribute(mMockTextPaint), null);
		builder.drawable(new ColorDrawable(10), 1, 2);
		Assert.assertFalse(builder.isRecycled());
		builder.build();
		Assert.assertTrue(builder.isRecycled());

		try {
			builder.build();
			Assert.fail("test build failed");
		} catch (IllegalStateException e) {

		}

		Paragraph.Builder p = builder;

		try {
			builder.text("dd", 0, 1, null, null, null, null);
			fail();
		} catch (Throwable throwable) {
			assertFalse(throwable instanceof AssertionError);
		}

		builder = Paragraph.Builder.newBuilder(new MockMeasurer(mMockTextPaint), Hypher.getInstance(), new MockTextAttribute(mMockTextPaint), null);
		Assert.assertSame(builder, p);
		Assert.assertFalse(builder.isRecycled());
	}

	@Test
	public void testParagraph() throws NoSuchFieldException, IllegalAccessException {
		String hello = "hello";
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(new MockMeasurer(mMockTextPaint), Hypher.getInstance(), new MockTextAttribute(mMockTextPaint), hello);
		Paragraph paragraph = builder.build();
		Assert.assertNotNull(paragraph);
		Assert.assertTrue(paragraph.isEmpty());
		Assert.assertFalse(paragraph.isRecycled());
		Assert.assertEquals(paragraph.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElementCount(), 2);
		Assert.assertSame(paragraph.getExtra(), hello);
		Field field = Paragraph.class.getDeclaredField("mLines");
		field.setAccessible(true);
		List<Line> lines = (List<Line>) field.get(paragraph);
		Assert.assertNotNull(lines);
		Assert.assertTrue(lines.isEmpty());
		lines.add(Line.obtain());
		Assert.assertEquals(paragraph.getLineCount(), 1);
		Assert.assertFalse(lines.isEmpty());
		Assert.assertSame(paragraph.getElement(0).getClass(), Glue.class);
		Assert.assertSame(paragraph.getElement(1).getClass(), Penalty.class);

		paragraph.recycle();
		Assert.assertTrue(paragraph.isRecycled());
		Paragraph prev = paragraph;
		Assert.assertEquals(paragraph.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElementCount(), 0);
		Assert.assertNull(paragraph.getExtra());
		lines = (List<Line>) field.get(paragraph);
		Assert.assertNotNull(lines);
		Assert.assertTrue(lines.isEmpty());

		// test recycle twice
		paragraph.recycle();

		builder = Paragraph.Builder.newBuilder(new MockMeasurer(mMockTextPaint), Hypher.getInstance(), new MockTextAttribute(mMockTextPaint), hello);
		paragraph = builder.build();
		Assert.assertSame(paragraph, prev);
		Assert.assertNotNull(paragraph);
		Assert.assertFalse(paragraph.isRecycled());
		Assert.assertEquals(paragraph.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElementCount(), 2);
		Assert.assertSame(paragraph.getExtra(), hello);
		lines = (List<Line>) field.get(paragraph);
		Assert.assertNotNull(lines);
		Assert.assertTrue(lines.isEmpty());
		Assert.assertSame(paragraph.getElement(0).getClass(), Glue.class);
		Assert.assertSame(paragraph.getElement(1).getClass(), Penalty.class);

		builder = Paragraph.Builder.newBuilder(new MockMeasurer(mMockTextPaint), Hypher.getInstance(), new MockTextAttribute(mMockTextPaint), null);
		builder.text("hello", 0, 1, null, null, null, null);
		paragraph = builder.build();
		Assert.assertFalse(paragraph.isEmpty());
		Assert.assertNull(paragraph.getExtra());
		Assert.assertEquals(paragraph.getElementCount(), 3);
		Assert.assertSame(paragraph.getElement(0).getClass(), TextBox.class);
		Assert.assertSame(paragraph.getElement(1).getClass(), Glue.class);
		Assert.assertSame(paragraph.getElement(2).getClass(), Penalty.class);

		builder = Paragraph.Builder.newBuilder(new MockMeasurer(mMockTextPaint), Hypher.getInstance(), new MockTextAttribute(mMockTextPaint), null);
		builder.drawable(new ColorDrawable(10), 10, 10);
		paragraph = builder.build();
		Assert.assertFalse(paragraph.isEmpty());
		Assert.assertNull(paragraph.getExtra());
		Assert.assertNotEquals(paragraph.getElementCount(), 0);
		Assert.assertSame(paragraph.getElement(0).getClass(), DrawableBox.class);
		Assert.assertSame(paragraph.getElement(1).getClass(), Glue.class);
		Assert.assertSame(paragraph.getElement(2).getClass(), Penalty.class);
	}

	@Test
	public void testDrawableBox() {
		Drawable drawable = new ColorDrawable(19);
		DrawableBox drawableBox = DrawableBox.obtain(drawable, 1, 2);
		Assert.assertNotNull(drawableBox);
		Assert.assertFalse(drawableBox.isRecycled());
		Assert.assertSame(drawable, drawableBox.getDrawable());
		Assert.assertEquals(drawableBox.getWidth(), 1, 0);
		Assert.assertEquals(drawableBox.getHeight(), 2, 0);

		DrawableBox p = drawableBox;
		drawableBox.recycle();
		Assert.assertTrue(drawableBox.isRecycled());
		Assert.assertNotSame(drawable, drawableBox.getDrawable());
		Assert.assertNotEquals(drawableBox.getWidth(), 1, 0);
		Assert.assertNotEquals(drawableBox.getHeight(), 2, 0);

		// test recycle twice
		drawableBox.recycle();

		drawableBox = DrawableBox.obtain(new ColorDrawable(19), 1, 2);
		Assert.assertNotSame(drawable, drawableBox.getDrawable());
		Assert.assertFalse(drawableBox.isRecycled());
		Assert.assertEquals(drawableBox.getWidth(), 1, 0);
		Assert.assertEquals(drawableBox.getHeight(), 2, 0);
		Assert.assertSame(p, drawableBox);
	}

	private void checkBoxContent(TextBox box, String msg) {
		checkBoxContent(box, msg, mMockTextPaint.getMockTextHeight());
	}

	private void checkBoxContent(TextBox box, String msg, float height) {
		Assert.assertEquals(msg, box.toString());
		Assert.assertNotEquals(0, mMockTextPaint.getMockTextHeight(), 0);
		Assert.assertNotEquals(0, mMockTextPaint.getMockTextSize() * msg.length(), 0);
		Assert.assertEquals(box.getHeight(), height, 0);
		Assert.assertEquals(box.getWidth(), mMockTextPaint.getMockTextSize() * msg.length(), 0.1);
	}

	@Test
	public void testSum() {
		Sum sum = Sum.obtain();
		Assert.assertNotNull(sum);

		Glue glue = Glue.obtain(1, 2, 3);
		Assert.assertNotNull(glue);
		Assert.assertFalse(glue.isRecycled());
		sum.increaseGlue(glue);
		Assert.assertEquals(sum.getWidth(), glue.getWidth(), 0);
		Assert.assertEquals(sum.getShrink(), glue.getShrink(), 0);
		Assert.assertEquals(sum.getStretch(), glue.getStretch(), 0);

		sum.increaseWidth(10);
		Assert.assertEquals(sum.getWidth(), glue.getWidth() + 10, 0);

		Sum o = Sum.obtain(sum);
		Assert.assertNotSame(o, sum);

		Assert.assertEquals(sum.getWidth(), o.getWidth(), 0);
		Assert.assertEquals(sum.getShrink(), o.getShrink(), 0);
		Assert.assertEquals(sum.getStretch(), o.getStretch(), 0);

		o.recycle();
		Assert.assertTrue(o.isRecycled());
		Assert.assertNotEquals(sum.getWidth(), o.getWidth(), 0);
		Assert.assertNotEquals(sum.getShrink(), o.getShrink(), 0);
		Assert.assertNotEquals(sum.getStretch(), o.getStretch(), 0);

		// test recycle twice
		o.recycle();

		Sum p = o;
		o = Sum.obtain();
		Assert.assertSame(o, p);
		Assert.assertNotNull(o);
		Assert.assertFalse(o.isRecycled());
		Assert.assertNotEquals(sum.getWidth(), o.getWidth(), 0);
		Assert.assertNotEquals(sum.getShrink(), o.getShrink(), 0);
		Assert.assertNotEquals(sum.getStretch(), o.getStretch(), 0);
	}

	@Test
	public void testNode() {
		Node node = Node.obtain(null, null);
		Assert.assertNotNull(node);
		Assert.assertFalse(node.isRecycled());
		Assert.assertNull(node.next);
		Assert.assertNull(node.prev);
		Assert.assertNotNull(node.getData());

		Node.Data data = node.getData();
		Assert.assertNull(data.prev);
		Assert.assertNull(data.totals);
		Assert.assertEquals(data.position, 0);
		Assert.assertEquals(data.demerits, 0, 0);
		Assert.assertEquals(data.ratio, 0, 0);
		Assert.assertEquals(data.line, -1);
		Assert.assertEquals(data.fitnessClazz, 0);

		data.position = 1;
		data.demerits = 2;
		data.ratio = 3;
		data.line = 4;
		data.fitnessClazz = 5;
		data.totals = Sum.obtain();
		data.prev = Node.obtain(null, null);

		node.prev = Node.obtain(null, null);
		node.next = Node.obtain(null, null);

		node.recycle();
		Assert.assertTrue(node.isRecycled());
		Assert.assertNotNull(node);
		Assert.assertNull(node.next);
		Assert.assertNull(node.prev);
		Assert.assertNotNull(node.getData());
		Assert.assertNull(data.prev);
		Assert.assertNull(data.totals);
		Assert.assertEquals(data.position, 0);
		Assert.assertEquals(data.demerits, 0, 0);
		Assert.assertEquals(data.ratio, 0, 0);
		Assert.assertEquals(data.line, -1);
		Assert.assertEquals(data.fitnessClazz, 0);

		// test recycle twice
		node.recycle();

		Node previous = node;
		node = Node.obtain(null, null);
		Assert.assertSame(previous, node);
		Assert.assertNotNull(node);
		Assert.assertFalse(node.isRecycled());
		Assert.assertNull(node.next);
		Assert.assertNull(node.prev);
		Assert.assertNotNull(node.getData());
		Assert.assertNull(data.prev);
		Assert.assertNull(data.totals);
		Assert.assertEquals(data.position, 0);
		Assert.assertEquals(data.demerits, 0, 0);
		Assert.assertEquals(data.ratio, 0, 0);
		Assert.assertEquals(data.line, -1);
		Assert.assertEquals(data.fitnessClazz, 0);
	}

	@Test
	public void testBreakPoint() {
		BreakPoint breakPoint = BreakPoint.obtain(1, 2);
		Assert.assertNotNull(breakPoint);
		Assert.assertFalse(breakPoint.isRecycled());
		Assert.assertEquals(breakPoint.position, 1);
		Assert.assertEquals(breakPoint.ratio, 2, 0);

		breakPoint.recycle();
		Assert.assertTrue(breakPoint.isRecycled());
		Assert.assertEquals(breakPoint.position, -1);
		Assert.assertEquals(breakPoint.ratio, -1, 0);

		// test recycle twice
		breakPoint.recycle();

		BreakPoint t = breakPoint;
		breakPoint = BreakPoint.obtain(3, 4);
		Assert.assertSame(t, breakPoint);
		Assert.assertNotNull(breakPoint);
		Assert.assertFalse(breakPoint.isRecycled());
		Assert.assertEquals(breakPoint.position, 3);
		Assert.assertEquals(breakPoint.ratio, 4, 0);
	}

	@Test
	public void testCandidate() {
		Node node = Node.obtain(null, null);
		Assert.assertNotNull(node);
		Candidate candidate = Candidate.obtain(1, 2, node);
		Assert.assertNotNull(candidate);
		Assert.assertFalse(candidate.isRecycled());
		Assert.assertEquals(candidate.demerits, 1, 0);
		Assert.assertEquals(candidate.ratio, 2, 0);
		Assert.assertSame(node, candidate.active);

		candidate.recycle();
		Assert.assertTrue(candidate.isRecycled());
		Assert.assertEquals(candidate.demerits, Float.MAX_VALUE, 0);
		Assert.assertEquals(candidate.ratio, -1, 0);
		Assert.assertNull(candidate.active);

		// test recycle twice
		candidate.recycle();

		Candidate p = candidate;
		candidate = Candidate.obtain(3, 4, node);
		Assert.assertNotNull(candidate);
		Assert.assertFalse(candidate.isRecycled());
		Assert.assertEquals(candidate.demerits, 3, 0);
		Assert.assertEquals(candidate.ratio, 4, 0);
		Assert.assertSame(node, candidate.active);
		Assert.assertSame(p, candidate);
	}

	@Test
	public void testPage() {
		Page page = Page.obtain();
		Assert.assertNotNull(page);
		Assert.assertFalse(page.isRecycled());
		Assert.assertEquals(page.getWidth(), 0, 0);
		Assert.assertEquals(page.getHeight(), 0, 0);
		try {
			page.getSegment(0);
			fail("test get segment");
		} catch (IndexOutOfBoundsException e) {

		}
		Assert.assertEquals(page.getSegmentCount(), 0);

		page.setWidth(1);
		page.setHeight(2);
		Figure figure = Figure.obtain("", 1, 2);
		page.addSegment(figure);
		Assert.assertEquals(page.getWidth(), 1, 0);
		Assert.assertEquals(page.getHeight(), 2, 0);
		Assert.assertEquals(page.getSegmentCount(), 1);
		Assert.assertSame(page.getSegment(0), figure);
		try {
			page.getSegment(1);
			fail("test get segment");
		} catch (IndexOutOfBoundsException e) {

		}

		page.recycle();
		Assert.assertTrue(page.isRecycled());
		Assert.assertEquals(page.getWidth(), 0, 0);
		Assert.assertEquals(page.getHeight(), 0, 0);
		Assert.assertEquals(page.getSegmentCount(), 0);

		page.recycle();

		Page previous = page;
		page = Page.obtain();
		Assert.assertNotNull(page);
		assertSame(page, previous);
		Assert.assertFalse(page.isRecycled());
		Assert.assertEquals(page.getWidth(), 0, 0);
		Assert.assertEquals(page.getHeight(), 0, 0);
		try {
			page.getSegment(0);
			fail("test get segment");
		} catch (IndexOutOfBoundsException e) {

		}
		Assert.assertEquals(page.getSegmentCount(), 0);
	}

	@Test
	public void testDocument() {
		String msg = "hello";
		Document document = Document.obtain(msg);
		Assert.assertNotNull(document);
		Assert.assertFalse(document.isRecycled());
		Assert.assertSame(document.getExtra(), msg);
		Assert.assertEquals(document.getSegmentCount(), 0);
		Assert.assertEquals(document.getPageCount(), 0);
		try {
			document.getSegment(0);
			fail("test document get segment");
		} catch (IndexOutOfBoundsException e) {
		}
		try {
			document.getPage(0);
			fail("test document get page");
		} catch (IndexOutOfBoundsException e) {
		}

		Figure figure = Figure.obtain("", 1, 2);
		Page page = Page.obtain();
		document.addPage(page);
		document.addSegment(figure);
		Assert.assertEquals(document.getSegmentCount(), 1);
		Assert.assertEquals(document.getPageCount(), 1);
		try {
			document.getSegment(1);
			fail("test document get segment");
		} catch (IndexOutOfBoundsException e) {
		}
		try {
			document.getPage(1);
			fail("test document get page");
		} catch (IndexOutOfBoundsException e) {
		}
		Assert.assertSame(document.getPage(0), page);
		Assert.assertSame(document.getSegment(0), figure);

		Document previous = document;
		document.recycle();
		Assert.assertTrue(document.isRecycled());

		// test recycle twice
		document.recycle();

		document = Document.obtain();
		Assert.assertNotNull(document);
		Assert.assertFalse(document.isRecycled());
		Assert.assertNull(document.getExtra());
		Assert.assertSame(previous, document);
		Assert.assertEquals(document.getSegmentCount(), 0);
		Assert.assertEquals(document.getPageCount(), 0);
		try {
			document.getSegment(0);
			fail("test document get segment");
		} catch (IndexOutOfBoundsException e) {
		}
		try {
			document.getPage(0);
			fail("test document get page");
		} catch (IndexOutOfBoundsException e) {
		}
	}
}
