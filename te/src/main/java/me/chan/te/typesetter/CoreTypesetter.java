package me.chan.te.typesetter;

import android.support.annotation.Nullable;
import android.text.TextPaint;

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
		mSimpleTypesetter = new SimpleTypesetter(paint, option);
	}

	@Nullable
	@Override
	public Paragraph typeset(Segment segment, LineAttributes lineAttributes, Policy policy) {
		if (policy == Typesetter.Policy.FIT) {
			return mSimpleTypesetter.typeset(segment, lineAttributes, policy);
		}

		Paragraph paragraph = mTexTypesetter.typeset(segment, lineAttributes, policy);
		if (paragraph != null) {
			return paragraph;
		}
		return mSimpleTypesetter.typeset(segment, lineAttributes, policy);
	}
}
