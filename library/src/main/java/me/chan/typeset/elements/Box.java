package me.chan.typeset.elements;

public class Box extends Element {
	public final String content;

	public Box(float width, String content) {
		super(width);
		this.content = content;
	}
}
