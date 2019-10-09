package me.chan.androidtex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import me.chan.te.TeView;

public class ParagraphActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paragraph);

		final TeView teView = findViewById(R.id.text);
		findViewById(R.id.debug).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				teView.setDebugMode(!teView.isDebugMode());
			}
		});

		final View container = findViewById(R.id.option_container);
		findViewById(R.id.option).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (container.getVisibility() == View.VISIBLE) {
					container.setVisibility(View.GONE);
				} else {
					container.setVisibility(View.VISIBLE);
				}
			}
		});

		render("IAmLegend.txt", teView);

		findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("TheBookAndTheSword.txt", teView);
			}
		});

		findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("IAmLegend.txt", teView);
			}
		});

		RadioGroup radioGroup = findViewById(R.id.radio_group);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO
			}
		});
	}

	private void render(final String name, final TeView teView) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				final StringBuilder stringBuilder = new StringBuilder();
				try {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(getAssets().open(name))
					);
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line)
								.append("\n");
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							teView.setText(stringBuilder.toString());
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
