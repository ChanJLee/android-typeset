package me.chan.typeset.core.measurer;

public class TypesetMeasurer {
	private Listener mListener;

	public void setListener(Listener listener) {
		mListener = listener;
	}

	public void measure(CharSequence charSequence, int width) {

	}

	public interface Listener {
		void onTaskCompleted();
	}
}
