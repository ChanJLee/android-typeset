package me.chan.texas;

import androidx.annotation.Nullable;

import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;

public class MockTextSource extends TexasView.DocumentSource {

	private final String text;

	public MockTextSource(RenderOption option, TextAttribute textAttribute, Measurer measurer, String text) {
		setLoader(() -> new TexasOption(null, Hyphenation.getInstance(), measurer, textAttribute, option));
		this.text = text;
	}

	@Override
	protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
		return new Document.Builder()
				.addSegment(Paragraph.Builder.newBuilder(option)
						.text(text)
						.build())
				.build();
	}
}
