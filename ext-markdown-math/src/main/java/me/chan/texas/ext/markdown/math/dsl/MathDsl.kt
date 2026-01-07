package me.chan.texas.ext.markdown.math.dsl;

import me.chan.texas.ext.markdown.math.ast.*
import me.chan.texas.ext.markdown.math.ast.MathParser.getDelimitedLevel

@DslMarker
annotation class MathDslMarker

fun math(block: MathListBuilder.() -> Unit): MathList {
    return MathListBuilder().apply(block).build()
}

@MathDslMarker
class MathListBuilder {
    private val elements = mutableListOf<Ast>()

    // ========== 基础元素 ==========

    /**
     * 添加数字
     */
    fun number(value: String) {
        elements.add(Term(NumberAtom(value), null))
    }

    /**
     * 添加数字（支持 Int 和 Double）
     */
    fun number(value: Number) {
        number(value.toString())
    }

    /**
     * 添加变量
     */
    fun variable(name: String) {
        elements.add(Term(VariableAtom(name), null))
    }

    /**
     * 添加希腊字母
     */
    fun greekLetter(name: String, primeSuffix: String = "") {
        elements.add(Term(GreekLetterVariableAtom(name, primeSuffix), null))
    }

    /**
     * 添加符号
     */
    fun symbol(name: String) {
        elements.add(Term(SymbolAtom(name), null))
    }

    /**
     * 添加特殊字母（如 \ell, \hbar 等）
     */
    fun specialLetter(name: String, primeSuffix: String = "") {
        elements.add(Term(SpecialLetterVariableAtom(name, primeSuffix), null))
    }

    fun term(block: TermBuilder.() -> Unit) {
        elements.add(TermBuilder().apply(block).build())
    }

    // ========== 分组 ==========

    /**
     * 花括号分组 {}
     */
    fun group(block: MathListBuilder.() -> Unit) {
        val content = MathListBuilder().apply(block).build()
        elements.add(Term(Group('{', '}', content), null))
    }

    /**
     * 圆括号分组 ()
     */
    fun parentheses(block: MathListBuilder.() -> Unit) {
        val content = MathListBuilder().apply(block).build()
        elements.add(Term(Group('(', ')', content), null))
    }

    /**
     * 方括号分组 []
     */
    fun brackets(block: MathListBuilder.() -> Unit) {
        val content = MathListBuilder().apply(block).build()
        elements.add(Term(Group('[', ']', content), null))
    }

    // ========== 分数 ==========

    /**
     * 创建分数 \frac{分子}{分母}
     */
    fun frac(command: String = "frac", block: FracBuilder.() -> Unit) {
        elements.add(Term(FracBuilder().apply(block).build(command), null))
    }

    /**
     * 显示样式分数
     */
    fun dfrac(block: FracBuilder.() -> Unit) =

        frac("dfrac", block)

    /**
     * 文本样式分数
     */
    fun tfrac(block: FracBuilder.() -> Unit) =

        frac("tfrac", block)

    /**
     * 连分数
     */
    fun cfrac(block: FracBuilder.() -> Unit) =

        frac("cfrac", block)

    // ========== 二项式系数 ==========

    /**
     * 创建二项式系数 \binom{n}{k}
     */
    fun binom(command: String = "binom", block: BinomBuilder.() -> Unit) {
        elements.add(Term(BinomBuilder().apply(block).build(command), null))
    }

    fun dbinom(block: BinomBuilder.() -> Unit) = binom("dbinom", block)

    fun tbinom(block: BinomBuilder.() -> Unit) = binom("tbinom", block)

    // ========== 根号 ==========

    /**
     * 创建根号 \sqrt{内容}
     */
    fun sqrt(block: SqrtBuilder.() -> Unit) {
        elements.add(Term(SqrtBuilder().apply(block).build(), null))
    }

    // ========== 定界符 ==========

    /**
     * 创建自动大小的定界符 \left...\right
     */
    fun delimited(
        left: String,
        right: String,
        sizeCommand: String = "left",
        block: MathListBuilder.() -> Unit
    ) {
        val content = MathListBuilder().apply(block).build()
        elements.add(
            Term(
                DelimitedAtom(getDelimitedLevel(sizeCommand), left, content, right),
                null
            )
        )
    }

