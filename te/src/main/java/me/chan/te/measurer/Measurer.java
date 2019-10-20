package me.chan.te.measurer;

import android.text.TextPaint;

public interface Measurer {
	float getDesiredWidth(CharSequence charSequence, int start, int end, TextPaint textPaint);

	float getDesiredHeight(CharSequence charSequence, int start, int end, TextPaint textPaint);
}