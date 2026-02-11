package me.chan.texas.ext.image;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.RestrictTo;

/**
 * 加载异常
 */
@RestrictTo(LIBRARY)
public class LoadException extends Exception {
	public LoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
