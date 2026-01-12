package me.chan.texas.ext.markdown.math.renderer.fonts;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SymbolOptions {
	private static final String TAG = "SymbolOptions";
	public final Map<String, Symbol> all = new HashMap<>();

	/**
	 * 从 assets 文件夹加载符号配置
	 *
	 * @param context  Android Context
	 * @param fileName JSON 文件名，例如 "STIXTwoMath.json"
	 */
	public void load(Context context, String fileName) {
		try {
			InputStream is = context.getAssets().open(fileName);
			load(is);
		} catch (IOException e) {
			Log.e(TAG, "Failed to load " + fileName + " from assets", e);
		}
	}

	public void load(InputStream is) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder jsonBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonBuilder.append(line);
			}
			reader.close();
			is.close();

			parseJson(jsonBuilder.toString());
		} catch (IOException e) {
			Log.e(TAG, "Failed to load symbol", e);
		}
	}

	/**
	 * 手动解析 JSON 字符串
	 */
	private void parseJson(String json) {
		try {
			// 移除首尾空白和大括号
			json = json.trim();
			if (json.startsWith("{")) {
				json = json.substring(1);
			}
			if (json.endsWith("}")) {
				json = json.substring(0, json.length() - 1);
			}

			// 逐个解析符号
			int index = 0;
			while (index < json.length()) {
				// 跳过空白字符
				while (index < json.length() && isWhitespace(json.charAt(index))) {
					index++;
				}
				if (index >= json.length()) break;

				// 跳过逗号
				if (json.charAt(index) == ',') {
					index++;
					continue;
				}

				// 读取符号名称
				if (json.charAt(index) != '"') break;
				int nameStart = index + 1;
				int nameEnd = json.indexOf('"', nameStart);
				if (nameEnd == -1) break;
				String symbolName = json.substring(nameStart, nameEnd);
				index = nameEnd + 1;

				// 跳过冒号
				while (index < json.length() && (isWhitespace(json.charAt(index)) || json.charAt(index) == ':')) {
					index++;
				}

				// 解析符号对象
				if (index >= json.length() || json.charAt(index) != '{') break;
				int objStart = index;
				int objEnd = findMatchingBrace(json, objStart);
				if (objEnd == -1) break;

				String objJson = json.substring(objStart + 1, objEnd);
				Symbol symbol = parseSymbol(objJson);
				if (symbol != null) {
					all.put(symbolName, symbol);
				}

				index = objEnd + 1;
			}
		} catch (Exception e) {
			Log.e(TAG, "Failed to parse JSON", e);
		}
	}

	/**
	 * 解析单个符号对象
	 */
	private Symbol parseSymbol(String objJson) {
		try {
			String unicode = null;
			float[] bbox = null;

			int index = 0;
			while (index < objJson.length()) {
				// 跳过空白和逗号
				while (index < objJson.length() && (isWhitespace(objJson.charAt(index)) || objJson.charAt(index) == ',')) {
					index++;
				}
				if (index >= objJson.length()) break;

				// 读取字段名
				if (objJson.charAt(index) != '"') break;
				int fieldStart = index + 1;
				int fieldEnd = objJson.indexOf('"', fieldStart);
				if (fieldEnd == -1) break;
				String fieldName = objJson.substring(fieldStart, fieldEnd);
				index = fieldEnd + 1;

				// 跳过冒号
				while (index < objJson.length() && (isWhitespace(objJson.charAt(index)) || objJson.charAt(index) == ':')) {
					index++;
				}

				if (fieldName.equals("char")) {
					// 读取字符串值
					if (objJson.charAt(index) == '"') {
						int valueStart = index + 1;
						int valueEnd = objJson.indexOf('"', valueStart);
						if (valueEnd == -1) break;
						unicode = objJson.substring(valueStart, valueEnd);
						index = valueEnd + 1;
					}
				} else if (fieldName.equals("bbox")) {
					// 读取数组
					if (objJson.charAt(index) == '[') {
						int arrayStart = index + 1;
						int arrayEnd = objJson.indexOf(']', arrayStart);
						if (arrayEnd == -1) break;
						String arrayContent = objJson.substring(arrayStart, arrayEnd);
						bbox = parseFloatArray(arrayContent);
						index = arrayEnd + 1;
					}
				} else {
					// 跳过未知字段
					index = skipValue(objJson, index);
				}
			}

			if (unicode != null && bbox != null && bbox.length == 4) {
				return new Symbol(unicode, bbox);
			}
		} catch (Exception e) {
			Log.e(TAG, "Failed to parse symbol object", e);
		}
		return null;
	}

	/**
	 * 解析浮点数数组
	 */
	private float[] parseFloatArray(String arrayContent) {
		String[] parts = arrayContent.split(",");
		float[] result = new float[parts.length];
		for (int i = 0; i < parts.length; i++) {
			result[i] = Float.parseFloat(parts[i].trim());
		}
		return result;
	}

	/**
	 * 查找匹配的大括号
	 */
	private int findMatchingBrace(String json, int start) {
		int depth = 1;
		int index = start + 1;
		while (index < json.length() && depth > 0) {
			char c = json.charAt(index);
			if (c == '{') {
				depth++;
			} else if (c == '}') {
				depth--;
			} else if (c == '"') {
				// 跳过字符串内容
				index++;
				while (index < json.length() && json.charAt(index) != '"') {
					if (json.charAt(index) == '\\') {
						index++; // 跳过转义字符
					}
					index++;
				}
			}
			index++;
		}
		return depth == 0 ? index - 1 : -1;
	}

	/**
	 * 跳过 JSON 值
	 */
	private int skipValue(String json, int start) {
		while (start < json.length() && isWhitespace(json.charAt(start))) {
			start++;
		}
		if (start >= json.length()) return start;

		char c = json.charAt(start);
		if (c == '"') {
			// 字符串
			start++;
			while (start < json.length() && json.charAt(start) != '"') {
				if (json.charAt(start) == '\\') {
					start++;
				}
				start++;
			}
			return start + 1;
		} else if (c == '[') {
			// 数组
			int depth = 1;
			start++;
			while (start < json.length() && depth > 0) {
				if (json.charAt(start) == '[') depth++;
				else if (json.charAt(start) == ']') depth--;
				else if (json.charAt(start) == '"') {
					start++;
					while (start < json.length() && json.charAt(start) != '"') {
						if (json.charAt(start) == '\\') start++;
						start++;
					}
				}
				start++;
			}
			return start;
		} else if (c == '{') {
			// 对象
			return findMatchingBrace(json, start) + 1;
		} else {
			// 数字或布尔值
			while (start < json.length() && !isWhitespace(json.charAt(start))
					&& json.charAt(start) != ',' && json.charAt(start) != '}' && json.charAt(start) != ']') {
				start++;
			}
			return start;
		}
	}

	/**
	 * 判断是否为空白字符
	 */
	private boolean isWhitespace(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}
}