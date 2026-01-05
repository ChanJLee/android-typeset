package me.chan.texas.ext.markdown.math.renderer;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.ext.markdown.math.ast.AccentAtom;
import me.chan.texas.ext.markdown.math.ast.Ast;
import me.chan.texas.ext.markdown.math.ast.Atom;
import me.chan.texas.ext.markdown.math.ast.BinomAtom;
import me.chan.texas.ext.markdown.math.ast.DelimitedAtom;
import me.chan.texas.ext.markdown.math.ast.ExtensibleArrowAtom;
import me.chan.texas.ext.markdown.math.ast.FontAtom;
import me.chan.texas.ext.markdown.math.ast.FracAtom;
import me.chan.texas.ext.markdown.math.ast.FunctionCallAtom;
import me.chan.texas.ext.markdown.math.ast.GreekLetterVariableAtom;
import me.chan.texas.ext.markdown.math.ast.Group;
import me.chan.texas.ext.markdown.math.ast.LargeOperatorAtom;
import me.chan.texas.ext.markdown.math.ast.Length;
import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.MatrixAtom;
import me.chan.texas.ext.markdown.math.ast.MatrixRow;
import me.chan.texas.ext.markdown.math.ast.NumberAtom;
import me.chan.texas.ext.markdown.math.ast.PunctuationAtom;
import me.chan.texas.ext.markdown.math.ast.ScriptArg;
import me.chan.texas.ext.markdown.math.ast.SingleToken;
import me.chan.texas.ext.markdown.math.ast.Spacing;
import me.chan.texas.ext.markdown.math.ast.SpecialLetterVariableAtom;
import me.chan.texas.ext.markdown.math.ast.SpecialSymbolAtom;
import me.chan.texas.ext.markdown.math.ast.SqrtAtom;
import me.chan.texas.ext.markdown.math.ast.SupSubSuffix;
import me.chan.texas.ext.markdown.math.ast.SymbolAtom;
import me.chan.texas.ext.markdown.math.ast.Term;
import me.chan.texas.ext.markdown.math.ast.TextAtom;
import me.chan.texas.ext.markdown.math.ast.VariableAtom;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;
import me.chan.texas.utils.TexasUtils;

public class MathRendererInflater {
	private static final float SUB_EXP_FACTOR = 0.6f;

	public RendererNode inflate(MathPaint.Styles styles, MathList mathList) {
		return optimize(inflate0(styles, mathList));
	}

	private RendererNode optimize(RendererNode node) {
		if (!(node instanceof OptimizableRendererNode)) {
			return node;
		}

		return ((OptimizableRendererNode) node).optimize();
	}

	private RendererNode inflate0(MathPaint.Styles styles, MathList mathList) {
		List<RendererNode> list = new ArrayList<>();
		for (Ast ast : mathList.elements) {
			list.add(inflateElement(styles, ast));
		}
		return new LinearGroupNode(styles, list, LinearGroupNode.Gravity.HORIZONTAL);
	}

	private RendererNode inflateElement(MathPaint.Styles styles, Ast ast) {
		if (ast instanceof Term) {
			return inflateTerm(styles, (Term) ast);
		}

		if (ast instanceof Spacing) {
			return inflateSpacing(styles, (Spacing) ast);
		}

		throw new IllegalArgumentException("Unknown ast: " + ast);
	}

	private static final Spacing THICK_SPACE = new Spacing(";", null);

