package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.ext.markdown.math.UnitTestDslMarker

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
