package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class SpacingAsserter(val s: Spacing) {
    fun command(c: String?): SpacingAsserter {
        Assert.assertEquals(c, s.command)
        return this
    }
}
