package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.TextBox;

public class ReferenceCountingPointerUnitTest {

	@Test
	public void test() {
		TextBox box = TextBox.obtain("1", 0, 1, null, null, null, null);
		Assert.assertFalse(box.isRecycled());

		ReferenceCountingPointer<TextBox> pointer = new ReferenceCountingPointer<TextBox>(box, new ReferenceCountingPointer.Listener<TextBox>() {
			@Override
			public void onRelease(TextBox v) {
				v.recycle();
			}
		});

		ReferenceCountingPointer<TextBox> copy = new ReferenceCountingPointer<TextBox>(pointer);

		pointer.release();
		Assert.assertFalse(box.isRecycled());

		copy.release();
		Assert.assertTrue(box.isRecycled());
	}
}
