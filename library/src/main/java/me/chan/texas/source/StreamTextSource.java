package me.chan.texas.source;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * 流式文本源
 */
public class StreamTextSource extends Source<CharSequence> {

	private Reader mReader;

	public StreamTextSource(InputStream inputStream) {
		mReader = new InputStreamReader(inputStream);
	}

	@Override
	protected CharSequence onOpen(LoadingStrategy strategy) throws SourceOpenException {
		if (strategy == LoadingStrategy.INIT) {
			return onOpen0();
		}

		return null;
	}

	private CharSequence onOpen0() throws SourceOpenException {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			char[] buffer = new char[1024];
			int size;
			while ((size = mReader.read(buffer)) != -1) {
				stringBuilder.append(buffer, 0, size);
			}
		} catch (Throwable e) {
			throw new SourceOpenException("source open failed", e);
		}
		return stringBuilder.toString();
	}

	@Override
	protected void onClose() throws SourceCloseException {
		try {
			if (mReader != null) {
				mReader.close();
				mReader = null;
			}
		} catch (Throwable e) {
			throw new SourceCloseException("close source failed", e);
		}
	}
}
