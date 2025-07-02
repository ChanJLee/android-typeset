package me.chan.texas.renderer.ui.text;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.worker.RenderWorker;
import me.chan.texas.text.layout.Layout;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TextureParagraphView0Compat extends AbsTextureParagraphView {
	private Canvas mCanvas;

	public TextureParagraphView0Compat(Context context) {
		this(context, DEFAULT_RELAYOUT_PREDICATE);
	}

	public TextureParagraphView0Compat(Context context, RelayoutPredicate onSizeChangedListener) {
		super(context, onSizeChangedListener);
	}

	@Override
	protected void onClear() {
		
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
		
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
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
				mParagraphDecor
		);
		WorkerScheduler.render().submitSync(getToken(), args);
	}
}
