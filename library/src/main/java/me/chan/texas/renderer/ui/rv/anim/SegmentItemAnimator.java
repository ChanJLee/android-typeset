package me.chan.texas.renderer.ui.rv.anim;

import android.animation.Animator;
import android.view.View;

import androidx.annotation.Nullable;

import me.chan.texas.text.Segment;

public abstract class SegmentItemAnimator {

	public final Animator createAnimator(Segment segment, View itemView, ItemAnimType type) {
		return onCreateAnimator(segment, itemView, type);
	}

	/**
	 * @param segment  segment
	 * @param itemView itemView
	 * @param type     type
	 * @return Animator, 返回空则代表不显示动画
	 */
	@Nullable
	protected abstract Animator onCreateAnimator(Segment segment, View itemView, ItemAnimType type);
}
