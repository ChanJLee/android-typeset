package me.chan.te.test;

import org.junit.Assert;
import org.junit.Test;

import me.chan.te.misc.ObjectFactory;
import me.chan.te.text.Paragraph;

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

		// normal recycle
		objectFactory.release("hello");
		Assert.assertNotNull(objectFactory.acquire());
		Assert.assertNull(objectFactory.acquire());

		// over recycle
		objectFactory.release("world");
		objectFactory.release("hello");
		Assert.assertEquals(objectFactory.acquire(), "world");
		Assert.assertNull(objectFactory.acquire());

		ObjectFactory<Paragraph.Line> factory = new ObjectFactory<>(1);

		Paragraph.Line line = Paragraph.Line.obtain();
		Assert.assertNotNull(line);

		factory.release(line);
		Assert.assertSame(line, factory.acquire());

		factory.release(line);
		factory.clean();
		Assert.assertNotSame(line, factory.acquire());
	}
}
