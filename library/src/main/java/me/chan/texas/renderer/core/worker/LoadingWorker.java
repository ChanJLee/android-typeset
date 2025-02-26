package me.chan.texas.renderer.core.worker;

import androidx.annotation.RestrictTo;

import javax.inject.Inject;

import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.hyphenation.HyphenationPattern;
import me.chan.texas.measurer.MeasureFactory;
import me.chan.texas.measurer.Measurer;
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
				args.listener.onSuccess(result.option, result.document);
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
		if (token.isExpired()) {
			throw new TaskQueue.TokenExpiredException("stop parse, token expired", token);
		}

		TexasOption option = args.source.getLoader().load();
		Document document = args.source.read(option);
		return new LoadingResult(option, document);
	}

	public static TexasOption createTexasOption(PaintSet paintSet, TextAttribute textAttribute, Measurer measurer, RenderOption option) {
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

		return new TexasOption(paintSet, hyphenation, measurer, textAttribute, option);
	}

	public void cancel(TaskQueue.Token token) {
		mTaskQueue.cancel(token);
		mMessager.clear(token);
	}

	public interface Listener {
		void onStart();

		void onFailure(Throwable throwable);

		void onSuccess(TexasOption option, Document document);
	}

	public static class LoadingResult {
		public final Document document;
		public final TexasOption option;

		public LoadingResult(TexasOption option, Document document) {
			this.option = option;
			this.document = document;
		}
	}

	public static class Args {
		private final TexasView.DocumentSource source;
		private final LoadingWorker.Listener listener;

		public Args(TexasView.DocumentSource source, Listener listener) {
			this.source = source;
			this.listener = listener;
		}
	}
}
