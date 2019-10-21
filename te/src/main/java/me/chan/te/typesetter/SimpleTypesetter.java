package me.chan.te.typesetter;

import android.support.annotation.NonNull;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.chan.te.config.LineAttributes;
import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Glue;
import me.chan.te.data.Line;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Penalty;
import me.chan.te.data.Segment;
import me.chan.te.text.BreakStrategy;

class SimpleTypesetter implements Typesetter {
	private TextPaint mPaint;
	private TextPaint mWorkPaint = new TextPaint();
	private ElementFactory mElementFactory;

	SimpleTypesetter(TextPaint paint, ElementFactory elementFactory) {
		mPaint = paint;
		mElementFactory = elementFactory;
	}

	@NonNull
	public Paragraph typeset(Segment segment,
							 LineAttributes lineAttributes, BreakStrategy breakStrategy) {
		Paragraph paragraph = new Paragraph(lineAttributes);
		// 一行尽可能的占满尽可能多的字符
		// 如果如果只显示了一个并且还不足以完美显示，那么无脑折断
		List<? extends Element> elements = segment.getElements();
		List<Line> lines = new LinkedList<>();
		int size = elements.size();
		for (int i = 0; i < size; ) {
			float width = lineAttributes.get(lines.size()).getLineWidth();
			i = typesetLine(width, lines, elements, i, breakStrategy);
		}
		paragraph.setLines(lines);
		return paragraph;
	}

	private int typesetLine(float width, List<Line> lines, List<? extends Element> elements,
							int start, BreakStrategy breakStrategy) {
		int size = elements.size();

		// skip none box
		for (; start < size; ++start) {
			Element element = elements.get(start);
			if (element instanceof Box) {
				break;
			}
			mElementFactory.recycle(element);
		}

		if (start >= size) {
			return start;
		}

		List<Box> boxes = new ArrayList<>(12);
		float lineHeight = 0f;
		float currentLineWidth = 0f;
		float lineWidth = 0f;

		while (start < size) {
			Element element = elements.get(start);
			if (element instanceof Glue) {
				Glue glue = (Glue) element;
				currentLineWidth += (breakStrategy == BreakStrategy.BALANCED ? glue.getShrink() : glue.getWidth());
				++start;
				mElementFactory.recycle(element);
				continue;
			}

			if (!(element instanceof Box)) {
				++start;
				mElementFactory.recycle(element);
				continue;
			}

			mWorkPaint.set(mPaint);
			Box box = (Box) element;
			int next = mergeIf(elements, box, start + 1, currentLineWidth, width);
			if (next == -1) {
				break;
			}

			// 如果超出当前的长度 那么直接结束
			if (currentLineWidth + box.getWidth(mWorkPaint) > width) {
				break;
			}

			start = next;
			boxes.add(box);
			float boxWidth = box.getWidth(mWorkPaint);
			currentLineWidth += boxWidth;
			lineWidth += boxWidth;

			if (lineHeight < box.getHeight(mWorkPaint)) {
				lineHeight = box.getHeight(mWorkPaint);
			}
		}

		// 如果一行是空的，说明当前只能排一个，并且都显示不下
		if (boxes.isEmpty()) {
			return spiltIf(lines, elements, start, boxes, width);
		}

		lines.add(new Line(boxes, lineHeight, lineWidth, 0));
		return start;
	}

	private int mergeIf(List<? extends Element> elements, Box box,
						int start, float currentLineWidth, float width) {
		Box clone = null;
		for (; start < elements.size(); ++start) {
			Element element = elements.get(start);
			if (!(element instanceof Penalty)) {
				break;
			}

			Penalty penalty = (Penalty) element;
			if (start + 1 < elements.size() &&
					!(elements.get(start + 1) instanceof Box)) {
				break;
			}

			Box next = (Box) elements.get(start + 1);
			if (!next.canMerge(box)) {
				return -1;
			}

			if (clone == null) {
				clone = (Box) box.clone();
			}

			float cloneWidth = clone.getWidth(mWorkPaint);
			float nextWidth = next.getWidth(mWorkPaint);

			// 如果超出当前的长度 那么直接结束
			if (currentLineWidth + cloneWidth + nextWidth > width) {
				if (currentLineWidth + cloneWidth + penalty.getPenalty() <= width) {
					++start;
					clone.append("-");
					clone.setFlag(Box.FLAG_PENALTY);
					break;
				}
				return -1;
			}

			clone.append(next);
			++start;
		}

		if (clone != null) {
			box.copy(clone);
		}

		return start;
	}

	private int spiltIf(List<Line> lines, List<? extends Element> elements, int start, List<Box> boxes, float width) {
		if (start >= elements.size()) {
			return start;
		}

		Box box = (Box) elements.get(start);
		mWorkPaint.set(mPaint);
		Box[] children = box.spilt(mWorkPaint, width);
		if (children != null) {
			children[0].setFlag(Box.FLAG_SPILT);
			boxes.add(children[0]);
			box.copy(children[1]);
			box = children[0];
		} else {
			boxes.add(box);
			++start;
		}

		lines.add(new Line(boxes, box.getHeight(mWorkPaint), width, 0));
		return start;
	}
}
