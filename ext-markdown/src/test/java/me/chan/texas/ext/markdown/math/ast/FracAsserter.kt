package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

@UnitTestDslMarker
internal class FracAsserter(private val frac: FracAtom) {
    fun command(c: String?): FracAsserter {
        Assert.assertEquals(c, frac.command)
        return this
    }

    fun numerator(block: MathListAsserter.() -> Unit): FracAsserter {
        MathListAsserter(frac.numerator).block()
        return this
    }

    fun denominator(block: MathListAsserter.() -> Unit): FracAsserter {
        MathListAsserter(frac.denominator).block()
        return this
    }
}
