package me.chan.te.parser;

import android.support.annotation.NonNull;

import java.util.List;

import me.chan.te.data.ElementFactory;
import me.chan.te.data.Segment;

public interface Parser {

	@NonNull
	List<Segment> parser(@NonNull CharSequence charSequence, ElementFactory elementFactory);
}
