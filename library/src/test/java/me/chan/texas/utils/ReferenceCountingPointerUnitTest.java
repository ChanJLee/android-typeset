package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.text.layout.Glue;

public class ReferenceCountingPointerUnitTest {

	@Test
	public void test() {
		Glue glue = Glue.obtain(0, 0, 0, 0);
		Assert.assertFalse(glue.isRecycled());

		ReferenceCountingPointer<Glue> pointer = new ReferenceCountingPointer<Glue>(glue) {
			@Override
			protected void onRelease(Glue value) {
				value.recycle();
			}
		};

		ReferenceCountingPointer<Glue> copy = new ReferenceCountingPointer<Glue>(pointer) {
			@Override
			protected void onRelease(Glue value) {
				glue.recycle();
			}
		};

		pointer.release();
		Assert.assertFalse(glue.isRecycled());

		copy.release();
		Assert.assertTrue(glue.isRecycled());
	}
}
