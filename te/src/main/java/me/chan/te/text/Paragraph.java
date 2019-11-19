package me.chan.te.text;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.annotations.Hidden;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;
import me.chan.te.misc.DefaultRecyclable;
import me.chan.te.misc.ObjectFactory;
import me.chan.te.renderer.Clickable;
import me.chan.te.typesetter.ParagraphTypesetter;

/**
 * 段落
 */
public class Paragraph extends Segment {
	private static final ObjectFactory<Paragraph> POOL = new ObjectFactory<>(4096);

	private List<Line> mLines = new ArrayList<>(32);
	private List<Element> mElements = new ArrayList<>(512);
	private Object mExtra;
	private boolean mEmpty;

	public Paragraph(Object extra) {
		mExtra = extra;
	}

	public void setExtra(Object extra) {
		mExtra = extra;
	}

	@Nullable
	public Object getExtra() {
		return mExtra;
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
		Paragraph page = Paragraph.obtain(mExtra);
		page.mLines = list.subList(endIndex, list.size());
		page.mEmpty = mEmpty;
		// FIXME 可能内容不对称
		page.mElements = mElements;
		return page;
	}

	@Hidden
	public void addLine(Line line) {
		mLines.add(line);
	}

	public boolean isEmpty() {
		return mEmpty;
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

		mExtra = null;
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

	private static Paragraph obtain(Object extra) {
		Paragraph paragraph = POOL.acquire();
		if (paragraph == null) {
			return new Paragraph(extra);
		}
		paragraph.reuse();
		paragraph.mExtra = extra;
		return paragraph;
	}

	/**
	 * 需要避免多次创建
	 */
	public static class Builder extends DefaultRecyclable {
		private static final ObjectFactory<Builder> POOL = new ObjectFactory<>(8);

		private List<Integer> mHyphenated = new ArrayList<>(10);
		private Measurer mMeasurer;
		private Hypher mHypher;
		private TextAttribute mTextAttribute;
		private Paragraph mParagraph;
		private boolean mEmpty = true;
		private RichTextBuilder mRichTextBuilder = new RichTextBuilder(this);

		private Builder() {
		}

		public Builder text(CharSequence text, int start, int end) {
			RichTextBuilder richTextBuilder = richText(text, start, end);
			return richTextBuilder.build();
		}

		public RichTextBuilder richText(CharSequence text, int start, int end) {
			mRichTextBuilder.reset(text, start, end);
			return mRichTextBuilder;
		}

		public RichTextSpanBuilder richTextSpan(Clickable.OnClickedListener onClickedListener) {

		}

		public Builder drawable(Drawable drawable, float width, float height) {
			return drawable(drawable, width, height, null);
		}

		public Builder drawable(Drawable drawable, float width, float height, Clickable.OnClickedListener onClickedListener) {
			if (mParagraph == null) {
				throw new IllegalStateException("call newParagraph first");
			}

			List<Element> elements = mParagraph.mElements;
			elements.add(DrawableBox.obtain(drawable, width, height, onClickedListener));
			elements.add(Glue.obtain(mTextAttribute.getSpaceWidth(), mTextAttribute.getSpaceStretch(), mTextAttribute.getSpaceShrink()));
			mEmpty = false;
			return this;
		}

		public Paragraph build() {
			if (isRecycled()) {
				throw new IllegalStateException("call build twice");
			}

			int elementSize = mParagraph.mElements.size();
			if (elementSize != 0 && mParagraph.mElements.get(elementSize - 1) instanceof Glue) {
				mParagraph.mElements.remove(elementSize - 1);
			}

			mParagraph.mElements.add(Glue.obtain(0, ParagraphTypesetter.INFINITY, 0));
			mParagraph.mElements.add(Penalty.obtain(0, 0, -ParagraphTypesetter.INFINITY, true));
			mParagraph.mEmpty = mEmpty;
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
			mEmpty = true;
			mMeasurer = null;
			mTextAttribute = null;
			mHypher = null;
			mHyphenated.clear();
			mRichTextBuilder.mText = null;
			mRichTextBuilder.mStart = mRichTextBuilder.mEnd = 0;
			mRichTextBuilder.mBuilder = null;
			mRichTextBuilder.mTextStyle = null;
			mRichTextBuilder.mBackground = null;
			mRichTextBuilder.mForeground = null;
			mRichTextBuilder.mExtra = null;
			mRichTextBuilder.mOnClickedListener = null;
			POOL.release(this);
		}

		public static Builder newBuilder(Measurer measurer, Hypher hypher, TextAttribute textAttribute, Object extra) {
			Builder builder = POOL.acquire();
			if (builder == null) {
				builder = new Builder();
			}

			builder.mMeasurer = measurer;
			builder.mHypher = hypher;
			builder.mTextAttribute = textAttribute;
			builder.mParagraph = obtain(extra);
			builder.reuse();
			return builder;
		}

		public static void clean() {
			POOL.clean();
		}
	}

	public static class RichTextBuilder {
		private static final int MIN_HYPER_LEN = 4;

		private CharSequence mText;
		private int mStart;
		private int mEnd;
		private Builder mBuilder;
		private TextStyle mTextStyle;
		private Background mBackground;
		private Foreground mForeground;
		private Object mExtra;
		private Clickable.OnClickedListener mOnClickedListener;

		RichTextBuilder(Builder builder) {
			mBuilder = builder;
		}

		private void reset(@NonNull CharSequence text, int start, int end) {
			mText = text;
			mStart = start;
			mEnd = end;
		}

		public RichTextBuilder setTextStyle(TextStyle textStyle) {
			mTextStyle = textStyle;
			return this;
		}

		public RichTextBuilder setBackground(Background background) {
			mBackground = background;
			return this;
		}

		public RichTextBuilder setForeground(Foreground foreground) {
			mForeground = foreground;
			return this;
		}

		public RichTextBuilder setExtra(Object extra) {
			mExtra = extra;
			return this;
		}

		public RichTextBuilder setOnClickedListener(Clickable.OnClickedListener onClickedListener) {
			mOnClickedListener = onClickedListener;
			return this;
		}

		public Builder build() {
			if (mText == null) {
				throw new RuntimeException("call build twice");
			}

			if (mBuilder.mParagraph == null) {
				throw new IllegalStateException("call newParagraph first");
			}

			int len = mEnd - mStart;
			List<Element> elements = mBuilder.mParagraph.mElements;
			mBuilder.mHypher.hyphenate(mText, mStart, mEnd, mBuilder.mHyphenated);
			int size = mBuilder.mHyphenated.size();
			if (size == 0 || len < MIN_HYPER_LEN) {
				elements.add(TextBox.obtain(mText, mStart, mEnd,
						mBuilder.mMeasurer.getDesiredWidth(mText, mStart, mEnd, mTextStyle),
						mBuilder.mMeasurer.getDesiredHeight(mText, mStart, mEnd, mTextStyle),
						mTextStyle,
						mBackground,
						mForeground,
						mExtra,
						mOnClickedListener
				));
			} else {
				for (int j = 0; j < size; ++j) {
					int point = mBuilder.mHyphenated.get(j);
					if (point == mStart) {
						continue;
					}

					elements.add(TextBox.obtain(mText, mStart, point,
							mBuilder.mMeasurer.getDesiredWidth(mText, mStart, point, mTextStyle),
							mBuilder.mMeasurer.getDesiredHeight(mText, mStart, point, mTextStyle),
							mTextStyle,
							mBackground,
							mForeground,
							mExtra,
							mOnClickedListener
					));
					if (j != size - 1) {
						char ch = mText.charAt(point - 1);
						boolean isExplicitHyphen = ch == '-';
						elements.add(Penalty.obtain(
								isExplicitHyphen ? 0 : mBuilder.mTextAttribute.getHyphenWidth(),
								mBuilder.mTextAttribute.getHyphenHeight(),
								ParagraphTypesetter.HYPHEN_PENALTY,
								true));
					}
					mStart = point;
				}
			}
			mBuilder.mHyphenated.clear();
			elements.add(
					Glue.obtain(
							mBuilder.mTextAttribute.getSpaceWidth(),
							mBuilder.mTextAttribute.getSpaceStretch(),
							mBuilder.mTextAttribute.getSpaceShrink()
					)
			);
			mBuilder.mEmpty = false;
			return mBuilder;
		}
	}

	public static class RichTextSpanBuilder {
		private Builder mBuilder;
		private Clickable.OnClickedListener mOnClickedListener;

		RichTextSpanBuilder(Builder builder) {
			mBuilder = builder;
		}

		private void reset(Clickable.OnClickedListener onClickedListener) {
			mOnClickedListener = onClickedListener;
		}

		public RichTextBuilder next(CharSequence charSequence, int start, int end) {
			return mBuilder.richText(charSequence, start, end);
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

		private List<Box> mBoxes = new ArrayList<>(150);
		private float mLineHeight;
		private float mLineWidth;
		private float mRatio;
		private float mSpaceWidth;
		private Gravity mGravity = Gravity.LEFT;

		private Line() {
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
