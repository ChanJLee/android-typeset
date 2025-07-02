package me.chan.texas.renderer.core.worker;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.typesetter.AbsParagraphTypesetter;
import me.chan.texas.typesetter.ParagraphTypesetter;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ParagraphTypesetWorker {
	private final ParagraphTypesetter mTypesetter;

	public ParagraphTypesetWorker() {
		mTypesetter = new ParagraphTypesetter();
	}

	public String stats() {
		return mTypesetter.stats();
	}

	public void typeset(Args args) {
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
	}

	@VisibleForTesting
	public Object getTypesetterInternalState() {
		return mTypesetter.getInternalState();
	}

	
	public boolean desire(@NonNull Paragraph paragraph, int expectedWidth) {
		if (!paragraph.hasContent()) {
			return false;
		}

		ParagraphTypesetWorker.Args args = ParagraphTypesetWorker.Args.obtain(paragraph, expectedWidth);
		try {
			typeset(args);
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	
	public boolean desire(@NonNull Paragraph paragraph) {
		if (!paragraph.hasContent()) {
			return false;
		}

		ParagraphTypesetWorker.Args args = ParagraphTypesetWorker.Args.desire(paragraph);
		try {
			typeset(args);
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
