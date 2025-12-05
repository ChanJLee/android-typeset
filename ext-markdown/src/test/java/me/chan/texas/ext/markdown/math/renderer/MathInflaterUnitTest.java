package me.chan.texas.ext.markdown.math.renderer;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.MathParseException;
import me.chan.texas.ext.markdown.math.ast.MathParser;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;
import me.chan.texas.utils.CharStream;

import static org.junit.Assert.*;

/**
 * LaTeX 数学公式解析器全面测试 - 完整版（适配节点优化）
 * <p>
 * 测试目标：
 * 1. 覆盖 bnf_math.txt 中所有基础语法元素
 * 2. 验证 AST 结构的正确性
 * 3. 验证 AST 转换为 RenderNode 后的结构（优化后）
 * 4. 测试 MathFontOptions 中的符号查找（防止crash）
 * 5. 验证嵌套和复杂表达式
 * <p>
 * 注意：由于引入了节点优化，单个子节点的容器节点会被优化掉
 */
public class MathInflaterUnitTest {

	private MathRendererInflater inflater;
	private MathPaint.Styles defaultStyles;
	private MathPaint defaultPaint;

	@Before
	public void setUp() {
		inflater = new MathRendererInflater();
		TexasPaint texasPaint = new TexasPaintImpl();
		texasPaint.reset(new PaintSet(new MockTextPaint(1)));
		defaultPaint = new MathPaintImpl(texasPaint);
		defaultStyles = new MathPaint.Styles(defaultPaint);
	}

	// ============================================================
	// 辅助方法
	// ============================================================

	/**
	 * 解析输入并返回 AST
	 */
	private MathList parse(String input) throws MathParseException {
		CharStream stream = new CharStream(input, 0, input.length());
		MathParser parser = new MathParser(stream);
		return parser.parse();
	}

	/**
	 * 解析并渲染，返回 RendererNode
	 */
	private RendererNode parseAndRender(String input) throws MathParseException {
		MathList ast = parse(input);
		RendererNode node = inflater.inflate(defaultStyles, ast);
		// 触发 measure 以便获取子节点结构
		node.measure(defaultPaint);
		return node;
	}

