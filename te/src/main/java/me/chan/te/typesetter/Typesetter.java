package me.chan.te.typesetter;

import me.chan.te.config.LineAttributes;
import me.chan.te.log.Log;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Figure;
import me.chan.te.text.Paragraph;
import me.chan.te.text.Segment;

public class Typesetter {
	private ParagraphTypesetter mTexTypesetter;
	private ParagraphTypesetter mSimpleTypesetter;

	public Typesetter() {
		mTexTypesetter = new TexTypesetter();
		mSimpleTypesetter = new SimpleTypesetter();
	}

	/**
	 * @param segment        segment
	 * @param lineAttributes 行信息
	 * @param breakStrategy  {@link BreakStrategy}
	 * @return 是否排版成功
	 */
	public final boolean typeset(Segment segment, LineAttributes lineAttributes, BreakStrategy breakStrategy) {
		if (segment instanceof Paragraph) {
			return typeset((Paragraph) segment, lineAttributes, breakStrategy);
		}

		if (segment instanceof Figure) {
			return typeset((Figure) segment, lineAttributes);
		}

		return false;
	}

	protected boolean typeset(Figure figure, LineAttributes lineAttributes) {
		LineAttributes.Attribute attribute = lineAttributes.getDefaultAttribute();

		float lineWidth = attribute.getLineWidth();

		float width = figure.getWidth();
		float height = figure.getHeight();

		if (width >= 0 && height >= 0) {
			if (width > lineWidth) {
				figure.setWidth(lineWidth);
				figure.setHeight(height / width * lineWidth);
			}
			return true;
		}

		figure.setWidth(lineWidth);
		figure.setHeight(lineWidth / Figure.DEFAULT_RATIO);

		return true;
	}

	private boolean typeset(Paragraph paragraph, LineAttributes lineAttributes, BreakStrategy breakStrategy) {
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
