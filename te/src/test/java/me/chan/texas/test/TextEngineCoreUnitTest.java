package me.chan.texas.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import me.chan.texas.Texas;
import me.chan.texas.text.RenderOption;
import me.chan.texas.renderer.TextEngineCore;
import me.chan.texas.source.FileTextSource;
import me.chan.texas.test.mock.MockContext;
import me.chan.texas.test.mock.MockMeasurer;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.test.mock.MockThreadHandler;
import me.chan.texas.text.Document;
import me.chan.texas.text.Page;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextBox;

public class TextEngineCoreUnitTest {

	private TextEngineCore mTextEngineCore;
	private RenderOption mRenderOption;
	private MockTextPaint mMockTextPaint;

	@Before
	public void setup() throws NoSuchFieldException, IllegalAccessException {
		mMockTextPaint = new MockTextPaint();
		mRenderOption = new RenderOption();
		mTextEngineCore = new TextEngineCore(new MockContext(), null, mRenderOption, mMockTextPaint);
		MockMeasurer mockMeasurer = new MockMeasurer(mMockTextPaint);


		Class<?> clazz = TextEngineCore.class;
		Field field = clazz.getDeclaredField("mTextPaint");
		field.setAccessible(true);
		field.set(mTextEngineCore, mMockTextPaint);

		TextAttribute textAttribute = new TextAttribute(mockMeasurer);
		field = clazz.getDeclaredField("mTextAttribute");
		field.setAccessible(true);
		field.set(mTextEngineCore, textAttribute);


		field = clazz.getDeclaredField("mMeasurer");
		field.setAccessible(true);
		field.set(mTextEngineCore, mockMeasurer);

		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		memoryOption.setDocumentPageInitialCapacity(4)
				.setDocumentSegmentInitialCapacity(4)
				.setPageSegmentInitialCapacity(4)
				.setParagraphElementInitialCapacity(4)
				.setParagraphLineBoxInitialCapacity(4)
				.setParagraphLineInitialCapacity(4);
		Texas.setMemoryOption(memoryOption);
	}

	@Test
	public void test() throws FileNotFoundException, InterruptedException, IllegalAccessException, NoSuchFieldException {
		mRenderOption.setTextSize(1);
		testTypeset(new FileTextSource("../app/src/main/assets/TheBookAndTheSword.txt"), 1080, 1024, mRenderOption);
		mRenderOption.setTextSize(18);
		testTypeset(new FileTextSource("../app/src/main/assets/TheBookAndTheSword.txt"), 1080, 1024, mRenderOption);
		mRenderOption.setTextSize(540);
		testTypeset(new FileTextSource("../app/src/main/assets/TheBookAndTheSword.txt"), 1080, 1024, mRenderOption);
		mRenderOption.setTextSize(1080);
		testTypeset(new FileTextSource("../app/src/main/assets/TheBookAndTheSword.txt"), 1080, 1024, mRenderOption);
	}

	private int mWhat;
	private Object mValue;

	private void testTypeset(FileTextSource fileTextSource, int width, int height, RenderOption renderOption)
			throws NoSuchFieldException, InterruptedException, IllegalAccessException {
		mMockTextPaint.setMockTextSize((int) renderOption.getTextSize());
		mTextEngineCore.reload(renderOption);
		Class<?> clazz = TextEngineCore.class;
		Field field = clazz.getDeclaredField("mHandler");
		field.setAccessible(true);
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		field.set(mTextEngineCore, new MockThreadHandler() {
			@Override
			public void handleMessage(int what, Object value) {
				mWhat = what;
				mValue = value;
				countDownLatch.countDown();
			}
		});
		mTextEngineCore.typeset(fileTextSource, width, height);
		countDownLatch.await();

		if (mValue instanceof Throwable) {
			Throwable throwable = (Throwable) mValue;
			throwable.printStackTrace();
		}

		Assert.assertEquals(mWhat, 2);
		checkDocument((Document) mValue);
	}

	private void checkDocument(Document document) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < document.getPageCount(); ++i) {
			Page page = document.getPage(i);
			Assert.assertNotEquals(page.getSegmentCount(), 0);
			for (int j = 0; j < page.getSegmentCount(); ++j) {
				Paragraph paragraph = (Paragraph) page.getSegment(j);
				for (int x = 0; x < paragraph.getLineCount(); ++x) {
					Paragraph.Line line = paragraph.getLine(x);
					for (int y = 0; y < line.getCount(); ++y) {
						TextBox box = (TextBox) line.getBox(y);
						String string = box.toString();
						if (box.isPenalty() && string.length() != 0) {
							Assert.assertEquals(string.charAt(string.length() - 1), '-');
							stringBuilder.append(string.substring(0, string.length() - 1));
						} else {
							stringBuilder.append(string);
						}
					}
				}
			}
		}
		String origin = (String) document.getRaw();
		origin = origin.replaceAll("\\p{Z}+|\\t|\\r|\\n", "");
		Assert.assertEquals(origin, stringBuilder.toString());
		document.recycle();
	}
}
