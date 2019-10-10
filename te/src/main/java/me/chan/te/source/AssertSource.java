package me.chan.te.source;

import android.content.res.Resources;

import java.io.IOException;

public class AssertSource extends StreamSource {

	public AssertSource(Resources resources, String path) throws IOException {
		super(resources.getAssets().open(path));
	}
}
