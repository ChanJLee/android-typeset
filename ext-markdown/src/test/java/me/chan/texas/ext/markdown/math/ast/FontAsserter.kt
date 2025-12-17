package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class FontAsserter(val f: FontAtom) {
    fun command(c: String?): FontAsserter {
        Assert.assertEquals(c, f.command)
        return this
    }
}