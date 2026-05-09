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

	// ============================================================
	// 构造函数 / 容量
	// ============================================================

	@Test
	public void testDefaultConstructorCapacityIsTen() {
		IntMap<String> map = new IntMap<>();
		Assert.assertEquals(10, map.capacity());
		Assert.assertEquals(0, map.size());
		Assert.assertTrue(map.isEmpty());
	}

	@Test
	public void testZeroCapacityIsLightweight() {
		IntMap<String> map = new IntMap<>(0);
		Assert.assertEquals(0, map.capacity());
		Assert.assertTrue(map.isEmpty());
		Assert.assertEquals(0, map.size());
		Assert.assertNull(map.get(0));
		Assert.assertFalse(map.containsKey(0));
		Assert.assertFalse(map.containsValue("anything"));
	}

	@Test
	public void testCustomCapacityIsExact() {
		IntMap<String> map = new IntMap<>(7);
		Assert.assertEquals(7, map.capacity());
	}

	// ============================================================
	// put — key 边界 / 值边界
	// ============================================================

	@Test
	public void testPutNegativeKeysOrderedCorrectly() {
		IntMap<String> map = new IntMap<>();
		map.put(-100, "neg100");
		map.put(0, "zero");
		map.put(-1, "negOne");
		map.put(50, "fifty");

		Assert.assertEquals(-100, map.keyAt(0));
		Assert.assertEquals(-1, map.keyAt(1));
		Assert.assertEquals(0, map.keyAt(2));
		Assert.assertEquals(50, map.keyAt(3));
	}

	@Test
	public void testPutIntMinAndMaxKeys() {
		IntMap<String> map = new IntMap<>();
		map.put(Integer.MAX_VALUE, "max");
		map.put(Integer.MIN_VALUE, "min");
		map.put(0, "zero");

		Assert.assertEquals(Integer.MIN_VALUE, map.keyAt(0));
		Assert.assertEquals(0, map.keyAt(1));
		Assert.assertEquals(Integer.MAX_VALUE, map.keyAt(2));
		Assert.assertEquals("min", map.get(Integer.MIN_VALUE));
		Assert.assertEquals("max", map.get(Integer.MAX_VALUE));
		Assert.assertTrue(map.containsKey(Integer.MIN_VALUE));
		Assert.assertTrue(map.containsKey(Integer.MAX_VALUE));
	}

	@Test
	public void testPutNullValueDistinguishedFromMissingViaContainsKey() {
		IntMap<String> map = new IntMap<>();
		map.put(1, null);

		// get(missingKey) 与 put(k, null) 的 get 都返回 null
		Assert.assertNull(map.get(1));
		Assert.assertNull(map.get(99));

		// 默认值版本仍能区分：missing key 返回默认值，显式 null 仍返回 null
		Assert.assertNull(map.get(1, "fallback"));
		Assert.assertEquals("fallback", map.get(99, "fallback"));

		// 唯一可靠的区分手段是 containsKey
		Assert.assertTrue(map.containsKey(1));
		Assert.assertFalse(map.containsKey(99));
		Assert.assertEquals(1, map.size());
	}

	@Test
	public void testPutDescendingThenAscending_endsUpSorted() {
		IntMap<String> map = new IntMap<>();
		// 倒序触发反复的前端插入与扩容
		for (int i = 50; i > 0; --i) {
			map.put(i, "v" + i);
		}
		// 顺序覆盖所有 key
		for (int i = 1; i <= 50; ++i) {
			map.put(i, "u" + i);
		}
		Assert.assertEquals(50, map.size());
		for (int i = 0; i < 50; ++i) {
			Assert.assertEquals(i + 1, map.keyAt(i));
			Assert.assertEquals("u" + (i + 1), map.valueAt(i));
		}
	}

	@Test
	public void testPutManyKeysAcrossMultipleResizes() {
		IntMap<Integer> map = new IntMap<>(2);
		int total = 1024;
		for (int i = 0; i < total; ++i) {
			map.put(i, i * 10);
		}
		Assert.assertEquals(total, map.size());
		for (int i = 0; i < total; ++i) {
			Assert.assertEquals(Integer.valueOf(i * 10), map.get(i));
			Assert.assertEquals(i, map.keyAt(i));
		}
	}

	// ============================================================
	// 复活已删除的 key（put 走 i>=0 分支覆盖 DELETED slot）
	// ============================================================

	@Test
	public void testPutOnDeletedKeyResurrectsEntry() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(2, "b");
		map.put(3, "c");

		map.delete(2);
		// 不调用 size() 等触发 gc 的方法；put 同 key 应通过 binarySearch 命中 DELETED 槽并直接覆盖
		map.put(2, "resurrected");

		Assert.assertEquals(3, map.size());
		Assert.assertEquals("resurrected", map.get(2));
		Assert.assertEquals("a", map.get(1));
		Assert.assertEquals("c", map.get(3));
		Assert.assertTrue(map.containsKey(2));
	}

	// ============================================================
	// removeAt / removeAtRange 边界
	// ============================================================

	@Test
	public void testRemoveAtLastIndex() {
		IntMap<String> map = new IntMap<>();
		map.put(10, "a");
		map.put(20, "b");
		map.put(30, "c");

		map.removeAt(2);
		Assert.assertEquals(2, map.size());
		Assert.assertNull(map.get(30));
		Assert.assertEquals("a", map.get(10));
		Assert.assertEquals("b", map.get(20));
	}

	@Test
	public void testRemoveAtIsIdempotentOnAlreadyDeleted() {
		IntMap<String> map = new IntMap<>();
		map.put(10, "a");
		map.put(20, "b");

		map.removeAt(0);
		// 二次 removeAt 同一索引，状态应稳定
		map.removeAt(0);
		Assert.assertEquals(1, map.size());
		Assert.assertNull(map.get(10));
		Assert.assertEquals("b", map.get(20));
	}

	@Test
	public void testRemoveAtRangeZeroSize_isNoOp() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(2, "b");

		map.removeAtRange(0, 0);
		Assert.assertEquals(2, map.size());
		Assert.assertEquals("a", map.get(1));
		Assert.assertEquals("b", map.get(2));
	}

	@Test
	public void testRemoveAtRangeWholeMap_emptiesIt() {
		IntMap<String> map = new IntMap<>();
		for (int i = 0; i < 5; ++i) {
			map.put(i, "v" + i);
		}
		map.removeAtRange(0, 5);
		Assert.assertEquals(0, map.size());
		Assert.assertTrue(map.isEmpty());
		for (int i = 0; i < 5; ++i) {
			Assert.assertNull(map.get(i));
		}
	}

	@Test
	public void testRemoveAtRangeIndexAtSize_isNoOp() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(2, "b");

		map.removeAtRange(2, 5); // index >= size
		Assert.assertEquals(2, map.size());
	}

	// ============================================================
	// keyAt / valueAt — 触发 gc 后的可见性
	// ============================================================

	@Test
	public void testKeyAtAndValueAtAfterDelete_skipDeletedEntries() {
		IntMap<String> map = new IntMap<>();
		for (int i = 0; i < 5; ++i) {
			map.put(i, "v" + i);
		}
		map.delete(1);
		map.delete(3);

		// keyAt 会触发 gc，从而压缩出连续的活跃条目
		Assert.assertEquals(3, map.size());
		Assert.assertEquals(0, map.keyAt(0));
		Assert.assertEquals(2, map.keyAt(1));
		Assert.assertEquals(4, map.keyAt(2));
		Assert.assertEquals("v0", map.valueAt(0));
		Assert.assertEquals("v2", map.valueAt(1));
		Assert.assertEquals("v4", map.valueAt(2));
	}

	// ============================================================
	// indexOfKey / indexOfValue
	// ============================================================

	@Test
	public void testIndexOfKeyMissing_returnsEncodedInsertionPoint() {
		IntMap<String> map = new IntMap<>();
		map.put(10, "a");
		map.put(20, "b");
		map.put(30, "c");

		// missing keys: 二进制搜索的负值结果 ~idx 即插入点
		Assert.assertEquals(0, ~map.indexOfKey(5));   // 在最前
		Assert.assertEquals(1, ~map.indexOfKey(15));  // 在中间
		Assert.assertEquals(3, ~map.indexOfKey(99));  // 在最后
	}

	@Test
	public void testIndexOfValueWithDuplicates_returnsFirstHit() {
		IntMap<String> map = new IntMap<>();
		String shared = "shared";
		map.put(1, "a");
		map.put(2, shared);
		map.put(3, shared);
		map.put(4, "b");

		Assert.assertEquals(1, map.indexOfValue(shared));
	}

	@Test
	public void testIndexOfValueWithNull() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(2, null);
		map.put(3, "b");

		Assert.assertEquals(1, map.indexOfValue(null));
	}

	@Test
	public void testContainsValueUsesReferenceEquality_notEquals() {
		IntMap<String> map = new IntMap<>();
		String original = new String("payload");
		// new String(...) 保证不会被 JVM 内联到字面量常量池
		String equalButDistinct = new String("payload");
		Assert.assertEquals(original, equalButDistinct);
		Assert.assertNotSame(original, equalButDistinct);

		map.put(1, original);
		Assert.assertTrue(map.containsValue(original));
		Assert.assertFalse(map.containsValue(equalButDistinct));
	}

	@Test
	public void testContainsValueWithNull() {
		IntMap<String> map = new IntMap<>();
		Assert.assertFalse(map.containsValue(null));

		map.put(1, "a");
		Assert.assertFalse(map.containsValue(null));

		map.put(2, null);
		Assert.assertTrue(map.containsValue(null));
	}

	// ============================================================
	// clone
	// ============================================================

	@Test
	public void testCloneEmptyMap() {
		IntMap<String> map = new IntMap<>();
		IntMap<String> cloned = map.clone();

		Assert.assertTrue(cloned.isEmpty());
		Assert.assertEquals(0, cloned.size());
		cloned.put(1, "a");
		Assert.assertEquals(1, cloned.size());
		Assert.assertEquals(0, map.size()); // 原 map 不受影响
	}

	@Test
	public void testCloneKeysIndependent_afterRemoveOnClone() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(2, "b");
		map.put(3, "c");

		IntMap<String> cloned = map.clone();
		cloned.delete(2);
		// 强制 clone 完成 gc
		Assert.assertEquals(2, cloned.size());

		// 原 map 完整无损
		Assert.assertEquals(3, map.size());
		Assert.assertEquals("b", map.get(2));
	}

	// ============================================================
	// append
	// ============================================================

	@Test
	public void testAppendKeyEqualToLast_overwritesNotAppends() {
		IntMap<String> map = new IntMap<>();
		map.append(5, "first");
		// key 等于已有最大 key，append 退化为 put — 覆盖
		map.append(5, "second");

		Assert.assertEquals(1, map.size());
		Assert.assertEquals("second", map.get(5));
	}

	@Test
	public void testAppendAfterDelete_handlesGarbageCorrectly() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(5, "b");

		map.delete(5); // 留下 mGarbage
		// 仍然可以 append 一个比所有活跃 key 都大的 key
		map.append(10, "c");

		// size() 触发 gc，垃圾应被清理
		Assert.assertEquals(2, map.size());
		Assert.assertEquals("a", map.get(1));
		Assert.assertNull(map.get(5));
		Assert.assertEquals("c", map.get(10));
	}

	// ============================================================
	// putAll
	// ============================================================

	@Test
	public void testPutAllEmptySource_isNoOp() {
		IntMap<String> dest = new IntMap<>();
		dest.put(1, "a");
		dest.putAll(new IntMap<String>());

		Assert.assertEquals(1, dest.size());
		Assert.assertEquals("a", dest.get(1));
	}

	@Test
	public void testPutAllOverwritesExistingKeys() {
		IntMap<String> dest = new IntMap<>();
		dest.put(1, "old");
		dest.put(2, "keep");

		IntMap<String> src = new IntMap<>();
		src.put(1, "new");
		src.put(3, "added");

		dest.putAll(src);

		Assert.assertEquals(3, dest.size());
		Assert.assertEquals("new", dest.get(1));
		Assert.assertEquals("keep", dest.get(2));
		Assert.assertEquals("added", dest.get(3));
	}

	// ============================================================
	// toString
	// ============================================================

	@Test
	public void testToStringWithSelfReference_usesPlaceholder() {
		IntMap<Object> map = new IntMap<>();
		map.put(1, "a");
		map.put(2, map);

		String result = map.toString();
		Assert.assertTrue("expected (this Map) marker, got " + result,
			result.contains("(this Map)"));
		Assert.assertTrue(result.contains("1=a"));
	}

	@Test
	public void testToStringWithNullValue() {
		IntMap<String> map = new IntMap<>();
		map.put(1, null);
		Assert.assertEquals("{1=null}", map.toString());
	}

	@Test
	public void testToStringAfterDelete_reflectsLiveEntries() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(2, "b");
		map.put(3, "c");
		map.delete(2);

		Assert.assertEquals("{1=a, 3=c}", map.toString());
	}

	// ============================================================
	// clear / setValueAt / isEmpty
	// ============================================================

	@Test
	public void testClearAfterDelete_resetsGarbageState() {
		IntMap<String> map = new IntMap<>(4);
		map.put(1, "a");
		map.put(2, "b");
		map.delete(1); // mGarbage = true

		map.clear();
		Assert.assertTrue(map.isEmpty());
		Assert.assertEquals(0, map.size());

		// 重新填充至原始容量，不应触发额外扩容（gc state 已经被 clear 重置）
		int capBefore = map.capacity();
		for (int i = 0; i < capBefore; ++i) {
			map.put(i, "v" + i);
		}
		Assert.assertEquals(capBefore, map.size());
		Assert.assertEquals(capBefore, map.capacity());
	}

	@Test
	public void testSetValueAtToNull_keysRemainSearchable() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.setValueAt(0, null);

		Assert.assertNull(map.valueAt(0));
		Assert.assertNull(map.get(1));
		Assert.assertTrue(map.containsKey(1));
		Assert.assertEquals(1, map.size());
	}

	@Test
	public void testIsEmptyAfterAllDeleted() {
		IntMap<String> map = new IntMap<>();
		map.put(1, "a");
		map.put(2, "b");
		map.delete(1);
		map.delete(2);

		// isEmpty -> size -> gc -> 0
		Assert.assertTrue(map.isEmpty());
		Assert.assertEquals(0, map.size());
	}

	// ============================================================
	// 压力测试
	// ============================================================

	@Test
	public void testStressPutDeleteInterleaved() {
		IntMap<Integer> map = new IntMap<>();
		int n = 500;

		// 全量插入
		for (int i = 0; i < n; ++i) {
			map.put(i, i);
		}
		Assert.assertEquals(n, map.size());

		// 删除偶数 key
		for (int i = 0; i < n; i += 2) {
			map.delete(i);
		}
		Assert.assertEquals(n / 2, map.size());

		for (int i = 0; i < n; ++i) {
			if (i % 2 == 0) {
				Assert.assertNull(map.get(i));
				Assert.assertFalse(map.containsKey(i));
			} else {
				Assert.assertEquals(Integer.valueOf(i), map.get(i));
				Assert.assertTrue(map.containsKey(i));
			}
		}

		// 重新填回偶数 key（覆盖已删除/复用槽位）
		for (int i = 0; i < n; i += 2) {
			map.put(i, -i);
		}
		Assert.assertEquals(n, map.size());
		for (int i = 0; i < n; ++i) {
			Assert.assertEquals(Integer.valueOf(i % 2 == 0 ? -i : i), map.get(i));
		}

		// keyAt 全量按升序
		for (int idx = 0; idx < n; ++idx) {
			Assert.assertEquals(idx, map.keyAt(idx));
		}
	}
}