    fun leftRight(left: String, right: String, block: MathListBuilder.() -> Unit) =

        delimited(left, right, "left", block)

    fun bigl(left: String, right: String, block: MathListBuilder.() -> Unit) =

        delimited(left, right, "bigl", block)

    fun Bigl(left: String, right: String, block: MathListBuilder.() -> Unit) =

        delimited(left, right, "Bigl", block)

    fun biggl(left: String, right: String, block: MathListBuilder.() -> Unit) =

        delimited(left, right, "biggl", block)

    fun Biggl(left: String, right: String, block: MathListBuilder.() -> Unit) =

        delimited(left, right, "Biggl", block)

    // ========== 函数调用 ==========

    /**
     * 创建函数调用 \sin, \cos, \log 等
     */
    fun function(name: String) {
        elements.add(Term(FunctionCallAtom(name), null))
    }

    // 常用函数的快捷方法
    fun sin() =

        function("sin")

    fun cos() =

        function("cos")

    fun tan() =

        function("tan")

    fun cot() =

        function("cot")

    fun sec() =

        function("sec")

    fun csc() =

        function("csc")

    fun arcsin() =

        function("arcsin")

    fun arccos() =

        function("arccos")

    fun arctan() =

        function("arctan")

    fun sinh() =

        function("sinh")

    fun cosh() =

        function("cosh")

    fun tanh() =

        function("tanh")

    fun log() =

        function("log")

    fun ln() =

        function("ln")

    fun lg() =

        function("lg")

    fun exp() =

        function("exp")

    fun max() =

        function("max")

    fun min() =

        function("min")

    fun sup() =

        function("sup")

    fun inf() =

        function("inf")

    fun lim() =

        function("lim")

    fun limsup() =

        function("limsup")

    fun liminf() =

        function("liminf")

    fun det() =

        function("det")

    fun gcd() =

        function("gcd")

    // ========== 大型运算符 ==========

    /**
     * 创建大型运算符（求和、积分等）
     */
    fun largeOperator(name: String, block: (LargeOperatorBuilder.() -> Unit)? = null) {
        val builder = LargeOperatorBuilder(name)
        block?.invoke(builder)
        elements.add(builder.build())
    }

    // 常用大型运算符的快捷方法
    fun sum(block: (LargeOperatorBuilder.() -> Unit)? = null) =

        largeOperator("sum", block)

    fun prod(block: (LargeOperatorBuilder.() -> Unit)? = null) =

        largeOperator("prod", block)

    fun coprod(block: (LargeOperatorBuilder.() -> Unit)? = null) =

        largeOperator("coprod", block)

    fun integral(block: (LargeOperatorBuilder.() -> Unit)? = null) =

        largeOperator("int", block)

    fun iint(block: (LargeOperatorBuilder.() -> Unit)? = null) =

        largeOperator("iint", block)

    fun iiint(block: (LargeOperatorBuilder.() -> Unit)? = null) =

        largeOperator("iiint", block)

    fun oint(block: (LargeOperatorBuilder.() -> Unit)? = null) =

        largeOperator("oint", block)

    fun bigcup(block: (LargeOperatorBuilder.() -> Unit)? = null) =

        largeOperator("bigcup", block)

    fun bigcap(block: (LargeOperatorBuilder.() -> Unit)? = null) =

        largeOperator("bigcap", block)

    // ========== 矩阵 ==========

    /**
     * 创建矩阵
     */
    fun matrix(env: String, gravity: String? = null, block: MatrixBuilder.() -> Unit) {
        elements.add(Term(MatrixBuilder().apply(block).build(env, gravity), null))
    }

    // 常用矩阵类型的快捷方法
    fun pmatrix(block: MatrixBuilder.() -> Unit) =

        matrix("pmatrix", null, block)

    fun bmatrix(block: MatrixBuilder.() -> Unit) =

        matrix("bmatrix", null, block)

    fun Bmatrix(block: MatrixBuilder.() -> Unit) =

        matrix("Bmatrix", null, block)

    fun vmatrix(block: MatrixBuilder.() -> Unit) =

        matrix("vmatrix", null, block)

    fun Vmatrix(block: MatrixBuilder.() -> Unit) =

