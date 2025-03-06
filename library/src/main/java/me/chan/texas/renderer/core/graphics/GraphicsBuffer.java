package me.chan.texas.renderer.core.graphics;

import static me.chan.texas.renderer.core.WorkerScheduler.TASK_QUEUE_RENDER;

import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.RestrictTo;
import androidx.annotation.WorkerThread;

import java.util.concurrent.atomic.AtomicReference;

import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.utils.concurrency.TaskQueue;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class GraphicsBuffer {

	private static final boolean DEBUG = false;

	private boolean mAttached = false;

	private DoubleBuffer mBuffer;

	@MainThread
	public void attach(TaskQueue.Token token) {
		if (mBuffer == null) {
			mBuffer = new DoubleBuffer(token);
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
			TexturePicture picture = mBuffer.getPicture();
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

	private static class DoubleBuffer {
		private static final Picture EMPTY_PICTURE = new Picture();
		private final AtomicReference<Picture> mDrewPicture = new AtomicReference<>(EMPTY_PICTURE);
		private TexturePicture mDrawingPicture;

		private final TaskQueue.Token mToken;

		public DoubleBuffer(TaskQueue.Token token) {
			mToken = token;
		}

		@WorkerThread
		public Canvas lockCanvas(int width, int height) {
			if (mDrewPicture.get() == null) {
				return null;
			}

			if (mDrawingPicture != null) {
				throw new IllegalStateException("drawing picture is not null");
			}

			mDrawingPicture = TexturePicture.createPicture();
			return mDrawingPicture.beginRecording(width, height);
		}

		@WorkerThread
		public void unlockCanvas() {
			mDrawingPicture.endRecording();
			TexturePicture current = mDrawingPicture;
			mDrawingPicture = null;

			// ready recycle
			Picture old = mDrewPicture.getAndSet(current);
			if (old == null) {
				TexturePicture.releasePicture(current);
				return;
			}

			if (old != EMPTY_PICTURE) {
				TexturePicture.releasePicture((TexturePicture) old);
			}
		}

		@MainThread
		public void release() {
			final Picture picture = mDrewPicture.getAndSet(null);
			if (picture == null ||picture == EMPTY_PICTURE) {
				return;
			}

			WorkerScheduler.odd().submit(
					mToken /* 基本上是一个不可能的值 */,
					WorkerScheduler.getTaskQueue(TASK_QUEUE_RENDER),
					() -> TexturePicture.releasePicture((TexturePicture) picture));
		}

		@MainThread
		public TexturePicture getPicture() {
			Picture picture = mDrewPicture.get();
			if (picture == EMPTY_PICTURE) {
				return null;
			}

			return (TexturePicture) picture;
		}
	}
}
