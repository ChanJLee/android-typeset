package me.chan.te.test;

import org.junit.Assert;
import org.junit.Test;

import me.chan.te.misc.ObjectFactory;

public class ObjectFactoryUnitTest {

	@Test
	public void test() {

		try {
			new ObjectFactory<>(0);
			Assert.fail("test buffer size 0 failed");
		} catch (IllegalArgumentException e) {
			/* do nothing */
		}

		try {
			new ObjectFactory<>(-1);
			Assert.fail("test buffer size -1 failed");
		} catch (IllegalArgumentException e) {
			/* do nothing */
		}

		ObjectFactory<String> objectFactory = new ObjectFactory<>(1);
		Assert.assertNull(objectFactory.acquire());

		// normal release
		objectFactory.release("hello");
		Assert.assertNotNull(objectFactory.acquire());
		Assert.assertNull(objectFactory.acquire());

		// over release
		objectFactory.release("world");
		objectFactory.release("hello");
		Assert.assertEquals(objectFactory.acquire(), "world");
		Assert.assertNull(objectFactory.acquire());
	}
}
