package me.chan.texas.renderer.core.worker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import me.chan.texas.misc.RectF;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasCanvasImpl;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.renderer.ui.text.TextureParagraph;
import me.chan.texas.text.Appearance;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextStyle;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.StateList;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.utils.concurrency.Worker;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class RenderWorker {
	private static final boolean DEBUG = false;

	private static final int TYPE_SUCCESS = 1;
	private static final int TYPE_ERROR = 2;
	private static final String TAG = "RenderWorker";

	private final Worker mWorker;
	private final MsgHandler mMsgHandler;

	private Stats mStats;
	private final TexasPaintImpl mTexasPaint = new TexasPaintImpl();
	private final TexasCanvasImpl mCanvas = new TexasCanvasImpl();

	private final Worker.Task<RenderWorker.Args, Void> mTask = new Worker.Task<Args, Void>() {
		@Override
		public void onSuccess(Worker.Token token, Args args, Void ret) {
			MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_SUCCESS, args, ret);
			mMsgHandler.send(token, message);
		}

		@Override
		public void onError(Worker.Token token, Args args, Throwable error) {
			Log.w(TAG, error);
			MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_ERROR, args, error);
			mMsgHandler.send(token, message);
		}

		@Override
		protected Void onExec(Worker.Token token, Args args) throws Throwable {
			if (mStats != null) {
				++mStats.handleCount;
			}

			if (args.width > 0) {
				render(token, args.paragraph, args);
			}
			return null;
		}
	};

	public RenderWorker(Worker worker, MsgHandler msgHandler) {
		mWorker = worker;
		mMsgHandler = msgHandler;
		mMsgHandler.addListener((token, value) -> {
			Args args = value.asArg(Args.class);
			if (args == null) {
				return false;
			}

			if (DEBUG && Looper.myLooper() != Looper.getMainLooper()) {
				throw new RuntimeException("invalid thread");
			}

			args.renderer.syncUI();
			return true;
		});
	}

	public void submit(Worker.Token token, Args args) {
		if (mStats != null) {
			++mStats.requestCount;
		}
		mWorker.cancel(token);
		mWorker.async(token, args, mTask);
	}

	public void submitSync(Worker.Token token, Args args) {
		try {
			mWorker.sync(token, args, mTask);
		} catch (Throwable e) {
			Log.w(TAG, e);
		} finally {
			args.recycle();
		}
	}

	public void setStatsEnable(boolean enable) {
		if (!enable) {
			mStats = null;
			return;
		}

		if (mStats == null) {
			mStats = new Stats();
		}
	}

	@Nullable
	public Stats getStats() {
		return mStats;
	}

	private void render(Worker.Token token, Paragraph paragraph, Args args) {
		if (args.width <= 0) {
			return;
		}

		long ts = 0;
		if (mStats != null) {
			++mStats.drawCount;
			ts = SystemClock.elapsedRealtime();
		}

		render0(token.getId(), paragraph, args);

		if (mStats != null) {
			mStats.drawUsageMs += SystemClock.elapsedRealtime() - ts;
		}
	}

	private void render0(int taskId, Paragraph paragraph, Args args) {
		if (DEBUG) {
			Log.d("RenderWorker", "render: " + taskId);
		}

		Layout layout = paragraph.getLayout();
		Canvas rawCanvas = args.renderer.lockCanvas(layout.getWidth(), layout.getHeight());
		if (rawCanvas == null) {
			return;
		}

		try {
			mCanvas.reset(rawCanvas);
			ParagraphSelection selection = paragraph.getSelection(Selection.Type.SELECTION);
			if (selection != null) {
				mTexasPaint.reset(args.paintSet);
				selection.drawBackground(mCanvas, mTexasPaint, args.option);
			}

			selection = paragraph.getSelection(Selection.Type.HIGHLIGHT);
			if (selection != null) {
				mTexasPaint.reset(args.paintSet);
				selection.drawBackground(mCanvas, mTexasPaint, args.option);
			}

			// render background decor
			renderDecor(mCanvas, paragraph, args, true);

			// draw content
			renderContent(mCanvas, paragraph, args);

			// render decor
			renderDecor(mCanvas, paragraph, args, false);

			// render debug info
			renderDebug(taskId, mCanvas, paragraph, args);
		} finally {
			args.renderer.unlockCanvasAndPost(rawCanvas);
		}
	}

	private void renderDecor(TexasCanvas canvas, Paragraph paragraph, Args args, boolean background) {
		ParagraphDecor decor = paragraph.getDecor();
		if (decor == null) {
			return;
		}

		Layout layout = paragraph.getLayout();
		mTexasPaint.reset(args.paintSet);
		decor.draw(canvas, mTexasPaint, paragraph, args.width, layout.getHeight(), background);
	}

	private void renderContent(TexasCanvas canvas, Paragraph paragraph, Args args) {
		try {
			mDrawVisitor.setCanvas(canvas);
			mDrawVisitor.setRenderContext(args);
			mDrawVisitor.visit(paragraph);
		} catch (ParagraphVisitor.VisitException e) {
			Log.w("TexasRenderEngine", e);
		} finally {
			mDrawVisitor.clear();
		}
	}

	private void renderDebug(int taskId, TexasCanvas canvas, Paragraph paragraph, Args args) {
		if (!args.option.isDebugEnable()) {
			return;
		}

		if (mDebugDrawVisitor == null) {
			mDebugDrawVisitor = new DebugDrawVisitor(mTexasPaint);
		}

		try {
			mDebugDrawVisitor.setTaskId(taskId);
			mDebugDrawVisitor.setCanvas(canvas);
			mDebugDrawVisitor.setRenderArgs(args);
			mDebugDrawVisitor.visit(paragraph);
		} catch (ParagraphVisitor.VisitException e) {
			Log.w("TexasRenderEngine", e);
		} finally {
			mDebugDrawVisitor.clear();
		}
	}

	private final DrawVisitor mDrawVisitor = new DrawVisitor(mTexasPaint);

	public void cancel(Worker.Token token) {
		mWorker.cancel(token);
	}

	private final static class DrawVisitor extends ParagraphVisitor {
		private static final int STEP_DRAW_BACKGROUND = 0;
		private static final int STEP_DRAW_CONTENT = 1;
		private final StateList mStates = new StateList();

		private TexasCanvas mCanvas;
		private Line mLine;
		private Args mArgs;
		private boolean mIsInterrupted = false;
		private int mStep = STEP_DRAW_BACKGROUND;

		private final TexasPaint mWorkPaint;

		private ParagraphSelection mSelection;

		private ParagraphSelection mHighlight;

		public DrawVisitor(TexasPaint workPaint) {
			mWorkPaint = workPaint;
		}

		void setCanvas(TexasCanvas canvas) {
			mCanvas = canvas;
		}

		public void setRenderContext(Args args) {
			mArgs = args;
		}

		@Override
		public void visit(Paragraph paragraph) throws VisitException {
			mStep = STEP_DRAW_BACKGROUND;
			super.visit(paragraph);
			mStep = STEP_DRAW_CONTENT;
			super.visit(paragraph);
		}

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {
			mSelection = paragraph.getSelection(Selection.Type.SELECTION);
			mHighlight = paragraph.getSelection(Selection.Type.HIGHLIGHT);
		}

		@Override
		protected void onVisitParagraphEnd(Paragraph paragraph) {
			mSelection = null;
			mHighlight = null;
		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {
			mLine = line;
		}

		@Override
		protected void onVisitLineEnd(Line line, float x, float y) {
			/* noop */
		}

		@Override
		public void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
			boolean isSelected = isBoxSelected(mSelection, box);
			boolean isHighlighted = isBoxHighlighted(mHighlight, box);
			mStates.clear();
			mStates.setSelected(isSelected);
			mStates.setHighlighted(isHighlighted);
			if (mStep == STEP_DRAW_BACKGROUND) {
				drawBackground(box, inner, outer, context);
				return;
			}

			mWorkPaint.reset(mArgs.paintSet);
			setupTextStyles(mWorkPaint, box, isSelected, isHighlighted);

			drawContent(box, mWorkPaint, inner, outer, mStates);

			drawForeground(box, inner, outer, context);
		}

		private void setupTextStyles(TexasPaint workPaint, Box box, boolean isSelected, boolean isHighlighted) {
			if (!(box instanceof TextBox)) {
				return;
			}

			TextBox textBox = (TextBox) box;
			// 显示高亮
			Object tag = textBox.getTag();

			TextStyle textStyle = textBox.getTextStyle();
			if (textStyle != null) {
				textStyle.update(workPaint, tag);
			}

			if (mSelection != null && isSelected) {
				textStyle = mSelection.getStyle();
				if (textStyle != null) {
					textStyle.update(workPaint, box.getTag());
				}
			}

			if (mHighlight != null && isHighlighted) {
				textStyle = mHighlight.getStyle();
				if (textStyle != null) {
					textStyle.update(workPaint, box.getTag());
				}
			}
		}

		private void drawForeground(Box box, RectF inner, RectF outer, RendererContext context) {
			Appearance foreground = box.getForeground();
			if (foreground != null) {
				mWorkPaint.reset(mArgs.paintSet);
				foreground.draw(mCanvas, mWorkPaint, inner, outer, context);
			}
		}

		private void drawContent(Box box, TexasPaint workPaint, RectF inner, RectF outer, StateList states) {
			box.draw(mCanvas, workPaint, inner, outer, mLine.getBaselineOffset(), states);
		}

		private void drawBackground(Box box, RectF inner, RectF outer, RendererContext context) {
			Appearance background = box.getBackground();
			if (background != null) {
				mWorkPaint.reset(mArgs.paintSet);
				background.draw(mCanvas, mWorkPaint, inner, outer, context);
			}
		}

		private boolean isBoxSelected(ParagraphSelection selection, Box box) {
			if (selection == null) {
				return false;
			}

			return selection.isSelected(box);
		}

		private boolean isBoxHighlighted(ParagraphSelection selection, Box box) {
			if (selection == null) {
				return false;
			}

			return selection.isSelected(box);
		}

		public void clear() {
			mCanvas = null;
			mArgs = null;
			mLine = null;
			mIsInterrupted = false;
		}

		public boolean isInterrupted() {
			return mIsInterrupted;
		}
	}

	private DebugDrawVisitor mDebugDrawVisitor;

	private final static class DebugDrawVisitor extends ParagraphVisitor {
		private final Paint mDebugPaint;
		private TexasCanvas mCanvas;
		private Args mArgs;
		private static final int[] BACKGROUND = {
				0x33ff0000,
				0x3300ff00,
				0x330000ff
		};

		private int mTaskId;

		private final TexasPaint mWorkPaint;

		DebugDrawVisitor(TexasPaint workerPaint) {
			super();
			mDebugPaint = new Paint();
			mDebugPaint.setColor(Color.GREEN);
			mDebugPaint.setStyle(Paint.Style.STROKE);
			mDebugPaint.setTextSize(40);
			mWorkPaint = workerPaint;
		}

		public void setRenderArgs(Args args) {
			mArgs = args;
		}

		void setCanvas(TexasCanvas canvas) {
			mCanvas = canvas;
		}

		public void setTaskId(int taskId) {
			mTaskId = taskId;
		}

		void clear() {
			mCanvas = null;
		}

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {
			mWorkPaint.reset(mArgs.paintSet);
			mWorkPaint.set(mDebugPaint);
			mWorkPaint.setColor(BACKGROUND[paragraph.getId() % BACKGROUND.length]);
			mWorkPaint.setStyle(Paint.Style.FILL);
			Layout layout = paragraph.getLayout();
			mCanvas.drawRect(0, 0, mArgs.width, layout.getHeight(), mWorkPaint);

			mWorkPaint.setColor(Color.BLACK);
			mCanvas.drawText("task id: " + mTaskId + " " + layout.getAlgorithm(), 0, 40, mWorkPaint);
		}

		@Override
		public void onVisitParagraphEnd(Paragraph paragraph) {
			mWorkPaint.reset(mArgs.paintSet);
			mWorkPaint.setStyle(Paint.Style.STROKE);
			mWorkPaint.set(mDebugPaint);
			mWorkPaint.setColor(Color.RED);
			mWorkPaint.setStrokeWidth(10);
			int x = mArgs.width - 100;
			ParagraphSelection selection = paragraph.getSelection(Selection.Type.SELECTION);
			if (selection == null || selection.isEmpty()) {
				return;
			}

			RectF first = selection.getFirstRegion();
			RectF last = selection.getLastRegion();
			mCanvas.drawLine(x,
					first != null ? first.top : -1,
					x,
					last != null ? last.bottom : -1,
					mWorkPaint);
		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {

		}

		@Override
		public void onVisitLineEnd(Line line, float x, float y) {
			float startX = 0;
			float lineSpace = mArgs.paragraph.getLayout().getLineSpacingExtra();
			float startY = y + lineSpace;
			android.graphics.Rect rect = new android.graphics.Rect();
			String debugInfo = line.getInfoMsg();
			mDebugPaint.getTextBounds(debugInfo, 0, debugInfo.length(), rect);
			mDebugPaint.setColor(Color.BLUE);
			rect.offset((int) startX, (int) startY);
			mCanvas.getCanvas().drawRect(rect, mDebugPaint);
			mDebugPaint.setColor(Color.RED);
			mCanvas.getCanvas().drawText(debugInfo, startX, startY, mDebugPaint);
		}

		@Override
		public void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
			mDebugPaint.setColor(Color.GREEN);
			mCanvas.getCanvas().drawRect(inner.left, inner.top, inner.right, inner.bottom, mDebugPaint);
		}
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<Args> POOL = new ObjectPool<>(32);

		private Paragraph paragraph;
		private PaintSet paintSet;
		private RenderOption option;
		private TextureParagraph renderer;
		private int width;

		private Args() {
		}

		@Override
		protected void onRecycle() {
			paintSet = null;
			paragraph = null;
			option = null;
			renderer = null;
			width = 0;
			POOL.release(this);
		}

		public static Args obtain(@NonNull Paragraph source,
								  @NonNull RenderOption option,
								  @NonNull TextureParagraph render,
								  @IntRange(from = 1) int width,
								  @NonNull PaintSet paintSet) {
			Args args = POOL.acquire();
			if (args == null) {
				args = new Args();
			}

			args.paintSet = paintSet;
			args.paragraph = source;
			args.option = option;
			args.width = width;
			args.renderer = render;
			args.reuse();
			return args;
		}
	}

	private static class Stats {
		/**
		 * 请求的次数
		 */
		private int requestCount;
		/**
		 * 被处理的次数
		 */
		private int handleCount;
		/**
		 * 绘制的次数
		 */
		private int drawCount;
		/**
		 * 使用时间
		 */
		private long drawUsageMs;

		@Override
		public String toString() {
			float drawAvg = 0;
			if (drawCount != 0) {
				drawAvg = drawUsageMs * 1.0f / drawCount;
			}
			return "requestCount=" + requestCount + ", handleCount=" + handleCount + ", drawCount=" + drawCount + ", drawUsageMs=" + drawUsageMs + ", drawAvgMs=" + drawAvg;
		}
	}
}
