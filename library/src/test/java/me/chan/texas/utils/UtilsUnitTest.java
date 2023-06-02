package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

public class UtilsUnitTest {

	@Test
	public void test() {
		String fuck = "d+450";
		Assert.assertEquals(TexasUtils.parseInt(fuck, 1, fuck.length() - 1), 45);
		fuck = "d-450";
		Assert.assertEquals(TexasUtils.parseInt(fuck, 1, fuck.length()), -450);
	}
}
