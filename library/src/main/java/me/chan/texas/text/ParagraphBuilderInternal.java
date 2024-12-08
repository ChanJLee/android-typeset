package me.chan.texas.text;

import static me.chan.texas.text.Paragraph.TYPESET_POLICY_CJK_MIX_OPTIMIZATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.ibm.icu.text.Bidi;

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
import me.chan.texas.utils.TexasUtils;

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

	public void reset(TexasOption texasOption) {
		mRenderOption = texasOption.getRenderOption();
		mMeasurer = texasOption.getMeasurer();
		mHyphenation = texasOption.getHyphenation();
		mTextAttribute = texasOption.getTextAttribute();
		mParagraph = Paragraph.obtain();
		mParagraph.mLayout = Layout.obtain();
		mCommonGlue = Glue.obtain(mTextAttribute);
		mStretchOnlyGlue = Glue.obtain(
				0, 0, mTextAttribute.getSpaceStretch(), 0
		);
		mLastToken = null;
	}

	public void addTypesetPolicy(int policy) {
		mParagraph.mLayout.getAdvise().addTypesetPolicy(policy);
	}

	public void clearTypesetPolicy() {
		mParagraph.mLayout.getAdvise().clearTypesetPolicy();
	}

	private void appendEmoticon(Emoticon emoticon) {
		Token token = Token.obtainOtherWord();
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

		Layout.Advise advise = mParagraph.mLayout.getAdvise();
		if (!advise.checkTypesetPolicy(Paragraph.TYPESET_POLICY_BIDI_TEXT)) {
			appendRun(text, start, end, reader, false);
			return;
		}

		char[] buffer = TextBox.CHAR_ARRAY_POOL.obtain(end - start);
		TexasUtils.getChars(text, start, end, buffer, 0);
		Bidi bidi = new Bidi(buffer, 0, null, 0, end - start, Bidi.LEVEL_DEFAULT_LTR);
		for (int i = 0; i < bidi.getRunCount(); ++i) {
			int runStart = bidi.getRunStart(i);
			int runLimit = bidi.getRunLimit(i);
			boolean rtl = bidi.getRunLevel(i) % 2 != 0;
			appendRun(text, start + runStart, start + runLimit, reader, rtl);
		}
		TextBox.CHAR_ARRAY_POOL.release(buffer);
	}

	private void appendRun(CharSequence text, int start, int end,
						   @Nullable Paragraph.Builder.SpanReader reader, boolean rtl) {
		// 将句子转换为单词流
		// 单词流会分析出一个句子中每个字符所代表的语义，这样可以精确的识别诸如： isn't、1920s 为一个单词
		TokenStream tokenStream = TokenStream.obtain(text, start, end, rtl);
		try {

			// 追加一个空格
			// 这个未来还能不能适用，就要看状态推导图了，目前看一个token后接blank和none不影响状态机的跳转
			if (mLastToken != null && tokenStream.hasNext()) {
				tokenStream = TokenStream.link(TokenStream.obtain(" ", 0, 1), tokenStream);
			}

			Token prev = mLastToken;
			appendRun0(text, reader, tokenStream);
			if (prev != mLastToken && prev != null) {
				prev.recycle();
			}
		} finally {
			tokenStream.recycle();
		}
	}

	private void appendRun0(CharSequence text,
							Paragraph.Builder.SpanReader spanReader,
							TokenStream tokenStream) {
		while (tokenStream.hasNext()) {
			mLastToken = accept(mLastToken, tokenStream, text, spanReader);
		}
	}

	private void appendWordToken(CharSequence text,
								 Paragraph.Builder.SpanReader spanReader,
								 Token token) {
		int category = token.getCategory();
		if (category == Token.CATEGORY_NORMAL) {
			appendAsciiWordToken(text, spanReader, token);
		} else if (category == Token.CATEGORY_CJK) {
			appendCjkWordToken(text, spanReader, token);
		} else {
			appendWordTokenDirect(text, spanReader, token);
		}
	}

	private void appendWordTokenDirect(CharSequence text,
									   Paragraph.Builder.SpanReader spanReader,
									   Token token) {
		int start = token.getStart();
		int end = token.getEnd();
		Paragraph.Span span = null;
		if (spanReader != null) {
			span = spanReader.read(token);
		}

		TextStyle textStyle = null;
		Object tag = null;
		Appearance background = null;
		Appearance foreground = null;
		if (span != null) {
			TextStyles styles = span.mStyles;
			textStyle = styles.getTextStyle();
			tag = span.mTag;
			background = styles.getBackground();
			foreground = styles.getForeground();
		}

		TextBox textBox = TextBox.obtain(text, start, end,
				mMeasurer, textStyle,
				tag,
				background,
				foreground);

		if (token.isRtl()) {
			textBox.addAttribute(TextBox.ATTRIBUTE_RTL);
		}

		appendElement(textBox);

		if (span != null) {
			span.recycle();
		}
	}

	private void appendAsciiWordToken(CharSequence text,
									  Paragraph.Builder.SpanReader spanReader,
									  Token token) {
		Paragraph.Span span = null;
		if (spanReader != null) {
			span = spanReader.read(token);
		}

		TextStyle textStyle = null;
		Object tag = null;
		Appearance background = null;
		Appearance foreground = null;
		if (span != null) {
			TextStyles styles = span.mStyles;
			textStyle = styles.getTextStyle();
			tag = span.mTag;
			background = styles.getBackground();
			foreground = styles.getForeground();
		}

		appendEnText(text, token.getStart(), token.getEnd(), textStyle, tag, background, foreground);

		if (span != null) {
			span.recycle();
		}
	}

	private void appendCjkWordToken(CharSequence text,
									Paragraph.Builder.SpanReader spanReader,
									Token token) {
		Layout layout = mParagraph.getLayout();
		Layout.Advise advise = layout.getAdvise();
		boolean cjkOptimization = advise.checkTypesetPolicy(TYPESET_POLICY_CJK_MIX_OPTIMIZATION);
		Element linkElement = cjkOptimization ? Penalty.ADVISE_BREAK : mStretchOnlyGlue;

		Paragraph.Span span = null;
		if (spanReader != null) {
			span = spanReader.read(token);
		}

		for (int i = token.getStart(); i < token.getEnd(); ++i) {
			if (i != token.getStart()) {
				appendElement(linkElement);
			}

			int start = i;
			int end = i + 1;

			TextStyle textStyle = null;
			Object tag = null;
			Appearance background = null;
			Appearance foreground = null;
			if (span != null) {
				TextStyles styles = span.mStyles;
				textStyle = styles.getTextStyle();
				tag = span.mTag;
				background = styles.getBackground();
				foreground = styles.getForeground();
			}

			TextBox textBox = TextBox.obtain(text, start, end,
					mMeasurer, textStyle,
					tag,
					background,
					foreground);

			// 英文模式下 要对中文进行缩放
			if (cjkOptimization) {
				textBox.addAttribute(TextBox.ATTRIBUTE_ZOOM_OUT);
			}

			appendElement(textBox);
		}

		// TODO unit test
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
			span = spanReader.read(token);
		}

		TextStyle textStyle = null;
		Object tag = null;
		Appearance background = null;
		Appearance foreground = null;
		if (span != null) {
			TextStyles styles = span.mStyles;
			textStyle = styles.getTextStyle();
			tag = span.mTag;
			background = styles.getBackground();
			foreground = styles.getForeground();
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

	private void appendControlToken(Token token) {
		Layout.Advise advise = mParagraph.mLayout.getAdvise();
		if (!advise.checkTypesetPolicy(Paragraph.TYPESET_POLICY_ACCEPT_CONTROL_CHAR)) {
			appendElement(mCommonGlue);
			return;
		}

		if (token.checkAttribute(Token.CONTROL_ATTRIBUTE_SPACE)) {
			appendElement(mCommonGlue);
			return;
		}
		if (token.checkAttribute(Token.CONTROL_ATTRIBUTE_NEW_LINE)) {
			appendElement(Glue.TERMINAL);
			appendElement(Penalty.FORCE_BREAK);
			return;
		}

		if (token.checkAttribute(Token.CONTROL_ATTRIBUTE_TAB_HORIZONTAL)) {
			// todo support tab
			appendElement(mCommonGlue);
			return;
		}

		// TODO how to fix it.
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
			return perform0(builder, accepted, stream, text, spanReader);
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

	private static boolean hasSymbolTypefaceAttributesSafe(Token token) {
		return token != null && token.hasSymbolTypefaceAttributes();
	}

	private static boolean checkSymbolTokenAttributeSafe(Token token,
														 @Token.SymbolTokenAttribute int attr) {
		return token != null && token.getType() == Token.TYPE_SYMBOL && token.checkAttribute(attr);
	}

	static {
		// control 实际上可以约减掉了，但是因为为了后期好理解所以保留
		// 因为control存在，我们需要关心下一个token是什么，所以如果control删除，那么rules的api接口就要修改
		TYPESET_RULES = new ArrayList<>();
		TYPESET_RULES.add(new WordRules());
		TYPESET_RULES.add(new SymbolRules());
		TYPESET_RULES.add(new ControlRules());
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
				stream.restore(state);
				current.recycle();
				return false;
			}

			// 1: word -> state 2
			// 2: control -> noop
			// 3: none -> noop
			// 4: symbol -> prefix state 1
			int acceptedType = getTokenTypeSafe(accepted);
			if (acceptedType == Token.TYPE_WORD) {
				performPrefixState2(builder, accepted, current);
			} else if (acceptedType == Token.TYPE_SYMBOL) {
				performPrefixState1(builder, accepted, stream, state);
			}

			builder.appendWordToken(text, spanReader, current);

			accept(current);
			return true;
		}

		private static void performPrefixState2(ParagraphBuilderInternal builder, @NonNull Token accepted, Token current) {
			// 其实就是不同文字类型之间分割
			// 比如字母和数字之间加空格
			// 否则就是可以断点
			if (accepted.getCategory() != current.getCategory()) {
				builder.appendElement(builder.mCommonGlue);
			} else {
				builder.appendElement(Penalty.ADVISE_BREAK);
			}
		}

		private static void performPrefixState1(ParagraphBuilderInternal builder, @NonNull Token accepted, TokenStream stream, int state) {
			// 先获取建议
			Element adviseElement = checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL) ?
					Penalty.FORBIDDEN_BREAK : Penalty.ADVISE_BREAK;

			// 是否添加空格取决于前一个符号
			// 如果它要，那么就添加
			// 不要的话尝试用原先单词流中的数据填充，不过要注意，如果前面的单词不让填充空格，那么也是什么都不能做的

			if (checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT)) {
				builder.appendElementExcludeAdvise(adviseElement);
				builder.appendElement(builder.mCommonGlue);
				builder.appendElementExcludeAdvise(adviseElement);
			} else if (checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {
				if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
					builder.appendElementExcludeAdvise(adviseElement);
					builder.appendElement(obtainSymbolGlueFromStack(builder));
					builder.appendElementExcludeAdvise(adviseElement);
				}
			} else {
				// 那就要看原始的流中有没有要求添加空格
				Token realPrev = stream.tryGet(state, -1);
				if (realPrev != accepted &&
						getTokenTypeSafe(realPrev) == Token.TYPE_CONTROL &&
						!hasSymbolTypefaceAttributesSafe(accepted)) {
					builder.appendElementExcludeAdvise(adviseElement);
					builder.appendElement(builder.mCommonGlue);
					builder.appendElementExcludeAdvise(adviseElement);
				} else {
					builder.appendElement(adviseElement);
				}
			}
		}
	}

	private static class ControlRules extends TypesetRule {

		@Override
		public boolean perform0(ParagraphBuilderInternal builder, Token accepted,
								TokenStream stream,
								CharSequence text,
								Paragraph.Builder.SpanReader spanReader) {
			int state = stream.save();
			Token current = stream.next();
			if (current.getType() != Token.TYPE_CONTROL) {
				stream.restore(state);
				current.recycle();
				return false;
			}

			Layout.Advise advise = builder.mParagraph.mLayout.getAdvise();
			if (advise.checkTypesetPolicy(Paragraph.TYPESET_POLICY_ACCEPT_CONTROL_CHAR) &&
					current.checkAttribute(Token.CONTROL_ATTRIBUTE_NEW_LINE)) {
				builder.appendControlToken(current);
				accept(Token.obtain());
				return true;
			}

			Token next = stream.tryGet(0);
			//--------------------v next ----------
			// 				word		symbol		control		none
			//----------|-----------------------------
			// word    	|   direct 		trans		noop		noop
			// symbol 	|   trans		trans		noop		noop < 本质上都是丢给后面的人处理
			// control	|	noop		noop		noop		noop < 规避多个空格
			// none		|   noop  		noop		noop		noop < 首行不留白，这个可能作为 future 未来支持开启
			//-----------------------------------------
			//  ^ prev
			// trans 就要上面的规则去检查是否丢弃了 control，但是当前的规则不需要处理，所以本质上是noop

			int prevType = getTokenTypeSafe(accepted);
			int nextType = getTokenTypeSafe(next);

			if (prevType == Token.TYPE_WORD) {
				if (nextType == Token.TYPE_WORD) {
					appendControlToken(builder, current);
					accept(current);
					return true;
				}

				accept(accepted);
				return true;
			}

			if (prevType == Token.TYPE_SYMBOL ||
					prevType == Token.TYPE_CONTROL ||
					prevType == Token.TYPE_NONE) {
				accept(accepted);
				return true;
			}

			throw new IllegalStateException("control's rules under invalid state");
		}

		private void appendControlToken(ParagraphBuilderInternal builder, Token token) {
			if (token.checkAttribute(Token.CONTROL_ATTRIBUTE_SPACE)) {
				builder.appendElement(builder.mCommonGlue);
			} else if (token.checkAttribute(Token.CONTROL_ATTRIBUTE_TAB_HORIZONTAL)) {
				// todo support tab
				builder.appendElement(builder.mCommonGlue);
			}
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
				stream.restore(state);
				current.recycle();
				return false;
			}

			//--------------------v next ----------
			// 				word	unknown		symbol		control		none
			//----------|-----------------------------
			// word    	|   state2  state2		state2		state2		state2
			// unknown 	|   state2  state2		state2		state2		state2
			// symbol 	|   state1	state1		state1		state1		state1
			// control	|	-		-			-			-			-
			// none		|   直接进 	直接进 		直接进		直接进		直接进
			//-----------------------------------------
			//  ^ prev

			int prevType = getTokenTypeSafe(accepted);
			if (prevType == Token.TYPE_NONE) {
				TextBox textBox = builder.appendSymbolToken(text, spanReader, current);
				if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
					if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
						textBox.addAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT);
					}
				} else if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {
					if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
						textBox.addAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT);
					}
				}
				accept(current);
				return true;
			}

			if (prevType == Token.TYPE_CONTROL) {
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

			Element adviseElement = checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER) ?
					Penalty.FORBIDDEN_BREAK : Penalty.ADVISE_BREAK;

			// 生成一个 symbol
			TextBox box = builder.obtainSymbolTextBox(text, spanReader, current);
			if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
				if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
					box.addAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT);
				}
			} else if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {
				if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
					box.addAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT);
				}
			}

			// 明确的需要拉升左边
			if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT)) {
				builder.appendElementExcludeAdvise(adviseElement);
				builder.appendElement(builder.mCommonGlue);
				builder.appendElementExcludeAdvise(adviseElement);
			} else {
				// 看情况是否要填充空格
				// 找真实的解析buffer，看当前token在原文中是否有空格，如果有，且没有要求squish，那么就要填充空格
				// 但是填充空格的时候需要收到当前current的约束
				Token realPrev = stream.tryGet(state, -1);
				if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
					if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
						builder.appendElementExcludeAdvise(adviseElement);
						builder.appendElement(SymbolGlue.obtain(box));
						builder.appendElementExcludeAdvise(adviseElement);
					}
				} else if (getTokenTypeSafe(realPrev) == Token.TYPE_CONTROL && !hasSymbolTypefaceAttributesSafe(current)) {
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
			if (checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL) ||
					checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER)) {
				adviseElement = Penalty.FORBIDDEN_BREAK;
			} else {
				adviseElement = Penalty.ADVISE_BREAK;
			}

			TextBox box = builder.obtainSymbolTextBox(text, spanReader, current);
			if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
				if (builder.mRenderOption.isEnableFullWithSymbolOptimization()) {
					box.addAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT);
				}
			} else if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {
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

			if (checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT)) {

				if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT) ||
						checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
					performState1AdvisePadding(builder, adviseElement);
					return;
				}

				if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT)) {
					performState1AdviseNoop(builder, adviseElement);
					return;
				}

				performState1AdviseRaw(builder, stream, state, adviseElement);
				return;
			}

			if (checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {

				if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT)) {
					performState1AdvisePadding(builder, adviseElement);
					return;
				}

				if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
					performState1AdviseSymbolPadding(builder, box, adviseElement);
					return;
				}

				if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT) ||
						checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {
					performState1AdviseNoop(builder, adviseElement);
					return;
				}

				if (hasSymbolTypefaceAttributesSafe(current)) {
					throw new RuntimeException("symbol rule's state 1 advise logic error");
				}

				performState1AdviseRaw(builder, stream, state, adviseElement);
				return;
			}

			if (checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT)) {
				if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT) ||
						checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
					performState1AdviseNoop(builder, adviseElement);
					return;
				}

				performState1AdviseRaw(builder, stream, state, adviseElement);
				return;
			}

			if (checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
				if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
					performState1AdviseNoop(builder, adviseElement);
					return;
				}

				performState1AdviseRaw(builder, stream, state, adviseElement);
				return;
			}

			if (hasSymbolTypefaceAttributesSafe(accepted)) {
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
			if (getTokenTypeSafe(realPrev) == Token.TYPE_CONTROL) {
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
