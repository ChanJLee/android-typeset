package me.chan.texas.ext.markdown.math.ast

@UnitTestDslMarker
internal class GroupAsserter(private val group: Group) {

    fun content(block: MathListAsserter.() -> Unit): GroupAsserter {
        MathListAsserter(group.content).block()
        return this
    }
}