        matrix("Vmatrix", null, block)

    fun smallmatrix(block: MatrixBuilder.() -> Unit) =

        matrix("smallmatrix", null, block)

    fun cases(block: MatrixBuilder.() -> Unit) =

        matrix("cases", null, block)

    fun array(gravity: String, block: MatrixBuilder.() -> Unit) =

        matrix("array", gravity, block)

    // ========== 文本 ==========

    /**
     * 创建文本 \text{文本内容}
     */
    fun text(content: String, command: String = "text") {
        elements.add(Term(TextAtom(command, content), null))
    }

    fun textbf(content: String) =

        text("textbf", content)

    fun textit(content: String) =

        text("textit", content)

    fun textrm(content: String) =

        text("textrm", content)

    fun mbox(content: String) =

        text("mbox", content)

    fun textfield(content: String) =

        text("textfield", content)

    // ========== 重音符号 ==========

    /**
     * 创建重音 \hat{x}, \bar{x} 等
     */
    fun accent(command: String, block: MathListBuilder.() -> Unit) {
        val content = MathListBuilder().apply(block).build()
        elements.add(Term(AccentAtom(command, content), null))
    }

    fun hat(block: MathListBuilder.() -> Unit) =

        accent("hat", block)

    fun widehat(block: MathListBuilder.() -> Unit) =

        accent("widehat", block)

    fun bar(block: MathListBuilder.() -> Unit) =

        accent("bar", block)

    fun overline(block: MathListBuilder.() -> Unit) =

        accent("overline", block)

    fun underline(block: MathListBuilder.() -> Unit) =

        accent("underline", block)

    fun vec(block: MathListBuilder.() -> Unit) =

        accent("vec", block)

    fun tilde(block: MathListBuilder.() -> Unit) =

        accent("tilde", block)

    fun widetilde(block: MathListBuilder.() -> Unit) =

        accent("widetilde", block)

    fun dot(block: MathListBuilder.() -> Unit) =

        accent("dot", block)

    fun ddot(block: MathListBuilder.() -> Unit) =

        accent("ddot", block)

    fun dddot(block: MathListBuilder.() -> Unit) =

        accent("dddot", block)

    fun overrightarrow(block: MathListBuilder.() -> Unit) =

        accent("overrightarrow", block)

    fun overleftarrow(block: MathListBuilder.() -> Unit) =

        accent("overleftarrow", block)

    fun overbrace(block: MathListBuilder.() -> Unit) =

        accent("overbrace", block)

    fun underbrace(block: MathListBuilder.() -> Unit) =

        accent("underbrace", block)

    // ========== 字体命令 ==========

    /**
     * 创建字体命令 \mathbf{x}, \mathbb{R} 等
     */
    fun font(command: String, block: MathListBuilder.() -> Unit) {
        val content = MathListBuilder().apply(block).build()
        elements.add(Term(FontAtom(command, content), null))
    }

    fun mathrm(block: MathListBuilder.() -> Unit) =

        font("mathrm", block)

    fun mathit(block: MathListBuilder.() -> Unit) =

        font("mathit", block)

    fun mathbf(block: MathListBuilder.() -> Unit) =

        font("mathbf", block)

    fun mathsf(block: MathListBuilder.() -> Unit) =

        font("mathsf", block)

    fun mathtt(block: MathListBuilder.() -> Unit) =

        font("mathtt", block)

    fun mathcal(block: MathListBuilder.() -> Unit) =

        font("mathcal", block)

    fun mathbb(block: MathListBuilder.() -> Unit) =

        font("mathbb", block)

    fun mathfrak(block: MathListBuilder.() -> Unit) =

        font("mathfrak", block)

    fun mathscr(block: MathListBuilder.() -> Unit) =

        font("mathscr", block)

    fun boldsymbol(block: MathListBuilder.() -> Unit) =

        font("boldsymbol", block)

    fun bm(block: MathListBuilder.() -> Unit) =

        font("bm", block)

    // ========== 可扩展箭头 ==========

    /**
     * 创建可扩展箭头
     */
    fun extensibleArrow(command: String, block: ExtensibleArrowBuilder.() -> Unit) {
        elements.add(Term(ExtensibleArrowBuilder().apply(block).build(command), null))
    }

