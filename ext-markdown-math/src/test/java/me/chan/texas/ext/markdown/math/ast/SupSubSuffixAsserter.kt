package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.ext.markdown.math.UnitTestDslMarker
import org.junit.Assert

@UnitTestDslMarker
internal class SupSubSuffixAsserter(val s: SupSubSuffix) {
    fun noSuperscript(): SupSubSuffixAsserter {
        Assert.assertNull(s.superscript)
        return this
    }

    fun noSubscript(): SupSubSuffixAsserter {
        Assert.assertNull(s.subscript)
        return this
    }

    fun superscript(block: ScriptArgAsserter.() -> Unit): SupSubSuffixAsserter {
        ScriptArgAsserter(s.superscript).block()
        return this
    }

    fun subscript(block: ScriptArgAsserter.() -> Unit): SupSubSuffixAsserter {
        ScriptArgAsserter(s.subscript).block()
        return this
    }
}