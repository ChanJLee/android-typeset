package me.chan.androidtex;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

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

		ActivityCompat.requestPermissions(this, new String[]{
				Manifest.permission.INTERNET,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_EXTERNAL_STORAGE,
		}, 1);

		Log.d("chan_debug", ContextCompat.getDrawable(this, R.drawable.me_chan_te_bg_foot) + "");

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
