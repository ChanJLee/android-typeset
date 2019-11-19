package me.chan.te.typesetter;

import me.chan.te.text.TextAttribute;
import me.chan.te.text.Box;
import me.chan.te.text.Glue;
import me.chan.te.text.Penalty;
import me.chan.te.text.TextBox;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Gravity;
import me.chan.te.text.Paragraph;

class SimpleParagraphTypesetter implements ParagraphTypesetter {

	@Override
	public boolean typeset(Paragraph paragraph,
						   TextAttribute textAttribute, BreakStrategy breakStrategy) {
		// 一行尽可能的占满尽可能多的字符
		// 如果如果只显示了一个并且还不足以完美显示，那么无脑折断
		int size = paragraph.getElementCount();
		for (int i = 0; i < size; ) {
			TextAttribute.LineAttribute attribute = textAttribute.get(paragraph.getLineCount());
			i = typesetLine(attribute,
					paragraph,
					i,
					breakStrategy,
					textAttribute.getSpaceWidth()
			);
		}

		int lineCount = paragraph.getLineCount();
		if (breakStrategy == BreakStrategy.SIMPLE || lineCount == 0) {
			return true;
		}

		return true;
	}

	private int typesetLine(TextAttribute.LineAttribute attribute,
							Paragraph paragraph, int start, BreakStrategy breakStrategy, float wordSpaceWidth) {
		int size = paragraph.getElementCount();
		float lineWidth = attribute.getLineWidth();
		Gravity gravity = attribute.getGravity();

		// skip none box
		for (; start < size; ++start) {
			Paragraph.Element element = paragraph.getElement(start);
			if (element instanceof Box) {
				break;
			}
		}

		if (start >= size) {
			return start;
		}

		Paragraph.Line line = Paragraph.Line.obtain();
		float lineHeight = 0f;
		float currentLineWidth = 0f;
		float boxTotalWidth = 0f;

		while (start < size) {
			Paragraph.Element element = paragraph.getElement(start);
			if (element instanceof Glue) {
				Glue glue = (Glue) element;
				currentLineWidth += (breakStrategy == BreakStrategy.BALANCED ?
						wordSpaceWidth - glue.getShrink() : wordSpaceWidth);
				++start;
				continue;
			}

			if (!(element instanceof Box)) {
				++start;
				continue;
			}

			Box box = (Box) element;
			int next = mergeIf(box, start + 1, size, paragraph);

			// 如果超出当前的长度 那么直接结束
			if (currentLineWidth + box.getWidth() > lineWidth) {
				// 超出长度，如果因为当前 box 被merge过，需要调整下下标
				if (start + 1 != next) {
					start = next - 1;
					paragraph.replace(start, box);
				}

				// 如果一行是空的，说明当前只能排一个，并且都显示不下
				if (line.isEmpty()) {
					return spiltIf(paragraph, box, start, line, lineWidth, gravity);
				}
				break;
			}

			// 否则正常的跳转下一个下标
			start = next;

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

	private int mergeIf(Box box, int start, int end, Paragraph paragraph) {
		if (!(box instanceof TextBox)) {
			return start;
		}

		TextBox current = (TextBox) box;
		for (; start < end; ++start) {
			Paragraph.Element element = paragraph.getElement(start);
			if (element instanceof Penalty) {
				/* do nothing */
			} else if (element instanceof TextBox) {
				TextBox other = (TextBox) element;
				current.append(other);
			} else {
				return start;
			}
		}

		return start;
	}

	private int spiltIf(Paragraph paragraph, Box box, int currentIndex, Paragraph.Line line, float width, Gravity gravity) {
		if (currentIndex >= paragraph.getElementCount()) {
			return currentIndex;
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

			// 并且忽略吃掉当前的box
			return currentIndex + 1;
		}

		TextBox current = (TextBox) box;
		TextBox suffix = null;
		if ((suffix = current.spilt(width)) != null) {
			line.add(box);
			paragraph.replace(currentIndex, suffix);
		} else {
			++currentIndex;
			line.add(box);
		}

		line.setSpaceWidth(0);
		line.setLineWidth(width);
		line.setLineHeight(box.getHeight());
		line.setRatio(0);
		line.setGravity(gravity);
		paragraph.addLine(line);
		return currentIndex;
	}
}
