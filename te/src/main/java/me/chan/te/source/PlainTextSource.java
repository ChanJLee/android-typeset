package me.chan.te.source;

public class PlainTextSource implements Source<CharSequence> {

	private CharSequence mCharSequence;

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
