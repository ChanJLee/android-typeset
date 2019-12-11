package com.shanbay.lib.texas.renderer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shanbay.lib.log.Log;
import com.shanbay.lib.texas.text.OnClickedListener;
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
			w("invalid selection, get paragraph, return -1");
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
		if (mParagraphSelection == null) {
			w("invalid selection, get top, return -1");
			return -1;
		}

		return mParagraphSelection.getTopEdgeOnScreen();
	}

	/**
	 * 获取选中内容高亮的bottom边界，坐标相对于整个屏幕
	 *
	 * @return bottom边界
	 */
	public float getSelectedBottomEdgeOnScreen() {
		if (mParagraphSelection == null) {
			w("invalid selection, get bottom, return -1");
			return -1;
		}

		return mParagraphSelection.getBottomEdgeOnScreen();
	}

	/**
	 * 高亮所有符合tag的元素
	 * {@link Paragraph.Builder#newSpanBuilder()}
	 * {@link Paragraph.Builder#newSpanBuilder(OnClickedListener)}
	 * {@link Paragraph.SpanBuilder#tag(Object)}
	 *
	 * @param tags tags
	 */
	public void selectedByTags(Object... tags) {
		if (tags == null || tags.length == 0 || mParagraphSelection == null) {
			w("invalid argument or selection, ignore");
			return;
		}

		onSelectedByTags(mParagraphSelection, Arrays.asList(tags));
	}

	/**
	 * 高亮所有符合tag的元素
	 * {@link Paragraph.Builder#newSpanBuilder()}
	 * {@link Paragraph.Builder#newSpanBuilder(OnClickedListener)}
	 * {@link Paragraph.SpanBuilder#tag(Object)}
	 *
	 * @param tags tags
	 */
	public void selectedByTags(List<?> tags) {
		if (mParagraphSelection == null || tags == null || tags.isEmpty()) {
			w("invalid argument or selection, ignore");
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
			w("invalid selection, clear ignore");
			return;
		}

		// 通知内容被清除的时候还需要
		ParagraphSelection paragraphSelection = mParagraphSelection;
		mParagraphSelection = null;
		onClear(paragraphSelection);
	}

	abstract void onClear(@NonNull ParagraphSelection paragraphSelection);

	private static void w(String msg) {
		Log.w("TexasSelection", msg);
	}
}
