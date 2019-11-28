package me.chan.te.renderer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import me.chan.te.R;
import me.chan.te.parser.Parser;
import me.chan.te.source.Source;
import me.chan.te.text.BreakStrategy;

public class TeView extends FrameLayout {

	private static final int BREAK_STRATEGY_SIMPLE = 1;
	private static final int BREAK_STRATEGY_BALANCE = 2;
	private static final int MODE_PAGING = 1;
	private static final int MODE_SLIDING = 2;
	private static final int DEFAULT_TEXT_SIZE = 18;
	private static final int DEFAULT_LINE_SPACE = 12;
	private static final int DEFAULT_SEGMENT_SPACE = 28;
	private static Map<String, WeakReference<Typeface>> TYPEFACE_CACHE = new HashMap<>();

	private Renderer mRenderer;

	public TeView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TeView(Context context, AttributeSet attrs, int defStyleAttr) {
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
			getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
					mRenderer.render(source, getWidth(), getHeight());
				}
			});
			return;
		}

		mRenderer.render(source, width, height);
	}

	public void clearSelection() {
		mRenderer.clearSelection();
	}

	public RenderOption createRendererOption() {
		return mRenderer.createRendererOption();
	}

	public void setParser(Parser<?> parser) {
		mRenderer.setParser(parser);
	}

	public void refresh(RenderOption renderOption) {
		mRenderer.refresh(renderOption);
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO need test
		mRenderer.release();
		super.onDetachedFromWindow();
	}
}
