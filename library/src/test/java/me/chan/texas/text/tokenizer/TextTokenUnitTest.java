package me.chan.texas.text.tokenizer;

import org.junit.Assert;
import org.junit.Test;

public class TextTokenUnitTest {

	// ============================================================
	// 工厂方法 / 池化生命周期
	// ============================================================

	@Test
	public void obtain_returnsNonRecycledTokenWithDefaultFields() {
		TextToken t = TextToken.obtain();
		Assert.assertFalse(t.isRecycled());
		Assert.assertEquals(Token.TYPE_NONE, t.getType());
		Assert.assertEquals(0, t.getCategory());
		Assert.assertEquals(0, t.getStart());
		Assert.assertEquals(0, t.getEnd());
		Assert.assertEquals(0, t.size());
		Assert.assertFalse(t.isRtl());
		Assert.assertNull(t.getCharSequence());
		t.recycle();
	}

	@Test
	public void obtainOtherWord_setsTypeWord() {
		TextToken t = TextToken.obtainOtherWord();
		Assert.assertFalse(t.isRecycled());
		Assert.assertEquals(Token.TYPE_WORD, t.getType());
		t.recycle();
	}

	@Test
	public void copy_copiesAllFields() {
		TextToken src = TextToken.obtain();
		src.mCharSequence = "hello world";
		src.mStart = 6;
		src.mEnd = 11;
		src.mType = Token.TYPE_WORD;
		src.mCategory = Token.CATEGORY_NORMAL;
		src.mAttributes = 0x05;
		src.mRtl = true;

		TextToken copy = TextToken.copy(src);

		Assert.assertSame(src.mCharSequence, copy.mCharSequence);
		Assert.assertEquals(src.mStart, copy.mStart);
		Assert.assertEquals(src.mEnd, copy.mEnd);
		Assert.assertEquals(src.getType(), copy.getType());
		Assert.assertEquals(src.getCategory(), copy.getCategory());
		Assert.assertEquals(src.mAttributes, copy.mAttributes);
		Assert.assertEquals(src.isRtl(), copy.isRtl());

		// 拷贝是独立实例
		Assert.assertNotSame(src, copy);
		src.recycle();
		copy.recycle();
	}

	@Test
	public void recycle_clearsFields_andSetsRecycledFlag() {
		TextToken t = TextToken.obtain();
		t.mCharSequence = "test";
		t.mStart = 0;
		t.mEnd = 4;
		t.mType = Token.TYPE_WORD;
		t.mCategory = Token.CATEGORY_NORMAL;
		t.mAttributes = 0x10;
		t.mRtl = true;

		t.recycle();

		Assert.assertTrue(t.isRecycled());
		Assert.assertNull(t.mCharSequence);
		Assert.assertEquals(0, t.mStart);
		Assert.assertEquals(0, t.mEnd);
		Assert.assertEquals(Token.TYPE_NONE, t.mType);
		Assert.assertEquals(0, t.mCategory);
		Assert.assertEquals(0, t.mAttributes);
		Assert.assertFalse(t.mRtl);
	}

	@Test
	public void recycle_isIdempotent() {
		TextToken t = TextToken.obtain();
		t.recycle();
		Assert.assertTrue(t.isRecycled());
		// 第二次 recycle 不应抛、状态不变
		t.recycle();
		Assert.assertTrue(t.isRecycled());
	}

	// ============================================================
	// 基础访问器
	// ============================================================

	@Test
	public void sizeReflectsRange() {
		TextToken t = TextToken.obtain();
		t.mCharSequence = "0123456789";
		t.mStart = 2;
		t.mEnd = 7;
		Assert.assertEquals(5, t.size());
		Assert.assertEquals(2, t.getStart());
		Assert.assertEquals(7, t.getEnd());
		Assert.assertSame("0123456789", t.getCharSequence());
		t.recycle();
	}

	@Test
	public void typeAndCategoryReflectFields() {
		TextToken t = TextToken.obtain();
		t.mType = Token.TYPE_SYMBOL;
		t.mCategory = Token.CATEGORY_PUNCTUATION;
		Assert.assertEquals(Token.TYPE_SYMBOL, t.getType());
		Assert.assertEquals(Token.CATEGORY_PUNCTUATION, t.getCategory());
		t.recycle();
	}

	@Test
	public void isRtl_reflectsField() {
		TextToken t = TextToken.obtain();
		Assert.assertFalse(t.isRtl());
		t.mRtl = true;
		Assert.assertTrue(t.isRtl());
		t.recycle();
	}

