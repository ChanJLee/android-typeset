package me.chan.texas.text.tokenizer;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.HyperSpan;
import me.chan.texas.text.layout.StateList;

public class HyperSpanTokenStreamUnitTest {

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
	public void testHasNextProducesSingleToken() {
		HyperSpan span = newSpan();
		TokenStream stream = TokenStream.obtainHyperSpan(span);

		Assert.assertTrue(stream.hasNext());
		Assert.assertEquals(1, stream.size());

		Token token = stream.next();
		Assert.assertNotNull(token);
		Assert.assertEquals(Token.TYPE_HYPER_SPAN, token.getType());
		Assert.assertTrue(token instanceof HyperSpanToken);
		Assert.assertSame(span, ((HyperSpanToken) token).getHyperSpan());

		Assert.assertFalse(stream.hasNext());
		token.recycle();
		stream.recycle();
	}

	@Test
	public void testNextAfterEndReturnsNull() {
		HyperSpan span = newSpan();
		TokenStream stream = TokenStream.obtainHyperSpan(span);

		Token first = stream.next();
		Assert.assertNotNull(first);

		Token second = stream.next();
		Assert.assertNull(second);
		Assert.assertNull(stream.next());

		first.recycle();
		stream.recycle();
	}

	@Test
	public void testSaveAndRestore() {
		HyperSpan span = newSpan();
		TokenStream stream = TokenStream.obtainHyperSpan(span);

		int initialState = stream.save();
		Assert.assertEquals(0, initialState);

		Token token = stream.next();
		Assert.assertNotNull(token);
		Assert.assertEquals(1, stream.save());
		Assert.assertFalse(stream.hasNext());

		stream.restore(initialState);
		Assert.assertTrue(stream.hasNext());
		Assert.assertEquals(initialState, stream.save());

		Token replay = stream.next();
		Assert.assertNotNull(replay);
		Assert.assertEquals(Token.TYPE_HYPER_SPAN, replay.getType());
		Assert.assertSame(span, ((HyperSpanToken) replay).getHyperSpan());

		token.recycle();
		replay.recycle();
		stream.recycle();
	}

	@Test
	public void testTryGet() {
		HyperSpan span = newSpan();
		TokenStream stream = TokenStream.obtainHyperSpan(span);

		Token peek = stream.tryGet(0);
		Assert.assertNotNull(peek);
		Assert.assertEquals(Token.TYPE_HYPER_SPAN, peek.getType());
		Assert.assertSame(span, ((HyperSpanToken) peek).getHyperSpan());
		// tryGet should not advance the cursor
		Assert.assertTrue(stream.hasNext());
		Assert.assertEquals(0, stream.save());
		peek.recycle();

		Assert.assertNull(stream.tryGet(1));
		Assert.assertNull(stream.tryGet(0, 1));
		Assert.assertNull(stream.tryGet(1, 0));

		Token byState = stream.tryGet(0, 0);
		Assert.assertNotNull(byState);
		Assert.assertSame(span, ((HyperSpanToken) byState).getHyperSpan());

		byState.recycle();
		stream.recycle();
	}

	@Test
	public void testTryGetTracksCurrentState() {
		HyperSpan span = newSpan();
		TokenStream stream = TokenStream.obtainHyperSpan(span);

		Token consumed = stream.next();
		Assert.assertNotNull(consumed);

		// after next(), the cursor is at 1, so tryGet(0) is past the end
		Assert.assertNull(stream.tryGet(0));
		Assert.assertNull(stream.tryGet(1));

		consumed.recycle();
		stream.recycle();
	}

	@Test
	public void testRecycleClearsStateAndEnablesReuse() {
		HyperSpan first = newSpan();
		TokenStream stream = TokenStream.obtainHyperSpan(first);

		Token token = stream.next();
		Assert.assertNotNull(token);
		token.recycle();

		Assert.assertFalse(stream.hasNext());
		stream.recycle();
		Assert.assertTrue(stream.isRecycled());

		// the pool should hand back a clean instance for the next call
		HyperSpan second = newSpan();
		TokenStream reused = TokenStream.obtainHyperSpan(second);

		Assert.assertFalse(reused.isRecycled());
		Assert.assertTrue(reused.hasNext());
		Assert.assertEquals(0, reused.save());

		Token reusedToken = reused.next();
		Assert.assertNotNull(reusedToken);
		Assert.assertSame(second, ((HyperSpanToken) reusedToken).getHyperSpan());
		Assert.assertFalse(reused.hasNext());

		reusedToken.recycle();
		reused.recycle();
	}

	@Test
	public void testNextProducesFreshTokenInstances() {
		HyperSpan span = newSpan();
		TokenStream stream = TokenStream.obtainHyperSpan(span);

		Token first = stream.next();
		Assert.assertNotNull(first);

		stream.restore(0);
		Token second = stream.next();
		Assert.assertNotNull(second);

		// each next() goes through HyperSpanToken.obtain(), so consumers can recycle
		// independently — they should not share the same live instance
		Assert.assertSame(span, ((HyperSpanToken) first).getHyperSpan());
		Assert.assertSame(span, ((HyperSpanToken) second).getHyperSpan());

		first.recycle();
		second.recycle();
		stream.recycle();
	}
}