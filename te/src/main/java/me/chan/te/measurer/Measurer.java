package me.chan.te.measurer;

public interface Measurer {
	float getDesiredWidth(CharSequence charSequence, int start, int end);

	float getDesiredHeight(CharSequence charSequence, int start, int end);

	float getFontSpacing();
}