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

	public Box obtainTextBox(@NonNull CharSequence charSequence) {
		return obtainTextBox(charSequence, 0, charSequence.length());
	}

	public Box obtainTextBox(@NonNull CharSequence charSequence, int start, int end) {
		return obtainTextBox(charSequence, start, end, null);
	}

	public Box obtainTextBox(@NonNull CharSequence charSequence, int start, int end, BoxStyle boxStyle) {
		TextBox box = mBoxPool.acquire();
		if (box == null) {
			return new TextBox(mMeasurer);
		}

		box.reset(charSequence, start, end, boxStyle);
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

	public Penalty obtainPenalty(float width, float penalty, boolean flag) {
		Penalty p = mPenaltyPool.acquire();
		if (p == null) {
			return new Penalty(width, penalty, flag);
		}
		p.reset(width, penalty, flag);
		return p;
	}

	public void recycle(Element element) {
		if (element == null) {
			return;
		}

		element.release();

		if (element instanceof TextBox) {
			mBoxPool.release((TextBox) element);
		} else if (element instanceof Penalty) {
			mPenaltyPool.release((Penalty) element);
		} else {
			mGluePool.release((Glue) element);
		}
	}
}
