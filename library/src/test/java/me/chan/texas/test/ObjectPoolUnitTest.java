package me.chan.texas.test;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.layout.Line;

public class ObjectPoolUnitTest {

	@Test
	public void test() {

		try {
			new ObjectPool<>(0);
			Assert.fail("test buffer size 0 failed");
		} catch (IllegalArgumentException e) {
			/* do nothing */
		}

		try {
			new ObjectPool<>(-1);
			Assert.fail("test buffer size -1 failed");
		} catch (IllegalArgumentException e) {
			/* do nothing */
		}

		ObjectPool<String> objectPool = new ObjectPool<>(1);
		Assert.assertNull(objectPool.acquire());

		// normal recycle
		objectPool.release("hello");
		Assert.assertNotNull(objectPool.acquire());
		Assert.assertNull(objectPool.acquire());

		// over recycle
		objectPool.release("world");
		objectPool.release("hello");
		Assert.assertEquals(objectPool.acquire(), "world");
		Assert.assertNull(objectPool.acquire());

		ObjectPool<Line> factory = new ObjectPool<>(1);

		Line line = Line.obtain();
		Assert.assertNotNull(line);

		factory.release(line);
		Assert.assertSame(line, factory.acquire());

		factory.release(line);
		factory.clean();
		Assert.assertNotSame(line, factory.acquire());
	}
}
