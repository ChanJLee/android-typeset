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

	public Glue(float width, float stretch, float shrink) {
		super(width);
		this.stretch = stretch;
		this.shrink = shrink;
	}
}
