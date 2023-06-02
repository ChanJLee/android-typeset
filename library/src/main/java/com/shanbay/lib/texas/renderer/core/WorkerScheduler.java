package com.shanbay.lib.texas.renderer.core;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.Texas;
import com.shanbay.lib.texas.di.TexasComponent;
import com.shanbay.lib.texas.di.core.TextEngineCoreComponent;
import com.shanbay.lib.texas.renderer.core.sync.WorkerMessager;
import com.shanbay.lib.texas.renderer.core.worker.OddWorker;
import com.shanbay.lib.texas.renderer.core.worker.ParseWorker;
import com.shanbay.lib.texas.renderer.core.worker.RenderWorker;
import com.shanbay.lib.texas.renderer.core.worker.TypesetWorker;
import com.shanbay.lib.texas.utils.concurrency.TaskQueue;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * 工作调度
 */
@RestrictTo(LIBRARY)
public class WorkerScheduler {
	private static volatile WorkerScheduler sInstance;

	// handler需要设置线程可见性，这样一旦释放了handler，工作线程能立马看到
	// 滞后的消息就不会发到主线程
	@Inject
	@Named("MiscTask")
	TaskQueue mMiscTaskQueue;

	@Inject
	@Named("RendererTask")
	TaskQueue mRendererTaskQueue;

	@Inject
	WorkerMessager mMessager;

	private final RenderWorker mRenderWorker;
	private final TypesetWorker mTypesetWorker;
	private final ParseWorker mParseWorker;
	private final OddWorker mOddWorker;

	private WorkerScheduler() {
		TexasComponent texasComponent = Texas.getTexasComponent();
		TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
		textEngineCoreComponent.inject(this);

		mRenderWorker = new RenderWorker(mRendererTaskQueue, mMessager);
		mTypesetWorker = new TypesetWorker(mMiscTaskQueue, mMessager);
		mParseWorker = new ParseWorker(mMiscTaskQueue, mMessager);
		mOddWorker = new OddWorker();
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

	public static TypesetWorker typeset() {
		return getInstance().mTypesetWorker;
	}

	public static OddWorker odd() {
		return getInstance().mOddWorker;
	}


	public static final int TASK_QUEUE_RENDER = 1;
	public static final int TASK_QUEUE_TYPESET = 2;
	public static final int TASK_QUEUE_PARSE = 3;

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
		}

		throw new IllegalArgumentException("unknown task queue type");
	}

	public static void cancelAll(int taskId) {
		WorkerScheduler scheduler = getInstance();
		scheduler.mMiscTaskQueue.cancel(taskId);
		scheduler.mRendererTaskQueue.cancel(taskId);
		scheduler.mMessager.clear(taskId);
	}
}
