package me.chan.lib.log.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by mikyou on 2017/8/30.
 */

public class FileUtils {
	/**
	 * 读取文件内容
	 *
	 * @param filePath 文件路径
	 * @return 读取失败，返回null
	 */
	public static String readFile(String filePath) {
		if (!isFileExist(filePath)) {
			return null;
		}

		FileReader reader = null;
		String retVal = null;

		try {
			StringBuilder fileContent = new StringBuilder();
			reader = new FileReader(filePath);
			char[] buf = new char[512];
			int size = -1;
			while ((size = reader.read(buf)) != -1) {
				fileContent.append(buf, 0, size);
			}
			retVal = fileContent.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeQuietly(reader);
		}
		return retVal;
	}

	/**
	 * 写文件
	 *
	 * @param filePath 文件路径
	 * @param content  文件内容
	 * @return true 成功
	 */
	public static boolean writeFile(String filePath, String content) {
		if (StringUtils.isBlank(content) || StringUtils.isBlank(filePath)) {
			return false;
		}

		FileWriter writer = null;
		boolean retVal = false;

		try {
			writer = new FileWriter(filePath);
			writer.write(content);
			writer.flush();
			retVal = true;
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof IOException) {
				try {
					deleteFile(filePath);
				} catch (Exception e1) {
				}
			}
		} finally {
			closeQuietly(writer);
		}

		return retVal;
	}

	/**
	 * 删除文件
	 *
	 * @param path 文件路径
	 * @return true 删除成功
	 */
	public static boolean deleteFile(String path) {
		if (!isFileExist(path)) {
			return true;
		}

		return new File(path).delete();
	}

	public static boolean isFileExist(String filePath) {
		if (StringUtils.isBlank(filePath)) {
			return false;
		}

		File file = new File(filePath);
		return (file.exists() && file.isFile());
	}

	public static void closeQuietly(Reader input) {
		try {
			if (input != null) {
				input.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

	public static void closeQuietly(Writer output) {
		try {
			if (output != null) {
				output.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}
}
