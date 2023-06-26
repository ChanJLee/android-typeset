package me.chan.texas.renderer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.AnyThread;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.R;
import me.chan.texas.TexasOption;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.annotations.Idempotent;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.source.ObjectSource;
import me.chan.texas.source.Source;
import me.chan.texas.source.SourceOpenException;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.HyphenStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.utils.TexasUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 渲染引擎入口视图
 */
public final class TexasView extends FrameLayout {
	/**
	 * 非正常下标
	 */
	public static final int NO_POSITION = RecyclerView.NO_POSITION;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BREAK_STRATEGY_SIMPLE = 1;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BREAK_STRATEGY_BALANCE = 2;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int HYPHEN_STRATEGY_US = 1;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int HYPHEN_STRATEGY_UK = 2;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int DEFAULT_TEXT_SIZE = 18;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int DEFAULT_LINE_SPACE = 12;

	/**
	 * TexasView 是禁止的
	 */
	public static final int SCROLL_STATE_IDLE = 0;

	/**
	 * TexasView 正在被用户拖动
	 */
	public static final int SCROLL_STATE_DRAGGING = 1;

	/**
	 * TexasView 正在滚动，但不是因为用户的拖动导致的，比如动画导致TexasView滚动
	 */
	public static final int SCROLL_STATE_SETTLING = 2;

