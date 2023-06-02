package me.chan.texas.renderer;

import android.graphics.RectF;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class ParagraphVisitor {

	private final RectF mInnerRect = new RectF();
	private final RectF mOuterRect = new RectF();
	private int mVisitSig = SIG_NORMAL;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	protected int mCurrentBoxIndexInternal;

	/**
	 * 正常模式
	 */
	public static final int SIG_NORMAL = 0;
	/**
	 * 暂停所有visit
	 */
	public static final int SIG_STOP_PARA_VISIT = 1;
	/**
	 * 暂停当前行的visit
	 */
	public static final int SIG_STOP_LINE_VISIT = 2;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({SIG_NORMAL, SIG_STOP_LINE_VISIT, SIG_STOP_PARA_VISIT})
	public @interface VisitSig {

	}

	public void visit(Paragraph paragraph, RenderOption renderOption) throws VisitException {
		try {
			onVisitParagraphStart(paragraph);
			Layout layout = paragraph.getLayout();
			float x = layout.getPaddingLeft();
			float y = layout.getPaddingTop();
			int end = layout.getLineCount();
			for (int i = 0; i < end && mVisitSig != SIG_STOP_PARA_VISIT; ++i) {
				Line line = layout.getLine(i);
				y += line.getLineHeight();

				visitLine(line, x, y);

				float lineSpace = layout.getLineSpace();
				y += lineSpace;
			}
			onVisitParagraphEnd(paragraph);
		} catch (Throwable throwable) {
			// 忽略因为 release，而visit依旧在运行，导致访问内部数据结构出错的问题
			throw new VisitException(throwable);
		} finally {
			mVisitSig = SIG_NORMAL;
		}
	}

	private void visitLine(Line line, float bottomX, float bottomY) {
		onVisitLineStart(line, bottomX, bottomY);

		int size = line.getCount();
		for (int i = 0; i < size && mVisitSig == SIG_NORMAL; ++i) {
			Element element = line.getElement(i);
			if (element instanceof Glue) {
				bottomX += getAdjustGlueWidth(line, (Glue) element);
				continue;
			}

			Box box = (Box) element;
			float width = box.getWidth();

			float left = bottomX;
			float right = bottomX + width;
			float top = bottomY - line.getLineHeight() - line.getTopPadding();
			float bottom = bottomY;
			mInnerRect.set(left, top, right, bottom);
			mOuterRect.set(left, top, right, bottom);

			// set left
			Element leftElement = null;
			int index = i;
			float offset = 0;
			while (--index >= 0) {
				leftElement = line.getElement(index);
				if (leftElement instanceof Box) {
					break;
				}

				offset += getAdjustGlueWidth(line, (Glue) leftElement);
			}
			mOuterRect.left -= (offset / 2);

			// set right
			Element rightElement = null;
			index = i;
			offset = 0;
			while (++index < size) {
				rightElement = line.getElement(index);
				if (rightElement instanceof Box) {
					break;
				}

				offset += getAdjustGlueWidth(line, (Glue) rightElement);
			}
			mOuterRect.right += (offset / 2);

			mCurrentBoxIndexInternal = i;
			onVisitBox(box, mInnerRect, mOuterRect);
			bottomX += width;
		}

		onVisitLineEnd(line, bottomX, bottomY);
		// 如果是暂停当前行的visit，那么下一次开始的时候要清空状态
		if (mVisitSig == SIG_STOP_LINE_VISIT) {
			mVisitSig = SIG_NORMAL;
		}
	}

	private float getAdjustGlueWidth(Line line, Glue glue) {
		float ratio = line.getRatio();
		if (ratio == 0) {
			return glue.getWidth();
		}

		if (ratio > 0) {
			return glue.getWidth() + ratio * glue.getStretch();
		}

		return glue.getWidth() + ratio * glue.getShrink();
	}

	/**
	 * @param sig 通知 visitor下一步动作
	 */
	protected void sendVisitSig(@VisitSig int sig) {
		mVisitSig = sig;
	}

	protected abstract void onVisitParagraphStart(Paragraph paragraph);

	protected abstract void onVisitParagraphEnd(Paragraph paragraph);

	protected abstract void onVisitLineStart(Line line, float x, float y);

	protected abstract void onVisitLineEnd(Line line, float x, float y);

	/**
	 * @param box   box
	 * @param inner 内部box绘制区域
	 * @param outer 外部绘制区域
	 */
	protected abstract void onVisitBox(Box box, RectF inner, RectF outer);

	/**
	 * 访问异常，可能因为paragraph被回收，然而访问还在进行时抛出
	 * https://bugly.qq.com/v2/crash-reporting/crashes/900021510/415701/report?pid=1&search=texas&searchType=detail&bundleId=&channelId=&version=all&tagList=&start=0&date=all
	 */
	public static class VisitException extends Exception {
		public VisitException(Throwable cause) {
			super(cause);
		}
	}
}
