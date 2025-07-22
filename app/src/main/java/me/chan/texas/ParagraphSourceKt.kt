package me.chan.texas

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import androidx.core.content.ContextCompat
import me.chan.texas.renderer.ui.text.ParagraphView
import me.chan.texas.text.Paragraph
import me.chan.texas.text.dsl.Size
import me.chan.texas.text.dsl.para
import me.chan.texas.text.dsl.ratio

class ParagraphSourceKt(private val _index: Int, context: Context) : ParagraphView.ParagraphSource() {
    private val mFlagWidth: Float
    private val mFlagHeight: Float
    private val mFlag: Drawable
    private val STATE_NORMAL = intArrayOf(-android.R.attr.state_pressed)
    private val STATE_PRESSED = intArrayOf(android.R.attr.state_pressed)

    init {
        mFlag = ContextCompat.getDrawable(context, me.chan.texas.debug.R.drawable.me_chan_te_flag)!!
        val resources = context.resources;
        mFlagWidth =
            resources.getDimension(me.chan.texas.debug.R.dimen.com_shanbay_lib_texas_flag_width);
        mFlagHeight =
            resources.getDimension(me.chan.texas.debug.R.dimen.com_shanbay_lib_texas_flag_height);
    }

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

            hypeSpan(
                Size.ratio(
                    mFlagWidth,
                    mFlagHeight
                )
            ) { paint, inner, outer, baselineOffset, states ->
                val x = inner.left
                val y = inner.bottom - baselineOffset
                if (mFlag is StateListDrawable) {
                    mFlag.setState(if (states.isSelected) STATE_PRESSED else STATE_NORMAL);
                }

                mFlag.setBounds(
                    x.toInt(),
                    (y - inner.height()).toInt(),
                    (x + inner.width()).toInt(),
                    y.toInt()
                )
                draw(mFlag)
            }
        }
    }
}