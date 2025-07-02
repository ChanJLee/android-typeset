package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.Appearance;
import me.chan.texas.text.Emoticon;
import me.chan.texas.text.TextAttribute;

/**
 * 可绘制box，可以是图片，表情
 */
@RestrictTo(LIBRARY)
public class DrawableBox extends Box {
	private static final ObjectPool<DrawableBox> POOL = new ObjectPool<>(Texas.getMemoryOption().getEmoticonBufferSize());
	private static final int[] STATE_PRESSED = {
			android.R.attr.state_pressed,
	};
	private static final int[] STATE_NORMAL = {
			-android.R.attr.state_pressed
	};

	private Drawable mDrawable;
	private Emoticon mEmoticon;

	public DrawableBox(@NonNull Drawable drawable, float width, float height, Emoticon emoticon) {
		super(width, height);
		mDrawable = drawable;
		mEmoticon = emoticon;
	}

	@Override
	public void draw(Canvas canvas, Paint paint, float x, float y, StateList states) {
		Drawable drawable = mDrawable;
		if (mDrawable instanceof StateListDrawable) {
			StateListDrawable stateListDrawable = (StateListDrawable) mDrawable;
			stateListDrawable.setState(states.isSelected() ? STATE_PRESSED : STATE_NORMAL);
			drawable = stateListDrawable.getCurrent();
		}

		drawable.setBounds((int) x, (int) (y - getHeight()), (int) (x + getWidth()), (int) y);
		drawable.draw(canvas);
	}

	public Drawable getDrawable() {
		return mDrawable;
	}

	public void setDrawable(Drawable drawable) {
		mDrawable = drawable;
	}

	public Emoticon getEmoticon() {
		return mEmoticon;
	}

	@Override
	protected void onRecycle() {
		super.onRecycle();
		mDrawable = null;
		mEmoticon.recycle();
		mEmoticon = null;
		POOL.release(this);
	}

	public static DrawableBox obtain(Drawable drawable,
									 float width, float height,
									 Emoticon emoticon, Object tag,
									 Appearance background,
									 Appearance foreground) {
		DrawableBox drawableBox = POOL.acquire();
		if (drawableBox == null) {
			drawableBox = new DrawableBox(drawable, width, height, emoticon);
		}

		drawableBox.mWidth = width;
		drawableBox.mHeight = height;
		drawableBox.mTag = tag;
		drawableBox.mBackground = background;
		drawableBox.mForeground = foreground;
		drawableBox.mDrawable = drawable;
		drawableBox.mEmoticon = emoticon;
		drawableBox.reuse();
		return drawableBox;
	}

	public static void clean() {
		POOL.clean();
	}

	@Override
	public void measure(Measurer measurer, TextAttribute textAttribute) {
		/* do nothing */
	}
}
