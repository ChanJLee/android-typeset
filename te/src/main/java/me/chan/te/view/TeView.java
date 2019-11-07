package me.chan.te.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import me.chan.te.R;
import me.chan.te.parser.Parser;
import me.chan.te.source.Source;
import me.chan.te.text.BreakStrategy;

public class TeView extends FrameLayout {

	private static final int BREAK_STRATEGY_SIMPLE = 1;
	private static final int BREAK_STRATEGY_BALANCE = 2;

	static final int MODE_PAGING = 1;
	static final int MODE_SLIDING = 2;


	private Adapter mAdapter;
	private RecyclerView mImpl;

	public TeView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mImpl = new RecyclerView(context);
		mImpl.setClipToPadding(false);
		mImpl.setClipChildren(false);
		mImpl.setLayoutManager(new LinearLayoutManager(context));
		addView(mImpl, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		mAdapter = new Adapter(context);
		mImpl.setAdapter(mAdapter);

		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attributeSet, int defStyleAttr) {
		@SuppressLint("CustomViewStyleable")
		TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.me_chan_te_TeView, defStyleAttr, 0);
		try {
			init(typedArray);
		} finally {
			typedArray.recycle();
		}
	}

	private void init(TypedArray typedArray) {
		float segmentSpace = typedArray.getDimension(R.styleable.me_chan_te_TeView_segmentSpace, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()));
		mImpl.addItemDecoration(new SpaceItemDecoration(segmentSpace));

		int mode = typedArray.getInt(R.styleable.me_chan_te_TeView_segmentSpace, MODE_SLIDING);
		if (mode == MODE_PAGING) {
			PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
			pagerSnapHelper.attachToRecyclerView(mImpl);
		}

		float textSize = typedArray.getDimension(R.styleable.me_chan_te_TeView_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
		mAdapter.setTextSize(textSize);

		int breakStrategy = typedArray.getInt(R.styleable.me_chan_te_TeView_breakStrategy, BREAK_STRATEGY_BALANCE);
		if (breakStrategy == BREAK_STRATEGY_SIMPLE) {
			mAdapter.setBreakStrategy(BreakStrategy.SIMPLE);
		} else {
			mAdapter.setBreakStrategy(BreakStrategy.BALANCED);
		}

		boolean wordSelectable = typedArray.getBoolean(R.styleable.me_chan_te_TeView_wordSelectable, false);
		// TODO


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

	public void setTypeface(Typeface typeface) {
		mAdapter.setTypeface(typeface);
	}

	public void setTextColor(int color) {
		mAdapter.setTextColor(color);
	}

	@Override
	protected void onDetachedFromWindow() {
		mAdapter.release();
		super.onDetachedFromWindow();
	}
}
