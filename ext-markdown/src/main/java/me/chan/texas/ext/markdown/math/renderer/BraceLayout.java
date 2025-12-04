package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.Nullable;

import me.chan.texas.ext.markdown.math.ast.DelimitedAtom;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class BraceLayout extends GroupRendererNode {

	private final RendererNode mLeftSymbol;
	private final RendererNode mRightSymbol;
	private final RendererNode mContent;
	private final int mLevel;

	public BraceLayout(MathPaint.Styles styles,
					   int level,
					   @Nullable RendererNode leftSymbol, RendererNode content, @Nullable RendererNode rightSymbol) {
		super(styles);

		mLevel = level;
		mLeftSymbol = leftSymbol;
		mRightSymbol = rightSymbol;
		mContent = content;

		if (leftSymbol != null) {
			leftSymbol.setClipContent(true);
		}

		if (rightSymbol != null) {
			rightSymbol.setClipContent(true);
		}
	}

	private static final float SYMBOL_PADDING = 0.4f;
	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mContent.measure(paint);

		int exceptHeight = (int) Math.ceil(mContent.getHeight() * ((mLevel - DelimitedAtom.LEVEL_L0) * 0.4f + 1));

		float left = 0;
		if (mLeftSymbol != null) {
			mLeftSymbol.measure(paint, RendererNode.makeUnspecifiedMeasureSpec(), RendererNode.makeMeasureSpec(exceptHeight, RendererNode.EXACTLY));
			left += (mLeftSymbol.getWidth() * SYMBOL_PADDING);
			mLeftSymbol.layout(left, 0);
			left = mLeftSymbol.getRight();
		}

		mContent.layout(left, 0);
		left = mContent.getRight();

		if (mRightSymbol != null) {
			mRightSymbol.measure(paint, RendererNode.makeUnspecifiedMeasureSpec(), RendererNode.makeMeasureSpec(exceptHeight, RendererNode.EXACTLY));
			mRightSymbol.layout(left, 0);
			left = mRightSymbol.getRight() + mRightSymbol.getWidth() * SYMBOL_PADDING;
		}

		int height = mRightSymbol == null && mLeftSymbol == null ?
				mContent.getHeight() :
				(int) Math.ceil(Math.max(exceptHeight, mContent.getHeight()));

		mContent.translate(0, (height - mContent.getHeight()) / 2f);

		setMeasuredSize((int) Math.ceil(left), height);
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		mContent.draw(canvas, paint);
		if (mLeftSymbol != null) {
			mLeftSymbol.draw(canvas, paint);
		}
		if (mRightSymbol != null) {
			mRightSymbol.draw(canvas, paint);
		}
	}

	@Override
	protected String toPretty() {
		return "[]";
	}
}
