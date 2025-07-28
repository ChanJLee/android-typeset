package me.chan.texas.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.layout.DrawableBox;

public class EmoticonUnitTest {

	@Test
	public void testEmoticon() throws NoSuchFieldException, IllegalAccessException {
		Drawable drawable = new ColorDrawable(19);
		Emoticon emoticon = Emoticon.obtain(drawable, 1, 2);
		Assert.assertNotNull(emoticon);
		Assert.assertSame(emoticon.getDrawable(), drawable);
		Assert.assertEquals(emoticon.getWidth(), 1, 0);
		Assert.assertEquals(emoticon.getHeight(), 2, 0);

		Drawable drawable1 = new ColorDrawable(20);
		emoticon.setDrawable(drawable1);
		Assert.assertSame(drawable1, emoticon.getDrawable());

		DrawableBox drawableBox = (DrawableBox) emoticon.getDrawableBox();
		Assert.assertSame(drawableBox.getSpan(), emoticon);
	}

	@Test
	public void test() {
		ColorDrawable colorDrawable = new ColorDrawable(1);
		String tag = "msg";
		Appearance bg = new Appearance() {
			@Override
			public void draw(TexasCanvas canvas, TexasPaint textPaint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		Appearance fg = new Appearance() {
			@Override
			public void draw(TexasCanvas canvas, TexasPaint textPaint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		Emoticon emoticon = Emoticon.obtain(colorDrawable, 1, 2, tag, bg, fg);
		Assert.assertNotNull(emoticon);

		Assert.assertEquals(emoticon.getWidth(), 1, 0);
		Assert.assertEquals(emoticon.getHeight(), 2, 0);
		Assert.assertEquals(emoticon.getDrawable(), colorDrawable);

		DrawableBox drawableBox = (DrawableBox) emoticon.getDrawableBox();
		Assert.assertNotNull(drawableBox);
		Assert.assertEquals(drawableBox.getSpan(), emoticon);
		Assert.assertEquals(drawableBox.getWidth(), 1, 0);
		Assert.assertEquals(drawableBox.getHeight(), 2, 0);
		Assert.assertEquals(drawableBox.getTag(), tag);
		Assert.assertEquals(drawableBox.getForeground(), fg);
		Assert.assertEquals(drawableBox.getBackground(), bg);

		ColorDrawable colorDrawable2 = new ColorDrawable(2);
		emoticon.setDrawable(colorDrawable2);
		Assert.assertEquals(emoticon.getDrawable(), colorDrawable2);
		tag = "fffff";
		emoticon.setTag(tag);
		Assert.assertEquals(drawableBox.getTag(), tag);
	}
}
