package me.chan.texas.renderer.selection;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextPaint;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.collection.SparseArrayCompat;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文本选中区域
 */
@RestrictTo(LIBRARY)
public class ParagraphSelection extends DefaultRecyclable {
	private static final ObjectPool<ParagraphSelection> POOL = new ObjectPool<>(32);
	private static final AtomicInteger UUID = new AtomicInteger(0);

	private Paragraph mParagraph;
	private int mBgColor;
	private int mTextColor;
	private Style mStyle;
	private final List<RectF> mBackgrounds = new ArrayList<>();
	private int mId;
	private int mIndex;
	private final SparseArrayCompat<Box> mSet = new SparseArrayCompat<>(32);

	private ParagraphSelection() {
	}

	private void reset(Paragraph paragraph, Style style) {
		mParagraph = paragraph;
		mId = UUID.incrementAndGet();
		mStyle = style;
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		mIndex = index;
	}

	/**
	 * 更新 style
	 *
	 * @param renderOption render option
	 */
	public void updateStyle(RenderOption renderOption) {
		if (mStyle != null) {
			mStyle.update(this, renderOption);
		}
	}

	public int getId() {
		return mId;
	}

	public int getBgColor() {
		return mBgColor;
	}

	public int getTextColor() {
		return mTextColor;
	}

	public Paragraph getParagraph() {
		return mParagraph;
	}

	private Box mFirst;
	private Box mLast;

	public void pushBox(Box box) {
		mSet.put(box.getId(), box);

		if (mLast == null) {
			mLast = box;
		}

		mFirst = box;
	}

	public void appendBox(Box box) {
		mSet.put(box.getId(), box);

		if (mFirst == null) {
			mFirst = box;
		}

		mLast = box;
	}

	/**
	 * @return 选中区域是空的
	 */
	public boolean isSelectedRegionEmpty() {
		return mBackgrounds.isEmpty();
	}

	public void draw(Canvas canvas, TextPaint textPaint, float radius) {
		if (radius <= 0) {
			for (RectF rectF : mBackgrounds) {
				canvas.drawRect(rectF, textPaint);
			}
			return;
		}

		for (RectF rectF : mBackgrounds) {
			canvas.drawRoundRect(rectF, radius, radius, textPaint);
		}
	}

	public void appendRegion(RectF rectF) {
		mBackgrounds.add(rectF);
	}

	public void pushRegion(RectF rectF) {
		mBackgrounds.add(0, rectF);
	}

	/**
	 * @return 因为排版的时候单词会被拆分，因此会导致用户设置的tag重复，这个方法内部还需要去去重，但是无法对空tag去重，所以忽略空tag
	 */
	public List<Object> getSelectedTags() {
		List<Object> result = new ArrayList<>();
		Object last = null; /* 去重 */
		Layout layout = mParagraph.getLayout();
		int count = layout.getLineCount();
		for (int i = 0; i < count; ++i) {
			Line line = layout.getLine(i);
			for (int j = 0; j < line.getCount(); ++j) {
				Element element = line.getElement(j);
				if (!(element instanceof Box)) {
					continue;
				}

				Box box = (Box) element;
				if (!isSelected(box)) {
					continue;
				}

				Object tag = box.getTag();
				if (tag == last || tag == null) {
					continue;
				}

				result.add(tag);
				last = tag;
			}
		}
		return result;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		mFirst = mLast = null;
		mBackgrounds.clear();
		mParagraph = null;
		mBgColor = mTextColor = 0;
		mStyle = null;
		mId = 0;
		mIndex = 0;
		super.recycle();
		POOL.release(this);
	}

	/**
	 * @param paragraph paragraph
	 * @param style     渲染样式
	 * @return selection selection
	 */
	public static ParagraphSelection obtain(Paragraph paragraph, Style style) {
		ParagraphSelection paragraphSelection = POOL.acquire();
		if (paragraphSelection == null) {
			paragraphSelection = new ParagraphSelection();
		}

		paragraphSelection.reuse();
		paragraphSelection.reset(paragraph, style);
		return paragraphSelection;
	}

	@Nullable
	public RectF getFirstRegion() {
		if (mBackgrounds.isEmpty()) {
			return null;
		}
		return mBackgrounds.get(0);
	}

	@Nullable
	public RectF getLastRegion() {
		if (mBackgrounds.isEmpty()) {
			return null;
		}
		return mBackgrounds.get(mBackgrounds.size() - 1);
	}

	@Nullable
	public Box getFirstBox() {
		return mFirst;
	}

	@Nullable
	public Box getLastBox() {
		return mLast;
	}

	public boolean isEmpty() {
		return mFirst == null;
	}

	public boolean isSelected(Box box) {
		return mSet.containsKey(box.getId());
	}

	public void clear() {
		mSet.clear();
		mFirst = mLast = null;
		mBackgrounds.clear();
	}

	public interface Style {
		void update(ParagraphSelection paragraphSelection, RenderOption renderOption);
	}

	public static final Style LONG_CLICK = (paragraphSelection, renderOption) -> {
		paragraphSelection.mTextColor = renderOption.getSelectedByLongClickTextColor();
		paragraphSelection.mBgColor = renderOption.getSelectedByLongClickBackgroundColor();
	};

	public static final Style CLICK = (paragraphSelection, renderOption) -> {
		paragraphSelection.mTextColor = renderOption.getSelectedTextColor();
		paragraphSelection.mBgColor = renderOption.getSelectedBackgroundColor();
	};
}
