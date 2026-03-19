package me.chan.texas.renderer.selection;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Paint;

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
import me.chan.texas.renderer.CompositeRectDrawable;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.renderer.RectDrawable;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextStyle;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextBox;

/**
 * 文本选中区域
 */
public class ParagraphSelection extends DefaultRecyclable {
	private static final ObjectPool<ParagraphSelection> POOL = new ObjectPool<>(32);
	private static final AtomicInteger UUID = new AtomicInteger(0);

	private InternalSelectionStyle mStyle;
	private int mId;
	private final BitBucket mSet = new BitBucket(128);
	private final RectDrawable mDrawable = new RectDrawable();
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

	@RestrictTo(LIBRARY)
	public void appendRegion(CompositeRectDrawable drawable) {
		mDrawable.append(drawable);
	}

	@RestrictTo(LIBRARY)
	public void prependRegion(CompositeRectDrawable drawable) {
		mDrawable.prepend(drawable);
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
		return mDrawable.isEmpty();
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
		mDrawable.clear();
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
		if (mDrawable.isEmpty()) {
			return null;
		}

		CompositeRectDrawable drawable = mDrawable.get(0);
		if (drawable.isEmpty()) {
			return null;
		}

		return drawable.get(0);
	}

	@Nullable
	public RectF getLastRegion() {
		if (mDrawable.isEmpty()) {
			return null;
		}

		CompositeRectDrawable drawable = mDrawable.get(mDrawable.size() - 1);
		if (drawable.isEmpty()) {
			return null;
		}

		return drawable.get(drawable.size() - 1);
	}

	@RestrictTo(LIBRARY)
	@VisibleForTesting
	public List<RectF> getBackgrounds() {
		List<RectF> background = new ArrayList<>();
		for (CompositeRectDrawable drawable : mDrawable.getBackgrounds()) {
			background.addAll(drawable.getBackgrounds());
		}
		return background;
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
		mDrawable.clear();
	}

	public void drawBackground(TexasCanvas canvas, TexasPaint paint, RenderOption option) {
		mStyle.update();

		float radius = option.getSelectedBackgroundRoundRadius();
		paint.setColor(mStyle.mBackgroundColor);
		if (radius <= 0) {
			mDrawable.drawRect(canvas, paint);
			return;
		}

		mDrawable.drawRoundRect(canvas, paint, radius);
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
		private float mFakeBoldFactor = 0f;

		public void reset(Selection.Styles styles) {
			mStyles = styles;
		}

		public void update() {
			mTextColor = mStyles.getTextColor();
			mBackgroundColor = mStyles.getBackgroundColor();
			mFakeBoldFactor = mStyles.getFakeBoldFactor();
		}

		@Override
		public void update(@NonNull TexasPaint textPaint, @NonNull TextBox box) {
			textPaint.setColor(mTextColor);
			if (mFakeBoldFactor > 0f) {
				textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				textPaint.setStrokeWidth(textPaint.getTextSize() * mFakeBoldFactor);
			}
		}
	}
}
