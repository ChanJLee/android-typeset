package me.chan.texas.ext.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import me.chan.texas.renderer.ui.text.ParagraphView as AndroidParagraphView

/**
 * 在 Compose 中使用 [me.chan.texas.renderer.ui.text.ParagraphView] 渲染单个段落文本。
 *
 * 视图随组合创建 / 释放（内部自动调用 [AndroidParagraphView.discard]），
 * [text] 内容变化时才会触发 [AndroidParagraphView.setText]，普通重组不会重复排版。
 *
 * @param text     段落文本
 * @param modifier Compose 修饰符
 * @param onCreate 视图创建时回调一次，用于设置监听器、RenderOption 等
 * @param update   每次重组时回调，用于把 Compose 状态同步到视图
 *                 （如需 [AndroidParagraphView.setParagraph] / setSource 等高级用法，可在此直接操作视图）
 */
@Composable
fun ParagraphView(
	text: CharSequence,
	modifier: Modifier = Modifier,
	onCreate: (AndroidParagraphView) -> Unit = {},
	update: (AndroidParagraphView) -> Unit = {},
) {
	AndroidView(
		factory = { context -> AndroidParagraphView(context, null).also(onCreate) },
		modifier = modifier,
		onRelease = { it.discard() },
		update = { view ->
			if (view.getTag(R.id.me_chan_texas_ext_compose_source) != text) {
				view.setTag(R.id.me_chan_texas_ext_compose_source, text)
				view.setText(text)
			}
			update(view)
		},
	)
}
