package me.chan.te.renderer;

public interface ThreadHandler {
	void sendMessage(int what, Object value);

	void handleMessage(int what, Object value);

	void clear();
}
