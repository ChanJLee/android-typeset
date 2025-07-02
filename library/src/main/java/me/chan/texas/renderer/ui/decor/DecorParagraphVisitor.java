package me.chan.texas.renderer.ui.decor;

import me.chan.texas.misc.Rect;

import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Line;

abstract class DecorParagraphVisitor extends ParagraphVisitor {
	protected Rect mViewportOuter;
	protected Rect mViewportInner;

	public final void handle(Paragraph paragraph, Rect viewportOuter, Rect viewportInner) {
		mViewportOuter = viewportOuter;
		mViewportInner = viewportInner;
		try {
			visit(paragraph);
		} catch (Throwable ignore) {
			/* noop */
		} finally {
			mViewportOuter = null;
			mViewportInner = null;
		}
	}

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {

	}

	@Override
	protected void onVisitParagraphEnd(Paragraph paragraph) {
		mViewportOuter = mViewportInner = null;
	}

	@Override
	protected void onVisitLineStart(Line line, float x, float y) {

	}

	@Override
	protected void onVisitLineEnd(Line line, float x, float y) {

	}
}