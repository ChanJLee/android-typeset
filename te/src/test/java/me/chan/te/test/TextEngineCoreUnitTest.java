package me.chan.te.test;

import org.junit.Before;
import org.junit.Test;

import me.chan.te.renderer.RenderOption;
import me.chan.te.renderer.TextEngineCore;
import me.chan.te.source.FileTextSource;
import me.chan.te.test.mock.MockTextPaint;

public class TextEngineCoreUnitTest {

	private TextEngineCore mTextEngineCore;
	private RenderOption mRenderOption;
	private MockTextPaint mMockTextPaint;

	@Before
	public void setup() {
		mMockTextPaint = new MockTextPaint();
		mRenderOption = new RenderOption();
		mTextEngineCore = new TextEngineCore(null, mRenderOption);
	}

	@Test
	public void test() {

	}

	private void testTypeset(FileTextSource fileTextSource, int width, int height, RenderOption renderOption) {
		mMockTextPaint.setMockTextSize((int) renderOption.getTextSize());
		mTextEngineCore.reload(renderOption);
		mTextEngineCore.typeset(fileTextSource, width, height);
	}
}
