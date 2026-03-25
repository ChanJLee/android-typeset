package me.chan.texas.text.layout;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.Texas;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.misc.RectF;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.util.TexasIterator;
import me.chan.texas.utils.TexasUtils;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 绘制行
 */
public class Line extends DefaultRecyclable {
	private static final ObjectPool<Line> POOL = new ObjectPool<>(Texas.getMemoryOption().getLineBufferSize());
	private static final ObjectPool<Builder> BUILDER_POOL = new ObjectPool<>(4);

	private final List<Element> mElements;
	private float mLineHeight;
	private float mLineWidth;
	private float mRatio;
	private float mBaselineOffset;
	private final RectF mBounds = new RectF();

	private Line() {
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mElements = new ArrayList<>(memoryOption.getLineBoxInitialCapacity());
		reset();
	}

	private void reset() {
		mElements.clear();
		mLineHeight = -1;
		mRatio = -1;
		mBaselineOffset = 0;
		mBounds.setEmpty();
	}

	public float getLineHeight() {
		return mLineHeight;
	}

	@RestrictTo(LIBRARY)
	public void setLineWidth(float lineWidth) {
		mLineWidth = lineWidth;
	}

	@RestrictTo(LIBRARY)
	public float getRatio() {
		return mRatio;
	}

	@RestrictTo(LIBRARY)
	public void setLineHeight(float lineHeight) {
		mLineHeight = lineHeight;
	}

	public float getLineWidth() {
		return mLineWidth;
	}

	@RestrictTo(LIBRARY)
	public void setRatio(float ratio) {
		mRatio = ratio;
	}

	@Override
	protected void onRecycle() {
		reset();
		POOL.release(this);
	}

	@RestrictTo(LIBRARY)
	public int getElementCount() {
		return mElements.size();
	}

	public int getSpanCount() {
		return getElementCount();
	}

	public float getBaselineOffset() {
		return mBaselineOffset;
	}

	@RestrictTo(LIBRARY)
	public void add(Element element) {
		mElements.add(element);
	}

	public boolean isEmpty() {
		return mElements.isEmpty();
	}

	@RestrictTo(LIBRARY)
	public Element getElement(int index) {
		return mElements.get(index);
	}

	public Span getSpan(int index) {
		return (Span) getElement(index);
	}

	@RestrictTo(LIBRARY)
	public void replace(int prevIndex, Span span) {
		mElements.set(prevIndex, span);
	}

	@RestrictTo(LIBRARY)
	public static void clean() {
		POOL.clean();
	}

	@RestrictTo(LIBRARY)
	public String getInfoMsg() {
		return String.valueOf(getRatio());
	}

	@Override
	public String toString() {
		int size = mElements.size();
		if (size == 0 || mBounds.isEmpty()) {
			return "";
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < mElements.size(); ++i) {
			Element element = mElements.get(i);
			if (element instanceof Span) {
				Span current = (Span) element;
				stringBuilder.append(current);
				if (Float.compare(current.getInnerBounds().right, current.getOuterBounds().right) != 0) {
					stringBuilder.append(" ");
				}
			}
		}
		return stringBuilder.toString();
	}

	@RestrictTo(LIBRARY)
	public static Line obtain() {
		Line line = POOL.acquire();
		if (line == null) {
			return new Line();
		}
		line.reset();
		line.reuse();
		return line;
	}

	@RestrictTo(LIBRARY)
	public int indexOf(Element element) {
		return mElements.indexOf(element);
	}

	@RestrictTo(LIBRARY)
	public void trim() {
		// 1. 给定一个数组，里面有两种元素，一种是box，另一种是其他
		// 2. 移除数组中非box的元素且保证box的顺序
		// 3. 尝试用o(n)的方法合并
		// 例子：
		// 一个数组内容[b, o, b, o, o] 其中b代表box，合并数组后内容变成[b, b]
		int writeIndex = findWritePoint();
		for (int i = writeIndex + 1; i < mElements.size(); ++i) {
			Element element = mElements.get(i);
			if (element instanceof Span) {
				mElements.set(writeIndex++, element);
			}
		}

		int count = mElements.size() - writeIndex;
		for (int i = 0; i < count; ++i) {
			mElements.remove(mElements.size() - 1);
		}
	}

	private int findWritePoint() {
		int i = 0;
		for (; i < mElements.size(); ++i) {
			Element element = mElements.get(i);
			if (!(element instanceof Span)) {
				return i;
			}
		}
		return i;
	}

	@RestrictTo(LIBRARY)
	public void setBounds(RectF bounds) {
		TexasUtils.copyRect(mBounds, bounds);
	}

	public RectF getBounds() {
		return mBounds;
	}

