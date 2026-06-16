package me.chan.texas;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.misc.Rect;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.ui.RendererHost;
import me.chan.texas.renderer.ui.rv.DefaultItemAnimator;
import me.chan.texas.text.Segment;

/**
 * 用于测试 DefaultItemAnimator 各个动画路径：
 * - animateAdd / animateRemove / animateMove
 * - animateChange (true two-holder change)
 * - animateChange (payload reuse 同一 holder, 走 move 路径)
 * - animateChange (newHolder == null, 验证 NPE fix)
 * - 中途取消 (endAnimation / endAnimations)
 * - 验证 isRunning() 在 endAnimations() 后回归 false (验证 mChangesList 清理 fix)
 * <p>
 * 直接使用 RecyclerView + 我们的 DefaultItemAnimator, 绕开 TexasView 的 diff 限制,
 * 可以触发 notifyItemMoved / notifyItemChanged.
 */
public class ItemAnimatorDemoActivity extends AppCompatActivity {

	private static final long DURATION = 1500L;

	private RecyclerView mList;
	private TextView mStatus;
	private DefaultItemAnimator mAnimator;
	private Adapter mAdapter;
	private final Handler mHandler = new Handler(Looper.getMainLooper());
	private int mItemSeq = 0;

	private final AtomicInteger mAddStart = new AtomicInteger();
	private final AtomicInteger mAddEnd = new AtomicInteger();
	private final AtomicInteger mAddCancel = new AtomicInteger();
	private final AtomicInteger mRemoveStart = new AtomicInteger();
	private final AtomicInteger mRemoveEnd = new AtomicInteger();
	private final AtomicInteger mRemoveCancel = new AtomicInteger();
	private final AtomicInteger mMoveStart = new AtomicInteger();
	private final AtomicInteger mMoveEnd = new AtomicInteger();
	private final AtomicInteger mMoveCancel = new AtomicInteger();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(me.chan.texas.debug.R.layout.activity_item_animator_demo);

		mList = findViewById(me.chan.texas.debug.R.id.list);
		mStatus = findViewById(me.chan.texas.debug.R.id.status);

		mAnimator = new DefaultItemAnimator();
		mAnimator.setAddDuration(DURATION);
		mAnimator.setRemoveDuration(DURATION);
		mAnimator.setMoveDuration(DURATION);
		mAnimator.setChangeDuration(DURATION);
		mAnimator.setSegmentItemAnimator(new TestSegmentAnimator());

		mList.setLayoutManager(new LinearLayoutManager(this));
		mList.setItemAnimator(mAnimator);
		mAdapter = new Adapter();
		mList.setAdapter(mAdapter);

		mAdapter.seedInitial();

		bind(me.chan.texas.debug.R.id.btn_add, v -> mAdapter.addAt(0));
		bind(me.chan.texas.debug.R.id.btn_add_mid, v -> mAdapter.addAt(mAdapter.getItemCount() / 2));
		bind(me.chan.texas.debug.R.id.btn_remove, v -> mAdapter.removeAt(0));
		bind(me.chan.texas.debug.R.id.btn_remove_last, v -> mAdapter.removeAt(mAdapter.getItemCount() - 1));
		bind(me.chan.texas.debug.R.id.btn_move, v -> {
			if (mAdapter.getItemCount() >= 2) {
				mAdapter.move(0, mAdapter.getItemCount() - 1);
			}
		});
		bind(me.chan.texas.debug.R.id.btn_change, v -> mAdapter.changeAt(0, /* payload= */ false));
		bind(me.chan.texas.debug.R.id.btn_change_payload, v -> mAdapter.changeAt(0, /* payload= */ true));
		bind(me.chan.texas.debug.R.id.btn_change_null_new, v -> triggerChangeNullNewHolder());
		bind(me.chan.texas.debug.R.id.btn_cancel_add, v -> {
			int pos = mAdapter.addAt(0);
			mHandler.postDelayed(() -> mAdapter.removeAt(pos), DURATION / 3);
		});
		bind(me.chan.texas.debug.R.id.btn_cancel_remove, v -> {
			if (mAdapter.getItemCount() <= 0) {
				return;
			}
			mAdapter.removeAt(0);
			mHandler.postDelayed(() -> mAnimator.endAnimations(), DURATION / 3);
		});
		bind(me.chan.texas.debug.R.id.btn_end_all, v -> {
			mAnimator.endAnimations();
			Toast.makeText(this,
					"endAnimations() done — isRunning=" + mAnimator.isRunning(),
					Toast.LENGTH_SHORT).show();
		});
		bind(me.chan.texas.debug.R.id.btn_stress, v -> stress());
		bind(me.chan.texas.debug.R.id.btn_reset, v -> {
			mAnimator.endAnimations();
			resetCounters();
			mAdapter.reset();
		});

