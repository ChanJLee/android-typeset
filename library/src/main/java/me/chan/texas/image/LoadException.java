package me.chan.texas.image;

import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;


@RestrictTo(LIBRARY)
public class LoadException extends Exception {
	public LoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
