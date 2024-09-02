package me.chan.texas.text;

import static me.chan.texas.text.Paragraph.TYPESET_POLICY_CJK_OPTIMIZATION;
import static me.chan.texas.text.Paragraph.TYPESET_POLICY_DEFAULT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import me.chan.texas.BuildConfig;
import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.icu.UnicodeUtils;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Penalty;
import me.chan.texas.text.layout.SymbolGlue;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.text.tokenizer.Token;
import me.chan.texas.text.tokenizer.TokenStream;
import me.chan.texas.utils.IntArray;

import java.util.ArrayList;
import java.util.List;

/*
 * 把文本添加过程抽离出来了，因为太复杂了，需要在另外一个文件里面单独写明
 * */
class ParagraphBuilderInternal {
	private static final int MIN_HYPER_LEN = 4;

	private final IntArray mHyphenated = new IntArray();
	private Measurer mMeasurer;
	private Hyphenation mHyphenation;
	private TextAttribute mTextAttribute;
	private Paragraph mParagraph;
	private RenderOption mRenderOption;
	private Object mTag;
	private final Paragraph.SpanBuilder mSpanBuilder;

	private float mLineSpace = -1;
	private BreakStrategy mBreakStrategy;
	private Token mLastToken;

	private Glue mCommonGlue;
	private Glue mStretchOnlyGlue;

	public ParagraphBuilderInternal(Paragraph.Builder builder) {
		mSpanBuilder = new Paragraph.SpanBuilder(builder);
	}

	public void lineSpace(float lineSpace) {
		mLineSpace = lineSpace;
	}

	public void breakStrategy(BreakStrategy breakStrategy) {
		mBreakStrategy = breakStrategy;
	}

	public void tag(Object tag) {
		mTag = tag;
	}

	public void text(CharSequence text, int start, int end) {
		appendSent(text, start, end, null);
	}

	public Paragraph.SpanBuilder newSpanBuilder() {
		mSpanBuilder.reset();
		return mSpanBuilder;
	}

	public void stream(CharSequence text, int start, int end, Paragraph.Builder.SpanReader spanReader) {
		appendSent(text, start, end, spanReader);
	}

	/**
	 * 颜文字
	 *
	 * @param emoticon 颜文字
	 */
	public void emoticon(Emoticon emoticon) {
		if (mParagraph == null) {
			throw new IllegalStateException("call newParagraph first");
		}

		appendEmoticon(emoticon);
	}

	/**
	 * @param brk 是否需要添加一个换行符
	 * @return paragraph
	 */
	public Paragraph build(boolean brk) {
		if (brk) {
			brk();
		}

		Layout.Advise advise = mParagraph.mLayout.getAdvise();
		advise.setLineSpace(mLineSpace);
		advise.setBreakStrategy(mBreakStrategy);
		mParagraph.mTag = mTag;
		mParagraph.mId = Segment.nextId();
		return mParagraph;
	}

	public void reset() {
		mParagraph = null;
		mMeasurer = null;
		mRenderOption = null;
		mTextAttribute = null;
		mHyphenation = null;
		mHyphenated.clear();
		mTag = null;
		mSpanBuilder.reset();
		mLineSpace = -1;
		mLastToken = null;
		mBreakStrategy = null;
		mStretchOnlyGlue = null;
		mCommonGlue = null;
	}

	public void reset(TexasOption texasOption,
					  @Paragraph.TypesetPolicy int typesetPolicy) {
		mRenderOption = texasOption.getRenderOption();
		mMeasurer = texasOption.getMeasurer();
		mHyphenation = texasOption.getHyphenation();
		mTextAttribute = texasOption.getTextAttribute();
		mParagraph = Paragraph.obtain();
		mParagraph.mLayout = Layout.obtain();
		Layout.Advise advise = mParagraph.mLayout.getAdvise();
		advise.setTypesetPolicy(typesetPolicy);
		mCommonGlue = Glue.obtain(mTextAttribute);
		mStretchOnlyGlue = Glue.obtain(
				0, 0, mTextAttribute.getSpaceStretch(), 0
		);
		mLastToken = null;
	}

	private void appendEmoticon(Emoticon emoticon) {
		Token token = Token.obtainNone();
		appendElement(emoticon.getDrawableBox());
		mLastToken = token;
	}