    fun xrightarrow(block: ExtensibleArrowBuilder.() -> Unit) =

        extensibleArrow("xrightarrow", block)

    fun xleftarrow(block: ExtensibleArrowBuilder.() -> Unit) =

        extensibleArrow("xleftarrow", block)

    fun xleftrightarrow(block: ExtensibleArrowBuilder.() -> Unit) =

        extensibleArrow("xleftrightarrow", block)

    fun xRightarrow(block: ExtensibleArrowBuilder.() -> Unit) =

        extensibleArrow("xRightarrow", block)

    fun xLeftarrow(block: ExtensibleArrowBuilder.() -> Unit) =

        extensibleArrow("xLeftarrow", block)

    fun xLeftrightarrow(block: ExtensibleArrowBuilder.() -> Unit) =

        extensibleArrow("xLeftrightarrow", block)

    fun xhookrightarrow(block: ExtensibleArrowBuilder.() -> Unit) =

        extensibleArrow("xhookrightarrow", block)

    fun xhookleftarrow(block: ExtensibleArrowBuilder.() -> Unit) =

        extensibleArrow("xhookleftarrow", block)

    fun xmapsto(block: ExtensibleArrowBuilder.() -> Unit) =

        extensibleArrow("xmapsto", block)

    // ========== 间距 ==========

    fun thinSpace() = elements.add(

        Spacing(",")
    )

    fun mediumSpace() = elements.add(

        Spacing(":")
    )

    fun thickSpace() = elements.add(

        Spacing(";")
    )

    fun negativeSpace() = elements.add(

        Spacing("!")
    )

    fun quad() = elements.add(

        Spacing("quad")
    )

    fun qquad() = elements.add(

        Spacing("qquad")
    )

    // ========== 原始元素添加 ==========

    /**
     * 直接添加 Ast 元素（用于高级场景）
     */
    fun add(element: Ast) {
        elements.add(element)
    }

    /**
     * 添加已构建的 MathList
     */
    fun add(mathList: MathList) {
        elements.addAll(mathList.elements)
    }

    internal fun

            build():

            MathList {
        return MathList(elements)
    }
}

// ========================================
// 辅助构建器类
// ========================================

/**
 * Term 构建器（支持上下标）
 */
@MathDslMarker
class TermBuilder {
    private var atom: Atom? = null
    private var sup: ScriptArg? = null
    private var sub: ScriptArg? = null
    private var reverse = false

    /**
     * 设置原子
     */
    fun atom(block: AtomBuilder.() -> Unit) {
        atom = AtomBuilder().apply(block).build()
    }

    /**
     * 设置上标
     */
    fun superscript(block: MathListBuilder.() -> Unit) {
        sup = ScriptArg(MathListBuilder().apply(block).build())
    }

    /**
     * 设置下标
     */
    fun subscript(block: MathListBuilder.() -> Unit) {
        sub = ScriptArg(MathListBuilder().apply(block).build())
    }

    internal fun

            build():

            Term {
        val finalAtom = atom ?: throw IllegalStateException("Term must have an atom")
        val suffix = if (sup != null || sub != null) {
            SupSubSuffix(sup, sub, reverse)
        } else null
        return Term(finalAtom, suffix)
    }
}

/**
 * Atom 构建器（用于在 term 中构建单个原子）
 */
@MathDslMarker
class AtomBuilder {
    private var atom: Atom? = null

    fun number(value: String) {
        atom = NumberAtom(value)
    }

    fun number(value: Number) {
        number(value.toString())
    }

    fun variable(name: String) {
        atom = VariableAtom(name)
    }

    fun greekLetter(name: String, primeSuffix: String = "") {
        atom = GreekLetterVariableAtom(name, primeSuffix)
    }

    fun symbol(name: String) {
        atom = SymbolAtom(name)
    }

    fun group(block: MathListBuilder.() -> Unit) {
        val content = MathListBuilder().apply(block).build()
        atom = Group('{', '}', content)
    }

    fun frac(command: String = "frac", block: FracBuilder.() -> Unit) {
        atom = FracBuilder().apply(block).build(command)
    }

