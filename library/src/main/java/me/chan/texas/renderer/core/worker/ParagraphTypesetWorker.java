package me.chan.texas.renderer.core.worker;

import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.core.sync.WorkerMessager;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Region;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Penalty;
import me.chan.texas.typesetter.ParagraphTypesetter;
import me.chan.texas.typesetter.utils.ElementStream;
import me.chan.texas.utils.concurrency.TaskQueue;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ParagraphTypesetWorker implements TaskQueue.Task<ParagraphTypesetWorker.Args, Paragraph>,
		TaskQueue.Listener<ParagraphTypesetWorker.Args, Paragraph> {
	private static final int TYPE_SUCCESS = 1;
	private static final int TYPE_ERROR = 2;

	private final ParagraphTypesetter mTypesetter;
	private final TaskQueue mTaskQueue;
	private final WorkerMessager mMessager;

	public ParagraphTypesetWorker(TaskQueue taskQueue, WorkerMessager messager) {
		mTaskQueue = taskQueue;
		mMessager = messager;
		mTypesetter = new ParagraphTypesetter();
		mMessager.addListener((id, message) -> {
			Args args = message.asArg(Args.class);
			if (args == null) {
				return false;
			}

			switch (message.type()) {
				case TYPE_SUCCESS:
					if (args.listener != null) {
						args.listener.onTypesetSuccess(message.value());
					}
					break;
				case TYPE_ERROR:
					if (args.listener != null) {
						args.listener.onTypesetFailure(message.error());
					}
					break;
			}
			args.recycle();
			return true;
		});
	}

	public String stats() {
		return mTypesetter.stats();
	}

	public void submit(TaskQueue.Token token, Args args) {
		mTaskQueue.submit(token, args, this, this);
	}

	public Paragraph submitSync(TaskQueue.Token token, Args args) throws Throwable {
		return mTaskQueue.submitSync(token, args, this);
	}

	public void cancel(TaskQueue.Token token) {
		mTaskQueue.cancel(token);
	}

	@VisibleForTesting
	public Object getTypesetterInternalState() {
		return mTypesetter.getInternalState();
	}

	@Override
	public void onStart(TaskQueue.Token token, Args args) {
		/* do nothing */
	}

	@Override
	public void onSuccess(TaskQueue.Token token, Args args, Paragraph ret) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_SUCCESS, args, ret);
		mMessager.send(token, message);
	}

	@Override
	public void onError(TaskQueue.Token token, Args args, Throwable throwable) {
		Log.w("TypesetWorker", throwable);
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_ERROR, args, throwable);
		mMessager.send(token, message);
	}

	@Override
	public Paragraph run(TaskQueue.Token token, Args args) throws Throwable {
		Paragraph paragraph = args.paragraph;
		Layout layout = paragraph.getLayout();

		Layout.Advise advise = layout.getAdvise();
		BreakStrategy breakStrategy = advise.getBreakStrategy();
		if (breakStrategy == null) {
			breakStrategy = args.option.getBreakStrategy();
		}

		float lineSpace = advise.getLineSpace();
		if (lineSpace < 0) {
			lineSpace = args.option.getLineSpace();
		}

		if (!mTypesetter.typeset(paragraph, breakStrategy, args.width, lineSpace)) {
			throw new RuntimeException("typeset failed");
		}
		return paragraph;
	}

	/**
	 * 预测宽高
	 *
	 * @param paragraph 段落
	 * @param region    段落的宽高
	 * @param option    渲染选项
	 * @return true表示成功
	 */
	public boolean desire(@NonNull Paragraph paragraph, @NonNull Region region, RenderOption option) {
		// TODO paragraph view 的 source 内部进行load
		ElementStream stream = paragraph.tryGetElementStream();
		if (stream == null || stream.isEmpty()) {
			return false;
		}

		try {
			int width = 0;
			int height = 0;
			int lineCount = 0;

			int tmpWidth = 0;
			int lineHeight = -1;
			int count = stream.size();
			for (int i = 0; i < count; ++i) {
				Element element = stream.get(i);
				if (element == Penalty.FORCE_BREAK || i + 1 == count) {
					if (tmpWidth >= width) {
						width = tmpWidth;
					}

					++lineCount;
					if (lineHeight > 0) {
						height += lineHeight;
					}
					lineHeight = -1;
					tmpWidth = 0;
					continue;
				}

				if (element instanceof Box) {
					Box box = (Box) element;
					tmpWidth += box.getWidth();
					float boxHeight = box.getHeight();
					if (boxHeight > lineHeight) {
						lineHeight = (int) boxHeight;
					}
				} else if (element instanceof Glue && element != Glue.TERMINAL && element != Glue.EMPTY) {
					Glue glue = (Glue) element;
					tmpWidth += glue.getWidth();
				}
			}

			Layout layout = paragraph.getLayout();
			Layout.Advise advise = layout.getAdvise();

			float lineSpace = advise.getLineSpace();
			if (lineSpace < 0) {
				lineSpace = option.getLineSpace();
			}
			height += (int) ((lineCount - 1) * lineSpace);

			region.setWidth(width);
			region.setHeight(height);

			return true;
		} catch (Throwable ignore) {
			return false;
		}
	}

	public interface Listener {

		void onTypesetSuccess(Paragraph paragraph);

		void onTypesetFailure(Throwable throwable);
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<Args> POOL = new ObjectPool<>(32);
		private Paragraph paragraph;
		private RenderOption option;
		private int width;

		private Listener listener;

		private Args() {
		}

		@Override
		protected void onRecycle() {
			paragraph = null;
			option = null;
			listener = null;
			width = 0;
			POOL.release(this);
		}

		public static Args obtain(@NonNull Paragraph paragraph,
								  @NonNull RenderOption option,
								  @IntRange(from = 1) int width) {
			return obtain(paragraph, option, width, null);
		}

		public static Args obtain(@NonNull Paragraph paragraph,
								  @NonNull RenderOption option,
								  @IntRange(from = 1) int width,
								  @Nullable Listener listener) {
			Args args = POOL.acquire();
			if (args == null) {
				args = new Args();
			}

			args.paragraph = paragraph;
			args.option = option;
			args.width = width;
			args.listener = listener;
			args.reuse();
			return args;
		}
	}
}
