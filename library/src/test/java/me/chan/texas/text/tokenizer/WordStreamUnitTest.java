package me.chan.texas.text.tokenizer;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.text.UnicodeSetIterator;
import com.shanbay.lib.texas.TestUtils;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WordStreamUnitTest {

	@Test
	public void testBase() throws IOException {
		for (int i = 1; i <= 6; ++i) {
			File file = new File("../app/src/main/assets/harry" + i + ".txt");
			System.out.println(file.getAbsolutePath());
			assertTrue(file.exists());

			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line)
						.append("\n");
			}

			String text = stringBuilder.toString();
			assertNotEquals(text.length(), 0);

			long timestamp = System.currentTimeMillis();

			StringBuilder builder = new StringBuilder();
			BreakIterator boundary = BreakIterator.getWordInstance();
			boundary.setText(text);
			int start = boundary.first();
			boundary.getRuleStatus();
			for (int end = boundary.next();
				 end != BreakIterator.DONE;
				 start = end, end = boundary.next()) {
				builder.append(text, start, end);
			}

			Assert.assertEquals(builder.toString(), text);

			System.out.println("used time: " + (System.currentTimeMillis() - timestamp) + ", text len: " + text.length());
		}
	}

	@Test
	public void test() throws IOException {
		TextTokenStream stream = new TextTokenStream();
		for (int i = 1; i <= 6; ++i) {
			File file = new File("../app/src/main/assets/harry" + i + ".txt");
			System.out.println(file.getAbsolutePath());
			assertTrue(file.exists());

			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line)
						.append("\n");
			}

			String text = stringBuilder.toString();
			assertNotEquals(text.length(), 0);

			long timestamp = System.currentTimeMillis();

			StringBuilder builder = new StringBuilder();
			stream.setText(text, 0, text.length());
			Token token = null;
			while ((token = stream.next()) != null) {
				builder.append(token.getCharSequence(), token.getStart(), token.getEnd());
			}
			Assert.assertEquals(builder.toString(), text);

			System.out.println("used time: " + (System.currentTimeMillis() - timestamp) + ", text len: " + text.length());
		}
	}

	@Test
	public void testSimple() {
		TextTokenStream stream = new TextTokenStream();
		Assert.assertNull(stream.next());
		Assert.assertNull(stream.tryGet(stream.save(), -1));
		String msg = "0 2 4";
		stream.setText(msg, 0, msg.length());
		Assert.assertNull(stream.tryGet(stream.save(), -1));
		Assert.assertNull(stream.tryGet(stream.save(), 5));
		Token token = null;
		token = stream.tryGet(stream.save(), 4);
		Assert.assertEquals(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString(), "4");

		while ((token = stream.next()) != null) {
			System.out.println(token.getCharSequence().subSequence(token.getStart(), token.getEnd()));
		}
		token = stream.tryGet(stream.save(), -5);
		Assert.assertEquals(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString(), "0");
		Assert.assertNull(stream.tryGet(stream.save(), -6));

		stream.reset();
		Assert.assertNull(stream.tryGet(stream.save(), -1));
		Assert.assertNull(stream.tryGet(stream.save(), 5));
		token = stream.tryGet(stream.save(), 4);
		Assert.assertEquals(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString(), "4");

		System.out.println("====================================");
		stream.setText(msg, 1, 3);
		while ((token = stream.next()) != null) {
			System.out.println(token.getCharSequence().subSequence(token.getStart(), token.getEnd()));
		}
	}

	@Test
	public void testSave() {
		String[] array = {"aa", " ", "11", " ", "hello-world四五R&B"};
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : array) {
			stringBuilder.append(s);
		}
		String text = stringBuilder.toString();

		TextTokenStream stream = new TextTokenStream();
		Assert.assertNull(stream.next());
		Assert.assertEquals(stream.save(), 0);

		stream.setText(text, 0, text.length());

		List<Token> list = new ArrayList<>();
		{
			Token token = Token.obtain();
			token.mCharSequence = "aa";
			token.mStart = 0;
			token.mEnd = 2;
			token.mMask = Token.CATEGORY_NORMAL;
			token.mType = Token.TYPE_WORD;
			list.add(token);
		}

		{
			Token token = Token.obtain();
			token.mCharSequence = " ";
			token.mStart = 0;
			token.mEnd = 1;
			token.mMask = Token.CATEGORY_CONTROL;
			token.mType = Token.TYPE_CONTROL;
			list.add(token);
		}

		{
			Token token = Token.obtain();
			token.mCharSequence = "11";
			token.mStart = 0;
			token.mEnd = 2;
			token.mMask = Token.CATEGORY_NUMBER;
			token.mType = Token.TYPE_WORD;
			list.add(token);
		}

		{
			Token token = Token.obtain();
			token.mCharSequence = " ";
			token.mStart = 0;
			token.mEnd = 1;
			token.mMask = Token.CATEGORY_CONTROL;
			token.mType = Token.TYPE_CONTROL;
			list.add(token);
		}

		{
			Token token = Token.obtain();
			token.mCharSequence = "hello-world";
			token.mStart = 0;
			token.mEnd = 11;
			token.mMask = Token.CATEGORY_NORMAL;
			token.mType = Token.TYPE_WORD;
			list.add(token);
		}

		{
			Token token = Token.obtain();
			token.mCharSequence = "四五";
			token.mStart = 0;
			token.mEnd = 2;
			token.mMask = Token.CATEGORY_CJK;
			token.mType = Token.TYPE_WORD;
			list.add(token);
		}

		{
			Token token = Token.obtain();
			token.mCharSequence = "R";
			token.mStart = 0;
			token.mEnd = 1;
			token.mMask = Token.CATEGORY_NORMAL;
			token.mType = Token.TYPE_WORD;
			list.add(token);
		}

		{
			Token token = Token.obtain();
			token.mCharSequence = "&";
			token.mStart = 0;
			token.mEnd = 1;
			token.mMask = TextTokenStream.getMask(Token.CATEGORY_PUNCTUATION,
					TextTokenStream.getAdvise('&', Character.getType('&')));
			token.mType = Token.TYPE_SYMBOL;
			list.add(token);
		}

		{
			Token token = Token.obtain();
			token.mCharSequence = "B";
			token.mStart = 0;
			token.mEnd = 1;
			token.mMask = Token.CATEGORY_NORMAL;
			token.mType = Token.TYPE_WORD;
			list.add(token);
		}

		int save = stream.save();
		Iterator<Token> iterator = list.iterator();
		Token token = null;
		while ((token = stream.next()) != null) {
			Token except = iterator.next();
			Assert.assertEquals(except, token);
		}
		Assert.assertEquals(stream.save(), list.size());

		stream.restore(save);
		iterator = list.iterator();
		while ((token = stream.next()) != null) {
			Token except = iterator.next();
			Assert.assertEquals(except, token);
		}
		Assert.assertEquals(stream.save(), list.size());

		TokenStream tmp = stream;
		stream.recycle();
		TestUtils.testRecycled(tmp);

		stream = TextTokenStream.obtain("hello", 1, 2);
		Assert.assertSame(stream, tmp);
	}

	@Test
	public void unit() throws IOException {
		String v = printSet("[[:Ps:]]", "PS") + printSet("[[:Pe:]]", "PE") + printSet("[[:Pi:]]", "PI") + printSet("[[:Pf:]]", "PF") + printSet("[[:Po:]]", "PO");
		File file = new File("../app/build/predefine.txt");
		file.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(v.getBytes());
		fileOutputStream.close();
	}

	private String printSet(String pattern, String name) {
		// pattern: Ps
		UnicodeSet unicodeSet = new UnicodeSet(pattern);
		List<Integer> points = new ArrayList<>();
		// 遍历
		UnicodeSetIterator iterator = new UnicodeSetIterator(unicodeSet);
		while (iterator.next()) {
			points.add(iterator.codepoint);
		}
		points.sort((o1, o2) -> o1 - o2);

		StringBuilder stringBuilder = new StringBuilder("private final char[] " + name + " = {\n");
		for (int i = 0; i < points.size(); ++i) {
			int v = points.get(i);
			char c = (char) v;
			stringBuilder.append(String.format("\t'%s', /* \\u%04x */\n", c == '\'' || c == '\\' ? "\\" + c : c, v));
		}
		stringBuilder.append("};\n");
		return stringBuilder.toString();
	}

	private final char[] PS_STRETCH_LEFT = {
			'(', /* \u0028 */
			'[', /* \u005b */
			'{', /* \u007b */
			'༺', /* \u0f3a */
			'༼', /* \u0f3c */
			'᚛', /* \u169b */
			'‚', /* \u201a */
			'„', /* \u201e */
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

	private final char[] PS_SQUISH_LEFT = {
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
			'﹁', /* \ufe41 */
			'﹃', /* \ufe43 */
			'﹙', /* \ufe59 */
			'﹛', /* \ufe5b */
			'﹝', /* \ufe5d */
			'（', /* \uff08 */
			'［', /* \uff3b */
			'｟', /* \uff5f */
	};

	private final char[] PE_SQUISH_RIGHT = {
			'〉', /* \u232a */
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
			'）', /* \uff09 */
			'］', /* \uff3d */
			'｠', /* \uff60 */
	};

	private final char[] PE_STRETCH_RIGHT = {
			')', /* \u0029 */
			']', /* \u005d */
			'}', /* \u007d */
			'༻', /* \u0f3b */
			'༽', /* \u0f3d */
			'᚜', /* \u169c */
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
			'｣', /* \uff63 */
	};

	private final char[] PI = {
			'«', /* \u00ab */
			'‘', /* \u2018 */
			'‛', /* \u201b */
			'“', /* \u201c */
			'‟', /* \u201f */
			'‹', /* \u2039 */
			'⸂', /* \u2e02 */
			'⸄', /* \u2e04 */
			'⸉', /* \u2e09 */
			'⸌', /* \u2e0c */
			'⸜', /* \u2e1c */
			'⸠', /* \u2e20 */
	};

	private final char[] PF = {
			'»', /* \u00bb */
			'’', /* \u2019 */
			'”', /* \u201d */
			'›', /* \u203a */
			'⸃', /* \u2e03 */
			'⸅', /* \u2e05 */
			'⸊', /* \u2e0a */
			'⸍', /* \u2e0d */
			'⸝', /* \u2e1d */
			'⸡', /* \u2e21 */
	};

	private final char[] PO_STRETCH_LEFT = {
			'¿', /* \u00bf */
	};

	private final char[] PO_SQUISH_RIGHT = {
			'、', /* \u3001 */
			'。', /* \u3002 */
			'！', /* \uff01 */
			'，', /* \uff0c */
			'．', /* \uff0e */
			'：', /* \uff1a */
			'；', /* \uff1b */
			'？', /* \uff1f */
	};

	private final char[] PO_SQUISH_LEFT = {
			'︐', /* \ufe10 */
			'︑', /* \ufe11 */
			'︒', /* \ufe12 */
			'︓', /* \ufe13 */
			'︔', /* \ufe14 */
			'︕', /* \ufe15 */
			'︖', /* \ufe16 */
	};

	private final char[] PO_STRETCH_RIGHT = {
			'!', /* \u0021 */
			',', /* \u002c */
			'.', /* \u002e */
			':', /* \u003a */
			';', /* \u003b */
			'?', /* \u003f */
			';', /* \u037e */
			'｡', /* \uff61 */
			'､', /* \uff64 */
	};

	@Test
	public void predefine() throws IOException {
		Set<Integer> squishLeft = new HashSet<>();
		char[] tmp = {
				'《', '（', '『', '「'
		};
		addAll(squishLeft, tmp);
		Set<Integer> squishRight = new HashSet<>();
		tmp = new char[]{
				'，', '。', '、', '：', '；', '？', '！', '》', '）', '』', '」'
		};
		addAll(squishRight, tmp);
		Set<Integer> stretchLeft = new HashSet<>();
		tmp = new char[]{
				'<', '(', '[', '{', 0x201c /* “ */, '‘' /* 0x2018 */,
		};
		addAll(stretchLeft, tmp);
		Set<Integer> stretchRight = new HashSet<>();
		tmp = new char[]{
				',', '.', ':', ';', '?', '!', ':', '>', ')', ']', '}', 0x201d /* ” */, '’' /* 0x2019 */,
		};
		addAll(stretchRight, tmp);

		addAll(squishLeft, PS_SQUISH_LEFT);
		addAll(squishRight, PE_SQUISH_RIGHT);
		addAll(stretchLeft, PS_STRETCH_LEFT);
		addAll(stretchRight, PE_STRETCH_RIGHT);
		addAll(stretchLeft, PI);
		addAll(stretchRight, PF);
		addAll(stretchLeft, PO_STRETCH_LEFT);
		addAll(stretchRight, PO_STRETCH_RIGHT);
		addAll(squishRight, PO_SQUISH_RIGHT);
		addAll(squishLeft, PO_SQUISH_LEFT);

		String code = toCode("SQUISH_RIGHT_MAP", squishRight) + toCode("SQUISH_LEFT_MAP", squishLeft) + toCode("STRETCH_RIGHT_MAP", stretchRight) + toCode("STRETCH_LEFT_MAP", stretchLeft);
		File file = new File("../predefine_final.txt");
		file.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(code.getBytes());
		fileOutputStream.close();
	}

	private void addAll(Set<Integer> set, char[] array) {
		for (char c : array) {
			set.add((int) c);
		}
	}

	private String toCode(String name, Set<Integer> set) {
		List<Integer> list = new ArrayList<>(set);
		list.sort(Integer::compareTo);
		StringBuilder stringBuilder = new StringBuilder("@VisibleForTesting\n public static final char[] " + name + " = {\n");
		for (int i = 0; i < list.size(); ++i) {
			int v = list.get(i);
			char c = (char) v;
			stringBuilder.append(String.format("\t'%s', /* \\u%04x */\n", c == '\'' || c == '\\' ? "\\" + c : c, v));
		}
		stringBuilder.append("};\n");
		return stringBuilder.toString();
	}

	@Test
	public void testLink() {
		TokenStream tokenStream = TokenStream.link(TokenStream.obtain(" ", 0, 1), TokenStream.obtain("a  b", 0, 4));
		Assert.assertEquals(4, tokenStream.size());
		Assert.assertTrue(tokenStream.hasNext());
		int save = tokenStream.save();
		assertToken(tokenStream.next(), " ");

		Assert.assertTrue(tokenStream.hasNext());
		assertToken(tokenStream.next(), "a");
		tokenStream.restore(save);
		Assert.assertTrue(tokenStream.hasNext());
		assertToken(tokenStream.next(), " ");

		Assert.assertTrue(tokenStream.hasNext());
		assertToken(tokenStream.next(), "a");
		Assert.assertTrue(tokenStream.hasNext());
		assertToken(tokenStream.next(), "  ");
		Assert.assertTrue(tokenStream.hasNext());
		assertToken(tokenStream.next(), "b");

		Assert.assertFalse(tokenStream.hasNext());
		assertToken(tokenStream.tryGet(save, 0), " ");
		assertToken(tokenStream.tryGet(-1), "b");
		Assert.assertNull(tokenStream.tryGet(4));

		TokenStream tmp = tokenStream;
		tokenStream.recycle();
		TestUtils.testRecycled(tokenStream);
		tokenStream = TokenStream.link(TokenStream.obtain(" ", 0, 1), TokenStream.obtain("a b", 0, 3));
		Assert.assertSame(tokenStream, tmp);
	}

	private void assertToken(Token token, String except) {
		Assert.assertEquals(except, token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString());
	}
}