    fun sqrt(block: SqrtBuilder.() -> Unit) {
        atom = SqrtBuilder().apply(block).build()
    }

    internal fun

            build():

            Atom {
        return atom ?: throw IllegalStateException("Atom must be set")
    }
}

/**
 * 分数构建器
 */
@MathDslMarker
class FracBuilder {
    private var numerator: MathList? = null
    private var denominator: MathList? = null

    fun numerator(block: MathListBuilder.() -> Unit) {
        numerator = MathListBuilder().apply(block).build()
    }

    fun denominator(block: MathListBuilder.() -> Unit) {
        denominator = MathListBuilder().apply(block).build()
    }

    internal fun

            build(command: String):

            FracAtom {
        return FracAtom(
            command,
            numerator ?: throw IllegalStateException("Numerator is required"),
            denominator ?: throw IllegalStateException("Denominator is required")
        )
    }
}

/**
 * 二项式系数构建器
 */
@MathDslMarker
class BinomBuilder {
    private var top: MathList? = null
    private var bottom: MathList? = null

    fun top(block: MathListBuilder.() -> Unit) {
        top = MathListBuilder().apply(block).build()
    }

    fun bottom(block: MathListBuilder.() -> Unit) {
        bottom = MathListBuilder().apply(block).build()
    }

    internal fun

            build(command: String):

            BinomAtom {
        return BinomAtom(
            command,
            top ?: throw IllegalStateException("Top is required"),
            bottom ?: throw IllegalStateException("Bottom is required")
        )
    }
}

/**
 * 根号构建器
 */
@MathDslMarker
class SqrtBuilder {
    private var content: MathList? = null
    private var index: MathList? = null

    fun content(block: MathListBuilder.() -> Unit) {
        content = MathListBuilder().apply(block).build()
    }

    fun index(block: MathListBuilder.() -> Unit) {
        index = MathListBuilder().apply(block).build()
    }

    internal fun

            build():

            SqrtAtom {
        return SqrtAtom(
            content ?: throw IllegalStateException("Content is required"),
            index
        )
    }
}

/**
 * 大型运算符构建器
 */
@MathDslMarker
class LargeOperatorBuilder(private val name: String) {
    private var sup: ScriptArg? = null
    private var sub: ScriptArg? = null

    fun superscript(block: MathListBuilder.() -> Unit) {
        sup = ScriptArg(MathListBuilder().apply(block).build())
    }

    fun subscript(block: MathListBuilder.() -> Unit) {
        sub = ScriptArg(MathListBuilder().apply(block).build())
    }

    internal fun

            build():

            Term {
        val atom = LargeOperatorAtom(name)
        val suffix = if (sup != null || sub != null) {
            SupSubSuffix(sup, sub, false)
        } else null
        return Term(atom, suffix)
    }
}

/**
 * 矩阵构建器
 */
@MathDslMarker
class MatrixBuilder {
    private val rows = mutableListOf<MatrixRow>()

    fun row(block: MatrixRowBuilder.() -> Unit) {
        rows.add(MatrixRowBuilder().apply(block).build())
    }

    internal fun

            build(env: String, gravity: String?):

            MatrixAtom {
        return MatrixAtom(env, gravity, rows)
    }
}

/**
 * 矩阵行构建器
 */
@MathDslMarker
class MatrixRowBuilder {
    private val cells = mutableListOf<MathList>()

    fun cell(block: MathListBuilder.() -> Unit) {
        cells.add(MathListBuilder().apply(block).build())
    }

    internal fun

            build():

            MatrixRow {
        return MatrixRow(cells)
    }
}

/**
 * 可扩展箭头构建器
 */
@MathDslMarker
class ExtensibleArrowBuilder {
    private var above: MathList? = null
    private var below: MathList? = null

    fun above(block: MathListBuilder.() -> Unit) {
        above = MathListBuilder().apply(block).build()
    }

    fun below(block: MathListBuilder.() -> Unit) {
        below = MathListBuilder().apply(block).build()
    }

    internal fun

            build(command: String):

            ExtensibleArrowAtom {
        return ExtensibleArrowAtom(
            command,
            above ?: throw IllegalStateException("Above content is required"),
            below
        )
    }
}

// ========================================
// 扩展函数 - 便利方法
// ========================================

