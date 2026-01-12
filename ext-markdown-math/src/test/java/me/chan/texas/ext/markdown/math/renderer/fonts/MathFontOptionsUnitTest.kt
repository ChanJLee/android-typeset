package me.chan.texas.ext.markdown.math.renderer.fonts

import me.chan.texas.ext.markdown.math.ast.*
import org.junit.Assert
import org.junit.Test

/**
 * 测试 MathFontOptions 中的所有符号映射
 * 这个测试很重要，因为缺少映射会导致运行时崩溃
 */
class MathFontOptionsUnitTest {

    // ========== 大型运算符测试 (Large Operators) ==========

    @Test
    fun `test large operator sum`() {
        val atom = LargeOperatorAtom("sum")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("sum 应该有对应的符号", symbol)
        Assert.assertEquals("∑", symbol?.c)
    }

    @Test
    fun `test large operator prod`() {
        val atom = LargeOperatorAtom("prod")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("prod 应该有对应的符号", symbol)
        Assert.assertEquals("∏", symbol?.c)
    }

    @Test
    fun `test large operator coprod`() {
        val atom = LargeOperatorAtom("coprod")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("coprod 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator int`() {
        val atom = LargeOperatorAtom("int")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("int 应该有对应的符号", symbol)
        Assert.assertEquals("∫", symbol?.c)
    }

    @Test
    fun `test large operator iint`() {
        val atom = LargeOperatorAtom("iint")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("iint 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator iiint`() {
        val atom = LargeOperatorAtom("iiint")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("iiint 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator oint`() {
        val atom = LargeOperatorAtom("oint")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("oint 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator oiint`() {
        val atom = LargeOperatorAtom("oiint")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("oiint 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator oiiint`() {
        val atom = LargeOperatorAtom("oiiint")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("oiiint 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator bigcup`() {
        val atom = LargeOperatorAtom("bigcup")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("bigcup 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator bigcap`() {
        val atom = LargeOperatorAtom("bigcap")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("bigcap 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator bigvee`() {
        val atom = LargeOperatorAtom("bigvee")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("bigvee 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator bigwedge`() {
        val atom = LargeOperatorAtom("bigwedge")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("bigwedge 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator bigoplus`() {
        val atom = LargeOperatorAtom("bigoplus")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("bigoplus 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator bigotimes`() {
        val atom = LargeOperatorAtom("bigotimes")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("bigotimes 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator bigodot`() {
        val atom = LargeOperatorAtom("bigodot")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("bigodot 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator biguplus`() {
        val atom = LargeOperatorAtom("biguplus")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("biguplus 应该有对应的符号", symbol)
    }

    @Test
    fun `test large operator bigsqcup`() {
        val atom = LargeOperatorAtom("bigsqcup")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("bigsqcup 应该有对应的符号", symbol)
    }

    @Test
    fun `test text operator lim`() {
        val text = MathFontOptions.textOp("lim")
        Assert.assertNotNull("lim 应该有对应的文本", text)
        Assert.assertEquals("lim", text)
    }

    @Test
    fun `test text operator limsup`() {
        val text = MathFontOptions.textOp("limsup")
        Assert.assertNotNull("limsup 应该有对应的文本", text)
        Assert.assertEquals("lim sup", text)
    }

    @Test
    fun `test text operator liminf`() {
        val text = MathFontOptions.textOp("liminf")
        Assert.assertNotNull("liminf 应该有对应的文本", text)
        Assert.assertEquals("lim inf", text)
    }

    // ========== 希腊字母测试 (Greek Letters) ==========

    @Test
    fun `test greek letter alpha`() {
        val atom = GreekLetterVariableAtom("alpha")
        val text = MathFontOptions.ast(atom)
        Assert.assertNotNull("alpha 应该有对应的字符", text)
        Assert.assertEquals("\u03B1", text)
    }

    @Test
    fun `test greek letter beta`() {
        val atom = GreekLetterVariableAtom("beta")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03B2", text)
    }

    @Test
    fun `test greek letter gamma`() {
        val atom = GreekLetterVariableAtom("gamma")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03B3", text)
    }

    @Test
    fun `test greek letter delta`() {
        val atom = GreekLetterVariableAtom("delta")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03B4", text)
    }

    @Test
    fun `test greek letter epsilon`() {
        val atom = GreekLetterVariableAtom("epsilon")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("ϵ", text)
    }

    @Test
    fun `test greek letter varepsilon`() {
        val atom = GreekLetterVariableAtom("varepsilon")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("ε", text)
    }

    @Test
    fun `test greek letter zeta`() {
        val atom = GreekLetterVariableAtom("zeta")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03B6", text)
    }

    @Test
    fun `test greek letter eta`() {
        val atom = GreekLetterVariableAtom("eta")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03B7", text)
    }

    @Test
    fun `test greek letter theta`() {
        val atom = GreekLetterVariableAtom("theta")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03B8", text)
    }

    @Test
    fun `test greek letter vartheta`() {
        val atom = GreekLetterVariableAtom("vartheta")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03D1", text)
    }

    @Test
    fun `test greek letter iota`() {
        val atom = GreekLetterVariableAtom("iota")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03B9", text)
    }

    @Test
    fun `test greek letter kappa`() {
        val atom = GreekLetterVariableAtom("kappa")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03BA", text)
    }

    @Test
    fun `test greek letter lambda`() {
        val atom = GreekLetterVariableAtom("lambda")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03BB", text)
    }

    @Test
    fun `test greek letter mu`() {
        val atom = GreekLetterVariableAtom("mu")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03BC", text)
    }

    @Test
    fun `test greek letter nu`() {
        val atom = GreekLetterVariableAtom("nu")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03BD", text)
    }

    @Test
    fun `test greek letter xi`() {
        val atom = GreekLetterVariableAtom("xi")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03BE", text)
    }

    @Test
    fun `test greek letter pi`() {
        val atom = GreekLetterVariableAtom("pi")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03C0", text)
    }

    @Test
    fun `test greek letter varpi`() {
        val atom = GreekLetterVariableAtom("varpi")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03D6", text)
    }

    @Test
    fun `test greek letter rho`() {
        val atom = GreekLetterVariableAtom("rho")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03C1", text)
    }

    @Test
    fun `test greek letter varrho`() {
        val atom = GreekLetterVariableAtom("varrho")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03F1", text)
    }

    @Test
    fun `test greek letter sigma`() {
        val atom = GreekLetterVariableAtom("sigma")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03C3", text)
    }

    @Test
    fun `test greek letter varsigma`() {
        val atom = GreekLetterVariableAtom("varsigma")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03C2", text)
    }

    @Test
    fun `test greek letter tau`() {
        val atom = GreekLetterVariableAtom("tau")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03C4", text)
    }

    @Test
    fun `test greek letter upsilon`() {
        val atom = GreekLetterVariableAtom("upsilon")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03C5", text)
    }

    @Test
    fun `test greek letter phi`() {
        val atom = GreekLetterVariableAtom("phi")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("ϕ", text)
    }

    @Test
    fun `test greek letter varphi`() {
        val atom = GreekLetterVariableAtom("varphi")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("φ", text)
    }

    @Test
    fun `test greek letter chi`() {
        val atom = GreekLetterVariableAtom("chi")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03C7", text)
    }

    @Test
    fun `test greek letter psi`() {
        val atom = GreekLetterVariableAtom("psi")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03C8", text)
    }

    @Test
    fun `test greek letter omega`() {
        val atom = GreekLetterVariableAtom("omega")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03C9", text)
    }

    @Test
    fun `test greek letter Gamma`() {
        val atom = GreekLetterVariableAtom("Gamma")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u0393", text)
    }

    @Test
    fun `test greek letter Delta`() {
        val atom = GreekLetterVariableAtom("Delta")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u0394", text)
    }

    @Test
    fun `test greek letter Theta`() {
        val atom = GreekLetterVariableAtom("Theta")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u0398", text)
    }

    @Test
    fun `test greek letter Lambda`() {
        val atom = GreekLetterVariableAtom("Lambda")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u039B", text)
    }

    @Test
    fun `test greek letter Xi`() {
        val atom = GreekLetterVariableAtom("Xi")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u039E", text)
    }

    @Test
    fun `test greek letter Pi`() {
        val atom = GreekLetterVariableAtom("Pi")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03A0", text)
    }

    @Test
    fun `test greek letter Sigma`() {
        val atom = GreekLetterVariableAtom("Sigma")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03A3", text)
    }

    @Test
    fun `test greek letter Upsilon`() {
        val atom = GreekLetterVariableAtom("Upsilon")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03A5", text)
    }

    @Test
    fun `test greek letter Phi`() {
        val atom = GreekLetterVariableAtom("Phi")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03A6", text)
    }

    @Test
    fun `test greek letter Psi`() {
        val atom = GreekLetterVariableAtom("Psi")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03A8", text)
    }

    @Test
    fun `test greek letter Omega`() {
        val atom = GreekLetterVariableAtom("Omega")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u03A9", text)
    }

    @Test
    fun `test greek letter infty`() {
        val atom = GreekLetterVariableAtom("infty")
        val text = MathFontOptions.ast(atom)
        Assert.assertEquals("\u221E", text)
    }

    // ========== 基本符号测试 (Basic Symbols) ==========

    @Test
    fun `test symbol plus`() {
        val atom = SymbolAtom("+")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("+ 应该有对应的符号", symbol)
        Assert.assertEquals("+", symbol?.c)
    }

    @Test
    fun `test symbol minus`() {
        val atom = SymbolAtom("-")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("- 应该有对应的符号", symbol)
        Assert.assertEquals("-", symbol?.c)
    }

    @Test
    fun `test symbol multiply`() {
        val atom = SymbolAtom("*")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("* 应该有对应的符号", symbol)
        Assert.assertEquals("*", symbol?.c)
    }

    @Test
    fun `test symbol divide`() {
        val atom = SymbolAtom("/")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("/ 应该有对应的符号", symbol)
        Assert.assertEquals("/", symbol?.c)
    }

    @Test
    fun `test symbol times`() {
        val atom = SymbolAtom("\\times")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\times 应该有对应的符号", symbol)
        Assert.assertEquals("×", symbol?.c)
    }

    @Test
    fun `test symbol cdot`() {
        val atom = SymbolAtom("\\cdot")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\cdot 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol div`() {
        val atom = SymbolAtom("\\div")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\div 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol pm`() {
        val atom = SymbolAtom("\\pm")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\pm 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol mp`() {
        val atom = SymbolAtom("\\mp")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\mp 应该有对应的符号", symbol)
    }

    // ========== 关系运算符测试 (Relation Operators) ==========

    @Test
    fun `test symbol equal`() {
        val atom = SymbolAtom("=")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("= 应该有对应的符号", symbol)
        Assert.assertEquals("=", symbol?.c)
    }

    @Test
    fun `test symbol neq`() {
        val atom = SymbolAtom("\\neq")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\neq 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol equiv`() {
        val atom = SymbolAtom("\\equiv")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\equiv 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol approx`() {
        val atom = SymbolAtom("\\approx")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\approx 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol cong`() {
        val atom = SymbolAtom("\\cong")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\cong 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol sim`() {
        val atom = SymbolAtom("\\sim")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\sim 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol less`() {
        val atom = SymbolAtom("<")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("< 应该有对应的符号", symbol)
        Assert.assertEquals("<", symbol?.c)
    }

    @Test
    fun `test symbol greater`() {
        val atom = SymbolAtom(">")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("> 应该有对应的符号", symbol)
        Assert.assertEquals(">", symbol?.c)
    }

    @Test
    fun `test symbol le`() {
        val atom = SymbolAtom("\\le")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\le 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol ge`() {
        val atom = SymbolAtom("\\ge")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\ge 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol leq`() {
        val atom = SymbolAtom("\\leq")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\leq 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol geq`() {
        val atom = SymbolAtom("\\geq")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\geq 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol ll`() {
        val atom = SymbolAtom("\\ll")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\ll 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol gg`() {
        val atom = SymbolAtom("\\gg")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\gg 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol simeq`() {
        val atom = SymbolAtom("\\simeq")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\simeq 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol asymp`() {
        val atom = SymbolAtom("\\asymp")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\asymp 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol propto`() {
        val atom = SymbolAtom("\\propto")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\propto 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol prec`() {
        val atom = SymbolAtom("\\prec")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\prec 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol succ`() {
        val atom = SymbolAtom("\\succ")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\succ 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol preceq`() {
        val atom = SymbolAtom("\\preceq")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\preceq 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol succeq`() {
        val atom = SymbolAtom("\\succeq")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\succeq 应该有对应的符号", symbol)
    }

    // ========== 集合运算符测试 (Set Operators) ==========

    @Test
    fun `test symbol in`() {
        val atom = SymbolAtom("\\in")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\in 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol notin`() {
        val atom = SymbolAtom("\\notin")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\notin 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol subset`() {
        val atom = SymbolAtom("\\subset")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\subset 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol supset`() {
        val atom = SymbolAtom("\\supset")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\supset 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol subseteq`() {
        val atom = SymbolAtom("\\subseteq")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\subseteq 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol supseteq`() {
        val atom = SymbolAtom("\\supseteq")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\supseteq 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol cup`() {
        val atom = SymbolAtom("\\cup")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\cup 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol cap`() {
        val atom = SymbolAtom("\\cap")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\cap 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol wedge`() {
        val atom = SymbolAtom("\\wedge")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\wedge 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol vee`() {
        val atom = SymbolAtom("\\vee")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\vee 应该有对应的符号", symbol)
    }

    // ========== 箭头运算符测试 (Arrow Operators) ==========

    @Test
    fun `test symbol to`() {
        val atom = SymbolAtom("\\to")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\to 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol rightarrow`() {
        val atom = SymbolAtom("\\rightarrow")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\rightarrow 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol leftarrow`() {
        val atom = SymbolAtom("\\leftarrow")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\leftarrow 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol leftrightarrow`() {
        val atom = SymbolAtom("\\leftrightarrow")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\leftrightarrow 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol Rightarrow`() {
        val atom = SymbolAtom("\\Rightarrow")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\Rightarrow 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol Leftarrow`() {
        val atom = SymbolAtom("\\Leftarrow")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\Leftarrow 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol Leftrightarrow`() {
        val atom = SymbolAtom("\\Leftrightarrow")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\Leftrightarrow 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol implies`() {
        val atom = SymbolAtom("\\implies")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\implies 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol iff`() {
        val atom = SymbolAtom("\\iff")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("\\iff 应该有对应的符号", symbol)
    }

    // ========== 特殊符号测试 (Special Symbols) ==========

    @Test
    fun `test special symbol perp`() {
        val atom = SpecialSymbolAtom("perp")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("perp 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol parallel`() {
        val atom = SpecialSymbolAtom("parallel")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("parallel 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol mid`() {
        val atom = SpecialSymbolAtom("mid")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("mid 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol nmid`() {
        val atom = SpecialSymbolAtom("nmid")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("nmid 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol triangleleft`() {
        val atom = SpecialSymbolAtom("triangleleft")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("triangleleft 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol triangleright`() {
        val atom = SpecialSymbolAtom("triangleright")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("triangleright 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol bowtie`() {
        val atom = SpecialSymbolAtom("bowtie")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("bowtie 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol models`() {
        val atom = SpecialSymbolAtom("models")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("models 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol neg`() {
        val atom = SpecialSymbolAtom("neg")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("neg 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol dots`() {
        val atom = SpecialSymbolAtom("dots")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("dots 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol ldots`() {
        val atom = SpecialSymbolAtom("ldots")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("ldots 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol cdots`() {
        val atom = SpecialSymbolAtom("cdots")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("cdots 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol vdots`() {
        val atom = SpecialSymbolAtom("vdots")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("vdots 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol ddots`() {
        val atom = SpecialSymbolAtom("ddots")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("ddots 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol angle`() {
        val atom = SpecialSymbolAtom("angle")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("angle 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol therefore`() {
        val atom = SpecialSymbolAtom("therefore")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("therefore 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol because`() {
        val atom = SpecialSymbolAtom("because")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("because 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol forall`() {
        val atom = SpecialSymbolAtom("forall")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("forall 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol exists`() {
        val atom = SpecialSymbolAtom("exists")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("exists 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol nexists`() {
        val atom = SpecialSymbolAtom("nexists")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("nexists 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol emptyset`() {
        val atom = SpecialSymbolAtom("emptyset")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("emptyset 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol varnothing`() {
        val atom = SpecialSymbolAtom("varnothing")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("varnothing 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol hbar`() {
        val atom = SpecialSymbolAtom("hbar")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("hbar 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol nabla`() {
        val atom = SpecialSymbolAtom("nabla")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("nabla 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol partial`() {
        val atom = SpecialSymbolAtom("partial")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("partial 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol ell`() {
        val atom = SpecialSymbolAtom("ell")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("ell 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol wp`() {
        val atom = SpecialSymbolAtom("wp")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("wp 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol Re`() {
        val atom = SpecialSymbolAtom("Re")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("Re 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol Im`() {
        val atom = SpecialSymbolAtom("Im")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("Im 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol aleph`() {
        val atom = SpecialSymbolAtom("aleph")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("aleph 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol infty`() {
        val atom = SpecialSymbolAtom("infty")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("infty 应该有对应的符号", symbol)
    }

    // ========== 圈运算符测试 (Circle Operators) ==========

    @Test
    fun `test special symbol setminus`() {
        val atom = SpecialSymbolAtom("setminus")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("setminus 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol circ`() {
        val atom = SpecialSymbolAtom("circ")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("circ 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol oplus`() {
        val atom = SpecialSymbolAtom("oplus")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("oplus 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol ominus`() {
        val atom = SpecialSymbolAtom("ominus")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("ominus 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol otimes`() {
        val atom = SpecialSymbolAtom("otimes")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("otimes 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol oslash`() {
        val atom = SpecialSymbolAtom("oslash")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("oslash 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol odot`() {
        val atom = SpecialSymbolAtom("odot")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("odot 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol bullet`() {
        val atom = SpecialSymbolAtom("bullet")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("bullet 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol star`() {
        val atom = SpecialSymbolAtom("star")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("star 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol dagger`() {
        val atom = SpecialSymbolAtom("dagger")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("dagger 应该有对应的符号", symbol)
    }

    @Test
    fun `test special symbol ddagger`() {
        val atom = SpecialSymbolAtom("ddagger")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("ddagger 应该有对应的符号", symbol)
    }

    // ========== 可扩展箭头测试 (Extensible Arrows) ==========

    @Test
    fun `test extensible arrow xrightarrow`() {
        val atom = ExtensibleArrowAtom("xrightarrow", MathList(emptyList()), null)
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("xrightarrow 应该有对应的符号", symbol)
        Assert.assertEquals("→", symbol?.c)
    }

    @Test
    fun `test extensible arrow xleftarrow`() {
        val atom = ExtensibleArrowAtom("xleftarrow", MathList(emptyList()), null)
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("xleftarrow 应该有对应的符号", symbol)
        Assert.assertEquals("←", symbol?.c)
    }

    @Test
    fun `test extensible arrow xleftrightarrow`() {
        val atom = ExtensibleArrowAtom("xleftrightarrow", MathList(emptyList()), null)
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("xleftrightarrow 应该有对应的符号", symbol)
        Assert.assertEquals("↔", symbol?.c)
    }

    @Test
    fun `test extensible arrow xRightarrow`() {
        val atom = ExtensibleArrowAtom("xRightarrow", MathList(emptyList()), null)
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("xRightarrow 应该有对应的符号", symbol)
        Assert.assertEquals("⇒", symbol?.c)
    }

    @Test
    fun `test extensible arrow xLeftarrow`() {
        val atom = ExtensibleArrowAtom("xLeftarrow", MathList(emptyList()), null)
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("xLeftarrow 应该有对应的符号", symbol)
        Assert.assertEquals("⇐", symbol?.c)
    }

    @Test
    fun `test extensible arrow xLeftrightarrow`() {
        val atom = ExtensibleArrowAtom("xLeftrightarrow", MathList(emptyList()), null)
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("xLeftrightarrow 应该有对应的符号", symbol)
        Assert.assertEquals("⇔", symbol?.c)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test extensible arrow unknown defaults to right arrow`() {
        val atom = ExtensibleArrowAtom("xhookrightarrowxxxx", MathList(emptyList()), null)
        MathFontOptions.ast(atom)
    }

    // ========== 文本运算符测试 (Text Operators/Functions) ==========

    @Test
    fun `test text operator sin`() {
        val text = MathFontOptions.textOp("sin")
        Assert.assertEquals("sin", text)
    }

    @Test
    fun `test text operator cos`() {
        val text = MathFontOptions.textOp("cos")
        Assert.assertEquals("cos", text)
    }

    @Test
    fun `test text operator tan`() {
        val text = MathFontOptions.textOp("tan")
        Assert.assertEquals("tan", text)
    }

    @Test
    fun `test text operator cot`() {
        val text = MathFontOptions.textOp("cot")
        Assert.assertEquals("cot", text)
    }

    @Test
    fun `test text operator sec`() {
        val text = MathFontOptions.textOp("sec")
        Assert.assertEquals("sec", text)
    }

    @Test
    fun `test text operator csc`() {
        val text = MathFontOptions.textOp("csc")
        Assert.assertEquals("csc", text)
    }

    @Test
    fun `test text operator log`() {
        val text = MathFontOptions.textOp("log")
        Assert.assertEquals("log", text)
    }

    @Test
    fun `test text operator ln`() {
        val text = MathFontOptions.textOp("ln")
        Assert.assertEquals("ln", text)
    }

    @Test
    fun `test text operator lg`() {
        val text = MathFontOptions.textOp("lg")
        Assert.assertEquals("lg", text)
    }

    @Test
    fun `test text operator exp`() {
        val text = MathFontOptions.textOp("exp")
        Assert.assertEquals("exp", text)
    }

    @Test
    fun `test text operator max`() {
        val text = MathFontOptions.textOp("max")
        Assert.assertEquals("max", text)
    }

    @Test
    fun `test text operator min`() {
        val text = MathFontOptions.textOp("min")
        Assert.assertEquals("min", text)
    }

    @Test
    fun `test text operator sup`() {
        val text = MathFontOptions.textOp("sup")
        Assert.assertEquals("sup", text)
    }

    @Test
    fun `test text operator inf`() {
        val text = MathFontOptions.textOp("inf")
        Assert.assertEquals("inf", text)
    }

    @Test
    fun `test text operator det`() {
        val text = MathFontOptions.textOp("det")
        Assert.assertEquals("det", text)
    }

    @Test
    fun `test text operator dim`() {
        val text = MathFontOptions.textOp("dim")
        Assert.assertEquals("dim", text)
    }

    @Test
    fun `test text operator gcd`() {
        val text = MathFontOptions.textOp("gcd")
        Assert.assertEquals("gcd", text)
    }

    @Test
    fun `test text operator ker`() {
        val text = MathFontOptions.textOp("ker")
        Assert.assertEquals("ker", text)
    }

    // ========== 定界符符号测试 (Delimiter Symbols) ==========

    @Test
    fun `test symbol langle`() {
        val symbol = MathFontOptions.symbol("angleleft")
        Assert.assertNotNull("langle 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol rangle`() {
        val symbol = MathFontOptions.symbol("angleright")
        Assert.assertNotNull("rangle 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol divides`() {
        val symbol = MathFontOptions.symbol("divides")
        Assert.assertNotNull("divides (|) 应该有对应的符号", symbol)
    }

    @Test
    fun `test symbol parallel for pipes`() {
        val symbol = MathFontOptions.symbol("parallel")
        Assert.assertNotNull("parallel (||) 应该有对应的符号", symbol)
    }

    // ========== 特殊字母变量测试 (Special Letter Variables) ==========

    @Test
    fun `test special letter variable hbar`() {
        val atom = SpecialLetterVariableAtom("hbar")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("hbar 应该有对应的符号", symbol)
    }

    @Test
    fun `test special letter variable ell`() {
        val atom = SpecialLetterVariableAtom("ell")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("ell 应该有对应的符号", symbol)
    }

    @Test
    fun `test special letter variable wp`() {
        val atom = SpecialLetterVariableAtom("wp")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("wp 应该有对应的符号", symbol)
    }

    @Test
    fun `test special letter variable Re`() {
        val atom = SpecialLetterVariableAtom("Re")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("Re 应该有对应的符号", symbol)
    }

    @Test
    fun `test special letter variable Im`() {
        val atom = SpecialLetterVariableAtom("Im")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("Im 应该有对应的符号", symbol)
    }

    @Test
    fun `test special letter variable aleph`() {
        val atom = SpecialLetterVariableAtom("aleph")
        val symbol = MathFontOptions.ast(atom)
        Assert.assertNotNull("aleph 应该有对应的符号", symbol)
    }
}
