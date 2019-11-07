package me.chan.te.typesetter;

import me.chan.te.config.LineAttributes;
import me.chan.te.log.Log;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Paragraph;

public class ParagraphTypesetterImpl implements ParagraphTypesetter {
	private ParagraphTypesetter mTexTypesetter;
	private ParagraphTypesetter mSimpleTypesetter;

	public ParagraphTypesetterImpl() {
		mTexTypesetter = new TexParagraphTypesetter();
		mSimpleTypesetter = new SimpleParagraphTypesetter();
	}

	@Override
	public boolean typeset(Paragraph paragraph, LineAttributes lineAttributes, BreakStrategy breakStrategy) {
		if (breakStrategy == BreakStrategy.SIMPLE) {
			return mSimpleTypesetter.typeset(paragraph, lineAttributes, breakStrategy);
		}

		if (!mTexTypesetter.typeset(paragraph, lineAttributes, breakStrategy)) {
			Log.w("use tex algorithm failed, fallback to simple algorithm");
			// tex 存在找不到完美解的情况，如果在这种case下
			// 回归到朴素的排版算法
			return mSimpleTypesetter.typeset(paragraph, lineAttributes, breakStrategy);
		}

		return true;
	}
}
