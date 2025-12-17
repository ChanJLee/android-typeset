package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

@UnitTestDslMarker
internal class MatrixAsserter(val m: MatrixAtom) {
    private var _index = 0

    fun environment(e: String?): MatrixAsserter {
        Assert.assertEquals(e, m.env)
        return this
    }

    fun rowCount(c: Int): MatrixAsserter {
        Assert.assertEquals(c.toLong(), m.rows.size.toLong())
        return this
    }

    fun row(block: RowAsserter.() -> Unit): MatrixAsserter {
        RowAsserter(m.rows[_index++]).block()
        return this
    }

    fun eof() {
        Assert.assertEquals(_index, m.rows.size)
    }
}
