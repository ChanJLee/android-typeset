package me.chan.texas.renderer.ui.rv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class TexasItemAnimator extends RecyclerView.ItemAnimator {
	private static final long APPEARANCE_DURATION = 250;
	private static final long DISAPPEARANCE_DURATION = 200;

	private final AnimTracker mTracker = new AnimTracker();

	@Override
	public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
		endAnimation(viewHolder);
		mTracker.add(viewHolder, AnimRecord.TYPE_DISAPPEARANCE);
		return true;
	}

	@Override
	public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
		endAnimation(viewHolder);
		View itemView = viewHolder.itemView;
		int height = postLayoutInfo.bottom - postLayoutInfo.top;

		itemView.setAlpha(0f);
		itemView.setTranslationY(-height);

		mTracker.add(viewHolder, AnimRecord.TYPE_APPEARANCE);
		return true;
	}

	@Override
	public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
		return false;
	}

	@Override
	public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
		return false;
	}

	@Override
	public void runPendingAnimations() {
		List<RecyclerView.ViewHolder> pending = mTracker.holdersByPhase(AnimRecord.PHASE_PENDING);
		if (pending.isEmpty()) {
			return;
		}

		for (RecyclerView.ViewHolder holder : pending) {
			mTracker.advanceTo(holder, AnimRecord.PHASE_POSTPONED);
			ViewCompat.postOnAnimation(holder.itemView, () -> {
				AnimRecord record = mTracker.get(holder);
				if (record != null && record.phase == AnimRecord.PHASE_POSTPONED) {
					mTracker.advanceTo(holder, AnimRecord.PHASE_RUNNING);
					startAnimation(holder, record.type);
				}
			});
		}
	}

	private void startAnimation(@NonNull RecyclerView.ViewHolder holder, int type) {
		dispatchAnimationStarted(holder);
		View itemView = holder.itemView;

		if (type == AnimRecord.TYPE_APPEARANCE) {
			itemView.animate()
					.alpha(1f)
					.translationY(0f)
					.setDuration(APPEARANCE_DURATION)
					.setInterpolator(new DecelerateInterpolator())
					.setListener(createEndListener(holder))
					.start();
		} else {
			int height = itemView.getHeight();
			itemView.animate()
					.alpha(0f)
					.translationY(-height)
					.setDuration(DISAPPEARANCE_DURATION)
					.setInterpolator(new AccelerateInterpolator())
					.setListener(createEndListener(holder))
					.start();
		}
	}

	private AnimatorListenerAdapter createEndListener(@NonNull RecyclerView.ViewHolder holder) {
		return new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				holder.itemView.animate().setListener(null);
				resetView(holder.itemView);
				if (mTracker.remove(holder)) {
					dispatchAnimationFinished(holder);
				}
			}
		};
	}

	private void resetView(View view) {
		view.setAlpha(1f);
		view.setTranslationY(0f);
	}

	@Override
	public void endAnimation(@NonNull RecyclerView.ViewHolder item) {
		AnimRecord record = mTracker.get(item);
		if (record == null) {
			return;
		}
		boolean isRunning = record.phase == AnimRecord.PHASE_RUNNING;
		item.itemView.animate().cancel();
		resetView(item.itemView);
		// running: cancel() 已同步触发 onAnimationEnd → dispatch，不再重复
		if (!isRunning && mTracker.remove(item)) {
			dispatchAnimationFinished(item);
		}
	}

	@Override
	public void endAnimations() {
		for (RecyclerView.ViewHolder holder : mTracker.allHolders()) {
			endAnimation(holder);
		}
	}

	@Override
	public boolean isRunning() {
		return !mTracker.isEmpty();
	}

	static class AnimRecord {
		static final int TYPE_APPEARANCE = 1;
		static final int TYPE_DISAPPEARANCE = 2;

		static final int PHASE_PENDING = 0;
		static final int PHASE_POSTPONED = 1;
		static final int PHASE_RUNNING = 2;

		final int type;
		int phase;

		AnimRecord(int type) {
			this.type = type;
			this.phase = PHASE_PENDING;
		}
	}

	static class AnimTracker {
		private final HashMap<RecyclerView.ViewHolder, AnimRecord> mRecords = new HashMap<>();

		void add(RecyclerView.ViewHolder holder, int type) {
			mRecords.put(holder, new AnimRecord(type));
		}

		boolean remove(RecyclerView.ViewHolder holder) {
			return mRecords.remove(holder) != null;
		}

		@Nullable
		AnimRecord get(RecyclerView.ViewHolder holder) {
			return mRecords.get(holder);
		}

		void advanceTo(RecyclerView.ViewHolder holder, int phase) {
			AnimRecord record = mRecords.get(holder);
			if (record != null) {
				record.phase = phase;
			}
		}

		List<RecyclerView.ViewHolder> holdersByPhase(int phase) {
			List<RecyclerView.ViewHolder> result = new ArrayList<>();
			for (HashMap.Entry<RecyclerView.ViewHolder, AnimRecord> entry : mRecords.entrySet()) {
				if (entry.getValue().phase == phase) {
					result.add(entry.getKey());
				}
			}
			return result;
		}

		List<RecyclerView.ViewHolder> allHolders() {
			return new ArrayList<>(mRecords.keySet());
		}

		boolean isEmpty() {
			return mRecords.isEmpty();
		}
	}
}
