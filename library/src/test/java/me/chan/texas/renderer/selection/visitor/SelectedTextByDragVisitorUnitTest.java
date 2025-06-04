package me.chan.texas.renderer.selection.visitor;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.PointF;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.text.layout.Line;

public class SelectedTextByDragVisitorUnitTest {

	@Test
	public void test() {
		PointF p1 = new PointF(10, 20);
		PointF p2 = new PointF(20, 30);

		Line line = Line.obtain();
		line.setLineWidth(40);
		line.setLineHeight(10);

		SelectedTextByDragVisitor.LineRange lineRange = new SelectedTextByDragVisitor.LineRange();
		SelectedTextByDragVisitor.updateLineRange(line, 0, p1.y - 1, p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_STOP_LINE_VISIT, lineRange.sig);
		Assert.assertNull(lineRange.policy);
		Assert.assertEquals(0, lineRange.startX, 0);
		Assert.assertEquals(0, lineRange.endX, 0);

		SelectedTextByDragVisitor.updateLineRange(line, 0, p2.y + line.getLineHeight(), p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_STOP_PARA_VISIT, lineRange.sig);
		Assert.assertNull(lineRange.policy);
		Assert.assertEquals(0, lineRange.startX, 0);
		Assert.assertEquals(0, lineRange.endX, 0);

		// 单行
		SelectedTextByDragVisitor.updateLineRange(line, 0, p2.y, p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_NORMAL, lineRange.sig);
		Assert.assertEquals(SelectedTextByDragVisitor.LINE_RANGE_POLICY_BETWEEN_P1X_P2X, lineRange.policy);
		Assert.assertEquals(10, lineRange.startX, 0);
		Assert.assertEquals(20, lineRange.endX, 0);
		// 模拟非左对齐
		SelectedTextByDragVisitor.updateLineRange(line, 10, p2.y, p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_NORMAL, lineRange.sig);
		Assert.assertEquals(SelectedTextByDragVisitor.LINE_RANGE_POLICY_BETWEEN_P1X_P2X, lineRange.policy);
		Assert.assertEquals(10, lineRange.startX, 0);
		Assert.assertEquals(20, lineRange.endX, 0);

		// 双行
		// --ooo
		///ooo--
		p2.y += 10;
		SelectedTextByDragVisitor.updateLineRange(line, 0, p2.y - 10, p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_NORMAL, lineRange.sig);
		Assert.assertEquals(SelectedTextByDragVisitor.LINE_RANGE_POLICY_P1X_TO_END, lineRange.policy);
		Assert.assertEquals(10, lineRange.startX, 0);
		Assert.assertEquals(40, lineRange.endX, 0);
		// 模拟非左对齐
		SelectedTextByDragVisitor.updateLineRange(line, 10, p2.y - 10, p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_NORMAL, lineRange.sig);
		Assert.assertEquals(SelectedTextByDragVisitor.LINE_RANGE_POLICY_P1X_TO_END, lineRange.policy);
		Assert.assertEquals(10, lineRange.startX, 0);
		Assert.assertEquals(50, lineRange.endX, 0);

		SelectedTextByDragVisitor.updateLineRange(line, 0, p2.y, p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_NORMAL, lineRange.sig);
		Assert.assertEquals(SelectedTextByDragVisitor.LINE_RANGE_POLICY_START_TO_P2X, lineRange.policy);
		Assert.assertEquals(0, lineRange.startX, 0);
		Assert.assertEquals(20, lineRange.endX, 0);
		// 模拟非左对齐
		SelectedTextByDragVisitor.updateLineRange(line, 10, p2.y, p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_NORMAL, lineRange.sig);
		Assert.assertEquals(SelectedTextByDragVisitor.LINE_RANGE_POLICY_START_TO_P2X, lineRange.policy);
		Assert.assertEquals(10, lineRange.startX, 0);
		Assert.assertEquals(20, lineRange.endX, 0);

		// --000
		//00000
		//00---
		p2.y += 10;
		SelectedTextByDragVisitor.updateLineRange(line, 0, p2.y - 10, p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_NORMAL, lineRange.sig);
		Assert.assertEquals(SelectedTextByDragVisitor.LINE_RANGE_POLICY_ALL, lineRange.policy);
		Assert.assertEquals(0, lineRange.startX, 0);
		Assert.assertEquals(40, lineRange.endX, 0);
		// 模拟非左对齐
		SelectedTextByDragVisitor.updateLineRange(line, 10, p2.y - 10, p1, p2, lineRange);
		Assert.assertEquals(ParagraphVisitor.SIG_NORMAL, lineRange.sig);
		Assert.assertEquals(SelectedTextByDragVisitor.LINE_RANGE_POLICY_ALL, lineRange.policy);
		Assert.assertEquals(10, lineRange.startX, 0);
		Assert.assertEquals(50, lineRange.endX, 0);
	}
}
