package me.chan.te.typesetter;

import java.util.List;

import me.chan.te.config.LineAttributes;
import me.chan.te.data.Box;
import me.chan.te.data.DrawableBox;
import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.data.Penalty;
import me.chan.te.data.TextBox;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Gravity;
import me.chan.te.text.Line;
import me.chan.te.text.Paragraph;

class SimpleTypesetter implements ParagraphTypesetter {

	@Override
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
		int next;

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
			start = mergeIf(box, start + 1, size, elements);

			// 如果超出当前的长度 那么直接结束
			if (currentLineWidth + box.getWidth() > lineWidth) {
				// 如果一行是空的，说明当前只能排一个，并且都显示不下
				if (line.isEmpty()) {
					return spiltIf(paragraph, box, elements, next, line, lineWidth, gravity);
				}

				break;
			}

			line.add(box);
			float boxWidth = box.getWidth();
			currentLineWidth += boxWidth;
			boxTotalWidth += boxWidth;

			if (lineHeight < box.getHeight()) {
				lineHeight = box.getHeight();
			}
		}

		if (breakStrategy == BreakStrategy.SIMPLE) {
			line.setSpaceWidth(wordSpaceWidth);
		} else {
			int boxSize = line.getCount();
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

	private int mergeIf(Box box, int start, int end, List<? extends Element> elements) {
		if (!(box instanceof TextBox)) {
			return start;
		}

		TextBox current = (TextBox) box;
		for (; start < end; ++start) {
			Element element = elements.get(start);
			if (element instanceof Glue) {
				break;
			} else if (element instanceof DrawableBox) {
				break;
			} else if (element instanceof Penalty) {
				/* do nothing */
			} else if (element instanceof TextBox) {
				TextBox other = (TextBox) element;
				current.append(other);
			} else {
				break;
			}
		}

		return start;
	}

	private int spiltIf(Paragraph paragraph, Box box, List<? extends Element> elements, int next, Line line, float width, Gravity gravity) {
		if (next >= elements.size()) {
			return next;
		}

		// 如果不是文本 那不好分割 直接返回
		if (!(box instanceof TextBox)) {
			line.add(box);
			line.setSpaceWidth(0);
			line.setLineWidth(width);
			line.setLineHeight(box.getHeight());
			line.setRatio(0);
			line.setGravity(gravity);
			paragraph.addLine(line);
			return next;
		}

		TextBox current = (TextBox) box;
		TextBox suffix = null;
		if ((suffix = current.spilt(width)) != null) {
			// 否则要往前退一格
			if (elements.get(next) instanceof Glue) {
				--next;
			}
			List<Element> list = (List<Element>) elements;
			line.add(box);
			list.set(next, suffix);
		} else {
			line.add(box);
		}

		line.setSpaceWidth(0);
		line.setLineWidth(width);
		line.setLineHeight(box.getHeight());
		line.setRatio(0);
		line.setGravity(gravity);
		paragraph.addLine(line);
		return next;
	}
}
