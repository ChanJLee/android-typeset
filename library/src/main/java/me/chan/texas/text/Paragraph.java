package me.chan.texas.text;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Rect;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.highlight.ParagraphHighlight;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Layout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 段落
 */
public final class Paragraph extends DefaultRecyclable implements Segment {
	private static final ObjectPool<Paragraph> POOL = new ObjectPool<>(Texas.getMemoryOption().getParagraphBufferSize());

	@NonNull
	@RestrictTo(LIBRARY)
	volatile Layout mLayout;

	@RestrictTo(LIBRARY)
	final List<Element> mElements;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	Object mTag;
	/**
	 * 默认
	 */
	public static final int TYPESET_POLICY_DEFAULT = 0;
	/**
	 * CJK混合优化，会使得cjk文字和英文渲染的时候，显得字体更小一点
	 */
	public static final int TYPESET_POLICY_CJK_MIX_OPTIMIZATION = 1;
	/**
	 * 双向文本，非常耗时，不建议使用
	 */
	public static final int TYPESET_POLICY_BIDI_TEXT = 2;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({TYPESET_POLICY_DEFAULT, TYPESET_POLICY_CJK_MIX_OPTIMIZATION, TYPESET_POLICY_BIDI_TEXT})
	public @interface TypesetPolicy {
	}

	int mId;

	@Nullable
	@Override
	public Object getTag() {
		return mTag;
	}

	@Override
	public void getRect(@NonNull Rect rect) {
		mLayout.getRect(rect);
	}

	@Nullable
	@Override
	public Rect getRect() {
		return mLayout.getRect();
	}

	@Override
	public void setRect(Rect rect) {
		mLayout.setRect(rect);
	}

	private ParagraphHighlight mHighlight;

	private ParagraphSelection mSelection;

	private ParagraphDecor mDecor;

	@RestrictTo(LIBRARY)
	@Nullable
	public ParagraphHighlight getHighlight() {
		return mHighlight;
	}

	@RestrictTo(LIBRARY)
	public void setHighlight(ParagraphHighlight highlight) {
		mHighlight = highlight;
	}

	@RestrictTo(LIBRARY)
	@Nullable
	public ParagraphSelection getSelection() {
		return mSelection;
	}

	@RestrictTo(LIBRARY)
	public void setSelection(ParagraphSelection selection) {
		mSelection = selection;
	}

	private Paragraph(Object tag) {
		mTag = tag;
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mElements = new ArrayList<>(memoryOption.getParagraphElementInitialCapacity());
	}

	@RestrictTo(LIBRARY)
	public synchronized Layout swap(@NonNull Layout layout) {
		layout.finishLayout();
		Layout old = mLayout;
		mLayout = layout;
		return old;
	}

	@Override
	protected void onRecycle() {
		mId = 0;
		mLayout.clear();
		for (int i = 0; i < mElements.size(); ++i) {
			mElements.get(i).recycle();
		}
		mElements.clear();
		mTag = null;
		if (mHighlight != null) {
			mHighlight.recycle();
			mHighlight = null;
		}
		if (mSelection != null) {
			mSelection.recycle();
			mSelection = null;
		}
		POOL.release(this);
	}

	@Override
	public int getId() {
		return mId;
	}

	public boolean hasContent() {
		return !mElements.isEmpty();
	}

	@RestrictTo(LIBRARY)
	public int getElementCount() {
		return mElements.size();
	}

	@RestrictTo(LIBRARY)
	public Element getElement(int index) {
		return mElements.get(index);
	}

	@RestrictTo(LIBRARY)
	public static void clean() {
		POOL.clean();
	}

	@RestrictTo(LIBRARY)
	public Layout getLayout() {
		return mLayout;
	}

	@RestrictTo(LIBRARY)
	static Paragraph obtain() {
		Paragraph paragraph = POOL.acquire();
		if (paragraph == null) {
			paragraph = new Paragraph(null);
		}
		paragraph.reuse();
		return paragraph;
	}

	/**
	 * 构造器，注意要尽量避免重复创建
	 */
	public static class Builder extends DefaultRecyclable {
		private static final ObjectPool<Builder> POOL = new ObjectPool<>(8);

		// real builder
		private final ParagraphBuilderInternal mBuilder0;

		private Builder() {
			mBuilder0 = new ParagraphBuilderInternal(this);
		}

		public Builder lineSpace(float lineSpace) {
			mBuilder0.lineSpace(lineSpace);
			return this;
		}

		public Builder breakStrategy(BreakStrategy breakStrategy) {
			mBuilder0.breakStrategy(breakStrategy);
			return this;
		}

