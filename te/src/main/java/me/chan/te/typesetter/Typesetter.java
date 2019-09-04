package me.chan.te.typesetter;

import java.util.List;

import me.chan.te.data.Element;
import me.chan.te.data.LineConfiguration;
import me.chan.te.data.Paragraph;

public interface Typesetter {
	Paragraph typeset(List<? extends Element> elements, LineConfiguration lineConfiguration);
}
