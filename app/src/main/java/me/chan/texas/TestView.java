package me.chan.texas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TestView extends View {
	private final TextPaint mPaint;
	private StaticLayout mLayout;

	private CharSequence mText;

	public TestView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(50);
		mPaint.setColor(Color.BLACK);
	}

	public void setText(CharSequence text) {
		mText = text;
		requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !TextUtils.isEmpty(mText)) {
			mLayout = StaticLayout.Builder.obtain(mText, 0, mText.length(), mPaint, getMeasuredWidth()).build();
		}
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);
		if (mLayout == null) {
			return;
		}

		mLayout.draw(canvas);
		canvas.save();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			canvas.drawTextRun(mText, 0, mText.length(), 0, mText.length(), 0, 100, true, mPaint);
		}
		canvas.restore();
	}
}
