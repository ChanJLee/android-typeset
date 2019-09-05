package me.chan.te.data;

import java.util.List;

public class Paragraph {
	private List<Line> mLines;

	public void setLines(List<Line> lines) {
		mLines = lines;
	}

	public List<Line> getLines() {
		return mLines;
	}
}
