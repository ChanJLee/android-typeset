package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.EmptyStackException;

public class IntStackUnitTest {

	// ============================================================
	// 构造函数
	// ============================================================

	@Test
	public void defaultConstructor_isEmpty() {
		IntStack stack = new IntStack();
		Assert.assertTrue(stack.empty());
		Assert.assertEquals(0, stack.size());
	}

	@Test
	public void copyConstructor_fromEmpty_alsoEmpty() {
		IntStack original = new IntStack();
		IntStack copy = new IntStack(original);

		Assert.assertTrue(copy.empty());
		Assert.assertEquals(0, copy.size());

		// 双方独立可用
		copy.push(42);
		Assert.assertTrue(original.empty());
		Assert.assertEquals(1, copy.size());
		Assert.assertEquals(42, copy.top());
	}

	@Test
	public void copyConstructor_fromPopulated_clonesState() {
		IntStack original = new IntStack();
		original.push(1);
		original.push(2);
		original.push(3);

		IntStack copy = new IntStack(original);

		Assert.assertEquals(3, copy.size());
		Assert.assertEquals(3, copy.top());
		Assert.assertEquals(1, copy.bottom());
	}

	@Test
	public void copyConstructor_isDeep_mutationsAreIndependent() {
		IntStack original = new IntStack();
		original.push(1);
		original.push(2);

		IntStack copy = new IntStack(original);

		original.push(99);
		Assert.assertEquals(3, original.size());
		Assert.assertEquals(99, original.top());

		// copy 不应受影响
		Assert.assertEquals(2, copy.size());
		Assert.assertEquals(2, copy.top());

		// 反向修改 copy 也不应影响 original
		Assert.assertEquals(2, copy.pop());
		Assert.assertEquals(1, copy.size());
		Assert.assertEquals(99, original.top());
		Assert.assertEquals(3, original.size());
	}

	@Test
	public void copyConstructor_preservesGrownBacking() {
		// 构造一个超过默认容量的栈，验证 copy 正确处理 grown array
		IntStack original = new IntStack();
		int n = IntStack.DEFAULT_SIZE * 4 + 1;
		for (int i = 0; i < n; ++i) {
			original.push(i);
		}

		IntStack copy = new IntStack(original);
		Assert.assertEquals(n, copy.size());
		for (int i = n - 1; i >= 0; --i) {
			Assert.assertEquals(i, copy.pop());
		}
		Assert.assertTrue(copy.empty());

		// 原栈未被消费
		Assert.assertEquals(n, original.size());
	}

	// ============================================================
	// empty / size 一致性
	// ============================================================

	@Test
	public void emptyAndSize_stayConsistent() {
		IntStack stack = new IntStack();
		Assert.assertTrue(stack.empty());
		Assert.assertEquals(0, stack.size());

		stack.push(10);
		Assert.assertFalse(stack.empty());
		Assert.assertEquals(1, stack.size());

		stack.push(20);
		Assert.assertEquals(2, stack.size());

		stack.pop();
		Assert.assertEquals(1, stack.size());
		Assert.assertFalse(stack.empty());

		stack.pop();
		Assert.assertEquals(0, stack.size());
		Assert.assertTrue(stack.empty());
	}

	// ============================================================
	// push
	// ============================================================

	@Test
	public void push_singleElement_setsTopAndBottom() {
		IntStack stack = new IntStack();
		stack.push(7);
		Assert.assertEquals(1, stack.size());
		Assert.assertEquals(7, stack.top());
		Assert.assertEquals(7, stack.bottom());
	}

	@Test
	public void push_duplicatesAreAllowed() {
		IntStack stack = new IntStack();
		stack.push(5);
		stack.push(5);
		stack.push(5);
		Assert.assertEquals(3, stack.size());
		Assert.assertEquals(5, stack.top());
		Assert.assertEquals(5, stack.bottom());
	}

	@Test
	public void push_zeroAndNegativeAndBoundaryValues() {
		IntStack stack = new IntStack();
		stack.push(0);
		stack.push(-1);
		stack.push(Integer.MIN_VALUE);
		stack.push(Integer.MAX_VALUE);

		Assert.assertEquals(4, stack.size());
		Assert.assertEquals(Integer.MAX_VALUE, stack.pop());
		Assert.assertEquals(Integer.MIN_VALUE, stack.pop());
		Assert.assertEquals(-1, stack.pop());
		Assert.assertEquals(0, stack.pop());
		Assert.assertTrue(stack.empty());
	}

