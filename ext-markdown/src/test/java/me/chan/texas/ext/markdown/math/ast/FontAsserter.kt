package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.ext.markdown.math.UnitTestDslMarker
import org.junit.Assert

@UnitTestDslMarker
internal class FontAsserter(val f: FontAtom) {
    fun command(c: String?): FontAsserter {
        Assert.assertEquals(c, f.command)
        return this
    }

    fun content(block: MathListAsserter.() -> Unit): FontAsserter {
        MathListAsserter(f.content).apply {
            block()
            eof()
        }
        return this
    }
}