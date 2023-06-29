package me.chan.androidtex;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.media.MediaRecorder;
import android.view.Surface;
import android.view.View;

import java.io.File;

public class ViewVideoRecorder {
	private final View mTargetView;
	private Surface encodeSurface;
	private MediaRecorder mediaRecorder;

	public ViewVideoRecorder(View targetView) {
		mTargetView = targetView;
	}

	@SuppressLint("WrongConstant")
	public boolean start(File output) {
		output.delete();

		int width = mTargetView.getWidth();
		int height = mTargetView.getHeight();
		if (width % 2 == 1) {
			--width;
		}
		if (height % 2 == 1) {
			--height;
		}

		mediaRecorder = new MediaRecorder();
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mediaRecorder.setVideoFrameRate(30);
		mediaRecorder.setVideoSize(width, height);
		mediaRecorder.setVideoEncodingBitRate(width * height * 8);
		mediaRecorder.setOutputFile(output.getAbsolutePath());
		mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
			}
		});

		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
		} catch (Throwable e) {
			return false;
		}

		encodeSurface = mediaRecorder.getSurface();
		return true;
	}

	public void take() {
		Canvas canvas = encodeSurface.lockCanvas(null);
		mTargetView.draw(canvas);
		encodeSurface.unlockCanvasAndPost(canvas);
	}

	public void stop() {
		try {
			mediaRecorder.stop();
			mediaRecorder.release();
		} catch (Throwable ignore) {
		}
	}
}
