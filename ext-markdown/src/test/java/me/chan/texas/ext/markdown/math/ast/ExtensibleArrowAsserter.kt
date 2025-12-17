package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

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
        MathListAsserter(atom.below!!).block()
        return this
    }

    fun noAbove(): ExtensibleArrowAsserter {
        Assert.assertNull("没有上方内容", atom.above)
        return this
    }

    fun above(block: MathListAsserter.() -> Unit): ExtensibleArrowAsserter {
        MathListAsserter(atom.above).block()
        return this
    }
}
