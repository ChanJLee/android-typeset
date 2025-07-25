package me.chan.texas.misc;

import org.junit.Assert;
import org.junit.Test;

public class BitBucket8UnitTest {

	@Test
	public void testWR() {
		BitBucket8 bitmap = new BitBucket8();

		Assert.assertFalse(bitmap.get(0));
		try {
			Assert.assertFalse(bitmap.get(-1));
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {

		}

		try {
			Assert.assertFalse(bitmap.get(8));
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {

		}

		bitmap.set(0, true);
		Assert.assertTrue(bitmap.get(0));
		bitmap.set(0, false);
		Assert.assertFalse(bitmap.get(0));

		bitmap.set(7, true);
		Assert.assertTrue(bitmap.get(7));
		bitmap.set(7, false);
		Assert.assertFalse(bitmap.get(7));

		bitmap.set(3, true);
		Assert.assertTrue(bitmap.get(3));
		bitmap.set(3, false);
		Assert.assertFalse(bitmap.get(3));

		bitmap.clear();
		Assert.assertFalse(bitmap.get(7));
		Assert.assertEquals(0, bitmap.getRange(0, 8));
	}

	@Test
	public void testRange() {
		BitBucket8 bitmap = new BitBucket8();
		Assert.assertEquals(0, bitmap.getRange(0, 8));

		try {
			bitmap.getRange(1, 9);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {

		}

		try {
			bitmap.getRange(1, 10);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {

		}

		bitmap.set(0, true);
		bitmap.set(1, true);
		bitmap.set(5, true);
		bitmap.set(6, true);
		bitmap.set(7, true);
		bitmap.set(2, true);
		bitmap.set(4, true);

		// 0b11110111
		Assert.assertEquals(1, bitmap.getRange(0, 1));
		Assert.assertEquals(3, bitmap.getRange(0, 2));
		Assert.assertEquals( (byte) 0b11110111, (byte) bitmap.getRange(0, 8));
		Assert.assertEquals(0b1111, bitmap.getRange(4, 8));
		Assert.assertEquals(0b0111, bitmap.getRange(0, 4));
	}

	@Test
	public void testConstructor() {
		BitBucket8 bitmap1 = new BitBucket8();
		Assert.assertEquals(0, bitmap1.getBits());

		BitBucket8 bitmap2 = new BitBucket8((byte) 0b10101010);
		Assert.assertEquals((byte) 0b10101010, bitmap2.getBits());
		Assert.assertTrue(bitmap2.get(1));
		Assert.assertTrue(bitmap2.get(3));
		Assert.assertTrue(bitmap2.get(5));
		Assert.assertTrue(bitmap2.get(7));
		Assert.assertFalse(bitmap2.get(0));
		Assert.assertFalse(bitmap2.get(2));
		Assert.assertFalse(bitmap2.get(4));
		Assert.assertFalse(bitmap2.get(6));
	}

	@Test
	public void testSetClearMethods() {
		BitBucket8 bitmap = new BitBucket8();

		// Test set(index) method
		bitmap.set(3);
		Assert.assertTrue(bitmap.get(3));

		// Test clear(index) method
		bitmap.clear(3);
		Assert.assertFalse(bitmap.get(3));

		// Test multiple bits
		bitmap.set(0);
		bitmap.set(2);
		bitmap.set(7);
		Assert.assertTrue(bitmap.get(0));
		Assert.assertTrue(bitmap.get(2));
		Assert.assertTrue(bitmap.get(7));
		Assert.assertFalse(bitmap.get(1));
		Assert.assertFalse(bitmap.get(3));
	}

	@Test
	public void testReset() {
		BitBucket8 bitmap = new BitBucket8();
		bitmap.set(0, true);
		bitmap.set(3, true);
		bitmap.set(7, true);

		// Reset to specific value
		bitmap.reset((byte) 0b01010101);
		Assert.assertEquals((byte) 0b01010101, bitmap.getBits());
		Assert.assertTrue(bitmap.get(0));
		Assert.assertTrue(bitmap.get(2));
		Assert.assertTrue(bitmap.get(4));
		Assert.assertTrue(bitmap.get(6));
		Assert.assertFalse(bitmap.get(1));
		Assert.assertFalse(bitmap.get(3));
		Assert.assertFalse(bitmap.get(5));
		Assert.assertFalse(bitmap.get(7));

		// Reset to zero
		bitmap.reset((byte) 0);
		Assert.assertEquals(0, bitmap.getBits());
		for (int i = 0; i < 8; i++) {
			Assert.assertFalse(bitmap.get(i));
		}
	}

	@Test
	public void testEqualsAndHashCode() {
		BitBucket8 bitmap1 = new BitBucket8((byte) 0b10101010);
		BitBucket8 bitmap2 = new BitBucket8((byte) 0b10101010);
		BitBucket8 bitmap3 = new BitBucket8((byte) 0b01010101);

		// Test equals
		Assert.assertTrue(bitmap1.equals(bitmap2));
		Assert.assertFalse(bitmap1.equals(bitmap3));
		Assert.assertTrue(bitmap1.equals(bitmap1));
		Assert.assertFalse(bitmap1.equals(null));
		Assert.assertFalse(bitmap1.equals("not a BitBucket8"));

		// Test hashCode
		Assert.assertEquals(bitmap1.hashCode(), bitmap2.hashCode());
		Assert.assertNotEquals(bitmap1.hashCode(), bitmap3.hashCode());
	}

	@Test
	public void testCopy() {
		BitBucket8 bitmap1 = new BitBucket8((byte) 0b01001100);
		BitBucket8 bitmap2 = new BitBucket8();

		bitmap2.copy(bitmap1);
		Assert.assertEquals(bitmap1.getBits(), bitmap2.getBits());
		Assert.assertTrue(bitmap1.equals(bitmap2));

		// Modify original, copy should remain unchanged
		bitmap1.set(1, true);
		Assert.assertNotEquals(bitmap1.getBits(), bitmap2.getBits());
		Assert.assertFalse(bitmap1.equals(bitmap2));
	}

	@Test
	public void testGetBits() {
		BitBucket8 bitmap = new BitBucket8();
		Assert.assertEquals(0, bitmap.getBits());

		bitmap.set(0, true);
		bitmap.set(7, true);
		Assert.assertEquals((byte) 0b10000001, bitmap.getBits());

		bitmap.set(3, true);
		Assert.assertEquals((byte) 0b10001001, bitmap.getBits());
	}

	@Test
	public void testRangeEdgeCases() {
		BitBucket8 bitmap = new BitBucket8((byte) 0b11110000);

		// Test single bit ranges
		Assert.assertEquals(0, bitmap.getRange(0, 1));
		Assert.assertEquals(1, bitmap.getRange(4, 5));
		Assert.assertEquals(1, bitmap.getRange(7, 8));

		// Test invalid ranges
		try {
			bitmap.getRange(-1, 4);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {
		}

		try {
			bitmap.getRange(4, 3);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {
		}

		try {
			bitmap.getRange(0, 9);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {
		}

		try {
			bitmap.getRange(5, 14);
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException ignore) {
		}
	}

	@Test
	public void testAllBitsOperations() {
		BitBucket8 bitmap = new BitBucket8();

		// Set all bits to true
		for (int i = 0; i < 8; i++) {
			bitmap.set(i, true);
		}
		Assert.assertEquals((byte) 0b11111111, bitmap.getBits());

		// Clear all bits using clear()
		bitmap.clear();
		Assert.assertEquals(0, bitmap.getBits());
		for (int i = 0; i < 8; i++) {
			Assert.assertFalse(bitmap.get(i));
		}

		// Set alternating pattern
		for (int i = 0; i < 8; i += 2) {
			bitmap.set(i, true);
		}
		Assert.assertEquals((byte) 0b01010101, bitmap.getBits());
	}
}
