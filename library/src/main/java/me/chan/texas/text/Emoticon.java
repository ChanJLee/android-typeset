package me.chan.texas.text;

import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.Texas;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.layout.DrawableBox;


public final class Emoticon extends DefaultRecyclable {
	private static final ObjectPool<Emoticon> POOL = new ObjectPool<>(Texas.getMemoryOption().getEmoticonBufferSize());

	@VisibleForTesting
	public static boolean hasBuffered() {
		return !POOL.isEmpty();
	}

	private DrawableBox mDrawableBox;

	private Emoticon(@NonNull Drawable drawable, float width, float height, Object tag, Appearance background, Appearance foreground) {
		mDrawableBox = DrawableBox.obtain(drawable, width, height, this, tag, background, foreground);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public DrawableBox getDrawableBox() {
		return mDrawableBox;
	}

	@Override
	protected void onRecycle() {
		mDrawableBox = null;
		POOL.release(this);
	}

	
	public float getWidth() {
		return mDrawableBox == null ? 0 : mDrawableBox.getWidth();
	}

	
	public float getHeight() {
		return mDrawableBox == null ? 0 : mDrawableBox.getHeight();
	}

	
	public Drawable getDrawable() {
		return mDrawableBox == null ? null : mDrawableBox.getDrawable();
	}

	
	public void setDrawable(@NonNull Drawable drawable) {
		if (mDrawableBox == null) {
			return;
		}

		if (drawable.getIntrinsicWidth() != getWidth() ||
				drawable.getIntrinsicHeight() != getHeight()) {
			Log.w("Emoticon", "drawable size changed, may be cause some problem");
		}
		mDrawableBox.setDrawable(drawable);
	}

	
	public void setTag(Object tag) {
		if (mDrawableBox == null) {
			return;
		}

		mDrawableBox.setTag(tag);
	}

	
	public static Emoticon obtain(Drawable drawable, float width, float height) {
		return obtain(drawable, width, height, null, null, null);
	}

	
	public static Emoticon obtain(Drawable drawable, float width, float height, Object tag, Appearance background, Appearance foreground) {
		Emoticon emoticon = POOL.acquire();
		if (emoticon == null) {
			return new Emoticon(drawable, width, height, tag, background, foreground);
		}

		emoticon.mDrawableBox = DrawableBox.obtain(drawable, width, height, emoticon, tag, background, foreground);
		emoticon.reuse();
		return emoticon;
	}

	public static void clean() {
		POOL.clean();
	}
}
