package me.chan.te.data;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.misc.ObjectFactory;

public class ImageBox extends Box {
	private static final ObjectFactory<DrawableBox> POOL = new ObjectFactory<>(512);

	private String mUrl;

	public ImageBox(String url, float width, float height, Object extra) {
		super(width, height, extra);
		mUrl = url;
	}

	@Override
	public Object clone() {
		return null;
	}

	@Override
	public void append(Box other) {

	}

	@Override
	public void append(Penalty penalty) {

	}

	@Override
	public boolean canMerge(Box other) {
		return false;
	}

	@Override
	public void draw(Canvas canvas, TextPaint paint, float x, float y) {
		// TODO 同步加载图片
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public boolean canSpilt() {
		return false;
	}

	@Nullable
	@Override
	public Box spilt(float limitWidth) {
		return null;
	}

	@Override
	protected void onCopy(@NonNull Box other) {

	}

	@Override
	public void recycle() {

	}
}
