package me.chan.texas.text;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

public abstract class ViewSegment extends Segment {

	private View mView;
	private FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);

	public final void attach(LayoutInflater layoutInflater, FrameLayout frameLayout) {
		View view = getView(layoutInflater, frameLayout);
		ViewParent viewParent = view.getParent();
		if (viewParent instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) viewParent;
			viewGroup.removeView(view);
		}
		frameLayout.addView(view, mLayoutParams);
	}

	private View getView(LayoutInflater layoutInflater, FrameLayout parent) {
		if (mView != null) {
			return mView;
		}

		mView = onCreateView(layoutInflater, parent);
		return mView;
	}

	protected abstract View onCreateView(LayoutInflater layoutInflater, ViewGroup parent);

	public final void render() {
		onRender();
	}

	protected abstract void onRender();

	@Override
	public final void recycle() {
		if (isRecycled()) {
			return;
		}
		super.recycle();
	}

	@Override
	public final boolean isRecycled() {
		return super.isRecycled();
	}

	@Override
	public final void reuse() {
		super.reuse();
	}
}
