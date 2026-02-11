package me.chan.texas;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.di.DaggerTexasComponent;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.issue.IssueSystem;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.DrawableBox;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.utils.TexasUtils;

import java.util.HashSet;
import java.util.Set;

public class Texas {
	@RestrictTo(LIBRARY)
	public static final boolean DEBUG_DRAG = false;

	public static final float INFINITY_PENALTY = 1000;
	public static final int HYPHEN_PENALTY = 20;

	private static MemoryOption sMemoryOption;
	private static final Set<LifecycleCallback> LIFECYCLE_CALLBACKS = new HashSet<>();
	private static Context sAppContext;

	@RestrictTo(LIBRARY)
	public static void registerLifecycleCallback(LifecycleCallback callback) {
		LIFECYCLE_CALLBACKS.add(callback);
	}

	@RestrictTo(LIBRARY)
	public static void unregisterLifecycleCallback(LifecycleCallback callback) {
		LIFECYCLE_CALLBACKS.remove(callback);
	}

	@RestrictTo(LIBRARY)
	@NonNull
	public static MemoryOption getMemoryOption() {
		if (sMemoryOption == null) {
			// 防止某些机器上 memory option 被回收
			// 几乎不可能走到这里
			// 一旦发生了，就用默认的内存选项
			// 默认的内存选项是最保守的选项
			sMemoryOption = new MemoryOption();
		}
		return sMemoryOption;
	}

	/**
	 * 初始化
	 *
	 * @param application application
	 */
	public static void init(Application application) {
		init(application, new MemoryOption());
	}

	/**
	 * 初始化
	 *
	 * @param application  application
	 * @param memoryOption 内存选项
	 */
	public static void init(Application application, MemoryOption memoryOption) {
		if (sAppContext != null) {
			return;
		}

		sAppContext = application;
		sMemoryOption = memoryOption;
		application.registerComponentCallbacks(new ComponentCallbacks() {
			@Override
			public void onConfigurationChanged(Configuration newConfig) {

			}

			@Override
			public void onLowMemory() {
				// 某些机型可能上来就走到了这里
				// 无论怎样，一旦发生内存紧张的情况
				// 都需要重置下内存选项
				// 这时候几乎禁用缓存
				// 针对buffer size的选项，只有在 某些机型可能上来就走到了这里 才生效
				// 如果一开始不紧张，buffer size的设置是无效的
				Texas.MemoryOption memoryOption = Texas.getMemoryOption();
				memoryOption.setDocumentSegmentInitialCapacity(4)
						.setParagraphElementInitialCapacity(4)
						.setLineBoxInitialCapacity(4)
						.setParagraphLineInitialCapacity(4)
						.setTextBufferSize(4)
						.setLineBufferSize(4)
						.setParagraphBufferSize(4)
						.setFigureBufferSize(4)
						.setEmoticonBufferSize(4);
				clean();
			}
		});
		TexasUtils.init(application);
	}

	@RestrictTo(LIBRARY)
	public static Context getAppContext() {
		return sAppContext;
	}

	/**
	 * do clean
	 */
	@RestrictTo(LIBRARY)
	public static void clean() {
		try {
			Log.i("Texas", "clean text engine memory");
			// do clean
			// add engine clean code
			TextBox.clean();
			Line.clean();
			DrawableBox.clean();
			Paragraph.Builder.clean();
			for (LifecycleCallback callback : LIFECYCLE_CALLBACKS) {
				try {
					Log.i("Texas", "release: " + callback);
					callback.onClean();
				} catch (Throwable throwable) {
					Log.w("Texas", throwable);
				}
			}
			System.gc();
		} catch (Throwable throwable) {
			Log.w("Texas", throwable);
		}
	}

	/**
	 * 用来修改texas初始化的时候内存使用选项。
	 * 加大每种参数都会使得texas在运行的时候速度变快。
	 * 用空间换时间。
	 * 使用的时候根据自己的业务类型，灵活的调参。
	 * <p/>
	 * 另外，以BufferSize结尾的参数，是Texas内部使用的缓存大小，减小重复数据结构创建的次数。
	 * 以InitialCapacity结尾的参数，是用来预测内容可能的大小，减少容器扩容次数。
	 */
	public static class MemoryOption {
		private int mDocumentSegmentInitialCapacity = 32;
		private int mParagraphLineInitialCapacity = 16;
		private int mParagraphElementInitialCapacity = 128;
		private int mLineBoxInitialCapacity = 16;

		private int mTextBufferSize = 4096;
		private int mLineBufferSize = 1024;
		private int mParagraphBufferSize = 256;
		private int mFigureBufferSize = 8;
		private int mEmoticonBufferSize = 256;

		public int getTextBufferSize() {
			return mTextBufferSize;
		}

		/**
		 * 设置 文字 buffer size
		 *
		 * @param size 大小
		 * @return 当前对象
		 */
		public MemoryOption setTextBufferSize(int size) {
			if (size <= 0) {
				throw new IllegalArgumentException("size <= 0");
			}
			mTextBufferSize = size;
			return this;
		}

		public int getLineBufferSize() {
			return mLineBufferSize;
		}

		/**
		 * 设置 行 buffer size
		 *
		 * @param size 大小
		 * @return 当前对象
		 */
		public MemoryOption setLineBufferSize(int size) {
			if (size <= 0) {
				throw new IllegalArgumentException("size <= 0");
			}
			mLineBufferSize = size;
			return this;
		}

