package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class BinomAsserter(private val binom: BinomAtom) {
    fun command(c: String?): BinomAsserter {
        Assert.assertEquals(c, binom.command)
        return this
    }

    fun upper(): MathListAsserter {
        return MathListAsserter(binom.upper)
    }

    fun lower(): MathListAsserter {
        return MathListAsserter(binom.lower)
    }

    fun upperToString(s: String?): BinomAsserter {
        Assert.assertEquals(s, binom.upper.toString())
        return this
    }

    fun lowerToString(s: String?): BinomAsserter {
        Assert.assertEquals(s, binom.lower.toString())
        return this
    }
}