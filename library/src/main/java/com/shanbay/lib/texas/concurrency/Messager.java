package com.shanbay.lib.texas.concurrency;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.RestrictTo;

/**
 * 线程同步器
 */
@RestrictTo(LIBRARY)
public abstract class Messager {

	protected HandleListener mListener;

	public void setListener(HandleListener listener) {
		mListener = listener;
	}

	/**
	 * 发送消息
	 *
	 * @param what  消息类型
	 * @param value 消息附带的值
	 */
	public abstract void send(int what, Object value);

	/**
	 * 清空消息
	 */
	public abstract void clear();

	public interface HandleListener {
		/**
		 * 处理消息
		 *
		 * @param what
		 * @param value
		 */
		void handleMessage(int what, Object value);
	}
}
