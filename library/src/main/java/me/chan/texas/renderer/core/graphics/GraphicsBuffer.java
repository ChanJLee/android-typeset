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
		private final AtomicReference<Picture> mPicture = new AtomicReference<>(EMPTY_PICTURE);
		private TexturePicture mPendingPicture;

		private final TaskQueue.Token mToken;

		public DoubleBuffer(TaskQueue.Token token) {
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
			// Ensure that a drawing operation was in progress.
			if (mPendingPicture == null) {
				throw new IllegalStateException("No drawing operation is in progress.");
			}
			mPendingPicture.endRecording();

			TexturePicture pending = mPendingPicture;
			mPendingPicture = null;
			// ready recycle
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
					mToken /* 基本上是一个不可能的值 */,
					WorkerScheduler.getTaskQueue(TASK_QUEUE_RENDER),
					() -> TexturePicture.releasePicture((TexturePicture) picture));
		}

		@MainThread
		public TexturePicture getPicture() {
			Picture picture = mPicture.get();
			if (picture == EMPTY_PICTURE) {
				return null;
			}

			return (TexturePicture) picture;
		}
	}
}
