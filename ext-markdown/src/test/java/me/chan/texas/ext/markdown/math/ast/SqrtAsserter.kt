package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class SqrtAsserter(private val sqrt: SqrtAtom) {
    fun hasRoot(): SqrtAsserter {
        Assert.assertNotNull(sqrt.root)
        return this
    }

    fun noRoot(): SqrtAsserter {
        Assert.assertNull(sqrt.root)
        return this
    }

    fun content(): MathListAsserter {
        return MathListAsserter(sqrt.content)
    }

    fun root(): MathListAsserter {
        return MathListAsserter(sqrt.root)
    }
}