package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.ext.markdown.math.UnitTestDslMarker
import org.junit.Assert

@UnitTestDslMarker
internal class SqrtAsserter(private val sqrt: SqrtAtom) {
    fun noRoot(): SqrtAsserter {
        Assert.assertNull(sqrt.root)
        return this
    }

    fun content(block: MathListAsserter.() -> Unit): SqrtAsserter {
        MathListAsserter(sqrt.content).apply {
            block()
            eof()
        }
        return this
    }

    fun root(block: MathListAsserter.() -> Unit): SqrtAsserter {
        MathListAsserter(sqrt.root).apply {
            block()
            eof()
        }
        return this
    }
}