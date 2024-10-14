package me.chan.texas.renderer.selection.visitor;

import android.graphics.RectF;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextStyles;
import me.chan.texas.text.layout.Box;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class PredicatesDriveSelectedVisitor extends SelectedVisitor {
	private ParagraphPredicates mPredicates;

	public void reset(RenderOption renderOption, ParagraphPredicates predicates, @Nullable TextStyles styles) {
		mPredicates = predicates;

		// 如果样式为空，则按照长按的效果来处理
		if (styles == null) {
			super.reset(true, renderOption);
			return;
		}

		super.reset(styles, renderOption);
	}

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {
		if (!mPredicates.acceptParagraph(paragraph.getTag())) {
			ParagraphSelection prev = paragraph.getSelection();
			if (prev != null) {
				prev.recycle();
			}
			paragraph.setSelection(null);
			sendVisitSig(SIG_STOP_PARA_VISIT);
		}
	}

	@Override
	protected boolean selected(Box box, RectF inner, RectF outer) {
		return mPredicates.acceptSpan(box.getTag());
	}

	@Override
	public void onVisitParagraphEnd(Paragraph paragraph) {
		mPredicates = null;
		super.onVisitParagraphEnd(paragraph);
	}
}
