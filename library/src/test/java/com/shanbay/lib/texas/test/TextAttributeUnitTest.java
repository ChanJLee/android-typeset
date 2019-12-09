package com.shanbay.lib.texas.test;

import org.junit.Test;

import com.shanbay.lib.texas.test.mock.MockMeasurer;
import com.shanbay.lib.texas.test.mock.MockTextPaint;
import com.shanbay.lib.texas.text.TextAttribute;
import com.shanbay.lib.texas.text.Gravity;

import static org.junit.Assert.assertEquals;

public class TextAttributeUnitTest {

	@Test
	public void test() {
		TextAttribute textAttribute = new TextAttribute(new MockMeasurer(new MockTextPaint(20)));
		textAttribute.setDefaultAttribute(new TextAttribute.LineAttribute(10, Gravity.LEFT));
		textAttribute.add(1, new TextAttribute.LineAttribute(20, Gravity.LEFT));
		textAttribute.add(2, new TextAttribute.LineAttribute(30, Gravity.LEFT));

		assertEquals(textAttribute.get(10).getLineWidth(), 10f, 0);
		assertEquals(textAttribute.get(1).getLineWidth(), 20f, 0);

		textAttribute.remove(2);
		assertEquals(textAttribute.get(2).getLineWidth(), 10f, 0);
	}
}
