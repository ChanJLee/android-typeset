package me.chan.texas.renderer.ui.text;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Process;
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
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextGravity;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.utils.TexasUtils;

import java.lang.ref.WeakReference;

import javax.inject.Inject;


public class ParagraphView extends FrameLayout {
	public static final boolean DEBUG = true;

	private static final String TAG = "ParagraphView";

	@NonNull
	private final TextureParagraph mRender;

	private RenderOption mRenderOption;

	
	private Paragraph mParagraph;

	private OnClickedListener mOnClickedListener;

	private final SelectedTextByClickedVisitor mSelectedTextByClickedVisitor = new SelectedTextByClickedVisitor();

	
	private final PredicatesDriveSelectedVisitor mPredicatesDriveSelectedVisitor = new PredicatesDriveSelectedVisitor();

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
			paragraph.bind(mHost);
			requestLayout();
		}

		@Override
		public void onParseFailure(Throwable throwable) {
			Log.w(TAG, throwable);
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
			AbsTextureParagraphView.RelayoutPredicate relayoutPredicate = (view, paragraph) -> {
				ViewGroup.LayoutParams layoutParams = getLayoutParams();
				if (layoutParams == null) {
					return true;
				}

				Layout layout = paragraph.getLayout();
				if (layout.getHeight() != view.getHeight()) {
					return layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT;
				}

				return false;
			};
			mRender = mRenderOption.isCompatMode() ? new TextureParagraphView0Compat(context, relayoutPredicate) : new TextureParagraphView0(context, relayoutPredicate);
			addView((View) mRender, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			OnSelectedChangedListener onSelectedChangedListener = new OnSelectedChangedListener() {
				@Override
				public boolean onSegmentClicked(TouchEvent e, Paragraph paragraph, int eventType) {
					return handleParagraphClicked(e, eventType);
				}

				@Override
				public boolean onBoxSelected(TouchEvent e, Paragraph paragraph, @EventType int eventType, Box box) {
					return handleParagraphSelected(e, paragraph, eventType, box);
				}
			};
			mRender.setOnTextSelectedListener(onSelectedChangedListener);
			mRender.setOnMeasureInterceptor(this::handleMeasureRenderer);
			setVerticalAlignment(mRenderOption);

			TexasComponent texasComponent = Texas.getTexasComponent();
			TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
			textEngineCoreComponent.inject(this);
			checkUIThreadPriority();

			String text = typedArray.getString(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_text);
			if (!TextUtils.isEmpty(text)) {
				setText(text);
			}
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
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (DEBUG) {
			Log.d(TAG, "onSizeChanged: " + w + "x" + h);
		}
	}

	private boolean handleParagraphSelected(TouchEvent event, Paragraph paragraph, @OnSelectedChangedListener.EventType int eventType, Box box) {
		if (mOnClickedListener == null) {
			return false;
		}

		if (eventType == OnSelectedChangedListener.EVENT_CLICKED || eventType == OnSelectedChangedListener.EVENT_LONG_CLICKED) {
			boolean handled = handleParagraphSelected(event, paragraph, eventType == OnSelectedChangedListener.EVENT_LONG_CLICKED, box);
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

	private boolean handleParagraphSelected(TouchEvent event, Paragraph paragraph, boolean isLongClicked, Box box) {

		clearSelection();

		SpanPredicate predicate = isLongClicked ? mOnSpanLongClickedPredicate : mOnSpanClickedPredicate;
		if (predicate == null) {
			return false;
		}

		try {
			boolean handled = handleParagraphSelected0(paragraph, isLongClicked, box, predicate);
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
			
		}

		return true;
	}

	private boolean handleParagraphSelected0(Paragraph paragraph, boolean isLongClicked, Box box, SpanPredicate predicate) throws ParagraphVisitor.VisitException {
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
					box.getTag()
			);


			mSelectedTextByClickedVisitor.startVisit(
					paragraph
			);
			render0(paragraph);

			return mSelectedTextByClickedVisitor.isHandled();
		} finally {
			mSelectedTextByClickedVisitor.clear();
		}
	}

	
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

		int height = layout.getHeight();
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
			worker.desire(mParagraph, width);
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

		mRender.render(paragraph, mUiThreadPaintSet, mRenderOption, null, mSpanTouchEventHandler);
	}

	
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

	
	public void setSource(@NonNull ParagraphSource source) {
		if (DEBUG) {
			Log.d(TAG, "setSource: source = " + source);
		}


		discard(false);


		mSource = source;
		source.attach(this);


		ParseWorker.Args args = ParseWorker.Args.obtain(source, mParseListener);
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

	
	@NonNull
	public RenderOption createRendererOption() {
		return new RenderOption(mRenderOption);
	}

	
	public void refresh(@NonNull RenderOption renderOption) {
		int cmpType = TexasUtils.cmp(mRenderOption, renderOption);

		mRenderOption = renderOption;
		mUiThreadPaintSet.refresh(renderOption);
		if (mParagraph != null) {
			Layout layout = mParagraph.getLayout();
			Layout.Advise advise = layout.getAdvise();
			advise.copy(renderOption);
		}
		setVerticalAlignment(renderOption);

		if (cmpType == TexasUtils.CmpType.CMP_LOAD) {

			discard(false);


			ParseWorker.Args args = ParseWorker.Args.obtain(mSource, mParseListener);
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

	
	@Nullable
	public Selection highlightParagraphs(ParagraphPredicates predicates) {
		return highlightParagraphs(predicates, null);
	}

	
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
			
		} finally {
			mPredicatesDriveSelectedVisitor.clear();
		}

		return null;
	}

	
	public void discard() {
		discard(true);
	}

	private void discard(boolean releaseBuffer) {
		if (releaseBuffer) {
			mRender.clear();
		}
		WorkerScheduler.cancelAll(mRender.getToken());
	}

	
	public void setOnClickedListener(OnClickedListener onClickedListener) {
		mOnClickedListener = onClickedListener;
	}

	
	public interface OnClickedListener {
		
		void onSpanClicked(ParagraphView paragraphView, TouchEvent event, Object tag);

		
		void onSpanLongClicked(ParagraphView paragraphView, TouchEvent event, Object tag);

		
		void onEmptyClicked(ParagraphView paragraphView, TouchEvent event);

		
		void onDoubleClicked(ParagraphView paragraphView, TouchEvent event);
	}

	private RenderOption createRenderOption(Context context, TypedArray typedArray) {
		Resources resources = getResources();
		RenderOption renderOption = new RenderOption();


		renderOption.setTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_textColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_text_color)
				)
		);


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


		renderOption.setTextSize(
				typedArray.getDimensionPixelSize(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_textSize,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_SP,
								TexasView.DEFAULT_TEXT_SIZE,
								resources.getDisplayMetrics()
						)
				)
		);


		renderOption.setLineSpacingExtra(
				typedArray.getDimensionPixelSize(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_lineSpacingExtra, 0)
		);


		renderOption.setSelectedBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_selectedBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_theme_color)
				)
		);


		renderOption.setSelectedTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_selectedTextColor, Color.WHITE)
		);


		renderOption.setSelectedByLongClickBackgroundColor(
				typedArray.getColor(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_spanSelectedBackgroundColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_span_bg_color)
				)
		);


		renderOption.setSelectedByLongClickTextColor(
				typedArray.getColor(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_spanSelectedTextColor,
						ContextCompat.getColor(context, R.color.me_chan_texas_text_color))
		);


		int breakStrategy = typedArray.getInt(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_breakStrategy, TexasView.BREAK_STRATEGY_BALANCE);
		renderOption.setBreakStrategy(
				breakStrategy == TexasView.BREAK_STRATEGY_SIMPLE ?
						BreakStrategy.SIMPLE : BreakStrategy.BALANCED
		);


		renderOption.setWordSelectable(
				typedArray.getBoolean(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_wordSelectable, true)
		);


		int hyphenStrategy = typedArray.getInt(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_hyphenStrategy, TexasView.HYPHEN_STRATEGY_US);
		renderOption.setHyphenStrategy(
				hyphenStrategy == TexasView.HYPHEN_STRATEGY_UK ?
						HyphenStrategy.UK : HyphenStrategy.US
		);


		renderOption.setEnableLazyRender(
				false
		);


		renderOption.setSpanHighlightTextColor(
				ContextCompat.getColor(context, R.color.me_chan_texas_theme_color)
		);


		renderOption.setLoadingBackgroundColor(
				ContextCompat.getColor(context, R.color.me_chan_texas_loading_bg)
		);


		renderOption.setDragViewColor(
				ContextCompat.getColor(context, R.color.me_chan_texas_drag_view_color)
		);


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


		renderOption.setCompatMode(
				typedArray.getBoolean(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_compatMode, false)
		);


		int textGravity = typedArray.getInt(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_textGravity, TextGravity.TOP | TextGravity.START);
		renderOption.setTextGravity(textGravity);


		renderOption.setDebugEnable(
				typedArray.getBoolean(R.styleable.me_chan_texas_ParagraphView_me_chan_texas_ParagraphView_debugEnable, false)
		);

		return renderOption;
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

			return onRead(mParagraphView.createTexasOption());
		}

		protected abstract Paragraph onRead(TexasOption option);
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
}
