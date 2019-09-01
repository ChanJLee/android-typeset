package me.chan.te.data;

public class Box<T> implements Element {

	private T mValue;

	public Box(T value) {
		mValue = value;
	}

	@Override
	public float getWidth() {
		// TODO
		return 0;
	}
}
