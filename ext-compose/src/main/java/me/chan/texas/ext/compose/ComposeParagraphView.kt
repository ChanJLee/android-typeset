package me.chan.texas.ext.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import me.chan.texas.renderer.ui.text.ParagraphView

/**
 * 在 Compose 中使用 [ParagraphView] 渲染富文本段落。
 *
 * [source] 是工厂函数而非实例：`ParagraphSource` 有状态（attach 到单个视图），
 * 不可跨视图复用。包装器在每次真正需要加载时调用工厂获取全新实例，因此调用方
 * 无需（也不应）用 `remember` 缓存 source。工厂返回的数据源重写
 * `onRead(TexasOption)`，可用 `Paragraph.Builder` / `para` DSL 构建带样式、
 * tag、hyperSpan 的完整富文本段落。
 *
 * 加载时机：视图创建后加载一次；之后仅当 [contentKey] 变化时重新调用工厂并
 * 加载，普通重组不会触发。
 *
 * 视图随组合创建 / 释放，内部自动调用 [ParagraphView.discard]。
 *
 * @param modifier   Compose 修饰符
 * @param contentKey 内容标识，变化时重新加载；null 表示只在视图创建时加载一次
 * @param onCreate   视图创建时回调一次，用于设置监听器、RenderOption 等
 * @param update     每次重组时回调，用于把 Compose 状态同步到视图
 * @param source     段落数据源工厂，每次加载都会创建全新实例
 */
@Composable
fun ComposeParagraphView(
	modifier: Modifier = Modifier,
	contentKey: Any? = null,
	onCreate: (ParagraphView) -> Unit = {},
	update: (ParagraphView) -> Unit = {},
	source: () -> ParagraphView.ParagraphSource,
) {
	ParagraphViewHost(
		contentKey = contentKey ?: DefaultContentKey,
		modifier = modifier,
		onCreate = onCreate,
		update = update,
		bind = { it.setSource(source()) },
	)
}

/**
 * 在 Compose 中使用 [ParagraphView] 渲染纯文本段落（[text] 变化时才重新排版）。
 *
 * 只需展示纯文本时的便捷重载；需要富文本能力请使用接收数据源工厂的重载。
 */
@Composable
fun ComposeParagraphView(
	text: CharSequence,
	modifier: Modifier = Modifier,
	onCreate: (ParagraphView) -> Unit = {},
	update: (ParagraphView) -> Unit = {},
) {
	ParagraphViewHost(
		contentKey = text,
		modifier = modifier,
		onCreate = onCreate,
		update = update,
		bind = { it.setText(text) },
	)
}

@Composable
private fun ParagraphViewHost(
	contentKey: Any,
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
			if (view.getTag(R.id.me_chan_texas_ext_compose_source) != contentKey) {
				view.setTag(R.id.me_chan_texas_ext_compose_source, contentKey)
				bind(view)
			}
			update(view)
		},
	)
}
