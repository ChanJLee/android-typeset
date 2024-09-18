package me.chan.texas.renderer;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.chan.texas.BuildConfig;
import me.chan.texas.R;
import me.chan.texas.image.ImageLoader;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.TypesetEngine;
import me.chan.texas.renderer.highlight.Highlight;
import me.chan.texas.renderer.highlight.HighlightManager;
import me.chan.texas.renderer.highlight.ParagraphHighlight;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.renderer.selection.SelectionManager;
import me.chan.texas.renderer.ui.RendererAdapter;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.renderer.ui.rv.SegmentItemDecoration;
import me.chan.texas.renderer.ui.rv.TexasLinearLayoutManager;
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.text.Document;
import me.chan.texas.utils.TexasUtils;
import me.chan.texas.utils.concurrency.TaskQueue;

/**
 * 协调各个组件一起工作
 */
@RestrictTo(LIBRARY)
public class Renderer implements SelectionManager.Listener {
	private final TaskQueue.Token mToken;

	/**
	 * 显示参数
	 */
	private RenderOption mRenderOption;
	/**
	 * 显示总窗口
	 */
	private final TexasView mTexasView;

	/**
	 * 排版子系统
	 */
	private TypesetEngine mTypesetEngine;
	/**
	 * 数据子系统
	 */
	private final RendererAdapter mAdapter;
	/**
	 * 视图显示窗口
	 */
	private final TexasRecyclerView mImpl;
	/**
	 * 视图排版子系统
	 */
	private final TexasLinearLayoutManager mLinearLayoutManager;
	/**
	 * 内容选择子系统
	 */
	private final SelectionManager mSelectionManager;
	/**
	 * 内容高亮
	 */
	private final HighlightManager mHighlightManager;