	/**
	 * 以下的代码都是为了将一个句子添加到当前段落中，并追加一个glue
	 */
	private void appendSent(CharSequence text, int start, int end, @Nullable Paragraph.Builder.SpanReader reader) {
		if (text == null) {
			throw new RuntimeException("call build twice");
		}

		if (mParagraph == null) {
			throw new IllegalStateException("call newParagraph first");
		}

		if (start >= end) {
			return;
		}

		// 将句子转换为单词流
		// 单词流会分析出一个句子中每个字符所代表的语义，这样可以精确的识别诸如： isn't、1920s 为一个单词
		TokenStream tokenStream = null;
		try {
			tokenStream = TokenStream.obtain(text, start, end);
			if (tokenStream == null) {
				return;
			}

			// 追加一个空格
			// 这个未来还能不能适用，就要看状态推导图了，目前看一个token后接blank和none不影响状态机的跳转
			if (mLastToken != null && tokenStream.hasNext()) {
				tokenStream.ahead(Token.obtainBlank());
			}

			Token prev = mLastToken;
			appendSent0(text, reader, tokenStream);
			if (prev != mLastToken && prev != null) {
				prev.recycle();
			}

			// 因为token会跟着stream销毁，所以要保留上次的token
			if (mLastToken != null) {
				mLastToken = Token.copy(mLastToken);
			}
		} finally {
			if (tokenStream != null) {
				tokenStream.recycle();
			}
		}
	}

	private void appendSent0(CharSequence text,
							 Paragraph.Builder.SpanReader spanReader,
							 TokenStream tokenStream) {
		while (tokenStream.hasNext()) {
			mLastToken = accept(mLastToken, tokenStream, text, spanReader);
		}
	}

	private void appendWordToken(CharSequence text,
								 Paragraph.Builder.SpanReader spanReader,
								 Token token) {
		// 区分中英文
		if (token.checkAttribute(Token.WORD_TYPE_MASK, Token.WORD_TYPE_LATIN)) {
			appendLatinWordToken(text, spanReader, token);
		} else if (token.checkAttribute(Token.WORD_TYPE_MASK, Token.WORD_TYPE_CN)) {
			appendCnWordToken(text, spanReader, token);
		} else if (token.checkAttribute(Token.WORD_TYPE_MASK, Token.WORD_TYPE_CONTEXT_SENSITIVE)) {
			appendContextSensitiveWordToken(text, spanReader, token);
		} else {
			throw new IllegalArgumentException("unknown token type");
		}
	}

	private void appendContextSensitiveWordToken(CharSequence text,
												 Paragraph.Builder.SpanReader spanReader,
												 Token token) {
		int start = token.getStart();
		int end = token.getEnd();
		Paragraph.Span span = null;
		if (spanReader != null) {
			span = spanReader.read(text, start, end);
		}

		TextStyle textStyle = null;
		Object tag = null;
		Appearance background = null;
		Appearance foreground = null;
		if (span != null) {
			textStyle = span.mTextStyle;
			tag = span.mTag;
			background = span.mBackground;
			foreground = span.mForeground;
		}

		TextBox textBox = TextBox.obtain(text, start, end,
				mMeasurer, textStyle,
				tag,
				background,
				foreground);
		textBox.addAttribute(TextBox.ATTRIBUTE_RTL);

		appendElement(textBox);

		if (span != null) {
			span.recycle();
		}
	}

	private void appendLatinWordToken(CharSequence text,
									  Paragraph.Builder.SpanReader spanReader,
									  Token token) {
		Paragraph.Span span = null;
		if (spanReader != null) {
			int start = token.getStart();
			int end = token.getEnd();
			span = spanReader.read(text, start, end);
		}

		TextStyle textStyle = null;
		Object tag = null;
		Appearance background = null;
		Appearance foreground = null;
		if (span != null) {
			textStyle = span.mTextStyle;
			tag = span.mTag;
			background = span.mBackground;
			foreground = span.mForeground;
		}

		appendEnText(text, token.getStart(), token.getEnd(), textStyle, tag, background, foreground);

		if (span != null) {
			span.recycle();
		}
	}

