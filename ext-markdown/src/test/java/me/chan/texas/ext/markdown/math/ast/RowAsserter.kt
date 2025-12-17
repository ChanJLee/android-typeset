package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

@UnitTestDslMarker
internal class RowAsserter(private val row: MatrixRow) {
    private var _index = 0

    fun next(block: MathListAsserter.() -> Unit): RowAsserter {
        MathListAsserter(row.elements[_index++]).block()
        return this
    }

    fun eof() {
        Assert.assertEquals(_index, row.elements.size)
    }
}
