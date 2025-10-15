package me.chan.texas.ext.markdown.parser;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

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
				"\\sum_{i=1}^{n} i^2",
				"\\sin x + y",
				"\\alpha + \\beta",
				"a+b*c"
		};

		for (String testCase : testCases) {
			System.out.println("\n输入: " + testCase);
			try {
				Lexer lexer = new Lexer(testCase);
				List<Token> tokens = lexer.tokenize();
				System.out.println("Tokens: " + tokens);

				MathParser parser = new MathParser(tokens);
				MathList ast = parser.parse();
				System.out.println("解析成功!");
				System.out.println("输出: " + ast.toLatex());

			} catch (ParseException e) {
				System.out.println("解析错误: " + e.getMessage());
			}
		}
	}
}