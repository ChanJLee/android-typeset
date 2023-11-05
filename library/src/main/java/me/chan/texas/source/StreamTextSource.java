package me.chan.texas.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.chan.texas.renderer.LoadingStrategy;

/**
 * 流式文本源
 */
public class StreamTextSource extends Source<CharSequence> {
    private static final int BUFFER_SIZE = 128;

    private InputStream mInputStream;
    private final List<CharSequence> mCachedBuffer = new ArrayList<>();
    private int mIndex = -1;

    public StreamTextSource(InputStream inputStream) {
        mInputStream = inputStream;
    }

    @Override
    protected CharSequence onOpen(LoadingStrategy strategy) throws SourceOpenException {
        if (strategy == LoadingStrategy.LOAD_PREVIOUS) {
            return mCachedBuffer.isEmpty() || mIndex - 1 >= 0 ? null : mCachedBuffer.get(--mIndex);
        } else if (strategy == LoadingStrategy.LOAD_REFRESH) {
            return mCachedBuffer.isEmpty() ? null : mCachedBuffer.get(mIndex);
        } else {
            if (strategy != LoadingStrategy.LOAD_MORE) {
                throw new IllegalArgumentException("unknown load strategy");
            }
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
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStreamReader = new InputStreamReader(mInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            for (int i = 0; i < BUFFER_SIZE && (line = bufferedReader.readLine()) != null; ++i) {
                stringBuilder.append(line)
                        .append("\n");
            }

            if (stringBuilder.length() == 0) {
                return null;
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
    protected void onClose() throws SourceCloseException {
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
