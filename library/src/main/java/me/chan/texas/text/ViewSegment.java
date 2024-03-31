package me.chan.texas.text;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.DefaultRecyclable;

/**
 * 用户自定义视图片段
 */
public abstract class ViewSegment extends DefaultRecyclable implements Segment {
	private int mLayout;
	private boolean mIncremental;
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
	 * @param layout      layout id
	 * @param incremental 是否是增量更新
	 */
	public ViewSegment(@LayoutRes int layout, boolean incremental) {
		this(layout, incremental, null);
	}

	/**
	 * 用户自定义视图
	 *
	 * @param layout      layout id
	 * @param incremental 是否是增量更新
	 * @param tag         唯一标识
	 */
	public ViewSegment(@LayoutRes int layout, boolean incremental, Object tag) {
		mTag = tag;
		mLayout = layout;
		mIncremental = incremental;
		mId = Segment.nextId();
	}

	@RestrictTo(LIBRARY)
	public int getLayout() {
		return mLayout;
	}

	public boolean isIncremental() {
		return mIncremental;
	}

	public final void render(View view) {
		onRender(view);
	}

	/**
	 * 开始渲染
	 *
	 * @param view 当前所要捆绑data的视图
	 */
	protected abstract void onRender(View view);

	@Override
	public final void recycle() {
		// view segment 不支持复用
		// 因为可能导致内存泄露
		if (isRecycled()) {
			return;
		}
		super.recycle();
		mTag = null;
		mRect = null;
		mIncremental = false;
		mLayout = 0;
		mId = 0;
	}

	@Override
	public final boolean isRecycled() {
		return super.isRecycled();
	}

	@Override
	public final void reuse() {
		super.reuse();
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
}
