package me.chan.texas.text;

import android.text.TextPaint;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import java.util.concurrent.atomic.AtomicInteger;


public abstract class TextStyle {

	private static final AtomicInteger UUID = new AtomicInteger(Integer.MIN_VALUE);

	public static final TextStyle NONE = new TextStyle() {
		@Override
		public void update(TextPaint textPaint, @Nullable Object tag) {
			
		}
	};
	public static final TextStyle BOLD = new TextStyle() {

		@Override
		public void update(TextPaint textPaint, @Nullable Object tag) {
			textPaint.setFakeBoldText(true);
		}
	};
	public static final TextStyle ITALIC = new TextStyle() {
		@Override
		public void update(TextPaint textPaint, @Nullable Object tag) {
			textPaint.setTextSkewX(-0.25f);
		}
	};

	public static final TextStyle BOLD_ITALIC = new TextStyle() {
		@Override
		public void update(TextPaint textPaint, @Nullable Object tag) {
			textPaint.setFakeBoldText(true);
			textPaint.setTextSkewX(-0.25f);
		}
	};

	private final int mId;

	public TextStyle() {
		mId = UUID.incrementAndGet();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public int getId() {
		return mId;
	}




	
	@AnyThread
	public abstract void update(@NonNull TextPaint textPaint, @Nullable Object tag);
}
