package com.shanbay.lib.texas.parser;

import androidx.annotation.NonNull;

import com.shanbay.lib.texas.renderer.RenderOption;
import com.shanbay.lib.texas.text.Document;
import com.shanbay.lib.texas.hypher.Hypher;
import com.shanbay.lib.texas.measurer.Measurer;
import com.shanbay.lib.texas.text.TextAttribute;

/**
 * 解析输入数据
 *
 * @param <T> 解析器接受的参数
 */
public interface Parser<T> {

	/**
	 * @param content       内容 {@link com.shanbay.lib.texas.source.Source}
	 * @param measurer      字体测量器
	 * @param hypher        断字
	 * @param textAttribute 行属性
	 * @param renderOption  render option
	 * @return 文档
	 * @throws ParseException       解析错误的时候抛出
	 * @throws InterruptedException 解析时发生线程中断的时候抛出
	 */
	@NonNull
	Document parse(@NonNull T content,
				   Measurer measurer,
				   Hypher hypher,
				   TextAttribute textAttribute,
				   RenderOption renderOption)
			throws ParseException, InterruptedException;
}
