package me.chan.texas.ext.markdown.ast;

import java.util.Collections;
import java.util.List;

public final class MdParagraph implements MdBlock {
	public final List<MdInline> inlines;

	public MdParagraph(List<MdInline> inlines) {
		this.inlines = Collections.unmodifiableList(inlines);
	}
}
