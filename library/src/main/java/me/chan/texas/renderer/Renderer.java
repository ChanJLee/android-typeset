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
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.worker.LoadingWorker;
import me.chan.texas.renderer.core.worker.MixWorker;
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

    public Renderer(final TexasView texasView, RenderOption renderOption, TaskQueue.Token token) {
        mToken = token;
        mTexasView = texasView;
        mRenderOption = renderOption;

        // misc modules
        Context context = texasView.getContext();
        ImageLoader imageLoader = new ImageLoader(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // core
        mTypesetEngine = new TypesetEngine(this, mRenderOption, mToken);

        // rv
        mLinearLayoutManager = new TexasLinearLayoutManager(context);
        mImpl = new TexasRecyclerView(new ContextThemeWrapper(context, R.style.me_chan_texas_TexasRecyclerView), mLinearLayoutManager);
        mImpl.setClipToPadding(false);
        mImpl.setClipChildren(false);
        mImpl.setOnClickedListener((x, y) -> mTexasView.notifyEmptyClicked(x, y));
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
            public void onSegmentClicked(float x, float y, Object tag) {
                mTexasView.notifySegmentClicked(x, y, tag);
            }

            @Override
            public void onSegmentDoubleClicked(float x, float y, Object tag) {
                mTexasView.notifySegmentDoubleClicked(x, y, tag);
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

    public void start(LoadingStrategy strategy) {
        onStart();
        mTexasView.notifyRenderStart(strategy);
    }

    protected void onStart() {
        d("on start");
        mHighlightManager.clear();
        mSelectionManager.clear();
        mAdapter.clear();
    }

    public void render(LoadingStrategy strategy, @NonNull Document document, PaintSet paintSet) {
        if (mTypesetEngine == null) {
            w("render, core is null");
            return;
        }

        onRenderer(
                document,
                paintSet,
                mRenderOption
        );
        mTexasView.notifyRenderEnd(strategy);
    }

    private void render(LoadingStrategy loadingStrategy, int start, int end) {
        if (mTypesetEngine == null) {
            w("render, core is null");
            return;
        }

        mAdapter.render(
                loadingStrategy,
                start,
                end
        );
    }

    protected void onRenderer(Document document,
                              PaintSet paintSet,
                              RenderOption renderOption) {
        if (document == null) {
            /* do nothing */
            return;
        }

        mHighlightManager.clear();
        mSelectionManager.clear();
        mAdapter.render(
                document,
                paintSet,
                renderOption
        );
    }

    public void error(LoadingStrategy strategy, Throwable throwable) {
        w(throwable);
        onError(throwable);
        mTexasView.notifyRenderError(strategy, throwable);
    }

    protected void onError(Throwable throwable) {
        Log.w("SlidingTexasRenderer", throwable);
    }

    @CallSuper
    public void release() {
        onRelease();
        if (mTypesetEngine == null) {
            w("release, core is null");
            return;
        }

        WorkerScheduler.loading().cancel(mToken);
        mTypesetEngine.release();
        mTypesetEngine = null;
    }

    public RenderOption createRendererOption() {
        return new RenderOption(mRenderOption);
    }

    public void refresh(RenderOption renderOption) {
        if (mTypesetEngine == null) {
            // fix https://bugly.qq.com/v2/crash-reporting/crashes/900021510/404021/report?pid=1&search=texas&searchType=detail&bundleId=&channelId=&version=all&tagList=&start=0&date=all
            w("refresh, core is null");
            return;
        }

        d("refresh");
        int cmpType = TexasUtils.cmp(mRenderOption, renderOption);

        RenderOption prev = mRenderOption;
        mRenderOption = renderOption;
        mAdapter.updateRenderOption(mRenderOption);
        mSelectionManager.updateRenderOption(mRenderOption);
        mTypesetEngine.updateRenderOption(mRenderOption);

        if (cmpType == TexasUtils.CmpType.CMP_LOAD) {
            load("render option changed", mTypesetEngine.getWidth(), LoadingStrategy.LOAD_REFRESH);
            return;
        } else if (cmpType == TexasUtils.CmpType.CMP_TYPESET) {
            Document document = mTypesetEngine.getDocument();
            mTypesetEngine.typeset(document, LoadingStrategy.TYPESET_ONLY);
            return;
        }

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

    /**
     * @param width 期望的宽度，如果是负值，那么就忽略排版，只会解析
     */
    public void load(String reason, int width, LoadingStrategy strategy) {
        if (BuildConfig.DEBUG) {
            Log.d("TexasRenderer", "load, reason: " + reason);
        }

        if (mTypesetEngine == null) {
            w("render, core is null");
            return;
        }

        // 如果是刷新，那么之前的解析都可以取消掉了
        if (strategy == LoadingStrategy.LOAD_REFRESH) {
            WorkerScheduler.loading().cancel(mToken);
        } else if (strategy == LoadingStrategy.LOAD_RELOAD) {
            WorkerScheduler.loading().cancel(mToken);
            mTypesetEngine.reset();
            mAdapter.clear();
        }
        WorkerScheduler.mix().cancel(mToken);

        mTypesetEngine.setWidth(width);

        LoadingWorker.Args args = LoadingWorker.Args.obtain(mRenderOption, mTexasView.getAdapter(), strategy, new LoadingWorker.Listener() {
            @Override
            public void onStart() {
                d("try loading doc, width: " + width + ", strategy: " + strategy);
                mTexasView.notifyRenderStart(strategy);
            }

            @Override
            public void onFailure(Throwable throwable) {
                mTexasView.notifyRenderError(strategy, throwable);
            }

            @Override
            public void onSuccess(LoadingStrategy strategy, Document document, int start, int end) {
                if (mTypesetEngine == null) {
                    return;
                }

                if (BuildConfig.DEBUG) {
                    Log.d("TexasRenderer", "load success, reason: " + reason +
                            ", width: " + mTypesetEngine.getWidth() +
                            ", start: " + start +
                            ", end: " + end);
                }

                if (strategy == LoadingStrategy.LOAD_REFRESH || strategy == LoadingStrategy.LOAD_RELOAD) {
                    mTypesetEngine.typeset(document, strategy);
                    return;
                }

                mTypesetEngine.typeset(document, strategy, start, end, new MixWorker.Listener() {
                    @Override
                    public void onStart(LoadingStrategy loadingStrategy) {
                        /* noop */
                    }

                    @Override
                    public void onFailure(LoadingStrategy loadingStrategy, Throwable throwable) {
                        mTexasView.notifyRenderError(loadingStrategy, throwable);
                    }

                    @Override
                    public void onSuccess(LoadingStrategy loadingStrategy, TypesetEngine.TypesetResult result) {
                        render(strategy, start, end);
                        mTexasView.notifyRenderEnd(loadingStrategy);
                    }
                });
            }
        });
        WorkerScheduler.loading().submit(mToken, args);
    }

    public void typeset(String reason, int width, LoadingStrategy strategy) {
        if (BuildConfig.DEBUG) {
            Log.d("TexasRenderer", "typeset, reason: " + reason);
        }

        // 重新排版会将之前的解析任务都失效
        WorkerScheduler.mix().cancel(mToken);

        mTypesetEngine.setWidth(width);
        Document document = mTypesetEngine.getDocument();
        if (document == null || width <= 0) {
            return;
        }
        mTypesetEngine.typeset(document, strategy);
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

    public void setOnSpanLongClickedPredicate(OnSpanLongClickedPredicate predicate) {
        mSelectionManager.setOnLongClickedPredicate(predicate);
    }

    public void setOnSpanClickedPredicate(OnSpanClickedPredicate predicate) {
        mSelectionManager.setOnClickedPredicate(predicate);
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
        if (mTypesetEngine != null) {
            mTypesetEngine.setSegmentDecoration(segmentDecoration);
        }
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
    public void onSpanClicked(float x, float y, Object tag) {
        mTexasView.notifySpanClicked(x, y, tag);
    }

    @Override
    public void onSpanLongClicked(float x, float y, Object tag) {
        mTexasView.notifySpanLongClicked(x, y, tag);
    }

    @Override
    public void onDragStart(float x, float y) {
        mTexasView.notifyDragStart(x, y);
    }

    @Override
    public void onDragEnd(float x, float y) {
        mTexasView.notifyDragEnd(x, y);
    }

    @Override
    public void onDragDismiss() {
        mTexasView.notifyDragDismiss();
    }

    @Override
    public void onSegmentDoubleClicked(Object paragraphTag, float x, float y) {
        mTexasView.notifySegmentDoubleClicked(x, y, paragraphTag);
    }

    @Override
    public void onSegmentClicked(Object paragraphTag, float rawX, float rawY) {
        mTexasView.notifySegmentClicked(rawX, rawY, paragraphTag);
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
