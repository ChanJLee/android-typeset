package me.chan.texas.renderer.selection.visitor;

import android.graphics.RectF;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.text.Paragraph;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.TextStyles;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.DrawableBox;
import me.chan.texas.text.layout.Line;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class SelectedVisitor extends ParagraphVisitor {

	protected ParagraphSelection mSelection;

	// 绘制背景用
	private RectF mRectF;
	private float mLastLineBottom;
	private float mLastLineTop;

	protected RenderOption mRenderOption;

	/**
	 * @param isLongClicked 是否长按
	 * @param renderOption  render option
	 */
	public void reset(boolean isLongClicked, RenderOption renderOption) {
		if (mSelection != null) {
			throw new IllegalStateException("missing call clear before reuse visitor?");
		}
		mSelection = ParagraphSelection.obtain(isLongClicked);
		mRenderOption = renderOption;
	}

	/**
	 * @param textStyles   text styles
	 * @param renderOption render option
	 */
	public void reset(TextStyles textStyles, RenderOption renderOption) {
		if (mSelection != null) {
			throw new IllegalStateException("missing call clear before reuse visitor?");
		}
		mSelection = ParagraphSelection.obtain(textStyles);
		mRenderOption = renderOption;
	}

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {

	}

	@Override
	final public void visit(Paragraph paragraph) throws VisitException {
		super.visit(paragraph);
	}

	public void startVisit(Paragraph paragraph) throws VisitException {
		ParagraphSelection prev = paragraph.getSelection();
		paragraph.setSelection(mSelection);
		super.visit(paragraph);
		if (prev != null) {
			prev.recycle();
		}
	}

	@Override
	public void onVisitParagraphEnd(Paragraph paragraph) {
		ParagraphSelection selection = paragraph.getSelection();
		if (selection != mSelection) {
			mSelection.recycle();
		}
	}

	@CallSuper
	public void clear() {
		mSelection = null;
		mLastLineBottom = mLastLineTop = -1;
		mRectF = null;
	}

	@Override
	public void onVisitLineStart(Line line, float bottomX, float bottomY) {
		mLastLineBottom = bottomY;
		mLastLineTop = bottomY - line.getLineHeight();
	}

	@Override
	public void onVisitLineEnd(Line line, float x, float y) {
		if (mRectF != null) {
			mSelection.appendRegion(mRectF);
			mRectF = null;
		}
	}

	@Override
	public void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
		if (selected(box, inner, outer)) {
			if (!(box instanceof DrawableBox) || includeSelectNonTextBoxRegion()) {
				appendRect(outer);
			} else {
				closeRect();
			}
			mSelection.appendBox(box);
		} else {
			closeRect();
		}
	}

	/**
	 * @return 是否可选中非文本的box
	 */
	protected boolean includeSelectNonTextBoxRegion() {
		return mRenderOption.isDrawEmoticonSelection();
	}

	private void appendRect(RectF rectF) {
		if (mRectF == null) {
			mRectF = new RectF(rectF.left, mLastLineTop, rectF.right, mLastLineBottom);
		}
		mRectF.right = rectF.right;
	}

	private void closeRect() {
		if (mRectF != null) {
			mSelection.appendRegion(mRectF);
			mRectF = null;
		}
	}

	protected abstract boolean selected(Box box, RectF inner, RectF outer);
}