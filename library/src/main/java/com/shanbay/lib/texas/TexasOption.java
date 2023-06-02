package com.shanbay.lib.texas;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.hyphenation.Hyphenation;
import com.shanbay.lib.texas.measurer.Measurer;
import com.shanbay.lib.texas.renderer.RenderOption;
import com.shanbay.lib.texas.text.TextAttribute;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * texas选项，包括各种解析参数
 */
public class TexasOption {
	private final Hyphenation mHyphenation;
	private final Measurer mMeasurer;
	private final TextAttribute mTextAttribute;
	private final RenderOption mRenderOption;

	@RestrictTo(LIBRARY)
	public TexasOption(Hyphenation hyphenation, Measurer measurer, TextAttribute textAttribute, RenderOption renderOption) {
		mHyphenation = hyphenation;
		mMeasurer = measurer;
		mTextAttribute = textAttribute;
		mRenderOption = renderOption;
	}

	@RestrictTo(LIBRARY)
	public Hyphenation getHyphenation() {
		return mHyphenation;
	}

	@RestrictTo(LIBRARY)
	public Measurer getMeasurer() {
		return mMeasurer;
	}

	@RestrictTo(LIBRARY)
	public TextAttribute getTextAttribute() {
		return mTextAttribute;
	}

	public RenderOption getRenderOption() {
		return mRenderOption;
	}
}
