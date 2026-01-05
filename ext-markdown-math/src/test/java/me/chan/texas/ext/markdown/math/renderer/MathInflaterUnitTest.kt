package me.chan.texas.ext.markdown.math.renderer

import me.chan.texas.ext.markdown.math.ast.MathList
import me.chan.texas.ext.markdown.math.ast.MathParseException
import me.chan.texas.ext.markdown.math.ast.MathParser
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions
import me.chan.texas.misc.PaintSet
import me.chan.texas.renderer.core.graphics.TexasPaint
import me.chan.texas.renderer.core.graphics.TexasPaintImpl
import me.chan.texas.utils.CharStream
import org.junit.Before
import org.junit.Test

class MathInflaterUnitTest {
    private var inflater: MathRendererInflater? = null
    private var defaultStyles: MathPaint.Styles? = null
    private var defaultPaint: MathPaint? = null

    @Before
    fun setUp() {
        inflater = MathRendererInflater()
        val texasPaint: TexasPaint = TexasPaintImpl()
        texasPaint.reset(PaintSet(MockTextPaint(1)))
        defaultPaint = MathPaintImpl(texasPaint)
        defaultStyles = MathPaint.Styles(defaultPaint)
    }

    /**
     * 解析输入并返回 AST
     */
    @Throws(MathParseException::class)
    private fun parse(input: String): MathList? {
        val stream = CharStream(input, 0, input.length)
        val parser = MathParser(stream)
        return parser.parse()
    }

    /**
     * 解析并渲染，返回 RendererNode
     */
    @Throws(MathParseException::class)
    private fun inflate(input: String, block: DispatchAsserter.() -> Unit) {
        val ast = parse(input)
        val node = inflater!!.inflate(defaultStyles, ast)
        // 触发 measure 以便获取子节点结构
        node.measure(defaultPaint)
        node.layout(0f, 0f)
        DispatchAsserter(node).block()
    }

    @Test
    fun `test accent single token`() {
        inflate("\\hat x") {
            accent {
                cmd {
                    symbol {
                        content(MathFontOptions.symbol("asciicircum"))
                    }
                }

                content {
                    text {
                        content("x")
                    }
                }
            }
        }
    }

