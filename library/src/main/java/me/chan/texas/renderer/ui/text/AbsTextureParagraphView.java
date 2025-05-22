package me.chan.texas.renderer.ui.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.BuildConfig;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.utils.concurrency.TaskQueue;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class AbsTextureParagraphView extends View implements TextureParagraph {
	private final TaskQueue.Token mToken = TaskQueue.Token.newInstance();

	protected RenderOption mRenderOption;
	protected Paragraph mParagraph;
	protected PaintSet mPaintSet;
	protected ParagraphDecor mParagraphDecor;
	@NonNull
	private final ParagraphViewMotion mParagraphViewMotion;
	private final RelayoutPredicate mRelayoutPredicate;
	protected static final RelayoutPredicate DEFAULT_RELAYOUT_PREDICATE = new RelayoutPredicate() {

		@Override
		public boolean apply(AbsTextureParagraphView view, Paragraph paragraph) {
			return true;
		}
	};

	public AbsTextureParagraphView(Context context) {
		this(context, DEFAULT_RELAYOUT_PREDICATE);
	}

	public AbsTextureParagraphView(Context context, RelayoutPredicate relayoutPredicate) {
		super(context);
		mParagraphViewMotion = new ParagraphViewMotion(context, this);
		mRelayoutPredicate = relayoutPredicate;
	}

	@Override
	public final void setOnTextSelectedListener(OnSelectedChangedListener onTextSelectedListener) {
		mParagraphViewMotion.setOnTextSelectedListener(onTextSelectedListener);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mParagraphViewMotion.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightMode = 0;
		if (mParagraph != null && (heightMode = MeasureSpec.getMode(heightMeasureSpec)) != MeasureSpec.EXACTLY) {
			int height = mParagraph.getLayout().getHeight();
			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
			}
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(
					height,
					MeasureSpec.EXACTLY
			);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public void render(@NonNull Paragraph paragraph,
					   @NonNull PaintSet paintSet,
					   @NonNull RenderOption renderOption,
					   @Nullable ParagraphDecor decor,
					   @Nullable SpanTouchEventHandler spanClickedEventHandler) {
		mParagraph = paragraph;
		mPaintSet = paintSet;
		mRenderOption = renderOption;
		mParagraphDecor = decor;
		mParagraphViewMotion.setup(paragraph, renderOption, decor, spanClickedEventHandler);

		scheduleRender();
	}

	private void scheduleRender() {
		onRender();

		// request layout
		Layout layout = mParagraph.getLayout();
		int width = layout.getWidth();
		int height = layout.getHeight();
		int windowWidth = getWidth();
		int windowHeight = getHeight();
		boolean sizeChanged = width != windowWidth || height != windowHeight;
		if (sizeChanged && mRelayoutPredicate.apply(this, mParagraph)) {
			// 尽可能减少 requestLayout 的调用
			if (BuildConfig.DEBUG) {
				Log.d("ParagraphViewTag", "scheduleRender: requestLayout");
			}
			requestLayout();
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mParagraph != null) {
			scheduleRender();
		}
	}

	@Override
	protected final void onDetachedFromWindow() {
		clear();
		super.onDetachedFromWindow();
	}

	@Override
	public final void clear() {
		onClear();
	}

	@CallSuper
	protected abstract void onClear();

	protected abstract void onRender();

	@Override
	public TaskQueue.Token getToken() {
		return mToken;
	}

	@Override
	public Paragraph getParagraph() {
		return mParagraph;
	}

	public interface RelayoutPredicate {

		/**
		 * @param view      当前的view
		 * @param paragraph 新的paragraph
		 * @return true 表示需要重新布局，false 表示不需要重新布局
		 */
		boolean apply(AbsTextureParagraphView view, Paragraph paragraph);
	}
}
