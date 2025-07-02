package me.chan.texas.misc;

import android.graphics.Typeface;
import android.text.TextPaint;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.Texas;
import me.chan.texas.compat.TextPaintCompat;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.utils.TexasUtils;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class PaintSet {
	private final TextPaint mPaint;

	public PaintSet(RenderOption renderOption) {
		mPaint = TextPaintCompat.create(TextPaint.ANTI_ALIAS_FLAG);


		Typeface defaultTypeface = Texas.getDefaultTypeface();
		if (defaultTypeface != null) {
			mPaint.setTypeface(defaultTypeface);
		}

		TexasUtils.setupTextPaint(mPaint);

		refresh(renderOption);
	}

	@VisibleForTesting
	public PaintSet(TextPaint paint) {
		mPaint = paint;
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

	
	public TextPaint getWorkPaint(@NonNull TextPaint copy) {
		copy.set(mPaint);
		return copy;
	}

	public void set(PaintSet other) {
		mPaint.set(other.mPaint);
	}
}
