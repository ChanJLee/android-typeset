package me.chan.texas.text;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.measurer.Measurer;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.util.Log;


@RestrictTo(LIBRARY)
public class TextAttribute {

	private float mHyphenWidth;
	private float mSpaceWidth;
	private float mSpaceStretch;
	private float mSpaceShrink;
	private float mHyphenHeight;

	public TextAttribute(Measurer measurer) {
		refresh(measurer);
	}

	public void refresh(Measurer measurer) {
		Measurer.CharSequenceSpec spec = measurer.measure("-", 0, 1, null, null);
		mHyphenWidth = spec.getWidth();
		mHyphenHeight = spec.getHeight();

		Texas.TypesetFactor factor = Texas.getTypesetFactor();
		mSpaceWidth = (float) Math.ceil(mHyphenWidth * factor.spaceWidthFactor);
		mSpaceStretch = mHyphenWidth * factor.spaceStretchFactor;
		mSpaceShrink = mHyphenWidth * factor.spaceShrinkFactor;

		i(toString());
	}

	
	public float getHyphenWidth() {
		return mHyphenWidth;
	}

	
	public float getHyphenHeight() {
		return mHyphenHeight;
	}

	
	public float getSpaceWidth() {
		return mSpaceWidth;
	}

	
	public float getSpaceStretch() {
		return mSpaceStretch;
	}

	
	public float getSpaceShrink() {
		return mSpaceShrink;
	}

	private static void i(String msg) {
		Log.i("TexasText", msg);
	}

	@Override
	public String toString() {
		return new StringBuilder(64)
				.append("TextAttribute{")
				.append("mHyphenWidth=")
				.append(mHyphenWidth)
				.append(", mSpaceWidth=")
				.append(mSpaceWidth)
				.append(", mSpaceStretch=")
				.append(mSpaceStretch)
				.append(", mSpaceShrink=")
				.append(mSpaceShrink)
				.append(", mHyphenHeight=")
				.append(mHyphenHeight)
				.append('}')
				.toString();
	}
}
