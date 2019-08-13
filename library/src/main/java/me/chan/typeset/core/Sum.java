package me.chan.typeset.core;

public class Sum {
	public float width = 0;
	public float stretch = 0;
	public float shrink = 0;

	public Sum(Sum other) {
		this.width = other.width;
		this.shrink = other.shrink;
		this.stretch = other.stretch;
	}

	public Sum() {
	}
}
