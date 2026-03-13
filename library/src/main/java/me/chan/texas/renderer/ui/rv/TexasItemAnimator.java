package me.chan.texas.renderer.ui.rv;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

class TexasItemAnimator extends RecyclerView.ItemAnimator {
	private static final String TAG = "TexasItemAnim";

	@Override
	public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
		Log.d(TAG, "disappearance");
		return false;
	}

	@Override
	public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
		Log.d(TAG, "appearance");
		return false;
	}

	@Override
	public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
		Log.d(TAG, "persistence");
		return false;
	}

	@Override
	public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
		Log.d(TAG, "change");
		return false;
	}

	@Override
	public void runPendingAnimations() {

	}

	@Override
	public void endAnimation(@NonNull RecyclerView.ViewHolder item) {

	}

	@Override
	public void endAnimations() {

	}

	@Override
	public boolean isRunning() {
		return false;
	}
}
