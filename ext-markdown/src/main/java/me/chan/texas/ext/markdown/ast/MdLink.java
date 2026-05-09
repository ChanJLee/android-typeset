package me.chan.texas.ext.markdown.ast;

import java.util.Collections;
import java.util.List;

public final class MdLink implements MdInline {
	public final List<MdInline> inlines;
	public final String url;
	public final String title;

	public MdLink(List<MdInline> inlines, String url, String title) {
		this.inlines = Collections.unmodifiableList(inlines);
		this.url = url;
		this.title = title;
	}
}
