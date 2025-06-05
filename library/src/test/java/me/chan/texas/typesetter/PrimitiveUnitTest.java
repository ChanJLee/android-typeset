package me.chan.texas.typesetter;

import me.chan.texas.TexasOption;
import me.chan.texas.di.FakeMeasureFactory;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.BoundCheckDrawer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.typesetter.simple.SimpleParagraphTypesetter;
import me.chan.texas.typesetter.tex.TexParagraphTypesetter;

import org.junit.Assert;
import org.junit.Test;

public class PrimitiveUnitTest {
	private final TexasOption mTexasOption;

	public PrimitiveUnitTest() {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setMockTextSize(1);

		Measurer measurer = new MockMeasurer(textPaint);
		TextAttribute textAttribute = new TextAttribute(measurer);

		mTexasOption = new TexasOption(new PaintSet(textPaint), Hyphenation.getInstance(), measurer, textAttribute, new RenderOption());
	}

	@Test
	public void testSimple() {
		// multi line
		System.out.println(">>>>> simple 0");
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(mTexasOption);
		builder.newSpanBuilder()
				.next("1 2 3")
				.next("4 5")
				.buildSpan();
		checkContent(builder.build(), 6, BreakStrategy.SIMPLE,
				"1 2 3",
				"4 5"
		);

		// single line, full
		System.out.println(">>>>> simple 1");
		builder = Paragraph.Builder.newBuilder(mTexasOption);
		builder.newSpanBuilder()
				.next("1 2 3 ")
				.buildSpan();
		checkContent(builder.build(), 6, BreakStrategy.SIMPLE,
				"1 2 3"
		);

		// single line, not full
		System.out.println(">>>>> simple 2");
		builder = Paragraph.Builder.newBuilder(mTexasOption);
		builder.newSpanBuilder()
				.next("1")
				.buildSpan();
		checkContent(builder.build(), 6, BreakStrategy.SIMPLE,
				"1"
		);
	}

	@Test
	public void testBrk() {
		System.out.println(">>>>> simple force break 0");
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(mTexasOption);
		builder.newSpanBuilder()
				.next("1")
				.buildSpan()
				.brk()
				.newSpanBuilder()
				.next(" 2")
				.buildSpan()
				.brk()
				.newSpanBuilder()
				.next("3")
				.buildSpan();
		checkContent(builder.build(), 8, BreakStrategy.SIMPLE, "1", "2", "3");

		System.out.println(">>>>> tex force break 0");
		builder = Paragraph.Builder.newBuilder(mTexasOption);
		builder.newSpanBuilder()
				.next("1")
				.buildSpan()
				.brk()
				.newSpanBuilder()
				.next(" 2")
				.buildSpan()
				.brk()
				.newSpanBuilder()
				.next("3")
				.buildSpan();
		checkContent(builder.build(), 8, BreakStrategy.BALANCED, "1", "2", "3");
	}

	@Test
	public void testSimpleForbidBreak() {
		// 断点在 3 之前，存在可以断的点
		System.out.println(">>>>> simple fb break 0");
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(mTexasOption);
		builder.newSpanBuilder()
				.next("1112 3, 4 5")
				.buildSpan();
		checkContent(builder.build(), 6, BreakStrategy.SIMPLE,
				"1112",
				"3, 4 5"
		);

		// 断点在 3 之后 不存在可以断的点
		{
			// 至少还存在空格
			{
				System.out.println(">>>>> simple fb break 1");
				builder = Paragraph.Builder.newBuilder(mTexasOption);
				builder.newSpanBuilder()
						.next("1111 < 3 4 5")
						.buildSpan();
				checkContent(builder.build(), 6, BreakStrategy.SIMPLE,
						"1111",
						"<3 4 5"
				);
			}

			// 第一个过大
			{
				System.out.println(">>>>> simple fb break 2");
				builder = Paragraph.Builder.newBuilder(mTexasOption);
				builder.newSpanBuilder()
						.next("1234567 89")
						.buildSpan();
				checkContent(builder.build(), 6, BreakStrategy.SIMPLE,
						"1234567",
						"89"
				);
			}

			// 中间没有空格，但是第一个可以分割
			{
				System.out.println(">>>>> simple fb break 3");
				builder = Paragraph.Builder.newBuilder(mTexasOption);
				builder.newSpanBuilder()
						.next("triangle")
						.buildSpan();
				checkContent(builder.build(), 6, BreakStrategy.SIMPLE,
						"trian-",
						"gle"
				);
			}
		}
	}

