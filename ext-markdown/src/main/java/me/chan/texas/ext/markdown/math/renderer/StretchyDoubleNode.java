package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;

public class StretchyDoubleNode extends RendererNode {
	public static final int GRAVITY_TOP = 0;
	public static final int GRAVITY_BOTTOM = 1;
	public static final int GRAVITY_CENTER = 2;

	private final SymbolNode mSymbol;
	private final SymbolNode mExtension;
	private int mActualWidth;
	private final int mGravity;

	public StretchyDoubleNode(MathPaint.Styles styles, int gravity, Symbol symbol, Symbol extension) {
		super(styles);
		mSymbol = new SymbolNode(styles, symbol);
		mExtension = new SymbolNode(styles, extension);
		mGravity = gravity;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mSymbol.measure(paint);
		mExtension.measure(paint);

		float height = RendererNode.getSize(heightSpec);
		float width = RendererNode.getSize(widthSpec);

		if (RendererNode.getMode(heightSpec) != RendererNode.EXACTLY) {
			height = mSymbol.getHeight();
		}

		if (mGravity == GRAVITY_TOP) {
			mSymbol.layout(0);
		} else if (mGravity == GRAVITY_CENTER) {
			mSymbol.layout((height - mSymbol.getHeight()) / 2.0f);
		} else if (mGravity == GRAVITY_BOTTOM) {
			mSymbol.layout(height - mSymbol.getHeight());
		} else {
			throw new IllegalArgumentException("Unknown gravity: " + mGravity);
		}

		mExtension.layout(0);

		float left = Math.min(mSymbol.getLeft(), mExtension.getLeft());
		float right = Math.max(mSymbol.getRight(), mExtension.getRight());

		mActualWidth = (int) Math.ceil(right - left);
		if (RendererNode.getMode(widthSpec) == RendererNode.UNSPECIFIED) {
			width = mActualWidth;
		}

		float dx = -left;
		mSymbol.translate(dx, 0);
		mExtension.translate(dx, 0);
		setMeasuredSize((int) Math.ceil(width), (int) Math.ceil(height));
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		int width = getWidth();
		if (width != mActualWidth) {
			canvas.save();
			canvas.scale(width * 1.0f / mActualWidth, 1f);
		}

		mSymbol.draw(canvas, paint);

		drawExtension(canvas, paint);

		if (width != mActualWidth) {
			canvas.restore();
		}
	}

	private void drawExtension(MathCanvas canvas, MathPaint paint) {
		float height = getHeight() - mSymbol.getHeight();
		if (height <= 0) {
			return;
		}

		float scaleY = (height) / mExtension.getHeight();
		canvas.save();

		if (mGravity == GRAVITY_TOP) {
			canvas.translate(0, mSymbol.getBottom());
			canvas.scale(1, scaleY);
			mExtension.draw(canvas, paint);
		} else if (mGravity == GRAVITY_CENTER) {
			canvas.save();
			canvas.scale(1, scaleY / 2);
			mExtension.draw(canvas, paint);
			canvas.restore();

			canvas.translate(0, mSymbol.getBottom());
			canvas.scale(1, scaleY / 2);
			mExtension.draw(canvas, paint);
		} else {
			canvas.scale(1, scaleY);
			mExtension.draw(canvas, paint);
		}
		canvas.restore();
	}

	@Override
	protected void onDrawDebug(MathCanvas canvas, MathPaint paint) {
		super.onDrawDebug(canvas, paint);
		drawDebugBounds(canvas, paint);
	}

	@Override
	protected String toPretty() {
		return "stretchy";
	}
}
