package me.chan.texas.renderer.selection;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.misc.BitBucket;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextStyle;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;

/**
 * 文本选中区域
 */
public class ParagraphSelection extends DefaultRecyclable {
	private static final ObjectPool<ParagraphSelection> POOL = new ObjectPool<>(32);
	private static final AtomicInteger UUID = new AtomicInteger(0);

	private InternalSelectionStyle mStyle;
	private int mId;
	private final BitBucket mSet = new BitBucket(128);
	private final List<RectF> mBackgrounds = new ArrayList<>();
	private Selection.Type mType;

	private Paragraph mParagraph;

	private ParagraphSelection() {
	}

	public Selection.Type getType() {
		return mType;
	}

	private void reset(InternalSelectionStyle style) {
		mId = UUID.incrementAndGet();
		mStyle = style;
	}

	@RestrictTo(LIBRARY)
	public TextStyle getStyle() {
		return mStyle;
	}

	public Selection.Styles getSelectionStyle() {
		return mStyle.mStyles;
	}

	@RestrictTo(LIBRARY)
	public int getId() {
		return mId;
	}

	private Box mFirst;
	private Box mLast;

	@RestrictTo(LIBRARY)
	public void prependBox(Box box) {
		mSet.set(box.getSeq(), true);


		if (mLast == null) {
			mLast = box;
		}

		mFirst = box;
	}

	public void appendRegion(RectF rectF) {
		appendRegion(rectF.left, rectF.top, rectF.right, rectF.bottom);
	}

	@RestrictTo(LIBRARY)
	public void appendRegion(float left, float top, float right, float bottom) {
		if (mBackgrounds.isEmpty()) {
			appendRegion0(left, top, right, bottom);
			return;
		}

		RectF rectF = mBackgrounds.get(mBackgrounds.size() - 1);
		if (rectF.right == left) {
			rectF.right = right;
			rectF.top = Math.min(rectF.top, top);
			rectF.bottom = Math.max(rectF.bottom, bottom);
		} else {
			appendRegion0(left, top, right, bottom);
		}
	}

	private void appendRegion0(float left, float top, float right, float bottom) {
		RectF rectF = new RectF(left, top, right, bottom);
		mBackgrounds.add(rectF);
	}

	@RestrictTo(LIBRARY)
	public void prependRegion(RectF rectF) {
		mBackgrounds.add(0, rectF);
	}

	@RestrictTo(LIBRARY)
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

	public Paragraph getParagraph() {
		return mParagraph;
	}

	private static final List<Object> EMPTY = Collections.unmodifiableList(new ArrayList<>());

	/**
	 * @return 因为排版的时候单词会被拆分，因此会导致用户设置的tag重复，这个方法内部还需要去去重，但是无法对空tag去重，所以忽略空tag
	 */
	@NonNull
	@MainThread
	public List<Object> getSelectedTags() {
		List<Object> list = getSelectedTags0(mParagraph);
		if (list == null) {
			return EMPTY;
		}
		return list;
	}

	private List<Object> getSelectedTags0(Paragraph paragraph) {
		Layout layout = paragraph.getLayout();
		if (paragraph.isRecycled() || layout == null || layout.isRecycled()) {
			return null;
		}

		try {
			mGetSelectedTagVisitor.visit(paragraph);
			return mGetSelectedTagVisitor.tags;
		} catch (Throwable ignored) {
			return null;
		} finally {
			mGetSelectedTagVisitor.tags = null;
			mGetSelectedTagVisitor.selection = null;
		}
	}

	@Override
	protected void onRecycle() {
		mSet.clear();
		mBackgrounds.clear();
		mStyle = null;
		mId = 0;
		mParagraph = null;
		mFirst = mLast = null;
		mType = null;
		POOL.release(this);
	}

	/**
	 * @param styles 渲染样式
	 * @return selection selection
	 */
	@RestrictTo(LIBRARY)
	public static ParagraphSelection obtain(Selection.Type type, @NonNull Selection.Styles styles, Paragraph paragraph) {
		ParagraphSelection paragraphSelection = POOL.acquire();
		if (paragraphSelection == null) {
			paragraphSelection = new ParagraphSelection();
		}

		paragraphSelection.mType = type;
		paragraphSelection.mInternalTextStyle.reset(styles);
		paragraphSelection.reset(paragraphSelection.mInternalTextStyle);
		paragraphSelection.mParagraph = paragraph;
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

	@RestrictTo(LIBRARY)
	@VisibleForTesting
	public List<RectF> getBackgrounds() {
		return mBackgrounds;
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

	public void drawBackground(TexasCanvas canvas, TexasPaint paint, RenderOption option) {
		mStyle.update();

		float radius = option.getSelectedBackgroundRoundRadius();
		paint.setColor(mStyle.mBackgroundColor);
		if (radius <= 0) {
			for (RectF rectF : mBackgrounds) {
				canvas.drawRect(rectF.left, rectF.top, rectF.right, rectF.bottom, paint);
			}
			return;
		}

		for (RectF rectF : mBackgrounds) {
			canvas.drawRoundRect(rectF.left, rectF.top, rectF.right, rectF.bottom, radius, radius, paint);
		}
	}

	private final GetSelectedTagVisitor mGetSelectedTagVisitor = new GetSelectedTagVisitor();

	private class GetSelectedTagVisitor extends ParagraphVisitor {
		public List<Object> tags;
		public ParagraphSelection selection;

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {
			tags = new ArrayList<>();
			selection = paragraph.getSelection(mType);
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

	@VisibleForTesting
	@RestrictTo(LIBRARY)
	String toString(Paragraph paragraph) {
		Layout layout = paragraph.getLayout();
		if (paragraph.isRecycled() || layout == null || layout.isRecycled()) {
			return "";
		}

		try {
			StringBuilder builder = new StringBuilder();
			ParagraphVisitor paragraphVisitor = new ParagraphVisitor() {
				@Override
				protected void onVisitParagraphStart(Paragraph paragraph) {

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
					if (isSelected(box)) {
						builder.append(box).append(", ");
					}
				}
			};
			paragraphVisitor.visit(paragraph);
			return builder.toString();
		} catch (Throwable ignored) {
			return "";
		}
	}

	private final InternalSelectionStyle mInternalTextStyle = new InternalSelectionStyle();

	private static class InternalSelectionStyle extends TextStyle {
		private int mTextColor = 0;
		private Selection.Styles mStyles;
		private int mBackgroundColor = 0;

		public void reset(Selection.Styles styles) {
			mStyles = styles;
		}

		public void update() {
			mTextColor = mStyles.getTextColor();
			mBackgroundColor = mStyles.getBackgroundColor();
		}

		@Override
		public void update(@NonNull TexasPaint textPaint, @Nullable Object tag) {
			textPaint.setColor(mTextColor);
		}
	}
}
