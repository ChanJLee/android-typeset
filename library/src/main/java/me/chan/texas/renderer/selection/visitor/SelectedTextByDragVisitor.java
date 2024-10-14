package me.chan.texas.renderer.selection.visitor;

import android.graphics.RectF;

import androidx.annotation.RestrictTo;

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

	private Line mFirstSelectedLine, mLastSelectedLine;
	private float mLastBoxX;
	private final List<Float> mLinesWidthBuffer = new ArrayList<>();

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
		if (bottomY <= mY1) {
			sendVisitSig(SIG_STOP_LINE_VISIT);
		} else if (bottomY - line.getLineHeight() >= mY2) {
			sendVisitSig(SIG_STOP_PARA_VISIT);
		}
		super.onVisitLineStart(line, bottomX, bottomY);
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

		// 同行
		if (outer.top <= mY1 && outer.bottom >= mY1 &&
				outer.top <= mY2 && outer.bottom >= mY2) {
			float start = mX1;
			float end = mX2;
			if (mX1 > mX2) {
				start = mX2;
				end = mX1;
			}
			return (inner.right > start && inner.right <= end) ||
					(inner.left >= start && inner.left < end);
		}

		// 不同行
		// y方向一点不沾边 直接忽略
		if (outer.bottom <= mY1 || outer.top >= mY2) {
			return false;
		}

		// 全包裹
		if (outer.top >= mY1 && outer.bottom <= mY2) {
			return true;
		}

		// 上半部分夹住
		if (outer.top <= mY1 && outer.bottom >= mY1) {
			return mIsFocusP1 ?
					outer.bottom >= mY1 && inner.right > mX1
					: inner.right > mX1;
		}

		// 下半部分夹住
		return mIsFocusP1 ?
				inner.left < mX2
				: outer.top <= mY2 && inner.left < mX2;
	}

	@Override
	public void clear() {
		mFirstSelectedLine = mLastSelectedLine = null;
		mX1 = mX2 = mY2 = mY1 = 0;
		mIsFocusP1 = false;
		mLinesWidthBuffer.clear();
		super.clear();
	}

	private float mX1, mY1, mX2, mY2;
	private boolean mIsFocusP1;

	public void setRegion(float x1, float y1, float x2, float y2, boolean isFocusP1) {
		mX1 = x1;
		mY1 = y1;
		mX2 = x2;
		mY2 = y2;
		mIsFocusP1 = isFocusP1;
	}

	@Override
	public String toString() {
		return "Drag{" +
				"mX1=" + mX1 +
				", mY1=" + mY1 +
				", mX2=" + mX2 +
				", mY2=" + mY2 +
				", mIsFocusP1=" + mIsFocusP1 +
				'}';
	}
}
