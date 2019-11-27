package me.chan.te.text;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.Te;
import me.chan.te.annotations.Hidden;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;
import me.chan.te.misc.DefaultRecyclable;
import me.chan.te.misc.ObjectFactory;
import me.chan.te.typesetter.ParagraphTypesetter;

/**
 * 段落
 */
public class Paragraph extends Segment {
	private static final ObjectFactory<Paragraph> POOL = new ObjectFactory<>(4096);

	private List<Line> mLines;
	private List<Element> mElements;

	public Paragraph() {
		Te.MemoryOption memoryOption = Te.getMemoryOption();
		mLines = new ArrayList<>(memoryOption.getParagraphLineInitialCapacity());
		mElements = new ArrayList<>(memoryOption.getParagraphElementInitialCapacity());
	}

	public Line getLine(int index) {
		return mLines.get(index);
	}

	public int getLineCount() {
		return mLines.size();
	}

	public Paragraph spilt(int endIndex) {
		List<Line> list = mLines;
		mLines = list.subList(0, endIndex);
		Paragraph page = Paragraph.obtain();
		page.mLines = list.subList(endIndex, list.size());
		// 不拷贝mElements 复用的时候会出问题
		return page;
	}

	@Hidden
	public void addLine(Line line) {
		mLines.add(line);
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		for (int i = 0; i < mLines.size(); ++i) {
			mLines.get(i).recycle();
		}
		mLines.clear();

		for (int i = 0; i < mElements.size(); ++i) {
			mElements.get(i).recycle();
		}
		mElements.clear();
		POOL.release(this);
	}

	@Hidden
	public int getElementCount() {
		return mElements.size();
	}

	@Hidden
	public Element getElement(int index) {
		return mElements.get(index);
	}

	@Hidden
	public void replace(int index, Element element) {
		mElements.set(index, element);
	}

	public static void clean() {
		POOL.clean();
	}

	private static Paragraph obtain() {
		Paragraph paragraph = POOL.acquire();
		if (paragraph == null) {
			return new Paragraph();
		}
		paragraph.reuse();
		return paragraph;
	}

	/**
	 * 需要避免多次创建
	 */
	public static class Builder extends DefaultRecyclable {
		private static final ObjectFactory<Builder> POOL = new ObjectFactory<>(8);
		private static final int MIN_HYPER_LEN = 4;

		private List<Integer> mHyphenated = new ArrayList<>(10);
		private Measurer mMeasurer;
		private Hypher mHypher;
		private TextAttribute mTextAttribute;
		private Paragraph mParagraph;
		private SpanBuilder mSpanBuilder = new SpanBuilder(this);

		private Builder() {
		}

		public Builder text(CharSequence text) {
			return text(text, 0, text.length());
		}

		public Builder text(CharSequence text, int start, int end) {
			text(text, start, end, null, null, null);
			return this;
		}

		public SpanBuilder newSpanBuilder(OnClickedListener listener) {
			mSpanBuilder.reset(listener);
			return mSpanBuilder;
		}

		private void text(CharSequence text, int start, int end,
						  OnClickedListener onClickedListener, TextBox.Attribute attribute,
						  TextStyle textStyle) {
			if (text == null) {
				throw new RuntimeException("call build twice");
			}

			if (mParagraph == null) {
				throw new IllegalStateException("call newParagraph first");
			}

			int len = end - start;
			List<Element> elements = mParagraph.mElements;
			mHypher.hyphenate(text, start, end, mHyphenated);
			int size = mHyphenated.size();
			if (size == 0 || len < MIN_HYPER_LEN) {
				elements.add(TextBox.obtain(text, start, end,
						mMeasurer.getDesiredWidth(text, start, end, textStyle),
						mMeasurer.getDesiredHeight(text, start, end, textStyle),
						onClickedListener,
						attribute
				));
			} else {
				for (int j = 0; j < size; ++j) {
					int point = mHyphenated.get(j);
					if (point == start) {
						continue;
					}

					elements.add(TextBox.obtain(text, start, point,
							mMeasurer.getDesiredWidth(text, start, point, textStyle),
							mMeasurer.getDesiredHeight(text, start, point, textStyle),
							onClickedListener,
							attribute
					));
					if (j != size - 1) {
						char ch = text.charAt(point - 1);
						boolean isExplicitHyphen = ch == '-';
						elements.add(Penalty.obtain(
								isExplicitHyphen ? 0 : mTextAttribute.getHyphenWidth(),
								mTextAttribute.getHyphenHeight(),
								ParagraphTypesetter.HYPHEN_PENALTY,
								true));
					}
					start = point;
				}
			}
			mHyphenated.clear();
			elements.add(
					Glue.obtain(
							mTextAttribute.getSpaceWidth(),
							mTextAttribute.getSpaceStretch(),
							mTextAttribute.getSpaceShrink()
					)
			);
		}

		public Builder drawable(Drawable drawable, float width, float height) {
			return drawable(drawable, width, height, null);
		}

		public Builder drawable(Drawable drawable, float width, float height, OnClickedListener onClickedListener) {
			if (mParagraph == null) {
				throw new IllegalStateException("call newParagraph first");
			}

			List<Element> elements = mParagraph.mElements;
			elements.add(DrawableBox.obtain(drawable, width, height, onClickedListener));
			elements.add(Glue.obtain(mTextAttribute.getSpaceWidth(), mTextAttribute.getSpaceStretch(), mTextAttribute.getSpaceShrink()));
			return this;
		}

