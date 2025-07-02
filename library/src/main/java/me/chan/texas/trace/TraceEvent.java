package me.chan.texas.trace;

import androidx.annotation.IntDef;

import me.chan.texas.BuildConfig;

public class TraceEvent {
    @TraceLevel
    private static int sLevel = BuildConfig.DEBUG ? TraceLevel.VERBOSE : TraceLevel.INFO;

    public static void setLevel(@TraceLevel int level) {
        sLevel = level;
    }

    @IntDef({TraceLevel.VERBOSE, TraceLevel.DEBUG, TraceLevel.WARNING, TraceLevel.ERROR, TraceLevel.INFO})
    public @interface TraceLevel {
        int VERBOSE = 1;

        int DEBUG = 2;
        int INFO = 3;
        int WARNING = 4;
        int ERROR = 5;
    }

    public static Event verbose(String tag) {
        return new NoopEvent();
    }

    public static Event debug(String tag) {
        return new NoopEvent();
    }

    public static Event info(String tag) {
        return new NoopEvent();
    }

    public static Event warning(String tag) {
        return new NoopEvent();
    }

    public static Event error(String tag) {
        return new NoopEvent();
    }

    public interface Event {
        Event append(CharSequence s);

        Event append(CharSequence s, int start, int end);

        Event append(char[] str);

        Event append(char[] str, int offset, int len);

        Event append(boolean b);

        Event append(char c);

        Event append(int i);

        Event append(long lng);

        Event append(float f);

        Event append(double d);

        Event append(Object obj);

        void send();
    }

    private static class NoopEvent implements Event {

        @Override
        public Event append(CharSequence s) {
            return this;
        }

        @Override
        public Event append(CharSequence s, int start, int end) {
            return this;
        }

        @Override
        public Event append(char[] str) {
            return this;
        }

        @Override
        public Event append(char[] str, int offset, int len) {
            return this;
        }

        @Override
        public Event append(boolean b) {
            return this;
        }

        @Override
        public Event append(char c) {
            return this;
        }

        @Override
        public Event append(int i) {
            return this;
        }

        @Override
        public Event append(long lng) {
            return this;
        }

        @Override
        public Event append(float f) {
            return this;
        }

        @Override
        public Event append(double d) {
            return this;
        }

        @Override
        public Event append(Object obj) {
            return this;
        }

        @Override
        public void send() {
            
        }
    }
}
