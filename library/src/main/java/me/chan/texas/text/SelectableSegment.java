package me.chan.texas.text;

import me.chan.texas.renderer.ui.text.ParagraphView;

public interface SelectableSegment {
	int getParagraphCount();

	ParagraphView getParagraphView(int index);

	Paragraph getParagraph(int index);
}
