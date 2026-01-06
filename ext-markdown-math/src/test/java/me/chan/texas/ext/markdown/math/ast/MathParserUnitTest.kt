package me.chan.texas.ext.markdown.unitTest

import me.chan.texas.ext.markdown.math.ast.DelimitedAtom
import me.chan.texas.ext.markdown.math.ast.MathList
import me.chan.texas.ext.markdown.math.ast.MathListAsserter
import me.chan.texas.ext.markdown.math.ast.MathParseException
import me.chan.texas.ext.markdown.math.ast.MathParser
import org.junit.Test
import me.chan.texas.utils.CharStream
import org.junit.Assert
import org.junit.Assert.fail

/**
 * MathParser 完整覆盖测试
 * 基于 bnf_math_loose.txt 定义，覆盖所有语法规则
 */
class MathParserUnitTest {

    // ================================================================
    // 1. 基础原子 (Atoms) - BNF: Number, Variable, Symbol
    // ================================================================

    @Test
    fun `test number integers and decimals`() {
        "123".assert {
            term { number("123") }
        }

        "3.14".assert {
            term { number("3.14") }
        }

        "0.5".assert {
            term { number("0.5") }
        }

        "999.999".assert {
            term { number("999.999") }
        }
    }

    @Test
    fun `test single letter variables`() {
        "x".assert {
            term { variable("x") }
        }

        "a".assert {
            term { variable("a") }
        }

        "Z".assert {
            term { variable("Z") }
        }
    }

    @Test
    fun `test multi letter variables`() {
        "ab".assert {
            term { variable("ab") }
        }

        "xyz".assert {
            term { variable("xyz") }
        }
    }

    // ================================================================
    // 2. 希腊字母 (Greek Letters) - BNF: 65-72行
    // ================================================================

    @Test
    fun `test lowercase greek letters - alpha to mu`() {
        "\\alpha".assert { term { greekLetter("alpha") } }
        "\\beta".assert { term { greekLetter("beta") } }
        "\\gamma".assert { term { greekLetter("gamma") } }
        "\\delta".assert { term { greekLetter("delta") } }
        "\\epsilon".assert { term { greekLetter("epsilon") } }
        "\\varepsilon".assert { term { greekLetter("varepsilon") } }
        "\\zeta".assert { term { greekLetter("zeta") } }
        "\\eta".assert { term { greekLetter("eta") } }
        "\\theta".assert { term { greekLetter("theta") } }
        "\\vartheta".assert { term { greekLetter("vartheta") } }
        "\\iota".assert { term { greekLetter("iota") } }
        "\\kappa".assert { term { greekLetter("kappa") } }
        "\\lambda".assert { term { greekLetter("lambda") } }
        "\\mu".assert { term { greekLetter("mu") } }
    }

    @Test
    fun `test lowercase greek letters - nu to omega`() {
        "\\nu".assert { term { greekLetter("nu") } }
        "\\xi".assert { term { greekLetter("xi") } }
        "\\pi".assert { term { greekLetter("pi") } }
        "\\varpi".assert { term { greekLetter("varpi") } }
        "\\rho".assert { term { greekLetter("rho") } }
        "\\varrho".assert { term { greekLetter("varrho") } }
        "\\sigma".assert { term { greekLetter("sigma") } }
        "\\varsigma".assert { term { greekLetter("varsigma") } }
        "\\tau".assert { term { greekLetter("tau") } }
        "\\upsilon".assert { term { greekLetter("upsilon") } }
        "\\phi".assert { term { greekLetter("phi") } }
        "\\varphi".assert { term { greekLetter("varphi") } }
        "\\chi".assert { term { greekLetter("chi") } }
        "\\psi".assert { term { greekLetter("psi") } }
        "\\omega".assert { term { greekLetter("omega") } }
    }

    @Test
    fun `test uppercase greek letters`() {
        "\\Gamma".assert { term { greekLetter("Gamma") } }
        "\\Delta".assert { term { greekLetter("Delta") } }
        "\\Theta".assert { term { greekLetter("Theta") } }
        "\\Lambda".assert { term { greekLetter("Lambda") } }
        "\\Xi".assert { term { greekLetter("Xi") } }
        "\\Pi".assert { term { greekLetter("Pi") } }
        "\\Sigma".assert { term { greekLetter("Sigma") } }
        "\\Upsilon".assert { term { greekLetter("Upsilon") } }
        "\\Phi".assert { term { greekLetter("Phi") } }
        "\\Psi".assert { term { greekLetter("Psi") } }
        "\\Omega".assert { term { greekLetter("Omega") } }
    }

    @Test
    fun `test special greek letter infinity`() {
        "\\infty".assert {
            term { greekLetter("infty") }
        }
    }

    // ================================================================
    // 3. 符号 (Symbols) - BNF: 34-63行
    // ================================================================

    @Test
    fun `test basic single char symbols`() {
        "+".assert { term { symbol("+") } }
        "-".assert { term { symbol("-") } }
        "*".assert { term { symbol("*") } }
        "/".assert { term { symbol("/") } }
        "|".assert { term { symbol("|") } }
        "!".assert { term { symbol("!") } }
        "'".assert { term { symbol("'") } }
        ",".assert { term { symbol(",") } }
        ";".assert { term { symbol(";") } }
        "<".assert { term { symbol("<") } }
        ">".assert { term { symbol(">") } }
        "=".assert { term { symbol("=") } }
    }

    @Test
    fun `test arithmetic operators`() {
        "\\times".assert { term { symbol("\\times") } }
        "\\cdot".assert { term { symbol("\\cdot") } }
        "\\div".assert { term { symbol("\\div") } }
        "\\pm".assert { term { symbol("\\pm") } }
        "\\mp".assert { term { symbol("\\mp") } }
        "\\setminus".assert { term { symbol("\\setminus") } }
        "\\circ".assert { term { symbol("\\circ") } }
    }

    @Test
    fun `test binary operators - circled`() {
        "\\oplus".assert { term { symbol("\\oplus") } }
        "\\ominus".assert { term { symbol("\\ominus") } }
        "\\otimes".assert { term { symbol("\\otimes") } }
        "\\oslash".assert { term { symbol("\\oslash") } }
        "\\odot".assert { term { symbol("\\odot") } }
    }

    @Test
    fun `test binary operators - misc`() {
        "\\bullet".assert { term { symbol("\\bullet") } }
        "\\star".assert { term { symbol("\\star") } }
        "\\dagger".assert { term { symbol("\\dagger") } }
        "\\ddagger".assert { term { symbol("\\ddagger") } }
    }

