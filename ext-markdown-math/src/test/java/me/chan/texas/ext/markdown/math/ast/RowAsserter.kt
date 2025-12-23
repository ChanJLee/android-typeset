package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.ext.markdown.math.UnitTestDslMarker
import org.junit.Assert

@UnitTestDslMarker
internal class RowAsserter(private val row: MatrixRow) {
    private var _index = 0

    fun cell(block: MathListAsserter.() -> Unit): RowAsserter {
        MathListAsserter(row.elements[_index++]).apply {
            block()
            eof()
        }
        return this
    }

    fun eof() {
        Assert.assertEquals(_index, row.elements.size)
    }
}
