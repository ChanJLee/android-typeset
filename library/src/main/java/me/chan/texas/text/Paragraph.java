package me.chan.texas.text;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Rect;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;

import me.chan.texas.TexasOption;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.tokenizer.Token;
import me.chan.texas.typesetter.utils.ElementStream;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 段落
 */
public final class Paragraph implements Segment {

	@NonNull
	@RestrictTo(LIBRARY)
	volatile Layout mLayout;

	private final Builder mBuilder;

	private volatile ElementStream mElementStream;

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
	/**
	 * 是否接受文本中存在的control符号，比如\n
	 */
	public static final int TYPESET_POLICY_ACCEPT_CONTROL_CHAR = 4;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({TYPESET_POLICY_DEFAULT, TYPESET_POLICY_CJK_MIX_OPTIMIZATION, TYPESET_POLICY_BIDI_TEXT,
			TYPESET_POLICY_ACCEPT_CONTROL_CHAR})
	public @interface TypesetPolicy {
	}

	final int mId;

	@Nullable
	@Override
	public Object getTag() {
		return mBuilder.mTag;
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

	@Override
	public void recycle() {
		mLayout.recycle();
	}

	@Override
	public boolean isRecycled() {
		return mLayout.isRecycled();
	}

	private ParagraphSelection mSelection;

	@RestrictTo(LIBRARY)
	@Nullable
	public ParagraphSelection getSelection() {
		return mSelection;
	}

	@RestrictTo(LIBRARY)
	public void setSelection(ParagraphSelection selection) {
		mSelection = selection;
	}

	private Paragraph(Builder builder) {
		mBuilder = builder;
		mLayout = Layout.obtain();
		Layout.Advise advise = mLayout.getAdvise();
		advise.setLineSpace(builder.mLineSpace);
		advise.setBreakStrategy(builder.mBreakStrategy);
		advise.setTypesetPolicies(builder.mTypesetPolicy);
		mId = Segment.nextId();
	}

	@RestrictTo(LIBRARY)
	public synchronized Layout swap(@NonNull Layout layout) {
		layout.finishLayout();
		Layout old = mLayout;
		mLayout = layout;
		return old;
	}

	@Override
	public int getId() {
		return mId;
	}

	@NonNull
	@RestrictTo(LIBRARY)
	public Layout getLayout() {
		return mLayout;
	}

	@RestrictTo(LIBRARY)
	@WorkerThread
	public synchronized ElementStream getElementStream(TexasOption option) {
		if (mElementStream == null) {
			List<TextEditRecord> records = mBuilder.mRecords;
			ParagraphBuilderInternal builderInternal = new ParagraphBuilderInternal(option, records, mLayout.getAdvise());
			mElementStream = builderInternal.parse();
			for (TextEditRecord record : records) {
				record.recycle();
			}
			records.clear();
		}

		return mElementStream;
	}

	@Nullable
	public ElementStream tryGetElementStream() {
		return mElementStream;
	}

	/**
	 * span构造器
	 */
	public static class SpanBuilder {
		private final Builder mBuilder;
		private Span mSpan;

		private SpanBuilder(Builder builder) {
			mBuilder = builder;
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
			mSpan.setTextStyle(textStyle);
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
			mSpan.setBackground(background);
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
			mSpan.setForeground(foreground);
			return this;
		}


		private void flush() {
			if (mSpan == null || mSpan.isRecycled()) {
				return;
			}

			mBuilder.mRecords.add(mSpan);
			mSpan = null;
		}

		/**
		 * 创建新的span
		 *
		 * @return span
		 */
		public Builder finish() {
			flush();
			return mBuilder;
		}
	}

	/**
	 * 文本的样式
	 */
	public static class Span extends DefaultRecyclable implements TextEditRecord {
		private static final ObjectPool<Span> POOL = new ObjectPool<>(32);

		private CharSequence mText;
		private int mStart;
		private int mEnd;

		@RestrictTo(LIBRARY)
		final TextStyles mStyles = new TextStyles();
		Object mTag;

		private Span() {
		}

		public CharSequence getText() {
			return mText;
		}

		public int getStart() {
			return mStart;
		}

		public int getEnd() {
			return mEnd;
		}

		public void copyMeta(Span other) {
			this.mStyles.copy(other.mStyles);
			this.mTag = other.mTag;
		}

		@Override
		protected void onRecycle() {
			mText = null;
			mStart = mEnd = 0;
			mStyles.clear();
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
			mStyles.setTextStyle(textStyle);
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
			mStyles.setBackground(background);
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
			mStyles.setForeground(foreground);
			return this;
		}

		public static Span obtain(Token token) {
			return obtain(token.getCharSequence(), token.getStart(), token.getEnd());
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
			return mStyles.getTextStyle();
		}

		@VisibleForTesting
		public Appearance getBackground() {
			return mStyles.getBackground();
		}

		@VisibleForTesting
		public Appearance getForeground() {
			return mStyles.getForeground();
		}

		@VisibleForTesting
		public Object getTag() {
			return mTag;
		}
	}

	// TODO test recycle
	@RestrictTo(LIBRARY)
	public static class StreamEditRecord extends DefaultRecyclable implements TextEditRecord {
		private static final ObjectPool<StreamEditRecord> POOL = new ObjectPool<>(32);

		private CharSequence mText;
		private int mStart;
		private int mEnd;
		private Builder.SpanReader mSpanReader;

		private StreamEditRecord() {
		}

		public static TextEditRecord obtain(CharSequence text, int start, int end, Builder.SpanReader spanReader) {
			StreamEditRecord record = POOL.acquire();
			if (record == null) {
				record = new StreamEditRecord();
			}

			record.mText = text;
			record.mStart = start;
			record.mEnd = end;
			record.mSpanReader = spanReader;
			record.reuse();
			return record;
		}

		public CharSequence getText() {
			return mText;
		}

		public int getStart() {
			return mStart;
		}

		public int getEnd() {
			return mEnd;
		}

		public Builder.SpanReader getSpanReader() {
			return mSpanReader;
		}

		@Override
		protected void onRecycle() {
			mText = null;
			mStart = mEnd = 0;
			mSpanReader = null;
			POOL.release(this);
		}

		@NonNull
		@Override
		public String toString() {
			if (mText == null) {
				return "";
			}

			return mText.subSequence(mStart, mEnd).toString();
		}
	}

	@RestrictTo(LIBRARY)
	public static class BrkEditRecord implements TextEditRecord {
		public static final BrkEditRecord BRK = new BrkEditRecord();

		@Override
		public String toString() {
			return "BRK";
		}

		@Override
		public void recycle() {

		}
	}

	@RestrictTo(LIBRARY)
	public static class EmoticonEditRecord implements TextEditRecord {
		private final Emoticon mEmoticon;

		public EmoticonEditRecord(Emoticon emoticon) {
			mEmoticon = emoticon;
		}

		public Emoticon getEmoticon() {
			return mEmoticon;
		}

		@Override
		public void recycle() {

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

	/**
	 * 构造器，注意要尽量避免重复创建
	 */
	// todo 回归测试builder的策略有没有设定好
	public static class Builder {
		@RestrictTo(LIBRARY)
		int mTypesetPolicy;
		@RestrictTo(LIBRARY)
		float mLineSpace;
		@RestrictTo(LIBRARY)
		BreakStrategy mBreakStrategy;
		@RestrictTo(LIBRARY)
		Object mTag;
		@RestrictTo(LIBRARY)
		List<TextEditRecord> mRecords = new ArrayList<>(32);

		public Builder() {
			// TODO check prev-value
			this(TYPESET_POLICY_CJK_MIX_OPTIMIZATION);
		}

		public Builder(@TypesetPolicy int typesetPolicy) {
			mTypesetPolicy = typesetPolicy;
		}

		public Builder lineSpace(float lineSpace) {
			mLineSpace = lineSpace;
			return this;
		}

		public Builder breakStrategy(BreakStrategy breakStrategy) {
			mBreakStrategy = breakStrategy;
			return this;
		}

		/**
		 * @param tag 设置paragraph的额外信息，用来标识这个paragraph，因此需要保持唯一
		 * @return 当前对象
		 */
		public Builder tag(Object tag) {
			mTag = tag;
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
			mRecords.add(Span.obtain(text, start, end));
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
			return new SpanBuilder(this);
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
		 * @param spanReader span 读取
		 * @return 当前对象
		 */
		public Builder stream(CharSequence text, SpanReader spanReader) {
			return stream(text, 0, text.length(), spanReader);
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
			mRecords.add(StreamEditRecord.obtain(text, start, end, spanReader));
			return this;
		}

		public interface SpanReader {
			Span read(Token token);
		}

		/**
		 * 颜文字
		 *
		 * @param emoticon 颜文字
		 * @return 当前对象
		 */
		public Builder emoticon(Emoticon emoticon) {
			mRecords.add(new EmoticonEditRecord(emoticon));
			return this;
		}

		/**
		 * 强行断行
		 *
		 * @return 当前对象
		 */
		public Builder brk() {
			mRecords.add(BrkEditRecord.BRK);
			return this;
		}

		public Builder addTypesetPolicy(@TypesetPolicy int policy) {
			mTypesetPolicy |= policy;
			return this;
		}

		public Builder clearTypesetPolicy() {
			mTypesetPolicy = TYPESET_POLICY_DEFAULT;
			return this;
		}

		public Builder setTypesetPolicy(@TypesetPolicy int policy) {
			mTypesetPolicy = policy;
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
			if (brk) {
				mRecords.add(BrkEditRecord.BRK);
			}
			return new Paragraph(this);
		}
	}
}
