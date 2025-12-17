package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

@UnitTestDslMarker
internal class LargeOperatorAsserter(val l: LargeOperatorAtom) {
    fun name(n: String?): LargeOperatorAsserter {
        Assert.assertEquals(n, l.name)
        return this
    }
}