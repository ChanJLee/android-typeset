package me.chan.texas.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 文本文件源
 */
public class FileTextSource extends StreamTextSource {
	public FileTextSource(String fileName) throws FileNotFoundException {
		this(new File(fileName));
	}

	public FileTextSource(File file) throws FileNotFoundException {
		super(new FileInputStream(file));
	}
}
