package com.shanbay.lib.texas.renderer.core.graphics;

import static com.shanbay.lib.texas.renderer.core.WorkerScheduler.TASK_QUEUE_RENDER;

import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.RestrictTo;
import androidx.annotation.WorkerThread;

import com.shanbay.lib.texas.renderer.core.WorkerScheduler;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TextureStage {

	private static final boolean DEBUG = false;

	private boolean mAttached = false;
	private DoubleBuffer mBuffer;

	@MainThread
	public void attach() {
		if (mBuffer == null) {
			mBuffer = new DoubleBuffer();
		}
		mAttached = true;
	}

	public boolean isAttached() {
		return mAttached;
	}

	@MainThread
	public void detach() {
		if (mBuffer != null) {
			mBuffer.release();
			mBuffer = null;
		}
		mAttached = false;
	}

	@WorkerThread
	public Canvas lockCanvas(int width, int height) {
		if (mBuffer == null) {
			return null;
		}

		return mBuffer.lockCanvas(width, height);
	}

	@WorkerThread
	public void unlockCanvas() {
		if (mBuffer != null) {
			mBuffer.unlockCanvas();
		}
	}

	@MainThread
	public void draw(Canvas canvas) {
		if (!mAttached || mBuffer == null) {
			return;
		}

		long ts = 0;
		if (DEBUG) {
			ts = SystemClock.elapsedRealtime();
		}

		for (int i = 0; i < 3; ++i) {
			TextureScene picture = mBuffer.getPicture();
			if (picture == null) {
				break;
			}

			canvas.drawPicture(picture);
			if (!picture.isHackIsDrawFailed__()) {
				break;
			}

			if (DEBUG) {
				Log.d("RendererBuffer", "draw failed, retry");
			}
		}

		if (DEBUG) {
			Log.d("RendererBuffer", "draw time: " + (SystemClock.elapsedRealtime() - ts));
		}
	}

	private static class DoubleBuffer implements Runnable {
		private volatile TextureScene mDrewPicture;
		private TextureScene mDrawingPicture;

		private boolean mReleased = false;

		@WorkerThread
		public Canvas lockCanvas(int width, int height) {
			if (mReleased) {
				return null;
			}

			if (mDrawingPicture == null) {
				mDrawingPicture = TextureScene.createPicture();
			}

			return mDrawingPicture.beginRecording(width, height);
		}

		@WorkerThread
		public void unlockCanvas() {
			mDrawingPicture.endRecording();

			// ready recycle
			TextureScene tmp = mDrewPicture;

			// 读写栏栅，不然draw的时候会闪烁
			mDrewPicture = mDrawingPicture;
			mDrawingPicture = null;
			TextureScene.releasePicture(tmp);
		}

		@MainThread
		public void release() {
			WorkerScheduler.odd().submit(-1 /* 基本上是一个不可能的值 */, WorkerScheduler.getTaskQueue(TASK_QUEUE_RENDER), this);
		}

		@MainThread
		public TextureScene getPicture() {
			return mDrewPicture;
		}

		@Override
		public void run() {
			mReleased = true;
			if (mDrewPicture != null) {
				TextureScene.releasePicture(mDrewPicture);
				mDrewPicture = null;
			}

			if (mDrawingPicture != null) {
				TextureScene.releasePicture(mDrawingPicture);
				mDrawingPicture = null;
			}
		}
	}
}
