package me.chan.te.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import me.chan.te.source.SourceCloseException;
import me.chan.te.source.SourceOpenException;
import me.chan.te.source.StreamTextSource;
import me.chan.te.test.mock.MockFileInputStream;

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
}
