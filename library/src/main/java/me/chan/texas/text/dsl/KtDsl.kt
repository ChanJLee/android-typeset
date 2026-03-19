package me.chan.texas.text.dsl

import android.view.View
import me.chan.texas.TexasOption
import me.chan.texas.misc.RectF
import me.chan.texas.renderer.RendererContext
import me.chan.texas.renderer.TexasView.DocumentSource
import me.chan.texas.renderer.core.graphics.TexasCanvas
import me.chan.texas.renderer.core.graphics.TexasPaint
import me.chan.texas.renderer.ui.text.ParagraphView.ParagraphSource
import me.chan.texas.text.Appearance
import me.chan.texas.text.Document
import me.chan.texas.text.HyperSpan
import me.chan.texas.text.Measurable
import me.chan.texas.text.Paragraph
import me.chan.texas.text.TextStyle
import me.chan.texas.text.ViewSegment
import me.chan.texas.text.layout.StateList
import me.chan.texas.text.layout.TextBox

@DslMarker
annotation class TexasDslMarker

@TexasDslMarker
internal class HyperSpanDsl(
    private val _para: Paragraph.Builder,
    private val _onMeasure: Measurable.(lineHeight: Float, baselineOffset: Float) -> Unit,
    onDraw: TexasCanvas.(paint: TexasPaint, inner: RectF, outer: RectF, baselineOffset: Float, states: StateList) -> Unit
) {
    private val _span = object : HyperSpan() {
        override fun onDraw(
            canvas: TexasCanvas,
            paint: TexasPaint,
            inner: RectF,
            outer: RectF,
            baselineOffset: Float,
            states: StateList
        ) {
            canvas.onDraw(paint, inner, outer, baselineOffset, states)
        }

        override fun onMeasure(lineHeight: Float, baselineOffset: Float) {
            _onMeasure(this, lineHeight, baselineOffset)
        }
    }

    fun tag(tag: Any?) {
        _span.setTag(tag)
    }

    internal fun build() {
        _para.hyperSpan(_span)
    }
}

@TexasDslMarker
class SpanDsl(private val _span: Paragraph.SpanBuilder) {
    fun tag(tag: Any?) {
        _span.tag(tag)
    }

    fun style(block: TexasPaint.(box: TextBox) -> Unit) {
        _span.setTextStyle(object : TextStyle() {
            override fun update(textPaint: TexasPaint, box: TextBox) {
                textPaint.block(box)
            }
        })
    }

    fun background(block: TexasCanvas.(paint: TexasPaint, inner: RectF, outer: RectF, context: RendererContext) -> Unit) {
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

    fun foreground(block: TexasCanvas.(paint: TexasPaint, inner: RectF, outer: RectF, context: RendererContext) -> Unit) {
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

interface SpanSize {

    companion object : SpanSize {}
}

fun SpanSize.fixed(
    width: Float,
    height: Float
): Measurable.(lineHeight: Float, baselineOffset: Float) -> Unit {
    return { lineHeight, baselineOffset ->
        setMeasuredSize(width, height)
    }
}

/**
 * 计算宽高比
 */
fun SpanSize.ratio(
    ratio: Float /* width / height = ratio */,
    excludeBaselineOffset: Boolean = true
): Measurable.(lineHeight: Float, baselineOffset: Float) -> Unit {
    return { lineHeight, baselineOffset ->
        val height = if (excludeBaselineOffset) lineHeight - baselineOffset else lineHeight
        setMeasuredSize(height * ratio, height)
    }
}

fun SpanSize.ratio(
    width: Float,
    height: Float,
    excludeBaselineOffset: Boolean = true
): Measurable.(lineHeight: Float, baselineOffset: Float) -> Unit {
    return ratio(width / height, excludeBaselineOffset)
}

@TexasDslMarker
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

    fun hyperSpan(
        onMeasure: Measurable.(lineHeight: Float, baselineOffset: Float) -> Unit,
        tag: Any? = null,
        onDraw: TexasCanvas.(paint: TexasPaint, inner: RectF, outer: RectF, baselineOffset: Float, states: StateList) -> Unit
    ) {
        val span = HyperSpanDsl(_para, onMeasure, onDraw)
        span.tag(tag)
        span.build()
    }

    // build 函数只有当前文件可见
    internal fun build(): Paragraph {
        return _para.build()
    }
}

@TexasDslMarker
class ViewDsl(
    layoutId: Int,
    disableReuse: Boolean = false,
    tag: Any? = null,
    onRenderer: (View.() -> Unit)
) {
    private val _view = object : ViewSegment(layoutId, disableReuse, tag) {

        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
        }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
        }

        override fun onRender(view: View) {
            onRenderer(view)
        }
    }

    // build 函数只有当前文件可见
    internal fun build(): ViewSegment {
        return _view
    }
}

@TexasDslMarker
class DocumentDsl(private val _option: TexasOption, prevDoc: Document? = null) {
    private val _document = Document.Builder(prevDoc)

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

    fun view(
        layoutId: Int, disableReuse: Boolean = false, tag: Any? = null,
        onRenderer: (View.() -> Unit)
    ) {
        val view = ViewDsl(layoutId, disableReuse, tag, onRenderer)
        _document.addSegment(view.build())
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