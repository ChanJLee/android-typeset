package me.chan.te.typesetter;

import me.chan.te.annotations.Hidden;
import me.chan.te.text.TextAttribute;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Paragraph;

@Hidden
public interface ParagraphTypesetter {
	float INFINITY = 1000;
	int HYPHEN_PENALTY = 100;

	boolean typeset(Paragraph paragraph, TextAttribute textAttribute, BreakStrategy breakStrategy);
}
