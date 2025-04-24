package me.chan.texas.text.tokenizer;

import android.icu.text.Bidi;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

import me.chan.texas.misc.BitBucket32;

public class TokenStreamUnitTest {

	@Test
	public void testJoin() {
		// 家庭表情符号：父亲👨‍👩‍👧‍👦
		String familyEmoji = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66";
		TokenStream tokenStream = TokenStream.obtain(familyEmoji, 0, familyEmoji.length());
		Token token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_WORD, token.getType());
		Assert.assertEquals(Token.CATEGORY_UNKNOWN_LETTER, token.getCategory());
		Assert.assertFalse(tokenStream.hasNext());

		// 打印家庭表情符号
		System.out.println("家庭表情符号: " + familyEmoji);

		// 中等肤色的手掌表情符号 ✋🏽
		String mediumSkinToneHand = "✋\uD83C\uDFFD";
		tokenStream = TokenStream.obtain(familyEmoji, 0, familyEmoji.length());
		token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_WORD, token.getType());
		Assert.assertEquals(Token.CATEGORY_UNKNOWN_LETTER, token.getCategory());
		Assert.assertFalse(tokenStream.hasNext());

		// 打印中等肤色的手掌表情符号
		System.out.println("中等肤色的手掌: " + mediumSkinToneHand);

		// 浅色肤色的手掌表情符号 ✋🏼
		String lightSkinToneHand = "✋\uD83C\uDFFC";

		// 打印浅色肤色的手掌表情符号
		System.out.println("浅色肤色的手掌: " + lightSkinToneHand);
		tokenStream = TokenStream.obtain(familyEmoji, 0, familyEmoji.length());
		token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_WORD, token.getType());
		Assert.assertEquals(Token.CATEGORY_UNKNOWN_LETTER, token.getCategory());
		Assert.assertFalse(tokenStream.hasNext());
	}

	@Test
	public void testPrivateUse() {
		// 私用区字符 U+E000
		String privateUseChar = "\uE000";

		// 打印私用区字符
		System.out.println("私用区字符: " + privateUseChar);
		TokenStream tokenStream = TokenStream.obtain(privateUseChar, 0, privateUseChar.length());
		Token token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_WORD, token.getType());
		Assert.assertEquals(Token.CATEGORY_UNKNOWN_LETTER, token.getCategory());
		Assert.assertFalse(tokenStream.hasNext());
	}

	@Test
	public void testSpacingMark() {
		// 零宽空格 U+200B，实际上没有可见字符，但它会影响排版
		// 在 Unicode 中，Spacing Mark（间隔标记）是一类字符，它们通常用于修改文本中的空格行为，或者在不直接显示的情况下影响排版效果。
		// 这些字符的特点是，它们本身通常不显示任何可见符号，但可以控制字符之间的间距、对齐、排版、行间距等。与其他控制字符不同，
		// Spacing Mark 是一种显示上不会产生明显符号但对文本布局有影响的特殊字符。
		String textWithZeroWidthSpace = "Hello\u200BWorld";

		System.out.println("原始文本: HelloWorld");
		System.out.println("带零宽空格的文本: " + textWithZeroWidthSpace);
		TokenStream tokenStream = TokenStream.obtain(textWithZeroWidthSpace, 0, textWithZeroWidthSpace.length());
		Token token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_WORD, token.getType());
		Assert.assertEquals(Token.CATEGORY_NORMAL, token.getCategory());
		Assert.assertTrue(tokenStream.hasNext());

		token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_CONTROL, token.getType());
		Assert.assertTrue(tokenStream.hasNext());

		token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_WORD, token.getType());
		Assert.assertEquals(Token.CATEGORY_NORMAL, token.getCategory());
		Assert.assertFalse(tokenStream.hasNext());


		// 字符 "Hello" 和 ि
		String textWithSpace = "HelloिWorld";  // "Hello" 和 "World" 之间有一个空格

		// 打印带空格的文本
		System.out.println("带ि的文本: " + textWithSpace);
		tokenStream = TokenStream.obtain(textWithZeroWidthSpace, 0, textWithZeroWidthSpace.length());
		token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_WORD, token.getType());
		Assert.assertEquals(Token.CATEGORY_NORMAL, token.getCategory());
		Assert.assertTrue(tokenStream.hasNext());

		token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_CONTROL, token.getType());
		Assert.assertTrue(tokenStream.hasNext());

		token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_WORD, token.getType());
		Assert.assertEquals(Token.CATEGORY_NORMAL, token.getCategory());
		Assert.assertFalse(tokenStream.hasNext());
	}

	@Test
	public void testEnclosingMark() {
		// 使用封闭圆圈将字母 "A" 包围
		// Enclosing Mark 类字符通过包围或标记其他字符来影响文本的排版和视觉效果。它们的主要作用是将字符包围在各种形状（如圆圈、方框、三角形等）中，
		// 以突出或标记字符。Unicode 提供了多种封闭标记字符，开发者可以根据需要在文本中使用它们来实现更复杂的排版效果和符号表达。
		String enclosedA = "A\u20DD"; // A 被封闭在圆圈中

		// 打印带有封闭圆圈的字符
		System.out.println("封闭圆圈的字符: " + enclosedA);

		TokenStream tokenStream = TokenStream.obtain(enclosedA, 0, enclosedA.length());
		Token token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_WORD, token.getType());
		Assert.assertEquals(Token.CATEGORY_UNKNOWN_LETTER, token.getCategory());
		Assert.assertFalse(tokenStream.hasNext());
	}

	@Test
	public void testNonspacingMark() {
		// 字符 "e" 和组合尖音符（U+0301）
		String accentedE = "e\u0301";  // 字符 e + 组合尖音符（é）

		// 打印带有尖音符的字符
		System.out.println("带有尖音符的字符: " + accentedE);
		TokenStream tokenStream = TokenStream.obtain(accentedE, 0, accentedE.length());
		Token token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_WORD, token.getType());
		Assert.assertEquals(Token.CATEGORY_UNKNOWN_LETTER, token.getCategory());
		Assert.assertFalse(tokenStream.hasNext());
	}

	@Test
	public void testControl() {
		String text = "\t\n\r ";
		TokenStream tokenStream = TokenStream.obtain(text, 0, text.length());
		Token token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_CONTROL, token.getType());
		Assert.assertTrue(token.checkAttribute(Token.CONTROL_ATTRIBUTE_TAB_HORIZONTAL));
		Assert.assertFalse(token.checkAttribute(Token.CONTROL_ATTRIBUTE_NEW_LINE));
		Assert.assertFalse(token.checkAttribute(Token.CONTROL_ATTRIBUTE_SPACE));
		Assert.assertTrue(tokenStream.hasNext());

		token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_CONTROL, token.getType());
		Assert.assertFalse(token.checkAttribute(Token.CONTROL_ATTRIBUTE_TAB_HORIZONTAL));
		Assert.assertTrue(token.checkAttribute(Token.CONTROL_ATTRIBUTE_NEW_LINE));
		Assert.assertFalse(token.checkAttribute(Token.CONTROL_ATTRIBUTE_SPACE));
		Assert.assertTrue(tokenStream.hasNext());

		token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_CONTROL, token.getType());
		Assert.assertFalse(token.checkAttribute(Token.CONTROL_ATTRIBUTE_TAB_HORIZONTAL));
		Assert.assertFalse(token.checkAttribute(Token.CONTROL_ATTRIBUTE_NEW_LINE));
		Assert.assertFalse(token.checkAttribute(Token.CONTROL_ATTRIBUTE_SPACE));
		Assert.assertTrue(tokenStream.hasNext());

		token = tokenStream.next();
		Assert.assertEquals(Token.TYPE_CONTROL, token.getType());
		Assert.assertFalse(token.checkAttribute(Token.CONTROL_ATTRIBUTE_TAB_HORIZONTAL));
		Assert.assertFalse(token.checkAttribute(Token.CONTROL_ATTRIBUTE_NEW_LINE));
		Assert.assertTrue(token.checkAttribute(Token.CONTROL_ATTRIBUTE_SPACE));
		Assert.assertFalse(tokenStream.hasNext());
	}

	@Test
	public void print1() {
		String msg = "I don't. didn’t bite-size hello... essay-based. hello! R&B, R&B nice";
		System.out.println(msg.length() + " " + msg.codePointCount(0, msg.length()));
		String[] values = {
				"I",
				"don't",
				"didn’t",
				"bite-size",
				"hello",
				"essay-based",
				"hello",
				"R", "B",
				"R", "B",
				"nice",
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
		TokenStream reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_CJK, token.getCategory());
			Assert.assertEquals(Token.TYPE_WORD, token.getType());
		}

		msg = "の";
		reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_CJK, token.getCategory());
			Assert.assertEquals(Token.TYPE_WORD, token.getType());
		}

		msg = "나는";
		reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_CJK, token.getCategory());
			Assert.assertEquals(Token.TYPE_WORD, token.getType());
		}

		msg = "나는";
		reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_CJK, token.getCategory());
			Assert.assertEquals(Token.TYPE_WORD, token.getType());
		}

		msg = "อักษรไทย";
		reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_UNKNOWN_LETTER, token.getCategory());
			Assert.assertEquals(Token.TYPE_WORD, token.getType());
		}

		msg = "didn’t";
		reader = TokenStream.obtain(msg, 0, msg.length());
		while (reader.hasNext()) {
			Token token = reader.next();
			Assert.assertEquals(Token.CATEGORY_NORMAL, token.getCategory());
			Assert.assertEquals(Token.TYPE_WORD, token.getType());
		}
	}

	@Test
	public void testCategory2() {
		String msg = "19";
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
		String msg = "Twenty-five: Eating dinner";
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
