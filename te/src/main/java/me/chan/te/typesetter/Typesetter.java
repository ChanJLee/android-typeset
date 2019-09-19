package me.chan.te.typesetter;

import java.util.List;

import me.chan.te.data.Element;
import me.chan.te.config.LineAttributes;
import me.chan.te.data.Paragraph;

public interface Typesetter {
	Paragraph typeset(List<? extends Element> elements, LineAttributes lineAttributes);
}
