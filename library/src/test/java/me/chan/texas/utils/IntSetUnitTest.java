package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

public class IntSetUnitTest {

	// ============================================================
	// Constructor
	// ============================================================

	@Test
	public void defaultConstructor_isEmpty() {
		IntSet set = new IntSet();
		Assert.assertTrue(set.isEmpty());
		Assert.assertEquals(0, set.size());
		Assert.assertTrue(set.getBuckets().length >= 1);
	}

	@Test
	public void constructorWithPositiveSize_allocatesNonZeroCapacity() {
		IntSet set = new IntSet(8);
		Assert.assertTrue(set.isEmpty());
		Assert.assertEquals(0, set.size());
		Assert.assertTrue(set.getBuckets().length >= 1);
	}

	@Test
	public void constructorWithZero_fallsBackToDefaultSize() {
		IntSet set = new IntSet(0);
		Assert.assertEquals(IntSet.DEFAULT_SIZE, set.getBuckets().length);
		Assert.assertTrue(set.isEmpty());
	}

	@Test
	public void constructorWithNegativeSize_fallsBackToDefaultSize() {
		IntSet set = new IntSet(-1);
		Assert.assertEquals(IntSet.DEFAULT_SIZE, set.getBuckets().length);
		Assert.assertTrue(set.isEmpty());

		IntSet hugeNegative = new IntSet(Integer.MIN_VALUE);
		Assert.assertEquals(IntSet.DEFAULT_SIZE, hugeNegative.getBuckets().length);
	}

	// ============================================================
	// find — encoded results from binarySearch
	// ============================================================

	@Test
	public void find_onEmptySet_returnsBitwiseInverseOfZero() {
		IntSet set = new IntSet();
		Assert.assertEquals(0, ~set.find(0));
		Assert.assertEquals(0, ~set.find(Integer.MIN_VALUE));
		Assert.assertEquals(0, ~set.find(Integer.MAX_VALUE));
	}

	@Test
	public void find_existingValues_returnsIndex() {
		IntSet set = new IntSet();
		set.mock(new int[]{1, 5, 7, 10});
		Assert.assertEquals(0, set.find(1));
		Assert.assertEquals(1, set.find(5));
		Assert.assertEquals(2, set.find(7));
		Assert.assertEquals(3, set.find(10));
	}

	@Test
	public void find_missingValues_returnsBitwiseInverseInsertionPoint() {
		IntSet set = new IntSet();
		set.mock(new int[]{1, 5, 7, 10});

		// before everything
		Assert.assertEquals(0, ~set.find(0));
		Assert.assertEquals(0, ~set.find(Integer.MIN_VALUE));

		// after everything
		Assert.assertEquals(4, ~set.find(11));
		Assert.assertEquals(4, ~set.find(Integer.MAX_VALUE));

		// strictly between adjacent pairs
		Assert.assertEquals(1, ~set.find(2));
		Assert.assertEquals(1, ~set.find(4));
		Assert.assertEquals(2, ~set.find(6));
		Assert.assertEquals(3, ~set.find(8));
		Assert.assertEquals(3, ~set.find(9));
	}

	@Test
	public void find_oddSizeBoundaries() {
		IntSet set = new IntSet();
		set.mock(new int[]{1, 5, 7});
		Assert.assertEquals(0, ~set.find(0));
		Assert.assertEquals(3, ~set.find(11));
		Assert.assertEquals(1, ~set.find(2));
		Assert.assertEquals(2, ~set.find(6));
	}

	@Test
	public void find_singleElement() {
		IntSet set = new IntSet();
		set.mock(new int[]{42});
		Assert.assertEquals(0, set.find(42));
		Assert.assertEquals(0, ~set.find(41));
		Assert.assertEquals(1, ~set.find(43));
	}

	// ============================================================
	// add
	// ============================================================

	@Test
	public void add_intoEmptySet_returnsTrue() {
		IntSet set = new IntSet();
		Assert.assertTrue(set.add(0));
		Assert.assertEquals(1, set.size());
		cmp(new int[]{0}, set.getBuckets(), set.size());
	}