	private RendererNode inflateSpacing(MathPaint.Styles styles, Spacing spacing) {
		float textSize = styles.getTextSize();
		float width;
		float height = textSize;

		// 简单空格命令
		switch (spacing.command) {
			case ",":  // \, thin space
				width = textSize / 6f;  // 3/18 em
				break;
			case ":":  // \: medium space
				width = textSize * 2f / 9f;  // 4/18 em
				break;
			case ";":  // \; thick space
				width = textSize * 5f / 18f;  // 5/18 em
				break;
			case "!":  // \! negative thin space
				width = -textSize / 6f;  // -3/18 em
				break;
			case "quad":
				width = textSize;  // 1 em
				break;
			case "qquad":
				width = 2 * textSize;  // 2 em
				break;
			case "hspace":
				// 处理 \hspace{<length>}
				if (spacing.content instanceof Length) {
					Length length = (Length) spacing.content;
					width = calculateLength(length, textSize);
				} else {
					width = 0;
				}
				break;
			case "hphantom":
				// 水平占位：使用内容的宽度，但高度为0或不显示内容
				if (spacing.content instanceof MathList) {
					RendererNode content = inflate0(styles, (MathList) spacing.content);
					// 返回一个只占用宽度的节点
					return new PhantomNode(styles, content, true, false);
				}
				width = 0;
				break;
			case "vphantom":
				// 垂直占位：使用内容的高度，但宽度为0
				if (spacing.content instanceof MathList) {
					RendererNode content = inflate0(styles, (MathList) spacing.content);
					// 返回一个只占用高度的节点
					return new PhantomNode(styles, content, false, true);
				}
				width = 0;
				break;
			case "phantom":
				// 完全占位：使用内容的宽度和高度，但不显示内容
				if (spacing.content instanceof MathList) {
					RendererNode content = inflate0(styles, (MathList) spacing.content);
					// 返回一个占用宽高但不显示的节点
					return new PhantomNode(styles, content, true, true);
				}
				width = 0;
				break;
			default:
				width = 0;
				break;
		}

		return new SpaceNode(styles, (int) Math.ceil(width), (int) Math.ceil(height));
	}

	/**
	 * 根据 Length 对象计算实际像素值
	 */
	private float calculateLength(Length length, float textSize) {
		float value = Float.parseFloat(length.size.value);
		String unit = length.unit.unit;

		switch (unit) {
			case "em":
				return value * textSize;
			case "ex":
				// ex 通常是 em 的一半（x-height）
				return value * textSize * 0.5f;
			case "pt":
				// 1 pt = 1/72 inch, 需要根据屏幕密度转换
				// 简化处理：假设 textSize 对应 1em ≈ 16pt
				return value * textSize / 16f;
			case "px":
				return value;
			case "cm":
				// 1 cm ≈ 37.8 px (at 96 DPI)
				return value * 37.8f;
			case "mm":
				// 1 mm ≈ 3.78 px (at 96 DPI)
				return value * 3.78f;
			case "in":
				// 1 inch = 96 px (at 96 DPI)
				return value * 96f;
			default:
				return value * textSize;  // 默认按 em 处理
		}
	}

	private RendererNode inflateTerm(MathPaint.Styles styles, Term term) {
		RendererNode content = inflateAtom(styles, term.atom);

		SupSubSuffix suffix = term.suffix;
		if (suffix == null) {
			return content;
		}

		DecorGroupNode.Builder builder = new DecorGroupNode.Builder(styles, content);
		RendererNode superscript = null;
		RendererNode subscript = null;

		ScriptArg scriptArg = suffix.superscript;
		if (scriptArg != null) {
			superscript = inflateScriptArg(new MathPaint.Styles(styles).setTextSizeFactor(SUB_EXP_FACTOR), scriptArg);
		}

		scriptArg = suffix.subscript;
		if (scriptArg != null) {
			subscript = inflateScriptArg(new MathPaint.Styles(styles).setTextSizeFactor(SUB_EXP_FACTOR), scriptArg);
		}

		setupDecor(builder, term.atom, superscript, subscript);

		return builder.build();
	}

	private void setupDecor(DecorGroupNode.Builder builder, Atom content, RendererNode superscript, RendererNode subscript) {
		if (content instanceof LargeOperatorAtom) {
			LargeOperatorAtom atom = (LargeOperatorAtom) content;
			if ("sum".equals(atom.name) || "prod".equals(atom.name) || "coprod".equals(atom.name) ||
					"bigcup".equals(atom.name) || "bigcap".equals(atom.name) || "bigvee".equals(atom.name) || "bigwedge".equals(atom.name) ||
					"bigoplus".equals(atom.name) || "bigotimes".equals(atom.name) || "bigodot".equals(atom.name) ||
					"biguplus".equals(atom.name) || "bigsqcup".equals(atom.name) ||
					"lim".equals(atom.name) || "limsup".equals(atom.name) || "liminf".equals(atom.name)) {
				builder.top(superscript).bottom(subscript);
				return;
			}
		}

		builder.rightTop(superscript);
		builder.rightBottom(subscript);
	}

