package me.chan.te.renderer;

public interface Touchable {
	void setTouchListener(TouchListener listener);

	TouchListener getTouchListener();

	interface TouchListener {
		boolean onClicked(float x, float y);

		boolean onLongClicked(float x, float y);
	}
}
