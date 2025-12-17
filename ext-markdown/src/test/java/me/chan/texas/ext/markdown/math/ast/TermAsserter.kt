package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert

// ExpressionAsserter 已移除，因为 Expression 被移除了
/**
 * Term 验证器
 */
internal class TermAsserter(private val term: Term) {
    fun hasNoSuffix(): TermAsserter {
        Assert.assertNull("应该没有上下标", term.suffix)
        return this
    }

    fun hasSuffix(): TermAsserter {
        Assert.assertNotNull("应该有上下标", term.suffix)
        return this
    }

    fun suffix(): SupSubSuffixAsserter {
        Assert.assertNotNull("应该有上下标", term.suffix)
        return SupSubSuffixAsserter(term.suffix)
    }

    fun atomIsNumber(expectedValue: String?): TermAsserter {
        Assert.assertTrue("atom 应该是 NumberAtom", term.atom is NumberAtom)
        Assert.assertEquals("数字值", expectedValue, (term.atom as NumberAtom).value)
        return this
    }

    fun atomIsVariable(expectedName: String?): TermAsserter {
        Assert.assertTrue("atom 应该是 VariableAtom", term.atom is VariableAtom)
        Assert.assertEquals("变量名", expectedName, (term.atom as VariableAtom).name)
        return this
    }

    fun atomIsGreekLetter(expectedName: String?): TermAsserter {
        Assert.assertTrue("atom 应该是 GreekLetterAtom", term.atom is GreekLetterVariableAtom)
        val atom = term.atom as GreekLetterVariableAtom
        var name = atom.name
        if (atom.primeSuffix != null) {
            name += atom.primeSuffix
        }
        Assert.assertEquals("希腊字母", expectedName, name)
        return this
    }

    fun atomIsPunctuation(expectedSymbol: String?): TermAsserter {
        Assert.assertTrue("atom 应该是 PunctuationAtom", term.atom is PunctuationAtom)
        Assert.assertEquals("标点符号", expectedSymbol, (term.atom as PunctuationAtom).symbol)
        return this
    }

    fun atomIsSpecialSymbol(expectedName: String?): TermAsserter {
        Assert.assertTrue("atom 应该是 SpecialSymbolAtom", term.atom is SpecialSymbolAtom)
        Assert.assertEquals("特殊符号", expectedName, (term.atom as SpecialSymbolAtom).symbol)
        return this
    }

    fun atomIsGroup(): GroupAsserter {
        Assert.assertTrue("atom 应该是 Group", term.atom is Group)
        return GroupAsserter(term.atom as Group)
    }

    fun atomIsFrac(): FracAsserter {
        Assert.assertTrue("atom 应该是 FracAtom", term.atom is FracAtom)
        return FracAsserter(term.atom as FracAtom)
    }

    fun atomIsBinom(): BinomAsserter {
        Assert.assertTrue("atom 应该是 BinomAtom", term.atom is BinomAtom)
        return BinomAsserter(term.atom as BinomAtom)
    }

    fun atomIsSqrt(): SqrtAsserter {
        Assert.assertTrue("atom 应该是 SqrtAtom", term.atom is SqrtAtom)
        return SqrtAsserter(term.atom as SqrtAtom)
    }

    fun atomIsDelimited(): DelimitedAsserter {
        Assert.assertTrue("atom 应该是 DelimitedAtom", term.atom is DelimitedAtom)
        return DelimitedAsserter(term.atom as DelimitedAtom)
    }

    fun atomIsFunction(): FunctionCallAsserter {
        Assert.assertTrue("atom 应该是 FunctionCallAtom", term.atom is FunctionCallAtom)
        return FunctionCallAsserter(term.atom as FunctionCallAtom)
    }

    fun atomIsLargeOperator(): LargeOperatorAsserter {
        Assert.assertTrue("atom 应该是 LargeOperatorAtom", term.atom is LargeOperatorAtom)
        return LargeOperatorAsserter(term.atom as LargeOperatorAtom)
    }

    fun atomIsMatrix(): MatrixAsserter {
        Assert.assertTrue("atom 应该是 MatrixAtom", term.atom is MatrixAtom)
        return MatrixAsserter(term.atom as MatrixAtom)
    }

    fun atomIsText(): TextAsserter {
        Assert.assertTrue("atom 应该是 TextAtom", term.atom is TextAtom)
        return TextAsserter(term.atom as TextAtom)
    }

    fun atomIsAccent(): AccentAsserter {
        Assert.assertTrue("atom 应该是 AccentAtom", term.atom is AccentAtom)
        return AccentAsserter(term.atom as AccentAtom)
    }

    fun atomIsFont(): FontAsserter {
        Assert.assertTrue("atom 应该是 FontAtom", term.atom is FontAtom)
        return FontAsserter(term.atom as FontAtom)
    }

    fun and(): MathListAsserter? {
        // 返回上层 Asserter 以支持链式调用，这里需要一点 Hack 或者重新传入
        // 由于内部类结构简单，我们可以简单地假装测试流程继续
        return null // 注意：TermAsserter 结束通常意味着这一项验证完毕，回到 MathListAsserter 需要外部引用
    }

    fun atomIsSymbol(): SymbolAsserter {
        return SymbolAsserter(term.atom as SymbolAtom)
    }
}
