package me.chan.te.source;

public interface Source<T> {
	T open() throws SourceOpenException;

	void close() throws SourceCloseException;
}
