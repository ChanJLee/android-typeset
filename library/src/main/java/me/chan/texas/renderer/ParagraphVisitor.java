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

	private final State mState = new State();
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

	public void visit(Paragraph paragraph) throws VisitException {
		try {
			onVisitParagraphStart(paragraph);
			Layout layout = paragraph.getLayout();
			int end = layout.getLineCount();
			layout.prepareGetLineBoundsIncremental(mState.lineRect);
			for (int i = 0; i < end && mState.visitSig != SIG_STOP_PARA_VISIT; ++i) {
				Line line = layout.getLine(i);
				layout.getLineBoundsIncremental(i, mState.lineRect);

				mState.context.clear();
				mState.context.setParagraphLocationAttribute(RendererContext.LOCATION_PARAGRAPH_START, i == 0);
				mState.context.setParagraphLocationAttribute(RendererContext.LOCATION_PARAGRAPH_END, i == end - 1);

				visitLine(line, mState.lineRect.left, mState.lineRect.bottom);
			}
			onVisitParagraphEnd(paragraph);
		} catch (Throwable throwable) {
			// 忽略因为 release，而visit依旧在运行，导致访问内部数据结构出错的问题
			throw new VisitException(throwable);
		} finally {
			mState.visitSig = SIG_NORMAL;
		}
	}

	private void visitLine(Line line, float bottomX, float bottomY) {
		onVisitLineStart(line, bottomX, bottomY);

		int size = line.getCount();
		assignBoxMeta(line, 0, size, bottomX, bottomY, mState.context.currentBoxMetaInfo);
		if (mState.context.currentBoxMetaInfo.isValid() && mState.visitSig == SIG_NORMAL) {
			do {
				TexasUtils.copyRect(mState.innerRect, mState.context.currentBoxMetaInfo.inner);
				TexasUtils.copyRect(mState.outerRect, mState.context.currentBoxMetaInfo.inner);
				if (mState.context.prevBoxMetaInfo.isValid()) {
					mState.outerRect.left = (mState.context.prevBoxMetaInfo.inner.right + mState.context.currentBoxMetaInfo.inner.left) / 2.0f;
				}

				assignBoxMeta(line, mState.context.currentBoxMetaInfo.index + 1, size, mState.context.currentBoxMetaInfo.inner.right, bottomY, mState.context.nextBoxMetaInfo);
				if (mState.context.nextBoxMetaInfo.isValid()) {
					mState.outerRect.right = (mState.context.nextBoxMetaInfo.inner.left + mState.context.currentBoxMetaInfo.inner.right) / 2.0f;
				}

				onVisitBox(mState.context.currentBoxMetaInfo.box, mState.innerRect, mState.outerRect, mState.context);
				if (!mState.context.nextBoxMetaInfo.isValid() || mState.visitSig != SIG_NORMAL) {
					break;
				}

				mState.context.prevBoxMetaInfo.set(mState.context.currentBoxMetaInfo);
				mState.context.currentBoxMetaInfo.set(mState.context.nextBoxMetaInfo);
				mState.context.nextBoxMetaInfo.clear();
			} while (true);
		}

		onVisitLineEnd(line, bottomX, bottomY);
		// 如果是暂停当前行的visit，那么下一次开始的时候要清空状态
		if (mState.visitSig == SIG_STOP_LINE_VISIT) {
			mState.visitSig = SIG_NORMAL;
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
				meta.inner.top = bottomY - line.getLineHeight();
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
		mState.visitSig = sig;
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

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void saveTo(State state) {

	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void restore(State state) {

	}

	/**
	 * 访问异常，可能因为paragraph被回收，然而访问还在进行时抛出
	 */
	public static class VisitException extends Exception {
		public VisitException(Throwable cause) {
			super(cause);
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static class State {
		private final RectF innerRect = new RectF();
		private final RectF outerRect = new RectF();
		private final RectF lineRect = new RectF();
		private int visitSig = SIG_NORMAL;
		private final RendererContext context = new RendererContext();

		public void copy(State state) {
			TexasUtils.copyRect(state.innerRect, innerRect);
			TexasUtils.copyRect(state.outerRect, outerRect);
			TexasUtils.copyRect(state.lineRect, lineRect);
			state.visitSig = visitSig;
			state.context.copy(context);
		}
	}
}
