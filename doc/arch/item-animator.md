# DefaultItemAnimator 动画触发逻辑

`me.chan.texas.renderer.ui.rv.DefaultItemAnimator` 是 RecyclerView `ItemAnimator` 的实现，
对接 `TexasView.SegmentAnimator` 提供 add / remove / move / change 的动画能力。本文档说明
触发链路、播放顺序、内部状态和取消路径。

## 触发链路总览

```
Adapter.notifyItemXxx()
   ↓
RecyclerView 在下一帧 pre-layout/post-layout 比对位置
   ↓
SimpleItemAnimator.animateAdd / animateRemove / animateMove / animateChange  ← 仅入队
   ↓
RecyclerView 调用 runPendingAnimations()                                    ← 真正启动
   ↓
animateAddImpl / animateRemoveImpl / animateMoveImpl / animateChangeImpl
   ↓
SegmentAnimator.createXxxAnimator(segment, view)
   ↓
listener: onAnimationStart → dispatchXxxStarting
listener: onAnimationEnd   → dispatchXxxFinished + dispatchFinishedWhenDone
```

## Adapter API → animator 方法 的映射

| Adapter 调用 | 触发的方法 | 备注 |
|---|---|---|
| `notifyItemInserted` / `notifyItemRangeInserted` | `animateAdd` | 新 item 进场 |
| `notifyItemRemoved` / `notifyItemRangeRemoved` | `animateRemove` | item 离场 |
| `notifyItemMoved(from, to)` | `animateMove` | 显式移动 |
| **布局位移**（比如 insert 让后续 item 下移） | `animateMove` | 隐式产生，RV 自己算出来 |
| `notifyItemChanged(pos)`（**无 payload**） | `animateChange(old, new, …)` | `canReuseUpdatedViewHolder=false` → **双 holder**，做真正的 change |
| `notifyItemChanged(pos, payload)` | `animateChange(h, h, …)` → 立即转 `animateMove` | `payload` 非空 → 复用同一 holder，走 move 路径 |
| `notifyDataSetChanged()` | — | 全量重建，不触发动画 |

> 注意：在 Texas 高层 API（`TexasView.setSource` + `Document.Builder`）下，diff 由 `MixWorker.diff()`
> 计算，配置是 `detectMoves=false`、`areContentsTheSame == areItemsTheSame`。
> 因此通过 `setSource` 只会触发 `animateAdd / animateRemove`。要测试或使用 move / change，
> 必须直接对 RecyclerView Adapter 调相应的 `notifyItemMoved / notifyItemChanged`。

## animateXxx 阶段做的事（仅入队）

每个 `animateXxx` 方法都干两件事：

1. **`resetAnimation(holder)`** → `endAnimation(holder)` → `dispatchAnimatorCancel(view)`：
   把这个 holder 之前残留的动画状态（pending / running / tag）全部清干净。
2. 把 holder（或 `ChangeInfo` / `MoveInfo`）放进对应的 `mPendingXxx` 列表。

**`animateAdd` 多一步**：提前从 view tag `me_chan_texas_item_tag` 拿 `Segment`，
调 `SegmentAnimator.createAddAnimator(segment, view)`，把 animator 暂存到 view tag
`me_chan_texas_item_anim_tag` 上，等 `animateAddImpl` 来取。

> ⚠️ 业务必须在 `onBindViewHolder` 把 segment 写到 `me_chan_texas_item_tag`，
> 否则 Add 动画拿不到 animator，会立即派发 finish 没有视觉效果。

Remove / Move 不在 `animateXxx` 阶段拉 segment——它们是在 `animateXxxImpl` 阶段才取。

## runPendingAnimations 的播放顺序

强制串行：

```
remove ──┬─→ move ──┐
         ├─→ change ┴─→ add
```

通过 `ViewCompat.postOnAnimationDelayed(view, runnable, totalDelay)` 实现：

- move / change 在 `removeDuration` 后开始
- add 在 `removeDuration + max(moveDuration, changeDuration)` 后开始

视觉效果：旧的先走、空位先腾出、新的最后进。

## animateXxxImpl 真正启动动画

| Impl | 取 animator 的方式 | 完成时清 anim tag |
|---|---|---|
| `animateAddImpl` | 从 `me_chan_texas_item_anim_tag` 读（`animateAdd` 已经塞好） | ✅ |
| `animateRemoveImpl` | 现场调 `SegmentAnimator.createRemoveAnimator` | ✅ |
| `animateMoveImpl` | 现场调 `SegmentAnimator.createMoveAnimator` | ✅ |
| `animateChangeImpl` | **当前直接 dispatch finished**，没接 SegmentAnimator | — |

