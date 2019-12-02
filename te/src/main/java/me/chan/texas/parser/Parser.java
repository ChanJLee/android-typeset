package me.chan.texas.parser;

import android.support.annotation.NonNull;

import me.chan.texas.text.Document;
import me.chan.texas.hypher.Hypher;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.text.TextAttribute;

public interface Parser<T> {

	/**
	 * @param content       内容
	 * @param measurer      字体测量器
	 * @param hypher        断字
	 * @param textAttribute 行属性
	 * @return 文档
	 * @throws ParseException 解析错误的时候抛出
	 */
	@NonNull
	Document parse(@NonNull T content, Measurer measurer, Hypher hypher, TextAttribute textAttribute)
			throws ParseException, InterruptedException;
}