	public TexasIterator<Span> iterator() {
		return new TexasIterator<Span>() {
			private int mIndex = -1;

			@Override
			public Span next() {
				return restore(mIndex + 1);
			}

			@Override
			public Span prev() {
				return restore(mIndex - 1);
			}

			@Nullable
			@Override
			public Span current() {
				return restore(mIndex);
			}

			@Override
			public Span restore(int state) {
				if (state < 0 || state >= getElementCount()) {
					return null;
				}

				return (Span) getElement(mIndex = state);
			}

			@Override
			public int save() {
				return mIndex;
			}
		};
	}

	@RestrictTo(LIBRARY)
	public static class Builder extends DefaultRecyclable {
		private Line mLine;
		private Element mLastTextElement;

		private boolean mContainTerminal;

		private final List<Element> mElements = new ArrayList<>(128);

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

			if (element instanceof Span) {
				mLastTextElement = element;
				mElements.add(element);
			} else if (element instanceof Penalty) {
				Penalty penalty = (Penalty) element;
				if (penalty.isFlag()) {
					mLastTextElement = element;
				}
			} else if (element instanceof Glue) {
				// glue
				mElements.add(element);
			} else {
				throw new IllegalStateException("unknown element");
			}
		}

		public Line build(BreakStrategy breakStrategy, float lineWidth, float lineHeight) {
			// strip blank
			strip(mElements);
			if (mElements.isEmpty()) {
				return mLine;
			}

			// 添加 -
			appendIfSuffix(mElements, mLastTextElement);

			// 合並
			mergeText(mLine, mElements);

			// measure line
			measureLine(mLine, breakStrategy, lineWidth, lineHeight);

			// adjust
			if (mContainTerminal && mLine.mRatio > 0) {
				mLine.mRatio = 0;
			}

			return mLine;
		}

		private static void mergeText(Line line, List<Element> elements) {
			int count = elements.size();
			if (count == 0) {
				return;
			}

			int index = 0;
			while (index < count) {
				Element element = elements.get(index++);
				if (!(element instanceof TextSpan)) {
					line.add(element);
					continue;
				}

				if (index >= count) {
					line.add(element);
					break;
				}

				Element nextElement = elements.get(index);
				if (!(nextElement instanceof TextSpan)) {
					line.add(element);
					continue;
				}

				TextSpan current = (TextSpan) element;
				TextSpan next = (TextSpan) nextElement;
				if (!next.isSameGroup(current)) {
					line.add(element);
					continue;
				}

				TextSpan copy = TextSpan.obtain(current);
				if (!copy.merge(next)) {
					line.add(current);
					copy.recycle();
					continue;
				}

				line.add(copy);

				++index;
				while (index < count) {
					nextElement = elements.get(index);
					if (!(nextElement instanceof TextSpan)) {
						break;
					}

					next = (TextSpan) nextElement;
					if (!next.isSameGroup(current)) {
						break;
					}

					if (!copy.merge(next)) {
						break;
					}
					++index;
				}
			}
		}

		private static void strip(List<Element> elements) {
			int count = elements.size();
			for (int i = count - 1; i >= 0; --i) {
				Element element = elements.get(i);
				if (!(element instanceof Glue)) {
					break;
				}
				elements.remove(i);
			}
		}

		private static void appendIfSuffix(List<Element> elements, Element lastElement) {
			if (!(lastElement instanceof Penalty)) {
				return;
			}

			int size = elements.size();
			Element element = elements.get(size - 1);
			if (element instanceof TextSpan) {
				TextSpan copy = TextSpan.obtain((TextSpan) element);
				copy.merge((Penalty) lastElement);
				elements.set(size - 1, copy);
			}
		}

		private static void measureLine(Line line, BreakStrategy breakStrategy, float lineWidth, float lineHeight) {
			float boxWidth = 0;
			float glueWidth = 0;
			float glueShrink = 0;
			float glueStretch = 0;

			for (int i = 0; i < line.getElementCount(); ++i) {
				Element element = line.getElement(i);
				if (element instanceof Span) {
					Span span = (Span) element;
					boxWidth += span.getWidth();
					if (lineHeight < span.getHeight()) {
						lineHeight = span.getHeight();
					}

					line.mBaselineOffset = Math.max(span.getBaselineOffset(), line.mBaselineOffset);
				} else if (element instanceof Glue) {
					Glue glue = (Glue) element;
					glueWidth += glue.getWidth();
					glueShrink += glue.getShrink();
					glueStretch += glue.getStretch();
				}
			}

			float totalWidth = glueWidth + boxWidth;
			line.setLineHeight(lineHeight);
			if (breakStrategy == BreakStrategy.SIMPLE) {
				line.setLineWidth(totalWidth);
				line.setRatio(0);
				return;
			}

			line.setLineWidth(lineWidth);
			float ratio = 0;
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
		protected void onRecycle() {
			mLine = null;
			mLastTextElement = null;
			mContainTerminal = false;
			mElements.clear();
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