package me.chan.lib.log;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.tencent.mars.xlog.Xlog;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import me.chan.lib.log.compat.LibraryLoader;
import me.chan.lib.log.utils.MiscUtils;
import me.chan.lib.log.xlog.ActivityListener;

/**
 * 扇贝的日志类
 * <p>
 * 如果没有初始化，用的是系统默认的日志
 * <p>
 * 初始化之后，采用能够序列化的日志
 *
 * <pre> {@code
 * public class MyApplication extends Application {
 *    @Override
 *    public void onCreate() {
 * 		super.onCreate();
 * 		Log.init(this, "<你的日志存放路径>");
 *    }
 * }</pre>
 */
public class Log {

	/**
	 * Priority constant for the println method; use Log.v.
	 */
	public static final int VERBOSE = android.util.Log.VERBOSE;

	/**
	 * Priority constant for the println method; use Log.d.
	 */
	public static final int DEBUG = android.util.Log.DEBUG;

	/**
	 * Priority constant for the println method; use Log.i.
	 */
	public static final int INFO = android.util.Log.INFO;

	/**
	 * Priority constant for the println method; use Log.w.
	 */
	public static final int WARN = android.util.Log.WARN;

	/**
	 * Priority constant for the println method; use Log.e.
	 */
	public static final int ERROR = android.util.Log.ERROR;

	/**
	 * Priority constant for the println method.
	 */
	public static final int ASSERT = android.util.Log.ASSERT;

	private static final String TAG = "BayXLog";
	private static final String PUBLIC_KEY = "390ab5a64cfd5ee820272dd312dee500fa82c334abe298b2a93f45de5dc8eefb54b41c07c109219edf84c43b9a6f0544970b251fc093dca991af47482a929f17";

	private static ILog sImpl;
	private static String sLogPath;

	private static final LibraryLoader sLibraryLoader = new LibraryLoader("log", new String[]{"marsxlog"});

	static {
		sImpl = Build.VERSION.SDK_INT == 0 ? new JavaLog() : new AndroidLog();
	}

	public static boolean hasInit() {
		return sImpl instanceof TencentXLog;
	}

	public static void init(final Context context, final String logPath) {
		if (hasInit()) {
			throw new IllegalStateException("init twice");
		}

		if (!sLibraryLoader.load(context)) {
			e(TAG, "load so failed");
			return;
		}

		File file = new File(logPath);
		i(TAG, "log path: " + logPath);
		if (!file.exists() && !file.mkdirs()) {
			e(TAG, "mk log path failed");
			return;
		}

		final String cacheDir = context.getFilesDir() + "/bay_log_cache";
		String processName = MiscUtils.getProcessName(context);
		if (TextUtils.isEmpty(processName)) {
			e(TAG, "fetch process name failed");
			return;
		}

		String md5 = MiscUtils.getMD5(processName);
		String prefix = TextUtils.isEmpty(md5) ? processName : md5;
		i(TAG, "prefix: " + prefix);

		boolean isDebug = MiscUtils.isDebug(context);
		try {
			Xlog.appenderOpen(isDebug ? Xlog.LEVEL_DEBUG : Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cacheDir, logPath, prefix, 0, PUBLIC_KEY);
			Xlog.setConsoleLogOpen(isDebug);
		} catch (Throwable throwable) {
			e(TAG, "init xlog failed");
			return;
		}

		com.tencent.mars.xlog.Log.setLogImp(new Xlog());
		sImpl = new TencentXLog();
		i(TAG, "current process: " + processName);
		i(TAG, "current prefix: " + prefix);
		appendRuntimeLog(context);
		com.tencent.mars.xlog.Log.appenderFlush(false);

		Context app = context.getApplicationContext();
		if (app instanceof Application) {
			Application application = (Application) app;
			application.registerActivityLifecycleCallbacks(new ActivityListener());
		}
		sLogPath = logPath;

		i(TAG, "init xlog success");
	}

	/**
	 * @param enable 设置是否开启日志终端输出
	 */
	public static void setConsoleLogEnable(boolean enable) {
		Xlog.setConsoleLogOpen(enable);
	}

	private static void appendRuntimeLog(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			String version = packageInfo.versionName;
			i(TAG, "current version: " + version);
		} catch (Throwable e) {
			i(TAG, "get version name failed");
		}

