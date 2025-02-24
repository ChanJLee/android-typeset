package me.chan.texas.renderer.highlight;

import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.text.Paragraph;

public class Highlight extends Selection {

	public Highlight(TexasRecyclerView container, Styles styles) {
		super(container, styles);
	}

	@Override
	public void clear() {
		// 通知内容被清除的时候还需要
		for (Paragraph paragraph : mParagraphs) {
			if (paragraph.isRecycled()) {
				continue;
			}

			ParagraphSelection paragraphSelection = paragraph.getHighlight();
			if (paragraphSelection == null) {
				continue;
			}

			paragraph.setHighlight(null);
			try {
				paragraph.requestRedraw();
			} catch (Throwable ignore) {
				/* do nothing */
			}
			paragraphSelection.recycle();
		}
		mParagraphs.clear();
	}
}
