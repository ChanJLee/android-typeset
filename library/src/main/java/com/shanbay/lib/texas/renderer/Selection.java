package com.shanbay.lib.texas.renderer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shanbay.lib.texas.text.Paragraph;

import java.util.Arrays;
import java.util.List;

public abstract class Selection {

	private ParagraphSelection mParagraphSelection;

	Selection(ParagraphSelection paragraphSelection) {
		mParagraphSelection = paragraphSelection;
	}

	void update(Selection selection) {
		mParagraphSelection = selection.mParagraphSelection;
	}

	@Nullable
	Paragraph getParagraph() {
		if (mParagraphSelection == null) {
			return null;
		}
		return mParagraphSelection.getParagraph();
	}

	@Nullable
	ParagraphSelection getParagraphSelection() {
		return mParagraphSelection;
	}

	/**
	 * 获取选中内容高亮的top边界，坐标相对于整个屏幕
	 *
	 * @return top边界
	 */
	public float getSelectedTopEdgeOnScreen() {
		return mParagraphSelection == null ? -1 : mParagraphSelection.getTopEdgeOnScreen();
	}

	/**
	 * 获取选中内容高亮的bottom边界，坐标相对于整个屏幕
	 *
	 * @return bottom边界
	 */
	public float getSelectedBottomEdgeOnScreen() {
		return mParagraphSelection == null ? -1 : mParagraphSelection.getBottomEdgeOnScreen();
	}

	/**
	 * @param tags 重新高亮
	 */
	public void selectedByTags(Object... tags) {
		if (tags == null || tags.length == 0 || mParagraphSelection == null) {
			return;
		}

		onSelectedByTags(mParagraphSelection, Arrays.asList(tags));
	}

	public void selectedByTags(List<?> tags) {
		if (tags == null || tags.isEmpty() || mParagraphSelection == null) {
			return;
		}

		onSelectedByTags(mParagraphSelection, tags);
	}

	abstract void onSelectedByTags(@NonNull ParagraphSelection paragraphSelection, @NonNull List<?> tags);

	/**
	 * 清除选中
	 */
	public void clear() {
		if (mParagraphSelection == null) {
			return;
		}

		mParagraphSelection.clearSelection();
		onClear(mParagraphSelection.getParagraph());
		mParagraphSelection = null;
	}

	abstract void onClear(Paragraph paragraph);
}
