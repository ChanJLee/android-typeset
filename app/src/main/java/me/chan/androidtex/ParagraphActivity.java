package me.chan.androidtex;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import java.io.IOException;

import me.chan.te.Te;
import me.chan.te.parser.BookParser;
import me.chan.te.parser.TextParser;
import me.chan.te.source.AssetsTextSource;
import me.chan.te.text.BreakStrategy;
import me.chan.te.view.TeView;

public class ParagraphActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paragraph);

		final TeView teView = findViewById(R.id.text);
		teView.setTypeface(Typeface.createFromAsset(getAssets(), "SourceSerifPro-Regular.ttf"));
		findViewById(R.id.debug).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				teView.setDebugMode(!teView.isDebugMode());
			}
		});

		findViewById(R.id.gc).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Te.clean();
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

		teView.setParser(new BookParser(this));
		try {
			teView.setSource(new AssetsTextSource(this, "bay.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}

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
				if (checkedId == R.id.text_size_9) {
					teView.setTextSize(9);
				} else if (checkedId == R.id.text_size_18) {
					teView.setTextSize(18);
				} else if (checkedId == R.id.text_size_27) {
					teView.setTextSize(27);
				} else if (checkedId == R.id.text_size_45) {
					teView.setTextSize(45);
				} else if (checkedId == R.id.text_size_72) {
					teView.setTextSize(72);
				}
			}
		});

		CheckBox checkBox = findViewById(R.id.checkbox);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				teView.setBreakStrategy(isChecked ? BreakStrategy.BALANCED : BreakStrategy.SIMPLE);
			}
		});

		checkBox = findViewById(R.id.checkbox2);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				teView.setTextColor(isChecked ? Color.BLACK : Color.BLUE);
			}
		});
	}

	private void render(final String name, final TeView teView) {
		try {
			teView.setParser(new TextParser());
			teView.setSource(new AssetsTextSource(this, name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
