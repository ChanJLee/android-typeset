package me.chan.te.test;

import android.text.TextPaint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import me.chan.te.data.Box;
import me.chan.te.data.BoxStyle;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Glue;
import me.chan.te.data.Penalty;
import me.chan.te.data.TextBox;
import me.chan.te.test.mock.MockMeasurer;
import me.chan.te.test.mock.MockTextPaint;

import static org.junit.Assert.fail;

public class DataUnitTest {
	private ElementFactory mElementFactory;
	private MockTextPaint mMockTextPaint;

	@Before
	public void setup() {
		mMockTextPaint = new MockTextPaint();
		MockMeasurer mockMeasurer = new MockMeasurer(mMockTextPaint);
		mElementFactory = new ElementFactory(mockMeasurer);

		Assert.assertNotEquals(mMockTextPaint.getMockTextSize(), 0);
		Assert.assertNotEquals(mMockTextPaint.getMockTextHeight(), 0);

		String hello = "hello";
		Assert.assertNotEquals(mockMeasurer.getFontSpacing(), 0);
		Assert.assertEquals("check measure text", mockMeasurer.getDesiredWidth(hello,
				0, hello.length()), hello.length() * mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals("check measure text", mockMeasurer.getDesiredWidth(hello,
				1, 2), mMockTextPaint.getMockTextSize(), 0);

		try {
			mElementFactory.obtainTextBox(null, 0, 10, null);
			fail("obtain null box");
		} catch (Throwable throwable) {
		}

		try {
			mElementFactory.obtainTextBox(null);
			fail("obtain null box");
		} catch (Throwable throwable) {
		}
	}

	@Test
	public void testGlue() {
		Glue glue = mElementFactory.obtainGlue(1, 2, 3);
		Assert.assertNotNull(glue);

		Assert.assertEquals("check width: ", glue.getWidth(), 1, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), 2, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), 3, 0);

		Glue previous = glue;
		mElementFactory.recycle(glue);

		glue = mElementFactory.obtainGlue(4, 5, 6);
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
		mElementFactory.recycle(penalty);

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
		TextBox box = mElementFactory.obtainTextBox(msg);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());

		// check content
		Assert.assertNull(box.getBoxStyle());
		checkBoxContent(box, msg);

		BoxStyle boxStyle = new BoxStyle() {
			@Override
			public void update(TextPaint textPaint) {

			}

			@Override
			public boolean isConflict(BoxStyle other) {
				return false;
			}
		};

		TextBox box2 = mElementFactory.obtainTextBox(msg, 0, msg.length(), boxStyle);
		Assert.assertNotNull(box2);
		Assert.assertNotEquals(box, box2);
		Assert.assertEquals(box2.getBoxStyle(), boxStyle);
		checkBoxContent(box2, msg);

		TextBox prev = box2;
		mElementFactory.recycle(box2);
		msg = "hello";
		box2 = mElementFactory.obtainTextBox(msg, 0, msg.length(), boxStyle);
		Assert.assertNotNull(box2);
		Assert.assertSame(prev, box2);
		checkBoxContent(box2, msg);

		box = mElementFactory.obtainTextBox(msg, 1, msg.length(), boxStyle);
		Assert.assertNotNull(box);
		checkBoxContent(box, "ello");

		try {
			mElementFactory.obtainTextBox(msg, -1, msg.length(), boxStyle).toString();
			fail("check illegal index failed");
		} catch (Exception e) {

		}

		try {
			mElementFactory.obtainTextBox(msg, 0, msg.length() + 1, boxStyle).toString();
			fail("check illegal index failed");
		} catch (Exception e) {

		}
	}

	@Test
	public void testBoxEquals() {
		String msg = "hello world";
		TextBox box1 = mElementFactory.obtainTextBox(msg);
		Assert.assertNotNull(box1);

		TextBox box2 = mElementFactory.obtainTextBox(msg);
		Assert.assertNotNull(box2);

		Assert.assertNotSame(box1, box2);

		Assert.assertEquals(box1, box1);
		Assert.assertEquals(box2, box2);
		Assert.assertEquals(box1, box2);

		checkBoxContent(box1, msg);
		checkBoxContent(box2, msg);

		StringBuilder stringBuilder = new StringBuilder("hello ");
		stringBuilder.append("world");

		box2 = mElementFactory.obtainTextBox(stringBuilder.toString());
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
		TextBox box = mElementFactory.obtainTextBox(msg);
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
		mElementFactory.recycle(box);
		box = mElementFactory.obtainTextBox(msg);
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
		mElementFactory.recycle(box);
		box = mElementFactory.obtainTextBox(msg);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());

		String msg2 = "dcf";
		Box box2 = mElementFactory.obtainTextBox(msg2);
		Assert.assertNotNull(box2);
		Assert.assertFalse(box2.isPenalty());
		Assert.assertFalse(box2.isSplit());

		box.append(box2);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());

		checkBoxContent(box, msg + msg2);
		mElementFactory.recycle(box);
		box = mElementFactory.obtainTextBox(msg);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());
		checkBoxContent(box, msg);
	}

	@Test
	public void testBoxSpilt() {
		String msg = "hello world";
		TextBox box = mElementFactory.obtainTextBox(msg);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());
		Assert.assertFalse(box.isSplit());

		Assert.assertNull(box.spilt(-1));
		Assert.assertNull(box.spilt((msg.length() + 1) * mMockTextPaint.getMockTextSize()));

		TextBox[] boxes = box.spilt("hello".length() * mMockTextPaint.getMockTextSize());
		Assert.assertNotNull(boxes);
		Assert.assertNotNull(boxes[0]);
		Assert.assertNotNull(boxes[1]);

		boxes[0].setFlag(Box.FLAG_SPILT);
		checkBoxContent(boxes[0], "hello");
		checkBoxContent(boxes[1], " world");
		Assert.assertTrue(boxes[0].isSplit());

		Assert.assertEquals(boxes[0].getHeight(), box.getHeight(), 0);
		Assert.assertEquals(boxes[1].getHeight(), box.getHeight(), 0);

		Box previous = boxes[0];
		mElementFactory.recycle(boxes[0]);
		boxes[0] = mElementFactory.obtainTextBox("hello");
		Assert.assertSame(previous, boxes[0]);
		Assert.assertFalse(boxes[0].isSplit());
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
}
