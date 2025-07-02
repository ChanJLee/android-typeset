package me.chan.texas.renderer.core.graphics;

import static me.chan.texas.renderer.core.WorkerScheduler.TASK_QUEUE_RENDER;

import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;

import java.util.concurrent.atomic.AtomicReference;

import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.utils.concurrency.Worker;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class GraphicsBuffer {

	public static final boolean DEBUG = false;

	private boolean mAttached = false;

	private DoubleBuffer mBuffer;

	@MainThread
	public void attach(@NonNull Worker.Token token) {
		if (token == null) {
			throw new IllegalArgumentException("invalid argument");
		}

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
	public boolean draw(Canvas canvas) {
		if (!mAttached || mBuffer == null) {
			if (DEBUG) {
				Log.d("RendererBuffer", "draw failed, not attached");
			}
			return false;
		}

		long ts = 0;
		if (DEBUG) {
			ts = SystemClock.elapsedRealtime();
		}

		boolean ret = mBuffer.draw(canvas);

		if (DEBUG) {
			Log.d("RendererBuffer", "draw time: " + (SystemClock.elapsedRealtime() - ts));
		}
		return ret;
	}

	@VisibleForTesting
	static class DoubleBuffer {
		private static final Picture EMPTY_PICTURE = new Picture();
		private final AtomicReference<Picture> mPicture = new AtomicReference<>(EMPTY_PICTURE);
		private TexturePicture mPendingPicture;

		private final Worker.Token mToken;

		public DoubleBuffer(Worker.Token token) {
			mToken = token;
		}

		@WorkerThread
		public Canvas lockCanvas(int width, int height) {
			if (mPicture.get() == null) {
				return null;
			}

			if (mPendingPicture != null) {
				throw new IllegalStateException("drawing picture is not null");
			}

			mPendingPicture = TexturePicture.createPicture();
			return mPendingPicture.beginRecording(width, height);
		}

		@WorkerThread
		public void unlockCanvas() {

			if (mPendingPicture == null) {
				throw new IllegalStateException("No drawing operation is in progress.");
			}
			mPendingPicture.endRecording();

			TexturePicture pending = mPendingPicture;
			mPendingPicture = null;

			Picture old = mPicture.getAndSet(pending);
			if (old == null) {
				TexturePicture.releasePicture(pending);
				return;
			}

			if (old != EMPTY_PICTURE) {
				TexturePicture.releasePicture((TexturePicture) old);
			}
		}

		@MainThread
		public void release() {
			final Picture picture = mPicture.getAndSet(null);
			if (picture == null || picture == EMPTY_PICTURE) {
				return;
			}

			WorkerScheduler.odd().submit(
					mToken ,
					WorkerScheduler.getTaskQueue(TASK_QUEUE_RENDER),
					() -> TexturePicture.releasePicture((TexturePicture) picture));
		}

		@MainThread
		@VisibleForTesting
		final TexturePicture getPicture() {
			Picture picture = mPicture.get();
			if (picture == EMPTY_PICTURE) {
				return null;
			}

			return (TexturePicture) picture;
		}

		@MainThread
		public boolean draw(Canvas canvas) {
			TexturePicture picture = getPicture();
			if (picture == null) {
				if (DEBUG) {
					Log.d("RendererBuffer", "draw failed, picture is null");
				}
				return false;
			}

			canvas.drawPicture(picture);
			boolean ret = picture.isHackIsDrawFailed__();

			if (DEBUG && ret) {
				Log.d("RendererBuffer", "draw failed, retry");
			}

			return ret;
		}
	}
}
