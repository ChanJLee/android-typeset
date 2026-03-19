package me.chan.texas.renderer;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import static me.chan.texas.utils.TexasUtils.CmpType.CMP_IGNORE;

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
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.TypesetEngine;
import me.chan.texas.renderer.core.worker.MixWorker;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.renderer.selection.SelectionMethodImpl;
import me.chan.texas.renderer.selection.overlay.DragSelectViewImpl;
import me.chan.texas.renderer.ui.RendererAdapterImpl;
import me.chan.texas.renderer.ui.rv.SegmentItemDecoration;
import me.chan.texas.renderer.ui.rv.TexasLinearLayoutManagerImpl;
import me.chan.texas.renderer.ui.rv.TexasRecyclerViewImpl;
import me.chan.texas.text.Document;
import me.chan.texas.renderer.selection.SelectionMethod;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.layout.Span;
import me.chan.texas.utils.TexasUtils;
import me.chan.texas.utils.concurrency.Worker;

/**
 * 协调各个组件一起工作
 */
@RestrictTo(LIBRARY)
public class Renderer implements SelectionMethodImpl.Listener {

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
	private final RendererAdapterImpl mAdapter;
	/**
	 * 视图显示窗口
	 */
	private final TexasRecyclerViewImpl mRecyclerView;
	/**
	 * 视图排版子系统
	 */
	private final TexasLinearLayoutManagerImpl mLinearLayoutManager;
	/**
	 * 内容选择子系统
	 */
	private final SelectionMethodImpl mSelectionMethod;

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

	private final TypesetEngine.Listener mListener = new TypesetEngine.Listener() {
		@Override
		public void onStart() {
			mTexasView.notifyRenderStart();
		}

		@Override
		public void onFailure(Throwable throwable) {
			mTexasView.notifyRenderError(throwable);
		}

		@Override
		public void onSuccess(MixWorker.TypesetResult result) {
			render(result);
		}
	};

	public Renderer(final TexasView texasView, RenderOption renderOption, Worker.Token token) {
		mTexasView = texasView;
		mRenderOption = renderOption;

		// misc modules
		Context context = texasView.getContext();
		LayoutInflater layoutInflater = LayoutInflater.from(context);

		// core
		mTypesetEngine = new TypesetEngine(token);

		// rv
		mLinearLayoutManager = new TexasLinearLayoutManagerImpl(context);
		mRecyclerView = new TexasRecyclerViewImpl(new ContextThemeWrapper(context, R.style.me_chan_texas_TexasRecyclerView), mLinearLayoutManager);
		mRecyclerView.setClipToPadding(false);
		mRecyclerView.setClipChildren(false);
		mRecyclerView.setOnClickedListener(mTexasView::notifyEmptyClicked);
		mRecyclerView.setLayoutManager(mLinearLayoutManager);
		texasView.addView(mRecyclerView,
				new TexasView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT)
		);
		mRecyclerView.addOnScrollListener(mOnScrollListener);

		mAdapter = new RendererAdapterImpl(token, layoutInflater, mRecyclerView.getRecycledViewPool(), mRecyclerView);
		mAdapter.setListener(new RendererAdapterImpl.Listener() {
			@Override
			public void onSegmentClicked(TouchEvent event, Segment segment) {
				mTexasView.notifySegmentClicked(event, segment);
			}

			@Override
			public void onSegmentDoubleClicked(TouchEvent event, Segment segment) {
				mTexasView.notifySegmentDoubleClicked(event, segment);
			}
		});
		mRecyclerView.addItemDecoration(new SegmentItemDecoration(mAdapter));

