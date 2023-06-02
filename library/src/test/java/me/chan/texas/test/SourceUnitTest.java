package me.chan.texas.test;

import me.chan.texas.source.CacheSource;
import me.chan.texas.source.ObjectSource;
import me.chan.texas.source.SourceCloseException;
import me.chan.texas.source.SourceOpenException;
import me.chan.texas.source.StreamTextSource;
import me.chan.texas.test.mock.MockFileInputStream;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertFalse;

public class SourceUnitTest {

	@Test
	public void test() throws IOException {
		File file = new File("../app/src/main/assets/TheBookAndTheSword.txt");
		FileInputStream fileInputStream = new FileInputStream(file);
		StreamTextSource streamSource = new StreamTextSource(fileInputStream);
		CharSequence charSequence = null;
		try {
			charSequence = streamSource.open();
		} catch (SourceOpenException e) {
		}

		Assert.assertNotNull(charSequence);

		try {
			streamSource.close();
		} catch (SourceCloseException e) {
		}

		try {
			fileInputStream.read();
			Assert.fail("test stream close failed");
		} catch (Throwable throwable) {
			assertFalse(throwable instanceof AssertionError);
		}

		fileInputStream = new MockFileInputStream(file);
		try {
			streamSource = new StreamTextSource(fileInputStream);
			streamSource.open();
			Assert.fail("test read bad file failed");
		} catch (SourceOpenException e) {
		}
	}

	@Test
	public void testCacheSource() throws SourceOpenException, SourceCloseException {
		String str = "hello";
		CountSource countSource = new CountSource(str);
		CacheSource<String> cacheSource = new CacheSource<>(countSource);
		Assert.assertEquals(str, cacheSource.open());
		Assert.assertEquals(countSource.openCount, 1);
		Assert.assertEquals(countSource.closeCount, 0);
		cacheSource.close();
		Assert.assertEquals(countSource.closeCount, 1);

		// 读第二次测试幂等性
		Assert.assertEquals(str, cacheSource.open());
		Assert.assertEquals(countSource.openCount, 1);

		cacheSource.cleanCache();
		Assert.assertEquals(str, cacheSource.open());
		Assert.assertEquals(countSource.openCount, 2);
		Assert.assertEquals(countSource.closeCount, 1);
	}

	private static class CountSource extends ObjectSource<String> {
		public int openCount = 0;
		public int closeCount = 0;

		public CountSource(String object) {
			super(object);
		}

		@Override
		public String open() throws SourceOpenException {
			openCount++;
			return super.open();
		}

		@Override
		public void close() throws SourceCloseException {
			closeCount++;
			super.close();
		}
	}
}
