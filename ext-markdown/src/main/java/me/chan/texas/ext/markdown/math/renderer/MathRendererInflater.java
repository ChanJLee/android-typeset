package me.chan.texas.ext.markdown.math.renderer;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.ext.markdown.math.ast.AccentAtom;
import me.chan.texas.ext.markdown.math.ast.Ast;
import me.chan.texas.ext.markdown.math.ast.Atom;
import me.chan.texas.ext.markdown.math.ast.BinOpAtom;
import me.chan.texas.ext.markdown.math.ast.BinomAtom;
import me.chan.texas.ext.markdown.math.ast.DelimitedAtom;
import me.chan.texas.ext.markdown.math.ast.Expression;
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
import me.chan.texas.ext.markdown.math.ast.PostfixOp;
import me.chan.texas.ext.markdown.math.ast.PunctuationAtom;
import me.chan.texas.ext.markdown.math.ast.ScriptArg;
import me.chan.texas.ext.markdown.math.ast.SingleToken;
import me.chan.texas.ext.markdown.math.ast.Spacing;
import me.chan.texas.ext.markdown.math.ast.SpecialLetterVariableAtom;
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
	private static final float SUB_EXP_FACTOR = 0.6f;

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

	private static final Spacing COMMON_SPACE = new Spacing(",", null);

	private RendererNode inflateExpression(MathPaint.Styles styles, Expression expression) {
		List<RendererNode> list = new ArrayList<>();
		for (Ast ast : expression.elements) {
			if (ast instanceof Term) {
				list.add(inflateTerm(styles, (Term) ast));
			} else if (ast instanceof BinOpAtom) {
				list.add(inflateSpacing(styles, COMMON_SPACE));
				list.add(inflateBinOp(styles, (BinOpAtom) ast));
				list.add(inflateSpacing(styles, COMMON_SPACE));
			} else {
				throw new IllegalArgumentException("Unknown ast: " + ast);
			}
		}
		return new LinearGroupNode(styles, list, LinearGroupNode.Gravity.HORIZONTAL);
	}

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
					RendererNode content = inflate(styles, (MathList) spacing.content);
					// 返回一个只占用宽度的节点
					return new PhantomNode(styles, content, true, false);
				}
				width = 0;
				break;
			case "vphantom":
				// 垂直占位：使用内容的高度，但宽度为0
				if (spacing.content instanceof MathList) {
					RendererNode content = inflate(styles, (MathList) spacing.content);
					// 返回一个只占用高度的节点
					return new PhantomNode(styles, content, false, true);
				}
				width = 0;
				break;
			case "phantom":
				// 完全占位：使用内容的宽度和高度，但不显示内容
				if (spacing.content instanceof MathList) {
					RendererNode content = inflate(styles, (MathList) spacing.content);
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
		RendererNode postfixOp = null;
		if (term.postfixOp != null) {
			postfixOp = inflatePostfixOp(styles, term.postfixOp);
		}

		RendererNode content = inflateAtom(styles, term.atom);
		if (postfixOp != null) {
			List<RendererNode> list = new ArrayList<>();
			list.add(content);
			list.add(postfixOp);
			content = new LinearGroupNode(styles, list, LinearGroupNode.Gravity.HORIZONTAL);
		}

		DecorGroupNode.Builder builder = new DecorGroupNode.Builder(styles, content);

		if (term.unaryOp != null) {
			builder.left(new SymbolNode(styles, MathFontOptions.ast(term.unaryOp)));
		}

		SupSubSuffix suffix = term.suffix;
		if (suffix == null) {
			return builder.build();
		}

		ScriptArg scriptArg = suffix.superscript;
		if (scriptArg != null) {
			builder.rightTop(inflateScriptArg(new MathPaint.Styles(styles).setTextSizeFactor(SUB_EXP_FACTOR), scriptArg));
		}

		scriptArg = suffix.subscript;
		if (scriptArg != null) {
			builder.rightBottom(inflateScriptArg(new MathPaint.Styles(styles).setTextSizeFactor(SUB_EXP_FACTOR), scriptArg));
		}
		return builder.build();
	}

	private RendererNode inflateBinomAtom(MathPaint.Styles styles, BinomAtom atom) {
		RendererNode upper = inflate(styles.copy().setTextSizeFactor(0.8f), atom.upper);
		RendererNode lower = inflate(styles.copy().setTextSizeFactor(0.8f), atom.lower);
		List<RendererNode> list = new ArrayList<>();
		list.add(upper);
		list.add(lower);

		return new BraceLayout(
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
			RendererNode above = inflate(
					styles.copy().setTextSizeFactor(SUB_EXP_FACTOR),
					atom.above
			);
			builder.top(above);
		}

		// 下方内容（可选）
		if (atom.below != null) {
			RendererNode below = inflate(
					styles.copy().setTextSizeFactor(SUB_EXP_FACTOR),
					atom.below
			);
			builder.bottom(below);
		}

		return builder.build();
	}

	private RendererNode inflatePostfixOp(MathPaint.Styles styles, PostfixOp op) {
		return new TextNode(styles, op.op);
	}

	private RendererNode inflateScriptArg(MathPaint.Styles styles, ScriptArg scriptArg) {
		Ast content = scriptArg.content;
		if (content instanceof Group) {
			Group group = (Group) content;
			return inflateScriptArg(styles, group);
		}

		if (content instanceof SingleToken) {
			SingleToken token = (SingleToken) content;
			return inflateScriptArg(styles, token);
		}

		throw new IllegalArgumentException("Unknown script arg: " + scriptArg);
	}

	private RendererNode inflateScriptArg(MathPaint.Styles styles, Group group) {
		return inflate(styles, group.content);
	}

	private RendererNode inflateScriptArg(MathPaint.Styles styles, SingleToken singleToken) {
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
		if (TextUtils.equals("mathbf", atom.command)) {
			styles = styles.copy().setBold(true);
		}

		if (TextUtils.equals("mathit", atom.command)) {
			styles = styles.copy().setItalic(true);
		}

		return inflate(styles, atom.content);
	}

	private RendererNode inflateTextAtom(MathPaint.Styles styles, TextAtom atom) {
		if (TextUtils.equals("textbf", atom.command)) {
			styles = styles.copy().setBold(true);
		}

		if (TextUtils.equals("textit", atom.content)) {
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
			left = inflateDelimiter(styles, "lvert");
			right = inflateDelimiter(styles, "rvert");
		} else if ("Vmatrix".equals(atom.env)) {
			left = inflateDelimiter(styles, "lVert");
			right = inflateDelimiter(styles, "rVert");
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
		GridGroupNode content = new GridGroupNode(styles, atom.rows.size(), list);
		for (MatrixRow row : atom.rows) {
			for (MathList ast : row.elements) {
				list.add(inflate(styles, ast));
			}
		}

		return new BraceLayout(styles, DelimitedAtom.LEVEL_L0, left, content, right);
	}

	private RendererNode inflateLargeOperatorAtom(MathPaint.Styles styles, LargeOperatorAtom atom) {
		RendererNode subscript = null;
		if (atom.suffix != null && atom.suffix.subscript != null) {
			subscript = inflateScriptArg(styles.copy().setTextSizeFactor(SUB_EXP_FACTOR), atom.suffix.subscript);
		}

		RendererNode superscript = null;
		if (atom.suffix != null && atom.suffix.superscript != null) {
			superscript = inflateScriptArg(styles.copy().setTextSizeFactor(SUB_EXP_FACTOR), atom.suffix.superscript);
		}

		DecorGroupNode.Builder builder = new DecorGroupNode.Builder(styles, inflateLargeOp(styles, atom));
		if ("sum".equals(atom.name) || "prod".equals(atom.name) || "coprod".equals(atom.name) ||
				"bigcup".equals(atom.name) || "bigcap".equals(atom.name) || "bigvee".equals(atom.name) || "bigwedge".equals(atom.name) ||
				"bigoplus".equals(atom.name) || "bigotimes".equals(atom.name) || "bigodot".equals(atom.name) ||
				"biguplus".equals(atom.name) || "bigsqcup".equals(atom.name) ||
				"lim".equals(atom.name) || "limsup".equals(atom.name) || "liminf".equals(atom.name)) {
			builder.top(superscript).bottom(subscript);
		} else if ("int".equals(atom.name) || "iint".equals(atom.name) || "iiint".equals(atom.name) ||
				"oint".equals(atom.name) || "oiint".equals(atom.name) || "oiiint".equals(atom.name)) {
			builder.rightTop(superscript).rightBottom(subscript);
		} else {
			throw new IllegalArgumentException("unknown operator: " + atom.name);
		}

		return builder.build();
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

		if ("\\|".equals(delimiter) || "|".equals(delimiter)) {
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
		DecorGroupNode.Builder builder = new DecorGroupNode.Builder(styles, new TextNode(styles.copy().setItalic(true), functionCallAtom.name));

		if (functionCallAtom.argument != null) {
			if (functionCallAtom.argument instanceof DelimitedAtom) {
				builder.right(inflateDelimitedAtom(styles, (DelimitedAtom) functionCallAtom.argument));
			} else if (functionCallAtom.argument instanceof Group) {
				builder.right(inflateGroupAtom(styles, (Group) functionCallAtom.argument));
			} else if (functionCallAtom.argument instanceof SingleToken) {
				builder.right(inflateScriptArg(styles, (SingleToken) functionCallAtom.argument));
			} else {
				throw new IllegalArgumentException("unknown argument: " + functionCallAtom.argument);
			}
		}

		SupSubSuffix supSubSuffix = functionCallAtom.suffix;
		if (supSubSuffix != null) {
			if (supSubSuffix.subscript != null) {
				builder.rightBottom(inflateScriptArg(styles.copy().setTextSizeFactor(SUB_EXP_FACTOR), supSubSuffix.subscript));
			}

			if (supSubSuffix.superscript != null) {
				builder.rightTop(inflateScriptArg(styles.copy().setTextSizeFactor(SUB_EXP_FACTOR), supSubSuffix.superscript));
			}
		}
		return builder.build();
	}

	private RendererNode inflateSqrtAtom(MathPaint.Styles styles, SqrtAtom sqrtAtom) {
		RendererNode root = null;
		if (sqrtAtom.root != null) {
			root = inflate(new MathPaint.Styles(styles).setTextSizeFactor(SUB_EXP_FACTOR), sqrtAtom.root);
		}
		return new SqrtNode(styles, inflate(styles, sqrtAtom.content), root);
	}

	private RendererNode inflateFracAtom(MathPaint.Styles styles, FracAtom atom) {
		return new FractionNode(styles, inflate(styles, atom.numerator), inflate(styles, atom.denominator));
	}

	private RendererNode inflateGroupAtom(MathPaint.Styles styles, Group groupAtom) {
		return inflate(styles, groupAtom.content);
	}

	private RendererNode inflateAccentAtom(MathPaint.Styles styles, AccentAtom accentAtom) {
		if (accentAtom.content instanceof MathList) {
			return new AccentNode(styles, accentAtom.cmd, inflate(styles, (MathList) accentAtom.content));
		}

		return new AccentNode(styles, accentAtom.cmd, inflateAtom(styles, (Atom) accentAtom.content));
	}

	private RendererNode inflateBinOp(MathPaint.Styles styles, BinOpAtom atom) {
		Symbol symbol = MathFontOptions.ast(atom);
		if (symbol != null) {
			return new SymbolNode(styles, symbol);
		}

		return new TextNode(styles, atom.op);
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
