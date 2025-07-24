package me.chan.texas.text.layout;

import android.annotation.SuppressLint;

import androidx.annotation.RestrictTo;

import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.TextAttribute;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SymbolGlue extends Glue {
	private final static ObjectPool<SymbolGlue> POOL = new ObjectPool<>(1024);

	private static final float MAGIC_FACTOR = 0.7f;

	private TextBox mTextBox;

	private SymbolGlue() {
	}

	@SuppressLint("MissingSuperCall")
	@Override
	protected void onRecycle() {
		mWidth = mShrink = mStretch = 0;
		mRefreshFlag = 0;
		mTextBox = null;
		POOL.release(this);
	}

	@Override
	protected void onMeasure(Measurer measurer, TextAttribute textAttribute) {
		mTextBox.measure(measurer, textAttribute);
		mWidth = mTextBox.getWidth();
		mShrink = mWidth * MAGIC_FACTOR;
		mStretch = 0;
	}

	public static void clean() {
		POOL.clean();
	}

	public static SymbolGlue obtain(TextBox box) {
		SymbolGlue glue = POOL.acquire();
		if (glue == null) {
			glue = new SymbolGlue();
		} else {
			glue.reuse();
		}

		glue.mTextBox = box;
		glue.mWidth = box.getWidth();
		glue.mShrink = glue.mWidth * MAGIC_FACTOR;
		glue.mStretch = 0;
		glue.mRefreshFlag = 0;
		return glue;
	}

	@Override
	public String toString() {
		return "SymbolGlue{" +
				"mStretch=" + mStretch +
				", mShrink=" + mShrink +
				", mWidth=" + mWidth +
				'}';
	}
}
