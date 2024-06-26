package me.chan.texas.renderer.ui.rv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import me.chan.texas.renderer.TouchEvent;

@RestrictTo(LIBRARY)
@SuppressLint("ViewConstructor")
public class TexasRecyclerView extends RecyclerView {
	private SingleClickOnTouchListener mTeOnTouchListener;
	private OnClickedListener mOnClickedListener;
	private ScrollAction mScrollAction;
	private final TexasLinearLayoutManager mTexasLinearLayoutManager;

	public TexasRecyclerView(@NonNull Context context, TexasLinearLayoutManager texasLinearLayoutManager) {
		super(context);
		init(context);
		mTexasLinearLayoutManager = texasLinearLayoutManager;
	}

	private void init(Context context) {
		mTeOnTouchListener = new SingleClickOnTouchListener(context) {
			@Override
			protected void onClicked(MotionEvent event) {
				if (mOnClickedListener != null) {
					mOnClickedListener.onClicked(TouchEvent.obtain(TexasRecyclerView.this, event));
				}
			}
		};

		ItemAnimator itemAnimator = getItemAnimator();
		if (itemAnimator instanceof SimpleItemAnimator) {
			SimpleItemAnimator simpleItemAnimator = (SimpleItemAnimator) itemAnimator;
			simpleItemAnimator.setSupportsChangeAnimations(false);
			simpleItemAnimator.setChangeDuration(0);
		}
	}

	public void scrollToPosition(int position, boolean smooth, int offset) {
		if (position < 0) {
			return;
		}

		if (!smooth) {
			try {
				mTexasLinearLayoutManager.setOffset(offset);
				scrollToPosition(position);
			} finally {
				mTexasLinearLayoutManager.setOffset(0);
			}
			return;
		}

		if (mScrollAction == null) {
			mScrollAction = new ScrollAction();
		}
		mScrollAction.position = position;
		mScrollAction.offset = offset;
		post(mScrollAction);
	}

	public void getChildLocations(View child, int[] locations) {
		locations[0] = child.getLeft();
		locations[1] = child.getTop();
	}

	private class ScrollAction implements Runnable {
		public int position;
		public int offset;

		@Override
		public void run() {
			Adapter<?> adapter = getAdapter();
			if (adapter == null ||
					position < 0 || position >= adapter.getItemCount()) {
				return;
			}

			try {
				mTexasLinearLayoutManager.setOffset(offset);
				smoothScrollToPosition(position);
			} finally {
				mTexasLinearLayoutManager.setOffset(0);
			}
		}
	}

	private boolean mDisallowHandleTouchEvent = false;

	public void disallowHandleTouchEvent() {
		mDisallowHandleTouchEvent = true;
	}

	public void allowHandleTouchEvent() {
		mDisallowHandleTouchEvent = false;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (mDisallowHandleTouchEvent) {
			return false;
		}

		mTeOnTouchListener.onTouch(this, e);
		return super.onTouchEvent(e);
	}

	public void setOnClickedListener(OnClickedListener onClickedListener) {
		mOnClickedListener = onClickedListener;
	}

	public boolean isDisallowHandleTouchEvent() {
		return mDisallowHandleTouchEvent;
	}

	public interface OnClickedListener {
		void onClicked(TouchEvent event);
	}
}
