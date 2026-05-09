package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IntArrayUnitTest {

	// ============================================================
	// 构造函数
	// ============================================================

	@Test
	public void defaultConstructor_isEmpty() {
		IntArray arr = new IntArray();
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void constructorWithPositiveSize_isEmpty() {
		IntArray arr = new IntArray(8);
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void constructorWithZeroOrNegative_fallsBackToDefault() {
		IntArray zero = new IntArray(0);
		Assert.assertTrue(zero.empty());

		IntArray neg = new IntArray(-5);
		Assert.assertTrue(neg.empty());

		// DEFAULT_SIZE 个元素不应触发扩容
		for (int i = 0; i < IntArray.DEFAULT_SIZE; ++i) {
			zero.add(i);
		}
		Assert.assertEquals(IntArray.DEFAULT_SIZE, zero.size());
	}

	@Test
	public void constructorFromCollection_copiesValuesInOrder() {
		List<Integer> src = Arrays.asList(1, 2, 3, 4, 5);
		IntArray arr = new IntArray(src);

		Assert.assertEquals(5, arr.size());
		for (int i = 0; i < src.size(); ++i) {
			Assert.assertEquals((int) src.get(i), arr.get(i));
		}
	}

	@Test
	public void constructorFromEmptyCollection_isEmpty() {
		IntArray arr = new IntArray(Collections.<Integer>emptyList());
		Assert.assertTrue(arr.empty());

		arr.add(42);
		Assert.assertEquals(1, arr.size());
		Assert.assertEquals(42, arr.get(0));
	}

	@Test
	public void constructorFromArrayList_preservesOrder_includingBoundaryValues() {
		ArrayList<Integer> src = new ArrayList<>();
		src.add(Integer.MIN_VALUE);
		src.add(-1);
		src.add(0);
		src.add(1);
		src.add(Integer.MAX_VALUE);

		IntArray arr = new IntArray(src);
		Assert.assertEquals(5, arr.size());
		Assert.assertEquals(Integer.MIN_VALUE, arr.get(0));
		Assert.assertEquals(-1, arr.get(1));
		Assert.assertEquals(0, arr.get(2));
		Assert.assertEquals(1, arr.get(3));
		Assert.assertEquals(Integer.MAX_VALUE, arr.get(4));
	}

	// ============================================================
	// add / get / size / empty
	// ============================================================

	@Test
	public void add_storesElementsInInsertionOrder() {
		IntArray arr = new IntArray();
		arr.add(10);
		arr.add(20);
		arr.add(30);

		Assert.assertEquals(3, arr.size());
		Assert.assertFalse(arr.empty());
		Assert.assertEquals(10, arr.get(0));
		Assert.assertEquals(20, arr.get(1));
		Assert.assertEquals(30, arr.get(2));
	}

	@Test
	public void add_acceptsBoundaryValues() {
		IntArray arr = new IntArray();
		arr.add(Integer.MIN_VALUE);
		arr.add(Integer.MAX_VALUE);
		arr.add(0);
		arr.add(-1);

		Assert.assertEquals(4, arr.size());
		Assert.assertEquals(Integer.MIN_VALUE, arr.get(0));
		Assert.assertEquals(Integer.MAX_VALUE, arr.get(1));
		Assert.assertEquals(0, arr.get(2));
		Assert.assertEquals(-1, arr.get(3));
	}

	@Test
	public void add_acceptsDuplicateValues() {
		IntArray arr = new IntArray();
		arr.add(7);
		arr.add(7);
		arr.add(7);
		Assert.assertEquals(3, arr.size());
		for (int i = 0; i < 3; ++i) {
			Assert.assertEquals(7, arr.get(i));
		}
	}

	@Test
	public void add_growsBeyondDefaultCapacity_andRetainsAllValues() {
		IntArray arr = new IntArray();
		int n = IntArray.DEFAULT_SIZE * 8 + 3;
		for (int i = 0; i < n; ++i) {
			arr.add(i * 1000);
		}
		Assert.assertEquals(n, arr.size());
		for (int i = 0; i < n; ++i) {
			Assert.assertEquals(i * 1000, arr.get(i));
		}
	}

	// ============================================================
	// set
	// ============================================================

	@Test
	public void set_updatesValueAtIndex_withoutChangingSize() {
		IntArray arr = new IntArray();
		arr.add(1);
		arr.add(2);
		arr.add(3);

		arr.set(1, 99);
		Assert.assertEquals(3, arr.size());
		Assert.assertEquals(1, arr.get(0));
		Assert.assertEquals(99, arr.get(1));
		Assert.assertEquals(3, arr.get(2));
	}

	@Test
	public void set_atFirstAndLastIndex() {
		IntArray arr = new IntArray();
		arr.add(1);
		arr.add(2);
		arr.add(3);

		arr.set(0, -100);
		arr.set(2, Integer.MAX_VALUE);
		Assert.assertEquals(-100, arr.get(0));
		Assert.assertEquals(Integer.MAX_VALUE, arr.get(2));
	}

	// ============================================================
	// clear
	// ============================================================

	@Test
	public void clear_onEmpty_isNoOp() {
		IntArray arr = new IntArray();
		arr.clear();
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void clear_onPopulated_emptiesArray_andAllowsReuse() {
		IntArray arr = new IntArray();
		for (int i = 0; i < 50; ++i) arr.add(i);
		arr.clear();
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());

		arr.add(7);
		Assert.assertEquals(1, arr.size());
		Assert.assertEquals(7, arr.get(0));
	}

	// ============================================================
	// reverse
	// ============================================================

	@Test
	public void reverse_emptyArray_isNoOp() {
		IntArray arr = new IntArray();
		arr.reverse();
		Assert.assertTrue(arr.empty());
	}

	@Test
	public void reverse_singleElement_isNoOp() {
		IntArray arr = new IntArray();
		arr.add(42);
		arr.reverse();
		Assert.assertEquals(1, arr.size());
		Assert.assertEquals(42, arr.get(0));
	}

	@Test
	public void reverse_evenLength() {
		IntArray arr = new IntArray();
		arr.add(1);
		arr.add(2);
		arr.add(3);
		arr.add(4);
		arr.reverse();

		Assert.assertEquals(4, arr.get(0));
		Assert.assertEquals(3, arr.get(1));
		Assert.assertEquals(2, arr.get(2));
		Assert.assertEquals(1, arr.get(3));
	}

	@Test
	public void reverse_oddLength_keepsMiddleInPlace() {
		IntArray arr = new IntArray();
		arr.add(1);
		arr.add(2);
		arr.add(3);
		arr.add(4);
		arr.add(5);
		arr.reverse();

		Assert.assertEquals(5, arr.get(0));
		Assert.assertEquals(4, arr.get(1));
		Assert.assertEquals(3, arr.get(2));
		Assert.assertEquals(2, arr.get(3));
		Assert.assertEquals(1, arr.get(4));
	}

	@Test
	public void reverse_twice_isIdentity() {
		IntArray arr = new IntArray();
		int[] xs = {7, -3, Integer.MIN_VALUE, 0, Integer.MAX_VALUE, 99};
		for (int x : xs) arr.add(x);

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
		IntArray arr = new IntArray();
		Assert.assertFalse(arr.contains(0));
		Assert.assertFalse(arr.contains(Integer.MAX_VALUE));
		Assert.assertFalse(arr.contains(Integer.MIN_VALUE));
	}

	@Test
	public void contains_findsExistingElements() {
		IntArray arr = new IntArray();
		arr.add(10);
		arr.add(Integer.MIN_VALUE);
		arr.add(Integer.MAX_VALUE);

		Assert.assertTrue(arr.contains(10));
		Assert.assertTrue(arr.contains(Integer.MIN_VALUE));
		Assert.assertTrue(arr.contains(Integer.MAX_VALUE));
		Assert.assertFalse(arr.contains(11));
	}

	@Test
	public void contains_doesNotSeeRemovedTrailingValues() {
		IntArray arr = new IntArray();
		arr.add(1);
		arr.add(2);
		arr.add(3);
		// removeAt 不会清零；contains 必须只看 [0, size)
		arr.removeAt(2);
		Assert.assertFalse(arr.contains(3));
		Assert.assertTrue(arr.contains(1));
		Assert.assertTrue(arr.contains(2));
	}

	@Test
	public void contains_afterClear_returnsFalse() {
		IntArray arr = new IntArray();
		arr.add(7);
		arr.clear();
		Assert.assertFalse(arr.contains(7));
	}

	// ============================================================
	// removeAt
	// ============================================================

	@Test
	public void removeAt_outOfBounds_returnsFalse() {
		IntArray arr = new IntArray();
		Assert.assertFalse(arr.removeAt(-1));
		Assert.assertFalse(arr.removeAt(0));

		arr.add(1);
		Assert.assertFalse(arr.removeAt(-1));
		Assert.assertFalse(arr.removeAt(1));
		Assert.assertFalse(arr.removeAt(99));
		Assert.assertEquals(1, arr.size());
	}

	@Test
	public void removeAt_lastIndex_takesFastPath() {
		IntArray arr = new IntArray();
		arr.add(1);
		arr.add(2);
		arr.add(3);

		Assert.assertTrue(arr.removeAt(2));
		Assert.assertEquals(2, arr.size());
		Assert.assertEquals(1, arr.get(0));
		Assert.assertEquals(2, arr.get(1));
	}

	@Test
	public void removeAt_firstIndex_shiftsAllRemainingDown() {
		IntArray arr = new IntArray();
		arr.add(1);
		arr.add(2);
		arr.add(3);

		Assert.assertTrue(arr.removeAt(0));
		Assert.assertEquals(2, arr.size());
		Assert.assertEquals(2, arr.get(0));
		Assert.assertEquals(3, arr.get(1));
	}

	@Test
	public void removeAt_middleIndex_shiftsTailDown() {
		IntArray arr = new IntArray();
		arr.add(1);
		arr.add(2);
		arr.add(3);
		arr.add(4);

		Assert.assertTrue(arr.removeAt(1));
		Assert.assertEquals(3, arr.size());
		Assert.assertEquals(1, arr.get(0));
		Assert.assertEquals(3, arr.get(1));
		Assert.assertEquals(4, arr.get(2));
	}

	@Test
	public void removeAt_onlyElement_yieldsEmptyArray() {
		IntArray arr = new IntArray();
		arr.add(99);
		Assert.assertTrue(arr.removeAt(0));
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void removeAt_allElementsOneByOne_endsEmpty() {
		IntArray arr = new IntArray();
		for (int i = 0; i < 10; ++i) arr.add(i);
		// 反复从尾部删除
		for (int i = 9; i >= 0; --i) {
			Assert.assertTrue(arr.removeAt(i));
		}
		Assert.assertTrue(arr.empty());
	}

	@Test
	public void removeAt_repeatedlyFromFront_compactsCorrectly() {
		IntArray arr = new IntArray();
		for (int i = 0; i < 5; ++i) arr.add(i);

		// 反复从首位删除应得到 [1,2,3,4] -> [2,3,4] -> [3,4] -> [4] -> []
		Assert.assertTrue(arr.removeAt(0));
		Assert.assertEquals(1, arr.get(0));
		Assert.assertTrue(arr.removeAt(0));
		Assert.assertEquals(2, arr.get(0));
		Assert.assertTrue(arr.removeAt(0));
		Assert.assertEquals(3, arr.get(0));
		Assert.assertTrue(arr.removeAt(0));
		Assert.assertEquals(4, arr.get(0));
		Assert.assertTrue(arr.removeAt(0));
		Assert.assertTrue(arr.empty());
	}

	// ============================================================
	// zero
	// ============================================================

	@Test
	public void zero_setsAllElementsToZero_andResizesIndex() {
		IntArray arr = new IntArray();
		arr.add(1);
		arr.add(2);
		arr.add(3);

		arr.zero(3);
		Assert.assertTrue("expected size >= 3", arr.size() >= 3);
		// zero 用 idealByteArraySize 取整，可能大于请求大小
		for (int i = 0; i < 3; ++i) {
			Assert.assertEquals(0, arr.get(i));
		}
	}

	@Test
	public void zero_growsBackingWhenSizeExceedsCapacity() {
		IntArray arr = new IntArray(1);
		arr.zero(100);
		Assert.assertTrue(arr.size() >= 100);
		for (int i = 0; i < 100; ++i) {
			Assert.assertEquals(0, arr.get(i));
		}
	}

	@Test
	public void zero_clearsExistingValues_evenWhenWithinCapacity() {
		IntArray arr = new IntArray();
		for (int i = 1; i <= 10; ++i) arr.add(i);

		arr.zero(5);
		// zero 必须把现有值真清掉
		Assert.assertFalse(arr.contains(1));
		Assert.assertFalse(arr.contains(10));
		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(0, arr.get(i));
		}
	}

	// ============================================================
	// toString
	// ============================================================

	@Test
	public void toString_emptyArray() {
		Assert.assertEquals("[]", new IntArray().toString());
	}

	@Test
	public void toString_singleElement_hasNoTrailingSeparator() {
		IntArray arr = new IntArray();
		arr.add(42);
		Assert.assertEquals("[42]", arr.toString());
	}

	@Test
	public void toString_multipleElements_useCommaSpace() {
		IntArray arr = new IntArray();
		arr.add(1);
		arr.add(2);
		arr.add(3);
		Assert.assertEquals("[1, 2, 3]", arr.toString());
	}

	@Test
	public void toString_handlesBoundaryValues() {
		IntArray arr = new IntArray();
		arr.add(Integer.MIN_VALUE);
		arr.add(Integer.MAX_VALUE);
		Assert.assertEquals("[" + Integer.MIN_VALUE + ", " + Integer.MAX_VALUE + "]",
			arr.toString());
	}

	@Test
	public void toString_reflectsLiveStateAfterRemoveAndClear() {
		IntArray arr = new IntArray();
		arr.add(1);
		arr.add(2);
		arr.add(3);
		arr.removeAt(1);
		Assert.assertEquals("[1, 3]", arr.toString());

		arr.clear();
		Assert.assertEquals("[]", arr.toString());
	}

	// ============================================================
	// 综合 / 压力
	// ============================================================

	@Test
	public void stress_addReverseRemove_remainsConsistent() {
		IntArray arr = new IntArray(2);
		for (int i = 0; i < 200; ++i) arr.add(i);
		Assert.assertEquals(200, arr.size());

		arr.reverse();
		Assert.assertEquals(199, arr.get(0));
		Assert.assertEquals(0, arr.get(199));

		// 反复从中间删除
		int prevSize = arr.size();
		Assert.assertTrue(arr.removeAt(100));
		Assert.assertEquals(prevSize - 1, arr.size());

		arr.clear();
		Assert.assertTrue(arr.empty());
	}

	@Test
	public void endToEnd_basicLifecycle() {
		// 原始测试覆盖的端到端场景：填充 -> reverse -> clear
		IntArray arr = new IntArray();
		Assert.assertTrue(arr.empty());

		int n = IntArray.DEFAULT_SIZE * 2;
		for (int i = 0; i < n; ++i) {
			arr.add(i);
		}
		Assert.assertEquals(n, arr.size());
		for (int i = 0; i < n; ++i) {
			Assert.assertEquals(i, arr.get(i));
		}

		arr.reverse();
		for (int i = 0; i < n; ++i) {
			Assert.assertEquals(n - 1 - i, arr.get(i));
		}

		arr.clear();
		Assert.assertTrue(arr.empty());
	}
}