package com.shanbay.lib.texas.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 流式文本源
 */
public class StreamTextSource implements Source<CharSequence> {

	private InputStream mInputStream;

	public StreamTextSource(InputStream inputStream) {
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
			if (mInputStream != null) {
				mInputStream.close();
				mInputStream = null;
			}
		} catch (Throwable e) {
			throw new SourceCloseException("close source failed", e);
		}
	}
}
