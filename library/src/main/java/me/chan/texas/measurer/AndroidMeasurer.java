package me.chan.texas.measurer;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Paint;

import androidx.annotation.RestrictTo;

import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;
import me.chan.texas.text.TextStyle;
import me.chan.texas.utils.CharArrayPool;
import me.chan.texas.utils.TexasUtils;

/**
 * android的文本测量器
 */
@RestrictTo(LIBRARY)
public class AndroidMeasurer implements Measurer {

	private static final CharArrayPool POOL = new CharArrayPool();

	private final PaintSet mPaintSet;
	private final TexasPaintImpl mTexasPaint = new TexasPaintImpl();

	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private final CharSequenceSpec mBase = new CharSequenceSpec();

	public AndroidMeasurer(PaintSet paintSet) {
		mPaintSet = paintSet;
	}

	@Override
	public CharSequenceSpec getBaseSpec() {
		measure("-", 0, 1, null, null, mBase);
		return mBase;
	}

	@Override
	public void measure(CharSequence charSequence, int start, int end, TextStyle textStyle, Object tag, CharSequenceSpec spec) {
		mTexasPaint.reset(mPaintSet);
		if (textStyle != null) {
			textStyle.update(mTexasPaint, tag);
		}
		Paint paint = mTexasPaint.getPaint();

		// 不能使用 TextPaint getTextBounds
		// vivo 手机使用这个方法慢的出奇
		// BoringLayout 是用来测量单行文本的
		float width = 0;
		int size = end - start;
		char[] buf = POOL.obtain(size);
		TexasUtils.getChars(charSequence, start, end, buf, 0);
		width = paint.getRunAdvance(buf, 0, size, 0, size, false, size);
		POOL.release(buf);

		paint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		float height = (float) Math.ceil(fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading);
		float baselineOffset = (float) Math.ceil(fontMetrics.descent);
		spec.reset(width, height, baselineOffset);
	}
	
	public String stats() {
		return POOL.stats();
	}
}
