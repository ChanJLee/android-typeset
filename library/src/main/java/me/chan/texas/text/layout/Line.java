package me.chan.texas.text.layout;

import androidx.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.Texas;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.BreakStrategy;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 绘制行
 */
@RestrictTo(LIBRARY)
public class Line extends DefaultRecyclable {
	private static final ObjectPool<Line> POOL = new ObjectPool<>(Texas.getMemoryOption().getLineBufferSize());
	private static final ObjectPool<Builder> BUILDER_POOL = new ObjectPool<>(4);

	private final List<Element> mElements;
	private float mLineHeight;
	private float mRatio;
	private float mTopPadding;
	private float mBottomPadding;
	private float mBaselineOffset;

	private Line() {
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mElements = new ArrayList<>(memoryOption.getLineBoxInitialCapacity());
		reset();
	}

	private void reset() {
		mElements.clear();
		mLineHeight = -1;
		mRatio = -1;
		mBottomPadding = mTopPadding = mBaselineOffset = 0;
	}

	public float getLineHeight() {
		return mLineHeight;
	}

	public float getRatio() {
		return mRatio;
	}

	public void setLineHeight(float lineHeight) {
		mLineHeight = lineHeight;
	}

	public void setRatio(float ratio) {
		mRatio = ratio;
	}

	public float getTopPadding() {
		return mTopPadding;
	}

	public float getBottomPadding() {
		return mBottomPadding;
	}

	public float getBaselineOffset() {
		return mBaselineOffset;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		reset();
		super.recycle();
		POOL.release(this);
	}

	public int getCount() {
		return mElements.size();
	}

	public void addAll(List<? extends Element> list) {
		for (Element element : list) {
			add(element);
		}
	}

	public void add(Element element) {
		mElements.add(element);
	}

	public boolean isEmpty() {
		return mElements.isEmpty();
	}

	public Element getElement(int index) {
		return mElements.get(index);
	}

	public void replace(int prevIndex, Box box) {
		mElements.set(prevIndex, box);
	}

	public static void clean() {
		POOL.clean();
	}

	public String getInfoMsg() {
		return String.valueOf(getRatio());
	}

	@Override
	public String toString() {
		int size = mElements.size();
		if (size == 0) {
			return "";
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (Element element : mElements) {
			if (element instanceof Glue) {
				if (element != Glue.TERMINAL) {
					stringBuilder.append(" ");
				}
			} else {
				stringBuilder.append(element.toString());
			}
		}
		return stringBuilder.toString();
	}

	public static Line obtain() {
		Line line = POOL.acquire();
		if (line == null) {
			return new Line();
		}
		line.reset();
		line.reuse();
		return line;
	}

	public void removeLast(int start) {
		int targetSize = start + 1;
		int size = 0;
		while ((size = mElements.size()) > targetSize) {
			mElements.remove(size - 1);
		}
	}

	public int indexOf(Element element) {
		return mElements.indexOf(element);
	}

	public static class Builder extends DefaultRecyclable {
		private Line mLine;
		private Element mLastTextElement;

		private boolean mContainTerminal;

		private Builder() {
		}

		public void add(Element element) {
			// 剔除typeset建议语义
			if (element == Penalty.FORBIDDEN_BREAK ||
					element == Penalty.ADVISE_BREAK ||
					element == Penalty.FORCE_BREAK) {
				return;
			}

			if (element == Glue.TERMINAL) {
				mContainTerminal = true;
				return;
			}

			if (element instanceof Box) {
				mLastTextElement = element;
				mLine.mElements.add(element);
				if (element instanceof TextBox) {
					TextBox textBox = (TextBox) element;
					mLine.mBottomPadding = Math.max(textBox.getBottomPadding(), mLine.mBottomPadding);
					mLine.mTopPadding = Math.max(textBox.getTopPadding(), mLine.mTopPadding);
					mLine.mBaselineOffset = Math.max(textBox.getBaselineOffset(), mLine.mBaselineOffset);
				}
			} else if (element instanceof Penalty) {
				Penalty penalty = (Penalty) element;
				if (penalty.isFlag()) {
					mLastTextElement = element;
				}
			} else if (element instanceof Glue) {
				// glue
				mLine.mElements.add(element);
			} else {
				throw new IllegalStateException("unknown element");
			}
		}

		public Line build(BreakStrategy breakStrategy, int lineWidth) {
			// strip blank
			strip(mLine);
			if (mLine.isEmpty()) {
				return mLine;
			}

			// 添加 -
			appendIfSuffix(mLine, mLastTextElement);

			// measure line
			measureLine(mLine, breakStrategy, lineWidth);

			// adjust
			if (mContainTerminal && mLine.mRatio > 0) {
				mLine.mRatio = 0;
			}

			return mLine;
		}

		private static void strip(Line line) {
			int count = line.mElements.size();
			for (int i = count - 1; i >= 0; --i) {
				Element element = line.mElements.get(i);
				if (!(element instanceof Glue)) {
					break;
				}
				line.mElements.remove(i);
			}
		}

		private static void appendIfSuffix(Line line, Element lastElement) {
			if (!(lastElement instanceof Penalty)) {
				return;
			}

			int size = line.mElements.size();
			Element element = line.getElement(size - 1);
			if (element instanceof TextBox) {
				TextBox copy = TextBox.obtain((TextBox) element);
				copy.appendContent((Penalty) lastElement);
				line.mElements.set(size - 1, copy);
				line.mBottomPadding = Math.max(copy.getBottomPadding(), line.mBottomPadding);
				line.mTopPadding = Math.max(copy.getTopPadding(), line.mTopPadding);
				line.mBaselineOffset = Math.max(copy.getBaselineOffset(), line.mBaselineOffset);
			}
		}

		private static void measureLine(Line line, BreakStrategy breakStrategy, int lineWidth) {
			float lineHeight = 0;
			float boxWidth = 0;
			float glueWidth = 0;
			float glueShrink = 0;
			float glueStretch = 0;

			for (int i = 0; i < line.getCount(); ++i) {
				Element element = line.getElement(i);
				if (element instanceof Box) {
					Box box = (Box) element;
					boxWidth += box.getWidth();
					if (lineHeight < box.getHeight()) {
						lineHeight = box.getHeight();
					}
				} else if (element instanceof Glue) {
					Glue glue = (Glue) element;
					glueWidth += glue.getWidth();
					glueShrink += glue.getShrink();
					glueStretch += glue.getStretch();
				}
			}

			line.setLineHeight(lineHeight);
			if (breakStrategy == BreakStrategy.SIMPLE) {
				line.setRatio(0);
				return;
			}

			float ratio = 0;
			float totalWidth = glueWidth + boxWidth;
			if (totalWidth == lineWidth) {
				ratio = 0;
			} else if (totalWidth > lineWidth) {
				ratio = glueShrink != 0 ? (lineWidth - totalWidth) / glueShrink : 0;
			} else {
				ratio = glueStretch != 0 ? (lineWidth - totalWidth) / glueStretch : 0;
			}

			line.setRatio(ratio);
		}

		@Override
		public void recycle() {
			if (isRecycled()) {
				return;
			}

			mLine = null;
			mLastTextElement = null;
			mContainTerminal = false;
			super.recycle();

			BUILDER_POOL.release(this);
		}

		public static Builder obtain() {
			Builder builder = BUILDER_POOL.acquire();
			if (builder == null) {
				builder = new Builder();
			}

			builder.reuse();
			builder.mLine = Line.obtain();
			return builder;
		}
	}
}