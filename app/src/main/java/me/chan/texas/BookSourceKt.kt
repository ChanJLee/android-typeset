package me.chan.texas

import me.chan.texas.renderer.TexasView.DocumentSource
import me.chan.texas.text.Document
import me.chan.texas.text.dsl.document

class BookSourceKt : DocumentSource() {

    override fun onRead(option: TexasOption, previousDocument: Document?): Document {
        return document(option, previousDocument) {
            for (i in 0..10) {
                para {
                    span("title $i\n") {
                        tag(i)

                        style { textPaint, _ ->
                            textPaint.textSize = 20f
                        }

                        foreground { canvas, paint, inner, outer, context ->
                            paint.color = 0x11ff0000
                            canvas.drawRect(inner.left, inner.top, inner.right, inner.bottom, paint)
                        }
                    }

                    span("this is content")
                }
            }
        }
    }
}