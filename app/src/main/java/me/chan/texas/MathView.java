package me.chan.texas;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.chan.texas.ext.markdown.math.ast.MathParseException;
import me.chan.texas.ext.markdown.math.ast.MathParser;
import me.chan.texas.ext.markdown.math.renderer.AccentNode;
import me.chan.texas.ext.markdown.math.renderer.BraceLayout;
import me.chan.texas.ext.markdown.math.renderer.FractionNode;
import me.chan.texas.ext.markdown.math.renderer.GridGroupNode;
import me.chan.texas.ext.markdown.math.renderer.LinearGroupNode;
import me.chan.texas.ext.markdown.math.renderer.MathRendererInflater;
import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.SpaceNode;
import me.chan.texas.ext.markdown.math.renderer.SqrtNode;
import me.chan.texas.ext.markdown.math.renderer.StretchyTripeNode;
import me.chan.texas.ext.markdown.math.renderer.SymbolNode;
import me.chan.texas.ext.markdown.math.renderer.TextNode;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvasImpl;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;
import me.chan.texas.ext.markdown.math.renderer.fonts.SymbolOptions;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.graphics.TexasCanvasImpl;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;
import me.chan.texas.utils.CharStream;

public class MathView extends View {

	private RendererNode mRendererNode;

	private MathPaint mTexasPaint;
	private MathCanvas mCanvas;

	public MathView(Context context, @Nullable AttributeSet attrs) throws IOException {
		super(context, attrs);
		TextPaint textPaint = new TextPaint();

		textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "texas_markdown_ext/latinmodern-math.otf"));
		textPaint.setTextSize(16);
		textPaint.setStyle(Paint.Style.FILL);

