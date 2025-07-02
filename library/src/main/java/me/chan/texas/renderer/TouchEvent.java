package me.chan.texas.renderer;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RestrictTo;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

public class TouchEvent extends DefaultRecyclable {
	private static final ObjectPool<TouchEvent> POOL = new ObjectPool<>(36);

	private float mX;
	private float mY;
	private float mRawX;
	private float mRawY;

	private View mSourceView;

	private TouchEvent() {
	}

	@Override
	protected void onRecycle() {
		mX = mY = mRawX = mRawY = 0;
		mSourceView = null;
		POOL.release(this);
	}

	
	public float getX() {
		return mX;
	}

	
	public float getY() {
		return mY;
	}

	
	public float getRawX() {
		return mRawX;
	}

	
	public float getRawY() {
		return mRawY;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void adjust(ViewGroup root) {
		if (mSourceView == null) {
			throw new IllegalStateException("source view is null");
		}

		ViewGroup parent = (ViewGroup) mSourceView.getParent();
		if (parent == null) {
			return;
		}

		adjust0(parent);
		while (parent != root) {
			mSourceView = parent;
			parent = (ViewGroup) parent.getParent();
			if (parent == null) {
				break;
			}

			adjust0(parent);
		}

		mSourceView = null;
	}

	private void adjust0(ViewGroup parent) {
		float offsetX = parent.getScrollX() - mSourceView.getLeft();
		float offsetY = parent.getScrollY() - mSourceView.getTop();
		mX -= offsetX;
		mY -= offsetY;
	}

	public static TouchEvent obtain(View source, MotionEvent event) {
		return obtain(source, event.getX(), event.getY(), event.getRawX(), event.getRawY());
	}

	public static TouchEvent obtain(View source, float x, float y, float rawX, float rawY) {
		TouchEvent event = POOL.acquire();
		if (event == null) {
			event = new TouchEvent();
		}

		event.mX = x;
		event.mY = y;
		event.mRawX = rawX;
		event.mRawY = rawY;
		event.mSourceView = source;
		event.reuse();
		return event;
	}
}
