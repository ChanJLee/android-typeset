package me.chan.texas.ext.markdown.math.renderer;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.ext.markdown.math.ast.AccentAtom;
import me.chan.texas.ext.markdown.math.ast.Ast;
import me.chan.texas.ext.markdown.math.ast.Atom;
import me.chan.texas.ext.markdown.math.ast.BinOpAtom;
import me.chan.texas.ext.markdown.math.ast.DelimitedAtom;
import me.chan.texas.ext.markdown.math.ast.Expression;
import me.chan.texas.ext.markdown.math.ast.FontAtom;
import me.chan.texas.ext.markdown.math.ast.FracAtom;
import me.chan.texas.ext.markdown.math.ast.FunctionCallAtom;
import me.chan.texas.ext.markdown.math.ast.GreekLetterAtom;
import me.chan.texas.ext.markdown.math.ast.GroupAtom;
import me.chan.texas.ext.markdown.math.ast.GroupScriptArg;
import me.chan.texas.ext.markdown.math.ast.LargeOperatorAtom;
import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.MatrixAtom;
import me.chan.texas.ext.markdown.math.ast.NumberAtom;
import me.chan.texas.ext.markdown.math.ast.ScriptArg;
import me.chan.texas.ext.markdown.math.ast.SingleTokenScriptArg;
import me.chan.texas.ext.markdown.math.ast.Spacing;
import me.chan.texas.ext.markdown.math.ast.SpecialSymbolAtom;
import me.chan.texas.ext.markdown.math.ast.SqrtAtom;
import me.chan.texas.ext.markdown.math.ast.SupSubSuffix;
import me.chan.texas.ext.markdown.math.ast.Term;
import me.chan.texas.ext.markdown.math.ast.TextAtom;
import me.chan.texas.ext.markdown.math.ast.VariableAtom;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;

public class MathRendererInflater {

	public RendererNode inflate(MathPaint.Styles styles, MathList mathList) {
		List<RendererNode> list = new ArrayList<>();
		for (Ast ast : mathList.elements) {
			if (ast instanceof Expression) {
				list.add(inflateExpression(styles, (Expression) ast));
			} else if (ast instanceof Spacing) {
				list.add(inflateSpacing(styles, (Spacing) ast));
			} else {
				throw new IllegalArgumentException("Unknown ast: " + ast);
			}
		}

		return new LinearGroupNode(styles, list, LinearGroupNode.Gravity.HORIZONTAL);
	}

	private RendererNode inflateExpression(MathPaint.Styles styles, Expression expression) {
		List<RendererNode> list = new ArrayList<>();
		for (Ast ast : expression.elements) {
			if (ast instanceof Term) {
				list.add(inflateTerm(styles, (Term) ast));
			} else if (ast instanceof BinOpAtom) {
				list.add(inflateBinOp(styles, (BinOpAtom) ast));
			} else {
				throw new IllegalArgumentException("Unknown ast: " + ast);
			}
		}
		return new LinearGroupNode(styles, list, LinearGroupNode.Gravity.HORIZONTAL);
	}

	private RendererNode inflateSpacing(MathPaint.Styles styles, Spacing spacing) {
		throw new RuntimeException("Stub!");
	}

	private RendererNode inflateTerm(MathPaint.Styles styles, Term term) {
		DecorGroupNode.Builder builder = new DecorGroupNode.Builder(styles, inflateAtom(styles, term.atom));

		if (term.unaryOp != null) {
			builder.left(inflateSymbol(styles, term.unaryOp.op));
		}

		SupSubSuffix suffix = term.suffix;
		if (suffix == null) {
			return builder.build();
		}

		ScriptArg scriptArg = suffix.superscript;
		if (scriptArg != null) {
			builder.rightTop(inflateScriptArg(new MathPaint.Styles(styles).setTextSizeFactor(0.4F), scriptArg));
		}

		scriptArg = suffix.subscript;
		if (scriptArg != null) {
			builder.rightBottom(inflateScriptArg(new MathPaint.Styles(styles).setTextSizeFactor(0.4F), scriptArg));
		}
		return builder.build();
	}

