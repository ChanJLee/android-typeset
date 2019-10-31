package me.chan.te.text;

import android.graphics.Canvas;
import android.text.TextPaint;

import me.chan.te.misc.ObjectFactory;

public class UnderLine extends Foreground {
	private static final ObjectFactory<UnderLine> POOL = new ObjectFactory<>(256);

	private int mColor;

	private UnderLine(int color) {
		mColor = color;
	}

	@Override
	public boolean isConflict(Appearance other) {
		return !equals(other);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UnderLine underLine = (UnderLine) o;

		return mColor == underLine.mColor;
	}

	@Override
	public void draw(Canvas canvas, TextPaint textPaint, float left, float top, float right, float bottom) {
		// TODO
	}

	public static void clean() {
		POOL.clean();
	}

	@Override
	public void recycle() {
		mColor = -1;
		POOL.release(this);
	}

	public static UnderLine obtain(int color) {
		UnderLine underLine = POOL.acquire();
		if (underLine == null) {
			return new UnderLine(color);
		}
		underLine.mColor = color;
		return underLine;
	}
}
