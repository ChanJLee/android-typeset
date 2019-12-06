package me.chan.texas.typesetter;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;

@Hidden
public interface ParagraphTypesetter {
	float INFINITY = 1000;
	int HYPHEN_PENALTY = 100;

	boolean typeset(Paragraph paragraph, TextAttribute textAttribute, BreakStrategy breakStrategy);
}
