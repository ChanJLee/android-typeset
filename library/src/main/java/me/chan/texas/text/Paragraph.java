package me.chan.texas.text;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import me.chan.texas.misc.Rect;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.renderer.ui.RendererHost;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Penalty;
import me.chan.texas.text.tokenizer.Token;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


public final class Paragraph extends DefaultRecyclable implements Segment {
	private static final ObjectPool<Paragraph> POOL = new ObjectPool<>(Texas.getMemoryOption().getParagraphBufferSize());

	@NonNull
	@RestrictTo(LIBRARY)
	volatile Layout mLayout;

	@RestrictTo(LIBRARY)
	final List<Element> mElements;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	Object mTag;
	
	public static final int TYPESET_POLICY_DEFAULT = 0;
	
	public static final int TYPESET_POLICY_CJK_MIX_OPTIMIZATION = 1;
	
	public static final int TYPESET_POLICY_BIDI_TEXT = 2;
	
	public static final int TYPESET_POLICY_ACCEPT_CONTROL_CHAR = 4;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({TYPESET_POLICY_DEFAULT, TYPESET_POLICY_CJK_MIX_OPTIMIZATION, TYPESET_POLICY_BIDI_TEXT,
			TYPESET_POLICY_ACCEPT_CONTROL_CHAR})
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

	private ParagraphSelection mSelection;

	private ParagraphSelection mHighlight;

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
		POOL.release(this);
	}

	@Override
	public int getId() {
		return mId;
	}

	private RecyclerView.ViewHolder mHolder;
	private RendererHost mHost;

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

	
	public static class Builder extends DefaultRecyclable {
		private static final ObjectPool<Builder> POOL = new ObjectPool<>(8);


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

		public Builder textGravity(int gravity) {
			mBuilder0.textGravity(gravity);
			return this;
		}

		
		public Builder tag(Object tag) {
			mBuilder0.tag(tag);
			return this;
		}

		
		public Builder text(CharSequence text) {
			return text(text, 0, text.length());
		}

		
		public Builder text(CharSequence text, int start, int end) {
			mBuilder0.text(text, start, end);
			return this;
		}

		
		public SpanBuilder newSpanBuilder() {
			return mBuilder0.newSpanBuilder();
		}

		
		public Builder stream(CharSequence text, SpanReader spanReader) {
			return stream(text, 0, text.length(), spanReader);
		}

		
		public Builder stream(CharSequence text, int start, int end, SpanReader spanReader) {
			mBuilder0.stream(text, start, end, spanReader);
			return this;
		}

		
		public Builder appendSpaceEnable(boolean enable) {
			mBuilder0.appendSpaceEnable(enable);
			return this;
		}

		public interface SpanReader {
			Span read(Token token);
		}

		
		public Builder emoticon(Emoticon emoticon) {
			mBuilder0.emoticon(emoticon);
			return this;
		}

		
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

		
		public Paragraph build() {
			return build(true);
		}

		
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

		
		@Deprecated
		public static Builder newBuilder(TexasOption texasOption,
										 @TypesetPolicy int typesetPolicy) {
			return newBuilder(texasOption)
					.setTypesetPolicy(typesetPolicy);
		}

		
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

		
		public SpanBuilder next(CharSequence text) {
			return next(text, 0, text.length());
		}

		
		public SpanBuilder next(CharSequence text, int start, int end) {
			flush();
			mSpan = Span.obtain(text, start, end);
			return this;
		}

		
		public SpanBuilder tag(Object tag) {
			mSpan.mTag = tag;
			return this;
		}

		
		public SpanBuilder setTextStyle(TextStyle textStyle) {
			mSpan.setTextStyle(textStyle);
			return this;
		}

		
		public SpanBuilder setBackground(Appearance background) {
			mSpan.setBackground(background);
			return this;
		}

		
		public SpanBuilder setForeground(Appearance foreground) {
			mSpan.setForeground(foreground);
			return this;
		}


		private void flush() {
			if (mSpan == null || mSpan.isRecycled()) {
				return;
			}

			mBuilder.mBuilder0.stream(mSpan.mText, mSpan.mStart, mSpan.mEnd, this);


			reset();
		}

		
		public Builder buildSpan() {
			flush();
			return mBuilder;
		}

		@Override
		@RestrictTo(LIBRARY)
		public final Span read(Token token) {
			Span span = Span.obtain(mSpan.mText, mSpan.mStart, mSpan.mEnd);
			span.copyMeta(mSpan);
			return span;
		}
	}

	
	public static class Span extends DefaultRecyclable {
		private static final ObjectPool<Span> POOL = new ObjectPool<>(32);

		private CharSequence mText;
		private int mStart;
		private int mEnd;

		@RestrictTo(LIBRARY)
		final TextStyles mStyles = new TextStyles();
		Object mTag;

		private Span() {
		}

		@RestrictTo(LIBRARY)
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

		
		public Span tag(Object tag) {
			mTag = tag;
			return this;
		}

		
		public Span setTextStyle(TextStyle textStyle) {
			mStyles.setTextStyle(textStyle);
			return this;
		}

		
		public Span setBackground(Appearance background) {
			mStyles.setBackground(background);
			return this;
		}

		
		public Span setForeground(Appearance foreground) {
			mStyles.setForeground(foreground);
			return this;
		}

		public static Span obtain(Token token) {
			return obtain(token.getCharSequence(), token.getStart(), token.getEnd());
		}

		
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

	@NonNull
	@Override
	public String toString() {
		if (mLayout.getLineCount() == 0) {
			return mTag == null ? super.toString() : mTag.toString();
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
