package me.chan.texas.ext.markdown.math.ast;

import me.chan.texas.utils.CharStream;

public class MathParseException extends Exception {
	private int position;
	private CharStream stream;
	private String input;
	private String originalMessage;

	public MathParseException(String message, int position) {
		super(message + " at position " + position);
		this.position = position;
		this.originalMessage = message;
	}

	public MathParseException(String message, CharStream stream) {
		super(message + " at position " + stream.save());
		this.position = stream.save();
		this.stream = stream;
		// 尝试从 CharStream 获取原始输入
		this.input = extractInputFromStream(stream);
		this.originalMessage = message;
	}

	public int getPosition() {
		return position;
	}

	/**
	 * 从 CharStream 中提取原始输入字符串
	 */
	private String extractInputFromStream(CharStream stream) {
		try {
			// 假设 CharStream 有某种方法可以获取原始输入
			// 如果没有，可能需要在解析器中额外传入
			// 这里提供一个备选实现
			return null;  // 需要根据 CharStream 的实际 API 调整
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 设置原始输入字符串（如果 CharStream 无法直接提供）
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * 生成格式化的错误信息
	 *
	 * @return 包含错误位置和上下文的格式化字符串
	 */
	public String pretty() {
		if (input == null || input.isEmpty()) {
			return originalMessage + " at position " + position;
		}

		StringBuilder sb = new StringBuilder();

		// 1. 错误消息
		sb.append(originalMessage).append(" at position ").append(position).append("\n");

		// 2. 显示输入字符串
		sb.append(input).append("\n");

		// 3. 添加箭头指示错误位置
		// 注意：需要处理 position 可能超出字符串长度的情况
		int arrowPos = Math.min(position, input.length());
		for (int i = 0; i < arrowPos; i++) {
			sb.append(" ");
		}
		sb.append("^");

		return sb.toString();
	}

	/**
	 * 生成带上下文窗口的格式化错误信息
	 *
	 * @param contextSize 错误位置前后显示的字符数
	 * @return 格式化的错误信息
	 */
	public String prettyWithContext(int contextSize) {
		if (input == null || input.isEmpty()) {
			return originalMessage + " at position " + position;
		}

		StringBuilder sb = new StringBuilder();

		// 1. 错误消息
		sb.append(originalMessage).append(" at position ").append(position).append("\n");

		// 计算显示窗口
		int start = Math.max(0, position - contextSize);
		int end = Math.min(input.length(), position + contextSize);

		// 2. 显示上下文片段
		String contextStr = input.substring(start, end);

		// 如果不是从开头开始，添加省略号
		if (start > 0) {
			sb.append("...");
		}
		sb.append(contextStr);
		if (end < input.length()) {
			sb.append("...");
		}
		sb.append("\n");

		// 3. 添加箭头指示
		int arrowOffset = position - start;
		if (start > 0) {
			arrowOffset += 3; // "..." 的长度
		}
		for (int i = 0; i < arrowOffset; i++) {
			sb.append(" ");
		}
		sb.append("^");

		return sb.toString();
	}

	@Override
	public String toString() {
		return pretty();
	}
}