package me.chan.texas.ext.markdown.parser;

import org.junit.Test;


import me.chan.texas.utils.CharStream;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ParserUnitTest {

	@Test
	public void test() {
		String[] testCases = {
				"x^2",
				"x^{n+1}",
				"x^a+b",
				"\\frac{1}{2}",
				"\\sqrt{x+1}",
				"\\sqrt[3]{27}",
				"\\sum_{i=1}^{n} i^2",
				"\\sin x + y",
				"\\sin{x+y}",
				"\\alpha + \\beta",
				"a+b*c",
				"\\left( \\frac{a}{b} \\right)",
				"\\text{当x趋近于0时}",
				"\\hat{x} + \\vec{v}",
				"\\log_2 n"
		};

		for (String testCase : testCases) {
			System.out.println("\n输入: " + testCase);
			try {
				CharStream stream = new CharStream(testCase, 0, testCase.length());
				MathParser parser = new MathParser(stream);
				MathList ast = parser.parse();

				System.out.println("✅ 解析成功!");
				System.out.println("输出: " + ast.toLatex());

			} catch (MathParseException e) {
				System.out.println("❌ 解析错误: " + e.getMessage());
			}
		}
	}
}