package me.chan.te.config;

import android.text.Layout;
import android.text.TextPaint;

public class Option {
	public float infinity = 1000;
	public int hyphenPenalty = 100;
	public float demeritsLine = 1;
	// 对应 α
	public float demeritsFlagged = 100;
	// 对应 γ
	public float demeritsFitness = 3000;
	public int maxRelayoutTimes = 3;
	public int minHyperLen = 4;
	public float hyphenWidth;
	public float spaceWidth;
	public float spaceStretch;
	public float spaceShrink;
	public float indent;
	public float lineSpacing;

	public Option(TextPaint paint) {
		hyphenWidth = paint.measureText("-");
		spaceWidth = Layout.getDesiredWidth(" ", paint);
		spaceStretch = (spaceWidth * 3) / 9;
		spaceShrink = (spaceWidth * 3) / 20;
		// 首行缩进四个空格
		indent = spaceWidth * 4;
		// 1.0 倍行间距
		lineSpacing = (int) (paint.getFontSpacing());
	}
}
