package me.chan.texas.renderer.selection;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.misc.BitBucket;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.Appearance;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextStyle;
import me.chan.texas.text.TextStyles;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;

/**
 * 文本选中区域
 */
public class ParagraphSelection extends DefaultRecyclable {
	private static final ObjectPool<ParagraphSelection> POOL = new ObjectPool<>(32);
	private static final AtomicInteger UUID = new AtomicInteger(0);

	private TextStyles mStyles;
	private int mId;
	private final BitBucket mSet = new BitBucket(128);
	private final List<RectF> mBackgrounds = new ArrayList<>();

	private Paragraph mParagraph;

	private ParagraphSelection() {
	}

	private void reset(TextStyles styles) {
		mId = UUID.incrementAndGet();
		mStyles = styles;
	}

	@RestrictTo(LIBRARY)
	public TextStyles getStyles() {
		return mStyles;
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
	public void appendRegion(RectF rectF) {
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

	@Nullable
	@MainThread
	public List<Object> getSelectedTags() {
		return getSelectedTags(mParagraph);
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
		mBackgrounds.clear();
		mStyles = null;
		mId = 0;
		mParagraph = null;
		POOL.release(this);
	}

	@RestrictTo(LIBRARY)
	public static ParagraphSelection obtain(boolean isLongClicked, Paragraph paragraph) {
		ParagraphSelection paragraphSelection = POOL.acquire();
		if (paragraphSelection == null) {
			paragraphSelection = new ParagraphSelection();
		}

		paragraphSelection.reuse();
		paragraphSelection.mInternalTextStyles.reset(isLongClicked);
		paragraphSelection.reset(paragraphSelection.mInternalTextStyles);
		paragraphSelection.mParagraph = paragraph;
		return paragraphSelection;
	}

	/**
	 * @param styles 渲染样式
	 * @return selection selection
	 */
	@RestrictTo(LIBRARY)
	public static ParagraphSelection obtain(Selection.Styles styles, Paragraph paragraph) {
		if (styles == null) {
			return obtain(true, paragraph);
		}

		ParagraphSelection paragraphSelection = POOL.acquire();
		if (paragraphSelection == null) {
			paragraphSelection = new ParagraphSelection();
		}

		paragraphSelection.reset(new SelectionStyles(paragraphSelection, styles.getTextColor(), styles.getBackgroundColor()));
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

	@RestrictTo(LIBRARY)
	public void updateStyle(RenderOption option) {
		mStyles.update(option);
	}

	@RestrictTo(LIBRARY)
	public interface Style {
		void update(ParagraphSelection paragraphSelection, RenderOption renderOption);
	}

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

	private final InternalTextStyles mInternalTextStyles = new InternalTextStyles(this);

	private static class InternalTextStyles extends TextStyles {
		private int mTextColor = 0;
		private int mBackgroundColor = 0;
		private float mRound;

		private boolean mIsLongClicked;

		public void reset(boolean isLongClicked) {
			mIsLongClicked = isLongClicked;
		}

		public InternalTextStyles(ParagraphSelection selection) {
			setTextStyle(new TextStyle() {
				@Override
				public void update(@NonNull TextPaint textPaint, @Nullable Object tag) {
					textPaint.setColor(mTextColor);
				}
			});
			setBackground(new SelectionAppearance(selection) {
				@Override
				protected void onDraw(Canvas canvas, Paint paint) {
					paint.setColor(mBackgroundColor);
					canvas.drawRoundRect(mInner, mRound, mRound, paint);
				}
			});
		}

		@Override
		public void update(RenderOption option) {
			mRound = option.getSelectedBackgroundRoundRadius();
			if (mIsLongClicked) {
				mTextColor = option.getSelectedByLongClickTextColor();
				mBackgroundColor = option.getSelectedByLongClickBackgroundColor();
				return;
			}

			mTextColor = option.getSelectedTextColor();
			mBackgroundColor = option.getSelectedBackgroundColor();
		}
	}

	private static class SelectionStyles extends TextStyles {
		private float mRound;

		public SelectionStyles(ParagraphSelection selection, int textColor, int backgroundColor) {
			setTextStyle(new TextStyle() {
				@Override
				public void update(@NonNull TextPaint textPaint, @Nullable Object tag) {
					textPaint.setColor(textColor);
				}
			});

			if (backgroundColor != Color.TRANSPARENT) {
				setBackground(new SelectionAppearance(selection) {

					@Override
					protected void onDraw(Canvas canvas, Paint paint) {
						paint.setColor(backgroundColor);
						canvas.drawRoundRect(mInner, mRound, mRound, paint);
					}
				});
			}
		}

		@Override
		public void update(RenderOption option) {
			mRound = option.getSelectedBackgroundRoundRadius();
		}
	}

	@RestrictTo(LIBRARY)
	public static abstract class SelectionAppearance extends Appearance {

		protected final RectF mInner = new RectF();

		private boolean mShouldReset = false;

		private final ParagraphSelection mSelection;

		public SelectionAppearance(ParagraphSelection selection) {
			mSelection = selection;
		}

		@Override
		public final void draw(Canvas canvas, Paint paint, RectF inner, RectF outer, RendererContext context) {
			if (context.checkLocation(RendererContext.LOCATION_LINE_START)) {
				mShouldReset = true;
			}

			boolean isSelected = mSelection.isSelected(context.getCurrentBoxMetaInfo().box);
			if (isSelected) {
				if (mShouldReset) {
					mInner.set(inner);
					mShouldReset = false;
				} else {
					mInner.right = inner.right;
					mInner.top = Math.max(mInner.top, inner.top);
					mInner.bottom = Math.max(mInner.bottom, inner.bottom);
				}
			}

			if (context.checkLocation(RendererContext.LOCATION_LINE_END) || !isSelected) {
				if (!mInner.isEmpty()) {
					onDraw(canvas, paint);
				}
				mShouldReset = true;
				mInner.left = mInner.top = mInner.right = mInner.bottom = 0;
			}
		}

		protected abstract void onDraw(Canvas canvas, Paint paint);
	}
}
