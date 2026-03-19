package me.chan.texas.renderer;

import android.animation.Animator;
import android.view.View;

import androidx.annotation.NonNull;

import me.chan.texas.text.Segment;

public class SegmentAnimatorAdapter extends TexasView.SegmentAnimator {

	@Override
	protected Animator onCreateAddAnimator(@NonNull Segment segment, @NonNull View view) {
		return null;
	}

	@Override
	protected Animator onCreateRemoveAnimator(@NonNull Segment segment, @NonNull View view) {
		return null;
	}

	@Override
	protected Animator onCreateMoveAnimator(@NonNull Segment segment, @NonNull View view, int fromX, int fromY, int toX, int toY) {
		return null;
	}
}
