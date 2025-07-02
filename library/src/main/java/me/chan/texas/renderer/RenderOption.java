package me.chan.texas.renderer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.HyphenStrategy;
import me.chan.texas.text.TextGravity;
import me.chan.texas.utils.TexasUtils;


public class RenderOption {
	static final RenderOption DEFAULT = new RenderOption();

	
	private int mTextColor;
	private Typeface mTypeface;
	private float mTextSize;
	private float mLineSpacingExtra;
	private int mSelectedBackgroundColor;
	private int mSelectedTextColor;
	private BreakStrategy mBreakStrategy = BreakStrategy.SIMPLE;
	private boolean mWordSelectable;
	private boolean mDebugEnable;
	private boolean mOnDrawTsDebugEnable;
	private boolean mAsyncDrawTsDebugEnable;
	private boolean mFullWithSymbolOptimizationEnable = true;
	private boolean mDragToSelectEnable = true;
	private int mTextGravity = TextGravity.START | TextGravity.TOP;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RenderOption that = (RenderOption) o;

		if (mTextColor != that.mTextColor) return false;
		if (Float.compare(that.mTextSize, mTextSize) != 0) return false;
		if (Float.compare(that.mLineSpacingExtra, mLineSpacingExtra) != 0) return false;
		if (mSelectedBackgroundColor != that.mSelectedBackgroundColor) return false;
		if (mSelectedTextColor != that.mSelectedTextColor) return false;
		if (mWordSelectable != that.mWordSelectable) return false;
		if (mDebugEnable != that.mDebugEnable) return false;
		if (mOnDrawTsDebugEnable != that.mOnDrawTsDebugEnable) return false;
		if (mAsyncDrawTsDebugEnable != that.mAsyncDrawTsDebugEnable) return false;
		if (mSelectedByLongClickBackgroundColor != that.mSelectedByLongClickBackgroundColor)
			return false;
		if (mSelectedByLongClickTextColor != that.mSelectedByLongClickTextColor) return false;
		if (mEnableLazyRender != that.mEnableLazyRender) return false;
		if (mSpanHighlightTextColor != that.mSpanHighlightTextColor) return false;
		if (mLoadingBackgroundColor != that.mLoadingBackgroundColor) return false;
		if (mDrawEmoticonSelection != that.mDrawEmoticonSelection) return false;
		if (mDragViewColor != that.mDragViewColor) return false;
		if (mCompatMode != that.mCompatMode) return false;
		if (Float.compare(that.mSelectedBackgroundRoundRadius, mSelectedBackgroundRoundRadius) != 0)
			return false;
		if (!TexasUtils.equals(mTypeface, that.mTypeface))
			return false;
		if (mBreakStrategy != that.mBreakStrategy) return false;
		if (mHyphenStrategy != that.mHyphenStrategy) return false;
		if (mDragToSelectEnable != that.mDragToSelectEnable) return false;
		if (mTextGravity != that.mTextGravity) return false;
		return mHyphenStrategy == that.mHyphenStrategy;
	}

	@Override
	public int hashCode() {
		int result = mTextColor;
		result = 31 * result + (mTypeface != null ? mTypeface.hashCode() : 0);
		result = 31 * result + (mTextSize != +0.0f ? Float.floatToIntBits(mTextSize) : 0);
		result = 31 * result + (mLineSpacingExtra != +0.0f ? Float.floatToIntBits(mLineSpacingExtra) : 0);
		result = 31 * result + mSelectedBackgroundColor;
		result = 31 * result + mSelectedTextColor;
		result = 31 * result + (mBreakStrategy != null ? mBreakStrategy.hashCode() : 0);
		result = 31 * result + (mWordSelectable ? 1 : 0);
		result = 31 * result + (mDebugEnable ? 1 : 0);
		result = 31 * result + (mOnDrawTsDebugEnable ? 1 : 0);
		result = 31 * result + (mAsyncDrawTsDebugEnable ? 1 : 0);
		result = 31 * result + mSelectedByLongClickBackgroundColor;
		result = 31 * result + mSelectedByLongClickTextColor;
		result = 31 * result + (mHyphenStrategy != null ? mHyphenStrategy.hashCode() : 0);
		result = 31 * result + (mEnableLazyRender ? 1 : 0);
		result = 31 * result + mSpanHighlightTextColor;
		result = 31 * result + mLoadingBackgroundColor;
		result = 31 * result + (mDrawEmoticonSelection ? 1 : 0);
		result = 31 * result + mDragViewColor;
		result = 31 * result + (mCompatMode ? 1 : 0);
		result = 31 * result + (mSelectedBackgroundRoundRadius != +0.0f ? Float.floatToIntBits(mSelectedBackgroundRoundRadius) : 0);
		result = 31 * result + (mFullWithSymbolOptimizationEnable ? 1 : 0);
		result = 31 * result + (mDragToSelectEnable ? 1 : 0);
		result = 31 * result + mTextGravity;
		return result;
	}

	private int mSelectedByLongClickBackgroundColor;
	private int mSelectedByLongClickTextColor;
	private HyphenStrategy mHyphenStrategy;
	private boolean mEnableLazyRender;
	private int mSpanHighlightTextColor;
	private int mLoadingBackgroundColor;
	private boolean mDrawEmoticonSelection = false;
	private int mDragViewColor;
	private boolean mCompatMode = false;
	private float mSelectedBackgroundRoundRadius;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public RenderOption() {
		mHyphenStrategy = HyphenStrategy.US;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public RenderOption(RenderOption other) {
		this.mTextColor = other.mTextColor;
		this.mTypeface = other.mTypeface;
		this.mTextSize = other.mTextSize;
		this.mLineSpacingExtra = other.mLineSpacingExtra;
		this.mSelectedBackgroundColor = other.mSelectedBackgroundColor;
		this.mSelectedTextColor = other.mSelectedTextColor;
		this.mBreakStrategy = other.mBreakStrategy;
		this.mWordSelectable = other.mWordSelectable;
		this.mDebugEnable = other.mDebugEnable;
		this.mSelectedByLongClickBackgroundColor = other.mSelectedByLongClickBackgroundColor;
		this.mSelectedByLongClickTextColor = other.mSelectedByLongClickTextColor;
		this.mHyphenStrategy = other.mHyphenStrategy;
		this.mEnableLazyRender = other.mEnableLazyRender;
		this.mSpanHighlightTextColor = other.mSpanHighlightTextColor;
		this.mLoadingBackgroundColor = other.mLoadingBackgroundColor;
		this.mDrawEmoticonSelection = other.mDrawEmoticonSelection;
		this.mDragViewColor = other.mDragViewColor;
		this.mCompatMode = other.mCompatMode;
		this.mSelectedBackgroundRoundRadius = other.mSelectedBackgroundRoundRadius;
		this.mAsyncDrawTsDebugEnable = other.mAsyncDrawTsDebugEnable;
		this.mOnDrawTsDebugEnable = other.mAsyncDrawTsDebugEnable;
		this.mFullWithSymbolOptimizationEnable = other.mFullWithSymbolOptimizationEnable;
		this.mDragToSelectEnable = other.mDragToSelectEnable;
		this.mTextGravity = other.mTextGravity;
	}

	
	public boolean isDrawEmoticonSelection() {
		return mDrawEmoticonSelection;
	}

	
	public RenderOption setDrawEmoticonSelection(boolean enable) {
		mDrawEmoticonSelection = enable;
		return this;
	}

	
	public boolean isDebugEnable() {
		return mDebugEnable;
	}

	
	public RenderOption setDebugEnable(boolean debugEnable) {
		mDebugEnable = debugEnable;
		return this;
	}

	public int getDragViewColor() {
		return mDragViewColor;
	}

	public RenderOption setDragViewColor(@ColorInt int dragViewColor) {
		mDragViewColor = dragViewColor;
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

	
	public RenderOption setTextColor(int color) {
		mTextColor = color;
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

	
	public RenderOption setTextSize(@NonNull Context context, int unit, @FloatRange(from = 0) float value) {
		Resources resources = context.getResources();
		DisplayMetrics displayMetrics = resources.getDisplayMetrics();
		return setTextSize(
				TypedValue.applyDimension(unit, value, displayMetrics)
		);
	}

	
	public float getLineSpacingExtra() {
		return mLineSpacingExtra;
	}

	
	public RenderOption setLineSpacingExtra(float lineSpace) {
		mLineSpacingExtra = lineSpace;
		return this;
	}

	
	public int getSelectedBackgroundColor() {
		return mSelectedBackgroundColor;
	}

	
	public RenderOption setSelectedBackgroundColor(int color) {
		mSelectedBackgroundColor = color;
		return this;
	}

	
	public int getSelectedTextColor() {
		return mSelectedTextColor;
	}

	
	public RenderOption setSelectedTextColor(int color) {
		mSelectedTextColor = color;
		return this;
	}

	
	public int getSelectedByLongClickBackgroundColor() {
		return mSelectedByLongClickBackgroundColor;
	}

	
	public RenderOption setSelectedByLongClickBackgroundColor(int color) {
		mSelectedByLongClickBackgroundColor = color;
		return this;
	}

	
	public int getSelectedByLongClickTextColor() {
		return mSelectedByLongClickTextColor;
	}

	
	public RenderOption setSelectedByLongClickTextColor(int color) {
		mSelectedByLongClickTextColor = color;
		return this;
	}

	
	public HyphenStrategy getHyphenStrategy() {
		return mHyphenStrategy;
	}

	
	public RenderOption setHyphenStrategy(HyphenStrategy hyphenStrategy) {
		mHyphenStrategy = hyphenStrategy;
		return this;
	}

	
	public boolean isEnableLazyRender() {
		return mEnableLazyRender;
	}

	
	public RenderOption setEnableLazyRender(boolean enable) {
		mEnableLazyRender = enable;
		return this;
	}

	
	public int getSpanHighlightTextColor() {
		return mSpanHighlightTextColor;
	}

	
	public RenderOption setSpanHighlightTextColor(int color) {
		mSpanHighlightTextColor = color;
		return this;
	}

	
	public int getLoadingBackgroundColor() {
		return mLoadingBackgroundColor;
	}

	
	public RenderOption setLoadingBackgroundColor(int color) {
		mLoadingBackgroundColor = color;
		return this;
	}

	
	public boolean isCompatMode() {
		return mCompatMode;
	}

	
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public RenderOption setCompatMode(boolean enable) {
		mCompatMode = enable;
		return this;
	}

	
	public float getSelectedBackgroundRoundRadius() {
		return mSelectedBackgroundRoundRadius;
	}

	
	public RenderOption setSelectedBackgroundRoundRadius(float selectedBackgroundRoundRadius) {
		mSelectedBackgroundRoundRadius = selectedBackgroundRoundRadius;
		return this;
	}

	
	public boolean isOnDrawTsDebugEnable() {
		return mOnDrawTsDebugEnable;
	}

	
	public void setOnDrawTsDebugEnable(boolean enable) {
		mOnDrawTsDebugEnable = enable;
	}

	
	public boolean isAsyncDrawTsDebugEnable() {
		return mAsyncDrawTsDebugEnable;
	}

	
	public void setAsyncDrawTsDebugEnable(boolean enable) {
		mAsyncDrawTsDebugEnable = enable;
	}

	
	public boolean isFullWithSymbolOptimizationEnable() {
		return mFullWithSymbolOptimizationEnable;
	}

	
	public void setFullWithSymbolOptimizationEnable(boolean enable) {
		mFullWithSymbolOptimizationEnable = enable;
	}

	
	public boolean isDragToSelectEnable() {
		return mDragToSelectEnable;
	}

	
	public void setDragToSelectEnable(boolean enable) {
		mDragToSelectEnable = enable;
	}

	
	public int getTextGravity() {
		return mTextGravity;
	}

	
	public void setTextGravity(int gravity) {
		mTextGravity = adviceTextGravityMask(gravity);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static int adviceTextGravityMask(@TextGravity.GravityMask int gravity) {
		if ((gravity & TextGravity.HORIZONTAL_MASK) == 0) {
			gravity |= TextGravity.START;
		}

		if ((gravity & TextGravity.VERTICAL_MASK) == 0) {
			gravity |= TextGravity.TOP;
		}

		return gravity;
	}

	@Override
	public String toString() {
		return "RenderOption{" +
				"mTextColor=" + mTextColor +
				", mTypeface=" + mTypeface +
				", mTextSize=" + mTextSize +
				", mLineSpace=" + mLineSpacingExtra +
				", mSelectedBackgroundColor=" + mSelectedBackgroundColor +
				", mSelectedTextColor=" + mSelectedTextColor +
				", mBreakStrategy=" + mBreakStrategy +
				", mWordSelectable=" + mWordSelectable +
				", mEnableDebug=" + mDebugEnable +
				", mEnableOnDrawTsDebug=" + mOnDrawTsDebugEnable +
				", mEnableAsyncDrawTsDebug=" + mAsyncDrawTsDebugEnable +
				", mSelectedByLongClickBackgroundColor=" + mSelectedByLongClickBackgroundColor +
				", mSelectedByLongClickTextColor=" + mSelectedByLongClickTextColor +
				", mHyphenStrategy=" + mHyphenStrategy +
				", mEnableLazyRender=" + mEnableLazyRender +
				", mSpanHighlightTextColor=" + mSpanHighlightTextColor +
				", mLoadingBackgroundColor=" + mLoadingBackgroundColor +
				", mDrawEmoticonSelection=" + mDrawEmoticonSelection +
				", mDragViewColor=" + mDragViewColor +
				", mCompatMode=" + mCompatMode +
				", mSelectedBackgroundRoundRadius=" + mSelectedBackgroundRoundRadius +
				", mEnableFullWithSymbolOptimization=" + mFullWithSymbolOptimizationEnable +
				", mEnableDragToSelect=" + mDragToSelectEnable +
				", mGravity=" + mTextGravity +
				'}';
	}
}
