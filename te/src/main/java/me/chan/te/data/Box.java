package me.chan.te.data;

public class Box<Style> implements Element {

	private String mValue;
	private float mWidth;
	private Style mStyle;

	public Box(String value, float width) {
		this(value, width, null);
	}

	public Box(String value, float width, Style style) {
		mValue = value;
		mWidth = width;
		mStyle = style;
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

	public Style getStyle() {
		return mStyle;
	}

	public void setStyle(Style style) {
		mStyle = style;
	}

	@Override
	public float getWidth() {
		return mWidth;
	}
}
