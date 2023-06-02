package com.shanbay.lib.texas.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.EmptyStackException;

public class IntStackUnitTest {

	@Test
	public void test() {
		IntStack stack = new IntStack();
		Assert.assertTrue(stack.empty());

		try {
			stack.top();
			Assert.fail();
		} catch (EmptyStackException ignore) {

		}

		try {
			stack.bottom();
			Assert.fail();
		} catch (EmptyStackException ignore) {

		}

		try {
			stack.pop();
			Assert.fail();
		} catch (EmptyStackException ignore) {

		}

		int size = IntStack.DEFAULT_SIZE * 2;
		for (int i = 0; i < size; ++i) {
			stack.push(i);
			Assert.assertEquals(i, stack.top());
		}
		Assert.assertEquals(0, stack.bottom());

		for (int i = size - 1; i >= 0; --i) {
			Assert.assertEquals(i, stack.pop());
		}
		Assert.assertTrue(stack.empty());

		try {
			stack.top();
			Assert.fail();
		} catch (EmptyStackException ignore) {

		}

		try {
			stack.bottom();
			Assert.fail();
		} catch (EmptyStackException ignore) {

		}

		try {
			stack.pop();
			Assert.fail();
		} catch (EmptyStackException ignore) {

		}
	}
}
