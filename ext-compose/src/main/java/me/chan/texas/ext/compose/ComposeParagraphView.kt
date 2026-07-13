package me.chan.texas.ext.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import me.chan.texas.renderer.ui.text.ParagraphView

/**
 * 在 Compose 中使用 [ParagraphView] 渲染富文本段落。
 *
 * 通过 [ParagraphView.ParagraphSource] 提供数据，可使用 `Paragraph.Builder` /
 * `para` DSL 构建带样式、tag、hyperSpan 的完整富文本段落。
 *
 * 视图随组合创建 / 释放（内部自动调用 [ParagraphView.discard]），
 * [source] 实例变化时才会触发 [ParagraphView.setSource]，普通重组不会重复排版。
 *
 * @param source   段落数据源，重写 `onRead(TexasOption)` 返回 [me.chan.texas.text.Paragraph]
 * @param modifier Compose 修饰符
 * @param onCreate 视图创建时回调一次，用于设置监听器、RenderOption 等
 * @param update   每次重组时回调，用于把 Compose 状态同步到视图
 */
@Composable
fun ComposeParagraphView(
	source: ParagraphView.ParagraphSource,
	modifier: Modifier = Modifier,
	onCreate: (ParagraphView) -> Unit = {},
	update: (ParagraphView) -> Unit = {},
) {
	ParagraphViewHost(
		content = source,
		modifier = modifier,
		onCreate = onCreate,
		update = update,
		bind = { it.setSource(source) },
	)
}

/**
 * 在 Compose 中使用 [ParagraphView] 渲染纯文本段落（[text] 变化时才重新排版）。
 *
 * 只需展示纯文本时的便捷重载；需要富文本能力请使用接收
 * [ParagraphView.ParagraphSource] 的重载。
 */
@Composable
fun ComposeParagraphView(
	text: CharSequence,
	modifier: Modifier = Modifier,
	onCreate: (ParagraphView) -> Unit = {},
	update: (ParagraphView) -> Unit = {},
) {
	ParagraphViewHost(
		content = text,
		modifier = modifier,
		onCreate = onCreate,
		update = update,
		bind = { it.setText(text) },
	)
}

@Composable
private fun ParagraphViewHost(
	content: Any,
	modifier: Modifier,
	onCreate: (ParagraphView) -> Unit,
	update: (ParagraphView) -> Unit,
	bind: (ParagraphView) -> Unit,
) {
	AndroidView(
		factory = { context -> ParagraphView(context, null).also(onCreate) },
		modifier = modifier,
		onRelease = { it.discard() },
		update = { view ->
			if (view.getTag(R.id.me_chan_texas_ext_compose_source) != content) {
				view.setTag(R.id.me_chan_texas_ext_compose_source, content)
				bind(view)
			}
			update(view)
		},
	)
}
