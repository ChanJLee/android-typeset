package me.chan.androidtex;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;

import com.shanbay.lib.texas.Texas;
import com.shanbay.lib.texas.parser.TextParser;
import com.shanbay.lib.texas.renderer.RenderOption;
import com.shanbay.lib.texas.renderer.Selection;
import com.shanbay.lib.texas.renderer.TexasView;
import com.shanbay.lib.texas.source.AssetsTextSource;

public class ParagraphActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paragraph);

		final TexasView texasView = findViewById(R.id.text);
		findViewById(R.id.debug).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RenderOption renderOption = texasView.createRendererOption();
				renderOption.setEnableDebug(!renderOption.isEnableDebug());
				texasView.refresh(renderOption);
			}
		});

		RenderOption renderOption = texasView.createRendererOption();
		renderOption.setTypeface(Typeface.createFromAsset(getAssets(), "SourceSerifPro-Regular.ttf"));

		findViewById(R.id.gc).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Texas.clean();
			}
		});

		findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				texasView.refresh();
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

		BookParser bookParser = new BookParser(this);
		bookParser.setListener(new BookParser.Listener() {
			@Override
			public void onTextClicked(String text) {
				if (!TextUtils.equals(text, "was")) {
					return;
				}

				Selection selection = texasView.getSelection();
				if (selection == null) {
					return;
				}

				selection.selectByTags(2, 3, 4, 6);
			}
		});
		texasView.setParser(bookParser);
		try {
			texasView.setSource(new AssetsTextSource(this, "bay.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry1.txt", texasView);
			}
		});

		findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry2.txt", texasView);
			}
		});

		findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry3.txt", texasView);
			}
		});

		findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry4.txt", texasView);
			}
		});

		findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry5.txt", texasView);
			}
		});

		findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				render("harry6.txt", texasView);
			}
		});

		RadioGroup radioGroup = findViewById(R.id.radio_group);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				if (checkedId == R.id.text_size_9) {
//					teView.setTextSize(9);
//				} else if (checkedId == R.id.text_size_18) {
//					teView.setTextSize(18);
//				} else if (checkedId == R.id.text_size_27) {
//					teView.setTextSize(27);
//				} else if (checkedId == R.id.text_size_45) {
//					teView.setTextSize(45);
//				} else if (checkedId == R.id.text_size_72) {
//					teView.setTextSize(72);
//				}
			}
		});

		CheckBox checkBox = findViewById(R.id.checkbox);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//teView.setBreakStrategy(isChecked ? BreakStrategy.BALANCED : BreakStrategy.SIMPLE);
			}
		});

		checkBox = findViewById(R.id.checkbox2);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				teView.setTextColor(isChecked ? Color.BLACK : Color.BLUE);
			}
		});
	}

	private void render(final String name, final TexasView texasView) {
		try {
			texasView.setParser(new TextParser());
			texasView.setSource(new AssetsTextSource(this, name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
