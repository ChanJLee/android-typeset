package me.chan.texas;

import androidx.annotation.RestrictTo;

import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.TextAttribute;

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
