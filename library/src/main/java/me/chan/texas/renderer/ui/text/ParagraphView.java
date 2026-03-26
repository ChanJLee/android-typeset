package me.chan.texas.renderer.ui.text;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;

import me.chan.texas.R;
import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.measurer.MeasureFactory;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.worker.LoadingWorker;
import me.chan.texas.renderer.core.worker.ParseWorker;
import me.chan.texas.renderer.core.worker.ParagraphTypesetWorker;
import me.chan.texas.renderer.SpanPredicate;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.renderer.selection.visitor.PredicatesDriveSelectedVisitor;
import me.chan.texas.renderer.selection.visitor.SelectedTextByClickedVisitor;
import me.chan.texas.renderer.ui.RendererHost;
import me.chan.texas.source.Source;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.HyphenStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.renderer.selection.SelectionMethod;
import me.chan.texas.renderer.selection.SelectionProvider;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextGravity;
import me.chan.texas.text.layout.Span;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.utils.TexasUtils;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

/**
 * 用于显示文本内容
 * <p>
 * 当前内容都是异步渲染的，所以当你不需要显示某个内容的时候，就调用 {@link #discard()} 丢弃之前的任务
 */
public class ParagraphView extends FrameLayout {
	public static final boolean DEBUG = false;
	private static final String TAG = "ParagraphView";

	@NonNull
	private final TextureParagraph mRender;

	private RenderOption mRenderOption;

	private int mMaxLines = Integer.MAX_VALUE;
	private int mMinLines = 0;

	private boolean mOverrideStyles = false;

	/*
	 * 只会在parse后被赋值
	 * */
	private Paragraph mParagraph;

	private OnClickedListener mOnClickedListener;

	private final SelectedTextByClickedVisitor mSelectedTextByClickedVisitor = new SelectedTextByClickedVisitor();

	/**
	 * 用于自驱式的选中文本
	 * <p>
	 * 即主动调用 {@link TexasView#selectParagraphs} 接口，而不是通过点击操作
	 */
	private final PredicatesDriveSelectedVisitor mPredicatesDriveSelectedVisitor = new PredicatesDriveSelectedVisitor();

	private SpanTouchEventHandler mSpanTouchEventHandler;

	private final OnSelectedChangedListener onSelectedChangedListener = new OnSelectedChangedListener() {
		@Override
		public boolean onParagraphSelected(TouchEvent e, Paragraph paragraph, int eventType) {
			return handleParagraphClicked(e, eventType);
		}

		@Override
		public boolean onBoxSelected(TouchEvent e, Paragraph paragraph, @EventType int eventType, Span span) {
			return handleParagraphSelected(e, paragraph, eventType, span);
		}
	};
	private SelectionMethod mSelectionMethod = new SelectionMethod() {
		@NonNull
		@Override
		public SpanTouchEventHandler getSpanTouchEventHandler() {
			return mSpanTouchEventHandler;
		}

		@NonNull
		@Override
		public OnSelectedChangedListener getOnSelectedChangedListener() {
			return onSelectedChangedListener;
		}
	};

	private final SpanPredicate mOnSpanClickedPredicate = new SpanPredicate() {
		@Override
		public boolean accept(@Nullable Span clicked, @Nullable Span other) {
			return mSpanTouchEventHandler.applySpanClicked(clicked, other);
		}
	};
	private final SpanPredicate mOnSpanLongClickedPredicate = new SpanPredicate() {
		@Override
		public boolean accept(@Nullable Span clicked, @Nullable Span other) {
			return mSpanTouchEventHandler.applySpanLongClicked(clicked, other);
		}
	};

	private final ParseWorker.Listener mParseListener = new ParseWorker.Listener() {
		@Override
		public void onParseSuccess(Paragraph paragraph) {
			if (DEBUG) {
				Log.d(TAG, "onParseSuccess: " + paragraph);
			}

			mParagraph = paragraph;
			paragraph.bind(mHost);
			requestLayout();
		}

		@Override
		public void onParseFailure(Throwable throwable) {
			Log.w(TAG, throwable);
			if (mRenderListener != null) {
				mRenderListener.onError(ParagraphView.this, throwable);
			}
		}
	};

	private final RendererHost mHost = new RendererHost() {
		@Override
		public void updateSegment(Object unit, Segment segment) {
			if (segment == mParagraph) {
				redraw();
			}
		}
	};
	private RenderListener mRenderListener;

