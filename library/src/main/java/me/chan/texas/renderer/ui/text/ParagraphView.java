package me.chan.texas.renderer.ui.text;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import me.chan.texas.BuildConfig;
import me.chan.texas.R;
import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.annotations.Internal;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.hyphenation.HyphenationPattern;
import me.chan.texas.measurer.AndroidMeasurer;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.LoadingStrategy;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.worker.ParseWorker;
import me.chan.texas.renderer.core.worker.ParagraphTypesetWorker;
import me.chan.texas.renderer.SpanPredicate;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.visitor.SelectedTextByClickedVisitor;
import me.chan.texas.source.Source;
import me.chan.texas.source.SourceCloseException;
import me.chan.texas.source.SourceOpenException;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.HyphenStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Region;
import me.chan.texas.utils.TexasUtils;

import java.lang.ref.WeakReference;

/**
 * 用于显示文本内容
 * <p>
 * 当前内容都是异步渲染的，所以当你不需要显示某个内容的时候，就调用 {@link #discard()} 丢弃之前的任务
 */
public class ParagraphView extends FrameLayout {
	private static final boolean DEBUG = false;

	private static final String TAG = "ParagraphViewTag";

	@NonNull
	private final TextureParagraph mRender;

	private final PaintSet mPaintSet;
	private RenderOption mRenderOption;
	private final Measurer mMeasurer;
	private final TextAttribute mTextAttribute;

	/*
	 * 只会在parse后被赋值
	 * */
	private Paragraph mParagraph;

	private OnClickedListener mOnClickedListener;

	private final SelectedTextByClickedVisitor mSelectedTextByClickedVisitor = new SelectedTextByClickedVisitor();

	private final Region mRegion = new Region();

	private SpanTouchEventHandler mSpanTouchEventHandler;

	private final SpanPredicate mOnSpanClickedPredicate = new SpanPredicate() {
		@Override
		public boolean accept(@Nullable Object clickedTag, @Nullable Object tag) {
			return mSpanTouchEventHandler.applySpanClicked(clickedTag, tag);
		}
	};
	private final SpanPredicate mOnSpanLongClickedPredicate = new SpanPredicate() {
		@Override
		public boolean accept(@Nullable Object clickedTag, @Nullable Object tag) {
			return mSpanTouchEventHandler.applySpanLongClicked(clickedTag, tag);
		}
	};

	private final ParseWorker.Listener mParseListener = new ParseWorker.Listener() {
		@Override
		public void onParseSuccess(Paragraph paragraph) {
			if (DEBUG) {
				Log.d(TAG, "onParseSuccess: " + paragraph);
			}

			mParagraph = paragraph;
			requestLayout();
		}

		@Override
		public void onParseFailure(Throwable throwable) {
			Log.w(TAG, throwable);
		}
	};

	public ParagraphView(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ParagraphView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.me_chan_texas_ParagraphView, defStyleAttr, 0);
		try {
			mRenderOption = createRenderOption(context, typedArray);
			mPaintSet = new PaintSet(mRenderOption);
			mMeasurer = new AndroidMeasurer(mPaintSet);
			mTextAttribute = new TextAttribute(mMeasurer);
			mRender = mRenderOption.isCompatMode() || Build.VERSION.SDK_INT < Build.VERSION_CODES.M ?
					new TextureParagraphView0Compat(context) : new TextureParagraphView0(context);
			addView((View) mRender, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			OnSelectedChangedListener onSelectedChangedListener = new OnSelectedChangedListener() {
				@Override
				public boolean onSegmentClicked(View source, MotionEvent e, Paragraph paragraph, int eventType) {
					return handleParagraphClicked(source, e, eventType);
				}

				@Override
				public boolean onBoxSelected(View source, MotionEvent e, Paragraph paragraph, @EventType int eventType, Box box) {
					return handleParagraphSelected(source, e, paragraph, eventType, box);
				}
			};
			mRender.setOnTextSelectedListener(onSelectedChangedListener);

			String text = typedArray.getString(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_text);
			if (!TextUtils.isEmpty(text)) {
				setText(text);
			}
			checkUIThreadPriority();
		} finally {
			typedArray.recycle();
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mParagraph == null) {
			return;
		}

		Layout layout = mParagraph.getLayout();
		if (layout.getWidth() < 0 || layout.getHeight() < 0) {
			return;
		}

		render0(mParagraph);
	}

