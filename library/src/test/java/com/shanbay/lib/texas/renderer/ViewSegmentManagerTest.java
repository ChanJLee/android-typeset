package com.shanbay.lib.texas.renderer;

import com.shanbay.lib.texas.renderer.ui.TexasAdapter;

import org.junit.Assert;
import org.junit.Test;

public class ViewSegmentManagerTest {

	@Test
	public void test() {
		TexasAdapter.ViewSegmentManager manager = new TexasAdapter.ViewSegmentManager();
		try {
			Assert.assertEquals(manager.getLayout(1), 1);
			Assert.fail("check get layout failed");
		} catch (Throwable throwable) {
		}

		Assert.assertEquals(manager.getType(1, 0, false), 1);
		Assert.assertEquals(manager.getType(0, 0, false), 2);
		Assert.assertEquals(manager.getType(-3, 0, false), 3);

		Assert.assertEquals(manager.getType(1, 0, false), 1);
		Assert.assertEquals(manager.getType(0, 0, false), 2);
		Assert.assertEquals(manager.getType(-3, 0, false), 3);
		Assert.assertEquals(manager.getLayout(1), 1);
		Assert.assertEquals(manager.getLayout(2), 0);
		Assert.assertEquals(manager.getLayout(3), -3);


		int offset = -1;
		Assert.assertEquals(manager.getType(1, 0, true), -3 + offset);
		Assert.assertEquals(manager.getType(1, 1, true), -4 + offset);
		Assert.assertEquals(manager.getType(1, 2, true), -5 + offset);
		Assert.assertEquals(manager.getType(2, 3, true), -6 + offset);
		try {
			Assert.assertEquals(manager.getType(2, 2, true), -4);
			Assert.fail();
		} catch (IllegalStateException e) {

		}

		Assert.assertEquals(manager.getType(1, 0, true), -3 + offset);
		Assert.assertEquals(manager.getType(1, 1, true), -4 + offset);
		Assert.assertEquals(manager.getType(1, 2, true), -5 + offset);
		Assert.assertEquals(manager.getType(2, 3, true), -6 + offset);

		Assert.assertEquals(manager.getLayout(-3 + offset), 1);
		Assert.assertEquals(manager.getLayout(-4 + offset), 1);
		Assert.assertEquals(manager.getLayout(-5 + offset), 1);
		Assert.assertEquals(manager.getLayout(-6 + offset), 2);
	}
}
