package me.chan.texas.renderer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;

import me.chan.texas.misc.Rect;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.FrameLayout;

import androidx.annotation.AnyThread;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.UiThread;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import me.chan.texas.R;
import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.measurer.MeasureFactory;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.worker.LoadingWorker;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.source.Source;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.HyphenStrategy;
import me.chan.texas.text.Segment;
import me.chan.texas.renderer.selection.SelectionMethod;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextGravity;
import me.chan.texas.utils.TexasUtils;
import me.chan.texas.utils.concurrency.Worker;

/**
 * Rendering engine entry view
 */
public final class TexasView extends FrameLayout {
	private final Worker.Token mToken = Worker.Token.newInstance();
	/**
	 * Invalid position index
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

	/**
	 * TexasView is idle
	 */
	public static final int SCROLL_STATE_IDLE = 0;

	/**
	 * TexasView is being dragged by the user
	 */
	public static final int SCROLL_STATE_DRAGGING = 1;

	/**
	 * TexasView is scrolling, but not due to user dragging, such as animation causing TexasView to scroll
	 */
	public static final int SCROLL_STATE_SETTLING = 2;

	void notifySegmentClicked(TouchEvent event, Object tag) {
		if (mOnClickedListener != null) {
			event.adjust(this);
			mOnClickedListener.onSegmentClicked(this, event, tag);
		}
		event.recycle();
	}

