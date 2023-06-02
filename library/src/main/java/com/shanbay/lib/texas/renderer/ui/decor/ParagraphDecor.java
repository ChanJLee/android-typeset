package com.shanbay.lib.texas.renderer.ui.decor;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.shanbay.lib.texas.renderer.ParagraphVisitor;
import com.shanbay.lib.texas.renderer.RenderOption;
import com.shanbay.lib.texas.renderer.TexasView;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.layout.Box;
import com.shanbay.lib.texas.text.layout.Layout;
import com.shanbay.lib.texas.text.layout.Line;

import androidx.annotation.AnyThread;
import androidx.annotation.RestrictTo;

/**
 * {@link TexasView.SegmentDecoration}
 * <p>
 * 帮助你绘制 paragraph 的 sidebar
 */
public abstract class ParagraphDecor {

	private Canvas mCanvas;
	private Paragraph mParagraph;

	private final Rect mDrawOutRect = new Rect();
	private final Rect mDrawInRect = new Rect();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void draw(Canvas canvas, Paragraph paragraph, RenderOption renderOption, int width, int height) {
		if (renderOption == null) {
			return;
		}

		mParagraph = paragraph;
		mCanvas = canvas;
		try {
			mDrawOutRect.set(0, 0, width, height);
			Layout layout = paragraph.getLayout();
			mDrawInRect.set(layout.getPaddingLeft(), layout.getPaddingTop(), width - layout.getPaddingRight(), height - layout.getPaddingBottom());
			mDrawVisitor.handle(paragraph, mDrawOutRect, mDrawInRect, renderOption);
		} finally {
			mCanvas = null;
			mParagraph = null;
		}
	}

	private final Rect mTouchOutRect = new Rect();
	private final Rect mTouchInRect = new Rect();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public boolean handleTouchEvent(MotionEvent event, Paragraph paragraph, RenderOption renderOption, int width, int height) {
		if (renderOption == null) {
			return false;
		}

		mParagraph = paragraph;
		try {
			mTouchOutRect.set(0, 0, width, height);
			Layout layout = paragraph.getLayout();
			mTouchInRect.set(layout.getPaddingLeft(), layout.getPaddingTop(), width - layout.getPaddingRight(), height - layout.getPaddingBottom());
			return onTouchEvent(event, paragraph, mTouchOutRect, mTouchInRect);
		} finally {
			mParagraph = null;
		}
	}

	/**
	 * 准备绘制decor
	 *
	 * @param paragraph     当前paragraph
	 * @param viewportOuter 整个view的外部轮廓轮廓
	 * @param viewportInner 整个view真实轮廓即包含decor margin的矩形位置
	 */
	@AnyThread
	protected abstract void onPreDrawDecor(Paragraph paragraph, Rect viewportOuter, Rect viewportInner);

	/**
	 * 收集 decor 渲染信息（针对每个真实的span内容）
	 *
	 * @param paragraph  当前paragraph
	 * @param spanTag    行内的span tag
	 * @param spanOuter  当前 box outer rect
	 * @param spanInner  当前 box inner rect
	 * @param decorOuter 整个view的外部轮廓轮廓
	 * @param decorInner 整个view真实轮廓即包含decor margin的矩形位置
	 * @return true 代表收集完成，false 继续
	 */
	@AnyThread
	@ParagraphVisitor.VisitSig
	protected abstract int onLayoutDecor(Paragraph paragraph, Object spanTag, RectF spanOuter, RectF spanInner, Rect decorOuter, Rect decorInner);

	/**
	 * 开始绘制decor
	 *
	 * @param canvas     canvas
	 * @param paragraph  当前paragraph
	 * @param decorOuter 整个view的外部轮廓轮廓
	 * @param decorInner 整个view真实轮廓即包含decor margin的矩形位置
	 */
	@AnyThread
	protected abstract void onDrawDecor(Canvas canvas, Paragraph paragraph, Rect decorOuter, Rect decorInner);

	/**
	 * @param event      event
	 * @param paragraph  paragraph
	 * @param decorOuter 整个view的外部轮廓轮廓
	 * @param decorInner 整个view真实轮廓即包含decor margin的矩形位置
	 * @return true 代表消费事件
	 */
	protected abstract boolean onTouchEvent(MotionEvent event, Paragraph paragraph, Rect decorOuter, Rect decorInner);

	private static abstract class DecorParagraphVisitor extends ParagraphVisitor {
		protected Rect mViewportOuter;
		protected Rect mViewportInner;

		public void handle(Paragraph paragraph, Rect viewportOuter, Rect viewportInner, RenderOption renderOption) {
			mViewportOuter = viewportOuter;
			mViewportInner = viewportInner;
			try {
				visit(paragraph, renderOption);
			} catch (VisitException e) {
				e.printStackTrace();
			} finally {
				mViewportOuter = null;
				mViewportInner = null;
			}
		}

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {

		}

		@Override
		protected void onVisitParagraphEnd(Paragraph paragraph) {
			mViewportOuter = mViewportInner = null;
		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {

		}

		@Override
		protected void onVisitLineEnd(Line line, float x, float y) {

		}
	}

	private final DecorParagraphVisitor mDrawVisitor = new DecorParagraphVisitor() {

		@Override
		protected void onVisitParagraphEnd(Paragraph paragraph) {
			super.onVisitParagraphEnd(paragraph);
			onDrawDecor(mCanvas, mParagraph, mViewportOuter, mViewportInner);
		}

		@Override
		protected void onVisitBox(Box box, RectF inner, RectF outer) {
			int sig = onLayoutDecor(mParagraph, box.getTag(), inner, outer, mViewportOuter, mViewportInner);
			if (sig == SIG_STOP_PARA_VISIT) {
				sendVisitSig(SIG_STOP_PARA_VISIT);
			} else if (sig == SIG_STOP_LINE_VISIT) {
				sendVisitSig(SIG_STOP_LINE_VISIT);
			} else if (sig == SIG_NORMAL) {
				sendVisitSig(SIG_NORMAL);
			}
		}

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {
			onPreDrawDecor(mParagraph, mViewportOuter, mViewportInner);
			super.onVisitParagraphStart(paragraph);
		}
	};
}
