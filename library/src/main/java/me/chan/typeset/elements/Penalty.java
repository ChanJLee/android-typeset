package me.chan.typeset.elements;

public class Penalty extends Element {
	/**
	 * where3 = 1 if xiis a flagged penalty, otherwise3 = 0.
	 */
	public final boolean flag;
	/**
	 * where pi is the penalty at xi if ti=‘penalty’, otherwise pi= 0;
	 */
	public final float penalty;

	public Penalty(float width, int index, float penalty, boolean flag) {
		super(width, index);
		this.penalty = penalty;
		this.flag = flag;
	}
}
