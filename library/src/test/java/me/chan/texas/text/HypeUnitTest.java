package me.chan.texas.text;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.layout.DrawableBox;
import me.chan.texas.text.layout.StateList;

public class HypeUnitTest {

	@Test
	public void test() {
		Appearance bg = new Appearance() {
			@Override
			public void draw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		Appearance fg = new Appearance() {
			@Override
			public void draw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, RendererContext context) {

			}
		};
		MyHypeSpan myHypeSpan = new MyHypeSpan();
		myHypeSpan.setBackground(bg);
		myHypeSpan.setForeground(fg);
		myHypeSpan.setTag(fg);
		DrawableBox box = (DrawableBox) myHypeSpan.getDrawableBox();
		Assert.assertEquals(0, box.getWidth(), 0);
		Assert.assertEquals(0, box.getHeight(), 0);
		Assert.assertSame(bg, box.getBackground());
		Assert.assertSame(fg, box.getForeground());
		Assert.assertSame(fg, box.getTag());
		myHypeSpan.measure(1, 1);
		Assert.assertEquals(10, box.getWidth(), 0);
		Assert.assertEquals(20, box.getHeight(), 0);
	}
}

class MyHypeSpan extends HypeSpan {

	@Override
	protected void onDraw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states) {
		canvas.drawText("hello", inner.left, inner.bottom - baselineOffset, paint);
	}

	@Override
	protected void onMeasure(float lineHeight, float baselineOffset) {
		setMeasuredSize(10, 20);
	}
}
