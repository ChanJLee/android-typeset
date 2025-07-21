package me.chan.texas

import me.chan.texas.renderer.ui.text.ParagraphView
import me.chan.texas.text.Paragraph
import me.chan.texas.text.dsl.para

class ParagraphSourceKt(private val _index: Int) : ParagraphView.ParagraphSource() {

    override fun onRead(option: TexasOption): Paragraph {
        return para(option) {
            span("title ${_index}\n") {
                style { _ ->
                    isFakeBoldText = true
                }

                foreground { paint, inner, outer, context ->
                    paint.color = 0x11ff0000
                    drawRect(inner.left, inner.top, inner.right, inner.bottom, paint)
                }
            }

            span("this is content")
        }
    }
}