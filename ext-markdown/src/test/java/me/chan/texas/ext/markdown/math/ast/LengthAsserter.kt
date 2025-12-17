package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

@UnitTestDslMarker
internal class LengthAsserter(private val length: Length) {
    fun number(n: Int): LengthAsserter {
        Assert.assertEquals(n, length.size)
        return this
    }

    fun unit(u: String): LengthAsserter {
        Assert.assertEquals(u, length.unit)
        return this
    }
}
