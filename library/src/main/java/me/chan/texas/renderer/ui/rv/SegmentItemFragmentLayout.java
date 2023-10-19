package me.chan.texas.renderer.ui.rv;

import android.annotation.SuppressLint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.RestrictTo;

@SuppressLint("ViewConstructor")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SegmentItemFragmentLayout extends FrameLayout {

	private final TexasRecyclerView mRecyclerView;
	private OnClickedListener mOnClickedListener;
	private GestureDetector mGestureDetector;
	private EventListener mEventListener;

	public SegmentItemFragmentLayout(TexasRecyclerView recyclerView) {
		super(recyclerView.getContext());

		mRecyclerView = recyclerView;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mRecyclerView.isDisallowHandleTouchEvent()) {
			return false;
		}

		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getContext(), mEventListener = new EventListener());
		}
		mGestureDetector.setIsLongpressEnabled(true);
		mGestureDetector.setOnDoubleTapListener(mEventListener);
		return mGestureDetector.onTouchEvent(event);
	}

	public void setOnClickedListener(OnClickedListener onClickedListener) {
		mOnClickedListener = onClickedListener;
	}

	public View getContent() {
		return getChildAt(0);
	}

	public interface OnClickedListener {
		void onClicked(float x, float y);

		void onDoubleClicked(float x, float y);
	}

	private class EventListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
		@Override
		public boolean onDown(MotionEvent e) {
			/* do nothing */
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			/* do nothing */
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			/* do nothing */
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			/* do nothing */
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			/* do nothing */
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			/* do nothing */
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			mOnClickedListener.onClicked(e.getRawX(), e.getRawY());
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			mOnClickedListener.onDoubleClicked(e.getRawX(), e.getRawY());
			return true;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return true;
		}
	}
}
