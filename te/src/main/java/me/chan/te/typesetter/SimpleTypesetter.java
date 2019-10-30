package me.chan.te.typesetter;

import android.support.annotation.NonNull;

import java.util.List;

import me.chan.te.config.LineAttributes;
import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.data.Line;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Penalty;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Gravity;

class SimpleTypesetter implements Typesetter {

	@NonNull
	public boolean typeset(Paragraph paragraph,
						   LineAttributes lineAttributes, BreakStrategy breakStrategy) {
		// 一行尽可能的占满尽可能多的字符
		// 如果如果只显示了一个并且还不足以完美显示，那么无脑折断
		List<? extends Element> elements = paragraph.getElements();
		int size = elements.size();
		for (int i = 0; i < size; ) {
			LineAttributes.Attribute attribute = lineAttributes.get(paragraph.getLineCount());
			i = typesetLine(attribute,
					paragraph,
					i,
					breakStrategy
			);
		}

		int lineCount = paragraph.getLineCount();
		if (breakStrategy == BreakStrategy.SIMPLE || lineCount == 0) {
			return true;
		}

		return true;
	}

	private int typesetLine(LineAttributes.Attribute attribute,
							Paragraph paragraph, int start, BreakStrategy breakStrategy) {
		List<? extends Element> elements = paragraph.getElements();
		int size = elements.size();
		float lineWidth = attribute.getLineWidth();
		float wordSpaceWidth = attribute.getWordSpaceWidth();
		Gravity gravity = attribute.getGravity();

		// skip none box
		for (; start < size; ++start) {
			Element element = elements.get(start);
			if (element instanceof Box) {
				break;
			}
		}

		if (start >= size) {
			return start;
		}

		Line line = Line.obtain();
		float lineHeight = 0f;
		float currentLineWidth = 0f;
		float boxTotalWidth = 0f;

		while (start < size) {
			Element element = elements.get(start);
			if (element instanceof Glue) {
				Glue glue = (Glue) element;
				currentLineWidth += (breakStrategy == BreakStrategy.BALANCED ? glue.getShrink() : wordSpaceWidth);
				++start;
				continue;
			}

			if (!(element instanceof Box)) {
				++start;
				continue;
			}

			Box box = (Box) element;
			int next = mergeIf(elements, box, start + 1, currentLineWidth, lineWidth);
			if (next == -1) {
				break;
			}

			// 如果超出当前的长度 那么直接结束
			if (currentLineWidth + box.getWidth() > lineWidth) {
				break;
			}

			start = next;

			line.add(box);
			float boxWidth = box.getWidth();
			currentLineWidth += boxWidth;
			boxTotalWidth += boxWidth;

			if (lineHeight < box.getHeight()) {
				lineHeight = box.getHeight();
			}
		}

		// 如果一行是空的，说明当前只能排一个，并且都显示不下
		if (line.isEmpty()) {
			return spiltIf(paragraph, elements, start, line, lineWidth, gravity);
		}

		if (breakStrategy == BreakStrategy.SIMPLE) {
			line.setSpaceWidth(wordSpaceWidth);
		} else {
			int boxSize = line.getBoxes().size();
			if (boxSize > 1) {
				line.setSpaceWidth((lineWidth - boxTotalWidth) / (boxSize - 1));
			}
		}

		if (start == size && (line.getCount() - 1) * wordSpaceWidth < lineWidth) {
			line.setSpaceWidth(wordSpaceWidth);
		}

		line.setLineWidth(currentLineWidth);
		line.setLineHeight(lineHeight);
		line.setGravity(gravity);
		line.setRatio(0);
		paragraph.addLine(line);
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

			float cloneWidth = clone.getWidth();
			float nextWidth = next.getWidth();

			// 如果超出当前的长度 那么直接结束
			if (currentLineWidth + cloneWidth + nextWidth > width) {
				if (currentLineWidth + cloneWidth + penalty.getWidth() <= width) {
					++start;
					clone.append(penalty);
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

	private int spiltIf(Paragraph paragraph, List<? extends Element> elements, int start, Line line, float width, Gravity gravity) {
		if (start >= elements.size()) {
			return start;
		}

		Box box = (Box) elements.get(start);
		Box[] children = box.spilt(width);
		if (children != null) {
			children[0].setFlag(Box.FLAG_SPILT);
			line.add(children[0]);
			box.copy(children[1]);
			box = children[0];
		} else {
			line.add(box);
			++start;
		}

		line.setSpaceWidth(0);
		line.setLineWidth(width);
		line.setLineHeight(box.getHeight());
		line.setRatio(0);
		line.setGravity(gravity);
		paragraph.addLine(line);
		return start;
	}
}
