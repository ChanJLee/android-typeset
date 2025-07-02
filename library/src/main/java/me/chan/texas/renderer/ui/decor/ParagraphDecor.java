package me.chan.texas.renderer.ui.decor;

import android.graphics.Canvas;
import me.chan.texas.misc.Rect;

import me.chan.texas.misc.RectF;

import android.view.MotionEvent;

import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.text.Paragraph;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
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
	public final void draw(Canvas canvas, Paragraph paragraph, int width, int height) {
		mParagraph = paragraph;
		mCanvas = canvas;
		try {
			mDrawOutRect.set(0, 0, width, height);
			Layout layout = paragraph.getLayout();
			mDrawInRect.set(layout.getPaddingLeft(), layout.getPaddingTop(), width - layout.getPaddingRight(), height - layout.getPaddingBottom());
			mDrawVisitor.handle(paragraph, mDrawOutRect, mDrawInRect);
		} finally {
			mCanvas = null;
			mParagraph = null;
		}
	}

	private final Rect mTouchOutRect = new Rect();
	private final Rect mTouchInRect = new Rect();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public final boolean handleTouchEvent(MotionEvent event, Paragraph paragraph, RenderOption renderOption, int width, int height) {
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
	protected abstract void onStartLayoutParagraph(Paragraph paragraph, Rect viewportOuter, Rect viewportInner);

	protected abstract void onEndLayoutParagraph(Paragraph paragraph, Rect viewportOuter, Rect viewportInner);

	protected void onStartLayoutLine() {
	}

	protected void onEndLayoutLine() {
	}

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

	private final DecorParagraphVisitor mDrawVisitor = new DecorParagraphVisitor() {

		@Override
		protected void onVisitParagraphEnd(Paragraph paragraph) {
			super.onVisitParagraphEnd(paragraph);
			onEndLayoutParagraph(mParagraph, mViewportOuter, mViewportInner);
			onDrawDecor(mCanvas, mParagraph, mViewportOuter, mViewportInner);
		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {
			onStartLayoutLine();
			super.onVisitLineStart(line, x, y);
		}

		@Override
		protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
			int sig = onLayoutDecor(mParagraph, box.getTag(), inner, outer, mViewportOuter, mViewportInner);
			sendVisitSig(sig);
		}

		@Override
		protected void onVisitLineEnd(Line line, float x, float y) {
			super.onVisitLineEnd(line, x, y);
			onEndLayoutLine();
		}

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {
			onStartLayoutParagraph(mParagraph, mViewportOuter, mViewportInner);
			super.onVisitParagraphStart(paragraph);
		}
	};
}
