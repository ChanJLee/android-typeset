package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

// ... 其他 Asserter 同样只需要把涉及 Expression 的地方改为 MathListAsserter ...
internal class FracAsserter(private val frac: FracAtom) {
    fun command(c: String?): FracAsserter {
        Assert.assertEquals(c, frac.command)
        return this
    }

    fun numerator(): MathListAsserter {
        return MathListAsserter(frac.numerator)
    }

    fun denominator(): MathListAsserter {
        return MathListAsserter(frac.denominator)
    }

    fun numeratorToString(s: String?): FracAsserter {
        Assert.assertEquals(s, frac.numerator.toString())
        return this
    }

    fun denominatorToString(s: String?): FracAsserter {
        Assert.assertEquals(s, frac.denominator.toString())
        return this
    }
}
