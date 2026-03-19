package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.text.layout.TextSpan;

public class ReferenceCountingPointerUnitTest {

	@Test
	public void test() {
		TextSpan span = TextSpan.obtain("1", 0, 1, null, null, null, null);
		Assert.assertFalse(span.isRecycled());

		ReferenceCountingPointer<TextSpan> pointer = new ReferenceCountingPointer<TextSpan>(span, new ReferenceCountingPointer.Listener<TextSpan>() {
			@Override
			public void onRelease(TextSpan v) {
				v.recycle();
			}
		});

		ReferenceCountingPointer<TextSpan> copy = new ReferenceCountingPointer<TextSpan>(pointer);

		pointer.release();
		Assert.assertFalse(span.isRecycled());

		copy.release();
		Assert.assertTrue(span.isRecycled());
	}
}
