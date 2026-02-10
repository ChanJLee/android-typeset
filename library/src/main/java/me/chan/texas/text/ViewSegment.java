package me.chan.texas.text;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import me.chan.texas.misc.Rect;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.renderer.selection.SelectionMethod;
import me.chan.texas.renderer.selection.SelectionProvider;
import me.chan.texas.renderer.ui.RendererHost;
import me.chan.texas.renderer.ui.text.ParagraphView;

/**
 * 用户自定义视图片段
 */
public abstract class ViewSegment implements Segment {
	private Rect mRect;

	private int mId;
	private final Args mArgs;

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
	 *                     use {@link ViewSegment(Args)} instead
	 */
	@Deprecated
	public ViewSegment(@LayoutRes int layout, boolean disableReuse) {
		this(layout, disableReuse, null);
	}

	/**
	 * 用户自定义视图
	 *
	 * @param layout       layout id
	 * @param disableReuse 是否需要复用
	 * @param tag          唯一标识
	 *                     use {@link ViewSegment(Args)} instead
	 */
	@Deprecated
	public ViewSegment(@LayoutRes int layout, boolean disableReuse, Object tag) {
		this(new Args(layout).disableReuse(disableReuse).tag(tag));
	}

	public ViewSegment(@NonNull Args args) {
		mArgs = args;
		mId = Segment.nextId();
	}

	@RestrictTo(LIBRARY)
	public final int getLayout() {
		return mArgs.mLayout;
	}

	public final boolean isDisableReuse() {
		return mArgs.mDisableReuse;
	}

	private RendererHost mHost;
	private RecyclerView.ViewHolder mHolder;

	public final void render(View view, SelectionMethod method) {
		SelectionProvider provider = getSelectionProvider();
		if (provider != null) {
			for (int i = 0; i < provider.size(); ++i) {
				SelectionProvider.ParagraphBinding bind = provider.get(i);
				ParagraphView paragraphView = view.findViewById(bind.getId());
				bind.setView(paragraphView);
				paragraphView.setSelectionMethod(method);
				paragraphView.setParagraph(bind.getParagraph());
			}
		}
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
	public final void recycle() {
		mRect = null;
		mId = 0;
		mHost = null;
		mHolder = null;
	}

	@Override
	public final boolean isRecycled() {
		return mId == 0;
	}

	@Nullable
	@Override
	public final Object getTag() {
		return mArgs.mTag;
	}

	@Nullable
	@Override
	public final void getRect(Rect rect) {
		rect.set(mRect);
	}

	@Override
	public final void setPadding(Rect rect) {
		mRect = rect;
	}

	@Nullable
	@Override
	public final Rect getRect() {
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

	@Nullable
	public final SelectionProvider getSelectionProvider() {
		return mArgs.mSelectionProvider;
	}

	@Override
	public final int getIndex() {
		return mHost == null ? -1 : mHost.indexOf(this);
	}

	public static class Args {
		private final int mLayout;
		private boolean mDisableReuse;
		@RestrictTo(RestrictTo.Scope.LIBRARY)
		private Object mTag;
		private SelectionProvider mSelectionProvider;

		public Args(int layout) {
			mLayout = layout;
		}

		public Args disableReuse(boolean disableReuse) {
			mDisableReuse = disableReuse;
			return this;
		}

		public Args tag(Object tag) {
			mTag = tag;
			return this;
		}

		public Args addSelectionProvider(@IdRes int paragraphViewId, Paragraph paragraph) {
			if (mSelectionProvider == null) {
				mSelectionProvider = new SelectionProvider();
			}
			mSelectionProvider.add(new SelectionProvider.ParagraphBinding(paragraphViewId, paragraph));
			return this;
		}
	}
}
