package me.chan.androidtex;

import android.graphics.Paint;
import android.graphics.Rect;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shanbay.lib.log.Log;

public class AndroidTestActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_android_test);

		final TextView textView = findViewById(R.id.text);
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Rect rect = new Rect();
				int count = textView.getLineCount();
				for (int i = 0; i < count; ++i) {
					textView.getLineBounds(i, rect);
					Log.d("chan_debug", "index " + i + "height: " + rect.height());
				}

				Paint.FontMetrics fontMetrics = textView.getPaint().getFontMetrics();
				Log.d("chan_debug", (fontMetrics.descent - fontMetrics.ascent) +
						" descent" + fontMetrics.descent + " ascent" + fontMetrics.ascent +
						"top " + fontMetrics.top + " bottom" + fontMetrics.bottom);
			}
		});
	}
}
