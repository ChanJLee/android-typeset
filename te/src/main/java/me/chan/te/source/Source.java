package me.chan.te.source;

public interface Source {
	CharSequence open() throws SourceOpenException;

	void close() throws SourceCloseException;
}
