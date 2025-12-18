package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

@UnitTestDslMarker
internal class LengthAsserter(private val length: Length) {
    fun size(n: String): LengthAsserter {
        Assert.assertEquals(n, length.size.value)
        return this
    }

    fun unit(u: String): LengthAsserter {
        Assert.assertEquals(u, length.unit.unit)
        return this
    }
}
