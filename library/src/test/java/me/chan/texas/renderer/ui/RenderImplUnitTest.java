package me.chan.texas.renderer.ui;

import org.junit.Assert;
import org.junit.Test;

public class RenderImplUnitTest {

	@Test
	public void test() {
		Assert.assertTrue(RendererAdapterImpl.isDisableReuseType(1));
		Assert.assertFalse(RendererAdapterImpl.isDisableReuseType(0));
		Assert.assertFalse(RendererAdapterImpl.isDisableReuseType(-1));
	}
}