	@Override
	protected void onDetachedFromWindow() {
		mRender.clear();
		super.onDetachedFromWindow();
	}

	private boolean handleParagraphSelected(View source, MotionEvent e, Paragraph paragraph, @OnSelectedChangedListener.EventType int eventType, Box box) {
		if (mOnClickedListener == null) {
			return false;
		}

		if (eventType == OnSelectedChangedListener.EVENT_CLICKED || eventType == OnSelectedChangedListener.EVENT_LONG_CLICKED) {
			boolean handled = handleParagraphSelected(source, e, paragraph, eventType == OnSelectedChangedListener.EVENT_LONG_CLICKED, box);
			if (!handled && eventType == OnSelectedChangedListener.EVENT_CLICKED) {
				TouchEvent event = TouchEvent.obtain(source, e);
				event.adjust(this);
				mOnClickedListener.onEmptyClicked(this, event);
				event.recycle();
				return true;
			}

			return false;
		}

		return false;
	}

	private boolean handleParagraphClicked(View source, MotionEvent e, int eventType) {
		if (mOnClickedListener == null) {
			return false;
		}

		if (eventType == OnSelectedChangedListener.EVENT_DOUBLE_CLICKED) {
			TouchEvent event = TouchEvent.obtain(source, e);
			event.adjust(this);
			mOnClickedListener.onDoubleClicked(this, event);
			event.recycle();
			return true;
		}

		if (eventType == OnSelectedChangedListener.EVENT_CLICKED) {
			TouchEvent event = TouchEvent.obtain(source, e);
			event.adjust(this);
			mOnClickedListener.onEmptyClicked(this, event);
			event.recycle();
		}

		return false;
	}

	private boolean handleParagraphSelected(View source, MotionEvent e, Paragraph paragraph, boolean isLongClicked, Box box) {
		// 1. clear prev selection
		clearSelection();

		SpanPredicate predicate = isLongClicked ? mOnSpanLongClickedPredicate : mOnSpanClickedPredicate;
		if (predicate == null) {
			return false;
		}

		try {
			boolean handled = handleParagraphSelected0(paragraph, isLongClicked, box, predicate);
			TouchEvent event = TouchEvent.obtain(source, e);
			event.adjust(this);
			if (handled) {
				if (isLongClicked) {
					if (mOnClickedListener != null) {
						mOnClickedListener.onSpanLongClicked(this, event, box.getTag());
					}
				} else {
					if (mOnClickedListener != null) {
						mOnClickedListener.onSpanClicked(this, event, box.getTag());
					}
				}
			} else {
				if (mOnClickedListener != null) {
					mOnClickedListener.onEmptyClicked(this, event);
				}
			}
			event.recycle();
		} catch (ParagraphVisitor.VisitException ex) {
			/* do nothing */
		}

		return true;
	}

	private boolean handleParagraphSelected0(Paragraph paragraph, boolean isLongClicked, Box box, SpanPredicate predicate) throws ParagraphVisitor.VisitException {
		try {
			mSelectedTextByClickedVisitor.reset(
					isLongClicked,
					mRenderOption
			);
			mSelectedTextByClickedVisitor.setPredicate(
					predicate,
					box.getTag()
			);

			// update ui
			mSelectedTextByClickedVisitor.startVisit(
					paragraph
			);
			render0(paragraph);

			return mSelectedTextByClickedVisitor.isHandled();
		} finally {
			mSelectedTextByClickedVisitor.clear();
		}
	}

	/**
	 * 清除选中区域
	 */
	public void clearSelection() {
		if (mParagraph == null) {
			return;
		}

		ParagraphSelection selection = mParagraph.getSelection();
		if (selection == null) {
			return;
		}

		mParagraph.setSelection(null);
		selection.recycle();

		if (mParagraph != null) {
			render0(mParagraph);
		}
	}

