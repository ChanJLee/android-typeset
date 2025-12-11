package me.chan.texas.ext.markdown.math.view;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;

import me.chan.texas.ext.markdown.math.ast.Expression;
import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.MathParseException;
import me.chan.texas.ext.markdown.math.ast.MathParser;
import me.chan.texas.ext.markdown.math.ast.Term;
import me.chan.texas.ext.markdown.math.ast.TextAtom;
import me.chan.texas.ext.markdown.math.renderer.MathRendererInflater;
import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.utils.CharStream;
import me.chan.texas.utils.concurrency.Worker;

public class FormulaBackgroundTask extends Worker.Task<FormulaBackgroundTask.BackgroundArgs, FormulaBackgroundTask.Result> {
	public static final int TYPE_SUCCESS = 1;

	public static final int TYPE_ERROR = 2;

	public static final int TYPE_START = 3;

	private final MsgHandler mMsgHandler;

	public FormulaBackgroundTask(MsgHandler msgHandler) {
		mMsgHandler = msgHandler;
	}

	@Override
	public void onStart(Worker.Token token, BackgroundArgs args) {
		MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_START, args, null);
		mMsgHandler.send(token, message);
	}

	@Override
	public void onSuccess(Worker.Token token, BackgroundArgs args, Result ret) {
		MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_SUCCESS, args, ret);
		mMsgHandler.send(token, message);
	}

	@Override
	public void onError(Worker.Token token, BackgroundArgs args, Throwable error) {
		MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_ERROR, args, error);
		mMsgHandler.send(token, message);
	}

	@Override
	protected Result onExec(Worker.Token token, BackgroundArgs args) throws MathParseException {
		RendererNode node = args.node;
		if (node == null) {
			node = prepare(args.formula, args.paint);
		}

		draw(args.paint, args.canvas, args.renderer, node);

		return new Result(args, node);
	}

	private RendererNode prepare(String formula, MathPaint paint) {
		MathList mathList = parse(formula);

		MathRendererInflater inflater = new MathRendererInflater();
		MathPaint.Styles styles = new MathPaint.Styles(paint);
		RendererNode rendererNode = inflater.inflate(styles, mathList);
		rendererNode.measure(paint);
		rendererNode.layout(0, 0);

		return rendererNode;
	}

	private MathList parse(String formula) {
		try {
			CharStream stream = new CharStream(formula);
			MathParser mathParser = new MathParser(stream);
			return mathParser.parse();
		} catch (MathParseException e) {
			return error(e.pretty());
		} catch (Throwable throwable) {
			return error(throwable.getMessage());
		}
	}

	private MathList error(String msg) {
		return new MathList(Collections.singletonList(
				new Expression(Collections.singletonList(
						new Term(
								null,
								new TextAtom("textfield", msg),
								null
						)
				))
		));
	}

	private void draw(MathPaint paint, MathCanvas mathCanvas, AsyncMathViewRenderer renderer, RendererNode node) {
		Canvas canvas = renderer.lockCanvas(node.getWidth(), node.getHeight());
		if (canvas == null) {
			return;
		}

		mathCanvas.reset(canvas);
		node.draw(mathCanvas, paint);
		renderer.unlockCanvasAndPost(canvas);
	}

	public static class BackgroundArgs {
		public final String formula;
		public final MathPaint paint;
		public final MathCanvas canvas;
		public final AsyncMathViewRenderer renderer;
		@Nullable
		public final RendererNode node;

		public BackgroundArgs(String formula, MathPaint paint, MathCanvas canvas, AsyncMathViewRenderer renderer) {
			this(formula, paint, canvas, renderer, null);
		}

		public BackgroundArgs(String formula, MathPaint paint, MathCanvas canvas,
							  AsyncMathViewRenderer renderer,
							  @Nullable RendererNode node) {
			this.formula = formula;
			this.paint = paint;
			this.canvas = canvas;
			this.renderer = renderer;
			this.node = node;
		}
	}

	public static class Result {
		public final BackgroundArgs args;
		public final RendererNode rendererNode;

		public Result(BackgroundArgs args, RendererNode rendererNode) {
			this.args = args;
			this.rendererNode = rendererNode;
		}

		@NonNull
		@Override
		public String toString() {
			return "Result{" +
					"args=" + args.formula +
					'}';
		}
	}
}
