package me.chan.te.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileSource extends StreamSource {
	public FileSource(String fileName) throws FileNotFoundException {
		this(new File(fileName));
	}

	public FileSource(File file) throws FileNotFoundException {
		super(new FileInputStream(file));
	}
}
