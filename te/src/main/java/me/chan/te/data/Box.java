package me.chan.te.data;

public class Box<Extra> implements Element {

	private String mValue;
	private float mWidth;
	private float mHeight;
	private Extra mExtra;

	public Box(String value, float width, float height) {
		this(value, width, height, null);
	}

	public Box(String value, float width, float height, Extra extra) {
		mValue = value;
		mWidth = width;
		mHeight = height;
		mExtra = extra;
	}

	public void setWidth(float width) {
		mWidth = width;
	}

	public String getValue() {
		return mValue;
	}

	public void setValue(String value) {
		mValue = value;
	}

	public Extra getExtra() {
		return mExtra;
	}

	public void setExtra(Extra extra) {
		mExtra = extra;
	}

	@Override
	public float getWidth() {
		return mWidth;
	}

	public float getHeight() {
		return mHeight;
	}

	public boolean canMerge(Box<?> other) {
		return true;
	}
}
