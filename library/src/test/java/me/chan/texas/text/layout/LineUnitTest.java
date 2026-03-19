package me.chan.texas.text.layout;

import org.junit.Assert;
import org.junit.Test;

public class LineUnitTest {

	@Test
	public void test() {
		Line line = Line.obtain();
		Span b1 = TextSpan.obtain("b1", 0, 2, null, null, null, null);
		Span b2 = TextSpan.obtain("b2", 0, 2, null, null, null, null);
		Span b3 = TextSpan.obtain("b3", 0, 2, null, null, null, null);
		Span b4 = TextSpan.obtain("b4", 0, 2, null, null, null, null);
		Span b5 = TextSpan.obtain("b5", 0, 2, null, null, null, null);
		line.add(b1);
		line.add(b2);
		line.add(Glue.EMPTY);
		line.add(b3);
		line.add(Glue.EMPTY);
		line.add(Glue.EMPTY);
		line.add(b4);
		line.add(Glue.EMPTY);
		line.add(b5);
		line.add(Glue.EMPTY);

		Assert.assertSame(10, line.getElementCount());
		line.trim();
		Assert.assertSame(5, line.getElementCount());
	}
}
