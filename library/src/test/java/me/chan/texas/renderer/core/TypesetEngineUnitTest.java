package me.chan.texas.renderer.core;

import android.graphics.RectF;

import com.shanbay.lib.texas.test.mock.MockTextPaint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import me.chan.texas.Texas;
import me.chan.texas.adapter.TextAdapter;
import me.chan.texas.di.DaggerFakeTexasComponent;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.LoadingStrategy;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.source.FileTextSource;
import me.chan.texas.source.SourceOpenException;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TypesetContext;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.text.tokenizer.Tokenizer;
import me.chan.texas.utils.concurrency.TaskQueue;
import opennlp.tools.tokenize.TokenizerModel;

public class TypesetEngineUnitTest {
	static {
		TokenizerModel model = null;
		try {
			model = new TokenizerModel(new File("../library/src/main/assets/texas/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"));
			Tokenizer.setup(model);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private TypesetEngine mTypesetEngine;
	private RenderOption mRenderOption;

	@Before
	public void setup() throws NoSuchFieldException, IllegalAccessException {
		mRenderOption = new RenderOption();

		Texas.setTexasComponent(DaggerFakeTexasComponent.factory().create());

		mTypesetEngine = new TypesetEngine(mRenderOption, TaskQueue.Token.newInstance());
	}

	@Test
	public void test() throws IOException, InterruptedException, IllegalAccessException, NoSuchFieldException, SourceOpenException {
		Assert.assertNotNull(Tokenizer.getInstance(null));
		test("../app/src/main/assets/TheBookAndTheSword.txt");
		test("../app/src/main/assets/harry1.txt");
		test("../app/src/main/assets/harry2.txt");
		test("../app/src/main/assets/harry3.txt");
		test("../app/src/main/assets/harry4.txt");
		test("../app/src/main/assets/harry5.txt");
		test("../app/src/main/assets/harry6.txt");
		test("../app/src/main/assets/live.txt");
	}

	private void test(String fileName) throws FileNotFoundException, InterruptedException, SourceOpenException, IllegalAccessException, NoSuchFieldException {
//		mRenderOption.setTextSize(1);
//		testTypeset(new FileTextSource(fileName), 1080, 1024, mRenderOption, false);
//		mRenderOption.setTextSize(18);
//		testTypeset(new FileTextSource(fileName), 1080, 1024, mRenderOption, true);
//		mRenderOption.setTextSize(540);
//		testTypeset(new FileTextSource(fileName), 1080, 1024, mRenderOption, true);
//		Assert.assertSame(current, document);
//		System.out.println("text size 540, line count: " + getDocumentLineCount(document));
//		mRenderOption.setTextSize(1080);
//		testTypeset(new FileTextSource(fileName), 1080, 1024, mRenderOption, true);
//		Assert.assertSame(current, document);
//		System.out.println("text size 1080, line count: " + getDocumentLineCount(document));
//		mRenderOption.setTextSize(1081);
//		testTypeset(new FileTextSource(fileName), 1080, 1024, mRenderOption, true);
//		Assert.assertSame(current, document);
//		current = testTypeset(new FileTextSource(fileName), 1080, 1024, mRenderOption, false);
	}

	private int getDocumentLineCount(Document document) {
		int count = 0;
		for (int i = 0; i < document.getSegmentCount(); ++i) {
			Segment segment = document.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}
			Paragraph paragraph = (Paragraph) segment;
			Layout layout = paragraph.getLayout();
			count += layout.getLineCount();
		}
		return count;
	}

	private volatile int mWhat;
	private volatile Object mValue;

	private void testTypeset(FileTextSource source, int width, int height, RenderOption renderOption, boolean reload)
			throws NoSuchFieldException, InterruptedException, IllegalAccessException, SourceOpenException {
		TextAdapter textAdapter = new TextAdapter();
		textAdapter.setSource(source);
		if (reload) {
			mTypesetEngine.load("reload", width, LoadingStrategy.INIT, textAdapter, new TypesetEngine.Listener() {
				@Override
				public void onStart(LoadingStrategy strategy) {

				}

				@Override
				public void onFailure(LoadingStrategy strategy, Throwable throwable) {

				}

				@Override
				public void onSuccess(LoadingStrategy strategy, PaintSet paintSet, Document doc, int start, int end) {
					try {
						checkDocument(doc, (String) source.open(strategy));
					} catch (SourceOpenException e) {
						throw new RuntimeException(e);
					}
				}
			});
		} else {
			// TODO
			// mTypesetEngine.resize(width, document);
		}
	}

	private void checkDocument(Document document, String origin) {
//		StringBuilder stringBuilder = new StringBuilder();
//		TextAttribute textAttribute = new TextAttribute(new MockMeasurer(mMockTextPaint));
//
//		for (int j = 0; j < document.getSegmentCount(); ++j) {
//			Paragraph paragraph = (Paragraph) document.getSegment(j);
//			Layout layout = paragraph.getLayout();
//			for (int x = 0; x < layout.getLineCount(); ++x) {
//				Line line = layout.getLine(x);
//				for (int y = 0; y < line.getCount(); ++y) {
//					Element element = line.getElement(y);
//					if (!(element instanceof TextBox)) {
//						continue;
//					}
//
//					TextBox box = (TextBox) element;
//					String string = box.toString();
//					if (box.isPenalty() && string.length() != 0) {
//						Assert.assertEquals(string.charAt(string.length() - 1), '-');
//						stringBuilder.append(string.substring(0, string.length() - 1));
//					} else {
//						stringBuilder.append(string);
//					}
//				}
//			}
//		}
//		origin = origin.replaceAll("\\p{Z}+|\\t|\\r|\\n", "");
//		String current = stringBuilder.toString();
//		if (!current.equals(origin)) {
//			printDiff(origin, current);
//			throw new RuntimeException("check result failed");
//		}
	}

	private static void printDiff(String origin, String current) {
		int s1 = origin.length();
		int s2 = current.length();

		if (s1 != s2) {
			System.out.println("len not equals, " + s1 + " " + s2);
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
	public void testVisitor() throws FileNotFoundException, InterruptedException, SourceOpenException, IllegalAccessException, NoSuchFieldException, ParagraphVisitor.VisitException {
//		mRenderOption.setTextSize(1);
//		Document document = testTypeset(new FileTextSource("../app/src/main/assets/line_test.txt"), 15, 1024, mRenderOption, false);
//		Paragraph paragraph = (Paragraph) document.getSegment(0);
//
//		TestVisitor visitor = new TestVisitor() {
//			@Override
//			protected void onVisitBox(Box box, RectF inner, RectF outer, TypesetContext context) {
//				super.onVisitBox(box, inner, outer, context);
//				sendVisitSig(SIG_STOP_LINE_VISIT);
//			}
//		};
//		visitor.visit(paragraph, mRenderOption);
//		Assert.assertEquals(visitor.lineEndCount, visitor.lineStartCount);
//		Assert.assertEquals(visitor.lineStartCount, 2);
//		Assert.assertEquals(visitor.paraEndCount, visitor.paraStartCount);
//		Assert.assertEquals(visitor.paraEndCount, 1);
//		Assert.assertEquals(visitor.boxCount, 2);
//
//		visitor = new TestVisitor() {
//			@Override
//			protected void onVisitBox(Box box, RectF inner, RectF outer, TypesetContext context) {
//				super.onVisitBox(box, inner, outer, context);
//				sendVisitSig(SIG_STOP_PARA_VISIT);
//			}
//		};
//		visitor.visit(paragraph, mRenderOption);
//		Assert.assertEquals(visitor.lineEndCount, visitor.lineStartCount);
//		Assert.assertEquals(visitor.lineStartCount, 1);
//		Assert.assertEquals(visitor.paraEndCount, visitor.paraStartCount);
//		Assert.assertEquals(visitor.paraEndCount, 1);
//		Assert.assertEquals(visitor.boxCount, 1);
	}

	private static class TestVisitor extends ParagraphVisitor {

		public int lineStartCount;
		public int lineEndCount;
		public int paraStartCount;
		public int paraEndCount;
		public int boxCount;

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {
			++paraStartCount;
		}

		@Override
		protected void onVisitParagraphEnd(Paragraph paragraph) {
			++paraEndCount;
		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {
			++lineStartCount;
		}

		@Override
		protected void onVisitLineEnd(Line line, float x, float y) {
			++lineEndCount;
			Assert.assertNotNull(line.getInfoMsg());
		}

		@Override
		protected void onVisitBox(Box box, RectF inner, RectF outer, TypesetContext context) {
			++boxCount;
		}
	}
}
