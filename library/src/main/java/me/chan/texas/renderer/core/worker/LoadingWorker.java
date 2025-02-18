package me.chan.texas.renderer.core.worker;

import android.util.Log;

import androidx.annotation.RestrictTo;

import javax.inject.Inject;

import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.hyphenation.HyphenationPattern;
import me.chan.texas.measurer.MeasureFactory;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.sync.WorkerMessager;
import me.chan.texas.text.Document;
import me.chan.texas.text.HyphenStrategy;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.utils.concurrency.TaskQueue;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class LoadingWorker implements TaskQueue.Listener<LoadingWorker.Args, LoadingWorker.LoadingResult>, TaskQueue.Task<LoadingWorker.Args, LoadingWorker.LoadingResult> {
	private static final int TYPE_SUCCESS = 1;

	private static final int TYPE_ERROR = 2;

	private static final int TYPE_START = 3;

	public static final boolean DEBUG = false;

	private final TaskQueue mTaskQueue;
	private final WorkerMessager mMessager;

	@Inject
	MeasureFactory mMeasureFactory;

	public LoadingWorker(TaskQueue taskQueue, WorkerMessager messager) {
		mTaskQueue = taskQueue;
		mMessager = messager;
		mMessager.addListener((id, value) -> {
			LoadingWorker.Args args = value.asArg(LoadingWorker.Args.class);
			if (args == null) {
				return false;
			}

			if (value.type() == TYPE_START) {
				args.listener.onStart();
			} else if (value.type() == TYPE_SUCCESS) {
				LoadingResult result = value.value();
				args.listener.onSuccess(result.document, result.start, result.end);
			} else if (value.type() == TYPE_ERROR) {
				args.listener.onFailure(value.error());
			} else {
				throw new IllegalStateException("unknown mix's message type");
			}

			return true;
		});

		TexasComponent texasComponent = Texas.getTexasComponent();
		TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
		textEngineCoreComponent.inject(this);
	}

	public void submit(TaskQueue.Token token, LoadingWorker.Args args) {
		mTaskQueue.submit(token, args, this, this);
	}

	@Override
	public void onStart(TaskQueue.Token token, LoadingWorker.Args args) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_START, args, null);
		mMessager.send(token, message);
	}

	@Override
	public void onSuccess(TaskQueue.Token token, LoadingWorker.Args args, LoadingResult ret) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_SUCCESS, args, ret);
		mMessager.send(token, message);
	}

	@Override
	public void onError(TaskQueue.Token token, LoadingWorker.Args args, Throwable throwable) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_ERROR, args, throwable);
		mMessager.send(token, message);
	}

	@Override
	public LoadingResult run(TaskQueue.Token token, LoadingWorker.Args args) throws Throwable {
		PaintSet paintSet = new PaintSet(args.option);
		Measurer measurer = mMeasureFactory.create(paintSet);
		TextAttribute textAttribute = new TextAttribute(measurer);

		return parse(token, textAttribute, measurer, args.option, args.adapter);
	}

	private LoadingResult parse(TaskQueue.Token token, TextAttribute textAttribute, Measurer measurer, RenderOption option, TexasView.Adapter<?> adapter) throws TaskQueue.TokenExpiredException, ParseException {
		// 选择断字策略
		Hyphenation hyphenation = null;
		HyphenStrategy hyphenStrategy = option.getHyphenStrategy();
		if (hyphenStrategy == HyphenStrategy.US) {
			hyphenation = Hyphenation.getInstance(HyphenationPattern.EN_US);
		} else if (hyphenStrategy == HyphenStrategy.UK) {
			hyphenation = Hyphenation.getInstance(HyphenationPattern.EN_GB);
		} else if (hyphenStrategy == HyphenStrategy.NONE) {
			hyphenation = Hyphenation.getInstance(HyphenationPattern.NONE);
		} else {
			throw new IllegalArgumentException("unknown hyphen strategy");
		}

		// 已经发生了中断，那么直接返回
		if (token.isExpired()) {
			throw new TaskQueue.TokenExpiredException("stop parse, token expired", token);
		}

		TexasOption texasOption = new TexasOption(hyphenation, measurer, textAttribute, option);
		return adapter.getDocument(texasOption);
	}

	public static TexasOption createTexasOption(TextAttribute textAttribute, Measurer measurer, RenderOption option) {
		// 选择断字策略
		Hyphenation hyphenation = null;
		HyphenStrategy hyphenStrategy = option.getHyphenStrategy();
		if (hyphenStrategy == HyphenStrategy.US) {
			hyphenation = Hyphenation.getInstance(HyphenationPattern.EN_US);
		} else if (hyphenStrategy == HyphenStrategy.UK) {
			hyphenation = Hyphenation.getInstance(HyphenationPattern.EN_GB);
		} else if (hyphenStrategy == HyphenStrategy.NONE) {
			hyphenation = Hyphenation.getInstance(HyphenationPattern.NONE);
		} else {
			throw new IllegalArgumentException("unknown hyphen strategy");
		}

		return new TexasOption(hyphenation, measurer, textAttribute, option);
	}

	public void cancel(TaskQueue.Token token) {
		mTaskQueue.cancel(token);
		mMessager.clear(token);
	}

	public interface Listener {
		void onStart();

		void onFailure(Throwable throwable);

		void onSuccess(Document document, int start, int end);
	}

	public static class LoadingResult extends DefaultRecyclable {
		private static final ObjectPool<LoadingWorker.LoadingResult> POOL = new ObjectPool<>(32);

		private Document document;
		private int start;
		private int end;

		private LoadingResult() {

		}

		public static LoadingResult obtainWithoutContent(Document document) {
			final int size = document.getSegmentCount();
			return obtain(document, size, size);
		}

		public static LoadingResult obtain(Document document) {
			return obtain(document, 0, document.getSegmentCount());
		}

		public static LoadingResult obtain(Document document, int start, int end) {
			LoadingResult result = POOL.acquire();
			if (result == null) {
				result = new LoadingResult();
			}

			result.document = document;
			result.start = start;
			result.end = end;
			result.reuse();
			return result;
		}

		public Document getDocument() {
			return document;
		}

		@Override
		protected void onRecycle() {
			/* NOOP */
		}
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<LoadingWorker.Args> POOL = new ObjectPool<>(32);
		private RenderOption option;
		private LoadingWorker.Listener listener;
		private TexasView.Adapter<?> adapter;

		private Args() {
		}

		@Override
		protected void onRecycle() {
			option = null;
			listener = null;
			adapter = null;
			POOL.release(this);
		}

		public static LoadingWorker.Args obtain(RenderOption option,
												TexasView.Adapter<?> adapter,
												LoadingWorker.Listener listener) {
			LoadingWorker.Args args = POOL.acquire();
			if (args == null) {
				args = new LoadingWorker.Args();
			}

			args.option = option;
			args.listener = listener;
			args.adapter = adapter;
			args.reuse();
			return args;
		}
	}

	private static void d(String msg) {
		Log.d("LoadingTask", msg);
	}

	private static void i(String msg) {
		Log.i("LoadingTask", msg);
	}

	private static void w(String msg) {
		Log.w("LoadingTask", msg);
	}
}