每个 impl 把 animator 注册到 `mXxxAnimations`，挂上 listener：

- `onAnimationStart` → `dispatchXxxStarting`
- `onAnimationEnd`   → 清 anim tag、`dispatchXxxFinished`、从 `mXxxAnimations` 移除、`dispatchFinishedWhenDone`
- （move 额外有 `onAnimationCancel`：把 translation 归零）

如果 `createXxxAnimator` 返回 `null`，就直接同步派发 starting + finished（即"无动画"）。

## 取消路径

| 入口 | 作用 |
|---|---|
| `endAnimation(holder)` | 把某个 holder 从所有 pending / running 列表里捞出来强行结束 |
| `endAnimations()` | 全部强行结束，结束后 `isRunning()` 必须返回 false |
| `dispatchAnimatorCancel(view)` | 底层：读 `me_chan_texas_item_anim_tag` 上的 animator，`cancel()` 它。用 `CANCEL_CALLED_FLAG` 这个 sentinel listener 探测 `cancel()` 是否自然触发了 `onAnimationCancel`（动画**还没 start** 时 cancel 是 no-op）；没触发就**手动**派发一遍。保证业务 listener 里 `onAnimationCancel` 的副作用（比如 reset translation）一定能跑到。 |

## 关键不变量

1. **`me_chan_texas_item_tag`** —— 业务设置（一般由 `RendererAdapterImpl.onBindViewHolder` 写入），
   存 `Segment`，**`SegmentAnimator` 工厂的入参来源**。
2. **`me_chan_texas_item_anim_tag`** —— 框架内部，存当前在跑的 `Animator`，**取消时从这里拿**。
   自然结束也必须清空，与 add 路径保持一致。
3. **`isRunning()`** = `任意 mPendingXxx 非空 || 任意 mXxxAnimations 非空 || 任意 mXxxList 非空`。
   `endAnimations()` 必须把所有这些容器都清空，否则 `isRunning()` 会永远 true。

## 内部状态结构

- `mPendingRemovals / mPendingAdditions / mPendingMoves / mPendingChanges`：
  `animateXxx` 入队但尚未启动的 holder/info。
- `mAdditionsList / mMovesList / mChangesList`：
  已经从 pending 转移到 "等待 delay 触发" 阶段的 batches。每次 `runPendingAnimations()`
  会把 pending 整批快照成一个新的 list，加进对应的 list-of-list。
- `mAddAnimations / mRemoveAnimations / mMoveAnimations / mChangeAnimations`：
  当前**正在运行**动画的 holder 集合，由 `animateXxxImpl` 注册、`onAnimationEnd` 移除。

## 已知历史 bug 与修复要点

为方便后续维护，这些坑值得记录：

1. **`endAnimations()` 中对 `ChangeInfo` 的 holder 不做 null 检查**——`animateChange` 允许
   `newHolder == null`，而 `endChangeAnimationIfNecessary` 会把 holder 置为 null，
   遍历时 `changeInfo.newHolder.itemView` 会 NPE。
   修复：访问 `itemView` 前必须 null 检查。
2. **`mChangesList` 在 `endAnimations()` 里不会被清空**——内层循环只调
   `endChangeAnimationIfNecessary`，不像 moves/additions 那样 `remove(j)`，
   导致 `mChangesList` 永远非空、`isRunning()` 永远 true。
   修复：循环里加 `changes.remove(j)`，与 moves/additions 写法一致。
3. **`animateRemoveImpl` / `animateMoveImpl` 自然结束不清 anim tag**——
   `animateAddImpl` 的 `onAnimationEnd` 会把 tag 置 null，但另外两个没有，
   导致 view 长期持有已完成 Animator 引用；下次 `dispatchAnimatorCancel` 还会对
   已结束的 animator 走"手动派发 cancel"路径，重复触发 listener。
   修复：`onAnimationEnd` 里都加 `view.setTag(R.id.me_chan_texas_item_anim_tag, null)`，
   并把 `view.setTag(animator)` 移到 `animator.start()` 之前，避免同步完成竞态。
4. **`animateChangeImpl` 当前没真正接动画**——直接同步派发 finished。
   如果业务需要 change 动画，需要参照 `animateAddImpl` 的结构补一个
   `createChangeAnimator`。
