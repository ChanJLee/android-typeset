package me.chan.texas.text.layout;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.text.TextPaint;

import me.chan.texas.TestUtils;
import me.chan.texas.text.Appearance;
import me.chan.texas.text.DrawContext;
import me.chan.texas.text.Emoticon;

import org.junit.Assert;
import org.junit.Test;

public class EmoticonUnitTest {

	@Test
	public void testClean() {
		if (Emoticon.hasBuffered()) {
			Emoticon.clean();
		}
		Assert.assertFalse(Emoticon.hasBuffered());

		Emoticon emoticon = Emoticon.obtain(new ColorDrawable(1), 1, 2);
		Assert.assertNotNull(emoticon);
		Assert.assertFalse(emoticon.isRecycled());

		emoticon.recycle();
		Assert.assertTrue(emoticon.isRecycled());
		Assert.assertTrue(Emoticon.hasBuffered());
		Emoticon.clean();
		Assert.assertFalse(Emoticon.hasBuffered());
	}

	@Test
	public void test() {
		Emoticon.clean();

		ColorDrawable colorDrawable = new ColorDrawable(1);
		String tag = "msg";
		Appearance bg = new Appearance() {
			@Override
			public void draw(Canvas canvas, TextPaint textPaint, RectF inner, RectF outer, DrawContext context) {

			}
		};
		Appearance fg = new Appearance() {
			@Override
			public void draw(Canvas canvas, TextPaint textPaint, RectF inner, RectF outer, DrawContext context) {

			}
		};
		Emoticon emoticon = Emoticon.obtain(colorDrawable, 1, 2, tag, bg, fg);
		Assert.assertNotNull(emoticon);

		Assert.assertEquals(emoticon.getWidth(), 1, 0);
		Assert.assertEquals(emoticon.getHeight(), 2, 0);
		Assert.assertEquals(emoticon.getDrawable(), colorDrawable);

		DrawableBox drawableBox = emoticon.getDrawableBox();
		Assert.assertNotNull(drawableBox);
		Assert.assertEquals(drawableBox.getDrawable(), colorDrawable);
		Assert.assertEquals(drawableBox.getEmoticon(), emoticon);
		Assert.assertEquals(drawableBox.getWidth(), 1, 0);
		Assert.assertEquals(drawableBox.getHeight(), 2, 0);
		Assert.assertEquals(drawableBox.getTag(), tag);
		Assert.assertEquals(drawableBox.getForeground(), fg);
		Assert.assertEquals(drawableBox.getBackground(), bg);
		Assert.assertFalse(emoticon.isRecycled());

		ColorDrawable colorDrawable2 = new ColorDrawable(2);
		emoticon.setDrawable(colorDrawable2);
		Assert.assertEquals(emoticon.getDrawable(), colorDrawable2);
		tag = "fffff";
		emoticon.setTag(tag);
		Assert.assertEquals(drawableBox.getTag(), tag);


		emoticon.recycle();
		Assert.assertTrue(emoticon.isRecycled());
		TestUtils.testRecycled(emoticon);

		tag = "kkk";
		fg = new Appearance() {
			@Override
			public void draw(Canvas canvas, TextPaint textPaint, RectF inner, RectF outer, DrawContext context) {

			}
		};
		bg = new Appearance() {
			@Override
			public void draw(Canvas canvas, TextPaint textPaint, RectF inner, RectF outer, DrawContext context) {

			}
		};
		Emoticon emoticon1 = emoticon;
		emoticon = Emoticon.obtain(colorDrawable2, 3, 4, tag, bg, fg);
		Assert.assertSame(emoticon, emoticon1);
		drawableBox = emoticon.getDrawableBox();
		Assert.assertNotNull(drawableBox);
		Assert.assertEquals(drawableBox.getDrawable(), colorDrawable2);
		Assert.assertEquals(drawableBox.getEmoticon(), emoticon);
		Assert.assertEquals(drawableBox.getWidth(), 3, 0);
		Assert.assertEquals(drawableBox.getHeight(), 4, 0);
		Assert.assertEquals(drawableBox.getTag(), tag);
		Assert.assertEquals(drawableBox.getForeground(), fg);
		Assert.assertEquals(drawableBox.getBackground(), bg);
		Assert.assertFalse(emoticon.isRecycled());
	}
}
