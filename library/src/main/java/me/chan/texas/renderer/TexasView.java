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
import android.os.Process;
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
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.source.Source;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.HyphenStrategy;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextGravity;
import me.chan.texas.utils.TexasUtils;
import me.chan.texas.utils.concurrency.Worker;


public final class TexasView extends FrameLayout {
	private final Worker.Token mToken = Worker.Token.newInstance();
	
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

	
	public static final int SCROLL_STATE_IDLE = 0;

	
	public static final int SCROLL_STATE_DRAGGING = 1;

	
	public static final int SCROLL_STATE_SETTLING = 2;

	void notifySegmentClicked(TouchEvent event, Object tag) {
		if (mOnClickedListener != null) {
			event.adjust(this);
			mOnClickedListener.onSegmentClicked(event, tag);
		}
		event.recycle();
	}

	void notifySegmentDoubleClicked(TouchEvent event, Object tag) {
		if (mOnClickedListener != null) {
			event.adjust(this);
			mOnClickedListener.onSegmentDoubleClicked(event, tag);
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

		checkUIThreadPriority();
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

	private static void checkUIThreadPriority() {



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


		renderOption.setTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_textColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_text_color)
				)
		);


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


		renderOption.setTextSize(
				typedArray.getDimension(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_textSize,
						TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_SP,
								DEFAULT_TEXT_SIZE,
								resources.getDisplayMetrics()
						)
				)
		);


		renderOption.setLineSpacingExtra(
				typedArray.getDimension(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_lineSpacingExtra,
						0
				)
		);


		renderOption.setSelectedBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_selectedBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_theme_color)
				)
		);


		renderOption.setSelectedTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_selectedTextColor, Color.WHITE)
		);


		renderOption.setSelectedByLongClickBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_spanSelectedBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_span_bg_color)
				)
		);


		renderOption.setSelectedByLongClickTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_spanSelectedTextColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_text_color))
		);


		int breakStrategy = typedArray.getInt(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_breakStrategy, BREAK_STRATEGY_BALANCE);
		renderOption.setBreakStrategy(
				breakStrategy == BREAK_STRATEGY_SIMPLE ?
						BreakStrategy.SIMPLE : BreakStrategy.BALANCED
		);


		renderOption.setWordSelectable(
				typedArray.getBoolean(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_wordSelectable, true)
		);


		int hyphenStrategy = typedArray.getInt(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_hyphenStrategy, HYPHEN_STRATEGY_US);
		renderOption.setHyphenStrategy(
				hyphenStrategy == HYPHEN_STRATEGY_UK ?
						HyphenStrategy.UK : HyphenStrategy.US
		);


		renderOption.setEnableLazyRender(
				typedArray.getBoolean(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_lazyRender, true)
		);


		renderOption.setSpanHighlightTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_spanHighlightTextColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_theme_color)
				)
		);


		renderOption.setLoadingBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_loadingBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_loading_bg)
				)
		);


		renderOption.setDragViewColor(
				typedArray.getColor(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_dragViewColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_drag_view_color))
		);


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


		renderOption.setCompatMode(
				typedArray.getBoolean(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_compatMode, false)
		);



		if (!renderOption.isCompatMode() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			setLayerType(LAYER_TYPE_SOFTWARE, null);
		}


		renderOption.setDragToSelectEnable(
				typedArray.getBoolean(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_dragToSelectionEnable, true)
		);


		int textGravity = typedArray.getInt(R.styleable.me_chan_texas_TexasView_me_chan_texas_TexasView_textGravity, TextGravity.TOP | TextGravity.START);
		renderOption.setTextGravity(textGravity);

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

	private void load(String reason) {
		if (mRenderer == null) {
			return;
		}

		mRenderer.load(reason, getRenderWidth());
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		if (w != oldw) {
			mRenderer.resize("onSizeChanged", getRenderWidth());
		}
	}

	private int getRenderWidth() {
		int padding = mRenderer == null ? 0 : mRenderer.getPaddingWidth();
		return getWidth() - getPaddingLeft() - getPaddingRight() - padding;
	}

	
	@NonNull
	public RenderOption createRendererOption() {
		d("create new renderer option");
		return mRenderer == null ? RenderOption.DEFAULT : mRenderer.createRendererOption();
	}

	@Nullable
	RenderOption getRendererOption() {
		return mRenderer == null ? null : mRenderer.getRendererOption();
	}

	
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

	
	@Nullable
	public DocumentSource getSource() {
		return mSource;
	}

	
	public void refresh(RenderOption renderOption) {
		d("refresh render option");
		if (mRenderer != null) {
			mRenderer.refresh(renderOption);
		}
	}

	
	public void redraw() {
		if (mRenderer != null) {
			mRenderer.redraw();
		}
	}

	
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

	
	public void setParagraphDecor(ParagraphDecor decor) {
		if (mRenderer != null) {
			mRenderer.setParagraphDecor(decor);
		}
	}

	
	public int getFirstVisibleSegmentIndex(boolean completelyVisible) {
		return mRenderer == null ? NO_POSITION : mRenderer.getFirstVisibleSegmentIndex(completelyVisible);
	}

	
	public int getLastVisibleSegmentIndex(boolean completelyVisible) {
		return mRenderer == null ? NO_POSITION : mRenderer.getLastVisibleSegmentIndex(completelyVisible);
	}

	
	@Nullable
	public Document getDocument() {
		return mRenderer == null ? null : mRenderer.getDocument();
	}

	
	public void setRenderListener(RenderListener renderListener) {
		mRenderListener = renderListener;
	}

	
	@Nullable
	public Selection getSelection() {
		return getSelection(Selection.Type.SELECTION);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyEmptyClicked(TouchEvent event) {
		if (mOnClickedListener != null) {
			event.adjust(this);
			mOnClickedListener.onEmptyClicked(event);
		}
		event.recycle();
	}

	public void setOnDragSelectListener(OnDragSelectListener onDragSelectListener) {
		mOnDragSelectListener = onDragSelectListener;
	}

	
	public void setOnClickedListener(OnClickedListener onClickedListener) {
		mOnClickedListener = onClickedListener;
	}

	
	public void scrollToPosition(int position) {
		scrollToPosition(position, true);
	}

	
	public void scrollToPosition(int position, boolean smooth) {
		scrollToPosition(position, smooth, 0);
	}

	
	public void scrollToPosition(int position, boolean smooth, int offset) {
		if (mRenderer != null) {
			mRenderer.scrollToPosition(position, smooth, offset);
		}
	}

	
	public void smoothScrollBy(int dx, int dy) {
		if (mRenderer != null) {
			mRenderer.smoothScrollBy(dx, dy);
		}
	}

	
	@Nullable
	public Selection highlightParagraphs(ParagraphPredicates predicates) {
		return highlightParagraphs(predicates, null);
	}

	
	@Nullable
	public Selection highlightParagraphs(ParagraphPredicates predicates, Selection.Styles styles) {
		return highlightParagraphs(predicates, false, 0, styles);
	}

	
	@Nullable
	public Selection highlightParagraphs(ParagraphPredicates predicates, boolean scrollTo, int offset) {
		return highlightParagraphs(predicates, scrollTo, offset, null);
	}

	
	@Nullable
	public Selection highlightParagraphs(ParagraphPredicates predicates, boolean scrollTo, int offset, Selection.Styles styles) {
		if (mRenderer == null) {
			return null;
		}

		return mRenderer.highlightParagraphs(predicates, scrollTo, offset, styles);
	}

	
	public void clearHighlight() {
		if (mRenderer != null) {
			mRenderer.clearHighlight();
		}
	}

	
	public Selection getHighlight() {
		return getSelection(Selection.Type.HIGHLIGHT);
	}

	
	@Nullable
	public Selection getSelection(Selection.Type type) {
		return mRenderer == null ? null : mRenderer.getSelection(type);
	}

	public void setSpanTouchEventHandler(SpanTouchEventHandler listener) {
		if (mRenderer != null) {
			mRenderer.setSpanTouchEventHandler(listener);
		}
	}

	
	@Nullable
	public Selection selectParagraphs(ParagraphPredicates predicates) {
		return selectParagraphs(predicates, null);
	}

	
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

	
	public void clearSelection() {
		if (mRenderer != null) {
			mRenderer.clearSelection();
		}
	}

	
	@TexasViewScrollState
	public int getScrollState() {
		if (mRenderer == null) {
			return SCROLL_STATE_IDLE;
		}
		return mRenderer.getScrollState();
	}

	
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

	
	public void setScrollBarDrawable(Drawable drawable) {
		if (mRenderer != null) {
			mRenderer.setScrollBarDrawable(drawable);
		}
	}

	
	@Nullable
	public Drawable getScrollBarDrawable() {
		return mRenderer == null ? null : mRenderer.getScrollBarDrawable();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifySpanLongClicked(TouchEvent event, Object tag) {
		if (mOnClickedListener != null) {
			event.adjust(this);
			mOnClickedListener.onSpanLongClicked(event, tag);
		}
		event.recycle();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifySpanClicked(TouchEvent event, Object tag) {
		if (mOnClickedListener != null) {
			event.adjust(this);
			mOnClickedListener.onSpanClicked(event, tag);
		}
		event.recycle();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyDragStart(TouchEvent event) {
		if (mOnDragSelectListener != null) {
			event.adjust(this);
			mOnDragSelectListener.onDragStart(event);
		}
		event.recycle();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyDragEnd(TouchEvent event) {
		if (mOnDragSelectListener != null) {
			event.adjust(this);
			mOnDragSelectListener.onDragEnd(event);
		}
		event.recycle();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void notifyDragDismiss() {
		if (mOnDragSelectListener != null) {
			mOnDragSelectListener.onDragDismiss();
		}
	}

	
	public void resume() {
		if (mRenderer != null) {
			mRenderer.resume();
		}
	}

	
	public void pause() {
		if (mRenderer != null) {
			mRenderer.pause();
		}
	}

	
	public void setHasFixedSize(boolean enable) {
		if (mRenderer != null) {
			mRenderer.setHasFixedSize(enable);
		}
	}

	
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

	@NonNull
	TexasOption createTexasOption() {
		RenderOption option = getRendererOption();
		PaintSet paintSet = new PaintSet(option);
		Measurer measurer = mMeasureFactory.create(paintSet);
		TextAttribute textAttribute = new TextAttribute(measurer);
		return LoadingWorker.createTexasOption(paintSet, textAttribute, measurer, option);
	}

	
	public interface RenderListener {
		
		void onStart(TexasView texasView);

		
		void onEnd(TexasView texasView);

		
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

		
		protected abstract Document onRead(TexasOption option, @Nullable Document previousDocument);
	}

	
	public interface OnClickedListener {
		
		void onSpanClicked(TouchEvent event, Object tag);

		
		void onSpanLongClicked(TouchEvent event, Object tag);

		
		void onSegmentClicked(TouchEvent event, Object tag);

		
		void onEmptyClicked(TouchEvent event);

		
		void onSegmentDoubleClicked(TouchEvent event, Object tag);
	}

	
	public interface SegmentDecoration {
		
		@AnyThread
		void onDecorateSegment(int index, int count, Segment segment, Document document, Rect outRect);
	}

	
	public interface OnScrollListener {
		
		void onScrollStateChanged(TexasView view, int state);

		void onScrolled(int dx, int dy);
	}

	
	public interface OnDragSelectListener {
		

		void onDragStart(TouchEvent event);

		
		void onDragEnd(TouchEvent event);

		void onDragDismiss();
	}
}