		public int getParagraphBufferSize() {
			return mParagraphBufferSize;
		}

		/**
		 * 设置 段落 buffer size
		 *
		 * @param size 大小
		 * @return 当前对象
		 */
		public MemoryOption setParagraphBufferSize(int size) {
			if (size <= 0) {
				throw new IllegalArgumentException("size <= 0");
			}
			mParagraphBufferSize = size;
			return this;
		}

		public int getFigureBufferSize() {
			return mFigureBufferSize;
		}

		/**
		 * 设置 图片 buffer size
		 *
		 * @param size 大小
		 * @return 当前对象
		 */
		public MemoryOption setFigureBufferSize(int size) {
			if (size <= 0) {
				throw new IllegalArgumentException("size <= 0");
			}
			mFigureBufferSize = size;
			return this;
		}

		public int getEmoticonBufferSize() {
			return mEmoticonBufferSize;
		}

		/**
		 * 设置 颜文字 buffer size
		 *
		 * @param size 大小
		 * @return 当前对象
		 */
		public MemoryOption setEmoticonBufferSize(int size) {
			if (size <= 0) {
				throw new IllegalArgumentException("size <= 0");
			}
			mEmoticonBufferSize = size;
			return this;
		}

		public int getDocumentSegmentInitialCapacity() {
			return mDocumentSegmentInitialCapacity;
		}

		/**
		 * 设置document中为segment预留的大小
		 *
		 * @param size 大小
		 * @return 当前对象
		 */
		public MemoryOption setDocumentSegmentInitialCapacity(int size) {
			if (size <= 0) {
				throw new IllegalArgumentException("size <= 0");
			}
			mDocumentSegmentInitialCapacity = size;
			return this;
		}

		public int getParagraphLineInitialCapacity() {
			return mParagraphLineInitialCapacity;
		}

		/**
		 * 设置paragraph中为line预留的大小
		 *
		 * @param size 大小
		 * @return 当前对象
		 */
		public MemoryOption setParagraphLineInitialCapacity(int size) {
			if (size <= 0) {
				throw new IllegalArgumentException("size <= 0");
			}
			mParagraphLineInitialCapacity = size;
			return this;
		}

		public int getParagraphElementInitialCapacity() {
			return mParagraphElementInitialCapacity;
		}

		/**
		 * 设置paragraph中为element预留的大小
		 *
		 * @param size 大小
		 * @return 当前对象
		 */
		public MemoryOption setParagraphElementInitialCapacity(int size) {
			if (size <= 0) {
				throw new IllegalArgumentException("size <= 0");
			}
			mParagraphElementInitialCapacity = size;
			return this;
		}

		public int getLineBoxInitialCapacity() {
			return mLineBoxInitialCapacity;
		}

		/**
		 * 设置line为box预留的大小
		 *
		 * @param size 大小
		 * @return 当前对象
		 */
		public MemoryOption setLineBoxInitialCapacity(int size) {
			if (size <= 0) {
				throw new IllegalArgumentException("size <= 0");
			}
			mLineBoxInitialCapacity = size;
			return this;
		}
	}

	@RestrictTo(LIBRARY)
	public interface LifecycleCallback {
		void onClean();
	}

	private static TexasComponent sTexasComponent;

	static {
		sTexasComponent = DaggerTexasComponent.create();
	}

	@RestrictTo(LIBRARY)
	@VisibleForTesting
	public static void setTexasComponent(@NonNull TexasComponent texasComponent) {
		sTexasComponent = texasComponent;
	}

	@RestrictTo(LIBRARY)
	@NonNull
	public static TexasComponent getTexasComponent() {
		return sTexasComponent;
	}

	public static final boolean CHECK_RULES_TRANSLATE = false;

	public static void setIssueCallback(IssueCallback issueCallback) {
		IssueSystem.setIssueCallback(issueCallback);
	}

	public interface IssueCallback {
		/**
		 * @param moduleName 出问题的模块名
		 * @param throwable  报错
		 */
		void onIssueCaught(String moduleName, Throwable throwable);
	}

	private static boolean sEnableTexCompat = false;

	public static boolean isEnableTexCompat() {
		return sEnableTexCompat;
	}

	public static void setEnableTexCompat(boolean enable) {
		sEnableTexCompat = enable;
	}

	private static Typeface sDefaultTypeface = Typeface.DEFAULT;

	public static Typeface getDefaultTypeface() {
		return sDefaultTypeface;
	}

	public static void setDefaultTypeface(Typeface defaultTypeface) {
		sDefaultTypeface = defaultTypeface;
	}

	private static TypesetFactor sTypesetFactor = new TypesetFactor();

	public static TypesetFactor getTypesetFactor() {
		return sTypesetFactor;
	}

	public static void setTypesetFactor(@NonNull TypesetFactor factor) {
		if (factor == null) {
			throw new IllegalArgumentException("factor == null");
		}
		sTypesetFactor = factor;
	}

	public static class TypesetFactor {
		public float spaceWidthFactor;
		public float spaceStretchFactor;
		public float spaceShrinkFactor;

		public TypesetFactor() {
			this(1f, 0.6f, 0.2f);
		}

		public TypesetFactor(float spaceWidthFactor, float spaceStretchFactor, float spaceShrinkFactor) {
			this.spaceWidthFactor = spaceWidthFactor;
			this.spaceStretchFactor = spaceStretchFactor;
			this.spaceShrinkFactor = spaceShrinkFactor;
		}
	}
}
