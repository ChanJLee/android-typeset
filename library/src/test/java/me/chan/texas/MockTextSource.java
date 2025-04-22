package me.chan.texas;

import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.source.TextDocumentSource;
import me.chan.texas.text.TextAttribute;

public class MockTextSource extends TextDocumentSource {

	private Measurer measurer;
	private TextAttribute textAttribute;
	private RenderOption option;

	public MockTextSource(RenderOption option, TextAttribute textAttribute, Measurer measurer, String text) {
		super(text);
		this.option = option;
		this.textAttribute = textAttribute;
		this.measurer = measurer;
	}

	@Override
	protected TexasOption createTexasOption() {
		return new TexasOption(null, Hyphenation.getInstance(), measurer, textAttribute, option);
	}
}
