package me.chan.texas.renderer.selection.visitor;

import android.util.Log;

import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.Texas;
import me.chan.texas.misc.PointF;
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.CompositeRectDrawable;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Span;
import me.chan.texas.text.layout.DrawableSpan;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextSpan;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelectedTextByDragVisitor extends SelectedVisitor {
	@VisibleForTesting
	static final String LINE_RANGE_POLICY_ALL = "all";
	@VisibleForTesting
	public static final String LINE_RANGE_POLICY_START_TO_P2X = "line start to p2's x";
	@VisibleForTesting
	public static final String LINE_RANGE_POLICY_P1X_TO_END = "p1'x to line end";
	@VisibleForTesting
	public static final String LINE_RANGE_POLICY_BETWEEN_P1X_P2X = "between p1's and p2's x";

	private Line mFirstSelectedLine, mLastSelectedLine;
	private final PointF mP1 = new PointF();
	private final PointF mP2 = new PointF();
	private final LineRange mLineRange = new LineRange();

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {
		super.onVisitParagraphStart(paragraph);
		if (Texas.DEBUG_DRAG) {
			Log.d("drag_debug.visitor", "start visit paragraph: " + mP1 + " " + mP2);
		}
	}

	@Override
	public void onVisitParagraphEnd(Paragraph paragraph) {
		// 修正下选中区域
		if (mSelection.isSelectedRegionEmpty()) {
			return;
		}

		linkHead(paragraph, mFirstSelectedLine, mSelection.getFirstBox());
		linkTail(paragraph, mLastSelectedLine, mSelection.getLastBox());
	}

	private void linkHead(Paragraph paragraph, Line line, Span box) {
		if (line == null || !(box instanceof TextSpan)) {
			return;
		}

		int index = line.indexOf(box);
		RectF rectF = mSelection.getFirstRegion();
		index = linkText(line, index, false, rectF);
		if (index != -1) {
			return;
		}

		// 需要找上一行
		Layout layout = paragraph.getLayout();
		for (int lineIndex = layout.indexOf(line) - 1; lineIndex >= 0; --lineIndex) {
			line = layout.getLine(lineIndex);

			int count = line.getElementCount();
			if (count == 0) {
				return;
			}

			Element element = line.getElement(count - 1);
			if (!(element instanceof TextSpan)) {
				return;
			}

			TextSpan textBox = (TextSpan) element;
			if (!textBox.isPenalty()) {
				return;
			}

			Line tmp = layout.getLine(lineIndex);
			RectF bounds = tmp.getBounds();
			rectF = new RectF(bounds.right, bounds.top, bounds.right, bounds.bottom);
			index = linkText(line, count - 1, false, rectF);
			CompositeRectDrawable drawable = new CompositeRectDrawable();
			drawable.append(rectF.left, rectF.top, rectF.right, rectF.bottom);
			mSelection.prependRegion(drawable);
			if (index != -1) {
				return;
			}
		}
	}

	private int linkText(Line line, int index, boolean backward, RectF rectF) {
		int size = line.getElementCount();
		int step = backward ? 1 : -1;
		for (; index >= 0 && index < size; ) {
			Element element = line.getElement(index);
			index += step;
			Span box = (Span) element;
			if (backward) {
				mSelection.appendBox(box);
				rectF.right = box.getOuterBounds().right;
			} else {
				mSelection.prependBox(box);
				rectF.left = box.getOuterBounds().left;
			}

			if (box.isIsolate(backward)) {
				break;
			}
		}

		return index;
	}

	/**
	 * @param paragraph
	 * @param line
	 * @param box
	 */
	private void linkTail(Paragraph paragraph, Line line, Span box) {
		if (line == null || !(box instanceof TextSpan)) {
			return;
		}

		int index = line.indexOf(box);
		int size = line.getElementCount();

		RectF rectF = mSelection.getLastRegion();
		index = linkText(line, index, true, rectF);
		if (index != size) {
			return;
		}

		Element element = line.getElement(size - 1);
		if (!(element instanceof TextSpan)) {
			return;
		}

		TextSpan textBox = (TextSpan) element;
		if (!textBox.isPenalty()) {
			return;
		}

		Layout layout = paragraph.getLayout();
		int lineCount = layout.getLineCount();
		for (int lineIndex = layout.indexOf(line) + 1; lineIndex < lineCount; ++lineIndex) {
			line = layout.getLine(lineIndex);

			int count = line.getElementCount();
			if (count == 0) {
				return;
			}

			element = line.getElement(0);
			if (!(element instanceof TextSpan)) {
				return;
			}

			Line tmp = layout.getLine(lineIndex);
			RectF bounds = tmp.getBounds();
			rectF = new RectF(bounds.left, bounds.top, bounds.left, bounds.bottom);
			index = linkText(line, 0, true, rectF);
			CompositeRectDrawable drawable = new CompositeRectDrawable();
			drawable.append(rectF.left, rectF.top, rectF.right, rectF.bottom);
			mSelection.appendRegion(drawable);
			size = count;

			if (index != size) {
				return;
			}

			element = line.getElement(size - 1);
			if (!(element instanceof TextSpan)) {
				return;
			}

			textBox = (TextSpan) element;
			if (!textBox.isPenalty()) {
				return;
			}
		}
	}

	@Override
	public void onVisitLineStart(Line line, float bottomX, float bottomY) {
		mLineSelected = false;
		updateLineRange(line, bottomX, bottomY, mP1, mP2, mLineRange);
		if (mLineRange.sig != SIG_NORMAL) {
			sendVisitSig(mLineRange.sig);
		}

		if (Texas.DEBUG_DRAG) {
			Log.d("drag_debug.visitor", mLineRange.toString());
		}

		super.onVisitLineStart(line, bottomX, bottomY);
	}

	@VisibleForTesting
	static void updateLineRange(Line line, float bottomX, float bottomY, PointF p1, PointF p2, LineRange lineRange) {
		lineRange.sig = SIG_NORMAL;
		lineRange.startX = lineRange.endX = 0;
		lineRange.policy = null;

		if (bottomY < p1.y) {
			lineRange.sig = SIG_STOP_LINE_VISIT;
			return;
		}

		float top = bottomY - line.getLineHeight();
		if (top >= p2.y) {
			lineRange.sig = SIG_STOP_PARA_VISIT;
			return;
		}

		lineRange.startX = bottomX;
		lineRange.endX = bottomX + line.getLineWidth();

		if (top <= p1.y) {
			if (bottomY < p2.y) {
				lineRange.startX = p1.x;
				lineRange.policy = LINE_RANGE_POLICY_P1X_TO_END;
			} else {
				lineRange.startX = Math.min(p1.x, p2.x);
				lineRange.endX = Math.max(p1.x, p2.x);
				lineRange.policy = LINE_RANGE_POLICY_BETWEEN_P1X_P2X;
			}
		} else {
			if (bottomY < p2.y) {
				lineRange.startX = bottomX;
				lineRange.endX = bottomX + line.getLineWidth();
				lineRange.policy = LINE_RANGE_POLICY_ALL;
			} else {
				lineRange.endX = p2.x;
				lineRange.policy = LINE_RANGE_POLICY_START_TO_P2X;
			}
		}
	}

	public static class LineRange {
		public float startX, endX;
		public String policy;
		public int sig;

		@Override
		public String toString() {
			return "LineRange{" +
					"startX=" + startX +
					", endX=" + endX +
					", policy='" + policy + '\'' +
					", sig=" + ParagraphVisitor.sigToString(sig) +
					'}';
		}
	}

	@Override
	public void onVisitLineEnd(Line line, float x, float y) {
		super.onVisitLineEnd(line, x, y);
		if (!mLineSelected) {
			return;
		}

		if (mFirstSelectedLine == null) {
			mFirstSelectedLine = mLastSelectedLine = line;
			return;
		}

		mLastSelectedLine = line;
	}

	@Override
	protected boolean includeSelectNonTextBoxRegion() {
		return mRenderOption.isDrawEmoticonSelection();
	}

	private boolean mLineSelected = false;

	@Override
	protected boolean selected(Span box, RectF inner, RectF outer) {
		boolean result = selectedImpl(box, inner, outer);
		if (Texas.DEBUG_DRAG) {
			Log.d("drag_debug.visitor", "selected: " + result + " - " + box + " " + inner);
		}
		if (result) {
			mLineSelected = true;
		}
		return result;
	}

	private boolean selectedImpl(Span box, RectF inner, RectF outer) {
		if (!includeSelectNonTextBoxRegion() && box instanceof DrawableSpan) {
			return false;
		}

		return (inner.left >= mLineRange.startX && inner.left <= mLineRange.endX) ||
				(inner.right >= mLineRange.startX && inner.right <= mLineRange.endX);
	}

	@Override
	public void clear() {
		mFirstSelectedLine = mLastSelectedLine = null;
		super.clear();
	}

	public void setRegion(float x1, float y1, float x2, float y2) {
		mP1.set(x1, y1);
		mP2.set(x2, y2);
	}

	@Override
	public String toString() {
		return "Drag{" +
				"p1: " + mP1 + ", p2: " + mP2 +
				'}';
	}
}
