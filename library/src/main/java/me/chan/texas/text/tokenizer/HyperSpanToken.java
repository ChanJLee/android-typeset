package me.chan.texas.text.tokenizer;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.HyperSpan;

/**
 * 承载 {@link HyperSpan} 的 token。
 * <p>
 * 借助这个子类，超文字和文字 token 共用同一个 {@link TokenStream} 与排版规则管线，
 * 这样它前后的空格、断行建议等可以由统一的状态机处理。
 */
public class HyperSpanToken extends Token {
	private static final ObjectPool<HyperSpanToken> POOL = new ObjectPool<>(16);

	private HyperSpan mHyperSpan;

	private HyperSpanToken() {
	}

	@TokenType
	@Override
	public byte getType() {
		return TYPE_HYPER_SPAN;
	}

	public HyperSpan getHyperSpan() {
		return mHyperSpan;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	@Override
	public String getSemantics() {
		return "超文字";
	}

	@NonNull
	@Override
	public String toString() {
		if (isRecycled()) {
			return "<recycled>";
		}
		return "[" + getSemantics() + " <" + mHyperSpan + ">]";
	}

	@Override
	protected void onRecycle() {
		mHyperSpan = null;
		POOL.release(this);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static HyperSpanToken obtain(HyperSpan span) {
		HyperSpanToken token = POOL.acquire();
		if (token == null) {
			token = new HyperSpanToken();
		}

		token.reuse();
		token.mHyperSpan = span;
		return token;
	}
}