package me.chan.te.renderer;

import android.graphics.Typeface;
import android.support.annotation.NonNull;

import me.chan.te.text.BreakStrategy;

class RenderOption {
	private int mTextColor;
	private Typeface mTypeface;
	private float mTextSize;
	private float mLineSpace;
	private boolean mIndentEnable;
	private int mSelectedBgColor;
	private int mSelectedTextColor;
	private float mSegmentSpace;
	private BreakStrategy mBreakStrategy;
	private RendererMode mRendererMode;

	RenderOption() {
	}

	RenderOption(RenderOption other) {
		mTextColor = other.mTextColor;
		mTypeface = other.mTypeface;
		mTextSize = other.mTextSize;
		mLineSpace = other.mLineSpace;
		mIndentEnable = other.mIndentEnable;
		mSelectedBgColor = other.mSelectedBgColor;
		mSelectedTextColor = other.mSelectedTextColor;
		mSegmentSpace = other.mSegmentSpace;
		mBreakStrategy = other.mBreakStrategy;
		mRendererMode = other.mRendererMode;
	}

	public RendererMode getRendererMode() {
		return mRendererMode;
	}

	public void setRendererMode(RendererMode rendererMode) {
		mRendererMode = rendererMode;
	}

	public BreakStrategy getBreakStrategy() {
		return mBreakStrategy;
	}

	public void setBreakStrategy(BreakStrategy breakStrategy) {
		mBreakStrategy = breakStrategy;
	}

	public int getTextColor() {
		return mTextColor;
	}

	public void setTextColor(int textColor) {
		mTextColor = textColor;
	}

	public Typeface getTypeface() {
		return mTypeface;
	}

	public void setTypeface(@NonNull Typeface typeface) {
		mTypeface = typeface;
	}

	public float getTextSize() {
		return mTextSize;
	}

	public void setTextSize(float textSize) {
		mTextSize = textSize;
	}

	public float getLineSpace() {
		return mLineSpace;
	}

	public void setLineSpace(float lineSpace) {
		mLineSpace = lineSpace;
	}

	public boolean isIndentEnable() {
		return mIndentEnable;
	}

	public void setIndentEnable(boolean indentEnable) {
		mIndentEnable = indentEnable;
	}

	public int getSelectedBgColor() {
		return mSelectedBgColor;
	}

	public void setSelectedBgColor(int selectedBgColor) {
		mSelectedBgColor = selectedBgColor;
	}

	public int getSelectedTextColor() {
		return mSelectedTextColor;
	}

	public void setSelectedTextColor(int selectedTextColor) {
		mSelectedTextColor = selectedTextColor;
	}

	public float getSegmentSpace() {
		return mSegmentSpace;
	}

	public void setSegmentSpace(float segmentSpace) {
		mSegmentSpace = segmentSpace;
	}
}
