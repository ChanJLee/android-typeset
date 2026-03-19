package me.chan.texas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ScaleXSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;

import me.chan.texas.debug.R;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.ui.text.ParagraphView;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Span;
import me.chan.texas.text.tokenizer.Token;

public class SingleParagraphActivity extends AppCompatActivity {

	protected int geContentViewId() {
		return me.chan.texas.debug.R.layout.activity_single_paragraph;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(geContentViewId());

		Spannable spannable = new SpannableString("Hello, world!");
		spannable.setSpan(new ScaleXSpan(2.0f) {
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
			}

			@Override
			public void updateMeasureState(TextPaint ds) {
				super.updateMeasureState(ds);
			}
		}, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new ScaleXSpan(2.0f) {
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
			}

			@Override
			public void updateMeasureState(TextPaint ds) {
				super.updateMeasureState(ds);
			}
		}, 7, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		TextView demo = findViewById(R.id.clock);
		demo.setText(spannable);

		// String msg = "\ud83d\ude4b\uff08\u062a\u0645 \u062a\u0643\u0631\u0627\u0631 \u0643\u0644\u0645\u0629 '\u0643\u0648\u0628' \u0628\u0634\u0643\u0644 \u063a\u064a\u0631 \u0636\u0631\u0648\u0631\u064a.\uff09";
		String msg = "\u0067\u0308";
		TextView textView = findViewById(me.chan.texas.debug.R.id.text);
		textView.setText(msg);

		TestView testView = findViewById(me.chan.texas.debug.R.id.test_view);
		testView.setText(msg);

		ParagraphView paragraphView = findViewById(me.chan.texas.debug.R.id.paragraph);
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
			public boolean isSpanClickable(@NonNull Span box) {
				return true;
			}

			@Override
			public boolean applySpanClicked(@NonNull Span clicked, @NonNull Span other) {
				return clicked.getTag() == other.getTag();
			}

			@Override
			public boolean applySpanLongClicked(@NonNull Span clicked, @NonNull Span other) {
				return false;
			}
		});
		paragraphView.setSource(new ParagraphView.ParagraphSource() {
			@Override
			protected Paragraph onRead(TexasOption option) {
				Paragraph.Builder builder = Paragraph.Builder.newBuilder(option);
				builder.stream(msg, 0, msg.length(), token -> {
					Paragraph.SpanStyles span = Paragraph.SpanStyles.obtain(token);
					if (token.getCategory() != Token.CATEGORY_NORMAL) {
						return span;
					}

					return span.tag(new Tag(token.getStart(), token.getEnd()));
				});
				return builder.build();
			}
		});

		findViewById(me.chan.texas.debug.R.id.increase_padding).setOnClickListener(new View.OnClickListener() {
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