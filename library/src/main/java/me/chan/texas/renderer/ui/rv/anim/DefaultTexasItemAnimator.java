package me.chan.texas.renderer.ui.rv.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.chan.texas.R;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.text.Segment;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class DefaultTexasItemAnimator extends RecyclerView.ItemAnimator {

	@Nullable
	private TexasView.SegmentAnimator mSegmentItemAnimator;
	private final AnimTracker mTracker = new AnimTracker();

	public void setSegmentItemAnimator(@Nullable TexasView.SegmentAnimator segmentItemAnimator) {
		mSegmentItemAnimator = segmentItemAnimator;
	}

	@Override
	public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
		endAnimation(viewHolder);
		return createAnimator(viewHolder, AnimType.DISAPPEARANCE);
	}

	@Override
	public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
		endAnimation(viewHolder);
		return createAnimator(viewHolder, AnimType.APPEARANCE);
	}

	private boolean createAnimator(RecyclerView.ViewHolder holder, AnimType type) {
		if (mSegmentItemAnimator == null) {
			return false;
		}

		Segment segment = (Segment) holder.itemView.getTag(R.id.me_chan_texas_item_tag);
		if (segment == null) {
			return false;
		}

		Animator animator = mSegmentItemAnimator.createAnimator(segment, holder.itemView, type);
		if (animator == null) {
			return false;
		}

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				animation.removeListener(this);
				if (mTracker.remove(holder)) {
					dispatchAnimationFinished(holder);
				}
			}
		});
		mTracker.add(holder, new AnimRecord(type, animator));
		return true;
	}

	@Override
	public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
		endAnimation(viewHolder);
		return createAnimator(viewHolder, AnimType.PERSISTENCE);
	}

	@Override
	public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
		endAnimation(oldHolder);
		endAnimation(newHolder);
		boolean created = createAnimator(newHolder, AnimType.CHANGE);
		if (!created) {
			return false;
		}
		if (oldHolder != newHolder) {
			dispatchAnimationFinished(oldHolder);
		}
		return true;
	}

	@Override
	public void runPendingAnimations() {
		if (mTracker.isEmpty()) {
			return;
		}

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
					startAnimation(holder);
				}
			});
		}
	}

	private void startAnimation(@NonNull RecyclerView.ViewHolder holder) {
		AnimRecord record = mTracker.get(holder);
		if (record == null) {
			return;
		}

		dispatchAnimationStarted(holder);
		record.animator.start();
	}

	@Override
	public void endAnimation(@NonNull RecyclerView.ViewHolder item) {
		AnimRecord record = mTracker.get(item);
		if (record == null) {
			return;
		}

		boolean isRunning = record.phase == AnimRecord.PHASE_RUNNING;
		record.animator.cancel();
		// running: cancel() 已同步触发 onAnimationEnd → dispatch，不再重复
		if (!isRunning && mTracker.remove(item)) {
			dispatchAnimationFinished(item);
		}
	}

	@Override
	public void endAnimations() {
		if (mTracker.isEmpty()) {
			return;
		}

		for (RecyclerView.ViewHolder holder : mTracker.allHolders()) {
			endAnimation(holder);
		}
	}

	@Override
	public boolean isRunning() {
		return !mTracker.isEmpty();
	}
}
