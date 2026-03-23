package me.chan.texas.renderer.selection.visitor;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Span;

public class RebuildBackgroundSelectedVisitor extends SelectedVisitor {

	@Override
	protected ParagraphSelection onCreateSelection(Selection.Type type, Selection.Styles styles, Paragraph paragraph) {
		return paragraph.getSelection(type);
	}

	@Override
	protected boolean selected(Span span, RectF inner, RectF outer) {
		return mSelection.isSelected(span);
	}
}
