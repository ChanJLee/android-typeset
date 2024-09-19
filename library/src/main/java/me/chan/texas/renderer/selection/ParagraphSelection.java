package me.chan.texas.renderer.selection;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextPaint;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.misc.BitBucket;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;

/**
 * 文本选中区域
 */
@RestrictTo(LIBRARY)
public class ParagraphSelection extends DefaultRecyclable {
	private static final ObjectPool<ParagraphSelection> POOL = new ObjectPool<>(32);
	private static final AtomicInteger UUID = new AtomicInteger(0);

	private int mBgColor;
	private int mTextColor;
	private Style mStyle;
	private final List<RectF> mBackgrounds = new ArrayList<>();
	private int mId;
	private final BitBucket mSet = new BitBucket(128);

	private ParagraphSelection() {
	}

	private void reset(Style style) {
		mId = UUID.incrementAndGet();
		mStyle = style;
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

	private Box mFirst;
	private Box mLast;

	public void pushBox(Box box) {
		mSet.set(box.getSeq(), true);

		if (mLast == null) {
			mLast = box;
		}

		mFirst = box;
	}

	public void appendBox(Box box) {
		mSet.set(box.getSeq(), true);

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
	@Nullable
	@RestrictTo(LIBRARY)
	@MainThread
	public static List<Object> getSelectedTags(Paragraph paragraph) {
		Layout layout = paragraph.getLayout();
		if (paragraph.isRecycled() || layout == null || layout.isRecycled()) {
			return null;
		}

		try {
			GET_SELECTED_TAG_VISITOR.visit(paragraph);
			return GET_SELECTED_TAG_VISITOR.tags;
		} catch (Throwable ignored) {
			return null;
		} finally {
			GET_SELECTED_TAG_VISITOR.tags = null;
			GET_SELECTED_TAG_VISITOR.selection = null;
		}
	}

	@Override
	protected void onRecycle() {
		mSet.clear();
		mFirst = mLast = null;
		mBackgrounds.clear();
		mBgColor = mTextColor = 0;
		mStyle = null;
		mId = 0;
		POOL.release(this);
	}

	/**
	 * @param style 渲染样式
	 * @return selection selection
	 */
	public static ParagraphSelection obtain(Style style) {
		ParagraphSelection paragraphSelection = POOL.acquire();
		if (paragraphSelection == null) {
			paragraphSelection = new ParagraphSelection();
		}

		paragraphSelection.reuse();
		paragraphSelection.reset(style);
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

	@RestrictTo(LIBRARY)
	public boolean isSelected(Box box) {
		return mSet.get(box.getSeq());
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

	private final static GetSelectedTagVisitor GET_SELECTED_TAG_VISITOR = new GetSelectedTagVisitor();

	private static class GetSelectedTagVisitor extends ParagraphVisitor {
		public List<Object> tags;
		public ParagraphSelection selection;

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {
			tags = new ArrayList<>();
			selection = paragraph.getSelection();
		}

		@Override
		protected void onVisitParagraphEnd(Paragraph paragraph) {

		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {

		}

		@Override
		protected void onVisitLineEnd(Line line, float x, float y) {

		}

		@Override
		protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
			if (selection.isSelected(box)) {
				tags.add(box.getTag());
			}
		}
	}
}
