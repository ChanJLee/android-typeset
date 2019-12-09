package com.shanbay.lib.texas.image;

import com.shanbay.lib.texas.annotations.Hidden;

/**
 * 加载异常
 */
@Hidden
public class LoadException extends Exception {
	public LoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
