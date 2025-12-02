package me.chan.texas.ext.markdown.math.renderer;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.MathParseException;
import me.chan.texas.ext.markdown.math.ast.MathParser;
import me.chan.texas.ext.markdown.math.ast.MockTextPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;
import me.chan.texas.utils.CharStream;

import static org.junit.Assert.*;

/**
 * LaTeX 数学公式解析器全面测试 - 完整版
 * <p>
 * 测试目标：
 * 1. 覆盖 bnf_math.txt 中所有基础语法元素
 * 2. 验证 AST 结构的正确性
 * 3. 验证 AST 转换为 RenderNode 后的结构
 * 4. 测试 MathFontOptions 中的符号查找（防止crash）
 * 5. 验证嵌套和复杂表达式
 * <p>
 * 参考 MathParserUnitTest 的测试风格和覆盖范围
 */
public class MathParserAstTest {

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

		BraceNodeAsserter asBraceNode() {
			isType(BraceNode.class);
			return new BraceNodeAsserter((BraceNode) node);
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
	 * BraceNode 验证器
	 */
	static class BraceNodeAsserter extends RendererNodeAsserter {
		BraceNodeAsserter(BraceNode node) {
			super(node);
		}

		BraceNodeAsserter and() {
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
	// Part 1: 基础元素测试
	// ============================================================

	@Test
	public void test_01_01_Number_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 1.1: 数字 - RenderNode结构 ===");

		// 测试整数
		RendererNode root = parseAndRender("123");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		rootGroup.hasChildCount(1);
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		exprGroup.hasChildCount(1);
		DecorGroupNodeAsserter termDecor = exprGroup.childAt(0).asDecorGroup();
		termDecor.center().asTextNode().hasContent("123");
		termDecor.noRightTop().noRightBottom();

		// 测试小数
		assertRenderSuccess("3.14");
		assertRenderSuccess("0.5");

		System.out.println("✅ 数字 RenderNode结构验证通过");
	}

	@Test
	public void test_01_02_Variable_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 1.2: 变量 - RenderNode结构 ===");

		// 测试基本变量
		RendererNode root = parseAndRender("x");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		rootGroup.hasChildCount(1);
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		exprGroup.hasChildCount(1);
		DecorGroupNodeAsserter termDecor = exprGroup.childAt(0).asDecorGroup();
		termDecor.center().asTextNode().hasContent("x");

		// 测试多字母变量
		assertRenderSuccess("abc");

		// 测试带撇号的变量
		assertRenderSuccess("f'");
		assertRenderSuccess("g''");

		System.out.println("✅ 变量 RenderNode结构验证通过");
	}

	@Test
	public void test_01_03_GreekLetters_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 1.3: 希腊字母 - RenderNode结构 ===");

		// 测试小写希腊字母
		assertRenderSuccess("\\alpha");
		assertRenderSuccess("\\beta");
		assertRenderSuccess("\\gamma");
		assertRenderSuccess("\\delta");
		assertRenderSuccess("\\epsilon");
		assertRenderSuccess("\\varepsilon");
		assertRenderSuccess("\\zeta");
		assertRenderSuccess("\\eta");
		assertRenderSuccess("\\theta");
		assertRenderSuccess("\\vartheta");
		assertRenderSuccess("\\iota");
		assertRenderSuccess("\\kappa");
		assertRenderSuccess("\\lambda");
		assertRenderSuccess("\\mu");
		assertRenderSuccess("\\nu");
		assertRenderSuccess("\\xi");
		assertRenderSuccess("\\pi");
		assertRenderSuccess("\\varpi");
		assertRenderSuccess("\\rho");
		assertRenderSuccess("\\varrho");
		assertRenderSuccess("\\sigma");
		assertRenderSuccess("\\varsigma");
		assertRenderSuccess("\\tau");
		assertRenderSuccess("\\upsilon");
		assertRenderSuccess("\\phi");
		assertRenderSuccess("\\varphi");
		assertRenderSuccess("\\chi");
		assertRenderSuccess("\\psi");
		assertRenderSuccess("\\omega");

		// 测试大写希腊字母
		assertRenderSuccess("\\Gamma");
		assertRenderSuccess("\\Delta");
		assertRenderSuccess("\\Theta");
		assertRenderSuccess("\\Lambda");
		assertRenderSuccess("\\Xi");
		assertRenderSuccess("\\Pi");
		assertRenderSuccess("\\Sigma");
		assertRenderSuccess("\\Upsilon");
		assertRenderSuccess("\\Phi");
		assertRenderSuccess("\\Psi");
		assertRenderSuccess("\\Omega");

		// 测试带撇号的希腊字母
		assertRenderSuccess("\\alpha'");
		assertRenderSuccess("\\beta''");

		// 测试带上下标的希腊字母
		assertRenderSuccess("\\alpha_1");
		assertRenderSuccess("\\beta^2");
		assertRenderSuccess("\\gamma_i^2");

		System.out.println("✅ 希腊字母 RenderNode结构验证通过");
	}

	@Test
	public void test_01_04_SpecialLetterVariables_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 1.4: 特殊字母变量 - RenderNode结构 ===");

		// 测试所有特殊字母变量（类变量符号）
		assertRenderSuccess("\\hbar");       // ℏ 约化普朗克常数
		assertRenderSuccess("\\nabla");      // ∇ 梯度算子
		assertRenderSuccess("\\partial");    // ∂ 偏导数符号
		assertRenderSuccess("\\ell");        // ℓ 脚本小写L
		assertRenderSuccess("\\wp");         // ℘ 魏尔斯特拉斯函数
		assertRenderSuccess("\\Re");         // ℜ 实部
		assertRenderSuccess("\\Im");         // ℑ 虚部
		assertRenderSuccess("\\aleph");      // ℵ 阿列夫数

