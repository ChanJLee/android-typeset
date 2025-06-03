package me.chan.texas.utils.concurrency;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.WorkerThread;

import java.util.concurrent.atomic.AtomicInteger;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface Worker {
	<A, R> void async(Token token, @NonNull A args, @NonNull Task<A, R> task);

	<A, R> R sync(Token token, @NonNull A args, @NonNull Task<A, R> task) throws Throwable;

	void cancel(Token token);

	abstract class Task<A, R> {

		@WorkerThread
		public R exec(Token token, A args) throws Throwable {
			return onExec(token, args);
		}

		protected abstract R onExec(Token token, A args) throws Throwable;

		@WorkerThread
		protected void onStart(Token token, A args) {
		}

		@WorkerThread
		protected void onSuccess(Token token, A args, R ret) {
		}

		@WorkerThread
		protected void onError(Token token, A args, Throwable error) {
		}
	}

	class Token {
		private static final AtomicInteger UUID = new AtomicInteger();
		private final int mId = UUID.incrementAndGet();

		private volatile boolean mExpired = false;

		private Token() {

		}

		public int getId() {
			return mId;
		}

		public boolean isExpired() {
			return mExpired;
		}

		public void destroy() {
			if (mExpired) {
				throw new IllegalStateException("destroy token twice");
			}

			mExpired = true;
		}

		@NonNull
		@Override
		public String toString() {
			return "Token{" +
					"mId=" + mId +
					", mExpired=" + mExpired +
					'}';
		}

		public static Token newInstance() {
			return new Token();
		}
	}

	class TokenExpiredException extends Exception {
		private final Token mToken;

		public TokenExpiredException(String message, Token token) {
			super(message);
			mToken = token;
		}

		public Token getToken() {
			return mToken;
		}

		@NonNull
		@Override
		public String toString() {
			return "TokenExpiredException{" +
					"mToken=" + mToken + "," +
					"msg=" + getMessage() +
					'}';
		}
	}
}
