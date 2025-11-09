package me.chan.texas.ext.markdown.math.renderer;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.ext.markdown.math.ast.AccentAtom;
import me.chan.texas.ext.markdown.math.ast.Ast;
import me.chan.texas.ext.markdown.math.ast.Atom;
import me.chan.texas.ext.markdown.math.ast.BinOpAtom;
import me.chan.texas.ext.markdown.math.ast.DelimitedAtom;
import me.chan.texas.ext.markdown.math.ast.FracAtom;
import me.chan.texas.ext.markdown.math.ast.FunctionCallAtom;
import me.chan.texas.ext.markdown.math.ast.GreekLetterAtom;
import me.chan.texas.ext.markdown.math.ast.GroupAtom;
import me.chan.texas.ext.markdown.math.ast.GroupScriptArg;
import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.NumberAtom;
import me.chan.texas.ext.markdown.math.ast.ScriptArg;
import me.chan.texas.ext.markdown.math.ast.SingleTokenScriptArg;
import me.chan.texas.ext.markdown.math.ast.SqrtAtom;
import me.chan.texas.ext.markdown.math.ast.SupSubSuffix;
import me.chan.texas.ext.markdown.math.ast.Term;
import me.chan.texas.ext.markdown.math.ast.VariableAtom;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;

public class MathRendererInflater {

	public RendererNode inflate(MathList math) {
		return inflate0(1, math);
	}

	public RendererNode inflate0(float scale, MathList math) {
		List<RendererNode> list = new ArrayList<>();
		for (Ast ast : math.getAst()) {
			if (ast instanceof Term) {
				inflateTerm(list, scale, (Term) ast);
			} else if (ast instanceof BinOpAtom) {
				inflateBinOp(list, scale, (BinOpAtom) ast);
			} else {
				throw new IllegalArgumentException("Unknown ast: " + ast);
			}
		}

		return new LinearGroupNode(1, list, LinearGroupNode.Gravity.HORIZONTAL);
	}

	private void inflateTerm(List<RendererNode> nodes, float scale, Term term) {
		String op = term.getUnaryOp();
		if (op != null) {
			inflateSymbol(nodes, scale, op);
		}

		Atom atom = term.getAtom();
		DecorGroupNode.Builder builder = new DecorGroupNode.Builder(scale, inflateAtom(scale, atom));
		SupSubSuffix suffix = term.getSuffix();
		if (suffix == null) {
			nodes.add(builder.build());
			return;
		}

		ScriptArg scriptArg = suffix.getSuperscript();
		if (scriptArg != null) {
			builder.rightTop(inflateScriptArg(scale * 0.4f, scriptArg));
		}

		scriptArg = suffix.getSubscript();
		if (scriptArg != null) {
			builder.rightBottom(inflateScriptArg(scale * 0.4f, scriptArg));
		}
		nodes.add(builder.build());
	}

	private RendererNode inflateScriptArg(float scale, ScriptArg scriptArg) {
		if (scriptArg instanceof GroupScriptArg) {
			GroupScriptArg group = (GroupScriptArg) scriptArg;
			return inflateScriptArg(scale, group);
		}

		if (scriptArg instanceof SingleTokenScriptArg) {
			SingleTokenScriptArg token = (SingleTokenScriptArg) scriptArg;
			return inflateScriptArg(scale, token);
		}

		throw new IllegalArgumentException("Unknown script arg: " + scriptArg);
	}

	private RendererNode inflateScriptArg(float scale, GroupScriptArg group) {
		return inflate0(scale, group.getContent());
	}

	private RendererNode inflateScriptArg(float scale, SingleTokenScriptArg singleTokenScriptArg) {
		String token = singleTokenScriptArg.getToken();
		if (token.startsWith("\\")) {
			return new SymbolNode(scale, MathFontOptions.symbol(token));
		}

		return new TextNode(scale, token);
	}

	private RendererNode inflateAtom(float scale, Atom atom) {
		if (atom instanceof NumberAtom) {
			return new TextNode(scale, ((NumberAtom) atom).value);
		}

		if (atom instanceof VariableAtom) {
			return new TextNode(scale, String.valueOf(((VariableAtom) atom).name));
		}

		if (atom instanceof GreekLetterAtom) {
			return new SymbolNode(scale, MathFontOptions.ast((GreekLetterAtom) atom));
		}

		if (atom instanceof GroupAtom) {
			return inflateGroupAtom(scale, (GroupAtom) atom);
		}

		if (atom instanceof FracAtom) {
			return inflateFracAtom(scale, (FracAtom) atom);
		}

		if (atom instanceof SqrtAtom) {
			return inflateSqrtAtom(scale, (SqrtAtom) atom);
		}

		if (atom instanceof DelimitedAtom) {
			return inflateDelimitedAtom(scale, (DelimitedAtom) atom);
		}

		if (atom instanceof AccentAtom) {
			return inflateAccentAtom(scale, (AccentAtom) atom);
		}

		throw new IllegalArgumentException("Unknown atom: " + atom);
	}

	private RendererNode inflateDelimitedAtom(float scale, DelimitedAtom atom) {
//		List<RendererNode> nodes = new ArrayList<>();
//		if (atom.level == DelimitedAtom.LEVEL_L0) {
//
//		}
//		return new LinearGroupNode(scale, nodes, LinearGroupNode.Gravity.HORIZONTAL);
		throw new RuntimeException("Stub!");
	}

	private RendererNode inflateFunctionCallAtom(float scale, FunctionCallAtom functionCallAtom) {
		throw new RuntimeException("Stub!");
	}

	private RendererNode inflateSqrtAtom(float scale, SqrtAtom sqrtAtom) {
		RendererNode root = null;
		if (sqrtAtom.root != null) {
			root = inflate0(scale * 0.4f, sqrtAtom.root);
		}
		return new SqrtNode(scale, root, inflate0(scale, sqrtAtom.content));
	}

	private RendererNode inflateFracAtom(float scale, FracAtom atom) {
		return new FractionNode(scale, inflate0(scale, atom.numerator), inflate0(scale, atom.denominator));
	}

	private RendererNode inflateGroupAtom(float scale, GroupAtom groupAtom) {
		return inflate0(scale, groupAtom.getContent());
	}

	private RendererNode inflateAccentAtom(float scale, AccentAtom accentAtom) {
		return new AccentNode(scale, accentAtom.getAccentCmd(), inflateAst(scale, accentAtom.getContent()));
	}

	private RendererNode inflateAst(float scale, Ast ast) {
		throw new RuntimeException("Stub!");
	}

	private void inflateBinOp(List<RendererNode> nodes, float scale, BinOpAtom atom) {
		inflateSymbol(nodes, scale, atom.getOp());
	}

	private void inflateSymbol(List<RendererNode> nodes, float scale, String symbol) {
		if (symbol.startsWith("\\")) {
			symbol = symbol.substring(1);
			Symbol s = MathFontOptions.symbol(symbol);
			if (s != null) {
				nodes.add(new SymbolNode(scale, s));
				return;
			}

			String o = MathFontOptions.textOp(symbol);
			if (o != null) {
				nodes.add(new TextNode(scale, o));
			}

			throw new IllegalArgumentException("unknown symbol");
		}

		nodes.add(new TextNode(scale, symbol));
	}
}
