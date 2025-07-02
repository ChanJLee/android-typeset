package me.chan.texas.text.tokenizer;

import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.RuleBasedBreakIterator;

class WordBreaker {
	private static final String CATEGORY_BREAKER_US_RULE = "!!chain;\n" +
			"!!quoted_literals_only;\n" +
			"$Han=[:Han:];\n" +
			"$CR=[\\p{Word_Break=CR}];\n" +
			"$LF=[\\p{Word_Break=LF}];\n" +
			"$Newline=[\\p{Word_Break=Newline}];\n" +
			"$Extend=[\\p{Word_Break=Extend}-$Han];\n" +
			"$ZWJ=[\\p{Word_Break=ZWJ}];\n" +
			"$Regional_Indicator=[\\p{Word_Break=Regional_Indicator}];\n" +
			"$Format=[\\p{Word_Break=Format}];\n" +
			"$Katakana=[\\p{Word_Break=Katakana}];\n" +
			"$Hebrew_Letter=[\\p{Word_Break=Hebrew_Letter}];\n" +
			"$ALetter=[\\p{Word_Break=ALetter}];\n" +
			"$Single_Quote=[\\p{Word_Break=Single_Quote}];\n" +
			"$Double_Quote=[\\p{Word_Break=Double_Quote}];\n" +
			"$MidNumLet=[\\p{Word_Break=MidNumLet}];\n" +
			"$MidLetter=[\\p{Word_Break=MidLetter}-[\\:\\uFE55\\uFF1A]\\-\\u2011\\u00AD\\u2013\\u2014];\n" +
			"$MidNum=[\\p{Word_Break=MidNum}];\n" +
			"$Numeric=[\\p{Word_Break=Numeric}];\n" +
			"$ExtendNumLet=[\\p{Word_Break=ExtendNumLet}];\n" +
			"$WSegSpace=[\\p{Word_Break=WSegSpace}];\n" +
			"$Extended_Pict=[\\p{Extended_Pictographic}];\n" +
			"$Hiragana=[:Hiragana:];\n" +
			"$Ideographic=[\\p{Ideographic}];\n" +
			"$Control=[\\p{Grapheme_Cluster_Break=Control}];\n" +
			"$HangulSyllable=[\\uac00-\\ud7a3];\n" +
			"$ComplexContext=[:LineBreak=Complex_Context:];\n" +
			"$KanaKanji=[$Han$Hiragana$Katakana];\n" +
			"$dictionaryCJK=[$KanaKanji$HangulSyllable];\n" +
			"$dictionary=[$ComplexContext$dictionaryCJK];\n" +
			"$ALetterPlus=[$ALetter-$dictionaryCJK[$ComplexContext-$Extend-$Control]];\n" +
			"$CR$LF;\n" +
			"$ZWJ$Extended_Pict;\n" +
			"$WSegSpace$WSegSpace;\n" +
			"$ExFm=[$Extend$Format$ZWJ];\n" +
			"^$ExFm+;\n" +
			"[^$CR$LF$Newline$ExFm]$ExFm*;\n" +
			"$Numeric$ExFm*{100};\n" +
			"$ALetterPlus$ExFm*{200};\n" +  
			"$HangulSyllable{400};\n" +      
			"$Hebrew_Letter$ExFm*{201};\n" + 
			"$Katakana$ExFm*{400};\n" +      
			"$Hiragana$ExFm*{400};\n" +      
			"$Ideographic$ExFm*{400};\n" +   
			"($ALetterPlus|$Hebrew_Letter)$ExFm*($ALetterPlus|$Hebrew_Letter);\n" +
			"($ALetterPlus|$Hebrew_Letter)$ExFm*($MidLetter|$MidNumLet|$Single_Quote)$ExFm*($ALetterPlus|$Hebrew_Letter){200};\n" +
			"$Hebrew_Letter$ExFm*$Single_Quote{201};\n" + 
			"$Hebrew_Letter$ExFm*$Double_Quote$ExFm*$Hebrew_Letter;\n" +
			"$Numeric$ExFm*$Numeric;\n" +
			"($ALetterPlus|$Hebrew_Letter)$ExFm*$Numeric;\n" +
			"$Numeric$ExFm*($ALetterPlus|$Hebrew_Letter);\n" +
			"$Numeric$ExFm*($MidNum|$MidNumLet|$Single_Quote)$ExFm*$Numeric;\n" +
			"$Katakana$ExFm*$Katakana{400};\n" + 
			"$ALetterPlus$ExFm*$ExtendNumLet{200};\n" + 
			"$Hebrew_Letter$ExFm*$ExtendNumLet{201};\n" + 
			"$Numeric$ExFm*$ExtendNumLet{100};\n" +
			"$Katakana$ExFm*$ExtendNumLet{400};\n" + 
			"$ExtendNumLet$ExFm*$ExtendNumLet{201};\n" + 
			"$ExtendNumLet$ExFm*$ALetterPlus{200};\n" + 
			"$ExtendNumLet$ExFm*$Hebrew_Letter{201};\n" + 
			"$ExtendNumLet$ExFm*$Numeric{100};\n" +
			"$ExtendNumLet$ExFm*$Katakana{400};\n" + 
			"^$Regional_Indicator$ExFm*$Regional_Indicator;\n" +
			"$HangulSyllable$HangulSyllable{400};\n" + 
			"$KanaKanji$KanaKanji{400};\n" + 
			".;\n";

	private static BreakIterator sWordBreakIterator;

	public static BreakIterator getWordBreakIterator() {
		if (sWordBreakIterator == null) {
			sWordBreakIterator = new RuleBasedBreakIterator(CATEGORY_BREAKER_US_RULE);
		}
		return sWordBreakIterator;
	}
}