/**
 * 快捷创建简单的数字项（带上下标）
 */
fun MathListBuilder.numberWithScript(
    value: String,
    sup: String? = null,
    sub: String? = null
) {
    term {
        atom {
            number(value)
        }
        sup?.let {
            superscript {
                text(it)
            }
        }
        sub?.let {
            subscript {
                text(it)
            }
        }
    }
}

/**
 * 快捷创建简单的变量项（带上下标）
 */
fun MathListBuilder.variableWithScript(
    name: String,
    sup: String? = null,
    sub: String? = null
) {
    term {
        atom {
            variable(name)
        }
        sup?.let {
            superscript {
                text(it)
            }
        }
        sub?.let {
            subscript {
                text(it)
            }
        }
    }
}

/**
 * 创建 n 次根号 \sqrt[n]{x}
 */
fun MathListBuilder.nthRoot(n: Int, block: MathListBuilder.() -> Unit) {

    sqrt {
        index {
            number(n)
        }
        content(block)
    }
}

/**
 * 创建绝对值 |x|
 */
fun MathListBuilder.abs(block: MathListBuilder.() -> Unit) {

    leftRight("|", "|", block)
}

/**
 * 创建范数 ||x||
 */
fun MathListBuilder.norm(block: MathListBuilder.() -> Unit) {

    leftRight("\\|", "\\|", block)
}

/**
 * 创建尖括号 ⟨x⟩
 */
fun MathListBuilder.angleBrackets(block: MathListBuilder.() -> Unit) {

    leftRight("\\langle", "\\rangle", block)
}

/**
 * 创建简单分数（字符串参数）
 */
fun MathListBuilder.simpleFrac(num: String, denom: String) {
    frac {
        numerator {
            text(num)
        }
        denominator {
            text(denom)
        }
    }
}

/**
 * 创建下标表达式（快捷方法）
 */
fun MathListBuilder.sub(base: String, subscript: String) {
    term {
        atom {
            variable(base)
        }
        subscript {
            text(content = subscript)
        }
    }
}

/**
 * 创建上标表达式（快捷方法）
 */
fun MathListBuilder.sup(base: String, superscript: String) {
    term {
        atom {
            variable(base)
        }
        superscript {
            text(content = superscript)
        }
    }
}

/**
 * 添加多个变量（用空格分隔的字符串）
 */
fun MathListBuilder.variables(names: String) {
    names.split(" ").forEach {
        if (it.isNotEmpty()) variable(it)
    }
}

// ========================================
// 常用符号便利方法
// ========================================

fun MathListBuilder.plus() =

    symbol("+")

fun MathListBuilder.minus() =

    symbol("-")

fun MathListBuilder.times() =

    symbol("times")

fun MathListBuilder.cdot() =

    symbol("cdot")

fun MathListBuilder.div() =

    symbol("div")

fun MathListBuilder.pm() =

    symbol("pm")

fun MathListBuilder.mp() =

    symbol("mp")

fun MathListBuilder.equals() =

    symbol("=")

fun MathListBuilder.neq() =

    symbol("neq")

fun MathListBuilder.leq() =

    symbol("le")

fun MathListBuilder.geq() =

    symbol("ge")

fun MathListBuilder.lt() =

    symbol("<")

fun MathListBuilder.gt() =

    symbol(">")

fun MathListBuilder.ll() =

    symbol("ll")

fun MathListBuilder.gg() =

    symbol("gg")

fun MathListBuilder.approx() =

    symbol("approx")

fun MathListBuilder.equiv() =

    symbol("equiv")

fun MathListBuilder.cong() =

    symbol("cong")

fun MathListBuilder.sim() =

    symbol("sim")

fun MathListBuilder.propto() =

    symbol("propto")

fun MathListBuilder.inSymbol() =

    symbol("in")

fun MathListBuilder.notin() =

    symbol("notin")

fun MathListBuilder.subset() =

    symbol("subset")

fun MathListBuilder.supset() =

    symbol("supset")

fun MathListBuilder.subseteq() =

    symbol("subseteq")

fun MathListBuilder.supseteq() =

    symbol("supseteq")

fun MathListBuilder.cup() =

    symbol("cup")