	private void appendCnWordToken(CharSequence text,
								   Paragraph.Builder.SpanReader spanReader,
								   Token token) {
		Layout layout = mParagraph.getLayout();
		Layout.Advise advise = layout.getAdvise();
		int typesetPolicy = advise.getTypesetPolicy();
		Element linkElement = typesetPolicy == TYPESET_POLICY_CJK_OPTIMIZATION ? mStretchOnlyGlue : Penalty.ADVISE_BREAK;
		for (int i = token.getStart(); i < token.getEnd(); ++i) {
			if (i != token.getStart()) {
				appendElement(linkElement);
			}

			int start = i;
			int end = i + 1;
			Paragraph.Span span = null;
			if (spanReader != null) {
				span = spanReader.read(text, start, end);
			}

			TextStyle textStyle = null;
			Object tag = null;
			Appearance background = null;
			Appearance foreground = null;
			if (span != null) {
				textStyle = span.mTextStyle;
				tag = span.mTag;
				background = span.mBackground;
				foreground = span.mForeground;
			}

			TextBox textBox = TextBox.obtain(text, start, end,
					mMeasurer, textStyle,
					tag,
					background,
					foreground);

			// 英文模式下 要对中文进行缩放
			if (typesetPolicy == TYPESET_POLICY_DEFAULT) {
				textBox.addAttribute(TextBox.ATTRIBUTE_ZOOM_OUT);
			}

			appendElement(textBox);

			if (span != null) {
				span.recycle();
			}
		}
	}

	private void appendUnknownToken(CharSequence text,
									Paragraph.Builder.SpanReader spanReader,
									Token token) {
		Paragraph.Span span = null;
		if (spanReader != null) {
			int start = token.getStart();
			int end = token.getEnd();
			span = spanReader.read(text, start, end);
		}

		TextStyle textStyle = null;
		Object tag = null;
		Appearance background = null;
		Appearance foreground = null;
		if (span != null) {
			textStyle = span.mTextStyle;
			tag = span.mTag;
			background = span.mBackground;
			foreground = span.mForeground;
		}

		appendElement(TextBox.obtain(text, token.getStart(), token.getEnd(),
				mMeasurer, textStyle,
				tag,
				background,
				foreground));

		if (span != null) {
			span.recycle();
		}
	}

	private TextBox appendSymbolToken(CharSequence text,
									  Paragraph.Builder.SpanReader spanReader,
									  Token token) {
		TextBox textBox = obtainSymbolTextBox(text, spanReader, token);

		appendSymbolToken(textBox);

		return textBox;
	}

	private void appendSymbolToken(TextBox textBox) {
		appendElement(textBox);
	}

	private TextBox obtainSymbolTextBox(CharSequence text,
										Paragraph.Builder.SpanReader spanReader,
										Token token) {
		Paragraph.Span span = null;
		if (spanReader != null) {
			int start = token.getStart();
			int end = token.getEnd();
			span = spanReader.read(text, start, end);
		}

		TextStyle textStyle = null;
		Object tag = null;
		Appearance background = null;
		Appearance foreground = null;
		if (span != null) {
			textStyle = span.mTextStyle;
			tag = span.mTag;
			background = span.mBackground;
			foreground = span.mForeground;
		}

		TextBox textBox = TextBox.obtain(text, token.getStart(), token.getEnd(),
				mMeasurer, textStyle,
				tag,
				background,
				foreground);

		if (span != null) {
			span.recycle();
		}

		return textBox;
	}

	private void appendElement(Element element) {
		if (Texas.CHECK_RULES_TRANSLATE && !mParagraph.mElements.isEmpty()) {
			Element last = mParagraph.mElements.get(mParagraph.mElements.size() - 1);
			if (last == element) {
				throw new IllegalStateException("append same element twice");
			}
		}
		mParagraph.mElements.add(element);
	}

	/**
	 * 用于在添加glue前后使用，因为glue本身代表可以断点
	 * 所以没必要再添加advise break
	 *
	 * @param element element
	 */
	private void appendElementExcludeAdvise(Element element) {

		if (element == Penalty.ADVISE_BREAK) {
			return;
		}
		appendElement(element);
	}

