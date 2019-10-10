package me.chan.te.parser;

import android.support.annotation.NonNull;

import java.util.List;

import me.chan.te.config.Option;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;

public interface Parser {

	/**
	 * @param charSequence   内容
	 * @param elementFactory element factory
	 * @param hypher         断字
	 * @param option         选项
	 * @return
	 */
	@NonNull
	List<Segment> parser(@NonNull CharSequence charSequence, ElementFactory elementFactory, Hypher hypher, Option option);
}
