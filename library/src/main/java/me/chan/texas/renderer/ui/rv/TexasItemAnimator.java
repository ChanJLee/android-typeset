package me.chan.texas.renderer.ui.rv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class TexasItemAnimator extends RecyclerView.ItemAnimator {
	private static final long APPEARANCE_DURATION = 250;

	private final List<RecyclerView.ViewHolder> mPendingAppearances = new ArrayList<>();
	private final List<RecyclerView.ViewHolder> mRunningAppearances = new ArrayList<>();
	private final Set<RecyclerView.ViewHolder> mEndedHolders = new HashSet<>();

	@Override
	public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
		return false;
	}

	@Override
	public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
		endAnimation(viewHolder);
		View itemView = viewHolder.itemView;
		int height = postLayoutInfo.bottom - postLayoutInfo.top;

		itemView.setAlpha(0f);
		itemView.setTranslationY(-height);

		mPendingAppearances.add(viewHolder);
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
		if (mPendingAppearances.isEmpty()) {
			return;
		}
		List<RecyclerView.ViewHolder> toAnimate = new ArrayList<>(mPendingAppearances);
		mPendingAppearances.clear();

		for (RecyclerView.ViewHolder holder : toAnimate) {
			View itemView = holder.itemView;
			ViewCompat.postOnAnimation(itemView, () -> animateAppearanceImpl(holder));
		}
	}

	private void animateAppearanceImpl(@NonNull RecyclerView.ViewHolder holder) {
		if (mEndedHolders.remove(holder)) {
			return;
		}
		View itemView = holder.itemView;
		if (!itemView.isAttachedToWindow()) {
			return;
		}

		mRunningAppearances.add(holder);
		dispatchAnimationStarted(holder);

		itemView.animate()
				.alpha(1f)
				.translationY(0f)
				.setDuration(APPEARANCE_DURATION)
				.setInterpolator(new DecelerateInterpolator())
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						itemView.animate().setListener(null);
						mRunningAppearances.remove(holder);
						dispatchAnimationFinished(holder);
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						itemView.setAlpha(1f);
						itemView.setTranslationY(0f);
						mRunningAppearances.remove(holder);
						if (!mEndedHolders.remove(holder)) {
							dispatchAnimationFinished(holder);
						}
					}
				})
				.start();
	}

	@Override
	public void endAnimation(@NonNull RecyclerView.ViewHolder item) {
		View itemView = item.itemView;
		itemView.animate().cancel();
		itemView.setAlpha(1f);
		itemView.setTranslationY(0f);
		boolean wasPending = mPendingAppearances.remove(item);
		boolean wasRunning = mRunningAppearances.remove(item);
		if (wasPending || wasRunning) {
			mEndedHolders.add(item);
		}
		dispatchAnimationFinished(item);
	}

	@Override
	public void endAnimations() {
		for (RecyclerView.ViewHolder holder : new ArrayList<>(mPendingAppearances)) {
			endAnimation(holder);
		}
		for (RecyclerView.ViewHolder holder : new ArrayList<>(mRunningAppearances)) {
			endAnimation(holder);
		}
	}

	@Override
	public boolean isRunning() {
		return !mPendingAppearances.isEmpty() || !mRunningAppearances.isEmpty();
	}
}
