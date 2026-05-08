package me.chan.texas.ext.markdown.ast;

import java.util.Collections;
import java.util.List;

public final class MdTable implements MdBlock {
	public static final int ALIGN_NONE = 0;
	public static final int ALIGN_LEFT = 1;
	public static final int ALIGN_CENTER = 2;
	public static final int ALIGN_RIGHT = 3;

	public final List<List<MdInline>> header;
	public final int[] alignments;
	public final List<List<List<MdInline>>> body;

	public MdTable(List<List<MdInline>> header, int[] alignments, List<List<List<MdInline>>> body) {
		this.header = Collections.unmodifiableList(header);
		this.alignments = alignments;
		this.body = Collections.unmodifiableList(body);
	}
}