		/**
		 * @param tag 设置paragraph的额外信息，用来标识这个paragraph，因此需要保持唯一
		 * @return 当前对象
		 */
		public Builder tag(Object tag) {
			mBuilder0.tag(tag);
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
			mBuilder0.text(text, start, end);
			return this;
		}

		/**
		 * 创建一个span，span是一段富文本内容，内部有多段组成
		 * 每个组成部分可以设置文本样式，以及相应点击事件
		 * 另外一个span可以响应长按事件
		 *
		 * @return 当前对象
		 */
		public SpanBuilder newSpanBuilder() {
			return mBuilder0.newSpanBuilder();
		}

		/**
		 * 以stream流的模式设置文本
		 * 以文本 "Hi, World..." 为例
		 * 词法引擎回去解析这段文本，并解析成
		 * [Hi, 0-2]
		 * [,, 2-3]
		 * [World, 4-9]
		 * [..., 9-12]
		 * 的单词流，客户端根据下标 去确定当前的单词流样式
		 * <p>
		 * 该方法保证 下标间永不重叠，并且递增
		 * 下标的规则是 左闭右开即 [0, 3) 包含 0, 1, 2这三个下标
		 *
		 * @param text       text
		 * @param start      开始位置
		 * @param end        起始位置
		 * @param spanReader span 读取
		 * @return 当前对象
		 */
		public Builder stream(CharSequence text, int start, int end, SpanReader spanReader) {
			mBuilder0.stream(text, start, end, spanReader);
			return this;
		}

		public interface SpanReader {
			Span read(CharSequence text, int start, int end);
		}

		/**
		 * 颜文字
		 *
		 * @param emoticon 颜文字
		 * @return 当前对象
		 */
		public Builder emoticon(Emoticon emoticon) {
			mBuilder0.emoticon(emoticon);
			return this;
		}

		/**
		 * 强行断行
		 *
		 * @return 当前对象
		 */
		public Builder brk() {
			mBuilder0.brk();
			return this;
		}

		public Builder addTypesetPolicy(@TypesetPolicy int policy) {
			mBuilder0.addTypesetPolicy(policy);
			return this;
		}

		public Builder clearTypesetPolicy() {
			mBuilder0.clearTypesetPolicy();
			return this;
		}

		public Builder setTypesetPolicy(@TypesetPolicy int policy) {
			clearTypesetPolicy();
			addTypesetPolicy(policy);
			return this;
		}

		/**
		 * 构造一个paragraph
		 * <p/>
		 * Tips: 该方法会在段落默认添加一个换行，如果不需要，请参考 {@link #build(boolean)}
		 *
		 * @return paragraph
		 */
		public Paragraph build() {
			return build(true);
		}

		/**
		 * 构造一个paragraph
		 *
		 * @param brk 是否追加一个换行
		 * @return paragraph
		 */
		public Paragraph build(boolean brk) {
			if (isRecycled()) {
				throw new IllegalStateException("call build twice");
			}

			Paragraph paragraph = mBuilder0.build(brk);
			recycle();
			return paragraph;
		}

		@Override
		protected void onRecycle() {
			mBuilder0.reset();
			POOL.release(this);
		}

		/**
		 * use {@link #newBuilder(TexasOption)} & {@link #addTypesetPolicy(int)} instead
		 * <p>
		 * more {@link #clearTypesetPolicy()}
		 * </p>
		 *
		 * @param texasOption texas option
		 * @return 当前对象
		 */
		@Deprecated
		public static Builder newBuilder(TexasOption texasOption,
										 @TypesetPolicy int typesetPolicy) {
			return newBuilder(texasOption)
					.setTypesetPolicy(typesetPolicy);
		}

		/**
		 * @param texasOption texas option
		 * @return 当前对象
		 */
		public static Builder newBuilder(TexasOption texasOption) {
			Builder builder = POOL.acquire();
			if (builder == null) {
				builder = new Builder();
			}

			builder.mBuilder0.reset(texasOption);
			builder.reuse();
			return builder;
		}

		public static void clean() {
			POOL.clean();
		}
	}

	/**
	 * span构造器
	 */
	public static class SpanBuilder implements Builder.SpanReader {
		private final Builder mBuilder;
		private Span mSpan;

		SpanBuilder(Builder builder) {
			mBuilder = builder;
		}

		@RestrictTo(LIBRARY)
		void reset() {
			if (mSpan != null) {
				mSpan.recycle();
				mSpan = null;
			}
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
			mSpan = Span.obtain(text, start, end);
			return this;
		}

