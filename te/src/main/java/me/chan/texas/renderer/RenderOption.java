package me.chan.texas.renderer;

import android.graphics.Typeface;

import androidx.annotation.NonNull;

import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.UnderLine;

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
	private RendererMode mRendererMode;
	private boolean mWordSelectable;
	private boolean mEnableDebug;
	private int mSpanSelectedBackgroundColor;
	private int mSpanSelectedTextColor;
	private UnderLine mUnderLine;

	public RenderOption() {
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
		mRendererMode = other.mRendererMode;
		mWordSelectable = other.mWordSelectable;
		mEnableDebug = other.mEnableDebug;
		mSpanSelectedTextColor = other.mSpanSelectedTextColor;
		mSpanSelectedBackgroundColor = other.mSpanSelectedBackgroundColor;
		mUnderLine = other.mUnderLine;
	}

	public boolean isEnableDebug() {
		return mEnableDebug;
	}

	public void setEnableDebug(boolean enableDebug) {
		mEnableDebug = enableDebug;
	}

	public boolean isWordSelectable() {
		return mWordSelectable;
	}

	public void setWordSelectable(boolean wordSelectable) {
		mWordSelectable = wordSelectable;
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

	public int getSelectedBackgroundColor() {
		return mSelectedBackgroundColor;
	}

	public void setSelectedBackgroundColor(int selectedBackgroundColor) {
		mSelectedBackgroundColor = selectedBackgroundColor;
	}

	public int getSelectedTextColor() {
		return mSelectedTextColor;
	}

	public int getSpanSelectedBackgroundColor() {
		return mSpanSelectedBackgroundColor;
	}

	public void setSpanSelectedBackgroundColor(int selectedSpanBackgroundColor) {
		mSpanSelectedBackgroundColor = selectedSpanBackgroundColor;
	}

	public int getSpanSelectedTextColor() {
		return mSpanSelectedTextColor;
	}

	public void setSpanSelectedTextColor(int spanSelectedTextColor) {
		mSpanSelectedTextColor = spanSelectedTextColor;
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

	public UnderLine getUnderLine() {
		return mUnderLine;
	}

	public void setUnderLine(UnderLine underLine) {
		mUnderLine = underLine;
	}
}
