package me.chan.te.data;

import android.support.annotation.NonNull;

import me.chan.te.measurer.Measurer;
import me.chan.te.misc.ObjectFactory;

public class ElementFactory {

	private ObjectFactory<TextBox> mBoxPool = new ObjectFactory<>(10000);
	private ObjectFactory<Penalty> mPenaltyPool = new ObjectFactory<>(4000);
	private ObjectFactory<Glue> mGluePool = new ObjectFactory<>(10000);
	private Measurer mMeasurer;

	public ElementFactory(Measurer measurer) {
		mMeasurer = measurer;
	}

	public TextBox obtainTextBox(@NonNull CharSequence charSequence) {
		return obtainTextBox(charSequence, 0, charSequence.length());
	}

	public TextBox obtainTextBox(@NonNull CharSequence charSequence, int start, int end) {
		return obtainTextBox(charSequence, start, end, null);
	}

	public TextBox obtainTextBox(@NonNull CharSequence charSequence, int start, int end, BoxStyle boxStyle) {
		float width = mMeasurer.getDesiredWidth(charSequence, start, end);
		float height = mMeasurer.getDesiredHeight(charSequence, start, end);
		TextBox box = mBoxPool.acquire();
		if (box == null) {
			return new TextBox(charSequence, start, end, width, height, boxStyle);
		}

		box.reset(charSequence, start, end, width, height, boxStyle);
		return box;
	}

	public Glue obtainGlue(float width, float stretch, float shrink) {
		Glue glue = mGluePool.acquire();
		if (glue == null) {
			return new Glue(width, stretch, shrink);
		}
		glue.reset(width, stretch, shrink);
		return glue;
	}

	public void recycle(Element element) {
		if (element == null) {
			return;
		}

		element.recycle();

		if (element instanceof TextBox) {
			mBoxPool.release((TextBox) element);
		} else if (element instanceof Penalty) {
			mPenaltyPool.release((Penalty) element);
		} else {
			mGluePool.release((Glue) element);
		}
	}
}
