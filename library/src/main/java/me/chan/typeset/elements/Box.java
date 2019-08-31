package me.chan.typeset.elements;

public class Box extends Element {
	public final String content;

	public Box(float width, int index, String content) {
		super(width, index);
		this.content = content;
	}
}
