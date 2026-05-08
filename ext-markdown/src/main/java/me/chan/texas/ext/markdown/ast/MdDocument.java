package me.chan.texas.ext.markdown.ast;

import java.util.Collections;
import java.util.List;

public final class MdDocument implements MdNode {
	public final List<MdBlock> blocks;

	public MdDocument(List<MdBlock> blocks) {
		this.blocks = Collections.unmodifiableList(blocks);
	}
}
