package me.chan.texas.renderer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import com.shanbay.lib.texas.test.mock.MockTextAttribute;
import com.shanbay.lib.texas.test.mock.MockTextPaint;

import me.chan.texas.TexasOption;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.adapter.TextAdapter;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.worker.LoadingWorker;
import me.chan.texas.source.ObjectSource;
import me.chan.texas.source.SourceCloseException;
import me.chan.texas.source.SourceOpenException;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.layout.Region;
import me.chan.texas.text.layout.DrawableBox;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.Emoticon;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.text.tokenizer.Tokenizer;
import me.chan.texas.typesetter.ParagraphTypesetter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.tokenize.TokenizerModel;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TypesetterUnitTest {
	@Mock
	private Rect mRect;

	private MockTextPaint mPaint = null;
	private TextAttribute mMockTextAttribute;
	private MockMeasurer mMockMeasurer;

	static {
		TokenizerModel model = null;
		try {
			model = new TokenizerModel(new File("../library/src/main/assets/texas/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"));
			Tokenizer.setup(model);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Rect rect = (Rect) invocation.getMock();
				return rect.right - rect.left;
			}
		}).when(mRect).width();

		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Rect rect = (Rect) invocation.getMock();
				return rect.bottom - rect.top;
			}
		}).when(mRect).height();

		Hyphenation.getInstance();

		try {
			Class<?> clazz = Class.forName("com.shanbay.lib.log.Log$JavaLog");
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			Field field = Log.class.getDeclaredField("sImpl");
			field.setAccessible(true);
			constructor.setAccessible(true);
			field.set(null, constructor.newInstance());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		mPaint = new MockTextPaint();
		mMockMeasurer = new MockMeasurer(mPaint);
		mMockTextAttribute = new MockTextAttribute(mPaint);

		Assert.assertNotEquals(mMockTextAttribute.getHyphenWidth(), 0);
		Assert.assertNotEquals(mMockTextAttribute.getSpaceShrink(), 0);
		Assert.assertNotEquals(mMockTextAttribute.getSpaceStretch(), 0);
		Assert.assertNotEquals(mMockTextAttribute.getSpaceWidth(), 0);
	}

	@Test
	public void testMockTextPaint() {
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
	public void testTypesetterSimple() throws InterruptedException, SourceCloseException, SourceOpenException, ParseException, NoSuchFieldException, IllegalAccessException {
		Document document = checkContent("hello world", BreakStrategy.SIMPLE, 10, 1);
		printDocument(document);
		document = checkContent("hello world", BreakStrategy.SIMPLE, 10, 2);
		printDocument(document);
		document = checkContent("hello world", BreakStrategy.SIMPLE, 10, 3);
		printDocument(document);
		document = checkContent("hello world", BreakStrategy.SIMPLE, 100, 1);
		printDocument(document);
		document = checkContent("1 2 3 4 5 6 7 8 555565", BreakStrategy.SIMPLE, 5, 1);
		printDocument(document);
		document = checkContent("一二三四五六七八九", BreakStrategy.SIMPLE, 5, 2);
		printDocument(document);
	}

	@Test
	public void testTypesetterBalance() throws IllegalAccessException, InterruptedException, ParseException, SourceOpenException, SourceCloseException, NoSuchFieldException {
		Document document = checkContent("hello world", BreakStrategy.BALANCED, 10, 1);
		printDocument(document);
		document = checkContent("hello world", BreakStrategy.BALANCED, 10, 2);
		printDocument(document);
		document = checkContent("hello world", BreakStrategy.BALANCED, 10, 3);
		printDocument(document);
		document = checkContent("hello world", BreakStrategy.BALANCED, 100, 1);
		printDocument(document);
		document = checkContent("1 2 3 4 5 6 7 8 555565", BreakStrategy.BALANCED, 5, 1);
		printDocument(document);
		document = checkContent("一二三四五六七八九", BreakStrategy.BALANCED, 5, 2);
		printDocument(document);
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

	private void reset(float lineWidth, int textSize) throws NoSuchFieldException, IllegalAccessException {
		mPaint.setMockTextSize(textSize);
		mMockTextAttribute.refresh(mMockMeasurer);

		Field field = TextAttribute.class.getDeclaredField("mSpaceWidth");
		field.setAccessible(true);
		field.set(mMockTextAttribute, textSize);
	}

	private Document checkContent(String text, BreakStrategy breakStrategy, float lineWidth, int textSize) throws InterruptedException, SourceCloseException, SourceOpenException, ParseException, NoSuchFieldException, IllegalAccessException {
		System.out.println("check content, width: " + lineWidth + " text size: " + textSize + " " + breakStrategy);

		reset(lineWidth, textSize);

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		TextAdapter textParser = new TextAdapter();
		RenderOption renderOption = new RenderOption();
		textParser.setSource(new ObjectSource<>(text));
		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), mMockMeasurer, mMockTextAttribute, renderOption);
		LoadingWorker.LoadingResult result = textParser.getDocument(texasOption, LoadingStrategy.LOAD);
		Document document = result.getDocument();
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
					if (((TextBox) box).isPenalty()) {
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

		return document;
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
	public void testMixTypesetter() {
		testMixNormal();
		testMixFull();
		testMix();
	}

	private void testMix() {
		Document document = mockDocument(20, 20, 100, BreakStrategy.SIMPLE, "123", "123");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getSegmentCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		Paragraph paragraph = (Paragraph) document.getSegment(0);

		Layout layout = paragraph.getLayout();
		Assert.assertNotNull(paragraph);
		Assert.assertEquals(layout.getLineCount(), 2);

		Line line1 = layout.getLine(0);
		Line line2 = layout.getLine(1);

		Assert.assertEquals(line1.getCount(), 2);
		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);
		Assert.assertEquals(line1.getElement(1).getClass(), DrawableBox.class);
		Assert.assertEquals(line2.getCount(), 2);
		Assert.assertEquals(line2.getElement(0).getClass(), TextBox.class);
		Assert.assertEquals(line2.getElement(1).getClass(), DrawableBox.class);

		document = mockDocument(20, 20, 100, BreakStrategy.BALANCED, "123", "123");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getSegmentCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		paragraph = (Paragraph) document.getSegment(0);
		layout = paragraph.getLayout();

		Assert.assertNotNull(paragraph);
		Assert.assertEquals(layout.getLineCount(), 2);

		line1 = layout.getLine(0);
		line2 = layout.getLine(1);

		Assert.assertEquals(line1.getCount(), 2);
		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);
		Assert.assertEquals(line1.getElement(1).getClass(), DrawableBox.class);
		Assert.assertEquals(line2.getCount(), 2);
		Assert.assertEquals(line2.getElement(0).getClass(), TextBox.class);
		Assert.assertEquals(line2.getElement(1).getClass(), DrawableBox.class);
	}

	private void testMixNormal() {
		Document document = mockDocument(20, 20, 100, BreakStrategy.SIMPLE, "123");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getSegmentCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		Paragraph paragraph = (Paragraph) document.getSegment(0);
		Layout layout = paragraph.getLayout();

		Assert.assertNotNull(paragraph);
		Assert.assertEquals(layout.getLineCount(), 1);

		Line line1 = layout.getLine(0);

		Assert.assertEquals(line1.getCount(), 2);
		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);
		Assert.assertEquals(line1.getElement(1).getClass(), DrawableBox.class);

		document = mockDocument(20, 20, 100, BreakStrategy.BALANCED, "123");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getSegmentCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		paragraph = (Paragraph) document.getSegment(0);

		Assert.assertNotNull(paragraph);
		layout = paragraph.getLayout();
		Assert.assertEquals(layout.getLineCount(), 1);

		line1 = layout.getLine(0);

		Assert.assertEquals(line1.getCount(), 2);
		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);
		Assert.assertEquals(line1.getElement(1).getClass(), DrawableBox.class);
	}

	private void testMixFull() {
		Document document = mockDocument(20, 20, 100, BreakStrategy.SIMPLE, "12345");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getSegmentCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		Paragraph paragraph = (Paragraph) document.getSegment(0);
		Layout layout = paragraph.getLayout();

		Assert.assertNotNull(paragraph);
		Assert.assertEquals(layout.getLineCount(), 2);

		Line line1 = layout.getLine(0);
		Line line2 = layout.getLine(1);

		Assert.assertEquals(line2.getCount(), 1);
		Assert.assertEquals(line2.getElement(0).getClass(), DrawableBox.class);
		Assert.assertEquals(line1.getCount(), 1);
		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);

		document = mockDocument(20, 20, 100, BreakStrategy.BALANCED, "12345");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getSegmentCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		paragraph = (Paragraph) document.getSegment(0);
		layout = paragraph.getLayout();

		Assert.assertNotNull(paragraph);
		Assert.assertEquals(layout.getLineCount(), 2);

		line1 = layout.getLine(0);
		line2 = layout.getLine(1);

		Assert.assertEquals(line2.getCount(), 1);
		Assert.assertEquals(line2.getElement(0).getClass(), DrawableBox.class);
		Assert.assertEquals(line1.getCount(), 1);
		Assert.assertEquals(line1.getElement(0).getClass(), TextBox.class);
	}

	private Document mockDocument(int textSize, float drawableWidth, float width, BreakStrategy breakStrategy, CharSequence... s) {
		MockTextPaint textPaint = new MockTextPaint(textSize);
		Measurer measurer = new MockMeasurer(textPaint);
		Document document = Document.obtain();
		MockTextAttribute attribute = new MockTextAttribute(textPaint);
		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), measurer, attribute, null);
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		for (int i = 0; i < s.length; ++i) {
			builder.text(s[i], 0, s[i].length());
			builder.emoticon(Emoticon.obtain(new ColorDrawable(10), drawableWidth, 20));
		}
		List<Segment> list = new ArrayList<>();
		list.add(builder.build());
		document.insertTail(list);

		ParagraphTypesetter typesetter = new ParagraphTypesetter();
		for (int i = 0; i < document.getSegmentCount(); ++i) {
			typesetter.typeset((Paragraph) document.getSegment(i), breakStrategy, (int) width, 1);
		}

		return document;
	}

	@Test
	public void testEmptyLineWithBrk() {
		RenderOption renderOption = new RenderOption();
		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), mMockMeasurer, mMockTextAttribute, renderOption);

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		Paragraph paragraph = builder.build(true);
		Layout layout = paragraph.getLayout();

		ParagraphTypesetter typesetter = new ParagraphTypesetter();
		typesetter.typeset(paragraph, BreakStrategy.SIMPLE, 100, 1);
		Layout current = paragraph.getLayout();
		Assert.assertNotSame(layout, current);

		assertEquals(0, current.getLineCount());
	}

	@Test
	public void testEmptyLineWithoutBrk() {
		RenderOption renderOption = new RenderOption();
		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), mMockMeasurer, mMockTextAttribute, renderOption);

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		Paragraph paragraph = builder.build(false);
		Layout layout = paragraph.getLayout();

		ParagraphTypesetter typesetter = new ParagraphTypesetter();
		typesetter.typeset(paragraph, BreakStrategy.SIMPLE, 100, 1);
		Layout current = paragraph.getLayout();
		Assert.assertNotSame(layout, current);

		assertEquals(0, current.getLineCount());
	}

	@Test
	public void testDesire() {
		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpace(1);
		mPaint.setMockTextSize(1);
		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), mMockMeasurer, mMockTextAttribute, renderOption);

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		Paragraph paragraph = builder.build(false);
		Region region = new Region(100, 10);
		Assert.assertFalse(WorkerScheduler.typeset().desire(paragraph, region, renderOption));

		// TODO 只有一个换行符 按理说这里要留一个行高的空间
		builder = Paragraph.Builder.newBuilder(texasOption);
		paragraph = builder.build(true);
		Assert.assertTrue(WorkerScheduler.typeset().desire(paragraph, region, renderOption));
		Assert.assertEquals(region.getWidth(), 0);
		Assert.assertEquals(region.getHeight(), 0);


		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("12345")
				.brk()
				.text("12");
		paragraph = builder.build(true);
		Assert.assertTrue(WorkerScheduler.typeset().desire(paragraph, region, renderOption));
		Assert.assertEquals(5, region.getWidth());
		Assert.assertEquals(3, region.getHeight());

		builder = Paragraph.Builder.newBuilder(texasOption);
		builder.text("12345");
		paragraph = builder.build(true);
		Assert.assertTrue(WorkerScheduler.typeset().desire(paragraph, region, renderOption));
		Assert.assertEquals(5, region.getWidth());
		Assert.assertEquals(1, region.getHeight());
	}
}