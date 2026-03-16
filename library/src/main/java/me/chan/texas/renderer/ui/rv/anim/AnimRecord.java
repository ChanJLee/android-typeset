package me.chan.texas.renderer.ui.rv.anim;

import android.animation.Animator;

class AnimRecord {
	public static final int PHASE_PENDING = 0;
	public static final int PHASE_POSTPONED = 1;
	public static final int PHASE_RUNNING = 2;

	public final SegmentAnimType type;
	public int phase;
	public final Animator animator;

	AnimRecord(SegmentAnimType type, Animator animator) {
		this.type = type;
		this.animator = animator;
		this.phase = PHASE_PENDING;
	}
}