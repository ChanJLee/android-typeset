package me.chan.texas.renderer;

import android.graphics.Typeface;

import androidx.annotation.NonNull;

import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.HyphenStrategy;

public class RenderOption {
	private int mTextColor;
	private Typeface mTypeface;
	private float mTextSize;
	private float mLineSpace;
	private boolean mIndentEnable;
	private int mSelectedBackgroundColor;
	private int mSelectedTextColor;
	private float mSegmentSpace;
	private BreakStrategy mBreakStrategy;
	private boolean mWordSelectable;
	private boolean mEnableDebug;
	private int mSpanSelectedBackgroundColor;
	private int mSpanSelectedTextColor;
	private HyphenStrategy mHyphenStrategy;

	public RenderOption() {
		mHyphenStrategy = HyphenStrategy.US;
	}

	public RenderOption(RenderOption other) {
		mTextColor = other.mTextColor;
		mTypeface = other.mTypeface;
		mTextSize = other.mTextSize;
		mLineSpace = other.mLineSpace;
		mIndentEnable = other.mIndentEnable;
		mSelectedBackgroundColor = other.mSelectedBackgroundColor;
		mSelectedTextColor = other.mSelectedTextColor;
		mSegmentSpace = other.mSegmentSpace;
		mBreakStrategy = other.mBreakStrategy;
		mWordSelectable = other.mWordSelectable;
		mEnableDebug = other.mEnableDebug;
		mSpanSelectedTextColor = other.mSpanSelectedTextColor;
		mSpanSelectedBackgroundColor = other.mSpanSelectedBackgroundColor;
		mHyphenStrategy = other.mHyphenStrategy;
	}

	public boolean isEnableDebug() {
		return mEnableDebug;
	}

	public RenderOption setEnableDebug(boolean enableDebug) {
		mEnableDebug = enableDebug;
		return this;
	}

	public boolean isWordSelectable() {
		return mWordSelectable;
	}

	public RenderOption setWordSelectable(boolean wordSelectable) {
		mWordSelectable = wordSelectable;
		return this;
	}

	public BreakStrategy getBreakStrategy() {
		return mBreakStrategy;
	}

	public RenderOption setBreakStrategy(BreakStrategy breakStrategy) {
		mBreakStrategy = breakStrategy;
		return this;
	}

	public int getTextColor() {
		return mTextColor;
	}

	public RenderOption setTextColor(int textColor) {
		mTextColor = textColor;
		return this;
	}

	public Typeface getTypeface() {
		return mTypeface;
	}

	public RenderOption setTypeface(@NonNull Typeface typeface) {
		mTypeface = typeface;
		return this;
	}

	public float getTextSize() {
		return mTextSize;
	}

	public RenderOption setTextSize(float textSize) {
		mTextSize = textSize;
		return this;
	}

	public float getLineSpace() {
		return mLineSpace;
	}

	public RenderOption setLineSpace(float lineSpace) {
		mLineSpace = lineSpace;
		return this;
	}

	public boolean isIndentEnable() {
		return mIndentEnable;
	}

	public RenderOption setIndentEnable(boolean indentEnable) {
		mIndentEnable = indentEnable;
		return this;
	}

	public int getSelectedBackgroundColor() {
		return mSelectedBackgroundColor;
	}

	public RenderOption setSelectedBackgroundColor(int selectedBackgroundColor) {
		mSelectedBackgroundColor = selectedBackgroundColor;
		return this;
	}

	public int getSelectedTextColor() {
		return mSelectedTextColor;
	}

	public int getSpanSelectedBackgroundColor() {
		return mSpanSelectedBackgroundColor;
	}

	public RenderOption setSpanSelectedBackgroundColor(int selectedSpanBackgroundColor) {
		mSpanSelectedBackgroundColor = selectedSpanBackgroundColor;
		return this;
	}

	public int getSpanSelectedTextColor() {
		return mSpanSelectedTextColor;
	}

	public RenderOption setSpanSelectedTextColor(int spanSelectedTextColor) {
		mSpanSelectedTextColor = spanSelectedTextColor;
		return this;
	}

	public RenderOption setSelectedTextColor(int selectedTextColor) {
		mSelectedTextColor = selectedTextColor;
		return this;
	}

	public float getSegmentSpace() {
		return mSegmentSpace;
	}

	public RenderOption setSegmentSpace(float segmentSpace) {
		mSegmentSpace = segmentSpace;
		return this;
	}

	public HyphenStrategy getHyphenStrategy() {
		return mHyphenStrategy;
	}

	public void setHyphenStrategy(HyphenStrategy hyphenStrategy) {
		mHyphenStrategy = hyphenStrategy;
	}
}