	private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
			mTexasView.notifyScrollStateChanged(rvState2InternalState(newState));
		}

		@Override
		public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
			mTexasView.notifyScrollChanged(dx, dy);
		}
	};

	private TypesetEngine.Listener mListener = new TypesetEngine.Listener() {
		@Override
		public void onStart(LoadingStrategy strategy) {
			mTexasView.notifyRenderStart(strategy);
		}

		@Override
		public void onFailure(LoadingStrategy strategy, Throwable throwable) {
			mTexasView.notifyRenderError(strategy, throwable);
		}

		@Override
		public void onSuccess(LoadingStrategy strategy, PaintSet paintSet, Document doc, int start, int end) {
			render(strategy, paintSet, doc, start, end);
		}
	};

	public Renderer(final TexasView texasView, RenderOption renderOption, TaskQueue.Token token) {
		mToken = token;
		mTexasView = texasView;
		mRenderOption = renderOption;

		// misc modules
		Context context = texasView.getContext();
		ImageLoader imageLoader = new ImageLoader(context);
		LayoutInflater layoutInflater = LayoutInflater.from(context);

		// core
		mTypesetEngine = new TypesetEngine(mRenderOption, mToken);

		// rv
		mLinearLayoutManager = new TexasLinearLayoutManager(context);
		mImpl = new TexasRecyclerView(new ContextThemeWrapper(context, R.style.me_chan_texas_TexasRecyclerView), mLinearLayoutManager);
		mImpl.setClipToPadding(false);
		mImpl.setClipChildren(false);
		mImpl.setOnClickedListener(mTexasView::notifyEmptyClicked);
		mImpl.setLayoutManager(mLinearLayoutManager);
		texasView.addView(mImpl,
				new TexasView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT)
		);
		mImpl.addOnScrollListener(mOnScrollListener);

		mAdapter = new RendererAdapter(layoutInflater, imageLoader, mImpl.getRecycledViewPool(), mImpl);
		mAdapter.setListener(new RendererAdapter.Listener() {
			@Override
			public void onSegmentClicked(TouchEvent event, Object tag) {
				mTexasView.notifySegmentClicked(event, tag);
			}

			@Override
			public void onSegmentDoubleClicked(TouchEvent event, Object tag) {
				mTexasView.notifySegmentDoubleClicked(event, tag);
			}

			@Override
			public void onLoadingMore(int count) {
				mTexasView.scheduleLoadMore();
			}

			@Override
			public void onLoadingPrevious() {
				mTexasView.scheduleLoadPrevious();
			}
		});
		mImpl.addItemDecoration(new SegmentItemDecoration(mAdapter));

		// selection
		mSelectionManager = new SelectionManager(mAdapter, mLinearLayoutManager, this, texasView, mImpl);
		mAdapter.setSelectionManager(mSelectionManager);

		// highlight
		mHighlightManager = new HighlightManager(mAdapter);
		mAdapter.setHighlightManager(mHighlightManager);

		// adapter
		mImpl.setAdapter(mAdapter);
	}

	/**
	 * @param width 期望的宽度，如果是负值，那么就忽略排版，只会解析
	 */
	public void load(String reason, int width, LoadingStrategy strategy) {
		d("load, reason: " + reason);

		if (mTypesetEngine == null) {
			w("render, core is null");
			return;
		}

		mTypesetEngine.load(reason, width, strategy, mTexasView.getAdapter(), mListener);
	}

	public void typeset(String reason, int width, LoadingStrategy strategy) {
		if (BuildConfig.DEBUG) {
			Log.d("TexasRenderer", "typeset, reason: " + reason);
		}

		// 重新排版会将之前的解析任务都失效
		mTypesetEngine.resize(reason, width, strategy, mListener);
	}

	private void render(LoadingStrategy strategy, PaintSet paintSet, Document document, int start, int end) {
		if (strategy != LoadingStrategy.LOAD_PREVIOUS &&
				strategy != LoadingStrategy.LOAD_MORE) {
			clearHighlight();
			clearSelection();
		}

		mAdapter.render(strategy, paintSet, document, start, end, mRenderOption);

		mTexasView.notifyRenderEnd(strategy);
	}

	@CallSuper
	public void release() {
		onRelease();
		if (mTypesetEngine == null) {
			w("release, core is null");
			return;
		}

		mTypesetEngine.release();
		mTypesetEngine = null;
	}

	public RenderOption createRendererOption() {
		return new RenderOption(mRenderOption);
	}

	public void refresh(RenderOption renderOption) {
		if (mTypesetEngine == null) {
			// fix https://bugly.qq.com/v2/crash-reporting/crashes/900021510/404021/report?pid=1&search=texas&searchType=detail&bundleId=&channelId=&version=all&tagList=&start=0&date=all
			w("ignore refresh, typeset engine is null");
			return;
		}

		int cmpType = TexasUtils.cmp(mRenderOption, renderOption);
		d("refresh, cmp type: " + TexasUtils.cmpType2String(cmpType));

		RenderOption prev = mRenderOption;
		mRenderOption = renderOption;
		mAdapter.updateRenderOption(mRenderOption);
		mSelectionManager.updateRenderOption(mRenderOption);
		mTypesetEngine.updateRenderOption(mRenderOption);

		if (cmpType == TexasUtils.CmpType.CMP_LOAD) {
			d("render option changed, load");
			load("render option changed", mTypesetEngine.getWidth(), LoadingStrategy.INIT);
			return;
		} else if (cmpType == TexasUtils.CmpType.CMP_TYPESET) {
			d("render option changed, typeset");
			mTypesetEngine.resize("Renderer.refresh", LoadingStrategy.TYPESET_ONLY, mListener);
			return;
		}

		d("render option changed, redraw");
		if (cmpType != TexasUtils.CmpType.CMP_DRAW) {
			throw new IllegalStateException("unknown cmp type: " + cmpType);
		}

		// compat mode 改变了 只需要重新加载
		if (prev.isCompatMode() != mRenderOption.isCompatMode()) {
			reload();
			return;
		}

		redrawInternal();
	}

	@Nullable
	public Document getDocument() {
		if (mTypesetEngine == null) {
			w("get document, core is null");
			return null;
		}
		return mTypesetEngine.getDocument();
	}

	public Selection getSelection() {
		return mSelectionManager.getCurrentSelection();
	}

	public int getFirstVisibleSegmentIndex(boolean completelyVisible) {
		return completelyVisible ?
				mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() :
				mLinearLayoutManager.findFirstVisibleItemPosition();
	}

	public void scrollToPosition(int position, boolean smooth, int offset) {
		mImpl.scrollToPosition(position, smooth, offset);
	}

	public void highlightParagraphs(TexasView.HighlightPredicate predicate, boolean scrollTo, int offset) {
		Highlight area = mHighlightManager.highlightParagraphs(predicate);
		if (area == null || area.isEmpty()) {
			return;
		}

		if (!scrollTo) {
			return;
		}

		int firstVisibleItemIndex = mLinearLayoutManager.findFirstVisibleItemPosition();
		int lastVisibleItemIndex = mLinearLayoutManager.findLastVisibleItemPosition();
		int currentItemIndex = area.getFirstIndexInDocument();
		if (currentItemIndex <= firstVisibleItemIndex || currentItemIndex >= lastVisibleItemIndex) {
			ParagraphHighlight paragraphHighlight = area.get(0);
			mLinearLayoutManager.scrollToPositionWithOffset(currentItemIndex, (int) (offset - paragraphHighlight.getYInParagraph()));
		}
	}

	public void clearHighlight() {
		mHighlightManager.clear();
	}

	public void clearSelection() {
		mSelectionManager.clear();
	}

	public int getScrollState() {
		int state = mImpl.getScrollState();
		return rvState2InternalState(state);
	}

	private static int rvState2InternalState(int state) {
		if (state == RecyclerView.SCROLL_STATE_IDLE) {
			return TexasView.SCROLL_STATE_IDLE;
		} else if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
			return TexasView.SCROLL_STATE_DRAGGING;
		} else if (state == RecyclerView.SCROLL_STATE_SETTLING) {
			return TexasView.SCROLL_STATE_SETTLING;
		} else {
			throw new IllegalStateException("unknown rv state: " + state);
		}
	}

	public void setScrollBarEnable(boolean enable) {
		mImpl.setVerticalScrollBarEnabled(enable);
	}

	public void onRelease() {
		mImpl.removeOnScrollListener(mOnScrollListener);
		mImpl.stopScroll();
		mAdapter.release();
	}

	public void setScrollBarDrawable(Drawable drawable) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			mImpl.setVerticalScrollbarThumbDrawable(drawable);
		} else {
			setScrollBarDrawableLowQ(drawable);
		}
	}

	public Drawable getScrollBarDrawable() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			return mImpl.getVerticalScrollbarThumbDrawable();
		} else {
			return getScrollBarDrawableLowQ();
		}
	}

	@Nullable
	private Object getScrollbar() {
		try {
			Class<View> clazz = View.class;
			@SuppressLint({"DiscouragedPrivateApi", "SoonBlockedPrivateApi"})
			Method method = clazz.getDeclaredMethod("getScrollCache");
			method.setAccessible(true);
			Object cache = method.invoke(mImpl);
			if (cache == null) {
				Log.w("SlidingTexasRenderer", "get cache failed");
				return null;
			}

			Class<?> cacheClazz = cache.getClass();
			Field scrollBarField = cacheClazz.getDeclaredField("scrollBar");
			scrollBarField.setAccessible(true);
			return scrollBarField.get(cache);
		} catch (Throwable e) {
			Log.w("SlidingTexasRenderer", e);
		}
		return null;
	}

	private Drawable getScrollBarDrawableLowQ() {
		try {
			Object scrollbarObj = getScrollbar();
			if (scrollbarObj == null) {
				Log.w("SlidingTexasRenderer", "get scroll bar failed");
				return null;
			}

			Class<?> scrollbarClass = scrollbarObj.getClass();
			Field field = scrollbarClass.getDeclaredField("mVerticalThumb");
			field.setAccessible(true);
			return (Drawable) field.get(scrollbarObj);
		} catch (Throwable throwable) {
			Log.w("SlidingTexasRenderer", throwable);
		}
		return null;
	}

	@SuppressLint("DiscouragedPrivateApi")
	private void setScrollBarDrawableLowQ(Drawable drawable) {
		try {
			Object scrollbarObj = getScrollbar();
			if (scrollbarObj == null) {
				Log.w("SlidingTexasRenderer", "get scroll bar failed");
				return;
			}

			Class<?> scrollbarClass = scrollbarObj.getClass();
			Method method = scrollbarClass.getDeclaredMethod("setVerticalThumbDrawable", Drawable.class);
			method.setAccessible(true);
			method.invoke(scrollbarObj, drawable);
		} catch (Throwable e) {
			Log.w("SlidingTexasRenderer", e);
		}
	}

	public void setSpanTouchEventHandler(SpanTouchEventHandler listener) {
		mSelectionManager.setSpanTouchEventHandler(listener);
	}

	@SuppressLint("NotifyDataSetChanged")
	public void redraw() {
		redraw(0, mAdapter.getItemCount());
	}

	private void redraw(int start, int end) {
		if (start >= end || start < 0 || end > mAdapter.getItemCount()) {
			return;
		}

		try {
			mAdapter.notifyItemRangeChanged(start, end - start);
		} catch (Throwable ignore) {
			/* update */
		}
	}

	public void setPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
		mImpl.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}

	public void setSegmentDecoration(TexasView.SegmentDecoration segmentDecoration) {
		if (mTypesetEngine == null) {
			return;
		}

		mTypesetEngine.setSegmentDecoration(segmentDecoration);
		mTypesetEngine.resize("Renderer.setSegmentDecoration", LoadingStrategy.TYPESET_ONLY, mListener);
	}

	private static void d(String msg) {
		Log.d("TexasRenderer", msg);
	}

	private static void w(String msg) {
		Log.w("TexasRenderer", msg);
	}

	private static void w(Throwable throwable) {
		Log.w("TexasRenderer", throwable);
	}

	public RenderOption getRendererOption() {
		return mRenderOption;
	}

	public Selection selectParagraphs(TexasView.SelectionPredicate predicate) {
		return mSelectionManager.selectParagraphs(predicate);
	}

	public int getPaddingWidth() {
		return mImpl.getPaddingLeft() + mImpl.getPaddingRight();
	}

	@SuppressLint("NotifyDataSetChanged")
	public void reload() {
		try {
			mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
		} catch (Throwable ignore) {
			/* ignore */
		}
	}

	public void resume() {
		redrawInternal();
	}

	private void redrawInternal() {
		// 修复部分机型 在切换到后台后白屏的bug
		// Activity#makeVisible
		// see https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/app/Activity.java;drc=c7282e57cd01f1576baac04356bf99bee34e4c18;l=4246

		redraw(0, mAdapter.getItemCount());
	}

	public void pause() {
		/* do nothing */
	}

	public void setParagraphDecor(ParagraphDecor decor) {
		mAdapter.setParagraphDecor(decor);
		redrawInternal();
	}

	@Override
	public void onSpanClicked(TouchEvent event, Object tag) {
		mTexasView.notifySpanClicked(event, tag);
	}

	@Override
	public void onSpanLongClicked(TouchEvent event, Object tag) {
		mTexasView.notifySpanLongClicked(event, tag);
	}

	@Override
	public void onDragStart(TouchEvent event) {
		mTexasView.notifyDragStart(event);
	}

	@Override
	public void onDragEnd(TouchEvent event) {
		mTexasView.notifyDragEnd(event);
	}

	@Override
	public void onDragDismiss() {
		mTexasView.notifyDragDismiss();
	}

	@Override
	public void onSegmentDoubleClicked(TouchEvent event, Object paragraphTag) {
		mTexasView.notifySegmentDoubleClicked(event, paragraphTag);
	}

	@Override
	public void onSegmentClicked(TouchEvent event, Object paragraphTag) {
		mTexasView.notifySegmentClicked(event, paragraphTag);
	}

	public void setHasFixedSize(boolean enable) {
		mImpl.setHasFixedSize(enable);
	}

	public int getWidth() {
		return mTypesetEngine == null ? 0 : mTypesetEngine.getWidth();
	}

	public boolean hasContent() {
		return mAdapter != null && mAdapter.getItemCount() > 0;
	}
}
