package me.chan.te.renderer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import me.chan.te.text.OnClickedListener;

public class TeRecyclerView extends RecyclerView {
	private TeOnTouchListener mTeOnTouchListener;
	private OnClickedListener mOnClickedListener;

	public TeRecyclerView(@NonNull Context context) {
		super(context);
		init(context);
	}

	public TeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mTeOnTouchListener = new TeOnTouchListener(context) {
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
