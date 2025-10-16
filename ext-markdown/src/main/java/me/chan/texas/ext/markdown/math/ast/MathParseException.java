package me.chan.texas.ext.markdown.math.ast;

public class MathParseException extends Exception {
	int position;

	public MathParseException(String message, int position) {
		super(message + " at position " + position);
		this.position = position;
	}

	public int getPosition() {
		return position;
	}
}
