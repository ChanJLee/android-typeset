package me.chan.texas.ext.markdown.math.renderer;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.ext.markdown.math.ast.AccentAtom;
import me.chan.texas.ext.markdown.math.ast.Ast;
import me.chan.texas.ext.markdown.math.ast.Atom;
import me.chan.texas.ext.markdown.math.ast.BinOpAtom;
import me.chan.texas.ext.markdown.math.ast.GroupScriptArg;
import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.ScriptArg;
import me.chan.texas.ext.markdown.math.ast.SingleTokenScriptArg;
import me.chan.texas.ext.markdown.math.ast.SupSubSuffix;
import me.chan.texas.ext.markdown.math.ast.Term;
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
		if (atom instanceof AccentAtom) {
			AccentAtom accentAtom = (AccentAtom) atom;
			return inflateAccentAtom(scale, accentAtom);
		}

		throw new IllegalArgumentException("Unknown atom: " + atom);
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
