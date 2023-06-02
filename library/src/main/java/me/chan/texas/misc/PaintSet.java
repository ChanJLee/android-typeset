package me.chan.texas.misc;

import android.text.TextPaint;

import androidx.annotation.RestrictTo;

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

		TexasUtils.setupTextPaint(mPaint);
	}

	public PaintSet(RenderOption renderOption) {
		this();
		refresh(renderOption);
	}

	public void refresh(RenderOption renderOption) {
		mPaint.setColor(renderOption.getTextColor());
		mPaint.setTypeface(renderOption.getTypeface());
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
