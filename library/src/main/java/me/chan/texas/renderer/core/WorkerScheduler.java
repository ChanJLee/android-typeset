package me.chan.texas.renderer.core;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.Texas;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.renderer.core.sync.WorkerMessager;
import me.chan.texas.renderer.core.worker.MixTask;
import me.chan.texas.renderer.core.worker.OddWorker;
import me.chan.texas.renderer.core.worker.ParseWorker;
import me.chan.texas.renderer.core.worker.RenderWorker;
import me.chan.texas.renderer.core.worker.ParagraphTypesetWorker;
import me.chan.texas.utils.concurrency.TaskQueue;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * 工作调度
 */
@RestrictTo(LIBRARY)
public class WorkerScheduler {
    private static volatile WorkerScheduler sInstance;

    private static final AtomicInteger UUID = new AtomicInteger();

    // handler需要设置线程可见性，这样一旦释放了handler，工作线程能立马看到
    // 滞后的消息就不会发到主线程
    @Inject
    @Named("MiscTask")
    TaskQueue mMiscTaskQueue;

    @Inject
    @Named("RendererTask")
    TaskQueue mRendererTaskQueue;

    @Inject
    @Named("ComputeTask")
    TaskQueue mMixTaskQueue;

    @Inject
    WorkerMessager mMessager;

    private final RenderWorker mRenderWorker;
    private final ParagraphTypesetWorker mTypesetWorker;
    private final ParseWorker mParseWorker;
    private final OddWorker mOddWorker;
    private final MixTask mMixTask;

    private WorkerScheduler() {
        TexasComponent texasComponent = Texas.getTexasComponent();
        TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
        textEngineCoreComponent.inject(this);

        mRenderWorker = new RenderWorker(mRendererTaskQueue, mMessager);
        mTypesetWorker = new ParagraphTypesetWorker(mMiscTaskQueue, mMessager);
        mParseWorker = new ParseWorker(mMiscTaskQueue, mMessager);
        mOddWorker = new OddWorker();
        mMixTask = new MixTask(mMixTaskQueue, mMessager);
    }

    private static synchronized WorkerScheduler getInstance() {
        if (sInstance == null) {
            sInstance = new WorkerScheduler();
        }
        return sInstance;
    }

    public static ParseWorker parse() {
        return getInstance().mParseWorker;
    }

    public static RenderWorker render() {
        return getInstance().mRenderWorker;
    }

    public static ParagraphTypesetWorker typeset() {
        return getInstance().mTypesetWorker;
    }

    public static OddWorker odd() {
        return getInstance().mOddWorker;
    }

    /*
    * 合并排版结果
    * */
    public static MixTask mix() {
        return getInstance().mMixTask;
    }

    public static final int TASK_QUEUE_RENDER = 1;
    public static final int TASK_QUEUE_TYPESET = 2;
    public static final int TASK_QUEUE_PARSE = 3;
    public static final int TASK_QUEUE_COMPUTE = 4;

    @IntDef({TASK_QUEUE_RENDER, TASK_QUEUE_TYPESET, TASK_QUEUE_PARSE})
    public @interface TaskQueueType {

    }

    public static TaskQueue getTaskQueue(@TaskQueueType int type) {
        if (type == TASK_QUEUE_RENDER) {
            return getInstance().mRendererTaskQueue;
        } else if (type == TASK_QUEUE_TYPESET) {
            return getInstance().mMiscTaskQueue;
        } else if (type == TASK_QUEUE_PARSE) {
            return getInstance().mMiscTaskQueue;
        } else if (type == TASK_QUEUE_COMPUTE) {
            return getInstance().mMixTaskQueue;
        }

        throw new IllegalArgumentException("unknown task queue type");
    }

    public static void cancelAll(TaskQueue.Token token) {
        WorkerScheduler scheduler = getInstance();
        scheduler.mMiscTaskQueue.cancel(token);
        scheduler.mRendererTaskQueue.cancel(token);
        scheduler.mMixTaskQueue.cancel(token);
        scheduler.mMessager.clear(token);
    }

    public static int requestId() {
        return UUID.incrementAndGet();
    }
}
