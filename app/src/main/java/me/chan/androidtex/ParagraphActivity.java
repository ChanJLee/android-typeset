package me.chan.androidtex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

import java.io.IOException;

import me.chan.te.source.AssertSource;
import me.chan.te.view.TeView;

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
		try {
			teView.setSource(new AssertSource(this, name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
