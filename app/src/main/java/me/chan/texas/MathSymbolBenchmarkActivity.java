package me.chan.texas;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
		ActivityMathSymbolBenchmarkBinding binding = ActivityMathSymbolBenchmarkBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		String name = "Pd";
		SymbolOptions options = new SymbolOptions();
		Iterator<String> temp = null;
		try {
			Field field = SymbolOptions.class.getDeclaredField(name);
			Map<String, Symbol> map = (Map<String, Symbol>) field.get(options);
			temp = map.keySet().iterator();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		Iterator<String> it = temp;
		binding.indicator.setOnClickListener(v -> {
			if (!it.hasNext()) {
				Toast.makeText(v.getContext(), "没有更多了", Toast.LENGTH_SHORT).show();
				return;
			}

			String key = it.next();
			Symbol symbol = options.all.get(key);
//			renderSymbol(binding, key, symbol);
		});
	}

//	private void renderSymbol(ActivityMathSymbolBenchmarkBinding binding, String name, Symbol symbol) {
//		List<RendererNode> list = new ArrayList<>();
//		list.add(new TextNode(1f, "fg"));
//		list.add(new SymbolNode(1f, symbol));
//		list.add(new TextNode(1f, "fg"));
//		binding.indicator.setText("当前： " + name);
//		binding.mathView.setRendererNode(new LinearGroupNode(1F, list, LinearGroupNode.Gravity.HORIZONTAL));
//	}
}