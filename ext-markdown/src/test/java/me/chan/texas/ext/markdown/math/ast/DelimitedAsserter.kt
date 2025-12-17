package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class DelimitedAsserter(private val delimited: DelimitedAtom) {
    fun level(l: Int): DelimitedAsserter {
        Assert.assertEquals(l.toLong(), delimited.level.toLong())
        return this
    }

    fun leftDelimiter(s: String?): DelimitedAsserter {
        Assert.assertEquals(s, delimited.leftDelimiter)
        return this
    }

    fun rightDelimiter(s: String?): DelimitedAsserter {
        Assert.assertEquals(s, delimited.rightDelimiter)
        return this
    }

    fun content(): MathListAsserter {
        return MathListAsserter(delimited.content)
    }
}