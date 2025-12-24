package me.chan.texas.ext.markdown.math.renderer

import me.chan.texas.ext.markdown.math.UnitTestDslMarker
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol
import org.junit.Assert

open class RenderNodeAsserter(private val node: RendererNode) {

    fun location(left: Int, top: Int, right: Int, bottom: Int) {
        Assert.assertEquals(left, node.left)
        Assert.assertEquals(top, node.top)
        Assert.assertEquals(right, node.right)
        Assert.assertEquals(bottom, node.bottom)
    }
}

@UnitTestDslMarker
class AccentAsserter(private val node: AccentNode) : RenderNodeAsserter(node) {

    fun cmd(block: DispatchAsserter.() -> Unit): AccentAsserter {
        val asserter = DispatchAsserter(node.cmdNode)
        block(asserter)
        return this
    }

    fun content(block: DispatchAsserter.() -> Unit): AccentAsserter {
        val asserter = DispatchAsserter(node.content)
        block(asserter)
        return this
    }
}

@UnitTestDslMarker
class BraceAsserter(private val node: BraceGroupNode) : RenderNodeAsserter(node) {
    fun left(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.leftSymbol).block()
    }

    fun content(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.content).block()
    }

    fun right(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.rightSymbol).block()
    }
}

@UnitTestDslMarker
class DecorGroupAsserter(node: DecorGroupNode) : RenderNodeAsserter(node) {

}

@UnitTestDslMarker
class FractionAsserter(private val node: FractionNode) : RenderNodeAsserter(node) {
    fun numerator(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.numerator).block()
    }

    fun denominator(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.denominator).block()
    }
}

@UnitTestDslMarker
class GridGroupAsserter(node: GridGroupNode) : RenderNodeAsserter(node) {
    fun eof() {
        TODO("Not yet implemented")
    }
}

@UnitTestDslMarker
class DispatchAsserter(private val node: RendererNode) {

    fun text(block: TextAsserter.() -> Unit) {
        val asserter = TextAsserter(node as TextNode)
        block(asserter)
    }

    fun linearGroup(block: LinearGroupAsserter.() -> Unit) {
        val asserter = LinearGroupAsserter(node as LinearGroupNode)
        block(asserter)
        asserter.eof()
    }

    fun accent(block: AccentAsserter.() -> Unit) {
        val asserter = AccentAsserter(node as AccentNode)
        block(asserter)
    }

    fun brace(block: BraceAsserter.() -> Unit) {
        val asserter = BraceAsserter(node as BraceGroupNode)
        block(asserter)
    }

    fun decorGroup(block: DecorGroupAsserter.() -> Unit) {
        val asserter = DecorGroupAsserter(node as DecorGroupNode)
        block(asserter)
    }

    fun fraction(block: FractionAsserter.() -> Unit) {
        val asserter = FractionAsserter(node as FractionNode)
        block(asserter)
    }

    fun gridGroup(block: GridGroupAsserter.() -> Unit) {
        val asserter = GridGroupAsserter(node as GridGroupNode)
        block(asserter)
        asserter.eof()
    }

    fun phantom(block: PhantomAsserter.() -> Unit) {
        val asserter = PhantomAsserter(node as PhantomNode)
        block(asserter)
    }

    fun space(block: SpaceAsserter.() -> Unit) {
        val asserter = SpaceAsserter(node as SpaceNode)
        block(asserter)
    }

    fun sqrt(block: SqrtAsserter.() -> Unit) {
        val asserter = SqrtAsserter(node as SqrtNode)
        block(asserter)
    }

    fun symbol(block: SymbolAsserter.() -> Unit) {
        val asserter = SymbolAsserter(node as SymbolNode)
        block(asserter)
    }

    fun stretchy(block: StretchyAsserter.() -> Unit) {
        val asserter = StretchyAsserter(node as StretchyNode)
        block(asserter)
    }
}

@UnitTestDslMarker
class StretchyAsserter(private val node: StretchyNode) : RenderNodeAsserter(node) {

    fun symbol(s: Symbol?) {
        Assert.assertNotNull(s)
        Assert.assertEquals(s, node.symbol.symbol)
    }

    fun symbol(s: String) {
        Assert.assertEquals(s, node.symbol.symbol.unicode)
    }
}

@UnitTestDslMarker
class LinearGroupAsserter(private val node: LinearGroupNode) : RenderNodeAsserter(node) {
    private var _index = 0

    fun child(block: DispatchAsserter.() -> Unit) {
        val asserter = DispatchAsserter(node.getChildAt(_index++))
        block(asserter)
    }

    fun eof() {
        Assert.assertEquals(_index, node.getChildCount())
    }
}

@UnitTestDslMarker
class PhantomAsserter(private val node: PhantomNode) : RenderNodeAsserter(node) {
    fun content(block: DispatchAsserter.() -> Unit) {
        val asserter = DispatchAsserter(node.content)
        block(asserter)
    }
}

@UnitTestDslMarker
class SpaceAsserter(private val node: SpaceNode) : RenderNodeAsserter(node) {

}

@UnitTestDslMarker
class SqrtAsserter(private val node: SqrtNode) : RenderNodeAsserter(node) {
    fun noRoot() {
        Assert.assertNull(node.root)
    }

    fun root(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.root!!).block()
    }

    fun content(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.content).block()
    }
}

@UnitTestDslMarker
class SymbolAsserter(private val node: SymbolNode) : RenderNodeAsserter(node) {
    fun content(s: Symbol?) {
        Assert.assertNotNull(s)
        Assert.assertEquals(s, node.symbol)
    }

    fun content(s: String) {
        Assert.assertEquals(s, node.symbol.unicode)
    }
}

@UnitTestDslMarker
class TextAsserter(private val node: TextNode) : RenderNodeAsserter(node) {
    fun content(s: String) {
        Assert.assertEquals(s, node.content)
    }
}