	@Test
	public void add_keepsAscendingOrder_regardlessOfInsertOrder() {
		IntSet set = new IntSet();
		Assert.assertTrue(set.add(5));
		Assert.assertTrue(set.add(1));
		Assert.assertTrue(set.add(7));
		Assert.assertTrue(set.add(2));
		Assert.assertEquals(4, set.size());
		cmp(new int[]{1, 2, 5, 7}, set.getBuckets(), set.size());
	}

	@Test
	public void add_atFront_middle_andEnd_keepsOrder() {
		IntSet set = new IntSet();
		set.add(10);
		set.add(20);
		set.add(30);

		// insert at front
		Assert.assertTrue(set.add(5));
		cmp(new int[]{5, 10, 20, 30}, set.getBuckets(), set.size());

		// insert in the middle
		Assert.assertTrue(set.add(15));
		cmp(new int[]{5, 10, 15, 20, 30}, set.getBuckets(), set.size());

		// insert at the end
		Assert.assertTrue(set.add(40));
		cmp(new int[]{5, 10, 15, 20, 30, 40}, set.getBuckets(), set.size());
	}

	@Test
	public void add_duplicate_returnsFalseAndKeepsSize() {
		IntSet set = new IntSet();
		Assert.assertTrue(set.add(3));
		Assert.assertFalse(set.add(3));
		Assert.assertEquals(1, set.size());
		cmp(new int[]{3}, set.getBuckets(), set.size());
	}

	@Test
	public void add_existingMinElement_returnsFalse() {
		// Regression: index == 0 was once treated as "not found" (~0 == -1).
		IntSet set = new IntSet();
		set.add(1);
		set.add(2);
		set.add(7);
		Assert.assertFalse(set.add(1));
		Assert.assertEquals(3, set.size());
		cmp(new int[]{1, 2, 7}, set.getBuckets(), set.size());
	}

	@Test
	public void add_existingMaxElement_returnsFalse() {
		IntSet set = new IntSet();
		set.add(1);
		set.add(2);
		set.add(7);
		Assert.assertFalse(set.add(7));
		Assert.assertEquals(3, set.size());
		cmp(new int[]{1, 2, 7}, set.getBuckets(), set.size());
	}

	@Test
	public void add_existingMiddleElement_returnsFalse() {
		IntSet set = new IntSet();
		set.add(1);
		set.add(2);
		set.add(7);
		Assert.assertFalse(set.add(2));
		Assert.assertEquals(3, set.size());
		cmp(new int[]{1, 2, 7}, set.getBuckets(), set.size());
	}

	@Test
	public void add_negativeAndPositiveValues_areInterleavedInOrder() {
		IntSet set = new IntSet();
		set.add(0);
		set.add(-1);
		set.add(5);
		set.add(-100);
		set.add(2);
		cmp(new int[]{-100, -1, 0, 2, 5}, set.getBuckets(), set.size());
	}

	@Test
	public void add_intMinAndMax_handledCorrectly() {
		IntSet set = new IntSet();
		Assert.assertTrue(set.add(Integer.MAX_VALUE));
		Assert.assertTrue(set.add(Integer.MIN_VALUE));
		Assert.assertTrue(set.add(0));
		cmp(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE},
			set.getBuckets(), set.size());

