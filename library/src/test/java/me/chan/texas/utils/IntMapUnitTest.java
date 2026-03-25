package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

public class IntMapUnitTest {

	@Test
	public void testPutAndGet() {
		IntMap<String> map = new IntMap<>();
		Assert.assertTrue(map.isEmpty());
		Assert.assertEquals(0, map.size());

		map.put(1, "a");
		map.put(5, "b");
		map.put(3, "c");

		Assert.assertEquals(3, map.size());
		Assert.assertEquals("a", map.get(1));
		Assert.assertEquals("b", map.get(5));
		Assert.assertEquals("c", map.get(3));
		Assert.assertNull(map.get(2));
	}

	@Test
	public void testGetWithDefault() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");

		Assert.assertEquals("a", map.get(1, "default"));
		Assert.assertEquals("default", map.get(99, "default"));
	}

	@Test
	public void testPutOverwrite() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "old");
		Assert.assertEquals("old", map.get(1));

		map.put(1, "new");
		Assert.assertEquals(1, map.size());
		Assert.assertEquals("new", map.get(1));
	}

	@Test
	public void testDeleteAndRemove() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(3, "b");
		map.put(5, "c");

		map.delete(3);
		Assert.assertNull(map.get(3));
		Assert.assertEquals(2, map.size());

		map.remove(1);
		Assert.assertNull(map.get(1));
		Assert.assertEquals(1, map.size());

		// delete 不存在的 key 不应报错
		map.delete(999);
		Assert.assertEquals(1, map.size());
	}

	@Test
	public void testDeletedSlotReuse() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(3, "b");
		map.put(5, "c");

		map.delete(3);
		// put 到已删除的 slot，应复用而非扩容
		map.put(3, "reused");
		Assert.assertEquals("reused", map.get(3));
		Assert.assertEquals(3, map.size());
	}

	@Test
	public void testRemoveAt() {
		IntMap<String> map = new IntMap<>();
		map.put(10, "a");
		map.put(20, "b");
		map.put(30, "c");

		map.removeAt(1);
		Assert.assertNull(map.get(20));
		Assert.assertEquals(2, map.size());
	}

	@Test
	public void testRemoveAtRange() {
		IntMap<String> map = new IntMap<>();
		for (int i = 0; i < 5; i++) {
			map.put(i, "v" + i);
		}
		Assert.assertEquals(5, map.size());

		map.removeAtRange(1, 3);
		Assert.assertEquals(2, map.size());
		Assert.assertEquals("v0", map.get(0));
		Assert.assertNull(map.get(1));
		Assert.assertNull(map.get(2));
		Assert.assertNull(map.get(3));
		Assert.assertEquals("v4", map.get(4));
	}

	@Test
	public void testRemoveAtRangeOverflow() {
		IntMap<String> map = new IntMap<>();
		for (int i = 0; i < 3; i++) {
			map.put(i, "v" + i);
		}
		// size 超过实际范围，不应越界
		map.removeAtRange(1, 100);
		Assert.assertEquals(1, map.size());
		Assert.assertEquals("v0", map.get(0));
	}

	@Test
	public void testKeyAtAndValueAt() {
		IntMap<String> map = new IntMap<>();
		map.put(10, "a");
		map.put(5, "b");
		map.put(20, "c");

		// keyAt 按升序排列
		Assert.assertEquals(5, map.keyAt(0));
		Assert.assertEquals(10, map.keyAt(1));
		Assert.assertEquals(20, map.keyAt(2));

		Assert.assertEquals("b", map.valueAt(0));
		Assert.assertEquals("a", map.valueAt(1));
		Assert.assertEquals("c", map.valueAt(2));
	}

	@Test
	public void testSetValueAt() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "old");

		map.setValueAt(0, "new");
		Assert.assertEquals("new", map.valueAt(0));
		Assert.assertEquals("new", map.get(1));
	}

	@Test
	public void testIndexOfKey() {
		IntMap<String> map = new IntMap<>();
		map.put(5, "a");
		map.put(10, "b");

		Assert.assertEquals(0, map.indexOfKey(5));
		Assert.assertEquals(1, map.indexOfKey(10));
		Assert.assertTrue(map.indexOfKey(999) < 0);
	}

	@Test
	public void testIndexOfValue() {
		IntMap<String> map = new IntMap<>();
		String val = "target";
		map.put(1, "other");
		map.put(2, val);

		// indexOfValue 使用 == 比较
		Assert.assertEquals(1, map.indexOfValue(val));
		Assert.assertEquals(-1, map.indexOfValue("not_exist"));
	}

	@Test
	public void testContainsKeyAndValue() {
		IntMap<String> map = new IntMap<>();
		String val = "hello";
		map.put(1, val);

		Assert.assertTrue(map.containsKey(1));
		Assert.assertFalse(map.containsKey(2));

		Assert.assertTrue(map.containsValue(val));
		Assert.assertFalse(map.containsValue("world"));
	}

	@Test
	public void testClear() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(2, "b");
		Assert.assertFalse(map.isEmpty());

		map.clear();
		Assert.assertTrue(map.isEmpty());
		Assert.assertEquals(0, map.size());
		Assert.assertNull(map.get(1));
	}

	@Test
	public void testClone() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(2, "b");

		IntMap<String> cloned = map.clone();
		Assert.assertEquals(map.size(), cloned.size());
		Assert.assertEquals(map.get(1), cloned.get(1));
		Assert.assertEquals(map.get(2), cloned.get(2));

		// 修改 clone 不影响原 map
		cloned.put(1, "modified");
		Assert.assertEquals("a", map.get(1));
		Assert.assertEquals("modified", cloned.get(1));
	}

	@Test
	public void testAppend() {
		IntMap<String> map = new IntMap<>();
		// append 递增 key，走快速路径
		map.append(1, "a");
		map.append(5, "b");
		map.append(10, "c");

		Assert.assertEquals(3, map.size());
		Assert.assertEquals("a", map.get(1));
		Assert.assertEquals("b", map.get(5));
		Assert.assertEquals("c", map.get(10));
	}

	@Test
	public void testAppendFallbackToPut() {
		IntMap<String> map = new IntMap<>();
		map.append(5, "a");
		map.append(10, "b");
		// key <= 已有最大 key，退化为 put
		map.append(3, "c");

		Assert.assertEquals(3, map.size());
		Assert.assertEquals("c", map.get(3));
		Assert.assertEquals(3, map.keyAt(0));
		Assert.assertEquals(5, map.keyAt(1));
		Assert.assertEquals(10, map.keyAt(2));
	}

	@Test
	public void testPutAll() {
		IntMap<String> src = new IntMap<>();
		src.put(1, "a");
		src.put(2, "b");

		IntMap<String> dest = new IntMap<>();
		dest.put(3, "c");
		dest.putAll(src);

		Assert.assertEquals(3, dest.size());
		Assert.assertEquals("a", dest.get(1));
		Assert.assertEquals("b", dest.get(2));
		Assert.assertEquals("c", dest.get(3));
	}

	@Test
	public void testToString() {
		IntMap<String> map = new IntMap<>();
		Assert.assertEquals("{}", map.toString());

		map.put(1, "a");
		Assert.assertEquals("{1=a}", map.toString());

		map.put(2, "b");
		Assert.assertEquals("{1=a, 2=b}", map.toString());
	}

	@Test
	public void testGrowCapacity() {
		IntMap<String> map = new IntMap<>(2);
		Assert.assertEquals(2, map.capacity());

		map.put(1, "a");
		map.put(2, "b");
		// 第三个元素触发扩容
		map.put(3, "c");

		Assert.assertEquals(3, map.size());
		Assert.assertEquals("a", map.get(1));
		Assert.assertEquals("b", map.get(2));
		Assert.assertEquals("c", map.get(3));
		Assert.assertTrue(map.capacity() > 2);
	}

	@Test
	public void testGcTriggeredByPut() {
		IntMap<String> map = new IntMap<>(4);
		map.put(1, "a");
		map.put(2, "b");
		map.put(3, "c");
		map.put(4, "d");

		// 删除产生 garbage
		map.delete(1);
		map.delete(2);
		map.delete(3);

		// 此时 mGarbage=true, mSize(内部)仍为4 >= mKeys.length
		// put 新 key 会先触发 gc 回收空间，避免不必要的扩容
		int capBefore = map.capacity();
		map.put(5, "e");

		Assert.assertEquals(2, map.size());
		Assert.assertEquals("d", map.get(4));
		Assert.assertEquals("e", map.get(5));
		Assert.assertEquals(capBefore, map.capacity());
	}

	@Test
	public void testGetOnDeletedSlotReturnsDefault() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.delete(1);

		Assert.assertNull(map.get(1));
		Assert.assertEquals("fallback", map.get(1, "fallback"));
	}

	@Test
	public void testBinarySearchIndirect() {
		IntMap<String> map = new IntMap<>();
		for (int i = 0; i < 20; i += 2) {
			map.put(i, "v" + i);
		}

		for (int i = 0; i < 20; i += 2) {
			Assert.assertTrue(map.containsKey(i));
		}
		for (int i = 1; i < 20; i += 2) {
			Assert.assertFalse(map.containsKey(i));
		}
	}

	@Test
	public void testAppendGrow() {
		IntMap<String> map = new IntMap<>(2);
		map.append(1, "a");
		map.append(2, "b");
		// 触发 append 内部的扩容
		map.append(3, "c");

		Assert.assertEquals(3, map.size());
		Assert.assertEquals("c", map.get(3));
	}

	@Test
	public void testDeleteTwice() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.delete(1);
		// 二次删除同一 key 不应出错
		map.delete(1);
		Assert.assertEquals(0, map.size());
	}
}
