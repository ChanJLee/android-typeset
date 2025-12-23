package me.chan.texas.ext.markdown.math.renderer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;

public class RendererNodeUnitTest {
	private MathPaint.Styles defaultStyles;
	private MathPaint defaultPaint;

	@Before
	public void setUp() {
		TexasPaint texasPaint = new TexasPaintImpl();
		texasPaint.reset(new PaintSet(new MockTextPaint(1)));
		defaultPaint = new MathPaintImpl(texasPaint);
		defaultStyles = new MathPaint.Styles(defaultPaint);
	}

	@Test
	public void testMeasureSpec() {
		int spec = RendererNode.makeMeasureSpec(12, RendererNode.EXACTLY);
		Assert.assertEquals(12, RendererNode.getSize(spec));
		Assert.assertEquals(RendererNode.EXACTLY, RendererNode.getMode(spec));
	}

	// ==================== LinearGroupNode 优化测试 ====================

	@Test
	public void testLinearGroupNode_singleChild_sameStyles_shouldOptimize() {
		// 只有一个子节点且 styles 相同，应该优化为子节点
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode child = new TextNode(styles, "x");
		List<RendererNode> nodes = asList(child);

		LinearGroupNode groupNode = new LinearGroupNode(styles, nodes);
		RendererNode optimized = groupNode.optimize();

		Assert.assertSame("应该返回子节点本身", child, optimized);
	}

	@Test
	public void testLinearGroupNode_singleChild_differentStyles_shouldNotOptimize() {
		// 只有一个子节点但 styles 不同，不应该优化
		MathPaint.Styles parentStyles = new MathPaint.Styles(defaultPaint);
		MathPaint.Styles childStyles = new MathPaint.Styles(defaultPaint);
		childStyles.setTextSize(20);

		TextNode child = new TextNode(childStyles, "x");
		List<RendererNode> nodes = asList(child);

		LinearGroupNode groupNode = new LinearGroupNode(parentStyles, nodes);
		RendererNode optimized = groupNode.optimize();

		Assert.assertSame("styles 不同，应该返回自身", groupNode, optimized);
	}

	@Test
	public void testLinearGroupNode_singleChild_stylesReferenceEquality() {
		// 测试 styles 引用相等性：即使内容相同，如果不是同一个对象也不会优化
		MathPaint.Styles parentStyles = new MathPaint.Styles(defaultPaint);
		MathPaint.Styles childStyles = new MathPaint.Styles(defaultPaint);
		// 两个 styles 内容相同，但不是同一个引用

		TextNode child = new TextNode(childStyles, "x");
		List<RendererNode> nodes = asList(child);

		LinearGroupNode groupNode = new LinearGroupNode(parentStyles, nodes);
		RendererNode optimized = groupNode.optimize();

		Assert.assertSame("styles 引用不同，应该返回自身", groupNode, optimized);
	}

	@Test
	public void testLinearGroupNode_multipleChildren_shouldNotOptimize() {
		// 有多个子节点，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode child1 = new TextNode(styles, "x");
		TextNode child2 = new TextNode(styles, "y");
		List<RendererNode> nodes = Arrays.asList(child1, child2);

		LinearGroupNode groupNode = new LinearGroupNode(styles, nodes);
		RendererNode optimized = groupNode.optimize();

		Assert.assertSame("多个子节点，应该返回自身", groupNode, optimized);
	}

	@Test
	public void testLinearGroupNode_emptyChildren_shouldNotOptimize() {
		// 空子节点列表，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		List<RendererNode> nodes = new ArrayList<>();

		LinearGroupNode groupNode = new LinearGroupNode(styles, nodes);
		RendererNode optimized = groupNode.optimize();

		Assert.assertSame("空子节点，应该返回自身", groupNode, optimized);
	}

	@Test
	public void testLinearGroupNode_nestedOptimizable_shouldRecursivelyOptimize() {
		// 子节点也是 OptimizableRendererNode，应该递归优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode innerChild = new TextNode(styles, "x");
		List<RendererNode> innerNodes = asList(innerChild);
		LinearGroupNode innerGroup = new LinearGroupNode(styles, innerNodes);

		List<RendererNode> outerNodes = asList(innerGroup);
		LinearGroupNode outerGroup = new LinearGroupNode(styles, outerNodes);

		RendererNode optimized = outerGroup.optimize();

		Assert.assertSame("应该递归优化到最内层的 TextNode", innerChild, optimized);
	}

