package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.ext.markdown.math.UnitTestDslMarker
import org.junit.Assert

@UnitTestDslMarker
internal class ExtensibleArrowAsserter {
    private val atom: ExtensibleArrowAtom

    constructor(atom: ExtensibleArrowAtom) {
        this.atom = atom
    }

    fun noBelow(): ExtensibleArrowAsserter {
        Assert.assertNull("没有下方内容", atom.below)
        return this
    }

    fun below(block: MathListAsserter.() -> Unit): ExtensibleArrowAsserter {
        MathListAsserter(atom.below!!).apply {
            block()
            eof()
        }
        return this
    }

    fun noAbove(): ExtensibleArrowAsserter {
        Assert.assertNull("没有上方内容", atom.above)
        return this
    }

    fun above(block: MathListAsserter.() -> Unit): ExtensibleArrowAsserter {
        MathListAsserter(atom.above).apply {
            block()
            eof()
        }
        return this
    }
}
