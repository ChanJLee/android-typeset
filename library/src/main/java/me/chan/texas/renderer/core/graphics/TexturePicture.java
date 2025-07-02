package me.chan.texas.renderer.core.graphics;

import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.util.concurrent.atomic.AtomicInteger;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TexturePicture extends Picture {

	public static final AtomicInteger ALIVE_COUNT = new AtomicInteger(0);

	private static final boolean DEBUG = false;

	
	private boolean mHackIsReleased__ = false;

	private boolean mHackIsDrawFailed__ = false;

	@Override
	protected void finalize() throws Throwable {
		
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

		if (mHackIsReleased__) {
			return;
		}

		super.endRecording();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static TexturePicture createPicture() {
		ALIVE_COUNT.incrementAndGet();
		return new TexturePicture();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static void releasePicture(TexturePicture picture) {
		if (picture == null) {
			return;
		}

		picture.releaseHack__();
		ALIVE_COUNT.decrementAndGet();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static String getPictureStats() {
		return "alive picture: " + ALIVE_COUNT.get();
	}
}
