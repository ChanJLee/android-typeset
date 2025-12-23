package me.chan.texas.ext.markdown.math.ast;

import me.chan.texas.utils.CharStream;

public class MathParseException extends Exception {
	private final int position;
	private final CharSequence input;

	public MathParseException(String message, CharStream stream) {
		super(message + " at position " + stream.save());
		this.position = stream.save();
		// 从 CharStream 提取原始输入
		this.input = stream.getText();
	}

	public int getPosition() {
		return position;
	}

	/**
	 * 生成格式化的错误信息
	 * 格式：
	 * 1. 错误消息和位置
	 * 2. 完整输入字符串
	 * 3. 用箭头指示错误位置
	 */
	public String pretty() {
		StringBuilder sb = new StringBuilder();

		// 1. 错误消息
		sb.append(getMessage()).append("\n");

		// 2. 显示输入字符串
		int windowSize = 16;
		int start = position - windowSize;
		if (start < 0) {
			start = 0;
		}

		int end = position + windowSize;
		if (end > input.length()) {
			end = input.length();
		}
		sb.append(input, start, end).append("\n");

		// 3. 添加箭头指示错误位置
		for (int i = 0; i < windowSize; i++) {
			sb.append(" ");
		}
		sb.append("^\ninput:");
		sb.append(input).append("\n");

		return sb.toString();
	}

	@Override
	public String toString() {
		return pretty();
	}
}