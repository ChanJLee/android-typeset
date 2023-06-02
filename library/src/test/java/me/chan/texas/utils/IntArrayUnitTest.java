package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

public class IntArrayUnitTest {

	@Test
	public void test() {
		IntArray array = new IntArray();
		Assert.assertTrue(array.empty());

		int size = IntStack.DEFAULT_SIZE * 2;
		for (int i = 0; i < size; ++i) {
			array.add(i);
		}
		Assert.assertEquals(array.size(), size);

		for (int i = size - 1; i >= 0; --i) {
			Assert.assertEquals(i, array.get(i));
		}

		array.reverse();
		for (int i = 0; i < size; ++i) {
			Assert.assertEquals(size - 1 - i, array.get(i));
		}

		array.clear();
		Assert.assertTrue(array.empty());
	}
}
