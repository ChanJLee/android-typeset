package me.chan.texas.ext.compose

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import me.chan.texas.text.ViewSegment

/**
 * 在 Texas 文档流中内嵌 Compose 内容的 Segment。
 *
 * 与普通 [ViewSegment] 一样参与文档排列、增量 diff 与视图复用；
 * 组合的生命周期由 [ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool]
 * 管理，随引擎内部的视图回收自动销毁。
 *
 * 复用说明：默认参与视图复用（性能最优）。内容以当前 segment 实例为 key 做隔离，
 * 复用的视图被绑定到另一个 segment 时会丢弃前者遗留的 remember 状态，不会串位；
 * 但同一个 segment 滚出回收池再滚回来时，组合内的临时状态（remember）会重建。
 * 需要跨复用保活的状态请提升到业务层持有；如果内容组合成本高或确有视图级状态
 * 需要独占，可传 [disableReuse] = true 退出复用。
 *
 * ```kotlin
 * Document.Builder()
 *     .addSegment(paragraph)
 *     .addSegment(ComposeViewSegment { MyCard() })
 *     .build()
 * ```
 *
 * @param tag          Segment 唯一标识，参与增量更新 diff 与 [me.chan.texas.text.Segment.getTag]
 * @param disableReuse 是否禁止该 Segment 的视图参与复用池，默认参与复用
 * @param content      要渲染的 Compose 内容
 */
class ComposeViewSegment @JvmOverloads constructor(
	tag: Any? = null,
	disableReuse: Boolean = false,
	private val content: @Composable () -> Unit,
) : ViewSegment(
	Args(R.layout.me_chan_texas_ext_compose)
		.disableReuse(disableReuse)
		.tag(tag)
) {

	override fun onRender(view: View) {
		val composeView = view as ComposeView
		composeView.setViewCompositionStrategy(
			ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool
		)
		// 以 segment 实例为 key：复用的 ComposeView 换绑到另一个 segment 时，
		// 即使内容结构相同也会重建状态，避免 remember 状态串位
		composeView.setContent {
			key(this@ComposeViewSegment) {
				content()
			}
		}
	}
}