//		mRendererNode = new TextNode(new MathPaint.Styles(mTexasPaint), "Hello World!");
//		mRendererNode = RendererNodeInflater.mockText("ABgface + x + y");
//		mRendererNode.setScale(0.5f);
//		mRendererNode = RendererNodeInflater.mockSqrt();
//		mRendererNode = RendererNodeInflater.mockFractionNode();
//		{
//			mRendererNode = RendererNodeInflater.mockText();
//			textPaint.setTextSize(48);
//		}
//

		TexasPaintImpl paint = new TexasPaintImpl();
		paint.reset(new PaintSet(textPaint));
		mTexasPaint = new MathPaintImpl(paint);

		mCanvas = new MathCanvasImpl(new TexasCanvasImpl());

		{
			textPaint.setTextSize(128);
			mRendererNode = mockDecor();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mRendererNode.measure(mTexasPaint);
		super.onMeasure(MeasureSpec.makeMeasureSpec(Math.max(MeasureSpec.getSize(widthMeasureSpec), mRendererNode.getWidth()), MeasureSpec.EXACTLY),
				heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mRendererNode.layout(0, 0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mCanvas.reset(canvas);
		mRendererNode.draw(mCanvas, mTexasPaint);
	}

	public void setRendererNode(RendererNode rendererNode) {
		mRendererNode = rendererNode;
		requestLayout();
	}


	@VisibleForTesting
	public RendererNode mockText(String text) {
		return new TextNode(new MathPaint.Styles(mTexasPaint), text);
	}

	@VisibleForTesting
	public RendererNode mockText(MathPaint.Styles scale, String text) {
		return new TextNode(scale, text);
	}

	public RendererNode mockAccent() {
		List<RendererNode> list = new ArrayList<>();
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "hat", mockText("hat")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "widehat", mockText("widehat")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "tilde", mockText("tilde")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "widetilde", mockText("widetilde")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "dot", mockText("dot")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "ddot", mockText("ddot")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "dddot", mockText("dddot")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "acute", mockText("acute")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "grave", mockText("grave")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "breve", mockText("breve")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "check", mockText("hello world")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "underbrace", mockText("hello world")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "overbrace", mockText("hello world")));
		return new LinearGroupNode(new MathPaint.Styles(mTexasPaint), list);
	}

	public RendererNode mockFractionNode() {
		return new FractionNode(new MathPaint.Styles(mTexasPaint), mockText(" 1 + 2"), new SqrtNode(new MathPaint.Styles(mTexasPaint), mockText("2"), mockText(new MathPaint.Styles(mTexasPaint), "3")));
	}


	public RendererNode mockGrid() {
		List<RendererNode> list = new ArrayList<>();
		list.add(mockText("Hello World"));
		list.add(new SqrtNode(new MathPaint.Styles(mTexasPaint), mockText("8"), mockText(new MathPaint.Styles(mTexasPaint).setTextSize(0.4f * mTexasPaint.getTextSize()), "2")));
		list.add(mockFractionNode());

		list.add(mockFractionNode());
		list.add(new SqrtNode(new MathPaint.Styles(mTexasPaint), mockText("9"), mockText(new MathPaint.Styles(mTexasPaint).setTextSize(0.4f * mTexasPaint.getTextSize()), "5")));
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "underbrace", mockText("hello world")));

		list.add(mockFractionNode());
		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "overbrace", mockText("hello world")));

		return new GridGroupNode(new MathPaint.Styles(mTexasPaint), 3, list);
	}

	public RendererNode mockBrace() {
		return new BraceLayout(new MathPaint.Styles(mTexasPaint), 1, mockStretchyLeft(), mockGrid(), mockStretchyRight());
	}

	public RendererNode mockCondition() {
		List<RendererNode> rendererNodes = new ArrayList<>();
		rendererNodes.add(new TextNode(new MathPaint.Styles(mTexasPaint), "f(x)"));
		rendererNodes.add(new SpaceNode(new MathPaint.Styles(mTexasPaint).setTextSize(100f), 1, 1));
		rendererNodes.add(new BraceLayout(new MathPaint.Styles(mTexasPaint), 1, mockStretchyLeft(), mockConditionDetail(), mockStretchyRight()));
		return new LinearGroupNode(new MathPaint.Styles(mTexasPaint), rendererNodes);
	}

	public StretchyTripeNode mockStretchyLeft() {
		return new StretchyTripeNode(new MathPaint.Styles(mTexasPaint), MathFontOptions.symbol("uni23A7"), MathFontOptions.symbol("uni23A8"), MathFontOptions.symbol("uni23A9"), MathFontOptions.symbol("uni23AA"));
	}

	public RendererNode mockStretchyBenchmark() {
		List<RendererNode> list = new ArrayList<>();
		list.add(new TextNode(new MathPaint.Styles(mTexasPaint), "{"));
		list.add(mockStretchyLeft());
		return new LinearGroupNode(new MathPaint.Styles(mTexasPaint), list);
	}

	public StretchyTripeNode mockStretchyRight() {
		return new StretchyTripeNode(new MathPaint.Styles(mTexasPaint), MathFontOptions.symbol("uni23AB"), MathFontOptions.symbol("uni23AC"), MathFontOptions.symbol("uni23AD"), MathFontOptions.symbol("uni23AA"));
	}

	private RendererNode mockConditionDetail() {
		List<RendererNode> list = new ArrayList<>();
		list.add(mockText("Hello World"));
		list.add(new SpaceNode(new MathPaint.Styles(mTexasPaint).setTextSize(100f), 1, 1));
		list.add(mockText("x < 0"));

		list.add(mockFractionNode());
		list.add(new SpaceNode(new MathPaint.Styles(mTexasPaint).setTextSize(100f), 1, 1));
		list.add(mockText("x >= 0 且 x < 10"));

		list.add(new AccentNode(new MathPaint.Styles(mTexasPaint), "underbrace", mockText("hello world")));
		list.add(new SpaceNode(new MathPaint.Styles(mTexasPaint).setTextSize(100f), 1, 1));
		list.add(mockText("x >= 10"));

		return new GridGroupNode(new MathPaint.Styles(mTexasPaint), 3, list);
	}

	public RendererNode mockList() {
		List<RendererNode> list = new ArrayList<>();
		SymbolOptions options = new SymbolOptions();
		for (Map.Entry<String, Symbol> entry : options.Po.entrySet()) {
			list.add(new TextNode(new MathPaint.Styles(mTexasPaint), entry.getKey()));
			list.add(new SymbolNode(new MathPaint.Styles(mTexasPaint), entry.getValue()));
			Log.d("chan_debug", "key: " + entry.getKey() + " value: " + entry.getValue());
		}
		return new LinearGroupNode(new MathPaint.Styles(mTexasPaint), list);
	}

	@VisibleForTesting
	public SqrtNode mockSqrt(MathPaint.Styles scale, RendererNode content) {
		return new SqrtNode(scale, content, mockText(new MathPaint.Styles(scale).setTextSize(scale.getTextSize() * 0.3f), "x + y + z"));
	}

	public RendererNode mockDecor() {
		MathParser parser = new MathParser(new CharStream("\\cos \\left ( x + y \\right )"));
		MathRendererInflater inflater = new MathRendererInflater();
		try {
			return inflater.inflate(new MathPaint.Styles(mTexasPaint), parser.parse());
		} catch (MathParseException e) {
			throw new RuntimeException(e);
		}
	}
}