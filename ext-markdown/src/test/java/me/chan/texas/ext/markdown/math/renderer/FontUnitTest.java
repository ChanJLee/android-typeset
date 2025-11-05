package me.chan.texas.ext.markdown.math.renderer;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;

public class FontUnitTest {

	@Test
	public void test() throws IOException {
		File file = new File("./font_list.json");
		System.out.println(file.getAbsolutePath());

		Gson gson = new Gson();
		List<Font> fonts = gson.fromJson(new FileReader(file), new TypeToken<List<Font>>() {
		}.getType());
		Assert.assertNotNull(fonts);
		Assert.assertFalse(fonts.isEmpty());

		// 设置命令行的字体
		System.out.println("size: " + fonts.size());

		Map<String, List<Font>> category = new HashMap<>();


		// GLYPHS.put("alpha", "α");
		for (Font font : fonts) {
			List<Font> list = category.get(font.category);
			if (list == null) {
				list = new ArrayList<>();
				category.put(font.category, list);
			}
			list.add(font);
		}

		StringBuilder stringBuilder = new StringBuilder("package me.chan.texas.ext.markdown.math.renderer.fonts;\n\n")
				.append("import java.util.HashMap;\n")
				.append("import java.util.Map;\n\n")
				.append("// AUTO GEN BY TOOLS, DO NOT MODIFY IT!\n")
				.append("public class SymbolOptions {\n");

		List<Map.Entry<String, List<Font>>> entries = new ArrayList<>();
		for (Map.Entry<String, List<Font>> entry : category.entrySet()) {
			String key = entry.getKey();
			if (!key.startsWith("P") && !key.startsWith("S")) {
				continue;
			}

			stringBuilder.append("\tpublic final Map<String, Symbol> ")
					.append(key)
					.append(" = new HashMap<>();\n");
			entries.add(entry);
		}

		stringBuilder.append("\tpublic final Map<String, Symbol> ")
				.append("all")
				.append(" = new HashMap<>();\n");

		System.out.println(">>> category count: " + category.size());
		stringBuilder.append("\t").append("public SymbolOptions() {\n");
		for (Map.Entry<String, List<Font>> entry : entries) {
			String key = entry.getKey();
			if (!key.startsWith("P") && !key.startsWith("S")) {
				continue;
			}

			System.out.println("category: " + entry.getKey() + " -> " + entry.getValue().size());
			for (Font font : entry.getValue()) {
				stringBuilder.append("\t\t// ").append(font.name).append(" ").append(font.c).append("\n");
				stringBuilder
						.append("\t\t")
						.append(entry.getKey())
						.append(".put(\"").append(font.glyph).append("\", new Symbol(\"");
				String v = font.unicode;
				if ("\"".equals(font.c) || "\\".equals(font.c)) {
					stringBuilder.append("\\").append(font.c);
				} else {
					stringBuilder.append("\\u").append(v);
				}
				stringBuilder.append("\"));\n");
			}
			stringBuilder.append("\n\t\tall.putAll(")
					.append(key).append(");\n");
			stringBuilder.append("\n\n");
		}

		stringBuilder.append("\t}\n");

		stringBuilder.append("}");
		File out = new File("./build/glyphs.txt");
		System.out.println(out.getAbsolutePath());
		out.getParentFile().mkdirs();
		FileOutputStream os = new FileOutputStream(out);
		os.write(stringBuilder.toString().getBytes());
		os.close();
	}

	@Test
	public void testCodepoints() {
		Map<String, String> v = MathFontOptions.toMap();
		for (Map.Entry<String, String> entry : v.entrySet()) {
			print(entry.getKey(), entry.getValue());
		}

		print("emoji", "😂");
	}

	private void print(String key, String value) {
		if (value.length() > 1) {
			System.out.println(">>>>>>>> " + key + "[ " + value.length());
			for (int i = 0; i < value.length(); ++i) {
				char c = value.charAt(i);
				int p = value.codePointAt(0);
				System.out.println(c + "-" + p + " " + Character.isHighSurrogate(c));
			}
		}
	}

	public static class Font {
		public String name;
		public String unicode;
		@SerializedName("char")
		public String c;
		public String glyph;
		public String category;
	}
}
