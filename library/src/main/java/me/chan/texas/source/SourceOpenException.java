package me.chan.texas.source;

/**
 * 源打开异常
 */
public class SourceOpenException extends Exception {
	private final int mCode;

	/**
	 * @param message 错误msg
	 * @param cause   原因
	 */
	public SourceOpenException(String message, Throwable cause) {
		this(-1, message, cause);
	}

	/**
	 * @param code    错误码
	 * @param message 错误msg
	 * @param cause   原因
	 */
	public SourceOpenException(int code, String message, Throwable cause) {
		super(message, cause);
		mCode = code;
	}

	/**
	 * @return 错误码
	 */
	public int getCode() {
		return mCode;
	}
}
