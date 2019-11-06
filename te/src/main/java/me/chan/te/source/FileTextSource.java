package me.chan.te.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileTextSource extends StreamTextSource {
	public FileTextSource(String fileName) throws FileNotFoundException {
		this(new File(fileName));
	}

	public FileTextSource(File file) throws FileNotFoundException {
		super(new FileInputStream(file));
	}
}