	/**
	 * @param widthMeasureSpec  horizontal space requirements as imposed by the parent.
	 *                          The requirements are encoded with
	 *                          {@link android.view.View.MeasureSpec}.
	 * @param heightMeasureSpec vertical space requirements as imposed by the parent.
	 *                          The requirements are encoded with
	 *                          {@link android.view.View.MeasureSpec}.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mParagraph == null) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}

		int expectedWidthMode = MeasureSpec.getMode(widthMeasureSpec);
		int expectedHeightMode = MeasureSpec.getMode(heightMeasureSpec);
		int expectedWidth = MeasureSpec.getSize(widthMeasureSpec);
		int expectedHeight = MeasureSpec.getSize(heightMeasureSpec);

		int width = expectedWidth;
		if (expectedWidthMode == MeasureSpec.EXACTLY ||
				expectedWidthMode == MeasureSpec.AT_MOST) {
			if (!typeset0(expectedWidth - getPaddingLeft() - getPaddingRight())) {
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				return;
			}
		} else {
			mRegion.setWidth(0);
			mRegion.setHeight(0);
			if (!WorkerScheduler.typeset().desire(mParagraph, mRegion, mRenderOption) || mRegion.getWidth() <= 0 || mRegion.getHeight() <= 0) {
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				return;
			}
			width = mRegion.getWidth() + getPaddingLeft() + getPaddingRight();
		}

		Layout layout = mParagraph.getLayout();
		int height = layout.getHeight();
		if (expectedHeightMode == MeasureSpec.EXACTLY) {
			height = expectedHeight;
		} else if (expectedHeightMode == MeasureSpec.AT_MOST) {
			height = Math.min(height, expectedHeight);
		}

		super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onMeasure: widthSpec = " + MeasureSpec.toString(widthMeasureSpec) +
					", heightSpec = " + MeasureSpec.toString(heightMeasureSpec) +
					", width = " + getMeasuredWidth() +
					", height = " + getMeasuredHeight() +
					", tag = " + getTag());
		}
	}

	private boolean typeset0(int width) {
		try {
			ParagraphTypesetWorker worker = WorkerScheduler.typeset();
			ParagraphTypesetWorker.Args args = ParagraphTypesetWorker.Args.obtain(mParagraph, mRenderOption, width);
			worker.submitSync(mRender.getToken(), args);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	public void setSpanTouchEventHandler(@Nullable SpanTouchEventHandler spanTouchEventHandler) {
		mSpanTouchEventHandler = spanTouchEventHandler;
	}

	private void render0(Paragraph paragraph) {
		if (DEBUG) {
			Log.d(TAG, "render0: paragraph = " + paragraph);
		}

		mRender.render(paragraph, mPaintSet, mRenderOption, null, mSpanTouchEventHandler);
	}

	/**
	 * 因为需要动态调整paragraph的宽度，所以需要在onLayout中重新调整paragraph的宽度
	 *
	 * @param changed This is a new size or position for this view
	 * @param left    Left position, relative to parent
	 * @param top     Top position, relative to parent
	 * @param right   Right position, relative to parent
	 * @param bottom  Bottom position, relative to parent
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (DEBUG) {
			Log.d(TAG, "onLayout: changed = " + mParagraph);
		}

		super.onLayout(changed, left, top, right, bottom);
		onLayout0(left, right);
	}

	private void onLayout0(int left, int right) {
		if (mParagraph == null) {
			return;
		}

		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();

		int width = right - left - paddingLeft - paddingRight;
		Layout layout = mParagraph.getLayout();

		// 因为 padding 发生了变化
		if (layout.getWidth() != width && !typeset0(width)) {
			return;
		}

		render0(mParagraph);
	}

	private ParagraphSource mSource;

	/**
	 * @param source 段落源
	 */
	public void setSource(@NonNull ParagraphSource source) {
		if (DEBUG) {
			Log.d(TAG, "setSource: source = " + source);
		}

		// 丢弃之前的任务
		discard(false);

		// 清除之前的 selection
		clearSelection();

		// 赋予
		source.owner = this;

		// cache last source
		mSource = source;

		// 提交解析任务
		ParseWorker.Args args = ParseWorker.Args.obtain(source, LoadingStrategy.LOAD_MORE, mParseListener);
		ParseWorker worker = WorkerScheduler.parse();
		if (!isInEditMode()) {
			worker.submit(mRender.getToken(), args);
			return;
		}

		try {
			Paragraph paragraph = worker.submitSync(mRender.getToken(), args);
			mParseListener.onParseSuccess(paragraph);
		} catch (Throwable e) {
			mParseListener.onParseFailure(e);
		}
	}

	public void setText(@NonNull CharSequence text) {
		setText(text, 0, text.length());
	}

	public void setText(@NonNull CharSequence text, int start, int end) {
		if (mSource != null && mSource instanceof TextParagraphSource) {
			TextParagraphSource source = (TextParagraphSource) mSource;
			if (source.mText == text && source.mStart == start && source.mEnd == end) {
				return;
			}
		}

		setSource(new TextParagraphSource(text, start, end));
	}

