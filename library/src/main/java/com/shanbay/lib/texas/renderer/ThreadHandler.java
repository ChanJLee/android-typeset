package com.shanbay.lib.texas.renderer;

import com.shanbay.lib.texas.annotations.Hidden;

/**
 * 线程同步器
 */
@Hidden
interface ThreadHandler {
	/**
	 * 发送消息
	 *
	 * @param what  消息类型
	 * @param value 消息附带的值
	 */
	void sendMessage(int what, Object value);

	/**
	 * 处理消息
	 *
	 * @param what
	 * @param value
	 */
	void handleMessage(int what, Object value);

	/**
	 * 清空消息
	 */
	void clear();
}
