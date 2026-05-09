package me.chan.texas.ext.markdown.ast;

import java.util.Collections;
import java.util.List;

public final class MdBlockQuote implements MdBlock {
	public final List<MdBlock> blocks;

	public MdBlockQuote(List<MdBlock> blocks) {
		this.blocks = Collections.unmodifiableList(blocks);
	}
}
