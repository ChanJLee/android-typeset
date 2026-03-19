package me.chan.texas.renderer.selection.visitor;

import me.chan.texas.misc.RectF;

import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.SpanPredicate;
import me.chan.texas.text.layout.Span;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelectedTextByClickedVisitor extends SelectedVisitor {

	private SpanPredicate mPredicate;
	private Span mClickedTag;
	private boolean mHandled = false;

	public void setPredicate(SpanPredicate predicate, Span clicked) {
		mPredicate = predicate;
		mClickedTag = clicked;
	}

	@Override
	protected boolean selected(Span span, RectF inner, RectF outer) {
		boolean result = mPredicate.accept(mClickedTag, span);
		if (result) {
			mHandled = true;
		}
		return result;
	}

	@Override
	public void clear() {
		super.clear();
		mClickedTag = null;
		mPredicate = null;
		mHandled = false;
	}

	public boolean isHandled() {
		return mHandled;
	}
}
