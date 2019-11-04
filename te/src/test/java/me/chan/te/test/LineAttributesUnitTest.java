package me.chan.te.test;

import org.junit.Test;

import me.chan.te.config.LineAttributes;
import me.chan.te.text.Gravity;

import static org.junit.Assert.assertEquals;

public class LineAttributesUnitTest {

	@Test
	public void test() {
		LineAttributes lineAttributes = new LineAttributes(new LineAttributes.Attribute(10, Gravity.LEFT,  10));
		lineAttributes.add(1, new LineAttributes.Attribute(20, Gravity.LEFT,  10));
		lineAttributes.add(2, new LineAttributes.Attribute(30, Gravity.LEFT,  10));

		assertEquals(lineAttributes.get(10).getLineWidth(), 10f, 0);
		assertEquals(lineAttributes.get(1).getLineWidth(), 20f, 0);

		lineAttributes.remove(2);
		assertEquals(lineAttributes.get(2).getLineWidth(), 10f, 0);
	}
}
