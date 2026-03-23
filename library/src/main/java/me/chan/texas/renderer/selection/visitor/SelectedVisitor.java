package me.chan.texas.renderer.selection.visitor;

import me.chan.texas.misc.RectF;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.CompositeRectDrawable;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.text.Paragraph;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.layout.Span;
import me.chan.texas.text.layout.DrawableSpan;
import me.chan.texas.text.layout.Line;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class SelectedVisitor extends ParagraphVisitor {

	protected ParagraphSelection mSelection;

	// 绘制背景用
	private float mLastLineBottom;
	private float mLastLineTop;
	protected Selection.Type mType;
	protected RenderOption mRenderOption;
	private CompositeRectDrawable mCompositeRectDrawable;

	/**
	 * @param styles       styles
	 * @param renderOption render option
	 */
	public void reset(Selection.Type type, Selection.Styles styles, Paragraph paragraph, RenderOption renderOption) {
		if (mSelection != null) {
			throw new IllegalStateException("missing call clear before reuse visitor?");
		}
		mType = type;
		mSelection = onCreateSelection(type, styles, paragraph);
		mRenderOption = renderOption;
	}

	protected ParagraphSelection onCreateSelection(Selection.Type type, Selection.Styles styles, Paragraph paragraph) {
		return ParagraphSelection.obtain(type, styles, paragraph);
	}

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {
		/* NOOP */
	}

	@Override
	final public void visit(Paragraph paragraph) throws VisitException {
		throw new UnsupportedOperationException("use startVisit instead");
	}

	public void startVisit(Paragraph paragraph) throws VisitException {
		ParagraphSelection prev = paragraph.getSelection(mType);
		paragraph.setSelection(mType, null);
		super.visit(paragraph);
		if (mSelection.isEmpty()) {
			mSelection.recycle();
		} else {
			paragraph.setSelection(mType, mSelection);
		}
		if (prev != null && prev != mSelection) {
			prev.recycle();
		}
	}

	@Override
	public void onVisitParagraphEnd(Paragraph paragraph) {
		/* NOOP */
	}

	@CallSuper
	public void clear() {
		mSelection = null;
		mLastLineBottom = mLastLineTop = -1;
	}

	@Override
	public void onVisitLineStart(Line line, float bottomX, float bottomY) {
		mLastLineBottom = bottomY;
		mLastLineTop = bottomY - line.getLineHeight();
		mCompositeRectDrawable = new CompositeRectDrawable();
	}

	@Override
	public void onVisitLineEnd(Line line, float x, float y) {
		if (mCompositeRectDrawable.isEmpty()) {
			mCompositeRectDrawable = null;
			return;
		}

		mSelection.appendRegion(mCompositeRectDrawable);
		mCompositeRectDrawable = null;
	}

	@Override
	public void onVisitBox(Span span, RectF inner, RectF outer, @NonNull RendererContext context) {
		if (selected(span, inner, outer)) {
			if (!(span instanceof DrawableSpan) || includeSelectNonTextBoxRegion()) {
				mCompositeRectDrawable.append(outer.left, mLastLineTop, outer.right, mLastLineBottom);
			}
			mSelection.appendSpan(span);
		}
	}

	/**
	 * @return 是否可选中非文本的box
	 */
	protected boolean includeSelectNonTextBoxRegion() {
		return mRenderOption.isDrawEmoticonSelection();
	}

	protected abstract boolean selected(Span span, RectF inner, RectF outer);
}