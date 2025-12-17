package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

@UnitTestDslMarker
internal class AccentAsserter(val a: AccentAtom) {
    fun command(c: String): AccentAsserter {
        Assert.assertEquals(c, a.cmd)
        return this
    }

    fun singleToken(block: SingleTokenAsserter.() -> Unit): AccentAsserter {
        SingleTokenAsserter(a.content as SingleToken).block()
        return this
    }

    fun mathList(block: MathListAsserter.() -> Unit): AccentAsserter {
        MathListAsserter(a.content as MathList).block()
        return this
    }
}
