package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class TextAsserter(val t: TextAtom) {
    fun command(c: String?): TextAsserter {
        Assert.assertEquals(c, t.command)
        return this
    }

    fun content(s: String?): TextAsserter {
        Assert.assertEquals(s, t.content)
        return this
    }
}
