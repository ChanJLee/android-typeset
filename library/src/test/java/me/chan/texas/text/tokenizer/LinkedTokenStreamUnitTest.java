package me.chan.texas.text.tokenizer;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.HyperSpan;
import me.chan.texas.text.layout.StateList;

public class LinkedTokenStreamUnitTest {

	private static HyperSpan newSpan() {
		return new HyperSpan() {
			@Override
			protected void onDraw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states) {
			}

			@Override
			protected void onMeasure(float lineHeight, float baselineOffset) {
				setMeasuredSize(10, 20);
			}
		};
	}

	@Test
	public void testSizeIsSumOfChildren() {
		String text = "hello world";
		TokenStream textStream = TokenStream.obtain(text, 0, text.length());
		int textSize = textStream.size();

		TokenStream hyperStream = TokenStream.obtainHyperSpan(newSpan());
		TokenStream linked = TokenStream.link(textStream, hyperStream);

		Assert.assertEquals(textSize + 1, linked.size());
		Assert.assertTrue(linked.hasNext());

		linked.recycle();
	}

	@Test
	public void testNextWalksFirstStreamThenSecond() {
		HyperSpan span1 = newSpan();
		HyperSpan span2 = newSpan();
		TokenStream linked = TokenStream.link(
				TokenStream.obtainHyperSpan(span1),
				TokenStream.obtainHyperSpan(span2));

		Assert.assertEquals(2, linked.size());
		Assert.assertTrue(linked.hasNext());

		Token first = linked.next();
		Assert.assertNotNull(first);
		Assert.assertEquals(Token.TYPE_SYMBOL, first.getType());
		Assert.assertSame(span1, ((HyperSpanToken) first).getHyperSpan());
		Assert.assertTrue(linked.hasNext());

		Token second = linked.next();
		Assert.assertNotNull(second);
		Assert.assertEquals(Token.TYPE_SYMBOL, second.getType());
		Assert.assertSame(span2, ((HyperSpanToken) second).getHyperSpan());
		Assert.assertFalse(linked.hasNext());

		Assert.assertNull(linked.next());

		first.recycle();
		second.recycle();
		linked.recycle();
	}

	@Test
	public void testNextWalksTextThenHyperSpan() {
		String text = "AB";
		TokenStream textStream = TokenStream.obtain(text, 0, text.length());
		int textSize = textStream.size();
		Assert.assertTrue(textSize > 0);

		HyperSpan span = newSpan();
		TokenStream linked = TokenStream.link(textStream, TokenStream.obtainHyperSpan(span));

		for (int i = 0; i < textSize; ++i) {
			Assert.assertTrue(linked.hasNext());
			Token token = linked.next();
			Assert.assertNotNull(token);
			Assert.assertNotEquals(Token.TYPE_SYMBOL, token.getType());
			token.recycle();
		}

		Assert.assertTrue(linked.hasNext());
		Token tail = linked.next();
		Assert.assertNotNull(tail);
		Assert.assertEquals(Token.TYPE_SYMBOL, tail.getType());
		Assert.assertSame(span, ((HyperSpanToken) tail).getHyperSpan());
		Assert.assertFalse(linked.hasNext());

		tail.recycle();
		linked.recycle();
	}

	@Test
	public void testSaveAndRestoreSpansBoundary() {
		HyperSpan span1 = newSpan();
		HyperSpan span2 = newSpan();
		TokenStream linked = TokenStream.link(
				TokenStream.obtainHyperSpan(span1),
				TokenStream.obtainHyperSpan(span2));

		Assert.assertEquals(0, linked.save());

		Token first = linked.next();
		int afterFirst = linked.save();
		Assert.assertEquals(1, afterFirst);

		Token second = linked.next();
		Assert.assertEquals(2, linked.save());
		Assert.assertFalse(linked.hasNext());

		linked.restore(afterFirst);
		Assert.assertTrue(linked.hasNext());
		Token replay = linked.next();
		Assert.assertNotNull(replay);
		Assert.assertSame(span2, ((HyperSpanToken) replay).getHyperSpan());

		linked.restore(0);
		Assert.assertTrue(linked.hasNext());
		Token rewind = linked.next();
		Assert.assertNotNull(rewind);
		Assert.assertSame(span1, ((HyperSpanToken) rewind).getHyperSpan());

		first.recycle();
		second.recycle();
		replay.recycle();
		rewind.recycle();
		linked.recycle();
	}

	@Test
	public void testTryGetCrossesBoundaryWithoutAdvancing() {
		HyperSpan span1 = newSpan();
		HyperSpan span2 = newSpan();
		TokenStream linked = TokenStream.link(
				TokenStream.obtainHyperSpan(span1),
				TokenStream.obtainHyperSpan(span2));

		Token peek0 = linked.tryGet(0);
		Assert.assertNotNull(peek0);
		Assert.assertSame(span1, ((HyperSpanToken) peek0).getHyperSpan());

		Token peek1 = linked.tryGet(1);
		Assert.assertNotNull(peek1);
		Assert.assertSame(span2, ((HyperSpanToken) peek1).getHyperSpan());

		Assert.assertNull(linked.tryGet(2));

		// peeking should not move the cursor
		Assert.assertEquals(0, linked.save());
		Assert.assertTrue(linked.hasNext());

		Token byState1 = linked.tryGet(1, 0);
		Assert.assertNotNull(byState1);
		Assert.assertSame(span2, ((HyperSpanToken) byState1).getHyperSpan());

		Token byStateBoundary = linked.tryGet(0, 1);
		Assert.assertNotNull(byStateBoundary);
		Assert.assertSame(span2, ((HyperSpanToken) byStateBoundary).getHyperSpan());

		Assert.assertNull(linked.tryGet(2, 0));
		Assert.assertNull(linked.tryGet(1, 1));

		peek0.recycle();
		peek1.recycle();
		byState1.recycle();
		byStateBoundary.recycle();
		linked.recycle();
	}

	@Test
	public void testTryGetTracksCurrentCursor() {
		HyperSpan span1 = newSpan();
		HyperSpan span2 = newSpan();
		TokenStream linked = TokenStream.link(
				TokenStream.obtainHyperSpan(span1),
				TokenStream.obtainHyperSpan(span2));

		Token consumed = linked.next();
		Assert.assertNotNull(consumed);
		Assert.assertEquals(1, linked.save());

		// cursor is at 1, so tryGet(0) means absolute index 1 which is the second token
		Token peek = linked.tryGet(0);
		Assert.assertNotNull(peek);
		Assert.assertSame(span2, ((HyperSpanToken) peek).getHyperSpan());

		Assert.assertNull(linked.tryGet(1));

		consumed.recycle();
		peek.recycle();
		linked.recycle();
	}

	@Test
	public void testRecycleRecyclesBothChildren() {
		HyperSpan span1 = newSpan();
		HyperSpan span2 = newSpan();
		TokenStream child1 = TokenStream.obtainHyperSpan(span1);
		TokenStream child2 = TokenStream.obtainHyperSpan(span2);
		TokenStream linked = TokenStream.link(child1, child2);

		Assert.assertFalse(child1.isRecycled());
		Assert.assertFalse(child2.isRecycled());

		linked.recycle();

		Assert.assertTrue(linked.isRecycled());
		Assert.assertTrue(child1.isRecycled());
		Assert.assertTrue(child2.isRecycled());
	}

	@Test
	public void testLinkedInstanceReusedFromPool() {
		TokenStream first = TokenStream.link(
				TokenStream.obtainHyperSpan(newSpan()),
				TokenStream.obtainHyperSpan(newSpan()));
		// drain and recycle so the pool can hand it back
		while (first.hasNext()) {
			Token token = first.next();
			Assert.assertNotNull(token);
			token.recycle();
		}
		first.recycle();

		HyperSpan span1 = newSpan();
		HyperSpan span2 = newSpan();
		TokenStream reused = TokenStream.link(
				TokenStream.obtainHyperSpan(span1),
				TokenStream.obtainHyperSpan(span2));

		Assert.assertFalse(reused.isRecycled());
		Assert.assertEquals(0, reused.save());
		Assert.assertEquals(2, reused.size());

		Token t1 = reused.next();
		Assert.assertSame(span1, ((HyperSpanToken) t1).getHyperSpan());
		Token t2 = reused.next();
		Assert.assertSame(span2, ((HyperSpanToken) t2).getHyperSpan());
		Assert.assertFalse(reused.hasNext());

		t1.recycle();
		t2.recycle();
		reused.recycle();
	}
}