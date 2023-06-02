package com.shanbay.lib.texas.renderer.ui.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.renderer.core.WorkerScheduler;
import com.shanbay.lib.texas.renderer.core.graphics.TextureStage;
import com.shanbay.lib.texas.renderer.core.worker.RenderWorker;
import com.shanbay.lib.texas.text.layout.Layout;

@SuppressLint("ViewConstructor")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TextureParagraphView0 extends AbsTextureParagraphView {

	private final TextureStage mTextureStage;

	public TextureParagraphView0(Context context) {
		super(context);
		mTextureStage = new TextureStage();
	}

	@Override
	protected void onRender() {
		if (mParagraph == null) {
			return;
		}

		if (!mTextureStage.isAttached()) {
			mTextureStage.attach();
		}

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
		WorkerScheduler.render().submit(getTaskId(), args);
	}

	@Nullable
	@Override
	public Canvas lockCanvas(int width, int height) {
		if (width <= 0 || height <= 0) {
			return null;
		}

		return mTextureStage.lockCanvas(width, height);
	}

	@Override
	public void unlockCanvasAndPost(Canvas canvas) {
		if (canvas == null) {
			return;
		}

		mTextureStage.unlockCanvas();
	}

	@Override
	public void syncUI() {
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mTextureStage.draw(canvas);
	}

	@Override
	public void clear() {
		super.clear();
		mTextureStage.detach();
	}
}