	/**
	 * 创建一个新的渲染参数 结束后调用 {@link TexasView#refresh(RenderOption)} 刷新样式
	 * 这个操作是批量的，所以效率更高
	 *
	 * @return option 批量修改属性
	 */
	@NonNull
	public RenderOption createRendererOption() {
		return new RenderOption(mRenderOption);
	}

	/**
	 * 刷新内容，可能会重新排版 {@link TexasView#redraw()}
	 *
	 * @param renderOption option
	 */
	public void refresh(@NonNull RenderOption renderOption) {
		int cmpType = TexasUtils.cmp(mRenderOption, renderOption);

		mRenderOption = renderOption;
		mPaintSet.refresh(renderOption);

		if (cmpType == TexasUtils.CmpType.CMP_LOAD) {
			// 丢弃之前的任务
			discard(false);

			// 清除之前的 selection
			clearSelection();

			// 提交解析任务
			ParseWorker.Args args = ParseWorker.Args.obtain(mSource, LoadingStrategy.INIT, mParseListener);
			ParseWorker worker = WorkerScheduler.parse();
			worker.submit(mRender.getToken(), args);
		} else if (cmpType == TexasUtils.CmpType.CMP_TYPESET) {
			int width = getWidth() - getPaddingLeft() - getPaddingRight();
			if (width > 0) {
				typeset0(width);
			}
			return;
		}

		if (cmpType != TexasUtils.CmpType.CMP_DRAW) {
			throw new IllegalStateException("unknown cmp type: " + cmpType);
		}

		if (mParagraph != null) {
			render0(mParagraph);
		}
	}

	/**
	 * 只是简单的重新绘制内容
	 */
	public void redraw() {
		if (mParagraph != null) {
			render0(mParagraph);
		}
	}

	/**
	 * 丢弃当前的所有内容
	 */
	public void discard() {
		discard(true);
	}

	private void discard(boolean releaseBuffer) {
		if (releaseBuffer) {
			mRender.clear();
		}
		WorkerScheduler.cancelAll(mRender.getToken());
	}

	/**
	 * @param onClickedListener 设置点击事件
	 */
	public void setOnClickedListener(OnClickedListener onClickedListener) {
		mOnClickedListener = onClickedListener;
	}

	/**
	 * 设置paragraph source
	 */
	public static abstract class ParagraphSource extends Source<Paragraph> {

		@Internal
		private ParagraphView owner;

		@Override
		protected Paragraph onOpen(LoadingStrategy strategy) throws SourceOpenException {
			// 选择断字策略
			Hyphenation hyphenation = null;
			HyphenStrategy hyphenStrategy = owner.mRenderOption.getHyphenStrategy();
			if (hyphenStrategy == HyphenStrategy.US) {
				hyphenation = Hyphenation.getInstance(HyphenationPattern.EN_US);
			} else if (hyphenStrategy == HyphenStrategy.UK) {
				hyphenation = Hyphenation.getInstance(HyphenationPattern.EN_GB);
			} else {
				throw new IllegalArgumentException("unknown hyphen strategy");
			}

			TexasOption texasOption = new TexasOption(hyphenation, owner.mMeasurer, owner.mTextAttribute, owner.mRenderOption);
			return onOpen(texasOption);
		}

		/**
		 * see {@link Paragraph.Builder#newBuilder(TexasOption)} for more information
		 *
		 * @param option option
		 * @return paragraph
		 */
		@AnyThread
		protected abstract Paragraph onOpen(TexasOption option);
	}

	/**
	 * 点击事件
	 */
	public interface OnClickedListener {
		/**
		 * @param paragraphView 被点击的段落
		 * @param tag           被点击的text tag
		 */
		void onSpanClicked(ParagraphView paragraphView, TouchEvent event, Object tag);

		/**
		 * @param paragraphView 被点击的段落
		 * @param tag           被点击的text tag
		 */
		void onSpanLongClicked(ParagraphView paragraphView, TouchEvent event, Object tag);

		/**
		 * @param paragraphView 被点击的段落
		 */
		void onEmptyClicked(ParagraphView paragraphView, TouchEvent event);

		/**
		 * @param paragraphView 被点击的段落
		 */
		void onDoubleClicked(ParagraphView paragraphView, TouchEvent event);
	}

