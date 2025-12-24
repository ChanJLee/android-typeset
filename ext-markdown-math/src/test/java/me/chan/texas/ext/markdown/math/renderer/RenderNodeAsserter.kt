package me.chan.texas.ext.markdown.math.renderer

import me.chan.texas.ext.markdown.math.UnitTestDslMarker
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol
import me.chan.texas.misc.BitBucket32
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
class DecorGroupAsserter(private val node: DecorGroupNode) : RenderNodeAsserter(node) {
    private var _bit = BitBucket32()

    fun center(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.center).block()
        _bit[0] = true
    }

    fun left(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.leftNode).block()
        _bit[1] = true
    }

    fun leftTop(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.leftTop).block()
        _bit[2] = true
    }

    fun leftBottom(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.leftBottom).block()
        _bit[3] = true
    }

    fun right(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.rightNode).block()
        _bit[4] = true
    }

    fun rightTop(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.rightTop).block()
        _bit[5] = true
    }

    fun rightBottom(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.rightBottom).block()
        _bit[6] = true
    }

    fun top(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.getTopNode()).block()
        _bit[7] = true
    }

    fun bottom(block: DispatchAsserter.() -> Unit) {
        DispatchAsserter(node.getBottomNode()).block()
        _bit[8] = true
    }

    fun eof() {
        val cmp = BitBucket32()
        if (node.center != null) {
            cmp[0] = true
        }
        if (node.leftNode != null) {
            cmp[1] = true
        }
        if (node.leftTop != null) {
            cmp[2] = true
        }
        if (node.leftBottom != null) {
            cmp[3] = true
        }
        if (node.rightNode != null) {
            cmp[4] = true
        }
        if (node.rightTop != null) {
            cmp[5] = true
        }
        if (node.rightBottom != null) {
            cmp[6] = true
        }
        if (node.getTopNode() != null) {
            cmp[7] = true
        }
        if (node.bottomNode != null) {
            cmp[8] = true
        }
        Assert.assertEquals(cmp, _bit)
    }
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
class RowAsserter(private val node: LinearGroupNode) : RenderNodeAsserter(node) {
    fun content(block: LinearGroupAsserter.() -> Unit) {
        val asserter = LinearGroupAsserter(node)
        block(asserter)
        asserter.eof()
    }
}

@UnitTestDslMarker
class GridGroupAsserter(private val node: GridGroupNode) : RenderNodeAsserter(node) {
    private var _index = 0

    fun row(block: RowAsserter.() -> Unit) {
        val asserter = RowAsserter(node.getRow(_index++))
        block(asserter)
    }

    fun eof() {
        Assert.assertEquals(node.rowCount, _index)
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
        asserter.eof()
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