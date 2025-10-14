package me.chan.texas.ext.markdown.parser;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.utils.CharStream;
/*
* <math> ::= <math_list>

<math_list> ::= <math_atom> { <math_atom> }

<math_atom> ::= <ord_atom>
               | <sup_sub>
               | <frac>
               | <sqrt>
               | <delimited>
               | <function_call>
               | <matrix>
               | <operator_expr>
               | <symbol>
               | <group>

<ord_atom> ::= <variable> | <number> | <plain_symbol>

<variable> ::= <letter> | "\mathrm{" <letters> "}"

<number> ::= <digit> { <digit> } [ "." <digit> { <digit> } ]

<plain_symbol> ::= <single_symbol>
                 | "\" <symbol_name>

<symbol_name> ::= letter / letters / known_symbol_command  (* 如 alpha, beta, times, cdot 等 *)

<sup_sub> ::= <base> ( "^" <group_or_atom> | "_" <group_or_atom> | "^" <group_or_atom> "_" <group_or_atom> | "_" <group_or_atom> "^" <group_or_atom> )

<base> ::= <ord_atom> | <group> | <delimited> | <function_call>

<group_or_atom> ::= <group> | <ord_atom>

<group> ::= "{" <math_list> "}"

<frac> ::= "\frac" "{" <math_list> "}" "{" <math_list> "}"

<sqrt> ::= "\sqrt" [ "[" <math_list> "]" ] "{" <math_list> "}"

<delimited> ::= "\left" <delimiter> <math_list> "\right" <delimiter>

<delimiter> ::= "(" | ")" | "[" | "]" | "|" | "\langle" | "\rangle" | "\lvert" | "\rvert" | …

<function_call> ::= "\" <function_name> <group_or_atom>

<function_name> ::= "sin" | "cos" | "tan" | "log" | "ln" | "exp" | "max" | "min" | "lim" | "sum" | "prod" | …

<operator_expr> ::= <math_list> <operator> <math_list>

<operator> ::= "+" | "-" | "*" | "/" | "=" | "<" | ">" | "\times" | "\cdot" | "\le" | "\ge" | "\pm" | "\neq" | …

<matrix> ::= "\begin{" <matrix_env> "}" <matrix_rows> "\end{" <matrix_env> "}"

<matrix_env> ::= "matrix" | "pmatrix" | "bmatrix" | "vmatrix" | "Bmatrix"

<matrix_rows> ::= <matrix_row> { "\\" <matrix_row> }

<matrix_row> ::= <math_list> { "&" <math_list> }

* */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TexMathParser {
	private final CharStream input;

	public TexMathParser(CharStream input) {
		this.input = input;
	}

	public void parseMath() {
		System.out.println("Entering <math>");
		parseMathList();
		System.out.println("Exiting <math>");
	}

	private void parseMathList() {
		System.out.println("Entering <math_list>");
		while (!input.eof()) {
			parseMathAtom();
		}
		System.out.println("Exiting <math_list>");
	}

	private void parseMathAtom() {
		System.out.println("Entering <math_atom>");
		char ch = (char) input.peek();
		if (Character.isLetter(ch)) {
			parseOrdAtom();
		} else if (ch == '\\') {
			String command = input.peekCommand();
			switch (command) {
				case "frac" -> parseFrac();
				case "sqrt" -> parseSqrt();
				case "left" -> parseDelimited();
				case "sin", "cos", "tan", "log", "ln", "exp", "max", "min", "lim", "sum", "prod" ->
						parseFunctionCall();
				case "begin" -> parseMatrix();
				default -> parsePlainSymbol();
			}
		} else if (Character.isDigit(ch)) {
			parseOrdAtom();
		} else if (ch == '{') {
			parseGroup();
		} else if (ch == '^' || ch == '_') {
			parseSupSub();
		} else {
			parseOperatorExprOrSymbol();
		}
		System.out.println("Exiting <math_atom>");
	}

	private void parseOrdAtom() {
		System.out.println("Entering <ord_atom>");
		char ch = (char) input.peek();
		if (Character.isLetter(ch)) {
			parseVariable();
		} else if (Character.isDigit(ch)) {
			parseNumber();
		} else if (ch == '\\') {
			parsePlainSymbol();
		}
		System.out.println("Exiting <ord_atom>");
	}

	private void parseVariable() {
		System.out.println("Parsing <variable>");
		if (input.peek() == '\\') {
			input.eat(); // consume \
			if (input.peekString(7).equals("mathrm")) {
				input.adjust(7); // consume 'mathrm'
				if (input.eatIf('{')) {
					while (Character.isLetter(input.peek())) {
						input.eat();
					}
					input.eatIf('}');
				}
			}
		} else {
			input.eat(); // simple letter
		}
	}

	private void parseNumber() {
		System.out.println("Parsing <number>");
		while (Character.isDigit(input.peek())) input.eat();
		if (input.peek() == '.') {
			input.eat();
			while (Character.isDigit(input.peek())) input.eat();
		}
	}

	private void parsePlainSymbol() {
		System.out.println("Parsing <plain_symbol>");
		if (input.peek() == '\\') {
			input.eat(); // consume \
			while (Character.isLetter(input.peek())) input.eat(); // symbol name
		} else {
			input.eat(); // single symbol
		}
	}

	private void parseSupSub() {
		System.out.println("Parsing <sup_sub>");
		if (input.peek() == '^' || input.peek() == '_') {
			char first = (char) input.eat();
			parseGroupOrAtom();
			if (!input.eof() && (input.peek() == '^' || input.peek() == '_')) {
				char second = (char) input.eat();
				parseGroupOrAtom();
			}
		}
	}

	private void parseGroupOrAtom() {
		System.out.println("Parsing <group_or_atom>");
		char ch = (char) input.peek();
		if (ch == '{') {
			parseGroup();
		} else {
			parseOrdAtom();
		}
	}

	private void parseGroup() {
		System.out.println("Entering <group>");
		if (input.eatIf('{')) {
			parseMathList();
			input.eatIf('}');
		}
		System.out.println("Exiting <group>");
	}

	private void parseFrac() {
		System.out.println("Entering <frac>");
		input.consumeCommand("frac");
		parseGroup(); // numerator
		parseGroup(); // denominator
		System.out.println("Exiting <frac>");
	}

	private void parseSqrt() {
		System.out.println("Entering <sqrt>");
		input.consumeCommand("sqrt");
		if (input.eatIf('[')) {
			parseMathList();
			input.eatIf(']');
		}
		parseGroup(); // mandatory radicand
		System.out.println("Exiting <sqrt>");
	}

	private void parseDelimited() {
		System.out.println("Entering <delimited>");
		input.consumeCommand("left");
		input.eat(); // delimiter
		parseMathList();
		input.consumeCommand("right");
		input.eat(); // delimiter
		System.out.println("Exiting <delimited>");
	}

	private void parseFunctionCall() {
		System.out.println("Entering <function_call>");
		input.eat(); // consume '\'
		while (Character.isLetter(input.peek())) input.eat(); // function name
		parseGroupOrAtom();
		System.out.println("Exiting <function_call>");
	}

	private void parseMatrix() {
		System.out.println("Entering <matrix>");
		input.consumeCommand("begin");
		parseMatrixEnv();
		parseMatrixRows();
		input.consumeCommand("end");
		parseMatrixEnv();
		System.out.println("Exiting <matrix>");
	}

	private void parseMatrixEnv() {
		System.out.println("Parsing <matrix_env>");
		if (input.eatIf('{')) {
			while (Character.isLetter(input.peek())) input.eat();
			input.eatIf('}');
		}
	}

	private void parseMatrixRows() {
		System.out.println("Parsing <matrix_rows>");
		parseMatrixRow();
		while (!input.eof() && input.peek() == '\\') {
			input.eat();
			parseMatrixRow();
		}
	}

	private void parseMatrixRow() {
		System.out.println("Parsing <matrix_row>");
		parseMathList();
		while (!input.eof() && input.peek() == '&') {
			input.eat();
			parseMathList();
		}
	}

	private void parseOperatorExprOrSymbol() {
		System.out.println("Parsing <operator_expr_or_symbol>");
		input.eat();
	}
}
