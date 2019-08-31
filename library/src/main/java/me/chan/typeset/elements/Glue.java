package me.chan.typeset.elements;

public class Glue extends Element {
	/**
	 * stretchability
	 */
	public float stretch;
	/**
	 * shrinkability
	 */
	public float shrink;

	public Glue(float width, int index, float stretch, float shrink) {
		super(width, index);
		this.stretch = stretch;
		this.shrink = shrink;
	}
}