fun MathListBuilder.cap() =

    symbol("cap")

fun MathListBuilder.wedge() =

    symbol("wedge")

fun MathListBuilder.vee() =

    symbol("vee")

fun MathListBuilder.to() =

    symbol("to")

fun MathListBuilder.rightarrow() =

    symbol("rightarrow")

fun MathListBuilder.leftarrow() =

    symbol("leftarrow")

fun MathListBuilder.leftrightarrow() =

    symbol("leftrightarrow")

fun MathListBuilder.Rightarrow() =

    symbol("Rightarrow")

fun MathListBuilder.Leftarrow() =

    symbol("Leftarrow")

fun MathListBuilder.Leftrightarrow() =

    symbol("Leftrightarrow")

fun MathListBuilder.implies() =

    symbol("implies")

fun MathListBuilder.iff() =

    symbol("iff")

fun MathListBuilder.forall() =

    symbol("forall")

fun MathListBuilder.exists() =

    symbol("exists")

fun MathListBuilder.nexists() =

    symbol("nexists")

fun MathListBuilder.emptyset() =

    symbol("emptyset")

fun MathListBuilder.varnothing() =

    symbol("varnothing")

fun MathListBuilder.perp() =

    symbol("perp")

fun MathListBuilder.parallel() =

    symbol("parallel")

fun MathListBuilder.angle() =

    symbol("angle")

fun MathListBuilder.dots() =

    symbol("dots")

fun MathListBuilder.ldots() =

    symbol("ldots")

fun MathListBuilder.cdots() =

    symbol("cdots")

fun MathListBuilder.vdots() =

    symbol("vdots")

fun MathListBuilder.ddots() =

    symbol("ddots")

fun MathListBuilder.nabla() =

    symbol("nabla")

fun MathListBuilder.partial() =

    symbol("partial")

// ========================================
// 常用希腊字母便利方法
// ========================================

fun MathListBuilder.alpha() =

    greekLetter("alpha")

fun MathListBuilder.beta() =

    greekLetter("beta")

fun MathListBuilder.gamma() =

    greekLetter("gamma")

fun MathListBuilder.delta() =

    greekLetter("delta")

fun MathListBuilder.epsilon() =

    greekLetter("epsilon")

fun MathListBuilder.varepsilon() =

    greekLetter("varepsilon")

fun MathListBuilder.zeta() =

    greekLetter("zeta")

fun MathListBuilder.eta() =

    greekLetter("eta")

fun MathListBuilder.theta() =

    greekLetter("theta")

fun MathListBuilder.vartheta() =

    greekLetter("vartheta")

fun MathListBuilder.iota() =

    greekLetter("iota")

fun MathListBuilder.kappa() =

    greekLetter("kappa")

fun MathListBuilder.lambda() =

    greekLetter("lambda")

fun MathListBuilder.mu() =

    greekLetter("mu")

fun MathListBuilder.nu() =

    greekLetter("nu")

fun MathListBuilder.xi() =

    greekLetter("xi")

fun MathListBuilder.pi() =

    greekLetter("pi")

fun MathListBuilder.varpi() =

    greekLetter("varpi")

fun MathListBuilder.rho() =

    greekLetter("rho")

fun MathListBuilder.varrho() =

    greekLetter("varrho")

fun MathListBuilder.sigma() =

    greekLetter("sigma")

fun MathListBuilder.varsigma() =

    greekLetter("varsigma")

fun MathListBuilder.tau() =

    greekLetter("tau")

fun MathListBuilder.upsilon() =

    greekLetter("upsilon")

fun MathListBuilder.phi() =

    greekLetter("phi")

fun MathListBuilder.varphi() =

    greekLetter("varphi")

fun MathListBuilder.chi() =

    greekLetter("chi")

fun MathListBuilder.psi() =

    greekLetter("psi")

fun MathListBuilder.omega() =

    greekLetter("omega")

fun MathListBuilder.Gamma() =

    greekLetter("Gamma")

fun MathListBuilder.Delta() =

    greekLetter("Delta")

fun MathListBuilder.Theta() =

    greekLetter("Theta")

fun MathListBuilder.Lambda() =

    greekLetter("Lambda")

fun MathListBuilder.Xi() =

    greekLetter("Xi")

