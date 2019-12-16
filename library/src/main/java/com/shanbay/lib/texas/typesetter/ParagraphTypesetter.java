package com.shanbay.lib.texas.typesetter;

import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.text.TextAttribute;
import com.shanbay.lib.texas.text.BreakStrategy;
import com.shanbay.lib.texas.text.Paragraph;

@Hidden
public interface ParagraphTypesetter {
	float INFINITY = 1000;
	int HYPHEN_PENALTY = 20;

	boolean typeset(Paragraph paragraph, TextAttribute textAttribute, BreakStrategy breakStrategy);
}
