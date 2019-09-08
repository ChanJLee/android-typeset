package me.chan.te.data;

public class Box<Extra> implements Element {

	private String mText;
	private float mWidth;
	private float mHeight;
	private Extra mExtra;
	private boolean mPenalty = false;
	private boolean mDoNotDraw = false;

	public Box(String text, float width, float height) {
		this(text, width, height, null);
	}

	public Box(String text, float width, float height, Extra extra) {
		mText = text;
		mWidth = width;
		mHeight = height;
		mExtra = extra;
	}

	public void setWidth(float width) {
		mWidth = width;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
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

	public void setHeight(float height) {
		mHeight = height;
	}

	public boolean canMerge(Box<?> other) {
		return true;
	}

	public boolean isPenalty() {
		return mPenalty;
	}

	public void setPenalty(boolean penalty) {
		mPenalty = penalty;
	}

	public void setDoNotDraw(boolean doNotDraw) {
		mDoNotDraw = doNotDraw;
	}

	public boolean isDoNotDraw() {
		return mDoNotDraw;
	}

	@Override
	public String toString() {
		return "Box{" +
				"mText='" + mText + '\'' +
				", mWidth=" + mWidth +
				", mHeight=" + mHeight +
				", mExtra=" + mExtra +
				", mPenalty=" + mPenalty +
				'}';
	}
}
