package com.shanbay.lib.texas.text;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import com.shanbay.lib.texas.Texas;
import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.hypher.Hypher;
import com.shanbay.lib.texas.measurer.Measurer;
import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.misc.ObjectFactory;
import com.shanbay.lib.texas.typesetter.ParagraphTypesetter;

/**
 * 段落
 */
public class Paragraph extends Segment {
	private static final ObjectFactory<Paragraph> POOL = new ObjectFactory<>(4096);

	private List<Line> mLines;
	private List<Element> mElements;
	private Object mExtra;

	public Paragraph() {
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mLines = new ArrayList<>(memoryOption.getParagraphLineInitialCapacity());
		mElements = new ArrayList<>(memoryOption.getParagraphElementInitialCapacity());
	}

	@Hidden
	public Line getLine(int index) {
		return mLines.get(index);
	}

	@Hidden
	public int getLineCount() {
		return mLines.size();
	}

	@Hidden
	public void addLine(Line line) {
		mLines.add(line);
	}

	/**
	 * @return 获取paragraph上附加的信息
	 */
	public Object getExtra() {
		return mExtra;
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

	private static Paragraph obtain() {
		Paragraph paragraph = POOL.acquire();
		if (paragraph == null) {
			return new Paragraph();
		}
		paragraph.reuse();
		return paragraph;
	}

	/**
	 * 构造器，注意要尽量避免重复创建
	 */
	public static class Builder extends DefaultRecyclable {
		private static final ObjectFactory<Builder> POOL = new ObjectFactory<>(8);
		private static final int MIN_HYPER_LEN = 4;

		private List<Integer> mHyphenated = new ArrayList<>(10);
		private Measurer mMeasurer;
		private Hypher mHypher;
		private TextAttribute mTextAttribute;
		private Paragraph mParagraph;
		private Object mExtra;
		private SpanBuilder mSpanBuilder = new SpanBuilder(this);

		private Builder() {
		}

		/**
		 * @param extra 设置paragraph的额外信息
		 * @return 当前对象
		 */
		public Builder extra(Object extra) {
			mExtra = extra;
			return this;
		}

		/**
		 * @param text 文本
		 * @return 当前对象
		 */
		public Builder text(CharSequence text) {
			return text(text, 0, text.length());
		}

		/**
		 * @param text  文本
		 * @param start 开始下标
		 * @param end   结束下标
		 * @return 当前对象
		 */
		public Builder text(CharSequence text, int start, int end) {
			text(text, start, end, null, null, null);
			return this;
		}

		/**
		 * 创建一个span，span是一段富文本内容，内部有多段组成
		 * 每个组成部分可以设置文本样式，以及相应点击事件
		 * 另外一个span可以响应长按事件 {@link Builder#newSpanBuilder(OnClickedListener)}
		 *
		 * @return 当前对象
		 */
		public SpanBuilder newSpanBuilder() {
			return newSpanBuilder();
		}

		/**
		 * 创建一个span，span是一段富文本内容，内部有多段组成
		 * 每个组成部分可以设置文本样式，以及相应点击事件
		 * 另外一个span可以响应长按事件
		 *
		 * @param listener 创建一个span
		 * @return 当前对象
		 */
		public SpanBuilder newSpanBuilder(OnClickedListener listener) {
			mSpanBuilder.reset(listener);
			return mSpanBuilder;
		}

		/**
		 * 添加文字
		 *
		 * @param text              文字
		 * @param start             文字开始位置
		 * @param end               文字结束位置
		 * @param onClickedListener 文字的点击操作
		 * @param attribute         文字的属性
		 * @param textStyle         文字的style
		 */
		private void text(CharSequence text, int start, int end,
						  OnClickedListener onClickedListener,
						  TextBoxAttribute attribute,
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

		/**
		 * 添加drawable，如果给定宽高小于0，那么行为将是未定义的
		 *
		 * @param drawable drawable
		 * @param width    drawable宽度
		 * @param height   drawable高度
		 * @return 当前对象
		 */
		public Builder drawable(Drawable drawable, float width, float height) {
			return drawable(drawable, width, height, null);
		}

		/**
		 * 添加drawable，如果给定宽高小于0，那么行为将是未定义的
		 *
		 * @param drawable          drawable
		 * @param width             drawable宽度
		 * @param height            drawable高度
		 * @param onClickedListener drawable点击后的操作
		 * @return 当前对象
		 */
		public Builder drawable(Drawable drawable, float width, float height, OnClickedListener onClickedListener) {
			if (mParagraph == null) {
				throw new IllegalStateException("call newParagraph first");
			}

			List<Element> elements = mParagraph.mElements;
			elements.add(DrawableBox.obtain(drawable, width, height, onClickedListener));
			elements.add(Glue.obtain(mTextAttribute.getSpaceWidth(), mTextAttribute.getSpaceStretch(), mTextAttribute.getSpaceShrink()));
			return this;
		}

		/**
		 * 构造
		 *
		 * @return paragraph
		 */
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

			mParagraph.mExtra = mExtra;
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
			mExtra = null;
			mSpanBuilder.reset(null);
			POOL.release(this);
		}

		/**
		 * @param measurer      测量器
		 * @param hypher        断字器
		 * @param textAttribute 文本属性
		 * @return 当前对象
		 */
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

		@Hidden
		public static void clean() {
			POOL.clean();
		}
	}

	/**
	 * span构造器
	 */
	public static class SpanBuilder {
		private CharSequence mText;
		private int mStart;
		private int mEnd;
		private Builder mBuilder;
		private TextStyle mTextStyle;
		private Appearance mBackground;
		private Appearance mForeground;
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
			mOnClickedListener = null;
			mSpanOnClickedListener = onClickedListener;
		}

		/**
		 * 往当前span中添加一段文字
		 *
		 * @param text 文字
		 * @return 当前对象
		 */
		public SpanBuilder next(CharSequence text) {
			return next(text, 0, text.length());
		}

		/**
		 * 往当前span中添加一段文字
		 *
		 * @param text  文字
		 * @param start 文字的开始
		 * @param end   文字的结束
		 * @return 当前对象
		 */
		public SpanBuilder next(CharSequence text, int start, int end) {
			flush();
			mText = text;
			mStart = start;
			mEnd = end;
			return this;
		}

		/**
		 * 设置当前文字样式
		 * <p/>
		 * {@link TextStyle#BOLD}
		 * {@link TextStyle#BOLD_ITALIC}
		 * etc
		 *
		 * @param textStyle 文字属性
		 * @return 当前对象
		 */
		public SpanBuilder setTextStyle(TextStyle textStyle) {
			mTextStyle = textStyle;
			return this;
		}

		/**
		 * 设置当前文字背景
		 * <p/>
		 * {@link ColorGround}
		 *
		 * @param background 文字背景
		 * @return 当前对象
		 */
		public SpanBuilder setBackground(Appearance background) {
			mBackground = background;
			return this;
		}

		/**
		 * 设置当前文字前景
		 * <p/>
		 * {@link UnderLine}
		 *
		 * @param foreground 前景
		 * @return 当前对象
		 */
		public SpanBuilder setForeground(Appearance foreground) {
			mForeground = foreground;
			return this;
		}

		/**
		 * 设置span的点击事件，目前由长按触发
		 *
		 * @param onClickedListener
		 * @return 当前对象
		 */
		public SpanBuilder setOnClickedListener(OnClickedListener onClickedListener) {
			mOnClickedListener = onClickedListener;
			return this;
		}

		private void flush() {
			if (mText == null) {
				return;
			}

			TextBoxAttribute attribute = TextBoxAttribute.obtain();
			attribute.setBackground(mBackground);
			attribute.setForeground(mForeground);
			attribute.setTextStyle(mTextStyle);
			attribute.setSpanOnClickedListener(mSpanOnClickedListener);
			mBuilder.text(mText, mStart, mEnd, mOnClickedListener, attribute, mTextStyle);

			// reset
			reset(mSpanOnClickedListener);
		}

		/**
		 * 创建新的span
		 *
		 * @return span
		 */
		public Builder buildSpan() {
			flush();
			return mBuilder;
		}
	}
}
