package me.chan.texas.ext.markdown.math.renderer.fonts;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolOptions {
	public final Map<String, Symbol> all;

	public SymbolOptions() {
		InputStream is = SymbolOptions.class.getResourceAsStream("me/chan/texas/ext/markdown/math/renderer/fonts/STIXTwoMath.json");
		if (is == null) {
			throw new RuntimeException("无法找到 STIXTwoMath.json 资源文件");
		}
		try {
			InputStreamReader reader = new InputStreamReader(is);
			all = parseJson(reader);
			reader.close();
		} catch (Throwable throwable) {
			throw new RuntimeException("加载 STIXTwoMath.json 失败", throwable);
		}
	}

	/**
	 * 从 assets 文件夹加载符号配置
	 *
	 * @param context  Android Context
	 * @param fileName JSON 文件名，例如 "STIXTwoMath.json"
	 */
	public SymbolOptions(Context context, String fileName) throws IOException {
		this(context.getAssets().open(fileName));
	}

	/**
	 * 从输入流加载符号配置
	 *
	 * @param is 输入流
	 */
	public SymbolOptions(InputStream is) {
		try {
			InputStreamReader reader = new InputStreamReader(is);
			all = parseJson(reader);
			reader.close();
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	/**
	 * 使用 Gson 解析 JSON
	 */
	private Map<String, Symbol> parseJson(InputStreamReader is) {
		Gson gson = new Gson();
		Type type = new TypeToken<List<Symbol>>() {
		}.getType();
		List<Symbol> list = gson.fromJson(is, type);
		Map<String, Symbol> map = new HashMap<>();
		for (Symbol symbol : list) {
			map.put(symbol.c, symbol);
		}
		return map;
	}
}