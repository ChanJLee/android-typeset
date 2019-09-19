package me.chan.androidtex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import me.chan.te.TexView;

public class ParagraphActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paragraph);

		final TexView texView = findViewById(R.id.text);

		new Thread(new Runnable() {
			@Override
			public void run() {

				final StringBuilder stringBuilder = new StringBuilder();
				try {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(getAssets().open("test.txt"))
					);
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line)
						.append("\n");
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							texView.setText(stringBuilder.toString());
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
