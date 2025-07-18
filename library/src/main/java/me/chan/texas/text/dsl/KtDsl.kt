package me.chan.texas.text.dsl

import me.chan.texas.TexasOption
import me.chan.texas.misc.RectF
import me.chan.texas.renderer.RendererContext
import me.chan.texas.renderer.TexasView.DocumentSource
import me.chan.texas.renderer.core.graphics.TexasCanvas
import me.chan.texas.renderer.core.graphics.TexasPaint
import me.chan.texas.renderer.ui.text.ParagraphView.ParagraphSource
import me.chan.texas.text.Appearance
import me.chan.texas.text.Document
import me.chan.texas.text.HypeSpan
import me.chan.texas.text.Measurable
import me.chan.texas.text.Paragraph
import me.chan.texas.text.TextStyle
import me.chan.texas.text.layout.StateList

internal class HypeSpanDsl(
    private val _para: Paragraph.Builder,
    private val _onMeasure: (measurable: Measurable, lineHeight: Float, baselineOffset: Float) -> Unit,
    block: (canvas: TexasCanvas, paint: TexasPaint, inner: RectF, outer: RectF, baselineOffset: Float, states: StateList) -> Unit
) {
    private val _span = object : HypeSpan() {
        override fun onDraw(
            canvas: TexasCanvas,
            paint: TexasPaint,
            inner: RectF,
            outer: RectF,
            baselineOffset: Float,
            states: StateList
        ) {
            block(canvas, paint, inner, outer, baselineOffset, states)
        }

        override fun onMeasure(lineHeight: Float, baselineOffset: Float) {
            _onMeasure(this, lineHeight, baselineOffset)
        }
    }

    fun tag(tag: Any?) {
        _span.setTag(tag)
    }

    internal fun build() {
        _para.hypeSpan(_span)
    }
}

class SpanDsl(private val _span: Paragraph.SpanBuilder) {
    fun tag(tag: Any?) {
        _span.tag(tag)
    }

    fun style(block: (textPaint: TexasPaint, tag: Any?) -> Unit) {
        _span.setTextStyle(object : TextStyle() {
            override fun update(textPaint: TexasPaint, tag: Any?) {
                block(textPaint, tag)
            }
        })
    }

    fun background(block: (canvas: TexasCanvas, paint: TexasPaint, inner: RectF, outer: RectF, context: RendererContext) -> Unit) {
        _span.setBackground(object : Appearance() {
            override fun draw(
                canvas: TexasCanvas,
                paint: TexasPaint,
                inner: RectF,
                outer: RectF,
                context: RendererContext
            ) {
                block(canvas, paint, inner, outer, context)
            }
        })
    }

    fun foreground(block: (canvas: TexasCanvas, paint: TexasPaint, inner: RectF, outer: RectF, context: RendererContext) -> Unit) {
        _span.setForeground(object : Appearance() {
            override fun draw(
                canvas: TexasCanvas,
                paint: TexasPaint,
                inner: RectF,
                outer: RectF,
                context: RendererContext
            ) {
                block(canvas, paint, inner, outer, context)
            }
        })
    }

    internal fun build() {
        _span.buildSpan()
    }
}

interface Size {

    companion object : Size {}
}

fun Size.fixed(
    width: Float,
    height: Float
): (measurable: Measurable, lineHeight: Float, baselineOffset: Float) -> Unit {
    return { measureable, lineHeight, baselineOffset ->
        measureable.setMeasuredSize(width, height)
    }
}

class ParaDsl(option: TexasOption, typesetPolicy: Int) {
    private val _para = Paragraph.Builder.newBuilder(option)
        .setTypesetPolicy(typesetPolicy)

    fun span(
        text: CharSequence,
        start: Int = 0,
        end: Int = text.length,
        block: (SpanDsl.() -> Unit)? = null
    ) {
        val span = SpanDsl(
            _para.newSpanBuilder()
                .next(text, start, end)
        )
        block?.let { span.it() }
        span.build()
    }

    fun hypeSpan(
        width: Float,
        height: Float,
        block: (canvas: TexasCanvas, paint: TexasPaint, inner: RectF, outer: RectF, baselineOffset: Float, states: StateList) -> Unit
    ) {
        hypeSpan(onMeasure = Size.fixed(width, height), block = block)
    }

    fun hypeSpan(
        tag: Any? = null,
        onMeasure: (measurable: Measurable, lineHeight: Float, baselineOffset: Float) -> Unit,
        block: (canvas: TexasCanvas, paint: TexasPaint, inner: RectF, outer: RectF, baselineOffset: Float, states: StateList) -> Unit
    ) {
        val span = HypeSpanDsl(_para, onMeasure, block)
        span.tag(tag)
        span.build()
    }

    // build 函数只有当前文件可见
    internal fun build(): Paragraph {
        return _para.build()
    }
}

class DocumentDsl(private val _option: TexasOption, private val _prevDoc: Document? = null) {
    private val _document = Document.Builder(_prevDoc)

    internal fun build(): Document {
        return _document.build()
    }

    fun para(
        typesetPolicy: Int = Paragraph.TYPESET_POLICY_ACCEPT_CONTROL_CHAR,
        block: ParaDsl.() -> Unit
    ) {
        val para = ParaDsl(_option, typesetPolicy)
        para.block()
        _document.addSegment(para.build())
    }
}

fun DocumentSource.document(
    option: TexasOption,
    prevDoc: Document?,
    block: DocumentDsl.() -> Unit
): Document {
    val doc = DocumentDsl(option, prevDoc)
    doc.block()
    return doc.build()
}

fun ParagraphSource.para(
    option: TexasOption,
    typesetPolicy: Int = Paragraph.TYPESET_POLICY_ACCEPT_CONTROL_CHAR,
    block: ParaDsl.() -> Unit
): Paragraph {
    val para = ParaDsl(option, typesetPolicy)
    para.block()
    return para.build()
}