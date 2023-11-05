package me.chan.texas.renderer.core;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.util.Log;

import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import javax.inject.Inject;
import javax.inject.Named;

import me.chan.texas.Texas;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.Renderer;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.worker.MixWorker;
import me.chan.texas.text.Document;
import me.chan.texas.utils.concurrency.TaskQueue;

/**
 * 排版核心
 */
@RestrictTo(LIBRARY)
public class TypesetEngine {
    public static final boolean DEBUG = false;
    private final TaskQueue.Token mToken;
    private int mWidth = 0;
    private Document mDocument = null;

    private Renderer mRenderer;
    private RenderOption mRenderOption;
    private TexasView.SegmentDecoration mSegmentDecoration;

    @Inject
    @Named("ComputeTask")
    TaskQueue mComputeQueue;

    public TypesetEngine(Renderer renderer,
                         RenderOption renderOption,
                         TaskQueue.Token token) {
        mRenderer = renderer;
        mRenderOption = renderOption;
        mToken = token;

        TexasComponent texasComponent = Texas.getTexasComponent();
        TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
        textEngineCoreComponent.inject(this);

        if (DEBUG) {
            d("typeset engine created: " + mToken);
        }
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void typeset(Document document) {
        mDocument = document;
        typeset0(mWidth, document);
    }

    /**
     * typeset content
     *
     * @param outWidth width, must be > 0
     * @param document document
     */
    private void typeset0(final int outWidth, Document document) {
        if (outWidth <= 0) {
            w("typeset, width <= 0");
            mRenderer.error(new IllegalArgumentException("width and height must be large than 0"));
            return;
        }

        if (document == null) {
            return;
        }

        cancel();
        MixWorker.Args args = MixWorker.Args.obtain(
                outWidth,
                mRenderOption,
                document,
                mListener,
                mSegmentDecoration
        );
        WorkerScheduler.mix().submit(mToken, args);
    }

    private final MixWorker.Listener mListener = new MixWorker.Listener() {
        @Override
        public void onStart() {
            if (mRenderer != null) {
                mRenderer.start();
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            if (throwable instanceof TaskQueue.TokenExpiredException) {
                if (DEBUG) {
                    w(throwable);
                }
                return;
            }

            if (mRenderer != null) {
                mRenderer.error(throwable);
            }
        }

        @Override
        public void onSuccess(TypesetResult result) {
            if (mRenderer != null) {
                mRenderer.render(result.doc, result.paintSet);
            }
        }
    };

    public int getWidth() {
        return mWidth;
    }

    public static class TypesetResult {
        PaintSet paintSet;
        Document doc;

        public TypesetResult(PaintSet paintSet, Document doc) {
            this.paintSet = paintSet;
            this.doc = doc;
        }
    }

    public void release() {
        i("release");
        cancel();

        mToken.destroy();

        // 断开渲染
        mRenderer = null;
    }

    private void cancel() {
        // 取消准备发送的消息
        WorkerScheduler.mix().cancel(mToken);
    }

    public Document getDocument() {
        return mDocument;
    }

    public void updateRenderOption(RenderOption renderOption) {
        mRenderOption = renderOption;
    }

    public void setSegmentDecoration(TexasView.SegmentDecoration segmentDecoration) {
        mSegmentDecoration = segmentDecoration;
        if (mDocument == null) {
            i("document is null, setSegmentDecoration ignore");
            return;
        }

        // fail-fast
        if (mWidth <= 0) {
            i("width < 0, setSegmentDecoration ignore");
            return;
        }
        typeset(mDocument);
    }

    @VisibleForTesting
    public Object getTypesetterInternalState() {
        return WorkerScheduler.typeset().getTypesetterInternalState();
    }

    private static void d(String msg) {
        Log.d("TypesetEngine", msg);
    }

    private static void i(String msg) {
        Log.i("TypesetEngine", msg);
    }

    private static void w(String msg) {
        Log.w("TypesetEngine", msg);
    }

    private static void w(Throwable throwable) {
        Log.w("TypesetEngine", throwable);
    }

    private static void e(String msg, Throwable throwable) {
        Log.e("TypesetEngine", msg, throwable);
    }
}
