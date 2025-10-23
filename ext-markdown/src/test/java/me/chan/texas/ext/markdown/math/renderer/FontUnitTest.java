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
import java.util.List;

public class FontUnitTest {

	@Test
	public void test() throws IOException {
		File file = new File("./src/main/assets/texas_markdown_ext/font_glyphs.json");
		System.out.println(file.getAbsolutePath());

		Gson gson = new Gson();
		List<Font> fonts = gson.fromJson(new FileReader(file), new TypeToken<List<Font>>() {
		}.getType());
		Assert.assertNotNull(fonts);
		Assert.assertFalse(fonts.isEmpty());

		// 设置命令行的字体
		System.out.println("size: " + fonts.size());


		// GLYPHS.put("alpha", "α");
		StringBuilder stringBuilder = new StringBuilder();
		for (Font font : fonts) {
			stringBuilder.append("\t\t// ").append(font.name).append(" ").append(font.c).append("\n");
			stringBuilder.append("GLYPHS.put(\"").append(font.glyph).append("\", \"\\u").append(font.unicode).append("\");\n");
		}

		File out = new File("./build/glyphs.txt");
		System.out.println(out.getAbsolutePath());
		out.getParentFile().mkdirs();
		FileOutputStream os = new FileOutputStream(out);
		os.write(stringBuilder.toString().getBytes());
		os.close();
	}

	public static class Font {
		public String name;
		public String unicode;
		@SerializedName("char")
		public String c;
		public String glyph;
	}
}
