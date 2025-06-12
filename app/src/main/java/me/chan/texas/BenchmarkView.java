package me.chan.texas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BenchmarkView extends View {

	private final TextPaint mPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);

	public BenchmarkView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		mPaint.setTextSize(100);
		mPaint.setColor(Color.BLACK);
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);

		float baselineY = 200f;

		mPaint.setColor(Color.BLACK);
		Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
		canvas.drawLine(0, baselineY, getWidth(), baselineY, mPaint);
		canvas.drawText("qafnjP", 0, baselineY, mPaint);
		Log.d("BenchmarkView", "onDraw: " + fontMetrics);

		mPaint.setColor(Color.RED);
		canvas.drawLine(0, baselineY + fontMetrics.top, getWidth(), baselineY + fontMetrics.top, mPaint);
		mPaint.setColor(Color.GREEN);
		canvas.drawLine(0, baselineY + fontMetrics.ascent, getWidth(), baselineY + fontMetrics.ascent, mPaint);
		mPaint.setColor(Color.BLUE);
		canvas.drawLine(0, baselineY + fontMetrics.descent, getWidth(), baselineY + fontMetrics.descent, mPaint);
		mPaint.setColor(Color.YELLOW);
		canvas.drawLine(0, baselineY + fontMetrics.descent, getWidth(), baselineY + fontMetrics.descent, mPaint);
		mPaint.setColor(Color.CYAN);
		canvas.drawLine(0, baselineY + fontMetrics.bottom, getWidth(), baselineY + fontMetrics.bottom, mPaint);
	}
}
