package me.chan.texas.text.tokenizer;

import android.icu.text.Bidi;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

import me.chan.texas.misc.BitBucket32;

public class TokenStreamUnitTest {

	@Test
	public void print1() {
		String msg = "I don't. bite-size hello... essay-based. hello! R&B, R&B nice";
		System.out.println(msg.length() + " " + msg.codePointCount(0, msg.length()));
		String[] values = {
				"I",
				"don't",
				"bite-size",
				"hello",
				"essay-based",
				"hello",
				"R", "B",
				"R", "B",
				"nice"
		};

		System.out.println(msg);
		int index = 0;
		TokenStream reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			System.out.println(token.toString());
			if (token.getType() == Token.TYPE_WORD) {
				Assert.assertEquals(values[index++], token.mCharSequence.subSequence(token.mStart, token.mEnd));
				Assert.assertEquals(Token.CATEGORY_NORMAL, token.getCategory());
			} else if (token.getType() == Token.TYPE_SYMBOL) {
				Assert.assertEquals(Token.CATEGORY_PUNCTUATION, token.getCategory());
			}
		}
	}

	@Test
	public void testCategory() {
		String msg = "你好";
		System.out.println(msg.length() + " " + msg.codePointCount(0, msg.length()));
		TokenStream reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_CJK, token.getCategory());
			Assert.assertEquals(Token.TYPE_WORD, token.getType());
		}
	}

	@Test
	public void testCategory2() {
		String msg = "19";
		System.out.println(msg.length() + " " + msg.codePointCount(0, msg.length()));
		TokenStream reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_NUMBER, token.getCategory());
			Assert.assertEquals(Token.TYPE_WORD, token.getType());
		}
	}

	@Test
	public void testCategory3() {
		String msg = "✈️";
		System.out.println(msg.length() + " " + msg.codePointCount(0, msg.length()));
		TokenStream reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_SYMBOL, token.getCategory());
			Assert.assertEquals(Token.TYPE_SYMBOL, token.getType());
		}
	}

	@Test
	public void testCategory4() {
		String msg = "$";
		TokenStream reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_SYMBOL, token.getCategory());
			Assert.assertEquals(Token.TYPE_SYMBOL, token.getType());
			Assert.assertTrue(token.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL));
			Assert.assertFalse(token.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER));
		}

		msg = "&";
		reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_PUNCTUATION, token.getCategory());
			Assert.assertEquals(Token.TYPE_SYMBOL, token.getType());
			Assert.assertTrue(token.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL));
			Assert.assertTrue(token.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER));
		}

		msg = "《";
		reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_PUNCTUATION, token.getCategory());
			Assert.assertEquals(Token.TYPE_SYMBOL, token.getType());
			Assert.assertTrue(token.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL));
			Assert.assertFalse(token.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER));
			Assert.assertTrue(token.checkAttribute(Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT));
		}
	}

	@Test
	public void testCategory5() {
		String msg = "ضروري";
		System.out.println(msg);
		TokenStream reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_UNKNOWN_LETTER, token.getCategory());
			Assert.assertEquals(Token.TYPE_WORD, token.getType());
		}
	}

	@Test
	public void print2() {
		String msg = "don't.";
		System.out.println(msg.length() + " " + msg.codePointCount(0, msg.length()));
		TokenStream reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			System.out.println(token.toString());
		}
	}

	@Test
	public void print3() {
		String msg = "oh fuck...";
		TokenStream reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			System.out.println(token.toString());
		}
	}

	@Test
	public void print4() {
		// String msg = "\uD83D\uDE4BIt would be better to say：A cup of tea.\n❓Modifying Reason：The word 'cup' is repeated unnecessarily.（تم تكرار كلمة 'كوب' بشكل غير ضروري.）";
		// String msg = "\ud83d\ude4b\uff08\u062a\u0645 \u062a\u0643\u0631\u0627\u0631 \u0643\u0644\u0645\u0629 '\u0643\u0648\u0628' \u0628\u0634\u0643\u0644 \u063a\u064a\u0631 \u0636\u0631\u0648\u0631\u064a.\uff09";
		String msg = "\u0067\u0308";
		System.out.println(msg);
		TokenStream reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			System.out.println(token.toString());
		}

		System.out.println("===>" + Bidi.getBaseDirection(msg));
		System.out.println("===>" + Bidi.getBaseDirection("hello world"));
	}

	@Test
	public void testPrimitive() {
		BitBucket32 bucket = new BitBucket32();
		TextTokenStream.setupKinsokuAdvise(bucket, 0);
		Assert.assertEquals(bucket.getRange(0, 32), 0);
		bucket.clear();

		TextTokenStream.setupKinsokuAdvise(bucket, 0xb7);
		Assert.assertTrue(bucket.get(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL));
		Assert.assertTrue(bucket.get(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER));
		bucket.clear();

		for (int v : TextTokenStream.SQUISH_RIGHT_MAP) {
			TextTokenStream.setupSquishAdvise(bucket, v);
			Assert.assertTrue(bucket.get(Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT));
			Assert.assertEquals(1 << (Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT - Token.BIT_ATTRIBUTES_START),
					bucket.getRange(Token.BIT_ATTRIBUTES_START, Token.BIT_ATTRIBUTES_END));
			bucket.clear();
		}

		for (int v : TextTokenStream.SQUISH_LEFT_MAP) {
			TextTokenStream.setupSquishAdvise(bucket, v);
			Assert.assertTrue(bucket.get(Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT));
			Assert.assertEquals(1 << (Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT - Token.BIT_ATTRIBUTES_START),
					bucket.getRange(Token.BIT_ATTRIBUTES_START, Token.BIT_ATTRIBUTES_END));
			bucket.clear();
		}

		for (int v : TextTokenStream.STRETCH_RIGHT_MAP) {
			TextTokenStream.setupStretchAdvise(bucket, v);
			Assert.assertTrue(bucket.get(Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT));
			Assert.assertEquals(1 << (Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT - Token.BIT_ATTRIBUTES_START),
					bucket.getRange(Token.BIT_ATTRIBUTES_START, Token.BIT_ATTRIBUTES_END));
			bucket.clear();
		}

		for (int v : TextTokenStream.STRETCH_LEFT_MAP) {
			TextTokenStream.setupStretchAdvise(bucket, v);
			Assert.assertTrue(bucket.get(Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT));
			Assert.assertEquals(1 << (Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT - Token.BIT_ATTRIBUTES_START),
					bucket.getRange(Token.BIT_ATTRIBUTES_START, Token.BIT_ATTRIBUTES_END));
			bucket.clear();
		}
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

	@Test
	public void testTail() {
		Assert.assertEquals(32, Token.numberOfTrailingZeros(0));
		Assert.assertEquals(0, Token.numberOfTrailingZeros(1));
		Assert.assertEquals(1, Token.numberOfTrailingZeros(2));
		Assert.assertEquals(0, Token.numberOfTrailingZeros(3));
		Assert.assertEquals(2, Token.numberOfTrailingZeros(4));
		Assert.assertEquals(0, Token.numberOfTrailingZeros(5));
	}
}
