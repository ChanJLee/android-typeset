package me.chan.texas.ext.markdown.math;

import android.graphics.Typeface;

import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.MathParseException;
import me.chan.texas.ext.markdown.math.ast.MathParser;
import me.chan.texas.ext.markdown.math.renderer.LinearGroupNode;
import me.chan.texas.ext.markdown.math.renderer.MathRendererInflater;
import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;
import me.chan.texas.text.Paragraph;
import me.chan.texas.utils.CharStream;

public class TexMathParser {
	private final MathPaint mPaint;

	public TexMathParser(TexasOption option) {
		TexasPaintImpl paint = new TexasPaintImpl();
		PaintSet paintSet = new PaintSet(option.getPaintSet());
		paintSet.getPaint().setTypeface(getTypeface());
		paint.reset(paintSet);
		mPaint = new MathPaintImpl(paint);
	}

	public void parse(String math, Paragraph.Builder builder) throws MathParseException {
		MathParser mathParser = new MathParser(new CharStream(math));
		MathList list = mathParser.parse();
		MathRendererInflater inflater = new MathRendererInflater();
		RendererNode rendererNode = inflater.inflate(new MathPaint.Styles(mPaint), list);

		if (rendererNode instanceof LinearGroupNode) {
			LinearGroupNode linearGroupNode = (LinearGroupNode) rendererNode;
			if (linearGroupNode.getGravity() == LinearGroupNode.Gravity.HORIZONTAL) {
				appendLinearGroupNode(builder, linearGroupNode);
				return;
			}
		}
		append(builder, rendererNode);
	}

	private void appendLinearGroupNode(Paragraph.Builder builder, LinearGroupNode node) {
		for (int i = 0; i < node.getChildCount(); ++i) {
			RendererNode child = node.getChildAt(i);
			append(builder, child);
		}
	}

	private void append(Paragraph.Builder builder, RendererNode node) {
		builder.hyperSpan(new MathBox(node, mPaint));
	}

	private volatile static Typeface sTypeface;

	public synchronized static Typeface getTypeface() {
		if (sTypeface == null) {
			sTypeface = Typeface.createFromAsset(Texas.getAppContext().getAssets(), "texas_markdown_ext/latinmodern-math.otf");
		}
		return sTypeface;
	}
}
