package me.chan.te.typesetter;

import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.text.BreakStrategy;
import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;

public class CoreTypesetter implements Typesetter {
	private Typesetter mTexTypesetter;
	private Typesetter mSimpleTypesetter;

	public CoreTypesetter(TextPaint paint, Option option, ElementFactory elementFactory) {
		mTexTypesetter = new TexTypesetter(paint, option, elementFactory);
		mSimpleTypesetter = new SimpleTypesetter(paint, option, elementFactory);
	}

	@Nullable
	@Override
	public Paragraph typeset(Segment segment, LineAttributes lineAttributes, BreakStrategy breakStrategy) {
		if (breakStrategy == BreakStrategy.SIMPLE) {
			return mSimpleTypesetter.typeset(segment, lineAttributes, breakStrategy);
		}

		Paragraph paragraph = mTexTypesetter.typeset(segment, lineAttributes, breakStrategy);
		if (paragraph != null) {
			return paragraph;
		}

		// tex 存在找不到完美解的情况，如果在这种case下
		// 回归到朴素的排版算法
		return mSimpleTypesetter.typeset(segment, lineAttributes, breakStrategy);
	}
}
