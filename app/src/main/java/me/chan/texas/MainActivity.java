package me.chan.texas;

import android.Manifest;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import me.chan.androidtex.R;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.graphics.TexturePicture;
import me.chan.texas.utils.TexasUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private final List<TexturePicture> mPictures = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(me.chan.androidtex.R.layout.activity_main);

		ActivityCompat.requestPermissions(this, new String[]{
				Manifest.permission.INTERNET,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_EXTERNAL_STORAGE,
		}, 1);

		Log.d("chan_debug", ContextCompat.getDrawable(this, me.chan.androidtex.R.drawable.me_chan_te_bg_foot) + "");

		final TextPaint textPaint = new TextPaint();
		TexasUtils.setupTextPaint(textPaint);
		textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
		Log.d("chan_debug", Layout.getDesiredWidth("-", textPaint) + "");
		Log.d("chan_debug", Layout.getDesiredWidth(" ", textPaint) + "");
		Log.d("chan_debug", Layout.getDesiredWidth("qwertyuioplkjhgfdsazxcvbnm-", textPaint) + "");
		Log.d("chan_debug", Layout.getDesiredWidth("qwertyuioplkjhgfdsazxcvbnm", textPaint) + "");
		Log.d("chan_debug", "main thread: " + Thread.currentThread().getId());

		WorkerScheduler.render().setStatsEnable(true);

		findViewById(me.chan.androidtex.R.id.button2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), TexasViewDemoActivity.class);
				startActivity(intent);
			}
		});

		findViewById(me.chan.androidtex.R.id.button3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), SingleParagraphActivity.class);
				startActivity(intent);
			}
		});

		findViewById(me.chan.androidtex.R.id.alloc_picture).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String msg = "Hello World".intern();
				for (int i = 0; i < 1000; ++i) {
					TexturePicture picture = TexturePicture.createPicture();
					Canvas canvas = picture.beginRecording(1080, 540);
					for (int j = 0; j < 1000; ++j) {
						canvas.drawText(msg, 0, 0, textPaint);
					}
					picture.endRecording();
					mPictures.add(picture);
				}
			}
		});

		findViewById(me.chan.androidtex.R.id.free_picture).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (TexturePicture picture : mPictures) {
					TexturePicture.releasePicture(picture);
				}
				mPictures.clear();
			}
		});

		findViewById(me.chan.androidtex.R.id.paragraph_view_demo).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), ParagraphViewDemoActivity.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.indicator).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), LoadingIndicatorActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d("chan_debug", "onResume: " + TexturePicture.getPictureStats() + " " + WorkerScheduler.render().getStats());
	}
}
