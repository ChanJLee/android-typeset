package me.chan.te.source;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;

public class AssetsTextSource extends StreamTextSource {

	public AssetsTextSource(Context context, String path) throws IOException {
		this(context.getResources(), path);
	}

	public AssetsTextSource(Resources resources, String path) throws IOException {
		super(resources.getAssets().open(path));
	}
}
