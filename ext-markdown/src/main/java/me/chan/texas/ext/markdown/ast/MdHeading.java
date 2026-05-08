package me.chan.texas.ext.markdown.ast;

import java.util.Collections;
import java.util.List;

public final class MdHeading implements MdBlock {
	public static final int STYLE_ATX = 0;
	public static final int STYLE_SETEXT = 1;

	public final int level;
	public final int style;
	public final List<MdInline> inlines;

	public MdHeading(int level, int style, List<MdInline> inlines) {
		if (level < 1 || level > 6) {
			throw new IllegalArgumentException("heading level out of range: " + level);
		}
		this.level = level;
		this.style = style;
		this.inlines = Collections.unmodifiableList(inlines);
	}
}