		try {
			i(TAG, "current fingerprint: " + Build.FINGERPRINT);
			i(TAG, "current manufacturer: " + Build.MANUFACTURER);
			i(TAG, "current board: " + Build.BOARD);
			i(TAG, "current model: " + Build.MODEL);
			i(TAG, "current bootloader: " + Build.BOOTLOADER);
			i(TAG, "current id: " + Build.ID);
			i(TAG, "current display: " + Build.DISPLAY);
			i(TAG, "current product: " + Build.PRODUCT);
			i(TAG, "current device: " + Build.DEVICE);
			i(TAG, "current sdk version: " + Build.VERSION.SDK_INT);
			i(TAG, "current version release: " + Build.VERSION.RELEASE);
			i(TAG, "current incremental: " + Build.VERSION.INCREMENTAL);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				i(TAG, "current base os: " + Build.VERSION.BASE_OS);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				i(TAG, "current security patch: " + Build.VERSION.SECURITY_PATCH);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				i(TAG, "current preview sdk version: " + Build.VERSION.PREVIEW_SDK_INT);
			}
		} catch (Throwable throwable) {
			i(TAG, "append runtime log failed");
		}
	}

	/**
	 * @return 日志持久化目录
	 */
	@Nullable
	public static String getLogPath() {
		return sLogPath;
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

	private static class TencentXLog implements ILog {

		@Override
		public void v(String tag, String msg) {
			com.tencent.mars.xlog.Log.v(tag, msg);
		}

		@Override
		public void d(String tag, String msg) {
			com.tencent.mars.xlog.Log.d(tag, msg);
		}

		@Override
		public void d(String tag, String msg, Throwable throwable) {
			d(tag, msg);
			d(tag, ExceptionUtils.getStackTrace(throwable));
		}

		@Override
		public void i(String tag, String msg) {
			com.tencent.mars.xlog.Log.i(tag, msg);
		}

		@Override
		public void w(String tag, String msg) {
			com.tencent.mars.xlog.Log.w(tag, msg);
		}

		@Override
		public void w(String tag, Throwable throwable) {
			w(tag, ExceptionUtils.getStackTrace(throwable));
		}

		@Override
		public void e(String tag, String msg) {
			com.tencent.mars.xlog.Log.e(tag, msg);
		}

		@Override
		public void e(String tag, String msg, Throwable throwable) {
			e(tag, msg);
			e(tag, ExceptionUtils.getStackTrace(throwable));
		}

		@Override
		public boolean isLoggable(String tag, int level) {
			return true;
		}

		@Override
		public void appenderFlush(boolean sync) {
			com.tencent.mars.xlog.Log.appenderFlush(sync);
		}

		@Override
		public void appenderClose() {
			com.tencent.mars.xlog.Log.appenderClose();
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
			d(tag, ExceptionUtils.getStackTrace(throwable));
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
			w(tag, ExceptionUtils.getStackTrace(throwable));
		}

		@Override
		public void e(String tag, String msg) {
			System.out.println(getTimestamp() + " E] " + tag + ": " + msg);
		}

		@Override
		public void e(String tag, String msg, Throwable throwable) {
			e(tag, msg);
			e(tag, ExceptionUtils.getStackTrace(throwable));
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

	@WorkerThread
	public static void zipLogs(File output) throws IOException {
		appenderFlush(true);
		List<File> logs = getLogs();
		if (logs.isEmpty()) {
			return;
		}

		if (!output.exists()) {
			output.createNewFile();
		}

		ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(output));
		byte[] content = new byte[256];

		for (File logFile : logs) {
			ZipEntry zipEntry = new ZipEntry(logFile.getName());
			zipOutputStream.putNextEntry(zipEntry);
			FileInputStream fileInputStream = new FileInputStream(logFile);

			int length = -1;
			while ((length = fileInputStream.read(content)) != -1) {
				zipOutputStream.write(content, 0, length);
			}

			zipOutputStream.closeEntry();
		}

		zipOutputStream.close();
	}

	private static List<File> getLogs() {
		List<File> list = new ArrayList<>();
		String path = getLogPath();
		if (TextUtils.isEmpty(path)) {
			return list;
		}

		File file = new File(path);
		if (!file.exists()) {
			return list;
		}

		File[] files = file.listFiles();
		for (int i = 0; files != null && i < files.length; ++i) {

			String name = files[i].getName();
			if (TextUtils.isEmpty(name) || !name.endsWith(".xlog")) {
				continue;
			}
			list.add(files[i]);
		}
		return list;
	}
}
