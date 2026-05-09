package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LongArrayUnitTest {

	// ============================================================
	// 构造函数
	// ============================================================

	@Test
	public void defaultConstructor_isEmpty() {
		LongArray arr = new LongArray();
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void constructorWithPositiveSize_isEmpty() {
		LongArray arr = new LongArray(8);
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void constructorWithZeroOrNegative_fallsBackToDefault() {
		LongArray zero = new LongArray(0);
		Assert.assertTrue(zero.empty());

		LongArray neg = new LongArray(-5);
		Assert.assertTrue(neg.empty());

		// 容量足够装下默认条目而不扩容
		for (int i = 0; i < LongArray.DEFAULT_SIZE; ++i) {
			zero.add(i);
		}
		Assert.assertEquals(LongArray.DEFAULT_SIZE, zero.size());
	}

	@Test
	public void constructorFromCollection_copiesValuesInOrder() {
		List<Long> src = Arrays.asList(1L, 2L, 3L, 4L, 5L);
		LongArray arr = new LongArray(src);

		Assert.assertEquals(5, arr.size());
		for (int i = 0; i < src.size(); ++i) {
			Assert.assertEquals((long) src.get(i), arr.get(i));
		}
	}

	@Test
	public void constructorFromEmptyCollection_isEmpty() {
		LongArray arr = new LongArray(Collections.<Long>emptyList());
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());

		// 仍然可正常使用
		arr.add(42L);
		Assert.assertEquals(1, arr.size());
		Assert.assertEquals(42L, arr.get(0));
	}

	// ============================================================
	// add / get / size / empty
	// ============================================================

	@Test
	public void add_storesElementsInInsertionOrder() {
		LongArray arr = new LongArray();
		arr.add(10L);
		arr.add(20L);
		arr.add(30L);

		Assert.assertEquals(3, arr.size());
		Assert.assertFalse(arr.empty());
		Assert.assertEquals(10L, arr.get(0));
		Assert.assertEquals(20L, arr.get(1));
		Assert.assertEquals(30L, arr.get(2));
	}

	@Test
	public void add_acceptsLongBoundaryValues() {
		LongArray arr = new LongArray();
		arr.add(Long.MIN_VALUE);
		arr.add(Long.MAX_VALUE);
		arr.add(0L);
		arr.add(-1L);
		arr.add(1L << 40); // 不可表示为 int

		Assert.assertEquals(5, arr.size());
		Assert.assertEquals(Long.MIN_VALUE, arr.get(0));
		Assert.assertEquals(Long.MAX_VALUE, arr.get(1));
		Assert.assertEquals(0L, arr.get(2));
		Assert.assertEquals(-1L, arr.get(3));
		Assert.assertEquals(1L << 40, arr.get(4));
	}

	@Test
	public void add_growsBeyondDefaultCapacity_andRetainsAllValues() {
		LongArray arr = new LongArray();
		int n = LongArray.DEFAULT_SIZE * 8 + 3;
		for (int i = 0; i < n; ++i) {
			arr.add((long) i * 1000);
		}
		Assert.assertEquals(n, arr.size());
		for (int i = 0; i < n; ++i) {
			Assert.assertEquals((long) i * 1000, arr.get(i));
		}
	}

	// ============================================================
	// set
	// ============================================================

	@Test
	public void set_updatesValueAtIndex_withoutChangingSize() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);

		arr.set(1, 99L);
		Assert.assertEquals(3, arr.size());
		Assert.assertEquals(1L, arr.get(0));
		Assert.assertEquals(99L, arr.get(1));
		Assert.assertEquals(3L, arr.get(2));
	}

	@Test
	public void set_atFirstAndLastIndex() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);

		arr.set(0, -100L);
		arr.set(2, Long.MAX_VALUE);
		Assert.assertEquals(-100L, arr.get(0));
		Assert.assertEquals(Long.MAX_VALUE, arr.get(2));
	}

	// ============================================================
	// clear
	// ============================================================

	@Test
	public void clear_onEmpty_isNoOp() {
		LongArray arr = new LongArray();
		arr.clear();
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void clear_onPopulated_emptiesArray_andAllowsReuse() {
		LongArray arr = new LongArray();
		for (int i = 0; i < 50; ++i) arr.add(i);
		arr.clear();
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());

		arr.add(7L);
		Assert.assertEquals(1, arr.size());
		Assert.assertEquals(7L, arr.get(0));
	}

	// ============================================================
	// reverse
	// ============================================================

	@Test
	public void reverse_emptyArray_isNoOp() {
		LongArray arr = new LongArray();
		arr.reverse();
		Assert.assertTrue(arr.empty());
	}

	@Test
	public void reverse_singleElement_isNoOp() {
		LongArray arr = new LongArray();
		arr.add(42L);
		arr.reverse();
		Assert.assertEquals(1, arr.size());
		Assert.assertEquals(42L, arr.get(0));
	}

	@Test
	public void reverse_evenLength() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);
		arr.add(4L);
		arr.reverse();

		Assert.assertEquals(4L, arr.get(0));
		Assert.assertEquals(3L, arr.get(1));
		Assert.assertEquals(2L, arr.get(2));
		Assert.assertEquals(1L, arr.get(3));
	}

	@Test
	public void reverse_oddLength_keepsMiddleInPlace() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);
		arr.add(4L);
		arr.add(5L);
		arr.reverse();

		Assert.assertEquals(5L, arr.get(0));
		Assert.assertEquals(4L, arr.get(1));
		Assert.assertEquals(3L, arr.get(2));
		Assert.assertEquals(2L, arr.get(3));
		Assert.assertEquals(1L, arr.get(4));
	}

	@Test
	public void reverse_twice_isIdentity() {
		LongArray arr = new LongArray();
		long[] xs = {7L, -3L, Long.MIN_VALUE, 0L, Long.MAX_VALUE, 1L << 40};
		for (long x : xs) arr.add(x);

		arr.reverse();
		arr.reverse();

		Assert.assertEquals(xs.length, arr.size());
		for (int i = 0; i < xs.length; ++i) {
			Assert.assertEquals(xs[i], arr.get(i));
		}
	}

	// ============================================================
	// contains
	// ============================================================

	@Test
	public void contains_onEmpty_returnsFalse() {
		LongArray arr = new LongArray();
		Assert.assertFalse(arr.contains(0L));
		Assert.assertFalse(arr.contains(Long.MAX_VALUE));
	}

	@Test
	public void contains_findsExistingElements() {
		LongArray arr = new LongArray();
		arr.add(10L);
		arr.add(Long.MIN_VALUE);
		arr.add(Long.MAX_VALUE);

		Assert.assertTrue(arr.contains(10L));
		Assert.assertTrue(arr.contains(Long.MIN_VALUE));
		Assert.assertTrue(arr.contains(Long.MAX_VALUE));
		Assert.assertFalse(arr.contains(11L));
	}

	@Test
	public void contains_doesNotSeeRemovedTrailingValues() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);
		// removeAt 不一定会清零；contains 必须只看 [0, size)
		arr.removeAt(2);
		Assert.assertFalse(arr.contains(3L));
		Assert.assertTrue(arr.contains(1L));
		Assert.assertTrue(arr.contains(2L));
	}

	@Test
	public void contains_afterClear_returnsFalse() {
		LongArray arr = new LongArray();
		arr.add(7L);
		arr.clear();
		Assert.assertFalse(arr.contains(7L));
	}

	// ============================================================
	// removeAt
	// ============================================================

	@Test
	public void removeAt_outOfBounds_returnsFalse() {
		LongArray arr = new LongArray();
		Assert.assertFalse(arr.removeAt(-1));
		Assert.assertFalse(arr.removeAt(0));

		arr.add(1L);
		Assert.assertFalse(arr.removeAt(-1));
		Assert.assertFalse(arr.removeAt(1));
		Assert.assertFalse(arr.removeAt(99));
		Assert.assertEquals(1, arr.size());
	}

	@Test
	public void removeAt_lastIndex_takesFastPath() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);

		Assert.assertTrue(arr.removeAt(2));
		Assert.assertEquals(2, arr.size());
		Assert.assertEquals(1L, arr.get(0));
		Assert.assertEquals(2L, arr.get(1));
	}

	@Test
	public void removeAt_firstIndex_shiftsAllRemainingDown() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);

		Assert.assertTrue(arr.removeAt(0));
		Assert.assertEquals(2, arr.size());
		Assert.assertEquals(2L, arr.get(0));
		Assert.assertEquals(3L, arr.get(1));
	}

	@Test
	public void removeAt_middleIndex_shiftsTailDown() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);
		arr.add(4L);

		Assert.assertTrue(arr.removeAt(1));
		Assert.assertEquals(3, arr.size());
		Assert.assertEquals(1L, arr.get(0));
		Assert.assertEquals(3L, arr.get(1));
		Assert.assertEquals(4L, arr.get(2));
	}

	@Test
	public void removeAt_onlyElement_yieldsEmptyArray() {
		LongArray arr = new LongArray();
		arr.add(99L);
		Assert.assertTrue(arr.removeAt(0));
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void removeAt_allElementsOneByOne() {
		LongArray arr = new LongArray();
		for (int i = 0; i < 10; ++i) arr.add(i);
		// 反复从尾部删除
		for (int i = 9; i >= 0; --i) {
			Assert.assertTrue(arr.removeAt(i));
		}
		Assert.assertTrue(arr.empty());
	}

	// ============================================================
	// last / removeLast
	// ============================================================

	@Test
	public void last_returnsTailValue() {
		LongArray arr = new LongArray();
		arr.add(1L);
		Assert.assertEquals(1L, arr.last());

		arr.add(2L);
		Assert.assertEquals(2L, arr.last());

		arr.add(Long.MAX_VALUE);
		Assert.assertEquals(Long.MAX_VALUE, arr.last());
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void last_onEmpty_throws() {
		new LongArray().last();
	}

	@Test
	public void removeLast_decrementsSize() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);

		arr.removeLast();
		Assert.assertEquals(2, arr.size());
		Assert.assertEquals(2L, arr.last());

		arr.removeLast();
		arr.removeLast();
		Assert.assertTrue(arr.empty());
	}

	@Test
	public void removeLast_onEmpty_isSafeNoOp() {
		// removeLast 调用 removeAt(-1) — 受越界保护，不抛异常
		LongArray arr = new LongArray();
		arr.removeLast();
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	// ============================================================
	// insertAt
	// ============================================================

	@Test
	public void insertAt_atOrPastEnd_appendsLikeAdd() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);

		arr.insertAt(2, 3L);
		Assert.assertEquals(3, arr.size());
		Assert.assertEquals(3L, arr.get(2));

		// 索引超出当前尾部也退化为 add
		arr.insertAt(99, 4L);
		Assert.assertEquals(4, arr.size());
		Assert.assertEquals(4L, arr.get(3));
	}

	@Test
	public void insertAt_atFront_shiftsExistingValuesRight() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);

		arr.insertAt(0, 99L);
		Assert.assertEquals(4, arr.size());
		Assert.assertEquals(99L, arr.get(0));
		Assert.assertEquals(1L, arr.get(1));
		Assert.assertEquals(2L, arr.get(2));
		Assert.assertEquals(3L, arr.get(3));
	}

	@Test
	public void insertAt_inMiddle_shiftsTailRight() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);
		arr.add(4L);

		arr.insertAt(2, 99L);
		Assert.assertEquals(5, arr.size());
		Assert.assertEquals(1L, arr.get(0));
		Assert.assertEquals(2L, arr.get(1));
		Assert.assertEquals(99L, arr.get(2));
		Assert.assertEquals(3L, arr.get(3));
		Assert.assertEquals(4L, arr.get(4));
	}

	@Test
	public void insertAt_onEmpty_anyIndex_appends() {
		LongArray arr = new LongArray();
		// 索引 >= mIndex（含 0）走 add 分支
		arr.insertAt(0, 7L);
		Assert.assertEquals(1, arr.size());
		Assert.assertEquals(7L, arr.get(0));
	}

	@Test
	public void insertAt_atCapacityBoundary_triggersResize() {
		LongArray arr = new LongArray(1);
		// 用 add 填满首批容量，然后用 insertAt 在前端插入触发扩容
		arr.add(2L);
		arr.add(3L);
		arr.insertAt(0, 1L);

		Assert.assertEquals(3, arr.size());
		Assert.assertEquals(1L, arr.get(0));
		Assert.assertEquals(2L, arr.get(1));
		Assert.assertEquals(3L, arr.get(2));
	}

	// ============================================================
	// zero
	// ============================================================

	@Test
	public void zero_setsAllElementsToZero_andResizesIndex() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);

		arr.zero(3);
		Assert.assertTrue("expected size >= 3", arr.size() >= 3);
		// 前 3 个肯定是 0；zero 用 idealByteArraySize 取整，可能大于 3
		for (int i = 0; i < 3; ++i) {
			Assert.assertEquals(0L, arr.get(i));
		}
	}

	@Test
	public void zero_growsBackingWhenSizeExceedsCapacity() {
		LongArray arr = new LongArray(1);
		arr.zero(100);
		Assert.assertTrue(arr.size() >= 100);
		for (int i = 0; i < 100; ++i) {
			Assert.assertEquals(0L, arr.get(i));
		}
	}

	@Test
	public void zero_clearsExistingValues_evenWhenWithinCapacity() {
		LongArray arr = new LongArray();
		for (int i = 1; i <= 10; ++i) arr.add(i);

		arr.zero(5);
		// zero 必须把现有值真清掉，contains 不应再找到非零数据
		Assert.assertFalse(arr.contains(1L));
		Assert.assertFalse(arr.contains(10L));
		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(0L, arr.get(i));
		}
	}

	// ============================================================
	// toString
	// ============================================================

	@Test
	public void toString_emptyArray() {
		Assert.assertEquals("[]", new LongArray().toString());
	}

	@Test
	public void toString_singleElement_hasNoTrailingSeparator() {
		LongArray arr = new LongArray();
		arr.add(42L);
		Assert.assertEquals("[42]", arr.toString());
	}

	@Test
	public void toString_multipleElements_useCommaSpace() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);
		Assert.assertEquals("[1, 2, 3]", arr.toString());
	}

	@Test
	public void toString_handlesLongBoundaryValues() {
		LongArray arr = new LongArray();
		arr.add(Long.MIN_VALUE);
		arr.add(Long.MAX_VALUE);
		Assert.assertEquals("[" + Long.MIN_VALUE + ", " + Long.MAX_VALUE + "]",
			arr.toString());
	}

	@Test
	public void toString_reflectsLiveStateAfterRemoveAndClear() {
		LongArray arr = new LongArray();
		arr.add(1L);
		arr.add(2L);
		arr.add(3L);
		arr.removeAt(1);
		Assert.assertEquals("[1, 3]", arr.toString());

		arr.clear();
		Assert.assertEquals("[]", arr.toString());
	}

	// ============================================================
	// 综合 / 压力
	// ============================================================

	@Test
	public void stress_addInsertReverseRemove_remainsConsistent() {
		LongArray arr = new LongArray(2);
		// add + insertAt + reverse + removeAt 串联，检验不变量
		for (long i = 0; i < 100; ++i) arr.add(i);
		arr.insertAt(0, -1L);
		arr.insertAt(50, 999L);
		Assert.assertEquals(102, arr.size());
		Assert.assertEquals(-1L, arr.get(0));
		Assert.assertEquals(999L, arr.get(50));

		arr.reverse();
		Assert.assertEquals(-1L, arr.last());
		Assert.assertEquals(99L, arr.get(0));

		// 把开头若干个剥掉，中间残值不应漂出来
		int prevSize = arr.size();
		Assert.assertTrue(arr.removeAt(0));
		Assert.assertEquals(prevSize - 1, arr.size());
		Assert.assertEquals(98L, arr.get(0));
	}

	@Test
	public void constructorFromArrayList_preservesOrder() {
		ArrayList<Long> src = new ArrayList<>();
		src.add(Long.MIN_VALUE);
		src.add(0L);
		src.add(Long.MAX_VALUE);

		LongArray arr = new LongArray(src);
		Assert.assertEquals(3, arr.size());
		Assert.assertEquals(Long.MIN_VALUE, arr.get(0));
		Assert.assertEquals(0L, arr.get(1));
		Assert.assertEquals(Long.MAX_VALUE, arr.get(2));
	}
}