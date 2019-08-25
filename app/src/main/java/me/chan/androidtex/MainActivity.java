package me.chan.androidtex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.PrecomputedText;
import android.widget.TextView;

import me.chan.typeset.TypesetView;

public class MainActivity extends AppCompatActivity {

	private TypesetView mTypesetView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final String content = getResources().getString(R.string.test);
		mTypesetView = findViewById(R.id.view);
		mTypesetView.setText(content);

		final TextView textView = findViewById(R.id.text);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					final PrecomputedText.Params params = new PrecomputedText.Params.Builder(textView.getPaint())
							.setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY)
							.build();
					final PrecomputedText precomputedText = PrecomputedText.create(content, params);
					textView.post(new Runnable() {
						@Override
						public void run() {
							textView.setTextMetricsParams(params);
							textView.setText(precomputedText);
						}
					});
				}
			}).start();
		} else {
			textView.setText(content);
		}
	}

	@Override
	protected void onDestroy() {
		if (mTypesetView != null) {
			mTypesetView.release();
		}
		super.onDestroy();
	}
}
