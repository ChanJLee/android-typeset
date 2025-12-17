package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class BinomAsserter(private val binom: BinomAtom) {
    fun command(c: String): BinomAsserter {
        Assert.assertEquals(c, binom.command)
        return this
    }

    fun upper(block: MathListAsserter.() -> Unit): BinomAsserter {
        MathListAsserter(binom.upper).block()
        return this
    }

    fun lower(block: MathListAsserter.() -> Unit): BinomAsserter {
        MathListAsserter(binom.lower).block()
        return this
    }
}