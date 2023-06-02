package me.chan.texas.misc;

import org.junit.Assert;
import org.junit.Test;

public class BitmapUnitTest {

	@Test
	public void testSize() {
		Bitmap bitmap = new Bitmap(31);
		Assert.assertEquals(bitmap.size(), 32);

		bitmap = new Bitmap(0);
		Assert.assertEquals(bitmap.size(), 0);

		bitmap = new Bitmap(32);
		Assert.assertEquals(bitmap.size(), 32);

		bitmap = new Bitmap(33);
		Assert.assertEquals(bitmap.size(), 64);
	}

	@Test
	public void testWR() {
		Bitmap bitmap = new Bitmap(63);
		Assert.assertEquals(bitmap.size(), 64);

		Assert.assertFalse(bitmap.get(0));
		Assert.assertFalse(bitmap.get(-1));

		Assert.assertTrue(bitmap.set(0, true));
		Assert.assertTrue(bitmap.get(0));
		bitmap.set(0, false);
		Assert.assertFalse(bitmap.get(0));

		Assert.assertTrue(bitmap.set(31, true));
		Assert.assertTrue(bitmap.get(31));
		bitmap.set(31, false);
		Assert.assertFalse(bitmap.get(31));

		Assert.assertTrue(bitmap.set(5, true));
		Assert.assertTrue(bitmap.get(5));
		bitmap.set(5, false);
		Assert.assertFalse(bitmap.get(5));

		Assert.assertFalse(bitmap.set(64, true));
		Assert.assertFalse(bitmap.get(64));
		Assert.assertFalse(bitmap.set(-1, true));
		Assert.assertFalse(bitmap.get(-1));

		Assert.assertTrue(bitmap.set(32, true));
		Assert.assertTrue(bitmap.set(63, true));
		Assert.assertTrue(bitmap.get(32));
		Assert.assertTrue(bitmap.get(63));
		bitmap.clear();
		Assert.assertFalse(bitmap.get(32));
		Assert.assertFalse(bitmap.get(63));
	}
}
