package com.shanbay.lib.texas.test.mock;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MockFileInputStream extends FileInputStream {
	public MockFileInputStream(String name) throws FileNotFoundException {
		super(name);
	}

	public MockFileInputStream(File file) throws FileNotFoundException {
		super(file);
	}

	public MockFileInputStream(FileDescriptor fdObj) {
		super(fdObj);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		throw new IOException("mock IOException");
	}
}
