package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import static me.chan.texas.text.Paragraph.TYPESET_POLICY_CJK_MIX_OPTIMIZATION;
import static me.chan.texas.text.Paragraph.TYPESET_POLICY_DEFAULT;

import me.chan.texas.misc.Rect;

import android.text.TextUtils;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.misc.BitBucket32;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextGravity;

import java.util.ArrayList;
import java.util.List;

public class Layout extends DefaultRecyclable {
	private static final ObjectPool<Layout> POOL = new ObjectPool<>(Texas.getMemoryOption().getParagraphBufferSize());
	public static final String ALGORITHM_UNKNOWN = "unknown";

	private final Advise mAdvise = new Advise();
	private final List<Line> mLines;
	private int mWidth = -1;
	private Rect mRect;
	private String mAlgorithm = ALGORITHM_UNKNOWN;

	private Layout() {
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mLines = new ArrayList<>(memoryOption.getParagraphLineInitialCapacity());
	}

	@RestrictTo(LIBRARY)
	public Line getLine(int index) {
		return mLines.get(index);
	}

	public int getLineCount() {
		return mLines.size();
	}

	@RestrictTo(LIBRARY)
	public void addLine(Line line) {
		mLines.add(line);
	}

	@RestrictTo(LIBRARY)
	public int indexOf(Line line) {
		return mLines.indexOf(line);
	}

	@Override
	protected void onRecycle() {
		clear();
		mWidth = -1;
		mAdvise.reset();
		mRect = null;
		mAlgorithm = ALGORITHM_UNKNOWN;
		POOL.release(this);
	}

	public void clear() {
		for (int i = 0; i < mLines.size(); ++i) {
			mLines.get(i).recycle();
		}
		mLines.clear();
	}

	public static Layout obtain() {
		Layout layout = POOL.acquire();
		if (layout == null) {
			layout = new Layout();
		}
		layout.reuse();
		return layout;
	}

	public static Layout obtain(Layout other) {
		Layout layout = POOL.acquire();
		if (layout == null) {
			layout = new Layout();
		}
		layout.mAdvise.copy(other.mAdvise);
		layout.mRect = other.mRect;
		layout.reuse();
		return layout;
	}

	@RestrictTo(LIBRARY)
	public void setAlgorithm(String algorithm) {
		mAlgorithm = algorithm;
	}

	@RestrictTo(LIBRARY)
	public String getAlgorithm() {
		return mAlgorithm;
	}

	public boolean isLayout() {
		return !TextUtils.equals(mAlgorithm, ALGORITHM_UNKNOWN);
	}

	@RestrictTo(LIBRARY)
	public void setWidth(int width) {
		mWidth = width;
	}

	@RestrictTo(LIBRARY)
	public void getRect(Rect rect) {
		if (mRect == null) {
			return;
		}
		rect.set(mRect);
	}

	@RestrictTo(LIBRARY)
	public void setRect(Rect rect) {
		mRect = rect;
	}