	private void appendEnText(@NonNull CharSequence text, int start, int end,
							  TextStyle textStyle,
							  Object tag,
							  Appearance background,
							  Appearance foreground) {
		int len = end - start;
		if (len <= MIN_HYPER_LEN) {
			appendElement(TextBox.obtain(text, start, end,
					mMeasurer,
					textStyle,
					tag,
					background,
					foreground
			));
			return;
		}

		mHyphenated.clear();
		int groupId = mHyphenation.hyphenate(text, start, end, mHyphenated);
		int size = mHyphenated.size();
		if (size == 0) {
			appendElement(TextBox.obtain(text, start, end,
					mMeasurer,
					textStyle,
					tag,
					background,
					foreground,
					groupId
			));
		} else {
			for (int j = 0; j < size; ++j) {
				int point = mHyphenated.get(j);
				if (point == start) {
					continue;
				}

				TextBox box = TextBox.obtain(text, start, point,
						mMeasurer,
						textStyle,
						tag,
						background,
						foreground,
						groupId
				);
				appendElement(box);
				if (j != size - 1) {
					if (UnicodeUtils.isHyphen(text.charAt(point - 1))) {
						box.addAttribute(TextBox.ATTRIBUTE_PENALTY);
						appendElement(Penalty.obtainFakePenalty(Texas.HYPHEN_PENALTY));
					} else {
						appendElement(Penalty.obtain(Texas.HYPHEN_PENALTY,
								tag,
								textStyle,
								mMeasurer,
								mTextAttribute
						));
					}
				}
				start = point;
			}
		}
	}

	public void brk() {
		if (mLastToken != null) {
			mLastToken.recycle();
			mLastToken = null;
		}

		appendElement(Glue.TERMINAL);
		appendElement(Penalty.FORCE_BREAK);
	}

	private static abstract class TypesetRule {
		private Token mAcceptedToken;

		public final boolean perform(ParagraphBuilderInternal builder,
									 Token accepted,
									 TokenStream stream,
									 CharSequence text,
									 Paragraph.Builder.SpanReader spanReader) {
			int state = stream.save();
			boolean accept = perform0(builder, accepted, stream, text, spanReader);
			if (!accept) {
				stream.restore(state);
			}
			return accept;
		}

		protected abstract boolean perform0(ParagraphBuilderInternal builder,
											Token accepted,
											TokenStream stream,
											CharSequence text,
											Paragraph.Builder.SpanReader spanReader);

		final protected void accept(Token token) {
			mAcceptedToken = token;
		}

		@Nullable
		public Token getAcceptedToken() {
			return mAcceptedToken;
		}
	}

	private static final List<TypesetRule> TYPESET_RULES;

	private static int getTokenTypeSafe(Token token) {
		return token == null ? Token.TYPE_NONE : token.getType();
	}

	private static int getTokenAttributeSafe(Token token) {
		return token == null ? Token.TYPE_NONE : token.getAttributes();
	}

	private static boolean checkTokenAttributeSafe(Token token,
												   @Token.TokenMask int mask,
												   @Token.TokenAttribute int attr) {
		return token != null && token.checkAttribute(mask, attr);
	}

	static {
		// blank 实际上可以约减掉了，但是因为为了后期好理解所以保留
		// 因为blank存在，我们需要关心下一个token是什么，所以如果blank删除，那么rules的api接口就要修改
		TYPESET_RULES = new ArrayList<>();
		TYPESET_RULES.add(new WordRules());
		TYPESET_RULES.add(new SymbolRules());
		TYPESET_RULES.add(new BlankRules());
		TYPESET_RULES.add(new UnknownRules());
	}

	/**
	 * 规则见根目录的推导文件 rules.txt
	 *
	 * @return 实际被接受的token
	 */
	private Token accept(@Nullable Token accepted, /* 之前被接受的token */
						 TokenStream stream,
						 CharSequence text,
						 Paragraph.Builder.SpanReader spanReader) {
		int size = TYPESET_RULES.size();
		for (int i = 0; i < size; ++i) {
			TypesetRule rule = TYPESET_RULES.get(i);
			if (rule.perform(this, accepted, stream, text, spanReader)) {
				return rule.getAcceptedToken();
			}
		}

		throw new IllegalStateException("invalid state, no rule match");
	}

	private static class WordRules extends TypesetRule {

