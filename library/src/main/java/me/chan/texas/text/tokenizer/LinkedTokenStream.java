package me.chan.texas.text.tokenizer;

import androidx.annotation.Nullable;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

class LinkedTokenStream extends DefaultRecyclable implements TokenStream {
	private static final ObjectPool<LinkedTokenStream> POOL = new ObjectPool<>(8);

	private TokenStream mStream1;
	private TokenStream mStream2;
	private int mIndex;

	@Override
	public boolean hasNext() {
		return mIndex < size();
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
		int index = state + offset;
		return get(index);
	}

	@Override
	public Token tryGet(int offset) {
		return tryGet(save(), offset);
	}

	@Nullable
	private Token get(int index) {
		int s1Size = mStream1.size();
		if (index < s1Size) {
			return get0(mStream1, index);
		}
		return get0(mStream2, index - s1Size);
	}

	private static Token get0(TokenStream stream, int index) {
		return stream.tryGet(0, index);
	}

	@Override
	public int size() {
		return mStream1.size() + mStream2.size();
	}

	public static TokenStream obtain(TokenStream stream1, TokenStream stream2) {
		LinkedTokenStream instance = POOL.acquire();
		if (instance == null) {
			instance = new LinkedTokenStream();
		}
		instance.mStream1 = stream1;
		instance.mStream2 = stream2;
		instance.mIndex = 0;
		instance.reuse();
		return instance;
	}

	@Override
	protected void onRecycle() {
		mStream1.recycle();
		mStream2.recycle();
		POOL.release(this);
	}
}
