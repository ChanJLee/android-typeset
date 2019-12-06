package me.chan.texas.typesetter;

import me.chan.texas.text.TextAttribute;
import me.chan.texas.log.Log;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;

public class ParagraphTypesetterImpl implements ParagraphTypesetter {
	private ParagraphTypesetter mTexTypesetter;
	private ParagraphTypesetter mSimpleTypesetter;

	public ParagraphTypesetterImpl() {
		mTexTypesetter = new TexParagraphTypesetter();
		mSimpleTypesetter = new SimpleParagraphTypesetter();
	}

	@Override
	public boolean typeset(Paragraph paragraph, TextAttribute textAttribute, BreakStrategy breakStrategy) {
		if (breakStrategy == BreakStrategy.SIMPLE) {
			return mSimpleTypesetter.typeset(paragraph, textAttribute, breakStrategy);
		}

		if (!mTexTypesetter.typeset(paragraph, textAttribute, breakStrategy)) {
			Log.w("use tex algorithm failed, fallback to simple algorithm");
			// tex 存在找不到完美解的情况，如果在这种case下
			// 回归到朴素的排版算法
			return mSimpleTypesetter.typeset(paragraph, textAttribute, breakStrategy);
		}

		return true;
	}
}
