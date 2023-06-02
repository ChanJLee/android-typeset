package com.shanbay.lib.texas.renderer.ui.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.misc.PaintSet;
import com.shanbay.lib.texas.renderer.RenderOption;
import com.shanbay.lib.texas.renderer.highlight.ParagraphHighlight;
import com.shanbay.lib.texas.renderer.selection.ParagraphSelection;
import com.shanbay.lib.texas.renderer.ui.decor.ParagraphDecor;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.layout.Layout;

import java.util.concurrent.atomic.AtomicInteger;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class AbsTextureParagraphView extends View implements TextureParagraph {
	private static final AtomicInteger UUID = new AtomicInteger(0);

	private final int mId = UUID.incrementAndGet();

	protected RenderOption mRenderOption;
	protected Paragraph mParagraph;
	protected PaintSet mPaintSet;
	protected ParagraphSelection mParagraphSelection;
	protected ParagraphHighlight mHighlight;
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
					   @Nullable ParagraphSelection selection,
					   @Nullable ParagraphHighlight highlight,
					   @Nullable ParagraphDecor decor) {
		mParagraph = paragraph;
		mPaintSet = paintSet;
		mRenderOption = renderOption;
		mParagraphSelection = selection;
		mHighlight = highlight;
		mParagraphDecor = decor;
		mParagraphViewMotion.setup(paragraph, renderOption, decor);

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
	@CallSuper
	public void clear() {
		mParagraph = null;
		mPaintSet = null;
		mRenderOption = null;
		mParagraphSelection = null;
		mHighlight = null;
		mParagraphDecor = null;
		mParagraphViewMotion.clear();
	}

	protected abstract void onRender();

	@Override
	public int getTaskId() {
		return mId;
	}

	@Override
	public Paragraph getParagraph() {
		return mParagraph;
	}
}