	void notifySegmentDoubleClicked(TouchEvent event, Object tag) {
		if (mOnClickedListener != null) {
			event.adjust(this);
			mOnClickedListener.onSegmentDoubleClicked(this, event, tag);
		}
		event.recycle();
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING})
	public @interface TexasViewScrollState {
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public final static Map<String, WeakReference<Typeface>> TYPEFACE_CACHE = new HashMap<>();

	private DocumentSource mSource;
	private Renderer mRenderer;
	private RenderListener mRenderListener;
	private OnClickedListener mOnClickedListener;
	private OnScrollListener mOnScrollListener;
	private OnDragSelectListener mOnDragSelectListener;

	@Inject
	MeasureFactory mMeasureFactory;

	public TexasView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TexasView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);

		TexasComponent texasComponent = Texas.getTexasComponent();
		TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
		textEngineCoreComponent.inject(this);
	}

	private void init(Context context, AttributeSet attributeSet, int defStyleAttr) {
		@SuppressLint("CustomViewStyleable")
		TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.me_chan_texas_TexasView, defStyleAttr, 0);
		try {
			init(context, typedArray);
		} finally {
			typedArray.recycle();
		}
	}

	private void init(Context context, TypedArray typedArray) {
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

		// Set text color
		renderOption.setTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_textColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_text_color)
				)
		);

		// Set typeface
		renderOption.setTypeface(Texas.getDefaultTypeface());
		String typefacePath = typedArray.getString(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_typefaceAssets);
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

		// Set text size
		renderOption.setTextSize(
				typedArray.getDimension(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_textSize,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_SP,
								DEFAULT_TEXT_SIZE,
								resources.getDisplayMetrics()
						)
				)
		);

		// Line spacing
		renderOption.setLineSpacingExtra(
				typedArray.getDimension(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_lineSpacingExtra,
						0
				)
		);

		// Selected text background color
		renderOption.setSelectedBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_selectedBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_theme_color)
				)
		);

		// Selected text color
		renderOption.setSelectedTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_selectedTextColor, Color.WHITE)
		);

		// Selected span background color
		renderOption.setSelectedByLongClickBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_spanSelectedBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_span_bg_color)
				)
		);

		// Selected span text color
		renderOption.setSelectedByLongClickTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_spanSelectedTextColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_text_color))
		);

		// Line break strategy
		int breakStrategy = typedArray.getInt(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_breakStrategy, BREAK_STRATEGY_BALANCE);
		renderOption.setBreakStrategy(
				breakStrategy == BREAK_STRATEGY_SIMPLE ?
						BreakStrategy.SIMPLE : BreakStrategy.BALANCED
		);

		// Whether words are selectable
		renderOption.setWordSelectable(
				typedArray.getBoolean(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_wordSelectable, true)
		);

		// Hyphenation strategy
		int hyphenStrategy = typedArray.getInt(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_hyphenStrategy, HYPHEN_STRATEGY_US);
		renderOption.setHyphenStrategy(
				hyphenStrategy == HYPHEN_STRATEGY_UK ?
						HyphenStrategy.UK : HyphenStrategy.US
		);

		// Lazy rendering mode optimization
		renderOption.setEnableLazyRender(
				typedArray.getBoolean(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_lazyRender, true)
		);

		// Highlighted span text color
		renderOption.setSpanHighlightTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_spanHighlightTextColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_theme_color)
				)
		);

		// Loading background color
		renderOption.setLoadingBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_loadingBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_loading_bg)
				)
		);

		// Drag handle color
		renderOption.setDragViewColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_dragViewColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_drag_view_color))
		);

		// Set selected background round radius
		renderOption.setSelectedBackgroundRoundRadius(
				typedArray.getDimension(
						R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_selectedBackgroundRoundRadius,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP,
								3,
								getResources().getDisplayMetrics()
						)
				)
		);

		// Whether to enable compatibility mode
		renderOption.setCompatMode(
				typedArray.getBoolean(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_compatMode, false)
		);

		// If non-compatibility mode is enabled and system version is less than 6.0, disable hardware acceleration
		// {@link me.chan.texas.renderer.core.graphics.TextureScene}
		if (!renderOption.isCompatMode() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			setLayerType(LAYER_TYPE_SOFTWARE, null);
		}

		// Whether to enable drag to select
		renderOption.setDragToSelectEnable(
				typedArray.getBoolean(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_dragToSelectionEnable, true)
		);

		// Text gravity
		int textGravity = typedArray.getInt(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_textGravity, TextGravity.TOP | TextGravity.START);
		renderOption.setTextGravity(textGravity);

		// Enable bidirectional text
		renderOption.setBidiEnable(
				typedArray.getBoolean(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_bidiEnable, false)
		);

		mRenderer = new Renderer(this, renderOption, mToken);
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
			mRenderListener.onEnd(TexasView.this, getDocument());
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

	private void load(String reason) {
		if (mRenderer == null) {
			return;
		}

		mRenderer.load(reason, getRenderWidth());
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		/* render if size changed */
		if (w != oldw) {
			mRenderer.resize("onSizeChanged", getRenderWidth());
		}
	}

	private int getRenderWidth() {
		int padding = mRenderer == null ? 0 : mRenderer.getPaddingWidth();
		return getWidth() - getPaddingLeft() - getPaddingRight() - padding;
	}

	/**
	 * Create a new render option. Call {@link TexasView#refresh(RenderOption)} to refresh styles after completion
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
	 * Set data source
	 *
	 * @param source source
	 */
	@UiThread
	public void setSource(@NonNull DocumentSource source) {
		d("set adapter");
		if (mRenderer == null) {
			return;
		}

		if (mSource != null) {
			d("detach prev adapter");
			mSource.detach();
			mSource = null;
		}

		d("bind adapter");
		mSource = source;
		source.attach(this);
		load("setSource");
	}

	/**
	 * @return current data source
	 */
	@Nullable
	public DocumentSource getSource() {
		return mSource;
	}

	/**
	 * Refresh content, may re-layout {@link TexasView#redraw()}
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
	 * Redraw content
	 */
	public void redraw() {
		if (mRenderer != null) {
			mRenderer.redraw();
		}
	}

	/**
	 * Release resources
	 */
	public void release() {
		i("release");
		if (mSource != null) {
			mSource.detach();
			mSource = null;
		}
		mRenderListener = null;
		mOnClickedListener = null;
		mOnDragSelectListener = null;
		if (mRenderer != null) {
			mRenderer.release();
			mRenderer = null;
		}
	}

	/**
	 * Get the first visible segment index
	 *
	 * @param completelyVisible whether to require completely visible segment
	 * @return first visible segment index, {@link TexasView#NO_POSITION} if none
	 */
	public int getFirstVisibleSegmentIndex(boolean completelyVisible) {
		return mRenderer == null ? NO_POSITION : mRenderer.getFirstVisibleSegmentIndex(completelyVisible);
	}

	/**
	 * @param completelyVisible whether to require completely visible segment
	 * @return last visible segment index, {@link TexasView#NO_POSITION} if none
	 */
	public int getLastVisibleSegmentIndex(boolean completelyVisible) {
		return mRenderer == null ? NO_POSITION : mRenderer.getLastVisibleSegmentIndex(completelyVisible);
	}

	/**
	 * Return the document currently being rendered
	 *
	 * @return document
	 */
	@Nullable
	public Document getDocument() {
		return mRenderer == null ? null : mRenderer.getDocument();
	}

	/**
	 * @param renderListener render listener {@link RenderListener}
	 */
	public void setRenderListener(RenderListener renderListener) {
		mRenderListener = renderListener;
	}

	/**
	 * Get selection information
	 *
	 * @return selection information, default type is {@link Selection.Type#SELECTION}
	 */
	@Nullable
	public Selection getSelection() {
		return getSelection(Selection.Type.SELECTION);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyEmptyClicked(TouchEvent event) {
		if (mOnClickedListener != null) {
			event.adjust(this);
			mOnClickedListener.onEmptyClicked(this, event);
		}
		event.recycle();
	}

	public void setOnDragSelectListener(OnDragSelectListener onDragSelectListener) {
		mOnDragSelectListener = onDragSelectListener;
	}

	/**
	 * @param onClickedListener set click event listener
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
	 * Scroll to a certain segment of the document
	 *
	 * @param position segment index {@link Document#indexOfSegment(Segment)} {@link me.chan.texas.text.Paragraph}
	 * @param smooth   whether to scroll smoothly
	 */
	public void scrollToPosition(int position, boolean smooth) {
		scrollToPosition(position, smooth, 0);
	}

	/**
	 * Scroll to a certain segment of the document
	 *
	 * @param position segment index {@link Document#indexOfSegment(Segment)} {@link me.chan.texas.text.Paragraph}
	 * @param smooth   whether to scroll smoothly
	 * @param offset   offset in the scroll direction
	 */
	public void scrollToPosition(int position, boolean smooth, int offset) {
		if (mRenderer != null) {
			mRenderer.scrollToPosition(position, smooth, offset);
		}
	}

	/**
	 * Scroll offset
	 *
	 * @param dx horizontal scroll distance
	 * @param dy vertical scroll distance
	 */
	public void smoothScrollBy(int dx, int dy) {
		if (mRenderer != null) {
			mRenderer.smoothScrollBy(dx, dy);
		}
	}

	/**
	 * Highlight text in paragraphs, only takes effect after document is rendered
	 *
	 * @param predicates predicates
	 * @return selected area
	 */
	@Nullable
	public Selection highlightParagraphs(ParagraphPredicates predicates) {
		return highlightParagraphs(predicates, null);
	}

	/**
	 * Highlight text in paragraphs, only takes effect after document is rendered
	 *
	 * @param predicates predicates
	 * @param styles     {@link Selection.Styles#create(int, int)}
	 * @return selected area
	 */
	@Nullable
	public Selection highlightParagraphs(ParagraphPredicates predicates, Selection.Styles styles) {
		return highlightParagraphs(predicates, false, 0, styles);
	}

	/**
	 * Highlight text in paragraphs, only takes effect after document is rendered
	 *
	 * @param predicates predicates
	 * @param scrollTo   whether to scroll to highlighted area
	 * @param offset     scroll offset
	 * @return selected area
	 */
	@Nullable
	public Selection highlightParagraphs(ParagraphPredicates predicates, boolean scrollTo, int offset) {
		return highlightParagraphs(predicates, scrollTo, offset, null);
	}

	/**
	 * Highlight text in paragraphs, only takes effect after document is rendered
	 *
	 * @param predicates predicates
	 * @param scrollTo   whether to scroll to highlighted area
	 * @param offset     scroll offset
	 * @param styles     {@link Selection.Styles#create(int, int)}
	 * @return selected area
	 */
	@Nullable
	public Selection highlightParagraphs(ParagraphPredicates predicates, boolean scrollTo, int offset, Selection.Styles styles) {
		if (mRenderer == null) {
			return null;
		}

		return mRenderer.highlightParagraphs(predicates, scrollTo, offset, styles);
	}

	/**
	 * Clear highlight
	 */
	public void clearHighlight() {
		if (mRenderer != null) {
			mRenderer.clearHighlight();
		}
	}

	/**
	 * @return get highlighted area
	 */
	public Selection getHighlight() {
		return getSelection(Selection.Type.HIGHLIGHT);
	}

	/**
	 * Get selection information
	 *
	 * @param type selection type
	 * @return selection information
	 */
	@Nullable
	public Selection getSelection(Selection.Type type) {
		return mRenderer == null ? null : mRenderer.getSelection(type);
	}

	public void setSpanTouchEventHandler(SpanTouchEventHandler listener) {
		if (mRenderer != null) {
			mRenderer.setSpanTouchEventHandler(listener);
		}
	}

	/**
	 * Select text
	 *
	 * @param predicates predicate
	 * @return selected area
	 */
	@Nullable
	public Selection selectParagraphs(ParagraphPredicates predicates) {
		return selectParagraphs(predicates, null);
	}

	/**
	 * Select text
	 * {@link Selection.Styles#create(int, int)}
	 *
	 * @param predicates predicate
	 * @param styles     style of selected text, default style if null {@link RenderOption#setSelectedByLongClickTextColor(int)} ...
	 * @return selected area
	 */
	@Nullable
	public Selection selectParagraphs(ParagraphPredicates predicates, @Nullable Selection.Styles styles) {
		if (styles == null) {
			RenderOption renderOption = getRendererOption();
			if (renderOption == null) {
				return null;
			}

			styles = Selection.Styles.createFromTouch(renderOption, true);
			styles.setEnableDrag(true);
		}
		return mRenderer == null ? null : mRenderer.selectParagraphs(predicates, styles);
	}

	/**
	 * Clear selection area and hide drag handles
	 */
	public void clearSelection() {
		if (mRenderer != null) {
			mRenderer.clearSelection();
		}
	}

	/**
	 * Get scroll state
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
	 * Set whether to enable scroll bar
	 *
	 * @param enable whether to enable
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
	 * Set scroll bar style
	 *
	 * @param drawable style
	 */
	public void setScrollBarDrawable(Drawable drawable) {
		if (mRenderer != null) {
			mRenderer.setScrollBarDrawable(drawable);
		}
	}

	/**
	 * @return get scroll bar style
	 */
	@Nullable
	public Drawable getScrollBarDrawable() {
		return mRenderer == null ? null : mRenderer.getScrollBarDrawable();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifySpanLongClicked(TouchEvent event, Object tag) {
		if (mOnClickedListener != null) {
			event.adjust(this);
			mOnClickedListener.onSpanLongClicked(this, event, tag);
		}
		event.recycle();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifySpanClicked(TouchEvent event, Object tag) {
		if (mOnClickedListener != null) {
			event.adjust(this);
			mOnClickedListener.onSpanClicked(this, event, tag);
		}
		event.recycle();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyDragStart(TouchEvent event) {
		if (mOnDragSelectListener != null) {
			event.adjust(this);
			mOnDragSelectListener.onDragStart(this, event);
		}
		event.recycle();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyDragEnd(TouchEvent event) {
		if (mOnDragSelectListener != null) {
			event.adjust(this);
			mOnDragSelectListener.onDragEnd(this, event);
		}
		event.recycle();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyDragDismiss() {
		if (mOnDragSelectListener != null) {
			mOnDragSelectListener.onDragDismiss(this);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		resume();
	}

	@Override
	protected void onDetachedFromWindow() {
		pause();
		super.onDetachedFromWindow();
	}

	/**
	 * Notify engine to start responding
	 */
	private void resume() {
		if (mRenderer != null) {
			mRenderer.resume();
		}
	}

	/**
	 * Notify engine to stop responding
	 */
	private void pause() {
		if (mRenderer != null) {
			mRenderer.pause();
		}
	}

	/**
	 * If texas view has fixed size, you can set this flag to improve performance
	 *
	 * @param enable whether to set fixed size
	 */
	public void setHasFixedSize(boolean enable) {
		if (mRenderer != null) {
			mRenderer.setHasFixedSize(enable);
		}
	}

	/**
	 * @param listener set scroll state listener
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
			mOnScrollListener.onScrolled(this, dx, dy);
		}
	}

	@NonNull
	TexasOption createTexasOption() {
		RenderOption option = getRendererOption();
		PaintSet paintSet = new PaintSet(option);
		Measurer measurer = mMeasureFactory.create(paintSet);
		TextAttribute textAttribute = new TextAttribute(measurer);
		return LoadingWorker.createTexasOption(paintSet, textAttribute, measurer, option);
	}

	/**
	 * @return 获得选中的逻辑
	 */
	@Nullable
	public SelectionMethod getSelectionMethod() {
		return mRenderer == null ? null : mRenderer.getSelectionMethod();
	}

	/**
	 * Render listener
	 */
	public interface RenderListener {
		/**
		 * Called when rendering starts
		 *
		 * @param texasView view
		 */
		void onStart(TexasView texasView);

		/**
		 * Called when rendering ends
		 *
		 * @param texasView view
		 */
		void onEnd(TexasView texasView, Document document);

		/**
		 * Called when an error occurs
		 *
		 * @param texasView view
		 * @param throwable error
		 */
		void onError(TexasView texasView, Throwable throwable);
	}

	private static void d(String msg) {
		Log.d("TexasView", msg);
	}

	private static void i(String msg) {
		Log.i("TexasView", msg);
	}

	public static abstract class DocumentSource extends Source<LoadingWorker.LoadingResult> {
		private TexasView mTexasView;

		private void attach(@NonNull TexasView view) {
			mTexasView = view;
		}

		private void detach() {
			if (mTexasView == null) {
				return;
			}

			mTexasView = null;
		}

		@Override
		protected final LoadingWorker.LoadingResult onRead() {
			TexasOption option = createTexasOption();
			if (option == null) {
				return null;
			}

			Document base = createBaseDocument();
			Document document = onRead(option, base);
			if (!document.isCopy()) {
				base = null;
			}
			return new LoadingWorker.LoadingResult(option, base, document);
		}

		@VisibleForTesting
		@Nullable
		protected Document createBaseDocument() {
			return mTexasView.getDocument();
		}

		@VisibleForTesting
		protected TexasOption createTexasOption() {
			return mTexasView == null ? null : mTexasView.createTexasOption();
		}

		/**
		 * @param option           current option
		 * @param previousDocument previous document
		 * @return data
		 * <p>
		 * {@link Document.Builder(Document)} incremental content update
		 */
		protected abstract Document onRead(TexasOption option, @Nullable Document previousDocument);
	}

	/**
	 * Click event listener
	 */
	public interface OnClickedListener {
		/**
		 * @param view  view
		 * @param event touch event
		 * @param tag   clicked text tag
		 */
		void onSpanClicked(TexasView view, TouchEvent event, Object tag);

		/**
		 * @param view  view
		 * @param event touch event
		 * @param tag   clicked text tag
		 */
		void onSpanLongClicked(TexasView view, TouchEvent event, Object tag);

		/**
		 * @param view  view
		 * @param event touch event
		 * @param tag   clicked segment tag
		 */
		void onSegmentClicked(TexasView view, TouchEvent event, Object tag);

		/**
		 * @param view  view
		 * @param event touch event
		 */
		void onEmptyClicked(TexasView view, TouchEvent event);

		/**
		 * @param view  view
		 * @param event touch event
		 * @param tag   clicked segment tag
		 */
		void onSegmentDoubleClicked(TexasView view, TouchEvent event, Object tag);
	}

	/**
	 * Segment decorator
	 */
	public interface SegmentDecoration {
		/**
		 * Decorate a segment, can modify its edges
		 * <p>
		 * outRect.set(10, 10, 10, 10) means wrapping 10px space around the current segment
		 *
		 * @param index    current index
		 * @param count    total count
		 * @param segment  current segment to decorate
		 * @param document document containing the segment
		 * @param outRect  output edges
		 */
		@AnyThread
		void onDecorateSegment(int index, int count, Segment segment, Document document, Rect outRect);
	}

	/**
	 * Scroll state listener
	 */
	public interface OnScrollListener {
		/**
		 * @param view  view
		 * @param state {@link TexasView#SCROLL_STATE_DRAGGING} etc.
		 */
		void onScrollStateChanged(TexasView view, int state);

		/**
		 * @param view view
		 * @param dx   horizontal scroll distance
		 * @param dy   vertical scroll distance
		 */
		void onScrolled(TexasView view, int dx, int dy);
	}

	/**
	 * Drag listener
	 */
	public interface OnDragSelectListener {
		/**
		 * @param event touch event
		 */

		void onDragStart(TexasView view, TouchEvent event);

		/**
		 * @param view  texas view
		 * @param event touch event
		 */
		void onDragEnd(TexasView view, TouchEvent event);

		/**
		 * cancel drag
		 *
		 * @param view texas view
		 */
		void onDragDismiss(TexasView view);
	}
}