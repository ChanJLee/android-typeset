package me.chan.texas;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;

import me.chan.texas.debug.R;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.ui.text.ParagraphView;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.tokenizer.Token;

public class SingleParagraphWithHeightActivity extends Activity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_paragraph_with_height);

		ParagraphView paragraphView = findViewById(R.id.paragraph_view);
		findViewById(R.id.change_padding).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				paragraphView.setPadding(paragraphView.getPaddingLeft(), paragraphView.getPaddingTop() + 50, paragraphView.getPaddingRight(), paragraphView.getPaddingBottom());
			}
		});
	}
}