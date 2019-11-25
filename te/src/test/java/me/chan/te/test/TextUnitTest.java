package me.chan.te.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import me.chan.te.test.mock.MockMeasurer;
import me.chan.te.test.mock.MockTextPaint;
import me.chan.te.text.Background;
import me.chan.te.text.Foreground;
import me.chan.te.text.OnClickedListener;
import me.chan.te.text.Penalty;
import me.chan.te.text.TextBox;
import me.chan.te.text.TextStyle;
import me.chan.te.text.UnderLine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class TextUnitTest {

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
			TextBox.obtain(null, 0, 10, 1, 1, null, null).toString();
			fail("obtain null box");
		} catch (Throwable throwable) {
			assertFalse(throwable instanceof AssertionError);
		}

		try {
			TextBox.obtain(null, 1, 1, 1, 1, null, null).toString();
			fail("obtain null box");
		} catch (Throwable throwable) {
			assertFalse(throwable instanceof AssertionError);
		}
	}

	@Test
	public void testRichTextAttribute() throws NoSuchFieldException, IllegalAccessException {
		Class<?> clazz = TextBox.Attribute.class;
		Field textStyleField = clazz.getDeclaredField("mTextStyle");
		Field backgroundField = clazz.getDeclaredField("mBackground");
		Field foregroundField = clazz.getDeclaredField("mForeground");
		Field extraField = clazz.getDeclaredField("mExtra");
		textStyleField.setAccessible(true);
		backgroundField.setAccessible(true);
		foregroundField.setAccessible(true);
		extraField.setAccessible(true);

		TextBox.Attribute attribute = TextBox.Attribute.obtain();
		Assert.assertNotNull(attribute);
		Assert.assertNull(backgroundField.get(attribute));
		Assert.assertNull(extraField.get(attribute));
		Assert.assertNull(foregroundField.get(attribute));
		Assert.assertNull(textStyleField.get(attribute));

		Background background = Background.obtain(10);
		attribute.setBackground(background);
		Assert.assertSame(background, backgroundField.get(attribute));

		String msg = "hello";
		attribute.setExtra(msg);
		Assert.assertSame(msg, extraField.get(attribute));

		UnderLine underLine = UnderLine.obtain(10);
		attribute.setForeground(underLine);
		Assert.assertSame(underLine, foregroundField.get(attribute));

		TextStyle style = TextStyle.BOLD;
		attribute.setTextStyle(style);
		Assert.assertSame(style, textStyleField.get(attribute));

		attribute.recycle();
		Assert.assertNull(backgroundField.get(attribute));
		Assert.assertNull(extraField.get(attribute));
		Assert.assertNull(foregroundField.get(attribute));
		Assert.assertNull(textStyleField.get(attribute));
		// test recycle twice
		attribute.recycle();

		TextBox.Attribute p = attribute;
		attribute = TextBox.Attribute.obtain();
		Assert.assertSame(p, attribute);

		Assert.assertNull(backgroundField.get(attribute));
		Assert.assertNull(extraField.get(attribute));
		Assert.assertNull(foregroundField.get(attribute));
		Assert.assertNull(textStyleField.get(attribute));

		TextBox.Attribute next = TextBox.Attribute.obtain();
		Assert.assertNotSame(next, attribute);
	}

	@Test
	public void testBoxBase() {
		String msg = "hello world";
		TextBox box = TextBox.obtain(msg, 0, msg.length(),
				mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null, null);
		Assert.assertNotNull(box);
		assertFalse(box.isSelected());
		assertFalse(box.isRecycled());
		Assert.assertNull(box.getOnClickedListener());
		Assert.assertNull(box.getBackground());
		Assert.assertNull(box.getExtra());
		Assert.assertNull(box.getForeground());
		Assert.assertNull(box.getSpanOnClickedListener());
		Assert.assertNull(box.getTextStyle());
		Assert.assertFalse(box.isPenalty());
		box.setSelected(true);
		Assert.assertTrue(box.isSelected());


		OnClickedListener onClickedListener = new OnClickedListener() {
			@Override
			public boolean onClicked(float x, float y) {
				return false;
			}
		};
		OnClickedListener onSpanClickedListener = new OnClickedListener() {
			@Override
			public boolean onClicked(float x, float y) {
				return false;
			}
		};
		box.setOnClickedListener(onClickedListener);
		Assert.assertSame(onClickedListener, box.getOnClickedListener());


		// check content
		checkBoxContent(box, msg);

		TextStyle textStyle = TextStyle.NONE;
		Background background = Background.obtain(10);
		Foreground foreground = UnderLine.obtain(10);
		TextBox.Attribute attribute = TextBox.Attribute.obtain();
		attribute.setTextStyle(textStyle);
		attribute.setExtra(msg);
		attribute.setForeground(foreground);
		attribute.setBackground(background);
		attribute.setSpanOnClickedListener(onSpanClickedListener);


		box = TextBox.obtain(msg, 0, msg.length(),
				mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(),
				onClickedListener, attribute);
		Assert.assertNotNull(box);
		assertFalse(box.isSelected());
		assertFalse(box.isRecycled());
		Assert.assertFalse(box.isPenalty());
		Assert.assertSame(box.getOnClickedListener(), onClickedListener);
		Assert.assertSame(box.getBackground(), background);
		Assert.assertSame(box.getExtra(), msg);
		Assert.assertSame(box.getForeground(), foreground);
		Assert.assertSame(box.getSpanOnClickedListener(), onSpanClickedListener);
		Assert.assertSame(box.getTextStyle(), textStyle);
		checkBoxContent(box, msg);

		// check append
		Penalty penalty = Penalty.obtain(mMockTextPaint.getMockTextSize(), 2, 3, true);
		Assert.assertNotNull(penalty);
		box.append(penalty);
		Assert.assertTrue(box.isPenalty());
		checkBoxContent(box, msg + "-");

		// test equals
		TextBox suffix = box.spilt("hello".length() * mMockTextPaint.getMockTextSize());
		Assert.assertNotNull(suffix);
		Assert.assertFalse(box.isPenalty());
		Assert.assertTrue(suffix.isPenalty());
		checkBoxContent(box, "hello");
		checkBoxContent(suffix, " world" + "-");

		TextBox temp = TextBox.obtain(msg, 0, msg.length(),
				mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(),
				null, null);
		temp.copy(suffix);
		Assert.assertEquals(temp.isSelected(), suffix.isSelected());
		Assert.assertEquals(temp.isRecycled(), suffix.isRecycled());
		Assert.assertSame(temp.getOnClickedListener(), suffix.getOnClickedListener());
		Assert.assertSame(temp.getBackground(), suffix.getBackground());
		Assert.assertSame(temp.getExtra(), suffix.getExtra());
		Assert.assertSame(temp.getForeground(), suffix.getForeground());
		Assert.assertSame(temp.getSpanOnClickedListener(), suffix.getSpanOnClickedListener());
		Assert.assertSame(temp.getTextStyle(), suffix.getTextStyle());
		checkBoxContent(temp, " world" + "-");

		TextBox prev = box;
		box.recycle();

		assertFalse(box.isSelected());
		Assert.assertTrue(box.isRecycled());
		Assert.assertNull(box.getOnClickedListener());
		Assert.assertNull(box.getBackground());
		Assert.assertNull(box.getExtra());
		Assert.assertNull(box.getForeground());
		Assert.assertNull(box.getSpanOnClickedListener());
		Assert.assertNull(box.getTextStyle());

		// test recycle twice
		box.recycle();

		msg = "hello";
		box = TextBox.obtain(msg, 0, msg.length(),
				mMockTextPaint.getMockTextSize() * msg.length(),
				mMockTextPaint.getMockTextHeight(), null, null);
		Assert.assertNotSame(box, TextBox.obtain(msg, 0, msg.length(),
				mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(),
				null, null));
		Assert.assertNotNull(box);
		Assert.assertSame(box, prev);
		assertFalse(box.isSelected());
		assertFalse(box.isRecycled());
		Assert.assertNull(box.getOnClickedListener());
		Assert.assertNull(box.getBackground());
		Assert.assertNull(box.getExtra());
		Assert.assertNull(box.getForeground());
		Assert.assertNull(box.getSpanOnClickedListener());
		Assert.assertNull(box.getTextStyle());
		box.setSelected(true);
		Assert.assertTrue(box.isSelected());
		checkBoxContent(box, msg);

		box = TextBox.obtain(msg, 1, msg.length(), mMockTextPaint.getMockTextSize() * (msg.length() - 1), mMockTextPaint.getMockTextHeight(), null, null);
		Assert.assertNotNull(box);
		checkBoxContent(box, "ello");

		try {
			TextBox.obtain(msg, -1, msg.length(), 1, 1, null, null).toString();
			fail("check illegal index failed");
		} catch (Throwable e) {
			assertFalse(e instanceof AssertionError);
		}

		try {
			TextBox.obtain(msg, 0, msg.length() + 1, 1, 1, null, null).toString();
			fail("check illegal index failed");
		} catch (Throwable e) {
			assertFalse(e instanceof AssertionError);
		}
	}

	@Test
	public void testBoxEquals() {
		String msg = "hello world";
		TextBox.Attribute attribute = TextBox.Attribute.obtain();
		OnClickedListener onClickedListener = new OnClickedListener() {
			@Override
			public boolean onClicked(float x, float y) {
				return false;
			}
		};
		TextBox box1 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), onClickedListener, attribute);
		Assert.assertNotNull(box1);

		TextBox box2 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), onClickedListener, attribute);
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
		box2 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), onClickedListener, attribute);
		Assert.assertNotNull(box2);

		Assert.assertNotSame(box1, box2);

		Assert.assertEquals(box1, box1);
		Assert.assertEquals(box2, box2);
		Assert.assertEquals(box1, box2);

		checkBoxContent(box1, msg);
		checkBoxContent(box2, msg);
	}

	@Test
	public void testBoxSpilt() {
		String msg = "hello world";
		TextBox box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null, null);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());

		Assert.assertNull(box.spilt(-1));
		Assert.assertNull(box.spilt((msg.length() + 1) * mMockTextPaint.getMockTextSize()));

		TextBox suffix = box.spilt("hello".length() * mMockTextPaint.getMockTextSize());
		Assert.assertNotNull(suffix);

		checkBoxContent(box, "hello");
		checkBoxContent(suffix, " world");
		Assert.assertEquals(suffix.getHeight(), box.getHeight(), 0);
		Assert.assertEquals(suffix.getTextStyle(), box.getTextStyle());
		Assert.assertEquals(suffix.getExtra(), box.getExtra());

		TextBox previous = box;
		box.recycle();
		msg = "hello";
		box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null, null);
		Assert.assertSame(previous, box);
	}

	@Test
	public void testBoxCopy() {
		String msg = "hello world";
		TextBox box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null, null);
		box.setSelected(true);
		box.setOnClickedListener(new OnClickedListener() {
			@Override
			public boolean onClicked(float x, float y) {
				return false;
			}
		});

		TextBox copy = TextBox.obtain(msg + "x", 0, msg.length() + 1, mMockTextPaint.getMockTextSize() * (msg.length() + 1), mMockTextPaint.getMockTextHeight(),  null, null);
		checkBoxContent(copy, msg + "x");
		Assert.assertNotSame(copy, box);
		copy.copy(box);

		Assert.assertTrue(copy.isSelected());
		Assert.assertSame(copy.getOnClickedListener(), box.getOnClickedListener());
		checkBoxContent(copy, msg);
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