    @Test
    fun `test relation operators - equality`() {
        "\\neq".assert { term { symbol("\\neq") } }
        "\\equiv".assert { term { symbol("\\equiv") } }
        "\\approx".assert { term { symbol("\\approx") } }
        "\\cong".assert { term { symbol("\\cong") } }
        "\\sim".assert { term { symbol("\\sim") } }
        "\\simeq".assert { term { symbol("\\simeq") } }
        "\\asymp".assert { term { symbol("\\asymp") } }
        "\\propto".assert { term { symbol("\\propto") } }
    }

    @Test
    fun `test relation operators - inequality`() {
        "\\le".assert { term { symbol("\\le") } }
        "\\ge".assert { term { symbol("\\ge") } }
        "\\leq".assert { term { symbol("\\leq") } }
        "\\geq".assert { term { symbol("\\geq") } }
        "\\ll".assert { term { symbol("\\ll") } }
        "\\gg".assert { term { symbol("\\gg") } }
        "\\prec".assert { term { symbol("\\prec") } }
        "\\succ".assert { term { symbol("\\succ") } }
        "\\preceq".assert { term { symbol("\\preceq") } }
        "\\succeq".assert { term { symbol("\\succeq") } }
    }

    @Test
    fun `test set operators`() {
        "\\in".assert { term { symbol("\\in") } }
        "\\notin".assert { term { symbol("\\notin") } }
        "\\subset".assert { term { symbol("\\subset") } }
        "\\supset".assert { term { symbol("\\supset") } }
        "\\subseteq".assert { term { symbol("\\subseteq") } }
        "\\supseteq".assert { term { symbol("\\supseteq") } }
        "\\cup".assert { term { symbol("\\cup") } }
        "\\cap".assert { term { symbol("\\cap") } }
        "\\wedge".assert { term { symbol("\\wedge") } }
        "\\vee".assert { term { symbol("\\vee") } }
    }

    @Test
    fun `test arrow operators`() {
        "\\to".assert { term { symbol("\\to") } }
        "\\rightarrow".assert { term { symbol("\\rightarrow") } }
        "\\leftarrow".assert { term { symbol("\\leftarrow") } }
        "\\leftrightarrow".assert { term { symbol("\\leftrightarrow") } }
        "\\Rightarrow".assert { term { symbol("\\Rightarrow") } }
        "\\Leftarrow".assert { term { symbol("\\Leftarrow") } }
        "\\Leftrightarrow".assert { term { symbol("\\Leftrightarrow") } }
        "\\implies".assert { term { symbol("\\implies") } }
        "\\iff".assert { term { symbol("\\iff") } }
    }

    @Test
    fun `test geometry operators`() {
        "\\perp".assert { term { symbol("\\perp") } }
        "\\parallel".assert { term { symbol("\\parallel") } }
        "\\mid".assert { term { symbol("\\mid") } }
        "\\nmid".assert { term { symbol("\\nmid") } }
        "\\triangleleft".assert { term { symbol("\\triangleleft") } }
        "\\triangleright".assert { term { symbol("\\triangleright") } }
        "\\bowtie".assert { term { symbol("\\bowtie") } }
        "\\models".assert { term { symbol("\\models") } }
    }

    @Test
    fun `test logic operators`() {
        "\\neg".assert { term { symbol("\\neg") } }
        "\\forall".assert { term { symbol("\\forall") } }
        "\\exists".assert { term { symbol("\\exists") } }
        "\\nexists".assert { term { symbol("\\nexists") } }
    }

    @Test
    fun `test dots operators`() {
        "\\dots".assert { term { symbol("\\dots") } }
        "\\ldots".assert { term { symbol("\\ldots") } }
        "\\cdots".assert { term { symbol("\\cdots") } }
        "\\vdots".assert { term { symbol("\\vdots") } }
        "\\ddots".assert { term { symbol("\\ddots") } }
    }

    @Test
    fun `test special symbols`() {
        "\\angle".assert { term { symbol("\\angle") } }
        "\\therefore".assert { term { symbol("\\therefore") } }
        "\\because".assert { term { symbol("\\because") } }
        "\\emptyset".assert { term { symbol("\\emptyset") } }
        "\\varnothing".assert { term { symbol("\\varnothing") } }
        "\\hbar".assert { term { symbol("\\hbar") } }
        "\\nabla".assert { term { symbol("\\nabla") } }
        "\\partial".assert { term { symbol("\\partial") } }
        "\\ell".assert { term { symbol("\\ell") } }
        "\\wp".assert { term { symbol("\\wp") } }
        "\\Re".assert { term { symbol("\\Re") } }
        "\\Im".assert { term { symbol("\\Im") } }
        "\\aleph".assert { term { symbol("\\aleph") } }
    }

    // ================================================================
    // 4. 分组 (Groups) - BNF: 74行
    // ================================================================

    @Test
    fun `test curly braces group`() {
        "{a + b}".assert {
            term {
                group {
                    content {
                        term { variable("a") }
                        term { symbol("+") }
                        term { variable("b") }
                    }
                }
            }
        }
    }

    @Test
    fun `test parentheses group`() {
        "(x)".assert {
            term {
                group {
                    content {
                        term { variable("x") }
                    }
                }
            }
        }
    }

    @Test
    fun `test square brackets group`() {
        "[n]".assert {
            term {
                group {
                    content {
                        term { variable("n") }
                    }
                }
            }
        }
    }

    @Test
    fun `test escaped braces group`() {
        "\\{a\\}".assert {
            term {
                group {
                    content {
                        term { variable("a") }
                    }
                }
            }
        }
    }

    @Test
    fun `test empty group`() {
        "{}".assert {
            term {
                group {
                    content { /* expect empty */ }
                }
            }
        }
    }

    // ================================================================
    // 5. 上下标 (Scripts) - BNF: 76-86行
    // ================================================================

    @Test
    fun `test superscript with single token`() {
        "x^2".assert {
            term {
                variable("x")
                suffix {
                    superscript {
                        singleToken { number("2") }
                    }
                }
            }
        }
    }

    @Test
    fun `test subscript with single token`() {
        "x_i".assert {
            term {
                variable("x")
                suffix {
                    subscript {
                        singleToken { letter("i") }
                    }
                }
            }
        }
    }

    @Test
    fun `test superscript then subscript`() {
        "x^2_i".assert {
            term {
                variable("x")
                suffix {
                    superscript { singleToken { number("2") } }
                    subscript { singleToken { letter("i") } }
                }
            }
        }
    }

    @Test
    fun `test subscript then superscript`() {
        "x_i^2".assert {
            term {
                variable("x")
                suffix {
                    subscript { singleToken { letter("i") } }
                    superscript { singleToken { number("2") } }
                }
            }
        }
    }

