package me.chan.texas.text.tokenizer;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.HyperSpan;
import me.chan.texas.text.layout.StateList;

public class HyperSpanTokenUnitTest {

	private static HyperSpan newSpan() {
		return new HyperSpan() {
			@Override
			protected void onDraw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer,
				float baselineOffset, StateList states) {
			}

			@Override
			protected void onMeasure(float lineHeight, float baselineOffset) {
				setMeasuredSize(10, 20);
			}
		};
	}

	// ============================================================
	// 工厂 / 池化生命周期
	// ============================================================

	@Test
	public void obtain_returnsNonRecycledTokenWithSpan() {
		HyperSpan span = newSpan();
		HyperSpanToken token = HyperSpanToken.obtain(span);

		Assert.assertFalse(token.isRecycled());
		Assert.assertSame(span, token.getHyperSpan());
		token.recycle();
	}

	@Test
	public void obtain_acceptsNullSpan() {
		HyperSpanToken token = HyperSpanToken.obtain(null);

		Assert.assertFalse(token.isRecycled());
		Assert.assertNull(token.getHyperSpan());
		token.recycle();
	}

	@Test
	public void recycle_clearsSpan_andSetsRecycledFlag() {
		HyperSpan span = newSpan();
		HyperSpanToken token = HyperSpanToken.obtain(span);
		token.recycle();

		Assert.assertTrue(token.isRecycled());
		Assert.assertNull(token.getHyperSpan());
	}

	@Test
	public void recycle_isIdempotent() {
		HyperSpanToken token = HyperSpanToken.obtain(newSpan());
		token.recycle();
		token.recycle();
		Assert.assertTrue(token.isRecycled());
		Assert.assertNull(token.getHyperSpan());
	}

	@Test
	public void obtainAfterRecycle_resetsSpanToNew() {
		HyperSpan first = newSpan();
		HyperSpanToken a = HyperSpanToken.obtain(first);
		a.recycle();

		HyperSpan second = newSpan();
		HyperSpanToken b = HyperSpanToken.obtain(second);
		// 池可能回收同一实例，但 mHyperSpan 必须更新为 second
		Assert.assertSame(second, b.getHyperSpan());
		Assert.assertNotSame(first, b.getHyperSpan());
		b.recycle();
	}

	// ============================================================
	// type / semantics
	// ============================================================

	@Test
	public void getType_alwaysSymbol() {
		HyperSpanToken token = HyperSpanToken.obtain(newSpan());
		Assert.assertEquals(Token.TYPE_SYMBOL, token.getType());
		token.recycle();
	}

	@Test
	public void getType_isSymbolEvenWithNullSpan() {
		HyperSpanToken token = HyperSpanToken.obtain(null);
		Assert.assertEquals(Token.TYPE_SYMBOL, token.getType());
		token.recycle();
	}

	@Test
	public void getSemantics_returnsHyperTextLabel() {
		HyperSpanToken token = HyperSpanToken.obtain(newSpan());
		Assert.assertEquals("超文字", token.getSemantics());
		token.recycle();
	}

	@Test
	public void isRtl_inheritsFalseDefault() {
		HyperSpanToken token = HyperSpanToken.obtain(newSpan());
		Assert.assertFalse(token.isRtl());
		token.recycle();
	}

	// ============================================================
	// checkAttribute 委托给 HyperSpan
	// ============================================================

	@Test
	public void checkAttribute_falseByDefault() {
		HyperSpanToken token = HyperSpanToken.obtain(newSpan());
		Assert.assertFalse(token.checkAttribute(HyperSpan.AVOID_LINE_HEADER));
		Assert.assertFalse(token.checkAttribute(HyperSpan.AVOID_LINE_TAIL));
		Assert.assertFalse(token.checkAttribute(HyperSpan.STRETCH_LEFT));
		Assert.assertFalse(token.checkAttribute(HyperSpan.STRETCH_RIGHT));
		token.recycle();
	}

	@Test
	public void checkAttribute_reflectsSpanAttribute() {
		HyperSpan span = newSpan();
		span.addAttribute(HyperSpan.AVOID_LINE_HEADER);
		span.addAttribute(HyperSpan.STRETCH_RIGHT);

		HyperSpanToken token = HyperSpanToken.obtain(span);
		Assert.assertTrue(token.checkAttribute(HyperSpan.AVOID_LINE_HEADER));
		Assert.assertTrue(token.checkAttribute(HyperSpan.STRETCH_RIGHT));
		Assert.assertFalse(token.checkAttribute(HyperSpan.AVOID_LINE_TAIL));
		Assert.assertFalse(token.checkAttribute(HyperSpan.STRETCH_LEFT));
		token.recycle();
	}

	@Test
	public void checkAttribute_changesAfterSpanMutated() {
		HyperSpan span = newSpan();
		HyperSpanToken token = HyperSpanToken.obtain(span);
		Assert.assertFalse(token.checkAttribute(HyperSpan.AVOID_LINE_HEADER));

		span.addAttribute(HyperSpan.AVOID_LINE_HEADER);
		// token 直接代理给 span，所以应立即生效
		Assert.assertTrue(token.checkAttribute(HyperSpan.AVOID_LINE_HEADER));

		span.removeAttribute(HyperSpan.AVOID_LINE_HEADER);
		Assert.assertFalse(token.checkAttribute(HyperSpan.AVOID_LINE_HEADER));
		token.recycle();
	}

	@Test
	public void checkAttribute_falseWhenSpanIsNull() {
		HyperSpanToken token = HyperSpanToken.obtain(null);
		// 任意属性都返回 false 而不是抛 NPE
		Assert.assertFalse(token.checkAttribute(HyperSpan.AVOID_LINE_HEADER));
		Assert.assertFalse(token.checkAttribute(HyperSpan.STRETCH_RIGHT));
		Assert.assertFalse(token.checkAttribute(0));
		Assert.assertFalse(token.checkAttribute(31));
		token.recycle();
	}

	// ============================================================
	// hasSymbolTypefaceAttributes
	// ============================================================

	@Test
	public void hasSymbolTypefaceAttributes_falseByDefault() {
		HyperSpanToken token = HyperSpanToken.obtain(newSpan());
		Assert.assertFalse(token.hasSymbolTypefaceAttributes());
		token.recycle();
	}

	@Test
	public void hasSymbolTypefaceAttributes_falseForKinsokuOnly() {
		HyperSpan span = newSpan();
		span.addAttribute(HyperSpan.AVOID_LINE_HEADER);
		span.addAttribute(HyperSpan.AVOID_LINE_TAIL);

		HyperSpanToken token = HyperSpanToken.obtain(span);
		Assert.assertFalse(token.hasSymbolTypefaceAttributes());
		token.recycle();
	}

	@Test
	public void hasSymbolTypefaceAttributes_trueForStretchLeft() {
		HyperSpan span = newSpan();
		span.addAttribute(HyperSpan.STRETCH_LEFT);

		HyperSpanToken token = HyperSpanToken.obtain(span);
		Assert.assertTrue(token.hasSymbolTypefaceAttributes());
		token.recycle();
	}

	@Test
	public void hasSymbolTypefaceAttributes_trueForStretchRight() {
		HyperSpan span = newSpan();
		span.addAttribute(HyperSpan.STRETCH_RIGHT);

		HyperSpanToken token = HyperSpanToken.obtain(span);
		Assert.assertTrue(token.hasSymbolTypefaceAttributes());
		token.recycle();
	}

	@Test
	public void hasSymbolTypefaceAttributes_trueForKinsokuPlusStretch() {
		HyperSpan span = newSpan();
		span.addAttribute(HyperSpan.AVOID_LINE_HEADER);
		span.addAttribute(HyperSpan.STRETCH_LEFT);

		HyperSpanToken token = HyperSpanToken.obtain(span);
		Assert.assertTrue(token.hasSymbolTypefaceAttributes());
		token.recycle();
	}

	@Test
	public void hasSymbolTypefaceAttributes_falseWhenSpanIsNull() {
		HyperSpanToken token = HyperSpanToken.obtain(null);
		Assert.assertFalse(token.hasSymbolTypefaceAttributes());
		token.recycle();
	}

	// ============================================================
	// toString
	// ============================================================

	@Test
	public void toString_recycled_returnsRecycledMarker() {
		HyperSpanToken token = HyperSpanToken.obtain(newSpan());
		token.recycle();
		Assert.assertEquals("<recycled>", token.toString());
	}

	@Test
	public void toString_includesSemanticsAndSpanRendering() {
		HyperSpan span = newSpan();
		HyperSpanToken token = HyperSpanToken.obtain(span);

		String s = token.toString();
		Assert.assertTrue("got: " + s, s.startsWith("[超文字 <"));
		Assert.assertTrue(s.endsWith(">]"));
		Assert.assertTrue("expected span's toString to appear, got: " + s,
			s.contains(span.toString()));
		token.recycle();
	}

	@Test
	public void toString_withNullSpan_rendersNullPlaceholder() {
		HyperSpanToken token = HyperSpanToken.obtain(null);
		Assert.assertEquals("[超文字 <null>]", token.toString());
		token.recycle();
	}

	// ============================================================
	// 多 token 实例独立性
	// ============================================================

	@Test
	public void multipleTokens_holdSeparateSpans_independently() {
		HyperSpan s1 = newSpan();
		HyperSpan s2 = newSpan();
		s1.addAttribute(HyperSpan.AVOID_LINE_HEADER);
		s2.addAttribute(HyperSpan.STRETCH_RIGHT);

		HyperSpanToken t1 = HyperSpanToken.obtain(s1);
		HyperSpanToken t2 = HyperSpanToken.obtain(s2);

		Assert.assertNotSame(t1, t2);
		Assert.assertSame(s1, t1.getHyperSpan());
		Assert.assertSame(s2, t2.getHyperSpan());

		Assert.assertTrue(t1.checkAttribute(HyperSpan.AVOID_LINE_HEADER));
		Assert.assertFalse(t1.checkAttribute(HyperSpan.STRETCH_RIGHT));

		Assert.assertFalse(t2.checkAttribute(HyperSpan.AVOID_LINE_HEADER));
		Assert.assertTrue(t2.checkAttribute(HyperSpan.STRETCH_RIGHT));

		t1.recycle();
		t2.recycle();
	}
}
