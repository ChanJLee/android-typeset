package me.chan.te.typesetter;

import me.chan.te.annotations.Hidden;
import me.chan.te.config.LineAttributes;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Paragraph;

@Hidden
public interface ParagraphTypesetter {
	float INFINITY = 1000;
	int HYPHEN_PENALTY = 100;

	boolean typeset(Paragraph paragraph, LineAttributes lineAttributes, BreakStrategy breakStrategy);
}
