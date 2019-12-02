package me.chan.texas.renderer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import me.chan.texas.R;
import me.chan.texas.log.Log;
import me.chan.texas.parser.Parser;
import me.chan.texas.source.Source;
import me.chan.texas.text.BreakStrategy;

public class TexasView extends FrameLayout {

	private static final int BREAK_STRATEGY_SIMPLE = 1;
	private static final int BREAK_STRATEGY_BALANCE = 2;
	private static final int MODE_PAGING = 1;
	private static final int MODE_SLIDING = 2;
	private static final int DEFAULT_TEXT_SIZE = 18;
	private static final int DEFAULT_LINE_SPACE = 12;
	private static final int DEFAULT_SEGMENT_SPACE = 28;
	private static Map<String, WeakReference<Typeface>> TYPEFACE_CACHE = new HashMap<>();

	private Renderer mRenderer;
	private ViewTreeObserver.OnGlobalLayoutListener mLastOnGlobalLayoutListener = null;

	public TexasView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TexasView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attributeSet, int defStyleAttr) {
		@SuppressLint("CustomViewStyleable")
		TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.me_chan_te_TeView, defStyleAttr, 0);
		try {
			init(context, typedArray);
		} finally {
			typedArray.recycle();
		}
	}

	private void init(Context context, TypedArray typedArray) {
		Resources resources = getResources();
		RenderOption renderOption = new RenderOption();

		// 设置字体颜色
		renderOption.setTextColor(
				typedArray.getColor(R.styleable.me_chan_te_TeView_me_chan_te_textColor,
						ContextCompat.getColor(context, R.color.me_chan_te_text_color)
				)
		);

		// 设置字体
		String typefacePath = typedArray.getString(R.styleable.me_chan_te_TeView_me_chan_te_typefaceAssets);
		if (TextUtils.isEmpty(typefacePath)) {
			typefacePath = "typeface/SourceSerifPro-Regular.ttf";
		}
		WeakReference<Typeface> typefaceWeakReference = TYPEFACE_CACHE.get(typefacePath);
		Typeface typeface;
		if (typefaceWeakReference != null && (typeface = typefaceWeakReference.get()) != null) {
			renderOption.setTypeface(typeface);
		} else {
			typeface = Typeface.createFromAsset(context.getAssets(), "typeface/SourceSerifPro-Regular.ttf");
			typefaceWeakReference = new WeakReference<>(typeface);
			TYPEFACE_CACHE.put(typefacePath, typefaceWeakReference);
			renderOption.setTypeface(typeface);
		}

		// 设置字体大小
		renderOption.setTextSize(
				typedArray.getDimension(R.styleable.me_chan_te_TeView_me_chan_te_textSize,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_SP,
								DEFAULT_TEXT_SIZE,
								resources.getDisplayMetrics()
						)
				)
		);

		// 行间距
		renderOption.setLineSpace(
				typedArray.getDimension(R.styleable.me_chan_te_TeView_me_chan_te_lineSpace,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP,
								DEFAULT_LINE_SPACE,
								resources.getDisplayMetrics()
						)
				)
		);

		// 是否开启首行缩进
		renderOption.setIndentEnable(
				typedArray.getBoolean(R.styleable.me_chan_te_TeView_me_chan_te_indent, false)
		);

		// 选中字体的背景色
		renderOption.setSelectedBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_te_TeView_me_chan_te_selectedBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_te_theme_color)
				)
		);

		// 选中字体的颜色
		renderOption.setSelectedTextColor(
				typedArray.getColor(R.styleable.me_chan_te_TeView_me_chan_te_selectedTextColor, Color.WHITE)
		);

		// 选中span的背景色
		renderOption.setSpanSelectedBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_te_TeView_me_chan_te_spanSelectedBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_te_span_bg_color)
				)
		);

		// 选中span的字体颜色
		renderOption.setSpanSelectedTextColor(
				typedArray.getColor(R.styleable.me_chan_te_TeView_me_chan_te_spanSelectedTextColor,
						ContextCompat.getColor(context, R.color.me_chan_te_text_color))
		);

		// 片段间的间隔
		renderOption.setSegmentSpace(typedArray.getDimension(R.styleable.me_chan_te_TeView_me_chan_te_segmentSpace,
				TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP,
						DEFAULT_SEGMENT_SPACE,
						resources.getDisplayMetrics())
				)
		);

		// 断字策略
		int breakStrategy = typedArray.getInt(R.styleable.me_chan_te_TeView_me_chan_te_breakStrategy, BREAK_STRATEGY_SIMPLE);
		renderOption.setBreakStrategy(
				breakStrategy == BREAK_STRATEGY_BALANCE ?
						BreakStrategy.BALANCED : BreakStrategy.SIMPLE
		);

		// 是否可选单词
		renderOption.setWordSelectable(
				typedArray.getBoolean(R.styleable.me_chan_te_TeView_me_chan_te_wordSelectable, true)
		);

		int mode = typedArray.getInt(R.styleable.me_chan_te_TeView_me_chan_te_render_mode, MODE_SLIDING);
		if (mode == MODE_PAGING) {
			renderOption.setRendererMode(RendererMode.PAGING);
			mRenderer = new PagingRenderer(this, renderOption);
		} else {
			renderOption.setRendererMode(RendererMode.SLIDING);
			mRenderer = new SlidingRenderer(this, renderOption);
		}
	}

	public void setSource(final Source source) {
		int width = getWidth();
		int height = getHeight();
		if (width <= 0 || height <= 0) {
			i("unknown size, try later, width: " + width + " height: " + height);
			ViewTreeObserver viewTreeObserver = getViewTreeObserver();
			if (mLastOnGlobalLayoutListener != null) {
				d("remove last on global layout listener");
				viewTreeObserver.removeOnGlobalLayoutListener(mLastOnGlobalLayoutListener);
			}

			mLastOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
					mRenderer.render(source, getWidth(), getHeight());
					mLastOnGlobalLayoutListener = null;
				}
			};
			getViewTreeObserver().addOnGlobalLayoutListener(mLastOnGlobalLayoutListener);
			return;
		}

		d("set source direct");
		mRenderer.render(source, width, height);
	}

	public void clearSelection() {
		d("clear selection");
		mRenderer.clearSelection();
	}

	public RenderOption createRendererOption() {
		d("create new renderer option");
		return mRenderer.createRendererOption();
	}

	// TODO 考虑要不要暴露接口
	public void setParser(Parser<?> parser) {
		d("set parser");
		mRenderer.setParser(parser);
	}

	public void refresh(RenderOption renderOption) {
		d("refresh render option");
		mRenderer.refresh(renderOption);
	}

	@Override
	protected void onDetachedFromWindow() {
		i("on detached from window");
		mRenderer.release();
		super.onDetachedFromWindow();
	}

	private static void d(String msg) {
		Log.d("Texas", msg);
	}

	private static void i(String msg) {
		Log.i("Texas", msg);
	}
}
