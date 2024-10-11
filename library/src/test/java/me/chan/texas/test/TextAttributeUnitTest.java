package me.chan.texas.test;

import me.chan.texas.Texas;
import me.chan.texas.measurer.MockMeasurer;

import me.chan.texas.test.mock.MockTextPaint;

import me.chan.texas.text.TextAttribute;

import org.junit.Assert;
import org.junit.Test;

public class TextAttributeUnitTest {

	@Test
	public void test() {
		TextAttribute textAttribute = new TextAttribute(new MockMeasurer(new MockTextPaint(20)));
		Assert.assertNotNull(textAttribute);
		Assert.assertEquals(textAttribute.getHyphenHeight(), 20, 0);
		Assert.assertEquals(textAttribute.getHyphenWidth(), 20, 0);

		Texas.TypesetFactor factor = Texas.getTypesetFactor();
		Assert.assertEquals(textAttribute.getSpaceWidth(), 20 * factor.spaceWidthFactor, 0);
		Assert.assertEquals(textAttribute.getSpaceShrink(), 20 * factor.spaceShrinkFactor, 0);
		Assert.assertEquals(textAttribute.getSpaceStretch(), 20 * factor.spaceStretchFactor, 0);
	}
}
