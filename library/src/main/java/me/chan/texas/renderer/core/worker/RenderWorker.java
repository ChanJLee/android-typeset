package me.chan.texas.renderer.core.worker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.compat.TextPaintCompat;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
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

	private final TextPaint mWorkPaint = TextPaintCompat.create();

	private final Worker.Listener<RenderWorker.Args, Void> mListener = new Worker.Listener<RenderWorker.Args, Void>() {
		@Override
		public void onStart(Worker.Token token, Args args) {
			/* do nothing */
		}

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
	};
	private final Worker.Task<RenderWorker.Args, Void> mTask = (token, args) -> {
		if (mStats != null) {
			++mStats.handleCount;
		}

		if (args.width > 0) {
			render(token, args.paragraph, args);
		}
		return null;
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
		mWorker.async(token, args, mTask, mListener);
	}

	public void submitSync(Worker.Token token, Args args) {
		try {
			mWorker.sync(token, args, mTask);
			args.recycle();
		} catch (Throwable e) {
			Log.w(TAG, e);
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
		Canvas canvas = args.renderer.lockCanvas(layout.getWidth(), layout.getHeight());
		if (canvas == null) {
			return;
		}

		try {
			ParagraphSelection selection = paragraph.getSelection(Selection.Type.SELECTION);
			if (selection != null) {
				TextPaint workPaint = args.paintSet.getWorkPaint(mWorkPaint);
				selection.drawBackground(canvas, workPaint, args.option);
			}

			selection = paragraph.getSelection(Selection.Type.HIGHLIGHT);
			if (selection != null) {
				TextPaint workPaint = args.paintSet.getWorkPaint(mWorkPaint);
				selection.drawBackground(canvas, workPaint, args.option);
			}

			// draw content
			renderContent(canvas, paragraph, args);

			// render decor
			renderDecor(canvas, paragraph, args);

			// render debug info
			renderDebug(taskId, canvas, paragraph, args);
		} finally {
			args.renderer.unlockCanvasAndPost(canvas);
		}
	}

	private void renderDecor(Canvas canvas, Paragraph paragraph, Args args) {
		if (args.decor == null) {
			return;
		}

		Layout layout = paragraph.getLayout();
		args.decor.draw(canvas, paragraph, args.width, layout.getHeight());
	}

	private void renderContent(Canvas canvas, Paragraph paragraph, Args args) {
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

	private void renderDebug(int taskId, Canvas canvas, Paragraph paragraph, Args args) {
		if (!args.option.isDebugEnable()) {
			return;
		}

		if (mDebugDrawVisitor == null) {
			mDebugDrawVisitor = new DebugDrawVisitor(mWorkPaint);
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

	private final DrawVisitor mDrawVisitor = new DrawVisitor(mWorkPaint);

	public void cancel(Worker.Token token) {
		mWorker.cancel(token);
	}

	private final static class DrawVisitor extends ParagraphVisitor {
		private static final int STEP_DRAW_BACKGROUND = 0;
		private static final int STEP_DRAW_CONTENT = 1;
		private final StateList mStates = new StateList();

		private Canvas mCanvas;
		private Line mLine;
		private Args mArgs;
		private boolean mIsInterrupted = false;
		private int mStep = STEP_DRAW_BACKGROUND;

		private final TextPaint mWorkPaint;

		private ParagraphSelection mSelection;

		private ParagraphSelection mHighlight;

		public DrawVisitor(TextPaint workPaint) {
			mWorkPaint = workPaint;
		}

		void setCanvas(Canvas canvas) {
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

			TextPaint workPaint = mArgs.paintSet.getWorkPaint(mWorkPaint);
			setupTextStyles(workPaint, box, isSelected, isHighlighted);

			drawContent(box, workPaint, inner, mStates);

			drawForeground(box, inner, outer, context);
		}

		private void setupTextStyles(TextPaint workPaint, Box box, boolean isSelected, boolean isHighlighted) {
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
				TextPaint workPaint = mArgs.paintSet.getWorkPaint(mWorkPaint);
				foreground.draw(mCanvas, workPaint, inner, outer, context);
			}
		}

		private void drawContent(Box box, TextPaint workPaint, RectF inner, StateList states) {
			box.draw(mCanvas, workPaint, inner.left, inner.bottom - mLine.getBaselineOffset(), states);
		}

		private void drawBackground(Box box, RectF inner, RectF outer, RendererContext context) {
			Appearance background = box.getBackground();
			if (background != null) {
				TextPaint workPaint = mArgs.paintSet.getWorkPaint(mWorkPaint);
				background.draw(mCanvas, workPaint, inner, outer, context);
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
		private Canvas mCanvas;
		private Args mArgs;
		private static final int[] BACKGROUND = {
				0x33ff0000,
				0x3300ff00,
				0x330000ff
		};

		private int mTaskId;

		private final TextPaint mWorkPaint;

		DebugDrawVisitor(TextPaint workerPaint) {
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

		void setCanvas(Canvas canvas) {
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
			TextPaint workPaint = mArgs.paintSet.getWorkPaint(mWorkPaint);
			workPaint.set(mDebugPaint);
			workPaint.setColor(BACKGROUND[paragraph.getId() % BACKGROUND.length]);
			workPaint.setStyle(Paint.Style.FILL);
			Layout layout = paragraph.getLayout();
			mCanvas.drawRect(0, 0, mArgs.width, layout.getHeight(), workPaint);

			workPaint.setColor(Color.BLACK);
			mCanvas.drawText("task id: " + mTaskId + " " + layout.getAlgorithm(), 0, 40, workPaint);
		}

		@Override
		public void onVisitParagraphEnd(Paragraph paragraph) {
			TextPaint workPaint = mArgs.paintSet.getWorkPaint(mWorkPaint);
			workPaint.setStyle(Paint.Style.STROKE);
			workPaint.set(mDebugPaint);
			workPaint.setColor(Color.RED);
			workPaint.setStrokeWidth(10);
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
					workPaint);
		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {

		}

		@Override
		public void onVisitLineEnd(Line line, float x, float y) {
			float startX = 0;
			float lineSpace = mArgs.paragraph.getLayout().getLineSpace();
			float startY = y + lineSpace;
			Rect rect = new Rect();
			String debugInfo = line.getInfoMsg();
			mDebugPaint.getTextBounds(debugInfo, 0, debugInfo.length(), rect);
			mDebugPaint.setColor(Color.BLUE);
			rect.offset((int) startX, (int) startY);
			mCanvas.drawRect(rect, mDebugPaint);
			mDebugPaint.setColor(Color.RED);
			mCanvas.drawText(debugInfo, startX, startY, mDebugPaint);
		}

		@Override
		public void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
			mDebugPaint.setColor(Color.GREEN);
			mCanvas.drawRect(inner, mDebugPaint);
		}
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<Args> POOL = new ObjectPool<>(32);

		private Paragraph paragraph;
		private PaintSet paintSet;
		private RenderOption option;
		private TextureParagraph renderer;
		private ParagraphDecor decor;
		private int width;

		private Args() {
		}

		@Override
		protected void onRecycle() {
			paintSet = null;
			decor = null;
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
			return obtain(source, option, render, width, paintSet, null);
		}

		public static Args obtain(@NonNull Paragraph source,
								  @NonNull RenderOption option,
								  @NonNull TextureParagraph render,
								  @IntRange(from = 1) int width,
								  @NonNull PaintSet paintSet,
								  @Nullable ParagraphDecor decor) {
			Args args = POOL.acquire();
			if (args == null) {
				args = new Args();
			}

			args.paintSet = paintSet;
			args.decor = decor;
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
