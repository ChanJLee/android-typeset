package me.chan.texas.renderer.selection.visitor;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import me.chan.texas.TexasOption;
import me.chan.texas.di.FakeMeasureFactory;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.misc.PointF;
import me.chan.texas.misc.Rect;
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.Penalty;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.typesetter.ParagraphTypesetter;

public class SelectedTextByDragVisitorUnitTest {

	@Test
	public void testLineRange() {
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

	@Test
	public void testLinkWithTail() throws ParagraphVisitor.VisitException {
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);

		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		PaintSet paintSet = new PaintSet(factory.getMockTextPaint());
		TextAttribute textAttribute = new TextAttribute(measurer);

		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption)
				.text("triangle 1");
		Paragraph paragraph = builder.build();

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		paragraph.measure(measurer, textAttribute);
		texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 6);

		Layout layout = paragraph.getLayout();
		Assert.assertEquals(2, layout.getLineCount());

		SelectedTextByDragVisitor selectedTextByDragVisitor = new SelectedTextByDragVisitor();
		selectedTextByDragVisitor.reset(Selection.Type.SELECTION, Selection.Styles.create(0, 0), paragraph, renderOption);
		float tempX1 = 0;
		float tempY1 = 0f;
		float tempX2 = 2;
		float tempY2 = 1;
		selectedTextByDragVisitor.setRegion(tempX1, tempY1, tempX2, tempY2);
		selectedTextByDragVisitor.startVisit(paragraph);

		ParagraphSelection paragraphSelection = paragraph.getSelection(Selection.Type.SELECTION);
		Assert.assertNotNull(paragraphSelection);

		RectF rectF = paragraphSelection.getFirstRegion();
		Assert.assertNotNull(rectF);
		Assert.assertEquals(0, rectF.left, 0.001);
		Assert.assertEquals(6f, rectF.right, 0.001);
		Assert.assertEquals(0, rectF.top, 0.001);
		Assert.assertEquals(1, rectF.bottom, 0.001);

		rectF = paragraphSelection.getLastRegion();
		Assert.assertNotNull(rectF);
		Assert.assertEquals(0, rectF.left, 0.001);
		Assert.assertEquals(3.5, rectF.right, 0.001);
		Assert.assertEquals(2, rectF.top, 0.001);
		Assert.assertEquals(3, rectF.bottom, 0.001);
	}

	@Test
	public void testLineLink() throws ParagraphVisitor.VisitException {
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);

		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		PaintSet paintSet = new PaintSet(factory.getMockTextPaint());
		TextAttribute textAttribute = new TextAttribute(measurer);

		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption)
				.text("123 triangle 1");
		Paragraph paragraph = builder.build();

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();
		paragraph.measure(measurer, textAttribute);
		texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 4);

		//0-1 123
		//1-2
		//2-3 tri-
		//3-4
		//4-5 an-
		//5-6
		//6-7 gle
		Layout layout = paragraph.getLayout();
		Assert.assertEquals(5, layout.getLineCount());

		SelectedTextByDragVisitor selectedTextByDragVisitor = new SelectedTextByDragVisitor();
		selectedTextByDragVisitor.reset(Selection.Type.SELECTION, Selection.Styles.create(0, 0), paragraph, renderOption);
		float tempX1 = 0;
		float tempY1 = 4.5f;
		float tempX2 = 6;
		float tempY2 = 5;
		selectedTextByDragVisitor.setRegion(tempX1, tempY1, tempX2, tempY2);
		selectedTextByDragVisitor.startVisit(paragraph);

		ParagraphSelection paragraphSelection = paragraph.getSelection(Selection.Type.SELECTION);
		Assert.assertNotNull(paragraphSelection);

		RectF rectF = paragraphSelection.getLastRegion();
		Assert.assertNotNull(rectF);
		Assert.assertEquals(0, rectF.left, 0.001);
		Assert.assertEquals(3f, rectF.right, 0.001);
		Assert.assertEquals(6, rectF.top, 0.001);
		Assert.assertEquals(7, rectF.bottom, 0.001);

		rectF = paragraphSelection.getFirstRegion();
		Assert.assertNotNull(rectF);
		Assert.assertEquals(0, rectF.left, 0.001);
		Assert.assertEquals(4f, rectF.right, 0.001);
		Assert.assertEquals(2, rectF.top, 0.001);
		Assert.assertEquals(3, rectF.bottom, 0.001);

		selectedTextByDragVisitor.clear();
		paragraph.setRect(new Rect(1, 2, 3, 4));
		selectedTextByDragVisitor.reset(Selection.Type.SELECTION, Selection.Styles.create(0, 0), paragraph, renderOption);
		tempX1 = 0;
		tempY1 = 6.5f;
		tempX2 = 6;
		tempY2 = 7;
		selectedTextByDragVisitor.setRegion(tempX1, tempY1, tempX2, tempY2);
		selectedTextByDragVisitor.startVisit(paragraph);

		paragraphSelection = paragraph.getSelection(Selection.Type.SELECTION);
		Assert.assertNotNull(paragraphSelection);

		rectF = paragraphSelection.getLastRegion();
		Assert.assertNotNull(rectF);
		Assert.assertEquals(1, rectF.left, 0.001);
		Assert.assertEquals(4f, rectF.right, 0.001);
		Assert.assertEquals(8, rectF.top, 0.001);
		Assert.assertEquals(9, rectF.bottom, 0.001);

		rectF = paragraphSelection.getFirstRegion();
		Assert.assertNotNull(rectF);
		Assert.assertEquals(1, rectF.left, 0.001);
		Assert.assertEquals(5f, rectF.right, 0.001);
		Assert.assertEquals(4, rectF.top, 0.001);
		Assert.assertEquals(5, rectF.bottom, 0.001);

		builder = Paragraph.Builder.newBuilder(texasOption)
				.text("123 triangle 1");
		paragraph = builder.build();

		texTypesetter = new ParagraphTypesetter();
		paragraph.measure(measurer, textAttribute);
		texTypesetter.typeset(paragraph, BreakStrategy.BALANCED, 8);
		layout = paragraph.getLayout();
		Assert.assertEquals(2, layout.getLineCount());

		selectedTextByDragVisitor.clear();
		selectedTextByDragVisitor.reset(Selection.Type.SELECTION, Selection.Styles.create(0, 0), paragraph, renderOption);
		tempX1 = 0;
		tempY1 = 2f;
		tempX2 = 2;
		tempY2 = 3;
		selectedTextByDragVisitor.setRegion(tempX1, tempY1, tempX2, tempY2);
		selectedTextByDragVisitor.startVisit(paragraph);

		paragraphSelection = paragraph.getSelection(Selection.Type.SELECTION);
		Assert.assertNotNull(paragraphSelection);
		List<RectF> regions = paragraphSelection.getBackgrounds();
		Assert.assertEquals(2, regions.size());
		RectF excepted = new RectF(3.5f, 0, 8, 1);
		Assert.assertEquals(excepted, regions.get(0));
		excepted = new RectF(0, 2, 5.5f, 3);
		Assert.assertEquals(excepted, regions.get(1));

		selectedTextByDragVisitor.clear();
		selectedTextByDragVisitor.reset(Selection.Type.SELECTION, Selection.Styles.create(0, 0), paragraph, renderOption);
		tempX1 = 4;
		tempY1 = 1f;
		tempX2 = 5;
		tempY2 = 1;
		selectedTextByDragVisitor.setRegion(tempX1, tempY1, tempX2, tempY2);
		selectedTextByDragVisitor.startVisit(paragraph);

		paragraphSelection = paragraph.getSelection(Selection.Type.SELECTION);
		Assert.assertNotNull(paragraphSelection);
		regions = paragraphSelection.getBackgrounds();
		Assert.assertEquals(2, regions.size());
		excepted = new RectF(3.5f, 0, 8, 1);
		Assert.assertEquals(excepted, regions.get(0));
		excepted = new RectF(0, 2, 5.5f, 3);
		Assert.assertEquals(excepted, regions.get(1));
	}

	@Test
	public void testLineLinkWithMock() throws ParagraphVisitor.VisitException {
		FakeMeasureFactory factory = FakeMeasureFactory.getInstance();
		factory.getMockTextPaint().setMockTextSize(1);

		RenderOption renderOption = new RenderOption();
		renderOption.setLineSpacingExtra(1);
		Measurer measurer = new MockMeasurer(factory.getMockTextPaint());
		PaintSet paintSet = new PaintSet(factory.getMockTextPaint());
		TextAttribute textAttribute = new TextAttribute(measurer);

		TexasOption texasOption = new TexasOption(paintSet, Hyphenation.getInstance(), measurer, textAttribute, renderOption);
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption)
				.text("123 triangle 1");
		Paragraph paragraph = builder.build();
		paragraph.measure(measurer, textAttribute);

		TextBox box123 = (TextBox) paragraph.getElement(0);
		TextBox triBox = (TextBox) paragraph.getElement(2);
		TextBox angBox = (TextBox) paragraph.getElement(4);

		Glue glue = (Glue) paragraph.getElement(1);

		Assert.assertEquals("123", box123.toString());
		Assert.assertEquals("tri", triBox.toString());
		Assert.assertEquals("an", angBox.toString());

		angBox.merge(Penalty.obtain(1, null, null, textAttribute));
		Assert.assertEquals("an-", angBox.toString());

		Layout layout = Layout.obtain(paragraph.getLayout());

		Line line = Line.obtain();
		line.add(box123);
		line.add(glue);
		line.add(triBox);
		line.add(angBox);
		line.setLineWidth(glue.getWidth() + box123.getWidth() + triBox.getWidth() + angBox.getWidth());
		line.setRatio(0);
		line.setLineHeight(1);
		layout.addLine(line);

		line = Line.obtain();
		line.add(triBox);
		line.add(angBox);
		line.setLineWidth(triBox.getWidth() + angBox.getWidth());
		line.setRatio(0);
		line.setLineHeight(1);
		layout.addLine(line);

		line = Line.obtain();
		line.add(triBox);
		line.add(angBox);
		line.setLineWidth(triBox.getWidth() + angBox.getWidth());
		line.setRatio(0);
		line.setLineHeight(1);
		layout.addLine(line);

		line = Line.obtain();
		line.add(triBox);
		line.add(angBox);
		line.setLineWidth(triBox.getWidth() + angBox.getWidth());
		line.setRatio(0);
		line.setLineHeight(1);
		layout.addLine(line);

		line = Line.obtain();
		line.add(triBox);
		line.add(angBox);
		line.setLineWidth(triBox.getWidth() + angBox.getWidth());
		line.setRatio(0);
		line.setLineHeight(1);
		layout.addLine(line);

		line = Line.obtain();
		line.add(triBox);
		line.add(angBox);
		line.add(glue);
		line.add(box123);
		line.setLineWidth(glue.getWidth() + box123.getWidth() + triBox.getWidth() + angBox.getWidth());
		line.setRatio(0);
		line.setLineHeight(1);
		layout.addLine(line);

		layout.setWidth(10);
		paragraph.swap(layout);

		//0-1 123 trian-
		//1-2
		//2-3 trian-
		//3-4
		//4-5 trian-
		//5-6
		//6-7 trian-
		//7-8
		//8-9 trian-
		//9-10
		//10-11 trian- 123
		SelectedTextByDragVisitor selectedTextByDragVisitor = new SelectedTextByDragVisitor();
		selectedTextByDragVisitor.reset(Selection.Type.SELECTION, Selection.Styles.create(0, 0), paragraph, renderOption);
		float tempX1 = 4;
		float tempY1 = 4f;
		float tempX2 = 6;
		float tempY2 = 4.5f;
		selectedTextByDragVisitor.setRegion(tempX1, tempY1, tempX2, tempY2);
		selectedTextByDragVisitor.startVisit(paragraph);

		ParagraphSelection paragraphSelection = paragraph.getSelection(Selection.Type.SELECTION);
		Assert.assertNotNull(paragraphSelection);

		RectF rectF = paragraphSelection.getLastRegion();
		Assert.assertNotNull(rectF);
		Assert.assertEquals(0, rectF.left, 0.001);
		Assert.assertEquals(6.5f, rectF.right, 0.001);
		Assert.assertEquals(10, rectF.top, 0.001);
		Assert.assertEquals(11, rectF.bottom, 0.001);

		rectF = paragraphSelection.getFirstRegion();
		Assert.assertNotNull(rectF);
		Assert.assertEquals(3.5, rectF.left, 0.001);
		Assert.assertEquals(10, rectF.right, 0.001);
		Assert.assertEquals(0, rectF.top, 0.001);
		Assert.assertEquals(1, rectF.bottom, 0.001);
	}
}
