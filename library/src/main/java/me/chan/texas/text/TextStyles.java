package me.chan.texas.text;

public class TextStyles {
	private TextStyle mTextStyle;
	private Appearance mBackground;
	private Appearance mForeground;

	public TextStyle getTextStyle() {
		return mTextStyle;
	}

	public void setTextStyle(TextStyle textStyle) {
		mTextStyle = textStyle;
	}

	public Appearance getBackground() {
		return mBackground;
	}

	public void setBackground(Appearance background) {
		mBackground = background;
	}

	public Appearance getForeground() {
		return mForeground;
	}

	public void setForeground(Appearance foreground) {
		mForeground = foreground;
	}

	public void clear() {
		mTextStyle = null;
		mBackground = null;
		mForeground = null;
	}

	public void copy(TextStyles styles) {
		mTextStyle = styles.mTextStyle;
		mBackground = styles.mBackground;
		mForeground = styles.mForeground;
	}
}
