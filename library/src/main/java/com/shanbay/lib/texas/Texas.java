package com.shanbay.lib.texas;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

import com.shanbay.lib.log.Log;
import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.text.ColorGround;
import com.shanbay.lib.texas.text.Document;
import com.shanbay.lib.texas.text.DrawableBox;
import com.shanbay.lib.texas.text.Figure;
import com.shanbay.lib.texas.text.Glue;
import com.shanbay.lib.texas.text.Line;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.Penalty;
import com.shanbay.lib.texas.text.TextBox;
import com.shanbay.lib.texas.text.UnderLine;

import java.util.HashSet;
import java.util.Set;

public class Texas {

	private static MemoryOption sMemoryOption = new MemoryOption();
	private static final Set<LifecycleCallback> LIFECYCLE_CALLBACKS = new HashSet<>();

	@Hidden
	public static void registerLifecycleCallback(LifecycleCallback callback) {
		LIFECYCLE_CALLBACKS.add(callback);
	}

	@Hidden
	public static void unregisterLifecycleCallback(LifecycleCallback callback) {
		LIFECYCLE_CALLBACKS.remove(callback);
	}

	@Hidden
	public static MemoryOption getMemoryOption() {
		return sMemoryOption;
	}

	@Hidden
	public static void setMemoryOption(MemoryOption memoryOption) {
		sMemoryOption = memoryOption;
	}

	public static void init(Application application) {
		application.registerComponentCallbacks(new ComponentCallbacks() {
			@Override
			public void onConfigurationChanged(Configuration newConfig) {

			}

			@Override
			public void onLowMemory() {
				Texas.MemoryOption memoryOption = Texas.getMemoryOption();
				memoryOption.setDocumentSegmentInitialCapacity(4)
						.setParagraphElementInitialCapacity(4)
						.setParagraphLineBoxInitialCapacity(4)
						.setParagraphLineInitialCapacity(4);
				Texas.setMemoryOption(memoryOption);
				clean();
			}
		});
	}

	/**
	 * do clean
	 */
	@Hidden
	public static void clean() {
		Log.i("Texas", "clean text engine memory");
		// do clean
		// add engine clean code
		TextBox.clean();
		Glue.clean();
		Line.clean();
		Paragraph.clean();
		Penalty.clean();
		Document.clean();
		Figure.clean();
		DrawableBox.clean();
		ColorGround.clean();
		UnderLine.clean();
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
	}

	@Hidden
	public static class MemoryOption {
		private int mDocumentSegmentInitialCapacity = 512;
		private int mParagraphLineInitialCapacity = 32;
		private int mParagraphElementInitialCapacity = 128;
		private int mParagraphLineBoxInitialCapacity = 32;

		public int getDocumentSegmentInitialCapacity() {
			return mDocumentSegmentInitialCapacity;
		}

		public MemoryOption setDocumentSegmentInitialCapacity(int documentSegmentInitialCapacity) {
			mDocumentSegmentInitialCapacity = documentSegmentInitialCapacity;
			return this;
		}

		public int getParagraphLineInitialCapacity() {
			return mParagraphLineInitialCapacity;
		}

		public MemoryOption setParagraphLineInitialCapacity(int paragraphLineInitialCapacity) {
			mParagraphLineInitialCapacity = paragraphLineInitialCapacity;
			return this;
		}

		public int getParagraphElementInitialCapacity() {
			return mParagraphElementInitialCapacity;
		}

		public MemoryOption setParagraphElementInitialCapacity(int paragraphElementInitialCapacity) {
			mParagraphElementInitialCapacity = paragraphElementInitialCapacity;
			return this;
		}

		public int getParagraphLineBoxInitialCapacity() {
			return mParagraphLineBoxInitialCapacity;
		}

		public MemoryOption setParagraphLineBoxInitialCapacity(int paragraphLineBoxInitialCapacity) {
			mParagraphLineBoxInitialCapacity = paragraphLineBoxInitialCapacity;
			return this;
		}
	}

	@Hidden
	public interface LifecycleCallback {
		void onClean();
	}
}