	@Test
	public void push_atDefaultCapacity_doesNotResizeYet() {
		IntStack stack = new IntStack();
		// 一直填到容量上限，但未越界
		for (int i = 0; i < IntStack.DEFAULT_SIZE; ++i) {
			stack.push(i);
		}
		Assert.assertEquals(IntStack.DEFAULT_SIZE, stack.size());
		Assert.assertEquals(IntStack.DEFAULT_SIZE - 1, stack.top());
		Assert.assertEquals(0, stack.bottom());
	}

	@Test
	public void push_oneElementBeyondCapacity_triggersResize() {
		IntStack stack = new IntStack();
		// 触发一次扩容
		for (int i = 0; i <= IntStack.DEFAULT_SIZE; ++i) {
			stack.push(i);
		}
		Assert.assertEquals(IntStack.DEFAULT_SIZE + 1, stack.size());

		// 验证扩容后所有元素仍按 LIFO 顺序保留
		for (int i = IntStack.DEFAULT_SIZE; i >= 0; --i) {
			Assert.assertEquals(i, stack.pop());
		}
		Assert.assertTrue(stack.empty());
	}

	@Test
	public void push_manyResizes_preservesOrder() {
		IntStack stack = new IntStack();
		int n = IntStack.DEFAULT_SIZE * 64; // 触发多次扩容
		for (int i = 0; i < n; ++i) {
			stack.push(i);
		}
		Assert.assertEquals(n, stack.size());
		Assert.assertEquals(n - 1, stack.top());
		Assert.assertEquals(0, stack.bottom());

		for (int i = n - 1; i >= 0; --i) {
			Assert.assertEquals(i, stack.pop());
		}
		Assert.assertTrue(stack.empty());
	}

	// ============================================================
	// pop
	// ============================================================

	@Test(expected = EmptyStackException.class)
	public void pop_onEmptyStack_throws() {
		new IntStack().pop();
	}

	@Test(expected = EmptyStackException.class)
	public void pop_afterPushingAndPoppingAll_throws() {
		IntStack stack = new IntStack();
		stack.push(1);
		stack.pop();
		stack.pop(); // 这里应该抛
	}

	@Test
	public void pop_returnsValuesInLifoOrder() {
		IntStack stack = new IntStack();
		stack.push(1);
		stack.push(2);
		stack.push(3);

		Assert.assertEquals(3, stack.pop());
		Assert.assertEquals(2, stack.pop());
		Assert.assertEquals(1, stack.pop());
		Assert.assertTrue(stack.empty());
	}

	@Test
	public void pop_decreasesSize_byOne() {
		IntStack stack = new IntStack();
		stack.push(1);
		stack.push(2);
		Assert.assertEquals(2, stack.size());
		stack.pop();
		Assert.assertEquals(1, stack.size());
		stack.pop();
		Assert.assertEquals(0, stack.size());
	}

	@Test
	public void pop_thenPush_reusesSlot() {
		IntStack stack = new IntStack();
		stack.push(10);
		stack.push(20);
		Assert.assertEquals(20, stack.pop());

		stack.push(30);
		Assert.assertEquals(2, stack.size());
		Assert.assertEquals(30, stack.top());
		Assert.assertEquals(10, stack.bottom());
	}

	// ============================================================
	// top
	// ============================================================

	@Test(expected = EmptyStackException.class)
	public void top_onEmptyStack_throws() {
		new IntStack().top();
	}

	@Test(expected = EmptyStackException.class)
	public void top_onClearedStack_throws() {
		IntStack stack = new IntStack();
		stack.push(1);
		stack.clear();
		stack.top();
	}

	@Test
	public void top_doesNotModifyStack() {
		IntStack stack = new IntStack();
		stack.push(1);
		stack.push(2);
		Assert.assertEquals(2, stack.top());
		Assert.assertEquals(2, stack.top());
		Assert.assertEquals(2, stack.top());
		Assert.assertEquals(2, stack.size());
	}

	@Test
	public void top_reflectsPushesAndPops() {
		IntStack stack = new IntStack();
		stack.push(1);
		Assert.assertEquals(1, stack.top());
		stack.push(2);
		Assert.assertEquals(2, stack.top());
		stack.push(3);
		Assert.assertEquals(3, stack.top());
		stack.pop();
		Assert.assertEquals(2, stack.top());
		stack.pop();
		Assert.assertEquals(1, stack.top());
	}

	// ============================================================
	// bottom
	// ============================================================

	@Test(expected = EmptyStackException.class)
	public void bottom_onEmptyStack_throws() {
		new IntStack().bottom();
	}

