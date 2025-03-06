package me.chan.texas.renderer.ui.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

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

	public AbsTextureParagraphView(Context context) {
		super(context);
		mParagraphViewMotion = new ParagraphViewMotion(context, this);
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

		onRender();

		// request layout
		Layout layout = paragraph.getLayout();
		boolean relayout = getWidth() != layout.getWidth() || getHeight() != layout.getHeight();
		if (relayout) {
			// 尽可能减少 requestLayout 的调用
			requestLayout();
		}
	}

	@Override
	protected final void onDetachedFromWindow() {
		clear();
		super.onDetachedFromWindow();
	}

	@Override
	@CallSuper
	public void clear() {
		mParagraph = null;
		mPaintSet = null;
		mRenderOption = null;
		mParagraphDecor = null;
		mParagraphViewMotion.clear();
	}

	protected abstract void onRender();

	@Override
	public TaskQueue.Token getToken() {
		return mToken;
	}

	@Override
	public Paragraph getParagraph() {
		return mParagraph;
	}
}
