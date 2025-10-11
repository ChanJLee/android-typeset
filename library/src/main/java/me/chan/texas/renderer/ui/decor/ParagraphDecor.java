package me.chan.texas.renderer.ui.decor;

import me.chan.texas.misc.Rect;

import android.view.MotionEvent;

import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Layout;

import androidx.annotation.AnyThread;
import androidx.annotation.RestrictTo;

/**
 * {@link TexasView.SegmentDecoration}
 * <p>
 * 帮助你绘制 paragraph 的 sidebar
 */
public abstract class ParagraphDecor {

	private final Rect mDrawOutRect = new Rect();
	private final Rect mDrawInRect = new Rect();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public final void layout(Paragraph paragraph, int width, int height) {
		mDrawOutRect.set(0, 0, width, height);
		Layout layout = paragraph.getLayout();
		mDrawInRect.set(layout.getPaddingLeft(), layout.getPaddingTop(), width - layout.getPaddingRight(), height - layout.getPaddingBottom());
		onLayout(paragraph, mDrawOutRect, mDrawInRect);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public final void draw(TexasCanvas canvas, TexasPaint paint, Paragraph paragraph, int width, int height, boolean background) {
		mDrawOutRect.set(0, 0, width, height);
		Layout layout = paragraph.getLayout();
		mDrawInRect.set(layout.getPaddingLeft(), layout.getPaddingTop(), width - layout.getPaddingRight(), height - layout.getPaddingBottom());
		if (background) {
			onDrawBackground(canvas, paint, paragraph, mDrawOutRect, mDrawInRect);
			return;
		}
		onDraw(canvas, paint, paragraph, mDrawOutRect, mDrawInRect);
	}

	private final Rect mTouchOutRect = new Rect();
	private final Rect mTouchInRect = new Rect();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public final boolean handleTouchEvent(MotionEvent event, Paragraph paragraph, RenderOption renderOption, int width, int height) {
		if (renderOption == null) {
			return false;
		}

		mTouchOutRect.set(0, 0, width, height);
		Layout layout = paragraph.getLayout();
		mTouchInRect.set(layout.getPaddingLeft(), layout.getPaddingTop(), width - layout.getPaddingRight(), height - layout.getPaddingBottom());
		return onTouchEvent(event, paragraph, mTouchOutRect, mTouchInRect);
	}

	/**
	 * 收集 decor 渲染信息（针对每个真实的span内容）
	 *
	 * @param paragraph  当前paragraph
	 * @param decorOuter 整个view的外部轮廓轮廓
	 * @param decorInner 整个view真实轮廓即包含decor margin的矩形位置
	 * @return true 代表收集完成，false 继续
	 */
	@AnyThread
	protected abstract void onLayout(Paragraph paragraph, Rect decorOuter, Rect decorInner);

	/**
	 * 开始绘制decor
	 *
	 * @param canvas     canvas
	 * @param paint      paint
	 * @param paragraph  当前paragraph
	 * @param decorOuter 整个view的外部轮廓轮廓
	 * @param decorInner 整个view真实轮廓即包含decor margin的矩形位置
	 */
	@AnyThread
	protected abstract void onDraw(TexasCanvas canvas, TexasPaint paint, Paragraph paragraph, Rect decorOuter, Rect decorInner);

	/**
	 * 开始绘制decor背景，背景存在于文字之下
	 *
	 * @param canvas     canvas
	 * @param paint      paint
	 * @param paragraph  当前paragraph
	 * @param decorOuter 整个view的外部轮廓轮廓
	 * @param decorInner 整个view真实轮廓即包含decor margin的矩形位置
	 */
	@AnyThread
	protected void onDrawBackground(TexasCanvas canvas, TexasPaint paint, Paragraph paragraph, Rect decorOuter, Rect decorInner) {
	}

	/**
	 * @param event      event
	 * @param paragraph  paragraph
	 * @param decorOuter 整个view的外部轮廓轮廓
	 * @param decorInner 整个view真实轮廓即包含decor margin的矩形位置
	 * @return true 代表消费事件
	 */
	protected abstract boolean onTouchEvent(MotionEvent event, Paragraph paragraph, Rect decorOuter, Rect decorInner);
}
