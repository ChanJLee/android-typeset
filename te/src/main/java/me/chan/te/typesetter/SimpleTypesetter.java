package me.chan.te.typesetter;

import android.support.annotation.NonNull;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.text.BreakStrategy;
import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.Line;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Penalty;
import me.chan.te.data.Segment;

class SimpleTypesetter implements Typesetter {
	private Option mOption;
	private TextPaint mPaint;
	private TextPaint mWorkPaint = new TextPaint();
	private Box.Bound mBound = new Box.Bound();

	SimpleTypesetter(TextPaint paint, Option option) {
		mOption = option;
		mPaint = paint;
	}

	@NonNull
	public Paragraph typeset(Segment segment,
							 LineAttributes lineAttributes, BreakStrategy breakStrategy) {
		Paragraph paragraph = new Paragraph(lineAttributes);
		// 一行尽可能的占满尽可能多的字符
		// 如果如果只显示了一个并且还不足以完美显示，那么无脑折断
		List<? extends Element> elements = segment.getElements();
		int lineNumber = 0;
		List<Line> lines = new ArrayList<>();
		int size = elements.size();
		for (int i = 0; i < size; ) {
			float width = lineAttributes.get(lineNumber).getLineWidth();
			i = typesetLine(width, lines, elements, i, breakStrategy);
			++lineNumber;
		}
		paragraph.setLines(lines);
		return paragraph;
	}

	private int typesetLine(float width, List<Line> lines, List<? extends Element> elements,
							int start, BreakStrategy breakStrategy) {
		List<Box> boxes = new ArrayList<>();
		int size = elements.size();
		float lineHeight = 0f;
		float spaceWidth = mOption.getSpaceWidth();
		float currentLineWidth = 0f;

		main:
		for (; start < size; ++start) {
			Element element = elements.get(start);
			if (!(element instanceof Box)) {
				continue;
			}

			mWorkPaint.set(mPaint);
			Box box = (Box) element;
			box.getBound(mWorkPaint, mBound);

			while (currentLineWidth + mBound.getWidth() > width && (start + 1) < size) {
				if (!(elements.get(start + 1) instanceof Penalty)) {
					break;
				}


				Penalty penalty = (Penalty) elements.get(start + 1);
				if (penalty.getPenalty() == -mOption.INFINITY) {
					break main;
				}

				if (penalty.getPenalty() == mOption.INFINITY) {
					break;
				}

				start += 2;
				if (start >= size) {
					break;
				}

				if (!(elements.get(start) instanceof Box)) {
					break;
				}

				Box next = (Box) elements.get(start);
				if (!box.canMerge(next)) {
					break;
				}

				box.append(next);
			}

			box.getBound(mWorkPaint, mBound);

			boxes.add(box);
			currentLineWidth += (mBound.getWidth() + spaceWidth);

			if (lineHeight < mBound.getHeight()) {
				lineHeight = mBound.getHeight();
			}
		}

		// 如果一行是空的，说明当前只能排一个，并且都显示不下
		if (boxes.isEmpty()) {
			mWorkPaint.set(mPaint);
			handleSingleBoxLine(boxes, elements, start, width, mBound, mWorkPaint);
			lineHeight = mBound.getHeight();
		} else {
			start = handleFullLoadLine(elements, start - 1, width, currentLineWidth, boxes);
		}

		if (breakStrategy == BreakStrategy.BALANCED && boxes.size() > 1 && start != size) {
			spaceWidth = spaceWidth + (width - currentLineWidth) / (boxes.size() - 1);
		}

		lines.add(new Line(boxes, lineHeight, spaceWidth, 0));
		return start;
	}

	private void handleSingleBoxLine(List<Box> boxes, List<? extends Element> elements,
									 int start, float width, Box.Bound bound, TextPaint textPaint) {
		mWorkPaint.set(mPaint);
		Box box = (Box) elements.get(start);
		Box[] children = box.spilt(mWorkPaint, width);
		if (children != null) {
			children[0].setPenalty(true);
			boxes.add(children[0]);
			box.copy(children[1]);
			box = children[0];
		} else {
			boxes.add(box);
		}
		box.getBound(textPaint, bound);
	}

	private int handleFullLoadLine(List<? extends Element> elements, int start, float width, float currentWidth, List<Box> boxes) {
		int last = start + 1;
		Element next;
		if (last >= elements.size() ||
				!((next = elements.get(last)) instanceof Penalty)) {
			return start;
		}

		if (((Penalty) next).getPenalty() != mOption.INFINITY &&
				currentWidth + mOption.getHyphenWidth() <= width) {

			Box box = boxes.get(boxes.size() - 1);
			box.setPenalty(true);
			box.append("-");
			++start;
		} else {
			boxes.remove(boxes.size() - 1);
		}

		return start;
	}
}
