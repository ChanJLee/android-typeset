package me.chan.texas.renderer.core.worker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextPaint;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.shanbay.lib.log.Log;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.core.sync.WorkerMessager;
import me.chan.texas.renderer.highlight.ParagraphHighlight;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.renderer.ui.text.TextureParagraph;
import me.chan.texas.text.Appearance;
import me.chan.texas.text.DrawContext;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextStyle;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextBox;
import me.chan.texas.utils.concurrency.TaskQueue;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class RenderWorker implements TaskQueue.Task<RenderWorker.Args, Void>, TaskQueue.Listener<RenderWorker.Args, Void> {
	private static final boolean DEBUG = false;

	private static final int TYPE_SUCCESS = 1;
	private static final int TYPE_ERROR = 2;
	private static final String TAG = "RenderWorker";

	private final TaskQueue mTaskQueue;
	private final WorkerMessager mMessager;

	private Stats mStats;

	public RenderWorker(TaskQueue taskQueue, WorkerMessager messager) {
		mTaskQueue = taskQueue;
		mMessager = messager;
		mMessager.addListener(new WorkerMessager.Listener() {
			@Override
			public boolean handleMessage(int id, WorkerMessager.WorkerMessage value) {
				Args args = value.asArg(Args.class);
				if (args == null) {
					return false;
				}

				if (DEBUG && Looper.myLooper() != Looper.getMainLooper()) {
					throw new RuntimeException("invalid thread");
				}

				args.renderer.syncUI();
				return true;
			}
		});
	}

	public void submit(int id, Args args) {
		if (mStats != null) {
			++mStats.requestCount;
		}
		mTaskQueue.cancel(id);
		mTaskQueue.submit(id, args, this, this);
	}

	public void submitSync(int taskId, Args args) {
		try {
			mTaskQueue.submitSync(taskId, args, this);
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

	private void render(int taskId, Paragraph paragraph, Args args) {
		if (args.width <= 0) {
			return;
		}

		long ts = 0;
		if (mStats != null) {
			++mStats.drawCount;
			ts = SystemClock.elapsedRealtime();
		}

		render0(taskId, paragraph, args);

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
		args.decor.draw(canvas, paragraph, args.option, args.width, layout.getHeight());
	}

	private void renderContent(Canvas canvas, Paragraph paragraph, Args args) {
		try {
			ParagraphSelection selection = args.selection;
			if (selection != null) {
				selection.updateStyle(args.option);
			}

			ParagraphHighlight highlight = args.highlight;
			if (highlight != null) {
				highlight.updateStyle(args.option);
			}

			renderSelection(canvas, args);

			mDrawVisitor.setCanvas(canvas);
			mDrawVisitor.setRenderContext(args);
			mDrawVisitor.visit(paragraph, args.option);
		} catch (ParagraphVisitor.VisitException e) {
			Log.w("TexasRenderEngine", e);
		} finally {
			mDrawVisitor.clear();
		}
	}

	private void renderSelection(Canvas canvas, Args args) {
		ParagraphSelection selection = args.selection;
		if (selection == null) {
			return;
		}

		TextPaint workPaint = args.mPaintSet.getWorkPaint();
		workPaint.setColor(selection.getBgColor());
		selection.draw(canvas, workPaint, args.option.getSelectedBackgroundRoundRadius());
	}

	private void renderDebug(int taskId, Canvas canvas, Paragraph paragraph, Args args) {
		if (!args.option.isEnableDebug()) {
			return;
		}

		if (mDebugDrawVisitor == null) {
			mDebugDrawVisitor = new DebugDrawVisitor();
		}

		try {
			mDebugDrawVisitor.setTaskId(taskId);
			mDebugDrawVisitor.setCanvas(canvas);
			mDebugDrawVisitor.setRenderContext(args);
			mDebugDrawVisitor.visit(paragraph, args.option);
		} catch (ParagraphVisitor.VisitException e) {
			Log.w("TexasRenderEngine", e);
		} finally {
			mDebugDrawVisitor.clear();
		}
	}

	private final DrawVisitor mDrawVisitor = new DrawVisitor();

	@Override
	public Void run(int id, Args args) throws Throwable {
		if (mStats != null) {
			++mStats.handleCount;
		}

		if (args.width > 0) {
			render(id, args.paragraph, args);
		}
		return null;
	}

	@Override
	public void onStart(int id, Args args) {
		/* do nothing */
	}

	@Override
	public void onSuccess(int id, Args args, Void ret) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_SUCCESS, args, ret);
		mMessager.send(id, message);
	}

	@Override
	public void onError(int id, Args args, Throwable throwable) {
		Log.w(TAG, throwable);
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_ERROR, args, throwable);
		mMessager.send(id, message);
	}

	public void cancel(int taskId) {
		mTaskQueue.cancel(taskId);
	}

	private final static class DrawVisitor extends ParagraphVisitor {

		private Canvas mCanvas;
		private final DrawContext mDrawContext = new DrawContext();
		private Line mLine;
		private Args mArgs;
		private boolean mIsInterrupted = false;

		void setCanvas(Canvas canvas) {
			mCanvas = canvas;
		}

		public void setRenderContext(Args args) {
			mArgs = args;
		}

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {

		}

		@Override
		protected void onVisitParagraphEnd(Paragraph paragraph) {

		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {
			mLine = line;
		}

		@Override
		protected void onVisitLineEnd(Line line, float x, float y) {

		}

		private Object lookup(boolean prev) {
			int size = mLine.getCount();
			int offset = prev ? -1 : 1;
			int index = mCurrentBoxIndexInternal + offset;

			while (index >= 0 && index < size) {
				Element element = mLine.getElement(index);
				index += offset;
				if (element instanceof Box) {
					return ((Box) element).getTag();
				}
			}

			return null;
		}

		@Override
		public void onVisitBox(Box box, RectF inner, RectF outer) {
			mDrawContext.reset();
			mDrawContext.setTag(box.getTag());

			mDrawContext.setPrevTag(lookup(true));
			mDrawContext.setNextTag(lookup(false));

			boolean isSelected = isBoxSelected(box);

			// 先绘制背景
			drawBackground(box, isSelected, inner, outer);

			TextPaint workPaint = mArgs.mPaintSet.getWorkPaint();

			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				// 显示高亮
				Object tag = textBox.getTag();

				TextStyle textStyle = textBox.getTextStyle();
				if (textStyle != null) {
					textStyle.update(workPaint, tag);
				}

				if (mArgs.highlight != null && mArgs.highlight.isHighlight(box)) {
					workPaint.setColor(mArgs.highlight.getTextColor());
				}
			}

			if (mArgs.selection != null && isSelected) {
				workPaint.setColor(mArgs.selection.getTextColor());
			}

			drawContent(box, workPaint, inner, isSelected);

			drawForeground(box, inner, outer);
		}

		private void drawForeground(Box box, RectF inner, RectF outer) {
			Appearance foreground = box.getForeground();
			if (foreground != null) {
				TextPaint workPaint = mArgs.mPaintSet.getWorkPaint();
				foreground.draw(mCanvas, workPaint, inner, outer, mDrawContext);
			}
		}

		private void drawContent(Box box, TextPaint workPaint, RectF inner, boolean isSelected) {
			box.draw(mCanvas, workPaint, inner.left, inner.bottom - mLine.getBaselineOffset(), isSelected);
		}

		private void drawBackground(Box box, boolean isSelected, RectF inner, RectF outer) {
			Appearance background = box.getBackground();
			if (background != null && !isSelected) {
				TextPaint workPaint = mArgs.mPaintSet.getWorkPaint();
				background.draw(mCanvas, workPaint, inner, outer, mDrawContext);
			}
		}

		private boolean isBoxSelected(Box box) {
			if (mArgs == null || mArgs.selection == null) {
				return false;
			}

			return mArgs.selection.isSelected(box);
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
		private final int[] mLocation = new int[2];
		private Args mArgs;
		private static final int[] BACKGROUND = {
				0x33ff0000,
				0x3300ff00,
				0x330000ff
		};

		private int mTaskId;

		DebugDrawVisitor() {
			mDebugPaint = new Paint();
			mDebugPaint.setColor(Color.GREEN);
			mDebugPaint.setStyle(Paint.Style.STROKE);
			mDebugPaint.setTextSize(40);
		}

		public void setRenderContext(Args args) {
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
			TextPaint workPaint = mArgs.mPaintSet.getWorkPaint();
			workPaint.set(mDebugPaint);
			workPaint.setColor(BACKGROUND[mTaskId % BACKGROUND.length]);
			workPaint.setStyle(Paint.Style.FILL);
			Layout layout = paragraph.getLayout();
			mCanvas.drawRect(0, 0, mArgs.width, layout.getHeight(), workPaint);

			workPaint.setColor(Color.BLACK);
			mCanvas.drawText("task id: " + mTaskId + " " + layout.getAlgorithm(), 0, 40, workPaint);
		}

		@Override
		public void onVisitParagraphEnd(Paragraph paragraph) {
			TextPaint workPaint = mArgs.mPaintSet.getWorkPaint();
			workPaint.setStyle(Paint.Style.STROKE);
			workPaint.set(mDebugPaint);
			workPaint.setColor(Color.RED);
			workPaint.setStrokeWidth(10);
			mArgs.renderer.getLocationOnScreen(mLocation);
			int x = mArgs.width - 100;
			if (mArgs.selection == null) {
				return;
			}
			RectF first = mArgs.selection.getFirstRegion();
			RectF last = mArgs.selection.getLastRegion();
			mCanvas.drawLine(x,
					first != null ? first.left : -1,
					x,
					last != null ? last.right : -1,
					workPaint);
		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {

		}

		@Override
		public void onVisitLineEnd(Line line, float x, float y) {
			float startX = 0;
			float startY = y + mArgs.option.getLineSpace();
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
		public void onVisitBox(Box box, RectF inner, RectF outer) {
			mDebugPaint.setColor(Color.GREEN);
			mCanvas.drawRect(inner, mDebugPaint);
		}
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<Args> POOL = new ObjectPool<>(32);

		private ParagraphSelection selection;

		private ParagraphHighlight highlight;
		private Paragraph paragraph;
		private PaintSet mPaintSet;
		private RenderOption option;
		private TextureParagraph renderer;

		private ParagraphDecor decor;
		private int width;

		private Args() {
		}

		@Override
		public void recycle() {
			if (isRecycled()) {
				return;
			}

			selection = null;
			highlight = null;
			mPaintSet = null;
			decor = null;
			paragraph = null;
			option = null;
			renderer = null;
			width = 0;
			super.recycle();
			POOL.release(this);
		}

		public static Args obtain(@NonNull Paragraph source,
								  @NonNull RenderOption option,
								  @NonNull TextureParagraph render,
								  @IntRange(from = 1) int width,
								  @NonNull PaintSet paintSet) {
			return obtain(source, option, render, width, paintSet, null, null, null);
		}

		public static Args obtain(@NonNull Paragraph source,
								  @NonNull RenderOption option,
								  @NonNull TextureParagraph render,
								  @IntRange(from = 1) int width,
								  @NonNull PaintSet paintSet,
								  @Nullable ParagraphSelection selection,
								  @Nullable ParagraphHighlight highlight,
								  @Nullable ParagraphDecor decor) {
			Args args = POOL.acquire();
			if (args == null) {
				args = new Args();
			}

			args.selection = selection;
			args.highlight = highlight;
			args.mPaintSet = paintSet;
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
