package me.chan.texas.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import me.chan.texas.test.mock.MockMeasurer;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.Background;
import me.chan.texas.text.Foreground;
import me.chan.texas.text.OnClickedListener;
import me.chan.texas.text.Penalty;
import me.chan.texas.text.TextBox;
import me.chan.texas.text.TextStyle;
import me.chan.texas.text.UnderLine;

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
		textStyleField.setAccessible(true);
		backgroundField.setAccessible(true);
		foregroundField.setAccessible(true);

		TextBox.Attribute attribute = TextBox.Attribute.obtain();
		Assert.assertNotNull(attribute);
		Assert.assertNull(backgroundField.get(attribute));
		Assert.assertNull(foregroundField.get(attribute));
		Assert.assertNull(textStyleField.get(attribute));

		Background background = Background.obtain(10);
		attribute.setBackground(background);
		Assert.assertSame(background, backgroundField.get(attribute));

		String msg = "hello";

		UnderLine underLine = UnderLine.obtain(10);
		attribute.setForeground(underLine);
		Assert.assertSame(underLine, foregroundField.get(attribute));

		TextStyle style = TextStyle.BOLD;
		attribute.setTextStyle(style);
		Assert.assertSame(style, textStyleField.get(attribute));

		attribute.recycle();
		Assert.assertNull(backgroundField.get(attribute));
		Assert.assertNull(foregroundField.get(attribute));
		Assert.assertNull(textStyleField.get(attribute));
		// test recycle twice
		attribute.recycle();

		TextBox.Attribute p = attribute;
		attribute = TextBox.Attribute.obtain();
		Assert.assertSame(p, attribute);

		Assert.assertNull(backgroundField.get(attribute));
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
		Assert.assertNull(box.getForeground());
		Assert.assertNull(box.getSpanOnClickedListener());
		Assert.assertNull(box.getTextStyle());
		Assert.assertFalse(box.isPenalty());
		Assert.assertEquals(box.getWidth(), mMockTextPaint.getMockTextSize() * msg.length(), 0);
		Assert.assertEquals(box.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		box.setSelected(true);
		Assert.assertTrue(box.isSelected());


		OnClickedListener onClickedListener = new OnClickedListener() {
			@Override
			public void onClicked(float x, float y) {
			}
		};
		OnClickedListener onSpanClickedListener = new OnClickedListener() {
			@Override
			public void onClicked(float x, float y) {
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
		Assert.assertSame(box.getForeground(), foreground);
		Assert.assertSame(box.getSpanOnClickedListener(), onSpanClickedListener);
		Assert.assertSame(box.getTextStyle(), textStyle);
		Assert.assertEquals(box.getWidth(), mMockTextPaint.getMockTextSize() * msg.length(), 0);
		Assert.assertEquals(box.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		checkBoxContent(box, msg);

		// check append
		Penalty penalty = Penalty.obtain(mMockTextPaint.getMockTextSize(), 2, 3, true);
		Assert.assertNotNull(penalty);
		box.append(penalty);
		Assert.assertTrue(box.isPenalty());
		checkBoxContent(box, msg + "-");
		Assert.assertEquals(box.getWidth(), mMockTextPaint.getMockTextSize() * (msg.length() + 1), 0);
		Assert.assertEquals(box.getHeight(), mMockTextPaint.getMockTextHeight(), 0);

		// test spilt
		TextBox suffix = box.spilt("hello".length() * mMockTextPaint.getMockTextSize());
		Assert.assertNotNull(suffix);
		Assert.assertFalse(box.isPenalty());
		Assert.assertTrue(suffix.isPenalty());
		checkBoxContent(box, "hello");
		checkBoxContent(suffix, " world" + "-");
		Assert.assertEquals(box.isSelected(), suffix.isSelected());
		Assert.assertEquals(box.isRecycled(), suffix.isRecycled());
		Assert.assertSame(box.getOnClickedListener(), suffix.getOnClickedListener());
		Assert.assertSame(box.getBackground(), suffix.getBackground());
		Assert.assertSame(box.getForeground(), suffix.getForeground());
		Assert.assertSame(box.getSpanOnClickedListener(), suffix.getSpanOnClickedListener());
		Assert.assertSame(box.getTextStyle(), suffix.getTextStyle());
		Assert.assertEquals(box.getWidth(), mMockTextPaint.getMockTextSize() * "hello".length(), 0);
		Assert.assertEquals(box.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		Assert.assertEquals(suffix.getWidth(), mMockTextPaint.getMockTextSize() * " world-".length(), 0.1);
		Assert.assertEquals(suffix.getHeight(), mMockTextPaint.getMockTextHeight(), 0);

		// test copy
		TextBox temp = TextBox.obtain(msg, 0, msg.length(),
				mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(),
				null, null);
		temp.copy(suffix);
		Assert.assertEquals(temp.isSelected(), suffix.isSelected());
		Assert.assertEquals(temp.isRecycled(), suffix.isRecycled());
		Assert.assertSame(temp.getOnClickedListener(), suffix.getOnClickedListener());
		Assert.assertSame(temp.getBackground(), suffix.getBackground());
		Assert.assertSame(temp.getForeground(), suffix.getForeground());
		Assert.assertSame(temp.getSpanOnClickedListener(), suffix.getSpanOnClickedListener());
		Assert.assertSame(temp.getTextStyle(), suffix.getTextStyle());
		Assert.assertEquals(temp.getWidth(), mMockTextPaint.getMockTextSize() * " world-".length(), 0.1);
		Assert.assertEquals(temp.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		checkBoxContent(temp, " world" + "-");

		TextBox prev = box;
		box.recycle();

		assertFalse(box.isSelected());
		Assert.assertTrue(box.isRecycled());
		Assert.assertNull(box.getOnClickedListener());
		Assert.assertNull(box.getBackground());
		Assert.assertNull(box.getForeground());
		Assert.assertNull(box.getSpanOnClickedListener());
		Assert.assertNull(box.getTextStyle());
		Assert.assertNotEquals(box.getWidth(), mMockTextPaint.getMockTextSize() * "hello".length(), 0);
		Assert.assertNotEquals(box.getHeight(), mMockTextPaint.getMockTextHeight(), 0);

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
			public void onClicked(float x, float y) {
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
		box2 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), onClickedListener, null);
		Assert.assertNotNull(box2);

		Assert.assertNotSame(box1, box2);

		Assert.assertEquals(box1, box1);
		Assert.assertEquals(box2, box2);
		Assert.assertNotEquals(box1, box2);

		checkBoxContent(box1, msg);
		checkBoxContent(box2, msg);

		box2 = TextBox.obtain("kkk", 0, 3, mMockTextPaint.getMockTextSize() * 3, mMockTextPaint.getMockTextHeight(), onClickedListener, attribute);
		Assert.assertNotNull(box2);

		Assert.assertNotSame(box1, box2);

		Assert.assertEquals(box1, box1);
		Assert.assertEquals(box2, box2);
		Assert.assertNotEquals(box1, box2);

		checkBoxContent(box1, msg);
		checkBoxContent(box2, "kkk");


		box2 = TextBox.obtain(msg, 0, msg.length() - 1, mMockTextPaint.getMockTextSize() * (msg.length() - 1), mMockTextPaint.getMockTextHeight(), onClickedListener, attribute);
		Assert.assertNotNull(box2);

		Assert.assertNotSame(box1, box2);

		Assert.assertEquals(box1, box1);
		Assert.assertEquals(box2, box2);
		Assert.assertNotEquals(box1, box2);

		checkBoxContent(box1, msg);
		checkBoxContent(box2, "hello worl");

		box2 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * (msg.length()), mMockTextPaint.getMockTextHeight(), null, attribute);
		Assert.assertNotNull(box2);

		Assert.assertNotSame(box1, box2);

		Assert.assertEquals(box1, box1);
		Assert.assertEquals(box2, box2);
		Assert.assertNotEquals(box1, box2);

		checkBoxContent(box1, msg);
		checkBoxContent(box2, msg);

		box2 = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * (msg.length()), mMockTextPaint.getMockTextHeight() - 1, onClickedListener, attribute);
		Assert.assertNotNull(box2);

		Assert.assertNotSame(box1, box2);

		Assert.assertEquals(box1, box1);
		Assert.assertEquals(box2, box2);
		Assert.assertNotEquals(box1, box2);

		checkBoxContent(box1, msg);
		checkBoxContent(box2, msg, mMockTextPaint.getMockTextHeight() - 1);
	}

	@Test
	public void testBoxSpilt() {
		String msg = "hello world";
		TextBox box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null, null);
		Assert.assertNotNull(box);
		Assert.assertFalse(box.isPenalty());

		// spilt越界
		Assert.assertNull(box.spilt(-1));
		Assert.assertNull(box.spilt((msg.length() + 1) * mMockTextPaint.getMockTextSize()));

		TextBox suffix = box.spilt("hello".length() * mMockTextPaint.getMockTextSize());
		Assert.assertNotNull(suffix);

		checkBoxContent(box, "hello");
		checkBoxContent(suffix, " world");
		Assert.assertEquals(suffix.getHeight(), box.getHeight(), 0);
		Assert.assertEquals(suffix.getTextStyle(), box.getTextStyle());

		TextBox previous = box;
		box.recycle();

		msg = "hello";
		box = TextBox.obtain(msg, 0, msg.length(), mMockTextPaint.getMockTextSize() * msg.length(), mMockTextPaint.getMockTextHeight(), null, null);
		Assert.assertSame(previous, box);
	}

	@Test
	public void testBoxCopy() {
		String msg = "hello world";
		OnClickedListener onClickedListener = new OnClickedListener() {
			@Override
			public void onClicked(float x, float y) {
			}
		};
		TextBox.Attribute attribute = TextBox.Attribute.obtain();
		TextBox box = TextBox.obtain(msg, 0, msg.length(),
				mMockTextPaint.getMockTextSize() * msg.length(),
				mMockTextPaint.getMockTextHeight(),
				onClickedListener, attribute);
		box.setSelected(true);
		box.setOnClickedListener(new OnClickedListener() {
			@Override
			public void onClicked(float x, float y) {
			}
		});

		TextBox copy = TextBox.obtain("x", 0, 1, 1, 2, null, null);
		Assert.assertNotNull(copy);
		Assert.assertNotEquals(copy, box);

		copy.copy(box);
		Assert.assertEquals(box, copy);

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
