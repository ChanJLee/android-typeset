package me.chan.texas.text;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import me.chan.texas.misc.Rect;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.renderer.ui.RendererHost;


public abstract class ViewSegment extends DefaultRecyclable implements Segment {
	private int mLayout;
	private boolean mDisableReuse;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	private Object mTag;
	private Rect mRect;

	private int mId;

	

	
	public ViewSegment(@LayoutRes int layout) {
		this(layout, false);
	}

	
	public ViewSegment(@LayoutRes int layout, boolean disableReuse) {
		this(layout, disableReuse, null);
	}

	
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

	private RendererHost mHost;
	private RecyclerView.ViewHolder mHolder;

	public final void render(View view) {
		onRender(view);
	}

	public final void requestRedraw() {
		if (mHost != null) {
			mHost.updateSegment(mHolder, this);
		}
	}

	
	protected abstract void onRender(View view);

	@Override
	protected final void onRecycle() {
		mTag = null;
		mRect = null;
		mDisableReuse = false;
		mLayout = 0;
		mId = 0;
		mHost = null;
		mHolder = null;
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
	public final void attachToWindow(RecyclerView.ViewHolder viewHolder) {
		mHolder = viewHolder;
		onAttachedToWindow();
	}

	protected void onAttachedToWindow() {
	}

	@RestrictTo(LIBRARY)
	@Override
	public final void detachFromWindow(RecyclerView.ViewHolder viewHolder) {
		onDetachedFromWindow();
		mHolder = null;
	}

	@Override
	public final void bind(RendererHost host) {
		mHost = host;
	}

	protected void onDetachedFromWindow() {
	}

	@Override
	public final int getIndex() {
		return mHost == null ? -1 : mHost.indexOf(this);
	}
}
