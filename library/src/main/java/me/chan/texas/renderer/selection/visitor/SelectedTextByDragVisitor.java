package me.chan.texas.renderer.selection.visitor;

import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.Texas;
import me.chan.texas.misc.PointF;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.DrawableBox;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextBox;

import java.util.ArrayList;
import java.util.List;

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
	private float mLastBoxX;
	private final List<Float> mLinesWidthBuffer = new ArrayList<>();
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

	private void linkHead(Paragraph paragraph, Line line, Box box) {
		if (line == null || box == null) {
			return;
		}

		int index = line.indexOf(box) - 1;
		if (index < 0) {
			return;
		}

		RectF rectF = mSelection.getFirstRegion();
		assert rectF != null;

		index = link(line, index, false, rectF);
		if (index >= 0) {
			return;
		}

		Layout layout = paragraph.getLayout();
		// 需要找上一行
		for (int indexOfLine = layout.indexOf(line) - 1;
			 index < 0 && indexOfLine >= 0; --indexOfLine) {
			line = layout.getLine(indexOfLine);

			int count = line.getCount();
			if (count == 0) {
				return;
			}

			Element element = line.getElement(count - 1);
			if (!(element instanceof TextBox)) {
				return;
			}

			TextBox textBox = (TextBox) element;
			if (!textBox.isPenalty()) {
				return;
			}

			float bottom = rectF.top - layout.getLineSpace();
			float right = mLinesWidthBuffer.get(index);
			rectF = new RectF(right - box.getWidth(), bottom - line.getLineHeight(), right, bottom);
			mSelection.prependRegion(rectF);
			mSelection.prependBox(textBox);
			index = link(line, count - 2, false, rectF);
		}
	}

	private int link(Line line, int index, boolean toRight, RectF rectF) {
		int size = line.getCount();
		int step = toRight ? 1 : -1;
		for (; index >= 0 && index < size; index += step) {
			Element element = line.getElement(index);
			if (!(element instanceof Box)) {
				return index;
			}

			Box box = (Box) element;
			if (toRight) {
				mSelection.appendBox(box);
				rectF.right += box.getWidth();
			} else {
				mSelection.prependBox(box);
				rectF.left -= box.getWidth();
			}
		}

		return index;
	}

	private void linkTail(Paragraph paragraph, Line line, Box box) {
		if (line == null || box == null) {
			return;
		}

		int index = line.indexOf(box) + 1;
		int size = line.getCount();
		if (index >= size) {
			if (!(box instanceof TextBox) ||
					!((TextBox) box).isPenalty()) {
				return;
			}
		}

		RectF rectF = mSelection.getLastRegion();
		assert rectF != null;

		index = link(line, index, true, rectF);

		// 需要找下一行
		if (index < size) {
			return;
		}

		Element element = line.getElement(size - 1);
		if (!(element instanceof TextBox)) {
			return;
		}

		TextBox textBox = (TextBox) element;
		if (!textBox.isPenalty()) {
			return;
		}

		Layout layout = paragraph.getLayout();
		int lineCount = layout.getLineCount();
		for (int indexOfLine = layout.indexOf(line) + 1;
			 indexOfLine < lineCount && index >= size; ++indexOfLine) {
			line = layout.getLine(indexOfLine);

			int count = line.getCount();
			if (count == 0) {
				return;
			}

			element = line.getElement(0);
			if (!(element instanceof TextBox)) {
				return;
			}

			textBox = (TextBox) element;
			float top = rectF.bottom + layout.getLineSpace();
			rectF = new RectF(layout.getPaddingLeft(), top, textBox.getWidth() + layout.getPaddingLeft(), top + textBox.getHeight());
			mSelection.appendRegion(rectF);
			mSelection.appendBox(textBox);
			index = link(line, 1, true, rectF);
			size = count;
		}
	}

	@Override
	public void onVisitLineStart(Line line, float bottomX, float bottomY) {
		mLineSelected = false;
		mLastBoxX = 0;

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
					", sig=" + sig +
					'}';
		}
	}

	@Override
	public void onVisitLineEnd(Line line, float x, float y) {
		super.onVisitLineEnd(line, x, y);
		mLinesWidthBuffer.add(mLastBoxX);

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
	protected boolean selected(Box box, RectF inner, RectF outer) {
		boolean result = selectedImpl(box, inner, outer);
		if (Texas.DEBUG_DRAG) {
			Log.d("drag_debug.visitor", "selected: " + result + " - " + box + " " + inner);
		}
		if (result) {
			mLineSelected = true;
		}
		return result;
	}

	private boolean selectedImpl(Box box, RectF inner, RectF outer) {
		if (!includeSelectNonTextBoxRegion() && box instanceof DrawableBox) {
			return false;
		}

		mLastBoxX = inner.right;
		return (inner.left >= mLineRange.startX && inner.left <= mLineRange.endX) ||
				(inner.right >= mLineRange.startX && inner.right <= mLineRange.endX);
	}

	@Override
	public void clear() {
		mFirstSelectedLine = mLastSelectedLine = null;
		mLinesWidthBuffer.clear();
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