	@Test
	public void testLinearGroupNode_horizontalGravity() {
		// 测试水平方向的 LinearGroupNode
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode child = new TextNode(styles, "x");
		List<RendererNode> nodes = asList(child);

		LinearGroupNode groupNode = new LinearGroupNode(styles, nodes, LinearGroupNode.Gravity.HORIZONTAL);
		RendererNode optimized = groupNode.optimize();

		Assert.assertSame(child, optimized);
	}

	@Test
	public void testLinearGroupNode_verticalGravity() {
		// 测试垂直方向的 LinearGroupNode
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode child = new TextNode(styles, "x");
		List<RendererNode> nodes = asList(child);

		LinearGroupNode groupNode = new LinearGroupNode(styles, nodes, LinearGroupNode.Gravity.VERTICAL);
		RendererNode optimized = groupNode.optimize();

		Assert.assertSame(child, optimized);
	}

	@Test
	public void testLinearGroupNode_withGridGroupNode_shouldNotOptimizeFurther() {
		// GridGroupNode 不实现 OptimizableRendererNode
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		List<RendererNode> gridNodes = Arrays.asList(
				new TextNode(styles, "a"),
				new TextNode(styles, "b")
		);
		GridGroupNode gridNode = new GridGroupNode(styles, 2, gridNodes);

		List<RendererNode> nodes = asList(gridNode);
		LinearGroupNode groupNode = new LinearGroupNode(styles, nodes);
		RendererNode optimized = groupNode.optimize();

		// GridGroupNode 不能被优化，但 LinearGroupNode 可以优化为 GridGroupNode
		Assert.assertSame("应该优化为 GridGroupNode", gridNode, optimized);
	}

	// ==================== DecorGroupNode 优化测试 ====================

