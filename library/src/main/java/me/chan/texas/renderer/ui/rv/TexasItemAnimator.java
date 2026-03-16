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
import java.util.List;

class TexasItemAnimator extends RecyclerView.ItemAnimator {
	private static final long APPEARANCE_DURATION = 250;
	private static final long DISAPPEARANCE_DURATION = 200;

	private final List<RecyclerView.ViewHolder> mPendingAppearances = new ArrayList<>();
	private final List<RecyclerView.ViewHolder> mPostponedAppearances = new ArrayList<>();
	private final List<RecyclerView.ViewHolder> mRunningAppearances = new ArrayList<>();

	private final List<RecyclerView.ViewHolder> mPendingDisappearances = new ArrayList<>();
	private final List<RecyclerView.ViewHolder> mPostponedDisappearances = new ArrayList<>();
	private final List<RecyclerView.ViewHolder> mRunningDisappearances = new ArrayList<>();

	@Override
	public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
		endAnimation(viewHolder);
		mPendingDisappearances.add(viewHolder);
		return true;
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
		boolean hasDisappearances = !mPendingDisappearances.isEmpty();
		boolean hasAppearances = !mPendingAppearances.isEmpty();
		if (!hasDisappearances && !hasAppearances) {
			return;
		}

		if (hasDisappearances) {
			mPostponedDisappearances.addAll(mPendingDisappearances);
			mPendingDisappearances.clear();
			for (RecyclerView.ViewHolder holder : new ArrayList<>(mPostponedDisappearances)) {
				ViewCompat.postOnAnimation(holder.itemView, () -> {
					if (mPostponedDisappearances.remove(holder)) {
						animateDisappearanceImpl(holder);
					}
				});
			}
		}

		if (hasAppearances) {
			mPostponedAppearances.addAll(mPendingAppearances);
			mPendingAppearances.clear();
			for (RecyclerView.ViewHolder holder : new ArrayList<>(mPostponedAppearances)) {
				ViewCompat.postOnAnimation(holder.itemView, () -> {
					if (mPostponedAppearances.remove(holder)) {
						animateAppearanceImpl(holder);
					}
				});
			}
		}
	}

	private void animateAppearanceImpl(@NonNull RecyclerView.ViewHolder holder) {
		View itemView = holder.itemView;
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
						resetView(itemView);
						if (mRunningAppearances.remove(holder)) {
							dispatchAnimationFinished(holder);
						}
					}
				})
				.start();
	}

	private void animateDisappearanceImpl(@NonNull RecyclerView.ViewHolder holder) {
		View itemView = holder.itemView;
		mRunningDisappearances.add(holder);
		dispatchAnimationStarted(holder);

		int height = itemView.getHeight();
		itemView.animate()
				.alpha(0f)
				.translationY(-height)
				.setDuration(DISAPPEARANCE_DURATION)
				.setInterpolator(new AccelerateInterpolator())
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						itemView.animate().setListener(null);
						resetView(itemView);
						if (mRunningDisappearances.remove(holder)) {
							dispatchAnimationFinished(holder);
						}
					}
				})
				.start();
	}

	private void resetView(View view) {
		view.setAlpha(1f);
		view.setTranslationY(0f);
	}

	@Override
	public void endAnimation(@NonNull RecyclerView.ViewHolder item) {
		item.itemView.animate().cancel();
		resetView(item.itemView);

		boolean needDispatch = mPendingAppearances.remove(item)
				|| mPostponedAppearances.remove(item)
				|| mPendingDisappearances.remove(item)
				|| mPostponedDisappearances.remove(item);
		if (needDispatch) {
			dispatchAnimationFinished(item);
		}
	}

	@Override
	public void endAnimations() {
		List<List<RecyclerView.ViewHolder>> allLists = new ArrayList<>();
		allLists.add(mPendingAppearances);
		allLists.add(mPostponedAppearances);
		allLists.add(mRunningAppearances);
		allLists.add(mPendingDisappearances);
		allLists.add(mPostponedDisappearances);
		allLists.add(mRunningDisappearances);
		for (List<RecyclerView.ViewHolder> list : allLists) {
			for (RecyclerView.ViewHolder holder : new ArrayList<>(list)) {
				endAnimation(holder);
			}
		}
	}

	@Override
	public boolean isRunning() {
		return !mPendingAppearances.isEmpty()
				|| !mPostponedAppearances.isEmpty()
				|| !mRunningAppearances.isEmpty()
				|| !mPendingDisappearances.isEmpty()
				|| !mPostponedDisappearances.isEmpty()
				|| !mRunningDisappearances.isEmpty();
	}
}
