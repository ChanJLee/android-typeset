package me.chan.texas.renderer;

import com.shanbay.lib.texas.TestUtils;

import org.junit.Assert;
import org.junit.Test;

public class TouchEventUnitTest {

	@Test
	public void test() {
		TouchEvent event = TouchEvent.obtain(null, 1, 2, 3, 4);
		Assert.assertEquals(1, event.getX(), 0);
		Assert.assertEquals(2, event.getY(), 0);
		Assert.assertEquals(3, event.getRawX(), 0);
		Assert.assertEquals(4, event.getRawY(), 0);

		event.recycle();
		TestUtils.testRecycled(event);

		TouchEvent tmp = event;
		event = TouchEvent.obtain(null, 2, 3, 4, 5);
		Assert.assertSame(tmp, event);

		Assert.assertEquals(2, event.getX(), 0);
		Assert.assertEquals(3, event.getY(), 0);
		Assert.assertEquals(4, event.getRawX(), 0);
		Assert.assertEquals(5, event.getRawY(), 0);
	}
}
