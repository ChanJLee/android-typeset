package me.chan.texas.test;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import me.chan.texas.test.mock.MockContext;
import me.chan.texas.text.Background;
import me.chan.texas.text.Document;
import me.chan.texas.text.DrawableBox;
import me.chan.texas.text.Figure;
import me.chan.texas.text.Glue;
import me.chan.texas.text.Gravity;
import me.chan.texas.text.OnClickedListener;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Penalty;
import me.chan.texas.text.TextBox;
import me.chan.texas.text.UnderLine;
import me.chan.texas.text.ViewSegment;
import me.chan.texas.typesetter.BreakPoint;
import me.chan.texas.typesetter.Candidate;
import me.chan.texas.typesetter.Node;
import me.chan.texas.typesetter.Sum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class DataUnitTest {

	@Test
	public void testGlue() {
		Glue glue = Glue.obtain(1, 2, 3);
		Assert.assertNotNull(glue);

		Assert.assertFalse(glue.isRecycled());
		Assert.assertEquals("check width: ", glue.getWidth(), 1, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), 2, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), 3, 0);

		Glue previous = glue;
		glue.recycle();
		Assert.assertTrue(glue.isRecycled());
		Assert.assertEquals("check width: ", glue.getWidth(), -1, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), -1, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), -1, 0);

		// test recycle twice
		glue.recycle();

		glue = Glue.obtain(4, 5, 6);
		Assert.assertNotNull(glue);
		Assert.assertSame(previous, glue);
		Assert.assertFalse(glue.isRecycled());
		Assert.assertEquals("check width: ", glue.getWidth(), 4, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), 5, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), 6, 0);
		Assert.assertNotSame(glue, Glue.obtain(4, 5, 6));
	}

	@Test
	public void testPenalty() {
		Penalty penalty = Penalty.obtain(1, 2, 3, true);
		Assert.assertNotNull(penalty);

		Assert.assertFalse(penalty.isRecycled());
		Assert.assertEquals("check width: ", penalty.getWidth(), 1, 0);
		Assert.assertEquals("check height: ", penalty.getHeight(), 2, 0);
		Assert.assertEquals("check penalty: ", penalty.getPenalty(), 3, 0);
		Assert.assertTrue("check flag", penalty.isFlag());

		Penalty prev = penalty;
		penalty.recycle();
		Assert.assertTrue(penalty.isRecycled());
		Assert.assertEquals("check width: ", penalty.getWidth(), -1, 0);
		Assert.assertEquals("check height: ", penalty.getHeight(), -1, 0);
		Assert.assertEquals("check penalty: ", penalty.getPenalty(), -1, 0);
		Assert.assertFalse("check flag", penalty.isFlag());

		// test recycle twice
		penalty.recycle();

		penalty = Penalty.obtain(4, 5, 6, false);
		Assert.assertNotNull(penalty);
		Assert.assertSame(penalty, prev);
		Assert.assertFalse(penalty.isRecycled());
		Assert.assertEquals("check width: ", penalty.getWidth(), 4, 0);
		Assert.assertEquals("check height: ", penalty.getHeight(), 5, 0);
		Assert.assertEquals("check penalty: ", penalty.getPenalty(), 6, 0);
		Assert.assertFalse("check flag", penalty.isFlag());
		Assert.assertNotSame(penalty, Penalty.obtain(4, 5, 6, false));
	}

	@Test
	public void testBackground() {
		Background background = Background.obtain(10);
		assertNotNull(background);
		Assert.assertFalse(background.isRecycled());
		assertEquals(background.getColor(), 10);

		background.recycle();
		Assert.assertTrue(background.isRecycled());
		assertNotEquals(background.getColor(), 10);

		// test recycle twice
		background.recycle();

		Background p = background;
		background = Background.obtain(20);
		Assert.assertFalse(background.isRecycled());
		Assert.assertNotNull(background);
		Assert.assertSame(p, background);
		assertEquals(background.getColor(), 20);
		Assert.assertNotSame(background, Background.obtain(20));
	}

	@Test
	public void testForeground() {
		UnderLine underLine = UnderLine.obtain(10);
		assertNotNull(underLine);
		assertEquals(underLine.getColor(), 10);
		Assert.assertFalse(underLine.isRecycled());

		underLine.recycle();
		Assert.assertTrue(underLine.isRecycled());
		assertNotEquals(underLine.getColor(), 10);

		// test recycle twice
		underLine.recycle();

		UnderLine p = underLine;
		underLine = UnderLine.obtain(20);
		Assert.assertFalse(underLine.isRecycled());
		Assert.assertNotNull(underLine);
		Assert.assertSame(p, underLine);
		assertEquals(underLine.getColor(), 20);
		Assert.assertNotSame(underLine, UnderLine.obtain(20));
	}

	@Test
	public void testFigure() {
		String extra = "ok";
		String url = "hello";
		Figure figure = Figure.obtain(url, 1, 2);
		Assert.assertNotNull(figure);
		Assert.assertFalse(figure.isRecycled());
		Assert.assertSame(figure.getUrl(), url);
		Assert.assertEquals(figure.getWidth(), 1, 0);
		Assert.assertEquals(figure.getHeight(), 2, 0);

		Figure p = figure;
		figure.recycle();
		Assert.assertTrue(figure.isRecycled());
		Assert.assertNotSame(figure.getUrl(), url);
		Assert.assertNotEquals(figure.getWidth(), 1, 0);
		Assert.assertNotEquals(figure.getHeight(), 2, 0);

		figure.recycle();

		figure = Figure.obtain(url, 1, 2);
		Assert.assertSame(figure, p);
		Assert.assertNotNull(figure);
		Assert.assertFalse(figure.isRecycled());
		Assert.assertSame(figure.getUrl(), url);
		Assert.assertEquals(figure.getWidth(), 1, 0);
		Assert.assertEquals(figure.getHeight(), 2, 0);
		Assert.assertNotSame(figure, Figure.obtain(url, 1, 2));
	}

	@Test
	public void testLine() throws NoSuchFieldException, IllegalAccessException {
		Paragraph.Line line = Paragraph.Line.obtain();
		Field field = Paragraph.Line.class.getDeclaredField("mBoxes");
		field.setAccessible(true);
		List<TextBox> boxes = (List<TextBox>) field.get(line);
		Assert.assertNotNull(line);
		Assert.assertFalse(line.isRecycled());
		Assert.assertNotNull(boxes);
		Assert.assertTrue(boxes.isEmpty());
		line.setSpaceWidth(1);
		Assert.assertEquals(line.getSpaceWidth(), 1, 0);
		line.setLineHeight(2);
		Assert.assertEquals(line.getLineHeight(), 2, 0);
		line.setLineWidth(3);
		Assert.assertEquals(line.getLineWidth(), 3, 0);
		line.setRatio(4);
		Assert.assertEquals(line.getRatio(), 4, 0);
		Assert.assertSame(line.getGravity(), Gravity.LEFT);
		line.setGravity(Gravity.CENTER);
		Assert.assertSame(line.getGravity(), Gravity.CENTER);
		boxes.add(TextBox.obtain("hello", 0, 1, 1, 1, null, null));
		Assert.assertFalse(boxes.isEmpty());

		Paragraph.Line prev = line;
		line.recycle();
		Assert.assertTrue(line.isRecycled());
		boxes = (List<TextBox>) field.get(line);
		Assert.assertTrue(boxes.isEmpty());
		Assert.assertSame(line.getGravity(), Gravity.LEFT);
		Assert.assertNotEquals(line.getSpaceWidth(), 1, 0);
		Assert.assertNotEquals(line.getLineHeight(), 2, 0);
		Assert.assertNotEquals(line.getLineWidth(), 3, 0);
		Assert.assertNotEquals(line.getRatio(), 4, 0);

		// test recycle twice
		line.recycle();

		line = Paragraph.Line.obtain();
		Assert.assertNotSame(line, Paragraph.Line.obtain());
		boxes = (List<TextBox>) field.get(line);
		Assert.assertNotNull(line);
		Assert.assertFalse(line.isRecycled());
		Assert.assertSame(prev, line);
		Assert.assertTrue(boxes.isEmpty());
		Assert.assertSame(line.getGravity(), Gravity.LEFT);
		Assert.assertNotEquals(line.getSpaceWidth(), 1, 0);
		Assert.assertNotEquals(line.getLineHeight(), 2, 0);
		Assert.assertNotEquals(line.getLineWidth(), 3, 0);
		Assert.assertNotEquals(line.getRatio(), 4, 0);
	}

	@Test
	public void testDrawableBox() {
		Drawable drawable = new ColorDrawable(19);
		OnClickedListener onClickedListener = new OnClickedListener() {
			@Override
			public void onClicked(float x, float y) {
			}
		};
		DrawableBox drawableBox = DrawableBox.obtain(drawable, 1, 2, onClickedListener);
		Assert.assertNotNull(drawableBox);
		Assert.assertFalse(drawableBox.isRecycled());
		Assert.assertSame(drawable, drawableBox.getDrawable());
		Assert.assertEquals(drawableBox.getWidth(), 1, 0);
		Assert.assertEquals(drawableBox.getHeight(), 2, 0);
		Assert.assertSame(drawableBox.getOnClickedListener(), onClickedListener);

		DrawableBox p = drawableBox;
		drawableBox.recycle();
		Assert.assertNull(drawableBox.getOnClickedListener());
		Assert.assertTrue(drawableBox.isRecycled());
		Assert.assertNotSame(drawable, drawableBox.getDrawable());
		Assert.assertNotEquals(drawableBox.getWidth(), 1, 0);
		Assert.assertNotEquals(drawableBox.getHeight(), 2, 0);

		// test recycle twice
		drawableBox.recycle();

		drawableBox = DrawableBox.obtain(new ColorDrawable(19), 1, 2, null);
		Assert.assertNull(drawableBox.getOnClickedListener());
		Assert.assertNotSame(drawable, drawableBox.getDrawable());
		Assert.assertFalse(drawableBox.isRecycled());
		Assert.assertEquals(drawableBox.getWidth(), 1, 0);
		Assert.assertEquals(drawableBox.getHeight(), 2, 0);
		Assert.assertSame(p, drawableBox);
		Assert.assertNull(drawableBox.getOnClickedListener());
		Assert.assertNotSame(drawableBox, DrawableBox.obtain(new ColorDrawable(19), 1, 2, onClickedListener));
	}

	@Test
	public void testSum() {
		Sum sum = Sum.obtain();
		Assert.assertNotNull(sum);

		Glue glue = Glue.obtain(1, 2, 3);
		Assert.assertNotNull(glue);
		Assert.assertFalse(glue.isRecycled());
		sum.increaseGlue(glue);
		Assert.assertEquals(sum.getWidth(), glue.getWidth(), 0);
		Assert.assertEquals(sum.getShrink(), glue.getShrink(), 0);
		Assert.assertEquals(sum.getStretch(), glue.getStretch(), 0);

		sum.increaseWidth(10);
		Assert.assertEquals(sum.getWidth(), glue.getWidth() + 10, 0);

		Sum o = Sum.obtain(sum);
		Assert.assertNotSame(o, sum);

		Assert.assertEquals(sum.getWidth(), o.getWidth(), 0);
		Assert.assertEquals(sum.getShrink(), o.getShrink(), 0);
		Assert.assertEquals(sum.getStretch(), o.getStretch(), 0);

		o.recycle();
		Assert.assertTrue(o.isRecycled());
		Assert.assertNotEquals(sum.getWidth(), o.getWidth(), 0);
		Assert.assertNotEquals(sum.getShrink(), o.getShrink(), 0);
		Assert.assertNotEquals(sum.getStretch(), o.getStretch(), 0);

		// test recycle twice
		o.recycle();

		Sum p = o;
		o = Sum.obtain();
		Assert.assertSame(o, p);
		Assert.assertNotNull(o);
		Assert.assertFalse(o.isRecycled());
		Assert.assertNotEquals(sum.getWidth(), o.getWidth(), 0);
		Assert.assertNotEquals(sum.getShrink(), o.getShrink(), 0);
		Assert.assertNotEquals(sum.getStretch(), o.getStretch(), 0);
		Assert.assertNotSame(o, Sum.obtain());
	}

	@Test
	public void testNode() {
		Node node = Node.obtain();
		Assert.assertNotNull(node);
		Assert.assertFalse(node.isRecycled());
		Assert.assertNull(node.next);
		Assert.assertNull(node.prev);
		Assert.assertNotNull(node.getData());

		Node.Data data = node.getData();
		Assert.assertNull(data.prev);
		Assert.assertNull(data.totals);
		Assert.assertEquals(data.position, 0);
		Assert.assertEquals(data.demerits, 0, 0);
		Assert.assertEquals(data.ratio, 0, 0);
		Assert.assertEquals(data.line, -1);
		Assert.assertEquals(data.fitnessClazz, 0);

		data.position = 1;
		data.demerits = 2;
		data.ratio = 3;
		data.line = 4;
		data.fitnessClazz = 5;
		data.totals = Sum.obtain();
		data.prev = Node.obtain();

		node.prev = Node.obtain();
		node.next = Node.obtain();

		node.recycle();
		Assert.assertTrue(node.isRecycled());
		Assert.assertNotNull(node);
		Assert.assertNull(node.next);
		Assert.assertNull(node.prev);
		Assert.assertNotNull(node.getData());
		Assert.assertNull(data.prev);
		Assert.assertNull(data.totals);
		Assert.assertEquals(data.position, 0);
		Assert.assertEquals(data.demerits, 0, 0);
		Assert.assertEquals(data.ratio, 0, 0);
		Assert.assertEquals(data.line, -1);
		Assert.assertEquals(data.fitnessClazz, 0);

		// test recycle twice
		node.recycle();

		Node previous = node;
		node = Node.obtain();
		Assert.assertSame(previous, node);
		Assert.assertNotNull(node);
		Assert.assertFalse(node.isRecycled());
		Assert.assertNull(node.next);
		Assert.assertNull(node.prev);
		Assert.assertNotNull(node.getData());
		Assert.assertNull(data.prev);
		Assert.assertNull(data.totals);
		Assert.assertEquals(data.position, 0);
		Assert.assertEquals(data.demerits, 0, 0);
		Assert.assertEquals(data.ratio, 0, 0);
		Assert.assertEquals(data.line, -1);
		Assert.assertEquals(data.fitnessClazz, 0);
		Assert.assertNotSame(node, Node.obtain());
	}

	@Test
	public void testBreakPoint() {
		BreakPoint breakPoint = BreakPoint.obtain(1, 2);
		Assert.assertNotNull(breakPoint);
		Assert.assertFalse(breakPoint.isRecycled());
		Assert.assertEquals(breakPoint.position, 1);
		Assert.assertEquals(breakPoint.ratio, 2, 0);

		breakPoint.recycle();
		Assert.assertTrue(breakPoint.isRecycled());
		Assert.assertEquals(breakPoint.position, -1);
		Assert.assertEquals(breakPoint.ratio, -1, 0);

		// test recycle twice
		breakPoint.recycle();

		BreakPoint t = breakPoint;
		breakPoint = BreakPoint.obtain(3, 4);
		Assert.assertSame(t, breakPoint);
		Assert.assertNotNull(breakPoint);
		Assert.assertFalse(breakPoint.isRecycled());
		Assert.assertEquals(breakPoint.position, 3);
		Assert.assertEquals(breakPoint.ratio, 4, 0);
		Assert.assertNotSame(breakPoint, BreakPoint.obtain(3, 4));
	}

	@Test
	public void testCandidate() {
		Node node = Node.obtain();
		Assert.assertNotNull(node);
		Candidate candidate = Candidate.obtain(1, 2, node);
		Assert.assertNotNull(candidate);
		Assert.assertFalse(candidate.isRecycled());
		Assert.assertEquals(candidate.demerits, 1, 0);
		Assert.assertEquals(candidate.ratio, 2, 0);
		Assert.assertSame(node, candidate.active);

		candidate.recycle();
		Assert.assertTrue(candidate.isRecycled());
		Assert.assertEquals(candidate.demerits, Float.MAX_VALUE, 0);
		Assert.assertEquals(candidate.ratio, -1, 0);
		Assert.assertNull(candidate.active);

		// test recycle twice
		candidate.recycle();

		Candidate p = candidate;
		candidate = Candidate.obtain(3, 4, node);
		Assert.assertNotNull(candidate);
		Assert.assertFalse(candidate.isRecycled());
		Assert.assertEquals(candidate.demerits, 3, 0);
		Assert.assertEquals(candidate.ratio, 4, 0);
		Assert.assertSame(node, candidate.active);
		Assert.assertSame(p, candidate);
		Assert.assertNotSame(candidate, Candidate.obtain(1, 2, node));
	}

	@Test
	public void testDocument() {
		String msg = "hello";
		OnClickedListener onClickedListener = new OnClickedListener() {
			@Override
			public void onClicked(float x, float y) {
			}
		};
		Document document = Document.obtain(onClickedListener);
		Assert.assertNotNull(document);
		Assert.assertFalse(document.isRecycled());
		Assert.assertEquals(document.getSegmentCount(), 0);
		Assert.assertSame(onClickedListener, document.getOnClickedListener());
		document.setRaw(msg);
		Assert.assertSame(document.getRaw(), msg);
		try {
			document.getSegment(0);
			fail("test document get segment");
		} catch (IndexOutOfBoundsException e) {
		}

		Figure figure = Figure.obtain("", 1, 2);
		document.addSegment(figure);
		Assert.assertEquals(document.getSegmentCount(), 1);
		try {
			document.getSegment(1);
			fail("test document get segment");
		} catch (IndexOutOfBoundsException e) {
		}

		Assert.assertSame(document.getSegment(0), figure);
		ViewSegment viewSegment = new ViewSegment() {
			@Override
			protected View onCreateView(LayoutInflater layoutInflater, ViewGroup parent) {
				return null;
			}

			@Override
			protected void onRender() {

			}
		};
		document.setFocusSegment(viewSegment);
		Assert.assertSame(document.getFocusIndex(), -1);
		document.setFocusSegment(figure);
		Assert.assertSame(document.getFocusIndex(), 0);

		Document previous = document;
		document.recycle();
		Assert.assertNull(document.getOnClickedListener());
		Assert.assertNull(document.getRaw());
		Assert.assertTrue(document.isRecycled());

		// test recycle twice
		document.recycle();

		document = Document.obtain();
		Assert.assertNotNull(document);
		Assert.assertFalse(document.isRecycled());
		Assert.assertNull(document.getRaw());
		Assert.assertSame(previous, document);
		Assert.assertEquals(document.getSegmentCount(), 0);
		try {
			document.getSegment(0);
			fail("test document get segment");
		} catch (IndexOutOfBoundsException e) {
		}
		Assert.assertNotSame(document, Document.obtain());
	}

	@Test
	public void testViewFragment() {
		final View view = new View(new MockContext());
		ViewSegment viewSegment = new ViewSegment() {
			@Override
			protected View onCreateView(LayoutInflater layoutInflater, ViewGroup parent) {
				return view;
			}

			@Override
			protected void onRender() {

			}
		};
	}
}
