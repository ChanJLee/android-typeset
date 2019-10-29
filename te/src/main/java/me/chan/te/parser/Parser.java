package me.chan.te.parser;

import android.support.annotation.NonNull;

import java.util.List;

import me.chan.te.config.Option;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;

public interface Parser {

	/**
	 * @param charSequence 内容
	 * @param measurer     字体测量器
	 * @param hypher       断字
	 * @param option       选项
	 * @return
	 */
	@NonNull
	List<Segment> parse(@NonNull CharSequence charSequence, Measurer measurer, Hypher hypher, Option option);
}
