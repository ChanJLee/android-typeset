package me.chan.texas.text.tokenizer;

import android.icu.text.Bidi;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class TokenStreamUnitTest {

	private final TokenizerModel mModel;

	public TokenStreamUnitTest() throws IOException {
		mModel = new TokenizerModel(new File("../library/src/main/assets/texas/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"));
	}

	@Test
	public void testNlp2() throws IOException {
		TokenizerModel model = new TokenizerModel(new File("../library/src/main/assets/texas/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"));
		Tokenizer tokenizer = new TokenizerME(model);
		String[] tokens = tokenizer.tokenize("\"will! 你好R&B");
		for (String token : tokens) {
			System.out.println(token);
		}
	}

	@Test
	public void print1() {
		me.chan.texas.text.tokenizer.Tokenizer.setup(mModel);
		String msg = "I don't. bite-size hello... essay-based. hello! R&B, R&B nice";
		System.out.println(msg.length() + " " + msg.codePointCount(0, msg.length()));
		String[] values = {
				"I",
				"don't",
				"bite-size",
				"hello",
				"essay-based",
				"hello",
				"R&B",
				"R&B",
				"nice"
		};

		System.out.println(msg);
		int index = 0;
		TokenStream reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			System.out.println(token.toString());
			if (token.mType == Token.TYPE_WORD) {
				Assert.assertEquals(values[index++], token.mCharSequence.subSequence(token.mStart, token.mEnd));
			}
		}
	}

	@Test
	public void print2() {
		me.chan.texas.text.tokenizer.Tokenizer.setup(mModel);
		String msg = "don't.";
		System.out.println(msg.length() + " " + msg.codePointCount(0, msg.length()));
		TokenStream reader =  TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			System.out.println(token.toString());
		}
	}

	@Test
	public void print3() {
		me.chan.texas.text.tokenizer.Tokenizer.setup(mModel);
		String msg = "oh fuck...";
		TokenStream reader =  TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			System.out.println(token.toString());
		}
	}

	@Test
	public void print4() {
		me.chan.texas.text.tokenizer.Tokenizer.setup(mModel);
		// String msg = "\uD83D\uDE4BIt would be better to say：A cup of tea.\n❓Modifying Reason：The word 'cup' is repeated unnecessarily.（تم تكرار كلمة 'كوب' بشكل غير ضروري.）";
		// String msg = "\ud83d\ude4b\uff08\u062a\u0645 \u062a\u0643\u0631\u0627\u0631 \u0643\u0644\u0645\u0629 '\u0643\u0648\u0628' \u0628\u0634\u0643\u0644 \u063a\u064a\u0631 \u0636\u0631\u0648\u0631\u064a.\uff09";
		String msg = "\u0067\u0308";
		System.out.println(msg);
		TokenStream reader =  TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			System.out.println(token.toString());
		}

		System.out.println("===>" + Bidi.getBaseDirection(msg));
		System.out.println("===>" + Bidi.getBaseDirection("hello world"));
	}

	@Test
	public void testPrimitive() {
		for (int v : TokenStream.KINSOKU_AVOID_HEADER_MAP) {
			Assert.assertNotEquals(TokenStream.getKinsokuAdvise(v) & Token.SYMBOL_KINSOKU_AVOID_HEADER, 0);
		}
		for (int v : TokenStream.KINSOKU_AVOID_TAIL_MAP) {
			Assert.assertNotEquals(TokenStream.getKinsokuAdvise(v) & Token.SYMBOL_KINSOKU_AVOID_TAIL, 0);
		}
		Assert.assertEquals(TokenStream.getKinsokuAdvise(0), 0);
		Assert.assertEquals(TokenStream.getKinsokuAdvise(0xb7), Token.SYMBOL_KINSOKU_AVOID_TAIL | Token.SYMBOL_KINSOKU_AVOID_HEADER);


		for (int v : TokenStream.SQUISH_RIGHT_MAP) {
			Assert.assertEquals(TokenStream.getSquishAdvise(v), Token.SYMBOL_SQUISH_RIGHT);
		}
		for (int v : TokenStream.SQUISH_LEFT_MAP) {
			Assert.assertEquals(TokenStream.getSquishAdvise(v), Token.SYMBOL_SQUISH_LEFT);
		}
		Assert.assertEquals(TokenStream.getSquishAdvise(0), 0);


		for (int v : TokenStream.STRETCH_RIGHT_MAP) {
			Assert.assertEquals(TokenStream.getStretchAdvise(v), Token.SYMBOL_STRETCH_RIGHT);
		}
		for (int v : TokenStream.STRETCH_LEFT_MAP) {
			Assert.assertEquals(TokenStream.getStretchAdvise(v), Token.SYMBOL_STRETCH_LEFT);
		}
		Assert.assertEquals(TokenStream.getStretchAdvise(0), 0);
	}

	@Test
	public void testIntArraySorted() throws IllegalAccessException {
		Field[] fields = TokenStream.class.getDeclaredFields();
		for (Field field : fields) {
			if (field.getType() == int[].class) {
				System.out.println(field.getName());
				field.setAccessible(true);
				int[] array = (int[]) field.get(null);
				int v = array[0];
				for (int i = 1; i < array.length; ++i) {
					Assert.assertTrue(v <= array[i]);
					v = array[i];
				}
			}
		}
	}
}
