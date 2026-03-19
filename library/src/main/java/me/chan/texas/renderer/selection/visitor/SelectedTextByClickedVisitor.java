package me.chan.texas.renderer.selection.visitor;

import me.chan.texas.misc.RectF;

import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.SpanPredicate;
import me.chan.texas.text.layout.Box;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelectedTextByClickedVisitor extends SelectedVisitor {

	private SpanPredicate mPredicate;
	private Box mClickedTag;
	private boolean mHandled = false;

	public void setPredicate(SpanPredicate predicate, Box clicked) {
		mPredicate = predicate;
		mClickedTag = clicked;
	}

	@Override
	protected boolean selected(Box box, RectF inner, RectF outer) {
		boolean result = mPredicate.accept(mClickedTag, box);
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
