package me.chan.texas.test;

import static org.junit.Assert.assertFalse;

import me.chan.texas.test.mock.MockFileInputStream;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import me.chan.texas.source.SourceOpenException;
import me.chan.texas.source.StreamTextSource;

public class SourceUnitTest {

	@Test
	public void test() throws IOException {
		File file = new File("../app/src/main/assets/TheBookAndTheSword.txt");
		FileInputStream fileInputStream = new FileInputStream(file);
		StreamTextSource streamSource = new StreamTextSource(fileInputStream);
		CharSequence charSequence = null;
		try {
			charSequence = streamSource.open(LoadingStrategy.INIT);
		} catch (SourceOpenException e) {
		}

		Assert.assertNotNull(charSequence);

		streamSource.close();

		try {
			fileInputStream.read();
			Assert.fail("test stream close failed");
		} catch (Throwable throwable) {
			assertFalse(throwable instanceof AssertionError);
		}

		fileInputStream = new MockFileInputStream(file);
		try {
			streamSource = new StreamTextSource(fileInputStream);
			streamSource.open(LoadingStrategy.INIT);
			Assert.fail("test read bad file failed");
		} catch (SourceOpenException e) {
		}
	}

	@Test
	public void testLoadingStrategy() throws SourceOpenException {
		StreamTextSource streamSource = new StreamTextSource(new MockInputStream());

		Assert.assertNull(streamSource.open(LoadingStrategy.LOAD_PREVIOUS));
		Assert.assertNull(streamSource.open(LoadingStrategy.LOAD_MORE));
		Assert.assertNull(streamSource.open(LoadingStrategy.TYPESET_ONLY));
		Assert.assertEquals(streamSource.open(LoadingStrategy.INIT), "0\n1\n2\n3");
		Assert.assertNull(streamSource.open(LoadingStrategy.LOAD_PREVIOUS));
		Assert.assertEquals(streamSource.open(LoadingStrategy.INIT), "");
		Assert.assertNull(streamSource.open(LoadingStrategy.LOAD_MORE));
		Assert.assertNull(streamSource.open(LoadingStrategy.TYPESET_ONLY));

		streamSource.close();
	}

	private static class MockInputStream extends InputStream {

		private final byte[] mBytes = "0\n1\n2\n3".getBytes();
		private int mPtr;

		@Override
		public int read() throws IOException {
			return mPtr >= mBytes.length ? -1 : mBytes[mPtr++];
		}

		public void adjust(int offset) {
			mPtr += offset;
		}
	}
}
