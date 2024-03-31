package me.chan.texas.typesetter.tex;

import me.chan.texas.TexasOption;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.adapter.TextAdapter;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.source.FileTextSource;
import me.chan.texas.source.SourceOpenException;
import com.shanbay.lib.texas.test.mock.MockTextPaint;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.tokenizer.Tokenizer;
import me.chan.texas.typesetter.AbsParagraphTypesetter;
import me.chan.texas.utils.IntStack;
import me.chan.texas.utils.IntArray;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import opennlp.tools.tokenize.TokenizerModel;

public class TexRefactorUnitTest {
	static {
		TokenizerModel model = null;
		try {
			model = new TokenizerModel(new File("../library/src/main/assets/texas/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"));
			Tokenizer.setup(model);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private AbsParagraphTypesetter mTypesetter;
	private AbsParagraphTypesetter mTypesetterCompat;

	private RenderOption mRenderOption;
	private MockTextPaint mMockTextPaint;
	private Measurer mMockMeasurer;
	private TextAttribute mTextAttribute;

	@Before
	public void setup() throws NoSuchFieldException, IllegalAccessException {
		mMockTextPaint = new MockTextPaint();
		mRenderOption = new RenderOption();
		mMockMeasurer = new MockMeasurer(mMockTextPaint);
		mTextAttribute = new TextAttribute(mMockMeasurer);
		mTypesetter = new TexParagraphTypesetter();
		mTypesetterCompat = new TexParagraphTypesetterCompat();
	}

	@Test
	public void test() throws IOException, InterruptedException, IllegalAccessException, NoSuchFieldException, SourceOpenException, ParseException {
		if (!AbsParagraphTypesetter.DEBUG) {
			return;
		}

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

	private void test(String fileName) throws FileNotFoundException, InterruptedException, SourceOpenException, NoSuchFieldException, ParseException {
		mRenderOption.setTextSize(2);
		mRenderOption.setBreakStrategy(BreakStrategy.BALANCED);

		testTypeset(new FileTextSource(fileName), 1080);
	}

	private void testTypeset(FileTextSource source, int width)
			throws NoSuchFieldException, InterruptedException, SourceOpenException, ParseException {
		// todo test
//		CacheSource<CharSequence> fileTextSource = new CacheSource<>(source);
//		mMockTextPaint.setMockTextSize((int) mRenderOption.getTextSize());
//		TextAdapter textAdapter = new TextAdapter();
//		textAdapter.setSource(fileTextSource);
//		Document document = textAdapter.getDocument(new TexasOption(Hyphenation.getInstance(), mMockMeasurer, mTextAttribute, mRenderOption));
//		for (int i = 0; i < document.getSegmentCount(); ++i) {
//			Segment segment = document.getSegment(i);
//			if (!(segment instanceof Paragraph)) {
//				continue;
//			}
//
//			Paragraph paragraph = (Paragraph) segment;
//			mTypesetter.typeset(paragraph, BreakStrategy.BALANCED, width);
//
//			IntStack stack = (IntStack) mTypesetter.getInternalState();
//			Layout layout = paragraph.getLayout();
//			layout.clear();
//
//			mTypesetterCompat.typeset(paragraph, BreakStrategy.BALANCED, width);
//			IntArray array = (IntArray) mTypesetterCompat.getInternalState();
//
//			if (stack == null) {
//				Assert.assertNull(array);
//				continue;
//			}
//
//			Assert.assertEquals(stack.size() + 1, array.size());
//			for (int k = 1; k < array.size(); ++k) {
//				System.out.println(stack);
//				Assert.assertEquals(array.get(k), stack.pop());
//			}
//		}
	}
}
