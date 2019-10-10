package me.chan.te.source;

public class RawSource implements Source {

	private CharSequence mCharSequence;

	public RawSource(CharSequence charSequence) {
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
