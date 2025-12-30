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
        inflate("\\alpha") {
            text {
                content("α")
            }
        }
    }

    @Test
    fun `test symbol atom`() {
        inflate("+") {
            symbol {
                content(MathFontOptions.symbol("plus"))
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
    fun `test function call`() {
        inflate("\\sin") {
            linearGroup {
                child {
                    text {
                        content("sin")
                    }
                }
            }
        }
    }

    @Test
    fun `test large operator sum`() {
        inflate("\\sum") {
            symbol {
                content(MathFontOptions.symbol("summation"))
            }
        }
    }

    @Test
    fun `test large operator prod`() {
        inflate("\\prod") {
            symbol {
                content(MathFontOptions.symbol("product"))
            }
        }
    }

    @Test
    fun `test large operator int`() {
        inflate("\\int") {
            symbol {
                content(MathFontOptions.symbol("integral"))
            }
        }
    }

    @Test
    fun `test large operator lim`() {
        inflate("\\lim") {
            text {
                content("lim")
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

    // ========== Superscript and Subscript Tests ==========

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

    // ========== Group Tests ==========

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

    // ========== Delimiter Tests ==========

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
            brace {}
        }
    }

    @Test
    fun `test left right with floor`() {
        inflate("\\left\\lfloor x\\right\\rfloor") {
            brace {}
        }
    }

    @Test
    fun `test left right with ceil`() {
        inflate("\\left\\lceil x\\right\\rceil") {
            brace {}
        }
    }

    @Test
    fun `test left right with vert`() {
        inflate("\\left\\lvert x\\right\\rvert") {
            brace {}
        }
    }

    @Test
    fun `test left right with Vert`() {
        inflate("\\left\\lVert x\\right\\rVert") {
            brace {}
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

    // ========== Complex Tests ==========

    @Test
    fun `test complex expression`() {
        inflate("\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}") {
            fraction {}
        }
    }

    @Test
    fun `test nested fraction`() {
        inflate("\\frac{1}{\\frac{1}{x}}") {
            fraction {}
        }
    }

    @Test
    fun `test matrix with multiple rows`() {
        inflate("\\begin{pmatrix}1&2&3\\\\4&5&6\\\\7&8&9\\end{pmatrix}") {
            brace {}
        }
    }
}