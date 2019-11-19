package me.chan.te.renderer;

public interface Clickable {
	void setOnClickedListener(OnClickedListener listener);

	OnClickedListener getOnClickedListener();

	interface OnClickedListener {
		boolean onClicked(float x, float y);
	}
}