	private RendererNode inflateScriptArg(MathPaint.Styles styles, ScriptArg scriptArg) {
		if (scriptArg instanceof GroupScriptArg) {
			GroupScriptArg group = (GroupScriptArg) scriptArg;
			return inflateScriptArg(styles, group);
		}

		if (scriptArg instanceof SingleTokenScriptArg) {
			SingleTokenScriptArg token = (SingleTokenScriptArg) scriptArg;
			return inflateScriptArg(styles, token);
		}

		throw new IllegalArgumentException("Unknown script arg: " + scriptArg);
	}

	private RendererNode inflateScriptArg(MathPaint.Styles styles, GroupScriptArg group) {
		return inflate(styles, group.content.content);
	}

	private RendererNode inflateScriptArg(MathPaint.Styles styles, SingleTokenScriptArg singleTokenScriptArg) {
		String token = singleTokenScriptArg.token;
		if (token.startsWith("\\")) {
			return new SymbolNode(styles, MathFontOptions.symbol(token));
		}

		return new TextNode(styles, token);
	}

	private RendererNode inflateAtom(MathPaint.Styles styles, Atom atom) {
		if (atom instanceof NumberAtom) {
			return new TextNode(styles, ((NumberAtom) atom).value);
		}

		if (atom instanceof VariableAtom) {
			return new TextNode(styles, String.valueOf(((VariableAtom) atom).name));
		}

		if (atom instanceof GreekLetterAtom) {
			return new TextNode(styles, MathFontOptions.ast((GreekLetterAtom) atom));
		}

		if (atom instanceof GroupAtom) {
			return inflateGroupAtom(styles, (GroupAtom) atom);
		}

		if (atom instanceof FracAtom) {
			return inflateFracAtom(styles, (FracAtom) atom);
		}

		if (atom instanceof SqrtAtom) {
			return inflateSqrtAtom(styles, (SqrtAtom) atom);
		}

		if (atom instanceof DelimitedAtom) {
			return inflateDelimitedAtom(styles, (DelimitedAtom) atom);
		}

		if (atom instanceof FunctionCallAtom) {
			return inflateFunctionCallAtom(styles, (FunctionCallAtom) atom);
		}

		if (atom instanceof LargeOperatorAtom) {
			return inflateLargeOperatorAtom(styles, (LargeOperatorAtom) atom);
		}

		if (atom instanceof MatrixAtom) {
			return inflateMatrixAtom(styles, (MatrixAtom) atom);
		}

		if (atom instanceof TextAtom) {
			return inflateTextAtom(styles, (TextAtom) atom);
		}

		if (atom instanceof AccentAtom) {
			return inflateAccentAtom(styles, (AccentAtom) atom);
		}

		if (atom instanceof FontAtom) {
			return inflateFontAtom(styles, (FontAtom) atom);
		}

		if (atom instanceof SpecialSymbolAtom) {
			return inflateSpecialSymbolAtom(styles, (SpecialSymbolAtom) atom);
		}

		throw new IllegalArgumentException("Unknown atom: " + atom);
	}

	private RendererNode inflateSpecialSymbolAtom(MathPaint.Styles styles, SpecialSymbolAtom atom) {
		throw new RuntimeException("Stub!");
	}

	private RendererNode inflateFontAtom(MathPaint.Styles styles, FontAtom atom) {
		throw new RuntimeException("Stub!");
	}

	private RendererNode inflateTextAtom(MathPaint.Styles styles, TextAtom atom) {
		throw new RuntimeException("Stub!");
	}

	private RendererNode inflateMatrixAtom(MathPaint.Styles styles, MatrixAtom atom) {
		throw new RuntimeException("Stub!");
	}

