package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

public class IntSetUnitTest {

	@Test
	public void test() {
		IntSet set = new IntSet();
		Assert.assertTrue(set.isEmpty());

		set.mock(new int[]{1, 5, 7, 10});
		Assert.assertEquals(0, ~set.find(0));
		Assert.assertEquals(4, ~set.find(11));

		set.mock(new int[]{1, 5, 7});
		Assert.assertEquals(0, ~set.find(0));
		Assert.assertEquals(3, ~set.find(11));

		Assert.assertEquals(1, ~set.find(2));
		Assert.assertEquals(2, ~set.find(6));

		set.clear();
		Assert.assertEquals(0, set.size());

		set.add(5);
		Assert.assertEquals(1, set.size());
		cmp(new int[]{5}, set.getBuckets(), set.size());

		set.add(1);
		Assert.assertEquals(2, set.size());
		cmp(new int[]{1, 5}, set.getBuckets(), set.size());

		set.add(7);
		Assert.assertEquals(3, set.size());
		cmp(new int[]{1, 5, 7}, set.getBuckets(), set.size());

		set.add(2);
		Assert.assertEquals(4, set.size());
		cmp(new int[]{1, 2, 5, 7}, set.getBuckets(), set.size());

		Assert.assertFalse(set.remove(-1));
		Assert.assertEquals(4, set.size());
		cmp(new int[]{1, 2, 5, 7}, set.getBuckets(), set.size());

		Assert.assertTrue(set.remove(5));
		Assert.assertEquals(3, set.size());
		cmp(new int[]{1, 2, 7}, set.getBuckets(), set.size());

		// 添加已存在的最小元素（index == 0），验证 bug 修复
		Assert.assertFalse(set.add(1));
		Assert.assertEquals(3, set.size());
		cmp(new int[]{1, 2, 7}, set.getBuckets(), set.size());

		// 添加已存在的中间元素
		Assert.assertFalse(set.add(2));
		Assert.assertEquals(3, set.size());

		// 添加已存在的最大元素（index == mSize - 1）
		Assert.assertFalse(set.add(7));
		Assert.assertEquals(3, set.size());

		Assert.assertTrue(set.contains(1));
		Assert.assertTrue(set.contains(2));
		Assert.assertFalse(set.contains(5));

		set.clear();
		Assert.assertEquals(0, set.size());
		Assert.assertTrue(set.isEmpty());
	}

	private void cmp(int[] lhs, int[] rhs, int size) {
		for (int i = 0; i < size; ++i) {
			Assert.assertEquals(lhs[i], rhs[i]);
		}
	}
}
