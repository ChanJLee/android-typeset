package me.chan.texas.renderer;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public class RectDrawable {

	private final List<CompositeRectDrawable> mBackgrounds = new ArrayList<>();

	public void append(@NonNull CompositeRectDrawable drawable) {
		mBackgrounds.add(drawable);
	}

	public void prepend(@NonNull CompositeRectDrawable drawable) {
		mBackgrounds.add(0, drawable);
	}

	public void clear() {
		mBackgrounds.clear();
	}

	public boolean isEmpty() {
		return mBackgrounds.isEmpty();
	}

	public List<CompositeRectDrawable> getBackgrounds() {
		return mBackgrounds;
	}

	public int size() {
		return mBackgrounds.size();
	}

	public CompositeRectDrawable get(int index) {
		return mBackgrounds.get(index);
	}

	public void drawRect(@NonNull TexasCanvas canvas, @NonNull TexasPaint paint) {
		for (CompositeRectDrawable drawable : mBackgrounds) {
			drawable.drawRect(canvas, paint);
		}
	}

	public void drawRoundRect(@NonNull TexasCanvas canvas, @NonNull TexasPaint paint, @FloatRange(from = 0) float radius) {
		for (CompositeRectDrawable drawable : mBackgrounds) {
			drawable.drawRoundRect(canvas, paint, radius);
		}
	}
}