	private RenderOption createRenderOption(Context context, TypedArray typedArray) {
		Resources resources = getResources();
		RenderOption renderOption = new RenderOption();

		// 设置字体颜色
		renderOption.setTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_textColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_text_color)
				)
		);

		// 设置字体
		renderOption.setTypeface(Texas.getDefaultTypeface());
		String typefacePath = typedArray.getString(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_typefaceAssets);
		if (!TextUtils.isEmpty(typefacePath)) {
			WeakReference<Typeface> typefaceWeakReference = TexasView.TYPEFACE_CACHE.get(typefacePath);
			Typeface typeface;
			if (typefaceWeakReference != null && (typeface = typefaceWeakReference.get()) != null) {
				renderOption.setTypeface(typeface);
			} else {
				typeface = TexasUtils.createTypefaceFromAsset(context, typefacePath);
				typefaceWeakReference = new WeakReference<>(typeface);
				TexasView.TYPEFACE_CACHE.put(typefacePath, typefaceWeakReference);
				renderOption.setTypeface(typeface);
			}
		}

		// 设置字体大小
		renderOption.setTextSize(
				typedArray.getDimension(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_textSize,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_SP,
								TexasView.DEFAULT_TEXT_SIZE,
								resources.getDisplayMetrics()
						)
				)
		);

		// 行间距
		renderOption.setLineSpace(
				typedArray.getDimension(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_lineSpace,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP,
								TexasView.DEFAULT_LINE_SPACE,
								resources.getDisplayMetrics()
						)
				)
		);

		// 选中字体的背景色
		renderOption.setSelectedBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_selectedBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_theme_color)
				)
		);

		// 选中字体的颜色
		renderOption.setSelectedTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_selectedTextColor, Color.WHITE)
		);

		// 选中span的背景色
		renderOption.setSelectedByLongClickBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_spanSelectedBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_span_bg_color)
				)
		);

		// 选中span的字体颜色
		renderOption.setSelectedByLongClickTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_spanSelectedTextColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_text_color))
		);

		// 断字策略
		int breakStrategy = typedArray.getInt(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_breakStrategy, TexasView.BREAK_STRATEGY_BALANCE);
		renderOption.setBreakStrategy(
				breakStrategy == TexasView.BREAK_STRATEGY_SIMPLE ?
						BreakStrategy.SIMPLE : BreakStrategy.BALANCED
		);

		// 是否可选单词
		renderOption.setWordSelectable(
				typedArray.getBoolean(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_wordSelectable, true)
		);

		// 断字策略
		int hyphenStrategy = typedArray.getInt(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_hyphenStrategy, TexasView.HYPHEN_STRATEGY_US);
		renderOption.setHyphenStrategy(
				hyphenStrategy == TexasView.HYPHEN_STRATEGY_UK ?
						HyphenStrategy.UK : HyphenStrategy.US
		);

		// lazy 渲染模式优化
		renderOption.setEnableLazyRender(
				false
		);

		// 高亮span文字颜色
		renderOption.setSpanHighlightTextColor(
				ContextCompat.getColor(context, R.color.me_chan_texas_theme_color)
		);

		// 加载中背景色
		renderOption.setLoadingBackgroundColor(
				ContextCompat.getColor(context, R.color.me_chan_texas_loading_bg)
		);

		// 自由划线水滴颜色
		renderOption.setDragViewColor(
				ContextCompat.getColor(context, R.color.me_chan_texas_drag_view_color)
		);

		// 设置选中圆角半径
		renderOption.setSelectedBackgroundRoundRadius(
				typedArray.getDimension(
						R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_selectedBackgroundRoundRadius,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP,
								3,
								getResources().getDisplayMetrics()
						)
				)
		);

		// 是否开启兼容模式
		renderOption.setCompatMode(
				typedArray.getBoolean(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_compatMode, false)
		);

		return renderOption;
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

	private static class TextParagraphSource extends ParagraphSource {
		private final CharSequence mText;
		private final int mStart;
		private final int mEnd;
		private Paragraph mParagraph;

		public TextParagraphSource(CharSequence text, int start, int end) {
			mText = text;
			mStart = start;
			mEnd = end;
		}

		@Override
		protected Paragraph onOpen(TexasOption option) {
			if (mParagraph != null) {
				return mParagraph;
			}

			return mParagraph = Paragraph.Builder.newBuilder(option)
					.text(mText, mStart, mEnd)
					.build();
		}

		@Override
		protected void onClose() throws SourceCloseException {
			/* do nothing */
		}
	}
}