	public ParagraphView(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@Inject
	MeasureFactory mMeasureFactory;

	private final PaintSet mUiThreadPaintSet;

	public ParagraphView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.me_chan_texas_ParagraphView, defStyleAttr, 0);
		try {
			mRenderOption = createRenderOption(context, typedArray);
			mUiThreadPaintSet = new PaintSet(mRenderOption);
			setMaxLines0(typedArray.getInt(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_maxLines, Integer.MAX_VALUE));
			setMinLines0(typedArray.getInt(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_minLines, 0));
			if (typedArray.hasValue(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_lines)) {
				setLines0(typedArray.getInt(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_lines, 0));
			}
			AbsTextureParagraphView.LayoutPredicate relayoutPredicate = (view, paragraph) -> {
				ViewGroup.LayoutParams layoutParams = getLayoutParams();
				if (layoutParams == null) {
					return true;
				}

				Layout layout = paragraph.getLayout();
				if (getLayoutHeight(layout) != view.getHeight()) {
					return layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT;
				}

				return false;
			};
			mRender = mRenderOption.isCompatMode() ? new TextureParagraphView0Compat(context, relayoutPredicate) : new TextureParagraphView0(context, relayoutPredicate);
			addView((View) mRender, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			mRender.setOnMeasureInterceptor(this::handleMeasureRenderer);
			mRender.setRendererListener(this::handleRendererSuccess);
			setVerticalAlignment(mRenderOption);

			TexasComponent texasComponent = Texas.getTexasComponent();
			TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
			textEngineCoreComponent.inject(this);

			String text = typedArray.getString(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_text);
			if (!TextUtils.isEmpty(text)) {
				setText(text);
			}

			mOverrideStyles = typedArray.getBoolean(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_overrideStyles, mOverrideStyles);
		} finally {
			typedArray.recycle();
		}
	}

