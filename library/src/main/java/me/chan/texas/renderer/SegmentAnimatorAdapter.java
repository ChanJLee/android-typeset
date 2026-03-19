package me.chan.texas.renderer;

import android.animation.Animator;
import android.view.View;

import me.chan.texas.text.Segment;

public class SegmentAnimatorAdapter extends TexasView.SegmentAnimator {

	@Override
	protected Animator onCreateAddAnimator(Segment segment, View view) {
		return null;
	}

	@Override
	protected Animator onCreateRemoveAnimator(Segment segment, View view) {
		return null;
	}

	@Override
	protected Animator onCreateMoveAnimator(Segment segment, View view, int fromX, int fromY, int toX, int toY) {
		return null;
	}
}
