package me.chan.te.renderer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import me.chan.te.text.OnClickedListener;

class RecyclerViewInternal extends RecyclerView {
	private SingleClickOnTouchListener mTeOnTouchListener;
	private OnClickedListener mOnClickedListener;

	public RecyclerViewInternal(@NonNull Context context) {
		super(context);
		init(context);
	}

	public RecyclerViewInternal(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RecyclerViewInternal(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mTeOnTouchListener = new SingleClickOnTouchListener(context) {
			@Override
			protected void onClicked(float x, float y) {
				if (mOnClickedListener != null) {
					mOnClickedListener.onClicked(x, y);
				}
			}
		};
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		mTeOnTouchListener.onTouch(this, e);
		return super.onTouchEvent(e);
	}

	public void setOnClickedListener(OnClickedListener onClickedListener) {
		mOnClickedListener = onClickedListener;
	}
}
