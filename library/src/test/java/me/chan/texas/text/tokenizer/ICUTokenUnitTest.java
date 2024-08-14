package me.chan.texas.text.tokenizer;

import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import com.ibm.icu.text.BreakIterator;

import org.junit.Test;

public class ICUTokenUnitTest {

	@Test
	public void test() {
		String msg = "\u0067\u0308 fuck你好😜，你好真的假的？تم تكرار كلمة 'كوب' بشكل غير ضروري.";
		print(msg);
		print2(msg);
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

	private void print2(String source) {
		Bidi bidi = new Bidi();
		bidi.setPara(source, Bidi.LEVEL_DEFAULT_LTR, null);
		for (int i = 0; i < bidi.countRuns(); ++i) {
			BidiRun run = bidi.getVisualRun(i);
			System.out.println(run + "-[" + source.substring(run.getStart(), run.getLimit()) + "]");
		}
	}
}
