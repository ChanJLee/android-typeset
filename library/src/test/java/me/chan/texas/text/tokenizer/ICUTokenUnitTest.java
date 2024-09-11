package me.chan.texas.text.tokenizer;

import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import com.ibm.icu.text.BreakIterator;

import org.junit.Test;

public class ICUTokenUnitTest {

	@Test
	public void testSimple() {
//		String msg = "\u0067\u0308 fuck你好😜，你好真的假的？تم تكرار كلمة 'كوب' بشكل غير ضروري.";
		String msg = "9.0 にほん你ご나는  Chicago에  산다.100“get on”这里指“应付，过活”，“can't get on without”即为“失去……便寸步难行，不能没有……”。这里Laurie在极力表明Jo和他在一起是“全村的期望”，他不能没有她。don't. 9th 18岁 R&B";
//		String msg = "'hello";
		print(msg);
		print2(msg);
		print3(msg);
		print4(msg);
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

	private void print3(String source) {
		System.out.println("========== word instance");
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(source);

		int start = boundary.first();
		for (int end = boundary.next();
			 end != BreakIterator.DONE;
			 start = end, end = boundary.next()) {
			System.out.println("(" + start + "," + end + ")[" + source.substring(start, end) + "] + " + boundary.getRuleStatus());
		}
	}

	private void print4(String source) {
		System.out.println("========== word instance, my");
		BreakIterator boundary = WordStream.getWordBreakIterator();
		boundary.setText(source);

		int start = boundary.first();
		for (int end = boundary.next();
			 end != BreakIterator.DONE;
			 start = end, end = boundary.next()) {
			System.out.println("(" + start + "," + end + ")[" + source.substring(start, end) + "] + " + boundary.getRuleStatus());
		}
	}
}
