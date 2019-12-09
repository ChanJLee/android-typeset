package com.shanbay.lib.texas.renderer;

import android.graphics.Typeface;

import androidx.annotation.NonNull;

import com.shanbay.lib.texas.text.BreakStrategy;
import com.shanbay.lib.texas.text.HyphenStrategy;
import com.shanbay.lib.texas.text.OnClickedListener;
import com.shanbay.lib.texas.text.Segment;

/**
 * 渲染参数
 */
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
	 * @param textColor 颜色
	 * @return 当前对象
	 */
	public RenderOption setTextColor(int textColor) {
		mTextColor = textColor;
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
	 *
	 * @param textSize 字号
	 * @return 当前对象
	 */
	public RenderOption setTextSize(float textSize) {
		mTextSize = textSize;
		return this;
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
	 * @return 是否首行缩进
	 */
	public boolean isIndentEnable() {
		return mIndentEnable;
	}

	/**
	 * 设置首行缩进
	 *
	 * @param indentEnable 是否开启
	 * @return 当前对象
	 */
	public RenderOption setIndentEnable(boolean indentEnable) {
		mIndentEnable = indentEnable;
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
	 * @param selectedBackgroundColor 颜色
	 * @return 当前对象
	 */
	public RenderOption setSelectedBackgroundColor(int selectedBackgroundColor) {
		mSelectedBackgroundColor = selectedBackgroundColor;
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
	 * @param selectedTextColor 颜色
	 * @return 当前对象
	 */
	public RenderOption setSelectedTextColor(int selectedTextColor) {
		mSelectedTextColor = selectedTextColor;
		return this;
	}

	/**
	 * @return 获取span选中后背景的颜色 {@link com.shanbay.lib.texas.text.Paragraph.Builder#newSpanBuilder(OnClickedListener)}
	 */
	public int getSpanSelectedBackgroundColor() {
		return mSpanSelectedBackgroundColor;
	}

	/**
	 * 设置点击span后的背景色
	 *
	 * @param selectedSpanBackgroundColor 颜色 {@link com.shanbay.lib.texas.text.Paragraph.Builder#newSpanBuilder(OnClickedListener)}
	 * @return 当前对象
	 */
	public RenderOption setSpanSelectedBackgroundColor(int selectedSpanBackgroundColor) {
		mSpanSelectedBackgroundColor = selectedSpanBackgroundColor;
		return this;
	}

	/**
	 * @return 返回span选中后的颜色 {@link com.shanbay.lib.texas.text.Paragraph.Builder#newSpanBuilder(OnClickedListener)}
	 */
	public int getSpanSelectedTextColor() {
		return mSpanSelectedTextColor;
	}

	/**
	 * 设置span选中后的颜色
	 *
	 * @param spanSelectedTextColor 颜色 {@link com.shanbay.lib.texas.text.Paragraph.Builder#newSpanBuilder(OnClickedListener)}
	 * @return 当前对象
	 */
	public RenderOption setSpanSelectedTextColor(int spanSelectedTextColor) {
		mSpanSelectedTextColor = spanSelectedTextColor;
		return this;
	}

	/**
	 * @return 获取segment之间的间距
	 * <p/>
	 * {@link com.shanbay.lib.texas.text.Document#addSegment(Segment)}
	 * {@link com.shanbay.lib.texas.text.Paragraph}
	 * {@link com.shanbay.lib.texas.text.ViewSegment}
	 * {@link com.shanbay.lib.texas.text.Figure}
	 */
	public float getSegmentSpace() {
		return mSegmentSpace;
	}

	/**
	 * @param segmentSpace 设置segment之间的间距
	 * @return 当前对象
	 * <p/>
	 * {@link com.shanbay.lib.texas.text.Document#addSegment(Segment)}
	 * {@link com.shanbay.lib.texas.text.Paragraph}
	 * {@link com.shanbay.lib.texas.text.ViewSegment}
	 * {@link com.shanbay.lib.texas.text.Figure}
	 */
	public RenderOption setSegmentSpace(float segmentSpace) {
		mSegmentSpace = segmentSpace;
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
}
