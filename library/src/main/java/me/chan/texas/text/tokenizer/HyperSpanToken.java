package me.chan.texas.text.tokenizer;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.HyperSpan;

/**
 * 承载 {@link HyperSpan} 的 token。
 * <p>
 * 它对外报告 {@link #TYPE_SYMBOL}，并把 {@link #checkAttribute(int)} 等问询代理给
 * {@link HyperSpan}，让超文字与普通 symbol token 共享同一套排版规则。
 */
public class HyperSpanToken extends Token {
	private static final ObjectPool<HyperSpanToken> POOL = new ObjectPool<>(16);

	private HyperSpan mHyperSpan;

	private HyperSpanToken() {
	}

	@TokenType
	@Override
	public byte getType() {
		return TYPE_SYMBOL;
	}

	public HyperSpan getHyperSpan() {
		return mHyperSpan;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	@Override
	public boolean checkAttribute(int attribute) {
		return mHyperSpan != null && mHyperSpan.checkAttribute(attribute);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	@Override
	public boolean hasSymbolTypefaceAttributes() {
		return mHyperSpan != null && mHyperSpan.hasSymbolTypefaceAttributes();
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