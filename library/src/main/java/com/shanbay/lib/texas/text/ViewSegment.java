package com.shanbay.lib.texas.text;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shanbay.lib.texas.annotations.Hidden;

/**
 * 用户自定义视图片段
 */
public abstract class ViewSegment extends Segment {

	private View mView;
	private FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);

	@Hidden
	public final void attach(LayoutInflater layoutInflater, FrameLayout frameLayout) {
		frameLayout.removeAllViews();
		View view = getView(layoutInflater, frameLayout);
		ViewGroup viewGroup = (ViewGroup) view.getParent();
		if (viewGroup != null) {
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

	/**
	 * @param layoutInflater layout inflater
	 * @param parent         parent
	 * @return 当前视图view
	 */
	protected abstract View onCreateView(LayoutInflater layoutInflater, ViewGroup parent);

	public final void render() {
		onRender();
	}

	/**
	 * 开始渲染
	 */
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
