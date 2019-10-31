package me.chan.te.parser;

import android.support.annotation.NonNull;

import me.chan.te.config.Option;
import me.chan.te.text.Document;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;

public interface Parser {

	/**
	 * @param charSequence 内容
	 * @param measurer     字体测量器
	 * @param hypher       断字
	 * @param option       选项
	 * @return 文档
	 * @throws ParseException 解析错误的时候抛出
	 */
	@NonNull
	Document parse(@NonNull CharSequence charSequence, Measurer measurer, Hypher hypher, Option option) throws ParseException;
}
