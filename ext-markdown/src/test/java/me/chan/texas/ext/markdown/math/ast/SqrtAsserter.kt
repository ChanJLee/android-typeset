package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class SqrtAsserter(private val sqrt: SqrtAtom) {
    fun noRoot(): SqrtAsserter {
        Assert.assertNull(sqrt.root)
        return this
    }

    fun content(block: MathListAsserter.() -> Unit): SqrtAsserter {
        MathListAsserter(sqrt.content).block()
        return this
    }

    fun root(block: MathListAsserter.() -> Unit): SqrtAsserter {
        MathListAsserter(sqrt.root).block()
        return this
    }
}