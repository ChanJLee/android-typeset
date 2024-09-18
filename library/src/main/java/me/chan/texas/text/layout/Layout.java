package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import static me.chan.texas.text.Paragraph.TYPESET_POLICY_DEFAULT;

import android.graphics.Rect;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.BreakStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Layout extends DefaultRecyclable {
	private static final ObjectPool<Layout> POOL = new ObjectPool<>(Texas.getMemoryOption().getParagraphBufferSize());

	private static final AtomicInteger UUID = new AtomicInteger(0);

	private final Advise mAdvise = new Advise();

	private final List<Line> mLines;
	private int mLineWidth = -1;
	private Rect mRect;
	private float mLineSpace = 0;

	private String mAlgorithm = "unknown";

	private int mId = 0;

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
		mAlgorithm = "unknown";
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
		layout.mId = UUID.incrementAndGet();
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
		layout.mId = UUID.incrementAndGet();
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

	public int getId() {
		return mId;
	}

	@RestrictTo(LIBRARY)
	public static class Advise {
		/**
		 * 排版建议
		 */
		private float mLineSpace = -1;
		private BreakStrategy mBreakStrategy;
		private int mTypesetPolicy = TYPESET_POLICY_DEFAULT;

		public float getLineSpace() {
			return mLineSpace;
		}

		public void setLineSpace(float lineSpace) {
			mLineSpace = lineSpace;
		}

		public BreakStrategy getBreakStrategy() {
			return mBreakStrategy;
		}

		public void setBreakStrategy(BreakStrategy breakStrategy) {
			mBreakStrategy = breakStrategy;
		}

		public int getTypesetPolicy() {
			return mTypesetPolicy;
		}

		public void setTypesetPolicy(int typesetPolicy) {
			mTypesetPolicy = typesetPolicy;
		}

		private void clear() {
			mLineSpace = -1;
			mBreakStrategy = null;
			mTypesetPolicy = TYPESET_POLICY_DEFAULT;
		}

		public void copy(Advise advise) {
			mLineSpace = advise.mLineSpace;
			mTypesetPolicy = advise.mTypesetPolicy;
			mBreakStrategy = advise.mBreakStrategy;
		}
	}
}
