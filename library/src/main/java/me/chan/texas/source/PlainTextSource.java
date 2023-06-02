package me.chan.texas.source;

/**
 * 纯文本
 */
public class PlainTextSource implements Source<CharSequence> {

	private final CharSequence mCharSequence;

	public PlainTextSource(CharSequence charSequence) {
		mCharSequence = charSequence;
	}

	@Override
	public CharSequence open() throws SourceOpenException {
		return mCharSequence;
	}

	@Override
	public void close() throws SourceCloseException {
		/* do nothing */
	}
}
