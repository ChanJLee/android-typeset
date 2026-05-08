package me.chan.texas.text;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.layout.StateList;
import me.chan.texas.text.tokenizer.Token;

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
		Assert.assertFalse(span.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER));
		Assert.assertFalse(span.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL));
		Assert.assertFalse(span.checkAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT));
		Assert.assertFalse(span.checkAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT));
	}

	@Test
	public void testAttributeAddCheckRemove() {
		MyHypeSpan span = new MyHypeSpan();

		span.addAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER);
		Assert.assertTrue(span.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER));
		Assert.assertFalse(span.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL));
		Assert.assertTrue(span.hasSymbolAttributes());
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());

		span.addAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT);
		Assert.assertTrue(span.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER));
		Assert.assertTrue(span.checkAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT));
		Assert.assertTrue(span.hasSymbolTypefaceAttributes());

		span.removeAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER);
		Assert.assertFalse(span.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER));
		Assert.assertTrue(span.checkAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT));
		Assert.assertTrue(span.hasSymbolAttributes());

		span.removeAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT);
		Assert.assertFalse(span.hasSymbolAttributes());
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());
		Assert.assertEquals(0, span.getSymbolAttributes());
	}

	@Test
	public void testHasTypefaceAttributesIndependentBits() {
		// DrawableSpan 仅支持 KINSOKU_AVOID_HEADER/TAIL 与 STRETCH_LEFT/RIGHT，
		// 其中只有 STRETCH 视为字形属性，KINSOKU 不算。
		MyHypeSpan span = new MyHypeSpan();
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());

		span.addAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT);
		Assert.assertTrue(span.hasSymbolTypefaceAttributes());
		span.removeAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT);
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());

		span.addAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT);
		Assert.assertTrue(span.hasSymbolTypefaceAttributes());
		span.removeAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT);
		Assert.assertFalse(span.hasSymbolTypefaceAttributes());

		span.addAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER);
		span.addAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL);
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