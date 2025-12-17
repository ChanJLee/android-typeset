package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class DelimitedAsserter(private val delimited: DelimitedAtom) {
    fun level(l: Int): DelimitedAsserter {
        Assert.assertEquals(l.toLong(), delimited.level.toLong())
        return this
    }

    fun left(s: String?): DelimitedAsserter {
        Assert.assertEquals(s, delimited.leftDelimiter)
        return this
    }

    fun right(s: String?): DelimitedAsserter {
        Assert.assertEquals(s, delimited.rightDelimiter)
        return this
    }

    fun content(block: MathListAsserter.() -> Unit): DelimitedAsserter {
        MathListAsserter(delimited.content).block()
        return this
    }
}