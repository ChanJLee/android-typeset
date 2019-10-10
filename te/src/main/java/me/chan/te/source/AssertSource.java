package me.chan.te.source;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;

public class AssertSource extends StreamSource {

	public AssertSource(Context context, String path) throws IOException {
		this(context.getResources(), path);
	}

	public AssertSource(Resources resources, String path) throws IOException {
		super(resources.getAssets().open(path));
	}
}
