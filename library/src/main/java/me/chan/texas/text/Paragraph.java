package me.chan.texas.text;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import me.chan.texas.R;
import me.chan.texas.compat.Predicate;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.Rect;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.renderer.ui.RendererHost;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.text.layout.Span;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.Penalty;
import me.chan.texas.text.layout.TextSpan;
import me.chan.texas.text.tokenizer.Token;
import me.chan.texas.text.util.TexasIterator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 段落
 */
public final class Paragraph extends Segment {
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


	@NonNull
	@RestrictTo(LIBRARY)
	volatile Layout mLayout;

	@RestrictTo(LIBRARY)
	final List<Element> mElements;

	ParagraphDecor mDecor;

	int mId;

	private ParagraphSelection mSelection;

	private ParagraphSelection mHighlight;

	private RecyclerView.ViewHolder mHolder;

	private RendererHost mHost;

	@RestrictTo(LIBRARY)
	@Nullable
	public ParagraphDecor getDecor() {
		return mDecor;
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({TYPESET_POLICY_DEFAULT, TYPESET_POLICY_CJK_MIX_OPTIMIZATION, TYPESET_POLICY_BIDI_TEXT,
			TYPESET_POLICY_ACCEPT_CONTROL_CHAR})
	public @interface TypesetPolicy {
	}

	@Override
	public void getRect(@NonNull Rect rect) {
		mLayout.getPadding(rect);
	}

	@Nullable
	@Override
	public Rect getRect() {
		return mLayout.getPadding();
	}

	@Override
	@RestrictTo(LIBRARY)
	public void setPadding(Rect rect) {
		mLayout.setPadding(rect);
	}

	@RestrictTo(LIBRARY)
	@Nullable
	public ParagraphSelection getSelection(Selection.Type type) {
		if (type == Selection.Type.SELECTION) {
			return mSelection;
		} else if (type == Selection.Type.HIGHLIGHT) {
			return mHighlight;
		} else {
			throw new IllegalArgumentException("unknown type: " + type);
		}
	}

	@RestrictTo(LIBRARY)
	public void setSelection(Selection.Type type, ParagraphSelection selection) {
		if (type == Selection.Type.SELECTION) {
			mSelection = selection;
		} else if (type == Selection.Type.HIGHLIGHT) {
			mHighlight = selection;
		} else {
			throw new IllegalArgumentException("unknown type: " + type);
		}
	}

	private Paragraph(Object tag) {
		if (tag != null) {
			mTagsKv = new SparseArrayCompat<>();
			mTagsKv.put(R.id.me_chan_texas_view_segment_tag, tag);
		}
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mElements = new ArrayList<>(memoryOption.getParagraphElementInitialCapacity());
		mLayout = Layout.obtain();
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
	public void recycle() {
		mId = 0;
		mLayout.clear();
		for (int i = 0; i < mElements.size(); ++i) {
			mElements.get(i).recycle();
		}
		mElements.clear();
		mTagsKv = null;
		mDecor = null;
		if (mSelection != null) {
			mSelection.recycle();
			mSelection = null;
		}
		if (mHighlight != null) {
			mHighlight.recycle();
			mHighlight = null;
		}
		mHost = null;
		mHolder = null;
	}

	@Override
	public boolean isRecycled() {
		return mId == 0;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void bind(RendererHost host) {
		mHost = host;
	}

	@Override
	public void attachToWindow(RecyclerView.ViewHolder holder) {
		mHolder = holder;
	}

	@Override
	public void detachFromWindow(RecyclerView.ViewHolder holder) {
		mHolder = null;
	}

	@Override
	public void requestRedraw() {
		if (mHost == null) {
			return;
		}

		mHost.updateSegment(mHolder, this);
	}

	@Override
	public int getIndex() {
		return mHost == null ? -1 : mHost.indexOf(this);
	}

	public boolean hasContent() {
		int size = mElements.size();
		if (size > 2) {
			return true;
		}

		if (size == 2) {
			return mElements.get(0) != Glue.TERMINAL || mElements.get(1) != Penalty.FORCE_BREAK;
		}

		return size > 0;
	}

	@RestrictTo(LIBRARY)
	public int getElementCount() {
		return mElements.size();
	}

	@RestrictTo(LIBRARY)
	public Element getElement(int index) {
		return mElements.get(index);
	}

	public Layout getLayout() {
		return mLayout;
	}

	public TexasIterator<Line> iterator() {
		return new TexasIterator<Line>() {
			private int mIndex = -1;

			@Override
			public Line next() {
				return restore(mIndex + 1);
			}

			@Override
			public Line prev() {
				return restore(mIndex - 1);
			}

			@Nullable
			@Override
			public Line current() {
				return restore(mIndex);
			}

			@Override
			public Line restore(int state) {
				Layout layout = getLayout();
				if (layout == null || state < 0 || state >= layout.getLineCount()) {
					return null;
				}

				return layout.getLine(mIndex = state);
			}

			@Override
			public int save() {
				return mIndex;
			}
		};
	}

	@RestrictTo(LIBRARY)
	static Paragraph obtain() {
		return new Paragraph(null);
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

		public Builder lineSpacingExtra(float lineSpace) {
			mBuilder0.lineSpacingExtra(lineSpace);
			return this;
		}

		public Builder breakStrategy(BreakStrategy breakStrategy) {
			mBuilder0.breakStrategy(breakStrategy);
			return this;
		}

		public Builder setPadding(Rect padding) {
			mBuilder0.setPadding(padding);
			return this;
		}

		public Builder textGravity(int gravity) {
			mBuilder0.textGravity(gravity);
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
		 * @return 设置decor
		 */
		public Builder decor(ParagraphDecor decor) {
			mBuilder0.decor(decor);
			return this;
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
		 * @param spanReader span 读取
		 * @return 当前对象
		 */
		public Builder stream(CharSequence text, SpanStylesReader spanReader) {
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
		public Builder stream(CharSequence text, int start, int end, SpanStylesReader spanReader) {
			mBuilder0.stream(text, start, end, spanReader);
			return this;
		}

		/**
		 * @param enable 是否在每次添加文本后追加空格来自动分割不同的句子，默认打开
		 * @return 当前对象
		 */
		public Builder appendSpaceEnable(boolean enable) {
			mBuilder0.appendSpaceEnable(enable);
			return this;
		}

		public interface SpanStylesReader {
			SpanStyles read(Token token);
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
		 * 超文本
		 *
		 * @param span span
		 * @return 当前对象
		 */
		public Builder hyperSpan(HyperSpan span) {
			mBuilder0.hyperSpan(span);
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
	public static class SpanBuilder implements Builder.SpanStylesReader {
		private final Builder mBuilder;
		private SpanStyles mSpan;

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
			mSpan = SpanStyles.obtain(text, start, end);
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
		public final SpanStyles read(Token token) {
			SpanStyles span = SpanStyles.obtain(mSpan.mText, mSpan.mStart, mSpan.mEnd);
			span.copyMeta(mSpan);
			return span;
		}
	}

	/**
	 * 文本的样式
	 */
	public static class SpanStyles extends DefaultRecyclable {
		private static final ObjectPool<SpanStyles> POOL = new ObjectPool<>(32);

		private CharSequence mText;
		private int mStart;
		private int mEnd;

		@RestrictTo(LIBRARY)
		final TextStyles mStyles = new TextStyles();
		Object mTag;

		private SpanStyles() {
		}

		@RestrictTo(LIBRARY)
		public void copyMeta(SpanStyles other) {
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
		public SpanStyles setTag(Object tag) {
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
		public SpanStyles setTextStyle(TextStyle textStyle) {
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
		public SpanStyles setBackground(Appearance background) {
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
		public SpanStyles setForeground(Appearance foreground) {
			mStyles.setForeground(foreground);
			return this;
		}

		public static SpanStyles obtain(Token token) {
			return obtain(token.getCharSequence(), token.getStart(), token.getEnd());
		}

		/**
		 * @param text  文本
		 * @param start 起始
		 * @param end   结束
		 * @return span
		 */
		public static SpanStyles obtain(CharSequence text, int start, int end) {
			SpanStyles span = POOL.acquire();
			if (span == null) {
				span = new SpanStyles();
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

		public TextStyle getTextStyle() {
			return mStyles.getTextStyle();
		}

		public Appearance getBackground() {
			return mStyles.getBackground();
		}

		public Appearance getForeground() {
			return mStyles.getForeground();
		}

		public Object getTag() {
			return mTag;
		}
	}

	@WorkerThread
	@RestrictTo(LIBRARY)
	public void measure(Measurer measurer, TextAttribute textAttribute) {
		Layout layout = getLayout();
		layout.clear();

		int elementSize = getElementCount();
		for (int j = 0; j < elementSize; ++j) {
			Element element = getElement(j);
			element.measure(measurer, textAttribute);
		}
	}

	@NonNull
	public List<Paragraph> split(Predicate<Span> predicate) {
		List<Paragraph> paragraphs = new ArrayList<>();
		int start = 0;
		int end = 1;
		for (; end < mElements.size(); ++end) {
			Element element = mElements.get(end);
			if (!(element instanceof Span)) {
				continue;
			}

			Span span = (Span) element;
			if (predicate.test(span)) {
				end = adjustSpiltIndex(span, end + 1);
				paragraphs.add(fork(this, start, end));
				start = end;
				--end;
			}
		}

		if (start < end && end <= mElements.size()) {
			paragraphs.add(fork(this, start, end));
		}

		return paragraphs;
	}

	private int adjustSpiltIndex(Span anchor, int end) {
		if (!(anchor instanceof TextSpan)) {
			return end;
		}

		TextSpan anchorSpan = (TextSpan) anchor;
		for (; end < mElements.size(); ++end) {
			Element element = mElements.get(end);
			if (!(element instanceof Span)) {
				continue;
			}

			if (!(element instanceof TextSpan)) {
				break;
			}

			TextSpan span = (TextSpan) element;
			if (!span.isSameGroup(anchorSpan)) {
				break;
			}
		}
		return end;
	}

	private static Paragraph fork(Paragraph src, int start, int end) {
		Paragraph copy = new Paragraph(null);
		for (int i = start; i < end; ++i) {
			copy.mElements.add(src.mElements.get(i));
		}
		copy.mTagsKv = copyKv(src.mTagsKv);
		copy.mDecor = src.mDecor;

		copy.mSelection = copyParagraphSelection(src.mSelection, copy);
		copy.mHighlight = copyParagraphSelection(src.mHighlight, copy);
		copy.fillTail();

		Layout copyLayout = copy.getLayout();
		copyLayout.getAdvise().copy(src.getLayout().getAdvise());

		return copy;
	}

	private static SparseArrayCompat<Object> copyKv(SparseArrayCompat<Object> kv) {
		return copyKv(new SparseArrayCompat<>(), kv);
	}

	private static SparseArrayCompat<Object> copyKv(SparseArrayCompat<Object> copy, SparseArrayCompat<Object> kv) {
		if (kv == null) {
			return null;
		}

		for (int i = 0; i < kv.size(); ++i) {
			copy.put(kv.keyAt(i), kv.valueAt(i));
		}
		return copy;
	}

	private void fillTail() {
		int size = mElements.size();
		if (size >= 2 && mElements.get(size - 2) == Glue.TERMINAL && mElements.get(size - 1) == Penalty.FORCE_BREAK) {
			return;
		}

		mElements.add(Glue.TERMINAL);
		mElements.add(Penalty.FORCE_BREAK);
	}

	private static ParagraphSelection copyParagraphSelection(ParagraphSelection selection, Paragraph paragraph) {
		if (selection == null) {
			return null;
		}

		ParagraphSelection copy = ParagraphSelection.obtain(selection.getType(), selection.getSelectionStyle(), paragraph);
		for (int i = 0; i < paragraph.getElementCount(); ++i) {
			Element element = paragraph.getElement(i);
			if (element instanceof Span) {
				Span span = (Span) element;
				if (selection.isSelected(span)) {
					copy.appendSpan(span);
				}
			}
		}
		copy.setBackgroundInvalid(true);
		return copy;
	}

	public Paragraph merge(Paragraph other) {
		Paragraph copy = new Paragraph(null);
		copy.mElements.addAll(mElements);

		int size = copy.mElements.size();
		if (size >= 2
				&& copy.mElements.get(size - 2) == Glue.TERMINAL
				&& copy.mElements.get(size - 1) == Penalty.FORCE_BREAK) {
			copy.mElements.remove(size - 1);
			copy.mElements.remove(size - 2);
		}

		copy.mElements.addAll(other.mElements);

		copy.mTagsKv = copyKv(mTagsKv);
		if (other.mTagsKv != null) {
			if (copy.mTagsKv == null) {
				copy.mTagsKv = new SparseArrayCompat<>();
			}
			copyKv(copy.mTagsKv, other.mTagsKv);
		}
		copy.mDecor = mDecor;

		copy.mSelection = mergeParagraphSelection(mSelection, other.mSelection, copy);
		copy.mHighlight = mergeParagraphSelection(mHighlight, other.mHighlight, copy);

		Layout copyLayout = copy.getLayout();
		copyLayout.getAdvise().copy(getLayout().getAdvise());

		return copy;
	}

	private static ParagraphSelection mergeParagraphSelection(
			ParagraphSelection s1, ParagraphSelection s2, Paragraph paragraph) {
		if (s1 == null && s2 == null) {
			return null;
		}

		ParagraphSelection base = s1 != null ? s1 : s2;
		ParagraphSelection copy = ParagraphSelection.obtain(
				base.getType(), base.getSelectionStyle(), paragraph);
		for (int i = 0; i < paragraph.getElementCount(); ++i) {
			Element element = paragraph.getElement(i);
			if (element instanceof Span) {
				Span span = (Span) element;
				if ((s1 != null && s1.isSelected(span))
						|| (s2 != null && s2.isSelected(span))) {
					copy.appendSpan(span);
				}
			}
		}
		copy.setBackgroundInvalid(true);
		return copy;
	}

	@NonNull
	@Override
	public String toString() {
		if (mLayout.getLineCount() == 0) {
			Object tag = getTag();
			return tag == null ? super.toString() : tag.toString();
		}

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
