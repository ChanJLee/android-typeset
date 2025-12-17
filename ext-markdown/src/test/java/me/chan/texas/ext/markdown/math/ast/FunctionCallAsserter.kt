package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert


internal class FunctionCallAsserter(val f: FunctionCallAtom) {
    fun name(n: String?): FunctionCallAsserter {
        Assert.assertEquals(n, f.name)
        return this
    }
}