	@Test
	public void testDecorGroupNode_onlyCenter_sameStyles_shouldOptimize() {
		// 只有 center 节点且 styles 相同，应该优化为 center
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center).build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("只有 center 且 styles 相同，应该返回 center", center, optimized);
	}

	@Test
	public void testDecorGroupNode_onlyCenter_differentStyles_shouldNotOptimize() {
		// 只有 center 节点但 styles 不同，不应该优化
		MathPaint.Styles parentStyles = new MathPaint.Styles(defaultPaint);
		MathPaint.Styles childStyles = new MathPaint.Styles(defaultPaint);
		childStyles.setTextSize(20);

		TextNode center = new TextNode(childStyles, "x");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(parentStyles, center).build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("styles 不同，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_onlyCenter_stylesReferenceEquality() {
		// 测试 styles 引用相等性
		MathPaint.Styles parentStyles = new MathPaint.Styles(defaultPaint);
		MathPaint.Styles childStyles = new MathPaint.Styles(defaultPaint);

		TextNode center = new TextNode(childStyles, "x");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(parentStyles, center).build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("styles 引用不同，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_withTop_shouldNotOptimize() {
		// 有 top 装饰节点，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		TextNode top = new TextNode(styles, "^");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.top(top)
				.build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("有装饰节点，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_withBottom_shouldNotOptimize() {
		// 有 bottom 装饰节点，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		TextNode bottom = new TextNode(styles, "_");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.bottom(bottom)
				.build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("有装饰节点，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_withLeft_shouldNotOptimize() {
		// 有 left 装饰节点，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		TextNode left = new TextNode(styles, "<");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.left(left)
				.build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("有装饰节点，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_withRight_shouldNotOptimize() {
		// 有 right 装饰节点，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		TextNode right = new TextNode(styles, ">");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.right(right)
				.build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("有装饰节点，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_withLeftTop_shouldNotOptimize() {
		// 有 leftTop 装饰节点，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		TextNode leftTop = new TextNode(styles, "^");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.leftTop(leftTop)
				.build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("有装饰节点，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_withLeftBottom_shouldNotOptimize() {
		// 有 leftBottom 装饰节点，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		TextNode leftBottom = new TextNode(styles, "_");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.leftBottom(leftBottom)
				.build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("有装饰节点，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_withRightTop_shouldNotOptimize() {
		// 有 rightTop 装饰节点，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		TextNode rightTop = new TextNode(styles, "^");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.rightTop(rightTop)
				.build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("有装饰节点，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_withRightBottom_shouldNotOptimize() {
		// 有 rightBottom 装饰节点，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		TextNode rightBottom = new TextNode(styles, "_");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.rightBottom(rightBottom)
				.build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("有装饰节点，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_withMultipleDecors_shouldNotOptimize() {
		// 有多个装饰节点，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		TextNode top = new TextNode(styles, "^");
		TextNode bottom = new TextNode(styles, "_");
		TextNode left = new TextNode(styles, "<");
		TextNode right = new TextNode(styles, ">");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.top(top)
				.bottom(bottom)
				.left(left)
				.right(right)
				.build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("有多个装饰节点，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_nestedOptimizable_shouldRecursivelyOptimize() {
		// center 是 OptimizableRendererNode，应该递归优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode innerChild = new TextNode(styles, "x");
		List<RendererNode> innerNodes = asList(innerChild);
		LinearGroupNode innerGroup = new LinearGroupNode(styles, innerNodes);

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, innerGroup).build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("应该递归优化到最内层的 TextNode", innerChild, optimized);
	}

	@Test
	public void testDecorGroupNode_decorChildrenOptimizable_shouldRecursivelyOptimize() {
		// 装饰节点是 OptimizableRendererNode，应该递归优化但仍返回自身
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		TextNode topInner = new TextNode(styles, "^");
		List<RendererNode> topNodes = asList(topInner);
		LinearGroupNode topGroup = new LinearGroupNode(styles, topNodes);

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.top(topGroup)
				.build();
		RendererNode optimized = decorNode.optimize();

		// 有装饰节点，应该返回自身，但装饰节点内部应该被优化
		Assert.assertSame("有装饰节点，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_withNonOptimizableDecor_shouldNotOptimize() {
		// 装饰节点不是 OptimizableRendererNode（如 SpaceNode），应该保留
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		SpaceNode topSpace = new SpaceNode(styles, 10, 10);

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.top(topSpace)
				.build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("有 SpaceNode 装饰，应该返回自身", decorNode, optimized);
	}

	@Test
	public void testDecorGroupNode_withAllNonOptimizableDecors_shouldNotOptimize() {
		// 所有装饰节点都不是 OptimizableRendererNode
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode center = new TextNode(styles, "x");
		SpaceNode top = new SpaceNode(styles, 5, 5);
		SpaceNode bottom = new SpaceNode(styles, 5, 5);
		SpaceNode left = new SpaceNode(styles, 5, 5);
		SpaceNode right = new SpaceNode(styles, 5, 5);

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, center)
				.top(top)
				.bottom(bottom)
				.left(left)
				.right(right)
				.build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("有非可优化装饰节点，应该返回自身", decorNode, optimized);
	}

	// ==================== BraceLayout 优化测试 ====================

	@Test
	public void testBraceLayout_noBraces_sameStyles_shouldOptimize() {
		// 没有左右括号且 styles 相同，应该优化为 content
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode content = new TextNode(styles, "x");

		BraceGroupNode braceLayout = new BraceGroupNode(styles, 0, null, content, null);
		RendererNode optimized = braceLayout.optimize();

		Assert.assertSame("没有括号且 styles 相同，应该返回 content", content, optimized);
	}

	@Test
	public void testBraceLayout_noBraces_differentStyles_shouldNotOptimize() {
		// 没有左右括号但 styles 不同，不应该优化
		MathPaint.Styles parentStyles = new MathPaint.Styles(defaultPaint);
		MathPaint.Styles childStyles = new MathPaint.Styles(defaultPaint);
		childStyles.setTextSize(20);

		TextNode content = new TextNode(childStyles, "x");

		BraceGroupNode braceLayout = new BraceGroupNode(parentStyles, 0, null, content, null);
		RendererNode optimized = braceLayout.optimize();

		Assert.assertSame("styles 不同，应该返回自身", braceLayout, optimized);
	}

	@Test
	public void testBraceLayout_noBraces_stylesReferenceEquality() {
		// 测试 styles 引用相等性
		MathPaint.Styles parentStyles = new MathPaint.Styles(defaultPaint);
		MathPaint.Styles childStyles = new MathPaint.Styles(defaultPaint);

		TextNode content = new TextNode(childStyles, "x");

		BraceGroupNode braceLayout = new BraceGroupNode(parentStyles, 0, null, content, null);
		RendererNode optimized = braceLayout.optimize();

		Assert.assertSame("styles 引用不同，应该返回自身", braceLayout, optimized);
	}

	@Test
	public void testBraceLayout_withLeftBrace_shouldNotOptimize() {
		// 有左括号，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode content = new TextNode(styles, "x");
		SpaceNode leftBrace = new SpaceNode(styles, 10, 20);

		BraceGroupNode braceLayout = new BraceGroupNode(styles, 0, leftBrace, content, null);
		RendererNode optimized = braceLayout.optimize();

		Assert.assertSame("有左括号，应该返回自身", braceLayout, optimized);
	}

	@Test
	public void testBraceLayout_withRightBrace_shouldNotOptimize() {
		// 有右括号，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode content = new TextNode(styles, "x");
		SpaceNode rightBrace = new SpaceNode(styles, 10, 20);

		BraceGroupNode braceLayout = new BraceGroupNode(styles, 0, null, content, rightBrace);
		RendererNode optimized = braceLayout.optimize();

		Assert.assertSame("有右括号，应该返回自身", braceLayout, optimized);
	}

	@Test
	public void testBraceLayout_withBothBraces_shouldNotOptimize() {
		// 有左右括号，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode content = new TextNode(styles, "x");
		SpaceNode leftBrace = new SpaceNode(styles, 10, 20);
		SpaceNode rightBrace = new SpaceNode(styles, 10, 20);

		BraceGroupNode braceLayout = new BraceGroupNode(styles, 0, leftBrace, content, rightBrace);
		RendererNode optimized = braceLayout.optimize();

		Assert.assertSame("有左右括号，应该返回自身", braceLayout, optimized);
	}

	@Test
	public void testBraceLayout_nestedOptimizable_shouldRecursivelyOptimize() {
		// content 是 OptimizableRendererNode，应该递归优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode innerChild = new TextNode(styles, "x");
		List<RendererNode> innerNodes = asList(innerChild);
		LinearGroupNode innerGroup = new LinearGroupNode(styles, innerNodes);

		BraceGroupNode braceLayout = new BraceGroupNode(styles, 0, null, innerGroup, null);
		RendererNode optimized = braceLayout.optimize();

		Assert.assertSame("应该递归优化到最内层的 TextNode", innerChild, optimized);
	}

	@Test
	public void testBraceLayout_contentIsNonOptimizable_shouldStillCheckStyles() {
		// content 不是 OptimizableRendererNode，但应该检查 styles
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		SpaceNode content = new SpaceNode(styles, 10, 10);

		BraceGroupNode braceLayout = new BraceGroupNode(styles, 0, null, content, null);
		RendererNode optimized = braceLayout.optimize();

		Assert.assertSame("styles 相同但 content 不可优化，应该返回 content", content, optimized);
	}

	// ==================== 复杂嵌套优化测试 ====================

	@Test
	public void testComplexNested_linearInDecorInLinear_shouldOptimize() {
		// 测试复杂嵌套：LinearGroupNode -> DecorGroupNode -> LinearGroupNode -> TextNode
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);

		TextNode innerText = new TextNode(styles, "x");
		List<RendererNode> innerLinearNodes = asList(innerText);
		LinearGroupNode innerLinear = new LinearGroupNode(styles, innerLinearNodes);

		DecorGroupNode decor = new DecorGroupNode.Builder(styles, innerLinear).build();

		List<RendererNode> outerLinearNodes = asList(decor);
		LinearGroupNode outerLinear = new LinearGroupNode(styles, outerLinearNodes);

		RendererNode optimized = outerLinear.optimize();

		Assert.assertSame("应该递归优化到最内层的 TextNode", innerText, optimized);
	}

	@Test
	public void testComplexNested_braceInLinearInDecor_shouldOptimize() {
		// 测试复杂嵌套：DecorGroupNode -> LinearGroupNode -> BraceLayout -> TextNode
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);

		TextNode innerText = new TextNode(styles, "x");
		BraceGroupNode brace = new BraceGroupNode(styles, 0, null, innerText, null);

		List<RendererNode> linearNodes = asList(brace);
		LinearGroupNode linear = new LinearGroupNode(styles, linearNodes);

		DecorGroupNode decor = new DecorGroupNode.Builder(styles, linear).build();

		RendererNode optimized = decor.optimize();

		Assert.assertSame("应该递归优化到最内层的 TextNode", innerText, optimized);
	}

	private List<RendererNode> asList(RendererNode node) {
		List<RendererNode> list = new ArrayList<>();
		list.add(node);
		return list;
	}

	@Test
	public void testComplexNested_multiLayer_sameStyles_shouldFullyOptimize() {
		// 测试多层嵌套，全部 styles 相同
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);

		TextNode text = new TextNode(styles, "x");

		List<RendererNode> linear1Nodes = asList(text);
		LinearGroupNode linear1 = new LinearGroupNode(styles, linear1Nodes);

		BraceGroupNode brace = new BraceGroupNode(styles, 0, null, linear1, null);

		List<RendererNode> linear2Nodes = asList(brace);
		LinearGroupNode linear2 = new LinearGroupNode(styles, linear2Nodes);

		DecorGroupNode decor = new DecorGroupNode.Builder(styles, linear2).build();

		List<RendererNode> linear3Nodes = asList(decor);
		LinearGroupNode linear3 = new LinearGroupNode(styles, linear3Nodes);

		RendererNode optimized = linear3.optimize();

		Assert.assertSame("所有层都应该被优化掉，最终返回 TextNode", text, optimized);
	}

	@Test
	public void testComplexNested_mixedStyles_shouldPartiallyOptimize() {
		// 测试多层嵌套，部分 styles 不同
		MathPaint.Styles styles1 = new MathPaint.Styles(defaultPaint);
		MathPaint.Styles styles2 = new MathPaint.Styles(defaultPaint);
		styles2.setTextSize(20);

		TextNode text = new TextNode(styles1, "x");

		List<RendererNode> linear1Nodes = asList(text);
		LinearGroupNode linear1 = new LinearGroupNode(styles1, linear1Nodes);

		// 这里 styles 不同，无法继续优化
		DecorGroupNode decor = new DecorGroupNode.Builder(styles2, linear1).build();

		List<RendererNode> linear2Nodes = asList(decor);
		LinearGroupNode linear2 = new LinearGroupNode(styles2, linear2Nodes);

		RendererNode optimized = linear2.optimize();

		// linear2 只有一个子节点且 styles 相同，应该返回 decor
		Assert.assertSame("外层应该优化为 DecorGroupNode", decor, optimized);
	}

	@Test
	public void testComplexNested_withDecoration_shouldStopOptimization() {
		// 测试嵌套中有装饰节点，优化应该停止
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);

		TextNode text = new TextNode(styles, "x");
		TextNode topDecor = new TextNode(styles, "^");

		List<RendererNode> linearNodes = asList(text);
		LinearGroupNode linear = new LinearGroupNode(styles, linearNodes);

		// DecorGroupNode 有 top 装饰，无法优化
		DecorGroupNode decor = new DecorGroupNode.Builder(styles, linear)
				.top(topDecor)
				.build();

		List<RendererNode> outerLinearNodes = asList(decor);
		LinearGroupNode outerLinear = new LinearGroupNode(styles, outerLinearNodes);

		RendererNode optimized = outerLinear.optimize();

		// outerLinear 应该优化为 decor，但 decor 因为有装饰节点无法继续优化
		Assert.assertSame("应该优化到 DecorGroupNode 并停止", decor, optimized);
	}

	@Test
	public void testComplexNested_deepNesting_withStylesBarrier() {
		// 测试深度嵌套中间有 styles 屏障的情况
		MathPaint.Styles styles1 = new MathPaint.Styles(defaultPaint);
		MathPaint.Styles styles2 = new MathPaint.Styles(defaultPaint);
		styles2.setTextSize(15);
		MathPaint.Styles styles3 = new MathPaint.Styles(defaultPaint);
		styles3.setTextSize(20);

		// 最内层：styles1
		TextNode innerText = new TextNode(styles1, "x");
		List<RendererNode> layer1 = asList(innerText);
		LinearGroupNode linear1 = new LinearGroupNode(styles1, layer1);

		// 中间层：styles2（不同）
		DecorGroupNode decor1 = new DecorGroupNode.Builder(styles2, linear1).build();
		List<RendererNode> layer2 = asList(decor1);
		LinearGroupNode linear2 = new LinearGroupNode(styles2, layer2);

		// 外层：styles3（又不同）
		BraceGroupNode brace = new BraceGroupNode(styles3, 0, null, linear2, null);
		List<RendererNode> layer3 = asList(brace);
		LinearGroupNode linear3 = new LinearGroupNode(styles3, layer3);

		RendererNode optimized = linear3.optimize();

		// 应该只优化到 brace，因为 styles 不一致
		Assert.assertSame("应该优化到 BraceLayout", brace, optimized);
	}

	// ==================== 边界情况测试 ====================

	@Test
	public void testNonOptimizableNode_shouldReturnSelf() {
		// 测试非 OptimizableRendererNode 的节点（如 TextNode）
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode textNode = new TextNode(styles, "x");

		// TextNode 不实现 OptimizableRendererNode，没有 optimize 方法
		// 这里只是确保它可以正常使用
		Assert.assertNotNull(textNode);
	}

	@Test
	public void testSpaceNode_notOptimizable() {
		// 测试 SpaceNode 不实现 OptimizableRendererNode
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		SpaceNode spaceNode = new SpaceNode(styles, 10, 20);

		Assert.assertNotNull(spaceNode);
	}

	@Test
	public void testLinearGroupNode_withSpaceNodes_shouldNotOptimize() {
		// 测试 LinearGroupNode 包含多个 SpaceNode
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		SpaceNode space1 = new SpaceNode(styles, 10, 20);
		SpaceNode space2 = new SpaceNode(styles, 10, 20);
		List<RendererNode> nodes = Arrays.asList(space1, space2);

		LinearGroupNode groupNode = new LinearGroupNode(styles, nodes);
		RendererNode optimized = groupNode.optimize();

		Assert.assertSame("多个子节点，不应该优化", groupNode, optimized);
	}

	@Test
	public void testLinearGroupNode_withSingleSpaceNode_sameStyles() {
		// 测试 LinearGroupNode 包含单个 SpaceNode，styles 相同
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		SpaceNode space = new SpaceNode(styles, 10, 20);
		List<RendererNode> nodes = asList(space);

		LinearGroupNode groupNode = new LinearGroupNode(styles, nodes);
		RendererNode optimized = groupNode.optimize();

		Assert.assertSame("单个 SpaceNode 且 styles 相同，应该优化为 SpaceNode", space, optimized);
	}

	@Test
	public void testDecorGroupNode_withNullStyles_center() {
		// 测试 DecorGroupNode 中心节点 styles 为 null 的情况
		TextNode center = new TextNode(null, "x");

		DecorGroupNode decorNode = new DecorGroupNode.Builder(null, center).build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("styles 都为 null 视为相同，应该返回 center", center, optimized);
	}

	@Test
	public void testBraceLayout_withDifferentLevel() {
		// 测试 BraceLayout 不同的 level 参数
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode content = new TextNode(styles, "x");

		BraceGroupNode level0 = new BraceGroupNode(styles, 0, null, content, null);
		BraceGroupNode level1 = new BraceGroupNode(styles, 1, null, content, null);
		BraceGroupNode level2 = new BraceGroupNode(styles, 2, null, content, null);

		Assert.assertSame(content, level0.optimize());
		Assert.assertSame(content, level1.optimize());
		Assert.assertSame(content, level2.optimize());
	}

	@Test
	public void testLinearGroupNode_modificationSideEffect() {
		// 测试优化是否有副作用：修改了列表中的元素
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode innerChild = new TextNode(styles, "x");
		List<RendererNode> innerNodes = new ArrayList<>();
		innerNodes.add(innerChild);
		LinearGroupNode innerGroup = new LinearGroupNode(styles, innerNodes);

		List<RendererNode> outerNodes = new ArrayList<>();
		outerNodes.add(innerGroup);
		LinearGroupNode outerGroup = new LinearGroupNode(styles, outerNodes);

		RendererNode optimized = outerGroup.optimize();

		// 检查优化后，outerNodes 中的第一个元素是否被修改
		Assert.assertSame("应该递归优化到 TextNode", innerChild, optimized);
		// outerNodes 中的元素应该被修改为优化后的结果
		Assert.assertSame("列表中的元素应该被更新", innerChild, outerGroup.getChildAt(0));
	}

	@Test
	public void testDecorGroupNode_centerOptimizationSideEffect() {
		// 测试 DecorGroupNode center 优化的副作用
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode innerChild = new TextNode(styles, "x");
		List<RendererNode> innerNodes = asList(innerChild);
		LinearGroupNode innerGroup = new LinearGroupNode(styles, innerNodes);

		DecorGroupNode decorNode = new DecorGroupNode.Builder(styles, innerGroup).build();
		RendererNode optimized = decorNode.optimize();

		Assert.assertSame("应该递归优化到 TextNode", innerChild, optimized);
	}

	@Test
	public void testBraceLayout_contentOptimizationSideEffect() {
		// 测试 BraceLayout content 优化的副作用
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode innerChild = new TextNode(styles, "x");
		List<RendererNode> innerNodes = asList(innerChild);
		LinearGroupNode innerGroup = new LinearGroupNode(styles, innerNodes);

		BraceGroupNode braceLayout = new BraceGroupNode(styles, 0, null, innerGroup, null);
		RendererNode optimized = braceLayout.optimize();

		Assert.assertSame("应该递归优化到 TextNode", innerChild, optimized);
	}

	// ==================== Styles 引用相等性的关键测试 ====================

	@Test
	public void testStylesEquality_sameReference_shouldOptimize() {
		// 使用同一个 styles 引用，应该能优化
		MathPaint.Styles sharedStyles = new MathPaint.Styles(defaultPaint);
		TextNode child = new TextNode(sharedStyles, "x");
		List<RendererNode> nodes = asList(child);
		LinearGroupNode groupNode = new LinearGroupNode(sharedStyles, nodes);

		RendererNode optimized = groupNode.optimize();

		Assert.assertSame("同一个 styles 引用，应该优化", child, optimized);
	}

	@Test
	public void testStylesEquality_differentReference_shouldNotOptimize() {
		// 使用不同的 styles 引用，即使内容相同也不应该优化
		MathPaint.Styles parentStyles = new MathPaint.Styles(defaultPaint);
		MathPaint.Styles childStyles = new MathPaint.Styles(defaultPaint);
		TextNode child = new TextNode(childStyles, "x");
		List<RendererNode> nodes = asList(child);
		LinearGroupNode groupNode = new LinearGroupNode(parentStyles, nodes);

		RendererNode optimized = groupNode.optimize();

		Assert.assertSame("不同的 styles 引用，不应该优化", groupNode, optimized);
	}

	@Test
	public void testStylesEquality_bothNull_shouldOptimize() {
		// 两个 styles 都是 null，应该能优化
		TextNode child = new TextNode(null, "x");
		List<RendererNode> nodes = asList(child);
		LinearGroupNode groupNode = new LinearGroupNode(null, nodes);

		RendererNode optimized = groupNode.optimize();

		Assert.assertSame("两个 styles 都为 null，应该优化", child, optimized);
	}

	@Test
	public void testStylesEquality_oneNull_shouldNotOptimize() {
		// 一个 styles 是 null，另一个不是，不应该优化
		MathPaint.Styles styles = new MathPaint.Styles(defaultPaint);
		TextNode child = new TextNode(null, "x");
		List<RendererNode> nodes = asList(child);
		LinearGroupNode groupNode = new LinearGroupNode(styles, nodes);

		RendererNode optimized = groupNode.optimize();

		Assert.assertSame("一个 styles 为 null，另一个不是，不应该优化", groupNode, optimized);
	}
}