	private RendererNode inflateBinomAtom(MathPaint.Styles styles, BinomAtom atom) {
		RendererNode upper = inflate0(styles.copy().setTextSizeFactor(0.8f), atom.upper);
		RendererNode lower = inflate0(styles.copy().setTextSizeFactor(0.8f), atom.lower);
		List<RendererNode> list = new ArrayList<>();
		list.add(upper);
		list.add(lower);

		return new BraceGroupNode(
				styles, DelimitedAtom.LEVEL_L0,
				inflateDelimiter(styles, "("),
				new LinearGroupNode(styles, list, LinearGroupNode.Gravity.VERTICAL),
				inflateDelimiter(styles, ")")
		);
	}

	private RendererNode inflateExtensibleArrowAtom(MathPaint.Styles styles, ExtensibleArrowAtom atom) {
		// 获取箭头符号
		Symbol arrowSymbol = MathFontOptions.ast(atom);
		RendererNode arrowNode = new SymbolNode(styles, arrowSymbol);

		// 使用 DecorGroupNode 来布局箭头和上下文本
		DecorGroupNode.Builder builder = new DecorGroupNode.Builder(styles, arrowNode);

		// 上方内容（必需）
		if (atom.above != null) {
			RendererNode above = inflate0(
					styles.copy().setTextSizeFactor(SUB_EXP_FACTOR),
					atom.above
			);
			builder.top(above);
		}

		// 下方内容（可选）
		if (atom.below != null) {
			RendererNode below = inflate0(
					styles.copy().setTextSizeFactor(SUB_EXP_FACTOR),
					atom.below
			);
			builder.bottom(below);
		}

		return builder.build();
	}

	private RendererNode inflateScriptArg(MathPaint.Styles styles, ScriptArg scriptArg) {
		Ast content = scriptArg.content;
		if (content instanceof Group) {
			Group group = (Group) content;
			return inflateGroup(styles, group);
		}

		if (content instanceof SingleToken) {
			SingleToken token = (SingleToken) content;
			return inflateSingleToken(styles, token);
		}

		throw new IllegalArgumentException("Unknown script arg: " + scriptArg);
	}

	private RendererNode inflateGroup(MathPaint.Styles styles, Group group) {
		return inflate0(styles, group.content);
	}

	private RendererNode inflateSingleToken(MathPaint.Styles styles, SingleToken singleToken) {
		return inflateAtom(styles, singleToken.content);
	}

	private RendererNode inflateGreekLetterVariableAtom(MathPaint.Styles styles, GreekLetterVariableAtom atom) {
		return new TextNode(styles, MathFontOptions.ast(atom) + atom.primeSuffix);
	}

