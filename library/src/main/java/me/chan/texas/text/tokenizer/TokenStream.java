package me.chan.texas.text.tokenizer;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.Texas;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.icu.UnicodeUtils;
import me.chan.texas.utils.CharStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// sent ::= unit*
// unit ::= <symbol> | <word> | <blank> | <未知字符>
// word ::= cn | latin
// cn ::= <中文字>+
// latin ::= <0-9 拉丁字母 拉丁链接符号>*
// symbol ::= single symbol | multi symbol
// single symbol ::= UnicodeUtils#isSymbolsAndPunctuation
// blank ::= UnicodeUtils#isBreakTokenSymbol
// multi symbol ::= ...

/**
 * 大致划分出英文半角 中文 全角 空格 未知字符
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TokenStream extends DefaultRecyclable {
	private static final ObjectPool<TokenStream> POOL = new ObjectPool<>(4);

	private final List<Token> mBuffer = new ArrayList<>();

	private final CharStream mCharStream = new CharStream();

	private int mIndex = 0;

	private Tokenizer mTokenizer = null;

	private TokenStream() {
	}

	public int state() {
		return mIndex;
	}

	public void restore(int state) {
		mIndex = state;
	}

	@Nullable
	public Token tryGet(int state, int step) {
		int index = state + step;
		if (index < 0 || index >= mBuffer.size()) {
			return null;
		}

		return mBuffer.get(index);
	}

	private void sent(boolean preferLatin) {
		while (!mCharStream.eof()) {
			int state = mCharStream.save();
			if (!unit(preferLatin) || state == mCharStream.save()) {
				throw new IllegalStateException("parse state error");
			}
		}
	}

	private boolean unit(boolean preferLatin) {
		return word(preferLatin) || symbol() || blank() || unknown();
	}

	private boolean unknown() {
		if(context() || surrogate()) {
			return true;
		}

		int save = mCharStream.save();
		mCharStream.eat();
		Token token = Token.obtain();
		token.mCharSequence = mCharStream.getText();
		token.mStart = save;
		token.mEnd = save + 1;
		token.mType = Token.TYPE_UNKNOWN;
		return mBuffer.add(token);
	}

	private boolean context() {
		int save = mCharStream.save();
		int codePoint = mCharStream.eat();
		if (!UnicodeUtils.isContextSensitiveCharacter(codePoint)) {
			mCharStream.restore(save);
			return false;
		}

		Token token = Token.obtain();
		token.mCharSequence = mCharStream.getText();
		token.mStart = save;
		token.mEnd = save + 1;
		token.mType = Token.TYPE_UNKNOWN;
		token.mAttributes |= Token.WORD_TYPE_CONTEXT_SENSITIVE;

		while (!mCharStream.eof()) {
			codePoint = mCharStream.eat();
			if (!UnicodeUtils.isContextSensitiveCharacter((char) codePoint)) {
				mCharStream.adjust(-1);
				return mBuffer.add(token);
			}
			token.mEnd += 1;
		}

		return mBuffer.add(token);
	}

	private boolean surrogate() {
		int save = mCharStream.save();
		Token token = Token.obtain();
		token.mCharSequence = mCharStream.getText();
		token.mStart = save;
		token.mEnd = save;
		token.mType = Token.TYPE_UNKNOWN;

		while (!mCharStream.eof()) {
			int codePoint = mCharStream.eat();
			// TODO unit test
			if (!Character.isHighSurrogate((char) codePoint) || mCharStream.eof()) {
				mCharStream.adjust(-1);
				break;
			}

			// TODO unit test
			codePoint = mCharStream.eat();
			if (!Character.isLowSurrogate((char) codePoint)) {
				mCharStream.adjust(-2);
				break;
			}

			token.mEnd += 2;
		}

		if (token.mStart == token.mEnd) {
			token.recycle();
			return false;
		}

		return mBuffer.add(token);
	}

	private boolean symbol() {
		int save = mCharStream.save();
		int codePoint = mCharStream.eat();
		if (!UnicodeUtils.isSymbolsAndPunctuation(codePoint) ||
				UnicodeUtils.isBreakTokenSymbol(codePoint)) {
			mCharStream.restore(save);
			return false;
		}

		// maybe single symbol
		Token token = Token.obtain();
		token.mCharSequence = mCharStream.getText();
		token.mStart = save;
		token.mEnd = save + 1;
		token.mType = Token.TYPE_SYMBOL;

		// multi symbol
		if (codePoint == '.' &&
				mCharStream.tryCheck(0, '.') &&
				mCharStream.tryCheck(1, '.')) {
			mCharStream.adjust(2);
			token.mEnd = save + 3;
			token.mAttributes = Token.SYMBOL_KINSOKU_AVOID_HEADER;
		} else {
			token.mAttributes =
					getStretchAdvise(codePoint) |
							getSquishAdvise(codePoint) |
							getKinsokuAdvise(codePoint);
		}

		return mBuffer.add(token);
	}

	private boolean word(boolean preferLatin) {
		/* latin的查找要比cn慢很多 */
		if (preferLatin) {
			return latin() || cn();
		}

		return cn() || latin();
	}

	private boolean cn() {
		int save = mCharStream.save();
		int codePoint = mCharStream.eat();
		if (!UnicodeUtils.isCn(codePoint)) {
			mCharStream.restore(save);
			return false;
		}

		Token token = Token.obtain();
		token.mCharSequence = mCharStream.getText();
		token.mStart = save;
		token.mEnd = save + 1;
		token.mType = Token.TYPE_WORD;
		token.mAttributes = Token.WORD_TYPE_CN;

		while (!mCharStream.eof()) {
			codePoint = mCharStream.eat();
			if (!UnicodeUtils.isCn(codePoint)) {
				mCharStream.adjust(-1);
				break;
			}

			++token.mEnd;
		}

		return mBuffer.add(token);
	}

	/**
	 * Latin字母的符号范围在Unicode中是相对连续的，包括拉丁字母、标点符号、数字符号和其他相关符号。以下是Latin字母的一些常见范围：
	 * 基本拉丁字母（Basic Latin）：U+0000 至 U+007F
	 * <p>
	 * 包括大写和小写的拉丁字母（A-Z，a-z）、阿拉伯数字（0-9）以及常见的标点符号和控制字符。
	 * 拉丁-1补充（Latin-1 Supplement）：U+0080 至 U+00FF
	 * <p>
	 * 包括扩展的拉丁字母、重音符号、特殊字母、货币符号等。
	 * 拉丁扩展字母（Latin Extended-A/B/C/D）：U+0100 至 U+024F
	 * <p>
	 * 包括扩展的拉丁字母，如带重音符号的字母、附加字母、扩展标点符号等。
	 * 拉丁扩展附加（Latin Extended Additional）：U+1E00 至 U+1EFF
	 * <p>
	 * 包括进一步扩展的拉丁字母、附加的重音符号和其他特殊字符。
	 * 这些是一些常见的Latin字母的Unicode范围。请注意，Unicode标准还包含其他拉丁字母的范围，具体取决于特定字母、符号或脚本的需求。
	 * <p>
	 * 如果你需要更详细的Latin字母符号范围，请参考Unicode官方文档中有关拉丁字母的章节，以获得更准确和全面的信息
	 */
	private boolean latin() {
		int save = mCharStream.save();
		int codePoint = mCharStream.eat();
		if (!UnicodeUtils.isDigit(codePoint) &&
				!UnicodeUtils.isLatinLetter(codePoint)) {
			mCharStream.restore(save);
			return false;
		}

		Token token = Token.obtain();
		token.mCharSequence = mCharStream.getText();
		token.mStart = save;
		token.mEnd = save + 1;
		token.mType = Token.TYPE_WORD;
		token.mAttributes = Token.WORD_TYPE_LATIN;

		boolean containsSymbol = false;
		while (!mCharStream.eof()) {
			codePoint = mCharStream.eat();
			if (UnicodeUtils.isLatinLetter(codePoint) ||
					UnicodeUtils.isDigit(codePoint)) {
				++token.mEnd;
				continue;
			}

			if ((codePoint > ' ' && codePoint < 0x7f) /* 快速触发检测逻辑 */ ||
					(codePoint >= 0x00a1 && codePoint < 0x00ff) /* Latin-1 Supplement */ ||
					(codePoint >= 0x0100 && codePoint <= 0x024f) /* Latin Extended-A/B/C/D */ ||
					(codePoint >= 0x1e00 && codePoint <= 0x1eff) /* Latin Extended Additional */) {
				containsSymbol = true;
				++token.mEnd;
				continue;
			}

			// unknown ...
			mCharStream.adjust(-1);
			break;
		}

		if (!containsSymbol) {
			return mBuffer.add(token);
		}

		try {
			if (nlp(mCharStream.getText(), token.getStart(), token.getEnd())) {
				token.recycle();
				return true;
			}
		} catch (Throwable throwable) {
			Log.w("TokenStream", "error ->:" + mCharStream.getText().subSequence(token.mStart, token.mEnd));
			Log.w("TokenStream", throwable);
		}

		return mBuffer.add(token);
	}

	private boolean nlp(CharSequence text, int start, int end) {
		if (mTokenizer == null) {
			return false;
		}

		SpanStream stream = mTokenizer.tokenize(text, start, end);
		Token last = null;

		while (stream.hasNext()) {
			SpanStream.Span span = stream.next();

			Token token = Token.obtain();
			token.mCharSequence = mCharStream.getText();
			token.mStart = start;
			token.mEnd = span.getEnd();

			// 看下首字母
			int codePoint = text.charAt(span.getStart());
			boolean isSymbolsAndPunctuation = UnicodeUtils.isSymbolsAndPunctuation(codePoint);
			// 修正下
			if (isSymbolsAndPunctuation && token.mEnd - token.mStart > 1) {
				isSymbolsAndPunctuation = token.mEnd - token.mStart == 3 && codePoint == '.' &&
						mCharStream.peek(start + 1) == '.' && mCharStream.peek(start + 2) == '.';
			}

			token.mType = isSymbolsAndPunctuation ? Token.TYPE_SYMBOL : Token.TYPE_WORD;

			if (isSymbolsAndPunctuation) {
				if (token.mEnd - token.mStart == 3 && codePoint == '.' &&
						mCharStream.peek(start + 1) == '.' && mCharStream.peek(start + 2) == '.') {
					token.mAttributes = Token.SYMBOL_KINSOKU_AVOID_HEADER;
				} else {
					token.mAttributes =
							getStretchAdvise(codePoint) |
									getSquishAdvise(codePoint) |
									getKinsokuAdvise(codePoint);
				}
			} else {
				token.mAttributes = Token.WORD_TYPE_LATIN;
			}

			start = token.mEnd;

			// 看看能不能把文字合并
			if (!isSymbolsAndPunctuation && last != null && last.mType == Token.TYPE_WORD) {
				last.mEnd = token.mEnd;
				token.recycle();
				continue;
			}

			last = token;
			if (!mBuffer.add(token)) {
				return false;
			}
		}

		return true;
	}

	private boolean blank() {
		int save = mCharStream.save();
		int codePoint = mCharStream.eat();
		if (!UnicodeUtils.isBreakTokenSymbol(codePoint)) {
			mCharStream.restore(save);
			return false;
		}

		Token token = Token.obtain();
		token.mCharSequence = mCharStream.getText();
		token.mStart = save;
		token.mEnd = save + 1;
		token.mType = Token.TYPE_BLANK;

		while (!mCharStream.eof()) {
			codePoint = mCharStream.eat();
			if (!UnicodeUtils.isBreakTokenSymbol(codePoint)) {
				mCharStream.adjust(-1);
				break;
			}

			++token.mEnd;
		}

		return mBuffer.add(token);
	}

	private static Tokenizer getTokenizer() {
		Context context = Texas.getAppContext();
		try {
			return Tokenizer.getInstance(context);
		} catch (IOException e) {
			Log.w("TokenStream", e);
		}
		return null;
	}

	@Nullable
	public static TokenStream read(CharSequence charSequence, int start, int end) {
		return read(charSequence, start, end, true);
	}

	/**
	 * @param charSequence 文本
	 * @param start        开始位置
	 * @param end          结束位置
	 * @param preferLatin  是否优先使用拉丁分词
	 * @return 词法分析结果
	 */
	@Nullable
	public static TokenStream read(CharSequence charSequence, int start, int end, boolean preferLatin) {
		if (charSequence == null || start >= end || charSequence.length() <= start) {
			return null;
		}

		TokenStream tokenStream = obtain(charSequence, start, end);
		tokenStream.sent(preferLatin);

		return tokenStream;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		mIndex = 0;
		mCharStream.clear();
		for (Token token : mBuffer) {
			token.recycle();
		}
		mBuffer.clear();
		POOL.release(this);
	}

	@Nullable
	public Token tryGet(int step) {
		int index = mIndex + step;
		if (index < 0 || index >= mBuffer.size()) {
			return null;
		}

		return mBuffer.get(index);
	}

	private static TokenStream obtain(CharSequence text, int start, int end) {
		TokenStream tokenStream = POOL.acquire();
		if (tokenStream == null) {
			tokenStream = new TokenStream();
		}

		tokenStream.mCharStream.reset(text, start, end);
		tokenStream.mTokenizer = getTokenizer();
		tokenStream.reuse();
		return tokenStream;
	}

	@NonNull
	@Override
	public String toString() {
		return mCharStream.toString();
	}

	public Token next() {
		if (!hasNext()) {
			throw new IllegalStateException("empty reader stream");
		}

		return mBuffer.get(mIndex++);
	}

	public boolean hasNext() {
		return mIndex >= 0 && mIndex < mBuffer.size();
	}

	// 符号类相关的case
	@VisibleForTesting
	public static final int[] KINSOKU_AVOID_HEADER_MAP;

	@VisibleForTesting
	public static final int[] KINSOKU_AVOID_TAIL_MAP;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static int getKinsokuAdvise(int codePoint) {
		// 临时加的，没有放到表里面。
		// 规避在头部能最大程度减少视觉上的干扰。
		if (codePoint == '"' || codePoint == '\'') {
			return Token.SYMBOL_KINSOKU_AVOID_HEADER;
		}

		int advise = 0;
		int index = binarySearch(KINSOKU_AVOID_HEADER_MAP, codePoint);
		if (index >= 0) {
			advise |= Token.SYMBOL_KINSOKU_AVOID_HEADER;
		}

		index = binarySearch(KINSOKU_AVOID_TAIL_MAP, codePoint);
		if (index >= 0) {
			advise |= Token.SYMBOL_KINSOKU_AVOID_TAIL;
		}

		return advise;
	}

	@VisibleForTesting
	public static final int[] SQUISH_RIGHT_MAP;

	@VisibleForTesting
	public static final int[] SQUISH_LEFT_MAP;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static int getSquishAdvise(int codePoint) {
		int index = binarySearch(SQUISH_RIGHT_MAP, codePoint);
		if (index >= 0) {
			return Token.SYMBOL_SQUISH_RIGHT;
		}

		index = binarySearch(SQUISH_LEFT_MAP, codePoint);
		if (index >= 0) {
			return Token.SYMBOL_SQUISH_LEFT;
		}

		return 0;
	}

	@VisibleForTesting
	public static final int[] STRETCH_RIGHT_MAP;

	@VisibleForTesting
	public static final int[] STRETCH_LEFT_MAP;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static int getStretchAdvise(int codePoint) {
		int index = binarySearch(STRETCH_RIGHT_MAP, codePoint);
		if (index >= 0) {
			return Token.SYMBOL_STRETCH_RIGHT;
		}

		index = binarySearch(STRETCH_LEFT_MAP, codePoint);
		if (index >= 0) {
			return Token.SYMBOL_STRETCH_LEFT;
		}

		return 0;
	}

	public void ahead(Token token) {
		if (mIndex != 0) {
			throw new IllegalStateException("token stream has been read");
		}
		mBuffer.add(0, token);
	}

	static {
		Arrays.sort(KINSOKU_AVOID_HEADER_MAP = new int[]{
				0x21 /*!*/, 0x25 /*%*/, 0x27 /*'*/, 0x29 /*)*/, 0x2c /*,*/, 0x2d /*-*/, 0x2e /*.*/, 0x3a /*:*/, 0x3b /*;*/,
				0x3f /*?*/, 0x5d /*]*/, 0x7d /*}*/, 0xa8 /*¨*/, 0xb0 /*°*/, 0xb7 /*·*/, 0x2c7 /*ˇ*/, 0x2c9 /*ˉ*/, 0x2010 /*‐*/,
				0x2013 /*–*/, 0x2014 /*—*/, 0x2015 /*―*/, 0x2016 /*‖*/, 0x2019 /*’*/, 0x201d /*”*/, 0x2022 /*•*/, 0x2025 /*‥*/,
				0x2026 /*…*/, 0x2027 /*‧*/, 0x2030 /*‰*/, 0x2032 /*′*/, 0x2033 /*″*/, 0x2103 /*℃*/, 0x2109 /*℉*/, 0x2236 /*∶*/,
				0x3001 /*、*/, 0x3e /*>*/, 0x3002 /*。*/, 0x3005 /*々*/, 0x3009 /*〉*/, 0x300b /*》*/, 0x300d /*」*/, 0x300f /*』*/,
				0x3011 /*】*/, 0x3015 /*〕*/, 0x3017 /*〗*/, 0xfe30 /*︰*/, 0xfe34 /*︴*/, 0xfe36 /*︶*/, 0xfe38 /*︸*/, 0xfe3a /*︺*/,
				0xfe3c /*︼*/, 0xfe3e /*︾*/, 0xfe40 /*﹀*/, 0xfe42 /*﹂*/, 0xfe44 /*﹄*/, 0xfe4f /*﹏*/, 0xff01 /*！*/, 0xff02 /*＂*/,
				0xff05 /*％*/, 0xff07 /*＇*/, 0xff09 /*）*/, 0xff0c /*，*/, 0xff0d /*－*/, 0xff1a /*：*/, 0xff1b /*；*/, 0xff1f /*？*/,
				0xff3d /*］*/, 0xff40 /*｀*/, 0xff5c /*｜*/, 0xff5d /*｝*/, 0xff5e /*～*/, 0xffe0 /*￠*/
		});

		Arrays.sort(KINSOKU_AVOID_TAIL_MAP = new int[]{
				0x24 /*$*/, 0x28 /*(*/, 0x5b /*[*/, 0x5c /*\*/, 0x7b /*{*/, 0xa3 /*£*/, 0xa5 /*¥*/, 0xa7 /*§*/, 0xb7 /*·*/,
				0x2018 /*‘*/, 0x201c /*“*/, 0x2035 /*‵*/, 0x20ac /*€*/, 0x3008 /*〈*/, 0x300a /*《*/, 0x300c /*「*/, 0x300e /*『*/,
				0x3010 /*【*/, 0x3012 /*〒*/, 0x3016 /*〖*/, 0x3014 /*〔*/, 0xfe35 /*︵*/, 0xfe37 /*︷*/, 0xfe39 /*︹*/, 0xfe3b /*︻*/,
				0xfe3d /*︽*/, 0xfe3f /*︿*/, 0xfe41 /*﹁*/, 0xfe43 /*﹃*/, 0xfe5f /*﹟*/, 0xfe69 /*﹩*/, 0xfe6b /*﹫*/, 0xff03 /*＃*/,
				0xff04 /*＄*/, 0xff08 /*（*/, 0xff1c /*＜*/, 0xff20 /*＠*/, 0xff3b /*［*/, 0xff5b /*｛*/, 0xff5e /*～*/, 0xffe1 /*￡*/,
				0xffe5 /*￥*/, 0x3c /*<*/
		});

		Arrays.sort(SQUISH_RIGHT_MAP = new int[]{
				'，', '。', '、', '：', '；', '？', '！', '》', '）', '』', '」'
		});

		Arrays.sort(SQUISH_LEFT_MAP = new int[]{
				'《', '（', '『', '「'
		});

		Arrays.sort(STRETCH_RIGHT_MAP = new int[]{
				',', '.', ':', ';', '?', '!', ':', '>', ')', ']', '}', 0x201d /* ” */, '’' /* 0x2019 */,
		});

		Arrays.sort(STRETCH_LEFT_MAP = new int[]{
				'<', '(', '[', '{', 0x201c /* “ */, '‘' /* 0x2018 */,
		});
	}

	private static int binarySearch(int[] array, int value) {

		int lo = 0;
		int hi = array.length - 1;

		// not found
		if (value < array[lo] || value > array[hi]) {
			return -1;
		}

		while (lo <= hi) {
			final int mid = (lo + hi) >>> 1;
			final int midVal = array[mid];

			if (midVal < value) {
				lo = mid + 1;
			} else if (midVal > value) {
				hi = mid - 1;
			} else {
				return mid;  // value found
			}
		}

		/* not found */
		return -1;
	}
}
