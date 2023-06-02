package com.shanbay.lib.texas.renderer.ui.text;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.renderer.core.WorkerScheduler;
import com.shanbay.lib.texas.renderer.core.worker.RenderWorker;
import com.shanbay.lib.texas.text.layout.Layout;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TextureParagraphView0Compat extends AbsTextureParagraphView {
	private Canvas mCanvas;

	public TextureParagraphView0Compat(Context context) {
		super(context);
	}

	@Override
	protected void onRender() {
		invalidate();
	}

	@Override
	public Canvas lockCanvas(int width, int height) {
		return mCanvas;
	}

	@Override
	public void unlockCanvasAndPost(Canvas canvas) {
		mCanvas = null;
	}

	@Override
	public void syncUI() {
		/* do nothing */
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mParagraph == null) {
			return;
		}

		mCanvas = canvas;
		Layout layout = mParagraph.getLayout();
		RenderWorker.Args args = RenderWorker.Args.obtain(
				mParagraph,
				mRenderOption,
				this,
				layout.getWidth(),
				mPaintSet,
				mParagraphSelection,
				mHighlight,
				mParagraphDecor
		);
		WorkerScheduler.render().submitSync(getTaskId(), args);
	}
}