fun MathListBuilder.Pi() =

    greekLetter("Pi")

fun MathListBuilder.Sigma() =

    greekLetter("Sigma")

fun MathListBuilder.Upsilon() =

    greekLetter("Upsilon")

fun MathListBuilder.Phi() =

    greekLetter("Phi")

fun MathListBuilder.Psi() =

    greekLetter("Psi")

fun MathListBuilder.Omega() =

    greekLetter("Omega")

fun MathListBuilder.infty() =

    greekLetter("infty")

// ========================================
// 使用示例
// ========================================

/**
 * DSL 使用示例集合
 */
object MathDslExamples {

    /**
     * 示例 1: 简单表达式 2 + x
     */
    fun simple() =

        math {
            number(2)
            plus()
            variable("x")
        }

    /**
     * 示例 2: 带上下标 x^2 + y_1
     */
    fun withScripts() =

        math {
            term {
                atom {
                    variable("x")
                }
                superscript {
                    number(2)
                }
            }
            plus()
            term {
                atom {
                    variable("y")
                }
                subscript {
                    number(1)
                }
            }
        }

    /**
     * 示例 3: 二次公式
     */
    fun quadraticFormula() =

        math {
            variable("x")
            equals()
            frac {
                numerator {
                    minus()
                    variable("b")
                    pm()
                    sqrt {
                        content {
                            term {
                                atom {
                                    variable("b")
                                }
                                superscript {
                                    number(2)
                                }
                            }
                            minus()
                            number(4)
                            variable("a")
                            variable("c")
                        }
                    }
                }
                denominator {
                    number(2)
                    variable("a")
                }
            }
        }

    /**
     * 示例 4: 求和公式
     */
    fun sumFormula() =

        math {
            sum {
                subscript {
                    variable("i")
                    equals()
                    number(1)
                }
                superscript {
                    variable("n")
                }
            }
            term {
                atom {
                    variable("i")
                }
                superscript {
                    number(2)
                }
            }
        }

    /**
     * 示例 5: 矩阵
     */
    fun matrix() =

        math {
            pmatrix {
                row {
                    cell {
                        number(1)
                    }
                    cell {
                        number(2)
                    }
                }
                row {
                    cell {
                        number(3)
                    }
                    cell {
                        number(4)
                    }
                }
            }
        }

    /**
     * 示例 6: 积分
     */
    fun integral() =

        math {
            integral {
                subscript {
                    number(0)
                }
                superscript {
                    number(1)
                }
            }
            term {
                atom {
                    variable("x")
                }
                superscript {
                    number(2)
                }
            }
            variable("d")
            variable("x")
        }

    /**
     * 示例 7: 欧拉公式
     */
    fun euler() =

        math {
            term {
                atom {
                    variable("e")
                }
                superscript {
                    variable("i")
                    pi()
                }
            }
            plus()
            number(1)
            equals()
            number(0)
        }

    /**
     * 示例 8: 分段函数
     */
    fun piecewise() =

        math {
            variable("f")
            parentheses {
                variable("x")
            }
            equals()
            cases {
                row {
                    cell {
                        variable("x")
                        plus()
                        number(1)
                    }
                    cell {
                        text(content = "if ")
                        variable("x")
                        geq()
                        number(0)
                    }
                }
                row {
                    cell {
                        minus()
                        variable("x")
                    }
                    cell {
                        text(content = "if ")
                        variable("x")
                        lt()
                        number(0)
                    }
                }
            }
        }

    /**
     * 示例 9: 向量与范数
     */
    fun vectorNorm() =

        math {
            norm {
                vec {
                    variable("x")
                }
            }
            equals()
            sqrt {
                content {
                    sum {
                        subscript {
                            variable("i")
                        }
                    }
                    term {
                        atom {
                            variable("x")
                        }
                        subscript {
                            variable("i")
                        }
                        superscript {
                            number(2)
                        }
                    }
                }
            }
        }

    /**
     * 示例 10: 可扩展箭头
     */
    fun extensibleArrow() =

        math {
            variable("A")
            xrightarrow {
                above {
                    variable("f")
                }
                below {
                    text(content = "iso")
                }
            }
            variable("B")
        }
}