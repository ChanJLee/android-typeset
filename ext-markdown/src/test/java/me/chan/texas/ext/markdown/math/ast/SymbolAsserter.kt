package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

/**
     * SymbolAtom 验证器
     */
    internal class SymbolAsserter(private val binOp: SymbolAtom) {
        fun isOperator(expectedOp: String?): SymbolAsserter {
            Assert.assertEquals("二元运算符", expectedOp, binOp.symbol)
            return this
        }

        fun and(): MathListAsserter? {
            return null
        }
    }