		// selection
		DragSelectViewImpl selectionDragView = new DragSelectViewImpl(texasView.getContext(), texasView);
		selectionDragView.setEnable(renderOption.isDragToSelectEnable());
		texasView.addView(selectionDragView,
				new TexasView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT)
		);
		mSelectionMethod = new SelectionMethodImpl(mAdapter, mLinearLayoutManager, this, selectionDragView, mRecyclerView);
		mAdapter.setSelectionMethod(mSelectionMethod);

		// adapter
		mRecyclerView.setAdapter(mAdapter);
	}

	/**
	 * @param width 期望的宽度，如果是负值，那么就忽略排版，只会解析
	 */
	public void load(String reason, int width) {
		d("load, reason: " + reason);

		if (mTypesetEngine == null) {
			w("render, core is null");
			return;
		}

		mTypesetEngine.load(reason, width, mTexasView.getSource(), mListener);
	}

	public void resize(String reason, int width) {
		if (BuildConfig.DEBUG) {
			Log.d("TexasRenderer", "typeset, reason: " + reason);
		}

		// 重新排版会将之前的解析任务都失效
		mTypesetEngine.resize(reason, mTexasView.createTexasOption(), width, mListener);
	}

	private void render(MixWorker.TypesetResult result) {
		mAdapter.render(result);
		mSelectionMethod.renderDropView();
		mTexasView.notifyRenderEnd();
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
		if (cmpType == CMP_IGNORE) {
			return;
		}

		RenderOption prev = mRenderOption;
		mRenderOption = renderOption;
		mAdapter.updateRenderOption(mRenderOption);
		mSelectionMethod.updateRenderOption(mRenderOption);

		if (cmpType == TexasUtils.CmpType.CMP_LOAD) {
			d("render option changed, load");
			load("render option changed", mTypesetEngine.getWidth());
			return;
		} else if (cmpType == TexasUtils.CmpType.CMP_TYPESET) {
			d("render option changed, typeset");
			mTypesetEngine.resize("Renderer.refresh", mTexasView.createTexasOption(), mListener);
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

	public Selection getSelection(Selection.Type type) {
		return mSelectionMethod.getSelection(type);
	}

	public int getFirstVisibleSegmentIndex(boolean completelyVisible) {
		return completelyVisible ?
				mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() :
				mLinearLayoutManager.findFirstVisibleItemPosition();
	}

	public void scrollToPosition(int position, boolean smooth, int offset) {
		mRecyclerView.scrollToPosition(position, smooth, offset);
	}

	public Selection highlightParagraphs(ParagraphPredicates predicates, boolean scrollTo, int offset, Selection.Styles styles) {
		Selection highlight = mSelectionMethod.highlightParagraphs(
				predicates,
				styles == null ? Selection.Styles.createFromHighLight(mRenderOption).setEnableDrag(false) :
						styles.setEnableDrag(false)
		);
		if (highlight == null || highlight.isEmpty()) {
			return highlight;
		}

		if (scrollTo) {
			ParagraphSelection selection = highlight.get(0);
			int index = mAdapter.indexOf(selection.getParagraph());
			RectF region = selection.getFirstRegion();
			if (region != null) {
				offset = (int) (-region.top + offset);
			}
			mRecyclerView.scrollToPosition(index, true, offset);
		}

		return highlight;
	}

	public void clearHighlight() {
		mSelectionMethod.clearHighlight();
	}

	public void clearSelection() {
		mSelectionMethod.clear();
	}

	public int getScrollState() {
		int state = mRecyclerView.getScrollState();
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
		mRecyclerView.setVerticalScrollBarEnabled(enable);
	}

	public void onRelease() {
		mRecyclerView.removeOnScrollListener(mOnScrollListener);
		mRecyclerView.stopScroll();
		mAdapter.release();
	}

	public void setScrollBarDrawable(Drawable drawable) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			mRecyclerView.setVerticalScrollbarThumbDrawable(drawable);
		} else {
			setScrollBarDrawableLowQ(drawable);
		}
	}

	public Drawable getScrollBarDrawable() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			return mRecyclerView.getVerticalScrollbarThumbDrawable();
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
			Object cache = method.invoke(mRecyclerView);
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
		mSelectionMethod.setSpanTouchEventHandler(listener);
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
			mAdapter.redraw(start, end);
		} catch (Throwable ignore) {
			/* update */
		}
	}

	public void setPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
		mRecyclerView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}

	public void setSegmentDecoration(TexasView.SegmentDecoration segmentDecoration) {
		if (mTypesetEngine == null) {
			return;
		}

		mTypesetEngine.setSegmentDecoration(segmentDecoration);
		mTypesetEngine.resize("Renderer.setSegmentDecoration", mTexasView.createTexasOption(), mListener);
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

	public Selection selectParagraphs(ParagraphPredicates predicates, @NonNull Selection.Styles styles) {
		return mSelectionMethod.selectParagraphs(predicates, styles);
	}

	public int getPaddingWidth() {
		return mRecyclerView.getPaddingLeft() + mRecyclerView.getPaddingRight();
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

	@Override
	public void onSpanClicked(Paragraph paragraph, TouchEvent event, Span span) {
		mTexasView.notifySpanClicked(event, paragraph, span);
	}

	@Override
	public void onSpanLongClicked(Paragraph paragraph, TouchEvent event, Span span) {
		mTexasView.notifySpanLongClicked(event, paragraph, span);
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
	public void onSegmentDoubleClicked(TouchEvent event, Segment segment) {
		mTexasView.notifySegmentDoubleClicked(event, segment);
	}

	@Override
	public void onSegmentClicked(TouchEvent event, Segment segment) {
		mTexasView.notifySegmentClicked(event, segment);
	}

	public void setHasFixedSize(boolean enable) {
		mRecyclerView.setHasFixedSize(enable);
	}

	public int getWidth() {
		return mTypesetEngine == null ? 0 : mTypesetEngine.getWidth();
	}

	public boolean hasContent() {
		return mAdapter != null && mAdapter.getItemCount() > 0;
	}

	public int getLastVisibleSegmentIndex(boolean completelyVisible) {
		return completelyVisible ?
				mLinearLayoutManager.findLastCompletelyVisibleItemPosition() :
				mLinearLayoutManager.findLastVisibleItemPosition();
	}

	public void smoothScrollBy(int dx, int dy) {
		mRecyclerView.smoothScrollBy(dx, dy);
	}

	public SelectionMethod getSelectionMethod() {
		return mSelectionMethod;
	}

	public void setSegmentAnimator(TexasView.SegmentAnimator segmentAnimator) {
		mRecyclerView.setSegmentAnimator(segmentAnimator);
	}
}
