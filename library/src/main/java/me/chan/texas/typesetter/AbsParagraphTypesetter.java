package me.chan.texas.typesetter;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.renderer.selection.visitor.RebuildBackgroundSelectedVisitor;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextGravity;
import me.chan.texas.text.layout.Span;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextSpan;
import me.chan.texas.typesetter.utils.ElementStream;

@RestrictTo(LIBRARY)
public abstract class AbsParagraphTypesetter {
	public static final boolean DEBUG = false;
	public static final int INFINITY_WIDTH = Integer.MAX_VALUE;

	public final boolean typeset(Paragraph paragraph, BreakStrategy breakStrategy, RenderOption renderOption, int lineWidth, boolean desire) {
		if (DEBUG) {
			for (int i = 0; i < paragraph.getElementCount(); ++i) {
				Element element = paragraph.getElement(i);
				if (element instanceof TextSpan) {
					TextSpan textBox = (TextSpan) element;
					if (!textBox.hasAttribute(TextSpan.ATTRIBUTE_MEASURED)) {
						throw new IllegalStateException("text box not measured");
					}
				}
			}
		}

		if (!onTypeset(paragraph, breakStrategy, lineWidth)) {
			return false;
		}

		if (desire) {
			lineWidth = getDesiredWidth(paragraph);
		}

		buildLayoutBounds(paragraph, lineWidth);
		updateSelectionBackground(paragraph, renderOption);
		return true;
	}

	private static void updateSelectionBackground(Paragraph paragraph, RenderOption renderOption) {
		ParagraphSelection selection = paragraph.getSelection(Selection.Type.SELECTION);
		ParagraphSelection highlight = paragraph.getSelection(Selection.Type.HIGHLIGHT);

		if (isSelectionBackgroundValid(selection) && isSelectionBackgroundValid(highlight)) {
			return;
		}

		RebuildBackgroundSelectedVisitor visitor = new RebuildBackgroundSelectedVisitor();
		if (!isSelectionBackgroundValid(selection)) {
			rebuildBackgroundSelected(selection, visitor, renderOption);
		}

		if (!isSelectionBackgroundValid(highlight)) {
			rebuildBackgroundSelected(highlight, visitor, renderOption);
		}
	}

	private static void rebuildBackgroundSelected(ParagraphSelection selection, RebuildBackgroundSelectedVisitor visitor, RenderOption renderOption) {
		try {
			Paragraph paragraph = selection.getParagraph();
			visitor.reset(selection.getType(), selection.getSelectionStyle(), paragraph, renderOption);
			visitor.startVisit(paragraph);
		} catch (Throwable e) {
			/* TODO ? */
		} finally {
			visitor.clear();
		}
	}

	private static boolean isSelectionBackgroundValid(ParagraphSelection selection) {
		return selection == null || !selection.isBackgroundInvalid();
	}

	@VisibleForTesting
	public final boolean typeset(Paragraph paragraph, BreakStrategy breakStrategy, RenderOption renderOption, int lineWidth) {
		return typeset(paragraph, breakStrategy, renderOption, lineWidth, false);
	}

	private static int getDesiredWidth(Paragraph paragraph) {
		Layout layout = paragraph.getLayout();
		float actualWidth = 0;
		for (int i = 0; i < layout.getLineCount(); ++i) {
			Line line = layout.getLine(i);
			actualWidth = Math.max(line.getLineWidth(), actualWidth);
		}
		return (int) Math.ceil(actualWidth);
	}

	protected abstract boolean onTypeset(Paragraph paragraph, BreakStrategy breakStrategy, int lineWidth);

	/**
	 * @return debug 信息
	 */
	public Object getInternalState() {
		return null;
	}

