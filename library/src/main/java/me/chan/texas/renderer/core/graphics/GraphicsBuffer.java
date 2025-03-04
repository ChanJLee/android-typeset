package me.chan.texas.renderer.core.graphics;

import static me.chan.texas.renderer.core.WorkerScheduler.TASK_QUEUE_RENDER;

import android.graphics.Canvas;
import android.graphics.RenderNode;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.annotation.WorkerThread;

import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.utils.concurrency.TaskQueue;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class GraphicsBuffer {

	private static final boolean DEBUG = false;

	private boolean mAttached = false;

	private RendererBuffer mBuffer;

	@MainThread
	public void attach(TaskQueue.Token token) {
		if (mBuffer == null) {
			mBuffer = new RendererBufferCompat(token);
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

		mBuffer.draw(canvas);

		if (DEBUG) {
			Log.d("RendererBuffer", "draw time: " + (SystemClock.elapsedRealtime() - ts));
		}
	}

	private interface RendererBuffer {

		Canvas lockCanvas(int width, int height);

		void unlockCanvas();

		void release();

		void draw(Canvas canvas);
	}

	private static class DoubleBufferCompat implements Runnable, RendererBuffer {
		private volatile TexturePicture mDrewPicture;
		private TexturePicture mDrawingPicture;

		private boolean mReleased = false;

		private final TaskQueue.Token mToken;

		public DoubleBufferCompat(TaskQueue.Token token) {
			mToken = token;
		}

		@WorkerThread
		@Override
		public Canvas lockCanvas(int width, int height) {
			if (mReleased) {
				return null;
			}

			if (mDrawingPicture == null) {
				mDrawingPicture = TexturePicture.createPicture();
			}

			return mDrawingPicture.beginRecording(width, height);
		}

		@WorkerThread
		@Override
		public void unlockCanvas() {
			mDrawingPicture.endRecording();

			// ready recycle
			TexturePicture tmp = mDrewPicture;

			// 读写栏栅，不然draw的时候会闪烁
			mDrewPicture = mDrawingPicture;
			mDrawingPicture = null;
			TexturePicture.releasePicture(tmp);
		}

		@MainThread
		@Override
		public void release() {
			WorkerScheduler.odd().submit(mToken /* 基本上是一个不可能的值 */, WorkerScheduler.getTaskQueue(TASK_QUEUE_RENDER), this);
		}

		@Override
		public void draw(Canvas canvas) {
			for (int i = 0; i < 3; ++i) {
				TexturePicture picture = mDrewPicture;
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
		}

		@Override
		public void run() {
			mReleased = true;
			if (mDrewPicture != null) {
				TexturePicture.releasePicture(mDrewPicture);
				mDrewPicture = null;
			}

			if (mDrawingPicture != null) {
				TexturePicture.releasePicture(mDrawingPicture);
				mDrawingPicture = null;
			}
		}
	}

	private static class RendererBufferCompat implements Runnable, RendererBuffer {
		private volatile TexturePicture mPicture;
		private boolean mReleased = false;
		private final TaskQueue.Token mToken;

		public RendererBufferCompat(TaskQueue.Token token) {
			mToken = token;
		}

		@WorkerThread
		@Override
		public Canvas lockCanvas(int width, int height) {
			if (mReleased) {
				return null;
			}

			if (mPicture == null) {
				mPicture = TexturePicture.createPicture();
			}

			return mPicture.beginRecording(width, height);
		}

		@WorkerThread
		@Override
		public void unlockCanvas() {
			mPicture.endRecording();
		}

		@MainThread
		@Override
		public void release() {
			WorkerScheduler.odd().submit(mToken /* 基本上是一个不可能的值 */, WorkerScheduler.getTaskQueue(TASK_QUEUE_RENDER), this);
		}

		@Override
		public void draw(Canvas canvas) {
			for (int i = 0; i < 3; ++i) {
				TexturePicture picture = mPicture;
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
		}

		@Override
		public void run() {
			mReleased = true;
			if (mPicture != null) {
				TexturePicture.releasePicture(mPicture);
				mPicture = null;
			}
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	private static class RendererBuffer28 implements Runnable, RendererBuffer {
		private volatile RenderNode mBuffer;

		private boolean mReleased = false;

		private final TaskQueue.Token mToken;
		private static final AtomicInteger UUID = new AtomicInteger();

		public RendererBuffer28(TaskQueue.Token token) {
			mToken = token;
		}

		@WorkerThread
		@Override
		public Canvas lockCanvas(int width, int height) {
			if (mReleased) {
				return null;
			}

			if (mBuffer == null) {
				mBuffer = new RenderNode("buffer" + UUID.incrementAndGet());
			}

			mBuffer.setPosition(0, 0, width, height);
			return mBuffer.beginRecording(width, height);
		}

		@WorkerThread
		@Override
		public void unlockCanvas() {
			mBuffer.endRecording();
		}

		@MainThread
		@Override
		public void release() {
			WorkerScheduler.odd().submit(mToken /* 基本上是一个不可能的值 */, WorkerScheduler.getTaskQueue(TASK_QUEUE_RENDER), this);
		}

		@Override
		public void run() {
			mReleased = true;
			if (mBuffer != null) {
				release(mBuffer);
				mBuffer = null;
			}
		}

		@Override
		public void draw(Canvas canvas) {
			RenderNode node = mBuffer;
			if (node == null) {
				return;
			}

			canvas.drawRenderNode(node);
		}

		private static void release(RenderNode node) {
			// TODO
		}
	}
}
