package me.chan.androidtex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final TextPaint textPaint = new TextPaint();
		textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
		Log.d("chan_debug", Layout.getDesiredWidth("-", textPaint) + "");
		Log.d("chan_debug", Layout.getDesiredWidth(" ", textPaint) + "");
		Log.d("chan_debug", Layout.getDesiredWidth("qwertyuioplkjhgfdsazxcvbnm-", textPaint) + "");
		Log.d("chan_debug", Layout.getDesiredWidth("qwertyuioplkjhgfdsazxcvbnm", textPaint) + "");


		findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), ParagraphActivity.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), AndroidTestActivity.class);
				startActivity(intent);
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Log.d("chan_debug", "test lower");
					test(95, 122, textPaint);
					Log.d("chan_debug", "test upper");
					test(65, 90, textPaint);
					Log.d("chan_debug", "test chinese");
					test(0x4E00, 0x9FA5, textPaint);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void test(int codePointStart, int codePointEnd, TextPaint textPaint) throws UnsupportedEncodingException {
		StringBuilder stringBuilder = new StringBuilder();
		float prev = 0;
		for (int code = codePointStart; code <= codePointEnd; ++code) {
			String current = String.valueOf((char) code);
			float currentLen = Layout.getDesiredWidth(current, textPaint);
			stringBuilder.append(current);
			float last = Layout.getDesiredWidth(stringBuilder.toString(), textPaint);
			if (last - prev != currentLen) {
				throw new RuntimeException("unexpected char: " + current);
			}
			prev = last;
		}
	}
}
