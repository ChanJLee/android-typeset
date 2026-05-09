package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FloatArrayUnitTest {

	private static final float DELTA = 0.0f; // 测试用的都是精确可表示的 float

	// ============================================================
	// 构造函数
	// ============================================================

	@Test
	public void defaultConstructor_isEmpty() {
		FloatArray arr = new FloatArray();
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void constructorWithPositiveSize_isEmpty() {
		FloatArray arr = new FloatArray(8);
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void constructorWithZeroOrNegative_fallsBackToDefault() {
		FloatArray zero = new FloatArray(0);
		Assert.assertTrue(zero.empty());

		FloatArray neg = new FloatArray(-5);
		Assert.assertTrue(neg.empty());

		// DEFAULT_SIZE 个元素不应触发扩容
		for (int i = 0; i < FloatArray.DEFAULT_SIZE; ++i) {
			zero.add(i);
		}
		Assert.assertEquals(FloatArray.DEFAULT_SIZE, zero.size());
	}

	@Test
	public void constructorFromCollection_copiesValuesInOrder() {
		List<Float> src = Arrays.asList(0.5f, 1.5f, -2.25f, 3.75f);
		FloatArray arr = new FloatArray(src);

		Assert.assertEquals(4, arr.size());
		for (int i = 0; i < src.size(); ++i) {
			Assert.assertEquals((float) src.get(i), arr.get(i), DELTA);
		}
	}

	@Test
	public void constructorFromEmptyCollection_isEmpty() {
		FloatArray arr = new FloatArray(Collections.<Float>emptyList());
		Assert.assertTrue(arr.empty());

		arr.add(7.5f);
		Assert.assertEquals(1, arr.size());
		Assert.assertEquals(7.5f, arr.get(0), DELTA);
	}

	@Test
	public void constructorFromArrayList_preservesOrder_includingSpecialValues() {
		ArrayList<Float> src = new ArrayList<>();
		src.add(Float.NEGATIVE_INFINITY);
		src.add(-0.0f);
		src.add(0.0f);
		src.add(Float.MIN_VALUE);
		src.add(Float.MAX_VALUE);
		src.add(Float.POSITIVE_INFINITY);

		FloatArray arr = new FloatArray(src);
		Assert.assertEquals(6, arr.size());
		Assert.assertEquals(Float.NEGATIVE_INFINITY, arr.get(0), DELTA);
		Assert.assertEquals(-0.0f, arr.get(1), DELTA);
		Assert.assertEquals(0.0f, arr.get(2), DELTA);
		Assert.assertEquals(Float.MIN_VALUE, arr.get(3), DELTA);
		Assert.assertEquals(Float.MAX_VALUE, arr.get(4), DELTA);
		Assert.assertEquals(Float.POSITIVE_INFINITY, arr.get(5), DELTA);
	}

	// ============================================================
	// add / get / size / empty
	// ============================================================

	@Test
	public void add_storesElementsInInsertionOrder() {
		FloatArray arr = new FloatArray();
		arr.add(0.25f);
		arr.add(0.5f);
		arr.add(0.75f);

		Assert.assertEquals(3, arr.size());
		Assert.assertFalse(arr.empty());
		Assert.assertEquals(0.25f, arr.get(0), DELTA);
		Assert.assertEquals(0.5f, arr.get(1), DELTA);
		Assert.assertEquals(0.75f, arr.get(2), DELTA);
	}

	@Test
	public void add_acceptsFloatBoundaryValues() {
		FloatArray arr = new FloatArray();
		arr.add(Float.MIN_VALUE);
		arr.add(Float.MAX_VALUE);
		arr.add(0.0f);
		arr.add(-0.0f);
		arr.add(Float.POSITIVE_INFINITY);
		arr.add(Float.NEGATIVE_INFINITY);

		Assert.assertEquals(6, arr.size());
		Assert.assertEquals(Float.MIN_VALUE, arr.get(0), DELTA);
		Assert.assertEquals(Float.MAX_VALUE, arr.get(1), DELTA);
		// 0.0 与 -0.0 通过 floatToRawIntBits 才能被区分
		Assert.assertEquals(Integer.valueOf(Float.floatToRawIntBits(0.0f)),
			Integer.valueOf(Float.floatToRawIntBits(arr.get(2))));
		Assert.assertEquals(Integer.valueOf(Float.floatToRawIntBits(-0.0f)),
			Integer.valueOf(Float.floatToRawIntBits(arr.get(3))));
		Assert.assertEquals(Float.POSITIVE_INFINITY, arr.get(4), DELTA);
		Assert.assertEquals(Float.NEGATIVE_INFINITY, arr.get(5), DELTA);
	}

	@Test
	public void add_storesNaN_evenThoughNaNDoesNotEqualItself() {
		FloatArray arr = new FloatArray();
		arr.add(Float.NaN);
		Assert.assertEquals(1, arr.size());
		Assert.assertTrue(Float.isNaN(arr.get(0)));
	}

	@Test
	public void add_growsBeyondDefaultCapacity_andRetainsAllValues() {
		FloatArray arr = new FloatArray();
		int n = FloatArray.DEFAULT_SIZE * 8 + 3;
		for (int i = 0; i < n; ++i) {
			arr.add(i * 0.5f);
		}
		Assert.assertEquals(n, arr.size());
		for (int i = 0; i < n; ++i) {
			Assert.assertEquals(i * 0.5f, arr.get(i), DELTA);
		}
	}

	// ============================================================
	// set
	// ============================================================

	@Test
	public void set_updatesValueAtIndex_withoutChangingSize() {
		FloatArray arr = new FloatArray();
		arr.add(1.0f);
		arr.add(2.0f);
		arr.add(3.0f);

		arr.set(1, 99.5f);
		Assert.assertEquals(3, arr.size());
		Assert.assertEquals(1.0f, arr.get(0), DELTA);
		Assert.assertEquals(99.5f, arr.get(1), DELTA);
		Assert.assertEquals(3.0f, arr.get(2), DELTA);
	}

	@Test
	public void set_acceptsSpecialFloatValues() {
		FloatArray arr = new FloatArray();
		arr.add(0.0f);
		arr.set(0, Float.NaN);
		Assert.assertTrue(Float.isNaN(arr.get(0)));

		arr.set(0, Float.POSITIVE_INFINITY);
		Assert.assertEquals(Float.POSITIVE_INFINITY, arr.get(0), DELTA);
	}

	// ============================================================
	// clear
	// ============================================================

	@Test
	public void clear_onEmpty_isNoOp() {
		FloatArray arr = new FloatArray();
		arr.clear();
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void clear_emptiesPopulatedArray_andAllowsReuse() {
		FloatArray arr = new FloatArray();
		for (int i = 0; i < 50; ++i) arr.add(i * 0.25f);
		arr.clear();
		Assert.assertTrue(arr.empty());

		arr.add(7.5f);
		Assert.assertEquals(1, arr.size());
		Assert.assertEquals(7.5f, arr.get(0), DELTA);
	}

	// ============================================================
	// reverse
	// ============================================================

	@Test
	public void reverse_emptyArray_isNoOp() {
		FloatArray arr = new FloatArray();
		arr.reverse();
		Assert.assertTrue(arr.empty());
	}

	@Test
	public void reverse_singleElement_isNoOp() {
		FloatArray arr = new FloatArray();
		arr.add(42.5f);
		arr.reverse();
		Assert.assertEquals(1, arr.size());
		Assert.assertEquals(42.5f, arr.get(0), DELTA);
	}

	@Test
	public void reverse_evenLength() {
		FloatArray arr = new FloatArray();
		arr.add(1.5f);
		arr.add(2.5f);
		arr.add(3.5f);
		arr.add(4.5f);
		arr.reverse();

		Assert.assertEquals(4.5f, arr.get(0), DELTA);
		Assert.assertEquals(3.5f, arr.get(1), DELTA);
		Assert.assertEquals(2.5f, arr.get(2), DELTA);
		Assert.assertEquals(1.5f, arr.get(3), DELTA);
	}

	@Test
	public void reverse_oddLength_keepsMiddleInPlace() {
		FloatArray arr = new FloatArray();
		arr.add(1.0f);
		arr.add(2.0f);
		arr.add(3.0f);
		arr.add(4.0f);
		arr.add(5.0f);
		arr.reverse();

		Assert.assertEquals(5.0f, arr.get(0), DELTA);
		Assert.assertEquals(4.0f, arr.get(1), DELTA);
		Assert.assertEquals(3.0f, arr.get(2), DELTA);
		Assert.assertEquals(2.0f, arr.get(3), DELTA);
		Assert.assertEquals(1.0f, arr.get(4), DELTA);
	}

	@Test
	public void reverse_twice_isIdentity() {
		FloatArray arr = new FloatArray();
		float[] xs = {0.5f, -1.5f, Float.MIN_VALUE, 0.0f, Float.MAX_VALUE, 7.25f};
		for (float x : xs) arr.add(x);

		arr.reverse();
		arr.reverse();

		Assert.assertEquals(xs.length, arr.size());
		for (int i = 0; i < xs.length; ++i) {
			Assert.assertEquals(xs[i], arr.get(i), DELTA);
		}
	}

	// ============================================================
	// contains — float == 语义边界
	// ============================================================

	@Test
	public void contains_onEmpty_returnsFalse() {
		FloatArray arr = new FloatArray();
		Assert.assertFalse(arr.contains(0.0f));
		Assert.assertFalse(arr.contains(Float.MAX_VALUE));
	}

	@Test
	public void contains_findsExistingElements() {
		FloatArray arr = new FloatArray();
		arr.add(0.5f);
		arr.add(Float.POSITIVE_INFINITY);
		arr.add(Float.NEGATIVE_INFINITY);

		Assert.assertTrue(arr.contains(0.5f));
		Assert.assertTrue(arr.contains(Float.POSITIVE_INFINITY));
		Assert.assertTrue(arr.contains(Float.NEGATIVE_INFINITY));
		Assert.assertFalse(arr.contains(0.6f));
	}

	@Test
	public void contains_zeroEqualsNegativeZero_byFloatEquality() {
		// 在 IEEE-754 == 下，0.0f == -0.0f 成立
		FloatArray arr = new FloatArray();
		arr.add(-0.0f);
		Assert.assertTrue(arr.contains(0.0f));
		Assert.assertTrue(arr.contains(-0.0f));
	}

	@Test
	public void contains_doesNotFindNaN_eventIfPresent() {
		// NaN != NaN，所以基于 == 的 contains 永远找不到 NaN
		FloatArray arr = new FloatArray();
		arr.add(Float.NaN);
		Assert.assertFalse(arr.contains(Float.NaN));
	}

	@Test
	public void contains_doesNotSeeRemovedTrailingValues() {
		FloatArray arr = new FloatArray();
		arr.add(1.5f);
		arr.add(2.5f);
		arr.add(3.5f);
		arr.removeAt(2);
		Assert.assertFalse(arr.contains(3.5f));
		Assert.assertTrue(arr.contains(1.5f));
		Assert.assertTrue(arr.contains(2.5f));
	}

	@Test
	public void contains_afterClear_returnsFalse() {
		FloatArray arr = new FloatArray();
		arr.add(7.5f);
		arr.clear();
		Assert.assertFalse(arr.contains(7.5f));
	}

	// ============================================================
	// removeAt
	// ============================================================

	@Test
	public void removeAt_outOfBounds_returnsFalse() {
		FloatArray arr = new FloatArray();
		Assert.assertFalse(arr.removeAt(-1));
		Assert.assertFalse(arr.removeAt(0));

		arr.add(1.5f);
		Assert.assertFalse(arr.removeAt(-1));
		Assert.assertFalse(arr.removeAt(1));
		Assert.assertFalse(arr.removeAt(99));
		Assert.assertEquals(1, arr.size());
	}

	@Test
	public void removeAt_lastIndex_takesFastPath() {
		FloatArray arr = new FloatArray();
		arr.add(1.0f);
		arr.add(2.0f);
		arr.add(3.0f);

		Assert.assertTrue(arr.removeAt(2));
		Assert.assertEquals(2, arr.size());
		Assert.assertEquals(1.0f, arr.get(0), DELTA);
		Assert.assertEquals(2.0f, arr.get(1), DELTA);
	}

	@Test
	public void removeAt_firstIndex_shiftsAllRemainingDown() {
		FloatArray arr = new FloatArray();
		arr.add(1.0f);
		arr.add(2.0f);
		arr.add(3.0f);

		Assert.assertTrue(arr.removeAt(0));
		Assert.assertEquals(2, arr.size());
		Assert.assertEquals(2.0f, arr.get(0), DELTA);
		Assert.assertEquals(3.0f, arr.get(1), DELTA);
	}

	@Test
	public void removeAt_middleIndex_shiftsTailDown() {
		FloatArray arr = new FloatArray();
		arr.add(1.0f);
		arr.add(2.0f);
		arr.add(3.0f);
		arr.add(4.0f);

		Assert.assertTrue(arr.removeAt(1));
		Assert.assertEquals(3, arr.size());
		Assert.assertEquals(1.0f, arr.get(0), DELTA);
		Assert.assertEquals(3.0f, arr.get(1), DELTA);
		Assert.assertEquals(4.0f, arr.get(2), DELTA);
	}

	@Test
	public void removeAt_onlyElement_yieldsEmptyArray() {
		FloatArray arr = new FloatArray();
		arr.add(99.5f);
		Assert.assertTrue(arr.removeAt(0));
		Assert.assertTrue(arr.empty());
		Assert.assertEquals(0, arr.size());
	}

	@Test
	public void removeAt_allElementsOneByOne_endsEmpty() {
		FloatArray arr = new FloatArray();
		for (int i = 0; i < 10; ++i) arr.add(i * 0.5f);
		for (int i = 9; i >= 0; --i) {
			Assert.assertTrue(arr.removeAt(i));
		}
		Assert.assertTrue(arr.empty());
	}

	// ============================================================
	// zero
	// ============================================================

	@Test
	public void zero_setsAllElementsToZero_andResizesIndex() {
		FloatArray arr = new FloatArray();
		arr.add(1.5f);
		arr.add(2.5f);
		arr.add(3.5f);

		arr.zero(3);
		Assert.assertTrue("expected size >= 3", arr.size() >= 3);
		for (int i = 0; i < 3; ++i) {
			Assert.assertEquals(0.0f, arr.get(i), DELTA);
		}
	}

	@Test
	public void zero_growsBackingWhenSizeExceedsCapacity() {
		FloatArray arr = new FloatArray(1);
		arr.zero(100);
		Assert.assertTrue(arr.size() >= 100);
		for (int i = 0; i < 100; ++i) {
			Assert.assertEquals(0.0f, arr.get(i), DELTA);
		}
	}

	@Test
	public void zero_clearsExistingValues_evenWhenWithinCapacity() {
		FloatArray arr = new FloatArray();
		for (int i = 1; i <= 10; ++i) arr.add(i * 0.5f);

		arr.zero(5);
		// zero 必须把现有值真清掉
		Assert.assertFalse(arr.contains(0.5f));
		Assert.assertFalse(arr.contains(5.0f));
		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(0.0f, arr.get(i), DELTA);
		}
	}

	// ============================================================
	// toString
	// ============================================================

	@Test
	public void toString_emptyArray() {
		Assert.assertEquals("[]", new FloatArray().toString());
	}

	@Test
	public void toString_singleElement_hasNoTrailingSeparator() {
		FloatArray arr = new FloatArray();
		arr.add(0.5f);
		Assert.assertEquals("[0.5]", arr.toString());
	}

	@Test
	public void toString_multipleElements_useCommaSpace() {
		FloatArray arr = new FloatArray();
		arr.add(1.0f);
		arr.add(2.5f);
		arr.add(3.0f);
		Assert.assertEquals("[1.0, 2.5, 3.0]", arr.toString());
	}

	@Test
	public void toString_handlesSpecialFloatValues() {
		FloatArray arr = new FloatArray();
		arr.add(Float.NaN);
		arr.add(Float.POSITIVE_INFINITY);
		arr.add(Float.NEGATIVE_INFINITY);
		Assert.assertEquals("[NaN, Infinity, -Infinity]", arr.toString());
	}

	@Test
	public void toString_reflectsLiveStateAfterRemoveAndClear() {
		FloatArray arr = new FloatArray();
		arr.add(1.0f);
		arr.add(2.0f);
		arr.add(3.0f);
		arr.removeAt(1);
		Assert.assertEquals("[1.0, 3.0]", arr.toString());

		arr.clear();
		Assert.assertEquals("[]", arr.toString());
	}

	// ============================================================
	// 综合 / 压力
	// ============================================================

	@Test
	public void stress_addReverseRemove_remainsConsistent() {
		FloatArray arr = new FloatArray(2);
		for (int i = 0; i < 200; ++i) arr.add(i * 0.25f);
		Assert.assertEquals(200, arr.size());

		arr.reverse();
		Assert.assertEquals(199 * 0.25f, arr.get(0), DELTA);
		Assert.assertEquals(0.0f, arr.get(199), DELTA);

		// 反复从中间删除，索引应保持一致
		int prevSize = arr.size();
		Assert.assertTrue(arr.removeAt(100));
		Assert.assertEquals(prevSize - 1, arr.size());

		arr.clear();
		Assert.assertTrue(arr.empty());
	}
}