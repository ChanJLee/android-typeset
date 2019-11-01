package me.chan.te.typesetter;

import android.support.annotation.Nullable;

import me.chan.te.config.LineAttributes;
import me.chan.te.text.Paragraph;
import me.chan.te.log.Log;
import me.chan.te.text.BreakStrategy;

public class CoreTypesetter extends Typesetter {
	private Typesetter mTexTypesetter;
	private Typesetter mSimpleTypesetter;

	public CoreTypesetter() {
		mTexTypesetter = new TexTypesetter();
		mSimpleTypesetter = new SimpleTypesetter();
	}

	@Nullable
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
