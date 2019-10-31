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

	}

	public static void clean() {
		POOL.clean();
	}
}
