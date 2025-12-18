package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.ext.markdown.math.UnitTestDslMarker
import org.junit.Assert

@UnitTestDslMarker
internal class MathListAsserter(private val mathList: MathList) {
    private var currentIndex = 0

    fun hasSize(expectedSize: Int): MathListAsserter {
        Assert.assertEquals(
            "MathList 元素数量",
            expectedSize.toLong(),
            mathList.elements.size.toLong()
        )
        return this
    }

    fun term(block: TermAsserter.() -> Unit): MathListAsserter {
        nextTerm().block()
        return this
    }

    fun spacing(block: SpacingAsserter.() -> Unit): MathListAsserter {
        nextSpacing().block()
        return this
    }

    // --- 核心断言方法 ---
    fun elementIsTerm(index: Int): TermAsserter {
        Assert.assertTrue(
            "第" + index + "个元素应该是 Term",
            mathList.elements.get(index) is Term
        )
        return TermAsserter((mathList.elements.get(index) as Term?)!!)
    }

    fun elementIsSpacing(index: Int): SpacingAsserter {
        Assert.assertTrue(
            "第" + index + "个元素应该是 Spacing",
            mathList.elements.get(index) is Spacing
        )
        return SpacingAsserter((mathList.elements.get(index) as Spacing?)!!)
    }

    fun nextTerm(): TermAsserter {
        return elementIsTerm(currentIndex++)
    }

    fun nextSpacing(): SpacingAsserter {
        return elementIsSpacing(currentIndex++)
    }

    fun eof() {
        if (currentIndex != mathList.elements.size) {
            Assert.fail("没有找到 EOF")
        }
    }
}