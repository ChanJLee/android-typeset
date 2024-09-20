package me.chan.texas.misc;

import org.junit.Assert;
import org.junit.Test;

public class Bitmap32UnitTest {

	@Test
	public void testWR() {
		BitBucket32 bitmap = new BitBucket32();

		Assert.assertFalse(bitmap.get(0));
		try {
			Assert.assertFalse(bitmap.get(-1));
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {

		}

		try {
			Assert.assertFalse(bitmap.get(32));
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {

		}

		bitmap.set(0, true);
		Assert.assertTrue(bitmap.get(0));
		bitmap.set(0, false);
		Assert.assertFalse(bitmap.get(0));

		bitmap.set(31, true);
		Assert.assertTrue(bitmap.get(31));
		bitmap.set(31, false);
		Assert.assertFalse(bitmap.get(31));

		bitmap.set(5, true);
		Assert.assertTrue(bitmap.get(5));
		bitmap.set(5, false);
		Assert.assertFalse(bitmap.get(5));


		bitmap.clear();
		Assert.assertFalse(bitmap.get(31));
		Assert.assertEquals(0, bitmap.getRange(0, 32));
	}

	@Test
	public void testRange() {
		BitBucket32 bitmap = new BitBucket32();
		Assert.assertEquals(0, bitmap.getRange(0, 32));

		try {
			bitmap.getRange(1, 33);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {

		}

		try {
			bitmap.getRange(1, 34);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {

		}

		bitmap.set(0, true);
		bitmap.set(1, true);
		bitmap.set(29, true);
		bitmap.set(30, true);
		bitmap.set(31, true);
		bitmap.set(5, true);
		bitmap.set(7, true);

		// 0b11100000_00000000_00000000_10100011
		Assert.assertEquals(1, bitmap.getRange(0, 1));
		Assert.assertEquals(3, bitmap.getRange(0, 2));
		Assert.assertEquals(0b11100000_00000000_00000000_10100011, bitmap.getRange(0, 32));
		Assert.assertEquals(0b11100000_00000000, bitmap.getRange(16, 32));
		Assert.assertEquals(0b00000000_10100011, bitmap.getRange(0, 16));
	}
}
