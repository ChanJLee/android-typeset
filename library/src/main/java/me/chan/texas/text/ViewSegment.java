package me.chan.texas.text;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.renderer.ui.TexasRendererAdapter;

/**
 * 用户自定义视图片段
 */
public abstract class ViewSegment extends DefaultRecyclable implements Segment {
	private int mLayout;
	private boolean mDisableReuse;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	private Object mTag;
	private Rect mRect;

	private int mId;

	/**
	 * 用户自定义视图
	 *
	 * @param layout layout id
	 */
	public ViewSegment(@LayoutRes int layout) {
		this(layout, false);
	}

	/**
	 * 用户自定义视图
	 *
	 * @param layout       layout id
	 * @param disableReuse 是否需要复用
	 */
	public ViewSegment(@LayoutRes int layout, boolean disableReuse) {
		this(layout, disableReuse, null);
	}

	/**
	 * 用户自定义视图
	 *
	 * @param layout       layout id
	 * @param disableReuse 是否需要复用
	 * @param tag          唯一标识
	 */
	public ViewSegment(@LayoutRes int layout, boolean disableReuse, Object tag) {
		mTag = tag;
		mLayout = layout;
		mDisableReuse = disableReuse;
		mId = Segment.nextId();
	}

	@RestrictTo(LIBRARY)
	public int getLayout() {
		return mLayout;
	}

	public boolean isDisableReuse() {
		return mDisableReuse;
	}

	private TexasRendererAdapter mAdapter;

	public final void render(View view) {
		onRender(view);
	}

	protected void requestRedraw() {
		if (mAdapter != null) {
			mAdapter.sendSignal(this, TexasRendererAdapter.SIG_REDRAW);
		}
	}

	/**
	 * 开始渲染
	 *
	 * @param view 当前所要捆绑data的视图
	 */
	protected abstract void onRender(View view);

	@Override
	protected final void onRecycle() {
		mTag = null;
		mRect = null;
		mDisableReuse = false;
		mLayout = 0;
		mId = 0;
	}

	@Nullable
	@Override
	public Object getTag() {
		return mTag;
	}

	@Nullable
	@Override
	public void getRect(Rect rect) {
		rect.set(mRect);
	}

	@Override
	public void setRect(Rect rect) {
		mRect = rect;
	}

	@Nullable
	@Override
	public Rect getRect() {
		return mRect;
	}

	@Override
	public final int getId() {
		return mId;
	}

	@RestrictTo(LIBRARY)
	@Override
	public final void attachToWindow(TexasRendererAdapter adapter) {
		onAttachedToWindow();
	}

	protected void onAttachedToWindow() {
	}

	@RestrictTo(LIBRARY)
	@Override
	public final void detachFromWindow(TexasRendererAdapter adapter) {
		mAdapter = null;
		onDetachedFromWindow();
	}

	protected void onDetachedFromWindow() {
	}
}
