package me.chan.te.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamSource implements Source {

	private InputStream mInputStream;

	public StreamSource(InputStream inputStream) {
		mInputStream = inputStream;
	}

	@Override
	public CharSequence open() throws SourceOpenException {
		StringBuilder stringBuilder = new StringBuilder();
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			inputStreamReader = new InputStreamReader(mInputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line)
						.append("\n");
			}
		} catch (Throwable e) {
			throw new SourceOpenException("source open failed", e);
		} finally {
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public void close() throws SourceCloseException {
		try {
			mInputStream.close();
		} catch (Throwable e) {
			throw new SourceCloseException("close source failed", e);
		}
	}
}
