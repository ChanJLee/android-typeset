package me.chan.texas.renderer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import me.chan.texas.R;
import me.chan.texas.log.Log;
import me.chan.texas.parser.Parser;
import me.chan.texas.source.ObjectSource;
import me.chan.texas.source.Source;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;

public class TexasView extends FrameLayout {

	private static final int BREAK_STRATEGY_SIMPLE = 1;
	private static final int BREAK_STRATEGY_BALANCE = 2;
	private static final int DEFAULT_TEXT_SIZE = 18;
	private static final int DEFAULT_LINE_SPACE = 12;
	private static final int DEFAULT_SEGMENT_SPACE = 28;
	private static Map<String, WeakReference<Typeface>> TYPEFACE_CACHE = new HashMap<>();

	private Renderer mRenderer;
	private ViewTreeObserver.OnGlobalLayoutListener mLastOnGlobalLayoutListener = null;
	private RenderListener mRenderListener;
	private ScrollListener mScrollListener;

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
		renderOption.setTypeface(Typeface.DEFAULT);
		String typefacePath = typedArray.getString(R.styleable.me_chan_te_TeView_me_chan_te_typefaceAssets);
		if (!TextUtils.isEmpty(typefacePath)) {
			WeakReference<Typeface> typefaceWeakReference = TYPEFACE_CACHE.get(typefacePath);
			Typeface typeface;
			if (typefaceWeakReference != null && (typeface = typefaceWeakReference.get()) != null) {
				renderOption.setTypeface(typeface);
			} else {
				typeface = Typeface.createFromAsset(context.getAssets(), typefacePath);
				typefaceWeakReference = new WeakReference<>(typeface);
				TYPEFACE_CACHE.put(typefacePath, typefaceWeakReference);
				renderOption.setTypeface(typeface);
			}
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
		int breakStrategy = typedArray.getInt(R.styleable.me_chan_te_TeView_me_chan_te_breakStrategy, BREAK_STRATEGY_BALANCE);
		renderOption.setBreakStrategy(
				breakStrategy == BREAK_STRATEGY_SIMPLE ?
						BreakStrategy.SIMPLE : BreakStrategy.BALANCED
		);

		// 是否可选单词
		renderOption.setWordSelectable(
				typedArray.getBoolean(R.styleable.me_chan_te_TeView_me_chan_te_wordSelectable, true)
		);

		mRenderer = new SlidingRenderer(this, renderOption);
	}

	void notifyRenderStart() {
		if (mRenderListener != null) {
			mRenderListener.onStart(TexasView.this);
		}
	}

	void notifyRenderEnd() {
		if (mRenderListener != null) {
			mRenderListener.onEnd(TexasView.this);
		}
	}

	void notifyRenderError(Throwable throwable) {
		if (mRenderListener != null) {
			mRenderListener.onError(TexasView.this, throwable);
		}
	}

	/**
	 * 设置渲染源
	 * 渲染源的类型必须和parser的类型一致:
	 * <p/>
	 * {@link TexasView#setParser(Parser)}
	 * <p/>
	 * {@link Parser}
	 *
	 * @param o 对象
	 */
	public void setSource(Object o) {
		setSource(new ObjectSource(o));
	}

	/**
	 * 设置渲染源
	 * 渲染源的类型必须和parser的类型一致:
	 * <p/>
	 * {@link Parser}
	 *
	 * @param source 源
	 */
	public void setSource(final Source<?> source) {
		int width = getWidth() - getPaddingLeft() - getPaddingRight();
		if (width <= 0) {
			i("unknown size, try later, width: " + width);
			ViewTreeObserver viewTreeObserver = getViewTreeObserver();
			if (mLastOnGlobalLayoutListener != null) {
				d("remove last on global layout listener");
				viewTreeObserver.removeOnGlobalLayoutListener(mLastOnGlobalLayoutListener);
			}

			mLastOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
					mRenderer.render(source, getWidth());
					mLastOnGlobalLayoutListener = null;
				}
			};
			getViewTreeObserver().addOnGlobalLayoutListener(mLastOnGlobalLayoutListener);
			return;
		}

		d("set source direct");
		mRenderer.render(source, width);
	}

	/**
	 * 清除当前选中效果
	 */
	public void clearSelection() {
		d("start selection");
		mRenderer.clearSelection();
	}

	/**
	 * 创建一个新的渲染参数 结束后调用 {@link TexasView#refresh(RenderOption)} 刷新样式
	 *
	 * @return option
	 */
	public RenderOption createRendererOption() {
		d("create new renderer option");
		return mRenderer.createRendererOption();
	}

	/**
	 * 解析器能够接受的类型必须和source一致 {@link Source}
	 * 监听器必须在初始化的时候调用，否则行为将是未定义的
	 *
	 * @param parser 解析器
	 */
	public void setParser(Parser<?> parser) {
		d("set parser");
		mRenderer.setParser(parser);
	}

	/**
	 * 刷新当前内容
	 *
	 * @param renderOption option
	 */
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

	/**
	 * @return 获取第一个可见segment下标
	 */
	public int getFirstVisibleSegmentIndex() {
		return mRenderer.getFirstVisibleSegmentIndex();
	}

	/**
	 * @return 返回当前正在渲染的document
	 */
	@Nullable
	public Document getDocument() {
		return mRenderer.getDocument();
	}

	/**
	 * @param renderListener 渲染监听器 {@link RenderListener}
	 */
	public void setRenderListener(RenderListener renderListener) {
		mRenderListener = renderListener;
	}

	public void setScrollListener(ScrollListener scrollListener) {
		mScrollListener = scrollListener;
	}

	void notifyScrolled(int dx, int dy) {
		if (mScrollListener != null) {
			mScrollListener.onScrolled(this, dx, dy);
		}
	}

	public int getSelectedTop() {
		return -1;
	}

	public int getSelectedBottom() {
		return -1;
	}

	/**
	 * 渲染监听器
	 */
	public interface RenderListener {
		/**
		 * 开始渲染的时候调用
		 *
		 * @param texasView view
		 */
		void onStart(TexasView texasView);

		/**
		 * 渲染结束的时候调用
		 *
		 * @param texasView view
		 */
		void onEnd(TexasView texasView);

		/**
		 * 发生错误的时候调用
		 *
		 * @param texasView view
		 * @param throwable 错误
		 */
		void onError(TexasView texasView, Throwable throwable);
	}

	/**
	 * 滚动监听器
	 */
	public interface ScrollListener {
		/**
		 * @param texasView view
		 * @param dx        dx
		 * @param dy        dy
		 */
		void onScrolled(TexasView texasView, int dx, int dy);
	}

	private static void d(String msg) {
		Log.d("TexasView", msg);
	}

	private static void i(String msg) {
		Log.i("TexasView", msg);
	}
}
