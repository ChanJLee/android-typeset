package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class AccentAsserter(val a: AccentAtom) {
    fun command(c: String?): AccentAsserter {
        Assert.assertEquals(c, a.cmd)
        return this
    }
}
