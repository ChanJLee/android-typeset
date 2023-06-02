package com.shanbay.lib.texas.renderer.ui.rv;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.RestrictTo;

@SuppressLint("ViewConstructor")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SegmentItemFragmentLayout extends FrameLayout {

	private final TexasRecyclerView mRecyclerView;
	private final SingleClickOnTouchListener mTeOnTouchListener;
	private OnClickedListener mOnClickedListener;

	public SegmentItemFragmentLayout(TexasRecyclerView recyclerView) {
		super(recyclerView.getContext());

		mRecyclerView = recyclerView;
		mTeOnTouchListener = new SingleClickOnTouchListener(recyclerView.getContext()) {
			@Override
			protected void onClicked(float x, float y) {
				if (mOnClickedListener != null) {
					mOnClickedListener.onClicked(x, y);
				}
			}
		};
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mRecyclerView.isDisallowHandleTouchEvent()) {
			return false;
		}

		return mTeOnTouchListener.onTouch(this, event);
	}

	public void setOnClickedListener(OnClickedListener onClickedListener) {
		mOnClickedListener = onClickedListener;
	}

	public View getContent() {
		return getChildAt(0);
	}

	public interface OnClickedListener {
		void onClicked(float x, float y);
	}
}