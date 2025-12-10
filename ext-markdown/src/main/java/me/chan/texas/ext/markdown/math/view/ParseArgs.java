package me.chan.texas.ext.markdown.math.view;

import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class ParseArgs {
	public final String formula;
	public final MathPaint paint;

	public ParseArgs(String formula, MathPaint paint) {
		this.formula = formula;
		this.paint = paint;
	}
}