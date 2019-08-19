package me.chan.androidtex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import me.chan.typeset.TypesetView;

public class MainActivity extends AppCompatActivity {

	private TypesetView mTypesetView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String content = "As with you, so also with us, there are four points of the compass North, South, East, and West.";
		mTypesetView = findViewById(R.id.view);
		mTypesetView.setText(content);

		TextView textView = findViewById(R.id.text);
		textView.setText(content);
	}

	@Override
	protected void onDestroy() {
		if (mTypesetView != null) {
			mTypesetView.release();
		}
		super.onDestroy();
	}
}