	/**
	 * 反射辅助：获取私有字段
	 */
	private Object getPrivateField(Object obj, String fieldName) {
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			// 尝试从父类获取
			try {
				Field field = obj.getClass().getSuperclass().getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.get(obj);
			} catch (Exception e2) {
				throw new RuntimeException("无法获取字段: " + fieldName, e2);
			}
		}
	}

	/**
	 * 验证渲染不会crash
	 */
	private void assertRenderSuccess(String input) {
		try {
			RendererNode node = parseAndRender(input);
			assertNotNull("渲染节点不应为null: " + input, node);
			System.out.println("✅ " + input + " 渲染成功");
		} catch (MathParseException e) {
			fail("解析失败: " + input + " - " + e.pretty());
		} catch (Exception e) {
			fail("渲染失败（可能是符号查找crash）: " + input + " - " + e.getMessage());
		}
	}

	// ============================================================
	// 链式 API - RendererNode 验证器
	// ============================================================

	/**
	 * RendererNode 基础验证器
	 */
	static class RendererNodeAsserter {
		protected final RendererNode node;

		RendererNodeAsserter(RendererNode node) {
			this.node = node;
			assertNotNull("RendererNode不应为null", node);
		}

		RendererNodeAsserter isType(Class<? extends RendererNode> expectedType) {
			assertTrue("节点类型应该是 " + expectedType.getSimpleName() + " 但实际是 " + node.getClass().getSimpleName(),
					expectedType.isInstance(node));
			return this;
		}

		LinearGroupNodeAsserter asLinearGroup() {
			isType(LinearGroupNode.class);
			return new LinearGroupNodeAsserter((LinearGroupNode) node);
		}

		DecorGroupNodeAsserter asDecorGroup() {
			isType(DecorGroupNode.class);
			return new DecorGroupNodeAsserter((DecorGroupNode) node);
		}

		TextNodeAsserter asTextNode() {
			isType(TextNode.class);
			return new TextNodeAsserter((TextNode) node);
		}

		SpaceNodeAsserter asSpaceNode() {
			isType(SpaceNode.class);
			return new SpaceNodeAsserter((SpaceNode) node);
		}

		FractionNodeAsserter asFractionNode() {
			isType(FractionNode.class);
			return new FractionNodeAsserter((FractionNode) node);
		}

		SqrtNodeAsserter asSqrtNode() {
			isType(SqrtNode.class);
			return new SqrtNodeAsserter((SqrtNode) node);
		}

		GridGroupNodeAsserter asGridGroupNode() {
			isType(GridGroupNode.class);
			return new GridGroupNodeAsserter((GridGroupNode) node);
		}

		SymbolNodeAsserter asSymbolNode() {
			isType(SymbolNode.class);
			return new SymbolNodeAsserter((SymbolNode) node);
		}

		AccentNodeAsserter asAccentNode() {
			isType(AccentNode.class);
			return new AccentNodeAsserter((AccentNode) node);
		}

		PhantomNodeAsserter asPhantomNode() {
			isType(PhantomNode.class);
			return new PhantomNodeAsserter((PhantomNode) node);
		}

		RendererNodeAsserter and() {
			return this;
		}
	}

	/**
	 * LinearGroupNode 验证器
	 */
	static class LinearGroupNodeAsserter extends RendererNodeAsserter {
		private final LinearGroupNode linearGroup;
		private int currentIndex = 0;

		LinearGroupNodeAsserter(LinearGroupNode node) {
			super(node);
			this.linearGroup = node;
		}

		LinearGroupNodeAsserter hasChildCount(int expectedCount) {
			assertEquals("子节点数量", expectedCount, linearGroup.getChildCount());
			return this;
		}

		RendererNodeAsserter childAt(int index) {
			assertTrue("索引 " + index + " 超出范围", index < linearGroup.getChildCount());
			return new RendererNodeAsserter(linearGroup.getChildAt(index));
		}

		RendererNodeAsserter nextChild() {
			return childAt(currentIndex++);
		}

		LinearGroupNodeAsserter and() {
			return this;
		}
	}

	/**
	 * DecorGroupNode 验证器
	 */
	static class DecorGroupNodeAsserter extends RendererNodeAsserter {
		private final DecorGroupNode decorGroup;
		private final Object builder;

		DecorGroupNodeAsserter(DecorGroupNode node) {
			super(node);
			this.decorGroup = node;
			this.builder = getPrivateField(node, "mBuilder");
		}

		private Object getPrivateField(Object obj, String fieldName) {
			try {
				Field field = obj.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.get(obj);
			} catch (Exception e) {
				throw new RuntimeException("无法获取字段: " + fieldName, e);
			}
		}

		RendererNodeAsserter center() {
			RendererNode center = (RendererNode) getPrivateField(builder, "center");
			assertNotNull("center节点不应为null", center);
			return new RendererNodeAsserter(center);
		}

		DecorGroupNodeAsserter hasLeftTop() {
			RendererNode leftTop = (RendererNode) getPrivateField(builder, "leftTop");
			assertNotNull("应该有leftTop节点", leftTop);
			return this;
		}

		RendererNodeAsserter leftTop() {
			RendererNode leftTop = (RendererNode) getPrivateField(builder, "leftTop");
			assertNotNull("leftTop节点不应为null", leftTop);
			return new RendererNodeAsserter(leftTop);
		}

		DecorGroupNodeAsserter hasRightTop() {
			RendererNode rightTop = (RendererNode) getPrivateField(builder, "rightTop");
			assertNotNull("应该有rightTop节点", rightTop);
			return this;
		}

		RendererNodeAsserter rightTop() {
			RendererNode rightTop = (RendererNode) getPrivateField(builder, "rightTop");
			assertNotNull("rightTop节点不应为null", rightTop);
			return new RendererNodeAsserter(rightTop);
		}

		DecorGroupNodeAsserter hasRightBottom() {
			RendererNode rightBottom = (RendererNode) getPrivateField(builder, "rightBottom");
			assertNotNull("应该有rightBottom节点", rightBottom);
			return this;
		}

		RendererNodeAsserter rightBottom() {
			RendererNode rightBottom = (RendererNode) getPrivateField(builder, "rightBottom");
			assertNotNull("rightBottom节点不应为null", rightBottom);
			return new RendererNodeAsserter(rightBottom);
		}

		DecorGroupNodeAsserter hasLeft() {
			RendererNode left = (RendererNode) getPrivateField(builder, "left");
			assertNotNull("应该有left节点", left);
			return this;
		}

		RendererNodeAsserter left() {
			RendererNode left = (RendererNode) getPrivateField(builder, "left");
			assertNotNull("left节点不应为null", left);
			return new RendererNodeAsserter(left);
		}

		DecorGroupNodeAsserter hasTop() {
			RendererNode top = (RendererNode) getPrivateField(builder, "top");
			assertNotNull("应该有top节点", top);
			return this;
		}

		RendererNodeAsserter top() {
			RendererNode top = (RendererNode) getPrivateField(builder, "top");
			assertNotNull("top节点不应为null", top);
			return new RendererNodeAsserter(top);
		}

		DecorGroupNodeAsserter hasBottom() {
			RendererNode bottom = (RendererNode) getPrivateField(builder, "bottom");
			assertNotNull("应该有bottom节点", bottom);
			return this;
		}

		RendererNodeAsserter bottom() {
			RendererNode bottom = (RendererNode) getPrivateField(builder, "bottom");
			assertNotNull("bottom节点不应为null", bottom);
			return new RendererNodeAsserter(bottom);
		}

		DecorGroupNodeAsserter noLeftTop() {
			RendererNode leftTop = (RendererNode) getPrivateField(builder, "leftTop");
			assertNull("不应该有leftTop节点", leftTop);
			return this;
		}

		DecorGroupNodeAsserter noRightTop() {
			RendererNode rightTop = (RendererNode) getPrivateField(builder, "rightTop");
			assertNull("不应该有rightTop节点", rightTop);
			return this;
		}

		DecorGroupNodeAsserter noRightBottom() {
			RendererNode rightBottom = (RendererNode) getPrivateField(builder, "rightBottom");
			assertNull("不应该有rightBottom节点", rightBottom);
			return this;
		}

		DecorGroupNodeAsserter noLeft() {
			RendererNode left = (RendererNode) getPrivateField(builder, "left");
			assertNull("不应该有left节点", left);
			return this;
		}

		DecorGroupNodeAsserter and() {
			return this;
		}
	}

	/**
	 * TextNode 验证器
	 */
	static class TextNodeAsserter extends RendererNodeAsserter {
		private final TextNode textNode;

		TextNodeAsserter(TextNode node) {
			super(node);
			this.textNode = node;
		}

		TextNodeAsserter hasContent(String expectedContent) {
			String content = (String) getPrivateField(textNode, "mContent");
			assertEquals("文本内容", expectedContent, content);
			return this;
		}

		private Object getPrivateField(Object obj, String fieldName) {
			try {
				Field field = obj.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.get(obj);
			} catch (Exception e) {
				throw new RuntimeException("无法获取字段: " + fieldName, e);
			}
		}

		TextNodeAsserter and() {
			return this;
		}
	}

	/**
	 * SpaceNode 验证器
	 */
	static class SpaceNodeAsserter extends RendererNodeAsserter {
		SpaceNodeAsserter(SpaceNode node) {
			super(node);
		}

		SpaceNodeAsserter and() {
			return this;
		}
	}

	/**
	 * FractionNode 验证器
	 */
	static class FractionNodeAsserter extends RendererNodeAsserter {
		private final FractionNode fractionNode;

		FractionNodeAsserter(FractionNode node) {
			super(node);
			this.fractionNode = node;
		}

		RendererNodeAsserter numerator() {
			RendererNode numerator = (RendererNode) getPrivateField(fractionNode, "mNumerator");
			assertNotNull("分子不应为null", numerator);
			return new RendererNodeAsserter(numerator);
		}

		RendererNodeAsserter denominator() {
			RendererNode denominator = (RendererNode) getPrivateField(fractionNode, "mDenominator");
			assertNotNull("分母不应为null", denominator);
			return new RendererNodeAsserter(denominator);
		}

		private Object getPrivateField(Object obj, String fieldName) {
			try {
				Field field = obj.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.get(obj);
			} catch (Exception e) {
				throw new RuntimeException("无法获取字段: " + fieldName, e);
			}
		}

		FractionNodeAsserter and() {
			return this;
		}
	}

	/**
	 * SqrtNode 验证器
	 */
	static class SqrtNodeAsserter extends RendererNodeAsserter {
		private final SqrtNode sqrtNode;

		SqrtNodeAsserter(SqrtNode node) {
			super(node);
			this.sqrtNode = node;
		}

		RendererNodeAsserter content() {
			RendererNode content = (RendererNode) getPrivateField(sqrtNode, "mContent");
			assertNotNull("根式内容不应为null", content);
			return new RendererNodeAsserter(content);
		}

		SqrtNodeAsserter hasRoot() {
			RendererNode root = (RendererNode) getPrivateField(sqrtNode, "mRoot");
			assertNotNull("应该有根指数", root);
			return this;
		}

		RendererNodeAsserter root() {
			RendererNode root = (RendererNode) getPrivateField(sqrtNode, "mRoot");
			assertNotNull("根指数不应为null", root);
			return new RendererNodeAsserter(root);
		}

		private Object getPrivateField(Object obj, String fieldName) {
			try {
				Field field = obj.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.get(obj);
			} catch (Exception e) {
				throw new RuntimeException("无法获取字段: " + fieldName, e);
			}
		}

		SqrtNodeAsserter and() {
			return this;
		}
	}

	/**
	 * GridGroupNode 验证器
	 */
	static class GridGroupNodeAsserter extends RendererNodeAsserter {
		private final GridGroupNode gridNode;

		GridGroupNodeAsserter(GridGroupNode node) {
			super(node);
			this.gridNode = node;
		}

		@SuppressWarnings("unchecked")
		GridGroupNodeAsserter hasRowCount(int expectedCount) {
			List<List<RendererNode>> rows = (List<List<RendererNode>>) getPrivateField(gridNode, "mRows");
			assertEquals("行数", expectedCount, rows.size());
			return this;
		}

		private Object getPrivateField(Object obj, String fieldName) {
			try {
				Field field = obj.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.get(obj);
			} catch (Exception e) {
				throw new RuntimeException("无法获取字段: " + fieldName, e);
			}
		}

		GridGroupNodeAsserter and() {
			return this;
		}
	}

	/**
	 * SymbolNode 验证器
	 */
	static class SymbolNodeAsserter extends RendererNodeAsserter {
		SymbolNodeAsserter(SymbolNode node) {
			super(node);
		}

		SymbolNodeAsserter hasContent(String expectedContent) {
			SymbolNode symbolNode = (SymbolNode) node;
			assertEquals("符号内容", expectedContent, symbolNode.mSymbol.unicode);
			return this;
		}

		SymbolNodeAsserter and() {
			return this;
		}
	}

	/**
	 * AccentNode 验证器
	 */
	static class AccentNodeAsserter extends RendererNodeAsserter {
		private final AccentNode accentNode;

		AccentNodeAsserter(AccentNode node) {
			super(node);
			this.accentNode = node;
		}

		RendererNodeAsserter content() {
			RendererNode content = (RendererNode) getPrivateField(accentNode, "mContent");
			assertNotNull("重音内容不应为null", content);
			return new RendererNodeAsserter(content);
		}

		private Object getPrivateField(Object obj, String fieldName) {
			try {
				Field field = obj.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.get(obj);
			} catch (Exception e) {
				throw new RuntimeException("无法获取字段: " + fieldName, e);
			}
		}

		AccentNodeAsserter and() {
			return this;
		}
	}

	/**
	 * PhantomNode 验证器
	 */
	static class PhantomNodeAsserter extends RendererNodeAsserter {
		PhantomNodeAsserter(PhantomNode node) {
			super(node);
		}

		PhantomNodeAsserter and() {
			return this;
		}
	}

	/**
	 * 创建 RendererNode 验证器入口
	 */
	private RendererNodeAsserter assertRenderNode(RendererNode node) {
		return new RendererNodeAsserter(node);
	}

	// ============================================================
	// Part 1: 基础元素测试 - 完善版
	// ============================================================

	@Test
	public void test_01_01_Number_Complete() throws MathParseException {
		System.out.println("\n=== Part 1.1: 数字 - 完整测试 ===");

		// <number> ::= <digit> { <digit> } [ "." <digit> { <digit> } ]
		// 测试单个数字
		RendererNode root = parseAndRender("1");
		assertRenderNode(root).asTextNode().hasContent("1");

		root = parseAndRender("0");
		assertRenderNode(root).asTextNode().hasContent("0");

		// 测试多位整数
		root = parseAndRender("123");
		assertRenderNode(root).asTextNode().hasContent("123");

		root = parseAndRender("9876543210");
		assertRenderNode(root).asTextNode().hasContent("9876543210");

		// 测试小数 - 带前导数字
		assertRenderSuccess("3.14");
		assertRenderSuccess("0.5");
		assertRenderSuccess("123.456");
		assertRenderSuccess("0.0001");

		// 测试小数在表达式中
		assertRenderSuccess("3.14+2.71");
		assertRenderSuccess("1.5x");

		System.out.println("✅ 数字完整测试通过");
	}

	@Test
	public void test_01_02_Variable_Complete() throws MathParseException {
		System.out.println("\n=== Part 1.2: 变量 - 完整测试 ===");

		// <variable> ::= <letter> { <letter> } [ <prime_suffix> ]
		// 测试单字母变量
		RendererNode root = parseAndRender("x");
		assertRenderNode(root).asTextNode().hasContent("x");

		root = parseAndRender("X");
		assertRenderNode(root).asTextNode().hasContent("X");

		// 测试多字母变量
		root = parseAndRender("abc");
		assertRenderNode(root).asTextNode().hasContent("abc");

		assertRenderSuccess("xyz");
		assertRenderSuccess("ABC");
		assertRenderSuccess("function");

		// <prime_suffix> ::= "'" { "'" }
		// 测试带撇号的变量
		assertRenderSuccess("f'");
		assertRenderSuccess("g''");
		assertRenderSuccess("h'''");
		assertRenderSuccess("x'");

		// 测试变量在表达式中
		assertRenderSuccess("x+y");
		assertRenderSuccess("abc'");

		System.out.println("✅ 变量完整测试通过");
	}

	@Test
	public void test_01_03_GreekLetters_Complete() throws MathParseException {
		System.out.println("\n=== Part 1.3: 希腊字母 - 完整测试 ===");

		// <greek_letter> 所有小写希腊字母
		String[] lowerGreek = {
			"\\alpha", "\\beta", "\\gamma", "\\delta", "\\epsilon", "\\varepsilon",
			"\\zeta", "\\eta", "\\theta", "\\vartheta", "\\iota", "\\kappa",
			"\\lambda", "\\mu", "\\nu", "\\xi", "\\pi", "\\varpi", "\\rho", "\\varrho",
			"\\sigma", "\\varsigma", "\\tau", "\\upsilon", "\\phi", "\\varphi",
			"\\chi", "\\psi", "\\omega"
		};
		for (String letter : lowerGreek) {
			assertRenderSuccess(letter);
		}

		// 所有大写希腊字母
		String[] upperGreek = {
			"\\Gamma", "\\Delta", "\\Theta", "\\Lambda", "\\Xi", "\\Pi",
			"\\Sigma", "\\Upsilon", "\\Phi", "\\Psi", "\\Omega"
		};
		for (String letter : upperGreek) {
			assertRenderSuccess(letter);
		}

		// 无穷 (在 greek_letter 中定义)
		assertRenderSuccess("\\infty");

		// <greek_letter_variable> ::= <greek_letter> [ <prime_suffix> ]
		// 测试带撇号的希腊字母
		assertRenderSuccess("\\alpha'");
		assertRenderSuccess("\\beta''");
		assertRenderSuccess("\\gamma'''");

		// 测试带上下标的希腊字母
		assertRenderSuccess("\\alpha_1");
		assertRenderSuccess("\\beta^2");
		assertRenderSuccess("\\gamma_i^2");
		assertRenderSuccess("\\delta^{n+1}_{k}");

		System.out.println("✅ 希腊字母完整测试通过");
	}

	@Test
	public void test_01_04_SpecialLetterVariables_Complete() throws MathParseException {
		System.out.println("\n=== Part 1.4: 特殊字母变量 - 完整测试 ===");

		// <special_letter_variable> - 类变量符号，可被一元运算符修饰
		String[] specialLetters = {
			"\\hbar",      // ℏ 约化普朗克常数
			"\\nabla",     // ∇ 梯度算子
			"\\partial",   // ∂ 偏导数符号
			"\\ell",       // ℓ 脚本小写L
			"\\wp",        // ℘ 魏尔斯特拉斯函数
			"\\Re",        // ℜ 实部
			"\\Im",        // ℑ 虚部
			"\\aleph"      // ℵ 阿列夫数
		};
		for (String letter : specialLetters) {
			assertRenderSuccess(letter);
		}

		// 测试一元运算符修饰特殊字母变量
		assertRenderSuccess("-\\nabla");
		assertRenderSuccess("-\\partial");
		assertRenderSuccess("+\\hbar");
		assertRenderSuccess("\\pm\\nabla");
		assertRenderSuccess("\\mp\\partial");

		// 测试带上下标的特殊字母变量
		assertRenderSuccess("\\nabla^2");
		assertRenderSuccess("\\partial_t");
		assertRenderSuccess("\\partial_x^2");
		assertRenderSuccess("\\hbar^2");

		// 测试在表达式中
		assertRenderSuccess("\\nabla f");
		assertRenderSuccess("\\partial_x f");

		System.out.println("✅ 特殊字母变量完整测试通过");
	}

	@Test
	public void test_01_05_SpecialSymbols_Complete() throws MathParseException {
		System.out.println("\n=== Part 1.5: 特殊符号 - 完整测试 ===");

		// <special_symbol> - 不能被一元运算符修饰
		// 省略号
		assertRenderSuccess("\\dots");
		assertRenderSuccess("\\ldots");
		assertRenderSuccess("\\cdots");
		assertRenderSuccess("\\vdots");
		assertRenderSuccess("\\ddots");

		// 角度符号
		assertRenderSuccess("\\angle");

		// 逻辑标记符号
		assertRenderSuccess("\\therefore");
		assertRenderSuccess("\\because");

		// 量词
		assertRenderSuccess("\\forall");
		assertRenderSuccess("\\exists");
		assertRenderSuccess("\\nexists");

		// 空集
		assertRenderSuccess("\\emptyset");
		assertRenderSuccess("\\varnothing");

		// 测试特殊符号带上下标（可以带上下标）
		assertRenderSuccess("\\angle_1");
		assertRenderSuccess("\\dots^{n}");
		assertRenderSuccess("\\forall_{x}");
		assertRenderSuccess("\\exists^{y}");

		// 测试在表达式中
		assertRenderSuccess("1,2,\\dots,n");
		assertRenderSuccess("\\forall x");
		assertRenderSuccess("\\exists y");

		System.out.println("✅ 特殊符号完整测试通过");
	}

	// ============================================================
	// Part 2: 运算符测试 - 完善版
	// ============================================================

	@Test
	public void test_02_01_UnaryOperators_Complete() throws MathParseException {
		System.out.println("\n=== Part 2.1: 一元运算符 - 完整测试 ===");

		// <unary_op> ::= "+" | "-" | "\pm" | "\mp"
		// 测试所有一元运算符
		RendererNode root = parseAndRender("-x");
		DecorGroupNodeAsserter termDecor = assertRenderNode(root).asDecorGroup();
		termDecor.hasLeft();
		termDecor.left().asSymbolNode().hasContent("−");
		termDecor.center().asTextNode().hasContent("x");

		assertRenderSuccess("+x");
		assertRenderSuccess("\\pm x");
		assertRenderSuccess("\\mp x");

		// 测试一元运算符修饰数字
		assertRenderSuccess("-1");
		assertRenderSuccess("+5");
		assertRenderSuccess("\\pm 3");

		// 测试一元运算符修饰分组
		assertRenderSuccess("-{x+y}");
		assertRenderSuccess("+{a-b}");
		assertRenderSuccess("\\pm{x}");

		// 测试一元运算符修饰希腊字母
		assertRenderSuccess("-\\alpha");
		assertRenderSuccess("+\\beta");

		// 测试一元运算符修饰分式
		assertRenderSuccess("-\\frac{1}{2}");
		assertRenderSuccess("+\\frac{a}{b}");

		// 测试一元运算符修饰根式
		assertRenderSuccess("-\\sqrt{x}");
		assertRenderSuccess("+\\sqrt{2}");

		// 测试一元运算符修饰定界符
		assertRenderSuccess("-\\left(x+y\\right)");

		System.out.println("✅ 一元运算符完整测试通过");
	}

	@Test
	public void test_02_02_BinaryOperators_Complete() throws MathParseException {
		System.out.println("\n=== Part 2.2: 二元运算符 - 完整测试 ===");

		// <binary_op> 完整列表
		// 基本运算符
		String[] basicOps = {"+", "-", "*", "/", "|"};
		for (String op : basicOps) {
			assertRenderSuccess("a" + op + "b");
		}

		// LaTeX 运算符（需要空格分隔）
		String[] latexOps = {
			"\\times", "\\cdot", "\\div", "\\pm", "\\mp"
		};
		for (String op : latexOps) {
			assertRenderSuccess("a" + op + " b");
		}

		// 关系运算符（LaTeX 命令需要空格分隔）
		String[] relOps = {
			"=", "\\neq", "\\equiv", "\\approx", "\\cong", "\\sim",
			"<", ">", "\\le", "\\ge", "\\leq", "\\geq", "\\ll", "\\gg"
		};
		for (String op : relOps) {
			String input = op.startsWith("\\") ? "a" + op + " b" : "a" + op + "b";
			assertRenderSuccess(input);
		}

		// 集合运算符（LaTeX 命令需要空格分隔）
		String[] setOps = {
			"\\in", "\\notin", "\\subset", "\\supset", "\\subseteq", "\\supseteq",
			"\\cup", "\\cap", "\\wedge", "\\vee"
		};
		for (String op : setOps) {
			assertRenderSuccess("a" + op + " b");
		}

		// 箭头运算符（LaTeX 命令需要空格分隔）
		String[] arrowOps = {
			"\\to", "\\rightarrow", "\\leftarrow", "\\leftrightarrow",
			"\\Rightarrow", "\\Leftarrow", "\\Leftrightarrow",
			"\\implies", "\\iff"
		};
		for (String op : arrowOps) {
			assertRenderSuccess("A" + op + " B");
		}

		// 几何运算符
		assertRenderSuccess("a\\perp b");
		assertRenderSuccess("a\\parallel b");
		assertRenderSuccess("a\\mid b");

		// 验证结构
		RendererNode root = parseAndRender("a+b");
		LinearGroupNodeAsserter exprGroup = assertRenderNode(root).asLinearGroup();
		exprGroup.hasChildCount(5); // Term(a), Space, BinOp(+), Space, Term(b)
		exprGroup.childAt(0).asTextNode().hasContent("a");
		exprGroup.childAt(1).asSpaceNode();
		exprGroup.childAt(2).asSymbolNode().hasContent("+");
		exprGroup.childAt(3).asSpaceNode();
		exprGroup.childAt(4).asTextNode().hasContent("b");

		System.out.println("✅ 二元运算符完整测试通过");
	}

	@Test
	public void test_02_03_PostfixOperators_Complete() throws MathParseException {
		System.out.println("\n=== Part 2.3: 后缀运算符 - 完整测试 ===");

		// <postfix_op> ::= "!"
		// 测试基本阶乘
		assertRenderSuccess("n!");
		assertRenderSuccess("5!");
		assertRenderSuccess("x!");
		assertRenderSuccess("0!");

		// 测试阶乘与上下标的结合
		assertRenderSuccess("n^2!");
		assertRenderSuccess("n_i!");
		assertRenderSuccess("n_i^2!");

		// 测试阶乘在表达式中
		assertRenderSuccess("n!+1");
		assertRenderSuccess("n!-1");
		assertRenderSuccess("n!m");
		assertRenderSuccess("n!m!");

		// 测试阶乘在分式中
		assertRenderSuccess("\\frac{n!}{k!}");
		assertRenderSuccess("\\frac{n!}{k!(n-k)!}");

		// 测试括号内表达式的阶乘
		assertRenderSuccess("\\left(n-1\\right)!");
		assertRenderSuccess("\\left(n+1\\right)!");

		// 测试希腊字母的阶乘
		assertRenderSuccess("\\Gamma!");

		System.out.println("✅ 后缀运算符完整测试通过");
	}

	// ============================================================
	// Part 3: 标点符号测试 - 完善版
	// ============================================================

	@Test
	public void test_03_01_Punctuation_Complete() throws MathParseException {
		System.out.println("\n=== Part 3.1: 标点符号 - 完整测试 ===");

		// <punctuation> ::= "," | ";"
		// 测试逗号
		RendererNode root = parseAndRender("a,b");
		LinearGroupNodeAsserter exprGroup = assertRenderNode(root).asLinearGroup();
		exprGroup.hasChildCount(3);
		exprGroup.childAt(0).asTextNode().hasContent("a");
		exprGroup.childAt(1).asTextNode().hasContent(",");
		exprGroup.childAt(2).asTextNode().hasContent("b");

		// 测试多个逗号
		assertRenderSuccess("a,b,c");
		assertRenderSuccess("1,2,3,4,5");

		// 测试分号
		assertRenderSuccess("a;b");
		assertRenderSuccess("x;y;z");

		// 测试在定界符中
		assertRenderSuccess("f\\left(x,y\\right)");
		assertRenderSuccess("f\\left(x,y,z\\right)");
		assertRenderSuccess("\\left\\{x,y,z\\right\\}");
		assertRenderSuccess("\\left[a;b\\right]");

		// 测试在集合表示中
		assertRenderSuccess("\\left\\{x|x>0\\right\\}");

		System.out.println("✅ 标点符号完整测试通过");
	}

	// ============================================================
	// Part 4: 原子表达式测试 - 完善版
	// ============================================================

	@Test
	public void test_04_01_Group_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.1: 分组 - 完整测试 ===");

		// <group> ::= "{" <math_list> "}"
		assertRenderSuccess("{x}");
		assertRenderSuccess("{a+b}");
		assertRenderSuccess("{x^2}");

		// 测试嵌套分组
		assertRenderSuccess("{{x}}");
		assertRenderSuccess("{{{x}}}");
		assertRenderSuccess("{a{b}c}");

		// 测试空分组 - 如果支持的话
		// assertRenderSuccess("{}");

		// 测试分组在表达式中
		assertRenderSuccess("{a}+{b}");
		assertRenderSuccess("{a+b}^2");
		assertRenderSuccess("{a+b}_{i}");

		System.out.println("✅ 分组完整测试通过");
	}

	@Test
	public void test_04_02_Frac_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.2: 分式 - 完整测试 ===");

		// <frac> ::= "\frac" | "\dfrac" | "\tfrac" | "\cfrac"
		// 测试 \frac
		RendererNode root = parseAndRender("\\frac{1}{2}");
		FractionNodeAsserter frac = assertRenderNode(root).asFractionNode();
		frac.numerator().asTextNode().hasContent("1");
		frac.denominator().asTextNode().hasContent("2");

		// 测试 \dfrac
		assertRenderSuccess("\\dfrac{1}{2}");
		assertRenderSuccess("\\dfrac{a}{b}");

		// 测试 \tfrac
		assertRenderSuccess("\\tfrac{1}{2}");
		assertRenderSuccess("\\tfrac{a}{b}");

		// 测试 \cfrac
		assertRenderSuccess("\\cfrac{1}{2}");
		assertRenderSuccess("\\cfrac{a}{b}");

		// 测试复杂分式
		assertRenderSuccess("\\frac{x+y}{x-y}");
		assertRenderSuccess("\\frac{a^2}{b^2}");
		assertRenderSuccess("\\frac{\\alpha}{\\beta}");

		// 测试嵌套分式
		root = parseAndRender("\\frac{\\frac{1}{2}}{\\frac{3}{4}}");
		FractionNodeAsserter outerFrac = assertRenderNode(root).asFractionNode();
		outerFrac.numerator().asFractionNode();
		outerFrac.denominator().asFractionNode();

		// 测试分式带上下标
		assertRenderSuccess("\\frac{a}{b}^2");
		assertRenderSuccess("\\frac{a}{b}_i");
		assertRenderSuccess("\\frac{a}{b}^2_i");

		System.out.println("✅ 分式完整测试通过");
	}

	@Test
	public void test_04_03_Binom_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.3: 二项式系数 - 完整测试 ===");

		// <binom> ::= "\binom" | "\dbinom" | "\tbinom"
		// 测试 \binom
		assertRenderSuccess("\\binom{n}{k}");
		assertRenderSuccess("\\binom{5}{2}");
		assertRenderSuccess("\\binom{n+1}{k}");
		assertRenderSuccess("\\binom{n}{k+1}");

		// 测试 \dbinom
		assertRenderSuccess("\\dbinom{n}{k}");
		assertRenderSuccess("\\dbinom{10}{5}");

		// 测试 \tbinom
		assertRenderSuccess("\\tbinom{n}{k}");
		assertRenderSuccess("\\tbinom{3}{1}");

		// 测试带上下标
		assertRenderSuccess("\\binom{n}{k}^2");
		assertRenderSuccess("\\binom{n}{k}_i");
		assertRenderSuccess("\\binom{n}{k}^2_i");

		// 测试在表达式中
		assertRenderSuccess("\\binom{n}{k}+1");
		assertRenderSuccess("2\\binom{n}{k}");
		assertRenderSuccess("\\binom{n}{0}+\\binom{n}{1}");

		// 测试嵌套
		assertRenderSuccess("\\frac{\\binom{n}{k}}{2}");
		assertRenderSuccess("\\binom{\\binom{n}{k}}{2}");
		assertRenderSuccess("\\sqrt{\\binom{n}{k}}");

		System.out.println("✅ 二项式系数完整测试通过");
	}

	@Test
	public void test_04_04_Sqrt_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.4: 根式 - 完整测试 ===");

		// <sqrt> ::= "\sqrt" "{" <math_list> "}"
		//          | "\sqrt" "[" <math_list> "]" "{" <math_list> "}"
		// 测试基本根式
		RendererNode root = parseAndRender("\\sqrt{x}");
		SqrtNodeAsserter sqrt = assertRenderNode(root).asSqrtNode();
		sqrt.content().asTextNode().hasContent("x");

		assertRenderSuccess("\\sqrt{2}");
		assertRenderSuccess("\\sqrt{a+b}");

		// 测试带根指数
		assertRenderSuccess("\\sqrt[3]{27}");
		assertRenderSuccess("\\sqrt[n]{x}");
		assertRenderSuccess("\\sqrt[3]{x+y}");
		assertRenderSuccess("\\sqrt[n+1]{x}");

		// 验证带根指数的结构
		root = parseAndRender("\\sqrt[3]{8}");
		sqrt = assertRenderNode(root).asSqrtNode();
		sqrt.hasRoot();
		sqrt.root().asTextNode().hasContent("3");
		sqrt.content().asTextNode().hasContent("8");

		// 测试复杂内容
		assertRenderSuccess("\\sqrt{x^2+y^2}");
		assertRenderSuccess("\\sqrt{\\frac{a}{b}}");
		assertRenderSuccess("\\sqrt{\\sqrt{x}}");

		// 测试根式带上下标
		assertRenderSuccess("\\sqrt{x}^2");
		assertRenderSuccess("\\sqrt{x}_i");

		System.out.println("✅ 根式完整测试通过");
	}

	@Test
	public void test_04_05_Delimited_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.5: 定界符 - 完整测试 ===");

		// <delimited> - 所有级别和所有定界符类型
		// \left \right 级别
		assertRenderSuccess("\\left(x\\right)");
		assertRenderSuccess("\\left[x\\right]");
		assertRenderSuccess("\\left\\{x\\right\\}");
		assertRenderSuccess("\\left|x\\right|");
		assertRenderSuccess("\\left\\|x\\right\\|");
		assertRenderSuccess("\\left\\langle x\\right\\rangle");
		assertRenderSuccess("\\left\\lfloor x\\right\\rfloor");
		assertRenderSuccess("\\left\\lceil x\\right\\rceil");
		assertRenderSuccess("\\left\\lvert x\\right\\rvert");
		assertRenderSuccess("\\left\\lVert x\\right\\rVert");

		// 空定界符
		assertRenderSuccess("\\left.x\\right)");
		assertRenderSuccess("\\left(x\\right.");

		// \bigl \bigr 级别
		assertRenderSuccess("\\bigl(x\\bigr)");
		assertRenderSuccess("\\bigl[x\\bigr]");

		// \Bigl \Bigr 级别
		assertRenderSuccess("\\Bigl(x\\Bigr)");
		assertRenderSuccess("\\Bigl[x\\Bigr]");

		// \biggl \biggr 级别
		assertRenderSuccess("\\biggl(x\\biggr)");
		assertRenderSuccess("\\biggl[x\\biggr]");

		// \Biggl \Biggr 级别
		assertRenderSuccess("\\Biggl(x\\Biggr)");
		assertRenderSuccess("\\Biggl[x\\Biggr]");

		// 测试复杂内容
		assertRenderSuccess("\\left(x+y\\right)");
		assertRenderSuccess("\\left(\\frac{a}{b}\\right)");
		assertRenderSuccess("\\left[\\sqrt{x}\\right]");

		// 测试嵌套定界符
		assertRenderSuccess("\\left(\\left[x\\right]\\right)");
		assertRenderSuccess("\\left\\{\\left(x\\right)\\right\\}");

		// 测试带上下标
		assertRenderSuccess("\\left(x\\right)^2");
		assertRenderSuccess("\\left[x\\right]_i");

		System.out.println("✅ 定界符完整测试通过");
	}

	@Test
	public void test_04_06_Function_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.6: 函数 - 完整测试 ===");

		// <function_name> 所有函数
		// 三角函数
		String[] trigFuncs = {"\\sin", "\\cos", "\\tan", "\\cot", "\\sec", "\\csc"};
		for (String func : trigFuncs) {
			assertRenderSuccess(func + " x");
			assertRenderSuccess(func + "{x}");
			assertRenderSuccess(func + "\\left(x\\right)");
		}

		// 反三角函数
		String[] arcFuncs = {"\\arcsin", "\\arccos", "\\arctan"};
		for (String func : arcFuncs) {
			assertRenderSuccess(func + " x");
		}

		// 双曲函数
		String[] hypFuncs = {"\\sinh", "\\cosh", "\\tanh", "\\coth"};
		for (String func : hypFuncs) {
			assertRenderSuccess(func + " x");
		}

		// 对数函数
		String[] logFuncs = {"\\log", "\\ln", "\\lg", "\\exp"};
		for (String func : logFuncs) {
			assertRenderSuccess(func + " x");
		}

		// 其他函数
		String[] otherFuncs = {
			"\\max", "\\min", "\\sup", "\\inf",
			"\\arg", "\\deg", "\\det", "\\dim", "\\gcd", "\\hom", "\\ker",
			"\\Pr", "\\bmod", "\\pmod"
		};
		for (String func : otherFuncs) {
			assertRenderSuccess(func + " x");
		}

		// 测试带下标的函数
		assertRenderSuccess("\\log_2 x");
		assertRenderSuccess("\\log_{10} x");

		// 测试带上标的函数
		assertRenderSuccess("\\sin^2 x");
		assertRenderSuccess("\\cos^{-1} x");

		// 测试带上下标的函数
		assertRenderSuccess("\\log_a^b x");

		// 测试函数参数
		assertRenderSuccess("\\sin{x+y}");
		assertRenderSuccess("\\cos{\\frac{x}{2}}");
		assertRenderSuccess("\\sin\\left(\\frac{x}{2}\\right)");

		// 测试函数在表达式中
		assertRenderSuccess("\\sin x+\\cos x");
		assertRenderSuccess("\\sin^2 x+\\cos^2 x");

		System.out.println("✅ 函数完整测试通过");
	}

	@Test
	public void test_04_07_LargeOperator_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.7: 大型运算符 - 完整测试 ===");

		// <large_op_name> 所有大型运算符
		// 求和/乘积
		assertRenderSuccess("\\sum");
		assertRenderSuccess("\\sum_{i=1}^{n}");
		assertRenderSuccess("\\sum_{i=1}^{n}a_i");
		assertRenderSuccess("\\prod");
		assertRenderSuccess("\\prod_{i=1}^{n}");
		assertRenderSuccess("\\coprod");
		assertRenderSuccess("\\coprod_{i=1}^{n}");

		// 积分
		assertRenderSuccess("\\int");
		assertRenderSuccess("\\int_0^1");
		assertRenderSuccess("\\int_0^{\\infty}");
		assertRenderSuccess("\\int_{-\\infty}^{\\infty}");
		assertRenderSuccess("\\iint");
		assertRenderSuccess("\\iint_D");
		assertRenderSuccess("\\iiint");
		assertRenderSuccess("\\iiint_V");
		assertRenderSuccess("\\oint");
		assertRenderSuccess("\\oint_C");
		assertRenderSuccess("\\oiint");
		assertRenderSuccess("\\oiiint");

		// 大型集合运算符
		assertRenderSuccess("\\bigcup");
		assertRenderSuccess("\\bigcup_{i=1}^{n}");
		assertRenderSuccess("\\bigcap");
		assertRenderSuccess("\\bigcap_{i=1}^{n}");
		assertRenderSuccess("\\bigvee");
		assertRenderSuccess("\\bigwedge");
		assertRenderSuccess("\\bigoplus");
		assertRenderSuccess("\\bigotimes");
		assertRenderSuccess("\\bigodot");
		assertRenderSuccess("\\biguplus");
		assertRenderSuccess("\\bigsqcup");

		// 极限
		assertRenderSuccess("\\lim");
		assertRenderSuccess("\\lim_{x\\to 0}");
		assertRenderSuccess("\\lim_{n\\to\\infty}");
		assertRenderSuccess("\\limsup_{n\\to\\infty}");
		assertRenderSuccess("\\liminf_{n\\to\\infty}");

		// 验证结构
		RendererNode root = parseAndRender("\\sum_{i=1}^{n}a_i");
		LinearGroupNodeAsserter exprGroup = assertRenderNode(root).asLinearGroup();
		exprGroup.hasChildCount(2);
		DecorGroupNodeAsserter sumDecor = exprGroup.childAt(0).asDecorGroup();
		sumDecor.hasTop();
		sumDecor.hasBottom();

		System.out.println("✅ 大型运算符完整测试通过");
	}

	@Test
	public void test_04_08_Matrix_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.8: 矩阵 - 完整测试 ===");

		// <matrix_env> 所有矩阵环境
		// matrix - 无定界符
		assertRenderSuccess("\\begin{matrix}a&b\\\\c&d\\end{matrix}");

		// pmatrix - 圆括号
		assertRenderSuccess("\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}");

		// bmatrix - 方括号
		assertRenderSuccess("\\begin{bmatrix}a&b\\\\c&d\\end{bmatrix}");

		// Bmatrix - 花括号
		assertRenderSuccess("\\begin{Bmatrix}a&b\\\\c&d\\end{Bmatrix}");

		// vmatrix - 单竖线
		assertRenderSuccess("\\begin{vmatrix}a&b\\\\c&d\\end{vmatrix}");

		// Vmatrix - 双竖线
		assertRenderSuccess("\\begin{Vmatrix}a&b\\\\c&d\\end{Vmatrix}");

		// cases - 分段函数
		assertRenderSuccess("\\begin{cases}x,&x\\ge 0\\\\-x,&x<0\\end{cases}");

		// array - 带对齐方式
		assertRenderSuccess("\\begin{array}{cc}a&b\\\\c&d\\end{array}");
		assertRenderSuccess("\\begin{array}{lcr}a&b&c\\\\d&e&f\\end{array}");

		// 验证行数
		RendererNode root = parseAndRender("\\begin{matrix}a&b\\\\c&d\\end{matrix}");
		GridGroupNodeAsserter grid = assertRenderNode(root).asGridGroupNode();
		grid.hasRowCount(2);

		// 测试不同大小的矩阵
		assertRenderSuccess("\\begin{matrix}a\\end{matrix}");
		assertRenderSuccess("\\begin{matrix}a&b&c\\end{matrix}");
		assertRenderSuccess("\\begin{matrix}a\\\\b\\\\c\\end{matrix}");
		assertRenderSuccess("\\begin{matrix}a&b&c\\\\d&e&f\\\\g&h&i\\end{matrix}");

		// 测试矩阵中的复杂元素
		assertRenderSuccess("\\begin{pmatrix}\\frac{1}{2}&\\sqrt{2}\\\\\\alpha&\\beta\\end{pmatrix}");

		System.out.println("✅ 矩阵完整测试通过");
	}

	@Test
	public void test_04_09_Text_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.9: 文本 - 完整测试 ===");

		// <text> 所有文本命令
		assertRenderSuccess("\\text{hello}");
		assertRenderSuccess("\\text{Hello World}");
		assertRenderSuccess("\\text{当x趋近于0时}");

		assertRenderSuccess("\\mbox{text}");
		assertRenderSuccess("\\mbox{some text}");

		assertRenderSuccess("\\textrm{text}");
		assertRenderSuccess("\\textit{text}");
		assertRenderSuccess("\\textbf{text}");

		// 测试在表达式中
		assertRenderSuccess("x\\text{ if }x>0");
		assertRenderSuccess("f(x)\\text{ where }x\\in\\mathbb{R}");

		System.out.println("✅ 文本完整测试通过");
	}

	@Test
	public void test_04_10_Accent_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.10: 重音 - 完整测试 ===");

		// <accent_cmd> 所有重音命令
		// hat 类
		assertRenderSuccess("\\hat{x}");
		assertRenderSuccess("\\hat x");
		assertRenderSuccess("\\widehat{xyz}");
		assertRenderSuccess("\\widehat{x+y}");

		// tilde 类
		assertRenderSuccess("\\tilde{x}");
		assertRenderSuccess("\\tilde x");
		assertRenderSuccess("\\widetilde{xyz}");
		assertRenderSuccess("\\widetilde{x+y}");

		// bar 类
		assertRenderSuccess("\\bar{x}");
		assertRenderSuccess("\\bar x");
		assertRenderSuccess("\\overline{xyz}");
		assertRenderSuccess("\\overline{x+y}");
		assertRenderSuccess("\\underline{xyz}");
		assertRenderSuccess("\\underline{x+y}");

		// 向量和箭头
		assertRenderSuccess("\\vec{v}");
		assertRenderSuccess("\\vec v");
		assertRenderSuccess("\\overrightarrow{AB}");
		assertRenderSuccess("\\overleftarrow{AB}");

		// 点
		assertRenderSuccess("\\dot{x}");
		assertRenderSuccess("\\dot x");
		assertRenderSuccess("\\ddot{x}");
		assertRenderSuccess("\\ddot x");
		assertRenderSuccess("\\dddot{x}");
		assertRenderSuccess("\\dddot x");

		// 其他重音
		assertRenderSuccess("\\acute{x}");
		assertRenderSuccess("\\acute x");
		assertRenderSuccess("\\grave{x}");
		assertRenderSuccess("\\grave x");
		assertRenderSuccess("\\breve{x}");
		assertRenderSuccess("\\breve x");
		assertRenderSuccess("\\check{x}");
		assertRenderSuccess("\\check x");
		assertRenderSuccess("\\mathring{x}");
		assertRenderSuccess("\\mathring x");

		// 括号
		assertRenderSuccess("\\overbrace{x+y}");
		assertRenderSuccess("\\overbrace{a+b+c}");
		assertRenderSuccess("\\underbrace{x+y}");
		assertRenderSuccess("\\underbrace{a+b+c}");

		// 验证结构
		RendererNode root = parseAndRender("\\hat{x}");
		AccentNodeAsserter accent = assertRenderNode(root).asAccentNode();
		accent.content().asTextNode().hasContent("x");

		// 测试重音带上下标
		assertRenderSuccess("\\hat{x}^2");
		assertRenderSuccess("\\bar{x}_i");
		assertRenderSuccess("\\vec{v}^2_i");

		// 测试重音嵌套
		assertRenderSuccess("\\hat{\\bar{x}}");
		assertRenderSuccess("\\vec{\\hat{v}}");

		// 测试重音在表达式中
		assertRenderSuccess("\\hat{x}+\\hat{y}");
		assertRenderSuccess("\\bar{x}-\\bar{y}");

		// 测试重音在分式中
		assertRenderSuccess("\\frac{\\hat{x}}{y}");
		assertRenderSuccess("\\frac{x}{\\bar{y}}");

		System.out.println("✅ 重音完整测试通过");
	}

	@Test
	public void test_04_11_Font_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.11: 字体 - 完整测试 ===");

		// <font_cmd_name> 所有字体命令
		assertRenderSuccess("\\mathrm{x}");
		assertRenderSuccess("\\mathrm{abc}");
		assertRenderSuccess("\\mathit{x}");
		assertRenderSuccess("\\mathit{abc}");
		assertRenderSuccess("\\mathbf{x}");
		assertRenderSuccess("\\mathbf{abc}");
		assertRenderSuccess("\\mathsf{x}");
		assertRenderSuccess("\\mathsf{abc}");
		assertRenderSuccess("\\mathtt{x}");
		assertRenderSuccess("\\mathtt{abc}");
		assertRenderSuccess("\\mathcal{L}");
		assertRenderSuccess("\\mathcal{ABC}");
		assertRenderSuccess("\\mathbb{R}");
		assertRenderSuccess("\\mathbb{NZQRC}");
		assertRenderSuccess("\\mathfrak{g}");
		assertRenderSuccess("\\mathfrak{abc}");
		assertRenderSuccess("\\mathscr{F}");
		assertRenderSuccess("\\mathscr{ABC}");
		assertRenderSuccess("\\boldsymbol{\\alpha}");
		assertRenderSuccess("\\boldsymbol{x}");
		assertRenderSuccess("\\bm{\\alpha}");
		assertRenderSuccess("\\bm{x}");

		// 测试字体命令嵌套表达式
		assertRenderSuccess("\\mathbb{R}^n");
		assertRenderSuccess("\\mathcal{L}\\left(x\\right)");
		assertRenderSuccess("x\\in\\mathbb{R}");

		System.out.println("✅ 字体完整测试通过");
	}

	@Test
	public void test_04_12_ExtensibleArrow_Complete() throws MathParseException {
		System.out.println("\n=== Part 4.12: 可扩展箭头 - 完整测试 ===");

		// <extensible_arrow_cmd> 所有可扩展箭头命令
		// 基本形式：\cmd{上方}
		assertRenderSuccess("\\xrightarrow{上方}");
		assertRenderSuccess("\\xleftarrow{上方}");
		assertRenderSuccess("\\xleftrightarrow{上方}");
		assertRenderSuccess("\\xRightarrow{上方}");
		assertRenderSuccess("\\xLeftarrow{上方}");
		assertRenderSuccess("\\xLeftrightarrow{上方}");

		// 带下方标注：\cmd[下方]{上方}
		assertRenderSuccess("\\xrightarrow[下方]{上方}");
		assertRenderSuccess("\\xleftarrow[下方]{上方}");
		assertRenderSuccess("\\xleftrightarrow[下方]{上方}");
		assertRenderSuccess("\\xRightarrow[下方]{上方}");
		assertRenderSuccess("\\xLeftarrow[下方]{上方}");
		assertRenderSuccess("\\xLeftrightarrow[下方]{上方}");

		// 测试数学内容
		assertRenderSuccess("\\xrightarrow{f}");
		assertRenderSuccess("\\xrightarrow{n\\to\\infty}");
		assertRenderSuccess("\\xrightarrow[k\\to 0]{f(x)}");

		// 在表达式中使用
		assertRenderSuccess("A\\xrightarrow{f}B");
		assertRenderSuccess("X\\xleftarrow{g}Y\\xrightarrow{h}Z");

		// 复杂用例
		assertRenderSuccess("\\frac{\\bar{X}_n-\\mu}{\\sigma/\\sqrt{n}}\\xrightarrow{d}N\\left(0,1\\right)");

		System.out.println("✅ 可扩展箭头完整测试通过");
	}

	// ============================================================
	// Part 5: 上下标测试 - 完善版
	// ============================================================

	@Test
	public void test_05_01_SupSubSuffix_Complete() throws MathParseException {
		System.out.println("\n=== Part 5.1: 上下标 - 完整测试 ===");

		// <sup_sub_suffix> 四种形式
		// 形式1: "^" <script_arg>
		RendererNode root = parseAndRender("x^2");
		DecorGroupNodeAsserter decor = assertRenderNode(root).asDecorGroup();
		decor.center().asTextNode().hasContent("x");
		decor.hasRightTop();
		decor.noRightBottom();
		decor.rightTop().asTextNode().hasContent("2");

		assertRenderSuccess("x^a");
		assertRenderSuccess("x^{n+1}");
		assertRenderSuccess("e^{i\\pi}");

		// 形式2: "_" <script_arg>
		root = parseAndRender("x_1");
		decor = assertRenderNode(root).asDecorGroup();
		decor.center().asTextNode().hasContent("x");
		decor.hasRightBottom();
		decor.noRightTop();
		decor.rightBottom().asTextNode().hasContent("1");

		assertRenderSuccess("x_i");
		assertRenderSuccess("a_{i,j}");
		assertRenderSuccess("x_{n+1}");

		// 形式3: "^" <script_arg> "_" <script_arg>
		root = parseAndRender("x^2_1");
		decor = assertRenderNode(root).asDecorGroup();
		decor.center().asTextNode().hasContent("x");
		decor.hasRightTop();
		decor.hasRightBottom();
		decor.rightTop().asTextNode().hasContent("2");
		decor.rightBottom().asTextNode().hasContent("1");

		assertRenderSuccess("x^a_b");
		assertRenderSuccess("x^{n+1}_{i}");

		// 形式4: "_" <script_arg> "^" <script_arg>
		root = parseAndRender("x_1^2");
		decor = assertRenderNode(root).asDecorGroup();
		decor.hasRightTop();
		decor.hasRightBottom();

		assertRenderSuccess("x_a^b");
		assertRenderSuccess("x_{i}^{n+1}");

		// <script_arg> 不同类型
		// 数字
		assertRenderSuccess("x^0");
		assertRenderSuccess("x^{123}");

		// 变量
		assertRenderSuccess("x^n");
		assertRenderSuccess("x^{abc}");

		// 希腊字母
		assertRenderSuccess("x^\\alpha");
		assertRenderSuccess("x^{\\alpha+\\beta}");

		// 运算符符号（在上下标中允许）
		assertRenderSuccess("x^+");
		assertRenderSuccess("x^-");
		assertRenderSuccess("x^*");

		// 分组
		assertRenderSuccess("x^{a+b}");
		assertRenderSuccess("x_{a-b}");
		assertRenderSuccess("x^{\\frac{1}{2}}");

		System.out.println("✅ 上下标完整测试通过");
	}

	// ============================================================
	// Part 6: 空格命令测试 - 完善版
	// ============================================================

	@Test
	public void test_06_01_Spacing_Complete() throws MathParseException {
		System.out.println("\n=== Part 6.1: 空格命令 - 完整测试 ===");

		// <spacing> 所有空格命令
		// 基本空格
		assertRenderSuccess("a\\,b");    // thin space
		assertRenderSuccess("a\\:b");    // medium space
		assertRenderSuccess("a\\;b");    // thick space
		assertRenderSuccess("a\\!b");    // negative thin space
		assertRenderSuccess("a\\quad b");   // quad space
		assertRenderSuccess("a\\qquad b");  // double quad space

		// 验证结构
		RendererNode root = parseAndRender("a\\quad b");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		rootGroup.hasChildCount(3);
		rootGroup.childAt(0).asTextNode();
		rootGroup.childAt(1).asSpaceNode();
		rootGroup.childAt(2).asTextNode();

		// phantom 类（如果支持）
		assertRenderSuccess("a\\hphantom{xyz}b");
		assertRenderSuccess("a\\vphantom{xyz}b");
		assertRenderSuccess("a\\phantom{xyz}b");

		// 测试空格在不同位置
		assertRenderSuccess("\\quad x");
		assertRenderSuccess("x\\quad");
		assertRenderSuccess("x\\,y\\,z");

		System.out.println("✅ 空格命令完整测试通过");
	}

	// ============================================================
	// Part 7: 复杂表达式和真实世界公式测试 - 完善版
	// ============================================================

	@Test
	public void test_07_01_Expression_Structure() throws MathParseException {
		System.out.println("\n=== Part 7.1: 表达式结构 - 完整测试 ===");

		// <expression> ::= <term> { <separator> }
		// <separator> ::= <binary_op> <term> | <term>

		// 单个 term
		RendererNode root = parseAndRender("x");
		assertRenderNode(root).asTextNode().hasContent("x");

		// term 之间有二元运算符
		root = parseAndRender("a+b");
		assertRenderNode(root).asLinearGroup().hasChildCount(5);

		// term 之间直接相邻（隐式乘法）
		root = parseAndRender("xy");
		assertRenderNode(root).asLinearGroup().hasChildCount(2);

		// 混合情况
		assertRenderSuccess("ab+cd");
		assertRenderSuccess("2x+3y");
		assertRenderSuccess("a+bc+d");

		System.out.println("✅ 表达式结构完整测试通过");
	}

	@Test
	public void test_07_02_Term_Structure() throws MathParseException {
		System.out.println("\n=== Part 7.2: Term结构 - 完整测试 ===");

		// <term> 三种形式
		// 形式1: [ <unary_op> ] <operand_atom> [ <sup_sub_suffix> ] [ <postfix_op> ]
		assertRenderSuccess("x");           // 基本
		assertRenderSuccess("-x");          // 一元运算符
		assertRenderSuccess("x^2");         // 上下标
		assertRenderSuccess("n!");          // 后缀运算符
		assertRenderSuccess("-x^2");        // 一元 + 上下标
		assertRenderSuccess("x^2!");        // 上下标 + 后缀
		assertRenderSuccess("-x^2!");       // 全部组合

		// 形式2: <special_symbol> [ <sup_sub_suffix> ]
		assertRenderSuccess("\\dots");
		assertRenderSuccess("\\dots^n");
		assertRenderSuccess("\\angle_1");

		// 形式3: <punctuation>
		assertRenderSuccess("a,b");
		assertRenderSuccess("a;b");

		System.out.println("✅ Term结构完整测试通过");
	}

	// ============================================================
	// Part 8: 综合真实世界公式测试
	// ============================================================

	@Test
	public void test_08_01_Physics_Formulas() throws MathParseException {
		System.out.println("\n=== Part 8.1: 物理公式 ===");

		// 牛顿第二定律
		assertRenderSuccess("F=ma");

		// 动能
		assertRenderSuccess("E_k=\\frac{1}{2}mv^2");

		// 万有引力
		assertRenderSuccess("F=G\\frac{m_1m_2}{r^2}");

		// 薛定谔方程
		assertRenderSuccess("i\\hbar\\frac{\\partial}{\\partial t}\\Psi=\\hat{H}\\Psi");

		// 麦克斯韦方程组
		assertRenderSuccess("\\nabla\\cdot\\mathbf{E}=\\frac{\\rho}{\\epsilon_0}");
		assertRenderSuccess("\\nabla\\cdot\\mathbf{B}=0");
		assertRenderSuccess("\\nabla\\times\\mathbf{E}=-\\frac{\\partial\\mathbf{B}}{\\partial t}");
		assertRenderSuccess("\\nabla\\times\\mathbf{B}=\\mu_0\\left(\\mathbf{J}+\\epsilon_0\\frac{\\partial\\mathbf{E}}{\\partial t}\\right)");

		// 爱因斯坦质能方程
		assertRenderSuccess("E=mc^2");

		// 爱因斯坦场方程
		assertRenderSuccess("R_{\\mu\\nu}-\\frac{1}{2}Rg_{\\mu\\nu}+\\Lambda g_{\\mu\\nu}=\\frac{8\\pi G}{c^4}T_{\\mu\\nu}");

		System.out.println("✅ 物理公式测试通过");
	}

	@Test
	public void test_08_02_Calculus_Formulas() throws MathParseException {
		System.out.println("\n=== Part 8.2: 微积分公式 ===");

		// 导数定义
		assertRenderSuccess("f'\\left(x\\right)=\\lim_{h\\to 0}\\frac{f\\left(x+h\\right)-f\\left(x\\right)}{h}");

		// 偏导数
		assertRenderSuccess("\\frac{\\partial f}{\\partial x}");
		assertRenderSuccess("\\frac{\\partial^2 f}{\\partial x^2}");
		assertRenderSuccess("\\frac{\\partial^2 f}{\\partial x\\partial y}");

		// 积分
		assertRenderSuccess("\\int_a^b f\\left(x\\right)dx");
		assertRenderSuccess("\\int_{-\\infty}^{\\infty}e^{-x^2}dx=\\sqrt{\\pi}");

		// 多重积分
		assertRenderSuccess("\\iint_D f\\left(x,y\\right)dxdy");
		assertRenderSuccess("\\iiint_V f\\left(x,y,z\\right)dxdydz");

		// 泰勒级数
		assertRenderSuccess("f\\left(x\\right)=\\sum_{n=0}^{\\infty}\\frac{f^{\\left(n\\right)}\\left(a\\right)}{n!}\\left(x-a\\right)^n");

		// 傅里叶变换
		assertRenderSuccess("F\\left(\\omega\\right)=\\int_{-\\infty}^{\\infty}f\\left(t\\right)e^{-i\\omega t}dt");

		System.out.println("✅ 微积分公式测试通过");
	}

	@Test
	public void test_08_03_Algebra_Formulas() throws MathParseException {
		System.out.println("\n=== Part 8.3: 代数公式 ===");

		// 二次公式
		assertRenderSuccess("x=\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}");

		// 韦达定理
		assertRenderSuccess("x_1+x_2=-\\frac{b}{a}");
		assertRenderSuccess("x_1x_2=\\frac{c}{a}");

		// 二项式定理
		assertRenderSuccess("\\left(x+y\\right)^n=\\sum_{k=0}^{n}\\binom{n}{k}x^{n-k}y^k");

		// 欧拉公式
		assertRenderSuccess("e^{i\\theta}=\\cos\\theta+i\\sin\\theta");
		assertRenderSuccess("e^{i\\pi}+1=0");

		// 复数
		assertRenderSuccess("z=a+bi");
		assertRenderSuccess("|z|=\\sqrt{a^2+b^2}");
		assertRenderSuccess("\\bar{z}=a-bi");

		System.out.println("✅ 代数公式测试通过");
	}

	@Test
	public void test_08_04_LinearAlgebra_Formulas() throws MathParseException {
		System.out.println("\n=== Part 8.4: 线性代数公式 ===");

		// 矩阵乘法
		assertRenderSuccess("\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}\\begin{pmatrix}x\\\\y\\end{pmatrix}=\\begin{pmatrix}ax+by\\\\cx+dy\\end{pmatrix}");

		// 行列式
		assertRenderSuccess("\\det\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}=ad-bc");

		// 特征值
		assertRenderSuccess("\\det\\left(A-\\lambda I\\right)=0");

		// 矩阵转置
		assertRenderSuccess("\\left(AB\\right)^T=B^TA^T");

		// 矩阵求逆
		assertRenderSuccess("A^{-1}=\\frac{1}{\\det A}\\text{adj}A");

		System.out.println("✅ 线性代数公式测试通过");
	}

	@Test
	public void test_08_05_Probability_Formulas() throws MathParseException {
		System.out.println("\n=== Part 8.5: 概率统计公式 ===");

		// 贝叶斯定理
		assertRenderSuccess("P\\left(A|B\\right)=\\frac{P\\left(B|A\\right)P\\left(A\\right)}{P\\left(B\\right)}");

		// 期望
		assertRenderSuccess("E\\left[X\\right]=\\sum_{i}x_iP\\left(X=x_i\\right)");

		// 方差
		assertRenderSuccess("\\text{Var}\\left(X\\right)=E\\left[X^2\\right]-\\left(E\\left[X\\right]\\right)^2");

		// 正态分布
		assertRenderSuccess("f\\left(x\\right)=\\frac{1}{\\sigma\\sqrt{2\\pi}}e^{-\\frac{\\left(x-\\mu\\right)^2}{2\\sigma^2}}");

		// 中心极限定理
		assertRenderSuccess("\\frac{\\bar{X}_n-\\mu}{\\sigma/\\sqrt{n}}\\xrightarrow{d}N\\left(0,1\\right)");

		// 二项分布
		assertRenderSuccess("P\\left(X=k\\right)=\\binom{n}{k}p^k\\left(1-p\\right)^{n-k}");

		System.out.println("✅ 概率统计公式测试通过");
	}

	@Test
	public void test_08_06_SetTheory_Formulas() throws MathParseException {
		System.out.println("\n=== Part 8.6: 集合论公式 ===");

		// 集合表示
		assertRenderSuccess("A=\\left\\{x|x>0\\right\\}");
		assertRenderSuccess("\\mathbb{N}\\subset\\mathbb{Z}\\subset\\mathbb{Q}\\subset\\mathbb{R}\\subset\\mathbb{C}");

		// 集合运算
		assertRenderSuccess("A\\cup B");
		assertRenderSuccess("A\\cap B");
		assertRenderSuccess("A\\setminus B");

		// 德摩根定律
		assertRenderSuccess("\\left(A\\cup B\\right)^c=A^c\\cap B^c");
		assertRenderSuccess("\\left(A\\cap B\\right)^c=A^c\\cup B^c");

		// 空集
		assertRenderSuccess("A\\cap\\emptyset=\\emptyset");
		assertRenderSuccess("A\\cup\\emptyset=A");

		System.out.println("✅ 集合论公式测试通过");
	}

	@Test
	public void test_08_07_Logic_Formulas() throws MathParseException {
		System.out.println("\n=== Part 8.7: 逻辑公式 ===");

		// 量词
		assertRenderSuccess("\\forall x\\in A");
		assertRenderSuccess("\\exists x\\in A");
		assertRenderSuccess("\\nexists x\\in A");

		// 逻辑连接词
		assertRenderSuccess("p\\wedge q");
		assertRenderSuccess("p\\vee q");
		assertRenderSuccess("p\\Rightarrow q");
		assertRenderSuccess("p\\Leftrightarrow q");

		// 因果关系
		assertRenderSuccess("\\because a=b");
		assertRenderSuccess("\\therefore c=d");

		System.out.println("✅ 逻辑公式测试通过");
	}

	@Test
	public void test_08_08_Geometry_Formulas() throws MathParseException {
		System.out.println("\n=== Part 8.8: 几何公式 ===");

		// 角度
		assertRenderSuccess("\\angle ABC");
		assertRenderSuccess("\\angle A=90^\\circ");

		// 平行和垂直
		assertRenderSuccess("AB\\parallel CD");
		assertRenderSuccess("AB\\perp CD");

		// 三角形
		assertRenderSuccess("a^2+b^2=c^2");
		assertRenderSuccess("S=\\frac{1}{2}ab\\sin C");

		// 向量
		assertRenderSuccess("\\vec{AB}");
		assertRenderSuccess("\\overrightarrow{AB}+\\overrightarrow{BC}=\\overrightarrow{AC}");
		assertRenderSuccess("\\vec{a}\\cdot\\vec{b}=|\\vec{a}||\\vec{b}|\\cos\\theta");

		System.out.println("✅ 几何公式测试通过");
	}

	@Test
	public void test_09_Summary() {
		System.out.println("\n" + "=".repeat(60));
		System.out.println("BNF 完整测试总结：");
		System.out.println("=".repeat(60));
		System.out.println("✅ 基础元素: number, variable, greek_letter, special_letter_variable, special_symbol");
		System.out.println("✅ 运算符: unary_op, binary_op (全部), postfix_op");
		System.out.println("✅ 标点: punctuation (逗号、分号)");
		System.out.println("✅ 原子表达式: group, frac, binom, sqrt, delimited, function_call, large_operator, matrix, text, accent, font_command, extensible_arrow");
		System.out.println("✅ 上下标: sup_sub_suffix (四种形式), script_arg");
		System.out.println("✅ 空格: spacing (全部)");
		System.out.println("✅ 重音: accent_cmd (全部，包括 mathring)");
		System.out.println("✅ 定界符: delimiter (全部)");
		System.out.println("✅ 表达式结构: expression, term, separator");
		System.out.println("✅ 真实世界公式: 物理、微积分、代数、线性代数、概率统计、集合论、逻辑、几何");
		System.out.println("✅ RendererNode 结构验证");
		System.out.println("=".repeat(60));
	}
}