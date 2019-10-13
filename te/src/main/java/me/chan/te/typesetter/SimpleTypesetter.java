package me.chan.te.typesetter;

import android.support.annotation.NonNull;
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
import me.chan.te.data.Penalty;
import me.chan.te.data.Segment;
import me.chan.te.log.Log;
import me.chan.te.text.BreakStrategy;

class SimpleTypesetter implements Typesetter {
	private Option mOption;
	private TextPaint mPaint;
	private TextPaint mWorkPaint = new TextPaint();
	private Box.Bound mBound = new Box.Bound();
	private ElementFactory mElementFactory;

	SimpleTypesetter(TextPaint paint, Option option, ElementFactory elementFactory) {
		mOption = option;
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
		List<Line> lines = new ArrayList<>();
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
			if (elements.get(start) instanceof Box) {
				break;
			}
		}

		if (start >= size) {
			return start;
		}

		List<Box> boxes = new ArrayList<>();
		float lineHeight = 0f;
		float spaceWidth = mOption.getSpaceWidth();
		float currentLineWidth = 0f;

		while (start < size) {
			Element element = elements.get(start);
			if (!(element instanceof Box)) {
				++start;
				continue;
			}

			mWorkPaint.set(mPaint);
			Box box = (Box) element;
			int next = mergeIf(elements, box, start + 1, currentLineWidth, width);
			box.getBound(mWorkPaint, mBound);

			// 如果超出当前的长度 那么直接结束
			if (currentLineWidth + mBound.getWidth() > width) {
				break;
			}

			start = next;
			box.getBound(mWorkPaint, mBound);
			boxes.add(box);
			currentLineWidth += (mBound.getWidth() + spaceWidth);
			if (lineHeight < mBound.getHeight()) {
				lineHeight = mBound.getHeight();
			}
		}

		// 如果一行是空的，说明当前只能排一个，并且都显示不下
		if (boxes.isEmpty()) {
			start = spiltIf(elements, start, boxes, mBound, width);
			lineHeight = mBound.getHeight();
		}

		if (boxes.isEmpty()) {
			return start;
		}

		if (breakStrategy == BreakStrategy.BALANCED && boxes.size() > 1 && start != size) {
			spaceWidth = spaceWidth + (width - currentLineWidth) / (boxes.size() - 1);
		}

		lines.add(new Line(boxes, lineHeight, spaceWidth, 0));
		return start;
	}

	private int mergeIf(List<? extends Element> elements, Box box,
						int start, float currentLineWidth, float width) {
		Box clone = null;
		int prev = start;
		for (; start < elements.size(); ++start) {
			if (!(elements.get(start) instanceof Penalty)) {
				break;
			}

			if (start + 1 < elements.size() &&
					!(elements.get(start + 1) instanceof Box)) {
				break;
			}

			Box next = (Box) elements.get(start + 1);
			if (!next.canMerge(box)) {
				return prev;
			}

			if (clone == null) {
				clone = (Box) box.clone();
			}

			clone.append(next);
			clone.getBound(mWorkPaint, mBound);
			// 如果超出当前的长度 那么直接结束
			if (currentLineWidth + mBound.getWidth() > width) {
				return prev;
			}

			++start;
		}

		if (clone != null) {
			box.copy(clone);
		}

		return start;
	}

	private int spiltIf(List<? extends Element> elements, int start, List<Box> boxes, Box.Bound bound, float width) {
		if (start >= elements.size()) {
			return start;
		}

		Box box = (Box) elements.get(start);
		mWorkPaint.set(mPaint);
		Box[] children = box.spilt(mWorkPaint, width);
		if (children != null) {
			children[0].setPenalty(true);
			boxes.add(children[0]);
			box.copy(children[1]);
			box = children[0];
		} else {
			boxes.add(box);
			++start;
		}
		box.getBound(mWorkPaint, bound);
		return start;
	}
}
