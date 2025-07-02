package me.chan.texas.text;

import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.RenderOption;

public class TextStyles {
	private TextStyle mTextStyle;
	private Appearance mBackground;
	private Appearance mForeground;

	/**
	 * @return the textStyle
	 */
	public TextStyle getTextStyle() {
		return mTextStyle;
	}

	/**
	 * @param textStyle the textStyle to set
	 */
	public void setTextStyle(TextStyle textStyle) {
		mTextStyle = textStyle;
	}

	/**
	 * @return the background
	 */
	public Appearance getBackground() {
		return mBackground;
	}

	/**
	 * @param background the background to set
	 */
	public void setBackground(Appearance background) {
		mBackground = background;
	}

	/**
	 * @return the foreground
	 */
	public Appearance getForeground() {
		return mForeground;
	}

	/**
	 * @param foreground the foreground to set
	 */
	public void setForeground(Appearance foreground) {
		mForeground = foreground;
	}


	/**
	 * @param option the render option
	 */
	public void update(RenderOption option) {

	}

	/**
	 * Clear all styles
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void clear() {
		mTextStyle = null;
		mBackground = null;
		mForeground = null;
	}

	/**
	 * @param styles the styles to copy
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void copy(TextStyles styles) {
		mTextStyle = styles.mTextStyle;
		mBackground = styles.mBackground;
		mForeground = styles.mForeground;
	}
}
