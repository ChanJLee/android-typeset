package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

@UnitTestDslMarker
class SingleTokenAsserter(private val t: SingleToken) {

    fun number(n: String): SingleTokenAsserter {
        Assert.assertEquals(n, (t.content as NumberAtom).value)
        return this
    }

    fun letter(l: String): SingleTokenAsserter {
        Assert.assertEquals(l, (t.content as VariableAtom).name)
        return this
    }

    fun greekLetter(l: String): SingleTokenAsserter {
        Assert.assertEquals(l, (t.content as GreekLetterVariableAtom).name)
        return this
    }

    fun symbol(s: String): SingleTokenAsserter {
        Assert.assertEquals(s, (t.content as SymbolAtom).symbol)
        return this
    }
}