		@Override
		protected boolean perform0(ParagraphBuilderInternal builder, Token accepted,
								   TokenStream stream,
								   CharSequence text,
								   Paragraph.Builder.SpanReader spanReader) {
			int state = stream.save();
			Token current = stream.next();
			if (current.getType() != Token.TYPE_WORD) {
				return false;
			}

			// 1: word -> glue 其实就是中英文之间分割
			// 2: unknown -> glue
			// 3: blank -> noop
			// 4: none -> noop
			// 5: symbol -> prefix state 1
			int prevType = getTokenTypeSafe(accepted);
			if (prevType == Token.TYPE_WORD) {
				if (getTokenAttributeSafe(accepted) != getTokenAttributeSafe(current)) {
					builder.appendElement(builder.mCommonGlue);
				}
			} else if (prevType == Token.TYPE_SYMBOL) {
				performPrefixState1(builder, accepted, stream, state);
			} else if (prevType == Token.TYPE_UNKNOWN) {
				builder.appendElement(builder.mCommonGlue);
			}

			builder.appendWordToken(text, spanReader, current);

			accept(current);
			return true;
		}

		private static void performPrefixState1(ParagraphBuilderInternal builder, @NonNull Token accepted, TokenStream stream, int state) {
			// 先获取建议
			Element adviseElement = checkTokenAttributeSafe(accepted, Token.SYMBOL_KINSOKU_MASK, Token.SYMBOL_KINSOKU_AVOID_TAIL) ?
					Penalty.FORBIDDEN_BREAK : Penalty.ADVISE_BREAK;

			// 是否添加空格取决于前一个符号
			// 如果它要，那么就添加
			// 不要的话尝试用原先单词流中的数据填充，不过要注意，如果前面的单词不让填充空格，那么也是什么都不能做的

			if (checkTokenAttributeSafe(accepted, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_STRETCH_RIGHT)) {
				builder.appendElementExcludeAdvise(adviseElement);
				builder.appendElement(builder.mCommonGlue);
				builder.appendElementExcludeAdvise(adviseElement);
			} else if (checkTokenAttributeSafe(accepted, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_RIGHT)) {
				if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
					builder.appendElementExcludeAdvise(adviseElement);
					builder.appendElement(obtainSymbolGlueFromStack(builder));
					builder.appendElementExcludeAdvise(adviseElement);
				}
			} else {
				Token realPrev = stream.tryGet(state, -1);
				if (realPrev != accepted && getTokenTypeSafe(realPrev) == Token.TYPE_BLANK &&
						(getTokenAttributeSafe(accepted) & Token.SYMBOL_TYPEFACE_MASK) == 0) {
					builder.appendElementExcludeAdvise(adviseElement);
					builder.appendElement(builder.mCommonGlue);
					builder.appendElementExcludeAdvise(adviseElement);
				} else {
					builder.appendElement(adviseElement);
				}
			}
		}
	}

	private static class BlankRules extends TypesetRule {

		@Override
		public boolean perform0(ParagraphBuilderInternal builder, Token accepted,
								TokenStream stream,
								CharSequence text,
								Paragraph.Builder.SpanReader spanReader) {
			Token current = stream.next();
			if (current.getType() != Token.TYPE_BLANK) {
				return false;
			}

			Token next = stream.tryGet(0);
			//--------------------v next ----------
			// 				word	unknown		symbol		blank		none
			//----------|-----------------------------
			// word    	|   direct  direct		trans		noop		noop
			// unknown 	|   direct  direct		trans		noop		noop
			// symbol 	|   trans	trans		trans		noop		noop < 本质上都是丢给后面的人处理
			// blank	|	noop	noop		noop		noop		noop < 规避多个空格
			// none		|   noop 	noop 		noop		noop		noop < 首行不留白，这个可能作为 future 未来支持开启
			//-----------------------------------------
			//  ^ prev
			// trans 就要上面的规则去检查是否丢弃了 blank，但是当前的规则不需要处理，所以本质上是noop

			int prevType = getTokenTypeSafe(accepted);
			int nextType = getTokenTypeSafe(next);

			if (prevType == Token.TYPE_WORD ||
					prevType == Token.TYPE_UNKNOWN) {
				if (nextType == Token.TYPE_WORD ||
						nextType == Token.TYPE_UNKNOWN) {
					builder.appendElement(builder.mCommonGlue);
					accept(current);
					return true;
				}

				accept(accepted);
				return true;
			}

			if (prevType == Token.TYPE_SYMBOL ||
					prevType == Token.TYPE_BLANK ||
					prevType == Token.TYPE_NONE) {
				accept(accepted);
				return true;
			}

			throw new IllegalStateException("blank's rules under invalid state");
		}
	}

	private static class SymbolRules extends TypesetRule {

		@Override
		public boolean perform0(ParagraphBuilderInternal builder, Token accepted,
								TokenStream stream,
								CharSequence text,
								Paragraph.Builder.SpanReader spanReader) {
			int state = stream.save();
			Token current = stream.next();
			if (current.getType() != Token.TYPE_SYMBOL) {
				return false;
			}

			//--------------------v next ----------
			// 				word	unknown		symbol		blank		none
			//----------|-----------------------------
			// word    	|   state2  state2		state2		state2		state2
			// unknown 	|   state2  state2		state2		state2		state2
			// symbol 	|   state1	state1		state1		state1		state1
			// blank	|	-		-			-			-			-
			// none		|   直接进 	直接进 		直接进		直接进		直接进
			//-----------------------------------------
			//  ^ prev

			int prevType = getTokenTypeSafe(accepted);
			if (prevType == Token.TYPE_NONE) {
				TextBox textBox = builder.appendSymbolToken(text, spanReader, current);
				if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_LEFT)) {
					if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
						textBox.addAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT);
					}
				} else if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_RIGHT)) {
					if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
						textBox.addAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT);
					}
				}
				accept(current);
				return true;
			}

			if (prevType == Token.TYPE_BLANK) {
				// never
				throw new IllegalStateException("symbol's rules under invalid sate");
			}

			if (prevType == Token.TYPE_SYMBOL) {
				preformState1(builder, accepted, current, text, spanReader, stream, state);
				return true;
			}

			preformState2(builder, current, text, spanReader, stream, state);
			return true;
		}

		private void preformState2(ParagraphBuilderInternal builder, Token current,
								   CharSequence text, Paragraph.Builder.SpanReader spanReader,
								   TokenStream stream, int state) {

			// 前置条件就是 prev 是单词

			Element adviseElement = checkTokenAttributeSafe(current, Token.SYMBOL_KINSOKU_MASK, Token.SYMBOL_KINSOKU_AVOID_HEADER) ?
					Penalty.FORBIDDEN_BREAK : Penalty.ADVISE_BREAK;

			// 生成一个 symbol
			TextBox box = builder.obtainSymbolTextBox(text, spanReader, current);
			if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_LEFT)) {
				if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
					box.addAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT);
				}
			} else if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_RIGHT)) {
				if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
					box.addAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT);
				}
			}

			// 明确的需要拉升左边
			if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_STRETCH_LEFT)) {
				builder.appendElementExcludeAdvise(adviseElement);
				builder.appendElement(builder.mCommonGlue);
				builder.appendElementExcludeAdvise(adviseElement);
			} else {
				// 看情况是否要填充空格
				// 找真实的解析buffer，看当前token在原文中是否有空格，如果有，且没有要求squish，那么就要填充空格
				Token realPrev = stream.tryGet(state, -1);
				if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_LEFT)) {
					if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
						builder.appendElementExcludeAdvise(adviseElement);
						builder.appendElement(SymbolGlue.obtain(box));
						builder.appendElementExcludeAdvise(adviseElement);
					}
				} else if (getTokenTypeSafe(realPrev) == Token.TYPE_BLANK &&
						(getTokenAttributeSafe(current) & Token.SYMBOL_TYPEFACE_MASK) == 0) {
					builder.appendElementExcludeAdvise(adviseElement);
					builder.appendElement(builder.mCommonGlue);
					builder.appendElementExcludeAdvise(adviseElement);
				} else {
					builder.appendElement(adviseElement);
				}
			}

			builder.appendElement(box);

			accept(current);
		}

		private void preformState1(ParagraphBuilderInternal builder, Token accepted, Token current,
								   CharSequence text, Paragraph.Builder.SpanReader spanReader,
								   TokenStream stream, int state) {
			// advance penalty state table
			//--------------------v current -------------------------
			// 					avoid-header	avoid-tail  none
			//--------------|------------------------------------
			// avoid-header |   fb-brk			advise_brk	advise_brk
			// avoid-tail 	|	fb-brk			fb-brk		fb-brk
			// none 		|	fb-brk			advise_brk	advise_brk
			//---------------------------------------------------
			//  ^ prev
			Element adviseElement = null;
			if (checkTokenAttributeSafe(accepted, Token.SYMBOL_KINSOKU_MASK, Token.SYMBOL_KINSOKU_AVOID_TAIL) ||
					checkTokenAttributeSafe(current, Token.SYMBOL_KINSOKU_MASK, Token.SYMBOL_KINSOKU_AVOID_HEADER)) {
				adviseElement = Penalty.FORBIDDEN_BREAK;
			} else {
				adviseElement = Penalty.ADVISE_BREAK;
			}

			TextBox box = builder.obtainSymbolTextBox(text, spanReader, current);
			if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_LEFT)) {
				if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
					box.addAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT);
				}
			} else if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_RIGHT)) {
				if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
					box.addAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT);
				}
			}

			// 添加 advise
			preformState1Advise(builder, accepted, current, stream, state, box, adviseElement);

			builder.appendElement(box);

			accept(current);
		}

		/**
		 * 标点挤压逻辑
		 */
		private void preformState1Advise(ParagraphBuilderInternal builder,
										 Token accepted, Token current,
										 TokenStream stream, int state,
										 TextBox box, Element adviseElement) {
			// 之前没有吞入任何元素
			if (accepted == null) {
				return;
			}

			// 已经推入的符号现在都是没有额外空格的
			// padding state table
			//--------------------------v current ----------------------------------------------------------------------------
			// 						|	stretch-left('<')	squish-left('《') 	stretch-right('>') 	squish-right('》') 	none
			//----------------------|-----------------------------------------------------------------------------------------
			// stretch-right('>')	|   padding				padding  			noop				raw					raw
			// squish-right('》') 	|	padding				symbol-padding		noop				noop				raw
			// stretch-left('<')    |	noop				noop 				raw				    raw					raw
			// squish-left('《')   	|	raw					noop				raw					raw					raw
			// none 				|	raw					raw					raw					raw					raw
			//----------------------------------------------------------------------------------------------------------------
			// ^ accepted

			if (checkTokenAttributeSafe(accepted, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_STRETCH_RIGHT)) {

				if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_STRETCH_LEFT) ||
						checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_LEFT)) {
					performState1AdvisePadding(builder, adviseElement);
					return;
				}

				if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_STRETCH_RIGHT)) {
					performState1AdviseNoop(builder, adviseElement);
					return;
				}

				performState1AdviseRaw(builder, stream, state, adviseElement);
				return;
			}

			if (checkTokenAttributeSafe(accepted, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_RIGHT)) {

				if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_STRETCH_LEFT)) {
					performState1AdvisePadding(builder, adviseElement);
					return;
				}

				if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_LEFT)) {
					performState1AdviseSymbolPadding(builder, box, adviseElement);
					return;
				}

				if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_STRETCH_RIGHT) ||
						checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_RIGHT)) {
					performState1AdviseNoop(builder, adviseElement);
					return;
				}

				if ((getTokenAttributeSafe(current) & Token.SYMBOL_TYPEFACE_MASK) != Token.TYPE_NONE) {
					throw new RuntimeException("symbol rule's state 1 advise logic error");
				}

				performState1AdviseRaw(builder, stream, state, adviseElement);
				return;
			}

			if (checkTokenAttributeSafe(accepted, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_STRETCH_LEFT)) {
				if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_STRETCH_LEFT) ||
						checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_LEFT)) {
					performState1AdviseNoop(builder, adviseElement);
					return;
				}

				performState1AdviseRaw(builder, stream, state, adviseElement);
				return;
			}

			if (checkTokenAttributeSafe(accepted, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_LEFT)) {
				if (checkTokenAttributeSafe(current, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_LEFT)) {
					performState1AdviseNoop(builder, adviseElement);
					return;
				}

				performState1AdviseRaw(builder, stream, state, adviseElement);
				return;
			}

			if ((getTokenAttributeSafe(accepted) & Token.SYMBOL_TYPEFACE_MASK) != Token.TYPE_NONE) {
				throw new RuntimeException("symbol rule's state 1 advise logic error");
			}

			performState1AdviseRaw(builder, stream, state, adviseElement);
		}

		private void performState1AdviseNoop(ParagraphBuilderInternal builder, Element adviseElement) {
			builder.appendElement(adviseElement);
		}

		private void performState1AdviseRaw(ParagraphBuilderInternal builder,
											TokenStream stream, int state, Element adviseElement) {
			// noop 后续多了一些操作，要保留原始数据中的空格
			// 但是不能是 开头、有压缩的需求
			Token realPrev = stream.tryGet(state, -1);
			if (getTokenTypeSafe(realPrev) == Token.TYPE_BLANK) {
				builder.appendElementExcludeAdvise(adviseElement);
				builder.appendElement(builder.mCommonGlue);
				builder.appendElementExcludeAdvise(adviseElement);
				return;
			}

			builder.appendElement(adviseElement);
		}

		private void performState1AdviseSymbolPadding(ParagraphBuilderInternal builder,
													  TextBox box, Element adviseElement) {
			if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
				builder.appendElementExcludeAdvise(adviseElement);
				builder.appendElement(SymbolGlue.obtain(box));
				builder.appendElementExcludeAdvise(adviseElement);
			}
		}

		private void performState1AdvisePadding(ParagraphBuilderInternal builder, Element adviseElement) {
			builder.appendElementExcludeAdvise(adviseElement);
			builder.appendElement(builder.mCommonGlue);
			builder.appendElementExcludeAdvise(adviseElement);
		}
	}

	private static class UnknownRules extends TypesetRule {

		@Override
		public boolean perform0(ParagraphBuilderInternal builder, Token accepted,
								TokenStream stream,
								CharSequence text,
								Paragraph.Builder.SpanReader spanReader) {
			int state = stream.save();
			Token current = stream.next();
			if (current.getType() != Token.TYPE_UNKNOWN) {
				return false;
			}

			// 1: word -> glue
			// 2: unknown -> advise_brk
			// 3: blank -> noop
			// 4: none -> noop
			// 5: symbol -> prefix state 1
			int prevType = getTokenTypeSafe(accepted);
			if (prevType == Token.TYPE_WORD) {
				builder.appendElement(builder.mCommonGlue);
			} else if (prevType == Token.TYPE_UNKNOWN) {
				builder.appendElement(Penalty.ADVISE_BREAK);
			} else if (prevType == Token.TYPE_SYMBOL) {
				performPrefixState1(builder, accepted, stream, state);
			}

			builder.appendUnknownToken(text, spanReader, current);

			accept(current);
			return true;
		}

		private static void performPrefixState1(ParagraphBuilderInternal builder, Token accepted, TokenStream stream, int state) {
			// 先获取建议
			Element adviseElement = checkTokenAttributeSafe(accepted, Token.SYMBOL_KINSOKU_MASK, Token.SYMBOL_KINSOKU_AVOID_TAIL) ?
					Penalty.FORBIDDEN_BREAK : Penalty.ADVISE_BREAK;

			// 是否添加空格取决于前一个符号
			// 如果它要，那么就添加
			// 不要的话尝试用原先单词流中的数据填充，不过要注意，如果前面的单词不让填充空格，那么也是什么都不能做的

			if (checkTokenAttributeSafe(accepted, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_STRETCH_RIGHT)) {
				builder.appendElementExcludeAdvise(adviseElement);
				builder.appendElement(builder.mCommonGlue);
				builder.appendElementExcludeAdvise(adviseElement);
			} else if (checkTokenAttributeSafe(accepted, Token.SYMBOL_TYPEFACE_MASK, Token.SYMBOL_SQUISH_RIGHT)) {
				if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
					builder.appendElementExcludeAdvise(adviseElement);
					builder.appendElement(obtainSymbolGlueFromStack(builder));
					builder.appendElementExcludeAdvise(adviseElement);
				}
			} else {
				Token realPrev = stream.tryGet(state, -1);
				if (realPrev != accepted && getTokenTypeSafe(realPrev) == Token.TYPE_BLANK &&
						(getTokenAttributeSafe(accepted) & Token.SYMBOL_SQUISH_MASK) == 0) {
					builder.appendElementExcludeAdvise(adviseElement);
					builder.appendElement(builder.mCommonGlue);
					builder.appendElementExcludeAdvise(adviseElement);
				} else {
					builder.appendElement(adviseElement);
				}
			}
		}
	}

	private static Glue obtainSymbolGlueFromStack(ParagraphBuilderInternal builder) {
		Element last = builder.mParagraph.mElements.get(builder.mParagraph.mElements.size() - 1);
		if (BuildConfig.DEBUG && !(last instanceof TextBox)) {
			throw new IllegalStateException("last element must be TextBox");
		}

		TextBox textBox = (TextBox) last;
		if (BuildConfig.DEBUG && textBox.getAttribute() == 0) {
			throw new IllegalStateException("TextBox's attribute must be set");
		}

		return SymbolGlue.obtain(textBox);
	}
}
