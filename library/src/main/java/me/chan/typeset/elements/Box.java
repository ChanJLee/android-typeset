package me.chan.typeset.elements;

public class Box extends Element {
	public final String mContent;

	public Box(float width, String content) {
		super(width);
		mContent = content;
	}
}
