package me.chan.te.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import me.chan.te.source.Source;

public class TeView extends FrameLayout {

	private Adapter mAdapter;

	public TeView(Context context) {
		this(context, null);
	}

	public TeView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		RecyclerView recyclerView = new RecyclerView(context);
		recyclerView.setPadding(0, 20, 0, 20);
		recyclerView.setClipToPadding(false);
		recyclerView.setClipChildren(false);
		addView(recyclerView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		mAdapter = new Adapter(context);
		recyclerView.setAdapter(mAdapter);
	}

	public void setSource(final Source source) {
		int width = getWidth();
		if (width <= 0) {
			getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
					mAdapter.render(source, getWidth());
				}
			});
			return;
		}
		mAdapter.render(source, width);
	}

	public void setDebugMode(boolean debugMode) {
		mAdapter.setDebugMode(debugMode);
	}

	public boolean isDebugMode() {
		return mAdapter.isDebugMode();
	}
}
