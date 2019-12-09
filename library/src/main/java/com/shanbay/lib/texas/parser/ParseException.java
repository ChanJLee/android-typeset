package com.shanbay.lib.texas.parser;

/**
 * 解析异常
 */
public class ParseException extends RuntimeException {
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
