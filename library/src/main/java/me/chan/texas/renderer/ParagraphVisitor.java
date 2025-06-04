package me.chan.texas.renderer;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.RectF;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.utils.TexasUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class ParagraphVisitor {

	private final RectF mInnerRect = new RectF();
	private final RectF mOuterRect = new RectF();
	private final RectF mLineRect = new RectF();
	private int mVisitSig = SIG_NORMAL;

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

	public static String sigToString(@VisitSig int sig) {
		switch (sig) {
			case SIG_NORMAL:
				return "SIG_NORMAL";
			case SIG_STOP_LINE_VISIT:
				return "SIG_STOP_LINE_VISIT";
			case SIG_STOP_PARA_VISIT:
				return "SIG_STOP_PARA_VISIT";
			default:
				return "unknown sig";
		}
	}

	private final RendererContext mTypesetContext = new RendererContext();

	public void visit(Paragraph paragraph) throws VisitException {
		try {
			onVisitParagraphStart(paragraph);
			Layout layout = paragraph.getLayout();
			int end = layout.getLineCount();
			RectF lineRect = null;
			for (int i = 0; i < end && mVisitSig != SIG_STOP_PARA_VISIT; ++i) {
				Line line = layout.getLine(i);
				if (lineRect != null) {
					layout.getLineBoundsIncremental(i, lineRect);
				} else {
					lineRect = mLineRect;
					layout.getLineBounds(i, lineRect);
				}

				mTypesetContext.clear();
				mTypesetContext.setParagraphLocationAttribute(RendererContext.LOCATION_PARAGRAPH_START, i == 0);
				mTypesetContext.setParagraphLocationAttribute(RendererContext.LOCATION_PARAGRAPH_END, i == end - 1);

				visitLine(line, lineRect.left, lineRect.bottom);
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
		assignBoxMeta(line, 0, size, bottomX, bottomY, mTypesetContext.currentBoxMetaInfo);
		if (mTypesetContext.currentBoxMetaInfo.isValid() && mVisitSig == SIG_NORMAL) {
			do {
				TexasUtils.copyRect(mInnerRect, mTypesetContext.currentBoxMetaInfo.inner);
				TexasUtils.copyRect(mOuterRect, mTypesetContext.currentBoxMetaInfo.inner);
				if (mTypesetContext.prevBoxMetaInfo.isValid()) {
					mOuterRect.left = (mTypesetContext.prevBoxMetaInfo.inner.right + mTypesetContext.currentBoxMetaInfo.inner.left) / 2.0f;
				}

				assignBoxMeta(line, mTypesetContext.currentBoxMetaInfo.index + 1, size, mTypesetContext.currentBoxMetaInfo.inner.right, bottomY, mTypesetContext.nextBoxMetaInfo);
				if (mTypesetContext.nextBoxMetaInfo.isValid()) {
					mOuterRect.right = (mTypesetContext.nextBoxMetaInfo.inner.left + mTypesetContext.currentBoxMetaInfo.inner.right) / 2.0f;
				}

				onVisitBox(mTypesetContext.currentBoxMetaInfo.box, mInnerRect, mOuterRect, mTypesetContext);
				if (!mTypesetContext.nextBoxMetaInfo.isValid() || mVisitSig != SIG_NORMAL) {
					break;
				}

				mTypesetContext.prevBoxMetaInfo.set(mTypesetContext.currentBoxMetaInfo);
				mTypesetContext.currentBoxMetaInfo.set(mTypesetContext.nextBoxMetaInfo);
				mTypesetContext.nextBoxMetaInfo.clear();
			} while (true);
		}

		onVisitLineEnd(line, bottomX, bottomY);
		// 如果是暂停当前行的visit，那么下一次开始的时候要清空状态
		if (mVisitSig == SIG_STOP_LINE_VISIT) {
			mVisitSig = SIG_NORMAL;
		}
	}

	private void assignBoxMeta(Line line, int start, int end, float bottomX, float bottomY, RendererContext.BoxMetaInfo meta) {
		for (int index = start; index < end; ++index) {
			Element element = line.getElement(index);
			if (element instanceof Box) {
				Box box = (Box) element;
				float width = box.getWidth();
				meta.inner.left = bottomX;
				meta.inner.right = bottomX + width;
				meta.inner.top = bottomY - line.getLineHeight() - line.getTopPadding();
				meta.inner.bottom = bottomY;
				meta.box = box;
				meta.index = index;
				return;
			}
			bottomX += getAdjustGlueWidth(line, (Glue) element);
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
	protected abstract void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context);

	/**
	 * 访问异常，可能因为paragraph被回收，然而访问还在进行时抛出
	 */
	public static class VisitException extends Exception {
		public VisitException(Throwable cause) {
			super(cause);
		}
	}
}
