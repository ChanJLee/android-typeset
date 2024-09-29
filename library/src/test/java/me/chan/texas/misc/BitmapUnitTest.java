package me.chan.texas.misc;

import org.junit.Assert;
import org.junit.Test;

public class BitmapUnitTest {

	@Test
	public void testSize() {
		BitBucket bitmap = new BitBucket(31);
		Assert.assertEquals(bitmap.size(), 32);

		bitmap = new BitBucket(0);
		Assert.assertEquals(bitmap.size(), 0);

		bitmap = new BitBucket(32);
		Assert.assertEquals(bitmap.size(), 32);

		bitmap = new BitBucket(33);
		Assert.assertEquals(bitmap.size(), 64);
	}

	@Test
	public void testWR() {
		BitBucket bitmap = new BitBucket(63);
		Assert.assertEquals(bitmap.size(), 64);

		Assert.assertFalse(bitmap.get(0));
		try {
			Assert.assertFalse(bitmap.get(-1));
			Assert.fail();
		} catch (IllegalArgumentException e) {

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

		bitmap.set(64, true);
		Assert.assertEquals(bitmap.size(), 128);
		Assert.assertTrue(bitmap.get(64));
		bitmap.set(-1, true);
		Assert.assertFalse(bitmap.get(-1));

		bitmap.set(32, true);
		bitmap.set(63, true);
		Assert.assertTrue(bitmap.get(32));
		Assert.assertTrue(bitmap.get(63));
		bitmap.clear();
		Assert.assertFalse(bitmap.get(32));
		Assert.assertFalse(bitmap.get(63));
		Assert.assertEquals(0, bitmap.getRange(0, 32));
		Assert.assertEquals(0, bitmap.getRange(32, 64));
	}

	@Test
	public void testRange() {
		BitBucket bitmap = new BitBucket(32);
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
		bitmap.set(32, true);
		bitmap.set(34, true);
		bitmap.set(35, true);
		bitmap.set(63, true);
		bitmap.set(61, true);

		Assert.assertEquals(1, bitmap.getRange(0, 1));
		Assert.assertEquals(3, bitmap.getRange(0, 2));
		Assert.assertEquals(0b11100000_00000000_00000000_00000011, bitmap.getRange(0, 32));
		Assert.assertEquals(0b00000000_00000000_00000000_00000011, bitmap.getRange(30, 32));
		Assert.assertEquals(0b01110000_00000000_00000000_00000001, bitmap.getRange(1, 32));

		bitmap.set(5, true);
		bitmap.set(7, true);

		// 0b10100000_00000000_00000000_00001101_11100000_00000000_00000000_10100011
		Assert.assertEquals(0b10100000_00000000_00000000_00001101, bitmap.getRange(32, 64));
		Assert.assertEquals(0b1101_11100000_00000000_00000000_1010, bitmap.getRange(4, 36));
		Assert.assertEquals(0b00001101_11100000, bitmap.getRange(24, 40));
	}
}