	// ============================================================
	// checkAttribute — bit 编码语义
	// ============================================================

	@Test
	public void checkAttribute_zeroByDefault() {
		TextToken t = TextToken.obtain();
		Assert.assertFalse(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_LINE_HEADER));
		Assert.assertFalse(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_LINE_TAIL));
		Assert.assertFalse(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT));
		Assert.assertFalse(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT));
		Assert.assertFalse(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT));
		Assert.assertFalse(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT));
		t.recycle();
	}

	@Test
	public void checkAttribute_bitMappingFromMAttributes() {
		// mAttributes 的 bit 0 对应 attr 16 (BIT_ATTRIBUTES_START)
		// bit 5 对应 attr 21 (STRETCH_RIGHT)
		TextToken t = TextToken.obtain();

		t.mAttributes = (byte) 0x01; // bit 0 set
		Assert.assertTrue(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_LINE_HEADER)); // attr 16

		t.mAttributes = (byte) 0x02; // bit 1 set
		Assert.assertTrue(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_LINE_TAIL)); // attr 17

		t.mAttributes = (byte) 0x04; // bit 2
		Assert.assertTrue(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT)); // attr 18

		t.mAttributes = (byte) 0x08; // bit 3
		Assert.assertTrue(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)); // attr 19

		t.mAttributes = (byte) 0x10; // bit 4
		Assert.assertTrue(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT)); // attr 20

		t.mAttributes = (byte) 0x20; // bit 5
		Assert.assertTrue(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT)); // attr 21

		t.recycle();
	}

	@Test
	public void checkAttribute_multipleBits_eachReportsIndependently() {
		TextToken t = TextToken.obtain();
		// 同时设置 KINSOKU_HEADER (bit 0) 和 SQUISH_LEFT (bit 2)
		t.mAttributes = (byte) (0x01 | 0x04);

		Assert.assertTrue(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_LINE_HEADER));
		Assert.assertFalse(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_LINE_TAIL));
		Assert.assertTrue(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT));
		Assert.assertFalse(t.checkAttribute(Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT));

		t.recycle();
	}

	@Test
	public void checkAttribute_controlBitsShareEncodingWithSymbolBits() {
		// CONTROL_ATTRIBUTE_SPACE = 16 = SYMBOL_ATTRIBUTE_KINSOKU_AVOID_LINE_HEADER
		// 同一 bit 既可被解释为 control 也可被解释为 symbol 属性，按 token 类型决定
		TextToken t = TextToken.obtain();
		t.mAttributes = (byte) 0x04; // bit 2

		Assert.assertTrue(t.checkAttribute(Token.CONTROL_ATTRIBUTE_NEW_LINE)); // attr 18
		Assert.assertFalse(t.checkAttribute(Token.CONTROL_ATTRIBUTE_SPACE)); // attr 16
		Assert.assertFalse(t.checkAttribute(Token.CONTROL_ATTRIBUTE_TAB_HORIZONTAL)); // attr 17
		t.recycle();
	}

	// ============================================================
	// hasSymbolTypefaceAttributes
	// ============================================================

	@Test
	public void hasSymbolTypefaceAttributes_falseWhenNotSymbol() {
		TextToken t = TextToken.obtain();
		t.mType = Token.TYPE_WORD;
		t.mAttributes = (byte) 0xFF; // 所有位都置上
		Assert.assertFalse(t.hasSymbolTypefaceAttributes());
		t.recycle();
	}

	@Test
	public void hasSymbolTypefaceAttributes_falseForKinsokuOnly() {
		TextToken t = TextToken.obtain();
		t.mType = Token.TYPE_SYMBOL;
		t.mAttributes = (byte) 0x03; // 仅 bit 0/1 = KINSOKU 属性
		Assert.assertFalse(t.hasSymbolTypefaceAttributes());
		t.recycle();
	}

	@Test
	public void hasSymbolTypefaceAttributes_trueForSquishLeft() {
		TextToken t = TextToken.obtain();
		t.mType = Token.TYPE_SYMBOL;
		t.mAttributes = (byte) 0x04; // bit 2 = SQUISH_LEFT
		Assert.assertTrue(t.hasSymbolTypefaceAttributes());
		t.recycle();
	}

	@Test
	public void hasSymbolTypefaceAttributes_trueForStretchRight() {
		TextToken t = TextToken.obtain();
		t.mType = Token.TYPE_SYMBOL;
		t.mAttributes = (byte) 0x20; // bit 5 = STRETCH_RIGHT
		Assert.assertTrue(t.hasSymbolTypefaceAttributes());
		t.recycle();
	}

	// ============================================================
	// equals(String)
	// ============================================================

	@Test
	public void equalsString_matchingFullText_returnsTrue() {
		TextToken t = TextToken.obtain();
		t.mCharSequence = "hello";
		t.mStart = 0;
		t.mEnd = 5;
		Assert.assertTrue(t.equals("hello"));
		t.recycle();
	}

	@Test
	public void equalsString_lengthMismatch_returnsFalse() {
		TextToken t = TextToken.obtain();
		t.mCharSequence = "hello";
		t.mStart = 0;
		t.mEnd = 5;
		Assert.assertFalse(t.equals("hell"));
		Assert.assertFalse(t.equals("hellos"));
		t.recycle();
	}

	@Test
	public void equalsString_charMismatch_returnsFalse() {
		TextToken t = TextToken.obtain();
		t.mCharSequence = "hello";
		t.mStart = 0;
		t.mEnd = 5;
		Assert.assertFalse(t.equals("hellp"));
		t.recycle();
	}

	@Test
	public void equalsString_subrange_comparesOnlyTheRange() {
		TextToken t = TextToken.obtain();
		t.mCharSequence = "hello world";
		t.mStart = 6;
		t.mEnd = 11;
		Assert.assertTrue(t.equals("world"));
		Assert.assertFalse(t.equals("hello"));
		t.recycle();
	}

	@Test
	public void equalsString_nullCharSequence_returnsTrueRegardless() {
		// 当前实现：mCharSequence 为 null 时直接返回 true（无论参数为何）
		TextToken t = TextToken.obtain();
		Assert.assertNull(t.mCharSequence);
		Assert.assertTrue(t.equals(""));
		Assert.assertTrue(t.equals("anything"));
		t.recycle();
	}

	@Test
	public void equalsString_nullInput_returnsTrueRegardless() {
		// 当前实现：参数 s 为 null 时直接返回 true
		TextToken t = TextToken.obtain();
		t.mCharSequence = "hello";
		t.mStart = 0;
		t.mEnd = 5;
		Assert.assertTrue(t.equals((String) null));
		t.recycle();
	}

	// ============================================================
	// equals(Object) / hashCode
	// ============================================================

	@Test
	public void equalsObject_sameInstance_returnsTrue() {
		TextToken t = TextToken.obtain();
		Assert.assertEquals(t, t);
		t.recycle();
	}

	@Test
	public void equalsObject_null_returnsFalse() {
		TextToken t = TextToken.obtain();
		Assert.assertNotEquals(t, null);
		t.recycle();
	}

	@Test
	public void equalsObject_differentClass_returnsFalse() {
		TextToken t = TextToken.obtain();
		Assert.assertNotEquals(t, "hello");
		Assert.assertNotEquals(t, 42);
		t.recycle();
	}

	@Test
	public void equalsObject_differentType_returnsFalse() {
		TextToken a = TextToken.obtain();
		a.mType = Token.TYPE_WORD;
		TextToken b = TextToken.obtain();
		b.mType = Token.TYPE_SYMBOL;
		Assert.assertNotEquals(a, b);
		a.recycle();
		b.recycle();
	}

	@Test
	public void equalsObject_differentCategory_returnsFalse() {
		TextToken a = TextToken.obtain();
		a.mCategory = Token.CATEGORY_NORMAL;
		TextToken b = TextToken.obtain();
		b.mCategory = Token.CATEGORY_CJK;
		Assert.assertNotEquals(a, b);
		a.recycle();
		b.recycle();
	}

	@Test
	public void equalsObject_differentAttributes_returnsFalse() {
		TextToken a = TextToken.obtain();
		a.mAttributes = 0x01;
		TextToken b = TextToken.obtain();
		b.mAttributes = 0x02;
		Assert.assertNotEquals(a, b);
		a.recycle();
		b.recycle();
	}

	@Test
	public void equalsObject_differentRtl_returnsFalse() {
		TextToken a = TextToken.obtain();
		a.mRtl = false;
		TextToken b = TextToken.obtain();
		b.mRtl = true;
		Assert.assertNotEquals(a, b);
		a.recycle();
		b.recycle();
	}

	@Test
	public void equalsObject_bothCharSequencesNull_metadataMatch_returnsTrue() {
		// mCharSequence null 时跳过文本对比，仅靠元数据决定相等
		TextToken a = TextToken.obtain();
		TextToken b = TextToken.obtain();
		Assert.assertNotSame(a, b);
		Assert.assertEquals(a, b);
		a.recycle();
		b.recycle();
	}

	@Test
	public void equalsObject_oneSideCharSequenceNull_textCheckSkipped() {
		// 一侧 mCharSequence 为 null 时，equals(Object) 当前直接跳过文本比较
		TextToken a = TextToken.obtain();
		a.mCharSequence = "hello";
		a.mStart = 0;
		a.mEnd = 5;

		TextToken b = TextToken.obtain();
		// b.mCharSequence 保持 null

		Assert.assertEquals(a, b);
		a.recycle();
		b.recycle();
	}

	@Test
	public void equalsObject_differentRangeLength_returnsFalse() {
		TextToken a = TextToken.obtain();
		a.mCharSequence = "hello";
		a.mStart = 0;
		a.mEnd = 5;

		TextToken b = TextToken.obtain();
		b.mCharSequence = "hello";
		b.mStart = 0;
		b.mEnd = 4;

		Assert.assertNotEquals(a, b);
		a.recycle();
		b.recycle();
	}

	@Test
	public void equalsObject_sameWholeStringRange_returnsTrue() {
		TextToken a = TextToken.obtain();
		a.mCharSequence = "hello";
		a.mStart = 0;
		a.mEnd = 5;
		a.mType = Token.TYPE_WORD;
		a.mCategory = Token.CATEGORY_NORMAL;

		TextToken b = TextToken.obtain();
		b.mCharSequence = "hello";
		b.mStart = 0;
		b.mEnd = 5;
		b.mType = Token.TYPE_WORD;
		b.mCategory = Token.CATEGORY_NORMAL;

		Assert.assertEquals(a, b);
		a.recycle();
		b.recycle();
	}

	@Test
	public void hashCode_consistentForEqualMetadata() {
		TextToken a = TextToken.obtain();
		a.mType = Token.TYPE_WORD;
		a.mCategory = Token.CATEGORY_NORMAL;
		TextToken b = TextToken.obtain();
		b.mType = Token.TYPE_WORD;
		b.mCategory = Token.CATEGORY_NORMAL;

		Assert.assertEquals(a, b);
		Assert.assertEquals(a.hashCode(), b.hashCode());
		a.recycle();
		b.recycle();
	}

	// ============================================================
	// toString / getSemantics
	// ============================================================

	@Test
	public void toString_recycled_returnsRecycledMarker() {
		TextToken t = TextToken.obtain();
		t.recycle();
		Assert.assertEquals("<recycled>", t.toString());
	}

	@Test
	public void toString_emptyCharSequence_returnsEmptyString() {
		TextToken t = TextToken.obtain();
		Assert.assertNull(t.mCharSequence);
		Assert.assertEquals("", t.toString());

		t.mCharSequence = "";
		Assert.assertEquals("", t.toString());
		t.recycle();
	}

	@Test
	public void toString_nonEmpty_includesSemanticsAndDirectionMarker() {
		TextToken t = TextToken.obtain();
		t.mCharSequence = "hello";
		t.mStart = 0;
		t.mEnd = 5;
		t.mType = Token.TYPE_WORD;
		t.mCategory = Token.CATEGORY_NORMAL;
		t.mRtl = false;

		String s = t.toString();
		Assert.assertTrue("expected LTR marker, was: " + s, s.startsWith(">>"));
		Assert.assertTrue(s.contains("英文"));
		Assert.assertTrue(s.contains("hello"));
		t.recycle();
	}

	@Test
	public void toString_rtl_usesLeftMarker() {
		TextToken t = TextToken.obtain();
		t.mCharSequence = "ضروري";
		t.mStart = 0;
		t.mEnd = t.mCharSequence.length();
		t.mType = Token.TYPE_WORD;
		t.mCategory = Token.CATEGORY_UNKNOWN_LETTER;
		t.mRtl = true;

		Assert.assertTrue(t.toString().startsWith("<<"));
		t.recycle();
	}

	@Test
	public void getSemantics_byTypeAndCategory() {
		TextToken t = TextToken.obtain();

		t.mType = Token.TYPE_NONE;
		Assert.assertEquals("none", t.getSemantics());

		t.mType = Token.TYPE_CONTROL;
		Assert.assertEquals("空格", t.getSemantics());

		t.mType = Token.TYPE_WORD;
		t.mCategory = Token.CATEGORY_NORMAL;
		Assert.assertEquals("英文", t.getSemantics());

		t.mCategory = Token.CATEGORY_CJK;
		Assert.assertEquals("CJK", t.getSemantics());

		t.mCategory = Token.CATEGORY_NUMBER;
		Assert.assertEquals("数字", t.getSemantics());

		t.mCategory = Token.CATEGORY_UNKNOWN_LETTER;
		Assert.assertEquals("其它", t.getSemantics());

		t.recycle();
	}

	@Test(expected = IllegalStateException.class)
	public void getSemantics_unknownWordCategory_throws() {
		TextToken t = TextToken.obtain();
		t.mType = Token.TYPE_WORD;
		t.mCategory = (byte) 99; // 未定义
		try {
			t.getSemantics();
		} finally {
			t.recycle();
		}
	}

	@Test
	public void getSemantics_symbolToken_includesKinsokuAndTypefaceMarkers() {
		TextToken t = TextToken.obtain();
		t.mType = Token.TYPE_SYMBOL;
		// 设置避头 (bit 0) + 挤压左 (bit 2)
		t.mAttributes = (byte) (0x01 | 0x04);

		String s = t.getSemantics();
		Assert.assertTrue("got: " + s, s.startsWith("符号"));
		Assert.assertTrue(s.contains("避头"));
		Assert.assertTrue(s.contains("挤压左"));
		t.recycle();
	}

	@Test
	public void getSemantics_symbolToken_kinsokuTailAndStretchRight() {
		TextToken t = TextToken.obtain();
		t.mType = Token.TYPE_SYMBOL;
		// 避尾 (bit 1) + 拉伸右 (bit 5)
		t.mAttributes = (byte) (0x02 | 0x20);

		String s = t.getSemantics();
		Assert.assertTrue("got: " + s, s.contains("避尾"));
		Assert.assertTrue(s.contains("拉伸右"));
		t.recycle();
	}

	@Test
	public void getSemantics_symbolToken_squishAndStretchCombined() {
		TextToken t = TextToken.obtain();
		t.mType = Token.TYPE_SYMBOL;
		// 挤压左 (bit 2) + 拉伸左 (bit 4)
		t.mAttributes = (byte) (0x04 | 0x10);

		String s = t.getSemantics();
		Assert.assertTrue("got: " + s, s.contains("挤压左"));
		Assert.assertTrue(s.contains("拉伸左"));
		Assert.assertTrue("expected '&' joiner, got: " + s, s.contains("&"));
		t.recycle();
	}

	@Test
	public void toString_unknownTypeFallsBackToFallbackSemantics() {
		// 自定义一个未知 type（绕过标准枚举）
		TextToken t = TextToken.obtain();
		t.mType = (byte) 99;
		t.mCharSequence = "x";
		t.mStart = 0;
		t.mEnd = 1;
		String s = t.toString();
		Assert.assertTrue("got: " + s, s.contains("未知"));
		t.recycle();
	}

	// ============================================================
	// numberOfTrailingZeros — bit 工具
	// ============================================================

	@Test
	public void numberOfTrailingZeros_zeroReturnsThirtyTwo() {
		Assert.assertEquals(32, TextToken.numberOfTrailingZeros(0));
	}

	@Test
	public void numberOfTrailingZeros_powersOfTwo() {
		Assert.assertEquals(0, TextToken.numberOfTrailingZeros(1));
		Assert.assertEquals(1, TextToken.numberOfTrailingZeros(2));
		Assert.assertEquals(2, TextToken.numberOfTrailingZeros(4));
		Assert.assertEquals(3, TextToken.numberOfTrailingZeros(8));
		Assert.assertEquals(8, TextToken.numberOfTrailingZeros(1 << 8));
		Assert.assertEquals(16, TextToken.numberOfTrailingZeros(1 << 16));
		Assert.assertEquals(30, TextToken.numberOfTrailingZeros(1 << 30));
	}

	@Test
	public void numberOfTrailingZeros_oddNumbers_returnZero() {
		Assert.assertEquals(0, TextToken.numberOfTrailingZeros(1));
		Assert.assertEquals(0, TextToken.numberOfTrailingZeros(3));
		Assert.assertEquals(0, TextToken.numberOfTrailingZeros(5));
		Assert.assertEquals(0, TextToken.numberOfTrailingZeros(0x7FFFFFFF));
	}

	@Test
	public void numberOfTrailingZeros_matchesIntegerImpl_acrossBitWidths() {
		int[] samples = {1, 2, 3, 4, 5, 6, 7, 8, 12, 16, 24, 32, 64, 100, 128, 256, 1024, 0x10000, 0x12340000};
		for (int v : samples) {
			Assert.assertEquals("at " + Integer.toHexString(v),
				Integer.numberOfTrailingZeros(v),
				TextToken.numberOfTrailingZeros(v));
		}
	}
}