	@Test
	public void testTexForbidBreak() {
		// 断点在 3 之前，存在可以断的点
		System.out.println(">>>>> simple fb break 0");
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(mTexasOption);
		builder.newSpanBuilder()
				.next("1 2 3, 4 5")
				.buildSpan();
		checkContent(builder.build(), 6, BreakStrategy.BALANCED,
				"1 2 3,",
				"4 5"
		);

		// 断点在 3 之后 不存在可以断的点
		{
			// 至少还存在空格
			{
				System.out.println(">>>>> simple fb break 1");
				builder = Paragraph.Builder.newBuilder(mTexasOption);
				builder.newSpanBuilder()
						.next("111 < 3 4 5")
						.buildSpan();
				checkContent(builder.build(), 6, BreakStrategy.BALANCED,
						"111 <3",
						"4 5"
				);
			}

			// 第一个过大
			{
				System.out.println(">>>>> simple fb break 2");
				builder = Paragraph.Builder.newBuilder(mTexasOption);
				builder.newSpanBuilder()
						.next("1234567 89")
						.buildSpan();
				checkContent(builder.build(), 6, BreakStrategy.BALANCED
				);
			}

			// 中间没有空格，但是第一个可以分割
			{
				System.out.println(">>>>> simple fb break 3");
				builder = Paragraph.Builder.newBuilder(mTexasOption);
				builder.newSpanBuilder()
						.next("triangle")
						.buildSpan();
				checkContent(builder.build(), 6, BreakStrategy.BALANCED,
						"trian-",
						"gle"
				);
			}
		}
	}

	@Test
	public void testTex() {
		// multi line
		System.out.println(">>>>> tex 0");
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(mTexasOption);
		builder.newSpanBuilder()
				.next("1 2 3 4 5")
				.buildSpan();
		checkContent(builder.build(), 6, BreakStrategy.BALANCED,
				"1 2 3",
				"4 5"
		);
	}

	@Test
	public void testTexCn() {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setMockTextSize(2);

		Measurer measurer = new MockMeasurer(textPaint);
		TextAttribute textAttribute = new TextAttribute(measurer);

		TexasOption texasOption = new TexasOption(new PaintSet(textPaint), Hyphenation.getInstance(), measurer, textAttribute, new RenderOption());

		// multi line
		System.out.println(">>>>> tex cn");
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption, Paragraph.TYPESET_POLICY_DEFAULT);
		builder.newSpanBuilder()
				.next("一二三，四五，")
				.buildSpan();
		Paragraph paragraph = builder.build();
		checkContent(paragraph, 12, BreakStrategy.BALANCED,
				"一 二 三， 四",
				"五，"
		);

		System.out.println(">>>>> tex cn2");
		builder = Paragraph.Builder.newBuilder(texasOption)
				.clearTypesetPolicy();
		builder.newSpanBuilder()
				.next("一二三，四五，")
				.buildSpan();
		paragraph = builder.build();
		checkContent(paragraph, 12, BreakStrategy.BALANCED,
				"一 二 三， 四",
				"五，"
		);
	}

	private static void checkContent(Paragraph paragraph, int width, BreakStrategy strategy, String... lines) {
		checkContent(
				paragraph, width,
				strategy == BreakStrategy.SIMPLE ? new SimpleParagraphTypesetter() : new TexParagraphTypesetter(),
				strategy, lines
		);
	}

	private static void checkContent(Paragraph paragraph, int width, AbsParagraphTypesetter typesetter, BreakStrategy breakStrategy, String... lines) {
		typesetter.typeset(paragraph, breakStrategy, width);

		Layout layout = paragraph.getLayout();
		Assert.assertEquals(lines.length, layout.getLineCount());
		for (int i = 0; i < lines.length; ++i) {
			Line line = layout.getLine(i);
			String lineContent = line.toString();
			Assert.assertEquals(lines[i], lineContent);
		}

		BoundCheckDrawer boundCheckDrawer = new BoundCheckDrawer(width, true);
		try {
			boundCheckDrawer.visit(paragraph);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPenaltyOverflow() {
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);

		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpace(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		PaintSet paintSet = new PaintSet(factory.getMockTextPaint());
		TextAttribute textAttribute = new TextAttribute(measurer);

		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption)
				.text("triangle");
		Paragraph paragraph = builder.build();

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 5);

		Layout layout = paragraph.getLayout();
		Assert.assertEquals(3, layout.getLineCount());

		Assert.assertEquals("tri-", layout.getLine(0).toString());
		Assert.assertEquals("an-", layout.getLine(1).toString());
		Assert.assertEquals("gle", layout.getLine(2).toString());
	}
}
