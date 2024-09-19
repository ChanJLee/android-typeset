package me.chan.texas.renderer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shanbay.lib.texas.test.mock.MockTextPaint;

import me.chan.texas.Texas;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.adapter.TextAdapter;
import me.chan.texas.di.DaggerFakeTexasComponent;
import me.chan.texas.di.FakeMeasureFactory;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.worker.LoadingWorker;
import me.chan.texas.source.ObjectSource;
import me.chan.texas.source.SourceCloseException;
import me.chan.texas.source.SourceOpenException;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.typesetter.ParagraphTypesetter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.utils.concurrency.TaskQueue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TypesetterUnitTest {
	@Mock
	private Rect mRect;

	static {
		Texas.setTexasComponent(DaggerFakeTexasComponent.factory().create());
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doAnswer(invocation -> {
			Rect rect = (Rect) invocation.getMock();
			return rect.right - rect.left;
		}).when(mRect).width();

		Mockito.doAnswer(invocation -> {
			Rect rect = (Rect) invocation.getMock();
			return rect.bottom - rect.top;
		}).when(mRect).height();

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
	public void testTypesetterSimplePreCondition() throws InterruptedException, SourceCloseException, SourceOpenException, ParseException, NoSuchFieldException, IllegalAccessException {
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

		checkContentPredication("一二三四五六七八九", BreakStrategy.SIMPLE, 5, 1, Paragraph.TYPESET_POLICY_CJK_OPTIMIZATION, new String[]{
				"一 二 三 四 五",
				"六 七 八 九"
		});
	}

	@Test
	public void testTypesetterBalancePreCondition() throws IllegalAccessException, InterruptedException, ParseException, SourceOpenException, SourceCloseException, NoSuchFieldException {
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

		checkContentPredication("一二三四五六七八九", BreakStrategy.BALANCED, 5, 1, Paragraph.TYPESET_POLICY_CJK_OPTIMIZATION, new String[]{
				"一 二 三 四 五",
				"六 七 八 九"
		});

	}

	@Test
	public void testParagraphVisit() throws SourceCloseException, SourceOpenException, ParseException, NoSuchFieldException, InterruptedException, IllegalAccessException, ParagraphVisitor.VisitException {
		Paragraph paragraph = checkContentPredication("一二三四五六七八九一二三四", BreakStrategy.BALANCED, 5, 1, Paragraph.TYPESET_POLICY_CJK_OPTIMIZATION, new String[]{
				"一 二 三 四 五",
				"六 七 八 九 一",
				"二 三 四"
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
			protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {

			}
		}.visit(paragraph, new RenderOption());
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
			protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {

			}
		}.visit(paragraph, new RenderOption());
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
			protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
				if (integer.get() == 2) {
					Assert.fail("test stop line visit failed");
				}
			}
		}.visit(paragraph, new RenderOption());
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
			protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
				if (integer.get() > 2) {
					Assert.fail("test stop line visit failed");
				}
			}
		}.visit(paragraph, new RenderOption());
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
			protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
				sendVisitSig(ParagraphVisitor.SIG_STOP_PARA_VISIT);
				tagCount.incrementAndGet();
			}
		}.visit(paragraph, new RenderOption());
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
			protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
				sendVisitSig(ParagraphVisitor.SIG_STOP_LINE_VISIT);
				tagCount.incrementAndGet();
			}
		}.visit(paragraph, new RenderOption());
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
				for (int k = 0; k < line.getCount(); ++k) {
					Element element = line.getElement(k);
					if (element instanceof TextBox) {
						TextBox box = (TextBox) element;
						stringBuilder.append(box)
								.append(" ");
					}

				}
				Log.d("chan_debug", "line: " + j + ", space: " + " content: " + stringBuilder);
			}
		}
	}

	@Test
	public void testTypesetter() throws IOException, InterruptedException, SourceCloseException, SourceOpenException, ParseException, NoSuchFieldException, IllegalAccessException {
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
			checkContent(text, BreakStrategy.SIMPLE, 1080, 540);
			checkContent(text, BreakStrategy.SIMPLE, 1080, 1080);
			checkContent(text, BreakStrategy.SIMPLE, 1080, 1081);

			checkContent(text, BreakStrategy.BALANCED, 1080, 1);
			checkContent(text, BreakStrategy.BALANCED, 1080, 18);
			checkContent(text, BreakStrategy.BALANCED, 1080, 540);
			checkContent(text, BreakStrategy.BALANCED, 1080, 1080);
			checkContent(text, BreakStrategy.BALANCED, 1080, 1081);

			System.out.println("used time: " + (System.currentTimeMillis() - timestamp));
		}
	}

	private Paragraph checkContentPredication(String text, BreakStrategy breakStrategy, float lineWidth, int textSize, int policy, String[] exceptedLines) throws InterruptedException, SourceCloseException, SourceOpenException, ParseException, NoSuchFieldException, IllegalAccessException {
		System.out.println("check content predication, width: " + lineWidth + " text size: " + textSize + " " + breakStrategy + "->" + text);

		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(textSize);

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		TextAdapter textParser = new TextAdapter(policy);
		RenderOption renderOption = new RenderOption();
		textParser.setSource(new ObjectSource<>(text));

		LoadingJoinListener listener = new LoadingJoinListener();
		LoadingWorker.Args args = LoadingWorker.Args.obtain(new RenderOption(), textParser, LoadingStrategy.INIT, listener);
		WorkerScheduler.loading().submit(TaskQueue.Token.newInstance(), args);

		if (listener.mThrowable != null) {
			throw new RuntimeException(listener.mThrowable);
		}

		Document document = listener.mDocument;
		assertNotEquals(document.getSegmentCount(), 0);

		assertNotEquals(document.getSegmentCount(), 0);
		Assert.assertEquals(document.getSegmentCount(), 1);
		Segment segment = document.getSegment(0);

		Paragraph paragraph = (Paragraph) segment;
		texTypesetter.typeset(paragraph, breakStrategy, (int) lineWidth, 1);
		assertNotNull(paragraph);
		Layout layout = paragraph.getLayout();
		assertEquals(layout.getLineCount(), exceptedLines.length);

		for (int i = 0; i < layout.getLineCount(); ++i) {
			Line line = layout.getLine(i);
			Assert.assertEquals(line.toString(), exceptedLines[i]);
		}

		BoundCheckDrawer drawer = new BoundCheckDrawer(lineWidth);
		try {
			drawer.visit(paragraph, renderOption);
		} catch (ParagraphVisitor.VisitException e) {
			Assert.fail("fuck");
		}

		return paragraph;
	}

	private void checkContent(String text, BreakStrategy breakStrategy, float lineWidth, int textSize) throws InterruptedException, SourceCloseException, SourceOpenException, ParseException, NoSuchFieldException, IllegalAccessException {
		System.out.println("check content, width: " + lineWidth + " text size: " + textSize + " " + breakStrategy);

		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(textSize);

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		TextAdapter textParser = new TextAdapter();
		RenderOption renderOption = new RenderOption();
		textParser.setSource(new ObjectSource<>(text));
		LoadingJoinListener listener = new LoadingJoinListener();
		LoadingWorker.Args args = LoadingWorker.Args.obtain(new RenderOption(), textParser, LoadingStrategy.INIT, listener);
		WorkerScheduler.loading().submit(TaskQueue.Token.newInstance(), args);

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
			texTypesetter.typeset(paragraph, breakStrategy, (int) lineWidth, 1);
			assertNotNull(paragraph);
			Layout layout = paragraph.getLayout();
			assertNotEquals(layout.getLineCount(), 0);

			for (int j = 0; j < layout.getLineCount(); ++j) {
				Line l = layout.getLine(j);

				for (int x = 0; x < l.getCount(); ++x) {
					Element element = l.getElement(x);
					if (!(element instanceof TextBox)) {
						continue;
					}

					Box box = (Box) element;
					String content = box.toString();
					TextBox textBox = (TextBox) box;
					if (textBox.isPenalty() && textBox.hasAttribute(TextBox.ATTRIBUTE_PENDED_HYPHEN)) {
						Assert.assertEquals(content.charAt(content.length() - 1), '-');
						content = content.substring(0, content.length() - 1);
					}
					stringBuilder.append(content);
				}
			}

			BoundCheckDrawer drawer = new BoundCheckDrawer(lineWidth);
			try {
				drawer.visit(paragraph, renderOption);
			} catch (ParagraphVisitor.VisitException e) {
				Assert.fail("fuck");
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

//	@Test
//	public void testMixTypesetter() {
//		testMixNormal();
//		testMixFull();
//		testMix();
//	}
//
//	private void testMix() {
//		Document mockDocument(20, 20, 100, BreakStrategy.SIMPLE, "123", "123");
//		Assert.assertNotNull(document);
//		Assert.assertEquals(document.getSegmentCount(), 1);
//		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
//		Paragraph paragraph = (Paragraph) document.getSegment(0);
//
//		Layout layout = paragraph.getLayout();
//		Assert.assertNotNull(paragraph);
//		Assert.assertEquals(layout.getLineCount(), 2);
//
//		Line line1 = layout.getLine(0);
//		Line line2 = layout.getLine(1);
//
//		Assert.assertEquals(line1.getCount(), 2);
//		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);
//		Assert.assertEquals(line1.getElement(1).getClass(), DrawableBox.class);
//		Assert.assertEquals(line2.getCount(), 2);
//		Assert.assertEquals(line2.getElement(0).getClass(), TextBox.class);
//		Assert.assertEquals(line2.getElement(1).getClass(), DrawableBox.class);
//
//		mockDocument(20, 20, 100, BreakStrategy.BALANCED, "123", "123");
//		Assert.assertNotNull(document);
//		Assert.assertEquals(document.getSegmentCount(), 1);
//		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
//		paragraph = (Paragraph) document.getSegment(0);
//		layout = paragraph.getLayout();
//
//		Assert.assertNotNull(paragraph);
//		Assert.assertEquals(layout.getLineCount(), 2);
//
//		line1 = layout.getLine(0);
//		line2 = layout.getLine(1);
//
//		Assert.assertEquals(line1.getCount(), 2);
//		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);
//		Assert.assertEquals(line1.getElement(1).getClass(), DrawableBox.class);
//		Assert.assertEquals(line2.getCount(), 2);
//		Assert.assertEquals(line2.getElement(0).getClass(), TextBox.class);
//		Assert.assertEquals(line2.getElement(1).getClass(), DrawableBox.class);
//	}
//
//	private void testMixNormal() {
//		Document mockDocument(20, 20, 100, BreakStrategy.SIMPLE, "123");
//		Assert.assertNotNull(document);
//		Assert.assertEquals(document.getSegmentCount(), 1);
//		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
//		Paragraph paragraph = (Paragraph) document.getSegment(0);
//		Layout layout = paragraph.getLayout();
//
//		Assert.assertNotNull(paragraph);
//		Assert.assertEquals(layout.getLineCount(), 1);
//
//		Line line1 = layout.getLine(0);
//
//		Assert.assertEquals(line1.getCount(), 2);
//		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);
//		Assert.assertEquals(line1.getElement(1).getClass(), DrawableBox.class);
//
//		mockDocument(20, 20, 100, BreakStrategy.BALANCED, "123");
//		Assert.assertNotNull(document);
//		Assert.assertEquals(document.getSegmentCount(), 1);
//		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
//		paragraph = (Paragraph) document.getSegment(0);
//
//		Assert.assertNotNull(paragraph);
//		layout = paragraph.getLayout();
//		Assert.assertEquals(layout.getLineCount(), 1);
//
//		line1 = layout.getLine(0);
//
//		Assert.assertEquals(line1.getCount(), 2);
//		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);
//		Assert.assertEquals(line1.getElement(1).getClass(), DrawableBox.class);
//	}
//
//	private void testMixFull() {
//		Document mockDocument(20, 20, 100, BreakStrategy.SIMPLE, "12345");
//		Assert.assertNotNull(document);
//		Assert.assertEquals(document.getSegmentCount(), 1);
//		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
//		Paragraph paragraph = (Paragraph) document.getSegment(0);
//		Layout layout = paragraph.getLayout();
//
//		Assert.assertNotNull(paragraph);
//		Assert.assertEquals(layout.getLineCount(), 2);
//
//		Line line1 = layout.getLine(0);
//		Line line2 = layout.getLine(1);
//
//		Assert.assertEquals(line2.getCount(), 1);
//		Assert.assertEquals(line2.getElement(0).getClass(), DrawableBox.class);
//		Assert.assertEquals(line1.getCount(), 1);
//		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);
//
//		mockDocument(20, 20, 100, BreakStrategy.BALANCED, "12345");
//		Assert.assertNotNull(document);
//		Assert.assertEquals(document.getSegmentCount(), 1);
//		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
//		paragraph = (Paragraph) document.getSegment(0);
//		layout = paragraph.getLayout();
//
//		Assert.assertNotNull(paragraph);
//		Assert.assertEquals(layout.getLineCount(), 2);
//
//		line1 = layout.getLine(0);
//		line2 = layout.getLine(1);
//
//		Assert.assertEquals(line2.getCount(), 1);
//		Assert.assertEquals(line2.getElement(0).getClass(), DrawableBox.class);
//		Assert.assertEquals(line1.getCount(), 1);
//		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);
//	}
//
//	private Document mockDocument(int textSize, float drawableWidth, float width, BreakStrategy breakStrategy, CharSequence... s) {
//		MockTextPaint textPaint = new MockTextPaint(textSize);
//		Measurer measurer = new MockMeasurer(textPaint);
//		Document Document.obtain();
//		MockTextAttribute attribute = new MockTextAttribute(textPaint);
//		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), measurer, attribute, null);
//		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
//		for (int i = 0; i < s.length; ++i) {
//			builder.text(s[i], 0, s[i].length());
//			builder.emoticon(Emoticon.obtain(new ColorDrawable(10), drawableWidth, 20));
//		}
//		List<Segment> list = new ArrayList<>();
//		list.add(builder.build());
//		document.insertTail(list);
//
//		ParagraphTypesetter typesetter = new ParagraphTypesetter();
//		for (int i = 0; i < document.getSegmentCount(); ++i) {
//			typesetter.typeset((Paragraph) document.getSegment(i), breakStrategy, (int) width, 1);
//		}
//
//		return document;
//	}
//
//	@Test
//	public void testEmptyLineWithBrk() {
//		RenderOption renderOption = new RenderOption();
//		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), mMockMeasurer, mMockTextAttribute, renderOption);
//
//		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
//
//		Paragraph paragraph = builder.build(true);
//		Layout layout = paragraph.getLayout();
//
//		ParagraphTypesetter typesetter = new ParagraphTypesetter();
//		typesetter.typeset(paragraph, BreakStrategy.SIMPLE, 100, 1);
//		Layout current = paragraph.getLayout();
//		Assert.assertNotSame(layout, current);
//
//		assertEquals(0, current.getLineCount());
//	}
//
//	@Test
//	public void testEmptyLineWithoutBrk() {
//		RenderOption renderOption = new RenderOption();
//		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), mMockMeasurer, mMockTextAttribute, renderOption);
//
//		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
//
//		Paragraph paragraph = builder.build(false);
//		Layout layout = paragraph.getLayout();
//
//		ParagraphTypesetter typesetter = new ParagraphTypesetter();
//		typesetter.typeset(paragraph, BreakStrategy.SIMPLE, 100, 1);
//		Layout current = paragraph.getLayout();
//		Assert.assertNotSame(layout, current);
//
//		assertEquals(0, current.getLineCount());
//	}
//
//	@Test
//	public void testDesire() {
//		RenderOption renderOption = new RenderOption();
//		renderOption.setLineSpace(1);
//		mPaint.setMockTextSize(1);
//		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), mMockMeasurer, mMockTextAttribute, renderOption);
//
//		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
//
//		Paragraph paragraph = builder.build(false);
//		Region region = new Region(100, 10);
//		Assert.assertFalse(WorkerScheduler.typeset().desire(paragraph, region, renderOption));
//
//		// TODO 只有一个换行符 按理说这里要留一个行高的空间
//		builder = Paragraph.Builder.newBuilder(texasOption);
//		paragraph = builder.build(true);
//		Assert.assertTrue(WorkerScheduler.typeset().desire(paragraph, region, renderOption));
//		Assert.assertEquals(region.getWidth(), 0);
//		Assert.assertEquals(region.getHeight(), 0);
//
//
//		builder = Paragraph.Builder.newBuilder(texasOption);
//		builder.text("12345")
//				.brk()
//				.text("12");
//		paragraph = builder.build(true);
//		Assert.assertTrue(WorkerScheduler.typeset().desire(paragraph, region, renderOption));
//		Assert.assertEquals(5, region.getWidth());
//		Assert.assertEquals(3, region.getHeight());
//
//		builder = Paragraph.Builder.newBuilder(texasOption);
//		builder.text("12345");
//		paragraph = builder.build(true);
//		Assert.assertTrue(WorkerScheduler.typeset().desire(paragraph, region, renderOption));
//		Assert.assertEquals(5, region.getWidth());
//		Assert.assertEquals(1, region.getHeight());
//	}

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
		public void onSuccess(LoadingStrategy strategy, Document document, int start, int end) {
			mDocument = document;
		}
	}
}