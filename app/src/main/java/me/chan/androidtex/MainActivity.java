package me.chan.androidtex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;


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
	}
}
