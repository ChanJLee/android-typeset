package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class SpacingAsserter(val s: Spacing) {
    fun command(c: String?): SpacingAsserter {
        Assert.assertEquals(c, s.command)
        return this
    }

    fun length(block: LengthAsserter.() -> Unit): SpacingAsserter {
        LengthAsserter(s.content as Length).block()
        return this
    }

    fun content(block: MathListAsserter.() -> Unit): SpacingAsserter {
        MathListAsserter(s.content as MathList).block()
        return this
    }
}
