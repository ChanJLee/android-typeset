package me.chan.texas.ext.markdown.ast;

import java.util.Collections;
import java.util.List;

public final class MdEmphasis implements MdInline {
	public final String delimiter;
	public final List<MdInline> inlines;

	public MdEmphasis(String delimiter, List<MdInline> inlines) {
		this.delimiter = delimiter;
		this.inlines = Collections.unmodifiableList(inlines);
	}
}
