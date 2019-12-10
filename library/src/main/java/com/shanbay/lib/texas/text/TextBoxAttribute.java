package com.shanbay.lib.texas.text;

import com.shanbay.lib.texas.Texas;
import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.misc.ObjectFactory;

@Hidden
class TextBoxAttribute extends DefaultRecyclable {
	private final static ObjectFactory<TextBoxAttribute> POOL = new ObjectFactory<>(128);
	static {
		Texas.registerLifecycleCallback(new Texas.LifecycleCallback() {
			@Override
			public void onClean() {
				POOL.clean();
			}
		});
	}

	private TextStyle mTextStyle;
	private Appearance mBackground;
	private Appearance mForeground;
	private OnClickedListener mSpanOnClickedListener;
	private Object mTag;

	private TextBoxAttribute() {
	}

	public void setTextStyle(TextStyle textStyle) {
		mTextStyle = textStyle;
	}

	public void setBackground(Appearance background) {
		mBackground = background;
	}

	public void setForeground(Appearance foreground) {
		mForeground = foreground;
	}

	public void setSpanOnClickedListener(OnClickedListener spanOnClickedListener) {
		mSpanOnClickedListener = spanOnClickedListener;
	}

	public TextStyle getTextStyle() {
		return mTextStyle;
	}

	public Appearance getBackground() {
		return mBackground;
	}

	public Appearance getForeground() {
		return mForeground;
	}

	public OnClickedListener getSpanOnClickedListener() {
		return mSpanOnClickedListener;
	}

	public Object getTag() {
		return mTag;
	}

	public void setTag(Object tag) {
		mTag = tag;
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
}