package me.chan.texas.text;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import me.chan.texas.misc.Rect;

import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.renderer.ui.RendererHost;
import me.chan.texas.renderer.ui.text.TextureParagraph;

/**
 * 用户自定义视图片段
 */
public abstract class ViewSegment implements Segment {
	private int mLayout;
	private boolean mDisableReuse;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	private Object mTag;
	private Rect mRect;

	private int mId;

	/*
	 * 这个地方只能用layout id来做，不能用view，用id的话，实例是引擎内部创建，
	 * 这样复用的时候不会出现问题，否则的话上层瞎用就会导致不可预见的bug
	 * */

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

	/**
	 * 开始渲染
	 *
	 * @param view 当前所要捆绑data的视图
	 */
	protected abstract void onRender(View view);

	@Override
	public void recycle() {
		mTag = null;
		mRect = null;
		mDisableReuse = false;
		mLayout = 0;
		mId = 0;
		mHost = null;
		mHolder = null;
	}

	@Override
	public boolean isRecycled() {
		return mId == 0;
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
	public void setPadding(Rect rect) {
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

	@Nullable
	public TextureParagraph getTextureParagraph() {
		return null;
	}
}
