package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.ext.markdown.math.UnitTestDslMarker

@UnitTestDslMarker
internal class GroupAsserter(private val group: Group) {

    fun content(block: MathListAsserter.() -> Unit): GroupAsserter {
        MathListAsserter(group.content).apply {
            block()
            eof()
        }
        return this
    }
}