	public Rect getRect() {
		return mRect;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public int getPaddingLeft() {
		Rect rect = getRect();
		return rect == null ? 0 : rect.left;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public int getPaddingRight() {
		Rect rect = getRect();
		return rect == null ? 0 : rect.right;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public int getPaddingTop() {
		Rect rect = getRect();
		return rect == null ? 0 : rect.top;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public int getPaddingBottom() {
		Rect rect = getRect();
		return rect == null ? 0 : rect.bottom;
	}

	@RestrictTo(LIBRARY)
	public void prepareGetLineBoundsIncremental(RectF bounds) {
		bounds.left = getPaddingLeft();
		bounds.right = bounds.left + mWidth;
		bounds.top = 0;
		bounds.bottom = getPaddingTop() - getLineSpace();
	}

	@RestrictTo(LIBRARY)
	public void getLineBoundsIncremental(int index, RectF bounds) {
		if (index < 0 || index >= getLineCount()) {
			return;
		}

		Line line = getLine(index);
		getLineHorizontalBounds(line, bounds);
		bounds.top = bounds.bottom + getLineSpace();
		bounds.bottom = bounds.top + line.getLineHeight();
	}

	public void getLineBounds(int index, RectF bounds) {
		if (index < 0 || index >= getLineCount()) {
			return;
		}

		Line line = getLine(index);
		getLineHorizontalBounds(line, bounds);
		getLineVerticalBounds(index, bounds);
	}

	private void getLineHorizontalBounds(Line line, RectF bounds) {
		int horizontalGravity = mAdvise.getTextGravity() & TextGravity.HORIZONTAL_MASK;
		if (horizontalGravity == TextGravity.START) {
			bounds.left = getPaddingLeft();
		} else if (horizontalGravity == TextGravity.CENTER_HORIZONTAL) {
			float offsetX = (mWidth - line.getLineWidth()) / 2.0f;
			bounds.left = getPaddingLeft() + offsetX;
		} else if (horizontalGravity == TextGravity.END) {
			float offsetX = mWidth - line.getLineWidth();
			bounds.left = getPaddingLeft() + offsetX;
		} else {
			throw new IllegalStateException("unknown text gravity");
		}
		bounds.right = bounds.left + line.getLineWidth();
	}

	private void getLineVerticalBounds(int index, RectF bounds) {
		bounds.top = getPaddingTop();
		float lineSpace = getLineSpace();
		for (int i = 0; i < index; ++i) {
			Line prev = getLine(i);
			bounds.top = bounds.top + prev.getLineHeight() + lineSpace;
		}
		Line line = getLine(index);
		bounds.bottom = bounds.top + line.getLineHeight();
	}

	@RestrictTo(LIBRARY)
	public int getWidth() {
		return mWidth + getPaddingLeft() + getPaddingRight();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public int getHeight() {
		if (isRecycled()) {
			return 0;
		}

		int lineCount = getLineCount();
		if (lineCount > 0) {
			float height = getPaddingTop() + getPaddingBottom() + getHeight0();
			return (int) Math.ceil(height);
		}
		return 0;
	}

	private int getHeight0() {
		if (isRecycled()) {
			return 0;
		}

		int lineCount = getLineCount();
		if (lineCount > 0) {
			float height = 0;
			for (int i = 0; i < lineCount; ++i) {
				Line line = getLine(i);
				height += line.getLineHeight();
			}

			if (lineCount > 1) {
				height += ((lineCount - 1) * getLineSpace());
			}

			return (int) Math.ceil(height);
		}
		return 0;
	}

	@RestrictTo(LIBRARY)
	public Advise getAdvise() {
		return mAdvise;
	}

	public float getLineSpace() {
		return mAdvise.getLineSpacingExtra();
	}

	@Override
	public String toString() {
		if (mLines.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (Line line : mLines) {
			sb.append(line.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	@RestrictTo(LIBRARY)
	public void finishLayout() {
		if (mLines.isEmpty()) {
			return;
		}

		int seq = 0;
		for (int i = 0; i < mLines.size(); ++i) {
			Line line = mLines.get(i);
			for (int j = 0; j < line.getCount(); ++j) {
				Element element = line.getElement(j);
				if (element instanceof Box) {
					Box box = (Box) element;
					box.setSeq(seq++);
				}
			}
		}
	}

	@RestrictTo(LIBRARY)
	public static class Advise {
		private final static int INDEX_LINE_SPACE = 0;
		private final static int INDEX_BREAK_STRATEGY = 1;
		private final static int INDEX_TEXT_GRAVITY = 2;
		
		private float mLineSpacingExtra;
		private BreakStrategy mBreakStrategy;
		private int mTextGravity;
		private int mTypesetPolicies = TYPESET_POLICY_CJK_MIX_OPTIMIZATION;
		private final BitBucket32 mAttributesReference = new BitBucket32();

		public float getLineSpacingExtra() {
			return mLineSpacingExtra;
		}

		public void setLineSpacingExtra(float lineSpacingExtra) {
			mLineSpacingExtra = lineSpacingExtra;
			mAttributesReference.set(INDEX_LINE_SPACE);
		}

		public BreakStrategy getBreakStrategy() {
			return mBreakStrategy;
		}

		public void setBreakStrategy(BreakStrategy breakStrategy) {
			mBreakStrategy = breakStrategy;
			mAttributesReference.set(INDEX_BREAK_STRATEGY);
		}

		public int getTextGravity() {
			return mTextGravity;
		}

		public void setTextGravity(@TextGravity.GravityMask int gravity) {
			mTextGravity = RenderOption.adviceTextGravityMask(gravity);
			mAttributesReference.set(INDEX_TEXT_GRAVITY);
		}

		public boolean checkTypesetPolicy(@Paragraph.TypesetPolicy int typesetPolicy) {
			return (mTypesetPolicies & typesetPolicy) != 0;
		}

		public void addTypesetPolicy(@Paragraph.TypesetPolicy int typesetPolicy) {
			mTypesetPolicies |= typesetPolicy;
		}

		public void clearTypesetPolicy() {
			mTypesetPolicies = TYPESET_POLICY_DEFAULT;
		}

		void reset() {
			mLineSpacingExtra = -1;
			mBreakStrategy = null;
			mTypesetPolicies = TYPESET_POLICY_DEFAULT;
			mTextGravity = TextGravity.START | TextGravity.TOP;
			mAttributesReference.clear();
		}

		public void copy(RenderOption option) {
			if (!mAttributesReference.get(INDEX_LINE_SPACE)) {
				mLineSpacingExtra = option.getLineSpacingExtra();
			}
			if (!mAttributesReference.get(INDEX_BREAK_STRATEGY)) {
				mBreakStrategy = option.getBreakStrategy();
			}
			if (!mAttributesReference.get(INDEX_TEXT_GRAVITY)) {
				mTextGravity = option.getTextGravity();
			}
		}

		public void copy(Advise advise) {
			mLineSpacingExtra = advise.mLineSpacingExtra;
			mTypesetPolicies = advise.mTypesetPolicies;
			mBreakStrategy = advise.mBreakStrategy;
			mTextGravity = advise.mTextGravity;
			mAttributesReference.copy(advise.mAttributesReference);
		}
	}
}
