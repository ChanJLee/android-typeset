package com.shanbay.lib.texas.image;

import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 加载异常
 */
@RestrictTo(LIBRARY)
public class LoadException extends Exception {
	public LoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
