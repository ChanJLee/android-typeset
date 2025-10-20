package me.chan.texas.ext.markdown.math.renderer;

import org.junit.Assert;
import org.junit.Test;

public class RendererNodeUnitTest {

	@Test
	public void test() {
		int spec = RendererNode.makeMeasureSpec(12, RendererNode.EXACTLY);
		Assert.assertEquals(12, RendererNode.getSize(spec));
		Assert.assertEquals(RendererNode.EXACTLY, RendererNode.getMode(spec));
	}
}
