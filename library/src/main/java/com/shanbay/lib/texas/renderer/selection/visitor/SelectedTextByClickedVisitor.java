package com.shanbay.lib.texas.renderer.selection.visitor;

import android.graphics.RectF;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.renderer.OnSpanClickedPredicate;
import com.shanbay.lib.texas.text.layout.Box;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelectedTextByClickedVisitor extends SelectedVisitor {

	private OnSpanClickedPredicate mPredicate;
	private Object mClickedTag;
	private boolean mHandled = false;

	public void setPredicate(OnSpanClickedPredicate predicate, Object clicked) {
		mPredicate = predicate;
		mClickedTag = clicked;
	}

	@Override
	protected boolean selected(Box box, RectF inner, RectF outer) {
		boolean result = mPredicate.apply(mClickedTag, box.getTag());
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
