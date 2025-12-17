package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

@UnitTestDslMarker
internal class BinomAsserter(private val binom: BinomAtom) {
    fun command(c: String): BinomAsserter {
        Assert.assertEquals(c, binom.command)
        return this
    }

    fun upper(block: MathListAsserter.() -> Unit): BinomAsserter {
        MathListAsserter(binom.upper).apply {
            block()
            eof()
        }
        return this
    }

    fun lower(block: MathListAsserter.() -> Unit): BinomAsserter {
        MathListAsserter(binom.lower).apply {
            block()
            eof()
        }
        return this
    }
}