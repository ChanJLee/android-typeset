package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

internal class MatrixAsserter(val m: MatrixAtom) {
    fun environment(e: String?): MatrixAsserter {
        Assert.assertEquals(e, m.env)
        return this
    }

    fun rowCount(c: Int): MatrixAsserter {
        Assert.assertEquals(c.toLong(), m.rows.size.toLong())
        return this
    }
}
