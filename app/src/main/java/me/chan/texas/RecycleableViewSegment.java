package me.chan.texas;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import me.chan.texas.debug.R;
import me.chan.texas.text.ViewSegment;

public class RecycleableViewSegment extends ViewSegment {
	private final String text;

	public RecycleableViewSegment(String text) {
		super(R.layout.item_test_not_recycle);
		this.text = text;
	}

	private View mLastView;

	@Override
	protected void onRender(View view) {
		Log.d("chan_debug", "onRender: " + this + " " + view + " " + text);
		TextView current = view.findViewById(R.id.text);
		current.setBackgroundColor(Color.RED);
		current.setText(text);
	}
}
