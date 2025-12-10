package me.chan.texas.renderer.core;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import javax.inject.Inject;
import javax.inject.Named;

import me.chan.texas.Texas;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.renderer.core.worker.LoadingWorker;
import me.chan.texas.renderer.core.worker.MixWorker;
import me.chan.texas.renderer.core.worker.OddWorker;
import me.chan.texas.renderer.core.worker.ParagraphTypesetWorker;
import me.chan.texas.renderer.core.worker.ParseWorker;
import me.chan.texas.renderer.core.worker.RenderWorker;
import me.chan.texas.utils.concurrency.Worker;

/**
 * 工作调度
 */
@RestrictTo(LIBRARY)
public class WorkerScheduler {
	private static volatile WorkerScheduler sInstance;

	@Inject
	@Named("RendererWorker")
	Worker mRendererWorker;

	@Inject
	@Named("BackgroundWorker")
	Worker mBackgroundWorker;

	@Inject
	MsgHandler mMsgHandler;

	private final RenderWorker mRenderWorker;
	private final ParagraphTypesetWorker mTypesetWorker;
	private final ParseWorker mParseWorker;
	private final OddWorker mOddWorker;
	private final MixWorker mMixWorker;

	private final LoadingWorker mLoadingWorker;

	private WorkerScheduler() {
		TexasComponent texasComponent = Texas.getTexasComponent();
		TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
		textEngineCoreComponent.inject(this);

		mRenderWorker = new RenderWorker(mRendererWorker, mMsgHandler);
		mTypesetWorker = new ParagraphTypesetWorker();
		mParseWorker = new ParseWorker(mBackgroundWorker, mMsgHandler);
		mOddWorker = new OddWorker();
		mMixWorker = new MixWorker(mBackgroundWorker, mMsgHandler);
		mLoadingWorker = new LoadingWorker(mBackgroundWorker, mMsgHandler);
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
	public static MixWorker mix() {
		return getInstance().mMixWorker;
	}

	public static LoadingWorker loading() {
		return getInstance().mLoadingWorker;
	}

	public static Worker getBackgroundWorker() {
		return getInstance().mBackgroundWorker;
	}

	public static Worker getRendererWorker() {
		return getInstance().mRendererWorker;
	}

	public static MsgHandler getMsgHandler() {
		return getInstance().mMsgHandler;
	}

	public static final int TASK_QUEUE_RENDER = 1;
	public static final int TASK_QUEUE_TYPESET = 2;
	public static final int TASK_QUEUE_PARSE = 3;
	public static final int TASK_QUEUE_COMPUTE = 4;

	@IntDef({TASK_QUEUE_RENDER, TASK_QUEUE_TYPESET, TASK_QUEUE_PARSE})
	public @interface TaskQueueType {

	}

	public static Worker getTaskQueue(@TaskQueueType int type) {
		if (type == TASK_QUEUE_RENDER) {
			return getInstance().mRendererWorker;
		} else if (type == TASK_QUEUE_TYPESET) {
			return getInstance().mBackgroundWorker;
		} else if (type == TASK_QUEUE_PARSE) {
			return getInstance().mBackgroundWorker;
		} else if (type == TASK_QUEUE_COMPUTE) {
			return getInstance().mBackgroundWorker;
		}

		throw new IllegalArgumentException("unknown task queue type");
	}

	public static void cancelAll(Worker.Token token) {
		WorkerScheduler scheduler = getInstance();
		scheduler.mBackgroundWorker.cancel(token);
		scheduler.mRendererWorker.cancel(token);
		scheduler.mBackgroundWorker.cancel(token);
		scheduler.mMsgHandler.clear(token);
	}
}
