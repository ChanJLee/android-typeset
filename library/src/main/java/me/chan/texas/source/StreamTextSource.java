package me.chan.texas.source;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.chan.texas.renderer.LoadingStrategy;

/**
 * 流式文本源
 */
public class StreamTextSource extends Source<CharSequence> {

	private BufferedReader mReader;
	@Nullable
	private final List<CharSequence> mCachedBuffer;
	private int mIndex = -1;

	private final int mLoadBufferSize;

	public StreamTextSource(InputStream inputStream) {
		this(inputStream, -1);
	}

	public StreamTextSource(InputStream inputStream, int lazyLoadBufferSize) {
		mReader = new BufferedReader(new InputStreamReader(inputStream));
		if (lazyLoadBufferSize > 0) {
			mLoadBufferSize = lazyLoadBufferSize;
			mCachedBuffer = new ArrayList<>(lazyLoadBufferSize);
		} else {
			mLoadBufferSize = Integer.MAX_VALUE;
			mCachedBuffer = null;
		}
	}

	@Override
	protected CharSequence onOpen(LoadingStrategy strategy) throws SourceOpenException {
		if (strategy == LoadingStrategy.LOAD_PREVIOUS) {
			return mCachedBuffer == null || mCachedBuffer.isEmpty() || mIndex - 1 >= 0 ? null : mCachedBuffer.get(--mIndex);
		} else if (strategy == LoadingStrategy.LOAD_MORE) {
			/* noop */
		} else if (strategy == LoadingStrategy.LOAD) {
			mIndex = -1;
		} else {
			throw new IllegalArgumentException("unknown load strategy");
		}

		if (mCachedBuffer != null && mIndex > 0 && mIndex < mCachedBuffer.size()) {
			return mCachedBuffer.get(++mIndex);
		}
		CharSequence sequence = onOpen0();
		if (mCachedBuffer != null) {
			mCachedBuffer.add(sequence);
		}
		++mIndex;
		return sequence;
	}

	private CharSequence onOpen0() throws SourceOpenException {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			String line;
			int bufferSize = mLoadBufferSize;
			for (int i = 0; i < bufferSize && (line = mReader.readLine()) != null; ++i) {
				stringBuilder.append(line)
						.append("\n");
			}

			if (stringBuilder.length() == 0) {
				return null;
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
