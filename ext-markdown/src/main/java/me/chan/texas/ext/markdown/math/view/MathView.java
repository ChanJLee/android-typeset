package me.chan.texas.ext.markdown.math.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.chan.texas.ext.markdown.R;
import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvasImpl;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.graphics.GraphicsBuffer;
import me.chan.texas.renderer.core.graphics.TexasCanvasImpl;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.utils.TexasUtils;
import me.chan.texas.utils.concurrency.Worker;

public class MathView extends View {
	private static final boolean DEBUG = true;

	private final GraphicsBuffer mGraphicsBuffer;

	@Nullable
	private RendererNode mRendererNode;

	private final MathPaint mTexasPaint;
	private final MathCanvas mCanvas;

	private final Worker.Token mToken = Worker.Token.newInstance();
	private final Worker mBackgroundWorker = WorkerScheduler.getBackgroundWorker();
	private final Worker mRendererWorker = WorkerScheduler.getRendererWorker();
	private final MsgHandler mMsgHandler = WorkerScheduler.getMsgHandler();

	public MathView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		TextPaint textPaint = new TextPaint();
		textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "texas_markdown_ext/latinmodern-math.otf"));
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(64);

		TexasPaintImpl paint = new TexasPaintImpl();
		paint.reset(new PaintSet(textPaint));
		mTexasPaint = new MathPaintImpl(paint);
		mCanvas = new MathCanvasImpl(new TexasCanvasImpl());

		mGraphicsBuffer = new GraphicsBuffer();
		MsgHandler.Listener listener = (id, value) -> {
			if (id != mToken) {
				return false;
			}

			Object arg = value.arg();
			if (arg instanceof ParseArgs) {
				handleParse(value);
			}

			return true;
		};
		// TODO mem leak
		mMsgHandler.addListener(listener);

		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MathView);
		if (array.hasValue(R.styleable.MathView_textColor)) {
			textPaint.setColor(array.getColor(R.styleable.MathView_textColor, Color.BLACK));
		}
		if (array.hasValue(R.styleable.MathView_textSize)) {
			textPaint.setTextSize(array.getDimensionPixelSize(R.styleable.MathView_textSize, 64));
		}

		if (array.hasValue(R.styleable.MathView_formula)) {
			String formula = array.getString(R.styleable.MathView_formula);
			if (formula != null) {
				render(formula);
			}
		}
		array.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mRendererNode == null) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}

		super.onMeasure(MeasureSpec.makeMeasureSpec(Math.max(MeasureSpec.getSize(widthMeasureSpec), mRendererNode.getWidth()), MeasureSpec.EXACTLY),
				heightMeasureSpec);
	}


	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);
		if (mRendererNode == null) {
			return;
		}

		mCanvas.reset(canvas);
		mRendererNode.draw(mCanvas, mTexasPaint);
	}

	private String mPendingFormula;
	private final FormulaParseTask mFormulaParseTask = new FormulaParseTask(mMsgHandler);

	public void render(String formula) {
		if (TexasUtils.equals(formula, mPendingFormula)) {
			return;
		}

		mPendingFormula = formula;
		if (!isInEditMode()) {
			mBackgroundWorker.async(mToken, new ParseArgs(formula, mTexasPaint), mFormulaParseTask);
			return;
		}

		try {
			RendererNode rendererNode = mBackgroundWorker.sync(mToken, new ParseArgs(formula, mTexasPaint), mFormulaParseTask);
			render(rendererNode);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void handleParse(MsgHandler.Msg msg) {
		int type = msg.type();
		if (type == FormulaParseTask.TYPE_SUCCESS) {
			render((RendererNode) msg.value());
		}
	}

	private void render(RendererNode rendererNode) {
		mRendererNode = rendererNode;
		requestLayout();
	}

	public void cancel() {
		mBackgroundWorker.cancel(mToken);
		mRendererWorker.cancel(mToken);
	}
}