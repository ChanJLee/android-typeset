package me.chan.texas.ext.markdown.math.renderer

import me.chan.texas.ext.markdown.math.UnitTestDslMarker
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol
import org.junit.Assert


@UnitTestDslMarker
class AccentAsserter(private val node: AccentNode) {

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
class BraceAsserter(node: BraceGroupNode) {

}

@UnitTestDslMarker
class DecorGroupAsserter(node: DecorGroupNode) {

}

@UnitTestDslMarker
class FractionAsserter(node: FractionNode) {

}

@UnitTestDslMarker
class GridGroupAsserter(node: GridGroupNode) {
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
}

@UnitTestDslMarker
class LinearGroupAsserter(private val linearGroup: LinearGroupNode) {
    private var _index = 0

    fun child(block: DispatchAsserter.() -> Unit) {
        val asserter = DispatchAsserter(linearGroup.getChildAt(_index++))
        block(asserter)
    }

    fun eof() {
        Assert.assertEquals(_index, linearGroup.getChildCount())
    }
}

@UnitTestDslMarker
class PhantomAsserter(node: PhantomNode) {

}

@UnitTestDslMarker
class SpaceAsserter(node: SpaceNode) {

}

@UnitTestDslMarker
class SqrtAsserter(node: SqrtNode) {

}

@UnitTestDslMarker
class SymbolAsserter(private val node: SymbolNode) {
    fun content(s: Symbol?) {
        Assert.assertNotNull(s)
        Assert.assertEquals(s, node.symbol)
    }
}

@UnitTestDslMarker
class TextAsserter(private val node: TextNode) {
    fun content(s: String) {
        Assert.assertEquals(s, node.content)
    }
}