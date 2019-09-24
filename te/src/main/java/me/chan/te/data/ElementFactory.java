package me.chan.te.data;

import me.chan.te.misc.ObjectFactory;

public class ElementFactory {

	private ObjectFactory<Box> mBoxPool = new ObjectFactory<>(10000);
	private ObjectFactory<Penalty> mPenaltyPool = new ObjectFactory<>(4000);
	private ObjectFactory<Glue> mGluePool = new ObjectFactory<>(10000);

	public Box obtainBox(CharSequence charSequence) {
		return obtainBox(charSequence, null);
	}

	public Box obtainBox(CharSequence charSequence, BoxStyle boxStyle) {
		Box box = mBoxPool.acquire();
		if (box == null) {
			box = new Box();
		}
		box.reset(charSequence, boxStyle);
		return box;
	}

	public void recycle(Element element) {
		if (element instanceof Box) {
			mBoxPool.release((Box) element);
		} else if (element instanceof Penalty) {
			mPenaltyPool.release((Penalty) element);
		} else {
			mGluePool.release((Glue) element);
		}
	}
}