	private void handleRendererSuccess(TextureParagraph textureParagraph) {
		Paragraph paragraph = textureParagraph.getParagraph();
		if (paragraph == null) {
			return;
		}

		if (mRenderListener != null) {
			mRenderListener.onRender(this, paragraph);
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
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (DEBUG) {
			Log.d(TAG, "onSizeChanged: " + w + "x" + h);
		}
	}

	private boolean handleParagraphSelected(TouchEvent event, Paragraph paragraph, @OnSelectedChangedListener.EventType int eventType, Span span) {
		if (mOnClickedListener == null) {
			return false;
		}

		if (eventType == OnSelectedChangedListener.EVENT_CLICKED || eventType == OnSelectedChangedListener.EVENT_LONG_CLICKED) {
			boolean handled = handleParagraphSelected(event, paragraph, eventType == OnSelectedChangedListener.EVENT_LONG_CLICKED, span);
			if (!handled && eventType == OnSelectedChangedListener.EVENT_CLICKED) {
				event.adjust(this);
				mOnClickedListener.onEmptyClicked(this, event);
				event.recycle();
				return true;
			}

			return false;
		}

		return false;
	}

	private boolean handleParagraphClicked(TouchEvent event, int eventType) {
		if (mOnClickedListener == null) {
			return false;
		}

		if (eventType == OnSelectedChangedListener.EVENT_DOUBLE_CLICKED) {
			event.adjust(this);
			mOnClickedListener.onDoubleClicked(this, event);
			event.recycle();
			return true;
		}

		if (eventType == OnSelectedChangedListener.EVENT_CLICKED) {
			event.adjust(this);
			mOnClickedListener.onEmptyClicked(this, event);
			event.recycle();
		}

		return false;
	}

	private boolean handleParagraphSelected(TouchEvent event, Paragraph paragraph, boolean isLongClicked, Span span) {
		// 1. clear prev selection
		clearSelection();

		SpanPredicate predicate = isLongClicked ? mOnSpanLongClickedPredicate : mOnSpanClickedPredicate;
		if (predicate == null) {
			return false;
		}

		try {
			boolean handled = handleParagraphSelected0(paragraph, isLongClicked, span, predicate);
			event.adjust(this);
			if (handled) {
				if (isLongClicked) {
					if (mOnClickedListener != null) {
						mOnClickedListener.onSpanLongClicked(this, event, span);
					}
				} else {
					if (mOnClickedListener != null) {
						mOnClickedListener.onSpanClicked(this, event, span);
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

	private boolean handleParagraphSelected0(Paragraph paragraph, boolean isLongClicked, Span span, SpanPredicate predicate) throws ParagraphVisitor.VisitException {
		try {
			mSelectedTextByClickedVisitor.reset(
					Selection.Type.SELECTION,
					Selection.Styles.createFromTouch(mRenderOption, isLongClicked)
							.setEnableDrag(false),
					paragraph,
					mRenderOption
			);
			mSelectedTextByClickedVisitor.setPredicate(
					predicate,
					span
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

		ParagraphSelection selection = mParagraph.getSelection(Selection.Type.SELECTION);
		if (selection == null) {
			return;
		}

		mParagraph.setSelection(Selection.Type.SELECTION, null);
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
		if (DEBUG) {
			Log.d(TAG, "onMeasure: widthSpec = " + MeasureSpec.toString(widthMeasureSpec) +
					", heightSpec = " + MeasureSpec.toString(heightMeasureSpec) +
					", tag = " + getTag());
		}

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (mParagraph == null) {
			if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			} else {
				super.onMeasure(
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
				);
			}
			if (DEBUG) {
				Log.d(TAG, "paragraph is null, width = " + getMeasuredWidth() +
						", height = " + getMeasuredHeight() +
						", tag = " + getTag());
			}
			return;
		}

		int width = MeasureSpec.getSize(widthMeasureSpec);
		if (widthMode == MeasureSpec.UNSPECIFIED) {
			width = Integer.MAX_VALUE;
		}

		long ts = DEBUG ? SystemClock.elapsedRealtime() : 0;
		boolean typesetResult = typeset0(width - getPaddingLeft() - getPaddingRight());
		if (DEBUG) {
			Log.d(TAG, "desire paragraph, width = " + width + ", cost = " + (SystemClock.elapsedRealtime() - ts));
		}

		if (heightMode != MeasureSpec.EXACTLY) {
			if (DEBUG) {
				Log.d(TAG, "try to desire paragraph, width = " + width);
			}

			if (typesetResult) {
				Layout layout = mParagraph.getLayout();
				int layoutHeight = layout.getHeight();
				if (DEBUG) {
					Log.d(TAG, "paragraph is desired, width = " + width + ", height = " + layoutHeight);
				}
				int height = layoutHeight + getPaddingTop() + getPaddingBottom();
				height = heightMode == MeasureSpec.AT_MOST ? Math.min(height, MeasureSpec.getSize(heightMeasureSpec)) : height;
				heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
			}
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (DEBUG) {
			Log.d(TAG, "width = " + getMeasuredWidth() +
					", height = " + getMeasuredHeight() +
					", tag = " + getTag());
		}
	}

	private boolean handleMeasureRenderer(OnMeasureInterceptor.MeasureSpecs specs) {
		if (mParagraph == null) {
			return false;
		}

		Layout layout = mParagraph.getLayout();
		if (!layout.isLayout()) {
			Log.d(TAG, "paragraph is not layout, ignore intercept measure");
			return false;
		}

		int height = getLayoutHeight(layout);
		int exceptedHeight = MeasureSpec.getSize(specs.heightSpec);
		int heightMode = MeasureSpec.getMode(specs.heightSpec);
		if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(height, exceptedHeight);
		} else if (heightMode == MeasureSpec.EXACTLY) {
			height = exceptedHeight;
		}
		specs.heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		return true;
	}

	private int getLayoutHeight(Layout layout) {
		int height = layout.getHeight();
		int lineCount = layout.getLineCount();
		if (DEBUG) {
			Log.d("ParagraphView", "getLayoutHeight, height = " + height + ", lineCount = " + lineCount + ", minLines = " + mMinLines + ", maxLines = " + mMaxLines);
		}
		if (lineCount >= mMinLines && lineCount <= mMaxLines) {
			return height;
		}

		float minHeight = getSuggestedMinHeight(height, layout);
		float maxHeight = getSuggestedMaxHeight(height, layout);
		if (DEBUG) {
			Log.d("ParagraphView", "getLayoutHeight, minHeight = " + minHeight + ", maxHeight = " + maxHeight);
		}

		if (height < minHeight) {
			return (int) Math.ceil(minHeight);
		}
		return (int) Math.ceil(maxHeight);
	}

	private float getSuggestedMaxHeight(int defaultHeight, Layout layout) {
		int lineCount = layout.getLineCount();
		if (lineCount <= mMaxLines) {
			return defaultHeight;
		}

		Line first = layout.getLine(0);
		Line last = layout.getLine(mMaxLines - 1);
		return (int) Math.ceil(last.getBounds().bottom - first.getBounds().top);
	}

	private int getSuggestedMinHeight(int defaultHeight, Layout layout) {
		int lineCount = layout.getLineCount();
		if (lineCount >= mMinLines) {
			return defaultHeight;
		}

		return (int) Math.ceil(defaultHeight * (mMinLines * 1.0f / lineCount));
	}

	private void setVerticalAlignment(RenderOption renderOption) {
		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mRender.getLayoutParams();
		if (layoutParams == null) {
			return;
		}

		int textGravity = renderOption.getTextGravity() & TextGravity.VERTICAL_MASK;
		if (textGravity == TextGravity.CENTER_VERTICAL) {
			layoutParams.gravity = Gravity.CENTER_VERTICAL;
		} else if (textGravity == TextGravity.TOP) {
			layoutParams.gravity = Gravity.TOP;
		} else if (textGravity == TextGravity.BOTTOM) {
			layoutParams.gravity = Gravity.BOTTOM;
		}
		mRender.setLayoutParams(layoutParams);
	}

	private boolean typeset0(int width) {
		try {
			ParagraphTypesetWorker worker = WorkerScheduler.typeset();
			Layout layout = mParagraph.getLayout();
			int exceptedWidth = width - layout.getPaddingRight() - layout.getPaddingLeft();
			if (layout.isLayout() && layout.getWidth() == exceptedWidth) {
				return true;
			}

			RenderOption option = mRenderOption;
			PaintSet paintSet = new PaintSet(option);
			Measurer measurer = mMeasureFactory.create(paintSet);
			Measurer.CharSequenceSpec spec = measurer.getBaseSpec();
			worker.desire(mParagraph, mRenderOption, exceptedWidth, spec.getHeight());
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

		mRender.render(paragraph, mUiThreadPaintSet, mRenderOption, mSelectionMethod);
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
			Log.d(TAG, "onLayout, changed " + changed + ", left " + left + ", top " + top + ", right " + right + ", bottom " + bottom + ", " + mParagraph);
		}

		super.onLayout(changed, left, top, right, bottom);
		onLayout0(left, right);
	}

	private void onLayout0(int left, int right) {
		if (mParagraph == null) {
			return;
		}

		Layout layout = mParagraph.getLayout();
		if (!layout.isLayout()) {
			return;
		}

		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();

		int width = right - left - paddingLeft - paddingRight;
		if (DEBUG) {
			Log.d(TAG, "onLayout0: width = " + width + ", layout width = " + layout.getWidth());
		}

		// 因为 padding 发生了变化
		if (layout.getWidth() != width) {
			Log.w(TAG, "paragraph width is changed, from " + layout.getWidth() + " to " + width + ", missing call onMeasure");
			typeset0(width);
		}

		render0(mParagraph);
	}

	private ParagraphSource mSource;

	private TexasOption createTexasOption() {
		RenderOption option = mRenderOption;
		PaintSet paintSet = new PaintSet(option);
		Measurer measurer = mMeasureFactory.create(paintSet);
		TextAttribute textAttribute = new TextAttribute(measurer);
		return LoadingWorker.createTexasOption(paintSet, textAttribute, measurer, option);
	}

	/**
	 * @param paragraph 直接设置paragraph
	 */
	public void setParagraph(Paragraph paragraph) {
		setSource(new DirectParagraphSource(paragraph));
	}

	/**
	 * @param source 段落源
	 */
	public void setSource(@NonNull ParagraphSource source) {
		if (DEBUG) {
			Log.d(TAG, "setSource: source = " + source);
		}

		// 丢弃之前的任务
		discard(false);

		// cache last source
		mSource = source;
		source.attach(this);

		// 提交解析任务
		if (mRenderListener != null) {
			mRenderListener.onStart(this, source);
		}
		ParseWorker.Args args = ParseWorker.Args.obtain(source, mParseListener);
		ParseWorker worker = WorkerScheduler.parse();
		if (!isInEditMode() || !(source instanceof DirectParagraphSource)) {
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
		if (cmpType == TexasUtils.CmpType.CMP_IGNORE) {
			return;
		}

		mRenderOption = renderOption;
		mUiThreadPaintSet.refresh(renderOption);
		if (mParagraph != null) {
			Layout layout = mParagraph.getLayout();
			Layout.Advise advise = layout.getAdvise();
			advise.copy(renderOption);
		}
		setVerticalAlignment(renderOption);

		if (mSource == null) {
			return;
		}
		if (cmpType != TexasUtils.CmpType.CMP_DRAW) {
			// 丢弃之前的任务
			discard(false);

			// 提交解析任务
			ParseWorker.Args args = ParseWorker.Args.obtain(mSource, mParseListener);
			ParseWorker worker = WorkerScheduler.parse();
			worker.submit(mRender.getToken(), args);
			return;
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

	@Nullable
	public Selection getSelection() {
		return getSelection(Selection.Type.SELECTION);
	}

	@Nullable
	public Selection getHighlight() {
		return getSelection(Selection.Type.HIGHLIGHT);
	}

	private Selection getSelection(Selection.Type type) {
		if (mParagraph == null) {
			return null;
		}

		ParagraphSelection paragraphSelection = mParagraph.getSelection(type);
		if (paragraphSelection == null) {
			return null;
		}

		Selection.Styles styles = paragraphSelection.getSelectionStyle();
		if (styles == null) {
			return null;
		}

		Selection selection = Selection.obtain(type, styles);
		selection.add(paragraphSelection);
		return selection;
	}

	/**
	 * 高亮paragraph中的文本，只在渲染出document后生效
	 *
	 * @param predicates 谓词
	 * @return 选中区域
	 */
	@Nullable
	public Selection highlightParagraphs(ParagraphPredicates predicates) {
		return highlightParagraphs(predicates, null);
	}

	/**
	 * 高亮paragraph中的文本，只在渲染出document后生效
	 *
	 * @param predicates 谓词
	 * @param styles     {@link Selection.Styles#create(int, int)}
	 * @return 选中区域
	 */
	@Nullable
	public Selection highlightParagraphs(ParagraphPredicates predicates, Selection.Styles styles) {
		if (mParagraph == null) {
			return null;
		}

		if (styles == null) {
			styles = Selection.Styles.createFromHighLight(mRenderOption).setEnableDrag(false);
		}

		try {
			mPredicatesDriveSelectedVisitor.reset(Selection.Type.HIGHLIGHT, mRenderOption, predicates, mParagraph, styles);
			mPredicatesDriveSelectedVisitor.startVisit(mParagraph);
			ParagraphSelection paragraphSelection = mParagraph.getSelection(Selection.Type.HIGHLIGHT);
			if (paragraphSelection != null) {
				Selection selection = Selection.obtain(Selection.Type.HIGHLIGHT, styles);
				selection.add(paragraphSelection);
				return selection;
			}
		} catch (ParagraphVisitor.VisitException ignored) {
			/* do nothing */
		} finally {
			mPredicatesDriveSelectedVisitor.clear();
		}

		return null;
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

	public void setMinLines(int lines) {
		if (mMinLines == lines) {
			return;
		}

		setMinLines0(lines);
		requestLayout();
	}

	private void setMinLines0(int lines) {
		if (lines < 0) {
			throw new IllegalArgumentException("lines must be >= 0");
		}
		if (lines > mMaxLines) {
			throw new IllegalArgumentException("lines must be <= maxLines");
		}

		mMinLines = lines;
	}

	public void setMaxLines(int lines) {
		if (mMaxLines == lines) {
			return;
		}

		setMaxLines0(lines);
		requestLayout();
	}

	private void setMaxLines0(int lines) {
		if (lines < 0) {
			throw new IllegalArgumentException("lines must be >= 0");
		}
		if (lines < mMinLines) {
			throw new IllegalArgumentException("lines must be >= minLines");
		}

		mMaxLines = lines;
	}

	private void setLines0(int lines) {
		setMinLines0(lines);
		setMaxLines0(lines);
	}

	public void setLines(int lines) {
		setLines0(lines);
		requestLayout();
	}

	public int getMaxLines() {
		return mMaxLines;
	}

	public int getMinLines() {
		return mMinLines;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void setSelectionMethod(@NonNull SelectionMethod selectionMethod) {
		mSelectionMethod = selectionMethod;
		mRender.setSelectionMethod(selectionMethod);
	}

	/**
	 * @return 是否允许在嵌入到 {@link TexasView} 中显示的时候，使用 {@link TexasView} 的样式，默认 false
	 * {@link SelectionProvider}
	 */
	public boolean isOverrideStyles() {
		return mOverrideStyles;
	}

	/**
	 * 点击事件
	 */
	public interface OnClickedListener {
		/**
		 * @param paragraphView 被点击的段落
		 * @param event         event
		 * @param span          clicked span
		 */
		void onSpanClicked(ParagraphView paragraphView, TouchEvent event, Span span);

		/**
		 * @param paragraphView 被点击的段落
		 * @param event         event
		 * @param span          clicked span
		 */
		void onSpanLongClicked(ParagraphView paragraphView, TouchEvent event, Span span);

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

		TypedArray themeArray = context.obtainStyledAttributes(new int[]{
				android.R.attr.textColorPrimary,
				android.R.attr.textSize
		});
		int defaultTextColor = themeArray.getColor(0, Color.BLACK);
		int defaultTextSize = themeArray.getDimensionPixelSize(1, 48);
		renderOption.setTextColor(defaultTextColor);
		renderOption.setTextSize(defaultTextSize);
		themeArray.recycle();

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
				typedArray.getDimensionPixelSize(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_textSize,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_SP,
								TexasView.DEFAULT_TEXT_SIZE,
								resources.getDisplayMetrics()
						)
				)
		);

		// 行间距
		renderOption.setLineSpacingExtra(
				typedArray.getDimensionPixelSize(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_lineSpacingExtra, 0)
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

		// 文字居中形式
		int textGravity = typedArray.getInt(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_textGravity, TextGravity.TOP | TextGravity.START);
		renderOption.setTextGravity(textGravity);

		// 开启debug
		renderOption.setDebugEnable(
				typedArray.getBoolean(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_debugEnable, false)
		);

		// 开启双向文本
		renderOption.setBidiEnable(
				typedArray.getBoolean(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_bidiEnable, false)
		);

		return renderOption;
	}

	public void setRenderListener(RenderListener renderListener) {
		mRenderListener = renderListener;
	}

	@NonNull
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public TextureParagraph getRender() {
		return mRender;
	}

	/**
	 * 设置paragraph source
	 */
	public static abstract class ParagraphSource extends Source<Paragraph> {
		private ParagraphView mParagraphView;

		private void attach(ParagraphView paragraphView) {
			mParagraphView = paragraphView;
		}

		@Nullable
		@Override
		protected final Paragraph onRead() {
			if (mParagraphView == null) {
				return null;
			}

			TexasOption option = mParagraphView.createTexasOption();
			Paragraph paragraph = onRead(mParagraphView.createTexasOption());
			Layout layout = paragraph.getLayout();
			if (!layout.isLayout() || layout.getAdvise().isModified(mParagraphView.mRenderOption)) {
				paragraph.measure(option.getMeasurer(), option.getTextAttribute());
			}
			return paragraph;
		}

		protected abstract Paragraph onRead(TexasOption option);
	}

	private static class DirectParagraphSource extends ParagraphSource {
		private final Paragraph mParagraph;

		public DirectParagraphSource(Paragraph paragraph) {
			mParagraph = paragraph;
		}

		@Override
		protected Paragraph onRead(TexasOption option) {
			return mParagraph;
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
		protected Paragraph onRead(TexasOption option) {
			if (mParagraph != null) {
				return mParagraph;
			}

			return mParagraph = Paragraph.Builder.newBuilder(option)
					.text(mText, mStart, mEnd)
					.build();
		}
	}

	/**
	 * Render listener
	 */
	public interface RenderListener {
		/**
		 * Called when parse source
		 *
		 * @param view view
		 */
		void onStart(ParagraphView view, ParagraphSource source);

		/**
		 * Called when render paragraph
		 *
		 * @param view      view
		 * @param paragraph paragraph
		 */
		void onRender(ParagraphView view, Paragraph paragraph);

		/**
		 * Called when an error occurs
		 *
		 * @param view      view
		 * @param throwable error
		 */
		void onError(ParagraphView view, Throwable throwable);
	}
}
