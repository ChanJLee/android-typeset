package me.chan.texas.text.layout;

import me.chan.texas.misc.RectF;

import me.chan.texas.TestUtils;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.Appearance;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.TextAttribute;

import org.junit.Assert;
import org.junit.Test;

public class BoxUnitTest {

	@Test
	public void test() {
		String tag = "hello";

		MockBox mockBox = new MockBox(1, 2);

		Assert.assertFalse(mockBox.isRecycled());
		Assert.assertEquals(mockBox.mWidth, 1, 0);
		Assert.assertEquals(mockBox.mHeight, 2, 0);
		Assert.assertNull(mockBox.getTag());
		Assert.assertNull(mockBox.mBackground);
		Assert.assertNull(mockBox.mForeground);

		// test set tag
		mockBox.setTag(tag);
		Assert.assertSame(tag, mockBox.getTag());

		// test set bg
		Appearance bg = new Appearance() {
			@Override
			public void draw(TexasCanvas canvas, TexasPaint textPaint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		mockBox.setBackground(bg);
		Assert.assertSame(mockBox.getBackground(), bg);

		// test set fg
		Appearance fg = new Appearance() {
			@Override
			public void draw(TexasCanvas canvas, TexasPaint textPaint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		mockBox.setForeground(fg);
		Assert.assertSame(fg, mockBox.getForeground());

		// check link
		// test recycle
		mockBox.recycle();
//		Assert.assertTrue(mockBox.isRecycled());
//		Assert.assertEquals(mockBox.mWidth, 0, 0);
//		Assert.assertEquals(mockBox.mHeight, 0, 0);
//		Assert.assertNull(mockBox.getTag());
//		Assert.assertNull(mockBox.mBackground);
//		Assert.assertNull(mockBox.mForeground);
//		Assert.assertNull(mockBox.mPrev);
//		Assert.assertNull(mockBox.mNext);
//		Assert.assertFalse(mockBox.isSelected());
		TestUtils.testRecycled(mockBox);
	}

	private static class MockBox extends Box {

		/**
		 * @param width  宽度
		 * @param height 高度
		 */
		public MockBox(float width, float height) {
			super(width, height);
		}

		@Override
		public boolean isIsolate(boolean backward, int index, int count) {
			return false;
		}

		@Override
		public void draw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states) {

		}

		@Override
		public void onMeasure(Measurer measurer, TextAttribute textAttribute) {

		}
	}
}
