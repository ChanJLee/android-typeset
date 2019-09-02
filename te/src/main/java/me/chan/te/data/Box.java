package me.chan.te.data;

public class Box<T> implements Element {

	private T mValue;
	private float mWidth;

	public Box(T value, float width) {
		mValue = value;
		mWidth = width;
	}

	@Override
	public float getWidth() {
		return mWidth;
	}
}
