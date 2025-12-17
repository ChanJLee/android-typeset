package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

// ExpressionAsserter 已移除，因为 Expression 被移除了
/**
 * Term 验证器
 */
internal class TermAsserter(private val term: Term) {
    fun noSuffix(): TermAsserter {
        Assert.assertNull("应该没有上下标", term.suffix)
        return this
    }

    fun suffix(block: SupSubSuffixAsserter.() -> Unit): TermAsserter {
        SupSubSuffixAsserter(term.suffix).block()
        return this;
    }

    fun number(expectedValue: String): TermAsserter {
        Assert.assertTrue("atom 应该是 NumberAtom", term.atom is NumberAtom)
        Assert.assertEquals("数字值", expectedValue, (term.atom as NumberAtom).value)
        return this
    }

    fun variable(expectedName: String): TermAsserter {
        Assert.assertTrue("atom 应该是 VariableAtom", term.atom is VariableAtom)
        Assert.assertEquals("变量名", expectedName, (term.atom as VariableAtom).name)
        return this
    }

    fun greekLetter(expectedName: String): TermAsserter {
        Assert.assertTrue("atom 应该是 GreekLetterAtom", term.atom is GreekLetterVariableAtom)
        val atom = term.atom as GreekLetterVariableAtom
        var name = atom.name
        if (atom.primeSuffix != null) {
            name += atom.primeSuffix
        }
        Assert.assertEquals("希腊字母", expectedName, name)
        return this
    }

    fun group(block: GroupAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 Group", term.atom is Group)
        GroupAsserter(term.atom as Group).block()
        return this
    }

    fun frac(block: FracAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 FracAtom", term.atom is FracAtom)
        FracAsserter(term.atom as FracAtom).block()
        return this
    }

    fun binom(block: BinomAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 BinomAtom", term.atom is BinomAtom)
        BinomAsserter(term.atom as BinomAtom).block()
        return this
    }

    fun sqrt(block: SqrtAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 SqrtAtom", term.atom is SqrtAtom)
        SqrtAsserter(term.atom as SqrtAtom).block()
        return this
    }

    fun delimited(block: DelimitedAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 DelimitedAtom", term.atom is DelimitedAtom)
        DelimitedAsserter(term.atom as DelimitedAtom).block()
        return this
    }

    fun function(block: FunctionCallAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 FunctionCallAtom", term.atom is FunctionCallAtom)
        FunctionCallAsserter(term.atom as FunctionCallAtom).block()
        return this
    }

    fun largeOperator(block: LargeOperatorAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 LargeOperatorAtom", term.atom is LargeOperatorAtom)
        LargeOperatorAsserter(term.atom as LargeOperatorAtom).block()
        return this
    }

    fun matrix(block: MatrixAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 MatrixAtom", term.atom is MatrixAtom)
        MatrixAsserter(term.atom as MatrixAtom).block()
        return this
    }

    fun text(block: TextAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 TextAtom", term.atom is TextAtom)
        TextAsserter(term.atom as TextAtom).block()
        return this
    }

    fun accent(block: AccentAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 AccentAtom", term.atom is AccentAtom)
        AccentAsserter(term.atom as AccentAtom).block()
        return this
    }

    fun font(block: FontAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 FontAtom", term.atom is FontAtom)
        FontAsserter(term.atom as FontAtom).block()
        return this
    }

    fun extensibleArrow(block: ExtensibleArrowAsserter.() -> Unit): TermAsserter {
        Assert.assertTrue("atom 应该是 ExtensibleArrowAtom", term.atom is ExtensibleArrowAtom)
        ExtensibleArrowAsserter(term.atom as ExtensibleArrowAtom).block()
        return this
    }

    fun symbol(expectedName: String): TermAsserter {
        Assert.assertTrue("atom 应该是 SymbolAtom", term.atom is SymbolAtom)
        Assert.assertEquals("符号名", expectedName, (term.atom as SymbolAtom).symbol)
        return this;
    }
}
