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

	private Token mLastToken;
	private boolean mAppendSpaceEnable = true;

	private Glue mCommonGlue;
	private Glue mStretchOnlyGlue;

	public ParagraphBuilderInternal(Paragraph.Builder builder) {
		mSpanBuilder = new Paragraph.SpanBuilder(builder);
	}

	public void lineSpacingExtra(float lineSpace) {
		mParagraph.mLayout.getAdvise().setLineSpacingExtra(lineSpace);
	}

	public void breakStrategy(BreakStrategy breakStrategy) {
		mParagraph.mLayout.getAdvise().setBreakStrategy(breakStrategy);
	}

	public void appendSpaceEnable(boolean enable) {
		mAppendSpaceEnable = enable;
	}

	public void textGravity(int gravity) {
		mParagraph.mLayout.getAdvise().setTextGravity(gravity);
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

	
	public void emoticon(Emoticon emoticon) {
		if (mParagraph == null) {
			throw new IllegalStateException("call newParagraph first");
		}

		appendEmoticon(emoticon);
	}

	
	public Paragraph build(boolean brk) {
		if (brk) {
			brk();
		}

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
		mLastToken = null;
		mAppendSpaceEnable = true;
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
		mParagraph.mLayout.getAdvise().copy(mRenderOption);
		mCommonGlue = Glue.obtain(mTextAttribute);
		mStretchOnlyGlue = Glue.obtain(
				0, 0, mTextAttribute.getSpaceStretch(), 0
		);
		mLastToken = null;
		mAppendSpaceEnable = true;
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


		TokenStream tokenStream = TokenStream.obtain(text, start, end, rtl);
		try {



			if (mAppendSpaceEnable && mLastToken != null && tokenStream.hasNext()) {
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


			if (cjkOptimization) {
				textBox.addAttribute(TextBox.ATTRIBUTE_ZOOM_OUT);
			}

			appendElement(textBox);
		}

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
			appendElement(Glue.obtain(mTextAttribute.getSpaceWidth() * 4, 0, 0, 0));
			return;
		}

		appendElement(mCommonGlue);
	}

	
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


		TYPESET_RULES = new ArrayList<>();
		TYPESET_RULES.add(new WordRules());
		TYPESET_RULES.add(new SymbolRules());
		TYPESET_RULES.add(new ControlRules());
	}

	
	private Token accept(@Nullable Token accepted, 
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



			if (accepted.getCategory() != current.getCategory()) {
				builder.appendElement(builder.mCommonGlue);
			} else {
				builder.appendElement(Penalty.ADVISE_BREAK);
			}
		}

		private static void performPrefixState1(ParagraphBuilderInternal builder, @NonNull Token accepted, TokenStream stream, int state) {

			Element adviseElement = checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL) ?
					Penalty.FORBIDDEN_BREAK : Penalty.ADVISE_BREAK;





			if (checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT)) {
				builder.appendElementExcludeAdvise(adviseElement);
				builder.appendElement(builder.mCommonGlue);
				builder.appendElementExcludeAdvise(adviseElement);
			} else if (checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {
				if (builder.mRenderOption.isFullWithSymbolOptimizationEnable()) {
					builder.appendElementExcludeAdvise(adviseElement);
					builder.appendElement(obtainSymbolGlueFromStack(builder));
					builder.appendElementExcludeAdvise(adviseElement);
				}
			} else {

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












			int prevType = getTokenTypeSafe(accepted);
			if (prevType == Token.TYPE_NONE) {
				TextBox textBox = builder.appendSymbolToken(text, spanReader, current);
				if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
					if (builder.mRenderOption.isFullWithSymbolOptimizationEnable()) {
						textBox.addAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT);
					}
				} else if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {
					if (builder.mRenderOption.isFullWithSymbolOptimizationEnable()) {
						textBox.addAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT);
					}
				}
				accept(current);
				return true;
			}

			if (prevType == Token.TYPE_CONTROL) {

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



			Element adviseElement = checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER) ?
					Penalty.FORBIDDEN_BREAK : Penalty.ADVISE_BREAK;


			TextBox box = builder.obtainSymbolTextBox(text, spanReader, current);
			if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
				if (builder.mRenderOption.isFullWithSymbolOptimizationEnable()) {
					box.addAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT);
				}
			} else if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {
				if (builder.mRenderOption.isFullWithSymbolOptimizationEnable()) {
					box.addAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT);
				}
			}


			if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT)) {
				builder.appendElementExcludeAdvise(adviseElement);
				builder.appendElement(builder.mCommonGlue);
				builder.appendElementExcludeAdvise(adviseElement);
			} else {



				Token realPrev = stream.tryGet(state, -1);
				if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
					if (builder.mRenderOption.isFullWithSymbolOptimizationEnable()) {
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









			Element adviseElement = null;
			if (checkSymbolTokenAttributeSafe(accepted, Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL) ||
					checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER)) {
				adviseElement = Penalty.FORBIDDEN_BREAK;
			} else {
				adviseElement = Penalty.ADVISE_BREAK;
			}

			TextBox box = builder.obtainSymbolTextBox(text, spanReader, current);
			if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)) {
				if (builder.mRenderOption.isFullWithSymbolOptimizationEnable()) {
					box.addAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT);
				}
			} else if (checkSymbolTokenAttributeSafe(current, Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {
				if (builder.mRenderOption.isFullWithSymbolOptimizationEnable()) {
					box.addAttribute(TextBox.ATTRIBUTE_SQUISH_RIGHT);
				}
			}


			preformState1Advise(builder, accepted, current, stream, state, box, adviseElement);

			builder.appendElement(box);

			accept(current);
		}

		
		private void preformState1Advise(ParagraphBuilderInternal builder,
										 Token accepted, Token current,
										 TokenStream stream, int state,
										 TextBox box, Element adviseElement) {

			if (accepted == null) {
				return;
			}














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
			if (builder.mRenderOption.isFullWithSymbolOptimizationEnable()) {
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