		/**
		 * 设置span tag
		 *
		 * @param tag 用来标识这个span，因此需要保持唯一
		 * @return 当前对象
		 */
		public SpanBuilder tag(Object tag) {
			mSpan.mTag = tag;
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
			mSpan.mTextStyle = textStyle;
			return this;
		}

		/**
		 * 设置当前文字背景
		 * <p/>
		 * {@link RectGround}
		 *
		 * @param background 文字背景
		 * @return 当前对象
		 */
		public SpanBuilder setBackground(Appearance background) {
			mSpan.mBackground = background;
			return this;
		}

		/**
		 * 设置当前文字前景
		 * <p/>
		 * {@link DotUnderLine}
		 *
		 * @param foreground 前景
		 * @return 当前对象
		 */
		public SpanBuilder setForeground(Appearance foreground) {
			mSpan.mForeground = foreground;
			return this;
		}


		private void flush() {
			if (mSpan == null || mSpan.isRecycled()) {
				return;
			}

			mBuilder.mBuilder0.stream(mSpan.mText, mSpan.mStart, mSpan.mEnd, this);

			// reset
			reset();
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

		@Override
		@RestrictTo(LIBRARY)
		public final Span read(CharSequence text, int start, int end) {
			Span span = Span.obtain(mSpan.mText, mSpan.mStart, mSpan.mEnd);
			span.copy(mSpan);
			return span;
		}
	}

	/**
	 * 文本的样式
	 */
	public static class Span extends DefaultRecyclable {
		private static final ObjectPool<Span> POOL = new ObjectPool<>(32);

		private CharSequence mText;
		private int mStart;
		private int mEnd;

		@RestrictTo(LIBRARY)
		TextStyle mTextStyle;
		@RestrictTo(LIBRARY)
		Appearance mBackground;
		@RestrictTo(LIBRARY)
		Appearance mForeground;
		@RestrictTo(LIBRARY)
		Object mTag;

		private Span() {
		}

		public void copy(Span other) {
			this.mText = other.mText;
			this.mStart = other.mStart;
			this.mEnd = other.mEnd;
			this.mTextStyle = other.mTextStyle;
			this.mBackground = other.mBackground;
			this.mForeground = other.mForeground;
			this.mTag = other.mTag;
		}

		@Override
		protected void onRecycle() {
			mText = null;
			mStart = mEnd = 0;
			mTextStyle = null;
			mBackground = null;
			mForeground = null;
			mTag = null;
			POOL.release(this);
		}

		@VisibleForTesting
		public static void clean() {
			POOL.clean();
		}

		/**
		 * 设置span tag
		 *
		 * @param tag 用来标识这个span，因此需要保持唯一
		 * @return 当前对象
		 */
		public Span tag(Object tag) {
			mTag = tag;
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
		public Span setTextStyle(TextStyle textStyle) {
			mTextStyle = textStyle;
			return this;
		}

		/**
		 * 设置当前文字背景
		 * <p/>
		 * {@link RectGround}
		 *
		 * @param background 文字背景
		 * @return 当前对象
		 */
		public Span setBackground(Appearance background) {
			mBackground = background;
			return this;
		}

		/**
		 * 设置当前文字前景
		 * <p/>
		 * {@link DotUnderLine}
		 *
		 * @param foreground 前景
		 * @return 当前对象
		 */
		public Span setForeground(Appearance foreground) {
			mForeground = foreground;
			return this;
		}

		/**
		 * @param text  文本
		 * @param start 起始
		 * @param end   结束
		 * @return span
		 */
		public static Span obtain(CharSequence text, int start, int end) {
			Span span = POOL.acquire();
			if (span == null) {
				span = new Span();
			}

			span.mText = text;
			span.mStart = start;
			span.mEnd = end;
			span.reuse();
			return span;
		}

		// FOR TEST
		@Override
		public String toString() {
			if (mText == null) {
				return "";
			}

			return String.valueOf(mText.subSequence(mStart, mEnd));
		}

		@VisibleForTesting
		public TextStyle getTextStyle() {
			return mTextStyle;
		}

		@VisibleForTesting
		public Appearance getBackground() {
			return mBackground;
		}

		@VisibleForTesting
		public Appearance getForeground() {
			return mForeground;
		}

		@VisibleForTesting
		public Object getTag() {
			return mTag;
		}
	}

	@NonNull
	@Override
	public String toString() {
		String digest = mLayout.toString();
		final int max = 16;
		if (digest.length() > max) {
			StringBuilder builder = new StringBuilder(32);
			builder.append(digest, 0, max);
			builder.append("...");
			digest = builder.toString();
		}
		return digest;
	}
}