		public Paragraph build() {
			if (isRecycled()) {
				throw new IllegalStateException("call build twice");
			}

			int elementSize = mParagraph.mElements.size();
			if (elementSize != 0) {
				if (mParagraph.mElements.get(elementSize - 1) instanceof Glue) {
					mParagraph.mElements.remove(elementSize - 1);
				}

				mParagraph.mElements.add(Glue.obtain(0, ParagraphTypesetter.INFINITY, 0));
				mParagraph.mElements.add(Penalty.obtain(0, 0, -ParagraphTypesetter.INFINITY, true));
			}

			Paragraph paragraph = mParagraph;
			recycle();
			return paragraph;
		}

		@Override
		public void recycle() {
			if (isRecycled()) {
				return;
			}

			super.recycle();
			mParagraph = null;
			mMeasurer = null;
			mTextAttribute = null;
			mHypher = null;
			mHyphenated.clear();
			mSpanBuilder.reset(null);
			POOL.release(this);
		}

		public static Builder newBuilder(Measurer measurer, Hypher hypher, TextAttribute textAttribute) {
			Builder builder = POOL.acquire();
			if (builder == null) {
				builder = new Builder();
			}

			builder.mMeasurer = measurer;
			builder.mHypher = hypher;
			builder.mTextAttribute = textAttribute;
			builder.mParagraph = obtain();
			builder.reuse();
			return builder;
		}

		public static void clean() {
			POOL.clean();
		}
	}

	public static class SpanBuilder {
		private CharSequence mText;
		private int mStart;
		private int mEnd;
		private Builder mBuilder;
		private TextStyle mTextStyle;
		private Background mBackground;
		private Foreground mForeground;
		private Object mExtra;
		private OnClickedListener mOnClickedListener;
		private OnClickedListener mSpanOnClickedListener;

		SpanBuilder(Builder builder) {
			mBuilder = builder;
		}

		private void reset(OnClickedListener onClickedListener) {
			mText = null;
			mStart = mEnd = 0;
			mTextStyle = null;
			mBackground = null;
			mForeground = null;
			mExtra = null;
			mOnClickedListener = null;
			mSpanOnClickedListener = onClickedListener;
		}

		public SpanBuilder next(CharSequence text) {
			return next(text, 0, text.length());
		}

		public SpanBuilder next(CharSequence text, int start, int end) {
			flush();
			mText = text;
			mStart = start;
			mEnd = end;
			return this;
		}

		public SpanBuilder setTextStyle(TextStyle textStyle) {
			mTextStyle = textStyle;
			return this;
		}

		public SpanBuilder setBackground(Background background) {
			mBackground = background;
			return this;
		}

		public SpanBuilder setForeground(Foreground foreground) {
			mForeground = foreground;
			return this;
		}

		public SpanBuilder setOnClickedListener(OnClickedListener onClickedListener) {
			mOnClickedListener = onClickedListener;
			return this;
		}

		private void flush() {
			if (mText == null) {
				return;
			}

			TextBox.Attribute attribute = TextBox.Attribute.obtain();
			attribute.setBackground(mBackground);
			attribute.setForeground(mForeground);
			attribute.setTextStyle(mTextStyle);
			attribute.setSpanOnClickedListener(mSpanOnClickedListener);
			mBuilder.text(mText, mStart, mEnd, mOnClickedListener, attribute, mTextStyle);

			// reset
			reset(mSpanOnClickedListener);
		}

		public Builder buildSpan() {
			flush();
			return mBuilder;
		}
	}

	/**
	 * 排版算法中基本元素的接口
	 */
	public static class Element extends DefaultRecyclable {
	}

	/**
	 * 绘制行
	 */
	public static class Line extends DefaultRecyclable {
		private static final ObjectFactory<Line> POOL = new ObjectFactory<>(4096);

		private List<Box> mBoxes;
		private float mLineHeight;
		private float mLineWidth;
		private float mRatio;
		private float mSpaceWidth;
		private Gravity mGravity = Gravity.LEFT;

		private Line() {
			Te.MemoryOption memoryOption = Te.getMemoryOption();
			mBoxes = new ArrayList<>(memoryOption.getParagraphLineBoxInitialCapacity());
			reset();
		}

		private void reset() {
			mBoxes.clear();
			mLineHeight = -1;
			mLineWidth = -1;
			mRatio = -1;
			mSpaceWidth = -1;
			mGravity = Gravity.LEFT;
		}

		public Gravity getGravity() {
			return mGravity;
		}

		public float getLineHeight() {
			return mLineHeight;
		}

		public float getRatio() {
			return mRatio;
		}

		public float getSpaceWidth() {
			return mSpaceWidth;
		}

		public void setSpaceWidth(float spaceWidth) {
			mSpaceWidth = spaceWidth;
		}

		public void setLineHeight(float lineHeight) {
			mLineHeight = lineHeight;
		}

		public void setLineWidth(float lineWidth) {
			mLineWidth = lineWidth;
		}

		public void setRatio(float ratio) {
			mRatio = ratio;
		}

		public void setGravity(Gravity gravity) {
			mGravity = gravity;
		}

		@Override
		public void recycle() {
			if (isRecycled()) {
				return;
			}

			super.recycle();
			reset();
			POOL.release(this);
		}

		public int getCount() {
			return mBoxes.size();
		}

		public void add(Box box) {
			mBoxes.add(box);
		}

		public boolean isEmpty() {
			return mBoxes.isEmpty();
		}

		public float getLineWidth() {
			return mLineWidth;
		}

		public Box getBox(int index) {
			return mBoxes.get(index);
		}

		public static void clean() {
			POOL.clean();
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
	}
}
