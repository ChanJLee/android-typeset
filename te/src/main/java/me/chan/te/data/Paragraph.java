package me.chan.te.data;

import java.util.ArrayList;
import java.util.List;

public class Paragraph {
	private List<Line> mLines = new ArrayList<>();

	public Paragraph(Line... lines) {
		for (int i = 0; lines != null && i < lines.length; ++i) {
			mLines.add(lines[i]);
		}
	}

	public Paragraph addLine(Line line) {
		mLines.add(line);
		return this;
	}

	public List<Line> getLines() {
		return mLines;
	}
}
