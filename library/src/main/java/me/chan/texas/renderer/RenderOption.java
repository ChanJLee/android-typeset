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
import me.chan.texas.utils.TexasUtils;

/**
 * 渲染参数
 */
public class RenderOption {
	static final RenderOption DEFAULT = new RenderOption();

	/**
	 * 增加字段记得需要更新：
	 * 1. copy ctor
	 * 2. equals
	 * 3. hashCode
	 */
	private int mTextColor;
	private Typeface mTypeface;
	private float mTextSize;
	private float mLineSpace;
	private int mSelectedBackgroundColor;
	private int mSelectedTextColor;
	private BreakStrategy mBreakStrategy;
	private boolean mWordSelectable;
	private boolean mEnableDebug;
	private boolean mEnableOnDrawTsDebug;
	private boolean mEnableAsyncDrawTsDebug;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RenderOption that = (RenderOption) o;

		if (mTextColor != that.mTextColor) return false;
		if (Float.compare(that.mTextSize, mTextSize) != 0) return false;
		if (Float.compare(that.mLineSpace, mLineSpace) != 0) return false;
		if (mSelectedBackgroundColor != that.mSelectedBackgroundColor) return false;
		if (mSelectedTextColor != that.mSelectedTextColor) return false;
		if (mWordSelectable != that.mWordSelectable) return false;
		if (mEnableDebug != that.mEnableDebug) return false;
		if (mEnableOnDrawTsDebug != that.mEnableOnDrawTsDebug) return false;
		if (mEnableAsyncDrawTsDebug != that.mEnableAsyncDrawTsDebug) return false;
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
		return mHyphenStrategy == that.mHyphenStrategy;
	}

	@Override
	public int hashCode() {
		int result = mTextColor;
		result = 31 * result + (mTypeface != null ? mTypeface.hashCode() : 0);
		result = 31 * result + (mTextSize != +0.0f ? Float.floatToIntBits(mTextSize) : 0);
		result = 31 * result + (mLineSpace != +0.0f ? Float.floatToIntBits(mLineSpace) : 0);
		result = 31 * result + mSelectedBackgroundColor;
		result = 31 * result + mSelectedTextColor;
		result = 31 * result + (mBreakStrategy != null ? mBreakStrategy.hashCode() : 0);
		result = 31 * result + (mWordSelectable ? 1 : 0);
		result = 31 * result + (mEnableDebug ? 1 : 0);
		result = 31 * result + (mEnableOnDrawTsDebug ? 1 : 0);
		result = 31 * result + (mEnableAsyncDrawTsDebug ? 1 : 0);
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
		this.mLineSpace = other.mLineSpace;
		this.mSelectedBackgroundColor = other.mSelectedBackgroundColor;
		this.mSelectedTextColor = other.mSelectedTextColor;
		this.mBreakStrategy = other.mBreakStrategy;
		this.mWordSelectable = other.mWordSelectable;
		this.mEnableDebug = other.mEnableDebug;
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
		this.mEnableAsyncDrawTsDebug = other.mEnableAsyncDrawTsDebug;
		this.mEnableOnDrawTsDebug = other.mEnableAsyncDrawTsDebug;
	}

	/**
	 * @return 表情符号是否渲染选中效果
	 */
	public boolean isDrawEmoticonSelection() {
		return mDrawEmoticonSelection;
	}

	/**
	 * 是否让表情符号也在被选中时渲染选中效果
	 *
	 * @param enable enable
	 * @return 当前对象
	 */
	public RenderOption setDrawEmoticonSelection(boolean enable) {
		mDrawEmoticonSelection = enable;
		return this;
	}

	/**
	 * @return 是否是debug模式
	 */
	public boolean isEnableDebug() {
		return mEnableDebug;
	}

	/**
	 * 开启debug模式
	 *
	 * @param enableDebug 是否开启
	 * @return 当前对象
	 */
	public RenderOption setEnableDebug(boolean enableDebug) {
		mEnableDebug = enableDebug;
		return this;
	}

	public int getDragViewColor() {
		return mDragViewColor;
	}

	public RenderOption setDragViewColor(@ColorInt int dragViewColor) {
		mDragViewColor = dragViewColor;
		return this;
	}

	/**
	 * @return 文字是否可以点击
	 */
	public boolean isWordSelectable() {
		return mWordSelectable;
	}

	/**
	 * 设置文字点击
	 *
	 * @param wordSelectable 是否开启功能
	 * @return 当前对象
	 */
	public RenderOption setWordSelectable(boolean wordSelectable) {
		mWordSelectable = wordSelectable;
		return this;
	}

	/**
	 * @return 断字策略
	 */
	public BreakStrategy getBreakStrategy() {
		return mBreakStrategy;
	}

	/**
	 * 设置断字策略
	 *
	 * @param breakStrategy {@link BreakStrategy}
	 * @return 当前对象
	 */
	public RenderOption setBreakStrategy(BreakStrategy breakStrategy) {
		mBreakStrategy = breakStrategy;
		return this;
	}

	/**
	 * @return 当前字体颜色
	 */
	public int getTextColor() {
		return mTextColor;
	}

	/**
	 * 设置字体颜色
	 *
	 * @param color 颜色
	 * @return 当前对象
	 */
	public RenderOption setTextColor(int color) {
		mTextColor = color;
		return this;
	}

	/**
	 * @return 当前字体
	 */
	public Typeface getTypeface() {
		return mTypeface;
	}

	/**
	 * 设置字体
	 *
	 * @param typeface 字体，非空
	 * @return 当前对象
	 */
	public RenderOption setTypeface(@NonNull Typeface typeface) {
		mTypeface = typeface;
		return this;
	}

	/**
	 * @return 字体大小
	 */
	public float getTextSize() {
		return mTextSize;
	}

	/**
	 * 设置当前字体大小
	 * {@link TypedValue#applyDimension(int, float, DisplayMetrics)}
	 *
	 * @param textSize 字号
	 * @return 当前对象
	 */
	public RenderOption setTextSize(float textSize) {
		mTextSize = textSize;
		return this;
	}

	/**
	 * @param context 上下文
	 * @param unit    {@link TypedValue#COMPLEX_UNIT_SP} {@link TypedValue#COMPLEX_UNIT_PX} {@link TypedValue#COMPLEX_UNIT_DIP} etc.
	 * @param value   字号
	 * @return 当前对象
	 */
	public RenderOption setTextSize(@NonNull Context context, int unit, @FloatRange(from = 0) float value) {
		Resources resources = context.getResources();
		DisplayMetrics displayMetrics = resources.getDisplayMetrics();
		return setTextSize(
				TypedValue.applyDimension(unit, value, displayMetrics)
		);
	}

	/**
	 * @return 获取行间距
	 */
	public float getLineSpace() {
		return mLineSpace;
	}

	/**
	 * 设置行间距
	 *
	 * @param lineSpace 行间距
	 * @return 当前对象
	 */
	public RenderOption setLineSpace(float lineSpace) {
		mLineSpace = lineSpace;
		return this;
	}

	/**
	 * @return 获取点击文字后选中效果的背景颜色
	 */
	public int getSelectedBackgroundColor() {
		return mSelectedBackgroundColor;
	}

	/**
	 * 设置点击文字后选中效果的背景颜色
	 *
	 * @param color 颜色
	 * @return 当前对象
	 */
	public RenderOption setSelectedBackgroundColor(int color) {
		mSelectedBackgroundColor = color;
		return this;
	}

	/**
	 * @return 获取点击文字后的颜色
	 */
	public int getSelectedTextColor() {
		return mSelectedTextColor;
	}

	/**
	 * 设置点击文字后的颜色
	 *
	 * @param color 颜色
	 * @return 当前对象
	 */
	public RenderOption setSelectedTextColor(int color) {
		mSelectedTextColor = color;
		return this;
	}

	/**
	 * 获取长按
	 */
	public int getSelectedByLongClickBackgroundColor() {
		return mSelectedByLongClickBackgroundColor;
	}

	/**
	 * 设置点击后的背景色
	 *
	 * @return 当前对象
	 */
	public RenderOption setSelectedByLongClickBackgroundColor(int color) {
		mSelectedByLongClickBackgroundColor = color;
		return this;
	}

	/**
	 * 获取长按时显示的文字颜色
	 */
	public int getSelectedByLongClickTextColor() {
		return mSelectedByLongClickTextColor;
	}

	/**
	 * 设置长按选中后的颜色
	 *
	 * @return 当前对象
	 */
	public RenderOption setSelectedByLongClickTextColor(int color) {
		mSelectedByLongClickTextColor = color;
		return this;
	}

	/**
	 * @return 获取断字策略
	 */
	public HyphenStrategy getHyphenStrategy() {
		return mHyphenStrategy;
	}

	/**
	 * @param hyphenStrategy 设置断字策略 {@link HyphenStrategy}
	 * @return 当前对象
	 */
	public RenderOption setHyphenStrategy(HyphenStrategy hyphenStrategy) {
		mHyphenStrategy = hyphenStrategy;
		return this;
	}

	/**
	 * 是否开启懒惰渲染模式
	 *
	 * @return 是否开启懒惰渲染模式
	 */
	public boolean isEnableLazyRender() {
		return mEnableLazyRender;
	}

	/**
	 * 开启懒惰渲染模式，即滚动的时候不渲染，等待滚动停止后渲染内容
	 *
	 * @param enable enable
	 * @return 当前对象
	 */
	public RenderOption setEnableLazyRender(boolean enable) {
		mEnableLazyRender = enable;
		return this;
	}

	/**
	 * @return 获取高亮字体颜色
	 */
	public int getSpanHighlightTextColor() {
		return mSpanHighlightTextColor;
	}

	/**
	 * 设置高亮字体颜色
	 *
	 * @param color 字体颜色
	 * @return 当前对象
	 */
	public RenderOption setSpanHighlightTextColor(int color) {
		mSpanHighlightTextColor = color;
		return this;
	}

	/**
	 * @return 获取加载时的背景色
	 */
	public int getLoadingBackgroundColor() {
		return mLoadingBackgroundColor;
	}

	/**
	 * 设置加载背景色
	 *
	 * @param color
	 * @return 当前对象
	 */
	public RenderOption setLoadingBackgroundColor(int color) {
		mLoadingBackgroundColor = color;
		return this;
	}

	/**
	 * @return 是否是兼容性渲染
	 */
	public boolean isCompatMode() {
		return mCompatMode;
	}

	/**
	 * 兼容模式 会采用同步渲染 更慢 更耗电
	 * 但是兼容性很好
	 * <p>
	 * Tips: 目前不支持在运行时动态修改
	 * </p>
	 *
	 * @param enable 是否开启兼容性渲染模式
	 * @return 当前对象
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public RenderOption setCompatMode(boolean enable) {
		mCompatMode = enable;
		return this;
	}

	/**
	 * @return 获取选中背景的圆角半径
	 */
	public float getSelectedBackgroundRoundRadius() {
		return mSelectedBackgroundRoundRadius;
	}

	/**
	 * @param selectedBackgroundRoundRadius 设置选中背景的圆角半径
	 * @return 当前对象
	 */
	public RenderOption setSelectedBackgroundRoundRadius(float selectedBackgroundRoundRadius) {
		mSelectedBackgroundRoundRadius = selectedBackgroundRoundRadius;
		return this;
	}

	/**
	 * @return 是否开启onDraw时间戳debug
	 */
	public boolean isEnableOnDrawTsDebug() {
		return mEnableOnDrawTsDebug;
	}

	/**
	 * @param enable 开启onDraw时间戳debug
	 */
	public void setEnableOnDrawTsDebug(boolean enable) {
		mEnableOnDrawTsDebug = enable;
	}

	/**
	 * @return 是否开启异步渲染时间戳debug
	 */
	public boolean isEnableAsyncDrawTsDebug() {
		return mEnableAsyncDrawTsDebug;
	}

	/**
	 * @param enable 开启异步渲染时间戳debug
	 */
	public void setEnableAsyncDrawTsDebug(boolean enable) {
		mEnableAsyncDrawTsDebug = enable;
	}

	@Override
	public String toString() {
		return "RenderOption{" +
				"mTextColor=" + mTextColor +
				", mTypeface=" + mTypeface +
				", mTextSize=" + mTextSize +
				", mLineSpace=" + mLineSpace +
				", mSelectedBackgroundColor=" + mSelectedBackgroundColor +
				", mSelectedTextColor=" + mSelectedTextColor +
				", mBreakStrategy=" + mBreakStrategy +
				", mWordSelectable=" + mWordSelectable +
				", mEnableDebug=" + mEnableDebug +
				", mEnableOnDrawTsDebug=" + mEnableOnDrawTsDebug +
				", mEnableAsyncDrawTsDebug=" + mEnableAsyncDrawTsDebug +
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
				'}';
	}
}
