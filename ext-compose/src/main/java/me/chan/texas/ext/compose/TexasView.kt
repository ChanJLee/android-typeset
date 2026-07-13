package me.chan.texas.ext.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import me.chan.texas.renderer.TexasView as AndroidTexasView

/**
 * 在 Compose 中使用 [me.chan.texas.renderer.TexasView] 渲染整篇文档。
 *
 * 视图随组合创建 / 释放（内部自动调用 [AndroidTexasView.release]），
 * [source] 实例变化时才会触发 [AndroidTexasView.setSource]，普通重组不会重复加载。
 *
 * @param source   文档数据源
 * @param modifier Compose 修饰符
 * @param onCreate 视图创建时回调一次，用于设置监听器、RenderOption 等
 * @param update   每次重组时回调，用于把 Compose 状态同步到视图
 */
@Composable
fun TexasView(
	source: AndroidTexasView.DocumentSource,
	modifier: Modifier = Modifier,
	onCreate: (AndroidTexasView) -> Unit = {},
	update: (AndroidTexasView) -> Unit = {},
) {
	AndroidView(
		factory = { context -> AndroidTexasView(context, null).also(onCreate) },
		modifier = modifier,
		onRelease = { it.release() },
		update = { view ->
			if (view.getTag(R.id.me_chan_texas_ext_compose_source) !== source) {
				view.setTag(R.id.me_chan_texas_ext_compose_source, source)
				view.setSource(source)
			}
			update(view)
		},
	)
}
