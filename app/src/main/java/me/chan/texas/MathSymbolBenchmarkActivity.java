package me.chan.texas;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.chan.texas.debug.R;
import me.chan.texas.debug.databinding.ActivityMathSymbolBenchmarkBinding;
import me.chan.texas.ext.markdown.math.renderer.LinearGroupNode;
import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.SymbolNode;
import me.chan.texas.ext.markdown.math.renderer.TextNode;
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
				binding.includeTopPadding.setOnCheckedChangeListener(null);
				binding.includeBottomPadding.setOnCheckedChangeListener(null);
				binding.height.setOnCheckedChangeListener(null);
				binding.includeTopPadding.setChecked((symbol.flags & Symbol.FLAG_TOP_PADDING) != 0);
				binding.includeBottomPadding.setChecked((symbol.flags & Symbol.FLAG_BOTTOM_PADDING) != 0);
				if ((symbol.flags & Symbol.FLAG_USE_CONST_TEXT_HEIGHT_LARGE) != 0) {
					binding.height.check(me.chan.texas.debug.R.id.large);
				} else if ((symbol.flags & Symbol.FLAG_USE_CONST_TEXT_HEIGHT_SMALL) != 0) {
					binding.height.check(me.chan.texas.debug.R.id.small);
				} else {
					binding.height.clearCheck();
				}
				binding.indicator.setText("name: " + key);
				binding.includeTopPadding.setOnCheckedChangeListener((buttonView, isChecked) -> {
					if (isChecked) {
						symbol.flags = symbol.flags | Symbol.FLAG_TOP_PADDING;
					} else {
						symbol.flags = symbol.flags & (~Symbol.FLAG_TOP_PADDING);
					}
					renderSymbol(binding, symbol);
				});

				binding.includeBottomPadding.setOnCheckedChangeListener((buttonView, isChecked) -> {
					if (isChecked) {
						symbol.flags = symbol.flags | Symbol.FLAG_BOTTOM_PADDING;
					} else {
						symbol.flags = symbol.flags & (~Symbol.FLAG_BOTTOM_PADDING);
					}
					renderSymbol(binding, symbol);
				});
				binding.height.setOnCheckedChangeListener((group, checkedId) -> {
					if (checkedId == R.id.large) {
						selectedLarge(binding, symbol);
					} else {
						selectedSmall(binding, symbol);
					}
				});

				renderSymbol(binding, symbol);
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
				Toast.makeText(v.getContext(), "保存成功", Toast.LENGTH_SHORT).show();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}

	private void selectedLarge(ActivityMathSymbolBenchmarkBinding binding, Symbol symbol) {
		symbol.flags = symbol.flags & (~Symbol.FLAG_USE_CONST_TEXT_HEIGHT_SMALL);
		symbol.flags = symbol.flags | Symbol.FLAG_USE_CONST_TEXT_HEIGHT_LARGE;
		renderSymbol(binding, symbol);
	}

	private void selectedSmall(ActivityMathSymbolBenchmarkBinding binding, Symbol symbol) {
		symbol.flags = symbol.flags & (~Symbol.FLAG_USE_CONST_TEXT_HEIGHT_LARGE);
		symbol.flags = symbol.flags | Symbol.FLAG_USE_CONST_TEXT_HEIGHT_SMALL;
		binding.mathView.setRendererNode(new SymbolNode(1.0f, symbol));
	}

	private void renderSymbol(ActivityMathSymbolBenchmarkBinding binding, Symbol symbol) {
		List<RendererNode> list = new ArrayList<>();
		list.add(new TextNode(1f, "fg"));
		list.add(new SymbolNode(1f, symbol));
		list.add(new TextNode(1f, "fg"));
		binding.mathView.setRendererNode(new LinearGroupNode(1F, list, LinearGroupNode.Gravity.HORIZONTAL));
	}
}