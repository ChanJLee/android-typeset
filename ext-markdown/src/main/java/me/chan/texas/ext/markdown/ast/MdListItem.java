package me.chan.texas.ext.markdown.ast;

import java.util.Collections;
import java.util.List;

public final class MdListItem implements MdNode {
	public final List<MdBlock> blocks;

	public MdListItem(List<MdBlock> blocks) {
		this.blocks = Collections.unmodifiableList(blocks);
	}
}
