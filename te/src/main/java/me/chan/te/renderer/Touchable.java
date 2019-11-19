package me.chan.te.renderer;

public interface Touchable {
	void setTouchListener(TouchListener listener);

	interface TouchListener {
		boolean onClicked(float x, float y);

		boolean onLongClicked(float x, float y);
	}
}
