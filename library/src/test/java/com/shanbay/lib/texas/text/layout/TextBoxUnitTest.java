package com.shanbay.lib.texas.text.layout;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextPaint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shanbay.lib.texas.TestUtils;
import com.shanbay.lib.texas.measurer.Measurer;
import com.shanbay.lib.texas.measurer.MockMeasurer;
import com.shanbay.lib.texas.test.mock.MockTextPaint;
import com.shanbay.lib.texas.text.Appearance;
import com.shanbay.lib.texas.text.DrawContext;
import com.shanbay.lib.texas.text.TextAttribute;
import com.shanbay.lib.texas.text.TextStyle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TextBoxUnitTest {
	private MockTextPaint mMockTextPaint;
	private MockMeasurer mMockMeasurer;
	private Appearance mBg;
	private Appearance mFg;
	private String mTag;
	private TextStyle mTextStyle;
	private TextAttribute mTextAttribute;

	@Before
	public void setup() {
		mMockTextPaint = new MockTextPaint();
		mMockMeasurer = new MockMeasurer(mMockTextPaint);
		mTextAttribute = new TextAttribute(mMockMeasurer);

		Assert.assertNotEquals(mMockTextPaint.getMockTextSize(), 0);
		Assert.assertNotEquals(mMockTextPaint.getMockTextHeight(), 0);

		mBg = new Appearance() {
			@Override
			public void draw(Canvas canvas, TextPaint textPaint, RectF inner, RectF outer, DrawContext context) {

			}
		};
		mFg = new Appearance() {
			@Override
			public void draw(Canvas canvas, TextPaint textPaint, RectF inner, RectF outer, DrawContext context) {

			}
		};
		mTag = "hello";
		mTextStyle = new TextStyle() {
			@Override
			public void update(@NonNull TextPaint textPaint, @Nullable Object tag) {

			}
		};
	}

	/**
	 * 测试前置条件，比如
	 */
	@Test
	public void preConditionTest() {
		Assert.assertNotNull(mMockTextPaint);
		Assert.assertNotNull(mMockMeasurer);
		Assert.assertNotNull(mBg);
		Assert.assertNotNull(mFg);
		Assert.assertNotNull(mTag);
		Assert.assertNotNull(mTextStyle);

		// test mock measure
		String hello = "hello";
		Measurer.CharSequenceSpec spec = Measurer.CharSequenceSpec.obtain();
		mMockMeasurer.measure(hello,
				0, hello.length(), TextStyle.NONE, null, spec);
		Assert.assertEquals("check measure text", spec.getWidth(), hello.length() * mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals("check measure text", spec.getHeight(), mMockTextPaint.getMockTextSize(), 0);
	}

	private TextBox create(String msg) {
		return TextBox.obtain(msg, 0, msg.length(), mMockMeasurer, mTextStyle, mTag, mBg, mFg);
	}

	private TextBox createRandom(String msg) {
		// test obtain after recycle
		Appearance bg = new Appearance() {
			@Override
			public void draw(Canvas canvas, TextPaint textPaint, RectF inner, RectF outer, DrawContext context) {

			}
		};
		Appearance fg = new Appearance() {
			@Override
			public void draw(Canvas canvas, TextPaint textPaint, RectF inner, RectF outer, DrawContext context) {

			}
		};
		TextStyle textStyle = new TextStyle() {
			@Override
			public void update(@NonNull TextPaint textPaint, @Nullable Object tag) {

			}
		};
		String tag = "foo";
		return TextBox.obtain(msg, 0, msg.length(), mMockMeasurer, textStyle, tag, bg, fg);
	}

	@Test
	public void testAppendBox() {
		String msg = "hello";

		TextBox textBox = create(msg);
		Assert.assertNotNull(textBox);
		Assert.assertEquals(textBox.toString(), msg);

		// check content is right
		// box
		Assert.assertEquals(textBox.getWidth(), msg.length() * mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals(textBox.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		Assert.assertEquals(textBox.getTag(), mTag);
		Assert.assertEquals(textBox.getBackground(), mBg);
		Assert.assertEquals(textBox.getForeground(), mFg);

		// text box
		Assert.assertEquals(textBox.toString(), msg);
		Assert.assertEquals(textBox.getText(), msg);
		Assert.assertEquals(textBox.getStart(), 0);
		Assert.assertEquals(textBox.getEnd(), msg.length());
		Assert.assertEquals(textBox.getTextStyle(), mTextStyle);
		Assert.assertFalse(textBox.isPenalty());
		Assert.assertFalse(textBox.isRecycled());

		// 下标不是从0开始的内容
		msg = "abc";
		textBox = TextBox.obtain(msg, 1, msg.length() - 1, mMockMeasurer, mTextStyle, mTag, mBg, mFg);
		Assert.assertEquals(textBox.getWidth(), mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals(textBox.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		Assert.assertEquals(textBox.getTag(), mTag);
		Assert.assertEquals(textBox.getBackground(), mBg);
		Assert.assertEquals(textBox.getForeground(), mFg);

		// text box
		Assert.assertEquals(textBox.toString(), "b");
		Assert.assertEquals(textBox.getText(), msg);
		Assert.assertEquals(textBox.getStart(), 1);
		Assert.assertEquals(textBox.getEnd(), msg.length() - 1);
		Assert.assertEquals(textBox.getTextStyle(), mTextStyle);
		Assert.assertFalse(textBox.isPenalty());
		Assert.assertFalse(textBox.isRecycled());
	}

	@Test
	public void testAppendPenalty() {
		String msg = "hello";

		TextBox textBox = create(msg);
		Assert.assertNotNull(textBox);
		Assert.assertEquals(textBox.toString(), msg);

		// check content is right
		// box
		Assert.assertEquals(textBox.getWidth(), msg.length() * mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals(textBox.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		Assert.assertEquals(textBox.getTag(), mTag);
		Assert.assertEquals(textBox.getBackground(), mBg);
		Assert.assertEquals(textBox.getForeground(), mFg);

		// text box
		Assert.assertEquals(textBox.toString(), msg);
		Assert.assertEquals(textBox.getText(), msg);
		Assert.assertEquals(textBox.getStart(), 0);
		Assert.assertEquals(textBox.getEnd(), msg.length());
		Assert.assertEquals(textBox.getTextStyle(), mTextStyle);
		Assert.assertFalse(textBox.isPenalty());
		Assert.assertFalse(textBox.isRecycled());

		Penalty penalty = Penalty.obtain(10, true, null, null, mMockMeasurer, mTextAttribute);
		textBox.appendContent(penalty);

		// check content not changed except text
		msg = "hello-";
		Assert.assertEquals(textBox.getWidth(), msg.length() * mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals(textBox.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		Assert.assertEquals(textBox.getTag(), mTag);
		Assert.assertEquals(textBox.getBackground(), mBg);
		Assert.assertEquals(textBox.getForeground(), mFg);

		// text box
		Assert.assertEquals(textBox.toString(), msg);
		Assert.assertEquals(textBox.getText(), msg);
		Assert.assertEquals(textBox.getStart(), 0);
		Assert.assertEquals(textBox.getEnd(), msg.length());
		Assert.assertEquals(textBox.getTextStyle(), mTextStyle);
		Assert.assertTrue(textBox.isPenalty());
		Assert.assertFalse(textBox.isRecycled());

		// 下标不是从0开始的内容
		msg = "abc";
		textBox = TextBox.obtain(msg, 1, msg.length() - 1, mMockMeasurer, mTextStyle, mTag, mBg, mFg);
		Assert.assertEquals(textBox.getWidth(), mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals(textBox.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		Assert.assertEquals(textBox.getTag(), mTag);
		Assert.assertEquals(textBox.getBackground(), mBg);
		Assert.assertEquals(textBox.getForeground(), mFg);

		// text box
		Assert.assertEquals(textBox.toString(), "b");
		Assert.assertEquals(textBox.getText(), msg);
		Assert.assertEquals(textBox.getStart(), 1);
		Assert.assertEquals(textBox.getEnd(), msg.length() - 1);
		Assert.assertEquals(textBox.getTextStyle(), mTextStyle);
		Assert.assertFalse(textBox.isPenalty());
		Assert.assertFalse(textBox.isRecycled());

		textBox.appendContent(penalty);
		Assert.assertEquals(textBox.getWidth(), 2 * mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals(textBox.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		Assert.assertEquals(textBox.getTag(), mTag);
		Assert.assertEquals(textBox.getBackground(), mBg);
		Assert.assertEquals(textBox.getForeground(), mFg);

		// text box
		Assert.assertEquals(textBox.toString(), "b-");
		Assert.assertEquals(textBox.getText(), "b-");
		Assert.assertEquals(textBox.getStart(), 0);
		Assert.assertEquals(textBox.getEnd(), 2);
		Assert.assertEquals(textBox.getTextStyle(), mTextStyle);
		Assert.assertTrue(textBox.isPenalty());
		Assert.assertFalse(textBox.isRecycled());

		// test append penalty twice
		try {
			textBox.appendContent(penalty);
			Assert.fail();
		} catch (Throwable t) {

		}
	}

	@Test
	public void testClean() {
		if (TextBox.hasBuffered()) {
			TextBox.clean();
		}

		Assert.assertFalse(TextBox.hasBuffered());
		TextBox textBox = create("hello");
		Assert.assertNotNull(textBox);

		Assert.assertFalse(textBox.isRecycled());
		textBox.recycle();
		Assert.assertTrue(textBox.isRecycled());

		TextBox.clean();
		Assert.assertFalse(TextBox.hasBuffered());
	}

	@Test
	public void testObtainAndRecycle() {
		TextBox.clean();

		String msg = "hello";
		TextBox textBox = TextBox.obtain(msg, 0, msg.length(), mMockMeasurer, mTextStyle, mTag, mBg, mFg);
		Assert.assertNotNull(textBox);
		// box
		Assert.assertEquals(textBox.getWidth(), msg.length() * mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals(textBox.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		Assert.assertEquals(textBox.getTag(), mTag);
		Assert.assertEquals(textBox.getBackground(), mBg);
		Assert.assertEquals(textBox.getForeground(), mFg);

		// text box
		Assert.assertEquals(textBox.toString(), msg);
		Assert.assertEquals(textBox.getText(), msg);
		Assert.assertEquals(textBox.getStart(), 0);
		Assert.assertEquals(textBox.getEnd(), msg.length());
		Assert.assertEquals(textBox.getTextStyle(), mTextStyle);
		Assert.assertFalse(textBox.isPenalty());
		Assert.assertFalse(textBox.isRecycled());
		Assert.assertEquals(textBox.getAttribute(), TextBox.ATTRIBUTE_NONE);
		float width = textBox.getWidth() / TextBox.SQUISH_FACTOR;
		textBox.addAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT);
		Assert.assertNotEquals(textBox.getAttribute(), TextBox.ATTRIBUTE_NONE);
		Assert.assertTrue(textBox.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));
		Assert.assertFalse(textBox.hasAttribute(TextBox.ATTRIBUTE_ZOOM_OUT));
		Assert.assertEquals(width, textBox.getWidth(), 0.1f);

		TextBox prev1 = textBox;

		// test obtain not recycle
		String subStr = "ell";
		textBox = TextBox.obtain(msg, 1, msg.length() - 1, mMockMeasurer, mTextStyle, mTag, mBg, mFg);
		Assert.assertNotNull(textBox);
		Assert.assertNotEquals(prev1, textBox);
		// box
		Assert.assertEquals(textBox.getWidth(), subStr.length() * mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals(textBox.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		Assert.assertEquals(textBox.getTag(), mTag);
		Assert.assertEquals(textBox.getBackground(), mBg);
		Assert.assertEquals(textBox.getForeground(), mFg);

		// text box
		Assert.assertEquals(textBox.toString(), subStr);
		Assert.assertEquals(textBox.getText(), msg);
		Assert.assertEquals(textBox.getStart(), 1);
		Assert.assertEquals(textBox.getEnd(), msg.length() - 1);
		Assert.assertEquals(textBox.getTextStyle(), mTextStyle);
		float height = textBox.getHeight() * TextBox.ZOOM_OUT_FACTOR;
		width = textBox.getWidth() * TextBox.ZOOM_OUT_FACTOR;
		textBox.addAttribute(TextBox.ATTRIBUTE_ZOOM_OUT);
		Assert.assertFalse(textBox.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));
		Assert.assertTrue(textBox.hasAttribute(TextBox.ATTRIBUTE_ZOOM_OUT));
		Assert.assertEquals(width, textBox.getWidth(), 0.1f);
		Assert.assertEquals(height, textBox.getHeight(), 0.1f);

		Penalty penalty = Penalty.obtain(0, true, null, null, mMockMeasurer, mTextAttribute);
		Assert.assertFalse(textBox.isPenalty());
		textBox.appendContent(penalty);
		Assert.assertTrue(textBox.isPenalty());
		Assert.assertFalse(textBox.isRecycled());

		textBox.recycle();
		TestUtils.testRecycled(textBox);
		Assert.assertFalse(textBox.isPenalty());
		Assert.assertEquals(textBox.getAttribute(), TextBox.ATTRIBUTE_NONE);
		Assert.assertTrue(textBox.isRecycled());

		TextBox prev2 = textBox;

		// test obtain after recycle
		Appearance bg = new Appearance() {
			@Override
			public void draw(Canvas canvas, TextPaint textPaint, RectF inner, RectF outer, DrawContext context) {

			}
		};
		Appearance fg = new Appearance() {
			@Override
			public void draw(Canvas canvas, TextPaint textPaint, RectF inner, RectF outer, DrawContext context) {

			}
		};
		TextStyle textStyle = new TextStyle() {
			@Override
			public void update(@NonNull TextPaint textPaint, @Nullable Object tag) {

			}
		};
		String tag = "foo";
		textBox = TextBox.obtain(msg, 0, msg.length(), mMockMeasurer, textStyle, tag, bg, fg);
		Assert.assertNotNull(textBox);
		Assert.assertSame(prev2, textBox);
		// box
		Assert.assertEquals(textBox.getWidth(), msg.length() * mMockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals(textBox.getHeight(), mMockTextPaint.getMockTextHeight(), 0);
		Assert.assertEquals(textBox.getTag(), tag);
		Assert.assertEquals(textBox.getBackground(), bg);
		Assert.assertEquals(textBox.getForeground(), fg);

		// text box
		Assert.assertEquals(textBox.toString(), msg);
		Assert.assertEquals(textBox.getText(), msg);
		Assert.assertEquals(textBox.getStart(), 0);
		Assert.assertEquals(textBox.getEnd(), msg.length());
		Assert.assertEquals(textBox.getTextStyle(), textStyle);
		Assert.assertFalse(textBox.isPenalty());
		Assert.assertFalse(textBox.isRecycled());
	}

	@Test
	public void testCopyAndEquals() {
		String msg1 = "hello";
		String msg2 = "world2";

		TextBox lhs = create(msg1);
		TextBox rhs = createRandom(msg2);

		Assert.assertNotNull(lhs);
		Assert.assertNotNull(rhs);
		Assert.assertNotSame(lhs, rhs);

		lhs.mHeight -= 1;

		// box
		Assert.assertNotEquals(lhs.getWidth(), rhs.getWidth(), 0);
		Assert.assertNotEquals(lhs.getHeight(), rhs.getHeight(), 0);
		Assert.assertNotEquals(lhs.getTag(), rhs.getTag());
		Assert.assertNotEquals(lhs.getBackground(), rhs.getBackground());
		Assert.assertNotEquals(lhs.getForeground(), rhs.getForeground());

		Penalty penalty = Penalty.obtain(1, true, null, null, mMockMeasurer, mTextAttribute);
		rhs.appendContent(penalty);

		// text box
		Assert.assertNotEquals(lhs.toString(), rhs.toString());
		Assert.assertNotEquals(lhs.getText(), rhs.getText());
		rhs.setStart(1);
		Assert.assertNotEquals(lhs.getStart(), rhs.getStart());
		Assert.assertNotEquals(lhs.getEnd(), rhs.getEnd());
		Assert.assertNotEquals(lhs.getTextStyle(), rhs.getTextStyle());
		Assert.assertNotEquals(lhs.isPenalty(), rhs.isPenalty());
		rhs.recycle();
		Assert.assertNotEquals(lhs.isRecycled(), rhs.isRecycled());

		rhs.reuse();
		rhs.copy(lhs);

		Assert.assertEquals(lhs.toString(), rhs.toString());
		Assert.assertEquals(lhs.getText(), rhs.getText());
		Assert.assertEquals(lhs.getStart(), rhs.getStart());
		Assert.assertEquals(lhs.getEnd(), rhs.getEnd());
		Assert.assertEquals(lhs.getTextStyle(), rhs.getTextStyle());
		Assert.assertEquals(lhs.isPenalty(), rhs.isPenalty());
		Assert.assertEquals(lhs.isRecycled(), rhs.isRecycled());

		Assert.assertEquals(lhs, rhs);
		Assert.assertTrue(TestUtils.reflectCompare(lhs, rhs, "mId"));
	}
}
