package me.chan.texas.text.tokenizer;

import org.junit.Test;

import java.text.BreakIterator;

public class ICUTokenUnitTest {

	@Test
	public void test() {
		String msg = "\u0067\u0308 fuck你好😜，你好真的假的？تم تكرار كلمة 'كوب' بشكل غير ضروري.";
		print(msg);
	}

	private void print(String source) {
		BreakIterator boundary = BreakIterator.getCharacterInstance();
		boundary.setText(source);

		int start = boundary.first();
		for (int end = boundary.next();
			 end != BreakIterator.DONE;
			 start = end, end = boundary.next()) {
			System.out.println("(" + start + "," + end + ")[" + source.substring(start, end) + "]");
		}
	}
}
