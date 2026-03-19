package me.chan.texas.text.layout;

import androidx.annotation.RestrictTo;

import me.chan.texas.measurer.Measurer;
import me.chan.texas.text.TextAttribute;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SymbolGlue extends Glue {

	private static final float MAGIC_FACTOR = 0.7f;

	private TextSpan mTextBox;

	private SymbolGlue() {
	}

	@Override
	public void measure(Measurer measurer, TextAttribute textAttribute) {
		mTextBox.measure(measurer, textAttribute);
		mWidth = mTextBox.getWidth();
		mShrink = mWidth * MAGIC_FACTOR;
		mStretch = 0;
	}

	public static SymbolGlue obtain(TextSpan span) {
		SymbolGlue glue = new SymbolGlue();
		glue.mTextBox = span;
		glue.mWidth = span.getWidth();
		glue.mShrink = glue.mWidth * MAGIC_FACTOR;
		glue.mStretch = 0;
		glue.mRefreshFlag = 0;
		return glue;
	}

	@Override
	public String toString() {
		return "SymbolGlue{" +
				"mStretch=" + mStretch +
				", mShrink=" + mShrink +
				", mWidth=" + mWidth +
				'}';
	}
}