	private RendererNode inflateAtom(MathPaint.Styles styles, Atom atom) {
		if (atom instanceof NumberAtom) {
			return new TextNode(styles, ((NumberAtom) atom).value);
		}

		if (atom instanceof VariableAtom) {
			return new TextNode(styles, String.valueOf(((VariableAtom) atom).name));
		}

		if (atom instanceof GreekLetterVariableAtom) {
			return inflateGreekLetterVariableAtom(styles, (GreekLetterVariableAtom) atom);
		}

		if (atom instanceof SymbolAtom) {
			return inflateSymbol(styles, (SymbolAtom) atom);
		}

		if (atom instanceof Group) {
			return inflateGroupAtom(styles, (Group) atom);
		}

		if (atom instanceof FracAtom) {
			return inflateFracAtom(styles, (FracAtom) atom);
		}

		if (atom instanceof SqrtAtom) {
			return inflateSqrtAtom(styles, (SqrtAtom) atom);
		}

		if (atom instanceof BinomAtom) {
			return inflateBinomAtom(styles, (BinomAtom) atom);
		}

		if (atom instanceof ExtensibleArrowAtom) {
			return inflateExtensibleArrowAtom(styles, (ExtensibleArrowAtom) atom);
		}

		if (atom instanceof DelimitedAtom) {
			return inflateDelimitedAtom(styles, (DelimitedAtom) atom);
		}

		if (atom instanceof FunctionCallAtom) {
			return inflateFunctionCallAtom(styles, (FunctionCallAtom) atom);
		}

		if (atom instanceof LargeOperatorAtom) {
			return inflateLargeOp(styles, (LargeOperatorAtom) atom);
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

		if (atom instanceof PunctuationAtom) {
			return inflatePunctuationAtom(styles, (PunctuationAtom) atom);
		}

		if (atom instanceof SpecialLetterVariableAtom) {
			return inflateSpecialLetterVariableAtom(styles, (SpecialLetterVariableAtom) atom);
		}

		throw new IllegalArgumentException("Unknown atom: " + atom);
	}

	private RendererNode inflateSpecialLetterVariableAtom(MathPaint.Styles styles, SpecialLetterVariableAtom atom) {
		Symbol symbol = MathFontOptions.ast(atom);
		if (symbol != null) {
			return new SymbolNode(styles, symbol);
		}
		// 如果没有对应的符号，回退到文本渲染
		return new TextNode(styles, "\\" + atom.name + atom.primeSuffix);
	}

	private RendererNode inflatePunctuationAtom(MathPaint.Styles styles, PunctuationAtom atom) {
		return new TextNode(styles, atom.symbol);
	}

	private RendererNode inflateSpecialSymbolAtom(MathPaint.Styles styles, SpecialSymbolAtom atom) {
		return new SymbolNode(styles, MathFontOptions.ast(atom));
	}

	private RendererNode inflateFontAtom(MathPaint.Styles styles, FontAtom atom) {
		if (TexasUtils.equals("mathbf", atom.command)) {
			styles = styles.copy().setBold(true);
		}

		if (TexasUtils.equals("mathit", atom.command)) {
			styles = styles.copy().setItalic(true);
		}

		return inflate0(styles, atom.content);
	}

	private RendererNode inflateTextAtom(MathPaint.Styles styles, TextAtom atom) {
		if (TexasUtils.equals("textfield", atom.command)) {
			return new TextFieldNode(styles, atom.content);
		}

		if (TexasUtils.equals("textbf", atom.command)) {
			styles = styles.copy().setBold(true);
		}

		if (TexasUtils.equals("textit", atom.command)) {
			styles = styles.copy().setItalic(true);
		}

		return new TextNode(styles, atom.content);
	}

	private RendererNode inflateMatrixAtom(MathPaint.Styles styles, MatrixAtom atom) {
		RendererNode left = null;
		RendererNode right = null;

		if ("pmatrix".equals(atom.env)) {
			left = inflateDelimiter(styles, "(");
			right = inflateDelimiter(styles, ")");
		} else if ("bmatrix".equals(atom.env)) {
			left = inflateDelimiter(styles, "[");
			right = inflateDelimiter(styles, "]");
		} else if ("Bmatrix".equals(atom.env)) {
			left = inflateDelimiter(styles, "{");
			right = inflateDelimiter(styles, "}");
		} else if ("vmatrix".equals(atom.env)) {
			left = inflateDelimiter(styles, "\\lvert");
			right = inflateDelimiter(styles, "\\rvert");
		} else if ("Vmatrix".equals(atom.env)) {
			left = inflateDelimiter(styles, "\\lVert");
			right = inflateDelimiter(styles, "\\rVert");
		} else if ("cases".equals(atom.env)) {
			left = inflateDelimiter(styles, "{");
			right = null;
		} else if ("array".equals(atom.env) || "smallmatrix".equals(atom.env)) {
			// array 和 smallmatrix 没有分隔符
		} else {
			if (!"matrix".equals(atom.env)) {
				throw new IllegalArgumentException("Unknown matrix env: " + atom.env);
			}
		}

		List<RendererNode> list = new ArrayList<>();
		for (int r = 0; r < atom.rows.size(); ++r) {
			MatrixRow row = atom.rows.get(r);
			for (int c = 0; c < row.elements.size(); ++c) {
				MathList ast = row.elements.get(c);
				list.add(inflate0(styles, ast));
				if (c != row.elements.size() - 1) {
					list.add(inflateSpacing(styles, THICK_SPACE));
				}
			}
		}

		GridGroupNode content = new GridGroupNode(styles, atom.rows.size() * 2 - 1, list);
		return new BraceGroupNode(styles, DelimitedAtom.LEVEL_L0, left, content, right);
	}

	private RendererNode inflateDelimitedAtom(MathPaint.Styles styles, DelimitedAtom atom) {
		return new BraceGroupNode(
				styles, atom.level,
				inflateDelimiter(styles, atom.leftDelimiter),
				inflate0(styles, atom.content),
				inflateDelimiter(styles, atom.rightDelimiter)
		);
	}

	private RendererNode inflateDelimiter(MathPaint.Styles styles, String delimiter) {
		if (delimiter == null || delimiter.isEmpty() || ".".equals(delimiter)) {
			return null;
		}

		if ("(".equals(delimiter)) {
			return new StretchyNode(styles, MathFontOptions.symbol("parenleft"));
		}

		if (")".equals(delimiter)) {
			return new StretchyNode(styles, MathFontOptions.symbol("parenright"));
		}

		if ("[".equals(delimiter)) {
			return new StretchyTripeNode(
					styles,
					MathFontOptions.symbol("uni23A1"),
					MathFontOptions.symbol("uni23A2"),
					MathFontOptions.symbol("uni23A3"),
					MathFontOptions.symbol("uni23A2")
			);
		}

		if ("]".equals(delimiter)) {
			return new StretchyTripeNode(
					styles,
					MathFontOptions.symbol("uni23A4"),
					MathFontOptions.symbol("uni23A5"),
					MathFontOptions.symbol("uni23A6"),
					MathFontOptions.symbol("uni23A5")
			);
		}

		if ("{".equals(delimiter)) {
			return new StretchyTripeNode(
					styles,
					MathFontOptions.symbol("uni23A7"),
					MathFontOptions.symbol("uni23A8"),
					MathFontOptions.symbol("uni23A9"),
					MathFontOptions.symbol("uni23AA")
			);
		}

		if ("}".equals(delimiter)) {
			return new StretchyTripeNode(
					styles,
					MathFontOptions.symbol("uni23AB"),
					MathFontOptions.symbol("uni23AC"),
					MathFontOptions.symbol("uni23AD"),
					MathFontOptions.symbol("uni23AA")
			);
		}

		if ("|".equals(delimiter)) {
			return new StretchyNode(
					styles,
					MathFontOptions.symbol("divides")
			);
		}

		if ("\\|".equals(delimiter) || "||".equals(delimiter)) {
			return new StretchyNode(
					styles,
					MathFontOptions.symbol("parallel")
			);
		}

		if ("\\langle".equals(delimiter)) {
			return new StretchyNode(styles, MathFontOptions.symbol("angleleft"));
		}

		if ("\\rangle".equals(delimiter)) {
			return new StretchyNode(styles, MathFontOptions.symbol("angleright"));
		}

		if ("\\lfloor".equals(delimiter)) {
			return new StretchyDoubleNode(
					styles,
					StretchyDoubleNode.GRAVITY_BOTTOM,
					MathFontOptions.symbol("uni23A3"),
					MathFontOptions.symbol("uni23A2")
			);
		}
		if ("\\rfloor".equals(delimiter)) {
			return new StretchyDoubleNode(
					styles,
					StretchyDoubleNode.GRAVITY_BOTTOM,
					MathFontOptions.symbol("uni23A6"),
					MathFontOptions.symbol("uni23A5")
			);
		}

		if ("\\lceil".equals(delimiter)) {
			return new StretchyDoubleNode(
					styles,
					StretchyDoubleNode.GRAVITY_TOP,
					MathFontOptions.symbol("uni23A1"),
					MathFontOptions.symbol("uni23A2")
			);
		}
		if ("\\rceil".equals(delimiter)) {
			return new StretchyDoubleNode(
					styles,
					StretchyDoubleNode.GRAVITY_TOP,
					MathFontOptions.symbol("uni23A4"),
					MathFontOptions.symbol("uni23A5")
			);
		}

		if ("\\lvert".equals(delimiter) || "\\rvert".equals(delimiter)) {
			return new StretchyNode(
					styles,
					MathFontOptions.symbol("divides")
			);
		}

		if ("\\lVert".equals(delimiter) || "\\rVert".equals(delimiter)) {
			return new StretchyNode(
					styles,
					MathFontOptions.symbol("parallel")
			);
		}

		throw new IllegalArgumentException("unknown delimiter: " + delimiter);
	}

	private RendererNode inflateFunctionCallAtom(MathPaint.Styles styles, FunctionCallAtom functionCallAtom) {
		return new TextNode(styles.copy().setItalic(true), functionCallAtom.name);
	}

	private RendererNode inflateSqrtAtom(MathPaint.Styles styles, SqrtAtom sqrtAtom) {
		RendererNode root = null;
		if (sqrtAtom.root != null) {
			root = inflate0(new MathPaint.Styles(styles).setTextSizeFactor(SUB_EXP_FACTOR), sqrtAtom.root);
		}
		return new SqrtNode(styles, inflate0(styles, sqrtAtom.content), root);
	}

	private RendererNode inflateFracAtom(MathPaint.Styles styles, FracAtom atom) {
		return new FractionNode(styles, inflate0(styles, atom.numerator), inflate0(styles, atom.denominator));
	}

	private RendererNode inflateGroupAtom(MathPaint.Styles styles, Group groupAtom) {
		if (groupAtom.s == '{') {
			return inflate0(styles, groupAtom.content);
		}

		List<RendererNode> nodes = new ArrayList<>();
		if (groupAtom.s == '(') {
			nodes.add(new SymbolNode(styles, MathFontOptions.symbol("parenleft")));
		} else if (groupAtom.s == '[') {
			nodes.add(new SymbolNode(styles, MathFontOptions.symbol("bracketleft")));
		} else if (groupAtom.s == '\\') {
			nodes.add(new SymbolNode(styles, MathFontOptions.symbol("braceleft")));
		} else {
			throw new IllegalArgumentException("unknown group delimiter: " + groupAtom.s);
		}

		nodes.add(inflate0(styles, groupAtom.content));

		if (groupAtom.s == '(') {
			nodes.add(new SymbolNode(styles, MathFontOptions.symbol("parenright")));
		} else if (groupAtom.s == '[') {
			nodes.add(new SymbolNode(styles, MathFontOptions.symbol("bracketright")));
		} else {
			nodes.add(new SymbolNode(styles, MathFontOptions.symbol("braceright")));
		}

		return new LinearGroupNode(styles, nodes, LinearGroupNode.Gravity.HORIZONTAL);
	}

	private RendererNode inflateAccentAtom(MathPaint.Styles styles, AccentAtom accentAtom) {
		if (accentAtom.content instanceof MathList) {
			return new AccentNode(styles, accentAtom.cmd, inflate0(styles, (MathList) accentAtom.content));
		}

		if (accentAtom.content instanceof SingleToken) {
			return new AccentNode(styles, accentAtom.cmd, inflateSingleToken(styles, (SingleToken) accentAtom.content));
		}

		return new AccentNode(styles, accentAtom.cmd, inflateAtom(styles, (Atom) accentAtom.content));
	}

	private RendererNode inflateSymbol(MathPaint.Styles styles, SymbolAtom atom) {
		Symbol symbol = MathFontOptions.ast(atom);
		if (symbol != null) {
			return new SymbolNode(styles, symbol);
		}

		return new TextNode(styles, atom.symbol);
	}

	private RendererNode inflateLargeOp(MathPaint.Styles styles, LargeOperatorAtom largeOperatorAtom) {
		Symbol s = MathFontOptions.ast(largeOperatorAtom);
		if (s != null) {
			return new SymbolNode(styles, s);
		}

		String o = MathFontOptions.textOp(largeOperatorAtom.name);
		if (o != null) {
			return new TextNode(styles, o);
		}

		throw new IllegalArgumentException("unknown operator: " + largeOperatorAtom.name);
	}
}
