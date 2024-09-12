package me.chan.texas.text.tokenizer;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

import java.util.Arrays;

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

	private final WordStream mStream = new WordStream();

	private TokenStream() {
	}


	public void reset() {
		mStream.reset();
	}

	public int save() {
		return mStream.save();
	}

	public void restore(int state) {
		mStream.restore(state);
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		POOL.release(this);
	}

	@Nullable
	public Token tryGet(int state, int offset) {
		return mStream.tryGet(state, offset);
	}

	@Nullable
	public Token tryGet(int step) {
		return tryGet(mStream.save(), step);
	}

	public Token next() {
		if (!hasNext()) {
			throw new IllegalStateException("empty reader stream");
		}

		return aop(mStream.next());
	}

	public boolean hasNext() {
		return mStream.hasNext();
	}

	private static Token aop(Token token) {
		throw new UnsupportedOperationException("not implemented");
	}

	public static TokenStream obtain(CharSequence text, int start, int end) {
		TokenStream tokenStream = POOL.acquire();
		if (tokenStream == null) {
			tokenStream = new TokenStream();
		}

		tokenStream.mStream.setText(text, start, end);
		tokenStream.reuse();
		return tokenStream;
	}

	@NonNull
	@Override
	public String toString() {
		return mStream.toString();
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
	public static final char[] SQUISH_RIGHT_MAP = {
			'〉', /* \u232a */
			'、', /* \u3001 */
			'。', /* \u3002 */
			'〉', /* \u3009 */
			'》', /* \u300b */
			'」', /* \u300d */
			'』', /* \u300f */
			'】', /* \u3011 */
			'〕', /* \u3015 */
			'〗', /* \u3017 */
			'〙', /* \u3019 */
			'〛', /* \u301b */
			'〞', /* \u301e */
			'〟', /* \u301f */
			'﹂', /* \ufe42 */
			'﹄', /* \ufe44 */
			'﹚', /* \ufe5a */
			'﹜', /* \ufe5c */
			'﹞', /* \ufe5e */
			'！', /* \uff01 */
			'）', /* \uff09 */
			'，', /* \uff0c */
			'．', /* \uff0e */
			'：', /* \uff1a */
			'；', /* \uff1b */
			'？', /* \uff1f */
			'］', /* \uff3d */
			'｠', /* \uff60 */
	};
	@VisibleForTesting
	public static final char[] SQUISH_LEFT_MAP = {
			'〈', /* \u2329 */
			'〈', /* \u3008 */
			'《', /* \u300a */
			'「', /* \u300c */
			'『', /* \u300e */
			'【', /* \u3010 */
			'〔', /* \u3014 */
			'〖', /* \u3016 */
			'〘', /* \u3018 */
			'〚', /* \u301a */
			'〝', /* \u301d */
			'︐', /* \ufe10 */
			'︑', /* \ufe11 */
			'︒', /* \ufe12 */
			'︓', /* \ufe13 */
			'︔', /* \ufe14 */
			'︕', /* \ufe15 */
			'︖', /* \ufe16 */
			'﹁', /* \ufe41 */
			'﹃', /* \ufe43 */
			'﹙', /* \ufe59 */
			'﹛', /* \ufe5b */
			'﹝', /* \ufe5d */
			'（', /* \uff08 */
			'［', /* \uff3b */
			'｟', /* \uff5f */
	};
	@VisibleForTesting
	public static final char[] STRETCH_RIGHT_MAP = {
			'!', /* \u0021 */
			')', /* \u0029 */
			',', /* \u002c */
			'.', /* \u002e */
			':', /* \u003a */
			';', /* \u003b */
			'>', /* \u003e */
			'?', /* \u003f */
			']', /* \u005d */
			'}', /* \u007d */
			'»', /* \u00bb */
			';', /* \u037e */
			'༻', /* \u0f3b */
			'༽', /* \u0f3d */
			'᚜', /* \u169c */
			'’', /* \u2019 */
			'”', /* \u201d */
			'›', /* \u203a */
			'⁆', /* \u2046 */
			'⁾', /* \u207e */
			'₎', /* \u208e */
			'⌉', /* \u2309 */
			'⌋', /* \u230b */
			'〉', /* \u232a */
			'❩', /* \u2769 */
			'❫', /* \u276b */
			'❭', /* \u276d */
			'❯', /* \u276f */
			'❱', /* \u2771 */
			'❳', /* \u2773 */
			'❵', /* \u2775 */
			'⟆', /* \u27c6 */
			'⟧', /* \u27e7 */
			'⟩', /* \u27e9 */
			'⟫', /* \u27eb */
			'⟭', /* \u27ed */
			'⟯', /* \u27ef */
			'⦄', /* \u2984 */
			'⦆', /* \u2986 */
			'⦈', /* \u2988 */
			'⦊', /* \u298a */
			'⦌', /* \u298c */
			'⦎', /* \u298e */
			'⦐', /* \u2990 */
			'⦒', /* \u2992 */
			'⦔', /* \u2994 */
			'⦖', /* \u2996 */
			'⦘', /* \u2998 */
			'⧙', /* \u29d9 */
			'⧛', /* \u29db */
			'⧽', /* \u29fd */
			'⸃', /* \u2e03 */
			'⸅', /* \u2e05 */
			'⸊', /* \u2e0a */
			'⸍', /* \u2e0d */
			'⸝', /* \u2e1d */
			'⸡', /* \u2e21 */
			'⸣', /* \u2e23 */
			'⸥', /* \u2e25 */
			'⸧', /* \u2e27 */
			'⸩', /* \u2e29 */
			'﴾', /* \ufd3e */
			'︘', /* \ufe18 */
			'︶', /* \ufe36 */
			'︸', /* \ufe38 */
			'︺', /* \ufe3a */
			'︼', /* \ufe3c */
			'︾', /* \ufe3e */
			'﹀', /* \ufe40 */
			'﹂', /* \ufe42 */
			'﹄', /* \ufe44 */
			'﹈', /* \ufe48 */
			'｝', /* \uff5d */
			'｡', /* \uff61 */
			'｣', /* \uff63 */
			'､', /* \uff64 */
	};

	@VisibleForTesting
	public static final char[] STRETCH_LEFT_MAP = {
			'(', /* \u0028 */
			'<', /* \u003c */
			'[', /* \u005b */
			'{', /* \u007b */
			'«', /* \u00ab */
			'¿', /* \u00bf */
			'༺', /* \u0f3a */
			'༼', /* \u0f3c */
			'᚛', /* \u169b */
			'‘', /* \u2018 */
			'‚', /* \u201a */
			'‛', /* \u201b */
			'“', /* \u201c */
			'„', /* \u201e */
			'‟', /* \u201f */
			'‹', /* \u2039 */
			'⁅', /* \u2045 */
			'⁽', /* \u207d */
			'₍', /* \u208d */
			'⌈', /* \u2308 */
			'⌊', /* \u230a */
			'〈', /* \u2329 */
			'❨', /* \u2768 */
			'❪', /* \u276a */
			'❬', /* \u276c */
			'❮', /* \u276e */
			'❰', /* \u2770 */
			'❲', /* \u2772 */
			'❴', /* \u2774 */
			'⟅', /* \u27c5 */
			'⟦', /* \u27e6 */
			'⟨', /* \u27e8 */
			'⟪', /* \u27ea */
			'⟬', /* \u27ec */
			'⟮', /* \u27ee */
			'⦃', /* \u2983 */
			'⦅', /* \u2985 */
			'⦇', /* \u2987 */
			'⦉', /* \u2989 */
			'⦋', /* \u298b */
			'⦍', /* \u298d */
			'⦏', /* \u298f */
			'⦑', /* \u2991 */
			'⦓', /* \u2993 */
			'⦕', /* \u2995 */
			'⦗', /* \u2997 */
			'⧘', /* \u29d8 */
			'⧚', /* \u29da */
			'⧼', /* \u29fc */
			'⸂', /* \u2e02 */
			'⸄', /* \u2e04 */
			'⸉', /* \u2e09 */
			'⸌', /* \u2e0c */
			'⸜', /* \u2e1c */
			'⸠', /* \u2e20 */
			'⸢', /* \u2e22 */
			'⸤', /* \u2e24 */
			'⸦', /* \u2e26 */
			'⸨', /* \u2e28 */
			'︗', /* \ufe17 */
			'︵', /* \ufe35 */
			'︷', /* \ufe37 */
			'︹', /* \ufe39 */
			'︻', /* \ufe3b */
			'︽', /* \ufe3d */
			'︿', /* \ufe3f */
			'﹇', /* \ufe47 */
			'｛', /* \uff5b */
			'｢', /* \uff62 */
	};

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
