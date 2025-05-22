package me.chan.texas.renderer.ui.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.graphics.GraphicsBuffer;
import me.chan.texas.renderer.core.worker.RenderWorker;
import me.chan.texas.text.layout.Layout;

@SuppressLint("ViewConstructor")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TextureParagraphView0 extends AbsTextureParagraphView {

	private final GraphicsBuffer mGraphicsBuffer;

	public TextureParagraphView0(Context context) {
		this(context, DEFAULT_RELAYOUT_PREDICATE);
	}

	public TextureParagraphView0(Context context, RelayoutPredicate onSizeChangedListener) {
		super(context, onSizeChangedListener);
		mGraphicsBuffer = new GraphicsBuffer();
	}

	@Override
	protected void onRender() {
		if (mParagraph == null) {
			return;
		}

		if (!mGraphicsBuffer.isAttached()) {
			mGraphicsBuffer.attach(getToken());
		}

		Layout layout = mParagraph.getLayout();
		RenderWorker.Args args = RenderWorker.Args.obtain(
				mParagraph,
				mRenderOption,
				this,
				layout.getWidth(),
				mPaintSet,
				mParagraphDecor
		);

		if (!isInEditMode()) {
			WorkerScheduler.render().submit(getToken(), args);
			return;
		}

		WorkerScheduler.render().submitSync(getToken(), args);
	}

	@Nullable
	@Override
	public Canvas lockCanvas(int width, int height) {
		if (width <= 0 || height <= 0) {
			return null;
		}

		return mGraphicsBuffer.lockCanvas(width, height);
	}

	@Override
	public void unlockCanvasAndPost(Canvas canvas) {
		if (canvas == null) {
			return;
		}

		mGraphicsBuffer.unlockCanvas();
	}

	@Override
	public void syncUI() {
		invalidate();
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		boolean ret = mGraphicsBuffer.draw(canvas);
		if (GraphicsBuffer.DEBUG && !ret) {
			Log.d("TextureParagraphView", "draw failed: " + mParagraph);
		}
	}

	@Override
	protected void onClear() {
		mGraphicsBuffer.detach();
	}
}
