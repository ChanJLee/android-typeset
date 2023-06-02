package me.chan.androidtex;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;

import com.shanbay.lib.texas.TexasOption;
import com.shanbay.lib.texas.renderer.OnSpanClickedPredicate;
import com.shanbay.lib.texas.renderer.ui.text.ParagraphView;
import com.shanbay.lib.texas.source.SourceCloseException;
import com.shanbay.lib.texas.text.Paragraph;

import java.lang.reflect.Method;

public class SingleParagraphActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_paragraph);

		String msg = "It was nearly midnight, and he was lying on his stomach in bed, the blankets drawn right over his head like a tent, a flashlight in one hand and a large leather-bound book (A History of Magic by Bathilda Bagshot) propped open against the pillow. Harry moved the tip of his eagle-feather quill down the page, frowning as he looked for something that would help him write his essay, \"Witch Burning in the Fourteenth Century Was Completely Pointless discuss.";
		TextView textView = findViewById(R.id.text);
		textView.setText(msg);

		ParagraphView paragraphView = findViewById(R.id.paragraph);
		paragraphView.setOnClickedListener(new ParagraphView.OnClickedListener() {
			@Override
			public void onSpanClicked(ParagraphView paragraphView, Object tag) {
				Toast.makeText(SingleParagraphActivity.this, "onSpanClicked", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSpanLongClicked(ParagraphView paragraphView, Object tag) {
				Toast.makeText(SingleParagraphActivity.this, "onSpanLongClicked", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onEmptyClicked(ParagraphView paragraphView) {
				Toast.makeText(SingleParagraphActivity.this, "onEmptyClicked", Toast.LENGTH_SHORT).show();
			}
		});
		paragraphView.setOnSpanClickedPredicate(new OnSpanClickedPredicate() {
			@Override
			public boolean apply(@Nullable Object clickedTag, @Nullable Object tag) {
				return clickedTag == tag;
			}
		});
		paragraphView.setSource(new ParagraphView.ParagraphSource() {
			@Override
			protected Paragraph open(TexasOption option) {
				Paragraph.Builder builder = Paragraph.Builder.newBuilder(option);
				builder.stream(msg, 0, msg.length(), new Paragraph.Builder.SpanReader() {
					@Override
					public Paragraph.Span read(CharSequence text, int start, int end) {
						Paragraph.Span span = Paragraph.Span.obtain(text, start, end);
						return span.tag(new Tag(start, end));
					}
				});
				return builder.build();
			}

			@Override
			public void close() throws SourceCloseException {

			}
		});

		findViewById(R.id.increase_padding).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				paragraphView.setPadding(paragraphView.getPaddingLeft() + 10, paragraphView.getPaddingTop(), paragraphView.getPaddingRight() + 10, paragraphView.getPaddingBottom());


				v.post(new Runnable() {
					@Override
					public void run() {
						log(v);
					}
				});
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