	void notifySegmentClicked(float x, float y, Object tag) {
		if (mOnClickedListener != null) {
			mOnClickedListener.onSegmentClicked(x, y, tag);
		}
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING})
	public @interface TexasViewScrollState {
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public final static Map<String, WeakReference<Typeface>> TYPEFACE_CACHE = new HashMap<>();

	private Renderer mRenderer;
	private ViewTreeObserver.OnGlobalLayoutListener mLastOnGlobalLayoutListener = null;
	private RenderListener mRenderListener;
	private OnClickedListener mOnClickedListener;
	private Adapter<?> mAdapter;
	private OnScrollListener mOnScrollListener;
	private OnDragSelectListener mOnDragSelectListener;

	public TexasView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TexasView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
		checkUIThreadPriority();
	}

	private void init(Context context, AttributeSet attributeSet, int defStyleAttr) {
		@SuppressLint("CustomViewStyleable")
		TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.com_shanbay_lib_texas_TeView, defStyleAttr, 0);
		try {
			init(context, typedArray);
		} finally {
			typedArray.recycle();
		}
	}

	private static void checkUIThreadPriority() {
		// On Android 8+, UI thread's priority already increase from 0 to -10(THREAD_PRIORITY_VIDEO),
		// higher than URGENT_DISPLAY (-8), we at least adjust to URGENT_DISPLAY when on 7 or under,
		// and it will help to improve TextureView performance
		try {
			int priority = Process.getThreadPriority(0);
			if (priority <= Process.THREAD_PRIORITY_URGENT_DISPLAY) {
				Log.i("Texas", "UI thread priority=" + priority + ", don't need to raise!");
				return;
			}

			Log.i("Texas", "UI thread priority=" + priority + ", need to raise!");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				Process.setThreadPriority(Process.THREAD_PRIORITY_VIDEO);
			} else {
				Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);
			}
		} catch (Throwable t) {
			Log.w("Texas", t);
		}
	}

	private void init(Context context, TypedArray typedArray) {
		Resources resources = getResources();
		RenderOption renderOption = new RenderOption();

		// 设置字体颜色
		renderOption.setTextColor(
				typedArray.getColor(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_textColor,
						ContextCompat.getColor(context, R.color.com_shanbay_lib_texas_text_color)
				)
		);

		// 设置字体
		renderOption.setTypeface(Typeface.DEFAULT);
		String typefacePath = typedArray.getString(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_typefaceAssets);
		if (!TextUtils.isEmpty(typefacePath)) {
			WeakReference<Typeface> typefaceWeakReference = TYPEFACE_CACHE.get(typefacePath);
			Typeface typeface;
			if (typefaceWeakReference != null && (typeface = typefaceWeakReference.get()) != null) {
				renderOption.setTypeface(typeface);
			} else {
				typeface = TexasUtils.createTypefaceFromAsset(context, typefacePath);
				typefaceWeakReference = new WeakReference<>(typeface);
				TYPEFACE_CACHE.put(typefacePath, typefaceWeakReference);
				renderOption.setTypeface(typeface);
			}
		}

		// 设置字体大小
		renderOption.setTextSize(
				typedArray.getDimension(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_textSize,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_SP,
								DEFAULT_TEXT_SIZE,
								resources.getDisplayMetrics()
						)
				)
		);

		// 行间距
		renderOption.setLineSpace(
				typedArray.getDimension(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_lineSpace,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP,
								DEFAULT_LINE_SPACE,
								resources.getDisplayMetrics()
						)
				)
		);

		// 选中字体的背景色
		renderOption.setSelectedBackgroundColor(
				typedArray.getColor(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_selectedBackgroundColor,
						ContextCompat.getColor(context, R.color.com_shanbay_lib_texas_theme_color)
				)
		);

		// 选中字体的颜色
		renderOption.setSelectedTextColor(
				typedArray.getColor(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_selectedTextColor, Color.WHITE)
		);

		// 选中span的背景色
		renderOption.setSelectedByLongClickBackgroundColor(
				typedArray.getColor(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_spanSelectedBackgroundColor,
						ContextCompat.getColor(context, R.color.com_shanbay_lib_texas_span_bg_color)
				)
		);

		// 选中span的字体颜色
		renderOption.setSelectedByLongClickTextColor(
				typedArray.getColor(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_spanSelectedTextColor,
						ContextCompat.getColor(context, R.color.com_shanbay_lib_texas_text_color))
		);

		// 断字策略
		int breakStrategy = typedArray.getInt(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_breakStrategy, BREAK_STRATEGY_BALANCE);
		renderOption.setBreakStrategy(
				breakStrategy == BREAK_STRATEGY_SIMPLE ?
						BreakStrategy.SIMPLE : BreakStrategy.BALANCED
		);

		// 是否可选单词
		renderOption.setWordSelectable(
				typedArray.getBoolean(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_wordSelectable, true)
		);

		// 断字策略
		int hyphenStrategy = typedArray.getInt(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_hyphenStrategy, HYPHEN_STRATEGY_US);
		renderOption.setHyphenStrategy(
				hyphenStrategy == HYPHEN_STRATEGY_UK ?
						HyphenStrategy.UK : HyphenStrategy.US
		);

		// lazy 渲染模式优化
		renderOption.setEnableLazyRender(
				typedArray.getBoolean(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_lazyRender, true)
		);

		// 高亮span文字颜色
		renderOption.setSpanHighlightTextColor(
				typedArray.getColor(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_spanHighlightTextColor,
						ContextCompat.getColor(context, R.color.com_shanbay_lib_texas_theme_color)
				)
		);

		// 加载中背景色
		renderOption.setLoadingBackgroundColor(
				typedArray.getColor(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_loadingBackgroundColor,
						ContextCompat.getColor(context, R.color.com_shanbay_lib_texas_loading_bg)
				)
		);

		// 自由划线水滴颜色
		renderOption.setDragViewColor(
				typedArray.getColor(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_dragViewColor,
						ContextCompat.getColor(context, R.color.com_shanbay_lib_texas_drag_view_color))
		);

		// 设置选中圆角半径
		renderOption.setSelectedBackgroundRoundRadius(
				typedArray.getDimension(
						R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_selectedBackgroundRoundRadius,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP,
								3,
								getResources().getDisplayMetrics()
						)
				)
		);

		// 是否开启兼容模式
		renderOption.setCompatMode(
				typedArray.getBoolean(R.styleable.com_shanbay_lib_texas_TeView_com_shanbay_lib_texas_compatMode, false)
		);


		// 如果开启了非兼容模式，且系统版本小于6.0，关闭硬件加速
		// {@link com.shanbay.lib.texas.renderer.core.graphics.TextureScene}
		if (!renderOption.isCompatMode() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			setLayerType(LAYER_TYPE_SOFTWARE, null);
		}

		mRenderer = new Renderer(this, renderOption);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyRenderStart() {
		if (mRenderListener != null) {
			mRenderListener.onStart(TexasView.this);
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyRenderEnd() {
		if (mRenderListener != null) {
			mRenderListener.onEnd(TexasView.this);
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyRenderError(Throwable throwable) {
		if (mRenderListener != null) {
			mRenderListener.onError(TexasView.this, throwable);
		}
	}

	public void setSegmentDecoration(@NonNull SegmentDecoration segmentDecoration) {
		if (mRenderer == null) {
			return;
		}

		mRenderer.setSegmentDecoration(segmentDecoration);
	}

	private void render() {
		if (mRenderer == null) {
			return;
		}

		int width = getRenderWidth();
		if (width > 0) {
			d("set source direct");
			mRenderer.render(width);
			return;
		}

		i("unknown size, try later, width: " + width);
		ViewTreeObserver viewTreeObserver = getViewTreeObserver();
		if (mLastOnGlobalLayoutListener != null) {
			d("remove last on global layout listener");
			viewTreeObserver.removeOnGlobalLayoutListener(mLastOnGlobalLayoutListener);
		}

		mLastOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int width = getRenderWidth();
				if (mRenderer == null) {
					i("renderer is null");
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
					return;
				}

				if (width <= 0) {
					i("unknown size, try later, width: " + width);
					return;
				}

				i("get width, render: " + width);
				getViewTreeObserver().removeOnGlobalLayoutListener(this);
				mRenderer.render(width);
				mLastOnGlobalLayoutListener = null;
			}
		};
		viewTreeObserver.addOnGlobalLayoutListener(mLastOnGlobalLayoutListener);
	}

	private int getRenderWidth() {
		int padding = mRenderer == null ? 0 : mRenderer.getPaddingWidth();
		return getWidth() - getPaddingLeft() - getPaddingRight() - padding;
	}

	/**
	 * 创建一个新的渲染参数 结束后调用 {@link TexasView#refresh(RenderOption)} 刷新样式
	 *
	 * @return option
	 */
	@NonNull
	public RenderOption createRendererOption() {
		d("create new renderer option");
		return mRenderer == null ? RenderOption.DEFAULT : mRenderer.createRendererOption();
	}

	@Nullable
	RenderOption getRendererOption() {
		return mRenderer == null ? null : mRenderer.getRendererOption();
	}

	/**
	 * 设置adapter
	 *
	 * @param adapter adapter
	 */
	public void setAdapter(@NonNull Adapter<?> adapter) {
		d("set adapter");
		if (mRenderer == null) {
			return;
		}

		if (mAdapter != null) {
			d("detach prev adapter");
			mAdapter.detach();
			mAdapter = null;
		}

		d("bind adapter");
		mRenderer.setAdapter(adapter);
		adapter.attach(this);
		mAdapter = adapter;
	}

	/**
	 * 获取当前adapter
	 *
	 * @return 当前adapter
	 */
	@Nullable
	public Adapter<?> getAdapter() {
		return mAdapter;
	}

	/**
	 * 刷新内容，可能会重新排版 {@link TexasView#redraw()}
	 *
	 * @param renderOption option
	 */
	public void refresh(RenderOption renderOption) {
		d("refresh render option");
		if (mRenderer != null) {
			mRenderer.refresh(renderOption);
		}
	}

	/**
	 * 只是简单的重新绘制内容
	 */
	public void redraw() {
		if (mRenderer != null) {
			mRenderer.redraw();
		}
	}

	/**
	 * 释放资源
	 */
	public void release() {
		i("release");
		if (mAdapter != null) {
			mAdapter.detach();
			mAdapter = null;
		}
		mRenderListener = null;
		mOnClickedListener = null;
		mOnDragSelectListener = null;
		mLastOnGlobalLayoutListener = null;
		if (mRenderer != null) {
			mRenderer.release();
			mRenderer = null;
		}
	}

	/**
	 * @param decor paragraph 装饰器
	 */
	public void setParagraphDecor(ParagraphDecor decor) {
		if (mRenderer != null) {
			mRenderer.setParagraphDecor(decor);
		}
	}

	/**
	 * 获取第一个可见segment下标
	 *
	 * @param completelyVisible 是否要完全可见的segment
	 * @return 第一个可见segment下标，没有则为 {@link TexasView#NO_POSITION}
	 */
	public int getFirstVisibleSegmentIndex(boolean completelyVisible) {
		return mRenderer == null ? NO_POSITION : mRenderer.getFirstVisibleSegmentIndex(completelyVisible);
	}

	/**
	 * 返回当前正在渲染的document
	 *
	 * @return document
	 */
	@Nullable
	public Document getDocument() {
		return mRenderer == null ? null : mRenderer.getDocument();
	}

	/**
	 * @param renderListener 渲染监听器 {@link RenderListener}
	 */
	public void setRenderListener(RenderListener renderListener) {
		mRenderListener = renderListener;
	}

	/**
	 * 获取选中信息
	 *
	 * @return 选中信息
	 */
	@Nullable
	public Selection getSelection() {
		return mRenderer == null ? null : mRenderer.getSelection();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyEmptyClicked(float x, float y) {
		if (mOnClickedListener != null) {
			mOnClickedListener.onEmptyClicked(x, y);
		}
	}

	public void setOnDragSelectListener(OnDragSelectListener onDragSelectListener) {
		mOnDragSelectListener = onDragSelectListener;
	}

	/**
	 * @param onClickedListener 设置点击事件
	 */
	public void setOnClickedListener(OnClickedListener onClickedListener) {
		mOnClickedListener = onClickedListener;
	}

	/**
	 * {@link TexasView#scrollToPosition(int, boolean)}
	 *
	 * @param position position
	 */
	public void scrollToPosition(int position) {
		scrollToPosition(position, true);
	}

	/**
	 * 滚动到document的某个segment
	 *
	 * @param position segment index {@link Document#indexOfSegment(Segment)} {@link Paragraph}
	 * @param smooth   滚动的时候是否平滑滚动
	 */
	public void scrollToPosition(int position, boolean smooth) {
		scrollToPosition(position, smooth, 0);
	}

	/**
	 * 滚动到document的某个segment
	 *
	 * @param position segment index {@link Document#indexOfSegment(Segment)} (Segment)} {@link Paragraph}
	 * @param smooth   滚动的时候是否平滑滚动
	 * @param offset   滑动方向上的offset
	 */
	public void scrollToPosition(int position, boolean smooth, int offset) {
		if (mRenderer != null) {
			mRenderer.scrollToPosition(position, smooth, offset);
		}
	}

	/**
	 * 高亮paragraph中的文本，只在渲染出document后生效
	 *
	 * @param predicate 谓词
	 */
	public void highlightParagraphs(HighlightPredicate predicate) {
		highlightParagraphs(predicate, false, 0);
	}

	/**
	 * 高亮paragraph中的文本，只在渲染出document后生效
	 *
	 * @param predicate 谓词
	 * @param scrollTo  是否滚动到高亮区域
	 * @param offset    滚动偏移
	 */
	public void highlightParagraphs(HighlightPredicate predicate, boolean scrollTo, int offset) {
		if (mRenderer == null) {
			return;
		}

		mRenderer.highlightParagraphs(predicate, scrollTo, offset);
	}

	/**
	 * 清除高亮
	 */
	public void clearHighlight() {
		if (mRenderer != null) {
			mRenderer.clearHighlight();
		}
	}

	/**
	 * 选中和高亮的定义
	 * <p>
	 * 高亮只是高亮文字
	 * <p>
	 * 选中可以由程序触发，比如我想让某一段文字选中，也可以通过点击事件触发，比如长按，点击单词。
	 */
	public void setOnSpanClickedPredicate(OnSpanClickedPredicate predicate) {
		if (mRenderer != null) {
			mRenderer.setOnSpanClickedPredicate(predicate);
		}
	}

	/**
	 * 长按触发
	 *
	 * @param predicate predicate
	 */
	public void setOnSpanLongClickedPredicate(OnSpanLongClickedPredicate predicate) {
		if (mRenderer != null) {
			mRenderer.setOnSpanLongClickedPredicate(predicate);
		}
	}

	/**
	 * 选中文本
	 *
	 * @param predicate predicate
	 * @return 选中区域
	 */
	@Nullable
	public Selection selectParagraphs(SelectionPredicate predicate) {
		return mRenderer == null ? null : mRenderer.selectParagraphs(predicate);
	}

	/**
	 * 清除选中区域并隐藏水滴
	 */
	public void clearSelection() {
		if (mRenderer != null) {
			mRenderer.clearSelection();
		}
	}

	/**
	 * 获取滚动状态
	 *
	 * @return {@link #SCROLL_STATE_DRAGGING} {@link #SCROLL_STATE_IDLE} {@link #SCROLL_STATE_SETTLING}
	 */
	@TexasViewScrollState
	public int getScrollState() {
		if (mRenderer == null) {
			return SCROLL_STATE_IDLE;
		}
		return mRenderer.getScrollState();
	}

	/**
	 * 设置是否有滚动条
	 *
	 * @param enable 是否开启
	 */
	public void setScrollBarEnable(boolean enable) {
		if (mRenderer != null) {
			mRenderer.setScrollBarEnable(enable);
		}
	}

	public void setRendererPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
		if (mRenderer != null) {
			mRenderer.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		}
	}

	/**
	 * 设置进度条样式
	 *
	 * @param drawable 样式
	 */
	public void setScrollBarDrawable(Drawable drawable) {
		if (mRenderer != null) {
			mRenderer.setScrollBarDrawable(drawable);
		}
	}

	/**
	 * @return 获取进度条样式
	 */
	@Nullable
	public Drawable getScrollBarDrawable() {
		return mRenderer == null ? null : mRenderer.getScrollBarDrawable();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifySpanLongClicked(float x, float y, Object tag) {
		if (mOnClickedListener != null) {
			mOnClickedListener.onSpanLongClicked(x, y, tag);
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifySpanClicked(float x, float y, Object tag) {
		if (mOnClickedListener != null) {
			mOnClickedListener.onSpanClicked(x, y, tag);
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyDragStart(float x, float y) {
		if (mOnDragSelectListener != null) {
			mOnDragSelectListener.onDragStart(x, y);
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyDragEnd(float x, float y) {
		if (mOnDragSelectListener != null) {
			mOnDragSelectListener.onDragEnd(x, y);
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyDragDismiss() {
		if (mOnDragSelectListener != null) {
			mOnDragSelectListener.onDragDismiss();
		}
	}

	/**
	 * 通知引擎开始响应
	 */
	public void resume() {
		if (mRenderer != null) {
			mRenderer.resume();
		}
	}

	/**
	 * 通知引擎可以停止响应
	 */
	public void pause() {
		if (mRenderer != null) {
			mRenderer.pause();
		}
	}

	/**
	 * 如果texas view有固定大小，可以设置这个flag来提高性能
	 *
	 * @param enable 设置是否固定大小
	 */
	public void setHasFixedSize(boolean enable) {
		if (mRenderer != null) {
			mRenderer.setHasFixedSize(enable);
		}
	}

	/**
	 * @param listener 设置滚动状态监听器
	 */
	public void setOnScrollListener(OnScrollListener listener) {
		mOnScrollListener = listener;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyScrollStateChanged(int state) {
		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChanged(this, state);
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyScrollChanged(int dx, int dy) {
		if (mOnScrollListener != null) {
			mOnScrollListener.onScrolled(dx, dy);
		}
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

	private static void d(String msg) {
		Log.d("TexasView", msg);
	}

	private static void i(String msg) {
		Log.i("TexasView", msg);
	}

	/**
	 * Adapter
	 *
	 * @param <T> Adapter接受的数据类型
	 */
	public abstract static class Adapter<T> {
		private Source<T> mSource;
		private TexasView mTexasView;

		private void attach(@NonNull TexasView view) {
			mTexasView = view;
			if (mSource != null) {
				view.render();
			}
		}

		private void detach() {
			mTexasView = null;
		}

		@UiThread
		public final void setData(T data) {
			setSource(new ObjectSource<>(data));
		}

		@UiThread
		public final void setSource(Source<T> source) {
			mSource = source;
			notifyViewsRender();
		}

		private void notifyViewsRender() {
			if (mTexasView != null) {
				mTexasView.render();
			}
		}

		/**
		 * @param content     内容 {@link Source}
		 * @param texasOption texas option
		 * @return 文档
		 * @throws ParseException 解析错误的时候抛出
		 */
		@NonNull
		@AnyThread
		protected abstract Document parse(@NonNull T content, TexasOption texasOption) throws ParseException;

		@NonNull
		@RestrictTo(RestrictTo.Scope.LIBRARY)
		public final Document getDocument(TexasOption texasOption) throws SourceOpenException, ParseException {
			if (mSource == null) {
				return Document.createEmptyDocument();
			}

			try {
				T value = mSource.open();
				return parse(value, texasOption);
			} finally {
				// close quietly
				try {
					mSource.close();
				} catch (Throwable throwable) {
					Log.w("TexasAdapter", throwable);
				}
			}
		}
	}

	public interface SelectionPredicate {

		/**
		 * @param paragraphTag {@link Paragraph#getTag()}
		 * @param spanTag      {@link Paragraph.SpanBuilder#tag(Object)}
		 * @return 是否选中
		 */
		@Idempotent
		boolean apply(Object paragraphTag, Object spanTag);
	}

	public interface HighlightPredicate {
		/**
		 * @param paragraphTag {@link Paragraph.Builder#tag(Object)}
		 * @param spanTag      {@link Paragraph.SpanBuilder#tag(Object)}
		 * @return 是否选中，这个函数必须是幂等的
		 */
		@Idempotent
		boolean apply(@Nullable Object paragraphTag, @Nullable Object spanTag);
	}

	/**
	 * use {@link me.chan.texas.renderer.OnSpanClickedPredicate} instead
	 */
	@Deprecated
	public interface OnSpanClickedPredicate extends me.chan.texas.renderer.OnSpanClickedPredicate {
	}

	/**
	 * use {@link me.chan.texas.renderer.OnSpanLongClickedPredicate} instead
	 */
	@Deprecated
	public interface OnSpanLongClickedPredicate extends me.chan.texas.renderer.OnSpanLongClickedPredicate {
	}

	/**
	 * 点击事件
	 */
	public interface OnClickedListener {
		/**
		 * @param x   点击发生时相对屏幕的x坐标
		 * @param y   点击发生时相对屏幕的y坐标
		 * @param tag 被点击的text tag
		 */
		void onSpanClicked(float x, float y, Object tag);

		/**
		 * @param x   点击发生时相对屏幕的x坐标
		 * @param y   点击发生时相对屏幕的y坐标
		 * @param tag 被点击的text tag
		 */
		void onSpanLongClicked(float x, float y, Object tag);

		/**
		 * @param x   点击发生时相对屏幕的x坐标
		 * @param y   点击发生时相对屏幕的y坐标
		 * @param tag 被点击的segment tag
		 */
		void onSegmentClicked(float x, float y, Object tag);

		/**
		 * @param x 点击发生时相对屏幕的x坐标
		 * @param y 点击发生时相对屏幕的y坐标
		 */
		void onEmptyClicked(float x, float y);
	}

	/**
	 * segment修饰器
	 */
	public interface SegmentDecoration {
		/**
		 * 装饰一个segment，可以修饰其边缘
		 * <p>
		 * outRect.set(10, 10, 10, 10) 即代表在当前segment周围包裹10px的空间
		 *
		 * @param index    当前所在的下标
		 * @param count    总个数
		 * @param segment  当前需要装饰的segment
		 * @param document segment所在的文档
		 * @param outRect  输出边缘
		 */
		@AnyThread
		void onDecorateSegment(int index, int count, Segment segment, Document document, Rect outRect);
	}

	/**
	 * 滚动状态监听
	 */
	public interface OnScrollListener {
		/**
		 * @param view  view
		 * @param state {@link TexasView#SCROLL_STATE_DRAGGING} etc.
		 */
		void onScrollStateChanged(TexasView view, int state);

		void onScrolled(int dx, int dy);
	}

	/**
	 * 拖动监听
	 */
	public interface OnDragSelectListener {
		/**
		 * @param x 点击发生时相对屏幕的x坐标
		 * @param y 点击发生时相对屏幕的y坐标
		 */

		void onDragStart(float x, float y);

		void onDragEnd(float x, float y);

		void onDragDismiss();
	}
}