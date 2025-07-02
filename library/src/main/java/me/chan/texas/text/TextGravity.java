package me.chan.texas.text;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

/**
 * 行gravity信息
 */
public class TextGravity {
	public static final int START = 1;
	public static final int END = 2;
	public static final int CENTER_HORIZONTAL = START | END;
	public static final int TOP = 4;
	public static final int BOTTOM = 8;
	public static final int CENTER_VERTICAL = TOP | BOTTOM;
	public static final int CENTER = CENTER_HORIZONTAL | CENTER_VERTICAL;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int VERTICAL_MASK = TOP | BOTTOM | CENTER_VERTICAL;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int HORIZONTAL_MASK = START | END | CENTER_HORIZONTAL;

	@IntDef({
			START,
			END,
			CENTER_HORIZONTAL,
			CENTER_VERTICAL,
			TOP,
			BOTTOM,
			CENTER
	})
	public @interface GravityMask {

	}
}
