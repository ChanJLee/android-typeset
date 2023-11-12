package me.chan.texas.source;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.chan.texas.BuildConfig;
import me.chan.texas.renderer.LoadingStrategy;

/**
 * 流式文本源
 */
public class StreamTextSource extends Source<CharSequence> {
    private static final int DEFAULT_BUFFER_SIZE = 128;

    private BufferedReader mReader;
    private final List<CharSequence> mCachedBuffer = new ArrayList<>();
    private int mIndex = -1;

    private final boolean mLazyLoad;

    public StreamTextSource(InputStream inputStream) {
        this(inputStream, false);
    }

    public StreamTextSource(InputStream inputStream, boolean lazyLoad) {
        mReader = new BufferedReader(new InputStreamReader(inputStream));
        mLazyLoad = lazyLoad;
    }

    @Override
    protected CharSequence onOpen(LoadingStrategy strategy) throws SourceOpenException {
        if (strategy == LoadingStrategy.LOAD_PREVIOUS) {
            return mCachedBuffer.isEmpty() || mIndex - 1 >= 0 ? null : mCachedBuffer.get(--mIndex);
        } else if (strategy == LoadingStrategy.LOAD_REFRESH) {
            return mCachedBuffer.isEmpty() ? null : mCachedBuffer.get(mIndex);
        } else if (strategy == LoadingStrategy.LOAD_MORE) {
            /* noop */
        } else if (strategy == LoadingStrategy.LOAD_RELOAD) {
            if (BuildConfig.DEBUG && !mCachedBuffer.isEmpty()) {
                throw new IllegalStateException("source has been read");
            }
        } else {
            throw new IllegalArgumentException("unknown load strategy");
        }

        if (mIndex > 0 && mIndex < mCachedBuffer.size()) {
            return mCachedBuffer.get(++mIndex);
        }
        CharSequence sequence = onOpen0();
        mCachedBuffer.add(sequence);
        ++mIndex;
        return sequence;
    }

    private CharSequence onOpen0() throws SourceOpenException {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            int bufferSize = mLazyLoad ? DEFAULT_BUFFER_SIZE : Integer.MAX_VALUE;
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
