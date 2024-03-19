package me.chan.texas.misc;

import android.graphics.Typeface;
import android.text.TextPaint;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.compat.TextPaintCompat;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.utils.TexasUtils;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class PaintSet {
	private final TextPaint mPaint;
	private final TextPaint mWorkPaint;

	public PaintSet() {
		mPaint = TextPaintCompat.create(TextPaint.ANTI_ALIAS_FLAG);
		mWorkPaint = TextPaintCompat.create();

		// support default typeface
		Typeface defaultTypeface = Texas.getDefaultTypeface();
		if (defaultTypeface != null) {
			mPaint.setTypeface(defaultTypeface);
		}

		TexasUtils.setupTextPaint(mPaint);
	}

	public PaintSet(RenderOption renderOption) {
		this();
		refresh(renderOption);
	}

	public void refresh(RenderOption renderOption) {
		mPaint.setColor(renderOption.getTextColor());
		Typeface typeface = renderOption.getTypeface();
		if (typeface != null) {
			mPaint.setTypeface(typeface);
		}
		mPaint.setTextSize(renderOption.getTextSize());
	}

	public TextPaint getPaint() {
		return mPaint;
	}

	public TextPaint getWorkPaint() {
		mWorkPaint.set(mPaint);
		return mWorkPaint;
	}

	public void set(PaintSet other) {
		mPaint.set(other.mPaint);
	}
}
