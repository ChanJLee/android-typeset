package me.chan.texas.text;

import androidx.annotation.Keep;

import me.chan.texas.Texas;
import me.chan.texas.annotations.Hidden;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectFactory;

@Hidden
class TextBoxAttribute extends DefaultRecyclable {
	private final static ObjectFactory<TextBoxAttribute> POOL = new ObjectFactory<>(128);
	static {
		Texas.register(TextBoxAttribute.class);
	}

	private TextStyle mTextStyle;
	private Background mBackground;
	private Foreground mForeground;
	private OnClickedListener mSpanOnClickedListener;

	private TextBoxAttribute() {
	}

	public void setTextStyle(TextStyle textStyle) {
		mTextStyle = textStyle;
	}

	public void setBackground(Background background) {
		mBackground = background;
	}

	public void setForeground(Foreground foreground) {
		mForeground = foreground;
	}

	public void setSpanOnClickedListener(OnClickedListener spanOnClickedListener) {
		mSpanOnClickedListener = spanOnClickedListener;
	}

	public TextStyle getTextStyle() {
		return mTextStyle;
	}

	public Background getBackground() {
		return mBackground;
	}

	public Foreground getForeground() {
		return mForeground;
	}

	public OnClickedListener getSpanOnClickedListener() {
		return mSpanOnClickedListener;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		mTextStyle = null;
		if (mBackground != null) {
			mBackground.recycle();
			mBackground = null;
		}
		if (mForeground != null) {
			mForeground.recycle();
			mForeground = null;
		}
		mSpanOnClickedListener = null;
		POOL.release(this);
	}

	public static TextBoxAttribute obtain() {
		TextBoxAttribute attribute = POOL.acquire();
		if (attribute == null) {
			return new TextBoxAttribute();
		}
		attribute.reuse();
		return attribute;
	}

	@Keep
	public static void clean() {
		POOL.clean();
	}
}