		startStatusLoop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}

	private void bind(int id, View.OnClickListener listener) {
		findViewById(id).setOnClickListener(listener);
	}

	/**
	 * 触发 animateChange(oldHolder, null, ...) 路径 —— 用来验证 NPE 修复.
	 * 直接通过 ItemAnimator 接口手工调用即可, 不需要绕过 RecyclerView 的状态.
	 */
	private void triggerChangeNullNewHolder() {
		if (mAdapter.getItemCount() == 0) {
			Toast.makeText(this, "需要先有 item", Toast.LENGTH_SHORT).show();
			return;
		}
		RecyclerView.ViewHolder vh = mList.findViewHolderForAdapterPosition(0);
		if (vh == null) {
			Toast.makeText(this, "找不到 holder", Toast.LENGTH_SHORT).show();
			return;
		}
		// animateChange(oldHolder, null, ...) → ChangeInfo.newHolder = null
		// 之后调 endAnimations() 应该不能 NPE.
		mAnimator.animateChange(vh, null, 0, 0, 0, 0);
		mAnimator.runPendingAnimations();
		mHandler.postDelayed(() -> {
			mAnimator.endAnimations();
			Toast.makeText(this,
					"endAnimations() 通过 (no NPE) isRunning=" + mAnimator.isRunning(),
					Toast.LENGTH_SHORT).show();
		}, DURATION / 3);
	}

	private void stress() {
		// 在不同延迟下并发触发 add / remove / move / change, 检查没有 crash / 悬挂状态.
		mAdapter.addAt(0);
		mHandler.postDelayed(() -> mAdapter.addAt(0), 50);
		mHandler.postDelayed(() -> mAdapter.addAt(0), 100);
		mHandler.postDelayed(() -> {
			if (mAdapter.getItemCount() >= 2) {
				mAdapter.move(0, 1);
			}
		}, 200);
		mHandler.postDelayed(() -> mAdapter.changeAt(0, false), 300);
		mHandler.postDelayed(() -> mAdapter.removeAt(0), 400);
		mHandler.postDelayed(() -> mAnimator.endAnimations(), 600);
		mHandler.postDelayed(() -> mAdapter.addAt(0), 700);
	}

	private void resetCounters() {
		mAddStart.set(0);
		mAddEnd.set(0);
		mAddCancel.set(0);
		mRemoveStart.set(0);
		mRemoveEnd.set(0);
		mRemoveCancel.set(0);
		mMoveStart.set(0);
		mMoveEnd.set(0);
		mMoveCancel.set(0);
	}

	private void startStatusLoop() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mStatus.setText(
						"items=" + mAdapter.getItemCount()
								+ " | isRunning=" + mAnimator.isRunning()
								+ "\nadd  s/e/c=" + mAddStart + "/" + mAddEnd + "/" + mAddCancel
								+ "  rm  s/e/c=" + mRemoveStart + "/" + mRemoveEnd + "/" + mRemoveCancel
								+ "  mv  s/e/c=" + mMoveStart + "/" + mMoveEnd + "/" + mMoveCancel);
				mHandler.postDelayed(this, 200);
			}
		});
	}

	// region SegmentAnimator =================================================

	private class TestSegmentAnimator extends TexasView.SegmentAnimator {

		@Override
		protected Animator onCreateAddAnimator(@NonNull Segment segment, @NonNull View view) {
			ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
			ObjectAnimator translate = ObjectAnimator.ofFloat(view, "translationX", -view.getWidth(), 0f);
			AnimatorSet set = new AnimatorSet();
			set.playTogether(alpha, translate);
			set.setDuration(DURATION);
			set.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					mAddStart.incrementAndGet();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					mAddCancel.incrementAndGet();
					view.setAlpha(1f);
					view.setTranslationX(0f);
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					mAddEnd.incrementAndGet();
				}
			});
			return set;
		}

		@Override
		protected Animator onCreateRemoveAnimator(@NonNull Segment segment, @NonNull View view) {
			ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
			ObjectAnimator translate = ObjectAnimator.ofFloat(view, "translationX", 0f, view.getWidth());
			AnimatorSet set = new AnimatorSet();
			set.playTogether(alpha, translate);
			set.setDuration(DURATION);
			set.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					mRemoveStart.incrementAndGet();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					mRemoveCancel.incrementAndGet();
					view.setAlpha(1f);
					view.setTranslationX(0f);
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					mRemoveEnd.incrementAndGet();
				}
			});
			return set;
		}

		@Override
		protected Animator onCreateMoveAnimator(@NonNull Segment segment, @NonNull View view,
												int fromX, int fromY, int toX, int toY) {
			int deltaX = toX - fromX;
			int deltaY = toY - fromY;
			if (deltaX == 0 && deltaY == 0) {
				return null;
			}
			view.setTranslationX(-deltaX);
			view.setTranslationY(-deltaY);
			ObjectAnimator tx = ObjectAnimator.ofFloat(view, "translationX", -deltaX, 0f);
			ObjectAnimator ty = ObjectAnimator.ofFloat(view, "translationY", -deltaY, 0f);
			AnimatorSet set = new AnimatorSet();
			set.playTogether(tx, ty);
			set.setDuration(DURATION);
			set.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					mMoveStart.incrementAndGet();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					mMoveCancel.incrementAndGet();
					view.setTranslationX(0f);
					view.setTranslationY(0f);
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					mMoveEnd.incrementAndGet();
				}
			});
			return set;
		}
	}

	// endregion

	// region Stub Segment ====================================================

	/**
	 * 最小可用 Segment, 仅为了让 DefaultItemAnimator 能从 view tag 中取到 segment, 触发自定义动画.
	 * 这里没有走 Texas 的渲染管线, 所有 LIBRARY 内部方法都直接 no-op.
	 */
	private static class StubSegment extends Segment {
		private final int mId;
		private boolean mRecycled;

		StubSegment(int id) {
			mId = id;
		}

		@Override
		public void getRect(Rect rect) {
			rect.set(0, 0, 0, 0);
		}

		@Override
		public Rect getRect() {
			return new Rect();
		}

		@Override
		public void setPadding(Rect rect) {
		}

		@Override
		public void recycle() {
			mRecycled = true;
		}

		@Override
		public boolean isRecycled() {
			return mRecycled;
		}

		@Override
		public int getId() {
			return mId;
		}

		@Override
		public void bind(RendererHost host) {
		}

		@Override
		public void attachToWindow(RecyclerView.ViewHolder holder) {
		}

		@Override
		public void detachFromWindow(RecyclerView.ViewHolder holder) {
		}

		@Override
		public void requestRedraw() {
		}

		@Override
		public int getIndex() {
			return 0;
		}
	}

	// endregion

	// region Adapter =========================================================

	private class Adapter extends RecyclerView.Adapter<Adapter.VH> {

		private final List<Item> mData = new ArrayList<>();

		void seedInitial() {
			mData.clear();
			for (int i = 0; i < 5; i++) {
				mData.add(newItem());
			}
			notifyDataSetChanged();
		}

		void reset() {
			int n = mData.size();
			mData.clear();
			notifyItemRangeRemoved(0, n);
			seedInitial();
		}

		int addAt(int index) {
			index = Math.max(0, Math.min(mData.size(), index));
			mData.add(index, newItem());
			notifyItemInserted(index);
			return index;
		}

		void removeAt(int index) {
			if (index < 0 || index >= mData.size()) {
				return;
			}
			mData.remove(index);
			notifyItemRemoved(index);
		}

		void move(int from, int to) {
			if (from < 0 || to < 0 || from >= mData.size() || to >= mData.size()) {
				return;
			}
			Collections.swap(mData, from, to);
			notifyItemMoved(from, to);
		}

		void changeAt(int index, boolean withPayload) {
			if (index < 0 || index >= mData.size()) {
				return;
			}
			Item item = mData.get(index);
			item.label = item.label + "*";
			if (withPayload) {
				// 走 canReuseUpdatedViewHolder → 复用同一个 holder, animateChange 内部转 animateMove.
				notifyItemChanged(index, "payload");
			} else {
				// 不带 payload → canReuseUpdatedViewHolder=false → 真正的双 holder change.
				notifyItemChanged(index);
			}
		}

		private Item newItem() {
			int id = ++mItemSeq;
			return new Item(id, "item #" + id);
		}

		@NonNull
		@Override
		public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			FrameLayout root = new FrameLayout(parent.getContext());
			root.setLayoutParams(new RecyclerView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			TextView tv = new TextView(parent.getContext());
			int pad = (int) (parent.getResources().getDisplayMetrics().density * 16);
			tv.setPadding(pad, pad, pad, pad);
			tv.setTextSize(16f);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			root.addView(tv, new FrameLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			return new VH(root, tv);
		}

		@Override
		public void onBindViewHolder(@NonNull VH holder, int position) {
			Item item = mData.get(position);
			holder.text.setText(item.label);
			int bg = (item.id * 0x224488) | 0xFFE0E0E0;
			holder.itemView.setBackgroundColor(Color.argb(0xFF,
					0xC0 + (item.id * 17) % 0x40,
					0xC0 + (item.id * 31) % 0x40,
					0xE0 + (item.id * 13) % 0x20));
			// 关键: DefaultItemAnimator 会从这个 tag 取 segment 来调 SegmentAnimator.
			holder.itemView.setTag(me.chan.texas.R.id.me_chan_texas_item_tag, item.segment);
		}

		@Override
		public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
			super.onBindViewHolder(holder, position, payloads);
		}

		@Override
		public int getItemCount() {
			return mData.size();
		}

		class VH extends RecyclerView.ViewHolder {
			final TextView text;

			VH(@NonNull View itemView, TextView text) {
				super(itemView);
				this.text = text;
			}
		}
	}

	private static class Item {
		final int id;
		final Segment segment;
		String label;

		Item(int id, String label) {
			this.id = id;
			this.label = label;
			this.segment = new StubSegment(id);
		}
	}

	// endregion
}
