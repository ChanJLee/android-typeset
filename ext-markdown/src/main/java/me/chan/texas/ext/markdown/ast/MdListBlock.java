package me.chan.texas.ext.markdown.ast;

import java.util.Collections;
import java.util.List;

public final class MdListBlock implements MdBlock {
	public static final int KIND_UNORDERED = 0;
	public static final int KIND_ORDERED = 1;

	public final int kind;
	/**
	 * 无序列表的项目符号 ('-' '*' '+')，有序列表为 '\0'
	 */
	public final char marker;
	/**
	 * 有序列表的起始数字，无序列表为 -1
	 */
	public final int start;
	public final List<MdListItem> items;

	public MdListBlock(int kind, char marker, int start, List<MdListItem> items) {
		this.kind = kind;
		this.marker = marker;
		this.start = start;
		this.items = Collections.unmodifiableList(items);
	}
}
