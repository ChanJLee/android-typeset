package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.text.layout.TextSpan;

public class ReferenceCountingPointerUnitTest {

	@Test
	public void test() {
		TextSpan span = TextSpan.obtain("1", 0, 1, null, null, null, null);
		Assert.assertFalse(box.isRecycled());

		ReferenceCountingPointer<TextSpan> pointer = new ReferenceCountingPointer<TextSpan>(box, new ReferenceCountingPointer.Listener<TextSpan>() {
			@Override
			public void onRelease(TextSpan v) {
				v.recycle();
			}
		});

		ReferenceCountingPointer<TextSpan> copy = new ReferenceCountingPointer<TextSpan>(pointer);

		pointer.release();
		Assert.assertFalse(box.isRecycled());

		copy.release();
		Assert.assertTrue(box.isRecycled());
	}
}
