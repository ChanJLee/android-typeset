package me.chan.texas.ext.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import me.chan.texas.renderer.TexasView

/**
 * 在 Compose 中使用 [TexasView] 渲染整篇文档。
 *
 * [source] 是工厂函数而非实例：`DocumentSource` 有状态（attach 到单个视图，
 * 视图 release 时会被 detach），不可跨视图复用。包装器在每次真正需要加载时
 * 调用工厂获取全新实例，因此调用方无需（也不应）用 `remember` 缓存 source。
 *
 * 加载时机：视图创建后加载一次；之后仅当 [contentKey] 变化时重新调用工厂并
 * 加载，普通重组不会触发。需要刷新内容时，传入新的 [contentKey]（例如章节 id、
 * 数据版本号）。
 *
 * 视图随组合创建 / 释放，内部自动调用 [TexasView.release]。
 *
 * @param modifier   Compose 修饰符
 * @param contentKey 内容标识，变化时重新加载；null 表示只在视图创建时加载一次
 * @param onCreate   视图创建时回调一次，用于设置监听器、RenderOption 等
 * @param update     每次重组时回调，用于把 Compose 状态同步到视图
 * @param source     文档数据源工厂，每次加载都会创建全新实例
 */
@Composable
fun ComposeTexasView(
	modifier: Modifier = Modifier,
	contentKey: Any? = null,
	onCreate: (TexasView) -> Unit = {},
	update: (TexasView) -> Unit = {},
	source: () -> TexasView.DocumentSource,
) {
	AndroidView(
		factory = { context -> TexasView(context, null).also(onCreate) },
		modifier = modifier,
		onRelease = { it.release() },
		update = { view ->
			val key = contentKey ?: DefaultContentKey
			if (view.getTag(R.id.me_chan_texas_ext_compose_source) != key) {
				view.setTag(R.id.me_chan_texas_ext_compose_source, key)
				view.setSource(source())
			}
			update(view)
		},
	)
}

/** [ComposeTexasView] 的 contentKey 为 null 时的占位 key：视图创建后只加载一次 */
internal object DefaultContentKey
