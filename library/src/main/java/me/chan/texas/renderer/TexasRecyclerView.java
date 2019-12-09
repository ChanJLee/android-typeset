package me.chan.texas.renderer;

import android.content.Context;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.text.OnClickedListener;

@Hidden
class TexasRecyclerView extends RecyclerView {
	private SingleClickOnTouchListener mTeOnTouchListener;
	private OnClickedListener mOnClickedListener;
	private ScrollAction mScrollAction;

	public TexasRecyclerView(@NonNull Context context) {
		super(context);
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

		ItemAnimator itemAnimator = getItemAnimator();
		if (itemAnimator instanceof SimpleItemAnimator) {
			SimpleItemAnimator simpleItemAnimator = (SimpleItemAnimator) itemAnimator;
			simpleItemAnimator.setSupportsChangeAnimations(false);
		}
	}

	public void scrollToPosition(int position) {
		if (position <= 0) {
			return;
		}

		if (mScrollAction == null) {
			mScrollAction = new ScrollAction();
		}
		mScrollAction.position = position;
		post(mScrollAction);
	}

	private class ScrollAction implements Runnable {
		public int position;

		@Override
		public void run() {
			Adapter<?> adapter = getAdapter();
			if (adapter == null ||
					position < 0 || position >= adapter.getItemCount()) {
				return;
			}

			smoothScrollToPosition(position);
		}
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
