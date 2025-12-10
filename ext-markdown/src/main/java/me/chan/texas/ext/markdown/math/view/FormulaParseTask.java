package me.chan.texas.ext.markdown.math.view;

import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.MathParseException;
import me.chan.texas.ext.markdown.math.ast.MathParser;
import me.chan.texas.ext.markdown.math.renderer.MathRendererInflater;
import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.utils.CharStream;
import me.chan.texas.utils.concurrency.Worker;

public class FormulaParseTask extends Worker.Task<ParseArgs, RendererNode> {
	public static final int TYPE_SUCCESS = 1;

	public static final int TYPE_ERROR = 2;

	public static final int TYPE_START = 3;

	private final MsgHandler mMsgHandler;

	public FormulaParseTask(MsgHandler msgHandler) {
		mMsgHandler = msgHandler;
	}

	@Override
	public void onStart(Worker.Token token, ParseArgs args) {
		MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_START, args, null);
		mMsgHandler.send(token, message);
	}

	@Override
	public void onSuccess(Worker.Token token, ParseArgs args, RendererNode ret) {
		MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_SUCCESS, args, ret);
		mMsgHandler.send(token, message);
	}

	@Override
	public void onError(Worker.Token token, ParseArgs args, Throwable error) {
		MsgHandler.Msg message = MsgHandler.Msg.obtain(TYPE_ERROR, args, error);
		mMsgHandler.send(token, message);
	}

	@Override
	protected RendererNode onExec(Worker.Token token, ParseArgs args) throws MathParseException {
		CharStream stream = new CharStream(args.formula);
		MathParser mathParser = new MathParser(stream);
		MathList mathList = mathParser.parse();

		MathRendererInflater inflater = new MathRendererInflater();
		MathPaint.Styles styles = new MathPaint.Styles(args.paint);
		RendererNode rendererNode = inflater.inflate(styles, mathList);
		rendererNode.measure(args.paint);
		rendererNode.layout(0, 0);
		return rendererNode;
	}
}
