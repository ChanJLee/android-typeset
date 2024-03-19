package me.chan.texas.source;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;

/**
 * 打开assets下的文本文件
 */
public class AssetsTextSource extends StreamTextSource {

	public AssetsTextSource(Context context, String path) throws IOException {
		this(context, path, -1);
	}

	public AssetsTextSource(Resources resources, String path) throws IOException {
		this(resources, path, -1);
	}

	public AssetsTextSource(Context context, String path, int lazyLoadBufferSize) throws IOException {
		this(context.getResources(), path, lazyLoadBufferSize);
	}

	public AssetsTextSource(Resources resources, String path, int lazyLoadBufferSize) throws IOException {
		super(resources.getAssets().open(path), lazyLoadBufferSize);
	}
}
