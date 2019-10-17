package me.chan.te.data;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.TextPaint;

import me.chan.te.misc.ObjectFactory;

public class ElementFactory {

	private ObjectFactory<Box> mBoxPool = new ObjectFactory<>(10000);
	private ObjectFactory<Penalty> mPenaltyPool = new ObjectFactory<>(4000);
	private ObjectFactory<Glue> mGluePool = new ObjectFactory<>(10000);
	private Box.Measurer mMeasurer;

	public ElementFactory() {
		this(new Box.Measurer() {
			@Override
			public float getDesiredWidth(CharSequence charSequence, int start, int end, TextPaint textPaint) {
				return Layout.getDesiredWidth(charSequence, start, end, textPaint);
			}

			@Override
			public float getDesiredHeight(CharSequence charSequence, int start, int end, TextPaint textPaint) {
				Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
				return fontMetrics.bottom - fontMetrics.top;
			}
		});
	}

	public ElementFactory(Box.Measurer measurer) {
		mMeasurer = measurer;
	}

	public Box obtainBox(@NonNull CharSequence charSequence) {
		return obtainBox(charSequence, 0, charSequence.length());
	}

	public Box obtainBox(@NonNull CharSequence charSequence, int start, int end) {
		return obtainBox(charSequence, start, end, null);
	}

	public Box obtainBox(@NonNull CharSequence charSequence, int start, int end, BoxStyle boxStyle) {
		if (charSequence == null) {
			return null;
		}

		Box box = mBoxPool.acquire();
		if (box == null) {
			box = new Box(mMeasurer);
		}
		box.reset(charSequence, start, end, boxStyle);
		return box;
	}

	public void recycle(Element element) {
		if (element == null) {
			return;
		}

		if (element instanceof Box) {
			mBoxPool.release((Box) element);
		} else if (element instanceof Penalty) {
			mPenaltyPool.release((Penalty) element);
		} else {
			mGluePool.release((Glue) element);
		}
	}
}
