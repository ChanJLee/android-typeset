package me.chan.texas.renderer.selection;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Canvas;
import android.graphics.Paint;
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
import me.chan.texas.text.BatchDrawAppearance;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextStyle;
import me.chan.texas.text.TextStyles;
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

	private TextStyles mStyles;
	private int mId;
	private final BitBucket mSet = new BitBucket(128);

	private ParagraphSelection() {
	}

	private void reset(TextStyles styles) {
		mId = UUID.incrementAndGet();
		mStyles = styles;
	}

	public TextStyles getStyles() {
		return mStyles;
	}

	public int getId() {
		return mId;
	}

	private final BoxLocation mFirst = new BoxLocation();
	private final BoxLocation mLast = new BoxLocation();

	@RestrictTo(LIBRARY)
	public void prependBox(Box box) {
		mSet.set(box.getSeq(), true);

		if (mLast.box == null) {
			mLast.box = box;
		}

		mFirst.box = box;
	}

	@RestrictTo(LIBRARY)
	public void appendRegion(RectF rectF) {
		mLast.rect.set(rectF);
	}

	@RestrictTo(LIBRARY)
	public void prependRegion(RectF rectF) {
		mFirst.rect.set(rectF);
	}

	@RestrictTo(LIBRARY)
	public void appendBox(Box box) {
		mSet.set(box.getSeq(), true);

		if (mFirst.box == null) {
			mFirst.box = box;
		}

		mLast.box = box;
	}

	/**
	 * @return 选中区域是空的
	 */
	public boolean isSelectedRegionEmpty() {
		return mFirst.rect.isEmpty() && mLast.rect.isEmpty();
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
		mFirst.clear();
		mLast.clear();
		mStyles = null;
		mId = 0;
		POOL.release(this);
	}

	@RestrictTo(LIBRARY)
	public static ParagraphSelection obtain(boolean isLongClicked) {
		ParagraphSelection paragraphSelection = POOL.acquire();
		if (paragraphSelection == null) {
			paragraphSelection = new ParagraphSelection();
		}

		paragraphSelection.reuse();
		paragraphSelection.mInternalTextStyles.reset(isLongClicked);
		paragraphSelection.reset(paragraphSelection.mInternalTextStyles);
		return paragraphSelection;
	}

	/**
	 * @param textStyles 渲染样式
	 * @return selection selection
	 */
	@RestrictTo(LIBRARY)
	public static ParagraphSelection obtain(TextStyles textStyles) {
		ParagraphSelection paragraphSelection = POOL.acquire();
		if (paragraphSelection == null) {
			paragraphSelection = new ParagraphSelection();
		}

		paragraphSelection.reuse();
		paragraphSelection.reset(textStyles);
		return paragraphSelection;
	}

	@Nullable
	public RectF getFirstRegion() {
		return mFirst.rect;
	}

	@Nullable
	public RectF getLastRegion() {
		return mLast.rect;
	}

	@Nullable
	public Box getFirstBox() {
		return mFirst.box;
	}

	@Nullable
	public Box getLastBox() {
		return mLast.box;
	}

	public boolean isEmpty() {
		return mFirst.box == null;
	}

	@RestrictTo(LIBRARY)
	public boolean isSelected(Box box) {
		return mSet.get(box.getSeq());
	}

	public void clear() {
		mSet.clear();
		mFirst.clear();
		mLast.clear();
	}

	public void updateStyle(RenderOption option) {
		mStyles.update(option);
	}

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
			setBackground(new BatchDrawAppearance() {
				@Override
				protected void onDraw(Canvas canvas, Paint paint, RectF inner, RectF outer) {
					paint.setColor(mBackgroundColor);
					canvas.drawRoundRect(inner, mRound, mRound, paint);
				}

				@Override
				protected boolean isSameGroup(RendererContext context) {
					return selection.isSelected(context.getCurrentBoxMetaInfo().box);
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

	private static final class BoxLocation {
		Box box;
		final RectF rect = new RectF();

		void clear() {
			box = null;
			rect.set(0, 0, 0, 0);
		}
	}
}