    @Test
    fun `test accent list`() {
        inflate("\\hat {x+y}") {
            accent {
                cmd {
                    symbol {
                        content(MathFontOptions.symbol("asciicircum"))
                    }
                }

                content {
                    linearGroup {
                        child {
                            text {
                                content("x")
                            }
                        }
                        child {
                            symbol {
                                content(MathFontOptions.symbol("plus"))
                            }
                        }
                        child {
                            text {
                                content("y")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test brace`() {
        inflate("\\left(x\\right)") {
            brace {
                left {
                    stretchy {
                        symbol(MathFontOptions.symbol("parenleft"))
                    }
                }
                content {
                    text {
                        content("x")
                    }
                }

                right {
                    stretchy {
                        symbol(MathFontOptions.symbol("parenright"))
                    }
                }
            }
        }
    }

    // ========== Spacing Tests ==========

    @Test
    fun `test thin space`() {
        inflate("a\\,b") {
            linearGroup {
                child {
                    text {
                        content("a")
                    }
                }
                child {
                    space {}
                }
                child {
                    text {
                        content("b")
                    }
                }
            }
        }
    }

    @Test
    fun `test medium space`() {
        inflate("a\\:b") {
            linearGroup {
                child {
                    text {
                        content("a")
                    }
                }
                child {
                    space {}
                }
                child {
                    text {
                        content("b")
                    }
                }
            }
        }
    }

    @Test
    fun `test thick space`() {
        inflate("a\\;b") {
            linearGroup {
                child {
                    text {
                        content("a")
                    }
                }
                child {
                    space {}
                }
                child {
                    text {
                        content("b")
                    }
                }
            }
        }
    }

    @Test
    fun `test negative thin space`() {
        inflate("a\\!b") {
            linearGroup {
                child {
                    text {
                        content("a")
                    }
                }
                child {
                    space {}
                }
                child {
                    text {
                        content("b")
                    }
                }
            }
        }
    }

    @Test
    fun `test quad space`() {
        inflate("a\\quad b") {
            linearGroup {
                child {
                    text {
                        content("a")
                    }
                }
                child {
                    space {}
                }
                child {
                    text {
                        content("b")
                    }
                }
            }
        }
    }

    @Test
    fun `test qquad space`() {
        inflate("a\\qquad b") {
            linearGroup {
                child {
                    text {
                        content("a")
                    }
                }
                child {
                    space {}
                }
                child {
                    text {
                        content("b")
                    }
                }
            }
        }
    }

    @Test
    fun `test hspace`() {
        inflate("a\\hspace{2em}b") {
            linearGroup {
                child {
                    text {
                        content("a")
                    }
                }
                child {
                    space {}
                }
                child {
                    text {
                        content("b")
                    }
                }
            }
        }
    }

    @Test
    fun `test hphantom`() {
        inflate("a\\hphantom{xyz}b") {
            linearGroup {
                child {
                    text {
                        content("a")
                    }
                }
                child {
                    phantom {
                        content {
                            linearGroup {
                                child {
                                    text {
                                        content("xyz")
                                    }
                                }
                            }
                        }
                    }
                }
                child {
                    text {
                        content("b")
                    }
                }
            }
        }
    }

    @Test
    fun `test vphantom`() {
        inflate("a\\vphantom{xyz}b") {
            linearGroup {
                child {
                    text {
                        content("a")
                    }
                }
                child {
                    phantom {
                        content {
                            linearGroup {
                                child {
                                    text {
                                        content("xyz")
                                    }
                                }
                            }
                        }
                    }
                }
                child {
                    text {
                        content("b")
                    }
                }
            }
        }
    }

    @Test
    fun `test phantom`() {
        inflate("a\\phantom{xyz}b") {
            linearGroup {
                child {
                    text {
                        content("a")
                    }
                }
                child {
                    phantom {
                        content {
                            linearGroup {
                                child {
                                    text {
                                        content("xyz")
                                    }
                                }
                            }
                        }
                    }
                }
                child {
                    text {
                        content("b")
                    }
                }
            }
        }
    }

    // ========== Atom Tests ==========

    @Test
    fun `test number atom`() {
        inflate("123") {
            text {
                content("123")
            }
        }
    }

    @Test
    fun `test variable atom`() {
        inflate("x") {
            text {
                content("x")
            }
        }
    }

    @Test
    fun `test greek letter variable`() {
        val list = mapOf<String, String>(
            "alpha" to "α",
            "beta" to "β",
            "gamma" to "γ",
            "delta" to "δ",
            "epsilon" to "ε",
            "varepsilon" to "ϵ",
            "zeta" to "ζ",
            "eta" to "η",
            "theta" to "θ",
            "vartheta" to "ϑ",
            "iota" to "ι",
            "kappa" to "κ",
            "lambda" to "λ",
            "mu" to "μ",
            "nu" to "ν",
            "xi" to "ξ",
            "pi" to "π",
            "varpi" to "ϖ",
            "rho" to "ρ",
            "varrho" to "ϱ",
            "sigma" to "σ",
            "varsigma" to "ς",
            "tau" to "τ",
            "upsilon" to "υ",
            "phi" to "φ",
            "varphi" to "ϕ",
            "chi" to "χ",
            "psi" to "ψ",
            "omega" to "ω",
            "Gamma" to "Γ",
            "Delta" to "Δ",
            "Theta" to "Θ",
            "Lambda" to "Λ",
            "Xi" to "Ξ",
            "Pi" to "Π",
            "Sigma" to "Σ",
            "Upsilon" to "Υ",
            "Phi" to "Φ",
            "Psi" to "Ψ",
            "Omega" to "Ω",
            "infty" to "∞"
        )
        for ((key, value) in list) {
            inflate("\\$key") {
                text {
                    content(value)
                }
            }
        }
    }

    @Test
    fun `test group with curly braces`() {
        inflate("{x+y}") {
            linearGroup {
                child {
                    text {
                        content("x")
                    }
                }
                child {
                    symbol {
                        content(MathFontOptions.symbol("plus"))
                    }
                }
                child {
                    text {
                        content("y")
                    }
                }
            }
        }
    }

    @Test
    fun `test group with parentheses`() {
        inflate("(x+y)") {
            linearGroup {
                child {
                    symbol {
                        content(MathFontOptions.symbol("parenleft"))
                    }
                }
                child {
                    linearGroup {
                        child {
                            text {
                                content("x")
                            }
                        }
                        child {
                            symbol {
                                content(MathFontOptions.symbol("plus"))
                            }
                        }
                        child {
                            text {
                                content("y")
                            }
                        }
                    }
                }
                child {
                    symbol {
                        content(MathFontOptions.symbol("parenright"))
                    }
                }
            }
        }
    }

    @Test
    fun `test group with square brackets`() {
        inflate("[x+y]") {
            linearGroup {
                child {
                    symbol {
                        content(MathFontOptions.symbol("bracketleft"))
                    }
                }
                child {
                    linearGroup {
                        child {
                            text {
                                content("x")
                            }
                        }
                        child {
                            symbol {
                                content(MathFontOptions.symbol("plus"))
                            }
                        }
                        child {
                            text {
                                content("y")
                            }
                        }
                    }
                }
                child {
                    symbol {
                        content(MathFontOptions.symbol("bracketright"))
                    }
                }
            }
        }
    }

    @Test
    fun `test group with explicit curly braces`() {
        inflate("\\{x+y\\}") {
            linearGroup {
                child {
                    symbol { content("{") }
                }

                child {
                    linearGroup {
                        child {
                            text {
                                content("x")
                            }
                        }
                        child {
                            symbol {
                                content(MathFontOptions.symbol("plus"))
                            }
                        }
                        child {
                            text {
                                content("y")
                            }
                        }
                    }
                }

                child {
                    symbol { content("}") }
                }
            }
        }
    }

    @Test
    fun `test fraction`() {
        inflate("\\frac{a}{b}") {
            fraction {
                numerator { text { content("a") } }
                denominator { text { content("b") } }
            }
        }
    }

    @Test
    fun `test dfraction`() {
        inflate("\\dfrac{a}{b}") {
            fraction {
                numerator { text { content("a") } }
                denominator { text { content("b") } }
            }
        }
    }

    @Test
    fun `test tfraction`() {
        inflate("\\tfrac{a}{b}") {
            fraction {
                numerator { text { content("a") } }
                denominator { text { content("b") } }
            }
        }
    }

    @Test
    fun `test cfraction`() {
        inflate("\\cfrac{a}{b}") {
            fraction {
                numerator { text { content("a") } }
                denominator { text { content("b") } }
            }
        }
    }

    @Test
    fun `test binom`() {
        inflate("\\binom{n}{k}") {
            brace {
                left { stretchy { symbol("(") } }
                content {
                    linearGroup {
                        child { text { content("n") } }
                        child { text { content("k") } }
                    }
                }
                right { stretchy { symbol(")") } }
            }
        }
    }

    @Test
    fun `test dbinom`() {
        inflate("\\dbinom{n}{k}") {
            brace {
                left { stretchy { symbol("(") } }
                content {
                    linearGroup {
                        child { text { content("n") } }
                        child { text { content("k") } }
                    }
                }
                right { stretchy { symbol(")") } }
            }
        }
    }

    @Test
    fun `test tbinom`() {
        inflate("\\tbinom{n}{k}") {
            brace {
                left { stretchy { symbol("(") } }
                content {
                    linearGroup {
                        child { text { content("n") } }
                        child { text { content("k") } }
                    }
                }
                right { stretchy { symbol(")") } }
            }
        }
    }

    @Test
    fun `test sqrt without root`() {
        inflate("\\sqrt{x}") {
            sqrt {
                noRoot()
                content {
                    text {
                        content("x")
                    }
                }
            }
        }
    }

    @Test
    fun `test sqrt with root`() {
        inflate("\\sqrt[3]{x}") {
            sqrt {
                root {
                    text {
                        content("3")
                    }
                }
                content {
                    text {
                        content("x")
                    }
                }
            }
        }
    }

    @Test
    fun `test left right with brackets`() {
        inflate("\\left(x\\right)") {
            brace {
                left { stretchy { symbol("(") } }
                content { text { content("x") } }
                right { stretchy { symbol(")") } }
            }
        }
    }

    @Test
    fun `test left right with square brackets`() {
        inflate("\\left[x\\right]") {
            brace {
                left { stretchy3 { symbol("⎡", "⎢", "⎣", "⎢") } }
                content { text { content("x") } }
                right { stretchy3 { symbol("⎤", "⎥", "⎦", "⎥") } }
            }
        }
    }

    @Test
    fun `test left right with curly braces`() {
        inflate("\\left\\{x\\right\\}") {
            brace {
                left { stretchy3 { symbol("⎧", "⎨", "⎩", "⎪") } }
                content { text { content("x") } }
                right { stretchy3 { symbol("⎫", "⎬", "⎭", "⎪") } }
            }
        }
    }

    @Test
    fun `test left right with single pipes`() {
        inflate("\\left|x\\right|") {
            brace {
                left { stretchy { symbol("∣") } }
                content { text { content("x") } }
                right { stretchy { symbol("∣") } }
            }
        }
    }

    @Test
    fun `test left right with pipes`() {
        inflate("\\left\\|x\\right\\|") {
            brace {
                left { stretchy { symbol("∥") } }
                content { text { content("x") } }
                right { stretchy { symbol("∥") } }
            }
        }
    }

    @Test
    fun `test left right with angle brackets`() {
        inflate("\\left\\langle x\\right\\rangle") {
            brace {
                left { stretchy { symbol("〈") } }
                content { text { content("x") } }
                right { stretchy { symbol("〉") } }
            }
        }
    }

    @Test
    fun `test left right with floor`() {
        inflate("\\left\\lfloor x\\right\\rfloor") {
            brace {
                left { stretchy2 { symbol("⎣", "⎢") } }
                content { text { content("x") } }
                right { stretchy2 { symbol("⎦", "⎥") } }
            }
        }
    }

    @Test
    fun `test left right with ceil`() {
        inflate("\\left\\lceil x\\right\\rceil") {
            brace {
                left { stretchy2 { symbol("⎡", "⎢") } }
                content { text { content("x") } }
                right { stretchy2 { symbol("⎤", "⎥") } }
            }
        }
    }

    @Test
    fun `test left right with vert`() {
        inflate("\\left\\lvert x\\right\\rvert") {
            brace {
                left { stretchy { symbol("∣") } }
                content { text { content("x") } }
                right { stretchy { symbol("∣") } }
            }
        }
    }

    @Test
    fun `test left right with Vert`() {
        inflate("\\left\\lVert x\\right\\rVert") {
            brace {
                left { stretchy { symbol("∥") } }
                content { text { content("x") } }
                right { stretchy { symbol("∥") } }
            }
        }
    }

    @Test
    fun `test left right with dot`() {
        inflate("\\left.x\\right)") {
            brace {
                noLeft()
                content {
                    text { content("x") }
                }
                right { stretchy { symbol(")") } }
            }
        }
    }

    @Test
    fun `test bigl`() {
        inflate("\\bigl(x\\bigr)") {
            brace {
                left { stretchy { symbol("(") } }
                content { text { content("x") } }
                right { stretchy { symbol(")") } }
            }
        }
    }

    @Test
    fun `test Bigl`() {
        inflate("\\Bigl[x\\Bigr]") {
            brace {
                left { stretchy3 { symbol("⎡", "⎢", "⎣", "⎢") } }
                content { text { content("x") } }
                right { stretchy3 { symbol("⎤", "⎥", "⎦", "⎥") } }
            }
        }
    }

    @Test
    fun `test biggl`() {
        inflate("\\biggl\\{x\\biggr\\}") {
            brace {
                left { stretchy3 { symbol("⎧", "⎨", "⎩", "⎪") } }
                content { text { content("x") } }
                right { stretchy3 { symbol("⎫", "⎬", "⎭", "⎪") } }
            }
        }
    }

    @Test
    fun `test Biggl`() {
        inflate("\\Biggl|x\\Biggr|") {
            brace {
                left { stretchy { symbol("∣") } }
                content { text { content("x") } }
                right { stretchy { symbol("∣") } }
            }
        }
    }

    @Test
    fun `test function call`() {
        val name = arrayOf(
            "sin",
            "cos",
            "tan",
            "cot",
            "sec",
            "csc",
            "arcsin",
            "arccos",
            "arctan",
            "sinh",
            "cosh",
            "tanh",
            "coth",
            "log",
            "ln",
            "lg",
            "exp",
            "max",
            "min",
            "sup",
            "inf",
            "arg",
            "deg",
            "det",
            "dim",
            "gcd",
            "hom",
            "ker",
            "Pr",
            "bmod",
            "pmod"
        )
        for (key in name) {
            inflate("\\$key") {
                linearGroup {
                    child {
                        text {
                            content(key)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `test large operator`() {
        var symbols = mapOf<String, String>(
            "sum" to "summation",
            "prod" to "product",
            "coprod" to "uni2210",
            "bigcup" to "uni22C3",
            "bigcap" to "uni22C2",
            "bigvee" to "uni22C1",
            "bigwedge" to "uni22C0",
            "bigoplus" to "uni2A01",
            "bigotimes" to "uni2A02",
            "bigodot" to "uni2A00",
            "biguplus" to "uni2A04",
            "bigsqcup" to "uni2A06"
        )
        for ((key, value) in symbols) {
            inflate("\\${key}_1^2") {
                decorGroup {
                    center {
                        symbol {
                            content(MathFontOptions.symbol(value))
                        }
                    }
                    bottom { text { content("1") } }
                    top { text { content("2") } }
                }
            }
        }

        symbols = mapOf(
            "int" to "integral",
            "iint" to "uni222C",
            "iiint" to "uni222D",
            "oint" to "contourintegral",
            "oiint" to "uni222F",
            "oiiint" to "uni2230",
        )
        for ((key, value) in symbols) {
            inflate("\\${key}_1^2") {
                decorGroup {
                    center {
                        symbol {
                            content(MathFontOptions.symbol(value))
                        }
                    }
                    rightBottom { text { content("1") } }
                    rightTop { text { content("2") } }
                }
            }
        }

        symbols = mapOf(
            "lim" to "lim",
            "limsup" to "lim sup",
            "liminf" to "lim inf",
        )
        for ((key, value) in symbols) {
            inflate("\\${key}_1^2") {
                decorGroup {
                    center {
                        text {
                            content(value)
                        }
                    }
                    bottom { text { content("1") } }
                    top { text { content("2") } }
                }
            }
        }
    }

    @Test
    fun `test extensible arrow`() {
        inflate("\\xrightarrow{above}") {
            decorGroup {
                center {
                    symbol {
                        content("→")
                    }
                }
                top { text { content("above") } }
            }
        }
    }

    @Test
    fun `test extensible arrow with below`() {
        inflate("\\xrightarrow[below]{above}") {
            decorGroup {
                center {
                    symbol {
                        content("→")
                    }
                }
                top { text { content("above") } }
                bottom { text { content("below") } }
            }
        }
    }

    @Test
    fun `test matrix`() {
        inflate("\\begin{matrix}a&b\\\\c&d\\end{matrix}") {
            gridGroup {
                row {
                    content {
                        child {
                            text { content("a") }
                        }
                        child { space { } }
                        child { text { content("b") } }
                    }
                }
                row {
                    content {
                        child {
                            text { content("c") }
                        }
                        child { space { } }
                        child { text { content("d") } }
                    }
                }
            }
        }
    }

    @Test
    fun `test pmatrix`() {
        inflate("\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}") {
            brace {
                left { stretchy { symbol("(") } }
                content {
                    gridGroup {
                        row {
                            content {
                                child {
                                    text { content("a") }
                                }
                                child { space { } }
                                child { text { content("b") } }
                            }
                        }
                        row {
                            content {
                                child {
                                    text { content("c") }
                                }
                                child { space { } }
                                child { text { content("d") } }
                            }
                        }
                    }
                }
                right { stretchy { symbol(")") } }
            }
        }
    }

    @Test
    fun `test bmatrix`() {
        inflate("\\begin{bmatrix}a&b\\\\c&d\\end{bmatrix}") {
            brace {
                left { stretchy3 { symbol("⎡", "⎢", "⎣", "⎢") } }
                content {
                    gridGroup {
                        row {
                            content {
                                child {
                                    text { content("a") }
                                }
                                child { space { } }
                                child { text { content("b") } }
                            }
                        }
                        row {
                            content {
                                child {
                                    text { content("c") }
                                }
                                child { space { } }
                                child { text { content("d") } }
                            }
                        }
                    }
                }
                right { stretchy3 { symbol("⎤", "⎥", "⎦", "⎥") } }
            }
        }
    }

    @Test
    fun `test vmatrix`() {
        inflate("\\begin{vmatrix}a&b\\\\c&d\\end{vmatrix}") {
            brace {
                left { stretchy { symbol("∣") } }
                content {
                    gridGroup {
                        row {
                            content {
                                child { text { content("a") } }
                                child { space { } }
                                child { text { content("b") } }
                            }
                        }
                        row {
                            content {
                                child { text { content("c") } }
                                child { space { } }
                                child { text { content("d") } }
                            }
                        }
                    }
                }
                right { stretchy { symbol("∣") } }
            }
        }
    }

    @Test
    fun `test cases`() {
        inflate("\\begin{cases}x&x>0\\\\-x&x<0\\end{cases}") {
            brace {
                left { stretchy3 { symbol("⎧", "⎨", "⎩", "⎪") } }
                content {
                    gridGroup {
                        row {
                            content {
                                child { text { content("x") } }
                                child { space { } }
                                child {
                                    linearGroup {
                                        child { text { content("x") } }
                                        child { symbol { content(">") } }
                                        child { text { content("0") } }
                                    }
                                }
                            }
                        }
                        row {
                            content {
                                child {
                                    linearGroup {
                                        child { symbol { content("−") } }
                                        child { text { content("x") } }
                                    }
                                }
                                child { space { } }
                                child {
                                    linearGroup {
                                        child { text { content("x") } }
                                        child { symbol { content("<") } }
                                        child { text { content("0") } }
                                    }
                                }
                            }
                        }
                    }
                }
                noRight()
            }
        }
    }

    @Test
    fun `test text atom`() {
        inflate("\\text{hello}") {
            text {
                content("hello")
            }
        }
    }

    @Test
    fun `test textbf`() {
        inflate("\\textbf{bold}") {
            linearGroup {
                child {
                    text {
                        content("bold")
                    }
                }
            }
        }
    }

    @Test
    fun `test textit`() {
        inflate("\\textit{italic}") {
            text {
                content("italic")
            }
        }
    }

    @Test
    fun `test mathbf font`() {
        inflate("\\mathbf{x}") {
            linearGroup {
                child {
                    text {
                        content("x")
                    }
                }
            }
        }
    }

    @Test
    fun `test mathit font`() {
        inflate("\\mathit{x}") {
            linearGroup {
                child {
                    text {
                        content("x")
                    }
                }
            }
        }
    }

    @Test
    fun `test punctuation`() {
        inflate(",") {
            symbol { content(",") }
        }
    }

    @Test
    fun `test superscript`() {
        inflate("x^2") {
            decorGroup {
                center { text { content("x") } }
                rightTop { text { content("2") } }
            }
        }
    }

    @Test
    fun `test subscript`() {
        inflate("x_1") {
            decorGroup {
                center { text { content("x") } }
                rightBottom { text { content("1") } }
            }
        }
    }

    @Test
    fun `test superscript and subscript`() {
        inflate("x^2_1") {
            decorGroup {
                center { text { content("x") } }
                rightTop { text { content("2") } }
                rightBottom { text { content("1") } }
            }
        }
    }

    @Test
    fun `test large operator with limits`() {
        inflate("\\sum_{i=1}^{n}") {
            decorGroup {
                bottom {
                    linearGroup {
                        child {
                            text {
                                content("i")
                            }
                        }
                        child {
                            symbol {
                                content("=")
                            }
                        }
                        child {
                            text {
                                content("1")
                            }
                        }
                    }
                }
                top {
                    text {
                        content("n")
                    }
                }
                center {
                    symbol { content("∑") }
                }
            }
        }
    }

    @Test
    fun `test matrix with multiple rows`() {
        inflate("\\begin{pmatrix}1&2&3\\\\4&5&6\\\\7&8&9\\end{pmatrix}") {
            brace {
                left { stretchy { symbol("(") } }
                right { stretchy { symbol(")") } }
            }
        }
    }

    @Test
    fun `test symbol atom`() {
        // Binary Operators
        var symbols = mapOf(
            "+" to "plus",
            "-" to "minus",
            "*" to "multiply",
            "/" to "divide",
            "," to "comma",
            "\\times" to "multiply",
            "\\cdot" to "uni22C5",
            "\\div" to "divide",
            "\\pm" to "plusminus",
            "\\mp" to "minusplus",
            "\\setminus" to "uni2216",
            "\\circ" to "uni2218",
            "\\oplus" to "circleplus",
            "\\ominus" to "uni2296",
            "\\otimes" to "circlemultiply",
            "\\oslash" to "circledivide",
            "\\odot" to "circledot",
            "\\bullet" to "bullet",
            "\\star" to "uni22C6",
            "\\dagger" to "dagger",
            "\\ddagger" to "daggerdbl"
        )

        for ((key, value) in symbols) {
            inflate(key) {
                symbol {
                    content(MathFontOptions.symbol(value))
                }
            }
        }

        // Relations
        symbols = mapOf(
            "=" to "equal",
            "\\neq" to "notequal",
            "\\equiv" to "equivalence",
            "\\approx" to "approxequal",
            "\\cong" to "uni2245",
            "\\sim" to "similar",
            "\\simeq" to "similar_equal",
            "\\asymp" to "uni224D",
            "\\propto" to "proportional",
            "<" to "less",
            ">" to "greater",
            "\\le" to "lessequal",
            "\\ge" to "greaterequal",
            "\\leq" to "lessequal",
            "\\geq" to "greaterequal",
            "\\ll" to "lessmuch",
            "\\gg" to "greatermuch",
            "\\prec" to "uni227A",
            "\\succ" to "uni227B",
            "\\preceq" to "uni227C",
            "\\succeq" to "uni227D",
            "\\perp" to "uni27C2",
            "\\parallel" to "parallel",
            "\\mid" to "divides",
            "|" to "divides",
            "\\nmid" to "uni2224",
            "\\bowtie" to "uni22C8",
            "\\models" to "uni22A8"
        )
        for ((key, value) in symbols) {
            inflate(key) {
                symbol {
                    content(MathFontOptions.symbol(value))
                }
            }
        }

        // Sets and Logic
        symbols = mapOf(
            "\\in" to "element",
            "\\notin" to "uni2209",
            "\\subset" to "propersubset",
            "\\supset" to "propersuperset",
            "\\subseteq" to "reflexsubset",
            "\\supseteq" to "reflexsuperset",
            "\\cup" to "union",
            "\\cap" to "intersection",
            "\\wedge" to "logicaland",
            "\\vee" to "logicalor",
            "\\neg" to "logicalnot",
            "\\forall" to "universal",
            "\\exists" to "existential",
            "\\nexists" to "uni2204",
            "\\emptyset" to "emptyset",
            "\\varnothing" to "emptyset"
        )
        for ((key, value) in symbols) {
            inflate(key) {
                symbol {
                    content(MathFontOptions.symbol(value))
                }
            }
        }

        // Arrows
        symbols = mapOf(
            "\\to" to "arrowright",
            "\\rightarrow" to "arrowright",
            "\\leftarrow" to "arrowleft",
            "\\leftrightarrow" to "arrowboth",
            "\\Rightarrow" to "arrowdblright",
            "\\Leftarrow" to "arrowdblleft",
            "\\Leftrightarrow" to "arrowdblboth",
            "\\implies" to "arrowdblright",
            "\\iff" to "arrowdblboth"
        )
        for ((key, value) in symbols) {
            inflate(key) {
                symbol {
                    content(MathFontOptions.symbol(value))
                }
            }
        }

        // Geometry & Others
        symbols = mapOf(
            "\\triangleleft" to "uni25C1",
            "\\triangleright" to "uni25B7",
            "\\angle" to "uni2220",
            "\\hbar" to "uni210F",
            "\\nabla" to "nabla",
            "\\partial" to "partialdiff",
            "\\ell" to "uni2113",
            "\\wp" to "weierstrass",
            "\\Re" to "Rfraktur",
            "\\Im" to "Ifraktur",
            "\\aleph" to "aleph",
            "\\dots" to "ellipsis",
            "\\ldots" to "ellipsis",
            "\\cdots" to "uni22EF",
            "\\vdots" to "uni22EE",
            "\\ddots" to "uni22F1",
            "\\therefore" to "therefore",
            "\\because" to "because"
        )
        for ((key, value) in symbols) {
            inflate(key) {
                symbol {
                    content(MathFontOptions.symbol(value))
                }
            }
        }
    }
}