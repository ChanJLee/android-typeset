package me.chan.texas.renderer.core.worker;

import androidx.annotation.RestrictTo;

import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.hyphenation.HyphenationPattern;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.text.Document;
import me.chan.texas.text.HyphenStrategy;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.utils.concurrency.Worker;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class LoadingWorker {
	private static final int TYPE_SUCCESS = 1;

	private static final int TYPE_ERROR = 2;

	private static final int TYPE_START = 3;

	public static final boolean DEBUG = false;

	private final Worker mWorker;
	private final MsgHandler mMsgHandler;

	private final Worker.Task<LoadingWorker.Args, LoadingWorker.LoadingResult> mTask = new Worker.Task<LoadingWorker.Args, LoadingWorker.LoadingResult>() {
		@Override
		public void onStart(Worker.Token token, LoadingWorker.Args args) {
			MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_START, args, null);
			mMsgHandler.send(token, message);
		}

		@Override
		public void onSuccess(Worker.Token token, LoadingWorker.Args args, LoadingResult ret) {
			MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_SUCCESS, args, ret);
			mMsgHandler.send(token, message);
		}

		@Override
		public void onError(Worker.Token token, LoadingWorker.Args args, Throwable error) {
			MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_ERROR, args, error);
			mMsgHandler.send(token, message);
		}

		@Override
		protected LoadingResult onExec(Worker.Token token, Args args) throws Throwable {
			if (token.isExpired()) {
				throw new Worker.TokenExpiredException("stop parse, token expired", token);
			}

			LoadingResult result = args.source.read();
			if (result == null) {
				throw new IllegalStateException("read failed");
			}

			return result;
		}
	};

	public LoadingWorker(Worker worker, MsgHandler msgHandler) {
		mWorker = worker;
		mMsgHandler = msgHandler;
		mMsgHandler.addListener((id, value) -> {
			LoadingWorker.Args args = value.asArg(LoadingWorker.Args.class);
			if (args == null) {
				return false;
			}

			if (value.type() == TYPE_START) {
				args.listener.onStart();
			} else if (value.type() == TYPE_SUCCESS) {
				LoadingResult result = value.value();
				args.listener.onSuccess(result.option, result.prev, result.document);
			} else if (value.type() == TYPE_ERROR) {
				args.listener.onFailure(value.error());
			} else {
				throw new IllegalStateException("unknown mix's message type");
			}

			return true;
		});
	}

	public void submit(Worker.Token token, LoadingWorker.Args args) {
		mWorker.async(token, args, mTask);
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

	public void cancel(Worker.Token token) {
		mWorker.cancel(token);
		mMsgHandler.clear(token);
	}

	public interface Listener {
		void onStart();

		void onFailure(Throwable throwable);

		void onSuccess(TexasOption option, Document prev, Document document);
	}

	public static class LoadingResult {
		public final Document prev;
		public final Document document;
		public final TexasOption option;

		public LoadingResult(TexasOption option, Document prev, Document document) {
			this.option = option;
			this.prev = prev;
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
