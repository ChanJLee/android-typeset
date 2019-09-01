package me.chan.te.log;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Log {
	private static final String TAG = "TextEngine";

	private static ILog sImpl;

	static {
		try {
			// 不能通过反射读取
			// 反射能够读到类，但是加载会失败
			android.util.Log.d(TAG, "test log");
			sImpl = new AndroidLog();
		} catch (Throwable throwable) {
			sImpl = new JavaLog();
		}
	}

	public static void v(String tag, String msg) {
		sImpl.v(tag, msg);
	}

	public static void d(String tag, String msg) {
		sImpl.d(tag, msg);
	}

	public static void d(String tag, String msg, Throwable throwable) {
		sImpl.d(tag, msg, throwable);
	}

	public static void i(String tag, String msg) {
		sImpl.i(tag, msg);
	}

	public static void w(String tag, String msg) {
		sImpl.w(tag, msg);
	}

	public static void w(String tag, Throwable throwable) {
		sImpl.w(tag, throwable);
	}

	public static void e(String tag, String msg) {
		sImpl.e(tag, msg);
	}

	public static void e(String tag, String msg, Throwable throwable) {
		sImpl.e(tag, msg, throwable);
	}

	public static boolean isLoggable(String tag, int level) {
		return sImpl.isLoggable(tag, level);
	}

	public static void appenderFlush(boolean sync) {
		sImpl.appenderFlush(sync);
	}

	public static void appenderClose() {
		sImpl.appenderClose();
	}

	public static void w(String msg) {
		w(TAG, msg);
	}

	private interface ILog {
		void v(String tag, String msg);

		void d(String tag, String msg);

		void d(String tag, String msg, Throwable throwable);

		void i(String tag, String msg);

		void w(String tag, String msg);

		void w(String tag, Throwable throwable);

		void e(String tag, String msg);

		void e(String tag, String msg, Throwable throwable);

		boolean isLoggable(String tag, int level);

		void appenderFlush(boolean sync);

		void appenderClose();
	}

	private static class AndroidLog implements ILog {

		@Override
		public void v(String tag, String msg) {
			android.util.Log.v(tag, msg);
		}

		@Override
		public void d(String tag, String msg) {
			android.util.Log.d(tag, msg);
		}

		@Override
		public void d(String tag, String msg, Throwable throwable) {
			android.util.Log.d(tag, msg, throwable);
		}

		@Override
		public void i(String tag, String msg) {
			android.util.Log.i(tag, msg);
		}

		@Override
		public void w(String tag, String msg) {
			android.util.Log.w(tag, msg);
		}

		@Override
		public void w(String tag, Throwable throwable) {
			android.util.Log.w(tag, throwable);
		}

		@Override
		public void e(String tag, String msg) {
			android.util.Log.e(tag, msg);
		}

		@Override
		public void e(String tag, String msg, Throwable throwable) {
			android.util.Log.e(tag, msg, throwable);
		}

		@Override
		public boolean isLoggable(String tag, int level) {
			return android.util.Log.isLoggable(tag, level);
		}

		@Override
		public void appenderFlush(boolean sync) {
			/* do nothing */
		}

		@Override
		public void appenderClose() {
			/* do nothing */
		}
	}

	private static class JavaLog implements ILog {

		@Override
		public void v(String tag, String msg) {
			System.out.println(getTimestamp() + " V] " + tag + ": " + msg);
		}

		@Override
		public void d(String tag, String msg) {
			System.out.println(getTimestamp() + " D] " + tag + ": " + msg);
		}

		@Override
		public void d(String tag, String msg, Throwable throwable) {
			d(tag, msg);
			throwable.printStackTrace(System.out);
		}

		@Override
		public void i(String tag, String msg) {
			System.out.println(getTimestamp() + " I] " + tag + ": " + msg);
		}

		@Override
		public void w(String tag, String msg) {
			System.out.println(getTimestamp() + " W] " + tag + ": " + msg);
		}

		@Override
		public void w(String tag, Throwable throwable) {
			w(tag, "stack trace:");
			throwable.printStackTrace(System.out);
		}

		@Override
		public void e(String tag, String msg) {
			System.out.println(getTimestamp() + " E] " + tag + ": " + msg);
		}

		@Override
		public void e(String tag, String msg, Throwable throwable) {
			e(tag, msg);
			throwable.printStackTrace(System.out);
		}

		@Override
		public boolean isLoggable(String tag, int level) {
			return true;
		}

		@Override
		public void appenderFlush(boolean sync) {
			/* do nothing */
		}

		@Override
		public void appenderClose() {
			/* do nothing */
		}

		private static String getTimestamp() {
			long millis = System.currentTimeMillis();
			long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
			long hours = TimeUnit.MILLISECONDS.toHours(millis);
			return String.format(Locale.CHINA, "%02d:%02d:%02d",
					(hours - TimeUnit.DAYS.toHours(TimeUnit.HOURS.toDays(hours)) + 8) % 24,
					minutes - TimeUnit.HOURS.toMinutes(hours), // The change is in this line
					TimeUnit.MILLISECONDS.toSeconds(millis) -
							TimeUnit.MINUTES.toSeconds(minutes));
		}
	}
}
