package me.chan.texas.ext.markdown.math.renderer;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.ext.markdown.math.ast.AccentAtom;
import me.chan.texas.ext.markdown.math.ast.Ast;
import me.chan.texas.ext.markdown.math.ast.Atom;
import me.chan.texas.ext.markdown.math.ast.BinOpAtom;
import me.chan.texas.ext.markdown.math.ast.DelimitedAtom;
import me.chan.texas.ext.markdown.math.ast.Expression;
import me.chan.texas.ext.markdown.math.ast.FracAtom;
import me.chan.texas.ext.markdown.math.ast.FunctionCallAtom;
import me.chan.texas.ext.markdown.math.ast.GreekLetterAtom;
import me.chan.texas.ext.markdown.math.ast.GroupAtom;
import me.chan.texas.ext.markdown.math.ast.GroupScriptArg;
import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.NumberAtom;
import me.chan.texas.ext.markdown.math.ast.ScriptArg;
import me.chan.texas.ext.markdown.math.ast.SingleTokenScriptArg;
import me.chan.texas.ext.markdown.math.ast.Spacing;
import me.chan.texas.ext.markdown.math.ast.SqrtAtom;
import me.chan.texas.ext.markdown.math.ast.SupSubSuffix;
import me.chan.texas.ext.markdown.math.ast.Term;
import me.chan.texas.ext.markdown.math.ast.VariableAtom;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;

public class MathRendererInflater {

	public RendererNode inflate(MathPaint.Styles styles, MathList math) {
		List<RendererNode> list = new ArrayList<>();
		for (Ast ast : math.elements) {
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

	}

	private RendererNode inflateTerm(MathPaint.Styles styles, Term term) {
		Atom atom = term.getAtom();
		DecorGroupNode.Builder builder = new DecorGroupNode.Builder(styles, inflateAtom(styles, atom));

		String op = term.getUnaryOp();
		if (op != null) {
			builder.left(inflateSymbol(styles, op));
		}

		SupSubSuffix suffix = term.getSuffix();
		if (suffix == null) {
			return builder.build();
		}

		ScriptArg scriptArg = suffix.getSuperscript();
		if (scriptArg != null) {
			builder.rightTop(inflateScriptArg(new MathPaint.Styles(styles).setTextSize(styles.getTextSize() * 0.4f), scriptArg));
		}

		scriptArg = suffix.getSubscript();
		if (scriptArg != null) {
			builder.rightBottom(inflateScriptArg(new MathPaint.Styles(styles).setTextSize(styles.getTextSize() * 0.4f), scriptArg));
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
		return inflate(styles, group.getContent());
	}

	private RendererNode inflateScriptArg(MathPaint.Styles styles, SingleTokenScriptArg singleTokenScriptArg) {
		String token = singleTokenScriptArg.getToken();
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
			return new SymbolNode(styles, MathFontOptions.ast((GreekLetterAtom) atom));
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

		if (atom instanceof AccentAtom) {
			return inflateAccentAtom(styles, (AccentAtom) atom);
		}

		throw new IllegalArgumentException("Unknown atom: " + atom);
	}

	private RendererNode inflateDelimitedAtom(MathPaint.Styles styles, DelimitedAtom atom) {
//		List<RendererNode> nodes = new ArrayList<>();
//		if (atom.level == DelimitedAtom.LEVEL_L0) {
//
//		}
//		return new LinearGroupNode( styles, nodes, LinearGroupNode.Gravity.HORIZONTAL);
		throw new RuntimeException("Stub!");
	}

	private RendererNode inflateFunctionCallAtom(MathPaint.Styles styles, FunctionCallAtom functionCallAtom) {
		throw new RuntimeException("Stub!");
	}

	private RendererNode inflateSqrtAtom(MathPaint.Styles styles, SqrtAtom sqrtAtom) {
		RendererNode root = null;
		if (sqrtAtom.root != null) {
			root = inflate(new MathPaint.Styles(styles).setTextSize(styles.getTextSize() * 0.4f), sqrtAtom.root);
		}
		return new SqrtNode(styles, root, inflate(styles, sqrtAtom.content));
	}

	private RendererNode inflateFracAtom(MathPaint.Styles styles, FracAtom atom) {
		return new FractionNode(styles, inflate(styles, atom.numerator), inflate(styles, atom.denominator));
	}

	private RendererNode inflateGroupAtom(MathPaint.Styles styles, GroupAtom groupAtom) {
		return inflate(styles, groupAtom.getContent());
	}

	private RendererNode inflateAccentAtom(MathPaint.Styles styles, AccentAtom accentAtom) {
		return new AccentNode(styles, accentAtom.getCmd(), inflateAst(styles, accentAtom.getContent()));
	}

	private RendererNode inflateAst(MathPaint.Styles styles, Ast ast) {
		throw new RuntimeException("Stub!");
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
