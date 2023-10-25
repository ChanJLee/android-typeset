package me.chan.texas.utils.concurrency;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.util.concurrent.atomic.AtomicInteger;

public interface TaskQueue {
    <A, R> void submit(Token token, @NonNull A args, @NonNull Task<A, R> task, @NonNull Listener<A, R> listener);

    <A, R> R submitSync(Token token, @NonNull A args, @NonNull Task<A, R> task) throws Throwable;

    void cancel(Token token);

    interface Listener<A, R> {
        @WorkerThread
        void onStart(Token token, A args);

        @WorkerThread
        void onSuccess(Token token, A args, R ret);

        @WorkerThread
        void onError(Token token, A args, Throwable throwable);
    }

    interface Task<A, R> {
        R run(Token token, A args) throws Throwable;
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

        @Override
        public String toString() {
            return "TokenExpiredException{" +
                    "mToken=" + mToken + "," +
                    "msg=" + getMessage() +
                    '}';
        }
    }
}
