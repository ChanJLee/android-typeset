package me.chan.androidtex;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;

import me.chan.texas.TexasOption;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.ui.text.ParagraphView;
import me.chan.texas.source.SourceCloseException;
import me.chan.texas.text.Paragraph;

public class SingleParagraphActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_paragraph);

		// String msg = "\ud83d\ude4b\uff08\u062a\u0645 \u062a\u0643\u0631\u0627\u0631 \u0643\u0644\u0645\u0629 '\u0643\u0648\u0628' \u0628\u0634\u0643\u0644 \u063a\u064a\u0631 \u0636\u0631\u0648\u0631\u064a.\uff09";
		String msg = "\u0067\u0308";
		TextView textView = findViewById(R.id.text);
		textView.setText(msg);

		TestView testView = findViewById(R.id.test_view);
		testView.setText(msg);

		ParagraphView paragraphView = findViewById(R.id.paragraph);
		paragraphView.setOnClickedListener(new ParagraphView.OnClickedListener() {
			@Override
			public void onSpanClicked(ParagraphView paragraphView, TouchEvent event, Object tag) {
				Toast.makeText(SingleParagraphActivity.this, "onSpanClicked", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSpanLongClicked(ParagraphView paragraphView, TouchEvent event, Object tag) {
				Toast.makeText(SingleParagraphActivity.this, "onSpanLongClicked", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onEmptyClicked(ParagraphView paragraphView, TouchEvent event) {
				Toast.makeText(SingleParagraphActivity.this, "onEmptyClicked", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDoubleClicked(ParagraphView paragraphView, TouchEvent event) {
				Toast.makeText(SingleParagraphActivity.this, "onDoubleClicked", Toast.LENGTH_SHORT).show();
			}
		});
		paragraphView.setSpanTouchEventHandler(new SpanTouchEventHandler() {
			@Override
			public boolean isSpanClickable(@Nullable Object tag) {
				return true;
			}

			@Override
			public boolean applySpanClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
				return clickedTag == otherTag;
			}

			@Override
			public boolean applySpanLongClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
				return false;
			}
		});
		paragraphView.setSource(new ParagraphView.ParagraphSource() {
			@Override
			protected Paragraph onOpen(TexasOption option) {
				Paragraph.Builder builder = Paragraph.Builder.newBuilder(option);
				builder.stream(msg, 0, msg.length(), (text, start, end) -> {
					Paragraph.Span span = Paragraph.Span.obtain(text, start, end);
					return span.tag(new Tag(start, end));
				});
				return builder.build();
			}

			@Override
			protected void onClose() throws SourceCloseException {

			}
		});

		findViewById(R.id.increase_padding).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				paragraphView.setPadding(paragraphView.getPaddingLeft() + 10, paragraphView.getPaddingTop(), paragraphView.getPaddingRight() + 10, paragraphView.getPaddingBottom());


				v.post(() -> log(v));
			}
		});
	}

	private static class Tag {
		private int start;
		private int end;

		public Tag(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}

	private void log(View view) {
		try {
			Class<?> clazz = Class.forName("android.view.ViewRootImpl");

			ViewParent parent = view.getParent();
			while (!clazz.isInstance(parent)) {
				parent = parent.getParent();
			}

			@SuppressLint("SoonBlockedPrivateApi") Method getWidth = clazz.getDeclaredMethod("getWidth");
			@SuppressLint("SoonBlockedPrivateApi") Method getHeight = clazz.getDeclaredMethod("getHeight");

			getWidth.setAccessible(true);
			getHeight.setAccessible(true);

			Log.d("chan_debug", "width: " + getWidth.invoke(parent) + ", height: " + getHeight.invoke(parent));
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
}