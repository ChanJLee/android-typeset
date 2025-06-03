package me.chan.texas.renderer.core.worker;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.source.Source;
import me.chan.texas.text.Paragraph;
import me.chan.texas.utils.concurrency.Worker;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ParseWorker {
	private static final int TYPE_SUCCESS = 1;
	private static final int TYPE_ERROR = 2;

	private final Worker mWorker;
	private final MsgHandler mMsgHandler;
	private final Worker.Task<ParseWorker.Args, Paragraph> mTask = new Worker.Task<Args, Paragraph>() {
		@Override
		public void onSuccess(Worker.Token token, Args args, Paragraph ret) {
			MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_SUCCESS, args, ret);
			mMsgHandler.send(token, message);
		}

		@Override
		public void onError(Worker.Token token, Args args, Throwable error) {
			Log.w("ParseWorker", error);
			MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_ERROR, args, error);
			mMsgHandler.send(token, message);
		}

		@Override
		protected Paragraph onExec(Worker.Token token, Args args) throws Throwable {
			return args.source.read();
		}
	};

	public ParseWorker(Worker worker, MsgHandler msgHandler) {
		mWorker = worker;
		mMsgHandler = msgHandler;
		mMsgHandler.addListener((token, value) -> {
			Args args = value.asArg(Args.class);
			if (args == null) {
				return false;
			}
			switch (value.type()) {
				case TYPE_SUCCESS:
					if (args.listener != null) {
						args.listener.onParseSuccess(value.value());
					}
					break;
				case TYPE_ERROR:
					if (args.listener != null) {
						args.listener.onParseFailure(value.error());
					}
					break;
			}
			args.recycle();
			return true;
		});
	}

	public void submit(Worker.Token token, Args args) {
		mWorker.async(token, args, mTask);
	}

	public Paragraph submitSync(Worker.Token token, Args args) throws Throwable {
		return mWorker.sync(token, args, mTask);
	}

	public interface Listener {
		void onParseSuccess(Paragraph paragraph);

		void onParseFailure(Throwable throwable);
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<Args> POOL = new ObjectPool<>(32);
		private Source<Paragraph> source;
		private Listener listener;

		private Args() {
		}

		@Override
		protected void onRecycle() {
			listener = null;
			source = null;
			POOL.release(this);
		}

		public static Args obtain(@NonNull Source<Paragraph> source,
								  @NonNull Listener listener) {
			Args args = POOL.acquire();
			if (args == null) {
				args = new Args();
			}

			args.source = source;
			args.listener = listener;
			args.reuse();
			return args;
		}
	}
}
