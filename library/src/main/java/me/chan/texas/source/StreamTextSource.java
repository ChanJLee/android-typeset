package me.chan.texas.source;

import androidx.annotation.IntRange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.chan.texas.renderer.LoadingStrategy;

/**
 * 流式文本源
 */
public class StreamTextSource extends Source<CharSequence> {

    private InputStream mInputStream;
    private final int mBufferSize;

    public StreamTextSource(InputStream inputStream) {
        this(inputStream, 128);
    }

    public StreamTextSource(InputStream inputStream, @IntRange(from = 0, to = Integer.MAX_VALUE) int bufferSize) {
        mInputStream = inputStream;
        mBufferSize = bufferSize;
    }

    @Override
    protected CharSequence onOpen(LoadingStrategy strategy) throws SourceOpenException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStreamReader = new InputStreamReader(mInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            for (int i = 0; i < mBufferSize && (line = bufferedReader.readLine()) != null; ++i) {
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
