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

/**
 * android的文本测量器
 */
@RestrictTo(LIBRARY)
public class AndroidMeasurer implements Measurer {

	private static final CharArrayPool POOL = new CharArrayPool();

	private final PaintSet mPaintSet;

	private final TextPaint mWorkPaint = TextPaintCompat.create();

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

		// 不能使用 TextPaint getTextBounds
		// vivo 手机使用这个方法慢的出奇
		// BoringLayout 是用来测量单行文本的
		float width = 0;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			int size = end - start;
			char[] buf = POOL.obtain(size);
			TextUtils.getChars(charSequence, start, end, buf, 0);
			width = textPaint.getRunAdvance(buf, 0, size, 0, size, false, size);
			POOL.release(buf);
		} else {
			width = (float) Math.ceil(BoringLayout.getDesiredWidth(charSequence, start, end, textPaint));
		}
		Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
		float height = (float) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
		float bottomPadding = (float) Math.ceil(fontMetrics.bottom - fontMetrics.descent);
		bottomPadding = Math.min(bottomPadding, 1);
		float topPadding = (float) Math.ceil(fontMetrics.ascent - fontMetrics.top);

		spec.reset(width, height, topPadding, bottomPadding, (float) Math.ceil(fontMetrics.descent));
	}

	public String stats() {
		return POOL.stats();
	}
}
