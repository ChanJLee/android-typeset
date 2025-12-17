package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

// ... 其他 Asserter 类 (FunctionCallAsserter, MatrixAsserter 等) 逻辑基本不变，
// 主要是内部持有 MathList 的地方，返回值改为 MathListAsserter
// 占位符类，避免编译错误
internal class SupSubSuffixAsserter(val s: SupSubSuffix) {
    fun noSuperscript(): SupSubSuffixAsserter {
        Assert.assertNull(s.superscript)
        return this
    }

    fun noSubscript(): SupSubSuffixAsserter {
        Assert.assertNull(s.subscript)
        return this
    }

    fun superscriptContent(block: ScriptArgAsserter.() -> Unit): SupSubSuffixAsserter {
        ScriptArgAsserter(s.superscript).block()
        return this
    }

    fun subscriptContent(block: ScriptArgAsserter.() -> Unit): SupSubSuffixAsserter {
        ScriptArgAsserter(s.subscript).block()
        return this
    }
}