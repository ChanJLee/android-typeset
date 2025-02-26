package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.text.layout.Glue;

public class ReferenceCountingPointerUnitTest {

	@Test
	public void test() {
		Glue glue = Glue.obtain(0, 0, 0, 0);
		Assert.assertFalse(glue.isRecycled());

		ReferenceCountingPointer<Glue> pointer = new ReferenceCountingPointer<Glue>(glue, new ReferenceCountingPointer.Listener<Glue>() {
			@Override
			public void onRelease(Glue v) {
				v.recycle();
			}
		});

		ReferenceCountingPointer<Glue> copy = new ReferenceCountingPointer<Glue>(pointer);

		pointer.release();
		Assert.assertFalse(glue.isRecycled());

		copy.release();
		Assert.assertTrue(glue.isRecycled());
	}
}
