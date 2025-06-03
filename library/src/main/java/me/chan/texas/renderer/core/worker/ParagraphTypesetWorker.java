package me.chan.texas.renderer.core.worker;

import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.typesetter.AbsParagraphTypesetter;
import me.chan.texas.typesetter.ParagraphTypesetter;
import me.chan.texas.utils.concurrency.Worker;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ParagraphTypesetWorker {
	private static final int TYPE_SUCCESS = 1;
	private static final int TYPE_ERROR = 2;

	private final ParagraphTypesetter mTypesetter;
	private final Worker mWorker;
	private final MsgHandler mMsgHandler;

	private final Worker.Task<ParagraphTypesetWorker.Args, Paragraph> mTask;

	public ParagraphTypesetWorker(Worker worker, MsgHandler msgHandler) {
		mWorker = worker;
		mMsgHandler = msgHandler;
		mTypesetter = new ParagraphTypesetter();
		mMsgHandler.addListener((id, message) -> {
			Args args = message.asArg(Args.class);
			if (args == null) {
				return false;
			}

			args.recycle();
			return true;
		});
		mTask = new Worker.Task<ParagraphTypesetWorker.Args, Paragraph>() {
			@Override
			public void onSuccess(Worker.Token token, Args args, Paragraph ret) {
				MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_SUCCESS, args, ret);
				mMsgHandler.send(token, message);
			}

			@Override
			public void onError(Worker.Token token, Args args, Throwable error) {
				Log.w("TypesetWorker", error);
				MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_ERROR, args, error);
				mMsgHandler.send(token, message);
			}

			@Override
			protected Paragraph onExec(Worker.Token token, Args args) {
				Paragraph paragraph = args.paragraph;
				Layout layout = paragraph.getLayout();

				Layout.Advise advise = layout.getAdvise();
				BreakStrategy breakStrategy = advise.getBreakStrategy();
				if (args.desired) {
					if (!mTypesetter.desire(paragraph, breakStrategy)) {
						throw new RuntimeException("desire failed");
					}
				} else {
					if (!mTypesetter.typeset(paragraph, breakStrategy, args.width)) {
						throw new RuntimeException("typeset failed");
					}
				}
				return paragraph;
			}
		};
	}

	public String stats() {
		return mTypesetter.stats();
	}

	public void submit(Worker.Token token, Args args) {
		mWorker.async(token, args, mTask);
	}

	public Paragraph submitSync(Worker.Token token, Args args) throws Throwable {
		return mWorker.sync(token, args, mTask);
	}

	public void cancel(Worker.Token token) {
		mWorker.cancel(token);
	}

	@VisibleForTesting
	public Object getTypesetterInternalState() {
		return mTypesetter.getInternalState();
	}

	/**
	 * 预测宽高
	 *
	 * @param paragraph 段落
	 * @param token     令牌
	 * @return true表示成功
	 */
	public boolean desire(@NonNull Paragraph paragraph, Worker.Token token, int expectedWidth) {
		if (!paragraph.hasContent()) {
			return false;
		}

		ParagraphTypesetWorker worker = WorkerScheduler.typeset();
		ParagraphTypesetWorker.Args args = ParagraphTypesetWorker.Args.obtain(paragraph, expectedWidth);
		try {
			worker.submitSync(token, args);
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	/**
	 * 预测宽高
	 *
	 * @param paragraph 段落
	 * @param token     令牌
	 * @return true表示成功
	 */
	public boolean desire(@NonNull Paragraph paragraph, Worker.Token token) {
		if (!paragraph.hasContent()) {
			return false;
		}

		ParagraphTypesetWorker worker = WorkerScheduler.typeset();
		ParagraphTypesetWorker.Args args = ParagraphTypesetWorker.Args.desire(paragraph);
		try {
			worker.submitSync(token, args);
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<Args> POOL = new ObjectPool<>(32);
		private Paragraph paragraph;
		private int width;
		private boolean desired;

		private Args() {
		}

		@Override
		protected void onRecycle() {
			paragraph = null;
			width = 0;
			desired = false;
			POOL.release(this);
		}

		public static Args obtain(@NonNull Paragraph paragraph,
								  @IntRange(from = 1) int width) {
			Args args = POOL.acquire();
			if (args == null) {
				args = new Args();
			}

			args.paragraph = paragraph;
			args.width = width;
			args.desired = false;
			args.reuse();
			return args;
		}

		public static Args desire(@NonNull Paragraph paragraph) {
			Args args = POOL.acquire();
			if (args == null) {
				args = new Args();
			}

			args.paragraph = paragraph;
			args.width = AbsParagraphTypesetter.INFINITY_WIDTH;
			args.desired = true;
			args.reuse();
			return args;
		}
	}
}
