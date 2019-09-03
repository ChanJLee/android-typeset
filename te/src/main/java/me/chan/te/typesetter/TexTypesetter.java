package me.chan.te.typesetter;

import android.graphics.Paint;

import java.util.List;

import me.chan.te.data.Element;
import me.chan.te.data.Option;
import me.chan.te.data.Paragraph;

public class TexTypesetter implements Typesetter {
	private Option mOption;
	private Paint mPaint;

	public TexTypesetter(Paint paint, Option option) {
		mOption = option;
		mPaint = paint;
	}

	@Override
	public Paragraph typeset(List<? extends Element> elements) {
		Paragraph paragraph = new Paragraph();
		return paragraph;
	}
}
