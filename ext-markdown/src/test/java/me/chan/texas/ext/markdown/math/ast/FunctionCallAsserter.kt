package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.ext.markdown.math.UnitTestDslMarker
import org.junit.Assert

@UnitTestDslMarker
internal class FunctionCallAsserter(val f: FunctionCallAtom) {
    fun name(n: String?): FunctionCallAsserter {
        Assert.assertEquals(n, f.name)
        return this
    }
}