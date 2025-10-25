package me.chan.texas.ext.markdown.math.renderer;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.chan.texas.ext.markdown.math.ast.AccentAtom;
import me.chan.texas.ext.markdown.math.ast.Ast;
import me.chan.texas.ext.markdown.math.ast.Atom;
import me.chan.texas.ext.markdown.math.ast.GroupScriptArg;
import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.ScriptArg;
import me.chan.texas.ext.markdown.math.ast.SingleTokenScriptArg;
import me.chan.texas.ext.markdown.math.ast.SupSubSuffix;
import me.chan.texas.ext.markdown.math.ast.Term;

public class MathRendererInflater {

	public RendererNode inflate(MathList math) {
		return inflate0(1, math);
	}

	public RendererNode inflate0(float scale, MathList math) {
		List<RendererNode> list = new ArrayList<>();

		Iterator<Term> term = math.getTerms().iterator();
		Iterator<String> operators = math.getOperators().iterator();
		while (term.hasNext()) {
			inflateTerm(list, scale, term.next());
			if (operators.hasNext()) {
				inflateSymbol(list, scale, operators.next());
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
			return new TextNode(scale, MathFontOptions.formatSymbol(token));
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

	private void inflateSymbol(List<RendererNode> nodes, float scale, String symbol) {
		nodes.add(new TextNode(scale, MathFontOptions.formatSymbol(symbol)));
	}

	@VisibleForTesting
	public static RendererNode mockText(String text) {
		return new TextNode(1f, text);
	}

	@VisibleForTesting
	public static RendererNode mockText(float scale, String text) {
		return new TextNode(scale, text);
	}

	public static RendererNode mockAccent() {
		List<RendererNode> list = new ArrayList<>();
		list.add(new AccentNode(1f, "hat", mockText("hello world")));
		list.add(new AccentNode(1f, "widehat", mockText("hello world")));
		list.add(new AccentNode(1f, "tilde", mockText("hello world")));
		list.add(new AccentNode(1f, "widetilde", mockText("hello world")));
		list.add(new AccentNode(1f, "dot", mockText("hello world")));
		list.add(new AccentNode(1f, "ddot", mockText("hello world")));
		list.add(new AccentNode(1f, "dddot", mockText("hello world")));
		list.add(new AccentNode(1f, "acute", mockText("hello world")));
		list.add(new AccentNode(1f, "breve", mockText("hello world")));
		list.add(new AccentNode(1f, "check", mockText("hello world")));
		list.add(new AccentNode(1f, "underbrace", mockText("hello world")));
		list.add(new AccentNode(1f, "overbrace", mockText("hello world")));
//		list.add(new AccentNode(1f, "underbrace", mockText("hello world")));
//		list.add(new AccentNode(1f, "underbrace", mockText("hello world")));
//		list.add(new AccentNode(1f, "underbrace", mockText("hello world")));
		return new LinearGroupNode(1, list, LinearGroupNode.Gravity.HORIZONTAL);
	}

	@VisibleForTesting
	public static RendererNode mockSqrt() {
		SqrtNode node1 = mockSqrt(1f, mockText(1f, "x1 + y1"));
		return mockSqrt(2, node1);
//		return node1;
	}

	public static RendererNode mockFractionNode() {
		return new FractionNode(2, mockSqrt(), mockSqrt());
	}

	public static RendererNode mockFonts() {
		return mockText("[ , ][!,!][\",\"][#,#][$,$][%,%][&,&][','][(,(][),)][*,*][+,+][,,,][-,-][.,.][/,/][0,0][1,1][2,2][3,3][4,4][5,5][6,6][7,7][8,8][9,9][:,:][;,;][<,<][=,=][>,>][?,?][@,@][A,A][B,B][C,C][D,D][E,E][F,F][G,G][H,H][I,I][J,J][K,K][L,L][M,M][N,N][O,O][P,P][Q,Q][R,R][S,S][T,T][U,U][V,V][W,W][X,X][Y,Y][Z,Z][[,[][\\,\\][],]][^,^][_,_][`,`][a,a][b,b][c,c][d,d][e,e][f,f][g,g][h,h][i,i][j,j][k,k][l,l][m,m][n,n][o,o][p,p][q,q][r,r][s,s][t,t][u,u][v,v][w,w][x,x][y,y][z,z][{,{][|,|][},}][~,~][ , ][¡,¡][¢,¢][£,£][¤,¤][¥,¥][¦,¦][§,§][¨,¨][©,©][ª,ª][«,«][¬,¬][®,®][¯,¯][°,°][±,±][´,´][µ,µ][¶,¶][·,·][¸,¸][º,º][»,»][¿,¿][À,À][Á,Á][Â,Â][Ã,Ã][Ä,Ä][Å,Å][Æ,Æ][Ç,Ç][È,È][É,É][Ê,Ê][Ë,Ë][Ì,Ì][Í,Í][Î,Î][Ï,Ï][Ð,Ð][Ñ,Ñ][Ò,Ò][Ó,Ó][Ô,Ô][Õ,Õ][Ö,Ö][×,×][Ø,Ø][Ù,Ù][Ú,Ú][Û,Û][Ü,Ü][Ý,Ý][Þ,Þ][ß,ß][à,à][á,á][â,â][ã,ã][ä,ä][å,å][æ,æ][ç,ç][è,è][é,é][ê,ê][ë,ë][ì,ì][í,í][î,î][ï,ï][ð,ð][ñ,ñ][ò,ò][ó,ó][ô,ô][õ,õ][ö,ö][÷,÷][ø,ø][ù,ù][ú,ú][û,û][ü,ü][ý,ý][þ,þ][ÿ,ÿ][Ā,Ā][ā,ā][Ă,Ă][ă,ă][Ą,Ą][ą,ą][Ć,Ć][ć,ć][Č,Č][č,č][Ď,Ď][ď,ď][Đ,Đ][Ē,Ē][ē,ē][Ė,Ė][ė,ė][Ę,Ę][ę,ę][Ě,Ě][ě,ě][Ğ,Ğ][ğ,ğ][Ģ,Ģ][ģ,ģ][Ĩ,Ĩ][ĩ,ĩ][Ī,Ī][ī,ī][Į,Į][į,į][İ,İ][ı,ı][Ĳ,Ĳ][ĳ,ĳ][Ķ,Ķ][ķ,ķ][Ĺ,Ĺ][ĺ,ĺ][Ļ,Ļ][ļ,ļ][Ľ,Ľ][ľ,ľ][Ł,Ł][ł,ł][Ń,Ń][ń,ń][Ņ,Ņ][ņ,ņ][Ň,Ň][ň,ň][Ŋ,Ŋ][ŋ,ŋ][Ō,Ō][ō,ō][Ő,Ő][ő,ő][Œ,Œ][œ,œ][Ŕ,Ŕ][ŕ,ŕ][Ŗ,Ŗ][ŗ,ŗ][Ř,Ř][ř,ř][Ś,Ś][ś,ś][Ş,Ş][ş,ş][Š,Š][š,š][Ţ,Ţ][ţ,ţ][Ť,Ť][ť,ť][Ũ,Ũ][ũ,ũ][Ū,Ū][ū,ū][Ů,Ů][ů,ů][Ű,Ű][ű,ű][Ų,Ų][ų,ų][Ÿ,Ÿ][Ź,Ź][ź,ź][Ż,Ż][ż,ż][Ž,Ž][ž,ž][ſ,ſ][Ơ,Ơ][ơ,ơ][Ư,Ư][ư,ư][Ș,Ș][ș,ș][Ț,Ț][ț,ț][ȷ,ȷ][ˆ,ˆ][ˇ,ˇ][˘,˘][˙,˙][˚,˚][˛,˛][˜,˜][˝,˝][̀,̀][́,́][̂,̂][̃,̃][̄,̄][̅,̅][̆,̆][̇,̇][̈,̈][̉,̉][̊,̊][̋,̋][̌,̌][̏,̏][̑,̑][̣,̣][̦,̦][̬,̬][̭,̭][̮,̮][̯,̯][̰,̰][̱,̱][̲,̲][̳,̳][̸,̸][̿,̿][͍,͍][Α,Α][Β,Β][Γ,Γ][Δ,Δ][Ε,Ε][Ζ,Ζ][Η,Η][Θ,Θ][Ι,Ι][Κ,Κ][Λ,Λ][Μ,Μ][Ν,Ν][Ξ,Ξ][Ο,Ο][Π,Π][Ρ,Ρ][Σ,Σ][Τ,Τ][Υ,Υ][Φ,Φ][Χ,Χ][Ψ,Ψ][Ω,Ω][α,α][β,β][γ,γ][δ,δ][ε,ε][ζ,ζ][η,η][θ,θ][ι,ι][κ,κ][λ,λ][μ,μ][ν,ν][ξ,ξ][ο,ο][π,π][ρ,ρ][ς,ς][σ,σ][τ,τ][υ,υ][φ,φ][χ,χ][ψ,ψ][ω,ω][ϑ,ϑ][ϕ,ϕ][ϖ,ϖ][ϰ,ϰ][ϱ,ϱ][ϴ,ϴ][ϵ,ϵ][Ạ,Ạ][ạ,ạ][Ả,Ả][ả,ả][Ấ,Ấ][ấ,ấ][Ầ,Ầ][ầ,ầ][Ẩ,Ẩ][ẩ,ẩ][Ẫ,Ẫ][ẫ,ẫ][Ậ,Ậ][ậ,ậ][Ắ,Ắ][ắ,ắ][Ằ,Ằ][ằ,ằ][Ẳ,Ẳ][ẳ,ẳ][Ẵ,Ẵ][ẵ,ẵ][Ặ,Ặ][ặ,ặ][Ẹ,Ẹ][ẹ,ẹ][Ẻ,Ẻ][ẻ,ẻ][Ẽ,Ẽ][ẽ,ẽ][Ế,Ế][ế,ế][Ề,Ề][ề,ề][Ể,Ể][ể,ể][Ễ,Ễ][ễ,ễ][Ệ,Ệ][ệ,ệ][Ỉ,Ỉ][ỉ,ỉ][Ị,Ị][ị,ị][Ọ,Ọ][ọ,ọ][Ỏ,Ỏ][ỏ,ỏ][Ố,Ố][ố,ố][Ồ,Ồ][ồ,ồ][Ổ,Ổ][ổ,ổ][Ỗ,Ỗ][ỗ,ỗ][Ộ,Ộ][ộ,ộ][Ớ,Ớ][ớ,ớ][Ờ,Ờ][ờ,ờ][Ở,Ở][ở,ở][Ỡ,Ỡ][ỡ,ỡ][Ợ,Ợ][ợ,ợ][Ụ,Ụ][ụ,ụ][Ủ,Ủ][ủ,ủ][Ứ,Ứ][ứ,ứ][Ừ,Ừ][ừ,ừ][Ử,Ử][ử,ử][Ữ,Ữ][ữ,ữ][Ự,Ự][ự,ự][Ỳ,Ỳ][ỳ,ỳ][Ỵ,Ỵ][ỵ,ỵ][Ỷ,Ỷ][ỷ,ỷ][Ỹ,Ỹ][ỹ,ỹ][ , ][ , ][ , ][ , ][ , ][ , ][ , ][ , ][ , ][ , ][ , ][\u200B,\u200B][\u200C,\u200C][\u200D,\u200D][‐,‐][‒,‒][–,–][—,—][―,―][‖,‖][‗,‗][‘,‘][’,’][‚,‚][“,“][”,”][„,„][†,†][‡,‡][•,•][…,…][ , ][‰,‰][‱,‱][′,′][″,″][‴,‴][‵,‵][‶,‶][‷,‷][‹,‹][›,›][※,※][‽,‽][⁄,⁄][⁒,⁒][⁗,⁗][ , ][\u2060,\u2060][\u2061,\u2061][\u2062,\u2062][\u2063,\u2063][\u2064,\u2064][₡,₡][€,€][⃐,⃐][⃑,⃑][⃒,⃒][⃓,⃓][⃔,⃔][⃕,⃕][⃖,⃖][⃗,⃗][⃘,⃘][⃛,⃛][⃜,⃜][⃝,⃝][⃞,⃞][⃟,⃟][⃡,⃡][⃤,⃤][⃥,⃥][⃦,⃦][⃨,⃨][⃩,⃩][⃪,⃪][⃫,⃫][⃬,⃬][⃭,⃭][⃮,⃮][⃯,⃯][⃰,⃰][ℂ,ℂ][℃,℃][ℇ,ℇ][℉,℉][ℋ,ℋ][ℌ,ℌ][ℍ,ℍ][ℎ,ℎ][ℏ,ℏ][ℐ,ℐ][ℑ,ℑ][ℒ,ℒ][ℓ,ℓ][ℕ,ℕ][№,№][℗,℗][℘,℘][ℙ,ℙ][ℚ,ℚ][ℛ,ℛ][ℜ,ℜ][ℝ,ℝ][℞,℞][℠,℠][™,™][ℤ,ℤ][Ω,Ω][℧,℧][ℨ,ℨ][K,K][Å,Å][ℬ,ℬ][ℭ,ℭ][℮,℮][ℰ,ℰ][ℱ,ℱ][ℳ,ℳ][ℵ,ℵ][ℶ,ℶ][ℷ,ℷ][ℸ,ℸ][ℼ,ℼ][ℽ,ℽ][ℾ,ℾ][ℿ,ℿ][⅀,⅀][ⅅ,ⅅ][ⅆ,ⅆ][ⅇ,ⅇ][ⅈ,ⅈ][ⅉ,ⅉ][←,←][↑,↑][→,→][↓,↓][↔,↔][↕,↕][↖,↖][↗,↗][↘,↘][↙,↙][↚,↚][↛,↛][↞,↞][↟,↟][↠,↠][↡,↡][↢,↢][↣,↣][↤,↤][↥,↥][↦,↦][↧,↧][↩,↩][↪,↪][↫,↫][↬,↬][↭,↭][↮,↮][↰,↰][↱,↱][↲,↲][↳,↳][↴,↴][↵,↵][↶,↶][↷,↷][↺,↺][↻,↻][↼,↼][↽,↽][↾,↾][↿,↿][⇀,⇀][⇁,⇁][⇂,⇂][⇃,⇃][⇄,⇄][⇅,⇅][⇆,⇆][⇇,⇇][⇈,⇈][⇉,⇉][⇊,⇊][⇋,⇋][⇌,⇌][⇍,⇍][⇎,⇎][⇏,⇏][⇐,⇐][⇑,⇑][⇒,⇒][⇓,⇓][⇔,⇔][⇕,⇕][⇖,⇖][⇗,⇗][⇘,⇘][⇙,⇙][⇚,⇚][⇛,⇛][⇜,⇜][⇝,⇝][⇦,⇦][⇧,⇧][⇨,⇨][⇩,⇩][⇳,⇳][⇵,⇵][⇶,⇶][∀,∀][∁,∁][∂,∂][∃,∃][∄,∄][∅,∅][∆,∆][∇,∇][∈,∈][∉,∉][∊,∊][∋,∋][∌,∌][∍,∍][∎,∎][∏,∏][∐,∐][∑,∑][−,−][∓,∓][∔,∔][∕,∕][∖,∖][∗,∗][∘,∘][∙,∙][√,√][∝,∝][∞,∞][∟,∟][∠,∠][∡,∡][∢,∢][∣,∣][∤,∤][∥,∥][∦,∦][∧,∧][∨,∨][∩,∩][∪,∪][∫,∫][∬,∬][∭,∭][∮,∮][∯,∯][∰,∰][∱,∱][∲,∲][∳,∳][∴,∴][∵,∵][∶,∶][∷,∷][∸,∸][∹,∹][∺,∺][∻,∻][∼,∼][∽,∽][∾,∾][∿,∿][≀,≀][≁,≁][≂,≂][≃,≃][≄,≄][≅,≅][≆,≆][≇,≇][≈,≈][≉,≉][≊,≊][≋,≋][≌,≌][≍,≍][≎,≎][≏,≏][≐,≐][≑,≑][≒,≒][≓,≓][≔,≔][≕,≕][≖,≖][≗,≗][≘,≘][≙,≙][≚,≚][≛,≛][≜,≜][≝,≝][≞,≞][≟,≟][≠,≠][≡,≡][≢,≢][≣,≣][≤,≤][≥,≥][≦,≦][≧,≧][≨,≨][≩,≩][≪,≪][≫,≫][≬,≬][≭,≭][≮,≮][≯,≯][≰,≰][≱,≱][≲,≲][≳,≳][≴,≴][≵,≵][≶,≶][≷,≷][≸,≸][≹,≹][≺,≺][≻,≻][≼,≼][≽,≽][≾,≾][≿,≿][⊀,⊀][⊁,⊁][⊂,⊂][⊃,⊃][⊄,⊄][⊅,⊅][⊆,⊆][⊇,⊇][⊈,⊈][⊉,⊉][⊊,⊊][⊋,⊋][⊌,⊌][⊍,⊍][⊎,⊎][⊏,⊏][⊐,⊐][⊑,⊑][⊒,⊒][⊓,⊓][⊔,⊔][⊕,⊕][⊖,⊖][⊗,⊗][⊘,⊘][⊙,⊙][⊚,⊚][⊛,⊛][⊜,⊜][⊝,⊝][⊞,⊞][⊟,⊟][⊠,⊠][⊡,⊡][⊢,⊢][⊣,⊣][⊤,⊤][⊥,⊥][⊦,⊦][⊧,⊧][⊨,⊨][⊩,⊩][⊪,⊪][⊫,⊫][⊬,⊬][⊭,⊭][⊮,⊮][⊯,⊯][⊲,⊲][⊳,⊳][⊴,⊴][⊵,⊵][⊶,⊶][⊷,⊷][⊸,⊸][⊹,⊹][⊺,⊺][⊻,⊻][⊼,⊼][⊽,⊽][⊾,⊾][⊿,⊿][⋀,⋀][⋁,⋁][⋂,⋂][⋃,⋃][⋄,⋄][⋅,⋅][⋆,⋆][⋇,⋇][⋈,⋈][⋉,⋉][⋊,⋊][⋋,⋋][⋌,⋌][⋍,⋍][⋎,⋎][⋏,⋏][⋐,⋐][⋑,⋑][⋒,⋒][⋓,⋓][⋕,⋕][⋖,⋖][⋗,⋗][⋘,⋘][⋙,⋙][⋚,⋚][⋛,⋛][⋜,⋜][⋝,⋝][⋞,⋞][⋟,⋟][⋠,⋠][⋡,⋡][⋢,⋢][⋣,⋣][⋤,⋤][⋥,⋥][⋦,⋦][⋧,⋧][⋨,⋨][⋩,⋩][⋪,⋪][⋫,⋫][⋬,⋬][⋭,⋭][⋮,⋮][⋯,⋯][⋰,⋰][⋱,⋱][⌀,⌀][⌅,⌅][⌆,⌆][⌈,⌈][⌉,⌉][⌊,⌊][⌋,⌋][⌐,⌐][⌙,⌙][⌜,⌜][⌝,⌝][⌞,⌞][⌟,⌟][⌠,⌠][⌡,⌡][⌢,⌢][⌣,⌣][〈,〈][〉,〉][⎛,⎛][⎜,⎜][⎝,⎝][⎞,⎞][⎟,⎟][⎠,⎠][⎡,⎡][⎢,⎢][⎣,⎣][⎤,⎤][⎥,⎥][⎦,⎦][⎧,⎧][⎨,⎨][⎩,⎩][⎪,⎪][⎫,⎫][⎬,⎬][⎭,⎭][⎲,⎲][⎳,⎳][⎴,⎴][⎵,⎵][⎷,⎷][⏐,⏐][⏜,⏜][⏝,⏝][⏞,⏞][⏟,⏟][⏠,⏠][⏡,⏡][␢,␢][␣,␣][─,─][│,│][┌,┌][┐,┐][└,└][┘,┘][├,├][┤,┤][┬,┬][┴,┴][┼,┼][▁,▁][█,█][░,░][▒,▒][▓,▓][■,■][□,□][▪,▪][▫,▫][▬,▬][▭,▭][▲,▲][△,△][▶,▶][▷,▷][▼,▼][▽,▽][◀,◀][◁,◁][◊,◊][○,○][●,●][◦,◦][◯,◯][♠,♠][♡,♡][♢,♢][♣,♣][♤,♤][♥,♥][♦,♦][♧,♧][♪,♪][♭,♭][♮,♮][♯,♯][⚭,⚭][⚮,⚮][✓,✓][✠,✠][❚,❚][➡,➡][⟂,⟂][⟘,⟘][⟙,⟙][⟚,⟚][⟛,⟛][⟜,⟜][⟝,⟝][⟞,⟞][⟠,⟠][⟡,⟡][⟢,⟢][⟣,⟣][⟦,⟦][⟧,⟧][⟨,⟨][⟩,⟩][⟪,⟪][⟫,⟫][⟮,⟮][⟯,⟯][⟴,⟴][⟵,⟵][⟶,⟶][⟷,⟷][⟸,⟸][⟹,⟹][⟺,⟺][⟻,⟻][⟼,⟼][⟽,⟽][⟾,⟾][⟿,⟿][⤆,⤆][⤇,⤇][⨀,⨀][⨁,⨁][⨂,⨂][⨃,⨃][⨄,⨄][⨅,⨅][⨆,⨆][⨉,⨉][⨌,⨌][⨑,⨑][⨯,⨯][⨿,⨿][⩽,⩽][⩾,⩾][⪅,⪅][⪆,⪆][⪇,⪇][⪈,⪈][⪉,⪉][⪊,⪊][⪋,⪋][⪌,⪌][⪕,⪕][⪖,⪖][⪯,⪯][⪰,⪰][⬄,⬄][⬅,⬅][⬆,⬆][⬇,⬇][⬌,⬌][⬍,⬍][⬚,⬚][⬱,⬱][⬳,⬳][⸘,⸘][〖,〖][〗,〗][ﬀ,ﬀ][ﬁ,ﬁ][ﬂ,ﬂ][ﬃ,ﬃ][ﬄ,ﬄ][\uFEFF,\uFEFF][ᵀ0,\uD835\uDC00][ᵀ1,\uD835\uDC01][ᵀ2,\uD835\uDC02][ᵀ3,\uD835\uDC03][ᵀ4,\uD835\uDC04][ᵀ5,\uD835\uDC05][ᵀ6,\uD835\uDC06][ᵀ7,\uD835\uDC07][ᵀ8,\uD835\uDC08][ᵀ9,\uD835\uDC09][ᵀA,\uD835\uDC0A][ᵀB,\uD835\uDC0B][ᵀC,\uD835\uDC0C][ᵀD,\uD835\uDC0D][ᵀE,\uD835\uDC0E][ᵀF,\uD835\uDC0F][ᵁ0,\uD835\uDC10][ᵁ1,\uD835\uDC11][ᵁ2,\uD835\uDC12][ᵁ3,\uD835\uDC13][ᵁ4,\uD835\uDC14][ᵁ5,\uD835\uDC15][ᵁ6,\uD835\uDC16][ᵁ7,\uD835\uDC17][ᵁ8,\uD835\uDC18][ᵁ9,\uD835\uDC19][ᵁA,\uD835\uDC1A][ᵁB,\uD835\uDC1B][ᵁC,\uD835\uDC1C][ᵁD,\uD835\uDC1D][ᵁE,\uD835\uDC1E][ᵁF,\uD835\uDC1F][ᵂ0,\uD835\uDC20][ᵂ1,\uD835\uDC21][ᵂ2,\uD835\uDC22][ᵂ3,\uD835\uDC23][ᵂ4,\uD835\uDC24][ᵂ5,\uD835\uDC25][ᵂ6,\uD835\uDC26][ᵂ7,\uD835\uDC27][ᵂ8,\uD835\uDC28][ᵂ9,\uD835\uDC29][ᵂA,\uD835\uDC2A][ᵂB,\uD835\uDC2B][ᵂC,\uD835\uDC2C][ᵂD,\uD835\uDC2D][ᵂE,\uD835\uDC2E][ᵂF,\uD835\uDC2F][ᵃ0,\uD835\uDC30][ᵃ1,\uD835\uDC31][ᵃ2,\uD835\uDC32][ᵃ3,\uD835\uDC33][ᵃ4,\uD835\uDC34][ᵃ5,\uD835\uDC35][ᵃ6,\uD835\uDC36][ᵃ7,\uD835\uDC37][ᵃ8,\uD835\uDC38][ᵃ9,\uD835\uDC39][ᵃA,\uD835\uDC3A][ᵃB,\uD835\uDC3B][ᵃC,\uD835\uDC3C][ᵃD,\uD835\uDC3D][ᵃE,\uD835\uDC3E][ᵃF,\uD835\uDC3F][ᵄ0,\uD835\uDC40][ᵄ1,\uD835\uDC41][ᵄ2,\uD835\uDC42][ᵄ3,\uD835\uDC43][ᵄ4,\uD835\uDC44][ᵄ5,\uD835\uDC45][ᵄ6,\uD835\uDC46][ᵄ7,\uD835\uDC47][ᵄ8,\uD835\uDC48][ᵄ9,\uD835\uDC49][ᵄA,\uD835\uDC4A][ᵄB,\uD835\uDC4B][ᵄC,\uD835\uDC4C][ᵄD,\uD835\uDC4D][ᵄE,\uD835\uDC4E][ᵄF,\uD835\uDC4F][ᵅ0,\uD835\uDC50][ᵅ1,\uD835\uDC51][ᵅ2,\uD835\uDC52][ᵅ3,\uD835\uDC53][ᵅ4,\uD835\uDC54][ᵅ6,\uD835\uDC56][ᵅ7,\uD835\uDC57][ᵅ8,\uD835\uDC58][ᵅ9,\uD835\uDC59][ᵅA,\uD835\uDC5A][ᵅB,\uD835\uDC5B][ᵅC,\uD835\uDC5C][ᵅD,\uD835\uDC5D][ᵅE,\uD835\uDC5E][ᵅF,\uD835\uDC5F][ᵆ0,\uD835\uDC60][ᵆ1,\uD835\uDC61][ᵆ2,\uD835\uDC62][ᵆ3,\uD835\uDC63][ᵆ4,\uD835\uDC64][ᵆ5,\uD835\uDC65][ᵆ6,\uD835\uDC66][ᵆ7,\uD835\uDC67][ᵆ8,\uD835\uDC68][ᵆ9,\uD835\uDC69][ᵆA,\uD835\uDC6A][ᵆB,\uD835\uDC6B][ᵆC,\uD835\uDC6C][ᵆD,\uD835\uDC6D][ᵆE,\uD835\uDC6E][ᵆF,\uD835\uDC6F][ᵇ0,\uD835\uDC70][ᵇ1,\uD835\uDC71][ᵇ2,\uD835\uDC72][ᵇ3,\uD835\uDC73][ᵇ4,\uD835\uDC74][ᵇ5,\uD835\uDC75][ᵇ6,\uD835\uDC76][ᵇ7,\uD835\uDC77][ᵇ8,\uD835\uDC78][ᵇ9,\uD835\uDC79][ᵇA,\uD835\uDC7A][ᵇB,\uD835\uDC7B][ᵇC,\uD835\uDC7C][ᵇD,\uD835\uDC7D][ᵇE,\uD835\uDC7E][ᵇF,\uD835\uDC7F][ᵈ0,\uD835\uDC80][ᵈ1,\uD835\uDC81][ᵈ2,\uD835\uDC82][ᵈ3,\uD835\uDC83][ᵈ4,\uD835\uDC84][ᵈ5,\uD835\uDC85][ᵈ6,\uD835\uDC86][ᵈ7,\uD835\uDC87][ᵈ8,\uD835\uDC88][ᵈ9,\uD835\uDC89][ᵈA,\uD835\uDC8A][ᵈB,\uD835\uDC8B][ᵈC,\uD835\uDC8C][ᵈD,\uD835\uDC8D][ᵈE,\uD835\uDC8E][ᵈF,\uD835\uDC8F][ᵉ0,\uD835\uDC90][ᵉ1,\uD835\uDC91][ᵉ2,\uD835\uDC92][ᵉ3,\uD835\uDC93][ᵉ4,\uD835\uDC94][ᵉ5,\uD835\uDC95][ᵉ6,\uD835\uDC96][ᵉ7,\uD835\uDC97][ᵉ8,\uD835\uDC98][ᵉ9,\uD835\uDC99][ᵉA,\uD835\uDC9A][ᵉB,\uD835\uDC9B][ᵉC,\uD835\uDC9C][ᵉE,\uD835\uDC9E][ᵉF,\uD835\uDC9F][ᵊ2,\uD835\uDCA2][ᵊ5,\uD835\uDCA5][ᵊ6,\uD835\uDCA6][ᵊ9,\uD835\uDCA9][ᵊA,\uD835\uDCAA][ᵊB,\uD835\uDCAB][ᵊC,\uD835\uDCAC][ᵊE,\uD835\uDCAE][ᵊF,\uD835\uDCAF][ᵋ0,\uD835\uDCB0][ᵋ1,\uD835\uDCB1][ᵋ2,\uD835\uDCB2][ᵋ3,\uD835\uDCB3][ᵋ4,\uD835\uDCB4][ᵋ5,\uD835\uDCB5][ᵍ0,\uD835\uDCD0][ᵍ1,\uD835\uDCD1][ᵍ2,\uD835\uDCD2][ᵍ3,\uD835\uDCD3][ᵍ4,\uD835\uDCD4][ᵍ5,\uD835\uDCD5][ᵍ6,\uD835\uDCD6][ᵍ7,\uD835\uDCD7][ᵍ8,\uD835\uDCD8][ᵍ9,\uD835\uDCD9][ᵍA,\uD835\uDCDA][ᵍB,\uD835\uDCDB][ᵍC,\uD835\uDCDC][ᵍD,\uD835\uDCDD][ᵍE,\uD835\uDCDE][ᵍF,\uD835\uDCDF][ᵎ0,\uD835\uDCE0][ᵎ1,\uD835\uDCE1][ᵎ2,\uD835\uDCE2][ᵎ3,\uD835\uDCE3][ᵎ4,\uD835\uDCE4][ᵎ5,\uD835\uDCE5][ᵎ6,\uD835\uDCE6][ᵎ7,\uD835\uDCE7][ᵎ8,\uD835\uDCE8][ᵎ9,\uD835\uDCE9][ᵐ4,\uD835\uDD04][ᵐ5,\uD835\uDD05][ᵐ7,\uD835\uDD07][ᵐ8,\uD835\uDD08][ᵐ9,\uD835\uDD09][ᵐA,\uD835\uDD0A][ᵐD,\uD835\uDD0D][ᵐE,\uD835\uDD0E][ᵐF,\uD835\uDD0F][ᵑ0,\uD835\uDD10][ᵑ1,\uD835\uDD11][ᵑ2,\uD835\uDD12][ᵑ3,\uD835\uDD13][ᵑ4,\uD835\uDD14][ᵑ6,\uD835\uDD16][ᵑ7,\uD835\uDD17][ᵑ8,\uD835\uDD18][ᵑ9,\uD835\uDD19][ᵑA,\uD835\uDD1A][ᵑB,\uD835\uDD1B][ᵑC,\uD835\uDD1C][ᵑE,\uD835\uDD1E][ᵑF,\uD835\uDD1F][ᵒ0,\uD835\uDD20][ᵒ1,\uD835\uDD21][ᵒ2,\uD835\uDD22][ᵒ3,\uD835\uDD23][ᵒ4,\uD835\uDD24][ᵒ5,\uD835\uDD25][ᵒ6,\uD835\uDD26][ᵒ7,\uD835\uDD27][ᵒ8,\uD835\uDD28][ᵒ9,\uD835\uDD29][ᵒA,\uD835\uDD2A][ᵒB,\uD835\uDD2B][ᵒC,\uD835\uDD2C][ᵒD,\uD835\uDD2D][ᵒE,\uD835\uDD2E][ᵒF,\uD835\uDD2F][ᵓ0,\uD835\uDD30][ᵓ1,\uD835\uDD31][ᵓ2,\uD835\uDD32][ᵓ3,\uD835\uDD33][ᵓ4,\uD835\uDD34][ᵓ5,\uD835\uDD35][ᵓ6,\uD835\uDD36][ᵓ7,\uD835\uDD37][ᵓ8,\uD835\uDD38][ᵓ9,\uD835\uDD39][ᵓB,\uD835\uDD3B][ᵓC,\uD835\uDD3C][ᵓD,\uD835\uDD3D][ᵓE,\uD835\uDD3E][ᵔ0,\uD835\uDD40][ᵔ1,\uD835\uDD41][ᵔ2,\uD835\uDD42][ᵔ3,\uD835\uDD43][ᵔ4,\uD835\uDD44][ᵔ6,\uD835\uDD46][ᵔA,\uD835\uDD4A][ᵔB,\uD835\uDD4B][ᵔC,\uD835\uDD4C][ᵔD,\uD835\uDD4D][ᵔE,\uD835\uDD4E][ᵔF,\uD835\uDD4F][ᵕ0,\uD835\uDD50][ᵕ2,\uD835\uDD52][ᵕ3,\uD835\uDD53][ᵕ4,\uD835\uDD54][ᵕ5,\uD835\uDD55][ᵕ6,\uD835\uDD56][ᵕ7,\uD835\uDD57][ᵕ8,\uD835\uDD58][ᵕ9,\uD835\uDD59][ᵕA,\uD835\uDD5A][ᵕB,\uD835\uDD5B][ᵕC,\uD835\uDD5C][ᵕD,\uD835\uDD5D][ᵕE,\uD835\uDD5E][ᵕF,\uD835\uDD5F][ᵖ0,\uD835\uDD60][ᵖ1,\uD835\uDD61][ᵖ2,\uD835\uDD62][ᵖ3,\uD835\uDD63][ᵖ4,\uD835\uDD64][ᵖ5,\uD835\uDD65][ᵖ6,\uD835\uDD66][ᵖ7,\uD835\uDD67][ᵖ8,\uD835\uDD68][ᵖ9,\uD835\uDD69][ᵖA,\uD835\uDD6A][ᵖB,\uD835\uDD6B][ᵖC,\uD835\uDD6C][ᵖD,\uD835\uDD6D][ᵖE,\uD835\uDD6E][ᵖF,\uD835\uDD6F][ᵗ0,\uD835\uDD70][ᵗ1,\uD835\uDD71][ᵗ2,\uD835\uDD72][ᵗ3,\uD835\uDD73][ᵗ4,\uD835\uDD74][ᵗ5,\uD835\uDD75][ᵗ6,\uD835\uDD76][ᵗ7,\uD835\uDD77][ᵗ8,\uD835\uDD78][ᵗ9,\uD835\uDD79][ᵗA,\uD835\uDD7A][ᵗB,\uD835\uDD7B][ᵗC,\uD835\uDD7C][ᵗD,\uD835\uDD7D][ᵗE,\uD835\uDD7E][ᵗF,\uD835\uDD7F][ᵘ0,\uD835\uDD80][ᵘ1,\uD835\uDD81][ᵘ2,\uD835\uDD82][ᵘ3,\uD835\uDD83][ᵘ4,\uD835\uDD84][ᵘ5,\uD835\uDD85][ᵘ6,\uD835\uDD86][ᵘ7,\uD835\uDD87][ᵘ8,\uD835\uDD88][ᵘ9,\uD835\uDD89][ᵘA,\uD835\uDD8A][ᵘB,\uD835\uDD8B][ᵘC,\uD835\uDD8C][ᵘD,\uD835\uDD8D][ᵘE,\uD835\uDD8E][ᵘF,\uD835\uDD8F][ᵙ0,\uD835\uDD90][ᵙ1,\uD835\uDD91][ᵙ2,\uD835\uDD92][ᵙ3,\uD835\uDD93][ᵙ4,\uD835\uDD94][ᵙ5,\uD835\uDD95][ᵙ6,\uD835\uDD96][ᵙ7,\uD835\uDD97][ᵙ8,\uD835\uDD98][ᵙ9,\uD835\uDD99][ᵙA,\uD835\uDD9A][ᵙB,\uD835\uDD9B][ᵙC,\uD835\uDD9C][ᵙD,\uD835\uDD9D][ᵙE,\uD835\uDD9E][ᵙF,\uD835\uDD9F][ᵚ0,\uD835\uDDA0][ᵚ1,\uD835\uDDA1][ᵚ2,\uD835\uDDA2][ᵚ3,\uD835\uDDA3][ᵚ4,\uD835\uDDA4][ᵚ5,\uD835\uDDA5][ᵚ6,\uD835\uDDA6][ᵚ7,\uD835\uDDA7][ᵚ8,\uD835\uDDA8][ᵚ9,\uD835\uDDA9][ᵚA,\uD835\uDDAA][ᵚB,\uD835\uDDAB][ᵚC,\uD835\uDDAC][ᵚD,\uD835\uDDAD][ᵚE,\uD835\uDDAE][ᵚF,\uD835\uDDAF][ᵛ0,\uD835\uDDB0][ᵛ1,\uD835\uDDB1][ᵛ2,\uD835\uDDB2][ᵛ3,\uD835\uDDB3][ᵛ4,\uD835\uDDB4][ᵛ5,\uD835\uDDB5][ᵛ6,\uD835\uDDB6][ᵛ7,\uD835\uDDB7][ᵛ8,\uD835\uDDB8][ᵛ9,\uD835\uDDB9][ᵛA,\uD835\uDDBA][ᵛB,\uD835\uDDBB][ᵛC,\uD835\uDDBC][ᵛD,\uD835\uDDBD][ᵛE,\uD835\uDDBE][ᵛF,\uD835\uDDBF][ᵜ0,\uD835\uDDC0][ᵜ1,\uD835\uDDC1][ᵜ2,\uD835\uDDC2][ᵜ3,\uD835\uDDC3][ᵜ4,\uD835\uDDC4][ᵜ5,\uD835\uDDC5][ᵜ6,\uD835\uDDC6][ᵜ7,\uD835\uDDC7][ᵜ8,\uD835\uDDC8][ᵜ9,\uD835\uDDC9][ᵜA,\uD835\uDDCA][ᵜB,\uD835\uDDCB][ᵜC,\uD835\uDDCC][ᵜD,\uD835\uDDCD][ᵜE,\uD835\uDDCE][ᵜF,\uD835\uDDCF][ᵝ0,\uD835\uDDD0][ᵝ1,\uD835\uDDD1][ᵝ2,\uD835\uDDD2][ᵝ3,\uD835\uDDD3][ᵝ4,\uD835\uDDD4][ᵝ5,\uD835\uDDD5][ᵝ6,\uD835\uDDD6][ᵝ7,\uD835\uDDD7][ᵝ8,\uD835\uDDD8][ᵝ9,\uD835\uDDD9][ᵝA,\uD835\uDDDA][ᵝB,\uD835\uDDDB][ᵝC,\uD835\uDDDC][ᵝD,\uD835\uDDDD][ᵝE,\uD835\uDDDE][ᵝF,\uD835\uDDDF][ᵞ0,\uD835\uDDE0][ᵞ1,\uD835\uDDE1][ᵞ2,\uD835\uDDE2][ᵞ3,\uD835\uDDE3][ᵞ4,\uD835\uDDE4][ᵞ5,\uD835\uDDE5][ᵞ6,\uD835\uDDE6][ᵞ7,\uD835\uDDE7][ᵞ8,\uD835\uDDE8][ᵞ9,\uD835\uDDE9][ᵞA,\uD835\uDDEA][ᵞB,\uD835\uDDEB][ᵞC,\uD835\uDDEC][ᵞD,\uD835\uDDED][ᵞE,\uD835\uDDEE][ᵞF,\uD835\uDDEF][ᵟ0,\uD835\uDDF0][ᵟ1,\uD835\uDDF1][ᵟ2,\uD835\uDDF2][ᵟ3,\uD835\uDDF3][ᵟ4,\uD835\uDDF4][ᵟ5,\uD835\uDDF5][ᵟ6,\uD835\uDDF6][ᵟ7,\uD835\uDDF7][ᵟ8,\uD835\uDDF8][ᵟ9,\uD835\uDDF9][ᵟA,\uD835\uDDFA][ᵟB,\uD835\uDDFB][ᵟC,\uD835\uDDFC][ᵟD,\uD835\uDDFD][ᵟE,\uD835\uDDFE][ᵟF,\uD835\uDDFF][ᵠ0,\uD835\uDE00][ᵠ1,\uD835\uDE01][ᵠ2,\uD835\uDE02][ᵠ3,\uD835\uDE03][ᵠ4,\uD835\uDE04][ᵠ5,\uD835\uDE05][ᵠ6,\uD835\uDE06][ᵠ7,\uD835\uDE07][ᵠ8,\uD835\uDE08][ᵠ9,\uD835\uDE09][ᵠA,\uD835\uDE0A][ᵠB,\uD835\uDE0B][ᵠC,\uD835\uDE0C][ᵠD,\uD835\uDE0D][ᵠE,\uD835\uDE0E][ᵠF,\uD835\uDE0F][ᵡ0,\uD835\uDE10][ᵡ1,\uD835\uDE11][ᵡ2,\uD835\uDE12][ᵡ3,\uD835\uDE13][ᵡ4,\uD835\uDE14][ᵡ5,\uD835\uDE15][ᵡ6,\uD835\uDE16][ᵡ7,\uD835\uDE17][ᵡ8,\uD835\uDE18][ᵡ9,\uD835\uDE19][ᵡA,\uD835\uDE1A][ᵡB,\uD835\uDE1B][ᵡC,\uD835\uDE1C][ᵡD,\uD835\uDE1D][ᵡE,\uD835\uDE1E][ᵡF,\uD835\uDE1F][ᵢ0,\uD835\uDE20][ᵢ1,\uD835\uDE21][ᵢ2,\uD835\uDE22][ᵢ3,\uD835\uDE23][ᵢ4,\uD835\uDE24][ᵢ5,\uD835\uDE25][ᵢ6,\uD835\uDE26][ᵢ7,\uD835\uDE27][ᵢ8,\uD835\uDE28][ᵢ9,\uD835\uDE29][ᵢA,\uD835\uDE2A][ᵢB,\uD835\uDE2B][ᵢC,\uD835\uDE2C][ᵢD,\uD835\uDE2D][ᵢE,\uD835\uDE2E][ᵢF,\uD835\uDE2F][ᵣ0,\uD835\uDE30][ᵣ1,\uD835\uDE31][ᵣ2,\uD835\uDE32][ᵣ3,\uD835\uDE33][ᵣ4,\uD835\uDE34][ᵣ5,\uD835\uDE35][ᵣ6,\uD835\uDE36][ᵣ7,\uD835\uDE37][ᵣ8,\uD835\uDE38][ᵣ9,\uD835\uDE39][ᵣA,\uD835\uDE3A][ᵣB,\uD835\uDE3B][ᵣC,\uD835\uDE3C][ᵣD,\uD835\uDE3D][ᵣE,\uD835\uDE3E][ᵣF,\uD835\uDE3F][ᵤ0,\uD835\uDE40][ᵤ1,\uD835\uDE41][ᵤ2,\uD835\uDE42][ᵤ3,\uD835\uDE43][ᵤ4,\uD835\uDE44][ᵤ5,\uD835\uDE45][ᵤ6,\uD835\uDE46][ᵤ7,\uD835\uDE47][ᵤ8,\uD835\uDE48][ᵤ9,\uD835\uDE49][ᵤA,\uD835\uDE4A][ᵤB,\uD835\uDE4B][ᵤC,\uD835\uDE4C][ᵤD,\uD835\uDE4D][ᵤE,\uD835\uDE4E][ᵤF,\uD835\uDE4F][ᵥ0,\uD835\uDE50][ᵥ1,\uD835\uDE51][ᵥ2,\uD835\uDE52][ᵥ3,\uD835\uDE53][ᵥ4,\uD835\uDE54][ᵥ5,\uD835\uDE55][ᵥ6,\uD835\uDE56][ᵥ7,\uD835\uDE57][ᵥ8,\uD835\uDE58][ᵥ9,\uD835\uDE59][ᵥA,\uD835\uDE5A][ᵥB,\uD835\uDE5B][ᵥC,\uD835\uDE5C][ᵥD,\uD835\uDE5D][ᵥE,\uD835\uDE5E][ᵥF,\uD835\uDE5F][ᵦ0,\uD835\uDE60][ᵦ1,\uD835\uDE61][ᵦ2,\uD835\uDE62][ᵦ3,\uD835\uDE63][ᵦ4,\uD835\uDE64][ᵦ5,\uD835\uDE65][ᵦ6,\uD835\uDE66][ᵦ7,\uD835\uDE67][ᵦ8,\uD835\uDE68][ᵦ9,\uD835\uDE69][ᵦA,\uD835\uDE6A][ᵦB,\uD835\uDE6B][ᵦC,\uD835\uDE6C][ᵦD,\uD835\uDE6D][ᵦE,\uD835\uDE6E][ᵦF,\uD835\uDE6F][ᵧ0,\uD835\uDE70][ᵧ1,\uD835\uDE71][ᵧ2,\uD835\uDE72][ᵧ3,\uD835\uDE73][ᵧ4,\uD835\uDE74][ᵧ5,\uD835\uDE75][ᵧ6,\uD835\uDE76][ᵧ7,\uD835\uDE77][ᵧ8,\uD835\uDE78][ᵧ9,\uD835\uDE79][ᵧA,\uD835\uDE7A][ᵧB,\uD835\uDE7B][ᵧC,\uD835\uDE7C][ᵧD,\uD835\uDE7D][ᵧE,\uD835\uDE7E][ᵧF,\uD835\uDE7F][ᵨ0,\uD835\uDE80][ᵨ1,\uD835\uDE81][ᵨ2,\uD835\uDE82][ᵨ3,\uD835\uDE83][ᵨ4,\uD835\uDE84][ᵨ5,\uD835\uDE85][ᵨ6,\uD835\uDE86][ᵨ7,\uD835\uDE87][ᵨ8,\uD835\uDE88][ᵨ9,\uD835\uDE89][ᵨA,\uD835\uDE8A][ᵨB,\uD835\uDE8B][ᵨC,\uD835\uDE8C][ᵨD,\uD835\uDE8D][ᵨE,\uD835\uDE8E][ᵨF,\uD835\uDE8F][ᵩ0,\uD835\uDE90][ᵩ1,\uD835\uDE91][ᵩ2,\uD835\uDE92][ᵩ3,\uD835\uDE93][ᵩ4,\uD835\uDE94][ᵩ5,\uD835\uDE95][ᵩ6,\uD835\uDE96][ᵩ7,\uD835\uDE97][ᵩ8,\uD835\uDE98][ᵩ9,\uD835\uDE99][ᵩA,\uD835\uDE9A][ᵩB,\uD835\uDE9B][ᵩC,\uD835\uDE9C][ᵩD,\uD835\uDE9D][ᵩE,\uD835\uDE9E][ᵩF,\uD835\uDE9F][ᵪ0,\uD835\uDEA0][ᵪ1,\uD835\uDEA1][ᵪ2,\uD835\uDEA2][ᵪ3,\uD835\uDEA3][ᵪ4,\uD835\uDEA4][ᵪ5,\uD835\uDEA5][ᵪ8,\uD835\uDEA8][ᵪ9,\uD835\uDEA9][ᵪA,\uD835\uDEAA][ᵪB,\uD835\uDEAB][ᵪC,\uD835\uDEAC][ᵪD,\uD835\uDEAD][ᵪE,\uD835\uDEAE][ᵪF,\uD835\uDEAF][ᵫ0,\uD835\uDEB0][ᵫ1,\uD835\uDEB1][ᵫ2,\uD835\uDEB2][ᵫ3,\uD835\uDEB3][ᵫ4,\uD835\uDEB4][ᵫ5,\uD835\uDEB5][ᵫ6,\uD835\uDEB6][ᵫ7,\uD835\uDEB7][ᵫ8,\uD835\uDEB8][ᵫ9,\uD835\uDEB9][ᵫA,\uD835\uDEBA][ᵫB,\uD835\uDEBB][ᵫC,\uD835\uDEBC][ᵫD,\uD835\uDEBD][ᵫE,\uD835\uDEBE][ᵫF,\uD835\uDEBF][ᵬ0,\uD835\uDEC0][ᵬ1,\uD835\uDEC1][ᵬ2,\uD835\uDEC2][ᵬ3,\uD835\uDEC3][ᵬ4,\uD835\uDEC4][ᵬ5,\uD835\uDEC5][ᵬ6,\uD835\uDEC6][ᵬ7,\uD835\uDEC7][ᵬ8,\uD835\uDEC8][ᵬ9,\uD835\uDEC9][ᵬA,\uD835\uDECA][ᵬB,\uD835\uDECB][ᵬC,\uD835\uDECC][ᵬD,\uD835\uDECD][ᵬE,\uD835\uDECE][ᵬF,\uD835\uDECF][ᵭ0,\uD835\uDED0][ᵭ1,\uD835\uDED1][ᵭ2,\uD835\uDED2][ᵭ3,\uD835\uDED3][ᵭ4,\uD835\uDED4][ᵭ5,\uD835\uDED5][ᵭ6,\uD835\uDED6][ᵭ7,\uD835\uDED7][ᵭ8,\uD835\uDED8][ᵭ9,\uD835\uDED9][ᵭA,\uD835\uDEDA][ᵭB,\uD835\uDEDB][ᵭC,\uD835\uDEDC][ᵭD,\uD835\uDEDD][ᵭE,\uD835\uDEDE][ᵭF,\uD835\uDEDF][ᵮ0,\uD835\uDEE0][ᵮ1,\uD835\uDEE1][ᵮ2,\uD835\uDEE2][ᵮ3,\uD835\uDEE3][ᵮ4,\uD835\uDEE4][ᵮ5,\uD835\uDEE5][ᵮ6,\uD835\uDEE6][ᵮ7,\uD835\uDEE7][ᵮ8,\uD835\uDEE8][ᵮ9,\uD835\uDEE9][ᵮA,\uD835\uDEEA][ᵮB,\uD835\uDEEB][ᵮC,\uD835\uDEEC][ᵮD,\uD835\uDEED][ᵮE,\uD835\uDEEE][ᵮF,\uD835\uDEEF][ᵯ0,\uD835\uDEF0][ᵯ1,\uD835\uDEF1][ᵯ2,\uD835\uDEF2][ᵯ3,\uD835\uDEF3][ᵯ4,\uD835\uDEF4][ᵯ5,\uD835\uDEF5][ᵯ6,\uD835\uDEF6][ᵯ7,\uD835\uDEF7][ᵯ8,\uD835\uDEF8][ᵯ9,\uD835\uDEF9][ᵯA,\uD835\uDEFA][ᵯB,\uD835\uDEFB][ᵯC,\uD835\uDEFC][ᵯD,\uD835\uDEFD][ᵯE,\uD835\uDEFE][ᵯF,\uD835\uDEFF][ᵰ0,\uD835\uDF00][ᵰ1,\uD835\uDF01][ᵰ2,\uD835\uDF02][ᵰ3,\uD835\uDF03][ᵰ4,\uD835\uDF04][ᵰ5,\uD835\uDF05][ᵰ6,\uD835\uDF06][ᵰ7,\uD835\uDF07][ᵰ8,\uD835\uDF08][ᵰ9,\uD835\uDF09][ᵰA,\uD835\uDF0A][ᵰB,\uD835\uDF0B][ᵰC,\uD835\uDF0C][ᵰD,\uD835\uDF0D][ᵰE,\uD835\uDF0E][ᵰF,\uD835\uDF0F][ᵱ0,\uD835\uDF10][ᵱ1,\uD835\uDF11][ᵱ2,\uD835\uDF12][ᵱ3,\uD835\uDF13][ᵱ4,\uD835\uDF14][ᵱ5,\uD835\uDF15][ᵱ6,\uD835\uDF16][ᵱ7,\uD835\uDF17][ᵱ8,\uD835\uDF18][ᵱ9,\uD835\uDF19][ᵱA,\uD835\uDF1A][ᵱB,\uD835\uDF1B][ᵱC,\uD835\uDF1C][ᵱD,\uD835\uDF1D][ᵱE,\uD835\uDF1E][ᵱF,\uD835\uDF1F][ᵲ0,\uD835\uDF20][ᵲ1,\uD835\uDF21][ᵲ2,\uD835\uDF22][ᵲ3,\uD835\uDF23][ᵲ4,\uD835\uDF24][ᵲ5,\uD835\uDF25][ᵲ6,\uD835\uDF26][ᵲ7,\uD835\uDF27][ᵲ8,\uD835\uDF28][ᵲ9,\uD835\uDF29][ᵲA,\uD835\uDF2A][ᵲB,\uD835\uDF2B][ᵲC,\uD835\uDF2C][ᵲD,\uD835\uDF2D][ᵲE,\uD835\uDF2E][ᵲF,\uD835\uDF2F][ᵳ0,\uD835\uDF30][ᵳ1,\uD835\uDF31][ᵳ2,\uD835\uDF32][ᵳ3,\uD835\uDF33][ᵳ4,\uD835\uDF34][ᵳ5,\uD835\uDF35][ᵳ6,\uD835\uDF36][ᵳ7,\uD835\uDF37][ᵳ8,\uD835\uDF38][ᵳ9,\uD835\uDF39][ᵳA,\uD835\uDF3A][ᵳB,\uD835\uDF3B][ᵳC,\uD835\uDF3C][ᵳD,\uD835\uDF3D][ᵳE,\uD835\uDF3E][ᵳF,\uD835\uDF3F][ᵴ0,\uD835\uDF40][ᵴ1,\uD835\uDF41][ᵴ2,\uD835\uDF42][ᵴ3,\uD835\uDF43][ᵴ4,\uD835\uDF44][ᵴ5,\uD835\uDF45][ᵴ6,\uD835\uDF46][ᵴ7,\uD835\uDF47][ᵴ8,\uD835\uDF48][ᵴ9,\uD835\uDF49][ᵴA,\uD835\uDF4A][ᵴB,\uD835\uDF4B][ᵴC,\uD835\uDF4C][ᵴD,\uD835\uDF4D][ᵴE,\uD835\uDF4E][ᵴF,\uD835\uDF4F][ᵵ0,\uD835\uDF50][ᵵ1,\uD835\uDF51][ᵵ2,\uD835\uDF52][ᵵ3,\uD835\uDF53][ᵵ4,\uD835\uDF54][ᵵ5,\uD835\uDF55][ᵵ6,\uD835\uDF56][ᵵ7,\uD835\uDF57][ᵵ8,\uD835\uDF58][ᵵ9,\uD835\uDF59][ᵵA,\uD835\uDF5A][ᵵB,\uD835\uDF5B][ᵵC,\uD835\uDF5C][ᵵD,\uD835\uDF5D][ᵵE,\uD835\uDF5E][ᵵF,\uD835\uDF5F][ᵶ0,\uD835\uDF60][ᵶ1,\uD835\uDF61][ᵶ2,\uD835\uDF62][ᵶ3,\uD835\uDF63][ᵶ4,\uD835\uDF64][ᵶ5,\uD835\uDF65][ᵶ6,\uD835\uDF66][ᵶ7,\uD835\uDF67][ᵶ8,\uD835\uDF68][ᵶ9,\uD835\uDF69][ᵶA,\uD835\uDF6A][ᵶB,\uD835\uDF6B][ᵶC,\uD835\uDF6C][ᵶD,\uD835\uDF6D][ᵶE,\uD835\uDF6E][ᵶF,\uD835\uDF6F][ᵷ0,\uD835\uDF70][ᵷ1,\uD835\uDF71][ᵷ2,\uD835\uDF72][ᵷ3,\uD835\uDF73][ᵷ4,\uD835\uDF74][ᵷ5,\uD835\uDF75][ᵷ6,\uD835\uDF76][ᵷ7,\uD835\uDF77][ᵷ8,\uD835\uDF78][ᵷ9,\uD835\uDF79][ᵷA,\uD835\uDF7A][ᵷB,\uD835\uDF7B][ᵷC,\uD835\uDF7C][ᵷD,\uD835\uDF7D][ᵷE,\uD835\uDF7E][ᵷF,\uD835\uDF7F][ᵸ0,\uD835\uDF80][ᵸ1,\uD835\uDF81][ᵸ2,\uD835\uDF82][ᵸ3,\uD835\uDF83][ᵸ4,\uD835\uDF84][ᵸ5,\uD835\uDF85][ᵸ6,\uD835\uDF86][ᵸ7,\uD835\uDF87][ᵸ8,\uD835\uDF88][ᵸ9,\uD835\uDF89][ᵸA,\uD835\uDF8A][ᵸB,\uD835\uDF8B][ᵸC,\uD835\uDF8C][ᵸD,\uD835\uDF8D][ᵸE,\uD835\uDF8E][ᵸF,\uD835\uDF8F][ᵹ0,\uD835\uDF90][ᵹ1,\uD835\uDF91][ᵹ2,\uD835\uDF92][ᵹ3,\uD835\uDF93][ᵹ4,\uD835\uDF94][ᵹ5,\uD835\uDF95][ᵹ6,\uD835\uDF96][ᵹ7,\uD835\uDF97][ᵹ8,\uD835\uDF98][ᵹ9,\uD835\uDF99][ᵹA,\uD835\uDF9A][ᵹB,\uD835\uDF9B][ᵹC,\uD835\uDF9C][ᵹD,\uD835\uDF9D][ᵹE,\uD835\uDF9E][ᵹF,\uD835\uDF9F][ᵺ0,\uD835\uDFA0][ᵺ1,\uD835\uDFA1][ᵺ2,\uD835\uDFA2][ᵺ3,\uD835\uDFA3][ᵺ4,\uD835\uDFA4][ᵺ5,\uD835\uDFA5][ᵺ6,\uD835\uDFA6][ᵺ7,\uD835\uDFA7][ᵺ8,\uD835\uDFA8][ᵺ9,\uD835\uDFA9][ᵺA,\uD835\uDFAA][ᵺB,\uD835\uDFAB][ᵺC,\uD835\uDFAC][ᵺD,\uD835\uDFAD][ᵺE,\uD835\uDFAE][ᵺF,\uD835\uDFAF][ᵻ0,\uD835\uDFB0][ᵻ1,\uD835\uDFB1][ᵻ2,\uD835\uDFB2][ᵻ3,\uD835\uDFB3][ᵻ4,\uD835\uDFB4][ᵻ5,\uD835\uDFB5][ᵻ6,\uD835\uDFB6][ᵻ7,\uD835\uDFB7][ᵻ8,\uD835\uDFB8][ᵻ9,\uD835\uDFB9][ᵻA,\uD835\uDFBA][ᵻB,\uD835\uDFBB][ᵻC,\uD835\uDFBC][ᵻD,\uD835\uDFBD][ᵻE,\uD835\uDFBE][ᵻF,\uD835\uDFBF][ᵼ0,\uD835\uDFC0][ᵼ1,\uD835\uDFC1][ᵼ2,\uD835\uDFC2][ᵼ3,\uD835\uDFC3][ᵼ4,\uD835\uDFC4][ᵼ5,\uD835\uDFC5][ᵼ6,\uD835\uDFC6][ᵼ7,\uD835\uDFC7][ᵼ8,\uD835\uDFC8][ᵼ9,\uD835\uDFC9][ᵼE,\uD835\uDFCE][ᵼF,\uD835\uDFCF][ᵽ0,\uD835\uDFD0][ᵽ1,\uD835\uDFD1][ᵽ2,\uD835\uDFD2][ᵽ3,\uD835\uDFD3][ᵽ4,\uD835\uDFD4][ᵽ5,\uD835\uDFD5][ᵽ6,\uD835\uDFD6][ᵽ7,\uD835\uDFD7][ᵽ8,\uD835\uDFD8][ᵽ9,\uD835\uDFD9][ᵽA,\uD835\uDFDA][ᵽB,\uD835\uDFDB][ᵽC,\uD835\uDFDC][ᵽD,\uD835\uDFDD][ᵽE,\uD835\uDFDE][ᵽF,\uD835\uDFDF][ᵾ0,\uD835\uDFE0][ᵾ1,\uD835\uDFE1][ᵾ2,\uD835\uDFE2][ᵾ3,\uD835\uDFE3][ᵾ4,\uD835\uDFE4][ᵾ5,\uD835\uDFE5][ᵾ6,\uD835\uDFE6][ᵾ7,\uD835\uDFE7][ᵾ8,\uD835\uDFE8][ᵾ9,\uD835\uDFE9][ᵾA,\uD835\uDFEA][ᵾB,\uD835\uDFEB][ᵾC,\uD835\uDFEC][ᵾD,\uD835\uDFED][ᵾE,\uD835\uDFEE][ᵾF,\uD835\uDFEF][ᵿ0,\uD835\uDFF0][ᵿ1,\uD835\uDFF1][ᵿ2,\uD835\uDFF2][ᵿ3,\uD835\uDFF3][ᵿ4,\uD835\uDFF4][ᵿ5,\uD835\uDFF5][ᵿ6,\uD835\uDFF6][ᵿ7,\uD835\uDFF7][ᵿ8,\uD835\uDFF8][ᵿ9,\uD835\uDFF9][ᵿA,\uD835\uDFFA][ᵿB,\uD835\uDFFB][ᵿC,\uD835\uDFFC][ᵿD,\uD835\uDFFD][ᵿE,\uD835\uDFFE][ᵿF,\uD835\uDFFF]");
	}

	public static RendererNode mockDecor() {
		DecorGroupNode.Builder builder = new DecorGroupNode.Builder(3, new TextNode(3, "∫"));
		builder.top(new TextNode(1, "x + y"));
		builder.bottom(new TextNode(1, "y + z"));
		builder.leftTop(new TextNode(1, "z + y"));
		builder.leftBottom(new TextNode(1, "y + x"));
		builder.rightTop(new TextNode(1, "x + z"));
		builder.rightBottom(new TextNode(1, "z + x"));
		return builder.build();
	}

	public static RendererNode mockList() {
		List<RendererNode> list = new ArrayList<>();
		for (Map.Entry<String, String> entry : MathFontOptions.toMap().entrySet()) {
			list.add(new TextNode(1, entry.getKey()));
			list.add(new TextNode(1, entry.getValue()));
			Log.d("chan_debug", "key: " + entry.getKey() + " value: " + entry.getValue());
		}
		return new LinearGroupNode(1, list, LinearGroupNode.Gravity.HORIZONTAL);
	}

	@VisibleForTesting
	public static SqrtNode mockSqrt(float scale, RendererNode content) {
		return new SqrtNode(scale, content, mockText(scale * 0.3f, "x + y + z"));
	}

	public enum Mode {
		DISPLAY,
		INLINE,
	}

	public static class MathRenderer {

	}
}
