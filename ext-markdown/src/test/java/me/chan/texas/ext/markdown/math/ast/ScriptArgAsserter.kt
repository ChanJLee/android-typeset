package me.chan.texas.ext.markdown.math.ast

@UnitTestDslMarker
internal class ScriptArgAsserter(private val scriptArg: ScriptArg) {
    fun singleToken(block: SingleTokenAsserter.() -> Unit): ScriptArgAsserter {
        SingleTokenAsserter(scriptArg.content as SingleToken).block()
        return this
    }

    fun group(block: GroupAsserter.() -> Unit): ScriptArgAsserter {
        GroupAsserter(scriptArg.content as Group).block()
        return this
    }
}
