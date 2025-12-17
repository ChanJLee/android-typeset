package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

// ... 其他 Asserter 类保持大部分不变，只需要修改返回类型或 content() 方法的逻辑 ...
// 省略了未变动的 SupSubSuffixAsserter, GroupAsserter 等内部细节方法的重复代码
// 只要确保 GroupAsserter.content() 返回 new MathListAsserter(...) 即可。
internal class GroupAsserter(private val group: Group) {

    fun content(block: MathListAsserter.() -> Unit): GroupAsserter {
        MathListAsserter(group.content).block()
        return this
    }
}
