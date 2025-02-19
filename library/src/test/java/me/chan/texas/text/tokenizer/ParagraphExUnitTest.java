package me.chan.texas.text.tokenizer;

import com.ibm.icu.text.Bidi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.TextBox;

public class ParagraphExUnitTest {
	private Measurer mMeasurer;
	private TextAttribute mTextAttribute;
	private PaintSet mPaintSet;

	@Before
	public void setup() {
		MockTextPaint paint = new MockTextPaint(20);
		mMeasurer = new MockMeasurer(paint);
		mTextAttribute = new TextAttribute(mMeasurer);
		mPaintSet = new PaintSet(paint);
	}

	@Test
	public void testRtl() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption)
				.setTypesetPolicy(Paragraph.TYPESET_POLICY_BIDI_TEXT);

		List<Token> tokens = new ArrayList<>();
		{
			Token token = Token.obtain();
			String msg = "hello";
			token.mType = Token.TYPE_WORD;
			token.mStart = 0;
			token.mEnd = msg.length();
			token.mCharSequence = msg;
			token.mCategory = Token.CATEGORY_NORMAL;
			tokens.add(token);
		}

		{
			Token token = Token.obtain();
			String msg = "كلمة";
			token.mType = Token.TYPE_WORD;
			token.mStart = 0;
			token.mEnd = msg.length();
			token.mCharSequence = msg;
			token.mCategory = Token.CATEGORY_UNKNOWN_LETTER;
			token.mRtl = true;
			tokens.add(token);
		}

		{
			Token token = Token.obtain();
			String msg =  "تكرار";
			token.mType = Token.TYPE_WORD;
			token.mStart = 0;
			token.mEnd = msg.length();
			token.mCharSequence = msg;
			token.mCategory = Token.CATEGORY_UNKNOWN_LETTER;
			token.mRtl = true;
			tokens.add(token);
		}

		{
			Token token = Token.obtain();
			String msg = "تم";
			token.mType = Token.TYPE_WORD;
			token.mStart = 0;
			token.mEnd = msg.length();
			token.mCharSequence = msg;
			token.mCategory = Token.CATEGORY_UNKNOWN_LETTER;
			token.mRtl = true;
			tokens.add(token);
		}

		Iterator<Token> it = tokens.iterator();
		builder.stream("hello تم تكرار كلمة", token -> {
			Token expect = it.next();
			Assert.assertEquals(expect, token);
			return null;
		});

		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void testRtlButDisable() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		List<Token> tokens = new ArrayList<>();
		{
			Token token = Token.obtain();
			String msg = "hello";
			token.mType = Token.TYPE_WORD;
			token.mStart = 0;
			token.mEnd = msg.length();
			token.mCharSequence = msg;
			token.mCategory = Token.CATEGORY_NORMAL;
			tokens.add(token);
		}

		{
			Token token = Token.obtain();
			String msg = "تم";
			token.mType = Token.TYPE_WORD;
			token.mStart = 0;
			token.mEnd = msg.length();
			token.mCharSequence = msg;
			token.mCategory = Token.CATEGORY_UNKNOWN_LETTER;
			token.mRtl = false;
			tokens.add(token);
		}

		{
			Token token = Token.obtain();
			String msg =  "تكرار";
			token.mType = Token.TYPE_WORD;
			token.mStart = 0;
			token.mEnd = msg.length();
			token.mCharSequence = msg;
			token.mCategory = Token.CATEGORY_UNKNOWN_LETTER;
			token.mRtl = false;
			tokens.add(token);
		}

		{
			Token token = Token.obtain();
			String msg = "كلمة";
			token.mType = Token.TYPE_WORD;
			token.mStart = 0;
			token.mEnd = msg.length();
			token.mCharSequence = msg;
			token.mCategory = Token.CATEGORY_UNKNOWN_LETTER;
			token.mRtl = false;
			tokens.add(token);
		}

		Iterator<Token> it = tokens.iterator();
		builder.stream("hello تم تكرار كلمة", token -> {
			Token expect = it.next();
			Assert.assertEquals(expect, token);
			return null;
		});

		Assert.assertFalse(it.hasNext());
	}


	@Test
	public void testBase() {
		String text = "hello تم تكرار كلمة";
		int start = 0;
		int end = text.length();
		char[] buffer = TextBox.CHAR_ARRAY_POOL.obtain(end - start);
		for (int i = start; i < end; ++i) {
			buffer[i - start] = text.charAt(i);
		}
		Bidi bidi = new Bidi(buffer, 0, null, 0, end - start, Bidi.LEVEL_DEFAULT_LTR);
		for (int i = 0; i < bidi.getRunCount(); ++i) {
			int runStart = bidi.getRunStart(i);
			int runLimit = bidi.getRunLimit(i);
			boolean rtl = bidi.getRunLevel(i) % 2 != 0;
			System.out.println("[" + runStart + ", " + runLimit + ") " + (rtl ? "rtl" : "ltr") + " " + text.substring(runStart, runLimit));
		}
		TextBox.CHAR_ARRAY_POOL.release(buffer);
	}
}
