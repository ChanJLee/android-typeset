package me.chan.te.parser;

import android.support.annotation.NonNull;

import me.chan.te.text.Document;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;
import me.chan.te.text.TextAttribute;

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
