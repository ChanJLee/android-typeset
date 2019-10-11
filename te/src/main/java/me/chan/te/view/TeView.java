package me.chan.te.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import me.chan.te.parser.Parser;
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

	/**
	 * @param textSize sp unit
	 */
	public void setTextSize(float textSize) {
		setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
	}

	/**
	 * @param unit     {@link TypedValue#COMPLEX_UNIT_PX} etc
	 * @param textSize text size
	 */
	public void setTextSize(int unit, float textSize) {
		float px = TypedValue.applyDimension(unit, textSize, getResources().getDisplayMetrics());
		mAdapter.setTextSize(px);
	}

	public void setParser(Parser parser) {
		mAdapter.setParser(parser);
	}

	public void setDebugMode(boolean debugMode) {
		mAdapter.setDebugMode(debugMode);
	}

	public boolean isDebugMode() {
		return mAdapter.isDebugMode();
	}

	public void setBreakStrategy(BreakStrategy breakStrategy) {
		mAdapter.setBreakStrategy(breakStrategy);
	}

	public enum BreakStrategy {
		/**
		 * 简单的左对齐
		 */
		SIMPLE,
		BALANCED
	}
}