    @Test
    fun `test superscript with group`() {
        "x^{n+1}".assert {
            term {
                variable("x")
                suffix {
                    superscript {
                        group {
                            content {
                                term { variable("n") }
                                term { symbol("+") }
                                term { number("1") }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test nested superscript`() {
        "2^{2^n}".assert {
            term {
                number("2")
                suffix {
                    superscript {
                        group {
                            content {
                                term {
                                    number("2")
                                    suffix {
                                        superscript { singleToken { letter("n") } }
                                        noSubscript()
                                    }
                                }
                                eof()
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test script arg with greek letter`() {
        "x^\\alpha".assert {
            term {
                variable("x")
                suffix {
                    superscript {
                        singleToken { greekLetter("alpha") }
                    }
                }
            }
        }
    }

    @Test
    fun `test script arg with symbol`() {
        "x_+".assert {
            term {
                variable("x")
                suffix {
                    subscript {
                        singleToken { symbol("+") }
                    }
                }
            }
        }
    }

    // ================================================================
    // 6. 分数 (Fractions) - BNF: 88-91行
    // ================================================================

    @Test
    fun `test frac command`() {
        "\\frac{1}{2}".assert {
            term {
                fraction {
                    command("frac")
                    numerator { term { number("1") } }
                    denominator { term { number("2") } }
                }
            }
        }
    }

    @Test
    fun `test dfrac command`() {
        "\\dfrac{a}{b}".assert {
            term {
                fraction {
                    command("dfrac")
                    numerator { term { variable("a") } }
                    denominator { term { variable("b") } }
                }
            }
        }
    }

    @Test
    fun `test tfrac command`() {
        "\\tfrac{x}{y}".assert {
            term {
                fraction {
                    command("tfrac")
                    numerator { term { variable("x") } }
                    denominator { term { variable("y") } }
                }
            }
        }
    }

    @Test
    fun `test cfrac command`() {
        "\\cfrac{p}{q}".assert {
            term {
                fraction {
                    command("cfrac")
                    numerator { term { variable("p") } }
                    denominator { term { variable("q") } }
                }
            }
        }
    }

    @Test
    fun `test nested fractions`() {
        "\\frac{\\frac{1}{2}}{\\frac{3}{4}}".assert {
            term {
                fraction {
                    numerator {
                        term {
                            fraction {
                                numerator { term { number("1") } }
                                denominator { term { number("2") } }
                            }
                        }
                    }
                    denominator {
                        term {
                            fraction {
                                numerator { term { number("3") } }
                                denominator { term { number("4") } }
                            }
                        }
                    }
                }
            }
        }
    }

    // ================================================================
    // 7. 二项式系数 (Binomial) - BNF: 93-95行
    // ================================================================

    @Test
    fun `test binom command`() {
        "\\binom{n}{k}".assert {
            term {
                binom {
                    command("binom")
                    upper { term { variable("n") } }
                    lower { term { variable("k") } }
                }
            }
        }
    }

    @Test
    fun `test dbinom command`() {
        "\\dbinom{n}{k}".assert {
            term {
                binom {
                    command("dbinom")
                    upper { term { variable("n") } }
                    lower { term { variable("k") } }
                }
            }
        }
    }

    @Test
    fun `test tbinom command`() {
        "\\tbinom{n}{k}".assert {
            term {
                binom {
                    command("tbinom")
                    upper { term { variable("n") } }
                    lower { term { variable("k") } }
                }
            }
        }
    }

    @Test
    fun `test binom with complex expressions`() {
        "\\binom{n+1}{k-1}".assert {
            term {
                binom {
                    upper {
                        term { variable("n") }
                        term { symbol("+") }
                        term { number("1") }
                    }
                    lower {
                        term { variable("k") }
                        term { symbol("-") }
                        term { number("1") }
                    }
                }
            }
        }
    }

    // ================================================================
    // 8. 根式 (Roots) - BNF: 97-98行
    // ================================================================

    @Test
    fun `test sqrt without degree`() {
        "\\sqrt{x}".assert {
            term {
                sqrt {
                    noRoot()
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test sqrt with degree`() {
        "\\sqrt[3]{8}".assert {
            term {
                sqrt {
                    root { term { number("3") } }
                    content { term { number("8") } }
                }
            }
        }
    }

    @Test
    fun `test sqrt with variable degree`() {
        "\\sqrt[n]{x}".assert {
            term {
                sqrt {
                    root { term { variable("n") } }
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test nested sqrt`() {
        "\\sqrt{\\sqrt{x}}".assert {
            term {
                sqrt {
                    noRoot()
                    content {
                        term {
                            sqrt {
                                noRoot()
                                content { term { variable("x") } }
                            }
                        }
                    }
                }
            }
        }
    }

    // ================================================================
    // 9. 可扩展箭头 (Extensible Arrows) - BNF: 105-111行
    // ================================================================

    @Test
    fun `test xrightarrow without below`() {
        "\\xrightarrow{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above {
                        term { variable("text") }
                    }
                }
            }
        }
    }

    @Test
    fun `test xrightarrow with below`() {
        "\\xrightarrow[below]{above}".assert {
            term {
                extensibleArrow {
                    below { term { variable("below") } }
                    above { term { variable("above") } }
                }
            }
        }
    }

    @Test
    fun `test xleftarrow`() {
        "\\xleftarrow{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above { term { variable("text") } }
                }
            }
        }
    }

    @Test
    fun `test xleftrightarrow`() {
        "\\xleftrightarrow{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above { term { variable("text") } }
                }
            }
        }
    }

    @Test
    fun `test xRightarrow`() {
        "\\xRightarrow{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above { term { variable("text") } }
                }
            }
        }
    }

    @Test
    fun `test xLeftarrow`() {
        "\\xLeftarrow{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above { term { variable("text") } }
                }
            }
        }
    }

    @Test
    fun `test xLeftrightarrow`() {
        "\\xLeftrightarrow{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above { term { variable("text") } }
                }
            }
        }
    }

    @Test
    fun `test xhookrightarrow`() {
        "\\xhookrightarrow{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above { term { variable("text") } }
                }
            }
        }
    }

    @Test
    fun `test xhookleftarrow`() {
        "\\xhookleftarrow{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above { term { variable("text") } }
                }
            }
        }
    }

    @Test
    fun `test xtwoheadrightarrow`() {
        "\\xtwoheadrightarrow{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above { term { variable("text") } }
                }
            }
        }
    }

    @Test
    fun `test xtwoheadleftarrow`() {
        "\\xtwoheadleftarrow{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above { term { variable("text") } }
                }
            }
        }
    }

    @Test
    fun `test xmapsto`() {
        "\\xmapsto{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above { term { variable("text") } }
                }
            }
        }
    }

    @Test
    fun `test xtofrom`() {
        "\\xtofrom{text}".assert {
            term {
                extensibleArrow {
                    noBelow()
                    above { term { variable("text") } }
                }
            }
        }
    }

    // ================================================================
    // 10. 定界符 (Delimited) - BNF: 113-125行
    // ================================================================

    @Test
    fun `test left right delimiters`() {
        "\\left( x \\right)".assert {
            term {
                delimited {
                    left("(")
                    right(")")
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test bigl bigr delimiters`() {
        "\\bigl[ x \\bigr]".assert {
            term {
                delimited {
                    left("[")
                    right("]")
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test Bigl Bigr delimiters`() {
        "\\Bigl\\{ x \\Bigr\\}".assert {
            term {
                delimited {
                    left("{")
                    right("}")
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test biggl biggr delimiters`() {
        "\\biggl| x \\biggr|".assert {
            term {
                delimited {
                    left("|")
                    right("|")
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test Biggl Biggr delimiters`() {
        "\\Biggl( x \\Biggr)".assert {
            term {
                delimited {
                    left("(")
                    right(")")
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test null delimiter with dot`() {
        "\\left. x \\right|".assert {
            term {
                delimited {
                    left(".")
                    right("|")
                }
            }
        }
    }

    @Test
    fun `test langle rangle delimiters`() {
        "\\left\\langle x \\right\\rangle".assert {
            term {
                delimited {
                    left("\\langle")
                    right("\\rangle")
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test lfloor rfloor delimiters`() {
        "\\left\\lfloor x \\right\\rfloor".assert {
            term {
                delimited {
                    left("\\lfloor")
                    right("\\rfloor")
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test lceil rceil delimiters`() {
        "\\left\\lceil x \\right\\rceil".assert {
            term {
                delimited {
                    left("\\lceil")
                    right("\\rceil")
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test lvert rvert delimiters`() {
        "\\left\\lvert x \\right\\rvert".assert {
            term {
                delimited {
                    left("\\lvert")
                    right("\\rvert")
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test lVert rVert delimiters`() {
        "\\left\\lVert x \\right\\rVert".assert {
            term {
                delimited {
                    left("\\lVert")
                    right("\\rVert")
                    content { term { variable("x") } }
                }
            }
        }
    }

    // ================================================================
    // 11. 函数调用 (Function Call) - BNF: 127-135行
    // ================================================================

    @Test
    fun `test trigonometric functions`() {
        "\\sin".assert { term { function { name("sin") } } }
        "\\cos".assert { term { function { name("cos") } } }
        "\\tan".assert { term { function { name("tan") } } }
        "\\cot".assert { term { function { name("cot") } } }
        "\\sec".assert { term { function { name("sec") } } }
        "\\csc".assert { term { function { name("csc") } } }
    }

    @Test
    fun `test inverse trigonometric functions`() {
        "\\arcsin".assert { term { function { name("arcsin") } } }
        "\\arccos".assert { term { function { name("arccos") } } }
        "\\arctan".assert { term { function { name("arctan") } } }
    }

    @Test
    fun `test hyperbolic functions`() {
        "\\sinh".assert { term { function { name("sinh") } } }
        "\\cosh".assert { term { function { name("cosh") } } }
        "\\tanh".assert { term { function { name("tanh") } } }
        "\\coth".assert { term { function { name("coth") } } }
    }

    @Test
    fun `test logarithmic functions`() {
        "\\log".assert { term { function { name("log") } } }
        "\\ln".assert { term { function { name("ln") } } }
        "\\lg".assert { term { function { name("lg") } } }
        "\\exp".assert { term { function { name("exp") } } }
    }

    @Test
    fun `test extremum functions`() {
        "\\max".assert { term { function { name("max") } } }
        "\\min".assert { term { function { name("min") } } }
        "\\sup".assert { term { function { name("sup") } } }
        "\\inf".assert { term { function { name("inf") } } }
    }

    @Test
    fun `test other math functions`() {
        "\\arg".assert { term { function { name("arg") } } }
        "\\deg".assert { term { function { name("deg") } } }
        "\\det".assert { term { function { name("det") } } }
        "\\dim".assert { term { function { name("dim") } } }
        "\\gcd".assert { term { function { name("gcd") } } }
        "\\hom".assert { term { function { name("hom") } } }
        "\\ker".assert { term { function { name("ker") } } }
        "\\Pr".assert { term { function { name("Pr") } } }
        "\\bmod".assert { term { function { name("bmod") } } }
        "\\pmod".assert { term { function { name("pmod") } } }
    }

    @Test
    fun `test function with arguments`() {
        "\\sin x".assert {
            term { function { name("sin") } }
            term { variable("x") }
        }

        "\\log(x)".assert {
            term { function { name("log") } }
            term {
                group {
                    content { term { variable("x") } }
                }
            }
        }
    }

    // ================================================================
    // 12. 大型运算符 (Large Operators) - BNF: 137-144行
    // ================================================================

    @Test
    fun `test summation and product operators`() {
        "\\sum".assert { term { largeOperator { name("sum") } } }
        "\\prod".assert { term { largeOperator { name("prod") } } }
        "\\coprod".assert { term { largeOperator { name("coprod") } } }
    }

    @Test
    fun `test integral operators`() {
        "\\int".assert { term { largeOperator { name("int") } } }
        "\\iint".assert { term { largeOperator { name("iint") } } }
        "\\iiint".assert { term { largeOperator { name("iiint") } } }
        "\\oint".assert { term { largeOperator { name("oint") } } }
        "\\oiint".assert { term { largeOperator { name("oiint") } } }
        "\\oiiint".assert { term { largeOperator { name("oiiint") } } }
    }

    @Test
    fun `test big set operators`() {
        "\\bigcup".assert { term { largeOperator { name("bigcup") } } }
        "\\bigcap".assert { term { largeOperator { name("bigcap") } } }
        "\\bigvee".assert { term { largeOperator { name("bigvee") } } }
        "\\bigwedge".assert { term { largeOperator { name("bigwedge") } } }
    }

    @Test
    fun `test big circled operators`() {
        "\\bigoplus".assert { term { largeOperator { name("bigoplus") } } }
        "\\bigotimes".assert { term { largeOperator { name("bigotimes") } } }
        "\\bigodot".assert { term { largeOperator { name("bigodot") } } }
    }

    @Test
    fun `test big misc operators`() {
        "\\biguplus".assert { term { largeOperator { name("biguplus") } } }
        "\\bigsqcup".assert { term { largeOperator { name("bigsqcup") } } }
    }

    @Test
    fun `test limit operators`() {
        "\\lim".assert { term { largeOperator { name("lim") } } }
        "\\limsup".assert { term { largeOperator { name("limsup") } } }
        "\\liminf".assert { term { largeOperator { name("liminf") } } }
    }

    @Test
    fun `test operators with subscripts and superscripts`() {
        "\\sum_{i=1}^{n}".assert {
            term {
                largeOperator { name("sum") }
                suffix {
                    subscript {
                        group {
                            content {
                                term { variable("i") }
                                term { symbol("=") }
                                term { number("1") }
                            }
                        }
                    }
                    superscript {
                        group {
                            content { term { variable("n") } }
                        }
                    }
                }
            }
        }

        "\\int_0^\\infty".assert {
            term {
                largeOperator { name("int") }
                suffix {
                    subscript { singleToken { number("0") } }
                    superscript { singleToken { greekLetter("infty") } }
                }
            }
        }
    }

    // ================================================================
    // 13. 矩阵 (Matrix) - BNF: 146-155行
    // ================================================================

    @Test
    fun `test matrix environment`() {
        "\\begin{matrix}a&b\\\\c&d\\end{matrix}".assert {
            term {
                matrix {
                    environment("matrix")
                    row {
                        cell { term { variable("a") } }
                        cell { term { variable("b") } }
                        eof()
                    }
                    row {
                        cell { term { variable("c") } }
                        cell { term { variable("d") } }
                        eof()
                    }
                }
            }
        }
    }

    @Test
    fun `test pmatrix environment`() {
        "\\begin{pmatrix}1&2\\\\3&4\\end{pmatrix}".assert {
            term {
                matrix {
                    environment("pmatrix")
                    noGravity()
                    row {
                        cell {
                            term {
                                number("1")
                            }
                        }
                        cell {
                            term {
                                number("2")
                            }
                        }
                    }

                    row {
                        cell {
                            term {
                                number("3")
                            }
                        }
                        cell {
                            term {
                                number("4")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test bmatrix environment`() {
        "\\begin{bmatrix}1&2\\\\3&4\\end{bmatrix}".assert {
            term {
                matrix {
                    environment("bmatrix")
                    noGravity()
                    row {
                        cell {
                            term {
                                number("1")
                            }
                        }
                        cell {
                            term {
                                number("2")
                            }
                        }
                    }

                    row {
                        cell {
                            term {
                                number("3")
                            }
                        }
                        cell {
                            term {
                                number("4")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test Bmatrix environment`() {
        "\\begin{Bmatrix}1&2\\\\3&4\\end{Bmatrix}".assert {
            term {
                matrix {
                    environment("Bmatrix")
                    noGravity()
                    row {
                        cell {
                            term {
                                number("1")
                            }
                        }
                        cell {
                            term {
                                number("2")
                            }
                        }
                    }

                    row {
                        cell {
                            term {
                                number("3")
                            }
                        }
                        cell {
                            term {
                                number("4")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test vmatrix environment`() {
        "\\begin{vmatrix}1&2\\\\3&4\\end{vmatrix}".assert {
            term {
                matrix {
                    environment("vmatrix")
                    noGravity()
                    row {
                        cell {
                            term {
                                number("1")
                            }
                        }
                        cell {
                            term {
                                number("2")
                            }
                        }
                    }

                    row {
                        cell {
                            term {
                                number("3")
                            }
                        }
                        cell {
                            term {
                                number("4")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test Vmatrix environment`() {
        "\\begin{Vmatrix}1&2\\\\3&4\\end{Vmatrix}".assert {
            term {
                matrix {
                    environment("Vmatrix")
                    noGravity()
                    row {
                        cell {
                            term {
                                number("1")
                            }
                        }
                        cell {
                            term {
                                number("2")
                            }
                        }
                    }

                    row {
                        cell {
                            term {
                                number("3")
                            }
                        }
                        cell {
                            term {
                                number("4")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test smallmatrix environment`() {
        "\\begin{smallmatrix}a&b\\\\c&d\\end{smallmatrix}".assert {
            term {
                matrix {
                    environment("smallmatrix")
                    noGravity()
                    row {
                        cell {
                            term {
                                variable("a")
                            }
                        }
                        cell {
                            term {
                                variable("b")
                            }
                        }
                    }

                    row {
                        cell {
                            term {
                                variable("c")
                            }
                        }
                        cell {
                            term {
                                variable("d")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test array environment with alignment`() {
        "\\begin{array}{cc}a&b\\\\c&d\\end{array}".assert {
            term {
                matrix {
                    environment("array")
                    gravity("cc")
                    row {
                        cell {
                            term {
                                variable("a")
                            }
                        }
                        cell {
                            term {
                                variable("b")
                            }
                        }
                    }

                    row {
                        cell {
                            term {
                                variable("c")
                            }
                        }
                        cell {
                            term {
                                variable("d")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test array environment with mixed alignment`() {
        "\\begin{array}{lcr}left&center&right\\end{array}".assert {
            term {
                matrix {
                    environment("array")
                    gravity("lcr")
                    row {
                        cell { term { variable("left") } }
                        cell { term { variable("center") } }
                        cell { term { variable("right") } }
                    }
                }
            }
        }
    }

    @Test
    fun `test cases environment`() {
        "\\begin{cases}x&y\\\\a&b\\end{cases}".assert {
            term {
                matrix {
                    environment("cases")
                    noGravity()
                    row {
                        cell {
                            term {
                                variable("x")
                            }
                        }
                        cell {
                            term {
                                variable("y")
                            }
                        }
                    }

                    row {
                        cell {
                            term {
                                variable("a")
                            }
                        }
                        cell {
                            term {
                                variable("b")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test matrix with complex expressions`() {
        "\\begin{matrix}x^2&\\frac{1}{2}\\\\\\sqrt{3}&e^{i\\pi}\\end{matrix}".assert {
            term {
                matrix {
                    environment("matrix")
                    row {
                        cell {
                            term {
                                variable("x")
                                suffix {
                                    noSubscript()
                                    superscript {
                                        singleToken {
                                            number("2")
                                        }
                                    }
                                }
                            }
                        }
                        cell {
                            term {
                                fraction {
                                    numerator {
                                        term {
                                            number("1")
                                            noSuffix()
                                        }
                                    }
                                    denominator {
                                        term {
                                            number("2")
                                            noSuffix()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    row {
                        cell {
                            term {
                                sqrt {
                                    noRoot()
                                    content {
                                        term {
                                            number("3")
                                        }
                                    }
                                }
                            }
                        }

                        cell {
                            term {
                                variable("e")
                                suffix {
                                    superscript {
                                        group {
                                            content {
                                                term {
                                                    variable("i")
                                                }
                                                term { greekLetter("pi") }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ================================================================
    // 14. 文本命令 (Text Commands) - BNF: 157-162行
    // ================================================================

    @Test
    fun `test text command`() {
        "\\text{abc}".assert {
            term {
                text { content("abc") }
            }
        }
    }

    @Test
    fun `test mbox command`() {
        "\\mbox{text}".assert {
            term {
                text { content("text") }
            }
        }
    }

    @Test
    fun `test textrm command`() {
        "\\textrm{roman}".assert {
            term {
                text { content("roman") }
            }
        }
    }

    @Test
    fun `test textit command`() {
        "\\textit{italic}".assert {
            term {
                text { content("italic") }
            }
        }
    }

    @Test
    fun `test textbf command`() {
        "\\textbf{bold}".assert {
            term {
                text { content("bold") }
            }
        }
    }

    @Test
    fun `test text with unicode`() {
        "\\text{中文测试}".assert {
            term {
                text { content("中文测试") }
            }
        }

        "\\text{Hello 123 !@#}".assert {
            term {
                text { content("Hello 123 !@#") }
            }
        }
    }

    @Test
    fun `test text with spaces`() {
        "\\text{hello world}".assert {
            term {
                text { content("hello world") }
            }
        }
    }

    // ================================================================
    // 15. 字体命令 (Font Commands) - BNF: 166-170行
    // ================================================================

    @Test
    fun `test mathrm font`() {
        "\\mathrm{ABC}".assert {
            term {
                font {
                    command("mathrm")
                    content { term { variable("ABC") } }
                }
            }
        }
    }

    @Test
    fun `test mathit font`() {
        "\\mathit{xyz}".assert {
            term {
                font {
                    command("mathit")
                    content { term { variable("xyz") } }
                }
            }
        }
    }

    @Test
    fun `test mathbf font`() {
        "\\mathbf{X}".assert {
            term {
                font {
                    command("mathbf")
                    content { term { variable("X") } }
                }
            }
        }
    }

    @Test
    fun `test mathsf font`() {
        "\\mathsf{ABC}".assert {
            term {
                font {
                    command("mathsf")
                    content { term { variable("ABC") } }
                }
            }
        }
    }

    @Test
    fun `test mathtt font`() {
        "\\mathtt{code}".assert {
            term {
                font {
                    command("mathtt")
                    content { term { variable("code") } }
                }
            }
        }
    }

    @Test
    fun `test mathcal font`() {
        "\\mathcal{L}".assert {
            term {
                font {
                    command("mathcal")
                    content { term { variable("L") } }
                }
            }
        }
    }

    @Test
    fun `test mathbb font`() {
        "\\mathbb{R}".assert {
            term {
                font {
                    command("mathbb")
                    content { term { variable("R") } }
                }
            }
        }
    }

    @Test
    fun `test mathfrak font`() {
        "\\mathfrak{g}".assert {
            term {
                font {
                    command("mathfrak")
                    content { term { variable("g") } }
                }
            }
        }
    }

    @Test
    fun `test mathscr font`() {
        "\\mathscr{F}".assert {
            term {
                font {
                    command("mathscr")
                    content { term { variable("F") } }
                }
            }
        }
    }

    @Test
    fun `test boldsymbol font`() {
        "\\boldsymbol{\\alpha}".assert {
            term {
                font {
                    command("boldsymbol")
                    content { term { greekLetter("alpha") } }
                }
            }
        }
    }

    @Test
    fun `test bm font`() {
        "\\bm{\\beta}".assert {
            term {
                font {
                    command("bm")
                    content { term { greekLetter("beta") } }
                }
            }
        }
    }

    // ================================================================
    // 16. 重音符号 (Accent Commands) - BNF: 172-181行
    // ================================================================

    @Test
    fun `test accent with braces`() {
        "\\hat{a}".assert {
            term {
                accent {
                    command("hat")
                    mathList {
                        term { variable("a") }
                        eof()
                    }
                }
            }
        }
    }

    @Test
    fun `test accent with single token`() {
        "\\hat x".assert {
            term {
                accent {
                    command("hat")
                    singleToken { letter("x") }
                }
            }
        }
    }

    @Test
    fun `test hat and widehat`() {
        "\\widehat{abc}".assert {
            term {
                accent {
                    command("widehat")
                    mathList {
                        term { variable("abc") }
                    }
                }
            }
        }
    }

    @Test
    fun `test tilde and widetilde`() {
        "\\tilde{x}".assert {
            term {
                accent {
                    command("tilde")
                    mathList { term { variable("x") } }
                }
            }
        }

        "\\widetilde{xyz}".assert {
            term {
                accent {
                    command("widetilde")
                    mathList { term { variable("xyz") } }
                }
            }
        }
    }

    @Test
    fun `test bar overline underline`() {
        "\\bar{x}".assert {
            term {
                accent {
                    command("bar")
                    mathList { term { variable("x") } }
                }
            }
        }

        "\\overline{AB}".assert {
            term {
                accent {
                    command("overline")
                    mathList { term { variable("AB") } }
                }
            }
        }

        "\\underline{xyz}".assert {
            term {
                accent {
                    command("underline")
                    mathList { term { variable("xyz") } }
                }
            }
        }
    }

    @Test
    fun `test vec and arrow accents`() {
        "\\vec{a}".assert {
            term {
                accent {
                    command("vec")
                    mathList { term { variable("a") } }
                }
            }
        }

        "\\overrightarrow{AB}".assert {
            term {
                accent {
                    command("overrightarrow")
                    mathList { term { variable("AB") } }
                }
            }
        }

        "\\overleftarrow{BA}".assert {
            term {
                accent {
                    command("overleftarrow")
                    mathList { term { variable("BA") } }
                }
            }
        }
    }

    @Test
    fun `test dot accents`() {
        "\\dot{x}".assert {
            term {
                accent {
                    command("dot")
                    mathList { term { variable("x") } }
                }
            }
        }

        "\\ddot{x}".assert {
            term {
                accent {
                    command("ddot")
                    mathList { term { variable("x") } }
                }
            }
        }

        "\\dddot{x}".assert {
            term {
                accent {
                    command("dddot")
                    mathList { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test other accents`() {
        "\\acute{a}".assert {
            term {
                accent {
                    command("acute")
                    mathList { term { variable("a") } }
                }
            }
        }

        "\\grave{a}".assert {
            term {
                accent {
                    command("grave")
                    mathList { term { variable("a") } }
                }
            }
        }

        "\\breve{a}".assert {
            term {
                accent {
                    command("breve")
                    mathList { term { variable("a") } }
                }
            }
        }

        "\\check{a}".assert {
            term {
                accent {
                    command("check")
                    mathList { term { variable("a") } }
                }
            }
        }

        "\\mathring{a}".assert {
            term {
                accent {
                    command("mathring")
                    mathList { term { variable("a") } }
                }
            }
        }
    }

    @Test
    fun `test brace accents`() {
        "\\overbrace{x+y}".assert {
            term {
                accent {
                    command("overbrace")
                    mathList {
                        term { variable("x") }
                        term { symbol("+") }
                        term { variable("y") }
                    }
                }
            }
        }

        "\\underbrace{a+b+c}".assert {
            term {
                accent {
                    command("underbrace")
                    mathList {
                        term { variable("a") }
                        term { symbol("+") }
                        term { variable("b") }
                        term { symbol("+") }
                        term { variable("c") }
                    }
                }
            }
        }
    }

    // ================================================================
    // 17. 间距命令 (Spacing Commands) - BNF: 183-188行
    // ================================================================

    @Test
    fun `test basic spacing commands`() {
        "a\\,b".assert {
            term { variable("a") }
            spacing { command(",") }
            term { variable("b") }
        }

        "a\\:b".assert {
            term { variable("a") }
            spacing { command(":") }
            term { variable("b") }
        }

        "a\\;b".assert {
            term { variable("a") }
            spacing { command(";") }
            term { variable("b") }
        }

        "a\\!b".assert {
            term { variable("a") }
            spacing { command("!") }
            term { variable("b") }
        }
    }

    @Test
    fun `test quad spacing`() {
        "a \\quad b".assert {
            term { variable("a") }
            spacing { command("quad") }
            term { variable("b") }
        }

        "a\\qquad b".assert {
            term { variable("a") }
            spacing { command("qquad") }
            term { variable("b") }
        }
    }

    @Test
    fun `test hspace with different units`() {
        "a\\hspace{1em}b".assert {
            term { variable("a") }
            spacing {
                command("hspace")
                length {
                    size("1")
                    unit("em")
                }
            }
            term { variable("b") }
        }

        "a\\hspace{2.5pt}b".assert {
            term { variable("a") }
            spacing {
                command("hspace")
                length {
                    size("2.5")
                    unit("pt")
                }
            }
            term { variable("b") }
        }

        "a\\hspace{10px}b".assert {
            term { variable("a") }
            spacing {
                command("hspace")
                length {
                    size("10")
                    unit("px")
                }
            }
            term { variable("b") }
        }
    }

    @Test
    fun `test all length units`() {
        "\\hspace{1em}".assert {
            spacing { command("hspace"); length { unit("em") } }
        }
        "\\hspace{1ex}".assert {
            spacing { command("hspace"); length { unit("ex") } }
        }
        "\\hspace{1pt}".assert {
            spacing { command("hspace"); length { unit("pt") } }
        }
        "\\hspace{1px}".assert {
            spacing { command("hspace"); length { unit("px") } }
        }
        "\\hspace{1cm}".assert {
            spacing { command("hspace"); length { unit("cm") } }
        }
        "\\hspace{1mm}".assert {
            spacing { command("hspace"); length { unit("mm") } }
        }
        "\\hspace{1in}".assert {
            spacing { command("hspace"); length { unit("in") } }
        }
    }

    @Test
    fun `test phantom commands`() {
        "a\\hphantom{xyz}b".assert {
            term { variable("a") }
            spacing {
                command("hphantom")
                content {
                    term { variable("xyz") }
                }
            }
            term { variable("b") }
        }

        "a\\vphantom{\\frac{1}{2}}b".assert {
            term { variable("a") }
            spacing {
                command("vphantom")
                content {
                    term {
                        fraction {
                            numerator { term { number("1") } }
                            denominator { term { number("2") } }
                        }
                    }
                }
            }
            term { variable("b") }
        }

        "a\\phantom{xyz}b".assert {
            term { variable("a") }
            spacing {
                command("phantom")
                content {
                    term { variable("xyz") }
                }
            }
            term { variable("b") }
        }
    }

    // ================================================================
    // 18. 综合复杂测试 (Complex Real World Cases)
    // ================================================================

    @Test
    fun `test quadratic formula`() {
        "x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}".assert {
            term { variable("x") }
            term { symbol("=") }
            term {
                fraction {
                    numerator {
                        term { symbol("-") }
                        term { variable("b") }
                        term { symbol("\\pm") }
                        term {
                            sqrt {
                                noRoot()
                                content {
                                    term {
                                        variable("b")
                                        suffix {
                                            superscript { singleToken { number("2") } }
                                        }
                                    }
                                    term { symbol("-") }
                                    term { number("4") }
                                    term { variable("ac") }
                                }
                            }
                        }
                    }
                    denominator {
                        term { number("2") }
                        term { variable("a") }
                    }
                }
            }
        }
    }

    @Test
    fun `test integral with limits`() {
        "\\int_0^\\infty e^{-x^2} dx".assert {
            term {
                largeOperator { name("int") }
                suffix {
                    subscript { singleToken { number("0") } }
                    superscript { singleToken { greekLetter("infty") } }
                }
            }
            term {
                variable("e")
                suffix {
                    superscript {
                        group {
                            content {
                                term { symbol("-") }
                                term {
                                    variable("x")
                                    suffix {
                                        superscript { singleToken { number("2") } }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            term { variable("dx") }
        }
    }

    @Test
    fun `test summation formula`() {
        "\\sum_{i=1}^{n} i = \\frac{n(n+1)}{2}".assert {
            term {
                largeOperator { name("sum") }
                suffix {
                    subscript {
                        group {
                            content {
                                term { variable("i") }
                                term { symbol("=") }
                                term { number("1") }
                            }
                        }
                    }
                    superscript {
                        group {
                            content { term { variable("n") } }
                        }
                    }
                }
            }
            term { variable("i") }
            term { symbol("=") }
            term {
                fraction {
                    numerator {
                        term { variable("n") }
                        term {
                            group {
                                content {
                                    term { variable("n") }
                                    term { symbol("+") }
                                    term { number("1") }
                                }
                            }
                        }
                    }
                    denominator {
                        term { number("2") }
                    }
                }
            }
        }
    }

    @Test
    fun `test matrix equation`() {
        "\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}\\begin{pmatrix}x\\\\y\\end{pmatrix}".assert {
            term {
                matrix {
                    environment("pmatrix")
                    noGravity()
                    row {
                        cell {
                            term { variable("a") }
                        }
                        cell {
                            term { variable("b") }
                        }
                    }

                    row {
                        cell {
                            term { variable("c") }
                        }
                        cell {
                            term { variable("d") }
                        }
                    }
                }
            }
            term {
                matrix {
                    environment("pmatrix")
                    noGravity()
                    row {
                        cell {
                            term { variable("x") }
                        }
                    }

                    row {
                        cell {
                            term { variable("y") }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test binomial theorem`() {
        "\\binom{n}{k} = \\frac{n!}{k!(n-k)!}".assert {
            term {
                binom {
                    upper { term { variable("n") } }
                    lower { term { variable("k") } }
                }
            }
            term { symbol("=") }
            term {
                fraction {
                    numerator {
                        term { variable("n") }
                        term { symbol("!") }
                    }
                    denominator {
                        term { variable("k") }
                        term { symbol("!") }
                        term {
                            group {
                                content {
                                    term { variable("n") }
                                    term { symbol("-") }
                                    term { variable("k") }
                                }
                            }
                        }
                        term { symbol("!") }
                    }
                }
            }
        }
    }

    @Test
    fun `test taylor series`() {
        "f(x) = \\sum_{n=0}^{\\infty} \\frac{f^{(n)}(a)}{n!}(x-a)^n".assert {
            term { variable("f") }
            term {
                group {
                    content { term { variable("x") } }
                }
            }
            term { symbol("=") }
            term {
                largeOperator { name("sum") }
                suffix {
                    subscript {
                        group {
                            content {
                                term { variable("n") }
                                term { symbol("=") }
                                term { number("0") }
                            }
                        }
                    }
                    superscript {
                        group {
                            content { term { greekLetter("infty") } }
                        }
                    }
                }
            }
            term {
                fraction {
                    numerator {
                        term {
                            variable("f")
                            suffix {
                                superscript {
                                    group {
                                        content {
                                            term {
                                                group {
                                                    content { term { variable("n") } }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        term {
                            group {
                                content { term { variable("a") } }
                            }
                        }
                    }
                    denominator {
                        term { variable("n") }
                        term { symbol("!") }
                    }
                }
            }
            term {
                group {
                    content {
                        term { variable("x") }
                        term { symbol("-") }
                        term { variable("a") }
                    }
                }
                suffix {
                    superscript { singleToken { letter("n") } }
                }
            }
        }
    }

    @Test
    fun `test limit definition`() {
        "\\lim_{x\\to 0}\\frac{\\sin x}{x} = 1".assert {
            term {
                largeOperator { name("lim") }
                suffix {
                    subscript {
                        group {
                            content {
                                term { variable("x") }
                                term { symbol("\\to") }
                                term { number("0") }
                            }
                        }
                    }
                }
            }
            term {
                fraction {
                    numerator {
                        term { function { name("sin") } }
                        term { variable("x") }
                    }
                    denominator {
                        term { variable("x") }
                    }
                }
            }
            term { symbol("=") }
            term { number("1") }
        }
    }

    @Test
    fun `test cases environment with text`() {
        "f(x) = \\begin{cases}x^2 & \\text{if } x \\ge 0\\\\-x^2 & \\text{otherwise}\\end{cases}".assert {
            term { variable("f") }
            term {
                group {
                    content { term { variable("x") } }
                }
            }
            term { symbol("=") }
            term {
                matrix {
                    environment("cases")
                    row {
                        cell {
                            term {
                                variable("x")
                                suffix {
                                    noSubscript()
                                    superscript {
                                        singleToken {
                                            number("2")
                                        }
                                    }
                                }
                            }
                        }
                        cell {
                            term {
                                text { content("if ") }
                            }
                            term {
                                variable("x")
                            }
                            term {
                                symbol("\\ge")
                            }
                            term {
                                number("0")
                            }
                        }
                    }

                    row {
                        cell {
                            term {
                                symbol("-")
                            }
                            term {
                                variable("x")
                                suffix {
                                    noSubscript()
                                    superscript {
                                        singleToken {
                                            number("2")
                                        }
                                    }
                                }
                            }
                        }
                        cell {
                            term {
                                text { content("otherwise") }
                            }
                        }
                    }
                }
            }
        }
    }

    // ================================================================
    // 19. 边界情况与错误测试 (Edge Cases & Error Cases)
    // ================================================================

    @Test
    fun `test empty expressions`() {
        "{}".assert {
            term {
                group {
                    content { /* empty */ }
                }
            }
        }
    }

    @Test
    fun `test nested empty groups`() {
        "{{}}".assert {
            term {
                group {
                    content {
                        term {
                            group {
                                content { /* empty */ }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test deeply nested structures`() {
        "\\frac{\\frac{\\frac{1}{2}}{3}}{4}".assert {
            term {
                fraction {
                    numerator {
                        term {
                            fraction {
                                numerator {
                                    term {
                                        fraction {
                                            numerator { term { number("1") } }
                                            denominator { term { number("2") } }
                                        }
                                    }
                                }
                                denominator { term { number("3") } }
                            }
                        }
                    }
                    denominator { term { number("4") } }
                }
            }
        }
    }

    @Test
    fun `test whitespace handling`() {
        "  x  +  y  ".assert {
            term { variable("x") }
            term { symbol("+") }
            term { variable("y") }
        }
    }

    @Test(expected = MathParseException::class)
    fun `test error unclosed group`() {
        parse("{123")
    }

    @Test(expected = MathParseException::class)
    fun `test error missing fraction argument`() {
        parse("\\frac{1}")
    }

    @Test(expected = MathParseException::class)
    fun `test error unclosed delimiter`() {
        parse("\\left( x")
    }

    @Test(expected = MathParseException::class)
    fun `test error mismatched delimiters`() {
        // ignore mismatched
        parse("\\left( x \\right]")
    }

    @Test(expected = MathParseException::class)
    fun `test error mismatched delimiters with {`() {
        // ignore mismatched
        parse("\\left\\{ x \\right]")
    }

    @Test
    fun `test { delimiters`() {
        "\\left\\{x\\right\\}".assert {
            term {
                delimited {
                    level(DelimitedAtom.LEVEL_L0)
                    left("{")
                    content { term { variable("x") } }
                    right("}")
                }
            }
        }
    }

    @Test(expected = MathParseException::class)
    fun `test error unknown command`() {
        parse("\\unknowncommand")
    }

    @Test(expected = MathParseException::class)
    fun `test error unclosed binom`() {
        parse("\\binom{n}")
    }

    @Test(expected = MathParseException::class)
    fun `test error matrix environment mismatch`() {
        parse("\\begin{matrix}a\\end{pmatrix}")
    }

    @Test
    fun `test parse common symbol`() {
        parse("div  \\div = ÷ ")
    }

    // ================================================================
    // 工具方法 (Test Infrastructure)
    // ================================================================

    private fun parse(input: String) = MathParser(CharStream(input)).parse()

    /**
     * 连接 Parser 和 MathListAsserter
     */
    private fun String.assert(block: MathListAsserter.() -> Unit) {
        val ast = parse(this)
        val asserter = MathListAsserter(ast)
        asserter.apply(block)
        asserter.eof()
    }

    companion object {
        @Throws(MathParseException::class)
        fun parse(input: String): MathList {
            val stream = CharStream(input, 0, input.length)
            val parser = MathParser(stream)
            return parser.parse()
        }

        @JvmStatic
        fun assertParsesTo(input: String, expectedOutput: String?) {
            try {
                val ast: MathList = parse(input)
                val actual = ast.toString()
                Assert.assertEquals("输入: $input", expectedOutput, actual)
                println("✅ $input → $actual")
            } catch (e: MathParseException) {
                Assert.fail("解析失败: $input - ${e.pretty()}")
            }
        }
    }
}