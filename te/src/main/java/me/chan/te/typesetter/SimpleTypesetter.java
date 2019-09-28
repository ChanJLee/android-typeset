package me.chan.te.typesetter;

import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Line;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;

class SimpleTypesetter {
	private Option mOption;
	private TextPaint mPaint;
	private TextPaint mWorkPaint;
	private Box.Bound mBound;
	private ElementFactory mElementFactory;

	public SimpleTypesetter(Option option, TextPaint paint, TextPaint workPaint, Box.Bound bound, ElementFactory elementFactory) {
		mOption = option;
		mPaint = paint;
		mWorkPaint = workPaint;
		mBound = bound;
		mElementFactory = elementFactory;
	}

	public void typeset(Paragraph paragraph, Segment segment, LineAttributes lineAttributes) {
		// 一行尽可能的占满尽可能多的字符
		// 如果如果只显示了一个并且还不足以完美显示，那么无脑折断
		List<? extends Element> elements = segment.getElements();
		int lineNumber = 0;
		List<Line> lines = new ArrayList<>();
//		int size = elements.size();
//		for (int i = 0; i < size; ) {
//			float width = lineAttributes.get(lineNumber).getLineWidth();
//			i = typesetLine(width, lines, elements, i);
//			++lineNumber;
//		}
		paragraph.setLines(lines);
	}

	private int typesetLine(float width, List<Line> lines, List<? extends Element> elements, int start) {
		List<Box> boxes = new ArrayList<>();
		int size = elements.size();
		float lineHeight = 0f;
		float spaceWidth = mOption.spaceWidth;
		float currentLineWidth = 0f;
		for (; start < size; ++start) {
			Element element = elements.get(start);
			if (!(element instanceof Box)) {
				continue;
			}

			mWorkPaint.set(mPaint);
			Box box = (Box) element;
			box.getBound(mWorkPaint, mBound);

			// 排版结束
			if (currentLineWidth + mBound.getWidth() > width) {
				break;
			}

			currentLineWidth += (mBound.getWidth() + spaceWidth);
			boxes.add(box);
		}

		// 如果一行是空的，说明当前只能排一个，并且都显示不下
		if (boxes.isEmpty()) {
			handleSingleBoxLine(boxes, elements, start, width);
		} else {
			start = handleFullLoadLine(boxes, elements, start, width);
		}

		lines.add(new Line(boxes, lineHeight, spaceWidth, 0));
		return start;
	}

	private void handleSingleBoxLine(List<Box> boxes, List<? extends Element> elements, int start, float width) {
		mWorkPaint.set(mPaint);
		Box box = (Box) elements.get(start);
		Box[] children = box.spilt(mWorkPaint, width);
		if (children != null) {
			children[0].setPenalty(true);
			boxes.add(children[0]);
			box.copy(children[1]);
		}
	}

	private int handleFullLoadLine(List<Box> boxes, List<? extends Element> elements, int start, float width) {
		return start + 1;
	}
}
