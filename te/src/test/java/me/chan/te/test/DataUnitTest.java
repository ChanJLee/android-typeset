package me.chan.te.test;

import android.text.TextPaint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import me.chan.te.data.Box;
import me.chan.te.text.TextStyle;
import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.text.Line;
import me.chan.te.text.Paragraph;
import me.chan.te.data.Penalty;
import me.chan.te.data.TextBox;
import me.chan.te.hypher.Hypher;
import me.chan.te.test.mock.MockMeasurer;
import me.chan.te.test.mock.MockOption;
import me.chan.te.test.mock.MockTextPaint;
import me.chan.te.text.Gravity;
import me.chan.te.typesetter.BreakPoint;
import me.chan.te.typesetter.Candidate;
import me.chan.te.typesetter.Node;
import me.chan.te.typesetter.Sum;

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
		Assert.assertNotEquals(mockMeasurer.getLineSpacing(), 0);
		Assert.assertEquals("check measure text", mockMeasurer.getDesiredWidth(hello,
				0, hello.length()), hello.length() * mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals("check measure text", mockMeasurer.getDesiredWidth(hello,
				1, 2), mMockTextPaint.getMockTextSize(), 0);

		try {
			TextBox.obtain(null, 0, 10, 1, 1, null, null).toString();
			fail("obtain null box");
		} catch (Throwable throwable) {
		}

		try {
			TextBox.obtain(null, 1, 1, 1, 1, null, null).toString();
			fail("obtain null box");
		} catch (Throwable throwable) {
		}
	}

	@Test
	public void testGlue() {
		Glue glue = Glue.obtain(1, 2, 3);
		Assert.assertNotNull(glue);

		Assert.assertEquals("check width: ", glue.getWidth(), 1, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), 2, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), 3, 0);

		Glue previous = glue;
		glue.recycle();
		Assert.assertEquals("check width: ", glue.getWidth(), -1, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), -1, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), -1, 0);

		glue = Glue.obtain(4, 5, 6);
		Assert.assertNotNull(glue);
		Assert.assertSame(previous, glue);
		Assert.assertEquals("check width: ", glue.getWidth(), 4, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), 5, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), 6, 0);
	}

	@Test
	public void testPenalty() {
		Penalty penalty = Penalty.obtain(1, 2, 3, true);
		Assert.assertNotNull(penalty);

		Assert.assertEquals("check width: ", penalty.getWidth(), 1, 0);
		Assert.assertEquals("check height: ", penalty.getHeight(), 2, 0);
		Assert.assertEquals("check penalty: ", penalty.getPenalty(), 3, 0);
		Assert.assertTrue("check flag", penalty.isFlag());

		Penalty prev = penalty;
		penalty.recycle();
		Assert.assertEquals("check width: ", penalty.getWidth(), -1, 0);
		Assert.assertEquals("check height: ", penalty.getHeight(), -1, 0);
		Assert.assertEquals("check penalty: ", penalty.getPenalty(), -1, 0);
		Assert.assertFalse("check flag", penalty.isFlag());

		penalty = Penalty.obtain(4, 5, 6, false);
		Assert.assertNotNull(penalty);

		Assert.assertSame(penalty, prev);

		Assert.assertEquals("check width: ", penalty.getWidth(), 4, 0);
		Assert.assertEquals("check height: ", penalty.getHeight(), 5, 0);
		Assert.assertEquals("check penalty: ", penalty.getPenalty(), 6, 0);
		Assert.assertFalse("check flag", penalty.isFlag());
	}

	@Test
	public void testBoxBase() {
		String msg = "hello world";
		TextBox box = TextBox.obtain(msg, 0, msg.length(),
				mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());

		// check content
		Assert.assertNull(box.getTextStyle());
		checkBoxContent(box, msg);

		TextStyle textStyle = new TextStyle() {
			@Override
			public void update(TextPaint textPaint) {

			}

			@Override
			public boolean isConflict(TextStyle other) {
				return false;
			}
		};

		TextBox box2 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), textStyle, null);
		Assert.assertNotNull(box2);
		Assert.assertNotEquals(box, box2);
		Assert.assertEquals(box2.getTextStyle(), textStyle);
		checkBoxContent(box2, msg);

		TextBox prev = box2;
		box2.recycle();
		Assert.assertNull(box2.getTextStyle());
		Assert.assertEquals(box2.getHeight(), -1, 0);
		Assert.assertEquals(box2.getWidth(), -1, 0);

		msg = "hello";
		box2 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), textStyle);
		Assert.assertNotNull(box2);
		Assert.assertSame(prev, box2);
		checkBoxContent(box2, msg);

		box = TextBox.obtain(msg, 1, msg.length(), mMockTextPaint.getMockTextSize() * (msg.length() - 1), mMockTextPaint.getMockTextHeight(), textStyle);
		Assert.assertNotNull(box);
		checkBoxContent(box, "ello");

		try {
			TextBox.obtain(msg, -1, msg.length(), 1, 1, textStyle).toString();
			fail("check illegal index failed");
		} catch (Exception e) {

		}

		try {
			TextBox.obtain(msg, 0, msg.length() + 1, 1, 1, textStyle).toString();
			fail("check illegal index failed");
		} catch (Exception e) {

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
		Box box2 = TextBox.obtain(msg2, 0, msg2.length(), mMockTextPaint.getMockTextSize() * msg2.length(), mMockTextPaint.getMockTextHeight(), null);
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
		TextBox box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), new TextStyle() {
			@Override
			public void update(TextPaint textPaint) {

			}

			@Override
			public boolean isConflict(TextStyle other) {
				return false;
			}
		}, "hah");
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

		Box previous = box;
		box.recycle();
		msg = "hello";
		box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null);
		Assert.assertSame(previous, box);
		Assert.assertFalse(box.isSplit());
	}


	@Test
	public void testLine() throws NoSuchFieldException, IllegalAccessException {
		Line line = Line.obtain();
		Field field = Line.class.getDeclaredField("mBoxes");
		field.setAccessible(true);
		List<Box> boxes = (List<Box>) field.get(line);
		Assert.assertNotNull(line);
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
		boxes = (List<Box>) field.get(line);
		Assert.assertTrue(boxes.isEmpty());
		Assert.assertSame(line.getGravity(), Gravity.LEFT);
		Assert.assertNotEquals(line.getSpaceWidth(), 1, 0);
		Assert.assertNotEquals(line.getLineHeight(), 2, 0);
		Assert.assertNotEquals(line.getLineWidth(), 3, 0);
		Assert.assertNotEquals(line.getRatio(), 4, 0);

		line = Line.obtain();
		boxes = (List<Box>) field.get(line);
		;
		Assert.assertNotNull(line);
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
		Paragraph.Builder builder = new Paragraph.Builder(new MockMeasurer(mMockTextPaint), Hypher.getInstance(), new MockOption(mMockTextPaint));
		try {
			builder.build();
			fail("test build failed");
		} catch (Throwable throwable) {
		}

		try {
			builder.drawable(null);
			fail("test drawable failed");
		} catch (Throwable throwable) {
		}

		try {
			builder.text("hello");
			fail("test text failed");
		} catch (Throwable throwable) {
		}

		try {
			builder.text("hello", null);
			fail("test text failed");
		} catch (Throwable throwable) {
		}

		try {
			builder.text("hello", 0, 1);
			fail("test text failed");
		} catch (Throwable throwable) {
		}

		try {
			builder.text("hello", 0, 1, null);
			fail("test text failed");
		} catch (Throwable throwable) {
		}

		try {
			builder.newParagraph();
			builder.newParagraph();
			fail("test newParagraph failed");
		} catch (Throwable throwable) {
		}

		try {
			builder.newParagraph(null);
			builder.newParagraph(null);
			fail("test newParagraph failed");
		} catch (Throwable throwable) {
		}

		try {
			builder.newParagraph();
			builder.newParagraph(null);
			fail("test newParagraph failed");
		} catch (Throwable throwable) {
		}

		try {
			builder.newParagraph(null);
			builder.newParagraph();
			fail("test newParagraph failed");
		} catch (Throwable throwable) {
		}
	}

	@Test
	public void testParagraph() throws NoSuchFieldException, IllegalAccessException {
		Paragraph.Builder builder = new Paragraph.Builder(new MockMeasurer(mMockTextPaint), Hypher.getInstance(), new MockOption(mMockTextPaint));
		String hello = "hello";

		builder.newParagraph(hello);
		Paragraph paragraph = builder.build();
		Assert.assertNotNull(paragraph);
		Assert.assertEquals(paragraph.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElements().size(), 2);
		Assert.assertSame(paragraph.getExtra(), hello);
		Field field = Paragraph.class.getDeclaredField("mLines");
		field.setAccessible(true);
		List<Line> lines = (List<Line>) field.get(paragraph);
		Assert.assertNotNull(lines);
		Assert.assertTrue(lines.isEmpty());
		lines.add(Line.obtain());
		Assert.assertEquals(paragraph.getLineCount(), 1);
		Assert.assertFalse(lines.isEmpty());
		List<? extends Element> elements = paragraph.getElements();
		Assert.assertSame(elements.get(0).getClass(), Glue.class);
		Assert.assertSame(elements.get(1).getClass(), Penalty.class);

		paragraph.recycle();
		Paragraph prev = paragraph;
		Assert.assertEquals(paragraph.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElements().size(), 0);
		Assert.assertNull(paragraph.getExtra());
		lines = (List<Line>) field.get(paragraph);
		Assert.assertNotNull(lines);
		Assert.assertTrue(lines.isEmpty());

		builder.newParagraph(hello);
		paragraph = builder.build();
		Assert.assertSame(paragraph, prev);
		Assert.assertNotNull(paragraph);
		Assert.assertEquals(paragraph.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElements().size(), 2);
		Assert.assertSame(paragraph.getExtra(), hello);
		lines = (List<Line>) field.get(paragraph);
		Assert.assertNotNull(lines);
		Assert.assertTrue(lines.isEmpty());
		elements = paragraph.getElements();
		Assert.assertSame(elements.get(0).getClass(), Glue.class);
		Assert.assertSame(elements.get(1).getClass(), Penalty.class);

		// TODO test image
		builder.newParagraph();
		builder.text("hello");
		paragraph = builder.build();
		Assert.assertNull(paragraph.getExtra());
		Assert.assertEquals(paragraph.getElements().size(), 3);
		elements = paragraph.getElements();
		Assert.assertSame(elements.get(0).getClass(), TextBox.class);
		Assert.assertSame(elements.get(1).getClass(), Glue.class);
		Assert.assertSame(elements.get(2).getClass(), Penalty.class);
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
		Assert.assertNotEquals(sum.getWidth(), o.getWidth(), 0);
		Assert.assertNotEquals(sum.getShrink(), o.getShrink(), 0);
		Assert.assertNotEquals(sum.getStretch(), o.getStretch(), 0);
		Sum p = o;
		o = Sum.obtain();
		Assert.assertSame(o, p);

		Assert.assertNotEquals(sum.getWidth(), o.getWidth(), 0);
		Assert.assertNotEquals(sum.getShrink(), o.getShrink(), 0);
		Assert.assertNotEquals(sum.getStretch(), o.getStretch(), 0);
	}

	@Test
	public void testNode() {
		Node node = Node.obtain(null, null);
		Assert.assertNotNull(node);
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

		Node previous = node;
		node = Node.obtain(null, null);
		Assert.assertSame(previous, node);
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
	}

	@Test
	public void testBreakPoint() {
		BreakPoint breakPoint = BreakPoint.obtain(1, 2);
		Assert.assertNotNull(breakPoint);
		Assert.assertEquals(breakPoint.position, 1);
		Assert.assertEquals(breakPoint.ratio, 2, 0);

		breakPoint.recycle();
		Assert.assertEquals(breakPoint.position, -1);
		Assert.assertEquals(breakPoint.ratio, -1, 0);

		BreakPoint t = breakPoint;
		breakPoint = BreakPoint.obtain(3, 4);
		Assert.assertSame(t, breakPoint);
		Assert.assertNotNull(breakPoint);
		Assert.assertEquals(breakPoint.position, 3);
		Assert.assertEquals(breakPoint.ratio, 4, 0);
	}

	@Test
	public void testCandidate() {
		Node node = Node.obtain(null, null);
		Assert.assertNotNull(node);
		Candidate candidate = Candidate.obtain(1, 2, node);
		Assert.assertNotNull(candidate);
		Assert.assertEquals(candidate.demerits, 1, 0);
		Assert.assertEquals(candidate.ratio, 2, 0);
		Assert.assertSame(node, candidate.active);

		candidate.recycle();
		Assert.assertEquals(candidate.demerits, Float.MAX_VALUE, 0);
		Assert.assertEquals(candidate.ratio, -1, 0);
		Assert.assertNull(candidate.active);

		Candidate p = candidate;
		candidate = Candidate.obtain(3, 4, node);
		Assert.assertNotNull(candidate);
		Assert.assertEquals(candidate.demerits, 3, 0);
		Assert.assertEquals(candidate.ratio, 4, 0);
		Assert.assertSame(node, candidate.active);
		Assert.assertSame(p, candidate);
	}
}
