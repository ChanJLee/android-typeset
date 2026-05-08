package me.chan.texas.text;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
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
		Assert.assertEquals(0, myHypeSpan.getWidth(), 0);
		Assert.assertEquals(0, myHypeSpan.getHeight(), 0);
		Assert.assertSame(bg, myHypeSpan.getBackground());
		Assert.assertSame(fg, myHypeSpan.getForeground());
		Assert.assertSame(fg, myHypeSpan.getTag());
		myHypeSpan.measure();
		Assert.assertEquals(10, myHypeSpan.getWidth(), 0);
		Assert.assertEquals(20, myHypeSpan.getHeight(), 0);
	}

	@Test
	public void testAttributeDefaults() {
		MyHypeSpan span = new MyHypeSpan();

		Assert.assertFalse(span.hasSymbolAttributes());
		Assert.assertEquals(0, span.getSymbolAttributes());
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());
		Assert.assertFalse(span.checkAttribute(HyperSpan.AVOID_LINE_HEADER));
		Assert.assertFalse(span.checkAttribute(HyperSpan.AVOID_LINE_TAIL));
		Assert.assertFalse(span.checkAttribute(HyperSpan.STRETCH_LEFT));
		Assert.assertFalse(span.checkAttribute(HyperSpan.STRETCH_RIGHT));
	}

	@Test
	public void testAttributeAddCheckRemove() {
		MyHypeSpan span = new MyHypeSpan();

		span.addAttribute(HyperSpan.AVOID_LINE_HEADER);
		Assert.assertTrue(span.checkAttribute(HyperSpan.AVOID_LINE_HEADER));
		Assert.assertFalse(span.checkAttribute(HyperSpan.AVOID_LINE_TAIL));
		Assert.assertTrue(span.hasSymbolAttributes());
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());

		span.addAttribute(HyperSpan.STRETCH_RIGHT);
		Assert.assertTrue(span.checkAttribute(HyperSpan.AVOID_LINE_HEADER));
		Assert.assertTrue(span.checkAttribute(HyperSpan.STRETCH_RIGHT));
		Assert.assertTrue(span.hasSymbolTypefaceAttributes());

		span.removeAttribute(HyperSpan.AVOID_LINE_HEADER);
		Assert.assertFalse(span.checkAttribute(HyperSpan.AVOID_LINE_HEADER));
		Assert.assertTrue(span.checkAttribute(HyperSpan.STRETCH_RIGHT));
		Assert.assertTrue(span.hasSymbolAttributes());

		span.removeAttribute(HyperSpan.STRETCH_RIGHT);
		Assert.assertFalse(span.hasSymbolAttributes());
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());
		Assert.assertEquals(0, span.getSymbolAttributes());
	}

	@Test
	public void testHasTypefaceAttributesIndependentBits() {
		// HyperSpan 仅支持 KINSOKU_AVOID_HEADER/TAIL 与 STRETCH_LEFT/RIGHT，
		// 其中只有 STRETCH 视为字形属性，KINSOKU 不算。
		MyHypeSpan span = new MyHypeSpan();
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());

		span.addAttribute(HyperSpan.STRETCH_LEFT);
		Assert.assertTrue(span.hasSymbolTypefaceAttributes());
		span.removeAttribute(HyperSpan.STRETCH_LEFT);
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());

		span.addAttribute(HyperSpan.STRETCH_RIGHT);
		Assert.assertTrue(span.hasSymbolTypefaceAttributes());
		span.removeAttribute(HyperSpan.STRETCH_RIGHT);
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());

		span.addAttribute(HyperSpan.AVOID_LINE_HEADER);
		span.addAttribute(HyperSpan.AVOID_LINE_TAIL);
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());
	}
}

class MyHypeSpan extends HyperSpan {

	@Override
	protected void onDraw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states) {
		canvas.drawText("hello", inner.left, inner.bottom - baselineOffset, paint);
	}

	@Override
	protected void onMeasure(float lineHeight, float baselineOffset) {
		setMeasuredSize(10, 20);
	}

	public void measure() {
		setMeasuredSize(10, 20);
	}
}