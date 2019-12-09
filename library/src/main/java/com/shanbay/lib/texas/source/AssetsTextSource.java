package com.shanbay.lib.texas.source;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;

/**
 * 打开assets下的文本文件
 */
public class AssetsTextSource extends StreamTextSource {

	public AssetsTextSource(Context context, String path) throws IOException {
		this(context.getResources(), path);
	}

	public AssetsTextSource(Resources resources, String path) throws IOException {
		super(resources.getAssets().open(path));
	}
}