	/**
	 * 创建行
	 *
	 * @param stream   stream
	 * @param endState 结束位置
	 * @return 行
	 */
	protected static Line createLine(ElementStream stream, int endState, BreakStrategy breakStrategy, int lineWidth) {
		Line.Builder builder = Line.Builder.obtain();

		while (!stream.eof() && !stream.checkState(endState)) {
			Element element = stream.next();
			builder.add(element);
		}

		Line line = builder.build(breakStrategy, lineWidth);
		builder.recycle();
		return line;
	}


	public static void buildLayoutBounds(Paragraph paragraph, int width) {
		RectF lineRect = new RectF();
		RectF boxRect = new RectF();
		Layout layout = paragraph.getLayout();
		int horizontalGravity = layout.getHorizontalGravity();
		int paddingLeft = layout.getPaddingLeft();
		float lineSpacingExtra = (int) layout.getLineSpacingExtra();
		lineRect.bottom = layout.getPaddingTop() - lineSpacingExtra /* 为了方便叠加spacing */;

		int height = 0;
		int lineCount = layout.getLineCount();
		for (int i = 0; i < lineCount; ++i) {
			Line line = layout.getLine(i);
			height += line.getLineHeight();
			getLineHorizontalBounds(horizontalGravity, line, lineRect, width, paddingLeft);
			lineRect.top = lineRect.bottom + lineSpacingExtra;
			lineRect.bottom = lineRect.top + line.getLineHeight();
			line.setBounds(lineRect);
			Span prev = null;
			boxRect.set(lineRect.left, lineRect.top, lineRect.left, lineRect.bottom);
			for (int j = 0; j < line.getElementCount(); ++j) {
				Element element = line.getElement(j);
				if (element instanceof Span) {
					Span current = (Span) element;
					boxRect.right = boxRect.left + current.getWidth();
					current.setOuterBounds(boxRect);
					current.setInnerBounds(boxRect);
					if (prev != null) {
						prev.linkBounds(current);
					}
					prev = current;
					boxRect.left = boxRect.right;
					continue;
				}
				boxRect.left += getAdjustGlueWidth(line, (Glue) element);
			}
			line.trim();

			int size = line.getElementCount();
			if (size != 0) {
				Span element = (Span) line.getElement(0);
				if (element instanceof TextSpan) {
					TextSpan textBox = (TextSpan) element;
					textBox.addAttribute(TextSpan.ATTRIBUTE_LINE_HEADER);
				}

				element = (Span) line.getElement(size - 1);
				if (element instanceof TextSpan) {
					TextSpan textBox = (TextSpan) element;
					textBox.addAttribute(TextSpan.ATTRIBUTE_LINE_TAILER);
				}
			}
		}

		width = width + paddingLeft + layout.getPaddingRight();
		height = height + layout.getPaddingTop() + layout.getPaddingBottom();
		if (lineCount != 0) {
			height += (int) Math.ceil((lineSpacingExtra * (lineCount - 1)));
		}
		layout.setContentSize(width, height);
	}

	private static float getAdjustGlueWidth(Line line, Glue glue) {
		float ratio = line.getRatio();
		if (ratio == 0) {
			return glue.getWidth();
		}

		if (ratio > 0) {
			return glue.getWidth() + ratio * glue.getStretch();
		}

		return glue.getWidth() + ratio * glue.getShrink();
	}

	private static void getLineHorizontalBounds(int horizontalGravity, Line line, RectF bounds, int width, int paddingLeft) {
		if (horizontalGravity == TextGravity.START) {
			bounds.left = paddingLeft;
		} else if (horizontalGravity == TextGravity.CENTER_HORIZONTAL) {
			float offsetX = (width - line.getLineWidth()) / 2.0f;
			bounds.left = paddingLeft + offsetX;
		} else if (horizontalGravity == TextGravity.END) {
			float offsetX = width - line.getLineWidth();
			bounds.left = paddingLeft + offsetX;
		} else {
			throw new IllegalStateException("unknown text gravity");
		}
		bounds.right = bounds.left + line.getLineWidth();
	}
}
