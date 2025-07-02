package me.chan.texas.text;

import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.RenderOption;

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


	
	public void update(RenderOption option) {

	}

	
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void clear() {
		mTextStyle = null;
		mBackground = null;
		mForeground = null;
	}

	
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void copy(TextStyles styles) {
		mTextStyle = styles.mTextStyle;
		mBackground = styles.mBackground;
		mForeground = styles.mForeground;
	}
}