	private RendererNode inflateLargeOperatorAtom(MathPaint.Styles styles, LargeOperatorAtom atom) {
		throw new RuntimeException("Stub!");
	}

	private RendererNode inflateDelimitedAtom(MathPaint.Styles styles, DelimitedAtom atom) {
		return new BraceLayout(
				styles, atom.level,
				inflateDelimiter(styles, atom.leftDelimiter),
				inflate(styles, atom.content),
				inflateDelimiter(styles, atom.rightDelimiter)
		);
	}

	private RendererNode inflateDelimiter(MathPaint.Styles styles, String delimiter) {
		if (delimiter == null || delimiter.isEmpty() || ".".equals(delimiter)) {
			return null;
		}

		if ("\\lfloor".equals(delimiter)) {
			return new StretchyNode(styles,
					MathFontOptions.symbol("uni23A2"),
					MathFontOptions.symbol("uni23A2"),
					MathFontOptions.symbol("uni23A3"),
					MathFontOptions.symbol("uni23A2")
			);
		}
		if ("\\rfloor".equals(delimiter)) {
			return new StretchyNode(styles,
					MathFontOptions.symbol("uni23A5"),
					MathFontOptions.symbol("uni23A5"),
					MathFontOptions.symbol("uni23A6"),
					MathFontOptions.symbol("uni23A5")
			);
		}

		if ("\\lceil".equals(delimiter)) {
			return new StretchyNode(styles,
					MathFontOptions.symbol("uni23A1"),
					MathFontOptions.symbol("uni23A2"),
					MathFontOptions.symbol("uni23A2"),
					MathFontOptions.symbol("uni23A2")
			);
		}
		if ("\\rceil".equals(delimiter)) {
			return new StretchyNode(styles,
					MathFontOptions.symbol("uni23A4"),
					MathFontOptions.symbol("uni23A5"),
					MathFontOptions.symbol("uni23A5"),
					MathFontOptions.symbol("uni23A5")
			);
		}

		throw new IllegalArgumentException("unknown delimiter: " + delimiter);
	}

	private RendererNode inflateFunctionCallAtom(MathPaint.Styles styles, FunctionCallAtom functionCallAtom) {
		throw new RuntimeException("Stub!");
	}

	private RendererNode inflateSqrtAtom(MathPaint.Styles styles, SqrtAtom sqrtAtom) {
		RendererNode root = null;
		if (sqrtAtom.root != null) {
			root = inflate(new MathPaint.Styles(styles).setTextSizeFactor(0.4F), sqrtAtom.root);
		}
		return new SqrtNode(styles, root, inflate(styles, sqrtAtom.content));
	}

	private RendererNode inflateFracAtom(MathPaint.Styles styles, FracAtom atom) {
		return new FractionNode(styles, inflate(styles, atom.numerator), inflate(styles, atom.denominator));
	}

	private RendererNode inflateGroupAtom(MathPaint.Styles styles, GroupAtom groupAtom) {
		return inflate(styles, groupAtom.content);
	}

	private RendererNode inflateAccentAtom(MathPaint.Styles styles, AccentAtom accentAtom) {
		return new AccentNode(styles, accentAtom.cmd, inflate(styles, accentAtom.content));
	}

	private RendererNode inflateBinOp(MathPaint.Styles styles, BinOpAtom atom) {
		return inflateSymbol(styles, atom.getOp());
	}

	private RendererNode inflateSymbol(MathPaint.Styles styles, String symbol) {
		if (symbol.startsWith("\\")) {
			symbol = symbol.substring(1);
			Symbol s = MathFontOptions.symbol(symbol);
			if (s != null) {
				return new SymbolNode(styles, s);
			}

			String o = MathFontOptions.textOp(symbol);
			if (o != null) {
				return new TextNode(styles, o);
			}

			throw new IllegalArgumentException("unknown symbol");
		}

		return new TextNode(styles, symbol);
	}
}
