package me.chan.texas.source;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;

/**
 * 打开assets下的文本文件
 */
public class AssetsTextSource extends StreamTextSource {

	public AssetsTextSource(Context context, String path) throws IOException {
		this(context, path, false);
	}

	public AssetsTextSource(Resources resources, String path) throws IOException {
		this(resources, path, false);
	}

	public AssetsTextSource(Context context, String path, boolean lazyLoad) throws IOException {
		this(context.getResources(), path, lazyLoad);
	}

	public AssetsTextSource(Resources resources, String path, boolean lazyLoad) throws IOException {
		super(resources.getAssets().open(path), lazyLoad);
	}
}