		Assert.assertTrue(set.contains(Integer.MIN_VALUE));
		Assert.assertTrue(set.contains(Integer.MAX_VALUE));
		Assert.assertFalse(set.add(Integer.MAX_VALUE));
		Assert.assertFalse(set.add(Integer.MIN_VALUE));
		Assert.assertEquals(3, set.size());
	}

	@Test
	public void add_growsCapacityWhenFull_andRetainsAllElements() {
		IntSet set = new IntSet();
		int initialCapacity = set.getBuckets().length;

		// fill exactly to capacity
		for (int i = 0; i < initialCapacity; ++i) {
			Assert.assertTrue(set.add(i));
		}
		Assert.assertEquals(initialCapacity, set.size());
		Assert.assertEquals(initialCapacity, set.getBuckets().length);

		// one more triggers a doubling resize
		Assert.assertTrue(set.add(initialCapacity));
		Assert.assertEquals(initialCapacity + 1, set.size());
		Assert.assertEquals(initialCapacity * 2, set.getBuckets().length);

		for (int i = 0; i <= initialCapacity; ++i) {
			Assert.assertTrue(set.contains(i));
			Assert.assertEquals(i, set.find(i));
		}
	}

	@Test
	public void add_descendingInserts_triggerMultipleResizes() {
		// IntSet(1) starts with a tiny array, so descending inserts hammer
		// both the front-insertion path and repeated grow() calls.
		IntSet set = new IntSet(1);
		int total = 200;
		for (int i = total - 1; i >= 0; --i) {
			Assert.assertTrue(set.add(i));
		}
		Assert.assertEquals(total, set.size());
		for (int i = 0; i < total; ++i) {
			Assert.assertEquals(i, set.find(i));
			Assert.assertTrue(set.contains(i));
		}
	}

	@Test
	public void add_largeNumberOfRandomishInserts_resultsAreSortedAndUnique() {
		IntSet set = new IntSet();
		int[] xs = {17, 3, 99, -4, 17, 0, 50, -4, 7, 200, 8, 8, 8, -1000, 1000};
		for (int x : xs) set.add(x);

		int[] sortedUnique = {-1000, -4, 0, 3, 7, 8, 17, 50, 99, 200, 1000};
		cmp(sortedUnique, set.getBuckets(), set.size());
		Assert.assertEquals(sortedUnique.length, set.size());
	}

	// ============================================================
	// remove
	// ============================================================

	@Test
	public void remove_fromEmptySet_returnsFalse() {
		IntSet set = new IntSet();
		Assert.assertFalse(set.remove(1));
		Assert.assertFalse(set.remove(0));
		Assert.assertFalse(set.remove(Integer.MIN_VALUE));
		Assert.assertEquals(0, set.size());
	}

	@Test
	public void remove_missingValue_returnsFalseAndKeepsContents() {
		IntSet set = new IntSet();
		set.add(1);
		set.add(2);
		set.add(7);
		Assert.assertFalse(set.remove(-1));
		Assert.assertFalse(set.remove(3));
		Assert.assertFalse(set.remove(8));
		Assert.assertEquals(3, set.size());
		cmp(new int[]{1, 2, 7}, set.getBuckets(), set.size());
	}

	@Test
	public void remove_atFront_shiftsRemainingDown() {
		IntSet set = new IntSet();
		set.add(1);
		set.add(2);
		set.add(7);
		Assert.assertTrue(set.remove(1));
		Assert.assertEquals(2, set.size());
		cmp(new int[]{2, 7}, set.getBuckets(), set.size());
	}

	@Test
	public void remove_atEnd_decrementsSize() {
		IntSet set = new IntSet();
		set.add(1);
		set.add(2);
		set.add(7);
		Assert.assertTrue(set.remove(7));
		Assert.assertEquals(2, set.size());
		cmp(new int[]{1, 2}, set.getBuckets(), set.size());
	}

	@Test
	public void remove_inMiddle_shiftsRemainingDown() {
		IntSet set = new IntSet();
		set.add(1);
		set.add(2);
		set.add(7);
		Assert.assertTrue(set.remove(2));
		Assert.assertEquals(2, set.size());
		cmp(new int[]{1, 7}, set.getBuckets(), set.size());
	}

	@Test
	public void remove_onlyElement_yieldsEmptySet() {
		IntSet set = new IntSet();
		set.add(42);
		Assert.assertTrue(set.remove(42));
		Assert.assertEquals(0, set.size());
		Assert.assertTrue(set.isEmpty());
		Assert.assertFalse(set.contains(42));
	}

	@Test
	public void remove_allElementsOneByOne_endsEmpty() {
		IntSet set = new IntSet();
		int[] values = {10, 20, 30, 40, 50};
		for (int v : values) set.add(v);

		for (int v : values) {
			Assert.assertTrue(set.remove(v));
		}
		Assert.assertTrue(set.isEmpty());
		for (int v : values) {
			Assert.assertFalse(set.contains(v));
		}
	}

	@Test
	public void remove_thenAdd_reusesSlots() {
		IntSet set = new IntSet();
		set.add(1);
		set.add(2);
		set.add(3);
		Assert.assertTrue(set.remove(2));
		Assert.assertTrue(set.add(2));
		Assert.assertEquals(3, set.size());
		cmp(new int[]{1, 2, 3}, set.getBuckets(), set.size());
	}

	@Test
	public void remove_intMinAndMax_atBoundaryPositions() {
		IntSet set = new IntSet();
		set.add(Integer.MIN_VALUE);
		set.add(0);
		set.add(Integer.MAX_VALUE);
		Assert.assertTrue(set.remove(Integer.MIN_VALUE));
		Assert.assertTrue(set.remove(Integer.MAX_VALUE));
		Assert.assertEquals(1, set.size());
		cmp(new int[]{0}, set.getBuckets(), set.size());
		Assert.assertFalse(set.contains(Integer.MIN_VALUE));
		Assert.assertFalse(set.contains(Integer.MAX_VALUE));
		Assert.assertTrue(set.contains(0));
	}

	@Test
	public void remove_doubleRemove_secondCallReturnsFalse() {
		IntSet set = new IntSet();
		set.add(5);
		Assert.assertTrue(set.remove(5));
		Assert.assertFalse(set.remove(5));
		Assert.assertEquals(0, set.size());
	}

	// ============================================================
	// contains
	// ============================================================

	@Test
	public void contains_onEmptySet_returnsFalse() {
		IntSet set = new IntSet();
		Assert.assertFalse(set.contains(0));
		Assert.assertFalse(set.contains(Integer.MAX_VALUE));
		Assert.assertFalse(set.contains(Integer.MIN_VALUE));
	}

	@Test
	public void contains_reflectsAddAndRemove() {
		IntSet set = new IntSet();
		set.add(5);
		Assert.assertTrue(set.contains(5));
		Assert.assertFalse(set.contains(4));
		Assert.assertFalse(set.contains(6));
		set.remove(5);
		Assert.assertFalse(set.contains(5));
	}

	@Test
	public void contains_acrossManyInsertions() {
		IntSet set = new IntSet();
		for (int i = 0; i < 50; ++i) {
			set.add(i * 2);
		}
		for (int i = 0; i < 50; ++i) {
			Assert.assertTrue("missing even " + (i * 2), set.contains(i * 2));
			Assert.assertFalse("unexpected odd " + (i * 2 + 1), set.contains(i * 2 + 1));
		}
	}

	// ============================================================
	// clear
	// ============================================================

	@Test
	public void clear_onEmptySet_isNoOp() {
		IntSet set = new IntSet();
		set.clear();
		Assert.assertTrue(set.isEmpty());
		Assert.assertEquals(0, set.size());
	}

	@Test
	public void clear_emptiesPopulatedSet() {
		IntSet set = new IntSet();
		set.add(1);
		set.add(2);
		set.add(3);
		set.clear();
		Assert.assertTrue(set.isEmpty());
		Assert.assertEquals(0, set.size());
		Assert.assertFalse(set.contains(1));
		Assert.assertFalse(set.contains(2));
		Assert.assertFalse(set.contains(3));
	}

	@Test
	public void clear_doesNotShrinkBacking_butAllowsReuse() {
		IntSet set = new IntSet();
		for (int i = 0; i < 100; ++i) set.add(i);
		int capacityAfterGrowth = set.getBuckets().length;

		set.clear();
		// capacity is preserved across clear; clear is O(1)
		Assert.assertEquals(capacityAfterGrowth, set.getBuckets().length);

		Assert.assertTrue(set.add(99));
		Assert.assertEquals(1, set.size());
		Assert.assertTrue(set.contains(99));
		Assert.assertFalse(set.contains(0));
	}

	// ============================================================
	// size / isEmpty consistency
	// ============================================================

	@Test
	public void sizeAndIsEmpty_stayConsistentThroughLifecycle() {
		IntSet set = new IntSet();
		Assert.assertTrue(set.isEmpty());
		Assert.assertEquals(0, set.size());

		set.add(1);
		Assert.assertFalse(set.isEmpty());
		Assert.assertEquals(1, set.size());

		set.add(1); // duplicate
		Assert.assertEquals(1, set.size());

		set.remove(2); // missing
		Assert.assertEquals(1, set.size());

		set.remove(1);
		Assert.assertTrue(set.isEmpty());
		Assert.assertEquals(0, set.size());
	}

	// ============================================================
	// helpers
	// ============================================================

	private void cmp(int[] expected, int[] buckets, int size) {
		Assert.assertEquals("size mismatch", expected.length, size);
		for (int i = 0; i < size; ++i) {
			Assert.assertEquals("at index " + i, expected[i], buckets[i]);
		}
	}
}