	@Test(expected = EmptyStackException.class)
	public void bottom_onClearedStack_throws() {
		IntStack stack = new IntStack();
		stack.push(1);
		stack.clear();
		stack.bottom();
	}

	@Test
	public void bottom_isStableUnderPushes() {
		IntStack stack = new IntStack();
		stack.push(7);
		Assert.assertEquals(7, stack.bottom());

		for (int i = 0; i < 100; ++i) {
			stack.push(i);
			Assert.assertEquals(7, stack.bottom());
		}
	}

	@Test
	public void bottom_changesOnlyAfterStackBecomesEmptyAndRefilled() {
		IntStack stack = new IntStack();
		stack.push(1);
		stack.push(2);
		stack.push(3);
		Assert.assertEquals(1, stack.bottom());

		// pop 至非空时 bottom 不变
		stack.pop();
		Assert.assertEquals(1, stack.bottom());
		stack.pop();
		Assert.assertEquals(1, stack.bottom());

		// 清空再重填，bottom 变成新的第一个元素
		stack.pop();
		Assert.assertTrue(stack.empty());
		stack.push(99);
		Assert.assertEquals(99, stack.bottom());
	}

	// ============================================================
	// clear
	// ============================================================

	@Test
	public void clear_onEmpty_isNoOp() {
		IntStack stack = new IntStack();
		stack.clear();
		Assert.assertTrue(stack.empty());
		Assert.assertEquals(0, stack.size());
	}

	@Test
	public void clear_onPopulated_emptiesStack() {
		IntStack stack = new IntStack();
		stack.push(1);
		stack.push(2);
		stack.push(3);
		stack.clear();
		Assert.assertTrue(stack.empty());
		Assert.assertEquals(0, stack.size());
	}

	@Test
	public void clear_allowsReuse() {
		IntStack stack = new IntStack();
		for (int i = 0; i < 50; ++i) stack.push(i);
		stack.clear();

		stack.push(100);
		Assert.assertFalse(stack.empty());
		Assert.assertEquals(1, stack.size());
		Assert.assertEquals(100, stack.top());
		Assert.assertEquals(100, stack.bottom());
	}

	// ============================================================
	// toString
	// ============================================================

	@Test
	public void toString_emptyStack_returnsBraces() {
		Assert.assertEquals("{}", new IntStack().toString());
	}

	@Test
	public void toString_includesAllElementsWithTrailingComma() {
		IntStack stack = new IntStack();
		stack.push(1);
		Assert.assertEquals("{1,}", stack.toString());

		stack.push(2);
		stack.push(3);
		Assert.assertEquals("{1,2,3,}", stack.toString());
	}

	@Test
	public void toString_handlesNegativeAndBoundaryValues() {
		IntStack stack = new IntStack();
		stack.push(Integer.MIN_VALUE);
		stack.push(-1);
		stack.push(Integer.MAX_VALUE);
		Assert.assertEquals(
			"{" + Integer.MIN_VALUE + ",-1," + Integer.MAX_VALUE + ",}",
			stack.toString());
	}

	@Test
	public void toString_reflectsCurrentLiveState_afterPopAndClear() {
		IntStack stack = new IntStack();
		stack.push(1);
		stack.push(2);
		stack.push(3);
		stack.pop();
		Assert.assertEquals("{1,2,}", stack.toString());

		stack.clear();
		Assert.assertEquals("{}", stack.toString());
	}

	// ============================================================
	// 综合压力 / LIFO 不变量
	// ============================================================

	@Test
	public void interleavedPushAndPop_maintainsLifoOrder() {
		IntStack stack = new IntStack();
		stack.push(1);
		stack.push(2);
		Assert.assertEquals(2, stack.pop());
		stack.push(3);
		stack.push(4);
		Assert.assertEquals(4, stack.top());
		Assert.assertEquals(4, stack.pop());
		Assert.assertEquals(3, stack.pop());
		Assert.assertEquals(1, stack.pop());
		Assert.assertTrue(stack.empty());
	}

	@Test
	public void stress_alternatingPushPop_preservesInvariant() {
		IntStack stack = new IntStack();
		// 反复在容量边界附近抖动，验证多次扩容/收尾后的 LIFO 不变量
		for (int round = 0; round < 50; ++round) {
			for (int i = 0; i < IntStack.DEFAULT_SIZE * 3; ++i) {
				stack.push(round * 1000 + i);
			}
			for (int i = IntStack.DEFAULT_SIZE * 3 - 1; i >= 0; --i) {
				Assert.assertEquals(round * 1000 + i, stack.pop());
			}
			Assert.assertTrue(stack.empty());
		}
	}
}