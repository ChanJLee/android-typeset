package me.chan.texas.parser;

import androidx.annotation.NonNull;

import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.Document;
import me.chan.texas.hypher.Hypher;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.text.TextAttribute;

/**
 * 解析输入数据
 *
 * @param <T> 解析器接受的参数
 */
public interface Parser<T> {

	/**
	 * @param content       内容 {@link me.chan.texas.source.Source}
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
