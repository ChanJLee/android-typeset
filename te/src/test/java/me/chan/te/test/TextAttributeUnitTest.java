package me.chan.te.test;

import org.junit.Test;

import me.chan.te.test.mock.MockMeasurer;
import me.chan.te.test.mock.MockTextPaint;
import me.chan.te.text.TextAttribute;
import me.chan.te.text.Gravity;

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
