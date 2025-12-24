package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;


public class SqrtNode extends RendererNode implements OptimizableRendererNode, HorizontalCalibratedNode {
	private RendererNode mContent;

	@Nullable
	private RendererNode mRoot;
	private SqrtSymbolNode mSymbol;

	public SqrtNode(MathPaint.Styles styles, RendererNode content, @Nullable RendererNode root) {
		super(styles);
		mContent = content;
		mRoot = root;
		mSymbol = new SqrtSymbolNode(styles);
	}

	public RendererNode getContent() {
		return mContent;
	}

	@Nullable
	public RendererNode getRoot() {
		return mRoot;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mContent.measure(paint);
		mSymbol.resize(mContent.getHeight());
		mSymbol.measure(paint);
		mSymbol.setContentWidth(mContent.getWidth() + mSymbol.getKernAfterDegree());
		if (mRoot != null) {
			mRoot.measure(paint);
		}

		preLayout();
		float left = mSymbol.getLeft();
		float top = mSymbol.getTop();
		float right = mContent.getRight();
		float bottom = Math.max(mContent.getBottom(), mSymbol.getBottom());
		if (mRoot != null) {
			left = mRoot.getLeft();
		}

		setMeasuredSize((int) Math.ceil(right - left), (int) Math.ceil(bottom - top));
	}

	private void preLayout() {
		float left = 0;
		float top = 0;
		if (mRoot != null) {
			mRoot.layout(left, top);
			left = (mRoot.getRight() - mSymbol.getKernBeforeDegree());
			top = (mRoot.getBottom() + mSymbol.getHeight() * mSymbol.getDegreeBottomRaisePercent() - mSymbol.getHeight());
		}

		mSymbol.layout(left, top);
		left = (mSymbol.getRight() + mSymbol.getKernAfterDegree());

		mContent.layout(left, top + mSymbol.getTopPadding() + mSymbol.getVerticalGap());

		top = mSymbol.getTop();
		if (top < 0) {
			float dy = -top;
			if (mRoot != null) {
				mRoot.translate(0, dy);
			}
			mSymbol.translate(0, dy);
			mContent.translate(0, dy);
		}
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		int color = paint.getColor();

		if (DEBUG) {
			paint.setColor(Color.RED);
		}
		mContent.draw(canvas, paint);

		if (DEBUG) {
			paint.setColor(Color.BLUE);
		}
		if (mRoot != null) {
			mRoot.draw(canvas, paint);
		}

		if (DEBUG) {
			paint.setColor(Color.BLACK);
		}
		mSymbol.draw(canvas, paint);
		if (DEBUG) {
			paint.setColor(color);
		}
	}

	@Override
	protected String toPretty() {
		return "sqrt {}";
	}

	@NonNull
	@Override
	public RendererNode optimize() {
		if (mContent instanceof OptimizableRendererNode) {
			mContent = ((OptimizableRendererNode) mContent).optimize();
		}
		if (mRoot instanceof OptimizableRendererNode) {
			mRoot = ((OptimizableRendererNode) mRoot).optimize();
		}

		return this;
	}

	@Override
	public boolean supportAlignBaseline() {
		if (mContent instanceof HorizontalCalibratedNode) {
			HorizontalCalibratedNode node = (HorizontalCalibratedNode) mContent;
			return node.supportAlignBaseline();
		}

		return false;
	}

	@Override
	public float getBaseline() {
		return ((HorizontalCalibratedNode) mContent).getBaseline() + getTop();
	}

	@Override
	public float getContentCenterY() {
		return mContent.getContentCenterY() + mContent.getTop();
	}
}
