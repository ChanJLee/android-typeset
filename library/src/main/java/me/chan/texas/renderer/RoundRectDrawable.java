package me.chan.texas.renderer;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public class RoundRectDrawable {

	private final List<RectF> mBackgrounds = new ArrayList<>();

	public void append(@NonNull RectF rectF) {
		append(rectF.left, rectF.top, rectF.right, rectF.bottom);
	}

	public void append(float left, float top, float right, float bottom) {
		if (mBackgrounds.isEmpty()) {
			appendRegion0(left, top, right, bottom);
			return;
		}

		RectF rectF = mBackgrounds.get(mBackgrounds.size() - 1);
		if (Float.compare(rectF.right, left) == 0 || Math.abs(rectF.right - left) < 1) {
			rectF.right = right;
			rectF.top = Math.min(rectF.top, top);
			rectF.bottom = Math.max(rectF.bottom, bottom);
		} else {
			appendRegion0(left, top, right, bottom);
		}
	}

	private void appendRegion0(float left, float top, float right, float bottom) {
		RectF rectF = new RectF(left, top, right, bottom);
		mBackgrounds.add(rectF);
	}

	public void prepend(@NonNull RectF rectF) {
		prepend(rectF.left, rectF.top, rectF.right, rectF.bottom);
	}

	public void prepend(float left, float top, float right, float bottom) {
		if (isEmpty()) {
			prependRegion0(left, top, right, bottom);
			return;
		}

		RectF rectF = mBackgrounds.get(0);
		if (Float.compare(rectF.left, right) == 0 || Math.abs(rectF.left - right) < 1) {
			rectF.left = left;
			rectF.top = Math.min(rectF.top, top);
			rectF.bottom = Math.max(rectF.bottom, bottom);
		} else {
			prependRegion0(left, top, right, bottom);
		}
	}

	private void prependRegion0(float left, float top, float right, float bottom) {
		RectF rectF = new RectF(left, top, right, bottom);
		mBackgrounds.add(0, rectF);
	}

	public void clear() {
		mBackgrounds.clear();
	}

	public boolean isEmpty() {
		return mBackgrounds.isEmpty();
	}

	public List<RectF> getBackgrounds() {
		return mBackgrounds;
	}

	public int size() {
		return mBackgrounds.size();
	}

	public RectF get(int index) {
		return mBackgrounds.get(index);
	}

	public void drawRect(@NonNull TexasCanvas canvas, @NonNull TexasPaint paint) {
		for (RectF rectF : mBackgrounds) {
			canvas.drawRect(rectF.left, rectF.top, rectF.right, rectF.bottom, paint);
		}
	}

	public void drawRoundRect(@NonNull TexasCanvas canvas, @NonNull TexasPaint paint, @FloatRange(from = 0) float radius) {
		for (RectF rectF : mBackgrounds) {
			canvas.drawRoundRect(rectF.left, rectF.top, rectF.right, rectF.bottom, radius, radius, paint);
		}
	}
}
