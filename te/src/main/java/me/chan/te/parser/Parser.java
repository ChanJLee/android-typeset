package me.chan.te.parser;

import java.util.List;

import me.chan.te.data.Element;

public interface Parser {
	List<? extends Element> parser(CharSequence paragraph);
}
