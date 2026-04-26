package me.chan.texas.ext.markdown;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MarkdownDocument {
	private final List<Block> mBlocks;

	MarkdownDocument(List<Block> blocks) {
		mBlocks = Collections.unmodifiableList(new ArrayList<>(blocks));
	}

	public List<Block> getBlocks() {
		return mBlocks;
	}

	public int getBlockCount() {
		return mBlocks.size();
	}

	public Block getBlock(int index) {
		return mBlocks.get(index);
	}

	public enum BlockType {
		PARAGRAPH,
		HEADING,
		THEMATIC_BREAK,
		LIST,
		BLOCK_QUOTE,
		CODE_BLOCK,
		TABLE
	}

	public enum InlineType {
		TEXT,
		EMPHASIS,
		STRONG,
		INLINE_CODE,
		LINK,
		IMAGE,
		AUTOLINK
	}

	public enum ListType {
		ORDERED,
		UNORDERED
	}

	public enum Alignment {
		NONE,
		LEFT,
		CENTER,
		RIGHT
	}

	public abstract static class Block {
		private final BlockType mType;

		Block(BlockType type) {
			mType = type;
		}

		public BlockType getType() {
			return mType;
		}
	}

	public static final class ParagraphBlock extends Block {
		private final List<Inline> mInlines;

		ParagraphBlock(List<Inline> inlines) {
			super(BlockType.PARAGRAPH);
			mInlines = immutableList(inlines);
		}

		public List<Inline> getInlines() {
			return mInlines;
		}
	}

	public static final class HeadingBlock extends Block {
		private final int mLevel;
		private final List<Inline> mInlines;

		HeadingBlock(int level, List<Inline> inlines) {
			super(BlockType.HEADING);
			mLevel = level;
			mInlines = immutableList(inlines);
		}

		public int getLevel() {
			return mLevel;
		}

		public List<Inline> getInlines() {
			return mInlines;
		}
	}

	public static final class ThematicBreakBlock extends Block {
		ThematicBreakBlock() {
			super(BlockType.THEMATIC_BREAK);
		}
	}

	public static final class CodeBlock extends Block {
		private final String mInfoString;
		private final String mCode;
		private final boolean mFenced;

		CodeBlock(@Nullable String infoString, String code, boolean fenced) {
			super(BlockType.CODE_BLOCK);
			mInfoString = infoString == null ? "" : infoString;
			mCode = code;
			mFenced = fenced;
		}

		public String getInfoString() {
			return mInfoString;
		}

		public String getCode() {
			return mCode;
		}

		public boolean isFenced() {
			return mFenced;
		}
	}

	public static final class ListBlock extends Block {
		private final ListType mListType;
		private final int mStartNumber;
		private final List<ListItem> mItems;

		ListBlock(ListType listType, int startNumber, List<ListItem> items) {
			super(BlockType.LIST);
			mListType = listType;
			mStartNumber = startNumber;
			mItems = immutableList(items);
		}

		public ListType getListType() {
			return mListType;
		}

		public int getStartNumber() {
			return mStartNumber;
		}

		public List<ListItem> getItems() {
			return mItems;
		}
	}

	public static final class ListItem {
		private final List<Block> mBlocks;

		ListItem(List<Block> blocks) {
			mBlocks = immutableList(blocks);
		}

		public List<Block> getBlocks() {
			return mBlocks;
		}
	}

	public static final class BlockQuoteBlock extends Block {
		private final List<Block> mBlocks;

		BlockQuoteBlock(List<Block> blocks) {
			super(BlockType.BLOCK_QUOTE);
			mBlocks = immutableList(blocks);
		}

		public List<Block> getBlocks() {
			return mBlocks;
		}
	}

	public static final class TableBlock extends Block {
		private final TableRow mHeader;
		private final List<Alignment> mAlignments;
		private final List<TableRow> mRows;

		TableBlock(TableRow header, List<Alignment> alignments, List<TableRow> rows) {
			super(BlockType.TABLE);
			mHeader = header;
			mAlignments = immutableList(alignments);
			mRows = immutableList(rows);
		}

		public TableRow getHeader() {
			return mHeader;
		}

		public List<Alignment> getAlignments() {
			return mAlignments;
		}

		public List<TableRow> getRows() {
			return mRows;
		}
	}

	public static final class TableRow {
		private final List<List<Inline>> mCells;

		TableRow(List<List<Inline>> cells) {
			List<List<Inline>> copy = new ArrayList<>(cells.size());
			for (int i = 0; i < cells.size(); ++i) {
				copy.add(immutableList(cells.get(i)));
			}
			mCells = Collections.unmodifiableList(copy);
		}

		public List<List<Inline>> getCells() {
			return mCells;
		}
	}

	public abstract static class Inline {
		private final InlineType mType;

		Inline(InlineType type) {
			mType = type;
		}

		public InlineType getType() {
			return mType;
		}
	}

	public static final class TextInline extends Inline {
		private final String mText;

		TextInline(String text) {
			super(InlineType.TEXT);
			mText = text;
		}

		public String getText() {
			return mText;
		}
	}

	public static final class StyledInline extends Inline {
		private final List<Inline> mChildren;

		StyledInline(InlineType type, List<Inline> children) {
			super(type);
			mChildren = immutableList(children);
		}

		public List<Inline> getChildren() {
			return mChildren;
		}
	}

	public static final class CodeInline extends Inline {
		private final String mCode;

		CodeInline(String code) {
			super(InlineType.INLINE_CODE);
			mCode = code;
		}

		public String getCode() {
			return mCode;
		}
	}

	public static final class LinkInline extends Inline {
		private final List<Inline> mLabel;
		private final String mUrl;
		private final String mTitle;

		LinkInline(InlineType type, List<Inline> label, String url, @Nullable String title) {
			super(type);
			mLabel = immutableList(label);
			mUrl = url;
			mTitle = title == null ? "" : title;
		}

		public List<Inline> getLabel() {
			return mLabel;
		}

		public String getUrl() {
			return mUrl;
		}

		public String getTitle() {
			return mTitle;
		}
	}

	@NonNull
	private static <T> List<T> immutableList(List<T> list) {
		return Collections.unmodifiableList(new ArrayList<>(list));
	}
}
