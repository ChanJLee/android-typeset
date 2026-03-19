package me.chan.texas.renderer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import me.chan.texas.misc.Rect;
import me.chan.texas.misc.RectF;
import android.util.Log;

import androidx.annotation.NonNull;

import me.chan.texas.MockTextSource;
import me.chan.texas.TexasOption;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.test.mock.MockTextPaint;

import me.chan.texas.Texas;
import me.chan.texas.di.DaggerFakeTexasComponent;
import me.chan.texas.di.FakeMeasureFactory;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.worker.LoadingWorker;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Span;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.layout.TextSpan;
import me.chan.texas.typesetter.ParagraphTypesetter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.utils.concurrency.Worker;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TypesetterUnitTest {
	private final Rect mRect = new Rect();

	static {
		Texas.setTexasComponent(DaggerFakeTexasComponent.factory().create());
	}

	@Before
	public void setup() {
		Hyphenation.getInstance();
	}

	@Test
	public void testMockTextPaintPreCondition() {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setTextSize(18);

		String msg = "hello";
		textPaint.getTextBounds(msg, 0, msg.length(), mRect);

		assertEquals(mRect.height(), textPaint.getMockTextHeight());
		assertEquals(mRect.width(), textPaint.getMockTextSize() * msg.length());

		msg = "";
		textPaint.getTextBounds(msg, 0, msg.length(), mRect);
	}

	@Test
	public void testTypesetterSimplePreCondition() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
		checkContentPredication("hello world", BreakStrategy.SIMPLE, 10, 1, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"hello",
				"world"
		});
		checkContentPredication("hello world", BreakStrategy.SIMPLE, 10, 2, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"hello",
				"world"
		});
		checkContentPredication("hello world", BreakStrategy.SIMPLE, 12, 1, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"hello world"
		});
		checkContentPredication("hello world", BreakStrategy.SIMPLE, 14, 1, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"hello world"
		});

		checkContentPredication("1 2 3 555565", BreakStrategy.SIMPLE, 5, 1, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"1 2 3",
				"555565"
		});

		checkContentPredication("一二三四五六七八九", BreakStrategy.SIMPLE, 5, 1, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"一二三四五",
				"六七八九",
		});
	}

	@Test
	public void testTypesetterBalancePreCondition() throws IllegalAccessException, InterruptedException, NoSuchFieldException {
		checkContentPredication("hello world", BreakStrategy.BALANCED, 10, 1, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"hello",
				"world"
		});
		checkContentPredication("hello world", BreakStrategy.BALANCED, 10, 2, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"hello",
				"world"
		});
		checkContentPredication("hello world", BreakStrategy.BALANCED, 12, 1, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"hello world"
		});
		checkContentPredication("hello world", BreakStrategy.BALANCED, 14, 1, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"hello world"
		});

		checkContentPredication("1 2 3 555565", BreakStrategy.BALANCED, 5, 1, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"1 2 3",
				"555565"
		});

		checkContentPredication("一二三四五六七八九", BreakStrategy.BALANCED, 5, 1, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"一二三四五",
				"六七八九"
		});
	}

	@Test
	public void testParagraphVisit() throws NoSuchFieldException, InterruptedException, IllegalAccessException, ParagraphVisitor.VisitException {
		Paragraph paragraph = checkContentPredication("一二三四五六七八九一二三四", BreakStrategy.BALANCED, 5, 1, Paragraph.TYPESET_POLICY_DEFAULT, new String[]{
				"一二三四五",
				"六七八九一",
				"二三四"
		});
		new ParagraphVisitor() {

			@Override
			protected void onVisitParagraphStart(Paragraph paragraph) {
				sendVisitSig(ParagraphVisitor.SIG_STOP_PARA_VISIT);
			}

			@Override
			protected void onVisitParagraphEnd(Paragraph paragraph) {

			}

			@Override
			protected void onVisitLineStart(Line line, float x, float y) {
				Assert.fail("test stop para visit failed");
			}

			@Override
			protected void onVisitLineEnd(Line line, float x, float y) {

			}

			@Override
			protected void onVisitBox(Span span, RectF inner, RectF outer, @NonNull RendererContext context) {

			}
		}.visit(paragraph);
		AtomicInteger integer = new AtomicInteger(0);
		new ParagraphVisitor() {
			@Override
			protected void onVisitParagraphStart(Paragraph paragraph) {
			}

			@Override
			protected void onVisitParagraphEnd(Paragraph paragraph) {

			}

			@Override
			protected void onVisitLineStart(Line line, float x, float y) {
				integer.incrementAndGet();
			}

			@Override
			protected void onVisitLineEnd(Line line, float x, float y) {

			}

			@Override
			protected void onVisitBox(Span span, RectF inner, RectF outer, @NonNull RendererContext context) {

			}
		}.visit(paragraph);
		Assert.assertEquals(3, integer.get());

		integer.set(0);
		new ParagraphVisitor() {
			@Override
			protected void onVisitParagraphStart(Paragraph paragraph) {
			}

			@Override
			protected void onVisitParagraphEnd(Paragraph paragraph) {

			}

			@Override
			protected void onVisitLineStart(Line line, float x, float y) {
				integer.incrementAndGet();
				if (integer.get() == 2) {
					sendVisitSig(ParagraphVisitor.SIG_STOP_LINE_VISIT);
				}
			}

			@Override
			protected void onVisitLineEnd(Line line, float x, float y) {

			}

			@Override
			protected void onVisitBox(Span span, RectF inner, RectF outer, @NonNull RendererContext context) {
				if (integer.get() == 2) {
					Assert.fail("test stop line visit failed");
				}
			}
		}.visit(paragraph);
		Assert.assertEquals(3, integer.get());

		integer.set(0);
		new ParagraphVisitor() {
			@Override
			protected void onVisitParagraphStart(Paragraph paragraph) {
			}

			@Override
			protected void onVisitParagraphEnd(Paragraph paragraph) {

			}

			@Override
			protected void onVisitLineStart(Line line, float x, float y) {
				integer.incrementAndGet();
				if (integer.get() == 2) {
					sendVisitSig(ParagraphVisitor.SIG_STOP_PARA_VISIT);
				}
			}

			@Override
			protected void onVisitLineEnd(Line line, float x, float y) {

			}

			@Override
			protected void onVisitBox(Span span, RectF inner, RectF outer, @NonNull RendererContext context) {
				if (integer.get() > 2) {
					Assert.fail("test stop line visit failed");
				}
			}
		}.visit(paragraph);
		Assert.assertEquals(2, integer.get());

		integer.set(0);
		AtomicInteger tagCount = new AtomicInteger(0);
		new ParagraphVisitor() {
			@Override
			protected void onVisitParagraphStart(Paragraph paragraph) {
			}

			@Override
			protected void onVisitParagraphEnd(Paragraph paragraph) {

			}

			@Override
			protected void onVisitLineStart(Line line, float x, float y) {
				integer.incrementAndGet();
			}

			@Override
			protected void onVisitLineEnd(Line line, float x, float y) {

			}

			@Override
			protected void onVisitBox(Span span, RectF inner, RectF outer, @NonNull RendererContext context) {
				sendVisitSig(ParagraphVisitor.SIG_STOP_PARA_VISIT);
				tagCount.incrementAndGet();
			}
		}.visit(paragraph);
		Assert.assertEquals(1, integer.get());
		Assert.assertEquals(1, tagCount.get());

		integer.set(0);
		tagCount.set(0);
		new ParagraphVisitor() {
			@Override
			protected void onVisitParagraphStart(Paragraph paragraph) {
			}

			@Override
			protected void onVisitParagraphEnd(Paragraph paragraph) {

			}

			@Override
			protected void onVisitLineStart(Line line, float x, float y) {
				integer.incrementAndGet();
			}

			@Override
			protected void onVisitLineEnd(Line line, float x, float y) {

			}

			@Override
			protected void onVisitBox(Span span, RectF inner, RectF outer, @NonNull RendererContext context) {
				sendVisitSig(ParagraphVisitor.SIG_STOP_LINE_VISIT);
				tagCount.incrementAndGet();
			}
		}.visit(paragraph);
		Assert.assertEquals(3, integer.get());
		Assert.assertEquals(3, tagCount.get());
	}

	private void printDocument(Document document) {
		for (int i = 0; i < document.getSegmentCount(); ++i) {
			Segment segment = document.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			Layout layout = paragraph.getLayout();
			for (int j = 0; j < layout.getLineCount(); ++j) {
				Line line = layout.getLine(j);
				StringBuilder stringBuilder = new StringBuilder();
				for (int k = 0; k < line.getElementCount(); ++k) {
					Element element = line.getElement(k);
					if (element instanceof TextSpan) {
						TextSpan span = (TextSpan) element;
						stringBuilder.append(span)
								.append(" ");
					}

				}
				Log.d("chan_debug", "line: " + j + ", space: " + " content: " + stringBuilder);
			}
		}
	}

	@Test
	public void testTypesetter() throws IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {
		for (int i = 1; i <= 6; ++i) {
			File file = new File("../app/src/main/assets/harry" + i + ".txt");
			System.out.println(file.getAbsolutePath());
			assertTrue(file.exists());

			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line)
						.append("\n");
			}

			String text = stringBuilder.toString();
			assertNotEquals(text.length(), 0);

			long timestamp = System.currentTimeMillis();

			checkContent(text, BreakStrategy.SIMPLE, 1080, 1);
			checkContent(text, BreakStrategy.SIMPLE, 1080, 18);
			checkContent(text, BreakStrategy.SIMPLE, 1080, 540, false);
			checkContent(text, BreakStrategy.SIMPLE, 1080, 1080, false);
			checkContent(text, BreakStrategy.SIMPLE, 1080, 1081, false);

			checkContent(text, BreakStrategy.BALANCED, 1080, 1);
			checkContent(text, BreakStrategy.BALANCED, 1080, 18);
			checkContent(text, BreakStrategy.BALANCED, 1080, 540, false);
			checkContent(text, BreakStrategy.BALANCED, 1080, 1080, false);
			checkContent(text, BreakStrategy.BALANCED, 1080, 1081, false);

			System.out.println("used time: " + (System.currentTimeMillis() - timestamp));
		}
	}

	private Paragraph checkContentPredication(String text, BreakStrategy breakStrategy, float lineWidth, int textSize, int policy, String[] exceptedLines) throws InterruptedException, NoSuchFieldException, IllegalAccessException {
		System.out.println("check content predication, width: " + lineWidth + " text size: " + textSize + " " + breakStrategy + "->" + text);

		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(textSize);

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		LoadingJoinListener listener = new LoadingJoinListener();
		LoadingWorker.Args args = new LoadingWorker.Args(new MockTextSource(renderOption, new TextAttribute(measurer), measurer, text), listener);
		WorkerScheduler.loading().submit(Worker.Token.newInstance(), args);

		if (listener.mThrowable != null) {
			throw new RuntimeException(listener.mThrowable);
		}

		Document document = listener.mDocument;
		assertNotEquals(document.getSegmentCount(), 0);

		assertNotEquals(document.getSegmentCount(), 0);
		Assert.assertEquals(document.getSegmentCount(), 1);
		Segment segment = document.getSegment(0);

		Paragraph paragraph = (Paragraph) segment;
		paragraph.measure(measurer, new TextAttribute(measurer));
		texTypesetter.typeset(paragraph, breakStrategy, (int) lineWidth);
		assertNotNull(paragraph);
		Layout layout = paragraph.getLayout();
		assertEquals(layout.getLineCount(), exceptedLines.length);

		for (int i = 0; i < layout.getLineCount(); ++i) {
			Line line = layout.getLine(i);
			Assert.assertEquals(exceptedLines[i], line.toString());
		}

		BoundCheckDrawer drawer = new BoundCheckDrawer(lineWidth);
		try {
			drawer.visit(paragraph);
		} catch (ParagraphVisitor.VisitException e) {
			Assert.fail("fuck");
		}

		return paragraph;
	}

	private void checkContent(String text, BreakStrategy breakStrategy, float lineWidth, int textSize) throws NoSuchFieldException, InterruptedException, IllegalAccessException {
		checkContent(text, breakStrategy, lineWidth, textSize, true);
	}

	private void checkContent(String text, BreakStrategy breakStrategy, float lineWidth, int textSize, boolean enableBoundCheck) throws InterruptedException, NoSuchFieldException, IllegalAccessException {
		System.out.println("check content, width: " + lineWidth + " text size: " + textSize + " " + breakStrategy);

		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(textSize);

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		RenderOption renderOption = new RenderOption();
		LoadingJoinListener listener = new LoadingJoinListener();
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		LoadingWorker.Args args = new LoadingWorker.Args(new MockTextSource(renderOption, new TextAttribute(measurer), measurer, text), listener);
		WorkerScheduler.loading().submit(Worker.Token.newInstance(), args);

		if (listener.mThrowable != null) {
			throw new RuntimeException(listener.mThrowable);
		}

		Document document = listener.mDocument;
		assertNotEquals(document.getSegmentCount(), 0);

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < document.getSegmentCount(); ++i) {
			Segment segment = document.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			paragraph.measure(measurer, new TextAttribute(measurer));
			texTypesetter.typeset(paragraph, breakStrategy, (int) lineWidth);
			assertNotNull(paragraph);
			Layout layout = paragraph.getLayout();
			assertNotEquals(layout.getLineCount(), 0);

			for (int j = 0; j < layout.getLineCount(); ++j) {
				Line l = layout.getLine(j);

				for (int x = 0; x < l.getElementCount(); ++x) {
					Element element = l.getElement(x);
					if (!(element instanceof TextSpan)) {
						continue;
					}

					Span span = (Span) element;
					String content = span.toString();
					TextSpan textBox = (TextSpan) span;
					if (textBox.isPenalty() && textBox.hasAttribute(TextSpan.ATTRIBUTE_PENDED_HYPHEN)) {
						Assert.assertEquals(content.charAt(content.length() - 1), '-');
						content = content.substring(0, content.length() - 1);
					}
					stringBuilder.append(content);
				}
			}

			if (enableBoundCheck) {
				BoundCheckDrawer drawer = new BoundCheckDrawer(lineWidth);
				try {
					drawer.visit(paragraph);
				} catch (ParagraphVisitor.VisitException e) {
					Assert.fail("fuck");
				}
			}
		}

		String origin = text.replaceAll("\\p{Z}+|\\t|\\r|\\n", "");
		String current = stringBuilder.toString();

		if (!origin.equals(current)) {
			printDiff(origin, current);
			throw new RuntimeException("check result failed");
		}

		printDocument(document);
	}

	private static void printDiff(String origin, String current) {
		int s1 = origin.length();
		int s2 = current.length();

		if (s1 != s2) {
			System.out.println("len not equals");
//			return;
		}

		s1 = Math.min(s1, s2);
		for (int i = 0; i < s1; ++i) {
			if (origin.charAt(i) != current.charAt(i)) {
				System.out.println(origin.substring(i, Math.min(origin.length(), i + 10)));
				System.out.println(current.substring(i, Math.min(current.length(), i + 10)));
				return;
			}
		}
	}

	@Test
	public void testEmptyLineWithBrk() {
		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		PaintSet paintSet = new PaintSet(factory.getMockTextPaint());
		TextAttribute textAttribute = new TextAttribute(measurer);
		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		Paragraph paragraph = builder.build(true);
		Layout layout = paragraph.getLayout();

		ParagraphTypesetter typesetter = new ParagraphTypesetter();
		typesetter.typeset(paragraph, BreakStrategy.SIMPLE, 100);
		Layout current = paragraph.getLayout();
		Assert.assertNotSame(layout, current);

		assertEquals(0, current.getLineCount());
	}

	@Test
	public void testEmptyLineWithoutBrk() {
		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		PaintSet paintSet = new PaintSet(factory.getMockTextPaint());
		TextAttribute textAttribute = new TextAttribute(measurer);
		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		Paragraph paragraph = builder.build(false);
		Layout layout = paragraph.getLayout();

		ParagraphTypesetter typesetter = new ParagraphTypesetter();
		typesetter.typeset(paragraph, BreakStrategy.SIMPLE, 100);
		Layout current = paragraph.getLayout();
		Assert.assertNotSame(layout, current);

		assertEquals(0, current.getLineCount());
	}

	@Test
	public void testDesire() {
		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		PaintSet paintSet = new PaintSet(factory.getMockTextPaint());
		TextAttribute textAttribute = new TextAttribute(measurer);
		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		Paragraph paragraph = builder.build(false);
		Worker.Token token = Worker.Token.newInstance();
		Assert.assertFalse(WorkerScheduler.typeset().desire(paragraph));

		builder = Paragraph.Builder.newBuilder(texasOption);
		paragraph = builder.build(true);
		Assert.assertFalse(WorkerScheduler.typeset().desire(paragraph));

		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("12345")
				.brk()
				.text("12");
		paragraph = builder.build(true);
		paragraph.measure(measurer, textAttribute);
		Assert.assertTrue(WorkerScheduler.typeset().desire(paragraph));
		Layout layout = paragraph.getLayout();
		Assert.assertEquals(5, layout.getWidth());
		Assert.assertEquals(3, layout.getHeight());

		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("12345");
		paragraph = builder.build(true);
		paragraph.measure(measurer, textAttribute);
		Assert.assertTrue(WorkerScheduler.typeset().desire(paragraph));
		layout = paragraph.getLayout();
		Assert.assertEquals(5, layout.getWidth());
		Assert.assertEquals(1, layout.getHeight());
	}

	@Test
	public void testNewLine() {
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);

		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		PaintSet paintSet = new PaintSet(factory.getMockTextPaint());
		TextAttribute textAttribute = new TextAttribute(measurer);

		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption)
				.setTypesetPolicy(Paragraph.TYPESET_POLICY_ACCEPT_CONTROL_CHAR);

		String msg = " hello \n\t\r world";
		builder.text(msg);

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		Paragraph paragraph = builder.build();
		paragraph.measure(measurer, textAttribute);
		texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 100);

		Layout layout = paragraph.getLayout();
		Assert.assertEquals(2, layout.getLineCount());
		Assert.assertEquals("hello", layout.getLine(0).toString());
		Assert.assertEquals("world", layout.getLine(1).toString());

		paragraph.measure(measurer, textAttribute);
		texTypesetter.typeset(paragraph, BreakStrategy.BALANCED, 100);

		layout = paragraph.getLayout();
		Assert.assertEquals(2, layout.getLineCount());
		Assert.assertEquals("hello", layout.getLine(0).toString());
		Assert.assertEquals("world", layout.getLine(1).toString());
	}


	private static class LoadingJoinListener implements LoadingWorker.Listener {

		private Throwable mThrowable;
		private Document mDocument;

		@Override
		public void onStart() {

		}

		@Override
		public void onFailure(Throwable throwable) {
			mThrowable = throwable;
		}

		@Override
		public void onSuccess(TexasOption option, Document prev, Document document) {
			mDocument = document;
		}
	}
}