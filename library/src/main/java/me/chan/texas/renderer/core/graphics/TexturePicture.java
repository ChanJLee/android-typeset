package me.chan.texas.renderer.core.graphics;

import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.BuildConfig;
import me.chan.texas.misc.ResourceManager;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TexturePicture extends Picture {

	private static final AtomicInteger CREATE_PICTURE_COUNT = new AtomicInteger(0);
	private static final AtomicInteger RELEASE_PICTURE_COUNT = new AtomicInteger(0);


	private static final boolean DEBUG = false;

	/**
	 * 5.0 以下的系统，会在Picture.finalize()中调用native_destroy()，导致指针重复free
	 */
	private boolean mHackIsReleased__ = false;

	private boolean mHackIsDrawFailed__ = false;

	public TexturePicture() {
		if (BuildConfig.DEBUG) {
			ResourceManager.hold(this, o -> o.mHackIsReleased__);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		/* avoid 5.0 指针重复free的bug */
		releaseHack__();
	}

	public synchronized void releaseHack__() {
		if (mHackIsReleased__) {
			return;
		}

		try {
			super.finalize();
			mHackIsReleased__ = true;
		} catch (Throwable ignored) {
			/* do nothing */
		}
	}

	@Override
	public synchronized void draw(@NonNull Canvas canvas) {
		if (mHackIsReleased__) {
			mHackIsDrawFailed__ = true;
			return;
		}

		long ts = 0;
		if (DEBUG) {
			ts = SystemClock.elapsedRealtime();
		}

		super.draw(canvas);

		if (DEBUG) {
			Log.d("TexasPicture", "use time: " + (SystemClock.elapsedRealtime() - ts));
		}
	}

	public boolean isHackIsDrawFailed__() {
		return mHackIsDrawFailed__;
	}

	@Override
	public synchronized void endRecording() {
		// 主线程会在draw的时候主动call一下end，如果已经释放了，那么就崩溃了
		if (mHackIsReleased__) {
			return;
		}

		super.endRecording();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static TexturePicture createPicture() {
		CREATE_PICTURE_COUNT.incrementAndGet();
		return new TexturePicture();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static void releasePicture(TexturePicture picture) {
		if (picture == null) {
			return;
		}

		picture.releaseHack__();
		RELEASE_PICTURE_COUNT.incrementAndGet();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static String getPictureStats() {
		return "Create: " + CREATE_PICTURE_COUNT.get() + ", Release: " + RELEASE_PICTURE_COUNT.get();
	}
}
