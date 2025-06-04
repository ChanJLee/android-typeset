package me.chan.texas.renderer;

import me.chan.texas.misc.RectF;

import androidx.annotation.NonNull;

import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.Paragraph;

import org.junit.Assert;

public class BoundCheckDrawer extends ParagraphVisitor {

	private final float mWidth;
	private boolean mPrint = false;

	private float mCurrentX;

	public BoundCheckDrawer(float width) {
		this(width, false);
	}

	public BoundCheckDrawer(float width, boolean print) {
		mWidth = width;
		mPrint = print;
	}

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {

	}

	@Override
	protected void onVisitParagraphEnd(Paragraph paragraph) {

	}

	@Override
	protected void onVisitLineStart(Line line, float x, float y) {
		mCurrentX = x;
	}

	@Override
	protected void onVisitLineEnd(Line line, float x, float y) {
		if (line.getCount() > 1) {
			try {
				Assert.assertTrue("x out of range", mCurrentX <= (mWidth + 0.04f /* 精度问题 */));
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		}
		if (mPrint) {
			System.out.println("|");
		}
	}

	@Override
	protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
		mCurrentX = inner.left + box.getWidth();
		if (mPrint) {
			System.out.print(box);
			System.out.print(' ');
		}
	}
}
