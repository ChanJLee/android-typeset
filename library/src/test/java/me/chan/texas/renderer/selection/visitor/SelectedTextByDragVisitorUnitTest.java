package me.chan.texas.renderer.selection.visitor;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.PointF;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.text.layout.Line;

public class SelectedTextByDragVisitorUnitTest {

	@Test
	public void test() {
		PointF p1 = new PointF(0, 20);
		PointF p2 = new PointF(20, 30);

		Line line = Line.obtain();
		line.setLineWidth(10);
		line.setLineHeight(10);

		SelectedTextByDragVisitor.LineRange lineRange = new SelectedTextByDragVisitor.LineRange();
		SelectedTextByDragVisitor.updateLineRange(line, 0, p1.y - 1, p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_STOP_LINE_VISIT, lineRange.sig);

		lineRange = new SelectedTextByDragVisitor.LineRange();
		SelectedTextByDragVisitor.updateLineRange(line, 0, p2.y + line.getLineHeight(), p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_STOP_PARA_VISIT, lineRange.sig);
	}
}