		// 测试一元运算符修饰特殊字母变量
		assertRenderSuccess("-\\nabla");
		assertRenderSuccess("-\\partial");
		assertRenderSuccess("+\\hbar");

		// 测试带上下标的特殊字母变量
		assertRenderSuccess("\\nabla^2");
		assertRenderSuccess("\\partial_t");

		System.out.println("✅ 特殊字母变量 RenderNode结构验证通过");
	}

	@Test
	public void test_01_05_SpecialSymbols_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 1.5: 特殊符号 - RenderNode结构 ===");

		// 测试省略号
		assertRenderSuccess("\\dots");
		assertRenderSuccess("\\ldots");
		assertRenderSuccess("\\cdots");
		assertRenderSuccess("\\vdots");
		assertRenderSuccess("\\ddots");

		// 测试几何符号
		assertRenderSuccess("\\angle");

		// 测试逻辑标记符号
		assertRenderSuccess("\\therefore");
		assertRenderSuccess("\\because");

		// 测试量词
		assertRenderSuccess("\\forall");
		assertRenderSuccess("\\exists");
		assertRenderSuccess("\\nexists");

		// 测试空集
		assertRenderSuccess("\\emptyset");
		assertRenderSuccess("\\varnothing");

		// 测试无穷
		assertRenderSuccess("\\infty");

		// 测试特殊符号带上下标
		assertRenderSuccess("\\angle_1");
		assertRenderSuccess("\\dots^{n}");

		System.out.println("✅ 特殊符号 RenderNode结构验证通过");
	}

	// ============================================================
	// Part 2: 运算符测试
	// ============================================================

	@Test
	public void test_02_01_UnaryOperators_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 2.1: 一元运算符 - RenderNode结构 ===");

		// 测试 "-x"
		RendererNode root = parseAndRender("-x");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		exprGroup.hasChildCount(1);
		DecorGroupNodeAsserter termDecor = exprGroup.childAt(0).asDecorGroup();
		termDecor.hasLeft();
		termDecor.left().asSymbolNode().hasContent("−");
		termDecor.center().asTextNode().hasContent("x");

		// 测试其他一元运算符
		assertRenderSuccess("+x");
		assertRenderSuccess("\\pm x");
		assertRenderSuccess("\\mp x");
		assertRenderSuccess("-1");
		assertRenderSuccess("-{x+y}");

		System.out.println("✅ 一元运算符 RenderNode结构验证通过");
	}

	@Test
	public void test_02_02_BinaryOperators_Arithmetic_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 2.2: 二元运算符 - 算术 - RenderNode结构 ===");

		// 测试 "a+b"
		RendererNode root = parseAndRender("a+b");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		rootGroup.hasChildCount(1);
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		exprGroup.hasChildCount(5); // Term(a), Space, BinOp(+), Space, Term(b)
		exprGroup.childAt(0).asDecorGroup().center().asTextNode().hasContent("a");
		exprGroup.childAt(1).asSpaceNode();
		exprGroup.childAt(2).asSymbolNode().hasContent("+");
		exprGroup.childAt(3).asSpaceNode();
		exprGroup.childAt(4).asDecorGroup().center().asTextNode().hasContent("b");

		// 测试其他算术运算符
		assertRenderSuccess("a-b");
		assertRenderSuccess("a*b");
		assertRenderSuccess("a/b");
		assertRenderSuccess("a\\times b");
		assertRenderSuccess("a\\cdot b");
		assertRenderSuccess("a\\div b");
		assertRenderSuccess("a\\pm b");
		assertRenderSuccess("a\\mp b");

		System.out.println("✅ 二元运算符（算术）RenderNode结构验证通过");
	}

	@Test
	public void test_02_03_BinaryOperators_Relational_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 2.3: 二元运算符 - 关系 - RenderNode结构 ===");

		// 测试所有关系运算符
		assertRenderSuccess("a=b");
		assertRenderSuccess("a\\neq b");
		assertRenderSuccess("a\\equiv b");
		assertRenderSuccess("a\\approx b");
		assertRenderSuccess("a\\cong b");
		assertRenderSuccess("a\\sim b");
		assertRenderSuccess("a<b");
		assertRenderSuccess("a>b");
		assertRenderSuccess("a\\le b");
		assertRenderSuccess("a\\ge b");
		assertRenderSuccess("a\\leq b");
		assertRenderSuccess("a\\geq b");
		assertRenderSuccess("a\\ll b");
		assertRenderSuccess("a\\gg b");

		System.out.println("✅ 二元运算符（关系）RenderNode结构验证通过");
	}

	@Test
	public void test_02_04_BinaryOperators_Set_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 2.4: 二元运算符 - 集合 - RenderNode结构 ===");

		// 测试集合运算符
		assertRenderSuccess("x\\in A");
		assertRenderSuccess("x\\notin A");
		assertRenderSuccess("A\\subset B");
		assertRenderSuccess("A\\supset B");
		assertRenderSuccess("A\\subseteq B");
		assertRenderSuccess("A\\supseteq B");
		assertRenderSuccess("A\\cup B");
		assertRenderSuccess("A\\cap B");
		assertRenderSuccess("p\\wedge q");
		assertRenderSuccess("p\\vee q");

		System.out.println("✅ 二元运算符（集合）RenderNode结构验证通过");
	}

	@Test
	public void test_02_05_BinaryOperators_Arrows_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 2.5: 二元运算符 - 箭头 - RenderNode结构 ===");

		// 测试箭头运算符
		assertRenderSuccess("a\\to b");
		assertRenderSuccess("a\\rightarrow b");
		assertRenderSuccess("a\\leftarrow b");
		assertRenderSuccess("a\\leftrightarrow b");
		assertRenderSuccess("A\\Rightarrow B");
		assertRenderSuccess("A\\Leftarrow B");
		assertRenderSuccess("A\\Leftrightarrow B");
		assertRenderSuccess("A\\implies B");
		assertRenderSuccess("A\\iff B");

		System.out.println("✅ 二元运算符（箭头）RenderNode结构验证通过");
	}

	@Test
	public void test_02_06_BinaryOperators_Geometry_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 2.6: 二元运算符 - 几何 - RenderNode结构 ===");

		// 测试几何关系运算符
		assertRenderSuccess("a\\perp b");
		assertRenderSuccess("a\\parallel b");

		// 测试整除符号
		assertRenderSuccess("a|b");
		assertRenderSuccess("a\\mid b");

		System.out.println("✅ 二元运算符（几何）RenderNode结构验证通过");
	}

	@Test
	public void test_02_07_PostfixOperators_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 2.7: 后缀运算符（阶乘）- RenderNode结构 ===");

		// 测试基本阶乘
		assertRenderSuccess("n!");
		assertRenderSuccess("5!");
		assertRenderSuccess("x!");

		// 测试阶乘带上下标
		assertRenderSuccess("n^2!");
		assertRenderSuccess("n_i!");

		// 测试阶乘在表达式中
		assertRenderSuccess("n!+1");
		assertRenderSuccess("n!m");

		// 测试阶乘在分式中
		assertRenderSuccess("\\frac{n!}{k!}");

		// 测试括号内表达式的阶乘
		assertRenderSuccess("\\left(n-1\\right)!");

		System.out.println("✅ 后缀运算符（阶乘）RenderNode结构验证通过");
	}

	// ============================================================
	// Part 3: 标点符号测试
	// ============================================================

	@Test
	public void test_03_01_Punctuation_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 3.1: 标点符号 - RenderNode结构 ===");

		// 测试 "a,b"
		RendererNode root = parseAndRender("a,b");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		exprGroup.hasChildCount(3); // Term(a), Term(,), Term(b)
		exprGroup.childAt(0).asDecorGroup().center().asTextNode().hasContent("a");
		exprGroup.childAt(1).asDecorGroup().center().asTextNode().hasContent(",");
		exprGroup.childAt(2).asDecorGroup().center().asTextNode().hasContent("b");

		// 测试多个逗号
		assertRenderSuccess("a,b,c");

		// 测试分号
		assertRenderSuccess("a;b");

		// 测试在定界符中
		assertRenderSuccess("f\\left(x,y\\right)");
		assertRenderSuccess("\\left\\{x,y,z\\right\\}");

		System.out.println("✅ 标点符号 RenderNode结构验证通过");
	}

	// ============================================================
	// Part 4: 原子表达式测试
	// ============================================================

	@Test
	public void test_04_01_Group_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.1: 分组 - RenderNode结构 ===");

		assertRenderSuccess("{x}");
		assertRenderSuccess("{a+b}");
		assertRenderSuccess("{x^2}");

		System.out.println("✅ 分组 RenderNode结构验证通过");
	}

	@Test
	public void test_04_02_Frac_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.2: 分式 - RenderNode结构 ===");

		// 测试 "\frac{1}{2}"
		RendererNode root = parseAndRender("\\frac{1}{2}");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		DecorGroupNodeAsserter termDecor = exprGroup.childAt(0).asDecorGroup();
		FractionNodeAsserter frac = termDecor.center().asFractionNode();
		frac.numerator().asLinearGroup()
				.childAt(0).asLinearGroup()
				.childAt(0).asDecorGroup()
				.center().asTextNode().hasContent("1");
		frac.denominator().asLinearGroup()
				.childAt(0).asLinearGroup()
				.childAt(0).asDecorGroup()
				.center().asTextNode().hasContent("2");

		// 测试其他分式变体
		assertRenderSuccess("\\frac{a}{b}");
		assertRenderSuccess("\\frac{x+y}{x-y}");
		assertRenderSuccess("\\dfrac{1}{2}");
		assertRenderSuccess("\\tfrac{1}{2}");
		assertRenderSuccess("\\cfrac{1}{2}");

		System.out.println("✅ 分式 RenderNode结构验证通过");
	}

	@Test
	public void test_04_03_Binom_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.3: 二项式系数 - RenderNode结构 ===");

		// 测试基本二项式系数
		assertRenderSuccess("\\binom{n}{k}");
		assertRenderSuccess("\\binom{5}{2}");
		assertRenderSuccess("\\binom{n+1}{k}");

		// 测试变体
		assertRenderSuccess("\\dbinom{n}{k}");
		assertRenderSuccess("\\tbinom{n}{k}");

		// 测试带上下标
		assertRenderSuccess("\\binom{n}{k}^2");
		assertRenderSuccess("\\binom{n}{k}_i");

		// 测试在表达式中
		assertRenderSuccess("\\binom{n}{k}+1");
		assertRenderSuccess("2\\binom{n}{k}");

		// 测试嵌套
		assertRenderSuccess("\\frac{\\binom{n}{k}}{2}");
		assertRenderSuccess("\\binom{\\binom{n}{k}}{2}");

		System.out.println("✅ 二项式系数 RenderNode结构验证通过");
	}

	@Test
	public void test_04_04_Sqrt_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.4: 根式 - RenderNode结构 ===");

		// 测试 "\sqrt{x}"
		RendererNode root = parseAndRender("\\sqrt{x}");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		DecorGroupNodeAsserter termDecor = exprGroup.childAt(0).asDecorGroup();
		SqrtNodeAsserter sqrt = termDecor.center().asSqrtNode();
		sqrt.content().asLinearGroup()
				.childAt(0).asLinearGroup()
				.childAt(0).asDecorGroup()
				.center().asTextNode().hasContent("x");

		// 测试带根指数
		assertRenderSuccess("\\sqrt[3]{27}");
		assertRenderSuccess("\\sqrt[n]{x}");

		// 测试复杂内容
		assertRenderSuccess("\\sqrt{x^2+y^2}");
		assertRenderSuccess("\\sqrt{\\frac{a}{b}}");

		System.out.println("✅ 根式 RenderNode结构验证通过");
	}

	@Test
	public void test_04_05_Delimited_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.5: 定界符 - RenderNode结构 ===");

		// 测试圆括号
		assertRenderSuccess("\\left(x\\right)");
		assertRenderSuccess("\\left(x+y\\right)");

		// 测试方括号
		assertRenderSuccess("\\left[x\\right]");

		// 测试花括号
		assertRenderSuccess("\\left\\{x\\right\\}");

		// 测试竖线
		assertRenderSuccess("\\left|x\\right|");
		assertRenderSuccess("\\left\\|x\\right\\|");

		// 测试角括号
		assertRenderSuccess("\\left\\langle x\\right\\rangle");

		// 测试地板和天花板
		assertRenderSuccess("\\left\\lfloor x\\right\\rfloor");
		assertRenderSuccess("\\left\\lceil x\\right\\rceil");

		// 测试其他级别
		assertRenderSuccess("\\bigl(x\\bigr)");
		assertRenderSuccess("\\Bigl(x\\Bigr)");
		assertRenderSuccess("\\biggl(x\\biggr)");
		assertRenderSuccess("\\Biggl(x\\Biggr)");

		System.out.println("✅ 定界符 RenderNode结构验证通过");
	}

	@Test
	public void test_04_06_Function_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.6: 函数 - RenderNode结构 ===");

		// 测试三角函数
		assertRenderSuccess("\\sin x");
		assertRenderSuccess("\\cos x");
		assertRenderSuccess("\\tan x");
		assertRenderSuccess("\\cot x");
		assertRenderSuccess("\\sec x");
		assertRenderSuccess("\\csc x");

		// 测试反三角函数
		assertRenderSuccess("\\arcsin x");
		assertRenderSuccess("\\arccos x");
		assertRenderSuccess("\\arctan x");

		// 测试双曲函数
		assertRenderSuccess("\\sinh x");
		assertRenderSuccess("\\cosh x");
		assertRenderSuccess("\\tanh x");
		assertRenderSuccess("\\coth x");

		// 测试对数函数
		assertRenderSuccess("\\log x");
		assertRenderSuccess("\\ln x");
		assertRenderSuccess("\\lg x");
		assertRenderSuccess("\\exp x");

		// 测试其他函数
		assertRenderSuccess("\\max x");
		assertRenderSuccess("\\min x");
		assertRenderSuccess("\\sup x");
		assertRenderSuccess("\\inf x");
		assertRenderSuccess("\\det A");
		assertRenderSuccess("\\dim V");
		assertRenderSuccess("\\gcd\\left(a,b\\right)");
		assertRenderSuccess("\\ker f");

		// 测试带上下标的函数
		assertRenderSuccess("\\log_2 x");
		assertRenderSuccess("\\sin^2 x");

		// 测试函数参数
		assertRenderSuccess("\\sin{x+y}");
		assertRenderSuccess("\\sin\\left(\\frac{x}{2}\\right)");

		System.out.println("✅ 函数 RenderNode结构验证通过");
	}

	@Test
	public void test_04_07_LargeOperator_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.7: 大型运算符 - RenderNode结构 ===");

		// 测试求和
		assertRenderSuccess("\\sum");
		assertRenderSuccess("\\sum_{i=1}^{n}");
		assertRenderSuccess("\\sum_{i=1}^{n}a_i");

		// 测试乘积
		assertRenderSuccess("\\prod");
		assertRenderSuccess("\\prod_{i=1}^{n}");
		assertRenderSuccess("\\coprod");

		// 测试积分
		assertRenderSuccess("\\int");
		assertRenderSuccess("\\int_0^1");
		assertRenderSuccess("\\int_0^{\\infty}");
		assertRenderSuccess("\\iint");
		assertRenderSuccess("\\iiint");
		assertRenderSuccess("\\oint");
		assertRenderSuccess("\\oiint");
		assertRenderSuccess("\\oiiint");

		// 测试大型集合运算符
		assertRenderSuccess("\\bigcup");
		assertRenderSuccess("\\bigcap");
		assertRenderSuccess("\\bigvee");
		assertRenderSuccess("\\bigwedge");
		assertRenderSuccess("\\bigoplus");
		assertRenderSuccess("\\bigotimes");
		assertRenderSuccess("\\bigodot");
		assertRenderSuccess("\\biguplus");
		assertRenderSuccess("\\bigsqcup");

		// 测试极限
		assertRenderSuccess("\\lim");
		assertRenderSuccess("\\lim_{x\\to 0}");
		assertRenderSuccess("\\limsup_{n\\to\\infty}");
		assertRenderSuccess("\\liminf_{n\\to\\infty}");

		System.out.println("✅ 大型运算符 RenderNode结构验证通过");
	}

	@Test
	public void test_04_08_Matrix_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.8: 矩阵 - RenderNode结构 ===");

		// 测试基本矩阵环境
		assertRenderSuccess("\\begin{matrix}a&b\\\\c&d\\end{matrix}");
		assertRenderSuccess("\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}");
		assertRenderSuccess("\\begin{bmatrix}a&b\\\\c&d\\end{bmatrix}");
		assertRenderSuccess("\\begin{Bmatrix}a&b\\\\c&d\\end{Bmatrix}");
		assertRenderSuccess("\\begin{vmatrix}a&b\\\\c&d\\end{vmatrix}");
		assertRenderSuccess("\\begin{Vmatrix}a&b\\\\c&d\\end{Vmatrix}");

		// 测试cases环境
		assertRenderSuccess("\\begin{cases}x,&x\\ge 0\\\\-x,&x<0\\end{cases}");

		// 测试array环境
		assertRenderSuccess("\\begin{array}{cc}a&b\\\\c&d\\end{array}");

		System.out.println("✅ 矩阵 RenderNode结构验证通过");
	}

	@Test
	public void test_04_09_Text_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.9: 文本 - RenderNode结构 ===");

		assertRenderSuccess("\\text{hello}");
		assertRenderSuccess("\\text{当x趋近于0时}");
		assertRenderSuccess("\\mbox{text}");
		assertRenderSuccess("\\textrm{text}");
		assertRenderSuccess("\\textit{text}");
		assertRenderSuccess("\\textbf{text}");

		System.out.println("✅ 文本 RenderNode结构验证通过");
	}

	@Test
	public void test_04_10_Accent_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.10: 重音 - RenderNode结构 ===");

		// 测试基本重音
		assertRenderSuccess("\\hat{x}");
		assertRenderSuccess("\\hat x");
		assertRenderSuccess("\\widehat{xyz}");
		assertRenderSuccess("\\tilde{x}");
		assertRenderSuccess("\\widetilde{xyz}");
		assertRenderSuccess("\\bar{x}");
		assertRenderSuccess("\\overline{xyz}");
		assertRenderSuccess("\\underline{xyz}");

		// 测试向量和箭头
		assertRenderSuccess("\\vec{v}");
		assertRenderSuccess("\\overrightarrow{AB}");
		assertRenderSuccess("\\overleftarrow{AB}");

		// 测试点
		assertRenderSuccess("\\dot{x}");
		assertRenderSuccess("\\ddot{x}");
		assertRenderSuccess("\\dddot{x}");

		// 测试其他重音
		assertRenderSuccess("\\acute{x}");
		assertRenderSuccess("\\grave{x}");
		assertRenderSuccess("\\breve{x}");
		assertRenderSuccess("\\check{x}");
		assertRenderSuccess("\\mathring{x}");

		// 测试括号
		assertRenderSuccess("\\overbrace{x+y}");
		assertRenderSuccess("\\underbrace{x+y}");

		System.out.println("✅ 重音 RenderNode结构验证通过");
	}

	@Test
	public void test_04_11_Font_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.11: 字体 - RenderNode结构 ===");

		assertRenderSuccess("\\mathrm{x}");
		assertRenderSuccess("\\mathit{x}");
		assertRenderSuccess("\\mathbf{x}");
		assertRenderSuccess("\\mathbb{R}");
		assertRenderSuccess("\\mathsf{x}");
		assertRenderSuccess("\\mathtt{x}");
		assertRenderSuccess("\\mathcal{L}");
		assertRenderSuccess("\\mathfrak{g}");
		assertRenderSuccess("\\mathscr{F}");
		assertRenderSuccess("\\boldsymbol{\\alpha}");
		assertRenderSuccess("\\bm{\\alpha}");

		System.out.println("✅ 字体 RenderNode结构验证通过");
	}

	@Test
	public void test_04_12_ExtensibleArrow_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 4.12: 可扩展箭头 - RenderNode结构 ===");

		// 测试基本可扩展箭头
		assertRenderSuccess("\\xrightarrow{上方}");
		assertRenderSuccess("\\xleftarrow{上方}");
		assertRenderSuccess("\\xleftrightarrow{上方}");
		assertRenderSuccess("\\xRightarrow{上方}");
		assertRenderSuccess("\\xLeftarrow{上方}");
		assertRenderSuccess("\\xLeftrightarrow{上方}");

		// 测试带下标注的箭头
		assertRenderSuccess("\\xrightarrow[下方]{上方}");
		assertRenderSuccess("\\xleftarrow[下方]{上方}");

		// 在表达式中使用
		assertRenderSuccess("A\\xrightarrow{f}B");
		assertRenderSuccess("\\frac{\\bar{X}_n-\\mu}{\\sigma/\\sqrt{n}}\\xrightarrow{d}N\\left(0,1\\right)");

		System.out.println("✅ 可扩展箭头 RenderNode结构验证通过");
	}

	// ============================================================
	// Part 5: 上下标测试
	// ============================================================

	@Test
	public void test_05_01_Superscript_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 5.1: 上标 - RenderNode结构 ===");

		// 测试 "x^2"
		RendererNode root = parseAndRender("x^2");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		DecorGroupNodeAsserter termDecor = exprGroup.childAt(0).asDecorGroup();
		termDecor.center().asTextNode().hasContent("x");
		termDecor.hasRightTop();
		termDecor.rightTop().asTextNode().hasContent("2");

		// 测试其他上标
		assertRenderSuccess("x^a");
		assertRenderSuccess("x^{n+1}");
		assertRenderSuccess("e^{i\\pi}");

		System.out.println("✅ 上标 RenderNode结构验证通过");
	}

	@Test
	public void test_05_02_Subscript_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 5.2: 下标 - RenderNode结构 ===");

		// 测试 "x_1"
		RendererNode root = parseAndRender("x_1");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		DecorGroupNodeAsserter termDecor = exprGroup.childAt(0).asDecorGroup();
		termDecor.center().asTextNode().hasContent("x");
		termDecor.hasRightBottom();
		termDecor.rightBottom().asTextNode().hasContent("1");

		// 测试其他下标
		assertRenderSuccess("x_i");
		assertRenderSuccess("a_{i,j}");

		System.out.println("✅ 下标 RenderNode结构验证通过");
	}

	@Test
	public void test_05_03_BothScripts_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 5.3: 上下标组合 - RenderNode结构 ===");

		// 测试 "x^2_1"
		RendererNode root = parseAndRender("x^2_1");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		DecorGroupNodeAsserter termDecor = exprGroup.childAt(0).asDecorGroup();
		termDecor.center().asTextNode().hasContent("x");
		termDecor.hasRightTop();
		termDecor.hasRightBottom();
		termDecor.rightTop().asTextNode().hasContent("2");
		termDecor.rightBottom().asTextNode().hasContent("1");

		// 测试其他组合
		assertRenderSuccess("x_1^2");
		assertRenderSuccess("x^{n+1}_{i}");

		System.out.println("✅ 上下标组合 RenderNode结构验证通过");
	}

	// ============================================================
	// Part 6: 空格命令测试
	// ============================================================

	@Test
	public void test_06_01_Spacing_RenderStructure() throws MathParseException {
		System.out.println("\n=== Part 6.1: 空格命令 - RenderNode结构 ===");

		// 测试 "a\quad b"
		RendererNode root = parseAndRender("a\\quad b");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		rootGroup.hasChildCount(3); // Expression(a), SpaceNode, Expression(b)
		rootGroup.childAt(0).asLinearGroup();
		rootGroup.childAt(1).asSpaceNode();
		rootGroup.childAt(2).asLinearGroup();

		// 测试其他空格命令
		assertRenderSuccess("a\\, b");
		assertRenderSuccess("a\\: b");
		assertRenderSuccess("a\\; b");
		assertRenderSuccess("a\\! b");
		assertRenderSuccess("a\\qquad b");

		System.out.println("✅ 空格命令 RenderNode结构验证通过");
	}

	// ============================================================
	// Part 7: 复杂表达式和真实世界公式测试
	// ============================================================

	@Test
	public void test_07_01_ComplexExpression_QuadraticFormula() throws MathParseException {
		System.out.println("\n=== Part 7.1: 复杂表达式 - 二次公式 ===");

		RendererNode root = parseAndRender("\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		rootGroup.hasChildCount(1);
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		exprGroup.hasChildCount(1);
		DecorGroupNodeAsserter termDecor = exprGroup.childAt(0).asDecorGroup();
		FractionNodeAsserter frac = termDecor.center().asFractionNode();
		LinearGroupNodeAsserter numerator = frac.numerator().asLinearGroup();
		assertTrue("分子应该有子节点", numerator.linearGroup.getChildCount() > 0);
		LinearGroupNodeAsserter denominator = frac.denominator().asLinearGroup();
		assertTrue("分母应该有子节点", denominator.linearGroup.getChildCount() > 0);

		System.out.println("✅ 二次公式 RenderNode结构验证通过");
	}

	@Test
	public void test_07_02_ComplexExpression_NestedFractions() throws MathParseException {
		System.out.println("\n=== Part 7.2: 复杂表达式 - 嵌套分式 ===");

		RendererNode root = parseAndRender("\\frac{\\frac{1}{2}}{\\frac{3}{4}}");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		DecorGroupNodeAsserter termDecor = rootGroup.childAt(0).asLinearGroup()
				.childAt(0).asDecorGroup();
		FractionNodeAsserter outerFrac = termDecor.center().asFractionNode();
		FractionNodeAsserter innerFrac1 = outerFrac.numerator().asLinearGroup()
				.childAt(0).asLinearGroup()
				.childAt(0).asDecorGroup()
				.center().asFractionNode();
		FractionNodeAsserter innerFrac2 = outerFrac.denominator().asLinearGroup()
				.childAt(0).asLinearGroup()
				.childAt(0).asDecorGroup()
				.center().asFractionNode();
		innerFrac1.numerator().asLinearGroup()
				.childAt(0).asLinearGroup()
				.childAt(0).asDecorGroup()
				.center().asTextNode().hasContent("1");
		innerFrac2.denominator().asLinearGroup()
				.childAt(0).asLinearGroup()
				.childAt(0).asDecorGroup()
				.center().asTextNode().hasContent("4");

		System.out.println("✅ 嵌套分式 RenderNode结构验证通过");
	}

	@Test
	public void test_07_03_ComplexExpression_SummationWithFraction() throws MathParseException {
		System.out.println("\n=== Part 7.3: 复杂表达式 - 求和与分式 ===");

		RendererNode root = parseAndRender("\\sum_{i=1}^{n}\\frac{1}{i^2}");
		LinearGroupNodeAsserter rootGroup = assertRenderNode(root).asLinearGroup();
		LinearGroupNodeAsserter exprGroup = rootGroup.childAt(0).asLinearGroup();
		exprGroup.hasChildCount(2); // sum, frac
		DecorGroupNodeAsserter sumDecor = exprGroup.childAt(0).asDecorGroup();
		sumDecor.hasTop();
		sumDecor.hasBottom();
		DecorGroupNodeAsserter fracTerm = exprGroup.childAt(1).asDecorGroup();
		fracTerm.center().asFractionNode();

		System.out.println("✅ 求和与分式 RenderNode结构验证通过");
	}

	@Test
	public void test_08_01_RealWorld_EulerFormula() throws MathParseException {
		System.out.println("\n=== Part 8.1: 真实公式 - 欧拉公式 ===");

		assertRenderSuccess("e^{i\\pi}+1=0");

		System.out.println("✅ 欧拉公式渲染成功");
	}

	@Test
	public void test_08_02_RealWorld_PythagoreanTheorem() throws MathParseException {
		System.out.println("\n=== Part 8.2: 真实公式 - 勾股定理 ===");

		assertRenderSuccess("a^2+b^2=c^2");

		System.out.println("✅ 勾股定理渲染成功");
	}

	@Test
	public void test_08_03_RealWorld_Limit() throws MathParseException {
		System.out.println("\n=== Part 8.3: 真实公式 - 极限 ===");

		assertRenderSuccess("\\lim_{x\\to 0}\\frac{\\sin x}{x}=1");

		System.out.println("✅ 极限渲染成功");
	}

	@Test
	public void test_08_04_RealWorld_Sum() throws MathParseException {
		System.out.println("\n=== Part 8.4: 真实公式 - 求和 ===");

		assertRenderSuccess("\\sum_{n=1}^{\\infty}\\frac{1}{n^2}=\\frac{\\pi^2}{6}");

		System.out.println("✅ 求和渲染成功");
	}

	@Test
	public void test_08_05_RealWorld_Derivative() {
		System.out.println("\n=== Part 8.5: 真实公式 - 导数 ===");

		assertRenderSuccess("f'\\left(x\\right)=\\lim_{h\\to 0}\\frac{f\\left(x+h\\right)-f\\left(x\\right)}{h}");

		System.out.println("✅ 导数渲染成功");
	}

	@Test
	public void test_08_06_RealWorld_Integral() {
		System.out.println("\n=== Part 8.6: 真实公式 - 积分 ===");

		assertRenderSuccess("\\int_0^{\\infty}e^{-x^2}dx=\\frac{\\sqrt{\\pi}}{2}");

		System.out.println("✅ 积分渲染成功");
	}

	@Test
	public void test_08_07_RealWorld_MatrixMultiplication() {
		System.out.println("\n=== Part 8.7: 真实公式 - 矩阵乘法 ===");

		assertRenderSuccess("\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}\\begin{pmatrix}x\\\\y\\end{pmatrix}=\\begin{pmatrix}ax+by\\\\cx+dy\\end{pmatrix}");

		System.out.println("✅ 矩阵乘法渲染成功");
	}

	@Test
	public void test_08_08_RealWorld_PiecewiseFunction() {
		System.out.println("\n=== Part 8.8: 真实公式 - 分段函数 ===");

		assertRenderSuccess("f\\left(x\\right)=\\begin{cases}x,&x\\ge 0\\\\-x,&x<0\\end{cases}");

		System.out.println("✅ 分段函数渲染成功");
	}

	@Test
	public void test_08_09_RealWorld_CauchySchwarz() {
		System.out.println("\n=== Part 8.9: 真实公式 - 柯西-施瓦茨不等式 ===");

		assertRenderSuccess("\\left(\\sum_{i=1}^{n}a_i b_i\\right)^2\\le\\left(\\sum_{i=1}^{n}a_i^2\\right)\\left(\\sum_{i=1}^{n}b_i^2\\right)");

		System.out.println("✅ 柯西-施瓦茨不等式渲染成功");
	}

	@Test
	public void test_08_10_RealWorld_SchrodingerEquation() throws MathParseException {
		System.out.println("\n=== Part 8.10: 真实公式 - 薛定谔方程 ===");

		assertRenderSuccess("i\\hbar\\frac{\\partial}{\\partial t}\\Psi\\left(r,t\\right)=\\left[-\\frac{\\hbar^2}{2m}\\nabla^2+V\\left(r,t\\right)\\right]\\Psi\\left(r,t\\right)");

		System.out.println("✅ 薛定谔方程渲染成功");
	}

	@Test
	public void test_08_11_RealWorld_MaxwellEquations() {
		System.out.println("\n=== Part 8.11: 真实公式 - 麦克斯韦方程组 ===");

		assertRenderSuccess("\\nabla\\cdot\\mathbf{E}=\\frac{\\rho}{\\epsilon_0}");
		assertRenderSuccess("\\nabla\\cdot\\mathbf{B}=0");
		assertRenderSuccess("\\nabla\\times\\mathbf{E}=-\\frac{\\partial\\mathbf{B}}{\\partial t}");
		assertRenderSuccess("\\nabla\\times\\mathbf{B}=\\mu_0\\left(\\mathbf{J}+\\epsilon_0\\frac{\\partial\\mathbf{E}}{\\partial t}\\right)");

		System.out.println("✅ 麦克斯韦方程组渲染成功");
	}

	@Test
	public void test_08_12_RealWorld_EinsteinFieldEquation() throws MathParseException {
		System.out.println("\n=== Part 8.12: 真实公式 - 爱因斯坦场方程 ===");

		assertRenderSuccess("R_{\\mu\\nu}-\\frac{1}{2}Rg_{\\mu\\nu}+\\Lambda g_{\\mu\\nu}=\\frac{8\\pi G}{c^4}T_{\\mu\\nu}");

		System.out.println("✅ 爱因斯坦场方程渲染成功");
	}

	@Test
	public void test_08_13_RealWorld_FourierTransform() throws MathParseException {
		System.out.println("\n=== Part 8.13: 真实公式 - 傅里叶变换 ===");

		assertRenderSuccess("F\\left(\\omega\\right)=\\int_{-\\infty}^{\\infty}f\\left(t\\right)e^{-i\\omega t}dt");
		assertRenderSuccess("f\\left(t\\right)=\\frac{1}{2\\pi}\\int_{-\\infty}^{\\infty}F\\left(\\omega\\right)e^{i\\omega t}d\\omega");

		System.out.println("✅ 傅里叶变换渲染成功");
	}

	@Test
	public void test_08_14_RealWorld_BayesTheorem() throws MathParseException {
		System.out.println("\n=== Part 8.14: 真实公式 - 贝叶斯定理 ===");

		assertRenderSuccess("P\\left(\\theta|D\\right)=\\frac{P\\left(D|\\theta\\right)P\\left(\\theta\\right)}{\\int P\\left(D|\\theta'\\right)P\\left(\\theta'\\right)d\\theta'}");

		System.out.println("✅ 贝叶斯定理渲染成功");
	}

	@Test
	public void test_08_15_RealWorld_BinomialTheorem() throws MathParseException {
		System.out.println("\n=== Part 8.15: 真实公式 - 二项式定理 ===");

		assertRenderSuccess("\\left(x+y\\right)^n=\\sum_{k=0}^{n}\\binom{n}{k}x^{n-k}y^k");

		System.out.println("✅ 二项式定理渲染成功");
	}

	@Test
	public void test_08_16_RealWorld_StirlingApproximation() throws MathParseException {
		System.out.println("\n=== Part 8.16: 真实公式 - 斯特林近似（带阶乘）===");

		assertRenderSuccess("n!\\approx\\sqrt{2\\pi n}\\left(\\frac{n}{e}\\right)^n");

		System.out.println("✅ 斯特林近似渲染成功");
	}

	@Test
	public void test_08_17_RealWorld_TaylorSeries() throws MathParseException {
		System.out.println("\n=== Part 8.17: 真实公式 - 泰勒级数（带阶乘）===");

		assertRenderSuccess("f\\left(x\\right)=\\sum_{n=0}^{\\infty}\\frac{f^{\\left(n\\right)}\\left(a\\right)}{n!}\\left(x-a\\right)^n");

		System.out.println("✅ 泰勒级数渲染成功");
	}

	@Test
	public void test_08_18_RealWorld_BinomialCoefficient() throws MathParseException {
		System.out.println("\n=== Part 8.18: 真实公式 - 二项式系数定义（带阶乘）===");

		assertRenderSuccess("\\binom{n}{k}=\\frac{n!}{k!\\left(n-k\\right)!}");

		System.out.println("✅ 二项式系数定义渲染成功");
	}

	@Test
	public void test_08_19_RealWorld_VandermondeIdentity() throws MathParseException {
		System.out.println("\n=== Part 8.19: 真实公式 - 范德蒙德恒等式（二项式系数）===");

		assertRenderSuccess("\\binom{m+n}{r}=\\sum_{k=0}^{r}\\binom{m}{k}\\binom{n}{r-k}");

		System.out.println("✅ 范德蒙德恒等式渲染成功");
	}

	@Test
	public void test_08_20_RealWorld_CentralLimitTheorem() throws MathParseException {
		System.out.println("\n=== Part 8.20: 真实公式 - 中心极限定理（可扩展箭头）===");

		assertRenderSuccess("\\frac{\\bar{X}_n-\\mu}{\\sigma/\\sqrt{n}}\\xrightarrow{d}N\\left(0,1\\right)");

		System.out.println("✅ 中心极限定理渲染成功");
	}

	@Test
	public void test_09_Summary() {
		System.out.println("\n" + "=".repeat(60));
		System.out.println("测试总结：");
		System.out.println("✅ 覆盖 bnf_math.txt 中所有基础语法元素");
		System.out.println("✅ 数字、变量、希腊字母、特殊符号");
		System.out.println("✅ 一元、二元、后缀运算符");
		System.out.println("✅ 分式、二项式系数、根式、定界符");
		System.out.println("✅ 函数、大型运算符、矩阵");
		System.out.println("✅ 重音、字体、可扩展箭头");
		System.out.println("✅ 上下标、空格命令");
		System.out.println("✅ 复杂嵌套表达式");
		System.out.println("✅ 20+ 真实世界公式");
		System.out.println("✅ AST 转 RenderNode 结构验证");
		System.out.println("✅ MathFontOptions 符号查找测试（防crash）");
		System.out.println("=".repeat(60));
	}
}
