package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

public class CharArrayPoolUnitTest {

	@Test
	public void test() {
		CharArrayPool pool = new CharArrayPool();
		char[] chars = pool.obtain(10);
		Assert.assertEquals(chars.length, 16);
		char[] tmp = chars;
		pool.release(chars);
		chars = pool.obtain(10);
		Assert.assertSame(chars, tmp);

		try {
			pool.obtain(-1);
			Assert.fail("test alloc -1 array failed");
		} catch (IllegalArgumentException ignore) {

		}

		chars = pool.obtain(2047);
		Assert.assertEquals(chars.length, 2047);
		pool.release(chars);


		chars = pool.obtain(0);
		Assert.assertEquals(chars.length, 4);
		tmp = chars;
		pool.release(chars);
		chars = pool.obtain(0);
		Assert.assertSame(chars, tmp);

		chars = pool.obtain(4);
		Assert.assertEquals(chars.length, 4);
		tmp = chars;
		pool.release(chars);
		chars = pool.obtain(4);
		Assert.assertSame(chars, tmp);
	}
}
