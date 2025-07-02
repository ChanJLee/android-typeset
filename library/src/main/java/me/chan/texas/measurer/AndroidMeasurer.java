package me.chan.texas.measurer;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Paint;
import android.text.BoringLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import androidx.annotation.RestrictTo;

import me.chan.texas.compat.TextPaintCompat;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.text.TextStyle;
import me.chan.texas.utils.CharArrayPool;
import me.chan.texas.utils.TexasUtils;


@RestrictTo(LIBRARY)
public class AndroidMeasurer implements Measurer {

	private static final CharArrayPool POOL = new CharArrayPool();

	private final PaintSet mPaintSet;

	private final TextPaint mWorkPaint = TextPaintCompat.create();

	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();

	public AndroidMeasurer(PaintSet context) {
		mPaintSet = context;
	}

	@Override
	public CharSequenceSpec measure(CharSequence charSequence, int start, int end, TextStyle textStyle, Object tag) {
		CharSequenceSpec spec = new CharSequenceSpec();
		measure(charSequence, start, end, textStyle, tag, spec);
		return spec;
	}

	@Override
	public void measure(CharSequence charSequence, int start, int end, TextStyle textStyle, Object tag, CharSequenceSpec spec) {
		TextPaint textPaint = mPaintSet.getPaint();
		if (textStyle != null) {
			textPaint = mPaintSet.getWorkPaint(mWorkPaint);
			textStyle.update(textPaint, tag);
		}




		float width = 0;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			int size = end - start;
			char[] buf = POOL.obtain(size);
			TexasUtils.getChars(charSequence, start, end, buf, 0);
			width = textPaint.getRunAdvance(buf, 0, size, 0, size, false, size);
			POOL.release(buf);
		} else {
			width = (float) Math.ceil(BoringLayout.getDesiredWidth(charSequence, start, end, textPaint));
		}

		textPaint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		float height = (float) Math.ceil(fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading);
		spec.reset(width, height, (float) Math.ceil(fontMetrics.descent));
	}

	public String stats() {
		return POOL.stats();
	}
}
