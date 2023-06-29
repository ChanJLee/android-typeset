package com.shanbay.biz.web.handler.webrec;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Build;
import android.text.TextUtils;
import android.view.Surface;
import android.view.View;

import com.shanbay.biz.webview.R;
import com.shanbay.lib.log.Log;

import java.io.File;
import java.lang.reflect.Method;

public class ViewVideoRecorder implements Runnable {
	private static Method onDrawMethod;
	static {
		try {
			onDrawMethod = View.class.getDeclaredMethod("onDraw", Canvas.class);
			onDrawMethod.setAccessible(true);
		} catch (Throwable throwable) {
			onDrawMethod = null;
		}
	}

	private Thread mDrawThread;
	private boolean mIsWorking = false;
	private Throwable mThrowable;
	private final File mOutput;
	private int mWidth;
	private int mHeight;
	private final View mTargetView;
	private Bitmap mBitmap;
	private final Paint mPaint = new Paint();

	public ViewVideoRecorder(View targetView, File output) {
		mTargetView = targetView;
		mOutput = output;

		Resources resources = mTargetView.getResources();
		mWidth = (int) resources.getDimension(R.dimen.web_rc_width);
		mHeight = (int) resources.getDimension(R.dimen.web_rc_height);
		if (mWidth % 2 == 1) {
			--mWidth;
		}
		if (mHeight % 2 == 1) {
			--mHeight;
		}
	}

	@SuppressLint("WrongConstant")
	public boolean start() {
		stop(false);

		mIsWorking = true;
		mDrawThread = new Thread(this, "webrc_draw");
		mDrawThread.start();
		return true;
	}

	public void stop(boolean block) {
		if (!mIsWorking) {
			return;
		}

		mIsWorking = false;
		if (block && mDrawThread != null) {
			try {
				mDrawThread.join();
			} catch (InterruptedException e) {
				/* do nothing */
			}
		}
		mDrawThread = null;
	}

	@SuppressLint("WrongConstant")
	@Override
	public void run() {
		mOutput.delete();

		File tmp = new File(mOutput.getParentFile(), System.currentTimeMillis() + "tmp.track.mp4");
		MediaRecorder mediaRecorder = new MediaRecorder();
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mediaRecorder.setVideoFrameRate(30);
		mediaRecorder.setVideoSize(mWidth, mHeight);
		mediaRecorder.setVideoEncodingBitRate(mWidth * mHeight * 8);
		mediaRecorder.setOutputFile(tmp.getAbsolutePath());
		mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				mThrowable = new IllegalStateException("内部错误: " + what + "," + extra);
			}
		});

		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
		} catch (Throwable e) {
			// todo notify
			Log.w("WebViewRc", e);
			mThrowable = e;
			return;
		}

		Surface encodeSurface = mediaRecorder.getSurface();
		ImageReader imageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 1);

		Surface drawSurface = imageReader.getSurface();
		while (mIsWorking && mThrowable == null) {
			Canvas canvas = null;
			try {
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
					canvas = drawSurface.lockHardwareCanvas();
				} else {
					canvas = drawSurface.lockCanvas(null);
				}

				try {
					onDrawMethod.invoke(mTargetView, canvas);
				} catch (RuntimeException throwable) {
					String msg = throwable.getMessage();
					if (!TextUtils.equals("Probable deadlock detected due to WebView API being called on incorrect thread while the UI thread is blocked.", msg)) {
						throw throwable;
					}
				}
			} catch (Throwable throwable) {
				Log.w("WebViewRc", throwable);
				mThrowable = throwable;
				break;
			} finally {
				try {
					drawSurface.unlockCanvasAndPost(canvas);
				} catch (Throwable ignore) {
				}
			}

			Image image = null;
			try {
				image = imageReader.acquireLatestImage();
				handleInputImage(image, encodeSurface);
			} catch (Throwable throwable) {
				Log.w("WebViewRc", throwable);
			} finally {
				if (image != null) {
					image.close();
				}
			}
		}

		imageReader.close();
		try {
			mediaRecorder.stop();
			mediaRecorder.release();
			tmp.renameTo(mOutput);
		} catch (Throwable throwable) {
			mThrowable = throwable;
			tmp.delete();
		}
	}

	private void handleInputImage(Image image, Surface surface) {
		if (image == null) {
			return;
		}

		Image.Plane[] planes = image.getPlanes();
		if (planes == null || planes.length == 0) {
			return;
		}

		Image.Plane plane = planes[0];

		Canvas canvas = null;
		try {
			canvas = surface.lockCanvas(null);

			int pixelStride = plane.getPixelStride();
			int rowStride = plane.getRowStride();
			int rowPadding = rowStride - pixelStride * mWidth;

			int bitmapWidth = mWidth + rowPadding / pixelStride;
			int bitmapHeight = mHeight;
			if (mBitmap == null || mBitmap.getWidth() != bitmapWidth || mBitmap.getHeight() != bitmapHeight) {
				if (mBitmap != null) {
					mBitmap.recycle();
				}

				mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
			}

			// create bitmap
			mBitmap.copyPixelsFromBuffer(plane.getBuffer());
			canvas.drawBitmap(mBitmap, 0, 0, mPaint);
		} catch (Throwable throwable) {
			Log.w("WebViewRc", throwable);
			mThrowable = throwable;
			// todo do nothing
		} finally {
			try {
				if (canvas != null) {
					surface.unlockCanvasAndPost(canvas);
				}
			} catch (Throwable throwable) {
				Log.w("WebViewRc", throwable);
			}
		}
	}

	public Throwable getThrowable() {
		return mThrowable;
	}

	private static void d(String msg) {
		Log.d("WebViewRc", msg);
	}
}
