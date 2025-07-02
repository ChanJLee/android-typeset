package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.BitBucket32;

public class StateList {
	public static final int STATE_SELECTED = 1;
	public static final int STATE_HIGHLIGHT = 2;

	private final BitBucket32 mBits = new BitBucket32();

	@RestrictTo(LIBRARY)
	public void setSelected(boolean selected) {
		if (selected) {
			mBits.set(STATE_SELECTED);
		} else {
			mBits.clear(STATE_SELECTED);
		}
	}

	public boolean isSelected() {
		return getState(STATE_SELECTED);
	}

	public boolean getState(@StateCode int state) {
		return mBits.get(state);
	}

	@RestrictTo(LIBRARY)
	public void clear() {
		mBits.clear();
	}

	public void setHighlighted(boolean isHighlighted) {
		if (isHighlighted) {
			mBits.set(STATE_HIGHLIGHT);
		} else {
			mBits.clear(STATE_HIGHLIGHT);
		}
	}

	public boolean isHighlighted() {
		return getState(STATE_HIGHLIGHT);
	}

	@IntDef(flag = true, value = {STATE_SELECTED, STATE_HIGHLIGHT})
	public @interface StateCode {
	}
}
