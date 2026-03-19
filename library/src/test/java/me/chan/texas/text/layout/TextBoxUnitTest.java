package me.chan.texas.text.layout;

import me.chan.texas.TexasOption;
import me.chan.texas.di.FakeMeasureFactory;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.misc.Rect;
import me.chan.texas.misc.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.chan.texas.TestUtils;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.test.mock.MockTextPaint;

import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.text.Appearance;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextGravity;
import me.chan.texas.text.TextStyle;
import me.chan.texas.typesetter.ParagraphTypesetter;

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
			public void draw(TexasCanvas canvas, TexasPaint textPaint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		mFg = new Appearance() {
			@Override
			public void draw(TexasCanvas canvas, TexasPaint textPaint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		mTag = "hello";
		mTextStyle = new TextStyle() {
			@Override
			public void update(@NonNull TexasPaint textPaint, @Nullable Object tag) {

			}
		};
	}

	@Test
	public void testMerge() {
		String text = "hello world";

		TextSpan textBox1 = TextSpan.obtain(text, 0, 6, mTextStyle, null, null, null);
		textBox1.measure(mMockMeasurer, mTextAttribute);
		TextSpan textBox2 = TextSpan.obtain(text, 6, text.length(), mTextStyle, null, null, null);
		textBox2.measure(mMockMeasurer, mTextAttribute);

		// Set the same group ID to allow merging
		textBox1.mGroupId = 1;
		textBox2.mGroupId = 1;

		boolean merged = textBox1.merge(textBox2);

		Assert.assertTrue("TextBoxes should be merged", merged);
		Assert.assertEquals("Merged text should be 'helloworld'", "hello world", textBox1.getText().toString());
		Assert.assertEquals("Merged width should be the sum of both widths", text.length() * mMockTextPaint.getMockTextSize(), textBox1.getWidth(), 0);
		Assert.assertEquals("Merged height should be the maximum of both heights", Math.max(textBox1.getHeight(), textBox2.getHeight()), textBox1.getHeight(), 0);
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

	private TextSpan create(String msg) {
		TextSpan textBox = TextSpan.obtain(msg, 0, msg.length(), mTextStyle, mTag, mBg, mFg);
		textBox.measure(mMockMeasurer, mTextAttribute);
		return textBox;
	}

	private TextSpan createRandom(String msg) {
		// test obtain after recycle
		Appearance bg = new Appearance() {
			@Override
			public void draw(TexasCanvas canvas, TexasPaint textPaint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		Appearance fg = new Appearance() {
			@Override
			public void draw(TexasCanvas canvas, TexasPaint textPaint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		TextStyle textStyle = new TextStyle() {
			@Override
			public void update(@NonNull TexasPaint textPaint, @Nullable Object tag) {

			}
		};
		String tag = "foo";
		return TextSpan.obtain(msg, 0, msg.length(), textStyle, tag, bg, fg);
	}

	@Test
	public void testAppendBox() {
		String msg = "hello";

		TextSpan textBox = create(msg);
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
		textBox = TextSpan.obtain(msg, 1, msg.length() - 1, mTextStyle, mTag, mBg, mFg);
		textBox.measure(mMockMeasurer, mTextAttribute);
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

		TextSpan textBox = create(msg);
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

		Penalty penalty = Penalty.obtain(10, true, null, null);
		penalty.measure(mMockMeasurer, new TextAttribute(mMockMeasurer));
		textBox.merge(penalty);

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
		textBox = TextSpan.obtain(msg, 1, msg.length() - 1, mTextStyle, mTag, mBg, mFg);
		textBox.measure(mMockMeasurer, mTextAttribute);
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

		textBox.merge(penalty);
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
			textBox.merge(penalty);
			Assert.fail();
		} catch (Throwable t) {

		}
	}

	@Test
	public void testClean() {
		if (TextSpan.hasBuffered()) {
			TextSpan.clean();
		}

		Assert.assertFalse(TextSpan.hasBuffered());
		TextSpan textBox = create("hello");
		Assert.assertNotNull(textBox);

		Assert.assertFalse(textBox.isRecycled());
		textBox.recycle();
		Assert.assertTrue(textBox.isRecycled());

		TextSpan.clean();
		Assert.assertFalse(TextSpan.hasBuffered());
	}

	@Test
	public void testObtainAndRecycle() {
		TextSpan.clean();

		String msg = "hello";
		TextSpan textBox = TextSpan.obtain(msg, 0, msg.length(), mTextStyle, mTag, mBg, mFg);
		textBox.measure(mMockMeasurer, mTextAttribute);
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
		Assert.assertEquals(textBox.getAttribute(), TextSpan.ATTRIBUTE_MEASURED);
		float width = textBox.getWidth() / TextSpan.SQUISH_FACTOR;
		textBox.addAttribute(TextSpan.ATTRIBUTE_SQUISH_LEFT);
		Assert.assertNotEquals(textBox.getAttribute(), TextSpan.ATTRIBUTE_NONE);
		Assert.assertTrue(textBox.hasAttribute(TextSpan.ATTRIBUTE_SQUISH_LEFT));
		Assert.assertFalse(textBox.hasAttribute(TextSpan.ATTRIBUTE_ZOOM_OUT));
		Assert.assertEquals(width, textBox.getWidth(), 0.1f);

		TextSpan prev1 = textBox;

		// test obtain not recycle
		String subStr = "ell";
		textBox = TextSpan.obtain(msg, 1, msg.length() - 1, mTextStyle, mTag, mBg, mFg);
		textBox.measure(mMockMeasurer, mTextAttribute);
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
		float height = textBox.getHeight() * TextSpan.ZOOM_OUT_FACTOR;
		width = textBox.getWidth() * TextSpan.ZOOM_OUT_FACTOR;
		textBox.addAttribute(TextSpan.ATTRIBUTE_ZOOM_OUT);
		Assert.assertFalse(textBox.hasAttribute(TextSpan.ATTRIBUTE_SQUISH_LEFT));
		Assert.assertTrue(textBox.hasAttribute(TextSpan.ATTRIBUTE_ZOOM_OUT));
		Assert.assertEquals(width, textBox.getWidth(), 0.1f);
		Assert.assertEquals(height, textBox.getHeight(), 0.1f);

		Penalty penalty = Penalty.obtain(0, true, null, null);
		penalty.measure(mMockMeasurer, mTextAttribute);
		Assert.assertFalse(textBox.isPenalty());
		textBox.merge(penalty);
		Assert.assertTrue(textBox.isPenalty());
		Assert.assertFalse(textBox.isRecycled());

		textBox.recycle();
		TestUtils.testRecycled(textBox);
		Assert.assertFalse(textBox.isPenalty());
		Assert.assertEquals(textBox.getAttribute(), TextSpan.ATTRIBUTE_NONE);
		Assert.assertTrue(textBox.isRecycled());

		TextSpan prev2 = textBox;

		// test obtain after recycle
		Appearance bg = new Appearance() {
			@Override
			public void draw(TexasCanvas canvas, TexasPaint textPaint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		Appearance fg = new Appearance() {
			@Override
			public void draw(TexasCanvas canvas, TexasPaint textPaint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		TextStyle textStyle = new TextStyle() {
			@Override
			public void update(@NonNull TexasPaint textPaint, @Nullable Object tag) {

			}
		};
		String tag = "foo";
		textBox = TextSpan.obtain(msg, 0, msg.length(), textStyle, tag, bg, fg);
		textBox.measure(mMockMeasurer, mTextAttribute);
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

		TextSpan lhs = create(msg1);
		TextSpan rhs = createRandom(msg2);

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

		Penalty penalty = Penalty.obtain(1, true, null, null);
		penalty.measure(mMockMeasurer, mTextAttribute);
		rhs.merge(penalty);

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
		lhs.getInnerBounds().set(1, 2, 3, 4);
		lhs.getOuterBounds().set(5, 6, 7, 8);
		rhs.copy(lhs);

		Assert.assertEquals(lhs.toString(), rhs.toString());
		Assert.assertEquals(lhs.getText(), rhs.getText());
		Assert.assertEquals(lhs.getStart(), rhs.getStart());
		Assert.assertEquals(lhs.getEnd(), rhs.getEnd());
		Assert.assertEquals(lhs.getTextStyle(), rhs.getTextStyle());
		Assert.assertEquals(lhs.isPenalty(), rhs.isPenalty());
		Assert.assertEquals(lhs.isRecycled(), rhs.isRecycled());
		Assert.assertEquals(lhs.getInnerBounds(), rhs.getInnerBounds());
		Assert.assertEquals(lhs.getOuterBounds(), rhs.getOuterBounds());
		Assert.assertEquals(new RectF(1, 2, 3, 4), lhs.getInnerBounds());
		Assert.assertEquals(new RectF(5, 6, 7, 8), lhs.getOuterBounds());

		Assert.assertEquals(lhs, rhs);
		Assert.assertTrue(TestUtils.reflectCompare(lhs, rhs, "mId"));
	}

	@Test
	public void testBoxIsolate() {
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);

		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		PaintSet paintSet = new PaintSet(factory.getMockTextPaint());
		TextAttribute textAttribute = new TextAttribute(measurer);

		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption)
				.textGravity(TextGravity.START)
				.text("123 123, trangle");
		Paragraph paragraph = builder.build();
		paragraph.setPadding(new Rect(1, 2, 3, 4));

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		paragraph.measure(measurer, textAttribute);
		texTypesetter.typeset(paragraph, BreakStrategy.BALANCED, 100);

		Layout layout = paragraph.getLayout();
		Assert.assertEquals(1, layout.getLineCount());

		Line line = layout.getLine(0);
		Assert.assertEquals(4, line.getElementCount());

		Span span = (Span) line.getElement(0);
		Assert.assertTrue(box.isIsolate(false));
		Assert.assertTrue(box.isIsolate(true));

		box = (Span) line.getElement(1);
		Assert.assertTrue(box.isIsolate(false));
		Assert.assertFalse(box.isIsolate(true));

		box = (Span) line.getElement(2);
		Assert.assertFalse(box.isIsolate(false));
		Assert.assertTrue(box.isIsolate(true));

		box = (Span) line.getElement(3);
		Assert.assertTrue(box.isIsolate(false));
		Assert.assertTrue(box.isIsolate(true));
	}
}
