package me.chan.texas;

import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.source.TextDocumentSource;
import me.chan.texas.text.TextAttribute;

public class MockTextSource extends TextDocumentSource {

	public MockTextSource(RenderOption option, TextAttribute textAttribute, Measurer measurer, String text) {
		super(text);
		setLoader(() -> new TexasOption(null, Hyphenation.getInstance(), measurer, textAttribute, option));
	}
}
