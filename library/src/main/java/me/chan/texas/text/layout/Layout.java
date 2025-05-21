package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import static me.chan.texas.text.Paragraph.TYPESET_POLICY_CJK_MIX_OPTIMIZATION;
import static me.chan.texas.text.Paragraph.TYPESET_POLICY_DEFAULT;

import android.graphics.Rect;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.misc.BitBucket32;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
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
	private int mLineWidth = -1;
	private Rect mRect;
	private float mLineSpace = 0;
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
		mLineSpace = -1;
		mLineWidth = -1;
		mAdvise.clear();
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

	@RestrictTo(LIBRARY)
	public void setLineWidth(int lineWidth) {
		mLineWidth = lineWidth;
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
		int top = rect == null ? 0 : rect.top;
		if (getLineCount() != 0) {
			Line line = getLine(0);
			top += line.getTopPadding();
		}
		return top;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public int getPaddingBottom() {
		Rect rect = getRect();
		int bottom = rect == null ? 0 : rect.bottom;
		if (getLineCount() != 0) {
			Line line = getLine(getLineCount() - 1);
			bottom += line.getBottomPadding();
		}
		return bottom;
	}

	@RestrictTo(LIBRARY)
	public int getWidth() {
		return mLineWidth + getPaddingLeft() + getPaddingRight();
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
				height += ((lineCount - 1) * mLineSpace);
			}

			return (int) Math.ceil(height);
		}
		return 0;
	}

	@RestrictTo(LIBRARY)
	public void setLineSpace(float lineSpace) {
		mLineSpace = lineSpace;
	}

	@RestrictTo(LIBRARY)
	public Advise getAdvise() {
		return mAdvise;
	}

	public float getLineSpace() {
		return mLineSpace;
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
		/**
		 * 排版建议
		 */
		private float mLineSpace = -1;
		private BreakStrategy mBreakStrategy;
		private TextGravity mTextGravity;
		private int mTypesetPolicies = TYPESET_POLICY_CJK_MIX_OPTIMIZATION;
		private final BitBucket32 mAttributesReference = new BitBucket32();

		public float getLineSpace() {
			return mLineSpace;
		}

		public void setLineSpace(float lineSpace) {
			mLineSpace = lineSpace;
			mAttributesReference.set(INDEX_LINE_SPACE);
		}

		public BreakStrategy getBreakStrategy() {
			return mBreakStrategy;
		}

		public void setBreakStrategy(BreakStrategy breakStrategy) {
			mBreakStrategy = breakStrategy;
			mAttributesReference.set(INDEX_BREAK_STRATEGY);
		}

		public TextGravity getTextGravity() {
			return mTextGravity;
		}

		public void setTextGravity(TextGravity textGravity) {
			mTextGravity = textGravity;
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

		private void clear() {
			mLineSpace = -1;
			mBreakStrategy = null;
			mTypesetPolicies = TYPESET_POLICY_DEFAULT;
			mTextGravity = TextGravity.START;
			mAttributesReference.clear();
		}

		public void copy(RenderOption option) {
			if (!mAttributesReference.get(INDEX_LINE_SPACE)) {
				mLineSpace = option.getLineSpace();
			}
			if (!mAttributesReference.get(INDEX_BREAK_STRATEGY)) {
				mBreakStrategy = option.getBreakStrategy();
			}
			if (!mAttributesReference.get(INDEX_TEXT_GRAVITY)) {
				mTextGravity = option.getTextGravity();
			}
		}

		public void copy(Advise advise) {
			mLineSpace = advise.mLineSpace;
			mTypesetPolicies = advise.mTypesetPolicies;
			mBreakStrategy = advise.mBreakStrategy;
			mTextGravity = advise.mTextGravity;
			mAttributesReference.copy(advise.mAttributesReference);
		}
	}
}
