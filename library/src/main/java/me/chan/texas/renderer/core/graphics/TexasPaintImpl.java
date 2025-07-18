package me.chan.texas.renderer.core.graphics;

import android.graphics.Paint;

import androidx.annotation.NonNull;

public class TexasPaintImpl implements TexasPaint {
	@NonNull
	private Paint mPaint;

	public TexasPaintImpl(@NonNull Paint paint) {
		mPaint = paint;
	}

	public void setPaint(@NonNull Paint paint) {
		mPaint = paint;
	}
}
