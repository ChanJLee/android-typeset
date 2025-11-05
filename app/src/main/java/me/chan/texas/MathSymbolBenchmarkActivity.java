package me.chan.texas;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import me.chan.texas.debug.databinding.ActivityMathSymbolBenchmarkBinding;
import me.chan.texas.ext.markdown.math.renderer.SymbolNode;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;
import me.chan.texas.ext.markdown.math.renderer.fonts.SymbolOptions;

public class MathSymbolBenchmarkActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		EdgeToEdge.enable(this);
		ActivityMathSymbolBenchmarkBinding binding = ActivityMathSymbolBenchmarkBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
//		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//			Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//			return insets;
//		});

		String name = "Ps";
		SymbolOptions options = new SymbolOptions();
		Iterator<String> temp = null;
		try {
			Field field = SymbolOptions.class.getDeclaredField(name);
			Map<String, Symbol> map = (Map<String, Symbol>) field.get(options);
			temp = options.all.keySet().iterator();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		Iterator<String> it = temp;
		binding.indicator.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!it.hasNext()) {
					Toast.makeText(v.getContext(), "没有更多了", Toast.LENGTH_SHORT).show();
					return;
				}

				String key = it.next();
				Symbol symbol = options.all.get(key);
				binding.includePadding.setOnCheckedChangeListener(null);
				binding.useBaseline.setOnCheckedChangeListener(null);
				binding.useConstHeight.setOnCheckedChangeListener(null);
				binding.includePadding.setChecked((symbol.flags & Symbol.FLAG_INCLUDE_PADDING) != 0);
				binding.useBaseline.setChecked((symbol.flags & Symbol.FLAG_USE_BASELINE) != 0);
				binding.useConstHeight.setChecked((symbol.flags & Symbol.FLAG_USE_CONST_TEXT_HEIGHT) != 0);
				binding.indicator.setText("name: " + key);
				binding.includePadding.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							symbol.flags = symbol.flags | Symbol.FLAG_INCLUDE_PADDING;
						} else {
							symbol.flags = symbol.flags & (~Symbol.FLAG_INCLUDE_PADDING);
						}
						binding.mathView.setRendererNode(new SymbolNode(1.0f, symbol));
					}
				});
				binding.useBaseline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							symbol.flags = symbol.flags | Symbol.FLAG_USE_BASELINE;
						} else {
							symbol.flags = symbol.flags & (~Symbol.FLAG_USE_BASELINE);
						}
						binding.mathView.setRendererNode(new SymbolNode(1.0f, symbol));
					}
				});
				binding.useConstHeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							symbol.flags = symbol.flags | Symbol.FLAG_USE_CONST_TEXT_HEIGHT;
						} else {
							symbol.flags = symbol.flags & (~Symbol.FLAG_USE_CONST_TEXT_HEIGHT);
						}
						binding.mathView.setRendererNode(new SymbolNode(1.0f, symbol));
					}
				});

				binding.mathView.setRendererNode(new SymbolNode(1.0f, symbol));
			}
		});

		binding.save.setText("保存：" + name);
		binding.save.setOnClickListener(v -> {
			String json = new Gson().toJson(options);
			File dir = getDir("options", Context.MODE_PRIVATE);
			dir.mkdirs();

			File file = new File(dir, name + "_config.json");
			try {
				FileOutputStream os = new FileOutputStream(file);
				os.write(json.getBytes());
				os.flush();
				os.close();
				Log.d("chan_debug", "write to : " + file.getAbsolutePath());
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}
}