package me.chan.texas.text.tokenizer;

import androidx.annotation.Nullable;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.HyperSpan;

/**
 * 只产出一个 {@link HyperSpanToken} 的流。
 * <p>
 * 让超文字也能走 {@link me.chan.texas.text.ParagraphBuilderInternal} 里的规则管线。
 */
class HyperSpanTokenStream extends DefaultRecyclable implements TokenStream {
	private static final ObjectPool<HyperSpanTokenStream> POOL = new ObjectPool<>(8);

	private HyperSpan mHyperSpan;
	private int mIndex;

	@Override
	public boolean hasNext() {
		return mIndex < 1;
	}

	@Override
	public int save() {
		return mIndex;
	}

	@Override
	public void restore(int state) {
		mIndex = state;
	}

	@Override
	public Token next() {
		Token token = get(mIndex);
		if (token != null) {
			++mIndex;
		}
		return token;
	}

	@Override
	public Token tryGet(int state, int offset) {
		return get(state + offset);
	}

	@Override
	public Token tryGet(int offset) {
		return tryGet(save(), offset);
	}

	@Nullable
	private Token get(int index) {
		if (index != 0 || mHyperSpan == null) {
			return null;
		}
		return HyperSpanToken.obtain(mHyperSpan);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	protected void onRecycle() {
		mHyperSpan = null;
		mIndex = 0;
		POOL.release(this);
	}

	static HyperSpanTokenStream obtain(HyperSpan span) {
		HyperSpanTokenStream stream = POOL.acquire();
		if (stream == null) {
			stream = new HyperSpanTokenStream();
		}

		stream.mHyperSpan = span;
		stream.mIndex = 0;
		stream.reuse();
		return stream;
	}
}