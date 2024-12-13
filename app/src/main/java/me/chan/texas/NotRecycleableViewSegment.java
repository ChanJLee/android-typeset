package me.chan.texas;

import android.util.Log;
import android.view.View;

import me.chan.texas.debug.R;
import me.chan.texas.text.ViewSegment;

public class NotRecycleableViewSegment extends ViewSegment {
	public NotRecycleableViewSegment() {
		super(R.layout.item_test_not_recycle, true);
	}

	private View mLastView;

	@Override
	protected void onRender(View view) {
		Log.d("chan_debug", "onRender: " + this + " " + view);
		if (mLastView != null && mLastView != view) {
			throw new IllegalStateException("View is not recycled");
		}
		mLastView = view;
	}
}
