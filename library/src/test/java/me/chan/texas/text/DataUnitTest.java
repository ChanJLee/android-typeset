package me.chan.texas.text;

import me.chan.texas.misc.Rect;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.chan.texas.Texas;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.layout.DrawableBox;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.Penalty;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.typesetter.tex.Candidate;
import me.chan.texas.typesetter.tex.Node;
import me.chan.texas.typesetter.tex.Sum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import me.chan.texas.TestUtils;
import me.chan.texas.test.mock.MockTextPaint;

public class DataUnitTest {
	private TextAttribute mTextAttribute;
	private MockTextPaint mMockTextPaint;
	private MockMeasurer mMockMeasurer;

	@Before
	public void init() {
		mMockTextPaint = new MockTextPaint();
		mMockTextPaint.setMockTextSize(10);
		mMockMeasurer = new MockMeasurer(mMockTextPaint);
		mTextAttribute = new TextAttribute(mMockMeasurer);
		Texas.clean();
	}

	@Test
	public void testGlue() {
		mMockTextPaint.setMockTextSize(1);
		mTextAttribute.refresh(mMockMeasurer);

		Glue glue = Glue.obtain();
		Assert.assertNotNull(glue);

		Assert.assertFalse(glue.isRecycled());
		Assert.assertEquals("check width: ", glue.getWidth(), mTextAttribute.getSpaceWidth(), 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), mTextAttribute.getSpaceStretch(), 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), mTextAttribute.getSpaceShrink(), 0);

		Glue previous = glue;
		glue.recycle();
		Assert.assertTrue(glue.isRecycled());
		Assert.assertEquals("check width: ", glue.getWidth(), 0, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), 0, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), 0, 0);

		// test recycle twice
		glue.recycle();

		mMockTextPaint.setMockTextSize(2);
		mTextAttribute.refresh(mMockMeasurer);
		glue = Glue.obtain();
		Assert.assertNotNull(glue);
		Assert.assertSame(previous, glue);
		Assert.assertFalse(glue.isRecycled());
		Assert.assertEquals("check width: ", glue.getWidth(), mTextAttribute.getSpaceWidth(), 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), mTextAttribute.getSpaceStretch(), 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), mTextAttribute.getSpaceShrink(), 0);
		Assert.assertNotSame(glue, Glue.obtain());
	}

	@Test
	public void testPenalty() {
		mMockTextPaint.setMockTextSize(2);
		mTextAttribute.refresh(mMockMeasurer);

		TextStyle textStyle = new TextStyle() {
			@Override
			public void update(@NonNull TexasPaint textPaint, @Nullable Object tag) {

			}
		};
		String tag = "hello";
		Penalty penalty = Penalty.obtain(2, true, tag, textStyle, mTextAttribute);
		Assert.assertNotNull(penalty);

		Assert.assertFalse(penalty.isRecycled());
		Assert.assertEquals("check width: ", penalty.getWidth(), mTextAttribute.getHyphenWidth(), 0);
		Assert.assertEquals("check height: ", penalty.getHeight(), mTextAttribute.getHyphenHeight(), 0);
		Assert.assertEquals("check penalty: ", penalty.getPenalty(), 2, 0);
		Assert.assertTrue("check flag", penalty.isFlag());
		Assert.assertSame(penalty.getTag(), tag);
		Assert.assertSame(penalty.getTextStyle(), textStyle);

		Penalty prev = penalty;
		penalty.recycle();
//		Assert.assertTrue(penalty.isRecycled());
//		Assert.assertEquals("check width: ", penalty.getWidth(), -1, 0);
//		Assert.assertEquals("check height: ", penalty.getHeight(), -1, 0);
//		Assert.assertEquals("check penalty: ", penalty.getPenalty(), -1, 0);
//		Assert.assertFalse("check flag", penalty.isFlag());
		TestUtils.testRecycled(penalty);

		// test recycle twice
		penalty.recycle();

		mMockTextPaint.setMockTextSize(4);
		mTextAttribute.refresh(mMockMeasurer);
		TextStyle textStyle1 = new TextStyle() {
			@Override
			public void update(@NonNull TexasPaint textPaint, @Nullable Object tag) {

			}
		};
		String tag2 = "fuck2";
		penalty = Penalty.obtain(5, false, tag2, textStyle1, mTextAttribute);
		Assert.assertNotNull(penalty);
		Assert.assertSame(penalty, prev);
		Assert.assertFalse(penalty.isRecycled());
		Assert.assertSame(penalty.getTextStyle(), textStyle1);
		Assert.assertSame(penalty.getTag(), tag2);
		Assert.assertEquals("check width: ", penalty.getWidth(), 0, 0);
		Assert.assertEquals("check height: ", penalty.getHeight(), 0, 0);
		Assert.assertEquals("check penalty: ", penalty.getPenalty(), 5, 0);
		Assert.assertFalse("check flag", penalty.isFlag());
		Assert.assertNotSame(penalty, Penalty.obtain(10, true, "fuck2", new TextStyle() {
			@Override
			public void update(@NonNull TexasPaint textPaint, @Nullable Object tag) {

			}
		}, mTextAttribute));
	}

	@Test
	public void testBackground() {
		RectGround background = new RectGround(10);
		assertEquals(background.getColor(), 10);
	}

	@Test
	public void testForeground() {
		DotUnderLine dotUnderLine = new DotUnderLine(10);
		assertEquals(dotUnderLine.getColor(), 10);
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
		Assert.assertNull(figure.getTag());

		Figure p = figure;
		figure.recycle();
		Assert.assertTrue(figure.isRecycled());
		Assert.assertNotSame(figure.getUrl(), url);
		Assert.assertNotEquals(figure.getWidth(), 1, 0);
		Assert.assertNotEquals(figure.getHeight(), 2, 0);

		figure.recycle();

		figure = Figure.obtain(url, 1, 2, extra);
		Assert.assertSame(figure, p);
		Assert.assertNotNull(figure);
		Assert.assertFalse(figure.isRecycled());
		Assert.assertSame(figure.getUrl(), url);
		Assert.assertEquals(figure.getWidth(), 1, 0);
		Assert.assertEquals(figure.getHeight(), 2, 0);
		Assert.assertNotSame(figure, Figure.obtain(url, 1, 2));
		Assert.assertSame(figure.getTag(), extra);

		figure.recycle();
		TestUtils.testRecycled(figure);
	}

	@Test
	public void testLine() throws NoSuchFieldException, IllegalAccessException {
		Line line = Line.obtain();
		Field field = Line.class.getDeclaredField("mElements");
		field.setAccessible(true);
		List<TextBox> boxes = (List<TextBox>) field.get(line);
		Assert.assertNotNull(line);
		Assert.assertFalse(line.isRecycled());
		Assert.assertNotNull(boxes);
		Assert.assertTrue(boxes.isEmpty());
		line.setLineHeight(2);
		Assert.assertEquals(line.getLineHeight(), 2, 0);
		line.setRatio(4);
		Assert.assertEquals(line.getRatio(), 4, 0);


		mMockTextPaint.setMockTextSize(4);
		mTextAttribute.refresh(mMockMeasurer);
		boxes.add(TextBox.obtain("hello", 0, 1, null, null, null, null));
		Assert.assertFalse(boxes.isEmpty());

		Line prev = line;
		line.recycle();
		Assert.assertTrue(line.isRecycled());
		boxes = (List<TextBox>) field.get(line);
		Assert.assertTrue(boxes.isEmpty());
		Assert.assertNotEquals(line.getLineHeight(), 2, 0);
		Assert.assertNotEquals(line.getRatio(), 4, 0);

		// test recycle twice
		line.recycle();

		line = Line.obtain();
		Assert.assertNotSame(line, Line.obtain());
		boxes = (List<TextBox>) field.get(line);
		Assert.assertNotNull(line);
		Assert.assertFalse(line.isRecycled());
		Assert.assertSame(prev, line);
		Assert.assertTrue(boxes.isEmpty());
		Assert.assertNotEquals(line.getLineHeight(), 2, 0);
		Assert.assertNotEquals(line.getRatio(), 4, 0);
	}

	@Test
	public void testDrawableBox() {
		Drawable drawable = new ColorDrawable(19);

		Emoticon emoticon = Emoticon.obtain(drawable, 1, 2);
		DrawableBox drawableBox = (DrawableBox) emoticon.getDrawableBox();
		Assert.assertNotNull(drawableBox);
		Assert.assertFalse(drawableBox.isRecycled());
		Assert.assertEquals(drawableBox.getWidth(), 1, 0);
		Assert.assertEquals(drawableBox.getHeight(), 2, 0);

		DrawableBox p = drawableBox;
		drawableBox.recycle();
		Assert.assertTrue(drawableBox.isRecycled());
		Assert.assertNotSame(drawable, drawableBox.getSpan());
		Assert.assertNotEquals(drawableBox.getWidth(), 1, 0);
		Assert.assertNotEquals(drawableBox.getHeight(), 2, 0);
		Assert.assertNull(drawableBox.getSpan());

		// test recycle twice
		drawableBox.recycle();

		Emoticon emoticon1 = Emoticon.obtain(drawable, 2, 3);
		drawableBox = (DrawableBox) emoticon1.getDrawableBox();
		Assert.assertNotSame(emoticon, drawableBox.getSpan());
		Assert.assertFalse(drawableBox.isRecycled());
		Assert.assertEquals(drawableBox.getWidth(), 2, 0);
		Assert.assertEquals(drawableBox.getHeight(), 3, 0);
		Assert.assertSame(p, drawableBox);
		Assert.assertSame(emoticon1, drawableBox.getSpan());
		Assert.assertNotSame(drawableBox, DrawableBox.obtain(emoticon1, 1, 2));
	}

	@Test
	public void testSum() {
		Sum sum = Sum.obtain();
		Assert.assertNotNull(sum);

		Glue glue = Glue.obtain();
		Assert.assertNotNull(glue);
		Assert.assertFalse(glue.isRecycled());
		sum.increase(glue);
		Assert.assertEquals(sum.getWidth(), glue.getWidth(), 0);
		Assert.assertEquals(sum.getShrink(), glue.getShrink(), 0);
		Assert.assertEquals(sum.getStretch(), glue.getStretch(), 0);

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

		Assert.assertNull(node.link);
		Assert.assertNull(node.totals);
		Assert.assertEquals(node.state, 0);
		Assert.assertEquals(node.demerits, 0, 0);
		Assert.assertEquals(node.ratio, 0, 0);
		Assert.assertEquals(node.line, -1);
		Assert.assertEquals(node.fitness, 0);

		node.state = 1;
		node.demerits = 2;
		node.ratio = 3;
		node.line = 4;
		node.fitness = 5;
		node.totals = Sum.obtain();
		node.link = Node.obtain();

		node.prev = Node.obtain();
		node.next = Node.obtain();

		node.recycle();
		Assert.assertTrue(node.isRecycled());
		Assert.assertNotNull(node);
		Assert.assertNull(node.next);
		Assert.assertNull(node.prev);
		Assert.assertNull(node.link);
		Assert.assertNull(node.totals);
		Assert.assertEquals(node.state, 0);
		Assert.assertEquals(node.demerits, 0, 0);
		Assert.assertEquals(node.ratio, 0, 0);
		Assert.assertEquals(node.line, -1);
		Assert.assertEquals(node.fitness, 0);

		// test recycle twice
		node.recycle();

		Node previous = node;
		node = Node.obtain();
		Assert.assertSame(previous, node);
		Assert.assertNotNull(node);
		Assert.assertFalse(node.isRecycled());
		Assert.assertNull(node.next);
		Assert.assertNull(node.prev);
		Assert.assertNull(node.link);
		Assert.assertNull(node.totals);
		Assert.assertEquals(node.state, 0);
		Assert.assertEquals(node.demerits, 0, 0);
		Assert.assertEquals(node.ratio, 0, 0);
		Assert.assertEquals(node.line, -1);
		Assert.assertEquals(node.fitness, 0);
		Assert.assertNotSame(node, Node.obtain());
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
		Assert.assertEquals(candidate.demerits, 0, 0);
		Assert.assertEquals(candidate.ratio, 0, 0);
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
		Document.Builder document = new Document.Builder();
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getSegmentCount(), 0);
		try {
			document.getSegment(0);
			fail("test document get segment");
		} catch (IndexOutOfBoundsException e) {
		}

		List<Segment> segments = new ArrayList<>();
		Figure figure = Figure.obtain("", 1, 2);
		segments.add(figure);
		document.addSegments(segments);
		Assert.assertEquals(document.getSegmentCount(), 1);
		Assert.assertEquals(0, document.indexOfSegment(figure));
		Assert.assertEquals(-1, document.indexOfSegment(Figure.obtain("", 1, 2)));

		try {
			document.getSegment(1);
			fail("test document get segment");
		} catch (IndexOutOfBoundsException e) {
		}

		Assert.assertSame(document.getSegment(0), figure);
		ViewSegment viewSegment = new ViewSegment(1) {

			@Override
			protected void onRender(View view) {

			}
		};
		segments.clear();
		segments.add(viewSegment);
		document.addSegments(0, segments);

		Assert.assertEquals(document.indexOfSegment(null), -1);
		Assert.assertEquals(document.indexOfSegment(figure), 1);
		Assert.assertEquals(document.indexOfSegment(viewSegment), 0);
		Assert.assertEquals(document.getSegmentCount(), 2);
	}

	@Test
	public void testViewFragment() {
		ViewSegment viewSegment = new ViewSegment(1) {

			@Override
			protected void onRender(View v) {

			}
		};
		Assert.assertNull(viewSegment.getTag());
		Assert.assertEquals(viewSegment.getLayout(), 1);
		Assert.assertNull(viewSegment.getRect());


		ViewSegment p = viewSegment;
		viewSegment.recycle();

		Assert.assertNull(viewSegment.getTag());
		Assert.assertEquals(viewSegment.getLayout(), 0);
		Assert.assertNull(viewSegment.getRect());

		// test recycle twice
		// view segment do not have cache
		viewSegment.recycle();

		String msg = "hello";
		Rect rect = new Rect();
		viewSegment = new ViewSegment(2, true, msg) {

			@Override
			protected void onRender(View v) {

			}
		};
		viewSegment.setRect(rect);

		Assert.assertNotSame(viewSegment, p);
		Assert.assertSame(viewSegment.getRect(), rect);
		Assert.assertSame(viewSegment.getTag(), msg);
		Assert.assertTrue(viewSegment.isDisableReuse());
		Assert.assertEquals(viewSegment.getLayout(), 2);
	}
}
