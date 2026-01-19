package me.chan.texas.text;

import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;

import me.chan.texas.TestUtils;
import me.chan.texas.di.FakeMeasureFactory;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.test.mock.MockTextPaint;

import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.DrawableBox;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.Penalty;
import me.chan.texas.text.layout.SymbolGlue;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.text.tokenizer.Token;
import me.chan.texas.text.util.TexasIterator;
import me.chan.texas.typesetter.ParagraphTypesetter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ParagraphUnitTest {

	private Measurer mMeasurer;
	private TextAttribute mTextAttribute;
	private PaintSet mPaintSet;


	@Before
	public void setup() {
		MockTextPaint mockTextPaint = new MockTextPaint(20);
		mMeasurer = new MockMeasurer(mockTextPaint);
		mTextAttribute = new TextAttribute(mMeasurer);
		mPaintSet = new PaintSet(mockTextPaint);
	}

	@Test
	public void testIterator() {
		Paragraph paragraph = Paragraph.obtain();
		Layout layout = Layout.obtain();
		paragraph.swap(layout);

		Line l1 = Line.obtain();
		Line l2 = Line.obtain();
		Line l3 = Line.obtain();

		layout.addLine(l1);
		layout.addLine(l2);
		layout.addLine(l3);

		TexasIterator<Line> iterator = paragraph.iterator();

		Assert.assertNull(iterator.current());
		Assert.assertNull(iterator.prev());

		Assert.assertSame(l1, iterator.next());
		Assert.assertSame(l1, iterator.current());
		Assert.assertNull(iterator.prev());

		Assert.assertSame(l2, iterator.next());
		Assert.assertSame(l2, iterator.current());
		Assert.assertSame(l1, iterator.prev());

		// test prev
		int state = iterator.save();
		Assert.assertSame(l2, iterator.next());
		Assert.assertSame(l2, iterator.current());
		Assert.assertSame(l1, iterator.prev());
		iterator.next();

		Assert.assertSame(l3, iterator.next());
		Assert.assertSame(l3, iterator.current());
		Assert.assertNull(iterator.next());

		Assert.assertSame(l1, iterator.restore(state));
		Assert.assertSame(l2, iterator.next());
		Assert.assertSame(l2, iterator.current());
		Assert.assertSame(l1, iterator.prev());
	}

	@Test
	public void testSpan() {
		RectGround background = new RectGround(10);
		DotUnderLine dotUnderLine = new DotUnderLine(10);
		String tag = "msg";
		Paragraph.Span span = Paragraph.Span.obtain(tag, 0, tag.length()).setBackground(background).setForeground(dotUnderLine).tag(tag).setTextStyle(TextStyle.BOLD);
		Assert.assertSame(span.getTag(), tag);
		Assert.assertSame(span.getBackground(), background);
		Assert.assertSame(span.getForeground(), dotUnderLine);
		Assert.assertSame(span.getTextStyle(), TextStyle.BOLD);
		Assert.assertEquals(span.toString(), tag);


		Paragraph.Span.clean();
		span.recycle();

		Paragraph.Span span1 = span;
		TestUtils.testRecycled(span);

		background = new RectGround(10);
		dotUnderLine = new DotUnderLine(10);
		Object object = "fuck";
		span = Paragraph.Span.obtain(tag, 0, tag.length() - 1).setBackground(background).setForeground(dotUnderLine).tag(object).setTextStyle(TextStyle.BOLD_ITALIC);
		Assert.assertSame(span1, span);
		Assert.assertSame(span.getTag(), object);
		Assert.assertSame(span.getBackground(), background);
		Assert.assertSame(span.getForeground(), dotUnderLine);
		Assert.assertSame(span.getTextStyle(), TextStyle.BOLD_ITALIC);
		Assert.assertEquals(span.toString(), "ms");

		span.recycle();
		TestUtils.testRecycled(span);
	}

	@Test
	public void testBuilder() {
		String msg = "hello";
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption().setLineSpacingExtra(1));
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		builder.lineSpacingExtra(2);
		Paragraph paragraph = builder.build();
		Layout layout = paragraph.getLayout();
		Layout.Advise advise = layout.getAdvise();
		Assert.assertEquals(advise.getLineSpacingExtra(), 2, 0);
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
		builder = Paragraph.Builder.newBuilder(texasOption);
		Assert.assertSame(p, builder);
		Assert.assertNotSame(builder, Paragraph.Builder.newBuilder(texasOption));

		ColorDrawable colorDrawable = new ColorDrawable(10);
		Emoticon emoticon = Emoticon.obtain(colorDrawable, 1, 2);
		builder.emoticon(emoticon);
		builder.text(msg);

		RectGround background = new RectGround(10);
		DotUnderLine dotUnderLine = new DotUnderLine(10);
		String tag = "msg";
		Paragraph.SpanBuilder spanBuilder = builder.newSpanBuilder()
				.next("triangle")
				.setBackground(background)
				.setForeground(dotUnderLine)
				.tag(tag)
				.setTextStyle(TextStyle.BOLD);
		spanBuilder.buildSpan();

		builder.newSpanBuilder().next("ok").buildSpan();

		paragraph = builder.build();
		paragraph.measure(mMeasurer, mTextAttribute);
		layout = paragraph.getLayout();
		advise = layout.getAdvise();
		Assert.assertEquals(advise.getLineSpacingExtra(), 1, 0);
		Assert.assertEquals(paragraph.getElementCount(), 13);
		DrawableBox drawableBox = (DrawableBox) paragraph.getElement(0);
		Emoticon hypeSpan = (Emoticon) drawableBox.getSpan();
		Assert.assertSame(hypeSpan.getDrawable(), colorDrawable);
		Glue glue = (Glue) paragraph.getElement(1);
		glue.measure(mMeasurer, mTextAttribute);
		Assert.assertEquals(glue.getWidth(), mTextAttribute.getSpaceWidth(), 0);
		Assert.assertEquals(glue.getShrink(), mTextAttribute.getSpaceShrink(), 0);
		Assert.assertEquals(glue.getStretch(), mTextAttribute.getSpaceStretch(), 0);

		Measurer.CharSequenceSpec spec = Measurer.CharSequenceSpec.obtain();
		mMeasurer.measure(msg, 0, msg.length(), null, null, spec);
		TextBox textBox = (TextBox) paragraph.getElement(2);
		Assert.assertNull(textBox.getBackground());
		Assert.assertNull(textBox.getForeground());
		Assert.assertNull(textBox.getTextStyle());
		Assert.assertEquals(textBox.getHeight(), spec.getHeight(), 0);
		Assert.assertEquals(textBox.getWidth(), spec.getWidth(), 0);

		glue = (Glue) paragraph.getElement(3);
		Assert.assertEquals(glue.getWidth(), mTextAttribute.getSpaceWidth(), 0);
		Assert.assertEquals(glue.getShrink(), mTextAttribute.getSpaceShrink(), 0);
		Assert.assertEquals(glue.getStretch(), mTextAttribute.getSpaceStretch(), 0);

		String[] strings = new String[]{"tri", "an", "gle"};
		for (int i = 0; i < 3; ++i) {
			mMeasurer.measure(strings[i], 0, strings[i].length(), null, null, spec);
			textBox = (TextBox) paragraph.getElement(4 + i * 2);
			Assert.assertSame(textBox.getBackground(), background);
			Assert.assertSame(textBox.getForeground(), dotUnderLine);
			Assert.assertSame(textBox.getTextStyle(), TextStyle.BOLD);
			Assert.assertEquals(textBox.getHeight(), spec.getHeight(), 0);
			Assert.assertEquals(textBox.getWidth(), spec.getWidth(), 0);
			Assert.assertSame(textBox.getTag(), tag);

			int next = 5 + i * 2;
			if (next == 9) {
				continue;
			}

			mMeasurer.measure("-", 0, 1, null, null, spec);
			Penalty penalty = (Penalty) paragraph.getElement(5 + i * 2);
			Assert.assertEquals(penalty.getWidth(), mTextAttribute.getHyphenWidth(), 0);
			Assert.assertEquals(penalty.getHeight(), spec.getHeight(), 0);
			Assert.assertEquals(penalty.getPenalty(), Texas.HYPHEN_PENALTY, 0);
			Assert.assertTrue(penalty.isFlag());
		}

		glue = (Glue) paragraph.getElement(9);
		Assert.assertEquals(glue.getWidth(), mTextAttribute.getSpaceWidth(), 0);
		Assert.assertEquals(glue.getStretch(), mTextAttribute.getSpaceStretch(), 0);
		Assert.assertEquals(glue.getShrink(), mTextAttribute.getSpaceShrink(), 0);

		textBox = (TextBox) paragraph.getElement(10);
		Assert.assertEquals(textBox.toString(), "ok");
		Assert.assertNull(textBox.getTag());

		glue = (Glue) paragraph.getElement(11);
		Assert.assertEquals(glue.getWidth(), 0, 0);
		Assert.assertEquals(glue.getStretch(), Texas.INFINITY_PENALTY, 0);
		Assert.assertEquals(glue.getShrink(), 0, 0);

		Penalty penalty = (Penalty) paragraph.getElement(12);
		Assert.assertEquals(penalty.getWidth(), 0, 0);
		Assert.assertEquals(penalty.getHeight(), 0, 0);
		Assert.assertEquals(penalty.getPenalty(), -Texas.INFINITY_PENALTY, 0);
		Assert.assertTrue(penalty.isFlag());
	}

	private int mIndex = 0;

	@Test
	public void testStream() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		mIndex = 0;
		String[] arr = {"hello", ",", "world", ".", ".", ".", "fuck", "he's", "name"};
		String msg = " hello, world... \n\t\r fuck he's name";
		builder.stream(msg, 0, msg.length(), token -> {
			String s = (String) msg.subSequence(token.getStart(), token.getEnd());
			System.out.println(s);
			Assert.assertEquals(arr[mIndex++], s);
			return null;
		});

		Assert.assertEquals(arr.length, mIndex);
	}

	@Test
	public void testNewline() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption)
				.setTypesetPolicy(Paragraph.TYPESET_POLICY_ACCEPT_CONTROL_CHAR);

		mIndex = 0;
		String msg = " hello \n\t\r world";
		String[] arr = {"hello", "world"};
		builder.stream(msg, 0, msg.length(), token -> {
			String s = (String) msg.subSequence(token.getStart(), token.getEnd());
			System.out.println(s);
			Assert.assertEquals(arr[mIndex++], s);
			return null;
		});

		Assert.assertEquals(arr.length, mIndex);

		Paragraph paragraph = builder.build();
		Assert.assertEquals(6, paragraph.getElementCount());
		Assert.assertEquals("hello", paragraph.getElement(0).toString());
		Assert.assertSame(Glue.TERMINAL, paragraph.getElement(1));
		Assert.assertSame(Penalty.FORCE_BREAK, paragraph.getElement(2));
		Assert.assertEquals("world", paragraph.getElement(3).toString());
		Assert.assertSame(Glue.TERMINAL, paragraph.getElement(4));
		Assert.assertSame(Penalty.FORCE_BREAK, paragraph.getElement(5));
	}

	@Test
	public void testTextPenaltyWithTag() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		mIndex = 0;
		String msg = "bite-size";
		builder.stream(msg, 0, msg.length(), token -> Paragraph.Span.obtain(msg, token.getStart(), token.getEnd()).tag("fuck"));

		boolean found = false;
		Paragraph paragraph = builder.build();
		for (int i = 0; i < paragraph.getElementCount(); ++i) {
			Element element = paragraph.getElement(i);
			if (element.toString().equals("bite-")) {
				found = true;
				continue;
			}

			if (found) {
				Assert.assertTrue(element instanceof Penalty);
				Penalty penalty = (Penalty) element;
				Assert.assertFalse(penalty.isFlag());
				return;
			}
		}

		Assert.fail();
	}

	@Test
	public void testTextPenaltyWithoutTag() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		mIndex = 0;
		String msg = "bite-size";
		builder.stream(msg, 0, msg.length(), token -> null);

		boolean found = false;
		Paragraph paragraph = builder.build();
		for (int i = 0; i < paragraph.getElementCount(); ++i) {
			Element element = paragraph.getElement(i);
			if (element.toString().equals("bite-")) {
				found = true;
				continue;
			}

			if (found) {
				Assert.assertTrue(element instanceof Penalty);
				Penalty penalty = (Penalty) element;
				Assert.assertFalse(penalty.isFlag());
				return;
			}
		}

		Assert.fail();
	}

	@Test
	public void testWordSent() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Glue blank = Glue.obtain();

		{
			// case 1
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("don't.");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "don't", Penalty.FORBIDDEN_BREAK, ".", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("你好yes");
			paragraph = builder.build();
			checkContent(paragraph, "你", Penalty.ADVISE_BREAK, "好", blank, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);
		}

		{
			// case 2
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("😜😜yes");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "😜", Penalty.ADVISE_BREAK, "😜", blank, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);
		}

		{
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			// case 4
			builder.text("hello");
			// case 3
			builder.text("world");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "hello", blank, "world", Glue.TERMINAL, Penalty.FORCE_BREAK);
		}

		{
			// case 5
			{
				Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
				builder.text("?yes");
				Paragraph paragraph = builder.build();
				checkContent(paragraph, "?", blank, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

				builder = Paragraph.Builder.newBuilder(texasOption);
				builder.text("? yes");
				paragraph = builder.build();
				checkContent(paragraph, "?", blank, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);
			}

			{
				Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
				builder.text("》yes");
				Paragraph paragraph = builder.build();
				checkContent(paragraph, "》", SymbolGlue.class, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

				builder = Paragraph.Builder.newBuilder(texasOption);
				builder.text("》 yes");
				paragraph = builder.build();
				checkContent(paragraph, "》", SymbolGlue.class, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);
			}

			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("《yes");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "《", Penalty.FORBIDDEN_BREAK, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("\"yes");
			paragraph = builder.build();
			checkContent(paragraph, "\"", Penalty.ADVISE_BREAK, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("\" yes");
			paragraph = builder.build();
			checkContent(paragraph, "\"", blank, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);
		}
	}

	@Test
	public void testSymbolSent() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Glue blank = Glue.obtain();

		testSymbolSent2();

		testSymbolSent1();

		{
			// none
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("\"😜😜");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "\"", Penalty.ADVISE_BREAK, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("\"yes");
			paragraph = builder.build();
			checkContent(paragraph, "\"", Penalty.ADVISE_BREAK, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("\"《");
			paragraph = builder.build();
			checkContent(paragraph, "\"", Penalty.ADVISE_BREAK, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("hello");
			builder.text("\" ");
			paragraph = builder.build();
			checkContent(paragraph, "hello", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, "\"", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("\"");
			paragraph = builder.build();
			checkContent(paragraph, "\"", Glue.TERMINAL, Penalty.FORCE_BREAK);
		}
	}

	private void testSymbolSent1() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Glue blank = Glue.obtain();

		{
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(">《");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, ">", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);
			TextBox box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(4);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));


			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("><");
			paragraph = builder.build();
			checkContent(paragraph, ">", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, "<", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(4);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(">>");
			paragraph = builder.build();
			checkContent(paragraph, ">", Penalty.FORBIDDEN_BREAK, ">", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("> >");
			paragraph = builder.build();
			checkContent(paragraph, ">", Penalty.FORBIDDEN_BREAK, ">", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(">》");
			paragraph = builder.build();
			checkContent(paragraph, ">", Penalty.FORBIDDEN_BREAK, "》", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT));

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("> 》");
			paragraph = builder.build();
			checkContent(paragraph, ">", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, "》", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(4);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT));
		}

		{
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("》《");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "》", SymbolGlue.class, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);
			TextBox box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), TextBox.ATTRIBUTE_SQUISH_RIGHT);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));


			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("》<");
			paragraph = builder.build();
			checkContent(paragraph, "》", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, "<", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), TextBox.ATTRIBUTE_SQUISH_RIGHT);
			box = (TextBox) paragraph.getElement(4);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("》>");
			paragraph = builder.build();
			checkContent(paragraph, "》", Penalty.FORBIDDEN_BREAK, ">", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), TextBox.ATTRIBUTE_SQUISH_RIGHT);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("》 >");
			paragraph = builder.build();
			checkContent(paragraph, "》", Penalty.FORBIDDEN_BREAK, ">", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), TextBox.ATTRIBUTE_SQUISH_RIGHT);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("》》");
			paragraph = builder.build();
			checkContent(paragraph, "》", Penalty.FORBIDDEN_BREAK, "》", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), TextBox.ATTRIBUTE_SQUISH_RIGHT);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT));

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("》 》");
			paragraph = builder.build();
			checkContent(paragraph, "》", Penalty.FORBIDDEN_BREAK, "》", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), TextBox.ATTRIBUTE_SQUISH_RIGHT);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT));
		}

		{
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("<《");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "<", Penalty.FORBIDDEN_BREAK, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);
			TextBox box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));


			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("<<");
			paragraph = builder.build();
			checkContent(paragraph, "<", Penalty.FORBIDDEN_BREAK, "<", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("<>");
			paragraph = builder.build();
			checkContent(paragraph, "<", Penalty.FORBIDDEN_BREAK, ">", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("< >");
			paragraph = builder.build();
			checkContent(paragraph, "<", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, ">", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(4);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("<》");
			paragraph = builder.build();
			checkContent(paragraph, "<", Penalty.FORBIDDEN_BREAK, "》", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(2);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT));

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("< 》");
			paragraph = builder.build();
			checkContent(paragraph, "<", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, "》", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
			box = (TextBox) paragraph.getElement(4);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT));
		}

		{
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("《《");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "《", Penalty.FORBIDDEN_BREAK, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);
			TextBox box = (TextBox) paragraph.getElement(0);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));
			box = (TextBox) paragraph.getElement(2);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));


			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("《<");
			paragraph = builder.build();
			checkContent(paragraph, "《", Penalty.FORBIDDEN_BREAK, "<", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));
			box = (TextBox) paragraph.getElement(2);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("《>");
			paragraph = builder.build();
			checkContent(paragraph, "《", Penalty.FORBIDDEN_BREAK, ">", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));
			box = (TextBox) paragraph.getElement(2);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("《 >");
			paragraph = builder.build();
			checkContent(paragraph, "《", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, ">", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));
			box = (TextBox) paragraph.getElement(4);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("《》");
			paragraph = builder.build();
			checkContent(paragraph, "《", Penalty.FORBIDDEN_BREAK, "》", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));
			box = (TextBox) paragraph.getElement(2);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT));

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("《 》");
			paragraph = builder.build();
			checkContent(paragraph, "《", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, "》", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));
			box = (TextBox) paragraph.getElement(4);
			Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT));
		}

		{
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("《");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);
			TextBox box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), TextBox.ATTRIBUTE_SQUISH_LEFT);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("》");
			paragraph = builder.build();
			checkContent(paragraph, "》", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), TextBox.ATTRIBUTE_SQUISH_RIGHT);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("<");
			paragraph = builder.build();
			checkContent(paragraph, "<", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(">");
			paragraph = builder.build();
			checkContent(paragraph, ">", Glue.TERMINAL, Penalty.FORCE_BREAK);
			box = (TextBox) paragraph.getElement(0);
			Assert.assertEquals(box.getAttribute(), 0);
		}

		// avoid head + 收缩右
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("》《");
		Paragraph paragraph = builder.build();
		checkContent(paragraph, "》", SymbolGlue.class, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);
		TextBox box = (TextBox) paragraph.getElement(0);
		Assert.assertEquals(box.getAttribute(), TextBox.ATTRIBUTE_SQUISH_RIGHT);
		box = (TextBox) paragraph.getElement(2);
		Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));

		// avoid tail + 收缩左
		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("》,");
		paragraph = builder.build();
		checkContent(paragraph, "》", Penalty.FORBIDDEN_BREAK, ",", Glue.TERMINAL, Penalty.FORCE_BREAK);

		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("《,");
		paragraph = builder.build();
		checkContent(paragraph, "《", Penalty.FORBIDDEN_BREAK, ",", Glue.TERMINAL, Penalty.FORCE_BREAK);
		box = (TextBox) paragraph.getElement(0);
		Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));
		box = (TextBox) paragraph.getElement(2);
		Assert.assertEquals(box.getAttribute(), TextBox.ATTRIBUTE_NONE);

		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("《 ,");
		paragraph = builder.build();
		checkContent(paragraph, "《", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, ",", Glue.TERMINAL, Penalty.FORCE_BREAK);
		box = (TextBox) paragraph.getElement(0);
		Assert.assertTrue(box.hasAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT));
		box = (TextBox) paragraph.getElement(4);
		Assert.assertEquals(box.getAttribute(), TextBox.ATTRIBUTE_NONE);
	}

	private void testSymbolSent2() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Glue blank = Glue.obtain();

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("no》yes");
		Paragraph paragraph = builder.build();
		checkContent(paragraph, "no", Penalty.FORBIDDEN_BREAK, "》", SymbolGlue.class, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("no 》yes");
		paragraph = builder.build();
		checkContent(paragraph, "no", Penalty.FORBIDDEN_BREAK, "》", SymbolGlue.class, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

		// avoid tail + 收缩左
		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("no《yes");
		paragraph = builder.build();
		checkContent(paragraph, "no", SymbolGlue.class, "《", Penalty.FORBIDDEN_BREAK, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("no 《yes");
		paragraph = builder.build();
		checkContent(paragraph, "no", SymbolGlue.class, "《", Penalty.FORBIDDEN_BREAK, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("no #yes");
		paragraph = builder.build();
		checkContent(paragraph, "no", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, "#", Penalty.ADVISE_BREAK, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);
	}

	@Test
	public void testUnknownSent() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Glue blank = Glue.obtain();

		{
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			// case 4
			builder.text("😜😜");
			// case 3
			builder.text("😜😜");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "😜", Penalty.ADVISE_BREAK, "😜", blank, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);
		}

		{
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			// case 1
			builder.text("yes😜😜");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "yes", blank, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);
		}

		// case 2 不存在
		{
			// case 5
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("》😜😜");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "》", SymbolGlue.class, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("?😜😜");
			paragraph = builder.build();
			checkContent(paragraph, "?", blank, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("《😜😜");
			paragraph = builder.build();
			checkContent(paragraph, "《", Penalty.FORBIDDEN_BREAK, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("\"😜😜");
			paragraph = builder.build();
			checkContent(paragraph, "\"", Penalty.ADVISE_BREAK, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);
		}
//
//		{
//			String msg = "……";
//			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
//			builder.text(msg);
//			builder.stream(msg, new Paragraph.Builder.SpanReader() {
//				@Override
//				public Paragraph.Span read(CharSequence text, int start, int end) {
//					return Paragraph.Span.obtain(text, start, end);
//				}
//			});
//			Paragraph paragraph = builder.build();
//			checkContent(paragraph, "》", SymbolGlue.class, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);
//		}
	}

	@Test
	public void testBlankSent() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Glue blank = Glue.obtain();

		{
			// word
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("yes yes");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "yes", blank, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("yes 《");
			paragraph = builder.build();
			checkContent(paragraph, "yes", SymbolGlue.class, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("yes").text("《");
			paragraph = builder.build();
			checkContent(paragraph, "yes", SymbolGlue.class, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("yes ?");
			paragraph = builder.build();
			checkContent(paragraph, "yes", Penalty.FORBIDDEN_BREAK, "?", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("yes").text("?");
			paragraph = builder.build();
			checkContent(paragraph, "yes", Penalty.FORBIDDEN_BREAK, "?", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("yes  ");
			paragraph = builder.build();
			checkContent(paragraph, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("yes ");
			paragraph = builder.build();
			checkContent(paragraph, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);
		}

		{
			// unknown
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("😜😜 😜😜");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "😜", Penalty.ADVISE_BREAK, "😜", blank, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("😜😜 yes");
			paragraph = builder.build();
			checkContent(paragraph, "😜", Penalty.ADVISE_BREAK, "😜", blank, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("😜😜 《");
			paragraph = builder.build();
			checkContent(paragraph, "😜", Penalty.ADVISE_BREAK, "😜", SymbolGlue.class, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);
			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("😜😜 ?");
			paragraph = builder.build();
			checkContent(paragraph, "😜", Penalty.ADVISE_BREAK, "😜", Penalty.FORBIDDEN_BREAK, "?", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("😜😜  ");
			paragraph = builder.build();
			checkContent(paragraph, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("😜😜 ");
			paragraph = builder.build();
			checkContent(paragraph, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);
		}

		{
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("no》 yes");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "no", Penalty.FORBIDDEN_BREAK, "》", SymbolGlue.class, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			// avoid head + 拉伸右
			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("no> yes");
			paragraph = builder.build();
			checkContent(paragraph, "no", Penalty.FORBIDDEN_BREAK, ">", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			// simple
			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("no- yes");
			paragraph = builder.build();
			checkContent(paragraph, "no", Penalty.FORBIDDEN_BREAK, "-", blank, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			// avoid tail + 收缩左
			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("no 《yes");
			paragraph = builder.build();
			checkContent(paragraph, "no", SymbolGlue.class, "《", Penalty.FORBIDDEN_BREAK, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			// avoid tail + 拉伸左
			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("no <yes");
			paragraph = builder.build();
			checkContent(paragraph, "no", Penalty.FORBIDDEN_BREAK, blank, Penalty.FORBIDDEN_BREAK, "<", Penalty.FORBIDDEN_BREAK, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("no: yes");
			paragraph = builder.build();
			checkContent(paragraph, "no", Penalty.FORBIDDEN_BREAK, ":", blank, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(", 😜😜");
			paragraph = builder.build();
			checkContent(paragraph, ",", blank, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(", yes");
			paragraph = builder.build();
			checkContent(paragraph, ",", blank, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(", 《");
			paragraph = builder.build();
			checkContent(paragraph, ",", blank, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(",  ");
			paragraph = builder.build();
			checkContent(paragraph, ",", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(", ");
			paragraph = builder.build();
			checkContent(paragraph, ",", Glue.TERMINAL, Penalty.FORCE_BREAK);
		}

		{
			// blank
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("  😜😜");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("  yes");
			paragraph = builder.build();
			checkContent(paragraph, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("  《");
			paragraph = builder.build();
			checkContent(paragraph, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("   ");
			paragraph = builder.build();
			Assert.assertEquals(paragraph.getElementCount(), 2);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("  ");
			paragraph = builder.build();
			Assert.assertEquals(paragraph.getElementCount(), 2);
		}

		{
			// none
			Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(" 😜😜");
			Paragraph paragraph = builder.build();
			checkContent(paragraph, "😜", Penalty.ADVISE_BREAK, "😜", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(" yes");
			paragraph = builder.build();
			checkContent(paragraph, "yes", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(" 《");
			paragraph = builder.build();
			checkContent(paragraph, "《", Glue.TERMINAL, Penalty.FORCE_BREAK);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text("  ");
			paragraph = builder.build();
			Assert.assertEquals(paragraph.getElementCount(), 2);

			builder = Paragraph.Builder.newBuilder(texasOption);
			builder.text(" ");
			paragraph = builder.build();
			Assert.assertEquals(paragraph.getElementCount(), 2);
		}
	}

	private static void checkContent(Paragraph paragraph, Object... elements) {
		Assert.assertEquals(paragraph.getElementCount(), elements.length);
		for (int i = 0; i < elements.length; ++i) {
			if (elements[i] instanceof String) {
				TextBox box = (TextBox) paragraph.getElement(i);
				Assert.assertEquals(elements[i], box.toString());
			} else if (elements[i] == SymbolGlue.class) {
				Assert.assertTrue(paragraph.getElement(i) instanceof SymbolGlue);
			} else if (elements[i] instanceof Glue) {
				Glue glue = (Glue) paragraph.getElement(i);
				if (elements[i] == Glue.EMPTY || elements[i] == Glue.TERMINAL) {
					Assert.assertSame(glue, elements[i]);
				}
			} else {
				Penalty penalty = (Penalty) paragraph.getElement(i);
				if (elements[i] == Penalty.FORBIDDEN_BREAK || elements[i] == Penalty.FORCE_BREAK || elements[i] == Penalty.ADVISE_BREAK) {
					Assert.assertSame(penalty, elements[i]);
				}
			}
		}
	}

	@Test
	public void testParagraph() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		String msg = "xxx";
		builder.tag(msg);
		builder.text("hello");
		Paragraph paragraph = builder.build();
		Layout layout = paragraph.getLayout();
		Layout.Advise advise = layout.getAdvise();
		Assert.assertSame(msg, paragraph.getTag());
		Assert.assertEquals(advise.getLineSpacingExtra(), 0, 0);
		Assert.assertEquals(layout.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElementCount(), 3);

		Line line1 = Line.obtain();
		Line line2 = Line.obtain();
		Line line3 = Line.obtain();
		layout.addLine(line1);
		layout.addLine(line2);
		layout.addLine(line3);

		Assert.assertEquals(layout.getLineCount(), 3);
		Assert.assertSame(layout.getLine(0), line1);
		Assert.assertSame(layout.getLine(1), line2);
		Assert.assertSame(layout.getLine(2), line3);


		paragraph.recycle();
		Assert.assertEquals(layout.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElementCount(), 0);
		Assert.assertNull(paragraph.getTag());

		// check recycle twice
		paragraph.recycle();

		Paragraph paragraph1 = paragraph;
		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("triangle");
		paragraph = builder.build();
		layout = paragraph.getLayout();
		Assert.assertNotSame(paragraph, paragraph1);
		Assert.assertNull(paragraph.getTag());
		Assert.assertEquals(layout.getLineCount(), 0);
		Assert.assertEquals(paragraph.getElementCount(), 7);

		Assert.assertNotSame(paragraph, Paragraph.Builder.newBuilder(texasOption));
	}

	@Test
	public void testSpec() {
		Measurer.CharSequenceSpec spec = Measurer.CharSequenceSpec.obtain();
		Assert.assertNotNull(spec);
		spec.reset(1, 2, 5);
		Assert.assertEquals(spec.getWidth(), 1, 0);
		Assert.assertEquals(spec.getHeight(), 2, 0);
		Assert.assertEquals(spec.getBaselineOffset(), 5, 0);

		spec.recycle();

		TestUtils.testRecycled(spec);

		Measurer.CharSequenceSpec spec1 = Measurer.CharSequenceSpec.obtain();
		Assert.assertSame(spec1, spec);
		spec1.reset(2, 3, 6);
		Assert.assertEquals(spec.getWidth(), 2, 0);
		Assert.assertEquals(spec.getHeight(), 3, 0);
		Assert.assertEquals(spec.getBaselineOffset(), 6, 0);

		Assert.assertNotSame(spec1, Measurer.CharSequenceSpec.obtain());
	}

	@Test
	public void testForDebug() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		builder.text("we can't help it even now, you see — and I shouldn't like elegant society and you would, and you'd hate my scribbling");
		Paragraph paragraph = builder.build();
		Assert.assertNotNull(paragraph);
	}

	@Test
	public void testForDebug2() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		builder.text("cos-triangleok");
		Paragraph paragraph = builder.build();
		Assert.assertNotNull(paragraph);
	}

	@Test
	public void testAppendSpace() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption)
				.text("b")
				.text("c")
				.appendSpaceEnable(false)
				.text("d")
				.appendSpaceEnable(true)
				.text("e")
				.appendSpaceEnable(false);
		Paragraph paragraph = builder.build();
		Assert.assertNotNull(paragraph);

		Assert.assertTrue(paragraph.getElement(0) instanceof TextBox);
		Assert.assertTrue(paragraph.getElement(1) instanceof Glue);
		Assert.assertTrue(paragraph.getElement(2) instanceof TextBox);
		Assert.assertTrue(paragraph.getElement(3) == Penalty.ADVISE_BREAK);
		Assert.assertTrue(paragraph.getElement(4) instanceof TextBox);
		Assert.assertTrue(paragraph.getElement(5) instanceof Glue);
		Assert.assertTrue(paragraph.getElement(6) instanceof TextBox);
		Assert.assertTrue(paragraph.getElement(7) == Glue.TERMINAL);
		Assert.assertTrue(paragraph.getElement(8) == Penalty.FORCE_BREAK);

		Paragraph.Builder builder1 = Paragraph.Builder.newBuilder(texasOption);
		Assert.assertSame(builder1, builder);
		builder1.text("b")
				.text("c");
		paragraph = builder1.build();
		Assert.assertTrue(paragraph.getElement(0) instanceof TextBox);
		Assert.assertTrue(paragraph.getElement(1) instanceof Glue);
		Assert.assertTrue(paragraph.getElement(2) instanceof TextBox);
		Assert.assertTrue(paragraph.getElement(3) == Glue.TERMINAL);
		Assert.assertTrue(paragraph.getElement(4) == Penalty.FORCE_BREAK);
	}

	@Test
	public void testBuilderDisableHyphen() {
		RenderOption renderOption = new RenderOption();
		renderOption.setBreakStrategy(BreakStrategy.SIMPLE);

		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, renderOption);
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("triangle");
		Paragraph paragraph = builder.build();
//		Assert.assertEquals(7, paragraph.getElementCount());
//		Assert.assertEquals("triangle", paragraph.getElement(0).toString());
//		Assert.assertEquals(Glue.TERMINAL, paragraph.getElement(1));
//		Assert.assertEquals(Penalty.FORCE_BREAK, paragraph.getElement(2));
		Assert.assertEquals(7, paragraph.getElementCount());
		Assert.assertEquals("tri", paragraph.getElement(0).toString());
		Penalty penalty = (Penalty) paragraph.getElement(1);
		Assert.assertTrue(penalty.isFlag());
		Assert.assertEquals("an", paragraph.getElement(2).toString());
		penalty = (Penalty) paragraph.getElement(3);
		Assert.assertTrue(penalty.isFlag());
		Assert.assertEquals("gle", paragraph.getElement(4).toString());
		Assert.assertEquals(Glue.TERMINAL, paragraph.getElement(5));
		Assert.assertEquals(Penalty.FORCE_BREAK, paragraph.getElement(6));

		renderOption.setBreakStrategy(BreakStrategy.BALANCED);
		texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, renderOption);
		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("triangle");
		paragraph = builder.build();
		Assert.assertEquals(7, paragraph.getElementCount());
		Assert.assertEquals("tri", paragraph.getElement(0).toString());
		penalty = (Penalty) paragraph.getElement(1);
		Assert.assertTrue(penalty.isFlag());
		Assert.assertEquals("an", paragraph.getElement(2).toString());
		penalty = (Penalty) paragraph.getElement(3);
		Assert.assertTrue(penalty.isFlag());
		Assert.assertEquals("gle", paragraph.getElement(4).toString());
		Assert.assertEquals(Glue.TERMINAL, paragraph.getElement(5));
		Assert.assertEquals(Penalty.FORCE_BREAK, paragraph.getElement(6));

		builder = Paragraph.Builder.newBuilder(texasOption)
				.breakStrategy(BreakStrategy.SIMPLE);
		builder.text("triangle");
		paragraph = builder.build();
//		Assert.assertEquals(3, paragraph.getElementCount());
//		Assert.assertEquals("triangle", paragraph.getElement(0).toString());
//		Assert.assertEquals(Glue.TERMINAL, paragraph.getElement(1));
//		Assert.assertEquals(Penalty.FORCE_BREAK, paragraph.getElement(2));
		Assert.assertEquals(7, paragraph.getElementCount());
		Assert.assertEquals("tri", paragraph.getElement(0).toString());
		penalty = (Penalty) paragraph.getElement(1);
		Assert.assertTrue(penalty.isFlag());
		Assert.assertEquals("an", paragraph.getElement(2).toString());
		penalty = (Penalty) paragraph.getElement(3);
		Assert.assertTrue(penalty.isFlag());
		Assert.assertEquals("gle", paragraph.getElement(4).toString());
		Assert.assertEquals(Glue.TERMINAL, paragraph.getElement(5));
		Assert.assertEquals(Penalty.FORCE_BREAK, paragraph.getElement(6));
	}

	@Test
	public void testTextBoxSpanAttributes() {
		TextStyle expectedTextStyle = TextStyle.BOLD;
		Object expectedTag = "testTag";
		Appearance expectedBackground = new RectGround(10);
		Appearance expectedForeground = new DotUnderLine(10);

		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		builder.stream("你好", new Paragraph.Builder.SpanReader() {
			@Override
			public Paragraph.Span read(Token token) {
				return Paragraph.Span.obtain(token)
						.tag(expectedTag)
						.setTextStyle(expectedTextStyle)
						.setBackground(expectedBackground)
						.setForeground(expectedForeground);
			}
		});
		Paragraph paragraph = builder.build();

		for (int i = 0; i < paragraph.getElementCount(); i++) {
			Element element = paragraph.getElement(i);
			if (element instanceof TextBox) {
				TextBox textBox = (TextBox) element;
				Assert.assertEquals(expectedTag, textBox.getTag());
				Assert.assertEquals(expectedBackground, textBox.getBackground());
				Assert.assertEquals(expectedForeground, textBox.getForeground());
			}
		}
	}

	@Test
	public void testHasContent() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		Paragraph paragraph = builder.build();
		Assert.assertFalse(paragraph.hasContent());

		builder = Paragraph.Builder.newBuilder(texasOption);
		paragraph = builder.build(false);
		Assert.assertFalse(paragraph.hasContent());

		builder = Paragraph.Builder.newBuilder(texasOption)
				.text("hello");
		paragraph = builder.build(false);
		Assert.assertTrue(paragraph.hasContent());

		builder = Paragraph.Builder.newBuilder(texasOption)
				.text("hello")
				.text("world");
		paragraph = builder.build(false);
		Assert.assertTrue(paragraph.hasContent());

		builder = Paragraph.Builder.newBuilder(texasOption)
				.text("hello");
		paragraph = builder.build();
		Assert.assertTrue(paragraph.hasContent());
	}

	@Test
	public void testRendererContext() throws ParagraphVisitor.VisitException {
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);

		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());

		TexasOption texasOption = new TexasOption(new PaintSet(factory.getMockTextPaint()), Hyphenation.getInstance(), measurer, new TextAttribute(measurer), new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		Paragraph paragraph = builder
				.text("1 2 3")
				.brk()
				.text("4 5 6")
				.brk()
				.text("7 8 9")
				.brk()
				.build();

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();

		paragraph.measure(measurer, new TextAttribute(measurer));
		texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 10);

		Layout layout = paragraph.getLayout();
		Assert.assertEquals(3, layout.getLineCount());

		ParagraphVisitor visitor = new ParagraphVisitor() {
			@Override
			protected void onVisitParagraphStart(Paragraph paragraph) {

			}

			@Override
			protected void onVisitParagraphEnd(Paragraph paragraph) {

			}

			@Override
			protected void onVisitLineStart(Line line, float x, float y) {

			}

			@Override
			protected void onVisitLineEnd(Line line, float x, float y) {

			}

			@Override
			protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
				Assert.assertSame(box, context.getBox());
				if ("1".equals(box.toString())) {
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_LINE_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_MIDDLE));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_MIDDLE));
					Assert.assertTrue(box.isIsolate(true));
					Assert.assertTrue(box.isIsolate(false));
					Assert.assertEquals(0, context.getIndex());
				} else if ("2".equals(box.toString())) {
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_END));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_LINE_MIDDLE));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_MIDDLE));
					Assert.assertTrue(box.isIsolate(true));
					Assert.assertTrue(box.isIsolate(false));
					Assert.assertEquals(1, context.getIndex());
				} else if ("3".equals(box.toString())) {
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_START));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_LINE_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_MIDDLE));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_MIDDLE));
					Assert.assertTrue(box.isIsolate(true));
					Assert.assertTrue(box.isIsolate(false));
					Assert.assertEquals(2, context.getIndex());
				} else if ("4".equals(box.toString())) {
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_LINE_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_MIDDLE));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_END));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_MIDDLE));
					Assert.assertTrue(box.isIsolate(true));
					Assert.assertTrue(box.isIsolate(false));
					Assert.assertEquals(0, context.getIndex());
				} else if ("5".equals(box.toString())) {
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_END));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_LINE_MIDDLE));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_END));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_MIDDLE));
					Assert.assertTrue(box.isIsolate(true));
					Assert.assertTrue(box.isIsolate(false));
					Assert.assertEquals(1, context.getIndex());
				} else if ("6".equals(box.toString())) {
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_START));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_LINE_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_MIDDLE));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_END));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_MIDDLE));
					Assert.assertTrue(box.isIsolate(true));
					Assert.assertTrue(box.isIsolate(false));
					Assert.assertEquals(2, context.getIndex());
				} else if ("7".equals(box.toString())) {
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_LINE_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_MIDDLE));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_START));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_MIDDLE));
					Assert.assertTrue(box.isIsolate(true));
					Assert.assertTrue(box.isIsolate(false));
				} else if ("8".equals(box.toString())) {
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_START));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_END));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_LINE_MIDDLE));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_START));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_MIDDLE));
					Assert.assertTrue(box.isIsolate(true));
					Assert.assertTrue(box.isIsolate(false));
					Assert.assertEquals(1, context.getIndex());
				} else {
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_START));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_LINE_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_MIDDLE));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_START));
					Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_END));
					Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_MIDDLE));
					Assert.assertTrue(box.isIsolate(true));
					Assert.assertTrue(box.isIsolate(false));
					Assert.assertEquals(2, context.getIndex());
				}
			}
		};
		visitor.visit(paragraph);


		builder = Paragraph.Builder.newBuilder(texasOption);
		paragraph = builder
				.text("1")
				.brk()
				.build();

		paragraph.measure(measurer, new TextAttribute(measurer));
		texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 10);

		layout = paragraph.getLayout();
		Assert.assertEquals(1, layout.getLineCount());
		visitor = new ParagraphVisitor() {
			@Override
			protected void onVisitParagraphStart(Paragraph paragraph) {

			}

			@Override
			protected void onVisitParagraphEnd(Paragraph paragraph) {

			}

			@Override
			protected void onVisitLineStart(Line line, float x, float y) {

			}

			@Override
			protected void onVisitLineEnd(Line line, float x, float y) {

			}

			@Override
			protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
				Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_LINE_START));
				Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_LINE_END));
				Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_LINE_MIDDLE));
				Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_START));
				Assert.assertTrue(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_END));
				Assert.assertFalse(context.checkLocation(RendererContext.LOCATION_PARAGRAPH_MIDDLE));
			}
		};
		visitor.visit(paragraph);
	}
}
